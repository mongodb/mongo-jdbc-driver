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
class MongoSQLResultSetMetaDataTest extends MongoSQLMock {
    private static ResultSetMetaData resultSetMetaData;

    // __bot.a
    private static int DOUBLE_COL = 1;
    // __bot.str
    private static int STRING_COL = 2;
    // foo.a
    private static int ANY_OF_INT_STRING_COL = 3;
    // foo.b
    private static int INT_NULLABLE_COL = 4;
    // foo.c
    private static int INT_COL = 5;
    // foo.d
    private static int ANY_COL = 6;

    static {
        resultSetMetaData = new MongoSQLResultSetMetaData(generateMongoJsonSchema());
    }

    MongoResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(5, MongoSQLResultSetMetaDataTest.resultSetMetaData.getColumnCount());
    }

    @Test
    void testGetCatalogAndSchemaName() throws SQLException {
        assertEquals("", resultSetMetaData.getCatalogName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getSchemaName(DOUBLE_COL));
    }

    @Test
    void testGetColumnName() throws SQLException {
        assertEquals("a", resultSetMetaData.getColumnName(DOUBLE_COL));
        assertEquals("str", resultSetMetaData.getColumnName(STRING_COL));
        assertEquals("a", resultSetMetaData.getColumnName(ANY_OF_INT_STRING_COL));
        assertEquals("b", resultSetMetaData.getColumnName(INT_NULLABLE_COL));
        assertEquals("c", resultSetMetaData.getColumnName(INT_COL));
        assertEquals("d", resultSetMetaData.getColumnName(ANY_COL));
    }

    @Test
    void testGetColumnLabel() throws SQLException {
        assertEquals("a", resultSetMetaData.getColumnLabel(DOUBLE_COL));
        assertEquals("str", resultSetMetaData.getColumnLabel(STRING_COL));
        assertEquals("a", resultSetMetaData.getColumnLabel(ANY_OF_INT_STRING_COL));
        assertEquals("b", resultSetMetaData.getColumnLabel(INT_NULLABLE_COL));
        assertEquals("c", resultSetMetaData.getColumnLabel(INT_COL));
        assertEquals("d", resultSetMetaData.getColumnLabel(ANY_COL));
    }

    @Test
    void testIsCaseSensitive() throws SQLException {
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOUBLE_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(STRING_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_OF_INT_STRING_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_NULLABLE_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_COL));
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
    void testGetExtendedBsonTypeHelper() throws SQLException {
        assertEquals(ExtendedBsonType.ARRAY, MongoResultSetMetaData.getExtendedBsonTypeHelper("array"));
        assertEquals(ExtendedBsonType.BOOLEAN, MongoResultSetMetaData.getExtendedBsonTypeHelper("bool"));
        assertEquals(ExtendedBsonType.BINARY, MongoResultSetMetaData.getExtendedBsonTypeHelper("binData"));
        assertEquals(ExtendedBsonType.DATE_TIME, MongoResultSetMetaData.getExtendedBsonTypeHelper("date"));
        assertEquals(ExtendedBsonType.DB_POINTER, MongoResultSetMetaData.getExtendedBsonTypeHelper("dbPointer"));
        assertEquals(ExtendedBsonType.DECIMAL128, MongoResultSetMetaData.getExtendedBsonTypeHelper("decimal"));
        assertEquals(ExtendedBsonType.DOUBLE, MongoResultSetMetaData.getExtendedBsonTypeHelper("double"));
        assertEquals(ExtendedBsonType.INT32, MongoResultSetMetaData.getExtendedBsonTypeHelper("int"));
        assertEquals(ExtendedBsonType.JAVASCRIPT, MongoResultSetMetaData.getExtendedBsonTypeHelper("javascript"));
        assertEquals(
                ExtendedBsonType.JAVASCRIPT_WITH_SCOPE,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("javascriptWithScope"));
        assertEquals(ExtendedBsonType.INT64, MongoResultSetMetaData.getExtendedBsonTypeHelper("long"));
        assertEquals(ExtendedBsonType.MAX_KEY, MongoResultSetMetaData.getExtendedBsonTypeHelper("maxKey"));
        assertEquals(ExtendedBsonType.MIN_KEY, MongoResultSetMetaData.getExtendedBsonTypeHelper("minKey"));
        assertEquals(ExtendedBsonType.NULL, MongoResultSetMetaData.getExtendedBsonTypeHelper("null"));
        assertEquals(ExtendedBsonType.DOCUMENT, MongoResultSetMetaData.getExtendedBsonTypeHelper("object"));
        assertEquals(ExtendedBsonType.OBJECT_ID, MongoResultSetMetaData.getExtendedBsonTypeHelper("objectId"));
        assertEquals(
                ExtendedBsonType.REGULAR_EXPRESSION, MongoResultSetMetaData.getExtendedBsonTypeHelper("regex"));
        assertEquals(ExtendedBsonType.STRING, MongoResultSetMetaData.getExtendedBsonTypeHelper("string"));
        assertEquals(ExtendedBsonType.SYMBOL, MongoResultSetMetaData.getExtendedBsonTypeHelper("symbol"));
        assertEquals(ExtendedBsonType.TIMESTAMP, MongoResultSetMetaData.getExtendedBsonTypeHelper("timestamp"));
        assertEquals(ExtendedBsonType.UNDEFINED, MongoResultSetMetaData.getExtendedBsonTypeHelper("undefined"));
    }
}
