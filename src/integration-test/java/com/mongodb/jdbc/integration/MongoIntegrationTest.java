package com.mongodb.jdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class MongoIntegrationTest {
    public static final String LOCAL_URL = "mongodb://localhost:28017";
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/test";
    protected Connection conn;

    @BeforeEach
    protected abstract void setupConnection() throws SQLException;

    @AfterEach
    protected void cleanupTest() throws SQLException {
        conn.close();
    }

    public static final String URL_WITH_USER_AND_PW =
            "jdbc:mongodb://"
                    + System.getenv("ADL_TEST_USER")
                    + ":"
                    + System.getenv("ADL_TEST_PWD")
                    + "@"
                    + System.getenv("ADL_TEST_HOST")
                    + "/test";

    public static int countRows(ResultSet rs) throws SQLException {
        for (int i = 0; ; ++i) {
            if (!rs.next()) {
                return i;
            }
        }
    }

    public static Connection getBasicConnection(String dialect) throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("dialect", dialect);
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "integration_test");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        return DriverManager.getConnection(URL, p);
    }
}
