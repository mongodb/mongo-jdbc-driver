package com.mongodb.jdbc;

import com.mongodb.jdbc.logging.AutoLoggable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

@AutoLoggable
public class MongoSQLConnection extends MongoConnection implements Connection {

    public MongoSQLConnection(MongoConnectionProperties mongoConnectionProperties) {
        super(mongoConnectionProperties);
        super.getLogger().log(Level.INFO, "Dialect is MongoSQL");
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
