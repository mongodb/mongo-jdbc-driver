package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.bson.BsonValue;

public class MongoResultSetMetaData implements ResultSetMetaData {
    private Row row;

    public MongoResultSetMetaData(Row row) {
        this.row = row;
    }

    public int getColumnCount() throws SQLException {
        return row.size();
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public int isNullable(int column) throws SQLException {
        return columnNullableUnknown;
    }

    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        // TODO: Format as string and get length?
        throw new SQLException("unimplemented");
    }

    // getColumnLabel is for print out versus simple name, MongoDB makes no distinction.
    public String getColumnLabel(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getColumnName(int column) throws SQLException {
        return getColumnLabel(column);
    }

    public String getSchemaName(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getPrecision(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getScale(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getTableName(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getCatalogName(int column) throws SQLException {
        return getColumnLabel(column);
    }

    private BsonValue getObject(int column) throws SQLException {
        if (column > row.size()) {
            throw new SQLException("index out of bounds: '" + column + "'");
        }
        return row.values.get(column - 1).value;
    }

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

    public boolean isReadOnly(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isWritable(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    // --------------------------JDBC 2.0-----------------------------------

    public String getColumnClassName(int column) throws SQLException {
        Object o = getObject(column);
        return o.getClass().getName();
    }

    // java.sql.Wrapper impl
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
