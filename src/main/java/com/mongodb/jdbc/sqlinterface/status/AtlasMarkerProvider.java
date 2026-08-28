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

import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.jdbc.sqlinterface.exception.SQLInterfaceStatusException;
import com.mongodb.jdbc.sqlinterface.exception.SQLInterfaceStatusInvalidException;
import com.mongodb.jdbc.sqlinterface.exception.SQLInterfaceStatusUnavailableException;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An implementation of a marker provider backed by an Atlas-compatible cluster */
public class AtlasMarkerProvider {
    private static final Logger log = LoggerFactory.getLogger(AtlasMarkerProvider.class);

    private static final String ENTITLEMENT_DATABASE = "__mdb_internal_sqlinterface";
    private static final String ENTITLEMENT_COLLECTION = "__sql_status";
    private static final String ENTITLEMENT_FIELD_ID = "entitlement";
    private static final String ENTITLEMENT_FIELD_MARKER = "token";

    /**
     * Get the entitlement marker from the connected MongoDB instance
     *
     * @param client The connection to an Atlas instance
     * @return The entitlement marker
     * @throws SQLInterfaceStatusException If the marker is missing or invalid
     */
    public static SignedJWT getMarker(MongoClient client) throws SQLInterfaceStatusException {
        // Try to find the entitlement token
        MongoDatabase db = client.getDatabase(ENTITLEMENT_DATABASE);
        MongoCollection<Document> collection =
                db.getCollection(ENTITLEMENT_COLLECTION)
                        .withReadPreference(ReadPreference.primary());

        Document markerDoc =
                collection
                        .find(Filters.eq("_id", ENTITLEMENT_FIELD_ID))
                        .projection(
                                Projections.fields(
                                        Projections.include(ENTITLEMENT_FIELD_MARKER),
                                        Projections.exclude("_id")))
                        .first();
        if (markerDoc == null) {
            log.warn("No entitlement marker found");
            throw new SQLInterfaceStatusUnavailableException();
        }

        // Try to get the actual token
        try {
            String markerRaw = markerDoc.getString(ENTITLEMENT_FIELD_MARKER);
            return SignedJWT.parse(markerRaw);
        } catch (ClassCastException e) {
            log.warn("Entitlement marker's token field is not a string", e);
            throw new SQLInterfaceStatusInvalidException();
        } catch (ParseException e) {
            log.warn("Could not parse entitlement marker", e);
            throw new SQLInterfaceStatusInvalidException();
        }
    }
}
