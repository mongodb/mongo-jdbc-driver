package com.mongodb.jdbc;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonValue;

public class MongoResultDoc {
    public List<Column> columns;
    public List<BsonValue> values;

    public MongoResultDoc() {}

    List<BsonValue> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
