/*
 * Copyright 2022-present MongoDB, Inc.
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

import static com.mongodb.jdbc.MongoDriver.MongoJDBCProperty.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.mongodb.jdbc.MongoConnection;
import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.TestEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ADFIntegrationTest {
    private static final String CURRENT_DIR =
            Paths.get(".").toAbsolutePath().normalize().toString();

    private static final String LOCAL_HOST = "jdbc:mongodb://localhost";
    private static final String URL =
            System.getenv("ADF_TEST_LOCAL_HOST") != null
                    ? System.getenv("ADF_TEST_LOCAL_HOST")
                    : LOCAL_HOST;
    static final String DEFAULT_TEST_DB = "integration_test";
    public static final String TEST_DIRECTORY = "resources/integration_test/tests";
    private static final String EXPECTED_UUID =
            "{\"$uuid\":\"71bf369b-2c60-4e6f-b23f-f9e88167cc96\"}";
    private static final String[] UUID_REPRESENTATIONS = {
        "standard", "javalegacy", "csharplegacy", "pythonlegacy", "default"
    };
    private static final String UUID_COLLECTION = "uuid";

    private static List<TestEntry> testEntries;

    /**
     * Creates a new connection.
     *
     * @param extraProps Extra properties on top of the default ones that the class implementating
     *     it is using.
     * @return The connection.
     * @throws SQLException If the connection can not be created.
     */
    public MongoConnection getBasicConnection(Properties extraProps) throws SQLException {
        return getBasicConnection(DEFAULT_TEST_DB, extraProps);
    }

    public MongoConnection getBasicConnection(String db, Properties extraProps)
            throws SQLException {
        return getBasicConnection(db, extraProps, null);
    }

    public MongoConnection getBasicConnection(String db, Properties extraProps, String uriOptions)
            throws SQLException {
        String fullUrl = URL;
        Properties p = new java.util.Properties(extraProps);
        p.setProperty("user", System.getenv("ADF_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADF_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADF_TEST_LOCAL_AUTH_DB"));
        p.setProperty("database", db);
        p.setProperty("ssl", "false");

        if (uriOptions != null && !uriOptions.isEmpty()) {
            fullUrl += (URL.contains("?") ? "&" : "/?") + uriOptions;
        }

        return (MongoConnection) DriverManager.getConnection(fullUrl, p);
    }

    @BeforeAll
    public static void loadTestConfigs() throws IOException {
        testEntries = IntegrationTestUtils.loadTestConfigs(TEST_DIRECTORY);
    }

    @TestFactory
    Collection<DynamicTest> runIntegrationTests() {
        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (TestEntry testEntry : testEntries) {
            if (testEntry.skip_reason != null) {
                continue;
            }
            dynamicTests.add(
                    DynamicTest.dynamicTest(
                            testEntry.description,
                            () -> {
                                try (Connection conn = getBasicConnection(testEntry.db, null)) {
                                    IntegrationTestUtils.runTest(testEntry, conn, false);
                                }
                            }));
        }
        return dynamicTests;
    }

    /** Simple callable used to spawn a new statement and execute a query. */
    public static class SimpleQueryExecutor implements Callable<Void> {
        private final Connection conn;
        private final String query;

        public SimpleQueryExecutor(Connection conn, String query) {
            this.conn = conn;
            this.query = query;
        }

        @Override
        public Void call() throws Exception {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery(query);
            }
            return null;
        }
    }

    /**
     * Verifies that concurrent connections and statements are not impacting the logging
     * capabilities. Concurrent connection must be able to create log files with no issues.
     * Concurrent statements must be able to write their messages in the same log file.
     */
    @Test
    public void testLoggingWithParallelConnectionAndStatementExec() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Callable<Void>> tasks = new ArrayList<>();

        // Connection with no logging.
        MongoConnection noLogging = connect(null);
        // Connection only logging exceptions.
        MongoConnection logErrorOnly = connect(Level.SEVERE);
        // Connection logging all public method entries of the JDBC interface.
        MongoConnection logEntries = connect(Level.FINER);
        try {
            addSimpleQueryExecTasks(tasks, noLogging);
            addSimpleQueryExecTasks(tasks, logErrorOnly);
            addSimpleQueryExecTasks(tasks, logEntries);
            executor.invokeAll(tasks);
        } finally {
            executor.awaitTermination(1, TimeUnit.SECONDS);

            // Verify that there is only one log file
            List<File> logFiles =
                    Files.list(Paths.get(CURRENT_DIR))
                            .map(Path::toFile)
                            .filter(
                                    p ->
                                            p.isFile()
                                                    && p.getName()
                                                            .matches("connection.log(.\\d+)*"))
                            .collect(Collectors.toList());

            assertEquals(
                    1, logFiles.size(), "Expected only one log file, but found " + logFiles.size());

            if (noLogging != null) {
                cleanUp(noLogging);
            }
            if (logErrorOnly != null) {
                cleanUp(logErrorOnly);
            }
            if (logEntries != null) {
                cleanUp(logEntries);
            }
        }
    }

    /**
     * Connect with the given logging level.
     *
     * @param logLevel The log level or null if not logging.
     * @return the connection.
     * @throws SQLException If an error occurs during the connection process.
     */
    private MongoConnection connect(Level logLevel) throws SQLException {
        Properties loggingProps = new Properties();
        if (null != logLevel) {
            loggingProps.setProperty(LOG_LEVEL.getPropertyName(), logLevel.getName());
        }

        // Log files will be created in the current directory
        loggingProps.setProperty(LOG_DIR.getPropertyName(), CURRENT_DIR);
        return getBasicConnection(loggingProps);
    }

    /**
     * Add taks to execute a valid and an invalid statement via the given connection.
     *
     * @param tasks The tasks list to add new tasks to.
     * @param conn The connection to use to create new statements.
     * @throws SQLException If an error occurs when creating a new statement.
     */
    private void addSimpleQueryExecTasks(List<Callable<Void>> tasks, Connection conn)
            throws SQLException {
        // Connection with no logging and a valid query to execute.
        tasks.add(new ADFIntegrationTest.SimpleQueryExecutor(conn, "SELECT 1"));
        // Connection with no logging and an invalid query to execute.
        tasks.add(new ADFIntegrationTest.SimpleQueryExecutor(conn, "INVALID QUERY TO EXECUTE"));
    }

    /**
     * Clean-up after the logging test. It will close the connection and delete the log file if it
     * exists.
     *
     * @param conn The connection.
     */
    private void cleanUp(MongoConnection conn) {
        try {
            conn.close();
            File logFile = new File(CURRENT_DIR + File.separator + "connection.log");
            if (logFile.exists()) {
                logFile.delete();
            }
        } catch (Exception e) {
            // Ignore clean-up exceptions
            System.out.println("Clean-up error ignored.");
            e.printStackTrace();
        }
    }

    /**
     * Tests the handling of different UUID representations specified in the URI. The uuid fields
     * have been pre-loaded into the database, stored in their respective uuid representations
     * according to their type. This test verifies that each representation is correctly retrieved
     * and converted to the expected string format.
     */
    @Test
    public void testUUIDRepresentationInURI() {
        for (String representation : UUID_REPRESENTATIONS) {
            System.out.println("Testing with UUID representation: " + representation);

            try (MongoConnection conn =
                            representation.equals("default")
                                    ? getBasicConnection(DEFAULT_TEST_DB, null)
                                    : getBasicConnection(
                                            DEFAULT_TEST_DB,
                                            null,
                                            "uuidRepresentation=" + representation);
                    Statement stmt = conn.createStatement()) {

                // If no uuidRepresentation is specified in the URI, default to `pythonlegacy`
                String type = representation.equals("default") ? "pythonlegacy" : representation;
                String query = "SELECT * FROM " + UUID_COLLECTION + " WHERE type = '" + type + "'";

                try (ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        String uuid = rs.getString("uuid");
                        System.out.println(
                                "Representation: "
                                        + representation
                                        + ", Type: "
                                        + type
                                        + ", UUID: "
                                        + uuid);
                        assertEquals(
                                EXPECTED_UUID,
                                uuid,
                                "Mismatch for " + representation + " representation");
                    } else {
                        fail("No result found for type: " + type);
                    }
                }
            } catch (SQLException e) {
                fail("Failed to execute query for " + representation + ": " + e.getMessage());
            }
        }
    }

    /**
     * Tests the behavior of standard UUID representation when querying legacy UUID types. This test
     * ensures that when using the standard representation, legacy UUID types are correctly
     * retrieved and represented in the expected $binary format.
     */
    @Test
    public void testStandardRepresentationWithLegacyTypes() {
        try (MongoConnection conn =
                        getBasicConnection(DEFAULT_TEST_DB, null, "uuidRepresentation=STANDARD");
                Statement stmt = conn.createStatement()) {

            for (String legacyType : UUID_REPRESENTATIONS) {
                if (legacyType.equals("standard") || legacyType.equals("default")) continue;

                String query =
                        "SELECT * FROM " + UUID_COLLECTION + " WHERE type = '" + legacyType + "'";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        String uuid = rs.getString("uuid");
                        System.out.println(
                                "STANDARD representation - Type: "
                                        + legacyType
                                        + ", UUID: "
                                        + uuid);
                        assertTrue(
                                uuid.startsWith("{\"$binary\":"),
                                "Expected $binary format for "
                                        + legacyType
                                        + " type with STANDARD representation");
                        assertTrue(
                                uuid.contains("\"base64\":"),
                                "Expected base64 field in $binary format");
                        assertTrue(
                                uuid.contains("\"subType\":"),
                                "Expected subType field in $binary format");
                    } else {
                        fail("No result found for type: " + legacyType);
                    }
                }
            }
        } catch (SQLException e) {
            fail("Failed to execute query: " + e.getMessage());
        }
    }

    /**
     * Tests the behavior of different UUID representations when querying the 'javalegacy' UUID
     * type. This test verifies that each representation retrieves the 'javalegacy' UUID correctly,
     * and that the value of the UUID are different.
     */
    @Test
    public void testDifferentRepresentationsForJavaLegacy() {
        Set<String> uuidValues = new HashSet<>();
        for (String representation : UUID_REPRESENTATIONS) {
            if (representation.equals("default")) continue;
            try (MongoConnection conn =
                            getBasicConnection(
                                    DEFAULT_TEST_DB, null, "uuidRepresentation=" + representation);
                    Statement stmt = conn.createStatement();
                    ResultSet rs =
                            stmt.executeQuery(
                                    "SELECT * FROM "
                                            + UUID_COLLECTION
                                            + " WHERE type = 'javalegacy'")) {
                if (rs.next()) {
                    String uuid = rs.getString("uuid");
                    System.out.println(representation + " representation - UUID: " + uuid);
                    if (representation.equals("standard")) {
                        assertTrue(
                                uuid.startsWith("{\"$binary\":"),
                                "Expected $binary format for standard representation");
                    } else {
                        assertTrue(
                                uuid.startsWith("{\"$uuid\":"),
                                "Expected $uuid format for non-standard representation");
                    }
                    uuidValues.add(uuid);
                } else {
                    fail(
                            "No result found for 'javalegacy' type with "
                                    + representation
                                    + " representation");
                }
            } catch (SQLException e) {
                fail("Failed to execute query for " + representation + ": " + e.getMessage());
            }
        }
        assertEquals(4, uuidValues.size(), "Expected 4 different UUID values (including standard)");
    }
}
