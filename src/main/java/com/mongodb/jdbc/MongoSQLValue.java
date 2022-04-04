package com.mongodb.jdbc;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

/**
 * ExtJsonValue is a wrapper for BsonValue. The purpose of this class is to override the toString()
 * method to produce the extended JSON representation of a BsonValue, rather than the java driver's
 * default BsonValue.toString() output.
 *
 * <p>The driver's BsonValue class is abstract and intentionally cannot be extended by third
 * parties. The driver explains this is to keep the BSON type system closed.
 */
public class MongoSQLValue {
    static final JsonWriterSettings JSON_WRITER_SETTINGS =
            JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
    static final CodecRegistry CODEC_REGISTRY = fromProviders(new BsonValueCodecProvider());
    static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();

    // dateFormat cannot be static due to a threading bug in the library.
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private BsonValue v;

    public MongoSQLValue(BsonValue v) {
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
            case ARRAY:
                Codec<BsonArray> codec = CODEC_REGISTRY.get(BsonArray.class);
                StringWriter writer = new StringWriter();
                codec.encode(
                        new NoCheckStateJsonWriter(writer, JSON_WRITER_SETTINGS),
                        this.v.asArray(),
                        ENCODER_CONTEXT);
                writer.flush();
                return writer.toString();
            case BOOLEAN:
                return this.v.asBoolean().getValue() ? "true" : "false";
            case DATE_TIME:
                Date d = new Date(this.v.asDateTime().getValue());
                return dateFormat.format(d);
            case DECIMAL128:
                return this.v.asDecimal128().getValue().toString();
            case DOCUMENT:
                return this.v.asDocument().toJson(JSON_WRITER_SETTINGS);
            case DOUBLE:
                return Double.toString(this.v.asDouble().getValue());
            case INT32:
                return Integer.toString(this.v.asInt32().getValue());
            case INT64:
                return Long.toString(this.v.asInt64().getValue());
            case NULL:
                return null;
            case OBJECT_ID:
                return this.v.asObjectId().getValue().toString();
            case STRING:
                return this.v.asString().getValue();
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
            case BINARY:
            case DB_POINTER:
            case END_OF_DOCUMENT:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
            default:
                return "<invalid>"; // TODO - what should we do for these types?
        }
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
        return this.v.equals(((MongoSQLValue) o).v);
    }
}
