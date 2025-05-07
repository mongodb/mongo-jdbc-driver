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

package com.mongodb.jdbc.integration;

import static com.mongodb.jdbc.MongoDriver.AUTHENTICATION_ERROR_SQLSTATE;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.MongoConnection;
import com.mongodb.jdbc.MongoDatabaseMetaData;
import com.mongodb.jdbc.Pair;
import com.mongodb.jdbc.mongosql.MongoSQLException;
import java.sql.*;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DCIntegrationTest {

    /**
     * Connect to a remote cluster to use for the tests.
     *
     * @return the connection to the enterprise cluster to use for the tests.
     * @throws SQLException If the connection failed.
     */
    private Connection remoteTestInstanceConnect() throws SQLException {
        String mongoHost = System.getenv("SRV_TEST_HOST");
        assertNotNull(mongoHost, "SRV_TEST_HOST variable not set in environment");
        String mongoURI =
                "mongodb+srv://"
                        + mongoHost
                        + "/?readPreference=secondaryPreferred&connectTimeoutMS=300000";
        String fullURI = "jdbc:" + mongoURI;

        String user = System.getenv("SRV_TEST_USER");
        assertNotNull(user, "SRV_TEST_USER variable not set in environment");
        String pwd = System.getenv("SRV_TEST_PWD");
        assertNotNull(pwd, "SRV_TEST_PWD variable not set in environment");
        String authSource = System.getenv("SRV_TEST_AUTH_DB");
        assertNotNull(authSource, "SRV_TEST_AUTH_DB variable not set in environment");

        Properties p = new java.util.Properties();
        p.setProperty("user", user);
        p.setProperty("password", pwd);
        p.setProperty("authSource", authSource);
        p.setProperty("database", "test");

        return DriverManager.getConnection(fullURI, p);
    }

    /** Tests that the driver can work with SRV-style URIs. */
    @Test
    public void testConnectWithSRVURI() throws SQLException {
        try (Connection conn = remoteTestInstanceConnect(); ) {
            // Let's use the connection to make sure everything is working fine.
            conn.getMetaData().getDriverVersion();
        }
    }

    /**
     * Gets information from the environment to create a connection to a local mongod.
     *
     * @param typeEnvVar Either "MDB_LOCAL_PORT_COM" or "MDB_LOCAL_PORT_ENT"
     * @return A (jdbc_uri, properties) pair with which to create a MongoConnection
     */
    private Pair<String, Properties> createLocalMongodConnInfo(String typeEnvVar) {
        String mongoPort = System.getenv(typeEnvVar);
        assertNotNull(mongoPort, typeEnvVar + " variable not set in environment");

        String uri = "jdbc:mongodb://localhost:" + mongoPort + "/test";

        String user = System.getenv("LOCAL_MDB_USER");
        assertNotNull(user, "LOCAL_MDB_USER variable not set in environment");
        String pwd = System.getenv("LOCAL_MDB_PWD");
        assertNotNull(pwd, "LOCAL_MDB_PWD variable not set in environment");

        Properties p = new java.util.Properties();
        p.setProperty("user", user);
        p.setProperty("password", pwd);
        p.setProperty("authSource", "test");
        p.setProperty("database", "test");

        return new Pair<>(uri, p);
    }

    /**
     * Execute the given SQL query and checks the table and column names from the metadata, also
     * verifies that the cursor return is working Ok.
     *
     * @param query The SQL query to execute.
     * @param expectedTableNames The expected table names in the metadata.
     * @param expectedColumnLabels The expected column names in the metadata.
     * @throws SQLException if an error occurs.
     */
    private void executeQueryAndValidateResults(
            String query, String[] expectedTableNames, String[] expectedColumnLabels)
            throws SQLException {
        try (Connection conn = remoteTestInstanceConnect();
                Statement stmt = conn.createStatement(); ) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            assert (rsmd.getColumnCount() == expectedColumnLabels.length);
            int i = 1;
            for (String expectColumnLabel : expectedColumnLabels) {
                assertEquals(
                        rsmd.getColumnName(i),
                        (expectColumnLabel),
                        rsmd.getColumnName(1) + " != " + expectColumnLabel);
                i++;
            }
            assert (rs.next());
            // Let's also check that we can access the data and don't blow up.
            rs.getString(1);
            rs.close();
        }
    }

    /** Tests that the driver rejects the community edition of the server. */
    @Test
    public void testConnectionToCommunityServerFails() {
        Pair<String, Properties> info = createLocalMongodConnInfo("LOCAL_MDB_PORT_COM");
        try (MongoConnection conn =
                (MongoConnection) DriverManager.getConnection(info.left(), info.right()); ) {
            assertThrows(java.sql.SQLException.class, () -> {});

        } catch (SQLException e) {
            assertTrue(
                    e.getCause().getMessage().contains("Community edition detected"),
                    e.getCause().getMessage() + " doesn't contain \"Community edition detected\"");
        }
    }

    /** Tests that the driver connects to the enterprise edition of the server. */
    @Test
    public void testConnectionToEnterpriseServerSucceeds() throws SQLException {
        Pair<String, Properties> info = createLocalMongodConnInfo("LOCAL_MDB_PORT_ENT");
        try (Connection conn = DriverManager.getConnection(info.left(), info.right()); ) {
            // Let's use the connection to make sure everything is working fine.
            conn.getMetaData().getDriverVersion();
        }
    }

    @Test
    public void testInvalidQueryShouldFail() throws SQLException {
        try (Connection conn = remoteTestInstanceConnect();
                Statement stmt = conn.createStatement(); ) {
            // Invalid SQL query should fail
            Exception exception =
                    assertThrows(
                            RuntimeException.class,
                            () -> {
                                stmt.executeQuery("This is not valid SQL");
                            });
            // Let's make sure that we fail for the reason we expect it to.
            assertTrue(exception.getCause() instanceof MongoSQLException);
            assertTrue(exception.getMessage().contains("Error 2001"));
        }
    }

    @Test
    public void testValidSimpleQueryShouldSucceed() throws SQLException {
        String[] expectedTableNames = {"acc", "acc", "acc", "acc", "t", "t", "t", "t", "t", "t"};
        String[] expectedColumnLabels = {
            "_id",
            "account_id",
            "limit",
            "products",
            "_id",
            "account_id",
            "bucket_end_date",
            "bucket_start_date",
            "transaction_count",
            "transactions"
        };
        executeQueryAndValidateResults(
                "SELECT * from accounts acc JOIN transactions t on acc.account_id = t.account_id limit 5",
                expectedTableNames,
                expectedColumnLabels);
    }

    @Test
    public void testCollectionLessQueryShouldSucceed() throws SQLException {
        String[] expectedTableNames = {""};
        String[] expectedColumnLabels = {"_1"};
        executeQueryAndValidateResults("SELECT 1", expectedTableNames, expectedColumnLabels);
    }

    @Test
    public void testValidSimpleQueryNoSchemaForCollectionShouldSucceed() throws SQLException {
        String[] expectedTableNames = {""};
        String[] expectedColumnLabels = {"account_id"};
        executeQueryAndValidateResults(
                "SELECT account_id from acc_limit_over_1000 limit 5",
                expectedTableNames,
                expectedColumnLabels);
    }

    @Test
    public void testListDatabase() throws SQLException {
        try (Connection conn = remoteTestInstanceConnect(); ) {
            ResultSet rs = conn.getMetaData().getCatalogs();
            while (rs.next()) {
                // Verify that none of the system databases are returned
                assert (!MongoDatabaseMetaData.DISALLOWED_DB_NAMES
                        .matcher(rs.getString(1))
                        .matches());
            }
            rs.close();
        }
    }

    @Test
    public void testListTables() throws SQLException {
        try (Connection conn = remoteTestInstanceConnect(); ) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);
            while (rs.next()) {
                // Verify that none of the system collections are returned
                assert (!MongoDatabaseMetaData.DISALLOWED_COLLECTION_NAMES
                        .matcher(rs.getString(3))
                        .matches());
            }
            rs.close();
        }
    }

    @Test
    public void testColumnsMetadataForCollectionWithSchema() throws SQLException {
        String[] expectedColumnLabels = {"_id", "account_id", "limit", "products"};
        try (Connection conn = remoteTestInstanceConnect(); ) {
            ResultSet rs = conn.getMetaData().getColumns(null, null, "accounts", "%");
            for (String expectColumnLabel : expectedColumnLabels) {
                assert (rs.next());
                assertEquals(
                        rs.getString(4),
                        (expectColumnLabel),
                        rs.getString(4) + " != " + expectColumnLabel);
            }
            rs.close();
        }
    }

    @Test
    public void testColumnsMetadataForCollectionWithNoSchema() throws SQLException {
        try (Connection conn = remoteTestInstanceConnect(); ) {
            ResultSet rs = conn.getMetaData().getColumns(null, null, "acc_limit_over_1000", "%");
            // Check that the result set is empty and we don't blow up when calling next.
            assert (!rs.next());
            rs.close();
        }
    }

    @Test
    public void testInvalidCredentialsOnEnterpriseServer() throws SQLException {
        Pair<String, Properties> info = createLocalMongodConnInfo("LOCAL_MDB_PORT_ENT");
        info.right().setProperty("password", "invalid-password");

        SQLException thrown =
                assertThrows(
                        SQLException.class,
                        () -> DriverManager.getConnection(info.left(), info.right()),
                        "A SQLException should be thrown due to invalid credentials.");

        String message = thrown.getMessage().toLowerCase();
        assertTrue(
                message.contains("authentication failed"),
                "The error message should indicate that authentication failed.");
        assertEquals(
                AUTHENTICATION_ERROR_SQLSTATE,
                thrown.getSQLState(),
                "SQLSTATE should indicate an authentication failure ("
                        + AUTHENTICATION_ERROR_SQLSTATE
                        + ")");
    }
}
