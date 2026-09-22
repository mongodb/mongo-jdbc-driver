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

import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.jdbc.sqlinterface.exception.SQLInterfaceStatusException;
import java.util.Optional;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;

/**
 * Atlas-backed provider for a cluster's name
 *
 * <p>This uses the `hello.me` command to fetch the name of the currently connected cluster, if
 * within an atlas context.
 */
public class AtlasClusterNameProvider {
    /**
     * Extracts the cluster's lowercase canonical name from a `hello.me` hostname, which on an Atlas
     * dedicated cluster is `[cluster-name]-shard-[...].[hash].mongodb.net:[port]` (commercial Atlas)
     * or `[cluster-name]-shard-[...].[hash].mongodbgov.net:[port]` (Atlas for Government). If the
     * supplied uri does not match that pattern exactly, we assume that the backing connection is not
     * one of Atlas.
     *
     * @param uri A URI in the shape of the output of a `hello.me` message
     * @return The corresponding cluster name, or `Optional.empty`
     */
    public static Optional<String> extractClusterName(String uri) {
        // If the `hello.me` field is empty, then we're not in an Atlas context.
        if (uri == null) {
            return Optional.empty();
        }

        String host = uri.toLowerCase().split(":")[0];

        // Ensure that the domain is an atlas one (commercial or Atlas for Government)
        if (!host.endsWith(".mongodb.net") && !host.endsWith(".mongodbgov.net")) {
            return Optional.empty();
        }

        // Try to get just the name
        String[] hostParts = host.split("-shard-");
        if (hostParts.length < 2) {
            return Optional.empty();
        }

        String cluster = hostParts[0].trim();
        return cluster.isEmpty() ? Optional.empty() : Optional.of(cluster);
    }

    /**
     * Get the cluster's name, returning `Optional.empty` if there isn't one for the supplied
     * MongoDB connection
     *
     * @return The name of the cluster, if applicable
     * @throws SQLInterfaceStatusException if running a command against the mongo client fails
     */
    public static Optional<String> getClusterName(MongoClient client)
            throws SQLInterfaceStatusException {
        try {
            // Get the cluster name
            Document hello =
                    client.getDatabase("admin")
                            .withReadPreference(ReadPreference.primary())
                            .runCommand(new BsonDocument("hello", new BsonInt32(1)));

            // Attempt to extract the cluster's name
            Optional<String> clusterName;
            try {
                String me = hello.getString("me");
                clusterName = extractClusterName(me);
            } catch (ClassCastException e) {
                return Optional.empty();
            }

            return clusterName;
        } catch (MongoException e) {
            throw new SQLInterfaceStatusException(e.getMessage(), e);
        }
    }
}
