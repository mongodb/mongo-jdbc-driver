package com.mongodb.jdbc;

import org.bson.BsonType;
import java.sql.Types;

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
}
