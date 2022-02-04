package com.mongodb.jdbc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import static java.lang.String.format;

// Simple POJO for deserializing jsonschema.
public class JsonSchema {
    private BsonValue polymorphicBsonType;
    @BsonIgnore
    public Set<String> bsonType;
    public Map<String, JsonSchema> properties;
    public Set<JsonSchema> anyOf;
    public Set<String> required;
    public JsonSchema items;
    public boolean additionalProperties;

    public void setBsonType(BsonValue polymorphicBsonType) {
        this.polymorphicBsonType = polymorphicBsonType;
        if (polymorphicBsonType.isArray()) {
            this.bsonType = polymorphicBsonType.asArray().stream().map(val -> val.asString().getValue())
                    .collect(Collectors.toSet());
        }
        else if (polymorphicBsonType.isString()) {
            bsonType = new HashSet<String>();
            bsonType.add(polymorphicBsonType.asString().getValue());
        }
        else
        {
            throw new BsonInvalidOperationException("Value expected to be of type " + BsonType.ARRAY +" or " +
                    BsonType.STRING + " but  is of unexpected type " + polymorphicBsonType.getBsonType());
        }
    }
}
