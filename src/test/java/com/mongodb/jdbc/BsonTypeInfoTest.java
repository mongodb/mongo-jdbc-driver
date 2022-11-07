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

import static com.mongodb.jdbc.BsonTypeInfo.BSON_ARRAY;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_BINDATA;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_BOOL;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_DATE;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_DBPOINTER;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_DECIMAL;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_DOUBLE;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_INT;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_JAVASCRIPT;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_LONG;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_MAXKEY;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_MINKEY;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_NULL;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_OBJECT;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_OBJECTID;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_REGEX;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_STRING;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_SYMBOL;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_TIMESTAMP;
import static com.mongodb.jdbc.BsonTypeInfo.BSON_UNDEFINED;
import static com.mongodb.jdbc.BsonTypeInfo.getBsonTypeInfoByName;
import static com.mongodb.jdbc.BsonTypeInfo.getBsonTypeInfoFromBsonValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDbPointer;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonJavaScript;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonSymbol;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class BsonTypeInfoTest {
    @Test
    void testGetBsonTypeInfoByName() throws SQLException {
        assertEquals(BSON_ARRAY, getBsonTypeInfoByName("array"));
        assertEquals(BSON_BOOL, getBsonTypeInfoByName("bool"));
        assertEquals(BSON_BINDATA, getBsonTypeInfoByName("binData"));
        assertEquals(BSON_DATE, getBsonTypeInfoByName("date"));
        assertEquals(BSON_DBPOINTER, getBsonTypeInfoByName("dbPointer"));
        assertEquals(BSON_DECIMAL, getBsonTypeInfoByName("decimal"));
        assertEquals(BSON_DOUBLE, getBsonTypeInfoByName("double"));
        assertEquals(BSON_INT, getBsonTypeInfoByName("int"));
        assertEquals(BSON_JAVASCRIPT, getBsonTypeInfoByName("javascript"));
        assertEquals(BSON_JAVASCRIPTWITHSCOPE, getBsonTypeInfoByName("javascriptWithScope"));
        assertEquals(BSON_LONG, getBsonTypeInfoByName("long"));
        assertEquals(BSON_MAXKEY, getBsonTypeInfoByName("maxKey"));
        assertEquals(BSON_MINKEY, getBsonTypeInfoByName("minKey"));
        assertEquals(BSON_NULL, getBsonTypeInfoByName("null"));
        assertEquals(BSON_OBJECT, getBsonTypeInfoByName("object"));
        assertEquals(BSON_OBJECTID, getBsonTypeInfoByName("objectId"));
        assertEquals(BSON_REGEX, getBsonTypeInfoByName("regex"));
        assertEquals(BSON_STRING, getBsonTypeInfoByName("string"));
        assertEquals(BSON_SYMBOL, getBsonTypeInfoByName("symbol"));
        assertEquals(BSON_TIMESTAMP, getBsonTypeInfoByName("timestamp"));
        assertEquals(BSON_UNDEFINED, getBsonTypeInfoByName("undefined"));

        // Test invalid type name
        assertThrows(
                SQLException.class,
                () -> getBsonTypeInfoByName("invalid"),
                "invalid BSON type name expected to throw exception");
    }

    @Test
    void testGetBsonTypeInfoFromBsonValue() throws SQLException {
        assertEquals(BSON_ARRAY, getBsonTypeInfoFromBsonValue(new BsonArray()));
        assertEquals(BSON_BOOL, getBsonTypeInfoFromBsonValue(new BsonBoolean(true)));
        assertEquals(
                BSON_BINDATA,
                getBsonTypeInfoFromBsonValue(new BsonBinary("a".getBytes(StandardCharsets.UTF_8))));
        assertEquals(BSON_DATE, getBsonTypeInfoFromBsonValue(new BsonDateTime(1)));
        assertEquals(
                BSON_DBPOINTER,
                getBsonTypeInfoFromBsonValue(new BsonDbPointer("test", new ObjectId())));
        assertEquals(
                BSON_DECIMAL, getBsonTypeInfoFromBsonValue(new BsonDecimal128(new Decimal128(1))));
        assertEquals(BSON_DOUBLE, getBsonTypeInfoFromBsonValue(new BsonDouble(2.2)));
        assertEquals(BSON_INT, getBsonTypeInfoFromBsonValue(new BsonInt32(1)));
        assertEquals(BSON_JAVASCRIPT, getBsonTypeInfoFromBsonValue(new BsonJavaScript("")));
        assertEquals(
                BSON_JAVASCRIPTWITHSCOPE,
                getBsonTypeInfoFromBsonValue(new BsonJavaScriptWithScope("", new BsonDocument())));
        assertEquals(BSON_LONG, getBsonTypeInfoFromBsonValue(new BsonInt64(1)));
        assertEquals(BSON_MAXKEY, getBsonTypeInfoFromBsonValue(new BsonMaxKey()));
        assertEquals(BSON_MINKEY, getBsonTypeInfoFromBsonValue(new BsonMinKey()));
        assertEquals(BSON_NULL, getBsonTypeInfoFromBsonValue(new BsonNull()));
        assertEquals(BSON_OBJECTID, getBsonTypeInfoFromBsonValue(new BsonObjectId()));
        assertEquals(BSON_REGEX, getBsonTypeInfoFromBsonValue(new BsonRegularExpression("")));
        assertEquals(BSON_STRING, getBsonTypeInfoFromBsonValue(new BsonString("")));
        assertEquals(BSON_SYMBOL, getBsonTypeInfoFromBsonValue(new BsonSymbol("")));
        assertEquals(BSON_TIMESTAMP, getBsonTypeInfoFromBsonValue(new BsonTimestamp()));
        assertEquals(BSON_UNDEFINED, getBsonTypeInfoFromBsonValue(new BsonUndefined()));
        assertEquals(BSON_OBJECT, getBsonTypeInfoFromBsonValue(new BsonDocument()));
    }
}
