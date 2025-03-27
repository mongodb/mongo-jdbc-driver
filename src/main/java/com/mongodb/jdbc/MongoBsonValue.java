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

import java.io.StringWriter;
import java.util.Objects;
import java.util.UUID;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.UuidRepresentation;
import org.bson.codecs.BsonValueCodec;
import org.bson.codecs.EncoderContext;
import org.bson.internal.UuidHelper;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

/**
 * MongoBsonValue is a wrapper for BsonValue. The purpose of this class is to override the
 * toString() method to produce the extended JSON representation of a BsonValue rather than the java
 * driver's default BsonValue.toString() output.
 *
 * <p>The driver's BsonValue class is abstract and intentionally cannot be extended by third
 * parties. The driver explains this is to keep the BSON type system closed. Therefore, this class
 * does not extend BsonValue, instead it contains a BsonValue member.
 */
public class MongoBsonValue {
    private JsonWriterSettings JSON_WRITER_SETTINGS;
    static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();
    static final BsonValueCodec CODEC = new BsonValueCodec();

    private final UuidRepresentation uuidRepresentation;
    private final boolean extJsonMode;

    private BsonValue v;

    public MongoBsonValue(BsonValue v, boolean isExtended, UuidRepresentation uuidRepresentation) {
        this.v = v;
        this.setJsonWriterSettings(isExtended);
        this.extJsonMode = isExtended;
        this.uuidRepresentation = uuidRepresentation;
    }

    public void setJsonWriterSettings(boolean isExtended) {
        this.JSON_WRITER_SETTINGS =
                JsonWriterSettings.builder()
                        .outputMode(isExtended ? JsonMode.EXTENDED : JsonMode.RELAXED)
                        .build();
    }

    /** @return The underlying BsonValue */
    public BsonValue getBsonValue() {
        return this.v;
    }

    @Override
    public String toString() {
        if (this.v == null) {
            return null;
        }

        switch (this.v.getBsonType()) {
            case NULL:
                return null;
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;

            case STRING:
                // The extended JSON representation of a string value is
                // delimited by double quotes. We do not want to include
                // those quotes in the output of this method, so we simply
                // return the underlying String value.
                return this.v.asString().getValue();
            case BINARY:
                BsonBinary binary = this.v.asBinary();
                if (binary.getType() == BsonBinarySubType.UUID_STANDARD.getValue()
                        || binary.getType() == BsonBinarySubType.UUID_LEGACY.getValue()) {
                    return formatUuid(binary);
                }
                // Fall through to toExtendedJson(this.v) for other binary types

            case ARRAY:
            case DATE_TIME:
            case DB_POINTER:
            case DECIMAL128:
            case DOCUMENT:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case OBJECT_ID:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
                // These types are stringified in extended JSON format.
                return toExtendedJson(this.v);

            case BOOLEAN:
            case DOUBLE:
            case INT32:
            case INT64:
                // These types are also stringified in extended JSON
                // format. However, they cannot be written by the Java
                // driver's JsonWriter as top-level values, so we must
                // nest them in a document.
                BsonValue v = new BsonDocument("v", this.v);
                String s = toExtendedJson(v);

                // Substring starts at 6 because the extended JSON for
                // the document is:
                //   {"v": <this.v as extJSON>}
                // so the first 5 characters are '{"v": ' and the
                // actual value's serialization starts at position 6.
                // The actual value's serialization ends 1 character
                // before the end, to account for the closing '}'.
                return s.substring(6, s.length() - 1);

            case END_OF_DOCUMENT:
            default:
                return this.v.toString();
        }
    }

    // Formats a BSON binary object into a JSON string representation of a UUID.
    // If the BSON binary type is UUID_STANDARD, it directly converts it to a UUID.
    // Otherwise, it uses the specified or default UUID representation to decode the binary data.
    private String formatUuid(BsonBinary binary) {
        UUID uuid;
        byte binaryType = binary.getType();
        if (binaryType == BsonBinarySubType.UUID_STANDARD.getValue()) {
            uuid = binary.asUuid();
        } else {
            // When this.uuidRepresentation is UNSPECIFIED or null, set UuidRepresentation to PYTHON_LEGACY
            UuidRepresentation representationToUse =
                    (Objects.nonNull(this.uuidRepresentation)
                                    && this.uuidRepresentation != UuidRepresentation.UNSPECIFIED)
                            ? this.uuidRepresentation
                            : UuidRepresentation.PYTHON_LEGACY;
            if (binaryType == BsonBinarySubType.UUID_LEGACY.getValue()
                    && representationToUse == UuidRepresentation.STANDARD) {
                // UUID_LEGACY subtype and trying to get the standard representation causes a BSONException,
                // So we return the binary representation extended JSON instead
                return toExtendedJson(binary);
            }
            uuid =
                    UuidHelper.decodeBinaryToUuid(
                            binary.getData(), binary.getType(), representationToUse);
        }
        return String.format("{\"$uuid\":\"%s\"}", uuid.toString());
    }

    private String toExtendedJson(BsonValue v) {
        StringWriter w = new StringWriter();
        CODEC.encode(new NoCheckStateJsonWriter(w, JSON_WRITER_SETTINGS), v, ENCODER_CONTEXT);
        w.flush();
        return w.toString();
    }

    @Override
    public int hashCode() {
        return this.v.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // Compare the wrapped BsonValue for equality
        return this.v.equals(((MongoBsonValue) o).v);
    }
}
