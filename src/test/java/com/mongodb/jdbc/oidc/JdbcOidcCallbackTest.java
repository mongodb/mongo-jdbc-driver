/*
 * Copyright 2026-present MongoDB, Inc.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.OidcAuthFlow.TokenResult;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.security.auth.RefreshFailedException;
import org.junit.jupiter.api.Test;

class JdbcOidcCallbackTest {
    private final OidcAuthFlow authFlow = mock(OidcAuthFlow.class);
    private final IdpInfo idp = new JdbcIdpInfo("https://idp.example.com", "client", null);
    // Logins are shared by every callback in the JVM, so each test uses its own cluster.
    private final List<String> hosts = cluster();

    private static List<String> cluster() {
        return Collections.singletonList(UUID.randomUUID() + ":27017");
    }

    private JdbcOidcCallback newClient() {
        return new JdbcOidcCallback(authFlow, hosts);
    }

    private OidcCallbackContext request(String user, String refreshToken) {
        return request(user, refreshToken, idp);
    }

    private OidcCallbackContext request(String user, String refreshToken, IdpInfo idp) {
        return new JdbcOidcCallbackContext(Duration.ofMinutes(5), 1, refreshToken, idp, user);
    }

    private static IdpInfo withScopes(String... scopes) {
        return new JdbcIdpInfo("https://idp.example.com", "client", Arrays.asList(scopes));
    }

    private static TokenResult login(String accessToken, String refreshToken, Duration validFor) {
        return new TokenResult(accessToken, refreshToken, Instant.now().plus(validFor));
    }

    @Test
    void testNewClientReusesFreshAccessToken() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ofHours(1)));

        newClient().onRequest(request("user", null));

        assertEquals("a1", newClient().onRequest(request("user", null)).getAccessToken());
        verify(authFlow, times(1)).authCodeFlow(any());
    }

    @Test
    void testRefreshUsesNewestRefreshToken() throws Exception {
        // Logins that expire right away, so each request has to refresh.
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ZERO));
        when(authFlow.refresh(any(), any()))
                .thenReturn(login("a2", "r2", Duration.ZERO), login("a3", "r3", Duration.ZERO));

        JdbcOidcCallback first = newClient();
        first.onRequest(request("user", null));
        newClient().onRequest(request("user", null));
        // The first client reauthenticates with r1, which an IdP that rotates refresh tokens has
        // already invalidated by now.
        first.onRequest(request("user", "r1"));

        verify(authFlow).refresh(idp, "r1");
        verify(authFlow).refresh(idp, "r2");
    }

    @Test
    void testFailedRefreshLogsInAgain() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ZERO));
        when(authFlow.refresh(any(), any())).thenThrow(new RefreshFailedException("expired"));

        JdbcOidcCallback client = newClient();
        client.onRequest(request("user", null));
        client.onRequest(request("user", "r1"));

        verify(authFlow, times(2)).authCodeFlow(any());
    }

    @Test
    void testUnreachableIdpKeepsLogin() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ZERO));
        when(authFlow.refresh(any(), any())).thenReturn(null, login("a2", "r2", Duration.ZERO));

        newClient().onRequest(request("user", null));
        assertNull(newClient().onRequest(request("user", null)));

        assertEquals("a2", newClient().onRequest(request("user", null)).getAccessToken());
    }

    @Test
    void testRejectedLoginIsNotReused() throws Exception {
        when(authFlow.authCodeFlow(any()))
                .thenReturn(
                        login("a1", null, Duration.ofHours(1)),
                        login("a2", null, Duration.ofHours(1)));

        JdbcOidcCallback client = newClient();
        client.onRequest(request("user", null));

        // The driver asks the same client again without a refresh token: the server rejected a1.
        assertEquals("a2", client.onRequest(request("user", null)).getAccessToken());
        assertEquals("a2", newClient().onRequest(request("user", null)).getAccessToken());
    }

    @Test
    void testForgottenLoginIsNotReused() throws Exception {
        when(authFlow.authCodeFlow(any()))
                .thenReturn(
                        login("a1", null, Duration.ofHours(1)),
                        login("a2", null, Duration.ofHours(1)));

        JdbcOidcCallback client = newClient();
        client.onRequest(request("user", null));
        client.forgetLogin();

        assertEquals("a2", newClient().onRequest(request("user", null)).getAccessToken());
    }

    @Test
    void testLoginsAreNotSharedAcrossIdentities() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ofHours(1)));
        IdpInfo otherIssuer = new JdbcIdpInfo("https://other.example.com", "client", null);
        IdpInfo otherClient = new JdbcIdpInfo("https://idp.example.com", "other", null);

        newClient().onRequest(request("alice", null));
        newClient().onRequest(request("bob", null));
        new JdbcOidcCallback(authFlow, cluster()).onRequest(request("alice", null));
        newClient().onRequest(request("alice", null, withScopes("openid", "email")));
        newClient().onRequest(request("alice", null, otherIssuer));
        newClient().onRequest(request("alice", null, otherClient));

        verify(authFlow, times(6)).authCodeFlow(any());
    }

    @Test
    void testHostOrderAndCaseAndScopeOrderDoNotMatter() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ofHours(1)));
        String a = UUID.randomUUID() + ":27017";
        String b = UUID.randomUUID() + ":27017";

        new JdbcOidcCallback(authFlow, Arrays.asList(a, b))
                .onRequest(request("user", null, withScopes("openid", "email")));
        new JdbcOidcCallback(authFlow, Arrays.asList(b.toUpperCase(Locale.ROOT), a))
                .onRequest(request("user", null, withScopes("email", "openid")));

        verify(authFlow, times(1)).authCodeFlow(any());
    }

    @Test
    void testReusedLoginReportsRemainingLifetime() throws Exception {
        when(authFlow.authCodeFlow(any())).thenReturn(login("a1", "r1", Duration.ofMinutes(30)));
        newClient().onRequest(request("user", null));

        // OidcCallbackResult has no getter for its lifetime.
        Field expiresIn = OidcCallbackResult.class.getDeclaredField("expiresIn");
        expiresIn.setAccessible(true);
        Duration left = (Duration) expiresIn.get(newClient().onRequest(request("user", null)));
        assertTrue(left.compareTo(Duration.ofMinutes(29)) > 0);
        assertTrue(left.compareTo(Duration.ofMinutes(30)) <= 0);
    }
}
