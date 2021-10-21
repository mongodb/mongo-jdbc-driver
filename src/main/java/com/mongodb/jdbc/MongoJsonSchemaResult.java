package com.mongodb.jdbc;

public class MongoJsonSchemaResult {
    public class VersionedSchema {
        int version;
        MongoJsonSchema jsonSchema;
    }

    public class Description {
        String description;
    }

    public int ok;
    public Description metadata;
    public VersionedSchema schema;
}
