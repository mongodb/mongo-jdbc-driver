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

package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.jdbc.MongoDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Checks, from the server's side, that the driver's MongoClients don't leave connections open after
 * the JDBC connections using them are closed.
 *
 * <p>Each test tags its connections with a unique clientInfo, which the driver puts in the appName,
 * and counts the server connections carrying that tag.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientCacheIntegrationTest {
    private static final int CYCLES = 5;

    // Plain MongoClients used to count connections on each local server, keyed by the env var
    // holding that server's port.
    private final Map<String, MongoClient> observers = new HashMap<>();

    @BeforeAll
    public void setUp() {
        for (String portEnvVar : Arrays.asList("LOCAL_MDB_PORT_ENT", "LOCAL_MDB_PORT_COM")) {
            observers.put(
                    portEnvVar, MongoClients.create("mongodb://localhost:" + getEnv(portEnvVar)));
        }
    }

    // Start each test with an empty client cache. Idle clients cached by earlier tests would
    // otherwise be the first ones evicted.
    @BeforeEach
    public void closeCachedClients() {
        MongoDriver.closeAllClients();
    }

    @AfterAll
    public void tearDown() {
        for (MongoClient observer : observers.values()) {
            observer.close();
        }
    }

    private static String getEnv(String name) {
        String value = System.getenv(name);
        assertNotNull(value, name + " variable not set in environment");
        return value;
    }

    private static String localUri(String portEnvVar) {
        return "jdbc:mongodb://localhost:" + getEnv(portEnvVar) + "/test";
    }

    private static Properties connectionProperties(String tag, boolean disableClientCache) {
        Properties p = new Properties();
        p.setProperty("user", getEnv("LOCAL_MDB_USER"));
        p.setProperty("password", getEnv("LOCAL_MDB_PWD"));
        p.setProperty("authSource", "test");
        p.setProperty("database", "test");
        p.setProperty("clientInfo", tag + "+1.0");
        if (disableClientCache) {
            p.setProperty("disableclientcache", "true");
        }
        return p;
    }

    private static String uniqueTag(String prefix) {
        // Letters and digits only, so the tag can be used as-is in a $regex.
        return prefix + System.nanoTime();
    }

    /**
     * Counts the server connections whose appName matches tagPattern, grouped by the clientInfo tag
     * in the appName. Tags without connections are absent.
     */
    private Map<String, Long> serverConnectionsPerTag(String portEnvVar, String tagPattern) {
        Map<String, Long> counts = new HashMap<>();
        for (Document group :
                observers
                        .get(portEnvVar)
                        .getDatabase("admin")
                        .aggregate(
                                Arrays.asList(
                                        new Document(
                                                "$currentOp",
                                                new Document("allUsers", true)
                                                        .append("idleConnections", true)),
                                        new Document(
                                                "$match",
                                                new Document(
                                                        "appName",
                                                        new Document("$regex", tagPattern))),
                                        new Document(
                                                "$group",
                                                new Document("_id", "$appName")
                                                        .append("n", new Document("$sum", 1)))))) {
            // The appName ends with "|<tag>+1.0", from the clientInfo property.
            String appName = group.getString("_id");
            String tag = appName.substring(appName.lastIndexOf('|') + 1, appName.lastIndexOf('+'));
            counts.merge(tag, ((Number) group.get("n")).longValue(), Long::sum);
        }
        return counts;
    }

    /**
     * Monitors open their connections asynchronously and the server notices closed sockets a little
     * later, so wait until the counts stop changing before using them.
     */
    private Map<String, Long> settledServerConnectionsPerTag(String portEnvVar, String tagPattern)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + 15_000;
        Map<String, Long> previous = serverConnectionsPerTag(portEnvVar, tagPattern);
        int unchangedReads = 0;
        while (unchangedReads < 3 && System.currentTimeMillis() < deadline) {
            Thread.sleep(500);
            Map<String, Long> current = serverConnectionsPerTag(portEnvVar, tagPattern);
            unchangedReads = current.equals(previous) ? unchangedReads + 1 : 0;
            previous = current;
        }
        return previous;
    }

    private long settledServerConnections(String portEnvVar, String tag)
            throws InterruptedException {
        return settledServerConnectionsPerTag(portEnvVar, tag)
                .values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    // Tags <prefix>k0 through <prefix>k<count - 1>, for connections with distinct settings.
    private static List<String> numberedTags(String prefix, int count) {
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tags.add(prefix + "k" + i);
        }
        return tags;
    }

    private static void forceGarbageCollection() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }

    private static void openAndClose(String uri, Properties p) throws SQLException {
        try (Connection conn = DriverManager.getConnection(uri, p)) {
            conn.getMetaData().getDriverVersion();
        }
    }

    /**
     * Reconnecting with the same settings after the previous connections were closed and garbage
     * collected must reuse the cached client, not add another client's connections.
     */
    @Test
    public void testCachedClientDoesNotLeakConnectionsAfterGarbageCollection() throws Exception {
        String tag = uniqueTag("cachegc");
        String uri = localUri("LOCAL_MDB_PORT_ENT");
        Properties p = connectionProperties(tag, false);

        openAndClose(uri, p);
        forceGarbageCollection();
        long afterFirstCycle = settledServerConnections("LOCAL_MDB_PORT_ENT", tag);
        assertTrue(afterFirstCycle > 0, "Expected the cached client to hold server connections.");

        for (int i = 1; i < CYCLES; i++) {
            openAndClose(uri, p);
            forceGarbageCollection();
        }

        assertEquals(
                afterFirstCycle,
                settledServerConnections("LOCAL_MDB_PORT_ENT", tag),
                "Server connections grew across connect/close/GC cycles with identical settings.");
    }

    /** With the cache disabled, closing a connection must close its client's server connections. */
    @Test
    public void testDisableClientCacheReleasesServerConnections() throws Exception {
        String tag = uniqueTag("nocache");
        String uri = localUri("LOCAL_MDB_PORT_ENT");
        Properties p = connectionProperties(tag, true);

        for (int i = 0; i < CYCLES; i++) {
            openAndClose(uri, p);
        }

        assertEquals(0, settledServerConnections("LOCAL_MDB_PORT_ENT", tag));
    }

    /**
     * A connection that fails validation (here, because the server is the community edition) must
     * not leave its client's server connections open.
     */
    @Test
    public void testFailedConnectionValidationReleasesServerConnections() throws Exception {
        String tag = uniqueTag("failedconnect");
        String uri = localUri("LOCAL_MDB_PORT_COM");
        Properties p = connectionProperties(tag, true);

        for (int i = 0; i < CYCLES; i++) {
            assertThrows(SQLException.class, () -> DriverManager.getConnection(uri, p));
        }

        assertEquals(0, settledServerConnections("LOCAL_MDB_PORT_COM", tag));
    }

    /**
     * Past MAX_CACHED_CLIENTS, the cache closes its least recently used idle clients, which closes
     * their server connections.
     */
    @Test
    public void testCacheClosesLeastRecentlyUsedIdleClientsOverLimit() throws Exception {
        String prefix = uniqueTag("lru");
        String uri = localUri("LOCAL_MDB_PORT_ENT");
        int overLimit = 3;
        List<String> tags = numberedTags(prefix, MongoDriver.MAX_CACHED_CLIENTS + overLimit);
        for (String tag : tags) {
            openAndClose(uri, connectionProperties(tag, false));
        }

        assertEquals(
                new HashSet<>(tags.subList(overLimit, tags.size())),
                settledServerConnectionsPerTag("LOCAL_MDB_PORT_ENT", prefix).keySet(),
                "Only the most recently used clients should still hold server connections.");
    }

    /**
     * The cache never closes a client in use, even past MAX_CACHED_CLIENTS. Once its connection
     * closes, the cache closes that client to get back within the limit.
     */
    @Test
    public void testCacheKeepsClientsInUseOverLimit() throws Exception {
        String prefix = uniqueTag("inuse");
        String uri = localUri("LOCAL_MDB_PORT_ENT");
        List<String> tags = numberedTags(prefix, MongoDriver.MAX_CACHED_CLIENTS + 1);
        List<Connection> connections = new ArrayList<>();
        try {
            for (String tag : tags) {
                connections.add(DriverManager.getConnection(uri, connectionProperties(tag, false)));
            }
            assertEquals(
                    new HashSet<>(tags),
                    settledServerConnectionsPerTag("LOCAL_MDB_PORT_ENT", prefix).keySet(),
                    "Every client in use should still hold server connections.");

            connections.get(0).close();
            assertEquals(
                    new HashSet<>(tags.subList(1, tags.size())),
                    settledServerConnectionsPerTag("LOCAL_MDB_PORT_ENT", prefix).keySet(),
                    "The client that became idle should have been closed.");
            for (Connection conn : connections.subList(1, connections.size())) {
                assertTrue(conn.isValid(5), "A connection whose client is cached stopped working.");
            }
        } finally {
            for (Connection conn : connections) {
                conn.close();
            }
        }
    }
}
