package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.mongodb.jdbc.BsonTypeInfo.*;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class BsonTypeInfoTest {
    @Test
    void testGetBsonTypeInfoByName() throws SQLException {
        assertEquals(BSON_ARRAY, getBsonTypeInfoByName("array"));
        assertEquals(BSON_BOOL, getBsonTypeInfoByName("bool"));
        assertEquals(BSON_BINDATA, getBsonTypeInfoByName("binData"));
        assertEquals(BSON_DATE, getBsonTypeInfoByName("date"));
        assertEquals(BSON_DBPOINTER, getBsonTypeInfoByName("dbPointer"));
        assertEquals(BSON_DECIMAL, getBsonTypeInfoByName("decimal"));
        assertEquals(BSON_DOUBLE, getBsonTypeInfoByName("double"));
        assertEquals(BSON_INT, getBsonTypeInfoByName("int"));
        assertEquals(
                BSON_JAVASCRIPT, getBsonTypeInfoByName("javascript"));
        assertEquals(
                BSON_JAVASCRIPTWITHSCOPE,
                getBsonTypeInfoByName("javascriptWithScope"));
        assertEquals(BSON_LONG, getBsonTypeInfoByName("long"));
        assertEquals(BSON_MAXKEY, getBsonTypeInfoByName("maxKey"));
        assertEquals(BSON_MINKEY, getBsonTypeInfoByName("minKey"));
        assertEquals(BSON_NULL, getBsonTypeInfoByName("null"));
        assertEquals(BSON_OBJECT, getBsonTypeInfoByName("object"));
        assertEquals(BSON_OBJECTID, getBsonTypeInfoByName("objectId"));
        assertEquals(BSON_REGEX, getBsonTypeInfoByName("regex"));
        assertEquals(BSON_STRING, getBsonTypeInfoByName("string"));
        assertEquals(BSON_SYMBOL, getBsonTypeInfoByName("symbol"));
        assertEquals(BSON_TIMESTAMP, getBsonTypeInfoByName("timestamp"));
        assertEquals(BSON_UNDEFINED, getBsonTypeInfoByName("undefined"));

        // Test invalid type name
        assertThrows(SQLException.class, () -> getBsonTypeInfoByName("invalid"), "invalid BSON type name expected to throw exception");
    }
}
