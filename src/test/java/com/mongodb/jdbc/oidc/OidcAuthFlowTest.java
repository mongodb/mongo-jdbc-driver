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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * Tests the discovery-document endpoint validation in {@link OidcAuthFlow}.
 *
 * <p>The authorization and token endpoints come from an untrusted OIDC discovery document. These
 * tests assert that non-https endpoints are rejected, so a malicious document cannot steer the
 * driver to a file:/wss:/http: URL.
 */
public class OidcAuthFlowTest {

    private static final URI HTTPS_AUTH_ENDPOINT = URI.create("https://idp.example.com/authorize");
    private static final URI HTTPS_TOKEN_ENDPOINT = URI.create("https://idp.example.com/token");
    private static final URI HTTP_TOKEN_ENDPOINT = URI.create("http://idp.example.com/token");
    private static final URI HTTP_AUTH_ENDPOINT = URI.create("http://idp.example.com/authorize");

    private static OIDCProviderMetadata metadata(URI authorizationEndpoint, URI tokenEndpoint) {
        OIDCProviderMetadata metadata =
                new OIDCProviderMetadata(
                        new Issuer("https://idp.example.com"),
                        Collections.singletonList(SubjectType.PUBLIC),
                        URI.create("https://idp.example.com/jwks"));
        metadata.setAuthorizationEndpointURI(authorizationEndpoint);
        metadata.setTokenEndpointURI(tokenEndpoint);
        return metadata;
    }

    @Test
    public void rejectsFileAuthorizationEndpoint() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata =
                metadata(URI.create("file:///etc/passwd"), HTTPS_TOKEN_ENDPOINT);

        assertFalse(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    public void rejectsWssAuthorizationEndpoint() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata =
                metadata(URI.create("wss://evil.example.com/ws"), HTTPS_TOKEN_ENDPOINT);

        assertFalse(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    public void rejectsFileTokenEndpoint() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata =
                metadata(HTTPS_AUTH_ENDPOINT, URI.create("file:///etc/passwd"));

        assertFalse(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    public void rejectsHttpTokenEndpoint() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata = metadata(HTTPS_AUTH_ENDPOINT, HTTP_TOKEN_ENDPOINT);

        assertFalse(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    public void rejectsHttpAuthEndpoint() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata = metadata(HTTP_AUTH_ENDPOINT, HTTPS_TOKEN_ENDPOINT);

        assertFalse(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    public void acceptsHttpsEndpoints() {
        OidcAuthFlow authFlow = new OidcAuthFlow();
        OIDCProviderMetadata metadata = metadata(HTTPS_AUTH_ENDPOINT, HTTPS_TOKEN_ENDPOINT);

        assertTrue(authFlow.validateProviderMetadata(metadata));
    }

    @Test
    void testRefreshResponseWithoutRefreshTokenKeepsTheOneSent() {
        OIDCTokenResponse withoutRefreshToken =
                new OIDCTokenResponse(
                        new OIDCTokens(new BearerAccessToken("access", 3600, null), null));
        assertEquals("sent", OidcAuthFlow.toTokenResult(withoutRefreshToken, "sent").refreshToken);
    }

    @Test
    void testRefreshResponseWithNewRefreshTokenUsesIt() {
        OIDCTokenResponse rotated =
                new OIDCTokenResponse(
                        new OIDCTokens(
                                new BearerAccessToken("access", 3600, null),
                                new RefreshToken("rotated")));
        assertEquals("rotated", OidcAuthFlow.toTokenResult(rotated, "sent").refreshToken);
    }
}
