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
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.logging.MongoLogger;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.RefreshFailedException;

public class OidcAuthFlow {

    private static final Logger logger = Logger.getLogger(OidcAuthFlow.class.getName());
    private MongoLogger mongoLogger;
    private static final String OFFLINE_ACCESS = "offline_access";
    private static final String OPENID = "openid";

    public OidcAuthFlow() {}

    public OidcAuthFlow(MongoLogger parentLogger) {
        this.mongoLogger = new MongoLogger(OidcAuthFlow.class.getName(), parentLogger);
    }

    /**
     * Builds the OIDC scopes for the authorization request by combining default scopes with
     * client-requested scopes that are supported by the Identity Provider. Adds openid and
     * offline_access scopes by default, and includes the clientID/.default if in the requested
     * scopes.
     *
     * @param clientID the OAuth2 client identifier
     * @param idpServerInfo server information containing requested scopes and other IdP details
     * @param providerMetadata OIDC provider metadata containing supported scopes
     * @return a Scope object containing all valid scopes to be requested
     */
    public Scope buildScopes(
            String clientID, IdpInfo idpServerInfo, OIDCProviderMetadata providerMetadata) {
        Set<String> scopes = new HashSet<>();
        Scope supportedScopes = providerMetadata.getScopes();

        // Add openid and offline_access scopes by default
        scopes.add(OPENID);
        scopes.add(OFFLINE_ACCESS);

        // Add custom scopes from request that are supported by the IdP
        List<String> requestedScopes = idpServerInfo.getRequestScopes();
        if (requestedScopes != null) {
            // Always add clientID/.default if it's in requestedScopes, this was needed for Azure OIDC.
            String clientIDDefault = clientID + "/.default";
            if (requestedScopes.contains(clientIDDefault)) {
                scopes.add(clientIDDefault);
            }
            if (supportedScopes != null) {
                for (String scope : requestedScopes) {
                    if (supportedScopes.contains(scope)) {
                        scopes.add(scope);
                    } else {
                        logger.warning(
                                "Requested scope '" + scope + "' is not supported by the IdP");
                    }
                }
            }
        }

        Scope finalScopes = new Scope();
        for (String scope : scopes) {
            finalScopes.add(new Scope.Value(scope));
        }
        return finalScopes;
    }

