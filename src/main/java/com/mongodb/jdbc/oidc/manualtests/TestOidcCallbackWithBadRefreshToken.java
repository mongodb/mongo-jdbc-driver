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

import com.mongodb.jdbc.oidc.OidcCallback;
import com.mongodb.jdbc.oidc.OidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcCallbackResult;
import javax.security.auth.RefreshFailedException;

public class TestOidcCallbackWithBadRefreshToken {

    public static void main(String[] args) {
        OidcCallback oidcCallback = new OidcCallback();

        String badRefreshToken = "bad-refresh-token";
        OidcCallbackContext context =
                new OidcCallbackContext(null, 1, badRefreshToken, TestOidcUtils.IDP_INFO);

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
