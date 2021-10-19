package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.bson.BsonType;

public class MongoSQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {

    public MongoSQLResultSetMetaData(MongoResultDoc metadataDoc) {
        super(metadataDoc);
    }

    public String getDatasource(String columnLabel) {
        throw new RuntimeException("TODO");
    }

    @Override
    public int getColumnCount() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public int isNullable(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getTableName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public BsonType getBsonType(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
