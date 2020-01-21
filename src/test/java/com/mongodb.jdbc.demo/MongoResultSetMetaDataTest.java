package com.mongodb.jdbc.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.MongoResultSet;
import com.mongodb.jdbc.MongoResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.UUID;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetMetaDataTest {
    private static MongoResultSetMetaData resultSetMetaData;

    enum COL_IDX {
        NULL_COL,
        DOUBLE_COL,
        STRING_COL,
        BINARY_COL,
        UUID_COL,
        OBJECTID_COL,
        BOOLEAN_COL,
        DATE_COL,
        INTEGER_COL,
        LONG_COL,
        DECIMAL_COL
    }

    static {
        Document doc = new Document();
        doc.append("nullCol", null);
        doc.append("doubleCol", 1.1);
        doc.append("stringCol", "string data");
        doc.append("binaryCol", new Binary("data".getBytes()));
        doc.append("uuidCol", UUID.randomUUID());
        doc.append("objectIdCol", new ObjectId(new Date()));
        doc.append("booleanCol", true);
        doc.append("dateCol", new Date());
        doc.append("integerCol", 100);
        doc.append("longCol", 100L);
        doc.append("decimalCol", new Decimal128(100));

        resultSetMetaData = new MongoResultSetMetaData(doc);
    }

    static final String CURR_DOC = "currDoc";
    static final String NEXT_DOC = "nextDoc";

    MongoResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(11, MongoResultSetMetaDataTest.resultSetMetaData.getColumnCount());
    }

    @Test
    void testGetColumnType() throws SQLException {
        assertEquals(Types.NULL, resultSetMetaData.getColumnType(COL_IDX.NULL_COL.ordinal()));
        assertEquals(Types.DOUBLE, resultSetMetaData.getColumnType(COL_IDX.DOUBLE_COL.ordinal()));
        assertEquals(
                Types.LONGVARCHAR, resultSetMetaData.getColumnType(COL_IDX.STRING_COL.ordinal()));
        assertEquals(Types.BLOB, resultSetMetaData.getColumnType(COL_IDX.BINARY_COL.ordinal()));
        assertEquals(Types.BLOB, resultSetMetaData.getColumnType(COL_IDX.UUID_COL.ordinal()));
        assertEquals(
                Types.LONGVARCHAR, resultSetMetaData.getColumnType(COL_IDX.OBJECTID_COL.ordinal()));
        assertEquals(Types.BIT, resultSetMetaData.getColumnType(COL_IDX.BOOLEAN_COL.ordinal()));
        assertEquals(Types.TIMESTAMP, resultSetMetaData.getColumnType(COL_IDX.DATE_COL.ordinal()));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(COL_IDX.INTEGER_COL.ordinal()));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(COL_IDX.LONG_COL.ordinal()));
        assertEquals(Types.DECIMAL, resultSetMetaData.getColumnType(COL_IDX.DECIMAL_COL.ordinal()));
    }

    @Test
    void testGetColumnTypeClassName() throws SQLException {
        assertEquals(
                "java.lang.Object",
                resultSetMetaData.getColumnClassName(COL_IDX.NULL_COL.ordinal()));
        assertEquals(
                Double.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.DOUBLE_COL.ordinal()));
        assertEquals(
                String.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.STRING_COL.ordinal()));
        assertEquals(
                Binary.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.BINARY_COL.ordinal()));
        assertEquals(
                UUID.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.UUID_COL.ordinal()));
        assertEquals(
                ObjectId.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.OBJECTID_COL.ordinal()));
        assertEquals(
                Boolean.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.BOOLEAN_COL.ordinal()));
        assertEquals(
                Date.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.DATE_COL.ordinal()));
        assertEquals(
                Integer.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.INTEGER_COL.ordinal()));
        assertEquals(
                Long.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.LONG_COL.ordinal()));
        assertEquals(
                Decimal128.class.getName(),
                resultSetMetaData.getColumnClassName(COL_IDX.DECIMAL_COL.ordinal()));
    }

    @Test
    void testGetColumnTypeName() throws SQLException {
        assertEquals("null", resultSetMetaData.getColumnTypeName(COL_IDX.NULL_COL.ordinal()));
        assertEquals("double", resultSetMetaData.getColumnTypeName(COL_IDX.DOUBLE_COL.ordinal()));
        assertEquals("string", resultSetMetaData.getColumnTypeName(COL_IDX.STRING_COL.ordinal()));
        assertEquals("binData", resultSetMetaData.getColumnTypeName(COL_IDX.UUID_COL.ordinal()));
        assertEquals("binData", resultSetMetaData.getColumnTypeName(COL_IDX.BINARY_COL.ordinal()));
        assertEquals(
                "objectId", resultSetMetaData.getColumnTypeName(COL_IDX.OBJECTID_COL.ordinal()));
        assertEquals("bool", resultSetMetaData.getColumnTypeName(COL_IDX.BOOLEAN_COL.ordinal()));
        assertEquals("date", resultSetMetaData.getColumnTypeName(COL_IDX.DATE_COL.ordinal()));
        assertEquals("int", resultSetMetaData.getColumnTypeName(COL_IDX.INTEGER_COL.ordinal()));
        assertEquals("long", resultSetMetaData.getColumnTypeName(COL_IDX.LONG_COL.ordinal()));
        assertEquals("decimal", resultSetMetaData.getColumnTypeName(COL_IDX.DECIMAL_COL.ordinal()));
    }
}
