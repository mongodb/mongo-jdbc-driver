package com.mongodb.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MongoSqlTestUtils extends TestUtils {
    static final String URL = "jdbc:mongodb://localhost";
    public static final String MONGOSQL = "mongosql";
    public static final String TEST_DB = "integration_test";

    @Override
    protected Connection connect() throws Exception {
        // Connects to local ADL instance
        java.util.Properties p = new java.util.Properties();
        p.setProperty("dialect", MONGOSQL);
        p.setProperty("user", /*System.getenv("ADL_TEST_LOCAL_USER")*/ "mhuser");
        p.setProperty("password", /*System.getenv("ADL_TEST_LOCAL_PWD")*/ "pencil");
        p.setProperty("authSource", /*System.getenv("ADL_TEST_LOCAL_AUTH_DB")*/ "admin");
        p.setProperty("database", TEST_DB);
        p.setProperty("ssl", "false");
        Connection conn = DriverManager.getConnection(URL, p);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("select 1 from " + TEST_DB);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return conn;
    }
}
