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
