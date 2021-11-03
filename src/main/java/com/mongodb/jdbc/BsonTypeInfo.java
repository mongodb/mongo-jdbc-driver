package com.mongodb.jdbc;

import java.sql.SQLException;

public class BsonTypeInfo {
    public static final String BSON_BINDATA_TYPE_NAME = "binData";
    public static final String BSON_STRING_TYPE_NAME = "string";
    public static final String BSON_INT_TYPE_NAME = "int";
    public static final String BSON_BOOL_TYPE_NAME = "bool";
    public static final String BSON_DATE_TYPE_NAME = "date";
    public static final String BSON_DECIMAL_TYPE_NAME = "decimal";
    public static final String BSON_DOUBLE_TYPE_NAME = "double";
    public static final String BSON_LONG_TYPE_NAME = "long";
    public static final String BSON_ARRAY_TYPE_NAME = "array";
    public static final String BSON_OBJECT_TYPE_NAME = "object";
    public static final String BSON_OBJECTID_TYPE_NAME = "objectId";
    public static final String BSON_DBPOINTER_TYPE_NAME = "dbPointer";
    public static final String BSON_JAVASCRIPT_TYPE_NAME = "javascript";
    public static final String BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME = "javascriptWithScope";
    public static final String BSON_MAXKEY_TYPE_NAME = "maxKey";
    public static final String BSON_MINKEY_TYPE_NAME = "minKey";
    public static final String BSON_REGEX_TYPE_NAME = "regex";
    public static final String BSON_SYMBOL_TYPE_NAME = "symbol";
    public static final String BSON_TIMESTAMP_TYPE_NAME = "timestamp";
    public static final String BSON_UNDEFINED_TYPE_NAME = "undefined";
    public static final String BSON_BSON_TYPE_NAME = "bson";

