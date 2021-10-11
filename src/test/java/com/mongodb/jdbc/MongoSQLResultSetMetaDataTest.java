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
        assertEquals(true, resultSetMetaData.isSigned(DOUBLE_COL));
        assertEquals(false, resultSetMetaData.isSigned(STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_OF_INT_STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_NULLABLE_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_COL));
    }

    @Test
    void testGetColumnDisplaySize() throws SQLException {
    }

    @Test
    void testGetPrecision() throws SQLException {
    }

    @Test
    void testGetScale() throws SQLException {
    }

    @Test
    void testGetColumnType() throws SQLException {
    }

    @Test
    void testGetColumnTypeClassName() throws SQLException {
    }

    @Test
    void testGetColumnTypeName() throws SQLException {
    }

    @Test
    void testGetExtendedBsonTypeHelper() throws SQLException {
    }
}
