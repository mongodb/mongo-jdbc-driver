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
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.codecs.BsonValueCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

/**
 * MongoSQLBsonValue is a wrapper for BsonValue. The purpose of this class is to override the
 * toString() method to produce the extended JSON representation of a BsonValue rather than the java
 * driver's default BsonValue.toString() output.
 *
 * <p>The driver's BsonValue class is abstract and intentionally cannot be extended by third
 * parties. The driver explains this is to keep the BSON type system closed. Therefore, this class
 * does not extend BsonValue, instead it contains a BsonValue member.
 */
public class MongoBsonValue {
    static final JsonWriterSettings JSON_WRITER_SETTINGS =
            JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
    static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();

    private BsonValue v;

    public MongoBsonValue(BsonValue v) {
        this.v = v;
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

            case ARRAY:
            case BINARY:
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

    private String toExtendedJson(BsonValue v) {
        BsonValueCodec c = new BsonValueCodec();
        StringWriter w = new StringWriter();
        c.encode(new NoCheckStateJsonWriter(w, JSON_WRITER_SETTINGS), v, ENCODER_CONTEXT);
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
