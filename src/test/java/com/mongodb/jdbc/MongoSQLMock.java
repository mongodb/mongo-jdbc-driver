package com.mongodb.jdbc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static com.mongodb.jdbc.BsonTypeInfo.*;

import com.google.common.collect.ImmutableSet;
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
import org.bson.types.Decimal128;
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
    // all.doc
    protected static String ALL_DOC_COL_LABEL = "doc";
    // all.array
    protected static String ALL_ARRAY_COL_LABEL = "array";
    // all.binary
    protected static String ALL_BINARY_COL_LABEL = "binary";
    // all.undefined
    protected static String ALL_UNDEFINED_COL_LABEL = "undefined";
    // all.object_id
    protected static String ALL_OBJECT_ID_COL_LABEL = "object_id";
    // all.bool
    protected static String ALL_BOOL_COL_LABEL = "bool";
    // all.date
    protected static String ALL_DATE_COL_LABEL = "date";
    // all.null
    protected static String ALL_NULL_COL_LABEL = "null";
    // all.regex
    protected static String ALL_REGEX_COL_LABEL = "regex";
    // all.db_pointer
    protected static String ALL_DB_POINTER_COL_LABEL = "db_pointer";
    // all.javascript
    protected static String ALL_JAVASCRIPT_COL_LABEL = "javascript";
    // all.symbol
    protected static String ALL_SYMBOL_COL_LABEL = "symbol";
    // all.javascript_with_scope
    protected static String ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL = "javascript_with_scope";
    // all.int
    protected static String ALL_INT_COL_LABEL = "int";
    // all.timestamp
    protected static String ALL_TIMESTAMP_COL_LABEL = "timestamp";
    // all.long
    protected static String ALL_LONG_COL_LABEL = "long";
    // all.decimal
    protected static String ALL_DECIMAL_COL_LABEL = "decimal";
    // all.min_key
    protected static String ALL_MIN_KEY_COL_LABEL = "min_key";
    // all.max_key
    protected static String ALL_MAX_KEY_COL_LABEL = "max_key";

    protected static ObjectId ALL_OBJECT_ID_VAL = new ObjectId();

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
        /*
        {
            bsonType: object,
            properties: {
                all: {
                    bsonType: object,
                    properties: {
                        double: {
                            bsonType: double
                        },
                        string: {
                            bsonType: string
                        },
                        doc: {
                           bsonType: object,
                           properties: {
                              X: {
                                 bsonType: int
                              },
                              y: {
                                 bsonType: objectId
                              }
                           },
                           required: [x, y]
                        }
                        array: {
                           bsonType: array,
                           items: {
                               bsonType: int,
                           }
                        },
                        binary: {
                            bsonType: binData
                        },
                        undefined: {
                            bsonType: undefined
                        },
                        object_id: {
                            bsonType: objectId
                        },
                        bool: {
                            bsonType: bool
                        },
                        date: {
                            bsonType: date
                        },
                        null: {
                            bsonType: null
                        },
                        regex: {
                            bsonType: regex
                        },
                        db_pointer: {
                            bsonType: dbPointer
                        },
                        javascript: {
                            bsonType: javascript
                        },
                        symbol: {
                            bsonType: symbol
                        },
                        javascript_with_scope: {
                            bsonType: javascriptWithScope
                        },
                        int: {
                            bsonType: int
                        },
                        timestamp: {
                            bsonType: timestamp
                        },
                        long: {
                            bsonType: long
                        },
                        decimal: {
                            bsonType: decimal
                        },
                        min_key: {
                            bsonType: minKey
                        },
                        max_key: {
                            bsonType: maxKey
                        }
                    },
                    required: [
                        double, string, doc, array, binary, undefined,
                        object_id, bool, date, null, regex, db_pointer,
                        javascript, symbol, javascript_with_scope, int,
                        timestamp, long, decimal, min_key, max_key
                    ]
                }
            },
            required: [all]
        }
        */
        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.required.add("all");

        MongoJsonSchema allSchema = MongoJsonSchema.createEmptyObjectSchema();
        allSchema.required.addAll(ImmutableSet.of(
                ALL_DOUBLE_COL_LABEL,
                ALL_STRING_COL_LABEL,
                ALL_DOC_COL_LABEL,
                ALL_ARRAY_COL_LABEL,
                ALL_BINARY_COL_LABEL,
                ALL_UNDEFINED_COL_LABEL,
                ALL_OBJECT_ID_COL_LABEL,
                ALL_BOOL_COL_LABEL,
                ALL_DATE_COL_LABEL,
                ALL_NULL_COL_LABEL,
                ALL_REGEX_COL_LABEL,
                ALL_DB_POINTER_COL_LABEL,
                ALL_JAVASCRIPT_COL_LABEL,
                ALL_SYMBOL_COL_LABEL,
                ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL,
                ALL_INT_COL_LABEL,
                ALL_TIMESTAMP_COL_LABEL,
                ALL_LONG_COL_LABEL,
                ALL_DECIMAL_COL_LABEL,
                ALL_MIN_KEY_COL_LABEL,
                ALL_MAX_KEY_COL_LABEL
        ));

        MongoJsonSchema docSchema = MongoJsonSchema.createEmptyObjectSchema();
        docSchema.required.add("x");
        docSchema.required.add("y");
        docSchema.properties.put("x", MongoJsonSchema.createScalarSchema(BSON_INT.getBsonName()));
        docSchema.properties.put("y", MongoJsonSchema.createScalarSchema(BSON_OBJECTID.getBsonName()));

        MongoJsonSchema arraySchema = new MongoJsonSchema();
        arraySchema.bsonType = BSON_ARRAY.getBsonName();
        arraySchema.items = MongoJsonSchema.createScalarSchema(BSON_INT.getBsonName());

        allSchema.properties.put(ALL_DOUBLE_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_DOUBLE.getBsonName()));
        allSchema.properties.put(ALL_STRING_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_STRING.getBsonName()));
        allSchema.properties.put(ALL_DOC_COL_LABEL, docSchema);
        allSchema.properties.put(ALL_ARRAY_COL_LABEL, arraySchema);
        allSchema.properties.put(ALL_BINARY_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_BINDATA.getBsonName()));
        allSchema.properties.put(ALL_UNDEFINED_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_UNDEFINED.getBsonName()));
        allSchema.properties.put(ALL_OBJECT_ID_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_OBJECTID.getBsonName()));
        allSchema.properties.put(ALL_BOOL_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_BOOL.getBsonName()));
        allSchema.properties.put(ALL_DATE_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_DATE.getBsonName()));
        allSchema.properties.put(ALL_NULL_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_NULL.getBsonName()));
        allSchema.properties.put(ALL_REGEX_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_REGEX.getBsonName()));
        allSchema.properties.put(ALL_DB_POINTER_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_DBPOINTER.getBsonName()));
        allSchema.properties.put(ALL_JAVASCRIPT_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_JAVASCRIPT.getBsonName()));
        allSchema.properties.put(ALL_SYMBOL_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_SYMBOL.getBsonName()));
        allSchema.properties.put(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_JAVASCRIPTWITHSCOPE.getBsonName()));
        allSchema.properties.put(ALL_INT_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_INT.getBsonName()));
        allSchema.properties.put(ALL_TIMESTAMP_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_TIMESTAMP.getBsonName()));
        allSchema.properties.put(ALL_LONG_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_LONG.getBsonName()));
        allSchema.properties.put(ALL_DECIMAL_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_DECIMAL.getBsonName()));
        allSchema.properties.put(ALL_MIN_KEY_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_MINKEY.getBsonName()));
        allSchema.properties.put(ALL_MAX_KEY_COL_LABEL, MongoJsonSchema.createScalarSchema(BSON_MAXKEY.getBsonName()));

        schema.properties.put("all", allSchema);

        return schema;
    }

    static BsonDocument generateRowAllTypes() {
        BsonDocument document = new BsonDocument();
        BsonDocument all = new BsonDocument();

        BsonDocument allSubDoc = new BsonDocument();
        allSubDoc.put("x", new BsonInt32(10));
        allSubDoc.put("y", new BsonObjectId(ALL_OBJECT_ID_VAL));

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(7));
        array.add(new BsonInt32(8));
        array.add(new BsonInt32(9));

        all.put(ALL_DOUBLE_COL_LABEL, new BsonDouble(1.0));
        all.put(ALL_STRING_COL_LABEL, new BsonString("str"));
        all.put(ALL_DOC_COL_LABEL, allSubDoc);
        all.put(ALL_ARRAY_COL_LABEL, array);
        all.put(ALL_BINARY_COL_LABEL, new BsonBinary(new byte[0]));
        all.put(ALL_UNDEFINED_COL_LABEL, new BsonUndefined());
        all.put(ALL_OBJECT_ID_COL_LABEL, new BsonObjectId(ALL_OBJECT_ID_VAL));
        all.put(ALL_BOOL_COL_LABEL, new BsonBoolean(true));
        all.put(ALL_DATE_COL_LABEL, new BsonDateTime(1608916394000L));
        all.put(ALL_NULL_COL_LABEL, new BsonNull());
        all.put(ALL_REGEX_COL_LABEL, new BsonRegularExpression("abc", "i"));
        all.put(ALL_DB_POINTER_COL_LABEL, new BsonDbPointer("db2", ALL_OBJECT_ID_VAL));
        all.put(ALL_JAVASCRIPT_COL_LABEL, new BsonJavaScript("javascript"));
        all.put(ALL_SYMBOL_COL_LABEL, new BsonSymbol("sym"));
        all.put(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL, new BsonJavaScriptWithScope("code", new BsonDocument("x", new BsonInt32(1))));
        all.put(ALL_INT_COL_LABEL, new BsonInt32(3));
        all.put(ALL_TIMESTAMP_COL_LABEL, new BsonTimestamp(1412180887, 1));
        all.put(ALL_LONG_COL_LABEL, new BsonInt64(5));
        all.put(ALL_DECIMAL_COL_LABEL, new BsonDecimal128(Decimal128.parse("21.2")));
        all.put(ALL_MIN_KEY_COL_LABEL, new BsonMinKey());
        all.put(ALL_MAX_KEY_COL_LABEL, new BsonMaxKey());

        document.put("all", all);

        return document;
    }
}
