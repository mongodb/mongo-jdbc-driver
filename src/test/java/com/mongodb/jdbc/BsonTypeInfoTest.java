package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class BsonTypeInfoTest {
    @Test
    void testGetBsonTypeInfoByName() throws SQLException {
        assertEquals(BsonTypeInfo.BSON_ARRAY, BsonTypeInfo.getBsonTypeInfoByName("array"));
        assertEquals(BsonTypeInfo.BSON_BOOL, BsonTypeInfo.getBsonTypeInfoByName("bool"));
        assertEquals(BsonTypeInfo.BSON_BINDATA, BsonTypeInfo.getBsonTypeInfoByName("binData"));
        assertEquals(BsonTypeInfo.BSON_DATE, BsonTypeInfo.getBsonTypeInfoByName("date"));
        assertEquals(BsonTypeInfo.BSON_DBPOINTER, BsonTypeInfo.getBsonTypeInfoByName("dbPointer"));
        assertEquals(BsonTypeInfo.BSON_DECIMAL, BsonTypeInfo.getBsonTypeInfoByName("decimal"));
        assertEquals(BsonTypeInfo.BSON_DOUBLE, BsonTypeInfo.getBsonTypeInfoByName("double"));
        assertEquals(BsonTypeInfo.BSON_INT, BsonTypeInfo.getBsonTypeInfoByName("int"));
        assertEquals(
                BsonTypeInfo.BSON_JAVASCRIPT, BsonTypeInfo.getBsonTypeInfoByName("javascript"));
        assertEquals(
                BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE,
                BsonTypeInfo.getBsonTypeInfoByName("javascriptWithScope"));
        assertEquals(BsonTypeInfo.BSON_LONG, BsonTypeInfo.getBsonTypeInfoByName("long"));
        assertEquals(BsonTypeInfo.BSON_MAXKEY, BsonTypeInfo.getBsonTypeInfoByName("maxKey"));
        assertEquals(BsonTypeInfo.BSON_MINKEY, BsonTypeInfo.getBsonTypeInfoByName("minKey"));
        assertEquals(BsonTypeInfo.BSON_NULL, BsonTypeInfo.getBsonTypeInfoByName("null"));
        assertEquals(BsonTypeInfo.BSON_OBJECT, BsonTypeInfo.getBsonTypeInfoByName("object"));
        assertEquals(BsonTypeInfo.BSON_OBJECTID, BsonTypeInfo.getBsonTypeInfoByName("objectId"));
        assertEquals(BsonTypeInfo.BSON_REGEX, BsonTypeInfo.getBsonTypeInfoByName("regex"));
        assertEquals(BsonTypeInfo.BSON_STRING, BsonTypeInfo.getBsonTypeInfoByName("string"));
        assertEquals(BsonTypeInfo.BSON_SYMBOL, BsonTypeInfo.getBsonTypeInfoByName("symbol"));
        assertEquals(BsonTypeInfo.BSON_TIMESTAMP, BsonTypeInfo.getBsonTypeInfoByName("timestamp"));
        assertEquals(BsonTypeInfo.BSON_UNDEFINED, BsonTypeInfo.getBsonTypeInfoByName("undefined"));

        // Test invalid type name
        assertThrows(SQLException.class, () -> BsonTypeInfo.getBsonTypeInfoByName("invalid"), "invalid BSON type name expected to throw exception");
    }
}
