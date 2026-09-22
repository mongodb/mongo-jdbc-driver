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

package com.mongodb.jdbc.sqlinterface;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.sqlinterface.status.AtlasClusterNameProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Unit tests for ensuring that the cluster name from atlas is correctly extracted. */
public class AtlasClusterNameProviderTest {
    public AtlasClusterNameProviderTest() {}

    @Test
    void clusterNameFromReplicaSetHostExtracts() {
        String uri = "cluster0-shard-00-00.abc123.mongodb.net:27017";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.of("cluster0"));
    }

    @Test
    void clusterNameFromReplicaSetHostWithoutPortExtracts() {
        String uri = "cluster1-shard-00-00.abc123.mongodb.net";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.of("cluster1"));
    }

    @Test
    void clusterNameFromMongosHostExtracts() {
        String uri = "cluster3-shard-00-mongos-g0.abc123.mongodb.net:27017";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.of("cluster3"));
    }

    @Test
    void clusterNameFromGovHostExtracts() {
        // Atlas for Government clusters live on the `.mongodbgov.net` domain and must be gated too.
        String uri = "cluster0-shard-00-00.abc123.mongodbgov.net:27017";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.of("cluster0"));
    }

    @Test
    void clusterNameFromNonAtlasHostEmpty() {
        String uri = "localhost:27017";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.empty());
    }

    @Test
    void emptyClusterNameEmpty() {
        String uri = "-shard-00-00.abc123.mongodb.net";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.empty());
    }

    @Test
    void mongoLikeHostEmpty() {
        String uri = "cluster3-shard-00-mongos-g0.abc123.fakemongodb.net:27017";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.empty());
    }

    @Test
    void emptyIsEmpty() {
        String uri = "";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.empty());
    }

    @Test
    void whitespaceIsEmpty() {
        String uri = " \t\n";

        assertEquals(AtlasClusterNameProvider.extractClusterName(uri), Optional.empty());
    }
}
