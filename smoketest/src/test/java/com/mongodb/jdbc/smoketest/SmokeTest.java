package com.mongodb.jdbc.smoketest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * SmokeTest runs a test on built artifacts to verify that connection,
 * metadata retrieval, and querying is successful
 */
public class SmokeTest {
    static final String URL = "jdbc:mongodb://localhost";
    static final String DB = "integration_test";

    // Connection and simple query to use for sanity check.
    private Map<Connection, String> connections = new HashMap<>();

    public static Connection getBasicConnection(String url, String db)
            throws SQLException {
        Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADF_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADF_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADF_TEST_LOCAL_AUTH_DB"));
        p.setProperty("ssl", "false");
        p.setProperty("database", db);
        return DriverManager.getConnection(URL, p);
    }

    @BeforeEach
    public void setupConnection() throws SQLException {
        connections.put(getBasicConnection(URL, DB), "SELECT * from class");
        connections.put(getDirectRemoteInstanceConnection(), "Select * from accounts limit 5");
    }

    private Connection getDirectRemoteInstanceConnection() throws SQLException {
        String mongoHost = System.getenv("SRV_TEST_HOST");
        String mongoURI =
                "mongodb+srv://"
                        + mongoHost
                        + "/?readPreference=secondaryPreferred&connectTimeoutMS=300000";
        String fullURI = "jdbc:" + mongoURI;

        String user = System.getenv("SRV_TEST_USER");
        String pwd = System.getenv("SRV_TEST_PWD");
        String authSource = System.getenv("SRV_TEST_AUTH_DB");

        Properties p = new java.util.Properties();
        p.setProperty("user", user);
        p.setProperty("password", pwd);
        p.setProperty("authSource", authSource);
        p.setProperty("database", "test");

        return DriverManager.getConnection(fullURI, p);
    }

    @AfterEach
    protected void cleanupTest() throws SQLException {
        for (Connection conn : connections.keySet()) {
            conn.close();
        }
    }

    @Test
    public void databaseMetadataTest() throws SQLException {
        for (Connection conn : connections.keySet()) {
            DatabaseMetaData dbMetadata = conn.getMetaData();
            System.out.println(dbMetadata.getDriverName());
            System.out.println(dbMetadata.getDriverVersion());

            ResultSet rs = dbMetadata.getColumns(null, "%", "%", "%");
            rowsReturnedCheck(rs);
        }
    }

    @Test
    public  void queryTest() throws SQLException {
        for (Map.Entry<Connection, String> entry : connections.entrySet()) {
            try (Statement stmt = entry.getKey().createStatement()) {
                ResultSet rs = stmt.executeQuery(entry.getValue());
                rowsReturnedCheck(rs);
            }
        }
    }

    public static void rowsReturnedCheck(ResultSet rs) throws SQLException {
        int actualCount = 0;
        while (rs.next()) {
            actualCount++;
        }
        assertTrue(actualCount >= 1, "No rows returned in result set");
    }
}
