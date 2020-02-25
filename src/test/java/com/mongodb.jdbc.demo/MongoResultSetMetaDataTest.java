package com.mongodb.jdbc.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.Column;
import com.mongodb.jdbc.MongoResultSet;
import com.mongodb.jdbc.MongoResultSetMetaData;
import com.mongodb.jdbc.Row;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
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

    //    public String database;
    //    public String table;
    //    public String tableAlias;
    //    public String column;
    //    public String columnAlias;
    //    public BsonValue value;
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

        resultSetMetaData = new MongoResultSetMetaData(row);
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
        assertEquals(Types.REAL, resultSetMetaData.getColumnType(DECIMAL_COL));
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
