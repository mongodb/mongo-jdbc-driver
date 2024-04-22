package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.OidcCallback;
import com.mongodb.jdbc.oidc.OidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcCallbackResult;
import com.mongodb.jdbc.oidc.OidcTimeoutException;

import java.time.Duration;

public class TestOidcCallbackWithShortTimeout {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new OidcCallback();

        Duration shortTimeout = Duration.ofSeconds(2); // intentionally short to trigger timeout
        OidcCallbackContext context = new OidcCallbackContext(shortTimeout, 1, null, TestOidcUtils.IDP_INFO);

        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            // Timeout is expected when user input is required as it should take longer than 2 second.
            // It may pass if the user is already signed in and credentials are saved in the browser.
            System.out.println("This should not print, timeout expected. Sign out of the IdP to trigger a timeout.");
            System.out.println(result);
        } catch (OidcTimeoutException e) {
            System.err.println("Expected timeout occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
