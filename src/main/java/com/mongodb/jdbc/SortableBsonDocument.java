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

import java.util.List;
import org.bson.BsonDocument;

public class SortableBsonDocument extends BsonDocument implements Comparable<SortableBsonDocument> {

    static class SortSpec {
        String key;
        ValueType type;

        SortSpec(String key, ValueType type) {
            this.key = key;
            this.type = type;
        }
    }

    enum ValueType {
        String,
        Int,
        Boolean,
    }

    List<SortSpec> sortSpecs;
    BsonDocument nestedDocValue;

    SortableBsonDocument(List<SortSpec> sortSpecs, String key, BsonDocument docValue) {
        super(key, docValue);

        this.sortSpecs = sortSpecs;
        this.nestedDocValue = docValue;
    }

    @Override
    public int compareTo(SortableBsonDocument o) {
        int r = 0;
        for (SortSpec sortSpec : this.sortSpecs) {
            switch (sortSpec.type) {
                case String:
                    r =
                            this.nestedDocValue
                                    .getString(sortSpec.key)
                                    .compareTo(o.nestedDocValue.getString(sortSpec.key));
                    break;
                case Int:
                    r =
                            this.nestedDocValue
                                    .getInt32(sortSpec.key)
                                    .compareTo(o.nestedDocValue.getInt32(sortSpec.key));
                    break;
                case Boolean:
                    r =
                            this.nestedDocValue
                                    .getBoolean(sortSpec.key)
                                    .compareTo(o.nestedDocValue.getBoolean(sortSpec.key));
                    break;
            }

            if (r != 0) {
                return r;
            }
        }

        return r;
    }
}
