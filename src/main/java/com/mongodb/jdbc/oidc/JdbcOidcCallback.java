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

import com.mongodb.MongoCredential.OidcCallback;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.logging.MongoLogger;
import javax.security.auth.RefreshFailedException;

public class JdbcOidcCallback implements OidcCallback {
    private final OidcAuthFlow oidcAuthFlow;

    public JdbcOidcCallback() {
        this.oidcAuthFlow = new OidcAuthFlow();
    }

    public JdbcOidcCallback(MongoLogger parentLogger) {
        this.oidcAuthFlow = new OidcAuthFlow(parentLogger);
    }

    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext) {
        String refreshToken = callbackContext.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                return oidcAuthFlow.doRefresh(callbackContext);
            } catch (RefreshFailedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return oidcAuthFlow.doAuthCodeFlow(callbackContext);
            } catch (OidcTimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
