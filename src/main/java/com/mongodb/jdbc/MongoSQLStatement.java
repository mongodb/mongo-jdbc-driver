package com.mongodb.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

public class MongoSQLStatement extends MongoStatement implements Statement {
    public MongoSQLStatement(MongoConnection conn, String databaseName, boolean relaxed)
            throws SQLException {
        super(conn, databaseName);
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
