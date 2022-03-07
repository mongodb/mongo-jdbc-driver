package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
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
    private static MongoSQLResultSetMetaData resultSetMetaData;

    static {
        try {
            resultSetMetaData =
                    new MongoSQLResultSetMetaData(generateMongoJsonSchema(), true, 0, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    MongoSQLResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(9, MongoSQLResultSetMetaDataTest.resultSetMetaData.getColumnCount());
    }

    @Test
    void testMetadataFieldsdOrder() throws SQLException {

        // Verify that the columns are sorted alphabetically when the sortFieldsAlphabetically is true and that the original order is kept when it's false.
        String[] expected_sorted_columns =
                new String[] {"a", "binary", "str", "a", "b", "c", "d", "null", "vec"};
        String[] expected_original_columns =
                new String[] {"a", "binary", "str", "c", "a", "d", "b", "vec", "null"};
        MongoJsonSchema schema = generateMongoJsonSchema();
        MongoSQLResultSetMetaData unsortedMedata =
                new MongoSQLResultSetMetaData(schema, false, 0, 0);
        MongoSQLResultSetMetaData sortedMedata = new MongoSQLResultSetMetaData(schema, true, 0, 0);

        assertEquals(
                expected_original_columns.length,
                unsortedMedata.getColumnCount(),
                "The number of expected columns doesn't match the actual number of columns");
        for (int i = 0; i < unsortedMedata.getColumnCount(); i++) {
            assertEquals(expected_original_columns[i], unsortedMedata.getColumnName(i + 1));
        }

        assertEquals(
                expected_sorted_columns.length,
                sortedMedata.getColumnCount(),
                "The number of expected columns doesn't match the actual number of columns");
        for (int i = 0; i < sortedMedata.getColumnCount(); i++) {
            assertEquals(expected_sorted_columns[i], sortedMedata.getColumnName(i + 1));
        }
    }

    @Test
    void testGetCatalogAndSchemaName() throws SQLException {
        assertEquals("", resultSetMetaData.getCatalogName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getSchemaName(DOUBLE_COL));
    }

    @Test
    void testGetColumnName() throws SQLException {
        assertEquals(DOUBLE_COL_LABEL, resultSetMetaData.getColumnName(DOUBLE_COL));
        assertEquals(STRING_COL_LABEL, resultSetMetaData.getColumnName(STRING_COL));
        assertEquals(
                ANY_OF_INT_STRING_COL_LABEL,
                resultSetMetaData.getColumnName(ANY_OF_INT_STRING_COL));
        assertEquals(INT_NULLABLE_COL_LABEL, resultSetMetaData.getColumnName(INT_OR_NULL_COL));
        assertEquals(INT_COL_LABEL, resultSetMetaData.getColumnName(INT_COL));
        assertEquals(ANY_COL_LABEL, resultSetMetaData.getColumnName(ANY_COL));
        assertEquals(NULL_COL_LABEL, resultSetMetaData.getColumnName(NULL_COL));
        assertEquals(ARRAY_COL_LABEL, resultSetMetaData.getColumnName(ARRAY_COL));
    }

    @Test
    void testGetColumnLabel() throws SQLException {
        assertEquals(DOUBLE_COL_LABEL, resultSetMetaData.getColumnLabel(DOUBLE_COL));
        assertEquals(STRING_COL_LABEL, resultSetMetaData.getColumnLabel(STRING_COL));
        assertEquals(
                ANY_OF_INT_STRING_COL_LABEL,
                resultSetMetaData.getColumnLabel(ANY_OF_INT_STRING_COL));
        assertEquals(INT_NULLABLE_COL_LABEL, resultSetMetaData.getColumnLabel(INT_OR_NULL_COL));
        assertEquals(INT_COL_LABEL, resultSetMetaData.getColumnLabel(INT_COL));
        assertEquals(ANY_COL_LABEL, resultSetMetaData.getColumnLabel(ANY_COL));
        assertEquals(NULL_COL_LABEL, resultSetMetaData.getColumnName(NULL_COL));
        assertEquals(ARRAY_COL_LABEL, resultSetMetaData.getColumnLabel(ARRAY_COL));
    }

    @Test
    void testGetTableName() throws SQLException {
        assertEquals("", resultSetMetaData.getTableName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getTableName(STRING_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_OF_INT_STRING_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_OR_NULL_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_COL));
        assertEquals("foo", resultSetMetaData.getTableName(NULL_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ARRAY_COL));
    }

    @Test
    void testIsCaseSensitive() throws SQLException {
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOUBLE_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(STRING_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_OF_INT_STRING_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_OR_NULL_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(NULL_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(ARRAY_COL));
    }

    @Test
    void testIsNullable() throws SQLException {
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(DOUBLE_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(STRING_COL));
        assertEquals(
                ResultSetMetaData.columnNoNulls,
                resultSetMetaData.isNullable(ANY_OF_INT_STRING_COL));
        assertEquals(
                ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_OR_NULL_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(ANY_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(NULL_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(ARRAY_COL));
    }

    @Test
    void testIsSigned() throws SQLException {
        assertEquals(true, resultSetMetaData.isSigned(DOUBLE_COL));
        assertEquals(false, resultSetMetaData.isSigned(STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_OF_INT_STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_OR_NULL_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_COL));
        assertEquals(false, resultSetMetaData.isSigned(NULL_COL));
        assertEquals(false, resultSetMetaData.isSigned(ARRAY_COL));
    }

    @Test
    void testGetColumnDisplaySize() throws SQLException {
        assertEquals(15, resultSetMetaData.getColumnDisplaySize(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(STRING_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_OR_NULL_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(NULL_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ARRAY_COL));
    }

    @Test
    void testGetPrecision() throws SQLException {
        assertEquals(15, resultSetMetaData.getPrecision(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getPrecision(STRING_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_OR_NULL_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_COL));
        assertEquals(0, resultSetMetaData.getPrecision(NULL_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ARRAY_COL));
    }

    @Test
    void testGetScale() throws SQLException {
        assertEquals(15, resultSetMetaData.getScale(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getScale(STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_OF_INT_STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_OR_NULL_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_COL));
        assertEquals(0, resultSetMetaData.getScale(NULL_COL));
        assertEquals(0, resultSetMetaData.getScale(ARRAY_COL));
    }

    @Test
    void testGetColumnType() throws SQLException {
        assertEquals(Types.DOUBLE, resultSetMetaData.getColumnType(DOUBLE_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(STRING_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_OF_INT_STRING_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_OR_NULL_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_COL));
        assertEquals(Types.NULL, resultSetMetaData.getColumnType(NULL_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ARRAY_COL));
    }

    @Test
    void testGetColumnTypeClassName() throws SQLException {
        assertEquals(double.class.getName(), resultSetMetaData.getColumnClassName(DOUBLE_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(STRING_COL));
        assertEquals(
                BsonValue.class.getName(),
                resultSetMetaData.getColumnClassName(ANY_OF_INT_STRING_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_OR_NULL_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ANY_COL));
        assertEquals(null, resultSetMetaData.getColumnClassName(NULL_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ARRAY_COL));
    }

    @Test
    void testGetColumnTypeName() throws SQLException {
        assertEquals("double", resultSetMetaData.getColumnTypeName(DOUBLE_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(STRING_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_OF_INT_STRING_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_OR_NULL_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_COL));
        assertEquals("null", resultSetMetaData.getColumnTypeName(NULL_COL));
        assertEquals("array", resultSetMetaData.getColumnTypeName(ARRAY_COL));
    }

    @Test
    void testGetDatasource() throws SQLException {
        // note, we cannot get foo.a using the label a.
        assertEquals("", resultSetMetaData.getDatasource("a"));
        assertEquals("", resultSetMetaData.getDatasource("str"));
        assertEquals("foo", resultSetMetaData.getDatasource("b"));
        assertEquals("foo", resultSetMetaData.getDatasource("c"));
        assertEquals("foo", resultSetMetaData.getDatasource("d"));
        assertEquals("foo", resultSetMetaData.getDatasource("null"));
        assertEquals("foo", resultSetMetaData.getDatasource("vec"));
    }
}
