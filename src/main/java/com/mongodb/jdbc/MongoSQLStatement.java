package com.mongodb.jdbc;

import java.sql.*;

public class MongoSQLStatement extends MongoStatement implements Statement {

    public MongoSQLStatement(MongoConnection conn, String databaseName, boolean relaxed)
            throws SQLException {
        super(conn, databaseName, relaxed);
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
