package com.mongodb.jdbc;

import java.sql.SQLException;

public class BsonTypeInfo {
    public static final String BINDATA_TYPE_NAME = "binData";
    public static final String STRING_TYPE_NAME = "string";
    public static final String INT_TYPE_NAME = "int";
    public static final String BOOL_TYPE_NAME = "bool";
    public static final String DATE_TYPE_NAME = "date";
    public static final String DECIMAL_TYPE_NAME = "decimal";
    public static final String DOUBLE_TYPE_NAME = "double";
    public static final String LONG_TYPE_NAME = "long";
    public static final String ARRAY_TYPE_NAME = "array";
    public static final String OBJECT_TYPE_NAME = "object";
    public static final String OBJECTID_TYPE_NAME = "objectId";
    public static final String DBPOINTER_TYPE_NAME = "dbPointer";
    public static final String JAVASCRIPT_TYPE_NAME = "javascript";
    public static final String JAVASCRIPTWITHSCOPE_TYPE_NAME = "javascriptWithScope";
    public static final String MAXKEY_TYPE_NAME = "maxKey";
    public static final String MINKEY_TYPE_NAME = "minKey";
    public static final String REGEX_TYPE_NAME = "regex";
    public static final String SYMBOL_TYPE_NAME = "symbol";
    public static final String TIMESTAMP_TYPE_NAME = "timestamp";
    public static final String UNDEFINED_TYPE_NAME = "undefined";
    public static final String BSON_TYPE_NAME = "bson";

    // Helper functions for getTypeInfo
    public static Integer getPrecision(String typeName) throws SQLException {
        switch (typeName) {
            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
            case BSON_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case ARRAY_TYPE_NAME:
                return null;
            case OBJECTID_TYPE_NAME:
            case DATE_TYPE_NAME:
                return 24;
            case INT_TYPE_NAME:
                return 10;
            case BOOL_TYPE_NAME:
                return 1;
            case DECIMAL_TYPE_NAME:
                return 34;
            case DOUBLE_TYPE_NAME:
                return 15;
            case LONG_TYPE_NAME:
                return 19;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static boolean getCaseSensitivity(String typeName) throws SQLException {
        switch (typeName) {
            case STRING_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
                return true;
            case BINDATA_TYPE_NAME:
            case INT_TYPE_NAME:
            case BOOL_TYPE_NAME:
            case DATE_TYPE_NAME:
            case DECIMAL_TYPE_NAME:
            case DOUBLE_TYPE_NAME:
            case LONG_TYPE_NAME:
            case ARRAY_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case OBJECTID_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
            case BSON_TYPE_NAME:
                return false;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getMinScale(String typeName) throws SQLException {
        switch (typeName) {
            case DECIMAL_TYPE_NAME:
                return 34;
            case DOUBLE_TYPE_NAME:
                return 15;
            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
            case INT_TYPE_NAME:
            case BOOL_TYPE_NAME:
            case DATE_TYPE_NAME:
            case LONG_TYPE_NAME:
            case ARRAY_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case OBJECTID_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
            case BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getMaxScale(String typeName) throws SQLException {
        switch (typeName) {
            case DECIMAL_TYPE_NAME:
                return 34;
            case DOUBLE_TYPE_NAME:
                return 15;
            case DATE_TYPE_NAME:
                return 3;

            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
            case INT_TYPE_NAME:
            case BOOL_TYPE_NAME:
            case LONG_TYPE_NAME:
            case ARRAY_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case OBJECTID_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
            case BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getNumPrecRadix(String typeName) throws SQLException {
        switch (typeName) {
            case DECIMAL_TYPE_NAME:
                return 10;
            case INT_TYPE_NAME:
            case DOUBLE_TYPE_NAME:
            case LONG_TYPE_NAME:
                return 2;
            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
            case BOOL_TYPE_NAME:
            case DATE_TYPE_NAME:
            case ARRAY_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case OBJECTID_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
            case BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }
}
