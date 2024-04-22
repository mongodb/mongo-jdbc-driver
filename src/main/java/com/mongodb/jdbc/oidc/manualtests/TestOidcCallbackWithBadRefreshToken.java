package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.OidcCallback;
import com.mongodb.jdbc.oidc.OidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcCallbackResult;

import javax.security.auth.RefreshFailedException;

public class TestOidcCallbackWithBadRefreshToken {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new OidcCallback();

        String badRefreshToken = "bad-refresh-token";
        OidcCallbackContext context = new OidcCallbackContext(null, 1, badRefreshToken, TestOidcUtils.IDP_INFO);

        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            System.out.println("This should not print, bad refresh token expected to fail.");
            System.out.println(result);
        } catch (RefreshFailedException e) {
            System.err.println("Expected failure with bad refresh token: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
