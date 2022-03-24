package com.mongodb.jdbc;

import java.io.Writer;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

/**
 * NoCheckStateJsonWriter will allow writing of any Json value. It does not validate it is
 * constructing a valid document. Useful for writing Bson Values such as a BsonArray.
 */
public class NoCheckStateJsonWriter extends JsonWriter {

    public NoCheckStateJsonWriter(Writer writer, JsonWriterSettings settings) {
        super(writer, settings);
    }

    @Override
    protected boolean checkState(State[] validStates) {
        return true;
    }
}
