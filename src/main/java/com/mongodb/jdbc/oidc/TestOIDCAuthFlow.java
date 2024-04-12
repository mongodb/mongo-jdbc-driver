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

import java.util.Collections;

public class TestOIDCAuthFlow {
    public static void main(String[] args) {
        OIDCAuthFlow authFlow = new OIDCAuthFlow();

        IdpServerInfo idpServerInfo =
                new IdpServerInfo(
                        "https://mongodb-dev.okta.com/oauth2/ausqrxbcr53xakaRR357",
                        "0oarvap2r7PmNIBsS357",
                        Collections.singletonList("openid"));
        CallbackContext callbackContext = new CallbackContext(null, 1, null, idpServerInfo);

        try {
            OIDCCredential response = authFlow.doAuthCodeFlow(callbackContext);
            if (response != null) {
                System.out.println("Access Token: " + response.getAccessToken());
                System.out.println("Expires: " + response.getExpires());
                System.out.println("Refresh Token: " + response.getRefreshToken());
            } else {
                System.out.println("Authentication failed.");
            }
        } catch (Exception e) {
            System.err.println(
                    "An error occurred while running the OIDC authentication flow: "
                            + e.getMessage());
            e.printStackTrace();
        }
    }
}
