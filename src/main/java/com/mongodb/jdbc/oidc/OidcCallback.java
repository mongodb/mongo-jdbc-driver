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

import com.mongodb.jdbc.logging.MongoLogger;
import javax.security.auth.RefreshFailedException;

// TODO: This class is a placeholder for the OidcCallback,
//       it will be removed when Java Driver OIDC support is added.
public class OidcCallback {
    private final OidcAuthFlow oidcAuthFlow;

    public OidcCallback() {
        this.oidcAuthFlow = new OidcAuthFlow();
    }

    public OidcCallback(MongoLogger parentLogger) {
        this.oidcAuthFlow = new OidcAuthFlow(parentLogger);
    }

    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext)
            throws RefreshFailedException, OidcTimeoutException {
        String refreshToken = callbackContext.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            return oidcAuthFlow.doRefresh(callbackContext);
        } else {
            return oidcAuthFlow.doAuthCodeFlow(callbackContext);
        }
    }
}
