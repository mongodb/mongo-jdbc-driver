package com.mongodb.jdbc.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import com.mongodb.jdbc.Column;
import com.mongodb.jdbc.MongoResultSet;
import com.mongodb.jdbc.Row;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetTest {
    @Mock MongoCursor<Row> cursor;
    @Mock Row nextRow;
    MongoResultSet mockResultSet;
    private static MongoResultSet mongoResultSet;

    static int NULL_COL = 1;
    static int DOUBLE_COL = 2;
    static int STRING_COL = 3;
    static int BINARY_COL = 4;
    static int UUID_COL = 5;
    static int OBJECTID_COL = 6;
    static int BOOLEAN_COL = 7;
    static int DATE_COL = 8;
    static int INTEGER_COL = 9;
    static int LONG_COL = 10;
    static int DECIMAL_COL = 11;

    static Column newColumn(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            BsonValue value) {
        Column c = new Column();
        c.database = database;
        c.table = table;
        c.tableAlias = tableAlias;
        c.column = column;
        c.columnAlias = columnAlias;
        c.value = value;
        return c;
    }

    private static class MongoTestCursor implements MongoCursor<Row> {
        private List<Row> rows;
        private int rowNum = 0;

        public MongoTestCursor(List<Row> rows) {
            this.rows = rows;
        }

        @Override
        public void close() {}

        @Override
        public ServerAddress getServerAddress() {
            return new ServerAddress("127.0.0.1");
        }

        @Override
        public ServerCursor getServerCursor() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return rowNum + 1 < rows.size();
        }

        @Override
        public Row next() {
            return rows.get(rowNum++);
        }

        @Override
        public Row tryNext() {
            if (hasNext()) {
                return next();
            }
            return null;
        }
    }

    static {
        Row row = new Row();
        row.values = new ArrayList<>();

        row.values.add(newColumn("", "", "", "nullCol", "nullCol", new BsonNull()));
        row.values.add(newColumn("", "", "", "doubleCol", "doubleCol", new BsonDouble(1.1)));
        row.values.add(
                newColumn("", "", "", "stringCol", "stringCol", new BsonString("string data")));
        row.values.add(
                newColumn("", "", "", "binaryCol", "binaryCol", new BsonBinary("data".getBytes())));
        row.values.add(
                newColumn("", "", "", "uuidCol", "uuidCol", new BsonBinary(UUID.randomUUID())));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "objectIdCol",
                        "objectIdCol",
                        new BsonObjectId(new ObjectId(new Date()))));
        row.values.add(newColumn("", "", "", "booleanCol", "booleanCol", new BsonBoolean(true)));
        row.values.add(
                newColumn("", "", "", "dateCol", "dateCol", new BsonDateTime(1580511155627L)));
        row.values.add(newColumn("", "", "", "integerCol", "integerCol", new BsonInt32(100)));
        row.values.add(newColumn("", "", "", "longCol", "longCol", new BsonInt64(100L)));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "decimalCol",
                        "decimalCol",
                        new BsonDecimal128(new Decimal128(100L))));

        List<Row> rows = new ArrayList<Row>();
        rows.add(row);
        mongoResultSet = new MongoResultSet(new MongoTestCursor(rows));
    }

    static final String CURR_DOC = "currDoc";
    static final String NEXT_DOC = "nextDoc";

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // unit test sample
    @Test
    void returnNextRowWhenAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenReturn(nextRow);

        mockResultSet = new MongoResultSet(cursor);

        boolean hasNext = mockResultSet.next();
        assertTrue(hasNext);
        assertNotNull(mockResultSet.getCurrent());
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(false);

        mockResultSet = new MongoResultSet(cursor);

        boolean hasNext = mockResultSet.next();
        assertFalse(hasNext);
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
    }
}
