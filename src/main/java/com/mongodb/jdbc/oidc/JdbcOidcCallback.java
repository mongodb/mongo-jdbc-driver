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
import com.mongodb.jdbc.oidc.OidcAuthFlow.TokenResult;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.security.auth.RefreshFailedException;

public class JdbcOidcCallback implements OidcCallback {
    // The Java driver keeps OIDC tokens per MongoClient, and each connection has its own client, so
    // keep the latest login for each identity here. Otherwise every new connection would log in
    // through the browser again. Entries are never removed: browser logins only happen on a
    // desktop, so there are few, and keeping them spares users the browser while the refresh token
    // still works.
    private static final Map<Identity, Entry> logins = new ConcurrentHashMap<>();
    // The browser login listens on a fixed port, so only one can run at a time.
    private static final ReentrantLock browserLock = new ReentrantLock();
    // A cached access token is reused only if it stays valid at least this long.
    private static final Duration MIN_REMAINING = Duration.ofMinutes(5);

    // One identity's latest login, and the lock that lets one connection log in or refresh at a
    // time.
    private static final class Entry {
        final ReentrantLock lock = new ReentrantLock();
        // Empty until the first login, and after a login is rejected or fails to refresh. Atomic
        // because forgetLogin() clears it without the lock.
        final AtomicReference<TokenResult> login = new AtomicReference<>();
    }

    // Who a login belongs to: it's only reused for the same cluster, IdP, client, user and scopes.
    // Hosts and scopes are sets, so the same ones in a different order are the same identity, and
    // host names ignore case.
    private static final class Identity {
        final Set<String> hosts;
        final String issuer;
        final String clientId;
        // Null when the connection string has no user.
        final String userName;
        // A token may lack scopes the server now requests, so a scope change means a new login.
        final Set<String> scopes;

        Identity(List<String> hosts, IdpInfo idpInfo, String userName) {
            this.hosts =
                    hosts.stream().map(h -> h.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
            this.issuer = idpInfo.getIssuer();
            this.clientId = idpInfo.getClientId();
            this.userName = userName;
            this.scopes =
                    idpInfo.getRequestScopes() == null
                            ? Collections.emptySet()
                            : new HashSet<>(idpInfo.getRequestScopes());
        }

        // Every field must be compared here and hashed in hashCode, or different identities match.
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Identity)) {
                return false;
            }
            Identity other = (Identity) o;
            return hosts.equals(other.hosts)
                    && Objects.equals(issuer, other.issuer)
                    && Objects.equals(clientId, other.clientId)
                    && Objects.equals(userName, other.userName)
                    && scopes.equals(other.scopes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hosts, issuer, clientId, userName, scopes);
        }
    }

    private final OidcAuthFlow oidcAuthFlow;
    // Part of the identity, so a token is only sent to the cluster it was issued for.
    private final List<String> hosts;
    // The identity's entry and the login this callback last handed to its client.
    private volatile Entry entry;
    private volatile TokenResult served;

    public JdbcOidcCallback(OidcAuthFlow oidcAuthFlow, List<String> hosts) {
        this.oidcAuthFlow = oidcAuthFlow;
        this.hosts = hosts;
    }

    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext) {
        IdpInfo idpInfo = callbackContext.getIdpInfo();
        Identity identity = new Identity(hosts, idpInfo, callbackContext.getUserName());
        Instant deadline =
                callbackContext.getTimeout() == null
                        ? null
                        : Instant.now().plus(callbackContext.getTimeout());
        Entry entry = logins.computeIfAbsent(identity, k -> new Entry());
        acquire(entry.lock, deadline);
        try {
            this.entry = entry;
            boolean noRefreshToken = callbackContext.getRefreshToken() == null;
            if (noRefreshToken && served != null) {
                // This client already has a login, so the driver is starting over because the
                // server rejected it, or it expired with no refresh token. Don't hand it out again.
                entry.login.compareAndSet(served, null);
            }

            TokenResult cached = entry.login.get();
            if (noRefreshToken
                    && cached != null
                    && cached.expiresAt.isAfter(Instant.now().plus(MIN_REMAINING))) {
                served = cached;
                return cached.toCallbackResult();
            }

            // Use our refresh token over the driver's when we have one: it's the newest, and some
            // IdPs invalidate a refresh token once it has been used.
            String refreshToken =
                    cached != null && cached.refreshToken != null
                            ? cached.refreshToken
                            : callbackContext.getRefreshToken();
            if (refreshToken != null && !refreshToken.isEmpty()) {
                try {
                    // null means the refresh failed without the IdP rejecting the token
                    // (OidcAuthFlow logs why), so keep the login instead of sending the user to
                    // the browser.
                    return remember(entry, oidcAuthFlow.refresh(idpInfo, refreshToken));
                } catch (RefreshFailedException e) {
                    // Expired or revoked, so log in again below.
                    entry.login.compareAndSet(cached, null);
                }
            }

            acquire(browserLock, deadline);
            try {
                return remember(entry, oidcAuthFlow.authCodeFlow(callbackContext));
            } catch (OidcTimeoutException e) {
                throw new RuntimeException(e);
            } finally {
                browserLock.unlock();
            }
        } finally {
            entry.lock.unlock();
        }
    }

    /**
     * Forgets the login this callback handed out, after the server rejected it, so the next
     * connection logs in again instead of reusing it.
     */
    public void forgetLogin() {
        Entry entry = this.entry;
        TokenResult served = this.served;
        if (entry != null && served != null) {
            entry.login.compareAndSet(served, null);
        }
    }

    private OidcCallbackResult remember(Entry entry, TokenResult login) {
        if (login == null) {
            return null;
        }
        entry.login.set(login);
        served = login;
        return login.toCallbackResult();
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
