package com.mongodb.jdbc.oidc;

public class OidcTimeoutException extends Exception {
    public OidcTimeoutException(String message) {
        super(message);
    }
}
