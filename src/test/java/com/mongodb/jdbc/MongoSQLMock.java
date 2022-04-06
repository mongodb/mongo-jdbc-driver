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
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

public abstract class MongoSQLMock {
    static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");
    protected static String database = "test";
    // __bot.a
    protected static int DOUBLE_COL = 1;
    // __bot.binary
    protected static int BINARY_COL = 2;
    // __bot.str
    protected static int STRING_COL = 3;
    // foo.a
    protected static int ANY_OF_INT_STRING_COL = 4;
    // foo.b
    protected static int INT_OR_NULL_COL = 5;
    // foo.c
    protected static int INT_COL = 6;
    // foo.d
    protected static int ANY_COL = 7;
    // foo.doc
    protected static int DOC_COL = 8;
    // foo.null
    protected static int NULL_COL = 9;
    // foo.vec
    protected static int ARRAY_COL = 10;

    // __bot fields
    protected static String DOUBLE_COL_LABEL = "a";
    protected static String BINARY_COL_LABEL = "binary";
    protected static String STRING_COL_LABEL = "str";

    // foo fields
    protected static String ANY_OF_INT_STRING_COL_LABEL = "a";
    protected static String INT_NULLABLE_COL_LABEL = "b";
    protected static String INT_COL_LABEL = "c";
    protected static String ANY_COL_LABEL = "d";
    protected static String DOC_COL_LABEL = "doc";
    protected static String NULL_COL_LABEL = "null";
    protected static String ARRAY_COL_LABEL = "vec";

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

    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<BsonDocument> aggregateIterable;
    @Mock protected static MongoCursor<BsonDocument> mongoCursor;

    @InjectMocks
    protected static MongoConnection mongoConnection =
            new MongoSQLConnection(uri, database, null, null);

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
                        a: {
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
                        }
                    },
                    required: [a, b, vec, doc],
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
                ""
                        + "{"
                        + "    \"bsonType\": \"object\""
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
                        + "                \"double\", \"string\", \"object\", \"array\", \"binData\", \"undefined\","
                        + "                \"objectId\", \"bool\", \"date\", \"null\", \"regex\", \"dbPointer\","
                        + "                \"javascript\", \"symbol\", \"javascriptWithScope\", \"int\","
                        + "                \"timestamp\", \"long\", \"decimal\", \"minKey\", \"maxKey\""
                        + "            ]"
                        + "        }"
                        + "    },"
                        + "    \"required\": [\"all\"]"
                        + "}";

        return MongoDriver.registry
                .get(MongoJsonSchema.class)
                .decode(new JsonReader(schema), DecoderContext.builder().build());
    }

    static BsonDocument generateRowAllTypes() {
        String doc =
                ""
                        + "{"
                        + "    \"all\": {"
                        + "        \"double\": {"
                        + "            \"$numberDouble\": \"1.0\""
                        + "        },"
                        + "        \"string\": \"str\","
                        + "        \"object\": {"
                        + "            \"x\": 10,"
                        + "            \"y\": {"
                        + "                \"$oid\": \"57e193d7a9cc81b4027498b5\""
                        + "            }"
                        + "        },"
                        + "        \"array\": [7, 8, 9],"
                        + "        \"binData\": {"
                        + "            \"$binary\": {"
                        + "                \"base64\": \"\","
                        + "                \"subType\": \"00\""
                        + "            }"
                        + "        },"
                        + "        \"undefined\": {"
                        + "            \"$undefined\": true"
                        + "        },"
                        + "        \"objectId\": {"
                        + "            \"$oid\": \"57e193d7a9cc81b4027498b5\""
                        + "        },"
                        + "        \"bool\": true,"
                        + "        \"date\": {"
                        + "            \"$date\": {"
                        + "                \"$numberLong\": \"1608916394000\""
                        + "            }"
                        + "        },"
                        + "        \"null\": null,"
                        + "        \"regex\": {"
                        + "            \"$regularExpression\": {"
                        + "                \"pattern\": \"abc\","
                        + "                \"options\": \"i\""
                        + "            }"
                        + "        },"
                        + "        \"dbPointer\": {"
                        + "            \"$dbPointer\": {"
                        + "                \"$ref\": \"db2\","
                        + "                \"$id\": {"
                        + "                    \"$oid\": \"57e193d7a9cc81b4027498b5\""
                        + "                }"
                        + "            }"
                        + "        },"
                        + "        \"javascript\": {"
                        + "            \"$code\": \"javascript\""
                        + "        },"
                        + "        \"symbol\": {"
                        + "            \"$symbol\": \"sym\""
                        + "        },"
                        + "        \"javascriptWithScope\": {"
                        + "            \"$code\": \"code\","
                        + "            \"$scope\": {"
                        + "                \"x\": 1"
                        + "            }"
                        + "        },"
                        + "        \"int\": 3,"
                        + "        \"timestamp\": {"
                        + "            \"$timestamp\": {"
                        + "                \"t\": 1412180887,"
                        + "                \"i\": 1"
                        + "            }"
                        + "        },"
                        + "        \"long\": {"
                        + "            \"$numberLong\": \"5\""
                        + "        },"
                        + "        \"decimal\": {"
                        + "            \"$numberDecimal\": \"21.2\""
                        + "        },"
                        + "        \"minKey\": {"
                        + "            \"$minKey\": 1"
                        + "        },"
                        + "        \"maxKey\": {"
                        + "            \"$maxKey\": 1"
                        + "        }"
                        + "    }"
                        + "}";

        return new BsonDocumentCodec()
                .decode(new JsonReader(doc), DecoderContext.builder().build());
    }
}
