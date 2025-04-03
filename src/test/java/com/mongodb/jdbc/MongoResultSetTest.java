/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonUndefined;
import org.bson.UuidRepresentation;
import org.bson.codecs.BsonDateTimeCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.json.JsonReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoResultSetTest extends MongoMock {
    @Mock MongoCursor<BsonDocument> cursor;
    MongoResultSet mockResultSet;
    static MongoResultSet mongoResultSet;
    static MongoResultSet mongoResultSetAllTypes;
    static MongoResultSet mongoResultSetAllTypesLegacyUUID;
    static MongoResultSet mongoResultSetAllTypesExtJson;
    static MongoResultSet closedMongoResultSet;
    static MongoResultSet mongoResultSetExtended;

    private static MongoResultSetMetaData resultSetMetaData;
    private static MongoStatement mongoStatement;
    private static MongoJsonSchema schema;

    static {
        try {
            schema = generateMongoJsonSchema();
            resultSetMetaData =
                    new MongoResultSetMetaData(
                            schema, null, true, mongoConnection.getLogger(), 0, null);
            mongoStatement = new MongoStatement(mongoConnection, "test");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        BsonDocument document = new BsonDocument();

        BsonDocument bot = new BsonDocument();
        BsonDocument foo = new BsonDocument();
        document.put("", bot);
        document.put("foo", foo);

        bot.put(DOUBLE_COL_LABEL, new BsonDouble(2.4));
        byte binary[] = {10, 20, 30};
        bot.put(BINARY_COL_LABEL, new BsonBinary(binary));
        bot.put(STRING_COL_LABEL, new BsonString("b"));

        foo.put(ANY_OF_INT_STRING_COL_LABEL, new BsonInt32(3));
        foo.put(INT_NULLABLE_COL_LABEL, new BsonNull());
        foo.put(INT_COL_LABEL, new BsonInt32(4));
        foo.put(ANY_COL_LABEL, new BsonUndefined());
        foo.put(NULL_COL_LABEL, new BsonNull());

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(5));
        array.add(new BsonInt32(6));
        array.add(new BsonInt32(7));
        foo.put(ARRAY_COL_LABEL, array);

        BsonDocument fooSubDoc = new BsonDocument();
        fooSubDoc.put(INT_COL_LABEL, new BsonInt32(5));
        foo.put(DOC_COL_LABEL, fooSubDoc);

        List<BsonDocument> mongoResultDocs = new ArrayList<BsonDocument>();
        mongoResultDocs.add(document);

        // All types result set
        BsonDocument docAllTypes = generateRowAllTypes();
        List<BsonDocument> mongoResultDocsAllTypes = new ArrayList<BsonDocument>();
        mongoResultDocsAllTypes.add(docAllTypes);

        MongoJsonSchema schemaAllTypes = generateMongoJsonSchemaAllTypes();

        try {
            mongoResultSet =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocs),
                            schema,
                            null,
                            false,
                            UuidRepresentation.STANDARD);
            mongoResultSetAllTypes =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocsAllTypes),
                            schemaAllTypes,
                            null,
                            false,
                            UuidRepresentation.STANDARD);
            mongoResultSetAllTypesLegacyUUID =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocsAllTypes),
                            schemaAllTypes,
                            null,
                            false,
                            UuidRepresentation.JAVA_LEGACY);
            mongoResultSetAllTypesExtJson =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocsAllTypes),
                            schemaAllTypes,
                            null,
                            true,
                            UuidRepresentation.STANDARD);
            closedMongoResultSet =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocs),
                            schema,
                            null,
                            false,
                            UuidRepresentation.STANDARD);
            mongoResultSetExtended =
                    new MongoResultSet(
                            mongoStatement,
                            new BsonExplicitCursor(mongoResultDocs),
                            schema,
                            null,
                            true,
                            UuidRepresentation.STANDARD);
            mongoResultSet.next();
            mongoResultSetAllTypes.next();
            mongoResultSetAllTypesLegacyUUID.next();
            mongoResultSetAllTypesExtJson.next();
            closedMongoResultSet.next();
            mongoResultSetExtended.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setup() throws NoSuchFieldException {
        MongoMock.resetMockObjs();
    }

    @Test
    void testBinaryGetters() throws Exception {
        // Binary cannot be gotten through anything other than getString, getBlob, and getBinaryStream, currently.
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBoolean(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getLong(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getDouble(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBigDecimal(BINARY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTimestamp(BINARY_COL);
                });

        // Test wasNull.
        mongoResultSet.getString(INT_OR_NULL_COL);
        assertTrue(mongoResultSet.wasNull());
        mongoResultSet.getString(DOUBLE_COL);
        assertFalse(mongoResultSet.wasNull());

        mongoResultSet.getString(ANY_COL);
        assertTrue(mongoResultSet.wasNull());

        // Only Binary and null values can be gotten from getBlob
        assertNotNull(mongoResultSet.getBlob(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(STRING_COL_LABEL);
                });
        assertNull(mongoResultSet.getBlob(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(ARRAY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(DOC_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBlob(INT_COL);
                });

        // Only Binary and null values can be gotten from getBinaryStream
        assertNotNull(mongoResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(STRING_COL_LABEL);
                });

        assertNull(mongoResultSet.getBinaryStream(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(STRING_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(INT_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(ARRAY_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBinaryStream(DOC_COL);
                });

        // Only Binary and null values can be gotten from getBytes
        assertNotNull(mongoResultSet.getBinaryStream(BINARY_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBytes(STRING_COL_LABEL);
                });
        assertNull(mongoResultSet.getBytes(NULL_COL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBytes(DOUBLE_COL);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBytes(INT_COL);
                });
    }

    @Test
    void testGetUuidValues() throws Exception {
        // Test standard UUID
        Object actualStandardUuid = mongoResultSetAllTypes.getObject(ALL_STANDARD_UUID_COL_LABEL);
        assertEquals(ALL_UUID_VAL, actualStandardUuid);

        // Test legacy UUID
        Object actualLegacyUuid =
                mongoResultSetAllTypesLegacyUUID.getObject(ALL_LEGACY_UUID_COL_LABEL);
        assertEquals(ALL_UUID_VAL, actualLegacyUuid);

        // Test UUID as string
        assertEquals(
                ALL_UUID_STRING_EXT_VAL,
                mongoResultSetAllTypes.getString(ALL_STANDARD_UUID_COL_LABEL));
        assertEquals(
                ALL_UUID_STRING_EXT_VAL,
                mongoResultSetAllTypesLegacyUUID.getString(ALL_LEGACY_UUID_COL_LABEL));
        assertEquals(
                ALL_UUID_STRING_EXT_VAL,
                mongoResultSetAllTypesExtJson.getString(ALL_STANDARD_UUID_COL_LABEL));
    }

    @Test
    void testGetColumnByIdAndName() throws Exception {
        assertEquals(DOUBLE_COL, mongoResultSet.findColumn(DOUBLE_COL_LABEL));
        assertEquals(STRING_COL, mongoResultSet.findColumn(STRING_COL_LABEL));
        assertEquals(INT_OR_NULL_COL, mongoResultSet.findColumn(INT_NULLABLE_COL_LABEL));
        assertEquals(INT_COL, mongoResultSet.findColumn(INT_COL_LABEL));
        assertEquals(ANY_COL, mongoResultSet.findColumn(ANY_COL_LABEL));
        assertEquals(ARRAY_COL, mongoResultSet.findColumn(ARRAY_COL_LABEL));
        assertEquals(DOC_COL, mongoResultSet.findColumn(DOC_COL_LABEL));
        assertThrows(SQLException.class, () -> mongoResultSet.findColumn(BOT_DUP_COL_LABEL));
        assertThrows(SQLException.class, () -> mongoResultSet.findColumn(FOO_DUP_COL_LABEL));

        // Test that the IDX and LABELS are working together correctly.
        assertEquals(
                mongoResultSet.getString(DOUBLE_COL), mongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(STRING_COL), mongoResultSet.getString(STRING_COL_LABEL));
        assertEquals(
                mongoResultSet.getString(INT_OR_NULL_COL),
                mongoResultSet.getString(INT_NULLABLE_COL_LABEL));
        assertEquals(mongoResultSet.getString(INT_COL), mongoResultSet.getString(INT_COL_LABEL));
        assertEquals(mongoResultSet.getString(ANY_COL), mongoResultSet.getString(ANY_COL_LABEL));
        assertEquals(
                mongoResultSet.getBytes(BINARY_COL), mongoResultSet.getBytes(BINARY_COL_LABEL));
    }

    @Test
    void testGetStringValues() throws Exception {
        // DOUBLE_COL              2.4
        // STRING_COL              "b"
        // BINARY_COL              [10, 20, 30]
        // ANY_OF_INT_STRING_COL   3
        // INT_OR_NULL_COL         null
        // NULL_COL                null
        // INT_COL                 4
        // ANY_COL                 "{}"
        // ARRAY_COL               [5, 6, 7]
        // DOC_COL                 {"c": 5}

        //Test String values are as expected
        assertEquals("2.4", mongoResultSet.getString(DOUBLE_COL_LABEL));
        assertEquals("b", mongoResultSet.getString(STRING_COL_LABEL));
        assertNull(mongoResultSet.getString(INT_NULLABLE_COL_LABEL));
        assertNull(mongoResultSet.getString(NULL_COL));
        assertEquals("4", mongoResultSet.getString(INT_COL_LABEL));
        assertNull(mongoResultSet.getString(ANY_COL_LABEL));
        assertEquals("[5, 6, 7]", mongoResultSet.getString(ARRAY_COL_LABEL));
        assertEquals("{\"c\": 5}", mongoResultSet.getString(DOC_COL_LABEL));

        // Check getAsciiStream and getUnicodeStream output are non-null.
        assertNotNull(mongoResultSet.getAsciiStream(STRING_COL_LABEL));
        assertNotNull(mongoResultSet.getUnicodeStream(STRING_COL_LABEL));

        // Actually check getAsciiStream and getUnicodeStream output. We just check
        // that the length is what is expected.
        assertEquals(
                1, mongoResultSet.getAsciiStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
        assertEquals(
                1, mongoResultSet.getUnicodeStream(STRING_COL_LABEL).read(new byte[100], 0, 100));
    }

    @Test
    public void testGetStringAllTypes() throws Exception {
        // non-null types
        assertEquals(ALL_DOUBLE_COL_VAL, mongoResultSetAllTypes.getString(ALL_DOUBLE_COL_LABEL));
        assertEquals(ALL_OBJECT_COL_VAL, mongoResultSetAllTypes.getString(ALL_OBJECT_COL_LABEL));
        assertEquals(ALL_ARRAY_COL_VAL, mongoResultSetAllTypes.getString(ALL_ARRAY_COL_LABEL));
        assertEquals(ALL_BINARY_COL_VAL, mongoResultSetAllTypes.getString(ALL_BINARY_COL_LABEL));
        assertEquals(
                ALL_OBJECT_ID_COL_VAL, mongoResultSetAllTypes.getString(ALL_OBJECT_ID_COL_LABEL));
        assertEquals(ALL_BOOL_COL_VAL, mongoResultSetAllTypes.getString(ALL_BOOL_COL_LABEL));
        assertEquals(ALL_DATE_COL_VAL, mongoResultSetAllTypes.getString(ALL_DATE_COL_LABEL));
        assertEquals(ALL_REGEX_COL_VAL, mongoResultSetAllTypes.getString(ALL_REGEX_COL_LABEL));
        assertEquals(
                ALL_JAVASCRIPT_COL_VAL, mongoResultSetAllTypes.getString(ALL_JAVASCRIPT_COL_LABEL));
        assertEquals(ALL_SYMBOL_COL_VAL, mongoResultSetAllTypes.getString(ALL_SYMBOL_COL_LABEL));
        assertEquals(
                ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL,
                mongoResultSetAllTypes.getString(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL));
        assertEquals(ALL_INT_COL_VAL, mongoResultSetAllTypes.getString(ALL_INT_COL_LABEL));
        assertEquals(
                ALL_TIMESTAMP_COL_VAL, mongoResultSetAllTypes.getString(ALL_TIMESTAMP_COL_LABEL));
        assertEquals(ALL_LONG_COL_VAL, mongoResultSetAllTypes.getString(ALL_LONG_COL_LABEL));
        assertEquals(ALL_DECIMAL_COL_VAL, mongoResultSetAllTypes.getString(ALL_DECIMAL_COL_LABEL));
        assertEquals(ALL_MIN_KEY_COL_VAL, mongoResultSetAllTypes.getString(ALL_MIN_KEY_COL_LABEL));
        assertEquals(ALL_MAX_KEY_COL_VAL, mongoResultSetAllTypes.getString(ALL_MAX_KEY_COL_LABEL));

        // Note that the extended JSON representation of a string value is double quote delimited,
        // but we do not want to return quotes as part of the String.
        assertEquals("str", mongoResultSetAllTypes.getString(ALL_STRING_COL_LABEL));

        // Note that the Java driver still outputs the legacy representation for DBPointer, as
        // opposed to the new standard representation: { $dbPointer: { $ref: <namespace>, $id: <oid> } }.
        // This is sufficient for our purposes, though.
        assertEquals(
                "{\"$ref\": \"db2\", \"$id\": " + ALL_OBJECT_ID_COL_VAL + "}",
                mongoResultSetAllTypes.getString(ALL_DB_POINTER_COL_LABEL));

        // Note that getString() returns null for NULL and UNDEFINED BSON values
        assertNull(mongoResultSetAllTypes.getString(ALL_UNDEFINED_COL_LABEL));
        assertNull(mongoResultSetAllTypes.getString(ALL_NULL_COL_LABEL));
    }

    @Test
    public void testGetObjectToStringAllTypes() throws Exception {
        // Test getObject().toString() result for each BSON type.
        assertEquals(
                ALL_DOUBLE_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_DOUBLE_COL_LABEL).toString());
        assertEquals(
                ALL_OBJECT_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_OBJECT_COL_LABEL).toString());
        assertEquals(
                ALL_ARRAY_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_ARRAY_COL_LABEL).toString());
        assertEquals(
                ALL_OBJECT_ID_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_OBJECT_ID_COL_LABEL).toString());
        assertEquals(
                ALL_BOOL_COL_VAL, mongoResultSetAllTypes.getObject(ALL_BOOL_COL_LABEL).toString());
        assertEquals(
                ALL_REGEX_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_REGEX_COL_LABEL).toString());
        assertEquals(
                ALL_JAVASCRIPT_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_JAVASCRIPT_COL_LABEL).toString());
        assertEquals(
                ALL_SYMBOL_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_SYMBOL_COL_LABEL).toString());
        assertEquals(
                ALL_JAVASCRIPT_WITH_SCOPE_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL).toString());
        assertEquals(
                ALL_INT_COL_VAL, mongoResultSetAllTypes.getObject(ALL_INT_COL_LABEL).toString());
        assertEquals(
                ALL_TIMESTAMP_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_TIMESTAMP_COL_LABEL).toString());
        assertEquals(
                ALL_MIN_KEY_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_MIN_KEY_COL_LABEL).toString());
        assertEquals(
                ALL_MAX_KEY_COL_VAL,
                mongoResultSetAllTypes.getObject(ALL_MAX_KEY_COL_LABEL).toString());

        // Note that the extended JSON representation of a string value is double quote delimited,
        // but we do not want to return quotes as part of the String.
        assertEquals("str", mongoResultSetAllTypes.getObject(ALL_STRING_COL_LABEL).toString());

        // Note that the Java driver still outputs the legacy representation for DBPointer, as
        // opposed to the new standard representation: { $dbPointer: { $ref: <namespace>, $id: <oid> } }.
        // This is sufficient for our purposes, though.
        assertEquals(
                "{\"$ref\": \"db2\", \"$id\": " + ALL_OBJECT_ID_COL_VAL + "}",
                mongoResultSetAllTypes.getObject(ALL_DB_POINTER_COL_LABEL).toString());

        // Note that getObject() returns null for NULL and UNDEFINED BSON values, so we check
        // manually that their stringification returns what is expected.
        assertNull(mongoResultSetAllTypes.getObject(ALL_UNDEFINED_COL_LABEL));
        assertNull(
                new MongoBsonValue(new BsonUndefined(), false, UuidRepresentation.STANDARD)
                        .toString());
        assertNull(mongoResultSetAllTypes.getObject(ALL_NULL_COL_LABEL));
        assertNull(
                new MongoBsonValue(new BsonNull(), false, UuidRepresentation.STANDARD).toString());

        // Note that getObject() must return the following classes for the following types:
        //   - Types.BIGINT    => long
        //   - Types.DECIMAL   => java.math.BigDecimal
        //   - Types.BINARY    => byte[]
        //   - Types.TIMESTAMP => java.sql.Timestamp
        // Therefore, the getObject().toString() representations are not extended JSON.
        // Since Types.BINARY maps to an array, we omit its getObject().toString() test.
        assertEquals("2147483648", mongoResultSetAllTypes.getObject(ALL_LONG_COL_LABEL).toString());
        assertEquals("21.2", mongoResultSetAllTypes.getObject(ALL_DECIMAL_COL_LABEL).toString());

        Codec<BsonDateTime> c = new BsonDateTimeCodec();
        BsonDateTime d =
                c.decode(new JsonReader(ALL_DATE_COL_VAL), DecoderContext.builder().build());
        Timestamp t = new Timestamp(d.getValue());

        assertEquals(t.toString(), mongoResultSetAllTypes.getObject(ALL_DATE_COL_LABEL).toString());

        // test that the string values match for the objects with EXTENDED and RELAXED json modes
        assertEquals(
                new MongoBsonValue(new BsonInt32(3), true, UuidRepresentation.STANDARD).toString(),
                mongoResultSetExtended.getObject(ANY_OF_INT_STRING_COL).toString());
        assertEquals(
                new MongoBsonValue(new BsonInt32(3), false, UuidRepresentation.STANDARD).toString(),
                mongoResultSet.getObject(ANY_OF_INT_STRING_COL).toString());

        BsonDocument doc = new BsonDocument();
        doc.put(INT_COL_LABEL, new BsonInt32(5));
        assertEquals(
                new MongoBsonValue(doc, true, UuidRepresentation.STANDARD).toString(),
                mongoResultSetExtended.getObject(DOC_COL_LABEL).toString());
        assertEquals(
                new MongoBsonValue(doc, false, UuidRepresentation.STANDARD).toString(),
                mongoResultSet.getObject(DOC_COL_LABEL).toString());

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(5));
        array.add(new BsonInt32(6));
        array.add(new BsonInt32(7));
        assertEquals(
                (new MongoBsonValue(array, true, UuidRepresentation.STANDARD)).toString(),
                mongoResultSetExtended.getObject(ARRAY_COL_LABEL).toString());
        assertEquals(
                (new MongoBsonValue(array, false, UuidRepresentation.STANDARD)).toString(),
                mongoResultSet.getObject(ARRAY_COL_LABEL).toString());
    }

    @Test
    public void testGetObjectToStringMatchesGetString() throws Exception {
        // Assert that getObject().toString() matches getString() for BSON types
        // that map to JDBC Types.OTHER.
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_OBJECT_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_OBJECT_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_ARRAY_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_ARRAY_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_OBJECT_ID_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_OBJECT_ID_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_REGEX_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_REGEX_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_DB_POINTER_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_DB_POINTER_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_JAVASCRIPT_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_JAVASCRIPT_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_SYMBOL_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_SYMBOL_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_JAVASCRIPT_WITH_SCOPE_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_TIMESTAMP_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_TIMESTAMP_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_MIN_KEY_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_MIN_KEY_COL_LABEL).toString());
        assertEquals(
                mongoResultSetAllTypes.getString(ALL_MAX_KEY_COL_LABEL),
                mongoResultSetAllTypes.getObject(ALL_MAX_KEY_COL_LABEL).toString());
    }

    @Test
    void testGetCursorName() throws Exception {
        mongoStatement.setCursorName("test");
        assertEquals("test", mongoResultSet.getCursorName());
    }

    @Test
    void testGetArithmeticValues() throws Exception {
        // Test Double values are as expected
        assertEquals(0.0, mongoResultSet.getDouble(INT_NULLABLE_COL_LABEL));
        assertEquals(2.4, mongoResultSet.getDouble(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getDouble(STRING_COL_LABEL);
                });
        assertEquals(4.0, mongoResultSet.getDouble(INT_COL_LABEL));
        assertEquals(3.0, mongoResultSet.getDouble(ANY_OF_INT_STRING_COL));

        // Test BigDecimal values are as expected
        assertEquals(BigDecimal.ZERO, mongoResultSet.getBigDecimal(INT_NULLABLE_COL_LABEL));
        assertEquals(new BigDecimal(2.4), mongoResultSet.getBigDecimal(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getBigDecimal(STRING_COL_LABEL);
                });
        assertEquals(new BigDecimal(4.0), mongoResultSet.getBigDecimal(INT_COL_LABEL));
        assertEquals(new BigDecimal(3.0), mongoResultSet.getBigDecimal(ANY_OF_INT_STRING_COL));

        // Test Long values are as expected
        assertEquals(0L, mongoResultSet.getLong(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoResultSet.getLong(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getLong(STRING_COL_LABEL);
                });
        assertEquals(4L, mongoResultSet.getLong(INT_COL_LABEL));
        assertEquals(3L, mongoResultSet.getLong(ANY_OF_INT_STRING_COL));

        // Test Int values are as expected
        assertEquals(0, mongoResultSet.getInt(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoResultSet.getInt(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getInt(STRING_COL_LABEL);
                });
        assertEquals(4, mongoResultSet.getInt(INT_COL_LABEL));
        assertEquals(3, mongoResultSet.getInt(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetByteValues() throws Exception {
        // Test Byte values are as expected
        assertEquals(0, mongoResultSet.getByte(INT_NULLABLE_COL_LABEL));
        assertEquals(2, mongoResultSet.getByte(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getByte(STRING_COL_LABEL);
                });

        assertEquals(4, mongoResultSet.getByte(INT_COL_LABEL));
        assertEquals(3, mongoResultSet.getByte(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetBooleanValues() throws Exception {
        // Test Boolean values are as expected
        assertEquals(false, mongoResultSet.getBoolean(INT_NULLABLE_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(DOUBLE_COL_LABEL));
        // MongoDB converts all strings to true, even ""
        assertEquals(true, mongoResultSet.getBoolean(STRING_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(INT_COL_LABEL));
        assertEquals(true, mongoResultSet.getBoolean(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetTimestampValues() throws Exception {

        assertNull(mongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(2), mongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(new Timestamp(4L), mongoResultSet.getTimestamp(INT_COL_LABEL));
        assertEquals(new Timestamp(3L), mongoResultSet.getTimestamp(ANY_OF_INT_STRING_COL));

        assertNull(mongoResultSet.getTime(NULL_COL_LABEL));
        assertEquals(new Time(2), mongoResultSet.getTime(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTime(STRING_COL_LABEL);
                });
        assertEquals(new Time(4L), mongoResultSet.getTime(INT_COL_LABEL));
        assertEquals(new Time(3L), mongoResultSet.getTime(ANY_OF_INT_STRING_COL));

        assertNull(mongoResultSet.getTimestamp(NULL_COL_LABEL));
        assertEquals(new Timestamp(2), mongoResultSet.getTimestamp(DOUBLE_COL_LABEL));
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getTimestamp(STRING_COL_LABEL);
                });
        assertEquals(new Timestamp(4L), mongoResultSet.getTimestamp(INT_COL_LABEL));
        assertEquals(new Timestamp(3L), mongoResultSet.getTimestamp(ANY_OF_INT_STRING_COL));
    }

    @Test
    void testGetObject() throws Exception {
        // test that the index and label versions of getObject have matching results
        assertEquals(
                mongoResultSet.getObject(DOUBLE_COL), mongoResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals(
                mongoResultSet.getObject(STRING_COL), mongoResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                mongoResultSet.getObject(INT_OR_NULL_COL),
                mongoResultSet.getObject(INT_NULLABLE_COL_LABEL));
        assertEquals(mongoResultSet.getObject(INT_COL), mongoResultSet.getObject(INT_COL_LABEL));
        assertEquals(mongoResultSet.getObject(ANY_COL), mongoResultSet.getObject(ANY_COL_LABEL));
        assertEquals(
                mongoResultSet.getObject(ARRAY_COL), mongoResultSet.getObject(ARRAY_COL_LABEL));
        assertEquals(mongoResultSet.getObject(DOC_COL), mongoResultSet.getObject(DOC_COL_LABEL));

        // test that getObject returns the expected java object for each bson type
        assertNull(mongoResultSet.getObject(NULL_COL_LABEL));
        assertEquals(2.4, mongoResultSet.getObject(DOUBLE_COL_LABEL));
        assertEquals("b", mongoResultSet.getObject(STRING_COL_LABEL));
        assertEquals(
                new MongoBsonValue(new BsonInt32(3), false, UuidRepresentation.STANDARD),
                mongoResultSet.getObject(ANY_OF_INT_STRING_COL));

        assertNull(mongoResultSet.getObject(NULL_COL));
        assertEquals(4, mongoResultSet.getObject(INT_COL_LABEL));

        assertNull(mongoResultSet.getObject(ANY_COL_LABEL));

        BsonArray array = new BsonArray();
        array.add(new BsonInt32(5));
        array.add(new BsonInt32(6));
        array.add(new BsonInt32(7));
        assertEquals(
                new MongoBsonValue(array, false, UuidRepresentation.STANDARD),
                mongoResultSet.getObject(ARRAY_COL_LABEL));

        BsonDocument doc = new BsonDocument();
        doc.put(INT_COL_LABEL, new BsonInt32(5));
        assertEquals(
                new MongoBsonValue(doc, false, UuidRepresentation.STANDARD),
                mongoResultSet.getObject(DOC_COL_LABEL));

        byte[] binary = {10, 20, 30};
        assertArrayEquals(binary, (byte[]) mongoResultSet.getObject(BINARY_COL_LABEL));
    }

    @SuppressWarnings("deprecation")
    @Test
    void closedResultSets() throws Exception {
        try {
            closedMongoResultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertTrue(closedMongoResultSet.isClosed());

        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.next();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.wasNull();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBytes(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBytes("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getAsciiStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getAsciiStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getUnicodeStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getUnicodeStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBinaryStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBinaryStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBoolean(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBoolean("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getByte(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getByte("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getShort(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getShort("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getInt(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getInt("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getLong(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getLong("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFloat(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFloat("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDouble(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDouble("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBigDecimal("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.clearWarnings();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getMetaData();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.findColumn("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getCharacterStream("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.isFirst();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.isLast();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.previous();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getFetchDirection();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getType();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getConcurrency();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowUpdated();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowInserted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.rowDeleted();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.insertRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.deleteRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.refreshRow();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getStatement();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBlob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getBlob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getDate("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTime(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTime("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTimestamp(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getTimestamp("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getHoldability();
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNClob(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNClob("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNString(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNString("f");
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNCharacterStream(0);
                });
        assertThrows(
                SQLException.class,
                () -> {
                    closedMongoResultSet.getNCharacterStream("f");
                });
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            if (nextCalledOnCursor.get()) {
                                return generateRow();
                            }
                            nextCalledOnCursor.set(true);
                            return generateRow();
                        });

        mockResultSet =
                new MongoResultSet(
                        mongoStatement, cursor, schema, null, false, UuidRepresentation.STANDARD);

        boolean hasNext = mockResultSet.next();
        assertFalse(hasNext);
        assertNull(mockResultSet.getCurrent());
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
    }

    @Test
    void returnNextRowWhenAvailable() throws Exception {
        BsonDocument valuesDoc = new BsonDocument();
        BsonDocument valuesDoc2 = new BsonDocument();
        BsonDocument valuesDoc3 = new BsonDocument();

        BsonExplicitCursor cursor =
                new BsonExplicitCursor(Arrays.asList(valuesDoc, valuesDoc2, valuesDoc3));
        mockResultSet =
                new MongoResultSet(
                        mongoStatement, cursor, schema, null, false, UuidRepresentation.STANDARD);

        assertFalse(mockResultSet.isFirst());
        assertFalse(mockResultSet.isLast());

        boolean hasNext = mockResultSet.next();
        assertTrue(hasNext);
        assertNotNull(mockResultSet.getCurrent());
        // This still throws because "label" is unknown.
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString("label");
                });
        assertTrue(mockResultSet.isFirst());
        assertFalse(mockResultSet.isLast());
    }

    @Test
    void testEmptyResultSet() throws SQLException {

        String colName = "a";

        BsonDocument emptyResultDoc = new BsonDocument();

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            if (nextCalledOnCursor.get()) {
                                return false;
                            }
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet =
                new MongoResultSet(
                        mongoStatement, cursor, schema, null, false, UuidRepresentation.STANDARD);

        assertFalse(mockResultSet.isFirst());
        // For empty result set, isLast should always be true
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // For empty result set, isFirst should always be false
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertEquals(12, mockResultSet.getMetaData().getColumnCount());
        assertFalse(mockResultSet.next());
        // query value for existing column in empty result should result to exception
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString(colName);
                });
    }

    @Test
    void testEmptyResultSetWhenGetMetadataCalledFirst() throws SQLException {
        String colName = "a";

        BsonDocument emptyResultDoc = new BsonDocument();

        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(true);
        when(cursor.hasNext()).thenAnswer(invocation -> !nextCalledOnCursor.get());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            nextCalledOnCursor.set(true);
                            return emptyResultDoc;
                        });

        mockResultSet =
                new MongoResultSet(
                        mongoStatement, cursor, schema, null, false, UuidRepresentation.STANDARD);

        assertEquals(12, mockResultSet.getMetaData().getColumnCount());
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // For empty resultset, isFirst should always be false
        assertFalse(mockResultSet.isFirst());
        assertTrue(mockResultSet.isLast());
        assertFalse(mockResultSet.next());
        // query value for existing column in empty result should result to exception
        assertThrows(
                SQLException.class,
                () -> {
                    mockResultSet.getString(colName);
                });
    }

    @Test
    void sameMetaDataOnDifferentRowsEvenWithDifferentBsonTypes() throws SQLException {
        AtomicBoolean nextCalledOnCursor = new AtomicBoolean(false);

        MongoJsonSchema sameMetadataSchema = new MongoJsonSchema();
        sameMetadataSchema.bsonType = "object";
        sameMetadataSchema.required = new HashSet<String>();
        sameMetadataSchema.required.add("foo");

        MongoJsonSchema fooSchema = new MongoJsonSchema();
        fooSchema.bsonType = "object";
        fooSchema.required = new HashSet<String>();
        fooSchema.required.add("a");

        MongoJsonSchema aSchema = new MongoJsonSchema();
        aSchema.bsonType = "int";

        fooSchema.properties = new HashMap<String, MongoJsonSchema>();
        fooSchema.properties.put("a", aSchema);

        sameMetadataSchema.properties = new HashMap<String, MongoJsonSchema>();
        sameMetadataSchema.properties.put("foo", fooSchema);

        BsonDocument row1 = new BsonDocument();
        row1.append("foo.a", new BsonInt32(1));

        BsonDocument row2 = new BsonDocument();
        row1.append("foo.a", new BsonString("test"));

        List<BsonDocument> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        Iterator<BsonDocument> iter = rows.iterator();
        when(cursor.hasNext()).thenAnswer(invocation -> iter.hasNext());
        when(cursor.next())
                .thenAnswer(
                        invocation -> {
                            BsonDocument doc = iter.next();
                            return doc;
                        });

        mockResultSet =
                new MongoResultSet(
                        mongoStatement,
                        cursor,
                        sameMetadataSchema,
                        null,
                        false,
                        UuidRepresentation.STANDARD);

        ResultSetMetaData metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.INTEGER, metaData.getColumnType(1));

        mockResultSet.next();
        // This is still the first row
        metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.INTEGER, metaData.getColumnType(1));

        mockResultSet.next();
        // Second row
        metaData = mockResultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals(Types.INTEGER, metaData.getColumnType(1));
    }
}
