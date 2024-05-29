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

import com.mongodb.MongoCredential.OidcCallback;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.JdbcOidcCallback;
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcTimeoutException;
import java.time.Duration;

public class TestOidcCallbackWithShortTimeout {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback();

        Duration shortTimeout = Duration.ofSeconds(2); // intentionally short to trigger timeout
        OidcCallbackContext context =
                new JdbcOidcCallbackContext(shortTimeout, 1, null, TestOidcUtils.IDP_INFO, null);

        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            // Timeout is expected when user input is required as it should take longer than 2 second.
            // It may pass if the user is already signed in and credentials are saved in the browser.
            System.out.println(
                    "This should not print, timeout expected. Sign out of the IdP or clear the browser cache "
                            + "to trigger a timeout.");
            System.out.println(result);
        } catch (Exception e) {
            if (e.getCause() instanceof OidcTimeoutException) {
                System.err.println(
                        "Expected OidcTimeoutException occurred: " + e.getCause().getMessage());
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
