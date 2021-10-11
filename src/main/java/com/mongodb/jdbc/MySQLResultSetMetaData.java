package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

public class MySQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {
    protected List<Column> columns;
    protected HashMap<String, Integer> columnPositions;

    public MySQLResultSetMetaData(MongoResultDoc metadataDoc) {
        columns = metadataDoc.columns;

        columnPositions = new HashMap<>(columns.size());
        int i = 0;
        for (Column c : columns) {
            columnPositions.put(c.columnAlias, i++);
        }
    }

    public int getColumnPositionFromLabel(String label) {
        return columnPositions.get(label);
    }

    public boolean hasColumnWithLabel(String label) {
        return columnPositions.containsKey(label);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        checkBounds(column);
        return columnNullableUnknown;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).columnAlias;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).column;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column).tableAlias;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).database;
    }

    @Override
    public ExtendedBsonType getExtendedBsonType(int column) throws SQLException {
        checkBounds(column);
        String typeName = columns.get(column - 1).bsonType;
        return getExtendedBsonTypeHelper(typeName);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        ExtendedBsonType t = getExtendedBsonType(column);
        switch (t) {
            case ARRAY:
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
                return Types.DECIMAL;
            case DOCUMENT:
                return Types.NULL;
            case DOUBLE:
                return Types.DOUBLE;
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
            case ANY:
                throw new SQLException("MySQL Dialect does not support dynamic BSON values");
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        ExtendedBsonType t = getExtendedBsonType(column);
        switch (t) {
                // we will return the same names as the mongodb $type function:
            case ARRAY:
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
            case ANY:
                throw new SQLException("MySQL Dialect does not support dynamic BSON values");
        }
        throw new SQLException("unknown bson type: " + t);
    }
}
