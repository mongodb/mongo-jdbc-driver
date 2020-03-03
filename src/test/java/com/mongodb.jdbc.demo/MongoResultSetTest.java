package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
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
    static MongoResultSet relaxedMongoResultSet;
    static MongoResultSet strictMongoResultSet;

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
        strictMongoResultSet = new MongoResultSet(new MongoTestCursor(rows), false);
        relaxedMongoResultSet = new MongoResultSet(new MongoTestCursor(rows), true);
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testStrictGetters() throws Exception {
        boolean hasNext = strictMongoResultSet.next();
        assertTrue(hasNext);

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                strictMongoResultSet.getString(NULL_COL_IDX),
                strictMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(DOUBLE_COL_IDX),
                strictMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(STRING_COL_IDX),
                strictMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(OBJECTID_COL_IDX),
                strictMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(BOOLEAN_COL_IDX),
                strictMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(DATE_COL_IDX),
                strictMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(INTEGER_COL_IDX),
                strictMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(LONG_COL_IDX),
                strictMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getString(DECIMAL_COL_IDX),
                strictMongoResultSet.getString(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        assertEquals(
                strictMongoResultSet.getBlob(BINARY_COL_IDX),
                strictMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getBlob(UUID_COL_IDX),
                strictMongoResultSet.getBlob(UUID_COL_LABEL));

        // Binary cannot be gotten through anything other than getBlob, currently.
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getString(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getString(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBoolean(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBoolean(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getLong(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getLong(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getDouble(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getDouble(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(UUID_COL_IDX);
                });

        // Only Binary and String and null values can be gotten from getBlob
        assertNotNull(strictMongoResultSet.getBlob(STRING_COL_LABEL));
        assertNotNull(strictMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(strictMongoResultSet.getBlob(UUID_COL_LABEL));
        assertNull(strictMongoResultSet.getBlob(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(DOUBLE_COL_IDX);
                });
        assertNotNull(strictMongoResultSet.getBlob(OBJECTID_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(DECIMAL_COL_IDX);
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
        assertEquals(null, strictMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", strictMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", strictMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", strictMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", strictMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.00Z", strictMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(DECIMAL_COL_LABEL));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, strictMongoResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, strictMongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    strictMongoResultSet.getDouble(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getDouble(OBJECTID_COL_LABEL);
                });
        assertEquals(1.0, strictMongoResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000.0, strictMongoResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, strictMongoResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, strictMongoResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, strictMongoResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test BigDecimal values are as expected
        assertEquals(new BigDecimal(0.0), strictMongoResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), strictMongoResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(OBJECTID_COL_LABEL);
                });
        assertEquals(new BigDecimal(1.0), strictMongoResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
        assertEquals(
                new BigDecimal(-44364244526000L),
                strictMongoResultSet.getBigDecimal(DATE_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMongoResultSet.getBigDecimal(INTEGER_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMongoResultSet.getBigDecimal(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMongoResultSet.getBigDecimal(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, strictMongoResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1, strictMongoResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    strictMongoResultSet.getLong(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getLong(OBJECTID_COL_LABEL);
                });
        assertEquals(1L, strictMongoResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, strictMongoResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, strictMongoResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, strictMongoResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, strictMongoResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, strictMongoResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, strictMongoResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    strictMongoResultSet.getInt(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getInt(OBJECTID_COL_LABEL);
                });
        assertEquals(1, strictMongoResultSet.getInt(BOOLEAN_COL_LABEL));
        assertEquals(-1527325616, strictMongoResultSet.getInt(DATE_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getInt(INTEGER_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getInt(LONG_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getInt(DECIMAL_COL_LABEL));

        // We test Long, Int, and Byte, we can safely skip getShort tests

        // Test Byte values are as expected
        assertEquals(0, strictMongoResultSet.getByte(NULL_COL_LABEL));
        assertEquals(1, strictMongoResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    strictMongoResultSet.getByte(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getByte(OBJECTID_COL_LABEL);
                });
        assertEquals(1, strictMongoResultSet.getByte(BOOLEAN_COL_LABEL));
        // This is weird, but I'm not going to go against Java's casting semantics.
        assertEquals(80, strictMongoResultSet.getByte(DATE_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getByte(INTEGER_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getByte(LONG_COL_LABEL));
        assertEquals(100, strictMongoResultSet.getByte(DECIMAL_COL_LABEL));

        // Test Boolean values are as expected
        assertEquals(false, strictMongoResultSet.getBoolean(NULL_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, strictMongoResultSet.getBoolean(STRING_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBoolean(OBJECTID_COL_LABEL);
                });
        assertEquals(true, strictMongoResultSet.getBoolean(BOOLEAN_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(DATE_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertEquals(null, strictMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(
                new Timestamp(1580420718000L),
                strictMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(BOOLEAN_COL_LABEL);
                });
        assertEquals(
                new Timestamp(-44364244526000L), strictMongoResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(DECIMAL_COL_LABEL));

        assertEquals(null, strictMongoResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), strictMongoResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTime(STRING_COL_LABEL);
                });
        assertEquals(new Time(1580420718000L), strictMongoResultSet.getTime(OBJECTID_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTime(BOOLEAN_COL_LABEL);
                });
        assertEquals(new Time(-44364244526000L), strictMongoResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(DECIMAL_COL_LABEL));

        assertEquals(null, strictMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(
                new Timestamp(1580420718000L),
                strictMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(BOOLEAN_COL_LABEL);
                });
        assertEquals(
                new Timestamp(-44364244526000L), strictMongoResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMongoResultSet.getTimestamp(DECIMAL_COL_LABEL));
    }

    @Test
    void testRelaxedGetters() throws Exception {
        boolean hasNext = relaxedMongoResultSet.next();
        assertTrue(hasNext);

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                relaxedMongoResultSet.getString(NULL_COL_IDX),
                relaxedMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(DOUBLE_COL_IDX),
                relaxedMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(STRING_COL_IDX),
                relaxedMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(OBJECTID_COL_IDX),
                relaxedMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(BOOLEAN_COL_IDX),
                relaxedMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(DATE_COL_IDX),
                relaxedMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(INTEGER_COL_IDX),
                relaxedMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(LONG_COL_IDX),
                relaxedMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getString(DECIMAL_COL_IDX),
                relaxedMongoResultSet.getString(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        assertEquals(
                relaxedMongoResultSet.getBlob(BINARY_COL_IDX),
                relaxedMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getBlob(UUID_COL_IDX),
                relaxedMongoResultSet.getBlob(UUID_COL_LABEL));

        // Binary cannot be gotten through anything other than getBlob, currently, all of these
        // should be null/0/false.
        assertNull(relaxedMongoResultSet.getString(BINARY_COL_IDX));
        assertNull(relaxedMongoResultSet.getString(UUID_COL_IDX));
        assertFalse(relaxedMongoResultSet.getBoolean(BINARY_COL_IDX));
        assertFalse(relaxedMongoResultSet.getBoolean(UUID_COL_IDX));
        assertEquals(0L, relaxedMongoResultSet.getLong(BINARY_COL_IDX));
        assertEquals(0L, relaxedMongoResultSet.getLong(UUID_COL_IDX));
        assertEquals(0.0, relaxedMongoResultSet.getDouble(BINARY_COL_IDX));
        assertEquals(0.0, relaxedMongoResultSet.getDouble(UUID_COL_IDX));
        assertEquals(new BigDecimal(0L), relaxedMongoResultSet.getBigDecimal(BINARY_COL_IDX));
        assertEquals(new BigDecimal(0L), relaxedMongoResultSet.getBigDecimal(UUID_COL_IDX));
        assertNull(relaxedMongoResultSet.getTimestamp(BINARY_COL_IDX));
        assertNull(relaxedMongoResultSet.getTimestamp(UUID_COL_IDX));

        // Only Binary and String and null values can be gotten from getBlob
        assertNotNull(relaxedMongoResultSet.getBlob(STRING_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBlob(UUID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(NULL_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(DOUBLE_COL_IDX));
        assertNotNull(relaxedMongoResultSet.getBlob(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(BOOLEAN_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(DATE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(INTEGER_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(LONG_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(DECIMAL_COL_IDX));

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
        assertEquals(null, relaxedMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", relaxedMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", relaxedMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", relaxedMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", relaxedMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.00Z", relaxedMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(DECIMAL_COL_LABEL));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, relaxedMongoResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, relaxedMongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    relaxedMongoResultSet.getDouble(STRING_COL_LABEL);
                });
        assertEquals(0.0, relaxedMongoResultSet.getDouble(OBJECTID_COL_LABEL));
        assertEquals(1.0, relaxedMongoResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000.0, relaxedMongoResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test BigDecimal values are as expected
        assertEquals(new BigDecimal(0.0), relaxedMongoResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), relaxedMongoResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    relaxedMongoResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertEquals(new BigDecimal(0L), relaxedMongoResultSet.getBigDecimal(OBJECTID_COL_LABEL));
        assertEquals(new BigDecimal(1.0), relaxedMongoResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
        assertEquals(
                new BigDecimal(-44364244526000L),
                relaxedMongoResultSet.getBigDecimal(DATE_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(INTEGER_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, relaxedMongoResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    relaxedMongoResultSet.getLong(STRING_COL_LABEL);
                });
        assertEquals(0L, relaxedMongoResultSet.getLong(OBJECTID_COL_LABEL));
        assertEquals(1L, relaxedMongoResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, relaxedMongoResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, relaxedMongoResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    relaxedMongoResultSet.getInt(STRING_COL_LABEL);
                });
        assertEquals(0, relaxedMongoResultSet.getInt(OBJECTID_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getInt(BOOLEAN_COL_LABEL));
        assertEquals(-1527325616, relaxedMongoResultSet.getInt(DATE_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getInt(INTEGER_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getInt(LONG_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getInt(DECIMAL_COL_LABEL));

        // We test Long, Int, and Byte, we can safely skip getShort tests

        // Test Byte values are as expected
        assertEquals(0, relaxedMongoResultSet.getByte(NULL_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                NumberFormatException.class,
                () -> {
                    relaxedMongoResultSet.getByte(STRING_COL_LABEL);
                });
        assertEquals(0, relaxedMongoResultSet.getByte(OBJECTID_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getByte(BOOLEAN_COL_LABEL));
        // This is weird, but I'm not going to go against Java's casting semantics.
        assertEquals(80, relaxedMongoResultSet.getByte(DATE_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getByte(INTEGER_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getByte(LONG_COL_LABEL));
        assertEquals(100, relaxedMongoResultSet.getByte(DECIMAL_COL_LABEL));

        // Test Boolean values are as expected
        assertEquals(false, relaxedMongoResultSet.getBoolean(NULL_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, relaxedMongoResultSet.getBoolean(STRING_COL_LABEL));
        assertEquals(false, relaxedMongoResultSet.getBoolean(OBJECTID_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(BOOLEAN_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(DATE_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertEquals(null, relaxedMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    relaxedMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(
                new Timestamp(1580420718000L),
                relaxedMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(BOOLEAN_COL_LABEL));
        assertEquals(
                new Timestamp(-44364244526000L),
                relaxedMongoResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(DECIMAL_COL_LABEL));

        assertEquals(null, relaxedMongoResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), relaxedMongoResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    relaxedMongoResultSet.getTime(STRING_COL_LABEL);
                });
        assertEquals(new Time(1580420718000L), relaxedMongoResultSet.getTime(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTime(BOOLEAN_COL_LABEL));
        assertEquals(new Time(-44364244526000L), relaxedMongoResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(DECIMAL_COL_LABEL));

        assertEquals(null, relaxedMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    relaxedMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(
                new Timestamp(1580420718000L),
                relaxedMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(BOOLEAN_COL_LABEL));
        assertEquals(
                new Timestamp(-44364244526000L),
                relaxedMongoResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(DECIMAL_COL_LABEL));
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(false);

        mockResultSet = new MongoResultSet(cursor, false);

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

        mockResultSet = new MongoResultSet(cursor, false);

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
