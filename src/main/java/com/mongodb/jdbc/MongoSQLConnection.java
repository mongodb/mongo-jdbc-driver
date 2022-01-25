package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.logging.MongoSQLException;

import java.sql.*;
import java.util.logging.Level;

public class MongoSQLConnection extends MongoConnection implements Connection {

    public MongoSQLConnection(ConnectionString cs, String database) {
        super(cs, database);
        logger.log(Level.FINE, ">> Creating new MongoSQLConnection");
    }

    @Override
    public Statement createStatement() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        try {
            return new MongoSQLStatement(this, currentDB);
        } catch (IllegalArgumentException e) {
            throw new MongoSQLException(e, logger);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        try {
            return new MongoPreparedStatement(sql, new MongoSQLStatement(this, currentDB));
        } catch (IllegalArgumentException e) {
            throw new MongoSQLException(e, logger);
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return new MongoSQLDatabaseMetaData(this);
    }
}
