package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.sql.SQLException;
import java.util.ArrayList;
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
    static MongoResultSet mongoResultSet;

    static int NULL_COL_IDX = 1;
    static int DOUBLE_COL_IDX = 2;
    static int STRING_COL_IDX = 3;
    static int BINARY_COL_IDX = 4;
    static int UUID_COL_IDX = 5;
    static int OBJECTID_COL_IDX = 6;
    static int BOOLEAN_COL_IDX = 7;
    static int DATE_COL_IDX = 8;
    static int INTEGER_COL_IDX = 9;
    static int LONG_COL_IDX = 10;
    static int DECIMAL_COL_IDX = 11;

    static String NULL_COL_LABEL = "nullCol";
    static String DOUBLE_COL_LABEL = "doubleCol";
    static String STRING_COL_LABEL = "stringCol";
    static String BINARY_COL_LABEL = "binaryCol";
    static String UUID_COL_LABEL = "uuidCol";
    static String OBJECTID_COL_LABEL = "objectIdCol";
    static String BOOLEAN_COL_LABEL = "booleanCol";
    static String DATE_COL_LABEL = "dateCol";
    static String INTEGER_COL_LABEL = "integerCol";
    static String LONG_COL_LABEL = "longCol";
    static String DECIMAL_COL_LABEL = "decimalCol";

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
            return rowNum < rows.size();
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

        row.values.add(newColumn("", "", "", NULL_COL_LABEL, NULL_COL_LABEL, new BsonNull()));
        row.values.add(
                newColumn("", "", "", DOUBLE_COL_LABEL, DOUBLE_COL_LABEL, new BsonDouble(1.1)));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        STRING_COL_LABEL,
                        STRING_COL_LABEL,
                        new BsonString("string data")));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        BINARY_COL_LABEL,
                        BINARY_COL_LABEL,
                        new BsonBinary("data".getBytes())));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        UUID_COL_LABEL,
                        UUID_COL_LABEL,
                        new BsonBinary(new UUID(0, 0))));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        OBJECTID_COL_LABEL,
                        OBJECTID_COL_LABEL,
                        new BsonObjectId(new ObjectId("5e334e6e780812e4896dd65e"))));
        row.values.add(
                newColumn("", "", "", BOOLEAN_COL_LABEL, BOOLEAN_COL_LABEL, new BsonBoolean(true)));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        DATE_COL_LABEL,
                        DATE_COL_LABEL,
                        new BsonDateTime(-44364244526000L)));
        row.values.add(
                newColumn("", "", "", INTEGER_COL_LABEL, INTEGER_COL_LABEL, new BsonInt32(100)));
        row.values.add(newColumn("", "", "", LONG_COL_LABEL, LONG_COL_LABEL, new BsonInt64(100L)));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        DECIMAL_COL_LABEL,
                        DECIMAL_COL_LABEL,
                        new BsonDecimal128(new Decimal128(100L))));

        List<Row> rows = new ArrayList<Row>();
        rows.add(row);
        mongoResultSet = new MongoResultSet(new MongoTestCursor(rows));
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetters() throws Exception {
        boolean hasNext = mongoResultSet.next();
        assertTrue(hasNext);

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                mongoResultSet.getString(NULL_COL_IDX), mongoResultSet.getString(NULL_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(DOUBLE_COL_IDX),
                mongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(STRING_COL_IDX),
                mongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(OBJECTID_COL_IDX),
                mongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(BOOLEAN_COL_IDX),
                mongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(DATE_COL_IDX), mongoResultSet.getString(DATE_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(INTEGER_COL_IDX),
                mongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(LONG_COL_IDX), mongoResultSet.getString(LONG_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(DECIMAL_COL_IDX),
                mongoResultSet.getString(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        assertEquals(
                mongoResultSet.getBlob(BINARY_COL_IDX), mongoResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(mongoResultSet.getBlob(UUID_COL_IDX), mongoResultSet.getBlob(UUID_COL_LABEL));

        // Binary cannot be gotten through anything other than getBlob, currently.
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getString(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getString(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBoolean(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBoolean(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getLong(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getLong(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getDouble(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getDouble(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBigDecimal(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBigDecimal(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTimestamp(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTimestamp(UUID_COL_IDX);
                });

        // Only Binary and String and null values can be gotten from getBlob
        assertNotNull(mongoResultSet.getBlob(STRING_COL_LABEL));
        assertNotNull(mongoResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(mongoResultSet.getBlob(UUID_COL_LABEL));
        assertNull(mongoResultSet.getBlob(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(DECIMAL_COL_IDX);
                });

        //	NULL_COL 	 null
        //	DOUBLE_COL	 1.1
        //	STRING_COL	 "string data"
        //	OBJECTID_COL "5e334e6e780812e4896dd65e"
        //	BOOLEAN_COL	 true
        //	DATE_COL 	 some date
        //	INTEGER_COL	 100
        //	LONG_COL 	 100
        //	DECIMAL_COL	 100
        //
        //	Test String values are as expected
        assertEquals(null, mongoResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", mongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", mongoResultSet.getString(STRING_COL_LABEL));
        assertEquals("5e334e6e780812e4896dd65e", mongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", mongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.00Z", mongoResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", mongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", mongoResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", mongoResultSet.getString(DECIMAL_COL_LABEL));

        // Test Double values are as expected
        assertEquals(0.0, mongoResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, mongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    mongoResultSet.getDouble(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getDouble(OBJECTID_COL_LABEL);
                });
        assertEquals(1.0, mongoResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, mongoResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, mongoResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, mongoResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, mongoResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, mongoResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1, mongoResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    mongoResultSet.getLong(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getLong(OBJECTID_COL_LABEL);
                });
        assertEquals(1L, mongoResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, mongoResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, mongoResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, mongoResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, mongoResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, mongoResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, mongoResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    mongoResultSet.getInt(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getInt(OBJECTID_COL_LABEL);
                });
        assertEquals(1, mongoResultSet.getInt(BOOLEAN_COL_LABEL));
        assertEquals(-1527325616, mongoResultSet.getInt(DATE_COL_LABEL));
        assertEquals(100, mongoResultSet.getInt(INTEGER_COL_LABEL));
        assertEquals(100, mongoResultSet.getInt(LONG_COL_LABEL));
        assertEquals(100, mongoResultSet.getInt(DECIMAL_COL_LABEL));

        // Test Byte values are as expected
        assertEquals(0, mongoResultSet.getByte(NULL_COL_LABEL));
        assertEquals(1, mongoResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    mongoResultSet.getByte(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getByte(OBJECTID_COL_LABEL);
                });
        assertEquals(1, mongoResultSet.getByte(BOOLEAN_COL_LABEL));
        // This is weird, but I'm not going to go against Java's casting semantics.
        assertEquals(80, mongoResultSet.getByte(DATE_COL_LABEL));
        assertEquals(100, mongoResultSet.getByte(INTEGER_COL_LABEL));
        assertEquals(100, mongoResultSet.getByte(LONG_COL_LABEL));
        assertEquals(100, mongoResultSet.getByte(DECIMAL_COL_LABEL));

        // Test Boolean values are as expected
        assertEquals(false, mongoResultSet.getBoolean(NULL_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, mongoResultSet.getBoolean(STRING_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBoolean(OBJECTID_COL_LABEL);
                });
        assertEquals(true, mongoResultSet.getBoolean(BOOLEAN_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(DATE_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(DECIMAL_COL_LABEL));
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(false);

        mockResultSet = new MongoResultSet(cursor);

        boolean hasNext = mockResultSet.next();
        assertFalse(hasNext);
        assertNull(mockResultSet.getCurrent());
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
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
        // This still throws because "label" is unknown.
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
    }
}
