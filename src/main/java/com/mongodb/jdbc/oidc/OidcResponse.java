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

public class OidcResponse {
    private String code;
    private String state;
    private String error;
    private String errorDescription;

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (code != null) {
            sb.append("Code: ").append(code).append("\n");
        }
        if (state != null) {
            sb.append("State: ").append(state).append("\n");
        }
        if (error != null) {
            sb.append("Error: ").append(error).append("\n");
        }
        if (errorDescription != null) {
            sb.append("Error Description: ").append(errorDescription).append("\n");
        }
        return sb.toString();
    }
}
