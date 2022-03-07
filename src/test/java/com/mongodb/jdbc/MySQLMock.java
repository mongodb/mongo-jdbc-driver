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
import java.util.logging.Level;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

public abstract class MySQLMock {
    static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");;
    protected static String database = "test";
    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<MySQLResultDoc> aggregateIterable;
    @Mock protected static MongoCursor<MySQLResultDoc> mongoCursor;

    @InjectMocks
    protected static MongoConnection mongoConnection = new MySQLConnection(uri, database, null, null, null);

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
        when(mongoDatabase.aggregate(any(), eq(MySQLResultDoc.class)))
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

    MySQLColumnInfo generateCol(String database, String table, String column, String bsonType)
            throws SQLException {
        return new MySQLColumnInfo(database, table, table, column, column, bsonType);
    }

    MySQLResultDoc generateMetadataDoc() {
        /*
        {
           values: [
        {
             database: "myDB",
             table: "foo",
             tableAlias: "foo",
             column: "a",
             columnAlias: "a",
             value: 1
           },
        {
             database: "myDB",
             table: "foo",
             tableAlias: "foo",
             column: "b",
             columnAlias: "b",
             value: "test"
           }
           ]
         }
         */

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        try {
            metaDoc.columns.add(generateCol("myDB", "foo", "a", "int"));
            metaDoc.columns.add(generateCol("myDB", "foo", "b", "string"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return metaDoc;
    }

    MySQLResultDoc generateRow() {
        /*
        {
           values: [
        {
             database: "myDB",
             table: "foo",
             tableAlias: "foo",
             column: "a",
             columnAlias: "a",
             value: 1
           },
        {
             database: "myDB",
             table: "foo",
             tableAlias: "foo",
             column: "b",
             columnAlias: "b",
             value: "test"
           }
           ]
         }
         */
        MySQLResultDoc doc = new MySQLResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonInt32(1));
        doc.values.add(new BsonString("test"));

        return doc;
    }
}
