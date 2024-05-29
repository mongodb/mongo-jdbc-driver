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

package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.JdbcIdpInfo;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.util.Collections;
import java.util.List;

public class TestOidcUtils {

    public static String OIDC_ISSUER = "https://mongodb-dev.okta.com/oauth2/ausqrxbcr53xakaRR357";
    public static String OIDC_CLIENT_ID = "0oarvap2r7PmNIBsS357";
    public static final List<String> OPENID_SCOPE = Collections.singletonList("openid");

    public static final IdpInfo IDP_INFO =
            new JdbcIdpInfo(OIDC_ISSUER, OIDC_CLIENT_ID, OPENID_SCOPE);

    public static OidcCallbackResult testAuthCodeFlow(
            OidcCallbackContext callbackContext, OidcAuthFlow authFlow) {

        try {
            OidcCallbackResult result = authFlow.doAuthCodeFlow(callbackContext);
            if (result != null) {
                System.out.println("Access Token: " + result.getAccessToken());
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
