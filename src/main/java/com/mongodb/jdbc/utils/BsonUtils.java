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

package com.mongodb.jdbc.utils;

import com.mongodb.jdbc.MongoSerializationException;
import com.mongodb.jdbc.NoCheckStateJsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.*;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

/** Utility class for BSON serialization and deserialization. */
public class BsonUtils {
    public static final JsonWriterSettings JSON_WRITER_SETTINGS =
            JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).indent(true).build();

    public static final JsonWriterSettings JSON_WRITER_NO_INDENT_SETTINGS =
            JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).indent(false).build();

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

    public static <T> String toString(Codec<T> codec, T val, JsonWriterSettings settings) {
        try (StringWriter writer = new StringWriter();
                JsonWriter jsonWriter = new NoCheckStateJsonWriter(writer, settings)) {
            codec.encode(jsonWriter, val, EncoderContext.builder().build());
            writer.flush();

            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toString(Codec<T> codec, T val) {
        return toString(codec, val, JSON_WRITER_SETTINGS);
    }

    public static <T> BsonDocument toBsonDocument(Codec<T> codec, T val) {
        BsonDocument doc = new BsonDocument();
        try (BsonDocumentWriter writer = new BsonDocumentWriter(doc); ) {
            codec.encode(writer, val, EncoderContext.builder().build());
            writer.flush();
        }
        return doc;
    }
}
