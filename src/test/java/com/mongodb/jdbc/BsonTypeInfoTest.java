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

import static com.mongodb.jdbc.BsonTypeInfo.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

import org.bson.*;
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
        assertEquals(BSON_DOCUMENT, getBsonTypeInfoByName("document"));

        // Test invalid type name
        assertThrows(
                SQLException.class,
                () -> getBsonTypeInfoByName("invalid"),
                "invalid BSON type name expected to throw exception");
    }
    @Test
    void testGetBsonTypeInfoByValue() throws SQLException {
        assertEquals(BSON_ARRAY, getBsonTypeInfoFromBson(new BsonArray()));
        assertEquals(BSON_BOOL, getBsonTypeInfoFromBson(new BsonBoolean(true)));
        assertEquals(BSON_BINDATA, getBsonTypeInfoFromBson(new BsonBinary("a".getBytes(StandardCharsets.UTF_8))));
        assertEquals(BSON_DATE, getBsonTypeInfoFromBson(new BsonDateTime(1)));
        assertEquals(BSON_DBPOINTER, getBsonTypeInfoFromBson(new BsonDbPointer("test", new ObjectId())));
        assertEquals(BSON_DECIMAL, getBsonTypeInfoFromBson(new BsonDecimal128(new Decimal128(1))));
        assertEquals(BSON_DOUBLE, getBsonTypeInfoFromBson(new BsonDouble(2.2)));
        assertEquals(BSON_INT, getBsonTypeInfoFromBson(new BsonInt32(1)));
        assertEquals(BSON_JAVASCRIPT, getBsonTypeInfoFromBson(new BsonJavaScript("")));
        assertEquals(BSON_JAVASCRIPTWITHSCOPE, getBsonTypeInfoFromBson(new BsonJavaScriptWithScope("", new BsonDocument())));
        assertEquals(BSON_LONG, getBsonTypeInfoFromBson(new BsonInt64(1)));
        assertEquals(BSON_MAXKEY, getBsonTypeInfoFromBson(new BsonMaxKey()));
        assertEquals(BSON_MINKEY, getBsonTypeInfoFromBson(new BsonMinKey()));
        assertEquals(BSON_NULL, getBsonTypeInfoFromBson(new BsonNull()));
        assertEquals(BSON_OBJECTID, getBsonTypeInfoFromBson(new BsonObjectId()));
        assertEquals(BSON_REGEX, getBsonTypeInfoFromBson(new BsonRegularExpression("")));
        assertEquals(BSON_STRING, getBsonTypeInfoFromBson(new BsonString("")));
        assertEquals(BSON_SYMBOL, getBsonTypeInfoFromBson(new BsonSymbol("")));
        assertEquals(BSON_TIMESTAMP, getBsonTypeInfoFromBson(new BsonTimestamp()));
        assertEquals(BSON_UNDEFINED, getBsonTypeInfoFromBson(new BsonUndefined()));
        assertEquals(BSON_DOCUMENT, getBsonTypeInfoFromBson(new BsonDocument()));
    }
}
