package com.mongodb.jdbc;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bson.BsonValue;

// Simple POJO for deserializing jsonschema.
// For more details on jsonSchema, see https://docs.mongodb.com/manual/reference/operator/query/jsonSchema/.
public class JsonSchema {

    public BsonValue bsonType;
    public Map<String, JsonSchema> properties;
    public Set<JsonSchema> anyOf;
    public Set<String> required;
    public BsonValue items;
    public BsonValue additionalProperties;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MongoJsonSchema)) {
            return false;
        }
        MongoJsonSchema other = (MongoJsonSchema) obj;
        return Objects.equals(bsonType, other.bsonType)
                && Objects.equals(properties, other.properties)
                && Objects.equals(anyOf, other.anyOf)
                && Objects.equals(required, other.required)
                && Objects.equals(items, other.items)
                && Objects.equals(additionalProperties, other.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bsonType, properties, anyOf, required, items, additionalProperties);
    }
}
