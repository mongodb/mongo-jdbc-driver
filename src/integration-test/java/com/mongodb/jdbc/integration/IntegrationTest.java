package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntegrationTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("adl_test_host") + "/test";

    @Test
    public void testConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertTrue(conn.isValid(5));
    }

    @Test
    public void badUserName() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", "baduser");
        p.setProperty("password", System.getenv("adl_test_pwd"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("adl_test_auth_db"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(5));
    }

    @Test
    public void badPassword() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("adl_test_user"));
        p.setProperty("password", "badPass");
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("adl_test_auth_db"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(5));
    }

    @Test
    public void badAuthDB() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("adl_test_user"));
        p.setProperty("password", System.getenv("adl_test_pwd"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", "badDB");
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(5));
    }

    @Test
    public void badAuthMethod() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("adl_test_user"));
        p.setProperty("password", System.getenv("adl_test_pwd"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("adl_test_auth_db"));
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(5));
    }
}
