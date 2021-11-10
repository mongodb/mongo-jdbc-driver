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
                default:
                    throw new IllegalArgumentException("unreachable");
            }

            if (r != 0) {
                return r;
            }
        }

        return r;
    }
}