    public OidcCallbackResult doAuthCodeFlow(OidcCallbackContext callbackContext)
            throws OidcTimeoutException {
        IdpInfo idpServerInfo = callbackContext.getIdpInfo();
        String clientID = idpServerInfo.getClientId();
        String issuerURI = idpServerInfo.getIssuer();

        // Check that the IdP information is valid
        if (!validateIdpInfo(idpServerInfo, clientID, issuerURI)) {
            return null;
        }

        RFC8252HttpServer server = new RFC8252HttpServer();
        try {
            // Resolve OIDC provider metadata using the issuer URI and
            // extract authorization and token endpoint URIs.
            OIDCProviderMetadata providerMetadata =
                    OIDCProviderMetadata.resolve(new Issuer(issuerURI));
            URI authorizationEndpoint = providerMetadata.getAuthorizationEndpointURI();
            URI tokenEndpoint = providerMetadata.getTokenEndpointURI();
            Scope requestedScopes = buildScopes(clientID, idpServerInfo, providerMetadata);

            // Start the local RFC8252 HTTP server to receive the redirect.
            server.start();

            URI redirectURI =
                    new URI(
                            "http://localhost:"
                                    + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                    + "/redirect");
            State state = new State();
            CodeVerifier codeVerifier = new CodeVerifier();

            // Build the authorization request URI.
            AuthorizationRequest request =
                    new AuthorizationRequest.Builder(
                                    new ResponseType(ResponseType.Value.CODE),
                                    new ClientID(clientID))
                            .scope(requestedScopes)
                            .redirectionURI(redirectURI)
                            .state(state)
                            .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                            .endpointURI(authorizationEndpoint)
                            .build();

            // Open the browser to the authorization request URI.
            try {
                openURL(request.toURI().toString());
            } catch (Exception e) {
                log(Level.SEVERE, "Failed to open the browser: " + e.getMessage());
                return null;
            }

            // Wait for the authorization response from the local HTTP server.
            OidcResponse response = server.getOidcResponse(callbackContext.getTimeout());
            if (response == null || !state.getValue().equals(response.getState())) {
                log(Level.SEVERE, "OIDC response is null or returned an invalid state");
                return null;
            }

            // Generate token request from the authorization code and PKCE verifier.
            AuthorizationCode code = new AuthorizationCode(response.getCode());
            AuthorizationCodeGrant codeGrant =
                    new AuthorizationCodeGrant(code, redirectURI, codeVerifier);
            TokenRequest tokenRequest =
                    new TokenRequest(tokenEndpoint, new ClientID(clientID), codeGrant);

            // Sends the token exchange request and parse the response to obtain tokens.
            HTTPResponse httpResponse = tokenRequest.toHTTPRequest().send();
            TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpResponse);
            if (!tokenResponse.indicatesSuccess()) {
                log(Level.SEVERE, "Token request failed with response: " + httpResponse.getBody());
                return null;
            }

            return getOidcCallbackResultFromTokenResponse((OIDCTokenResponse) tokenResponse);
        } catch (Exception e) {
            log(Level.SEVERE, "Error during OIDC authentication " + e.getMessage());
            if (e instanceof OidcTimeoutException) {
                throw (OidcTimeoutException) e;
            }
            return null;
        } finally {
            try {
                // Sleeping to ensure the server stays up long enough to respond to the browser's
                // request after the redirect.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log(Level.WARNING, "Thread interrupted " + e.getMessage());
            }
            server.stop();
        }
    }

    /**
     * Opens the specified URI in the default web browser, supporting macOS, Windows, and
     * Linux/Unix. This method uses platform-specific commands to invoke the browser.
     *
     * @param url the URL to be opened as a string
     * @throws Exception if no supported browser is found or an error occurs while attempting to
     *     open the URL
     */
    private void openURL(String url) throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        logger.log(Level.INFO, "osName: " + osName);
        Runtime runtime = Runtime.getRuntime();

        if (osName.contains("windows")) {
            runtime.exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", url});
        } else if (osName.contains("mac os")) {
            runtime.exec(new String[] {"open", url});
        } else {
            String[] browsers = {"xdg-open", "firefox", "google-chrome"};
            IOException lastError = null;
            for (String browser : browsers) {
                try {
                    // Check if browser exists
                    Process process = runtime.exec(new String[] {"which", browser});
                    if (process.waitFor() == 0) {
                        runtime.exec(new String[] {browser, url});
                    }
                } catch (IOException e) {
                    lastError = e;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            throw lastError != null
                    ? lastError
                    : new IOException("No web browser found to open the URL");
        }
    }

    private void log(Level level, String message) {
        if (mongoLogger != null) {
            mongoLogger.log(level, message);
        } else {
            logger.log(level, message);
        }
    }

    public OidcCallbackResult doRefresh(OidcCallbackContext callbackContext)
            throws RefreshFailedException {
        IdpInfo idpServerInfo = callbackContext.getIdpInfo();
        String clientID = idpServerInfo.getClientId();
        String issuerURI = idpServerInfo.getIssuer();

        // Check that the IdP information is valid
        if (!validateIdpInfo(idpServerInfo, clientID, issuerURI)) {
            return null;
        }
        try {
            // Use OpenID Connect Discovery to fetch the provider metadata
            OIDCProviderMetadata providerMetadata =
                    OIDCProviderMetadata.resolve(new Issuer(issuerURI));
            URI tokenEndpoint = providerMetadata.getTokenEndpointURI();

            // This function will never be called without a refresh token (to be checked in the driver function),
            // but we throw an exception to be explicit about the fact that we expect a refresh token.
            String refreshToken = callbackContext.getRefreshToken();
            if (refreshToken == null) {
                throw new IllegalArgumentException("Refresh token is required");
            }

            RefreshTokenGrant refreshTokenGrant =
                    new RefreshTokenGrant(new RefreshToken(refreshToken));
            TokenRequest tokenRequest =
                    new TokenRequest(tokenEndpoint, new ClientID(clientID), refreshTokenGrant);
            HTTPResponse httpResponse = tokenRequest.toHTTPRequest().send();

            try {
                TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpResponse);
                if (!tokenResponse.indicatesSuccess()) {
                    TokenErrorResponse errorResponse = tokenResponse.toErrorResponse();
                    String errorCode =
                            errorResponse.getErrorObject() != null
                                    ? errorResponse.getErrorObject().getCode()
                                    : null;
                    String errorDescription =
                            errorResponse.getErrorObject() != null
                                    ? errorResponse.getErrorObject().getDescription()
                                    : null;
                    throw new RefreshFailedException(
                            "Token refresh failed with error: "
                                    + "code="
                                    + errorCode
                                    + ", description="
                                    + errorDescription);
                }
                return getOidcCallbackResultFromTokenResponse((OIDCTokenResponse) tokenResponse);
            } catch (ParseException e) {
                throw new RefreshFailedException(
                        "Failed to parse server response: "
                                + e.getMessage()
                                + " [response="
                                + httpResponse.getBody()
                                + "]");
            }

        } catch (Exception e) {
            log(Level.SEVERE, "OpenID Connect: Error during token refresh. " + e.getMessage());
            if (e instanceof RefreshFailedException) {
                throw (RefreshFailedException) e;
            }
            return null;
        }
    }

    private boolean validateIdpInfo(IdpInfo idpInfo, String clientID, String issuerURI) {
        if (idpInfo == null) {
            log(Level.SEVERE, "IdpServerInfo is null");
            return false;
        }
        if (clientID == null || clientID.isEmpty()) {
            log(Level.SEVERE, "Client ID is null or empty");
            return false;
        }
        if (!issuerURI.startsWith("https")) {
            log(Level.SEVERE, "Issuer URI must be HTTPS");
            return false;
        }
        return true;
    }

    private OidcCallbackResult getOidcCallbackResultFromTokenResponse(
            OIDCTokenResponse tokenResponse) {
        Tokens tokens = tokenResponse.getOIDCTokens();
        String accessToken = tokens.getAccessToken().getValue();
        String refreshToken =
                tokens.getRefreshToken() != null ? tokens.getRefreshToken().getValue() : null;
        Duration expiresIn = Duration.ofSeconds(tokens.getAccessToken().getLifetime());

        return new OidcCallbackResult(accessToken, expiresIn, refreshToken);
    }
}
