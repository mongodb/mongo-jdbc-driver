package com.mongodb.jdbc;

import java.sql.Types;

public enum BsonTypeInfo {
    BSON_DOUBLE("double", Types.DOUBLE, false, 15, 15, 2, 15, 15, 8),
    BSON_STRING("string", Types.LONGNVARCHAR, true, 0, 0, 0, null, null, null),
    BSON_OBJECT("object", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_ARRAY("array", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BINDATA("bindData", Types.BINARY, false, 0, 0, 0, null, null, null),
    BSON_UNDEFINED("undefined", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_OBJECTID("objectId", Types.OTHER, false, 0, 0, 0, 24, null, null),
    BSON_BOOLEAN("boolean", Types.BIT, false, 0, 0, 0, 1, null, 1),
    BSON_DATE("date", Types.TIMESTAMP, false, 0, 3, 0, 24, 3, 8),
    BSON_NULL("null", Types.NULL, false, 0, 0, 0, null, null, null),
    BSON_REGEX("regex", Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_DBPOINTER("dbPointer", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPT("javascript", Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_SYMBOL("symbol", Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPTWITHSCOPE("javascriptWithScope", Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_INT("int", Types.INTEGER, false, 0, 0, 2, 10, null, 4),
    BSON_TIMESTAMP("timestamp", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_LONG("long", Types.BIGINT, false, 0, 0, 2, 19, null, 8),
    BSON_DECIMAL("decimal", Types.DECIMAL, false, 34, 34, 10, 34, 34, 16),
    BSON_MINKEY("minKey", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_MAXKEY("maxKey", Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BSON("maxKey", Types.OTHER, false, 0, 0, 0, null, null, null);

    private final String bsonName;
    private final int jdbcType;
    private final boolean caseSensitivity;
    private final int minScale;
    private final int maxScale;
    private final int numPrecRadix;
    private final Integer precision;
    private final Integer decimalDigits;
    private final Integer charOctetLength;

    BsonTypeInfo(String bsonName, int jdbcType, boolean caseSensitivity, int minScale, int maxScale, int numPrecRadix, Integer precision, Integer decimalDigits, Integer charOctetLength) {
        this.bsonName = bsonName;
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
