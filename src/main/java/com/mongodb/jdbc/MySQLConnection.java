package com.mongodb.jdbc;

import com.mongodb.ConnectionString;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

public class MySQLConnection extends MongoConnection implements Connection {
    private boolean relaxed;

    public MySQLConnection(ConnectionString cs, String database, String conversionMode) {
        super(cs, database);
        relaxed = conversionMode == null || !conversionMode.equals("strict");
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkConnection();
        try {
            return new MySQLStatement(this, currentDB, relaxed);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkConnection();
        try {
            return new MongoPreparedStatement(sql, new MySQLStatement(this, currentDB, relaxed));
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MySQLDatabaseMetaData(this);
    }
}
