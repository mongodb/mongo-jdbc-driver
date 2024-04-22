package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.IdpInfo;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import com.mongodb.jdbc.oidc.OidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcCallbackResult;

import java.util.Collections;
import java.util.List;

public class TestOidcUtils {

    public static String OIDC_ISSUER = "https://mongodb-dev.okta.com/oauth2/ausqrxbcr53xakaRR357";
    public static String OIDC_CLIENT_ID = "0oarvap2r7PmNIBsS357";
    public static final List<String> OPENID_SCOPE = Collections.singletonList("openid");

    public static final IdpInfo IDP_INFO = new IdpInfo(OIDC_ISSUER, OIDC_CLIENT_ID, OPENID_SCOPE);



    public static OidcCallbackResult testAuthCodeFlow(
            OidcCallbackContext callbackContext, OidcAuthFlow authFlow) {

        try {
            OidcCallbackResult result = authFlow.doAuthCodeFlow(callbackContext);
            if (result != null) {
                System.out.println("Access Token: " + result.getAccessToken());
                System.out.println("Expires In: " + result.getExpiresIn());
                System.out.println("Refresh Token: " + result.getRefreshToken());
                return result;
            } else {
                System.out.println("Authentication failed.");
            }
        } catch (Exception e) {
            System.err.println(
                    "An error occurred while running the OIDC authentication flow: "
                            + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
