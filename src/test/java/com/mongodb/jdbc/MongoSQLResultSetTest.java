package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonUndefined;
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
class MongoSQLResultSetTest extends MongoSQLMock {
    @Mock MongoCursor<BsonDocument> cursor;
    MongoSQLResultSet mockResultSet;
    static MongoSQLResultSet mongoSQLResultSet;
    static MongoSQLResultSet mongoSQLResultSetAllTypes;
    static MongoSQLResultSet closedMongoSQLResultSet;

    private static MongoSQLResultSetMetaData resultSetMetaData;
    private static MongoSQLStatement mongoStatement;
    private static MongoJsonSchema schema;

    static {
        try {
            schema = generateMongoJsonSchema();
            resultSetMetaData =
                    new MongoSQLResultSetMetaData(schema, true, mongoConnection.getLogger(), 0);
            mongoStatement = new MongoSQLStatement(mongoConnection, "test");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        BsonDocument document = new BsonDocument();

        BsonDocument bot = new BsonDocument();
        BsonDocument foo = new BsonDocument();
        document.put("", bot);
        document.put("foo", foo);

        bot.put(DOUBLE_COL_LABEL, new BsonDouble(2.4));
        byte binary[] = {10, 20, 30};
        bot.put(BINARY_COL_LABEL, new BsonBinary(binary));
        bot.put(STRING_COL_LABEL, new BsonString("b"));

        foo.put(ANY_OF_INT_STRING_COL_LABEL, new BsonInt32(3));
        foo.put(INT_NULLABLE_COL_LABEL, new BsonNull());
        foo.put(INT_COL_LABEL, new BsonInt32(4));
        foo.put(ANY_COL_LABEL, new BsonUndefined());
        foo.put(NULL_COL_LABEL, new BsonNull());

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(5));
        array.add(new BsonInt32(6));
        array.add(new BsonInt32(7));
        foo.put(ARRAY_COL_LABEL, array);

        BsonDocument fooSubDoc = new BsonDocument();
        fooSubDoc.put(INT_COL_LABEL, new BsonInt32(5));
        foo.put(DOC_COL_LABEL, fooSubDoc);

        List<BsonDocument> mongoResultDocs = new ArrayList<BsonDocument>();
        mongoResultDocs.add(document);

        // All types result set
        BsonDocument docAllTypes = generateRowAllTypes();
        List<BsonDocument> mongoResultDocsAllTypes = new ArrayList<BsonDocument>();
        mongoResultDocsAllTypes.add(docAllTypes);

        MongoJsonSchema schemaAllTypes = generateMongoJsonSchemaAllTypes();

        try {
            mongoSQLResultSet =
                    new MongoSQLResultSet(
                            mongoStatement, new BsonExplicitCursor(mongoResultDocs), schema);
            mongoSQLResultSetAllTypes =
                    new MongoSQLResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocsAllTypes),
                            schemaAllTypes);
            closedMongoSQLResultSet =
                    new MongoSQLResultSet(
                            mongoStatement, new BsonExplicitCursor(mongoResultDocs), schema);
            mongoSQLResultSet.next();
            mongoSQLResultSetAllTypes.next();
            closedMongoSQLResultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setup() throws NoSuchFieldException {
        MongoSQLMock.resetMockObjs();
    }

