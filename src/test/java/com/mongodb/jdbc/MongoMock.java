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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.mongodb.ConnectionString;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;
import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.internal.UuidHelper;
import org.bson.json.JsonReader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

public abstract class MongoMock {
    static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");
    protected static String database = "test";
    // __bot.a
    protected static int DOUBLE_COL = 1;
    // __bot.binary
    protected static int BINARY_COL = 2;
    // __bot.dup
    protected static int BOT_DUP_COL = 3;
    // __bot.str
    protected static int STRING_COL = 4;
    // foo.a
    protected static int ANY_OF_INT_STRING_COL = 5;
    // foo.b
    protected static int INT_OR_NULL_COL = 6;
    // foo.c
    protected static int INT_COL = 7;
    // foo.d
    protected static int ANY_COL = 8;
    // foo.doc
    protected static int DOC_COL = 9;
    // foo.dup
    protected static int FOO_DUP_COL = 10;
    // foo.null
    protected static int NULL_COL = 11;
    // foo.vec
    protected static int ARRAY_COL = 12;

    // __bot fields
    protected static String DOUBLE_COL_LABEL = "a";
    protected static String BINARY_COL_LABEL = "binary";
    protected static String STRING_COL_LABEL = "str";

    protected static String BOT_DUP_COL_LABEL = "dup";

    // foo fields
    protected static String ANY_OF_INT_STRING_COL_LABEL = "anyOfStrOrInt";
    protected static String INT_NULLABLE_COL_LABEL = "b";
    protected static String INT_COL_LABEL = "c";
    protected static String ANY_COL_LABEL = "d";
    protected static String DOC_COL_LABEL = "doc";
    protected static String NULL_COL_LABEL = "null";
    protected static String ARRAY_COL_LABEL = "vec";

    protected static String FOO_DUP_COL_LABEL = "dup";

    // all.double
    protected static String ALL_DOUBLE_COL_LABEL = "double";
    // all.string
    protected static String ALL_STRING_COL_LABEL = "string";
    // all.object
    protected static String ALL_OBJECT_COL_LABEL = "object";
    // all.array
    protected static String ALL_ARRAY_COL_LABEL = "array";
    // all.binary
    protected static String ALL_BINARY_COL_LABEL = "binData";
    // all.undefined
    protected static String ALL_UNDEFINED_COL_LABEL = "undefined";
    // all.objectId
    protected static String ALL_OBJECT_ID_COL_LABEL = "objectId";
    // all.bool
    protected static String ALL_BOOL_COL_LABEL = "bool";
    // all.date
    protected static String ALL_DATE_COL_LABEL = "date";
    // all.null
    protected static String ALL_NULL_COL_LABEL = "null";
    // all.regex
    protected static String ALL_REGEX_COL_LABEL = "regex";
    // all.dbPointer
    protected static String ALL_DB_POINTER_COL_LABEL = "dbPointer";
    // all.javascript
    protected static String ALL_JAVASCRIPT_COL_LABEL = "javascript";
    // all.symbol
    protected static String ALL_SYMBOL_COL_LABEL = "symbol";
    // all.javascriptWithScope
    protected static String ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL = "javascriptWithScope";
    // all.int
    protected static String ALL_INT_COL_LABEL = "int";
    // all.timestamp
    protected static String ALL_TIMESTAMP_COL_LABEL = "timestamp";
    // all.long
    protected static String ALL_LONG_COL_LABEL = "long";
    // all.decimal
    protected static String ALL_DECIMAL_COL_LABEL = "decimal";
    // all.minKey
    protected static String ALL_MIN_KEY_COL_LABEL = "minKey";
    // all.maxKey
    protected static String ALL_MAX_KEY_COL_LABEL = "maxKey";
    // all.standardUuid
    protected static String ALL_STANDARD_UUID_COL_LABEL = "standardUuid";
    // all.legacyUuid
    protected static String ALL_LEGACY_UUID_COL_LABEL = "legacyUuid";

