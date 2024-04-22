package com.mongodb.jdbc.oidc;

import com.mongodb.jdbc.logging.MongoLogger;

import javax.security.auth.RefreshFailedException;

// TODO: This class is a placeholder for the OidcCallback,
//       it will be removed when Java Driver OIDC support is added.
public class OidcCallback {
    private final OidcAuthFlow oidcAuthFlow;

    public OidcCallback() {
        this.oidcAuthFlow = new OidcAuthFlow();
    }

    public OidcCallback(MongoLogger parentLogger) {
        this.oidcAuthFlow = new OidcAuthFlow(parentLogger);
    }

    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext) throws RefreshFailedException, OidcTimeoutException {
        String refreshToken = callbackContext.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            return oidcAuthFlow.doRefresh(callbackContext);
        } else {
            return oidcAuthFlow.doAuthCodeFlow(callbackContext);
        }
    }
}
