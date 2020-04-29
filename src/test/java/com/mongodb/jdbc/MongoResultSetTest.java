package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDbPointer;
import org.bson.BsonDecimal128;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonUndefined;
import org.bson.BsonValue;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoResultSetTest extends MongoMock {
    @Mock MongoCursor<MongoResultDoc> cursor;
    @Mock MongoResultDoc nextMongoResultDoc;
    MongoResultSet mockResultSet;
    static MongoStatement mongoStatement;
    static MongoResultSet relaxedMongoResultSet;
    static MongoResultSet strictMongoResultSet;
    static MongoResultSet closedMongoResultSet;

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
    static int UNDEFINED_COL_IDX = 12;
    static int DBPOINTER_COL_IDX = 13;

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
    static String UNDEFINED_COL_LABEL = "undefined";
    static String DBPOINTER_COL_LABEL = "db_pointer";

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

    static {
        MongoResultDoc mongoResultDoc = new MongoResultDoc(new ArrayList<>(), false);

        mongoResultDoc
                .getValues()
                .add(newColumn("", "", "", NULL_COL_LABEL, NULL_COL_LABEL, new BsonNull()));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                DOUBLE_COL_LABEL,
                                DOUBLE_COL_LABEL,
                                new BsonDouble(1.1)));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                STRING_COL_LABEL,
                                STRING_COL_LABEL,
                                new BsonString("string data")));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                BINARY_COL_LABEL,
                                BINARY_COL_LABEL,
                                new BsonBinary("data".getBytes())));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                UUID_COL_LABEL,
                                UUID_COL_LABEL,
                                new BsonBinary(new UUID(0, 0))));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                OBJECTID_COL_LABEL,
                                OBJECTID_COL_LABEL,
                                new BsonObjectId(new ObjectId("5e334e6e780812e4896dd65e"))));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                BOOLEAN_COL_LABEL,
                                BOOLEAN_COL_LABEL,
                                new BsonBoolean(true)));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                DATE_COL_LABEL,
                                DATE_COL_LABEL,
                                new BsonDateTime(-44364244526000L)));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                INTEGER_COL_LABEL,
                                INTEGER_COL_LABEL,
                                new BsonInt32(100)));
        mongoResultDoc
                .getValues()
                .add(newColumn("", "", "", LONG_COL_LABEL, LONG_COL_LABEL, new BsonInt64(100L)));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                DECIMAL_COL_LABEL,
                                DECIMAL_COL_LABEL,
                                new BsonDecimal128(new Decimal128(100L))));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                UNDEFINED_COL_LABEL,
                                UNDEFINED_COL_LABEL,
                                new BsonUndefined()));
        mongoResultDoc
                .getValues()
                .add(
                        newColumn(
                                "",
                                "",
                                "",
                                DBPOINTER_COL_LABEL,
                                DBPOINTER_COL_LABEL,
                                new BsonDbPointer(
                                        "foo", new ObjectId("5e334e6e780812e4896dd65e"))));

        List<MongoResultDoc> mongoResultDocs = new ArrayList<MongoResultDoc>();
        mongoResultDocs.add(mongoResultDoc);
        try {
            mongoStatement = new MongoStatement(mongoConnection, "test", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        strictMongoResultSet =
                new MongoResultSet(mongoStatement, new MongoExplicitCursor(mongoResultDocs), false);
        relaxedMongoResultSet =
                new MongoResultSet(mongoStatement, new MongoExplicitCursor(mongoResultDocs), true);
        closedMongoResultSet =
                new MongoResultSet(mongoStatement, new MongoExplicitCursor(mongoResultDocs), true);
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setup() throws NoSuchFieldException {
        super.resetMockObjs();
    }

    @Test
    void testStrictGetters() throws Exception {
        boolean hasNext = strictMongoResultSet.next();
        assertTrue(hasNext);

        // Test findColumn.
        assertEquals(NULL_COL_IDX, strictMongoResultSet.findColumn(NULL_COL_LABEL));
        assertEquals(DOUBLE_COL_IDX, strictMongoResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL_IDX, strictMongoResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(OBJECTID_COL_IDX, strictMongoResultSet.findColumn(OBJECTID_COL_LABEL));
        assertEquals(BOOLEAN_COL_IDX, strictMongoResultSet.findColumn(BOOLEAN_COL_LABEL));
        assertEquals(DATE_COL_IDX, strictMongoResultSet.findColumn(DATE_COL_LABEL));
        assertEquals(INTEGER_COL_IDX, strictMongoResultSet.findColumn(INTEGER_COL_LABEL));
        assertEquals(LONG_COL_IDX, strictMongoResultSet.findColumn(LONG_COL_LABEL));
        assertEquals(DECIMAL_COL_IDX, strictMongoResultSet.findColumn(DECIMAL_COL_LABEL));

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

        // Test wasNull.
        strictMongoResultSet.getString(NULL_COL_IDX);
        assertTrue(strictMongoResultSet.wasNull());
        strictMongoResultSet.getString(DOUBLE_COL_IDX);
        assertFalse(strictMongoResultSet.wasNull());

        strictMongoResultSet.getString(UNDEFINED_COL_IDX);
        assertTrue(strictMongoResultSet.wasNull());
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getString(DBPOINTER_COL_LABEL);
                });
        assertFalse(strictMongoResultSet.wasNull());

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        //
        assertEquals(
                strictMongoResultSet.getBytes(BINARY_COL_IDX),
                strictMongoResultSet.getBytes(BINARY_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getBytes(UUID_COL_IDX),
                strictMongoResultSet.getBytes(UUID_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getBlob(BINARY_COL_IDX),
                strictMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(
                strictMongoResultSet.getBlob(UUID_COL_IDX),
                strictMongoResultSet.getBlob(UUID_COL_LABEL));

        // Binary cannot be gotten through anything other than getBlob and getBinaryStream, currently.
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

        // Only Binary and null values can be gotten from getBlob
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(STRING_COL_LABEL);
                });
        assertNotNull(strictMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(strictMongoResultSet.getBlob(UUID_COL_LABEL));
        assertNull(strictMongoResultSet.getBlob(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBlob(OBJECTID_COL_IDX);
                });
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

        // Only Binary and null values can be gotten from getBinaryStream
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(STRING_COL_LABEL);
                });
        assertNotNull(strictMongoResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertNotNull(strictMongoResultSet.getBinaryStream(UUID_COL_LABEL));
        assertNull(strictMongoResultSet.getBinaryStream(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBinaryStream(DECIMAL_COL_IDX);
                });

        // Only Binary and null values can be gotten from getBytes
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(STRING_COL_LABEL);
                });
        assertNotNull(strictMongoResultSet.getBytes(BINARY_COL_LABEL));
        assertNotNull(strictMongoResultSet.getBytes(UUID_COL_LABEL));
        assertNull(strictMongoResultSet.getBytes(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBytes(DECIMAL_COL_IDX);
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
        assertNull(strictMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", strictMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", strictMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", strictMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", strictMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.000Z", strictMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", strictMongoResultSet.getString(DECIMAL_COL_LABEL));
        assertNotNull(strictMongoResultSet.getAsciiStream(OBJECTID_COL_LABEL));
        assertNotNull(strictMongoResultSet.getAsciiStream(STRING_COL_LABEL));
        assertNotNull(strictMongoResultSet.getUnicodeStream(OBJECTID_COL_LABEL));
        assertNotNull(strictMongoResultSet.getUnicodeStream(STRING_COL_LABEL));

        // Actually check getAsciiStream and getUnicodeStream output. We just check
        // that the length is what is expected.
        assertEquals(
                24,
                strictMongoResultSet
                        .getAsciiStream(OBJECTID_COL_LABEL)
                        .read(new byte[100], 0, 100));
        assertEquals(
                24,
                strictMongoResultSet
                        .getUnicodeStream(OBJECTID_COL_LABEL)
                        .read(new byte[100], 0, 100));

        assertEquals(
                11,
                strictMongoResultSet.getAsciiStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
        assertEquals(
                11,
                strictMongoResultSet
                        .getUnicodeStream(STRING_COL_LABEL)
                        .read(new byte[100], 0, 100));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, strictMongoResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, strictMongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
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
        assertEquals(BigDecimal.ZERO, strictMongoResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), strictMongoResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBigDecimal(OBJECTID_COL_LABEL);
                });
        assertEquals(BigDecimal.ONE, strictMongoResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
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
                SQLException.class,
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
                SQLException.class,
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
                SQLException.class,
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
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getBoolean(DATE_COL_LABEL);
                });
        assertEquals(true, strictMongoResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, strictMongoResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertNull(strictMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(OBJECTID_COL_LABEL);
                });
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

        assertNull(strictMongoResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), strictMongoResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTime(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTime(OBJECTID_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTime(BOOLEAN_COL_LABEL);
                });
        assertEquals(new Time(-44364244526000L), strictMongoResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), strictMongoResultSet.getTime(DECIMAL_COL_LABEL));

        assertNull(strictMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMongoResultSet.getTimestamp(OBJECTID_COL_LABEL);
                });
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
    void closedResultSets() throws Exception {
        try {
            closedMongoResultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertTrue(closedMongoResultSet.isClosed());

        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.next();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.wasNull();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBytes(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBytes("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getAsciiStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getAsciiStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getUnicodeStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getUnicodeStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBinaryStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBinaryStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBoolean(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBoolean("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getByte(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getByte("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getShort(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getShort("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getInt(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getInt("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getLong(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getLong("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFloat(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFloat("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDouble(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDouble("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.clearWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getCursorName();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getMetaData();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.findColumn("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getCharacterStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.isFirst();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.isLast();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.previous();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFetchDirection();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getType();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getConcurrency();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowUpdated();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowInserted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowDeleted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.insertRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.deleteRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.refreshRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getStatement();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBlob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBlob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTime(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTime("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTimestamp(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTimestamp("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getHoldability();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNCharacterStream("f");
                });
    }

    @Test
    void testRelaxedGetters() throws Exception {
        boolean hasNext = relaxedMongoResultSet.next();
        assertTrue(hasNext);

        // Test findColumn.
        assertEquals(NULL_COL_IDX, relaxedMongoResultSet.findColumn(NULL_COL_LABEL));
        assertEquals(DOUBLE_COL_IDX, relaxedMongoResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL_IDX, relaxedMongoResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(OBJECTID_COL_IDX, relaxedMongoResultSet.findColumn(OBJECTID_COL_LABEL));
        assertEquals(BOOLEAN_COL_IDX, relaxedMongoResultSet.findColumn(BOOLEAN_COL_LABEL));
        assertEquals(DATE_COL_IDX, relaxedMongoResultSet.findColumn(DATE_COL_LABEL));
        assertEquals(INTEGER_COL_IDX, relaxedMongoResultSet.findColumn(INTEGER_COL_LABEL));
        assertEquals(LONG_COL_IDX, relaxedMongoResultSet.findColumn(LONG_COL_LABEL));
        assertEquals(DECIMAL_COL_IDX, relaxedMongoResultSet.findColumn(DECIMAL_COL_LABEL));

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
        assertEquals(
                relaxedMongoResultSet.getBytes(BINARY_COL_IDX),
                relaxedMongoResultSet.getBytes(BINARY_COL_LABEL));
        assertEquals(
                relaxedMongoResultSet.getBytes(UUID_COL_IDX),
                relaxedMongoResultSet.getBytes(UUID_COL_LABEL));

        // Test wasNull.
        relaxedMongoResultSet.getString(NULL_COL_IDX);
        assertTrue(relaxedMongoResultSet.wasNull());
        relaxedMongoResultSet.getString(UNDEFINED_COL_IDX);
        assertTrue(relaxedMongoResultSet.wasNull());
        relaxedMongoResultSet.getString(DBPOINTER_COL_LABEL);
        assertTrue(relaxedMongoResultSet.wasNull());
        relaxedMongoResultSet.getString(DOUBLE_COL_IDX);
        assertFalse(relaxedMongoResultSet.wasNull());

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
        assertEquals(BigDecimal.ZERO, relaxedMongoResultSet.getBigDecimal(BINARY_COL_IDX));
        assertEquals(BigDecimal.ZERO, relaxedMongoResultSet.getBigDecimal(UUID_COL_IDX));
        assertNull(relaxedMongoResultSet.getTimestamp(BINARY_COL_IDX));
        assertNull(relaxedMongoResultSet.getTimestamp(UUID_COL_IDX));

        // Only Binary and null values can be gotten from getBlob
        assertNull(relaxedMongoResultSet.getBlob(STRING_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBlob(UUID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(NULL_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(DOUBLE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBlob(BOOLEAN_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(DATE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(INTEGER_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(LONG_COL_IDX));
        assertNull(relaxedMongoResultSet.getBlob(DECIMAL_COL_IDX));

        // Only Binary and null values can be gotten from getBinaryStream
        assertNull(relaxedMongoResultSet.getBinaryStream(STRING_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBinaryStream(UUID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBinaryStream(NULL_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBinaryStream(DOUBLE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBinaryStream(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBinaryStream(BOOLEAN_COL_IDX));
        assertNull(relaxedMongoResultSet.getBinaryStream(DATE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBinaryStream(INTEGER_COL_IDX));
        assertNull(relaxedMongoResultSet.getBinaryStream(LONG_COL_IDX));
        assertNull(relaxedMongoResultSet.getBinaryStream(DECIMAL_COL_IDX));

        // Only Binary and null values can be gotten from getBytes
        assertNull(relaxedMongoResultSet.getBytes(STRING_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBytes(BINARY_COL_LABEL));
        assertNotNull(relaxedMongoResultSet.getBytes(UUID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBytes(NULL_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBytes(DOUBLE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBytes(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getBytes(BOOLEAN_COL_IDX));
        assertNull(relaxedMongoResultSet.getBytes(DATE_COL_IDX));
        assertNull(relaxedMongoResultSet.getBytes(INTEGER_COL_IDX));
        assertNull(relaxedMongoResultSet.getBytes(LONG_COL_IDX));
        assertNull(relaxedMongoResultSet.getBytes(DECIMAL_COL_IDX));

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
        assertNull(relaxedMongoResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", relaxedMongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", relaxedMongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", relaxedMongoResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", relaxedMongoResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.000Z", relaxedMongoResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", relaxedMongoResultSet.getString(DECIMAL_COL_LABEL));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, relaxedMongoResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, relaxedMongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertEquals(0.0, relaxedMongoResultSet.getDouble(STRING_COL_LABEL));
        assertEquals(0.0, relaxedMongoResultSet.getDouble(OBJECTID_COL_LABEL));
        assertEquals(1.0, relaxedMongoResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000.0, relaxedMongoResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, relaxedMongoResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test BigDecimal values are as expected
        assertEquals(BigDecimal.ZERO, relaxedMongoResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), relaxedMongoResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertEquals(BigDecimal.ZERO, relaxedMongoResultSet.getBigDecimal(STRING_COL_LABEL));
        assertEquals(BigDecimal.ZERO, relaxedMongoResultSet.getBigDecimal(OBJECTID_COL_LABEL));
        assertEquals(BigDecimal.ONE, relaxedMongoResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
        assertEquals(
                new BigDecimal(-44364244526000L),
                relaxedMongoResultSet.getBigDecimal(DATE_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(INTEGER_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMongoResultSet.getBigDecimal(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, relaxedMongoResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1L, relaxedMongoResultSet.getLong(DOUBLE_COL_LABEL));
        assertEquals(0L, relaxedMongoResultSet.getLong(STRING_COL_LABEL));
        assertEquals(0L, relaxedMongoResultSet.getLong(OBJECTID_COL_LABEL));
        assertEquals(1L, relaxedMongoResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, relaxedMongoResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, relaxedMongoResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, relaxedMongoResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, relaxedMongoResultSet.getInt(DOUBLE_COL_LABEL));
        assertEquals(0, relaxedMongoResultSet.getInt(STRING_COL_LABEL));
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
        assertEquals(0, relaxedMongoResultSet.getByte(STRING_COL_LABEL));
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
        assertEquals(false, relaxedMongoResultSet.getBoolean(DATE_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, relaxedMongoResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertNull(relaxedMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(STRING_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(BOOLEAN_COL_LABEL));
        assertEquals(
                new Timestamp(-44364244526000L),
                relaxedMongoResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMongoResultSet.getTimestamp(DECIMAL_COL_LABEL));

        assertNull(relaxedMongoResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), relaxedMongoResultSet.getTime(DOUBLE_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTime(STRING_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTime(OBJECTID_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTime(BOOLEAN_COL_LABEL));
        assertEquals(new Time(-44364244526000L), relaxedMongoResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), relaxedMongoResultSet.getTime(DECIMAL_COL_LABEL));

        assertNull(relaxedMongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(STRING_COL_LABEL));
        assertNull(relaxedMongoResultSet.getTimestamp(OBJECTID_COL_LABEL));
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
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenReturn(generateRow(true));

        mockResultSet = new MongoResultSet(mongoStatement, cursor, false);

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
        when(cursor.next()).thenReturn(nextMongoResultDoc);

        nextMongoResultDoc.values = new ArrayList<>();

        mockResultSet = new MongoResultSet(mongoStatement, cursor, false);

        assertFalse(mockResultSet.isFirst());
        assertFalse(mockResultSet.isLast());

        boolean hasNext = mockResultSet.next();
        assertTrue(hasNext);
        assertNotNull(mockResultSet.getCurrent());
        // This still throws because "label" is unknown.
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
        assertTrue(mockResultSet.isFirst());
        assertFalse(mockResultSet.isLast());
    }

    @Test
    void testEmptyResultSet() throws SQLException {
        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
        List<Column> cols = new ArrayList<>();
        String colName = "a";
        cols.add(generateCol("myDB", "foo", colName, null));
        MongoResultDoc emptyResultDoc = new MongoResultDoc(cols, true);

        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            if (nextCalledOnCursor.get()) {
                                return false;
                            }
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet = new MongoResultSet(mongoStatement, cursor, false);

        assertFalse(mockResultSet.isFirst());
        // For empty result set, isLast should always be true
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // For empty result set, isFirst should always be false
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertEquals(1, mockResultSet.getMetaData().getColumnCount());
        assertFalse(mockResultSet.next());
        // query value for existing column in empty result should result to exception
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString(colName);
                });
    }

    @Test
    void testEmptyResultSetWhenGetMetadataCalledFirst() throws SQLException {
        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
        List<Column> cols = new ArrayList<>();
        String colName = "a";
        cols.add(generateCol("myDB", "foo", colName, null));
        MongoResultDoc emptyResultDoc = new MongoResultDoc(cols, true);

        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet = new MongoResultSet(mongoStatement, cursor, false);

        assertEquals(1, mockResultSet.getMetaData().getColumnCount());
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // For empty resultset, isFirst should always be false
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // query value for existing column in empty result should result to exception
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString(colName);
                });
    }

    @Test
    void differentMetaDataOnDifferentRows() throws SQLException {
        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
        List<Column> cols1 = new ArrayList<>();
        String colName = "a";
        cols1.add(generateCol("myDB", "foo", colName, new BsonInt32(1)));
        MongoResultDoc row1 = new MongoResultDoc(cols1, false);
        List<Column> cols2 = new ArrayList<>();
        cols2.add(generateCol("myDB", "foo", colName, new BsonString("test")));
        MongoResultDoc row2 = new MongoResultDoc(cols2, false);

        List<MongoResultDoc> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        Iterator<MongoResultDoc> iter = rows.iterator();
        when(cursor.hasNext()).thenAnswer(invocation -> iter.hasNext());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            MongoResultDoc doc = iter.next();
                            return doc;
                        });

        mockResultSet = new MongoResultSet(mongoStatement, cursor, false);

        ResultSetMetaData metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.INTEGER, metaData.getColumnType(1));

        mockResultSet.next();
        // This is still the first row
        metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.INTEGER, metaData.getColumnType(1));

        mockResultSet.next();
        // Second row
        metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.LONGVARCHAR, metaData.getColumnType(1));
    }
}
