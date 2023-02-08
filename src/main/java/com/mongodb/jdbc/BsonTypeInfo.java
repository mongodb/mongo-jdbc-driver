/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bson.BsonType;
import org.bson.BsonValue;

public enum BsonTypeInfo {
    BSON_DOUBLE("double", BsonType.DOUBLE, Types.DOUBLE, false, 15, 15, 2, 15, 15, 8),
    BSON_STRING("string", BsonType.STRING, Types.LONGVARCHAR, true, 0, 0, 0, null, null, null),
    BSON_OBJECT("object", BsonType.DOCUMENT, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_ARRAY("array", BsonType.ARRAY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BINDATA("binData", BsonType.BINARY, Types.BINARY, false, 0, 0, 0, null, null, null),
    BSON_UNDEFINED("undefined", BsonType.UNDEFINED, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_OBJECTID("objectId", BsonType.OBJECT_ID, Types.OTHER, false, 0, 0, 0, 24, null, null),
    BSON_BOOL("bool", BsonType.BOOLEAN, Types.BOOLEAN, false, 0, 0, 0, 1, null, 1),
    BSON_DATE("date", BsonType.DATE_TIME, Types.TIMESTAMP, false, 0, 3, 0, 24, 3, 8),
    BSON_NULL("null", BsonType.NULL, Types.NULL, false, 0, 0, 0, null, null, null),
    BSON_REGEX("regex", BsonType.REGULAR_EXPRESSION, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_DBPOINTER("dbPointer", BsonType.DB_POINTER, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPT(
            "javascript", BsonType.JAVASCRIPT, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_SYMBOL("symbol", BsonType.SYMBOL, Types.OTHER, true, 0, 0, 0, null, null, null),
    BSON_JAVASCRIPTWITHSCOPE(
            "javascriptWithScope",
            BsonType.JAVASCRIPT_WITH_SCOPE,
            Types.OTHER,
            true,
            0,
            0,
            0,
            null,
            null,
            null),
    BSON_INT("int", BsonType.INT32, Types.INTEGER, false, 0, 0, 2, 10, null, 4),
    BSON_TIMESTAMP("timestamp", BsonType.TIMESTAMP, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_LONG("long", BsonType.INT64, Types.BIGINT, false, 0, 0, 2, 19, null, 8),
    BSON_DECIMAL("decimal", BsonType.DECIMAL128, Types.DECIMAL, false, 34, 34, 10, 34, 34, 16),
    BSON_MINKEY("minKey", BsonType.MIN_KEY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_MAXKEY("maxKey", BsonType.MAX_KEY, Types.OTHER, false, 0, 0, 0, null, null, null),
    BSON_BSON("bson", BsonType.UNDEFINED, Types.OTHER, false, 0, 0, 0, null, null, null);

    // BSON_TYPE_NAMES is the set of all valid BSON type names as listed
    // here: https://mongodb.github.io/mongo-java-driver/3.12/javadoc/org/bson/BsonType.html
    // Note that BsonTypeInfo contains a BSON_BSON variant which is intentionally
    // omitted from this set. That is because "bson" is our catch-all for a
    // value that has "any" type, but is not an actual specific BSON type.
    private static final Set<String> BSON_TYPE_NAMES =
            new HashSet<>(
                    Arrays.asList(
                            BSON_DOUBLE.bsonName,
                            BSON_STRING.bsonName,
                            BSON_OBJECT.bsonName,
                            BSON_ARRAY.bsonName,
                            BSON_BINDATA.bsonName,
                            BSON_UNDEFINED.bsonName,
                            BSON_OBJECTID.bsonName,
                            BSON_BOOL.bsonName,
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
    // Minimum scale for numeric data and date time data.
    private final int minScale;
    // Maximum scale for numeric data and date time data.
    private final int maxScale;
    // Radix (typically either 10 or 2) for numeric data.
    private final int numPrecRadix;
    // The column/data size for the given type.
    //  - For numeric data, this is the maximum precision.
    //  - For character data, this is the maximum length in characters.
    //  - For datetime data types, this is the length in characters of the String representation (assuming the maximum
    //  allowed precision of the fractional seconds component).
    //  - For binary data, this is the maximum length in bytes.
    //  - For ObjectId (row id data type) data, this is the length in bytes.
    //  - Null is returned for data types where the column size is not applicable.
    private final Integer precision;
    // The number of fractional digits for numeric and date time data. Null is returned for data types where
    // DECIMAL_DIGITS is not applicable.
    // Also known as scale.
    private final Integer decimalDigits;
    // The length in bytes for fixed length data type.
    private final Integer fixedBytesLength;

    BsonTypeInfo(
            String bsonName,
            BsonType bsonType,
            int jdbcType,
            boolean caseSensitivity,
            int minScale,
            int maxScale,
            int numPrecRadix,
            Integer precision,
            Integer decimalDigits,
            Integer fixedBytesLength) {
        this.bsonName = bsonName;
        this.bsonType = bsonType;
        this.jdbcType = jdbcType;
        this.caseSensitivity = caseSensitivity;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.numPrecRadix = numPrecRadix;
        this.precision = precision;
        this.decimalDigits = decimalDigits;
        this.fixedBytesLength = fixedBytesLength;
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

    public boolean getCaseSensitivity() {
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

    public Integer getFixedBytesLength() {
        return fixedBytesLength;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * CHAR_OCTET_LENGTH is the maximum length of binary and character based data in bytes. For any
     * other datatype the value is null. We can use 'precision' combined with the data type for
     * reporting the correct info.
     */
    public Integer getCharOctetLength() {
        switch (this.bsonType) {
            case BINARY:
            case STRING:
                return this.precision;
            default:
                return null;
        }
    }

    /**
     * Converts a bson value to a BsonTypeInfo
     *
     * @param obj is the Bson value to convert.
     * @return BsonTypeInfo object corresponding to the Bson type.
     * @throws SQLException
     */
    public static BsonTypeInfo getBsonTypeInfoFromBsonValue(BsonValue obj) throws SQLException {
        if (obj == null) {
            throw new SQLException("Missing bson type name. Value is Null");
        }
        BsonType type = obj.getBsonType();
        switch (type) {
            case DOUBLE:
                return BSON_DOUBLE;
            case STRING:
                return BSON_STRING;
            case DOCUMENT:
                // BsonDocument and BSON_OBJECT are synonymous. To maintain consistency within ADF,
                // BsonDocuments will be treated as BSON_OBJECTs
                return BSON_OBJECT;
            case ARRAY:
                return BSON_ARRAY;
            case BINARY:
                return BSON_BINDATA;
            case UNDEFINED:
                return BSON_UNDEFINED;
            case OBJECT_ID:
                return BSON_OBJECTID;
            case BOOLEAN:
                return BSON_BOOL;
            case DATE_TIME:
                return BSON_DATE;
            case NULL:
                return BSON_NULL;
            case REGULAR_EXPRESSION:
                return BSON_REGEX;
            case DB_POINTER:
                return BSON_DBPOINTER;
            case JAVASCRIPT:
                return BSON_JAVASCRIPT;
            case SYMBOL:
                return BSON_SYMBOL;
            case JAVASCRIPT_WITH_SCOPE:
                return BSON_JAVASCRIPTWITHSCOPE;
            case INT32:
                return BSON_INT;
            case TIMESTAMP:
                return BSON_TIMESTAMP;
            case INT64:
                return BSON_LONG;
            case DECIMAL128:
                return BSON_DECIMAL;
            case MIN_KEY:
                return BSON_MINKEY;
            case MAX_KEY:
                return BSON_MAXKEY;
            default:
                throw new SQLException("Unknown bson type name: \"" + type.name() + "\"");
        }
    }

    /**
     * Converts a bson type name to a BsonTypeInfo
     *
     * @param typeName is the bson type string as returned from the mongodb aggregation $type
     *     function, this list of viable inputs is in the BSON_TYPE_NAMES static Set above.
     * @return BsonTypeInfo object corresponding to bson type name.
     */
    public static BsonTypeInfo getBsonTypeInfoByName(String typeName) throws SQLException {
        if (typeName == null) {
            throw new SQLException("Missing bson type name. Value is Null");
        }
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
                        return BSON_BOOL;
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
                    case 6:
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
