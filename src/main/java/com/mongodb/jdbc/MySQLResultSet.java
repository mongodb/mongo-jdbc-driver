package com.mongodb.jdbc;

import com.mongodb.client.MongoCursor;
import org.bson.BsonValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;

public class MySQLResultSet extends MongoResultSet implements ResultSet {

    public MySQLResultSet(
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
        checkBounds(columnIndex);
        return current.values.get(columnIndex - 1);
    }

    @Override
    protected BsonValue getBsonValue(String columnLabel) throws SQLException {
        return getBsonValue(findColumn(columnLabel));
    }

    @Override
    protected Object getObject(BsonValue o, int columnType) throws SQLException {
        switch (columnType) {
            case Types.ARRAY:
                // not supported
                break;
            case Types.BIGINT:
                return getInt(o);
            case Types.BINARY:
                // not supported
                break;
            case Types.BIT:
                return getBoolean(o);
            case Types.BLOB:
                // not supported
                break;
            case Types.BOOLEAN:
                return getBoolean(o);
            case Types.CHAR:
                // not supported
                break;
            case Types.CLOB:
                // not supported
                break;
            case Types.DATALINK:
                // not supported
                break;
            case Types.DATE:
                // not supported
                break;
            case Types.DECIMAL:
                return getBigDecimal(o);
            case Types.DISTINCT:
                // not supported
                break;
            case Types.DOUBLE:
                return getDouble(o);
            case Types.FLOAT:
                return getFloat(o);
            case Types.INTEGER:
                return getInt(o);
            case Types.JAVA_OBJECT:
                // not supported
                break;
            case Types.LONGNVARCHAR:
                return getString(o);
            case Types.LONGVARBINARY:
                // not supported
                break;
            case Types.LONGVARCHAR:
                return getString(o);
            case Types.NCHAR:
                return getString(o);
            case Types.NCLOB:
                // not supported
                break;
            case Types.NULL:
                return null;
            case Types.NUMERIC:
                return getDouble(o);
            case Types.NVARCHAR:
                return getString(o);
            case Types.OTHER:
                // not supported
                break;
            case Types.REAL:
                // not supported
                break;
            case Types.REF:
                // not supported
                break;
            case Types.REF_CURSOR:
                // not supported
                break;
            case Types.ROWID:
                // not supported
                break;
            case Types.SMALLINT:
                return getInt(o);
            case Types.SQLXML:
                // not supported
                break;
            case Types.STRUCT:
                // not supported
                break;
            case Types.TIME:
                // not supported
                break;
            case Types.TIME_WITH_TIMEZONE:
                // not supported
                break;
            case Types.TIMESTAMP:
                return getTimestamp(o);
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // not supported
                break;
            case Types.TINYINT:
                return getInt(o);
            case Types.VARBINARY:
                // not supported
                break;
            case Types.VARCHAR:
                return getString(o);
        }
        throw new SQLException("getObject not supported for column type " + columnType);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        int columnType = rsMetaData.getColumnType(columnIndex);
        return getObject(out, columnType);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        int columnType = rsMetaData.getColumnType(findColumn(columnLabel));
        return getObject(out, columnType);
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
}
