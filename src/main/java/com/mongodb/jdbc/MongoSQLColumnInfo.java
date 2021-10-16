package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonType;

public class MongoSQLColumnInfo implements MongoColumnInfo {
    private String datasource;
    private String field;
    private String bsonTypeName;
    private BsonType bsonType;
    private int jdbcType;
    private boolean isPolymorphic;
    private int nullable;

    MongoSQLColumnInfo(String datasource, String field, MongoJsonSchema schema, int nullable)
            throws SQLException {
        this.datasource = datasource;
        this.field = field;
        // All schemata except Any and AnyOf must have a bsonType.
        if (schema.bsonType != null) {
            bsonTypeName = schema.bsonType;
            bsonType = MongoColumnInfo.getBsonTypeHelper(schema.bsonType);
            jdbcType = getJDBCTypeForBsonType(this.bsonType);
            isPolymorphic = false;
            this.nullable = nullable;
            return;
        }
        if (schema.isAny()) {
            bsonTypeName = "bson";
            jdbcType = Types.OTHER;
            bsonType = BsonType.UNDEFINED;
            isPolymorphic = true;
            this.nullable = ResultSetMetaData.columnNullable;
            return;
        }
        // Otherwise, the schema must be an AnyOf.
        constructFromAnyOf(schema, nullable);
    }

    private void constructFromAnyOf(MongoJsonSchema schema, int nullable) throws SQLException {
        if (schema.anyOf == null) {
            throw new SQLException(
                    "invalid schema: both bsonType and anyOf are null and this is not ANY");
        }
        for (MongoJsonSchema anyOfSchema : schema.anyOf) {
            if (anyOfSchema.bsonType == null) {
                // Schemata returned by MongoSQL must be simplified. Having nested anyOf is invalid.
                throw new SQLException(
                        "invalid schema: anyOf subschema must have bsonType field; nested anyOf must be simplified");
            }
            // Presense of null means this is nullable, whether or not the required keys
            // of the parent object schema indicate this is nullable.
            if (anyOfSchema.bsonType.equals("null")) {
                nullable = ResultSetMetaData.columnNullable;
            } else {
                // If bsonTypeName is not null, there must be more than one non-null anyOf type, so
                // we default to "bson" and set isPolymorphic to true.
                if (bsonTypeName != null) {
                    bsonTypeName = "bson";
                    isPolymorphic = true;
                } else {
                    bsonTypeName = anyOfSchema.bsonType;
                }
            }
        }
        this.nullable = nullable;
        if (isPolymorphic) {
            bsonType = BsonType.UNDEFINED;
            jdbcType = Types.OTHER;
        } else {
            bsonType = MongoColumnInfo.getBsonTypeHelper(bsonTypeName);
            jdbcType = getJDBCTypeForBsonType(bsonType);
        }
    }

    private static int getJDBCTypeForBsonType(BsonType t) throws SQLException {
        switch (t) {
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
                return Types.LONGVARCHAR;
            case DECIMAL128:
                return Types.DECIMAL;
            case DOUBLE:
                return Types.DOUBLE;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean isPolymorphic() throws SQLException {
        return isPolymorphic;
    }

    @Override
    public BsonType getBsonType() throws SQLException {
        return bsonType;
    }

    @Override
    public String getBsonTypeName() throws SQLException {
        return bsonTypeName;
    }

    @Override
    public int getJDBCType() throws SQLException {
        return jdbcType;
    }

    @Override
    public int getNullability() throws SQLException {
        return nullable;
    }

    @Override
    public String getColumnName() throws SQLException {
        return field;
    }

    @Override
    public String getColumnAlias() throws SQLException {
        return field;
    }

    @Override
    public String getTableName() throws SQLException {
        return datasource;
    }

    @Override
    public String getTableAlias() throws SQLException {
        return datasource;
    }

    @Override
    public String getDatabase() throws SQLException {
        return "";
    }
}
