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
import javax.security.auth.RefreshFailedException;

public class TestOidcCallbackWithBadRefreshToken {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback();

        String badRefreshToken = "bad-refresh-token";
        OidcCallbackContext context =
                new JdbcOidcCallbackContext(null, 1, badRefreshToken, TestOidcUtils.IDP_INFO, null);

        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            System.out.println("This should not print, bad refresh token expected to fail.");
            System.out.println(result);
        } catch (Exception e) {
            if (e.getCause() instanceof RefreshFailedException) {
                System.err.println(
                        "Expected RefreshFailedException occurred: " + e.getCause().getMessage());
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
