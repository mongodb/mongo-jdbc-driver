package com.mongodb.jdbc;

import java.io.Writer;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class NoCheckStateJsonWriter extends JsonWriter {

    public NoCheckStateJsonWriter(Writer writer, JsonWriterSettings settings) {
        super(writer, settings);
    }

    @Override
    protected boolean checkState(State[] validStates) {
        return true;
    }
}
