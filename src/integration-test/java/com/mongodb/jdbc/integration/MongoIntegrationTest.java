package com.mongodb.jdbc.integration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class MongoIntegrationTest {
    public static int countRows(ResultSet rs) throws SQLException {
        for (int i = 0; ; ++i) {
            if (!rs.next()) {
                return i;
            }
        }
    }

    public abstract Connection getBasicConnection() throws SQLException;
}
