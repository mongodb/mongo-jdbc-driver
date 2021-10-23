package com.mongodb.jdbc;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonValue;

public class MySQLResultDoc {
    public List<MySQLColumnInfo> columns;
    public List<BsonValue> values;

    public MySQLResultDoc() {}

    List<BsonValue> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
