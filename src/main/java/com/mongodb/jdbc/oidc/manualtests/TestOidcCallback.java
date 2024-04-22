package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.OidcCallback;
import com.mongodb.jdbc.oidc.OidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcCallbackResult;

public class TestOidcCallback {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new OidcCallback();

        OidcCallbackContext initialContext = new OidcCallbackContext(null, 1, null, TestOidcUtils.IDP_INFO);
        try {
            OidcCallbackResult initialResult = oidcCallback.onRequest(initialContext);
            if (initialResult != null) {
                System.out.println("Access Token: " + initialResult.getAccessToken());
                System.out.println("Expires In: " + initialResult.getExpiresIn());
                System.out.println("Refresh Token: " + initialResult.getRefreshToken());
            } else {
                System.out.println("Authentication failed.");
            }
            OidcCallbackContext refreshContext = new OidcCallbackContext(null, 1, initialResult.getRefreshToken(), TestOidcUtils.IDP_INFO);
            OidcCallbackResult refreshResult = oidcCallback.onRequest(refreshContext);
            if (refreshResult != null) {
                System.out.println("Refreshed Access Token: " + refreshResult.getAccessToken());
                System.out.println("Refreshed Expires In: " + refreshResult.getExpiresIn());
                System.out.println(
                        "Refreshed Refresh Token: " + refreshResult.getRefreshToken());
            } else {
                System.out.println("Refresh token flow failed.");
            }
        } catch (Exception e) {
            System.err.println("Error during OIDC callback test: " + e.getMessage());
        }
    }
}
