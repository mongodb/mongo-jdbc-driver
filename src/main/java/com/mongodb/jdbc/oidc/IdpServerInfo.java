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

import java.util.List;

// TODO: This class is a placeholder for the IdpServerInfo,
//       it will be removed when Java Driver OIDC support is added.
public class IdpServerInfo {
    private String issuer;
    private String clientId;
    private List<String> requestScopes;

    public IdpServerInfo(String issuer, String clientId, List<String> requestScopes) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.requestScopes = requestScopes;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public List<String> getRequestScopes() {
        return requestScopes;
    }
}
