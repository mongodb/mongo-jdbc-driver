/*
 * Copyright 2024-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc.oidc;

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallback;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.MongoInterruptedException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.oidc.OidcAuthFlow.TokenResult;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.security.auth.RefreshFailedException;

public class JdbcOidcCallback implements OidcCallback {
    // The Java driver keeps OIDC tokens per MongoClient, and each connection has its own client, so
    // keep the latest login for each identity here. Otherwise every new connection would log in
    // through the browser again.
    private static final Map<List<Object>, TokenResult> logins = new ConcurrentHashMap<>();
    private static final Map<List<Object>, ReentrantLock> locks = new ConcurrentHashMap<>();
    // The browser login listens on a fixed port, so only one can run at a time.
    private static final ReentrantLock browserLock = new ReentrantLock();
    // A cached access token is reused only if it stays valid at least this long.
    private static final Duration MIN_REMAINING = Duration.ofMinutes(5);

    private final OidcAuthFlow oidcAuthFlow;
    // Part of the identity, so a token is only sent to the cluster it was issued for. When it's
    // null, the cluster is unknown and logins aren't shared.
    private final List<String> hosts;
    // The identity and login this callback last handed to its client.
    private volatile List<Object> identity;
    private volatile TokenResult served;

    public JdbcOidcCallback() {
        this(new OidcAuthFlow(), null);
    }

    public JdbcOidcCallback(MongoLogger parentLogger) {
        this(new OidcAuthFlow(parentLogger), null);
    }

    public JdbcOidcCallback(OidcAuthFlow oidcAuthFlow, List<String> hosts) {
        this.oidcAuthFlow = oidcAuthFlow;
        this.hosts = hosts;
    }

    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext) {
        IdpInfo idpInfo = callbackContext.getIdpInfo();
        List<Object> identity =
                Arrays.asList(
                        hosts,
                        idpInfo.getIssuer(),
                        idpInfo.getClientId(),
                        callbackContext.getUserName());
        Instant deadline =
                callbackContext.getTimeout() == null
                        ? null
                        : Instant.now().plus(callbackContext.getTimeout());
        ReentrantLock lock = locks.computeIfAbsent(identity, k -> new ReentrantLock());
        acquire(lock, deadline);
        try {
            this.identity = identity;
            boolean noRefreshToken = callbackContext.getRefreshToken() == null;
            if (noRefreshToken && served != null) {
                // This client already has a login, so the driver is starting over because the
                // server rejected it, or it expired with no refresh token. Don't hand it out again.
                logins.remove(identity, served);
            }

            TokenResult cached = hosts == null ? null : logins.get(identity);
            if (noRefreshToken
                    && cached != null
                    && cached.expiresAt.isAfter(Instant.now().plus(MIN_REMAINING))) {
                served = cached;
                return cached.result;
            }

            // Use our refresh token over the driver's when we have one: it's the newest, and some
            // IdPs invalidate a refresh token once it has been used.
            String refreshToken =
                    cached != null && cached.result.getRefreshToken() != null
                            ? cached.result.getRefreshToken()
                            : callbackContext.getRefreshToken();
            if (refreshToken != null && !refreshToken.isEmpty()) {
                try {
                    // null means the refresh failed without the IdP rejecting the token
                    // (OidcAuthFlow logs why), so keep the login instead of sending the user to
                    // the browser.
                    return remember(identity, oidcAuthFlow.refresh(idpInfo, refreshToken));
                } catch (RefreshFailedException e) {
                    // Expired or revoked, so log in again below.
                    logins.remove(identity, cached);
                }
            }

            acquire(browserLock, deadline);
            try {
                return remember(identity, oidcAuthFlow.authCodeFlow(callbackContext));
            } catch (OidcTimeoutException e) {
                throw new RuntimeException(e);
            } finally {
                browserLock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Forgets the login this callback handed out, after the server rejected it, so the next
     * connection logs in again instead of reusing it.
     */
    public void forgetLogin() {
        List<Object> identity = this.identity;
        TokenResult served = this.served;
        if (identity != null && served != null) {
            logins.remove(identity, served);
        }
    }

    private OidcCallbackResult remember(List<Object> identity, TokenResult login) {
        if (login == null) {
            return null;
        }
        if (hosts != null) {
            logins.put(identity, login);
        }
        served = login;
        return login.result;
    }

    // Waits for whoever holds the lock, but no longer than the driver allows this login.
    private static void acquire(ReentrantLock lock, Instant deadline) {
        try {
            if (deadline == null) {
                lock.lockInterruptibly();
            } else if (!lock.tryLock(
                    Math.max(0, Duration.between(Instant.now(), deadline).toMillis()),
                    TimeUnit.MILLISECONDS)) {
                throw new MongoTimeoutException("Timed out waiting for another OIDC login");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MongoInterruptedException("Interrupted waiting for another OIDC login", e);
        }
    }
}