    protected static String ALL_DOUBLE_COL_VAL = "1.0";
    protected static String ALL_STRING_COL_VAL = "\"str\"";
    protected static String ALL_OBJECT_ID_COL_VAL = "{\"$oid\": \"57e193d7a9cc81b4027498b5\"}";
    protected static String ALL_OBJECT_COL_VAL =
            "{\"x\": 10, \"y\": " + ALL_OBJECT_ID_COL_VAL + "}";
    protected static String ALL_ARRAY_COL_VAL = "[7, 8, 9]";
    protected static String ALL_BINARY_COL_VAL =
            "{\"$binary\": {\"base64\": \"\", \"subType\": \"00\"}}";
    protected static String ALL_UNDEFINED_COL_VAL = "{\"$undefined\": true}";
    protected static String ALL_BOOL_COL_VAL = "true";
    protected static String ALL_DATE_COL_VAL = "{\"$date\": \"2020-12-25T17:13:14Z\"}";
    protected static String ALL_NULL_COL_VAL = "null";
    protected static String ALL_REGEX_COL_VAL =
            "{\"$regularExpression\": {\"pattern\": \"abc\", \"options\": \"i\"}}";
    protected static String ALL_DB_POINTER_COL_VAL =
            "{\"$dbPointer\": {\"$ref\": \"db2\", \"$id\": " + ALL_OBJECT_ID_COL_VAL + "}}";
    protected static String ALL_JAVASCRIPT_COL_VAL = "{\"$code\": \"javascript\"}";
    protected static String ALL_SYMBOL_COL_VAL = "{\"$symbol\": \"sym\"}";
    protected static String ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL =
            "{\"$code\": \"code\", \"$scope\": {\"x\": 1}}";
    protected static String ALL_INT_COL_VAL = "3";
    protected static String ALL_TIMESTAMP_COL_VAL =
            "{\"$timestamp\": {\"t\": 1412180887, \"i\": 1}}";
    protected static String ALL_LONG_COL_VAL = "2147483648";
    protected static String ALL_DECIMAL_COL_VAL = "{\"$numberDecimal\": \"21.2\"}";
    protected static String ALL_MIN_KEY_COL_VAL = "{\"$minKey\": 1}";
    protected static String ALL_MAX_KEY_COL_VAL = "{\"$maxKey\": 1}";

    protected static String ALL_UUID_STRING_VAL = "00112233-4455-6677-8899-aabbccddeeff";
    protected static UUID ALL_UUID_VAL = UUID.fromString(ALL_UUID_STRING_VAL);
    protected static String ALL_UUID_STRING_EXT_VAL = "{\"$uuid\":\"" + ALL_UUID_STRING_VAL + "\"}";

    protected static String ALL_STANDARD_UUID_COL_VAL =
            String.format(
                    "{\"$binary\": {\"base64\": \"%s\", \"subType\": \"04\"}}",
                    Base64.getEncoder()
                            .encodeToString(
                                    UuidHelper.encodeUuidToBinary(
                                            ALL_UUID_VAL, UuidRepresentation.STANDARD)));

    protected static String ALL_LEGACY_UUID_COL_VAL =
            String.format(
                    "{\"$binary\": {\"base64\": \"%s\", \"subType\": \"03\"}}",
                    Base64.getEncoder()
                            .encodeToString(
                                    UuidHelper.encodeUuidToBinary(
                                            ALL_UUID_VAL, UuidRepresentation.JAVA_LEGACY)));

    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<BsonDocument> aggregateIterable;
    @Mock protected static MongoCursor<BsonDocument> mongoCursor;

    @InjectMocks
    protected static MongoConnection mongoConnection =
            new MongoConnection(
                    new MongoConnectionProperties(uri, database, null, null, null, false, null));