    // Helper functions for getTypeInfo
    public static Integer getPrecision(String typeName) throws SQLException {
        switch (typeName) {
            case BSON_BINDATA_TYPE_NAME:
            case BSON_STRING_TYPE_NAME:
            case BSON_OBJECT_TYPE_NAME:
            case BSON_DBPOINTER_TYPE_NAME:
            case BSON_MAXKEY_TYPE_NAME:
            case BSON_MINKEY_TYPE_NAME:
            case BSON_TIMESTAMP_TYPE_NAME:
            case BSON_UNDEFINED_TYPE_NAME:
            case BSON_BSON_TYPE_NAME:
            case BSON_JAVASCRIPT_TYPE_NAME:
            case BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case BSON_REGEX_TYPE_NAME:
            case BSON_SYMBOL_TYPE_NAME:
            case BSON_ARRAY_TYPE_NAME:
                return null;
            case BSON_OBJECTID_TYPE_NAME:
            case BSON_DATE_TYPE_NAME:
                return 24;
            case BSON_INT_TYPE_NAME:
                return 10;
            case BSON_BOOL_TYPE_NAME:
                return 1;
            case BSON_DECIMAL_TYPE_NAME:
                return 34;
            case BSON_DOUBLE_TYPE_NAME:
                return 15;
            case BSON_LONG_TYPE_NAME:
                return 19;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static boolean getCaseSensitivity(String typeName) throws SQLException {
        switch (typeName) {
            case BSON_STRING_TYPE_NAME:
            case BSON_REGEX_TYPE_NAME:
            case BSON_SYMBOL_TYPE_NAME:
            case BSON_JAVASCRIPT_TYPE_NAME:
            case BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME:
                return true;
            case BSON_BINDATA_TYPE_NAME:
            case BSON_INT_TYPE_NAME:
            case BSON_BOOL_TYPE_NAME:
            case BSON_DATE_TYPE_NAME:
            case BSON_DECIMAL_TYPE_NAME:
            case BSON_DOUBLE_TYPE_NAME:
            case BSON_LONG_TYPE_NAME:
            case BSON_ARRAY_TYPE_NAME:
            case BSON_OBJECT_TYPE_NAME:
            case BSON_OBJECTID_TYPE_NAME:
            case BSON_DBPOINTER_TYPE_NAME:
            case BSON_MAXKEY_TYPE_NAME:
            case BSON_MINKEY_TYPE_NAME:
            case BSON_TIMESTAMP_TYPE_NAME:
            case BSON_UNDEFINED_TYPE_NAME:
            case BSON_BSON_TYPE_NAME:
                return false;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getMinScale(String typeName) throws SQLException {
        switch (typeName) {
            case BSON_DECIMAL_TYPE_NAME:
                return 34;
            case BSON_DOUBLE_TYPE_NAME:
                return 15;
            case BSON_BINDATA_TYPE_NAME:
            case BSON_STRING_TYPE_NAME:
            case BSON_INT_TYPE_NAME:
            case BSON_BOOL_TYPE_NAME:
            case BSON_DATE_TYPE_NAME:
            case BSON_LONG_TYPE_NAME:
            case BSON_ARRAY_TYPE_NAME:
            case BSON_OBJECT_TYPE_NAME:
            case BSON_OBJECTID_TYPE_NAME:
            case BSON_DBPOINTER_TYPE_NAME:
            case BSON_JAVASCRIPT_TYPE_NAME:
            case BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case BSON_MAXKEY_TYPE_NAME:
            case BSON_MINKEY_TYPE_NAME:
            case BSON_REGEX_TYPE_NAME:
            case BSON_SYMBOL_TYPE_NAME:
            case BSON_TIMESTAMP_TYPE_NAME:
            case BSON_UNDEFINED_TYPE_NAME:
            case BSON_BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getMaxScale(String typeName) throws SQLException {
        switch (typeName) {
            case BSON_DECIMAL_TYPE_NAME:
                return 34;
            case BSON_DOUBLE_TYPE_NAME:
                return 15;
            case BSON_DATE_TYPE_NAME:
                return 3;

            case BSON_BINDATA_TYPE_NAME:
            case BSON_STRING_TYPE_NAME:
            case BSON_INT_TYPE_NAME:
            case BSON_BOOL_TYPE_NAME:
            case BSON_LONG_TYPE_NAME:
            case BSON_ARRAY_TYPE_NAME:
            case BSON_OBJECT_TYPE_NAME:
            case BSON_OBJECTID_TYPE_NAME:
            case BSON_DBPOINTER_TYPE_NAME:
            case BSON_JAVASCRIPT_TYPE_NAME:
            case BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case BSON_MAXKEY_TYPE_NAME:
            case BSON_MINKEY_TYPE_NAME:
            case BSON_REGEX_TYPE_NAME:
            case BSON_SYMBOL_TYPE_NAME:
            case BSON_TIMESTAMP_TYPE_NAME:
            case BSON_UNDEFINED_TYPE_NAME:
            case BSON_BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getNumPrecRadix(String typeName) throws SQLException {
        switch (typeName) {
            case BSON_DECIMAL_TYPE_NAME:
                return 10;
            case BSON_INT_TYPE_NAME:
            case BSON_DOUBLE_TYPE_NAME:
            case BSON_LONG_TYPE_NAME:
                return 2;
            case BSON_BINDATA_TYPE_NAME:
            case BSON_STRING_TYPE_NAME:
            case BSON_BOOL_TYPE_NAME:
            case BSON_DATE_TYPE_NAME:
            case BSON_ARRAY_TYPE_NAME:
            case BSON_OBJECT_TYPE_NAME:
            case BSON_OBJECTID_TYPE_NAME:
            case BSON_DBPOINTER_TYPE_NAME:
            case BSON_JAVASCRIPT_TYPE_NAME:
            case BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case BSON_MAXKEY_TYPE_NAME:
            case BSON_MINKEY_TYPE_NAME:
            case BSON_REGEX_TYPE_NAME:
            case BSON_SYMBOL_TYPE_NAME:
            case BSON_TIMESTAMP_TYPE_NAME:
            case BSON_UNDEFINED_TYPE_NAME:
            case BSON_BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }
}
