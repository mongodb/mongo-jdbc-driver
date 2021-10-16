package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
class MySQLResultSetTest extends MySQLMock {
    @Mock MongoCursor<MySQLResultDoc> cursor;
    MySQLResultSet mockResultSet;
    static MySQLStatement mongoStatement;
    static MySQLResultSet relaxedMySQLResultSet;
    static MySQLResultSet strictMySQLResultSet;
    static MySQLResultSet closedMySQLResultSet;

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

    static {
        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        try {
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", NULL_COL_LABEL, NULL_COL_LABEL, "null"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", DOUBLE_COL_LABEL, DOUBLE_COL_LABEL, "double"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", STRING_COL_LABEL, STRING_COL_LABEL, "string"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", BINARY_COL_LABEL, BINARY_COL_LABEL, "binData"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", UUID_COL_LABEL, UUID_COL_LABEL, "binData"));
            metaDoc.columns.add(
                    new MySQLColumnInfo(
                            "", "", "", OBJECTID_COL_LABEL, OBJECTID_COL_LABEL, "objectId"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", BOOLEAN_COL_LABEL, BOOLEAN_COL_LABEL, "bool"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", DATE_COL_LABEL, DATE_COL_LABEL, "date"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", INTEGER_COL_LABEL, INTEGER_COL_LABEL, "int"));
            metaDoc.columns.add(
                    new MySQLColumnInfo("", "", "", LONG_COL_LABEL, LONG_COL_LABEL, "long"));
            metaDoc.columns.add(
                    new MySQLColumnInfo(
                            "", "", "", DECIMAL_COL_LABEL, DECIMAL_COL_LABEL, "decimal"));
            metaDoc.columns.add(
                    new MySQLColumnInfo(
                            "", "", "", UNDEFINED_COL_LABEL, UNDEFINED_COL_LABEL, "undefined"));
            metaDoc.columns.add(
                    new MySQLColumnInfo(
                            "", "", "", DBPOINTER_COL_LABEL, DBPOINTER_COL_LABEL, "dbPointer"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MySQLResultDoc valuesDoc = new MySQLResultDoc();
        valuesDoc.values = new ArrayList<>();
        valuesDoc.values.add(new BsonNull());
        valuesDoc.values.add(new BsonDouble(1.1));
        valuesDoc.values.add(new BsonString("string data"));
        valuesDoc.values.add(new BsonBinary("data".getBytes()));
        valuesDoc.values.add(new BsonBinary(new UUID(0, 0)));
        valuesDoc.values.add(new BsonObjectId(new ObjectId("5e334e6e780812e4896dd65e")));
        valuesDoc.values.add(new BsonBoolean(true));
        valuesDoc.values.add(new BsonDateTime(-44364244526000L));
        valuesDoc.values.add(new BsonInt32(100));
        valuesDoc.values.add(new BsonInt64(100L));
        valuesDoc.values.add(new BsonDecimal128(new Decimal128(100L)));
        valuesDoc.values.add(new BsonUndefined());
        valuesDoc.values.add(new BsonDbPointer("foo", new ObjectId("5e334e6e780812e4896dd65e")));

        List<MySQLResultDoc> mongoResultDocs = new ArrayList<MySQLResultDoc>();
        mongoResultDocs.add(metaDoc);
        mongoResultDocs.add(valuesDoc);

        try {
            mongoStatement = new MySQLStatement(mongoConnection, "test", true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
        // create result sets used by tests.
        strictMySQLResultSet =
                new MySQLResultSet(mongoStatement, new MySQLExplicitCursor(mongoResultDocs), false);
        relaxedMySQLResultSet =
                new MySQLResultSet(mongoStatement, new MySQLExplicitCursor(mongoResultDocs), true);
        closedMySQLResultSet =
                new MySQLResultSet(mongoStatement, new MySQLExplicitCursor(mongoResultDocs), true);
        // call next() so that each result set is on the pre-populated row.
            relaxedMySQLResultSet.next();
            strictMySQLResultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setup() throws NoSuchFieldException {
        MySQLMock.resetMockObjs();
    }

    @SuppressWarnings("deprecation")
    @Test
    void testStrictGetters() throws Exception {
        // Test findColumn.
        assertEquals(NULL_COL_IDX, strictMySQLResultSet.findColumn(NULL_COL_LABEL));
        assertEquals(DOUBLE_COL_IDX, strictMySQLResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL_IDX, strictMySQLResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(OBJECTID_COL_IDX, strictMySQLResultSet.findColumn(OBJECTID_COL_LABEL));
        assertEquals(BOOLEAN_COL_IDX, strictMySQLResultSet.findColumn(BOOLEAN_COL_LABEL));
        assertEquals(DATE_COL_IDX, strictMySQLResultSet.findColumn(DATE_COL_LABEL));
        assertEquals(INTEGER_COL_IDX, strictMySQLResultSet.findColumn(INTEGER_COL_LABEL));
        assertEquals(LONG_COL_IDX, strictMySQLResultSet.findColumn(LONG_COL_LABEL));
        assertEquals(DECIMAL_COL_IDX, strictMySQLResultSet.findColumn(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                strictMySQLResultSet.getString(NULL_COL_IDX),
                strictMySQLResultSet.getString(NULL_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(DOUBLE_COL_IDX),
                strictMySQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(STRING_COL_IDX),
                strictMySQLResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(OBJECTID_COL_IDX),
                strictMySQLResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(BOOLEAN_COL_IDX),
                strictMySQLResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(DATE_COL_IDX),
                strictMySQLResultSet.getString(DATE_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(INTEGER_COL_IDX),
                strictMySQLResultSet.getString(INTEGER_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(LONG_COL_IDX),
                strictMySQLResultSet.getString(LONG_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getString(DECIMAL_COL_IDX),
                strictMySQLResultSet.getString(DECIMAL_COL_LABEL));

        // Test wasNull.
        strictMySQLResultSet.getString(NULL_COL_IDX);
        assertTrue(strictMySQLResultSet.wasNull());
        strictMySQLResultSet.getString(DOUBLE_COL_IDX);
        assertFalse(strictMySQLResultSet.wasNull());

        strictMySQLResultSet.getString(UNDEFINED_COL_IDX);
        assertTrue(strictMySQLResultSet.wasNull());
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getString(DBPOINTER_COL_LABEL);
                });
        assertFalse(strictMySQLResultSet.wasNull());

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        //
        assertEquals(
                strictMySQLResultSet.getBytes(BINARY_COL_IDX),
                strictMySQLResultSet.getBytes(BINARY_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getBytes(UUID_COL_IDX),
                strictMySQLResultSet.getBytes(UUID_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getBlob(BINARY_COL_IDX),
                strictMySQLResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getBlob(UUID_COL_IDX),
                strictMySQLResultSet.getBlob(UUID_COL_LABEL));

        // Binary cannot be gotten through anything other than getBlob and getBinaryStream, currently.
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getString(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getString(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBoolean(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBoolean(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getLong(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getLong(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getDouble(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getDouble(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBigDecimal(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBigDecimal(UUID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(BINARY_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(UUID_COL_IDX);
                });

        // Only Binary and null values can be gotten from getBlob
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(STRING_COL_LABEL);
                });
        assertNotNull(strictMySQLResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getBlob(UUID_COL_LABEL));
        assertNull(strictMySQLResultSet.getBlob(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBlob(DECIMAL_COL_IDX);
                });

        // Only Binary and null values can be gotten from getBinaryStream
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(STRING_COL_LABEL);
                });
        assertNotNull(strictMySQLResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getBinaryStream(UUID_COL_LABEL));
        assertNull(strictMySQLResultSet.getBinaryStream(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBinaryStream(DECIMAL_COL_IDX);
                });

        // Only Binary and null values can be gotten from getBytes
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(STRING_COL_LABEL);
                });
        assertNotNull(strictMySQLResultSet.getBytes(BINARY_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getBytes(UUID_COL_LABEL));
        assertNull(strictMySQLResultSet.getBytes(NULL_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(DOUBLE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(OBJECTID_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(BOOLEAN_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(DATE_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(INTEGER_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(LONG_COL_IDX);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBytes(DECIMAL_COL_IDX);
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
        assertNull(strictMySQLResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", strictMySQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", strictMySQLResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", strictMySQLResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", strictMySQLResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.000Z", strictMySQLResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", strictMySQLResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", strictMySQLResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", strictMySQLResultSet.getString(DECIMAL_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getAsciiStream(OBJECTID_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getAsciiStream(STRING_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getUnicodeStream(OBJECTID_COL_LABEL));
        assertNotNull(strictMySQLResultSet.getUnicodeStream(STRING_COL_LABEL));

        // Actually check getAsciiStream and getUnicodeStream output. We just check
        // that the length is what is expected.
        assertEquals(
                24,
                strictMySQLResultSet
                        .getAsciiStream(OBJECTID_COL_LABEL)
                        .read(new byte[100], 0, 100));
        assertEquals(
                24,
                strictMySQLResultSet
                        .getUnicodeStream(OBJECTID_COL_LABEL)
                        .read(new byte[100], 0, 100));

        assertEquals(
                11,
                strictMySQLResultSet.getAsciiStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
        assertEquals(
                11,
                strictMySQLResultSet
                        .getUnicodeStream(STRING_COL_LABEL)
                        .read(new byte[100], 0, 100));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, strictMySQLResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, strictMySQLResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getDouble(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getDouble(OBJECTID_COL_LABEL);
                });
        assertEquals(1.0, strictMySQLResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000.0, strictMySQLResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, strictMySQLResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, strictMySQLResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, strictMySQLResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test BigDecimal values are as expected
        assertEquals(BigDecimal.ZERO, strictMySQLResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), strictMySQLResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBigDecimal(OBJECTID_COL_LABEL);
                });
        assertEquals(BigDecimal.ONE, strictMySQLResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
        assertEquals(
                new BigDecimal(-44364244526000L),
                strictMySQLResultSet.getBigDecimal(DATE_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMySQLResultSet.getBigDecimal(INTEGER_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMySQLResultSet.getBigDecimal(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100.0), strictMySQLResultSet.getBigDecimal(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, strictMySQLResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1, strictMySQLResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getLong(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getLong(OBJECTID_COL_LABEL);
                });
        assertEquals(1L, strictMySQLResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, strictMySQLResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, strictMySQLResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, strictMySQLResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, strictMySQLResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, strictMySQLResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, strictMySQLResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getInt(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getInt(OBJECTID_COL_LABEL);
                });
        assertEquals(1, strictMySQLResultSet.getInt(BOOLEAN_COL_LABEL));
        assertEquals(-1527325616, strictMySQLResultSet.getInt(DATE_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getInt(INTEGER_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getInt(LONG_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getInt(DECIMAL_COL_LABEL));

        // We test Long, Int, and Byte, we can safely skip getShort tests

        // Test Byte values are as expected
        assertEquals(0, strictMySQLResultSet.getByte(NULL_COL_LABEL));
        assertEquals(1, strictMySQLResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getByte(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getByte(OBJECTID_COL_LABEL);
                });
        assertEquals(1, strictMySQLResultSet.getByte(BOOLEAN_COL_LABEL));
        // This is weird, but I'm not going to go against Java's casting semantics.
        assertEquals(80, strictMySQLResultSet.getByte(DATE_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getByte(INTEGER_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getByte(LONG_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getByte(DECIMAL_COL_LABEL));

        // Test Boolean values are as expected
        assertEquals(false, strictMySQLResultSet.getBoolean(NULL_COL_LABEL));
        assertEquals(true, strictMySQLResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, strictMySQLResultSet.getBoolean(STRING_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBoolean(OBJECTID_COL_LABEL);
                });
        assertEquals(true, strictMySQLResultSet.getBoolean(BOOLEAN_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getBoolean(DATE_COL_LABEL);
                });
        assertEquals(true, strictMySQLResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, strictMySQLResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, strictMySQLResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertNull(strictMySQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMySQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(OBJECTID_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(BOOLEAN_COL_LABEL);
                });
        assertEquals(
                new Timestamp(-44364244526000L), strictMySQLResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(DECIMAL_COL_LABEL));

        assertNull(strictMySQLResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), strictMySQLResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTime(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTime(OBJECTID_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTime(BOOLEAN_COL_LABEL);
                });
        assertEquals(new Time(-44364244526000L), strictMySQLResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), strictMySQLResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), strictMySQLResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), strictMySQLResultSet.getTime(DECIMAL_COL_LABEL));

        assertNull(strictMySQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), strictMySQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(OBJECTID_COL_LABEL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    strictMySQLResultSet.getTimestamp(BOOLEAN_COL_LABEL);
                });
        assertEquals(
                new Timestamp(-44364244526000L), strictMySQLResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), strictMySQLResultSet.getTimestamp(DECIMAL_COL_LABEL));
    }

    @Test
    void testGetObject() throws Exception {
        // test that the index and label versions of getObject have matching results
        assertEquals(
                strictMySQLResultSet.getObject(NULL_COL_IDX),
                strictMySQLResultSet.getObject(NULL_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(DOUBLE_COL_IDX),
                strictMySQLResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(STRING_COL_IDX),
                strictMySQLResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(OBJECTID_COL_IDX),
                strictMySQLResultSet.getObject(OBJECTID_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(BOOLEAN_COL_IDX),
                strictMySQLResultSet.getObject(BOOLEAN_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(DATE_COL_IDX),
                strictMySQLResultSet.getObject(DATE_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(INTEGER_COL_IDX),
                strictMySQLResultSet.getObject(INTEGER_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(LONG_COL_IDX),
                strictMySQLResultSet.getObject(LONG_COL_LABEL));
        assertEquals(
                strictMySQLResultSet.getObject(DECIMAL_COL_IDX),
                strictMySQLResultSet.getObject(DECIMAL_COL_LABEL));

        // test that getObject returns the expected java object for each bson type
        assertNull(strictMySQLResultSet.getObject(NULL_COL_LABEL));
        assertEquals(1.1, strictMySQLResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals("string data", strictMySQLResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", strictMySQLResultSet.getObject(OBJECTID_COL_LABEL));
        assertEquals(true, strictMySQLResultSet.getObject(BOOLEAN_COL_LABEL));
        assertEquals(new Date(-44364244526000L), strictMySQLResultSet.getObject(DATE_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getObject(INTEGER_COL_LABEL));
        assertEquals(100, strictMySQLResultSet.getObject(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100), strictMySQLResultSet.getObject(DECIMAL_COL_LABEL));
    }

    @SuppressWarnings("deprecation")
    @Test
    void closedResultSets() throws Exception {
        try {
            closedMySQLResultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertTrue(closedMySQLResultSet.isClosed());

        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.next();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.wasNull();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBytes(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBytes("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getAsciiStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getAsciiStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getUnicodeStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getUnicodeStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBinaryStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBinaryStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBoolean(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBoolean("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getByte(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getByte("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getShort(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getShort("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getInt(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getInt("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getLong(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getLong("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getFloat(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getFloat("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDouble(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDouble("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBigDecimal("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.clearWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getCursorName();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getMetaData();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.findColumn("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getCharacterStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.isFirst();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.isLast();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.previous();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getFetchDirection();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getType();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getConcurrency();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.rowUpdated();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.rowInserted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.rowDeleted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.insertRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.deleteRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.refreshRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getStatement();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBlob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getBlob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getTime(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getTime("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getTimestamp(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getTimestamp("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getHoldability();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMySQLResultSet.getNCharacterStream("f");
                });
    }

    @SuppressWarnings("deprecation")
    @Test
    void testRelaxedGetters() throws Exception {
        // Test findColumn.
        assertEquals(NULL_COL_IDX, relaxedMySQLResultSet.findColumn(NULL_COL_LABEL));
        assertEquals(DOUBLE_COL_IDX, relaxedMySQLResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL_IDX, relaxedMySQLResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(OBJECTID_COL_IDX, relaxedMySQLResultSet.findColumn(OBJECTID_COL_LABEL));
        assertEquals(BOOLEAN_COL_IDX, relaxedMySQLResultSet.findColumn(BOOLEAN_COL_LABEL));
        assertEquals(DATE_COL_IDX, relaxedMySQLResultSet.findColumn(DATE_COL_LABEL));
        assertEquals(INTEGER_COL_IDX, relaxedMySQLResultSet.findColumn(INTEGER_COL_LABEL));
        assertEquals(LONG_COL_IDX, relaxedMySQLResultSet.findColumn(LONG_COL_LABEL));
        assertEquals(DECIMAL_COL_IDX, relaxedMySQLResultSet.findColumn(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                relaxedMySQLResultSet.getString(NULL_COL_IDX),
                relaxedMySQLResultSet.getString(NULL_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(DOUBLE_COL_IDX),
                relaxedMySQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(STRING_COL_IDX),
                relaxedMySQLResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(OBJECTID_COL_IDX),
                relaxedMySQLResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(BOOLEAN_COL_IDX),
                relaxedMySQLResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(DATE_COL_IDX),
                relaxedMySQLResultSet.getString(DATE_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(INTEGER_COL_IDX),
                relaxedMySQLResultSet.getString(INTEGER_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(LONG_COL_IDX),
                relaxedMySQLResultSet.getString(LONG_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getString(DECIMAL_COL_IDX),
                relaxedMySQLResultSet.getString(DECIMAL_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly for the Binary types.
        assertEquals(
                relaxedMySQLResultSet.getBlob(BINARY_COL_IDX),
                relaxedMySQLResultSet.getBlob(BINARY_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getBlob(UUID_COL_IDX),
                relaxedMySQLResultSet.getBlob(UUID_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getBytes(BINARY_COL_IDX),
                relaxedMySQLResultSet.getBytes(BINARY_COL_LABEL));
        assertEquals(
                relaxedMySQLResultSet.getBytes(UUID_COL_IDX),
                relaxedMySQLResultSet.getBytes(UUID_COL_LABEL));

        // Test wasNull.
        relaxedMySQLResultSet.getString(NULL_COL_IDX);
        assertTrue(relaxedMySQLResultSet.wasNull());
        relaxedMySQLResultSet.getString(UNDEFINED_COL_IDX);
        assertTrue(relaxedMySQLResultSet.wasNull());
        relaxedMySQLResultSet.getString(DBPOINTER_COL_LABEL);
        assertTrue(relaxedMySQLResultSet.wasNull());
        relaxedMySQLResultSet.getString(DOUBLE_COL_IDX);
        assertFalse(relaxedMySQLResultSet.wasNull());

        // Binary cannot be gotten through anything other than getBlob, currently, all of these
        // should be null/0/false.
        assertNull(relaxedMySQLResultSet.getString(BINARY_COL_IDX));
        assertNull(relaxedMySQLResultSet.getString(UUID_COL_IDX));
        assertFalse(relaxedMySQLResultSet.getBoolean(BINARY_COL_IDX));
        assertFalse(relaxedMySQLResultSet.getBoolean(UUID_COL_IDX));
        assertEquals(0L, relaxedMySQLResultSet.getLong(BINARY_COL_IDX));
        assertEquals(0L, relaxedMySQLResultSet.getLong(UUID_COL_IDX));
        assertEquals(0.0, relaxedMySQLResultSet.getDouble(BINARY_COL_IDX));
        assertEquals(0.0, relaxedMySQLResultSet.getDouble(UUID_COL_IDX));
        assertEquals(BigDecimal.ZERO, relaxedMySQLResultSet.getBigDecimal(BINARY_COL_IDX));
        assertEquals(BigDecimal.ZERO, relaxedMySQLResultSet.getBigDecimal(UUID_COL_IDX));
        assertNull(relaxedMySQLResultSet.getTimestamp(BINARY_COL_IDX));
        assertNull(relaxedMySQLResultSet.getTimestamp(UUID_COL_IDX));

        // Only Binary and null values can be gotten from getBlob
        assertNull(relaxedMySQLResultSet.getBlob(STRING_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBlob(BINARY_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBlob(UUID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBlob(NULL_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBlob(DOUBLE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBlob(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBlob(BOOLEAN_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBlob(DATE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBlob(INTEGER_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBlob(LONG_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBlob(DECIMAL_COL_IDX));

        // Only Binary and null values can be gotten from getBinaryStream
        assertNull(relaxedMySQLResultSet.getBinaryStream(STRING_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBinaryStream(UUID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBinaryStream(NULL_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBinaryStream(DOUBLE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBinaryStream(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBinaryStream(BOOLEAN_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBinaryStream(DATE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBinaryStream(INTEGER_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBinaryStream(LONG_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBinaryStream(DECIMAL_COL_IDX));

        // Only Binary and null values can be gotten from getBytes
        assertNull(relaxedMySQLResultSet.getBytes(STRING_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBytes(BINARY_COL_LABEL));
        assertNotNull(relaxedMySQLResultSet.getBytes(UUID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBytes(NULL_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBytes(DOUBLE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBytes(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getBytes(BOOLEAN_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBytes(DATE_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBytes(INTEGER_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBytes(LONG_COL_IDX));
        assertNull(relaxedMySQLResultSet.getBytes(DECIMAL_COL_IDX));

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
        assertNull(relaxedMySQLResultSet.getString(NULL_COL_LABEL));
        assertEquals("1.1", relaxedMySQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("string data", relaxedMySQLResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                "5e334e6e780812e4896dd65e", relaxedMySQLResultSet.getString(OBJECTID_COL_LABEL));
        assertEquals("true", relaxedMySQLResultSet.getString(BOOLEAN_COL_LABEL));
        assertEquals("0564-02-23T22:44:34.000Z", relaxedMySQLResultSet.getString(DATE_COL_LABEL));
        assertEquals("100", relaxedMySQLResultSet.getString(INTEGER_COL_LABEL));
        assertEquals("100", relaxedMySQLResultSet.getString(LONG_COL_LABEL));
        assertEquals("100", relaxedMySQLResultSet.getString(DECIMAL_COL_LABEL));

        // getClob just wraps getString, we can ignore it

        // Test Double values are as expected
        assertEquals(0.0, relaxedMySQLResultSet.getDouble(NULL_COL_LABEL));
        assertEquals(1.1, relaxedMySQLResultSet.getDouble(DOUBLE_COL_LABEL));
        assertEquals(0.0, relaxedMySQLResultSet.getDouble(STRING_COL_LABEL));
        assertEquals(0.0, relaxedMySQLResultSet.getDouble(OBJECTID_COL_LABEL));
        assertEquals(1.0, relaxedMySQLResultSet.getDouble(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000.0, relaxedMySQLResultSet.getDouble(DATE_COL_LABEL));
        assertEquals(100.0, relaxedMySQLResultSet.getDouble(INTEGER_COL_LABEL));
        assertEquals(100.0, relaxedMySQLResultSet.getDouble(LONG_COL_LABEL));
        assertEquals(100.0, relaxedMySQLResultSet.getDouble(DECIMAL_COL_LABEL));

        // Test BigDecimal values are as expected
        assertEquals(BigDecimal.ZERO, relaxedMySQLResultSet.getBigDecimal(NULL_COL_LABEL));
        assertEquals(new BigDecimal(1.1), relaxedMySQLResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertEquals(BigDecimal.ZERO, relaxedMySQLResultSet.getBigDecimal(STRING_COL_LABEL));
        assertEquals(BigDecimal.ZERO, relaxedMySQLResultSet.getBigDecimal(OBJECTID_COL_LABEL));
        assertEquals(BigDecimal.ONE, relaxedMySQLResultSet.getBigDecimal(BOOLEAN_COL_LABEL));
        assertEquals(
                new BigDecimal(-44364244526000L),
                relaxedMySQLResultSet.getBigDecimal(DATE_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMySQLResultSet.getBigDecimal(INTEGER_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMySQLResultSet.getBigDecimal(LONG_COL_LABEL));
        assertEquals(new BigDecimal(100.0), relaxedMySQLResultSet.getBigDecimal(DECIMAL_COL_LABEL));

        // Test Long values are as expected
        assertEquals(0L, relaxedMySQLResultSet.getLong(NULL_COL_LABEL));
        assertEquals(1L, relaxedMySQLResultSet.getLong(DOUBLE_COL_LABEL));
        assertEquals(0L, relaxedMySQLResultSet.getLong(STRING_COL_LABEL));
        assertEquals(0L, relaxedMySQLResultSet.getLong(OBJECTID_COL_LABEL));
        assertEquals(1L, relaxedMySQLResultSet.getLong(BOOLEAN_COL_LABEL));
        assertEquals(-44364244526000L, relaxedMySQLResultSet.getLong(DATE_COL_LABEL));
        assertEquals(100L, relaxedMySQLResultSet.getLong(INTEGER_COL_LABEL));
        assertEquals(100L, relaxedMySQLResultSet.getLong(LONG_COL_LABEL));
        assertEquals(100L, relaxedMySQLResultSet.getLong(DECIMAL_COL_LABEL));

        // Test Int values are as expected
        assertEquals(0, relaxedMySQLResultSet.getInt(NULL_COL_LABEL));
        assertEquals(1, relaxedMySQLResultSet.getInt(DOUBLE_COL_LABEL));
        assertEquals(0, relaxedMySQLResultSet.getInt(STRING_COL_LABEL));
        assertEquals(0, relaxedMySQLResultSet.getInt(OBJECTID_COL_LABEL));
        assertEquals(1, relaxedMySQLResultSet.getInt(BOOLEAN_COL_LABEL));
        assertEquals(-1527325616, relaxedMySQLResultSet.getInt(DATE_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getInt(INTEGER_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getInt(LONG_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getInt(DECIMAL_COL_LABEL));

        // We test Long, Int, and Byte, we can safely skip getShort tests

        // Test Byte values are as expected
        assertEquals(0, relaxedMySQLResultSet.getByte(NULL_COL_LABEL));
        assertEquals(1, relaxedMySQLResultSet.getByte(DOUBLE_COL_LABEL));
        assertEquals(0, relaxedMySQLResultSet.getByte(STRING_COL_LABEL));
        assertEquals(0, relaxedMySQLResultSet.getByte(OBJECTID_COL_LABEL));
        assertEquals(1, relaxedMySQLResultSet.getByte(BOOLEAN_COL_LABEL));
        // This is weird, but I'm not going to go against Java's casting semantics.
        assertEquals(80, relaxedMySQLResultSet.getByte(DATE_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getByte(INTEGER_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getByte(LONG_COL_LABEL));
        assertEquals(100, relaxedMySQLResultSet.getByte(DECIMAL_COL_LABEL));

        // Test Boolean values are as expected
        assertEquals(false, relaxedMySQLResultSet.getBoolean(NULL_COL_LABEL));
        assertEquals(true, relaxedMySQLResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, relaxedMySQLResultSet.getBoolean(STRING_COL_LABEL));
        assertEquals(false, relaxedMySQLResultSet.getBoolean(OBJECTID_COL_LABEL));
        assertEquals(true, relaxedMySQLResultSet.getBoolean(BOOLEAN_COL_LABEL));
        assertEquals(false, relaxedMySQLResultSet.getBoolean(DATE_COL_LABEL));
        assertEquals(true, relaxedMySQLResultSet.getBoolean(INTEGER_COL_LABEL));
        assertEquals(true, relaxedMySQLResultSet.getBoolean(LONG_COL_LABEL));
        assertEquals(true, relaxedMySQLResultSet.getBoolean(DECIMAL_COL_LABEL));

        // Test getTimestamp
        assertNull(relaxedMySQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMySQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(STRING_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(BOOLEAN_COL_LABEL));
        assertEquals(
                new Timestamp(-44364244526000L),
                relaxedMySQLResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(DECIMAL_COL_LABEL));

        assertNull(relaxedMySQLResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(1), relaxedMySQLResultSet.getTime(DOUBLE_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTime(STRING_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTime(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTime(BOOLEAN_COL_LABEL));
        assertEquals(new Time(-44364244526000L), relaxedMySQLResultSet.getTime(DATE_COL_LABEL));
        assertEquals(new Time(100L), relaxedMySQLResultSet.getTime(INTEGER_COL_LABEL));
        assertEquals(new Time(100L), relaxedMySQLResultSet.getTime(LONG_COL_LABEL));
        assertEquals(new Time(100L), relaxedMySQLResultSet.getTime(DECIMAL_COL_LABEL));

        assertNull(relaxedMySQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(1), relaxedMySQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(STRING_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(OBJECTID_COL_LABEL));
        assertNull(relaxedMySQLResultSet.getTimestamp(BOOLEAN_COL_LABEL));
        assertEquals(
                new Timestamp(-44364244526000L),
                relaxedMySQLResultSet.getTimestamp(DATE_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(INTEGER_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(LONG_COL_LABEL));
        assertEquals(new Timestamp(100L), relaxedMySQLResultSet.getTimestamp(DECIMAL_COL_LABEL));
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            if (nextCalledOnCursor.get()) {
                                return generateRow();
                            }
                            nextCalledOnCursor.set(true);
                            return generateMetadataDoc();
                        });

        mockResultSet = new MySQLResultSet(mongoStatement, cursor, false);

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
        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();

        MySQLResultDoc valuesDoc = new MySQLResultDoc();
        valuesDoc.values = new ArrayList<>();

        MySQLResultDoc valuesDoc2 = new MySQLResultDoc();
        valuesDoc.values = new ArrayList<>();

        MySQLExplicitCursor cursor =
                new MySQLExplicitCursor(Arrays.asList(metaDoc, valuesDoc, valuesDoc2));
        mockResultSet = new MySQLResultSet(mongoStatement, cursor, false);

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

        String colName = "a";

        MySQLResultDoc emptyResultDoc = new MySQLResultDoc();
        emptyResultDoc.columns = new ArrayList<>();
        emptyResultDoc.columns.add(generateCol("myDB", "foo", colName, "string"));

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
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

        mockResultSet = new MySQLResultSet(mongoStatement, cursor, false);

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
        String colName = "a";

        MySQLResultDoc emptyResultDoc = new MySQLResultDoc();
        emptyResultDoc.columns = new ArrayList<>();
        emptyResultDoc.columns.add(generateCol("myDB", "foo", colName, "string"));

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet = new MySQLResultSet(mongoStatement, cursor, false);

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
    void sameMetaDataOnDifferentRowsEvenWithDifferentBsonTypes() throws SQLException {
        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(generateCol("myDB", "foo", "a", "int"));

        MySQLResultDoc row1 = new MySQLResultDoc();
        row1.values = new ArrayList<>();
        row1.values.add(new BsonInt32(1));

        MySQLResultDoc row2 = new MySQLResultDoc();
        row2.values = new ArrayList<>();
        row2.values.add(new BsonString("test"));

        List<MySQLResultDoc> rows = new ArrayList<>();
        rows.add(metaDoc);
        rows.add(row1);
        rows.add(row2);

        Iterator<MySQLResultDoc> iter = rows.iterator();
        when(cursor.hasNext()).thenAnswer(invocation -> iter.hasNext());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            MySQLResultDoc doc = iter.next();
                            return doc;
                        });

        mockResultSet = new MySQLResultSet(mongoStatement, cursor, false);

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
        assertEquals(Types.INTEGER, metaData.getColumnType(1));
    }
}