    private static Field getDeclaredFieldFromClassOrSuperClass(Class c, String fieldName)
            throws NoSuchFieldException {
        try {
            return c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = c.getSuperclass();
            if (superClass != null) {
                return getDeclaredFieldFromClassOrSuperClass(superClass, fieldName);
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    // reset the mock objects before every test case
    protected static void resetMockObjs() throws NoSuchFieldException {
        FieldSetter.setField(
                mongoConnection,
                getDeclaredFieldFromClassOrSuperClass(mongoConnection.getClass(), "mongoClient"),
                mongoClient);
        FieldSetter.setField(
                mongoConnection,
                getDeclaredFieldFromClassOrSuperClass(mongoConnection.getClass(), "isClosed"),
                false);
        FieldSetter.setField(
                mongoConnection,
                getDeclaredFieldFromClassOrSuperClass(mongoConnection.getClass(), "currentDB"),
                database);

        doNothing().when(mongoClient).close();
        // Mock mongoDatabase
        when(mongoConnection.getDatabase(anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.withCodecRegistry(any())).thenReturn(mongoDatabase);
        when(mongoDatabase.aggregate(any(), eq(BsonDocument.class))).thenReturn(aggregateIterable);
        // Mock aggregateIterable
        when(aggregateIterable.batchSize(anyInt())).thenReturn(aggregateIterable);
        when(aggregateIterable.maxTime(anyLong(), any())).thenReturn(aggregateIterable);
        when(aggregateIterable.cursor()).thenReturn(mongoCursor);

        // Mock MongoCursor
        when(mongoCursor.hasNext()).thenReturn(false);
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    interface TestInterface {
        void test() throws SQLException;
    }

    protected static MongoJsonSchema generateMongoJsonSchema() {
        /*
        {
            bsonType: object,
            properties: {
                foo: {
                    bsonType: object,
                    properties: {
                        c: {
                            bsonType: int
                        },
                        anyOfStrOrInt: {
                            anyOf: [
                                {bsonType: int},
                                {bsonType: string},
                            ],
                        },
                        d: {},
                        b: {
                            anyOf: [
                               {bsonType: int},
                               {bsonType: null},
                            ],
                        },
                        vec: {
                           bsonType: array,
                           items: {
                               bsonType: int,
                           }
                        },
                        null: {
                           bsonType: NULL
                        },
                        doc: {
                           bsonType: object,
                           properties: {
                              c: {
                                 bsonType: int
                              }
                           },
                           required: [c]
                        },
                        dup: {
                            bsonType: int
                       }
                    },
                    required: [anyOfStrOrInt, b, vec, doc, dup],
                },
                "": {
                   bsonType: object,
                   properties: {
                       a: {
                           bsonType: double
                       },
                       binary: {
                           bsonType: binary
                       }
                       str: {
                           bsonType: string
                       }
                       dup: {
                            bsonType: string
                       },
                      standardUuid: {
                           bsonType: binData
                       },
                       legacyUuid: {
                           bsonType: binData
                       }
                   }
                },
            },
            required: [foo, ""]
        }
        */
        MongoJsonSchema schema = new MongoJsonSchema();
        schema.bsonType = "object";
        schema.required = new HashSet<String>();
        schema.required.add("foo");

        MongoJsonSchema fooSchema = new MongoJsonSchema();
        fooSchema.bsonType = "object";
        fooSchema.required = new HashSet<String>();
        fooSchema.required.add(ANY_OF_INT_STRING_COL_LABEL);
        fooSchema.required.add(INT_NULLABLE_COL_LABEL);
        fooSchema.required.add(ARRAY_COL_LABEL);
        fooSchema.required.add(DOC_COL_LABEL);
        fooSchema.required.add(FOO_DUP_COL_LABEL);

        MongoJsonSchema aSchema = new MongoJsonSchema();
        aSchema.anyOf = new HashSet<MongoJsonSchema>();
        MongoJsonSchema anyOf1Schema = new MongoJsonSchema();
        anyOf1Schema.bsonType = "int";
        MongoJsonSchema anyOf2Schema = new MongoJsonSchema();
        anyOf2Schema.bsonType = "string";
        aSchema.anyOf.add(anyOf1Schema);
        aSchema.anyOf.add(anyOf2Schema);

        MongoJsonSchema bSchema = new MongoJsonSchema();
        bSchema.anyOf = new HashSet<MongoJsonSchema>();
        anyOf1Schema = new MongoJsonSchema();
        anyOf1Schema.bsonType = "int";
        anyOf2Schema = new MongoJsonSchema();
        anyOf2Schema.bsonType = "null";
        bSchema.anyOf.add(anyOf1Schema);
        bSchema.anyOf.add(anyOf2Schema);

        MongoJsonSchema cSchema = new MongoJsonSchema();
        cSchema.bsonType = "int";

        MongoJsonSchema vecSchema = new MongoJsonSchema();
        vecSchema.bsonType = "array";
        vecSchema.items = new MongoJsonSchema();
        vecSchema.items.bsonType = "int";

        MongoJsonSchema nullSchema = new MongoJsonSchema();
        nullSchema.bsonType = "null";

        MongoJsonSchema fooDupSchema = new MongoJsonSchema();
        fooDupSchema.bsonType = "int";

        // For the doc schema, we reuse the foo.c INT field variables
        MongoJsonSchema docSchema = MongoJsonSchema.createEmptyObjectSchema();
        docSchema.required.add(INT_COL_LABEL);
        docSchema.properties.put(INT_COL_LABEL, cSchema);

        fooSchema.properties = new LinkedHashMap<String, MongoJsonSchema>();
        fooSchema.properties.put(INT_COL_LABEL, cSchema);
        fooSchema.properties.put(ANY_OF_INT_STRING_COL_LABEL, aSchema);
        // new MongoJsonSchema() is the ANY schema
        fooSchema.properties.put(ANY_COL_LABEL, new MongoJsonSchema());
        fooSchema.properties.put(INT_NULLABLE_COL_LABEL, bSchema);
        fooSchema.properties.put(ARRAY_COL_LABEL, vecSchema);
        fooSchema.properties.put(NULL_COL_LABEL, nullSchema);
        fooSchema.properties.put(DOC_COL_LABEL, docSchema);
        fooSchema.properties.put(FOO_DUP_COL_LABEL, fooDupSchema);

        MongoJsonSchema botSchema = new MongoJsonSchema();
        botSchema.bsonType = "object";
        botSchema.properties = new LinkedHashMap<String, MongoJsonSchema>();
        aSchema = new MongoJsonSchema();
        aSchema.bsonType = "double";
        botSchema.properties.put(DOUBLE_COL_LABEL, aSchema);
        MongoJsonSchema binarySchema = new MongoJsonSchema();
        binarySchema.bsonType = "binData";
        botSchema.properties.put("binary", binarySchema);
        MongoJsonSchema strSchema = new MongoJsonSchema();
        strSchema.bsonType = "string";
        botSchema.properties.put("str", strSchema);
        MongoJsonSchema botDupSchema = new MongoJsonSchema();
        botDupSchema.bsonType = "string";
        botSchema.properties.put(BOT_DUP_COL_LABEL, botDupSchema);

        schema.properties = new LinkedHashMap<String, MongoJsonSchema>();
        schema.properties.put("foo", fooSchema);
        schema.properties.put("", botSchema);
        return schema;
    }

    BsonDocument generateRow() {
        /*
        {
            "foo.a": 1,
            "foo.b": null,
            "foo.c": 2,
            "foo.d": {
                "$undefined":true
            },
            "foo.null": null
            "foo.vec": [
                1,
                2,
                3
            ],
            "foo.doc": {
                "c": 5
            }
            "__bot.a": 1.2,
            "__bot.binary": <binary data>
            "__bot.str": "a"
        }
        */
        BsonDocument document = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        BsonDocument foo = new BsonDocument();

        foo.put(ANY_OF_INT_STRING_COL_LABEL, new BsonInt32(1));
        foo.put(INT_NULLABLE_COL_LABEL, new BsonNull());
        foo.put(INT_COL_LABEL, new BsonInt32(2));
        foo.put(ANY_COL_LABEL, new BsonUndefined());
        foo.put(NULL_COL_LABEL, new BsonNull());
        foo.put(FOO_DUP_COL_LABEL, new BsonInt32(8));

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(1));
        array.add(new BsonInt32(2));
        array.add(new BsonInt32(3));
        foo.put(ARRAY_COL_LABEL, array);

        BsonDocument fooSubDoc = new BsonDocument();
        fooSubDoc.put(INT_COL_LABEL, new BsonInt32(5));
        foo.put(DOC_COL_LABEL, fooSubDoc);

        bot.put(DOUBLE_COL_LABEL, new BsonDouble(1.2));
        byte binary[] = {10, 20, 30};
        bot.put(BINARY_COL_LABEL, new BsonBinary(binary));
        bot.put(STRING_COL_LABEL, new BsonString("a"));
        bot.put(BOT_DUP_COL_LABEL, new BsonString("dupCol"));
        document.put("", bot);
        document.put("foo", foo);

        return document;
    }

    MongoJsonSchemaResult generateSchema() {
        MongoJsonSchemaResult schemaResult = new MongoJsonSchemaResult();
        schemaResult.ok = 1;
        schemaResult.schema = new MongoVersionedJsonSchema();
        schemaResult.schema.mongoJsonSchema = generateMongoJsonSchema();
        return schemaResult;
    }

    protected static MongoJsonSchema generateMongoJsonSchemaAllTypes() {
        String schema =
                "{"
                        + "    \"bsonType\": \"object\","
                        + "    \"properties\": {"
                        + "        \"all\": {"
                        + "            \"bsonType\": \"object\","
                        + "            \"properties\": {"
                        + "                \"double\": {"
                        + "                    \"bsonType\": \"double\""
                        + "                },"
                        + "                \"string\": {"
                        + "                    \"bsonType\": \"string\""
                        + "                },"
                        + "                \"object\": {"
                        + "                    \"bsonType\": \"object\","
                        + "                    \"properties\": {"
                        + "                        \"x\": {"
                        + "                            \"bsonType\": \"int\""
                        + "                        },"
                        + "                        \"y\": {"
                        + "                            \"bsonType\": \"objectId\""
                        + "                        }"
                        + "                    }"
                        + "                },"
                        + "                \"array\": {"
                        + "                    \"bsonType\": \"array\","
                        + "                    \"items\": {"
                        + "                        \"bsonType\": \"int\""
                        + "                    }"
                        + "                },"
                        + "                \"binData\": {"
                        + "                    \"bsonType\": \"binData\""
                        + "                },"
                        + "                \"legacyUuid\": {"
                        + "                    \"bsonType\": \"binData\""
                        + "                },"
                        + "                \"standardUuid\": {"
                        + "                    \"bsonType\": \"binData\""
                        + "                },"
                        + "                \"undefined\": {"
                        + "                    \"bsonType\": \"undefined\""
                        + "                },"
                        + "                \"objectId\": {"
                        + "                    \"bsonType\": \"objectId\""
                        + "                },"
                        + "                \"bool\": {"
                        + "                    \"bsonType\": \"bool\""
                        + "                },"
                        + "                \"date\": {"
                        + "                    \"bsonType\": \"date\""
                        + "                },"
                        + "                \"null\": {"
                        + "                    \"bsonType\": \"null\""
                        + "                },"
                        + "                \"regex\": {"
                        + "                    \"bsonType\": \"regex\""
                        + "                },"
                        + "                \"dbPointer\": {"
                        + "                    \"bsonType\": \"dbPointer\""
                        + "                },"
                        + "                \"javascript\": {"
                        + "                    \"bsonType\": \"javascript\""
                        + "                },"
                        + "                \"symbol\": {"
                        + "                    \"bsonType\": \"symbol\""
                        + "                },"
                        + "                \"javascriptWithScope\": {"
                        + "                    \"bsonType\": \"javascriptWithScope\""
                        + "                },"
                        + "                \"int\": {"
                        + "                    \"bsonType\": \"int\""
                        + "                },"
                        + "                \"timestamp\": {"
                        + "                    \"bsonType\": \"timestamp\""
                        + "                },"
                        + "                \"long\": {"
                        + "                    \"bsonType\": \"long\""
                        + "                },"
                        + "                \"decimal\": {"
                        + "                    \"bsonType\": \"decimal\""
                        + "                },"
                        + "                \"minKey\": {"
                        + "                    \"bsonType\": \"minKey\""
                        + "                },"
                        + "                \"maxKey\": {"
                        + "                    \"bsonType\": \"maxKey\""
                        + "                }"
                        + "            },"
                        + "            \"required\": ["
                        + "                \"double\", \"string\", \"object\", \"array\", \"binData\", \"legacyUuid\", \"standardUuid\", \"undefined\","
                        + "                \"objectId\", \"bool\", \"date\", \"null\", \"regex\", \"dbPointer\","
                        + "                \"javascript\", \"symbol\", \"javascriptWithScope\", \"int\","
                        + "                \"timestamp\", \"long\", \"decimal\", \"minKey\", \"maxKey\""
                        + "            ]"
                        + "        }"
                        + "    },"
                        + "    \"required\": [\"all\"]"
                        + "}";

        return MongoDriver.REGISTRY
                .get(MongoJsonSchema.class)
                .decode(new JsonReader(schema), DecoderContext.builder().build());
    }

    static BsonDocument generateRowAllTypes() {
        String doc =
                "{\"all\": {"
                        + "\""
                        + ALL_DOUBLE_COL_LABEL
                        + "\": "
                        + ALL_DOUBLE_COL_VAL
                        + ","
                        + "\""
                        + ALL_STRING_COL_LABEL
                        + "\": "
                        + ALL_STRING_COL_VAL
                        + ","
                        + "\""
                        + ALL_OBJECT_COL_LABEL
                        + "\": "
                        + ALL_OBJECT_COL_VAL
                        + ","
                        + "\""
                        + ALL_ARRAY_COL_LABEL
                        + "\": "
                        + ALL_ARRAY_COL_VAL
                        + ","
                        + "\""
                        + ALL_BINARY_COL_LABEL
                        + "\": "
                        + ALL_BINARY_COL_VAL
                        + ","
                        + "\""
                        + ALL_LEGACY_UUID_COL_LABEL
                        + "\": "
                        + ALL_LEGACY_UUID_COL_VAL
                        + ","
                        + "\""
                        + ALL_STANDARD_UUID_COL_LABEL
                        + "\": "
                        + ALL_STANDARD_UUID_COL_VAL
                        + ","
                        + "\""
                        + ALL_UNDEFINED_COL_LABEL
                        + "\": "
                        + ALL_UNDEFINED_COL_VAL
                        + ","
                        + "\""
                        + ALL_OBJECT_ID_COL_LABEL
                        + "\": "
                        + ALL_OBJECT_ID_COL_VAL
                        + ","
                        + "\""
                        + ALL_BOOL_COL_LABEL
                        + "\": "
                        + ALL_BOOL_COL_VAL
                        + ","
                        + "\""
                        + ALL_DATE_COL_LABEL
                        + "\": "
                        + ALL_DATE_COL_VAL
                        + ","
                        + "\""
                        + ALL_NULL_COL_LABEL
                        + "\": "
                        + ALL_NULL_COL_VAL
                        + ","
                        + "\""
                        + ALL_REGEX_COL_LABEL
                        + "\": "
                        + ALL_REGEX_COL_VAL
                        + ","
                        + "\""
                        + ALL_DB_POINTER_COL_LABEL
                        + "\": "
                        + ALL_DB_POINTER_COL_VAL
                        + ","
                        + "\""
                        + ALL_JAVASCRIPT_COL_LABEL
                        + "\": "
                        + ALL_JAVASCRIPT_COL_VAL
                        + ","
                        + "\""
                        + ALL_SYMBOL_COL_LABEL
                        + "\": "
                        + ALL_SYMBOL_COL_VAL
                        + ","
                        + "\""
                        + ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL
                        + "\": "
                        + ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL
                        + ","
                        + "\""
                        + ALL_INT_COL_LABEL
                        + "\": "
                        + ALL_INT_COL_VAL
                        + ","
                        + "\""
                        + ALL_TIMESTAMP_COL_LABEL
                        + "\": "
                        + ALL_TIMESTAMP_COL_VAL
                        + ","
                        + "\""
                        + ALL_LONG_COL_LABEL
                        + "\": "
                        + ALL_LONG_COL_VAL
                        + ","
                        + "\""
                        + ALL_DECIMAL_COL_LABEL
                        + "\": "
                        + ALL_DECIMAL_COL_VAL
                        + ","
                        + "\""
                        + ALL_MIN_KEY_COL_LABEL
                        + "\": "
                        + ALL_MIN_KEY_COL_VAL
                        + ","
                        + "\""
                        + ALL_MAX_KEY_COL_LABEL
                        + "\": "
                        + ALL_MAX_KEY_COL_VAL
                        + "}}";

        return new BsonDocumentCodec()
                .decode(new JsonReader(doc), DecoderContext.builder().build());
    }
}
