package com.mongodb.jdbc;

import org.bson.BsonType;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum BsonTypeInfo {
    BSON_DOUBLE("double", BsonType.DOUBLE, Types.DOUBLE, false, 15, 15, 2, 15, 15, 8),
    BSON_STRING("string", BsonType.STRING, Types.LONGNVARCHAR, true, 0, 0, 0, null, null, null),
    BSON_OBJECT("object", BsonType.DOCUMENT, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_ARRAY("array", BsonType.ARRAY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BINDATA("binData", BsonType.BINARY, Types.BINARY, false, 0, 0, 0, null, null, null),
    BSON_UNDEFINED("undefined", BsonType.UNDEFINED, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_OBJECTID("objectId", BsonType.OBJECT_ID, Types.OTHER, false, 0, 0, 0, 24, null, null),
    BSON_BOOLEAN("boolean", BsonType.BOOLEAN, Types.BIT, false, 0, 0, 0, 1, null, 1),
    BSON_DATE("date", BsonType.DATE_TIME, Types.TIMESTAMP, false, 0, 3, 0, 24, 3, 8),
    BSON_NULL("null", BsonType.NULL, Types.NULL, false, 0, 0, 0, null, null, null),
    BSON_REGEX("regex", BsonType.REGULAR_EXPRESSION, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_DBPOINTER("dbPointer", BsonType.DB_POINTER, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPT("javascript", BsonType.JAVASCRIPT, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_SYMBOL("symbol", BsonType.SYMBOL, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPTWITHSCOPE("javascriptWithScope", BsonType.JAVASCRIPT_WITH_SCOPE, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_INT("int", BsonType.INT32, Types.INTEGER, false, 0, 0, 2, 10, null, 4),
    BSON_TIMESTAMP("timestamp", BsonType.TIMESTAMP, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_LONG("long", BsonType.INT64, Types.BIGINT, false, 0, 0, 2, 19, null, 8),
    BSON_DECIMAL("decimal", BsonType.DECIMAL128, Types.DECIMAL, false, 34, 34, 10, 34, 34, 16),
    BSON_MINKEY("minKey", BsonType.MIN_KEY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_MAXKEY("maxKey", BsonType.MAX_KEY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BSON("bson", null, Types.OTHER, false, 0, 0, 0, null, null, null);

    private static final Set<String> BSON_TYPE_NAMES = new HashSet<>(Arrays.asList(
            BSON_DOUBLE.bsonName,
            BSON_STRING.bsonName,
            BSON_OBJECT.bsonName,
            BSON_ARRAY.bsonName,
            BSON_BINDATA.bsonName,
            BSON_UNDEFINED.bsonName,
            BSON_OBJECTID.bsonName,
            BSON_BOOLEAN.bsonName,
            BSON_DATE.bsonName,
            BSON_NULL.bsonName,
            BSON_REGEX.bsonName,
            BSON_DBPOINTER.bsonName,
            BSON_JAVASCRIPT.bsonName,
            BSON_SYMBOL.bsonName,
            BSON_JAVASCRIPTWITHSCOPE.bsonName,
            BSON_INT.bsonName,
            BSON_TIMESTAMP.bsonName,
            BSON_LONG.bsonName,
            BSON_DECIMAL.bsonName,
            BSON_MINKEY.bsonName,
            BSON_MAXKEY.bsonName));

    private final String bsonName;
    private final BsonType bsonType;
    private final int jdbcType;
    private final boolean caseSensitivity;
    private final int minScale;
    private final int maxScale;
    private final int numPrecRadix;
    private final Integer precision;
    private final Integer decimalDigits;
    private final Integer charOctetLength;

    BsonTypeInfo(String bsonName, BsonType bsonType, int jdbcType, boolean caseSensitivity, int minScale, int maxScale, int numPrecRadix, Integer precision, Integer decimalDigits, Integer charOctetLength) {
        this.bsonName = bsonName;
        this.bsonType = bsonType;
        this.jdbcType = jdbcType;
        this.caseSensitivity = caseSensitivity;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.numPrecRadix = numPrecRadix;
        this.precision = precision;
        this.decimalDigits = decimalDigits;
        this.charOctetLength = charOctetLength;
    }

    public String getBsonName() {
        return bsonName;
    }

    public BsonType getBsonType() {
        return bsonType;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public boolean isCaseSensitivity() {
        return caseSensitivity;
    }

    public int getMinScale() {
        return minScale;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public int getNumPrecRadix() {
        return numPrecRadix;
    }

    public Integer getPrecision() {
        return precision;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public Integer getCharOctetLength() {
        return charOctetLength;
    }

    /**
     * Gets the BsonTypeInfo and nullability based on a MongoJsonSchema. The
     * selected BsonTypeInfo is based on the contents of the schema; the nullability
     * is based on both the contents of the schema and the boolean argument that
     * indicates whether this schema is required in a parent schema.
     *
     * @param schema The MongoJsonSchema for which to return BsonTypeInfo and nullability
     * @param required A flag indicating if this schema is required in a parent schema
     * @return The BsonTypeInfo and nullability info for the argued schema
     * @throws SQLException If there is an invalid schema provided
     */
    public Pair<BsonTypeInfo, Integer> getBsonTypeInfoAndNullabilityFromMongoSchema(MongoJsonSchema schema, boolean required) throws SQLException {
        int nullable = required ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable;

        // If the schema has a bson type, return it
        if (schema.bsonType != null) {
            nullable =
                    schema.bsonType.equals(BSON_NULL.bsonName)
                            ? DatabaseMetaData.columnNullable
                            : nullable;
            return new Pair<>(getBsonTypeInfoByName(schema.bsonType), nullable);
        }

        // If the schema is any, use the type "bson"
        if (schema.isAny()) {
            return new Pair<>(BSON_BSON, DatabaseMetaData.columnNullable);
        }

        // Otherwise, the schema must be an AnyOf
        if (schema.anyOf == null) {
            throw new SQLException(
                    "invalid schema: both bsonType and anyOf are null and this is not ANY");
        }

        BsonTypeInfo bsonTypeInfo = null;

        for (MongoJsonSchema anyOfSchema : schema.anyOf) {
            if (anyOfSchema.bsonType == null) {
                // Schemata returned by MongoSQL must be simplified. Having nested anyOf is invalid.
                throw new SQLException(
                        "invalid schema: anyOf subschema must have bsonType field; nested anyOf must be simplified");
            }
            // Presence of null means this is nullable, regardless of whether the required keys
            // of the parent object schema indicate this is nullable.
            if (anyOfSchema.bsonType.equals(BSON_NULL.bsonName)) {
                // This takes precedence over the nullability set by the required argument.
                nullable = DatabaseMetaData.columnNullable;
            } else {
                // If bsonTypeInfo is not null, there must be more than one non-null anyOf type, so
                // we default to "bson".
                if (bsonTypeInfo != null) {
                    bsonTypeInfo = BSON_BSON;
                } else {
                    bsonTypeInfo = getBsonTypeInfoByName(anyOfSchema.bsonType);
                }
            }
        }

        return new Pair<>(bsonTypeInfo, nullable);
    }

    /**
     * Converts a bson type name to a BsonTypeInfo
     *
     * @param typeName is the bson type string as returned from the mongodb aggregation $type
     *     function, this list of viable inputs is in the BSON_TYPE_NAMES static Set above.
     * @return BsonTypeInfo object corresponding to bson type name.
     */
    public static BsonTypeInfo getBsonTypeInfoByName(String typeName) throws SQLException {
        if (!BSON_TYPE_NAMES.contains(typeName)) {
            throw new SQLException("Unknown bson type name: \"" + typeName + "\"");
        }

        // All type names can be determined uniquely off a combination of first letter
        // and length except for "minKey" vs "maxKey" and "string" vs "symbol".
        switch (typeName.charAt(0)) {
            case 'a':
                return BSON_ARRAY;
            case 'b':
                switch (typeName.length()) {
                    case 4:
                        return BSON_BOOLEAN;
                    case 7:
                        return BSON_BINDATA;
                }
                break;
            case 'd':
                switch (typeName.length()) {
                    case 4:
                        return BSON_DATE;
                    case 6:
                        return BSON_DOUBLE;
                    case 7:
                        return BSON_DECIMAL;
                    case 9:
                        return BSON_DBPOINTER;
                }
                break;
            case 'i':
                return BSON_INT;
            case 'j':
                switch (typeName.length()) {
                    case 10:
                        return BSON_JAVASCRIPT;
                    case 19:
                        return BSON_JAVASCRIPTWITHSCOPE;
                }
                break;
            case 'l':
                return BSON_LONG;
            case 'm':
                switch (typeName.charAt(1)) {
                    case 'a':
                        return BSON_MAXKEY;
                    case 'i':
                        return BSON_MINKEY;
                }
                break;
            case 'n':
                return BSON_NULL;
            case 'o':
                switch (typeName.length()) {
                    case 6: // "object"
                        return BSON_OBJECT;
                    case 8:
                        return BSON_OBJECTID;
                }
                break;
            case 'r':
                return BSON_REGEX;
            case 's':
                switch (typeName.charAt(1)) {
                    case 't':
                        return BSON_STRING;
                    case 'y':
                        return BSON_SYMBOL;
                }
                break;
            case 't':
                return BSON_TIMESTAMP;
            case 'u':
                return BSON_UNDEFINED;
        }

        // This is unreachable.
        throw new SQLException("Unknown bson type name: \"" + typeName + "\"");
    }
}
