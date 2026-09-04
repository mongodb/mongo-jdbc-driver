/*
 * Copyright 2026-present MongoDB, Inc.
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

package com.mongodb.jdbc.sqlinterface.status;

/** Valid issuers from mongo-minted SQL Interface entitlement markers. */
public enum MongoIssuer {
    EMERGENCY {
        @Override
        public String toIssuer() {
            return EMERGENCY_ISSUER;
        }
    },
    SERVICE {
        @Override
        public String toIssuer() {
            return SERVICE_ISSUER;
        }
    };

    private static final String EMERGENCY_ISSUER = "mongosql-emergency";
    private static final String SERVICE_ISSUER = "mongosql-service";

    /**
     * Returns the issuer value corresponding for this issuer
     *
     * @return the issuer string value
     */
    public abstract String toIssuer();

    /**
     * Attempts to create a MongoIssuer from its string representation
     *
     * @param issuer The string representation of the issuer
     * @return The corresponding MongoIssuer
     * @throws IllegalArgumentException on unknown issuers
     */
    public static MongoIssuer fromString(String issuer) throws IllegalArgumentException {
        if (issuer.equals(EMERGENCY_ISSUER)) {
            return MongoIssuer.EMERGENCY;
        } else if (issuer.equals(SERVICE_ISSUER)) {
            return MongoIssuer.SERVICE;
        } else {
            throw new IllegalArgumentException(String.format("Invalid issuer: %s", issuer));
        }
    }
}
