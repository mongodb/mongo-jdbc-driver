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
import java.util.HashMap;
import java.util.HashSet;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonUndefined;
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
    // foo.null
    protected static int NULL_COL = 8;
    // foo.vec
    protected static int ARRAY_COL = 9;

    protected static String DOUBLE_COL_LABEL = "a";
    protected static String BINARY_COL_LABEL = "binary";
    protected static String STRING_COL_LABEL = "str";

    protected static String ANY_OF_INT_STRING_COL_LABEL = "a";
    protected static String INT_NULLABLE_COL_LABEL = "b";
    protected static String INT_COL_LABEL = "c";
    protected static String ANY_COL_LABEL = "d";
    protected static String NULL_COL_LABEL = "null";
    protected static String ARRAY_COL_LABEL = "vec";

    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<BsonDocument> aggregateIterable;
    @Mock protected static AggregateIterable<MongoJsonSchemaResult> jsonSchemaResultIterable;
    @Mock protected static MongoCursor<BsonDocument> mongoCursor;
    @Mock protected static MongoCursor<MongoJsonSchemaResult> mongoSchemaCursor;

    @InjectMocks
    protected static MongoConnection mongoConnection = new MongoSQLConnection(uri, database);

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
        //doReturn(jsonSchemaResultIterable)
        when(mongoDatabase.aggregate(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(jsonSchemaResultIterable);
        when(mongoDatabase.aggregate(any(), eq(BsonDocument.class))).thenReturn(aggregateIterable);
        // Mock aggregateIterable
        when(aggregateIterable.batchSize(anyInt())).thenReturn(aggregateIterable);
        when(aggregateIterable.maxTime(anyLong(), any())).thenReturn(aggregateIterable);
        when(aggregateIterable.cursor()).thenReturn(mongoCursor);
        when(jsonSchemaResultIterable.batchSize(anyInt())).thenReturn(jsonSchemaResultIterable);
        when(jsonSchemaResultIterable.maxTime(anyLong(), any()))
                .thenReturn(jsonSchemaResultIterable);
        when(jsonSchemaResultIterable.cursor()).thenReturn(mongoSchemaCursor);

        // Mock MongoCursor
        when(mongoCursor.hasNext()).thenReturn(false);
        when(mongoSchemaCursor.hasNext()).thenReturn(false);
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
                        }
                    },
                    required: [a, b, vec],
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
        fooSchema.required.add("a");
        fooSchema.required.add("b");
        fooSchema.required.add("vec");

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

        fooSchema.properties = new HashMap<String, MongoJsonSchema>();
        fooSchema.properties.put("c", cSchema);
        fooSchema.properties.put("a", aSchema);
        // new MongoJsonSchema() is the ANY schema
        fooSchema.properties.put("d", new MongoJsonSchema());
        fooSchema.properties.put("b", bSchema);
        fooSchema.properties.put("vec", vecSchema);
        fooSchema.properties.put("null", nullSchema);

        MongoJsonSchema botSchema = new MongoJsonSchema();
        botSchema.bsonType = "object";
        botSchema.properties = new HashMap<String, MongoJsonSchema>();
        aSchema = new MongoJsonSchema();
        aSchema.bsonType = "double";
        botSchema.properties.put("a", aSchema);
        MongoJsonSchema binarySchema = new MongoJsonSchema();
        binarySchema.bsonType = "binData";
        botSchema.properties.put("binary", binarySchema);
        MongoJsonSchema strSchema = new MongoJsonSchema();
        strSchema.bsonType = "string";
        botSchema.properties.put("str", strSchema);

        schema.properties = new HashMap<String, MongoJsonSchema>();
        schema.properties.put("foo", fooSchema);
        schema.properties.put("", botSchema);
        return schema;
    }

    BsonDocument generateRow() {
        /*
        {
            "foo.a":1,
            "foo.b":null,
            "foo.c":2,
            "foo.d":{
                "$undefined":true
            },
             "foo.null": null
             "foo.vec":[
                1,
                2,
                3
            ],
            "__bot.a":1.2,
            "__bot.str":"a"
        }
        */
        BsonDocument document = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        BsonDocument foo = new BsonDocument();

        foo.put("a", new BsonInt32(1));
        foo.put("b", new BsonNull());
        foo.put("c", new BsonInt32(2));
        foo.put("d", new BsonUndefined());
        foo.put("null", new BsonNull());

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(1));
        array.add(new BsonInt32(2));
        array.add(new BsonInt32(3));
        foo.put("vec", array);

        bot.put("a", new BsonDouble(1.2));
        byte binary[] = {10, 20, 30};
        bot.put("binary", new BsonBinary(binary));
        bot.put("str", new BsonString("a"));

        document.put("", bot);
        document.put("foo", foo);

        return document;
    }

    MongoJsonSchemaResult generateSchema() {
        MongoJsonSchemaResult schemaResult = new MongoJsonSchemaResult();
        schemaResult.ok = 1;
        schemaResult.schema = new MongoVersionedJsonSchema();
        schemaResult.schema.jsonSchema = generateMongoJsonSchema();
        return schemaResult;
    }
}
