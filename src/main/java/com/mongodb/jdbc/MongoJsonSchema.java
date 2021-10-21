package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MongoJsonSchema {
    public String bsonType;
    public Map<String, MongoJsonSchema> properties;
    public Set<MongoJsonSchema> anyOf;
    public Set<String> required;
    public MongoJsonSchema items;
    public boolean additionalProperties;

    public MongoJsonSchema() {}

    public static MongoJsonSchema createEmptyObjectSchema() {
        MongoJsonSchema ret = new MongoJsonSchema();
        ret.bsonType = "object";
        ret.properties = new HashMap<String, MongoJsonSchema>();
        ret.required = new HashSet<String>();
        return ret;
    }

    public static MongoJsonSchema createScalarSchema(String type) {
        MongoJsonSchema ret = new MongoJsonSchema();
        ret.bsonType = type;
        return ret;
    }

    @SafeVarargs
    public final void addRequiredKeys(Pair<String, String>... values) {
        if (properties == null) {
            properties = new HashMap<String, MongoJsonSchema>();
        }
        if (required == null) {
            required = new HashSet<String>();
        }
        for (Pair<String, String> p : values) {
            required.add(p.left());
            properties.put(p.left(), createScalarSchema(p.right()));
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    // Any is represented by the empty json schema {}, so all fields
    // will be null or false
    public boolean isAny() {
        return bsonType == null
                && properties == null
                && anyOf == null
                && required == null
                && items == null
                && additionalProperties == false;
    }

    public boolean isObject() {
        return bsonType != null && bsonType.equals("object");
    }
}
