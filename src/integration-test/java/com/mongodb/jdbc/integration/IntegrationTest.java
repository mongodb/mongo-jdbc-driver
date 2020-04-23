package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntegrationTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/test";

    //    @Test
    //    public void testFoo() throws SQLException {
    //        java.util.Properties p = new java.util.Properties();
    //        p.setProperty("user", System.getenv("ADL_TEST_USER"));
    //        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
    //        p.setProperty("database", "looker");
    //        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
    //        p.setProperty("ssl", "true");
    //        Connection conn = DriverManager.getConnection(URL, p);
    //        DatabaseMetaData dbmd = conn.getMetaData();
    //        System.out.println(dbmd.getStringFunctions());
    //        System.out.println(dbmd.getNumericFunctions());
    //        System.out.println(dbmd.getTimeDateFunctions());
    //        System.out.println(dbmd.getSystemFunctions());
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
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
        assertTrue(rs.next());
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
        Statement stmt = conn.createStatement();
        assertThrows(
                SQLException.class,
                () -> {
                    ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
                });
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
        Statement stmt = conn.createStatement();
        assertThrows(
                SQLException.class,
                () -> {
                    ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
                });
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
        Statement stmt = conn.createStatement();
        assertThrows(
                SQLException.class,
                () -> {
                    ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
                });
    }

    @Test
    public void badAuthMethod() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        Connection conn = DriverManager.getConnection(URL, p);
        Statement stmt = conn.createStatement();
        assertThrows(
                SQLException.class,
                () -> {
                    ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
                });
    }
}
