package com.mongodb.jdbc;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class MongoVersionedJsonSchema {
    public Integer version;
    public MongoJsonSchema mongoJsonSchema;

    /** Empty Json schema. */
    public MongoVersionedJsonSchema() {}

    /**
     * Deserialized json schema from a 'sqlgetschema' command.
     *
     * @param version The schema version.
     * @param schema The schema.
     */
    @BsonCreator
    public MongoVersionedJsonSchema(
            @BsonProperty("version") final Integer version,
            @BsonProperty("jsonSchema") JsonSchema schema) {
        this.version = version;
        this.mongoJsonSchema = MongoJsonSchema.toSimplifiedMongoJsonSchema(schema);
    }
}