    @Test
    void testBinaryGetters() throws Exception {
        // Binary cannot be gotten through anything other than getBlob and getBinaryStream, currently.
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getString(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBoolean(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getLong(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getDouble(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBigDecimal(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getTimestamp(BINARY_COL);
                });

        // Test wasNull.
        mongoSQLResultSet.getString(INT_OR_NULL_COL);
        assertTrue(mongoSQLResultSet.wasNull());
        mongoSQLResultSet.getString(DOUBLE_COL);
        assertFalse(mongoSQLResultSet.wasNull());

        mongoSQLResultSet.getString(ANY_COL);
        assertTrue(mongoSQLResultSet.wasNull());

        // Only Binary and null values can be gotten from getBlob
        assertNotNull(mongoSQLResultSet.getBlob(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBlob(STRING_COL_LABEL);
                });
        assertNull(mongoSQLResultSet.getBlob(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBlob(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBlob(ARRAY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBlob(DOC_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBlob(INT_COL);
                });

        // Only Binary and null values can be gotten from getBinaryStream
        assertNotNull(mongoSQLResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(STRING_COL_LABEL);
                });

        assertNull(mongoSQLResultSet.getBinaryStream(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(STRING_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(INT_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(ARRAY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBinaryStream(DOC_COL);
                });

        // Only Binary and null values can be gotten from getBytes
        assertNotNull(mongoSQLResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBytes(STRING_COL_LABEL);
                });
        assertNull(mongoSQLResultSet.getBytes(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBytes(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBytes(INT_COL);
                });
    }

    @Test
    void testGetColumnByIdAndName() throws Exception {
        assertEquals(DOUBLE_COL, mongoSQLResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL, mongoSQLResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(INT_OR_NULL_COL, mongoSQLResultSet.findColumn(INT_NULLABLE_COL_LABEL));
        assertEquals(INT_COL, mongoSQLResultSet.findColumn(INT_COL_LABEL));
        assertEquals(ANY_COL, mongoSQLResultSet.findColumn(ANY_COL_LABEL));
        assertEquals(ARRAY_COL, mongoSQLResultSet.findColumn(ARRAY_COL_LABEL));
        assertEquals(DOC_COL, mongoSQLResultSet.findColumn(DOC_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                mongoSQLResultSet.getString(DOUBLE_COL),
                mongoSQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getString(STRING_COL),
                mongoSQLResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getString(INT_OR_NULL_COL),
                mongoSQLResultSet.getString(INT_NULLABLE_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getString(INT_COL), mongoSQLResultSet.getString(INT_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getString(ANY_COL), mongoSQLResultSet.getString(ANY_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getBytes(BINARY_COL),
                mongoSQLResultSet.getBytes(BINARY_COL_LABEL));
    }

    @Test
    void testGetStringValues() throws Exception {
        // DOUBLE_COL              2.4
        // STRING_COL              "b"
        // BINARY_COL              [10, 20, 30]
        // ANY_OF_INT_STRING_COL   3
        // INT_OR_NULL_COL         null
        // NULL_COL                null
        // INT_COL                 4
        // ANY_COL                 "{}"
        // ARRAY_COL               [5, 6, 7]
        // DOC_COL                 {"c": 5}

        //Test String values are as expected
        assertEquals("2.4", mongoSQLResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("b", mongoSQLResultSet.getString(STRING_COL_LABEL));
        assertNull(mongoSQLResultSet.getString(INT_NULLABLE_COL_LABEL));
        assertNull(mongoSQLResultSet.getString(NULL_COL));
        assertEquals("4", mongoSQLResultSet.getString(INT_COL_LABEL));
        assertNull(mongoSQLResultSet.getString(ANY_COL_LABEL));
        assertEquals("[5, 6, 7]", mongoSQLResultSet.getString(ARRAY_COL_LABEL));
        assertEquals("{\"c\": 5}", mongoSQLResultSet.getString(DOC_COL_LABEL));

        // Check getAsciiStream and getUnicodeStream output are non-null.
        assertNotNull(mongoSQLResultSet.getAsciiStream(STRING_COL_LABEL));
        assertNotNull(mongoSQLResultSet.getUnicodeStream(STRING_COL_LABEL));

        // Actually check getAsciiStream and getUnicodeStream output. We just check
        // that the length is what is expected.
        assertEquals(
                1, mongoSQLResultSet.getAsciiStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
        assertEquals(
                1,
                mongoSQLResultSet.getUnicodeStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
    }

    @Test
    public void testGetStringAllTypes() throws Exception {
        // non-null types
        assertEquals(ALL_DOUBLE_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_DOUBLE_COL_LABEL));
        assertEquals(ALL_OBJECT_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_OBJECT_COL_LABEL));
        assertEquals(ALL_ARRAY_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_ARRAY_COL_LABEL));
        assertEquals(ALL_BINARY_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_BINARY_COL_LABEL));
        assertEquals(
                ALL_OBJECT_ID_COL_VAL,
                mongoSQLResultSetAllTypes.getString(ALL_OBJECT_ID_COL_LABEL));
        assertEquals(ALL_BOOL_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_BOOL_COL_LABEL));
        assertEquals(ALL_DATE_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_DATE_COL_LABEL));
        assertEquals(ALL_REGEX_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_REGEX_COL_LABEL));
        assertEquals(
                ALL_JAVASCRIPT_COL_VAL,
                mongoSQLResultSetAllTypes.getString(ALL_JAVASCRIPT_COL_LABEL));
        assertEquals(ALL_SYMBOL_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_SYMBOL_COL_LABEL));
        assertEquals(
                ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL,
                mongoSQLResultSetAllTypes.getString(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL));
        assertEquals(ALL_INT_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_INT_COL_LABEL));
        assertEquals(
                ALL_TIMESTAMP_COL_VAL,
                mongoSQLResultSetAllTypes.getString(ALL_TIMESTAMP_COL_LABEL));
        assertEquals(
                ALL_DECIMAL_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_DECIMAL_COL_LABEL));
        assertEquals(
                ALL_MIN_KEY_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_MIN_KEY_COL_LABEL));
        assertEquals(
                ALL_MAX_KEY_COL_VAL, mongoSQLResultSetAllTypes.getString(ALL_MAX_KEY_COL_LABEL));

        // Note that for "string" and "long", the ALL_X_COL_VAL must use extended json style-syntax in the
        // Mock class, but the output for these types is not extended json style, by design.
        assertEquals("str", mongoSQLResultSetAllTypes.getString(ALL_STRING_COL_LABEL));
        assertEquals("5", mongoSQLResultSetAllTypes.getString(ALL_LONG_COL_LABEL));

        // Note that the Java driver still outputs the legacy representation for DBPointer, as
        // opposed to the new standard representation: { $dbPointer: { $ref: <namespace>, $id: <oid> } }.
        // This is sufficient for our purposes, though.
        assertEquals(
                "{\"$ref\": \"db2\", \"$id\": " + ALL_OBJECT_ID_COL_VAL + "}",
                mongoSQLResultSetAllTypes.getString(ALL_DB_POINTER_COL_LABEL));

        // Note that getString() returns null for NULL and UNDEFINED BSON values
        assertNull(mongoSQLResultSetAllTypes.getString(ALL_UNDEFINED_COL_LABEL));
        assertNull(mongoSQLResultSetAllTypes.getString(ALL_NULL_COL_LABEL));
    }

    @Test
    public void testGetObjectToStringAllTypes() throws Exception {
        // Test getObject().toString() result for each BSON type
        assertEquals(
                ALL_DOUBLE_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_DOUBLE_COL_LABEL).toString());
        assertEquals(
                ALL_OBJECT_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_OBJECT_COL_LABEL).toString());
        assertEquals(
                ALL_ARRAY_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_ARRAY_COL_LABEL).toString());
        assertEquals(
                ALL_BINARY_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_BINARY_COL_LABEL).toString());
        assertEquals(
                ALL_OBJECT_ID_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_OBJECT_ID_COL_LABEL).toString());
        assertEquals(
                ALL_BOOL_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_BOOL_COL_LABEL).toString());
        assertEquals(
                ALL_DATE_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_DATE_COL_LABEL).toString());
        assertEquals(
                ALL_REGEX_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_REGEX_COL_LABEL).toString());
        assertEquals(
                ALL_JAVASCRIPT_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_JAVASCRIPT_COL_LABEL).toString());
        assertEquals(
                ALL_SYMBOL_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_SYMBOL_COL_LABEL).toString());
        assertEquals(
                ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL,
                mongoSQLResultSetAllTypes
                        .getObject(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL)
                        .toString());
        assertEquals(
                ALL_INT_COL_VAL, mongoSQLResultSetAllTypes.getObject(ALL_INT_COL_LABEL).toString());
        assertEquals(
                ALL_TIMESTAMP_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_TIMESTAMP_COL_LABEL).toString());
        assertEquals(
                ALL_DECIMAL_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_DECIMAL_COL_LABEL).toString());
        assertEquals(
                ALL_MIN_KEY_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_MIN_KEY_COL_LABEL).toString());
        assertEquals(
                ALL_MAX_KEY_COL_VAL,
                mongoSQLResultSetAllTypes.getObject(ALL_MAX_KEY_COL_LABEL).toString());

        // Note that for "string" and "long", the ALL_X_COL_VAL must use extended json style-syntax in the
        // Mock class, but the output for these types is not extended json style, by design.
        assertEquals("str", mongoSQLResultSetAllTypes.getObject(ALL_STRING_COL_LABEL).toString());
        assertEquals("5", mongoSQLResultSetAllTypes.getObject(ALL_LONG_COL_LABEL).toString());

        // Note that the Java driver still outputs the legacy representation for DBPointer, as
        // opposed to the new standard representation: { $dbPointer: { $ref: <namespace>, $id: <oid> } }.
        // This is sufficient for our purposes, though.
        assertEquals(
                "{\"$ref\": \"db2\", \"$id\": " + ALL_OBJECT_ID_COL_VAL + "}",
                mongoSQLResultSetAllTypes.getObject(ALL_DB_POINTER_COL_LABEL).toString());

        // Note that getObject() returns null for NULL and UNDEFINED BSON values, so we check
        // manually that their stringification returns what is expected.
        assertNull(mongoSQLResultSetAllTypes.getObject(ALL_UNDEFINED_COL_LABEL));
        assertNull(new MongoSQLValue(new BsonUndefined()).toString());
        assertNull(mongoSQLResultSetAllTypes.getObject(ALL_NULL_COL_LABEL));
        assertNull(new MongoSQLValue(new BsonNull()).toString());
    }

    @Test
    public void testGetObjectToStringMatchesGetString() throws Exception {
        // Null types omitted
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_DOUBLE_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_DOUBLE_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_STRING_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_STRING_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_OBJECT_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_OBJECT_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_ARRAY_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_ARRAY_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_BINARY_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_BINARY_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_OBJECT_ID_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_OBJECT_ID_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_BOOL_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_BOOL_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_DATE_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_DATE_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_REGEX_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_REGEX_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_DB_POINTER_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_DB_POINTER_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_JAVASCRIPT_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_JAVASCRIPT_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_SYMBOL_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_SYMBOL_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL),
                mongoSQLResultSetAllTypes
                        .getObject(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL)
                        .toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_INT_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_INT_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_TIMESTAMP_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_TIMESTAMP_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_LONG_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_LONG_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_DECIMAL_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_DECIMAL_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_MIN_KEY_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_MIN_KEY_COL_LABEL).toString());
        assertEquals(
                mongoSQLResultSetAllTypes.getString(ALL_MAX_KEY_COL_LABEL),
                mongoSQLResultSetAllTypes.getObject(ALL_MAX_KEY_COL_LABEL).toString());
    }

    @Test
    void testGetArithmeticValues() throws Exception {
        // Test Double values are as expected
        assertEquals(0.0, mongoSQLResultSet.getDouble(INT_NULLABLE_COL_LABEL));
        assertEquals(2.4, mongoSQLResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getDouble(STRING_COL_LABEL);
                });
        assertEquals(4.0, mongoSQLResultSet.getDouble(INT_COL_LABEL));
        assertEquals(3.0, mongoSQLResultSet.getDouble(ANY_OF_INT_STRING_COL));

        // Test BigDecimal values are as expected
        assertEquals(BigDecimal.ZERO, mongoSQLResultSet.getBigDecimal(INT_NULLABLE_COL_LABEL));
        assertEquals(new BigDecimal(2.4), mongoSQLResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertEquals(new BigDecimal(4.0), mongoSQLResultSet.getBigDecimal(INT_COL_LABEL));
        assertEquals(new BigDecimal(3.0), mongoSQLResultSet.getBigDecimal(ANY_OF_INT_STRING_COL));

        // Test Long values are as expected
        assertEquals(0L, mongoSQLResultSet.getLong(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoSQLResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getLong(STRING_COL_LABEL);
                });
        assertEquals(4L, mongoSQLResultSet.getLong(INT_COL_LABEL));
        assertEquals(3L, mongoSQLResultSet.getLong(ANY_OF_INT_STRING_COL));

        // Test Int values are as expected
        assertEquals(0, mongoSQLResultSet.getInt(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoSQLResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getInt(STRING_COL_LABEL);
                });
        assertEquals(4, mongoSQLResultSet.getInt(INT_COL_LABEL));
        assertEquals(3, mongoSQLResultSet.getInt(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetByteValues() throws Exception {
        // Test Byte values are as expected
        assertEquals(0, mongoSQLResultSet.getByte(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoSQLResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getByte(STRING_COL_LABEL);
                });

        assertEquals(4, mongoSQLResultSet.getByte(INT_COL_LABEL));
        assertEquals(3, mongoSQLResultSet.getByte(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetBooleanValues() throws Exception {
        // Test Boolean values are as expected
        assertEquals(false, mongoSQLResultSet.getBoolean(INT_NULLABLE_COL_LABEL));
        assertEquals(true, mongoSQLResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, mongoSQLResultSet.getBoolean(STRING_COL_LABEL));
        assertEquals(true, mongoSQLResultSet.getBoolean(INT_COL_LABEL));
        assertEquals(true, mongoSQLResultSet.getBoolean(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetTimestampValues() throws Exception {

        assertNull(mongoSQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(2), mongoSQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(new Timestamp(4L), mongoSQLResultSet.getTimestamp(INT_COL_LABEL));
        assertEquals(new Timestamp(3L), mongoSQLResultSet.getTimestamp(ANY_OF_INT_STRING_COL));

        assertNull(mongoSQLResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(2), mongoSQLResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getTime(STRING_COL_LABEL);
                });
        assertEquals(new Time(4L), mongoSQLResultSet.getTime(INT_COL_LABEL));
        assertEquals(new Time(3L), mongoSQLResultSet.getTime(ANY_OF_INT_STRING_COL));

        assertNull(mongoSQLResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(2), mongoSQLResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoSQLResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(new Timestamp(4L), mongoSQLResultSet.getTimestamp(INT_COL_LABEL));
        assertEquals(new Timestamp(3L), mongoSQLResultSet.getTimestamp(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetObject() throws Exception {
        // test that the index and label versions of getObject have matching results
        assertEquals(
                mongoSQLResultSet.getObject(DOUBLE_COL),
                mongoSQLResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(STRING_COL),
                mongoSQLResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(INT_OR_NULL_COL),
                mongoSQLResultSet.getObject(INT_NULLABLE_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(INT_COL), mongoSQLResultSet.getObject(INT_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(ANY_COL), mongoSQLResultSet.getObject(ANY_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(ARRAY_COL),
                mongoSQLResultSet.getObject(ARRAY_COL_LABEL));
        assertEquals(
                mongoSQLResultSet.getObject(DOC_COL), mongoSQLResultSet.getObject(DOC_COL_LABEL));

        // test that getObject returns the expected java object for each bson type
        assertNull(mongoSQLResultSet.getObject(NULL_COL_LABEL));
        assertEquals(2.4, mongoSQLResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals("b", mongoSQLResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                new MongoSQLValue(new BsonInt32(3)),
                mongoSQLResultSet.getObject(ANY_OF_INT_STRING_COL));

        assertNull(mongoSQLResultSet.getObject(NULL_COL));
        assertEquals(4, mongoSQLResultSet.getObject(INT_COL_LABEL));

        assertNull(mongoSQLResultSet.getObject(ANY_COL_LABEL));

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(5));
        array.add(new BsonInt32(6));
        array.add(new BsonInt32(7));
        assertEquals(new MongoSQLValue(array), mongoSQLResultSet.getObject(ARRAY_COL_LABEL));

        BsonDocument doc = new BsonDocument();
        doc.put(INT_COL_LABEL, new BsonInt32(5));
        assertEquals(new MongoSQLValue(doc), mongoSQLResultSet.getObject(DOC_COL_LABEL));

        byte binary[] = {10, 20, 30};
        assertEquals(new BsonBinary(binary), mongoSQLResultSet.getObject(BINARY_COL_LABEL));
    }

    @SuppressWarnings("deprecation")
    @Test
    void closedResultSets() throws Exception {
        try {
            closedMongoSQLResultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertTrue(closedMongoSQLResultSet.isClosed());

        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.next();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.wasNull();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBytes(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBytes("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getAsciiStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getAsciiStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getUnicodeStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getUnicodeStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBinaryStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBinaryStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBoolean(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBoolean("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getByte(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getByte("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getShort(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getShort("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getInt(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getInt("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getLong(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getLong("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getFloat(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getFloat("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDouble(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDouble("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBigDecimal("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.clearWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getCursorName();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getMetaData();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.findColumn("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getCharacterStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.isFirst();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.isLast();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.previous();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getFetchDirection();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getType();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getConcurrency();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.rowUpdated();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.rowInserted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.rowDeleted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.insertRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.deleteRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.refreshRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getStatement();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBlob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getBlob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getTime(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getTime("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getTimestamp(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getTimestamp("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getHoldability();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoSQLResultSet.getNCharacterStream("f");
                });
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            if (nextCalledOnCursor.get()) {
                                return generateRow();
                            }
                            nextCalledOnCursor.set(true);
                            return generateRow();
                        });

        mockResultSet = new MongoSQLResultSet(mongoStatement, cursor, schema);

        boolean hasNext = mockResultSet.next();
        assertFalse(hasNext);
        assertNull(mockResultSet.getCurrent());
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
    }

    @Test
    void returnNextRowWhenAvailable() throws Exception {
        BsonDocument valuesDoc = new BsonDocument();
        BsonDocument valuesDoc2 = new BsonDocument();
        BsonDocument valuesDoc3 = new BsonDocument();

        BsonExplicitCursor cursor =
                new BsonExplicitCursor(Arrays.asList(valuesDoc, valuesDoc2, valuesDoc3));
        mockResultSet = new MongoSQLResultSet(mongoStatement, cursor, schema);

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

        BsonDocument emptyResultDoc = new BsonDocument();

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
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

        mockResultSet = new MongoSQLResultSet(mongoStatement, cursor, schema);

        assertFalse(mockResultSet.isFirst());
        // For empty result set, isLast should always be true
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // For empty result set, isFirst should always be false
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertEquals(10, mockResultSet.getMetaData().getColumnCount());
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

        BsonDocument emptyResultDoc = new BsonDocument();

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet = new MongoSQLResultSet(mongoStatement, cursor, schema);

        assertEquals(10, mockResultSet.getMetaData().getColumnCount());
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

        MongoJsonSchema sameMetadataSchema = new MongoJsonSchema();
        sameMetadataSchema.bsonType = "object";
        sameMetadataSchema.required = new HashSet<String>();
        sameMetadataSchema.required.add("foo");

        MongoJsonSchema fooSchema = new MongoJsonSchema();
        fooSchema.bsonType = "object";
        fooSchema.required = new HashSet<String>();
        fooSchema.required.add("a");

        MongoJsonSchema aSchema = new MongoJsonSchema();
        aSchema.bsonType = "int";

        fooSchema.properties = new HashMap<String, MongoJsonSchema>();
        fooSchema.properties.put("a", aSchema);

        sameMetadataSchema.properties = new HashMap<String, MongoJsonSchema>();
        sameMetadataSchema.properties.put("foo", fooSchema);

        BsonDocument row1 = new BsonDocument();
        row1.append("foo.a", new BsonInt32(1));

        BsonDocument row2 = new BsonDocument();
        row1.append("foo.a", new BsonString("test"));

        List<BsonDocument> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        Iterator<BsonDocument> iter = rows.iterator();
        when(cursor.hasNext()).thenAnswer(invocation -> iter.hasNext());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            BsonDocument doc = iter.next();
                            return doc;
                        });

        mockResultSet = new MongoSQLResultSet(mongoStatement, cursor, sameMetadataSchema);

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
