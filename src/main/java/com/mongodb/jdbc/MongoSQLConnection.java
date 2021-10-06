package com.mongodb.jdbc;

import com.mongodb.ConnectionString;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

public class MongoSQLConnection extends MongoConnection implements Connection {

    public MongoSQLConnection(ConnectionString cs, String database) {
        super(cs, database);
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkConnection();
        try {
            return new MongoSQLStatement(this, currentDB);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
   }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        try {
            return new MongoPreparedStatement(sql, new MongoSQLStatement(this, currentDB));
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MongoSQLDatabaseMetaData(this);
    }
}
