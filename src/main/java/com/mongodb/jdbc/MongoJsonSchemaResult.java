package com.mongodb.jdbc;

import java.util.Map;

public class MongoJsonSchemaResult {
    public int ok;
    public Map<String, String> metadata;
    public MongoVersionedJsonSchema schema;
}
