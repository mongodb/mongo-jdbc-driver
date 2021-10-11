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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

public abstract class MongoSQLMock {
    static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");;
    protected static String database = "test";
    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<MongoResultDoc> aggregateIterable;
    @Mock protected static MongoCursor<MongoResultDoc> mongoCursor;

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
        when(mongoDatabase.aggregate(any(), eq(MongoResultDoc.class)))
                .thenReturn(aggregateIterable);
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
                     },
                     required: [a, b],
                 },
                 "": {
                    bsonType: object,
                    properties: {
                        a: {
                            bsonType: double
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

        fooSchema.properties.put("c", cSchema);
        fooSchema.properties.put("a", aSchema);
        // new MongoJsonSchema() == ANY
        fooSchema.properties.put("d", new MongoJsonSchema());
        fooSchema.properties.put("b", bSchema);

        MongoJsonSchema botSchema = new MongoJsonSchema();
        aSchema = new MongoJsonSchema();
        aSchema.bsonType = "double";
        botSchema.properties.put("a", aSchema);
        MongoJsonSchema strSchema = new MongoJsonSchema();
        strSchema.bsonType = "string";
        botSchema.properties.put("str", strSchema);

        schema.properties.put("foo", fooSchema);
        schema.properties.put("", botSchema);
        return schema;
    }

    BsonDocument generateRow() {
        /**/
        return null;
    }
}
