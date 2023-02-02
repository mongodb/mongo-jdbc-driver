package com.mongodb.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MongoSQLTestUtils extends TestUtils {
    static final String URL = "jdbc:mongodb://localhost";
    public static final String TEST_DB = "integration_test";
    public static final String DEFAULT_TEST_COLLECTION = "test_collection";

    @Override
    protected Connection connect() throws Exception {
        // Connects to local ADF instance
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADF_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADF_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADF_TEST_LOCAL_AUTH_DB"));
        p.setProperty("database", TEST_DB);
        p.setProperty("ssl", "false");
        Connection conn = DriverManager.getConnection(URL, p);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("select 1 from " + DEFAULT_TEST_COLLECTION);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return conn;
    }
}
