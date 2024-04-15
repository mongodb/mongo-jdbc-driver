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

import java.time.Duration;
import com.mongodb.lang.Nullable;

// TODO: This class is a placeholder for the OidcCallbackResult,
//       it will be removed when Java Driver OIDC support is added.
public class OidcCallbackResult {
    private final String accessToken;
    private final Duration expiresIn;
    @Nullable private final String refreshToken;

    public OidcCallbackResult(
            String accessToken, Duration expiresIn, @Nullable String refreshToken) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public OidcCallbackResult(String accessToken, Duration expiresIn) {
        this(accessToken, expiresIn, null);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Duration getExpiresIn() {
        return expiresIn;
    }

    @Nullable
    public String getRefreshToken() {
        return refreshToken;
    }
}
