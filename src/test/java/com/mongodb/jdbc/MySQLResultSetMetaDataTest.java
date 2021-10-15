package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.bson.BsonType;
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
class MySQLResultSetMetaDataTest extends MySQLMock {
    private static ResultSetMetaData resultSetMetaData;
    private static MySQLStatement mongoStatement;

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

    static {
        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        try {
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "nullCol", "nullCol", "null"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "doubleCol", "doubleCol", "double"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "stringCol", "stringCol", "string"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "binaryCol", "binaryCol", "binData"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "uuidCol", "uuidCol", "binData"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "objectIdCol", "objectIdCol", "objectId"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "booleanCol", "booleanColAlias", "bool"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "dateCol", "dateCol", "date"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "integerCol", "integerCol", "int"));
            metaDoc.columns.add(new MySQLColumnInfo("", "", "", "longCol", "longCol", "long"));
            metaDoc.columns.add(new MySQLColumnInfo("foo", "", "", "decimalCol", "decimalCol", "decimal"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        MySQLResultDoc valuesDoc = new MySQLResultDoc();
        valuesDoc.values = new ArrayList<>();
        valuesDoc.values.add(new BsonNull());
        valuesDoc.values.add(new BsonDouble(1.1));
        valuesDoc.values.add(new BsonString("string data"));
        valuesDoc.values.add(new BsonBinary("data".getBytes()));
        valuesDoc.values.add(new BsonBinary(UUID.randomUUID()));
        valuesDoc.values.add(new BsonObjectId(new ObjectId(new Date())));
        valuesDoc.values.add(new BsonBoolean(true));
        valuesDoc.values.add(new BsonDateTime(1580511155627L));
        valuesDoc.values.add(new BsonInt32(100));
        valuesDoc.values.add(new BsonInt64(100L));
        valuesDoc.values.add(new BsonDecimal128(new Decimal128(100L)));

        List<MySQLResultDoc> mongoResultDocs = new ArrayList<MySQLResultDoc>();
        mongoResultDocs.add(metaDoc);
        mongoResultDocs.add(valuesDoc);

        try {
            mongoStatement = new MySQLStatement(mongoConnection, "test", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MySQLResultSet rs =
                new MySQLResultSet(mongoStatement, new MySQLExplicitCursor(mongoResultDocs), false);

        try {
            rs.next();
            resultSetMetaData = rs.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    MySQLResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(11, MySQLResultSetMetaDataTest.resultSetMetaData.getColumnCount());
    }

    @Test
    void testGetCatalogAndSchemaName() throws SQLException {
        assertEquals("foo", resultSetMetaData.getCatalogName(DECIMAL_COL));
        assertEquals("", resultSetMetaData.getSchemaName(DECIMAL_COL));
    }

    @Test
    void testGetColumnName() throws SQLException {
        assertEquals("nullCol", resultSetMetaData.getColumnName(NULL_COL));
        assertEquals("doubleCol", resultSetMetaData.getColumnName(DOUBLE_COL));
        assertEquals("stringCol", resultSetMetaData.getColumnName(STRING_COL));
        assertEquals("binaryCol", resultSetMetaData.getColumnName(BINARY_COL));
        assertEquals("uuidCol", resultSetMetaData.getColumnName(UUID_COL));
        assertEquals("objectIdCol", resultSetMetaData.getColumnName(OBJECTID_COL));
        assertEquals("booleanCol", resultSetMetaData.getColumnName(BOOLEAN_COL));
        assertEquals("dateCol", resultSetMetaData.getColumnName(DATE_COL));
        assertEquals("integerCol", resultSetMetaData.getColumnName(INTEGER_COL));
        assertEquals("longCol", resultSetMetaData.getColumnName(LONG_COL));
        assertEquals("decimalCol", resultSetMetaData.getColumnName(DECIMAL_COL));
    }

    @Test
    void testGetColumnLabel() throws SQLException {
        assertEquals("nullCol", resultSetMetaData.getColumnLabel(NULL_COL));
        assertEquals("doubleCol", resultSetMetaData.getColumnLabel(DOUBLE_COL));
        assertEquals("stringCol", resultSetMetaData.getColumnLabel(STRING_COL));
        assertEquals("binaryCol", resultSetMetaData.getColumnLabel(BINARY_COL));
        assertEquals("uuidCol", resultSetMetaData.getColumnLabel(UUID_COL));
        assertEquals("objectIdCol", resultSetMetaData.getColumnLabel(OBJECTID_COL));
        assertEquals("booleanColAlias", resultSetMetaData.getColumnLabel(BOOLEAN_COL));
        assertEquals("dateCol", resultSetMetaData.getColumnLabel(DATE_COL));
        assertEquals("integerCol", resultSetMetaData.getColumnLabel(INTEGER_COL));
        assertEquals("longCol", resultSetMetaData.getColumnLabel(LONG_COL));
        assertEquals("decimalCol", resultSetMetaData.getColumnLabel(DECIMAL_COL));
    }

    @Test
    void testIsCaseSensitive() throws SQLException {
        assertEquals(false, resultSetMetaData.isCaseSensitive(NULL_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOUBLE_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(STRING_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(BINARY_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(UUID_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(OBJECTID_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(BOOLEAN_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(DATE_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INTEGER_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(LONG_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(DECIMAL_COL));
    }

    @Test
    void testIsSigned() throws SQLException {
        assertEquals(false, resultSetMetaData.isSigned(NULL_COL));
        assertEquals(true, resultSetMetaData.isSigned(DOUBLE_COL));
        assertEquals(false, resultSetMetaData.isSigned(STRING_COL));
        assertEquals(false, resultSetMetaData.isSigned(BINARY_COL));
        assertEquals(false, resultSetMetaData.isSigned(UUID_COL));
        assertEquals(false, resultSetMetaData.isSigned(OBJECTID_COL));
        assertEquals(false, resultSetMetaData.isSigned(BOOLEAN_COL));
        assertEquals(false, resultSetMetaData.isSigned(DATE_COL));
        assertEquals(true, resultSetMetaData.isSigned(INTEGER_COL));
        assertEquals(true, resultSetMetaData.isSigned(LONG_COL));
        assertEquals(true, resultSetMetaData.isSigned(DECIMAL_COL));
    }

    @Test
    void testGetColumnDisplaySize() throws SQLException {
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(NULL_COL));
        assertEquals(15, resultSetMetaData.getColumnDisplaySize(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(STRING_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(BINARY_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(UUID_COL));
        assertEquals(24, resultSetMetaData.getColumnDisplaySize(OBJECTID_COL));
        assertEquals(1, resultSetMetaData.getColumnDisplaySize(BOOLEAN_COL));
        assertEquals(24, resultSetMetaData.getColumnDisplaySize(DATE_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INTEGER_COL));
        assertEquals(19, resultSetMetaData.getColumnDisplaySize(LONG_COL));
        assertEquals(34, resultSetMetaData.getColumnDisplaySize(DECIMAL_COL));
    }

    @Test
    void testGetPrecision() throws SQLException {
        assertEquals(0, resultSetMetaData.getPrecision(NULL_COL));
        assertEquals(15, resultSetMetaData.getPrecision(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getPrecision(STRING_COL));
        assertEquals(0, resultSetMetaData.getPrecision(BINARY_COL));
        assertEquals(0, resultSetMetaData.getPrecision(UUID_COL));
        assertEquals(24, resultSetMetaData.getPrecision(OBJECTID_COL));
        assertEquals(1, resultSetMetaData.getPrecision(BOOLEAN_COL));
        assertEquals(24, resultSetMetaData.getPrecision(DATE_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INTEGER_COL));
        assertEquals(19, resultSetMetaData.getPrecision(LONG_COL));
        assertEquals(34, resultSetMetaData.getPrecision(DECIMAL_COL));
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
        assertEquals(null, resultSetMetaData.getColumnClassName(NULL_COL));
        assertEquals(double.class.getName(), resultSetMetaData.getColumnClassName(DOUBLE_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(STRING_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(OBJECTID_COL));
        assertEquals(boolean.class.getName(), resultSetMetaData.getColumnClassName(BOOLEAN_COL));
        assertEquals(Timestamp.class.getName(), resultSetMetaData.getColumnClassName(DATE_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INTEGER_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(LONG_COL));
        assertEquals(BigDecimal.class.getName(), resultSetMetaData.getColumnClassName(DECIMAL_COL));
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

    @Test
    void testGetBsonTypeHelper() throws SQLException {
        assertEquals(
                BsonType.ARRAY, MongoColumnInfo.getBsonTypeHelper("array"));
        assertEquals(
                BsonType.BOOLEAN, MongoColumnInfo.getBsonTypeHelper("bool"));
        assertEquals(
                BsonType.BINARY,
                MongoColumnInfo.getBsonTypeHelper("binData"));
        assertEquals(
                BsonType.DATE_TIME,
                MongoColumnInfo.getBsonTypeHelper("date"));
        assertEquals(
                BsonType.DB_POINTER,
                MongoColumnInfo.getBsonTypeHelper("dbPointer"));
        assertEquals(
                BsonType.DECIMAL128,
                MongoColumnInfo.getBsonTypeHelper("decimal"));
        assertEquals(
                BsonType.DOUBLE,
                MongoColumnInfo.getBsonTypeHelper("double"));
        assertEquals(
                BsonType.INT32, MongoColumnInfo.getBsonTypeHelper("int"));
        assertEquals(
                BsonType.JAVASCRIPT,
                MongoColumnInfo.getBsonTypeHelper("javascript"));
        assertEquals(
                BsonType.JAVASCRIPT_WITH_SCOPE,
                MongoColumnInfo.getBsonTypeHelper("javascriptWithScope"));
        assertEquals(
                BsonType.INT64, MongoColumnInfo.getBsonTypeHelper("long"));
        assertEquals(
                BsonType.MAX_KEY,
                MongoColumnInfo.getBsonTypeHelper("maxKey"));
        assertEquals(
                BsonType.MIN_KEY,
                MongoColumnInfo.getBsonTypeHelper("minKey"));
        assertEquals(
                BsonType.NULL, MongoColumnInfo.getBsonTypeHelper("null"));
        assertEquals(
                BsonType.DOCUMENT,
                MongoColumnInfo.getBsonTypeHelper("object"));
        assertEquals(
                BsonType.OBJECT_ID,
                MongoColumnInfo.getBsonTypeHelper("objectId"));
        assertEquals(
                BsonType.REGULAR_EXPRESSION,
                MongoColumnInfo.getBsonTypeHelper("regex"));
        assertEquals(
                BsonType.STRING,
                MongoColumnInfo.getBsonTypeHelper("string"));
        assertEquals(
                BsonType.SYMBOL,
                MongoColumnInfo.getBsonTypeHelper("symbol"));
        assertEquals(
                BsonType.TIMESTAMP,
                MongoColumnInfo.getBsonTypeHelper("timestamp"));
        assertEquals(
                BsonType.UNDEFINED,
                MongoColumnInfo.getBsonTypeHelper("undefined"));
    }
}
