package com.mongodb.jdbc.smoketest;

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
    public static final String MONGOSQL = "mongosql";

    public static Connection getBasicConnection(String url, String db)
            throws SQLException {
        Properties p = new java.util.Properties();
        p.setProperty("dialect", MONGOSQL);
        p.setProperty("user", System.getenv("ADL_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADL_TEST_LOCAL_AUTH_DB"));
        p.setProperty("ssl", "false");
        p.setProperty("database", db);
        return DriverManager.getConnection(URL, p);
    }

    public static void databaseMetadataTest(DatabaseMetaData dbMetadata) throws SQLException {
        ResultSet rs = dbMetadata.getColumns(null, "%", "%", "%");
        rowCountCheck(rs, 47);
    }

    public static void queryTest(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * from class";
            ResultSet rs = stmt.executeQuery(query);
            rowCountCheck(rs, 5);
        }
    }

    public static void rowCountCheck(ResultSet rs, int expectedCount) throws SQLException {
        int actualCount = 0;
        while (rs.next()) {
            actualCount++;
        }

        if (expectedCount != actualCount) {
            throw new RuntimeException("Incorrect row count, expected: " + expectedCount + " actual: " + actualCount);
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection conn = getBasicConnection(URL, DB)) {
            DatabaseMetaData dbMetadata = conn.getMetaData();
            System.out.println(dbMetadata.getDriverName());
            System.out.println(dbMetadata.getDriverVersion());

            databaseMetadataTest(dbMetadata);
            queryTest(conn);

        } catch( Exception e) {
            System.err.println(e);
            throw(e);
        }
    }
}
