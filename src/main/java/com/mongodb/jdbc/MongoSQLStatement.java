package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

public class MongoSQLStatement extends MongoStatement implements Statement {
    public MongoSQLStatement(MongoConnection conn, String databaseName)
            throws SQLException {
        Preconditions.checkNotNull(conn);
        Preconditions.checkNotNull(databaseName);
        this.conn = conn;
        currentDBName = databaseName;
        try {
            currentDB = conn.getDatabase(databaseName);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Database name %s is invalid", databaseName);
        }
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
