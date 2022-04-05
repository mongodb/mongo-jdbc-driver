package com.mongodb.jdbc;

import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import org.bson.BsonValue;
import org.bson.codecs.BsonValueCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

/**
 * MongoSQLValue is a wrapper for BsonValue. The purpose of this class is to override the toString()
 * method to produce the extended JSON representation of a BsonValue for types that correspond to
 * JDBC Types.OTHER, rather than the java driver's default BsonValue.toString() output.
 *
 * <p>The driver's BsonValue class is abstract and intentionally cannot be extended by third
 * parties. The driver explains this is to keep the BSON type system closed. Therefore, this class
 * does not extend BsonValue, instead it contains a BsonValue member.
 */
public class MongoSQLValue {
    static final JsonWriterSettings JSON_WRITER_SETTINGS =
            JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
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
            case STRING:
                return this.v.asString().getValue();
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
            case ARRAY:
            case BINARY:
            case DB_POINTER:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case OBJECT_ID:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
                // These types are stringified in extended JSON format.
                BsonValueCodec c = new BsonValueCodec();
                StringWriter w = new StringWriter();
                c.encode(
                        new NoCheckStateJsonWriter(w, JSON_WRITER_SETTINGS),
                        this.v,
                        ENCODER_CONTEXT);
                w.flush();
                return w.toString();
            case END_OF_DOCUMENT:
            default:
                return "<invalid>";
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
