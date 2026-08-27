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

package com.mongodb.jdbc.auth;

import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Optional;
import org.bson.Document;

/** An implementation of a marker provider backed by an Atlas-compatible cluster */
public class AtlasMarkerProvider implements MarkerProvider {
    private static final String ENTITLEMENT_DATABASE = "__mdb_internal_sqlinterface";
    private static final String ENTITLEMENT_COLLECTION = "__sql_status";
    private static final String ENTITLEMENT_FIELD_ID = "entitlement";
    private static final String ENTITLEMENT_FIELD_MARKER = "token";

    private SignedJWT token = null;

    public AtlasMarkerProvider(MongoClient conn) {
        // Try to find the entitlement token
        MongoDatabase db = conn.getDatabase(ENTITLEMENT_DATABASE);
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
            return;
        }

        // Try to get the actual token
        SignedJWT marker;
        try {
            String markerRaw = markerDoc.getString(ENTITLEMENT_FIELD_MARKER);
            marker = SignedJWT.parse(markerRaw);
        } catch (ClassCastException e) {
            // The token was not a string, so we treat it as an invalid token
            return;
        } catch (ParseException e) {
            // The token was malformed, so we treat it as an invalid token
            return;
        }

        this.token = marker;
    }

    @Override
    public Optional<SignedJWT> getMarker() {
        return this.token != null ? Optional.of(this.token) : Optional.empty();
    }
}
