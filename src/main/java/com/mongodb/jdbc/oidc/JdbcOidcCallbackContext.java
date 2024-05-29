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

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallbackContext;
import java.time.Duration;

public class JdbcOidcCallbackContext implements OidcCallbackContext {
    private Duration timeout;
    private int version;
    private String refreshToken;
    private IdpInfo idpInfo;
    private String userName;

    public JdbcOidcCallbackContext(
            Duration timeout, int version, String refreshToken, IdpInfo idpInfo, String userName) {
        this.timeout = timeout;
        this.version = version;
        this.refreshToken = refreshToken;
        this.idpInfo = idpInfo;
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public int getVersion() {
        return this.version;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public IdpInfo getIdpInfo() {
        return this.idpInfo;
    }
}
