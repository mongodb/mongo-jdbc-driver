package com.mongodb.jdbc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.mongodb.ConnectionString;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;

public abstract class MongoMock {
    static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");;
    protected static String database = "test";
    @Mock protected static MongoClient mongoClient;
    @Mock protected static MongoDatabase mongoDatabase;
    @Mock protected static AggregateIterable<MongoResultDoc> aggregateIterable;
    @Mock protected static MongoCursor<MongoResultDoc> mongoCursor;

    @InjectMocks
    protected static MongoConnection mongoConnection = new MongoConnection(uri, database, null);

    // reset the mock objects before every test case
    protected static void resetMockObjs() throws NoSuchFieldException {
        FieldSetter.setField(
                mongoConnection,
                mongoConnection.getClass().getDeclaredField("mongoClient"),
                mongoClient);
        FieldSetter.setField(
                mongoConnection, mongoConnection.getClass().getDeclaredField("isClosed"), false);
        FieldSetter.setField(
                mongoConnection,
                mongoConnection.getClass().getDeclaredField("currentDB"),
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

    Column generateCol(String database, String table, String column, BsonValue value) {
        Column col = new Column();
        col.database = database;
        col.table = table;
        col.tableAlias = table;
        col.column = column;
        col.columnAlias = column;
        col.value = value;
        return col;
    }

    MongoResultDoc generateRow(boolean isEmpty) {
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
        Column col1 = generateCol("myDB", "foo", "a", new BsonInt32(1));
        Column col2 = generateCol("myDB", "foo", "b", new BsonString("test"));

        ArrayList<Column> cols = new ArrayList<>();
        cols.add(col1);
        cols.add(col2);
        return new MongoResultDoc(cols, isEmpty);
    }
}
