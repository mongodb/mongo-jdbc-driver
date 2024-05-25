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

import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.time.Duration;

public class TestOidcAuthFlow {
    public static void main(String[] args) {
        OidcAuthFlow authFlow = new OidcAuthFlow();

        Duration timeout = Duration.ofMinutes(5);
        OidcCallbackContext callbackContext =
                new JdbcOidcCallbackContext(timeout, 1, null, TestOidcUtils.IDP_INFO, null);

        TestOidcUtils.testAuthCodeFlow(callbackContext, authFlow);
    }
}
