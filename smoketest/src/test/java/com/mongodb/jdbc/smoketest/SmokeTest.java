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
import java.util.Properties;

/**
 * SmokeTest runs a test on built artifacts to verify that connection,
 * metadata retrieval, and querying is successful
 */
public class SmokeTest {
    static final String URL = "jdbc:mongodb://localhost";
    static final String DB = "integration_test";

    private Connection conn;

    public static Connection getBasicConnection(String url, String db)
            throws SQLException {
        Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADL_TEST_LOCAL_AUTH_DB"));
        p.setProperty("ssl", "false");
        p.setProperty("database", db);
        return DriverManager.getConnection(URL, p);
    }

    @BeforeEach
    public void setupConnection() throws SQLException {
        conn = getBasicConnection(URL, DB);
    }

    @AfterEach
    protected void cleanupTest() throws SQLException {
        conn.close();
    }

    @Test
    public void databaseMetadataTest() throws SQLException {
        DatabaseMetaData dbMetadata = conn.getMetaData();
        System.out.println(dbMetadata.getDriverName());
        System.out.println(dbMetadata.getDriverVersion());

        ResultSet rs = dbMetadata.getColumns(null, "%", "%", "%");
        rowsReturnedCheck(rs);
    }

    @Test
    public  void queryTest() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * from class";
            ResultSet rs = stmt.executeQuery(query);
            rowsReturnedCheck(rs);
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
