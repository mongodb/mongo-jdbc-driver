package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import com.mongodb.jdbc.logging.AutoLoggable;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

@AutoLoggable
public class MySQLConnection extends MongoConnection implements Connection {
    private boolean relaxed;

    public MySQLConnection(
            ConnectionString cs,
            String database,
            String conversionMode,
            Level logLevel,
            File logDir,
            String[] clientInfo) {
        super(cs, database, logLevel, logDir, clientInfo);
        super.getLogger().log(Level.INFO, "Dialect is Mysql");
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
