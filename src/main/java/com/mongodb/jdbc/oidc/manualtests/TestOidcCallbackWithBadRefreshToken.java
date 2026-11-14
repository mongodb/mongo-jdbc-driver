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
import com.mongodb.jdbc.oidc.OidcAuthFlow;

public class TestOidcCallbackWithBadRefreshToken {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback(new OidcAuthFlow(), TestOidcUtils.HOSTS);

        String badRefreshToken = "bad-refresh-token";
        OidcCallbackContext context =
                new JdbcOidcCallbackContext(null, 1, badRefreshToken, TestOidcUtils.IDP_INFO, null);

        // A refresh token that fails falls back to logging in through the browser.
        OidcCallbackResult result = oidcCallback.onRequest(context);
        if (result != null) {
            System.out.println("Logged in through the browser after the bad refresh token failed.");
        } else {
            System.err.println("Expected a browser login after the bad refresh token failed.");
        }
    }
}
