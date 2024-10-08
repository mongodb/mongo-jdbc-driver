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

import java.nio.ByteBuffer;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.codecs.*;
import org.bson.io.BasicOutputBuffer;

/** Utility class for BSON serialization and deserialization. */
public class BsonUtils {

    private static final DocumentCodec DOCUMENT_CODEC = new DocumentCodec();

    /**
     * Serializes a BsonDocument into a BSON byte array.
     *
     * @param doc The BsonDocument to serialize.
     * @return BSON byte array.
     * @throws MongoSerializationException If serialization fails.
     */
    public static byte[] serialize(BsonDocument doc) throws MongoSerializationException {
        if (doc == null) {
            throw new MongoSerializationException("Cannot serialize a null BsonDocument.");
        }
        try (BasicOutputBuffer buffer = new BasicOutputBuffer();
                BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
            BsonDocumentCodec codec = new BsonDocumentCodec();
            codec.encode(writer, doc, EncoderContext.builder().build());
            writer.flush();
            return buffer.toByteArray();
        } catch (RuntimeException e) {
            throw new MongoSerializationException("Failed to serialize BSON.", e);
        }
    }

    /**
     * Deserializes a BSON byte array into a BsonDocument.
     *
     * @param bytes The BSON byte array.
     * @return The deserialized BsonDocument.
     * @throws MongoSerializationException If deserialization fails.
     */
    public static BsonDocument deserialize(byte[] bytes) throws MongoSerializationException {
        try (BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(bytes))) {
            BsonDocumentCodec codec = new BsonDocumentCodec();
            return codec.decode(reader, DecoderContext.builder().build());
        } catch (RuntimeException e) {
            throw new MongoSerializationException("Failed to deserialize BSON.", e);
        }
    }
}
