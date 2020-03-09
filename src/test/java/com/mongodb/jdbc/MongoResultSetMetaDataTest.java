package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.sql.Types;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetMetaDataTest {
    private static MongoResultSetMetaData resultSetMetaData;

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
        MongoResultSet rs = new MongoResultSet(null, new MongoTestCursor(rows), false);
        try {
            rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resultSetMetaData = new MongoResultSetMetaData(rs, row);
    }

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
    void testGetPrecision() throws SQLException {
        assertEquals(0, resultSetMetaData.getPrecision(NULL_COL));
        assertEquals(308, resultSetMetaData.getPrecision(DOUBLE_COL));
        assertEquals(11, resultSetMetaData.getPrecision(STRING_COL));
        assertEquals(4, resultSetMetaData.getPrecision(BINARY_COL));
        assertEquals(16, resultSetMetaData.getPrecision(UUID_COL));
        assertEquals(24, resultSetMetaData.getPrecision(OBJECTID_COL));
        assertEquals(1, resultSetMetaData.getPrecision(BOOLEAN_COL));
        assertEquals(24, resultSetMetaData.getPrecision(DATE_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INTEGER_COL));
        assertEquals(19, resultSetMetaData.getPrecision(LONG_COL));
        assertEquals(6145, resultSetMetaData.getPrecision(DECIMAL_COL));
    }

    @Test
    void testGetScale() throws SQLException {
        assertEquals(0, resultSetMetaData.getScale(NULL_COL));
        assertEquals(15, resultSetMetaData.getScale(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getScale(STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(BINARY_COL));
        assertEquals(0, resultSetMetaData.getScale(UUID_COL));
        assertEquals(0, resultSetMetaData.getScale(OBJECTID_COL));
        assertEquals(0, resultSetMetaData.getScale(BOOLEAN_COL));
        assertEquals(0, resultSetMetaData.getScale(DATE_COL));
        assertEquals(0, resultSetMetaData.getScale(INTEGER_COL));
        assertEquals(0, resultSetMetaData.getScale(LONG_COL));
        assertEquals(34, resultSetMetaData.getScale(DECIMAL_COL));
    }

    @Test
    void testGetColumnType() throws SQLException {
        assertEquals(Types.NULL, resultSetMetaData.getColumnType(NULL_COL));
        assertEquals(Types.DOUBLE, resultSetMetaData.getColumnType(DOUBLE_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(STRING_COL));
        assertEquals(Types.BLOB, resultSetMetaData.getColumnType(BINARY_COL));
        assertEquals(Types.BLOB, resultSetMetaData.getColumnType(UUID_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(OBJECTID_COL));
        assertEquals(Types.BIT, resultSetMetaData.getColumnType(BOOLEAN_COL));
        assertEquals(Types.TIMESTAMP, resultSetMetaData.getColumnType(DATE_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INTEGER_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(LONG_COL));
        assertEquals(Types.DECIMAL, resultSetMetaData.getColumnType(DECIMAL_COL));
    }

    @Test
    void testGetColumnTypeClassName() throws SQLException {
        assertEquals(BsonNull.class.getName(), resultSetMetaData.getColumnClassName(NULL_COL));
        assertEquals(BsonDouble.class.getName(), resultSetMetaData.getColumnClassName(DOUBLE_COL));
        assertEquals(BsonString.class.getName(), resultSetMetaData.getColumnClassName(STRING_COL));
        assertEquals(BsonBinary.class.getName(), resultSetMetaData.getColumnClassName(BINARY_COL));
        assertEquals(BsonBinary.class.getName(), resultSetMetaData.getColumnClassName(UUID_COL));
        assertEquals(
                BsonObjectId.class.getName(), resultSetMetaData.getColumnClassName(OBJECTID_COL));
        assertEquals(
                BsonBoolean.class.getName(), resultSetMetaData.getColumnClassName(BOOLEAN_COL));
        assertEquals(BsonDateTime.class.getName(), resultSetMetaData.getColumnClassName(DATE_COL));
        assertEquals(BsonInt32.class.getName(), resultSetMetaData.getColumnClassName(INTEGER_COL));
        assertEquals(BsonInt64.class.getName(), resultSetMetaData.getColumnClassName(LONG_COL));
        assertEquals(
                BsonDecimal128.class.getName(), resultSetMetaData.getColumnClassName(DECIMAL_COL));
    }

    @Test
    void testGetColumnTypeName() throws SQLException {
        assertEquals("null", resultSetMetaData.getColumnTypeName(NULL_COL));
        assertEquals("double", resultSetMetaData.getColumnTypeName(DOUBLE_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(STRING_COL));
        assertEquals("binData", resultSetMetaData.getColumnTypeName(UUID_COL));
        assertEquals("binData", resultSetMetaData.getColumnTypeName(BINARY_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(OBJECTID_COL));
        assertEquals("bool", resultSetMetaData.getColumnTypeName(BOOLEAN_COL));
        assertEquals("date", resultSetMetaData.getColumnTypeName(DATE_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INTEGER_COL));
        assertEquals("long", resultSetMetaData.getColumnTypeName(LONG_COL));
        assertEquals("decimal", resultSetMetaData.getColumnTypeName(DECIMAL_COL));
    }
}
