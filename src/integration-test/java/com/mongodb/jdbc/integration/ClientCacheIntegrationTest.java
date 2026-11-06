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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

    /** Counts the server connections whose appName contains tag, from the clientInfo property. */
    private long serverConnections(String portEnvVar, String tag) {
        Document count =
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
                                                        "appName", new Document("$regex", tag))),
                                        new Document("$count", "n")))
                        .first();
        return count == null ? 0 : ((Number) count.get("n")).longValue();
    }

    /**
     * Monitors open their connections asynchronously and the server notices closed sockets a little
     * later, so wait until the count stops changing before using it.
     */
    private long settledServerConnections(String portEnvVar, String tag)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + 15_000;
        long previous = serverConnections(portEnvVar, tag);
        int unchangedReads = 0;
        while (unchangedReads < 3 && System.currentTimeMillis() < deadline) {
            Thread.sleep(500);
            long current = serverConnections(portEnvVar, tag);
            unchangedReads = current == previous ? unchangedReads + 1 : 0;
            previous = current;
        }
        return previous;
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
     * not leave its client's server connections open, with or without the cache.
     */
    @Test
    public void testFailedConnectionValidationReleasesServerConnections() throws Exception {
        String uri = localUri("LOCAL_MDB_PORT_COM");
        for (boolean disableClientCache : new boolean[] {false, true}) {
            String tag = uniqueTag("failedconnect");
            Properties p = connectionProperties(tag, disableClientCache);

            for (int i = 0; i < CYCLES; i++) {
                assertThrows(SQLException.class, () -> DriverManager.getConnection(uri, p));
            }

            assertEquals(
                    0,
                    settledServerConnections("LOCAL_MDB_PORT_COM", tag),
                    "disableclientcache=" + disableClientCache);
        }
    }
}
