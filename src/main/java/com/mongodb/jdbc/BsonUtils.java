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

import java.sql.SQLException;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.Document;
import org.bson.codecs.*;
import org.bson.io.BasicOutputBuffer;

/** Utility class for BSON serialization and deserialization. */
public class BsonUtils {

    private static final DocumentCodec DOCUMENT_CODEC = new DocumentCodec();

    /**
     * Serializes a Document into a BSON byte array.
     *
     * @param doc The Document to serialize.
     * @return The BSON byte array.
     * @throws SQLException If serialization fails.
     */
    public static byte[] serialize(Document doc) throws SQLException {
        if (doc == null) {
            throw new SQLException("Cannot serialize a null Document.");
        }
        try (BasicOutputBuffer buffer = new BasicOutputBuffer();
                BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
            DOCUMENT_CODEC.encode(
                    writer,
                    doc,
                    EncoderContext.builder().isEncodingCollectibleDocument(true).build());
            writer.flush();
            return buffer.toByteArray();
        } catch (RuntimeException e) {
            throw new SQLException("Failed to serialize BSON.", e);
        }
    }

    /**
     * Deserializes a BSON byte array into a Document.
     *
     * @param bytes The BSON byte array.
     * @return The deserialized Document.
     * @throws SQLException If deserialization fails.
     */
    public static Document deserialize(byte[] bytes) throws SQLException {
        try (BsonBinaryReader reader = new BsonBinaryReader(java.nio.ByteBuffer.wrap(bytes))) {
            Document doc = DOCUMENT_CODEC.decode(reader, DecoderContext.builder().build());
            return doc;
        } catch (RuntimeException e) {
            throw new SQLException("Failed to deserialize BSON.", e);
        }
    }
}
