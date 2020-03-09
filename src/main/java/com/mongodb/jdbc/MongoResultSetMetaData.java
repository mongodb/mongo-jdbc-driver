package com.mongodb.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.bson.BsonValue;

public class MongoResultSetMetaData implements ResultSetMetaData {
    private Row row;
    private ResultSet parent;

    public MongoResultSetMetaData(MongoResultSet parent, Row row) {
        this.parent = parent;
        this.row = row;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return row.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return columnNullableUnknown;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        String value = parent.getString(column);
        return value.length();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnLabel(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return row.values.get(column).database;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        BsonValue o = getObject(column);
        if (o == null) {
            return 0;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                // This should be impossible. Perhaps throw an exception?
                return 0;
            case BINARY:
                return o.asBinary().getData().length;
            case BOOLEAN:
                return 1;
            case DATE_TIME:
                return 64;
            case DB_POINTER:
                return 1;
            case DECIMAL128:
                // I don't know what to do with this. Previously, we used DECIMAL here,
                // but DECIMAL is technically a fixed width type. I think we should switch
                // to REAL here.
                return 128;
            case DOCUMENT:
                return 0;
            case DOUBLE:
                return 64;
            case END_OF_DOCUMENT:
                return 0;
            case INT32:
                return 32;
            case INT64:
                return 64;
            case JAVASCRIPT:
                return 0;
            case JAVASCRIPT_WITH_SCOPE:
                return 0;
            case MAX_KEY:
                return 0;
            case MIN_KEY:
                return 0;
            case NULL:
                return 0;
            case OBJECT_ID:
                return parent.getString(column).length();
            case REGULAR_EXPRESSION:
                return 0;
            case STRING:
                return parent.getString(column).length();
            case SYMBOL:
                return 0;
            case TIMESTAMP:
                return 0;
            case UNDEFINED:
                return 0;
        }
        throw new SQLException("unknown bson type with value: " + o);
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return row.values.get(column).tableAlias;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return getSchemaName(column);
    }

    private BsonValue getObject(int column) throws SQLException {
        if (row == null) {
            throw new SQLException("no current row in ResultSet, did you call next()?");
        }
        if (column > row.size()) {
            throw new SQLException("index out of bounds: '" + column + "'");
        }
        return row.values.get(column - 1).value;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        BsonValue o = getObject(column);
        if (o == null) {
            return Types.NULL;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                // This should be impossible. Perhaps throw an exception?
                return Types.ARRAY;
            case BINARY:
                return Types.BLOB;
            case BOOLEAN:
                return Types.BIT;
            case DATE_TIME:
                return Types.TIMESTAMP;
            case DB_POINTER:
                return Types.NULL;
            case DECIMAL128:
                // I don't know what to do with this. Previously, we used DECIMAL here,
                // but DECIMAL is technically a fixed width type. I think we should switch
                // to REAL here.
                return Types.REAL;
            case DOCUMENT:
                return Types.NULL;
            case DOUBLE:
                return Types.DOUBLE;
            case END_OF_DOCUMENT:
                return Types.NULL;
            case INT32:
                return Types.INTEGER;
            case INT64:
                return Types.INTEGER;
            case JAVASCRIPT:
                return Types.NULL;
            case JAVASCRIPT_WITH_SCOPE:
                return Types.NULL;
            case MAX_KEY:
                return Types.NULL;
            case MIN_KEY:
                return Types.NULL;
            case NULL:
                return Types.NULL;
            case OBJECT_ID:
                return Types.LONGVARCHAR;
            case REGULAR_EXPRESSION:
                return Types.NULL;
            case STRING:
                return Types.LONGVARCHAR;
            case SYMBOL:
                return Types.NULL;
            case TIMESTAMP:
                return Types.NULL;
            case UNDEFINED:
                return Types.NULL;
        }
        throw new SQLException("unknown bson type with value: " + o);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        BsonValue o = getObject(column);
        if (o == null) {
            return "null";
        }
        switch (o.getBsonType()) {
                // we will return the same names as the mongodb $type function:
            case ARRAY:
                // This should be impossible. Perhaps throw an exception?
                return "array";
            case BINARY:
                return "binData";
            case BOOLEAN:
                return "bool";
            case DATE_TIME:
                return "date";
            case DB_POINTER:
                return "null";
            case DECIMAL128:
                // I don't know what to do with this. Previously, we used DECIMAL here,
                // but DECIMAL is technically a fixed width type. I think we should switch
                // to REAL here.
                return "decimal";
            case DOCUMENT:
                return "null";
            case DOUBLE:
                return "double";
            case END_OF_DOCUMENT:
                return "null";
            case INT32:
                return "int";
            case INT64:
                return "long";
            case JAVASCRIPT:
                return "null";
            case JAVASCRIPT_WITH_SCOPE:
                return "null";
            case MAX_KEY:
                return "null";
            case MIN_KEY:
                return "null";
            case NULL:
                return "null";
            case OBJECT_ID:
                return "string";
            case REGULAR_EXPRESSION:
                return "null";
            case STRING:
                return "string";
            case SYMBOL:
                return "null";
            case TIMESTAMP:
                return "null";
            case UNDEFINED:
                return "null";
        }
        throw new SQLException("unknown bson type with value: " + o);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    // --------------------------JDBC 2.0-----------------------------------

    @Override
    public String getColumnClassName(int column) throws SQLException {
        Object o = getObject(column);
        return o.getClass().getName();
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
