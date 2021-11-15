package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

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
    public static final String NULL_TYPE_NAME = "null";
    public static final String BSON_TYPE_NAME = "bson";

    public static Pair<String, Integer> getBsonTypeNameAndNullability(
            MongoJsonSchema schema, boolean required) throws SQLException {
        int nullable = required ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable;

        if (schema.bsonType != null) {
            nullable =
                    schema.bsonType.equals(NULL_TYPE_NAME)
                            ? DatabaseMetaData.columnNullable
                            : nullable;
            return new Pair<>(schema.bsonType, nullable);
        }

        if (schema.isAny()) {
            return new Pair<>(BSON_TYPE_NAME, DatabaseMetaData.columnNullable);
        }

        // Otherwise, the schema must be an AnyOf
        if (schema.anyOf == null) {
            throw new SQLException(
                    "invalid schema: both bsonType and anyOf are null and this is not ANY");
        }

        String bsonTypeName = null;

        for (MongoJsonSchema anyOfSchema : schema.anyOf) {
            if (anyOfSchema.bsonType == null) {
                // Schemata returned by MongoSQL must be simplified. Having nested anyOf is invalid.
                throw new SQLException(
                        "invalid schema: anyOf subschema must have bsonType field; nested anyOf must be simplified");
            }
            // Presence of null means this is nullable, whether or not the required keys
            // of the parent object schema indicate this is nullable.
            if (anyOfSchema.bsonType.equals(NULL_TYPE_NAME)) {
                // This will take precedent over the nullability set by the required argument.
                nullable = DatabaseMetaData.columnNullable;
            } else {
                // If bsonTypeName is not null, there must be more than one non-null anyOf type, so
                // we default to "bson".
                if (bsonTypeName != null) {
                    bsonTypeName = BSON_TYPE_NAME;
                } else {
                    bsonTypeName = anyOfSchema.bsonType;
                }
            }
        }

        return new Pair<>(bsonTypeName, nullable);
    }

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
            case NULL_TYPE_NAME:
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
            case NULL_TYPE_NAME:
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
            case NULL_TYPE_NAME:
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
            case NULL_TYPE_NAME:
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
            case NULL_TYPE_NAME:
            case BSON_TYPE_NAME:
                return 0;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static Integer getDecimalDigits(String typeName) throws SQLException {
        switch (typeName) {
            case DECIMAL_TYPE_NAME:
                return 34;
            case DOUBLE_TYPE_NAME:
                return 15;
            case DATE_TYPE_NAME:
                return 3;
            case INT_TYPE_NAME:
            case LONG_TYPE_NAME:
            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
            case BOOL_TYPE_NAME:
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
            case NULL_TYPE_NAME:
            case BSON_TYPE_NAME:
                return null;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static Integer getCharOctetLength(String typeName) throws SQLException {
        switch (typeName) {
            case DECIMAL_TYPE_NAME:
                return 16;
            case DOUBLE_TYPE_NAME:
            case LONG_TYPE_NAME:
            case DATE_TYPE_NAME:
                return 8;
            case INT_TYPE_NAME:
                return 4;
            case BOOL_TYPE_NAME:
                return 1;
            case BINDATA_TYPE_NAME:
            case STRING_TYPE_NAME:
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
            case NULL_TYPE_NAME:
            case BSON_TYPE_NAME:
                return null;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }

    public static int getJDBCType(String typeName) throws SQLException {
        switch (typeName) {
            case ARRAY_TYPE_NAME:
            case DBPOINTER_TYPE_NAME:
            case OBJECT_TYPE_NAME:
            case JAVASCRIPT_TYPE_NAME:
            case JAVASCRIPTWITHSCOPE_TYPE_NAME:
            case MAXKEY_TYPE_NAME:
            case MINKEY_TYPE_NAME:
            case OBJECTID_TYPE_NAME:
            case REGEX_TYPE_NAME:
            case SYMBOL_TYPE_NAME:
            case TIMESTAMP_TYPE_NAME:
            case UNDEFINED_TYPE_NAME:
                return Types.OTHER;
            case BINDATA_TYPE_NAME:
                return Types.BINARY;
            case BOOL_TYPE_NAME:
                return Types.BIT;
            case DATE_TYPE_NAME:
                return Types.TIMESTAMP;
            case INT_TYPE_NAME:
                return Types.INTEGER;
            case LONG_TYPE_NAME:
                return Types.BIGINT;
            case NULL_TYPE_NAME:
                return Types.NULL;
            case STRING_TYPE_NAME:
                return Types.LONGVARCHAR;
            case DECIMAL_TYPE_NAME:
                return Types.DECIMAL;
            case DOUBLE_TYPE_NAME:
                return Types.DOUBLE;
        }
        throw new SQLException("unknown bson typeName: " + typeName);
    }
}
