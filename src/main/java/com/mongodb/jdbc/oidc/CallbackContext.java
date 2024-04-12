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

// TODO: This class is a placeholder for the CallbackContext,
//       it will be removed when Java Driver OIDC support is added.
public class CallbackContext {
    private Long timeoutSeconds;
    private int version;
    private String refreshToken;
    private IdpServerInfo idpInfo;

    public CallbackContext(
            Long timeoutSeconds, int version, String refreshToken, IdpServerInfo idpInfo) {
        this.timeoutSeconds = timeoutSeconds;
        this.version = version;
        this.refreshToken = refreshToken;
        this.idpInfo = idpInfo;
    }

    public Long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public int getVersion() {
        return version;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public IdpServerInfo getIdpInfo() {
        return idpInfo;
    }
}
