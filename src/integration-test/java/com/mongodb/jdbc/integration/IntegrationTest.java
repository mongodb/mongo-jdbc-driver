package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntegrationTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/test";
    static final String URL_WITH_USER_AND_PW =
            "jdbc:mongodb://"
                    + System.getenv("ADL_TEST_USER")
                    + ":"
                    + System.getenv("ADL_TEST_PWD")
                    + "@"
                    + System.getenv("ADL_TEST_HOST")
                    + "/test";

    //    @Test
    //    public void testFoo() throws SQLException {
    //        java.util.Properties p = new java.util.Properties();
    //        //p.setProperty("user", System.getenv("ADL_TEST_USER"));
    //        //p.setProperty("password", System.getenv("ADL_TEST_PWD"));
    //        p.setProperty("database", "looker");
    //        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
    //        p.setProperty("ssl", "true");
    //        Connection conn = DriverManager.getConnection(URL_WITH_USER_AND_PW, p);
    //        DatabaseMetaData dbmd = conn.getMetaData();
    //        System.out.println(dbmd.getStringFunctions());
    //        System.out.println(dbmd.getNumericFunctions());
    //        System.out.println(dbmd.getTimeDateFunctions());
    //        System.out.println("___" + dbmd.getUserName());
    //        ResultSet rs = dbmd.getIndexInfo(null, null, null, false, false);
    //        while (rs.next()) {
    //            ResultSetMetaData rsmd = rs.getMetaData();
    //            for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
    //                System.out.println(rsmd.getColumnLabel(i) + ": " + rs.getString(i));
    //            }
    //            System.out.println("-------------------------------");
    //        }
    //    }
    //

    @Test
    public void testConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertTrue(conn.isValid(15));
    }

    @Test
    public void nullInfo() throws SQLException {
        // Make sure we don't get an NPE with null properties.
        Connection conn = DriverManager.getConnection(URL, null);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badUserName() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", "baduser");
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badPassword() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", "badPass");
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badAuthDB() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", "badDB");
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badAuthMethod() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }
}
