package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.sql.Types;
import java.sql.ResultSetMetaData;
import org.bson.BsonValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoSQLResultSetMetaDataTest extends MongoSQLMock {
    private static MongoResultSetMetaData resultSetMetaData;

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
    // foo.vec
    private static int ARRAY_COL = 7;

    static {
        try {
            resultSetMetaData = new MongoSQLResultSetMetaData(generateMongoJsonSchema());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    MongoResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(7, MongoSQLResultSetMetaDataTest.resultSetMetaData.getColumnCount());
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
        assertEquals("vec", resultSetMetaData.getColumnName(ARRAY_COL));
    }

    @Test
    void testGetColumnLabel() throws SQLException {
        assertEquals("a", resultSetMetaData.getColumnLabel(DOUBLE_COL));
        assertEquals("str", resultSetMetaData.getColumnLabel(STRING_COL));
        assertEquals("a", resultSetMetaData.getColumnLabel(ANY_OF_INT_STRING_COL));
        assertEquals("b", resultSetMetaData.getColumnLabel(INT_NULLABLE_COL));
        assertEquals("c", resultSetMetaData.getColumnLabel(INT_COL));
        assertEquals("d", resultSetMetaData.getColumnLabel(ANY_COL));
        assertEquals("vec", resultSetMetaData.getColumnLabel(ARRAY_COL));
    }

    @Test
    void testGetTableName() throws SQLException {
        assertEquals("", resultSetMetaData.getTableName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getTableName(STRING_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_OF_INT_STRING_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_NULLABLE_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ARRAY_COL));
    }

    @Test
    void testIsCaseSensitive() throws SQLException {
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOUBLE_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(STRING_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_OF_INT_STRING_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_NULLABLE_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(ARRAY_COL));
    }

    @Test
    void testIsNullable() throws SQLException {
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(DOUBLE_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(STRING_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(ANY_OF_INT_STRING_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_NULLABLE_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(ANY_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(ARRAY_COL));
    }

    @Test
    void testIsSigned() throws SQLException {
        assertEquals(true, resultSetMetaData.isSigned(DOUBLE_COL));
        assertEquals(false, resultSetMetaData.isSigned(STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_OF_INT_STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_NULLABLE_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_COL));
        assertEquals(false, resultSetMetaData.isSigned(ARRAY_COL));
    }

    @Test
    void testGetColumnDisplaySize() throws SQLException {
        assertEquals(15, resultSetMetaData.getColumnDisplaySize(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(STRING_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_NULLABLE_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ARRAY_COL));
    }

    @Test
    void testGetPrecision() throws SQLException {
        assertEquals(15, resultSetMetaData.getPrecision(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getPrecision(STRING_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_NULLABLE_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ARRAY_COL));
    }

    @Test
    void testGetScale() throws SQLException {
        assertEquals(15, resultSetMetaData.getScale(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getScale(STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_OF_INT_STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_NULLABLE_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_COL));
        assertEquals(0, resultSetMetaData.getScale(ARRAY_COL));
    }

    @Test
    void testGetColumnType() throws SQLException {
        assertEquals(Types.DOUBLE, resultSetMetaData.getColumnType(DOUBLE_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(STRING_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_OF_INT_STRING_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_NULLABLE_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ARRAY_COL));
    }

    @Test
    void testGetColumnTypeClassName() throws SQLException {
        assertEquals(double.class.getName(), resultSetMetaData.getColumnClassName(DOUBLE_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(STRING_COL));
        assertEquals(
                BsonValue.class.getName(),
                resultSetMetaData.getColumnClassName(ANY_OF_INT_STRING_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_NULLABLE_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ANY_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ARRAY_COL));
    }

    @Test
    void testGetColumnTypeName() throws SQLException {
        assertEquals("double", resultSetMetaData.getColumnTypeName(DOUBLE_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(STRING_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_OF_INT_STRING_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_NULLABLE_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_COL));
        assertEquals("array", resultSetMetaData.getColumnTypeName(ARRAY_COL));
    }

    @Test
    void testGetExtendedBsonTypeHelper() throws SQLException {
        // This test covers all bsonTypes not explicitly covered in the other tests
        assertEquals(
                ExtendedBsonType.ANY, MongoResultSetMetaData.getExtendedBsonTypeHelper("bson"));
        assertEquals(
                ExtendedBsonType.ARRAY, MongoResultSetMetaData.getExtendedBsonTypeHelper("array"));
        assertEquals(
                ExtendedBsonType.BOOLEAN, MongoResultSetMetaData.getExtendedBsonTypeHelper("bool"));
        assertEquals(
                ExtendedBsonType.BINARY,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("binData"));
        assertEquals(
                ExtendedBsonType.DATE_TIME,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("date"));
        assertEquals(
                ExtendedBsonType.DB_POINTER,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("dbPointer"));
        assertEquals(
                ExtendedBsonType.DECIMAL128,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("decimal"));
        assertEquals(
                ExtendedBsonType.DOUBLE,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("double"));
        assertEquals(
                ExtendedBsonType.INT32, MongoResultSetMetaData.getExtendedBsonTypeHelper("int"));
        assertEquals(
                ExtendedBsonType.JAVASCRIPT,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("javascript"));
        assertEquals(
                ExtendedBsonType.JAVASCRIPT_WITH_SCOPE,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("javascriptWithScope"));
        assertEquals(
                ExtendedBsonType.INT64, MongoResultSetMetaData.getExtendedBsonTypeHelper("long"));
        assertEquals(
                ExtendedBsonType.MAX_KEY,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("maxKey"));
        assertEquals(
                ExtendedBsonType.MIN_KEY,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("minKey"));
        assertEquals(
                ExtendedBsonType.NULL, MongoResultSetMetaData.getExtendedBsonTypeHelper("null"));
        assertEquals(
                ExtendedBsonType.DOCUMENT,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("object"));
        assertEquals(
                ExtendedBsonType.OBJECT_ID,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("objectId"));
        assertEquals(
                ExtendedBsonType.REGULAR_EXPRESSION,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("regex"));
        assertEquals(
                ExtendedBsonType.STRING,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("string"));
        assertEquals(
                ExtendedBsonType.SYMBOL,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("symbol"));
        assertEquals(
                ExtendedBsonType.TIMESTAMP,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("timestamp"));
        assertEquals(
                ExtendedBsonType.UNDEFINED,
                MongoResultSetMetaData.getExtendedBsonTypeHelper("undefined"));
    }
}
