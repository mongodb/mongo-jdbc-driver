package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonValue;

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
        ret.properties = new HashMap<>();
        ret.required = new HashSet<>();
        return ret;
    }

    public static MongoJsonSchema createScalarSchema(String type) {
        MongoJsonSchema ret = new MongoJsonSchema();
        ret.bsonType = type;
        return ret;
    }

    /**
     * Adds required scalar properties to a MongoJsonSchema, adding them as required and giving them
     * the passed bsonType. If properties or required for `this` are null, this method creates them.
     *
     * <p>ex: addRequiredScalarKeys( new Pair<>("foo", "int"), new Pair<>("bar", "string"), new
     * Pair<>("baz", "objectId"));
     *
     * @param scalarProperties are variadic pairs of (String property name, String property bson
     *     type). Each property type is converted into a scalar MongoJsonSchema with the proper
     *     bsonType
     * @return void
     */
    @SafeVarargs
    public final void addRequiredScalarKeys(Pair<String, String>... scalarProperties) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        if (required == null) {
            required = new HashSet<>();
        }
        for (Pair<String, String> p : scalarProperties) {
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
