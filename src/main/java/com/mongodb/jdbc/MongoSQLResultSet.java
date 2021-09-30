package com.mongodb.jdbc;

import com.mongodb.client.MongoCursor;
import org.bson.BsonValue;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class MongoSQLResultSet extends MongoResultSet implements ResultSet {

    public MongoSQLResultSet(
            Statement statement, MongoCursor<MongoResultDoc> cursor, boolean relaxed) {
        super(statement, cursor, relaxed);
    }

    // This is only used for testing, and that is why it has package level access, and the
    // tests have been moved into this package.
    MongoResultDoc getCurrent() {
        return current;
    }

    @Override
    protected BsonValue getBsonValue(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected BsonValue getBsonValue(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected Object getObject(BsonValue o, int columnType) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
