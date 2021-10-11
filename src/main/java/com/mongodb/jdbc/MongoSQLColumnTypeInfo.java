package com.mongodb.jdbc;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.ResultSetMetaData;

class MongoSQLColumnTypeInfo {
        int jdbcType;
        ExtendedBsonType bsonType;
        String bsonTypeName;
        int nullable;

        MongoSQLColumnTypeInfo(MongoJsonSchema schema, boolean nullable) throws SQLException {
            // All schemata except AnyOf and Unsat must have a ExtendedBsonType (and we do not support
            // Unsat).
            if(schema.bsonType != null) {
                this.bsonTypeName = bsonTypeName;
                this.bsonType = MongoResultSetMetaData.getExtendedBsonTypeHelper(bsonTypeName);
                this.jdbcType = getJDBCTypeForExtendedBsonType(this.bsonType);
                this.nullable = convertNullable(nullable);
                return;
            }
            // Otherwise, the schema must be an AnyOf.
            constructFromAnyOf(schema, nullable);
        }

        private int convertNullable(boolean nullable) {
             return nullable ?
                    ResultSetMetaData.columnNullable
                    :ResultSetMetaData.columnNoNulls;
        }

        private void constructFromAnyOf(MongoJsonSchema schema, boolean nullable) throws SQLException {
            if(schema.anyOf == null) {
                throw new SQLException("both bsonType and anyOf are null, this is not a valid schema");
            }
            for(MongoJsonSchema anyOfSchema: schema.anyOf) {
                if(anyOfSchema.bsonType == null) {
                    throw new SQLException("anyOf subschema must have bsonType field");
                }
                // Presense of null means this is nullable, whether or not the required keys
                // of the parent object schema indicate this is nullable.
                if(anyOfSchema.bsonType.equals("null")) {
                    nullable = true;
                } else {
                    // If bsonTypeName is not null, there must be more than one non-null anyOf type, so
                    // we default to "bson"
                    bsonTypeName = (bsonTypeName == null)?
                         anyOfSchema.bsonType
                        :"bson";
                }
            }
            this.bsonTypeName = bsonTypeName;
            this.bsonType = MongoResultSetMetaData.getExtendedBsonTypeHelper(bsonTypeName);
            this.jdbcType = getJDBCTypeForExtendedBsonType(this.bsonType);
            this.nullable = convertNullable(nullable);
        }

    private static int getJDBCTypeForExtendedBsonType(ExtendedBsonType t) throws SQLException {
        switch (t) {
            case ANY:
            case ARRAY:
            case DB_POINTER:
            case DOCUMENT:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case OBJECT_ID:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
            case UNDEFINED:
                return Types.OTHER;
            case BINARY:
                return Types.BINARY;
            case BOOLEAN:
                return Types.BIT;
            case DATE_TIME:
                return Types.TIMESTAMP;
            case INT32:
                return Types.INTEGER;
            case INT64:
                return Types.BIGINT;
            case NULL:
                return Types.NULL;
            case STRING:
                return  Types.LONGVARCHAR;
            case DECIMAL128:
                return Types.DECIMAL;
            case DOUBLE:
                return Types.DOUBLE;
        }
        throw new SQLException("unknown bson type: " + t);
    }
}
