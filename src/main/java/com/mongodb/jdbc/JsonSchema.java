package com.mongodb.jdbc;

import java.util.Map;
import java.util.Set;

// Simple POJO for deserializing jsonschema.
public class JsonSchema {
    public Set<String> bsonType;
    public Map<String, JsonSchema> properties;
    public Set<JsonSchema> anyOf;
    public Set<String> required;
    public JsonSchema items;
    public boolean additionalProperties;
}
