/*
 * Copyright 2024-present MongoDB, Inc.
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

import java.sql.SQLException;
import org.bson.Document;
import org.junit.jupiter.api.Test;

class BsonUtilsTest {

    @Test
    void testSerializeDeserialize() throws SQLException {
        Document originalDoc =
                new Document("name", "Test")
                        .append("value", 123)
                        .append("nested", new Document("key", "value"));

        byte[] serialized = BsonUtils.serialize(originalDoc);
        assertNotNull(serialized, "Serialized byte array should not be null");
        assertTrue(serialized.length > 0, "Serialized byte array should have content");

        Document deserializedDoc = BsonUtils.deserialize(serialized);
        assertNotNull(deserializedDoc, "Deserialized document should not be null");
        assertEquals(
                originalDoc,
                deserializedDoc,
                "Original and deserialized documents should be equal");
    }

    @Test
    void testSerializeNullDocument() {
        assertThrows(
                SQLException.class,
                () -> BsonUtils.serialize(null),
                "Serializing a null document should throw SQLException");
    }
}
