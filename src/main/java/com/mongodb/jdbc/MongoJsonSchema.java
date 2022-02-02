package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MongoJsonSchema {

    public String bsonType;
    public Map<String, MongoJsonSchema> properties;
    public Set<MongoJsonSchema> anyOf;
    public Set<String> required;
    public MongoJsonSchema items;
    public boolean additionalProperties;

    /**
     * Converts a deserialized jsonSchema into a MongoJsonSchema. The MongoJsonSchema instance is
     * semantically equivalent to the base jsonSchema, but bsonType has to be a single type
     * otherwise the types will get pushed down in the anyOf list.
     *
     * @param baseSchema The base json schema.
     * @return the corresponding MongoJsonSchema.
     */
    public static MongoJsonSchema toMongoJsonSchema(JsonSchema baseSchema) {
        if (null == baseSchema) {
            return null;
        }

        MongoJsonSchema result = new MongoJsonSchema();
        result.properties = toMongoJsonSchemaProperties(baseSchema.properties);
        if (null != baseSchema.anyOf) {
            result.anyOf = new HashSet<MongoJsonSchema>();
            for (JsonSchema baseAnyOf : baseSchema.anyOf) {
                result.anyOf.add(toMongoJsonSchema(baseAnyOf));
            }
        }
        result.required = baseSchema.required;
        result.items = toMongoJsonSchema(baseSchema.items);
        result.additionalProperties = baseSchema.additionalProperties;

        // [SQL-668]  bsonType is always a set of String, not a single String when deserializing the json data.
        //  If there are many types in the set and it can not be reduced to one type after eliminating any Null type in the list, the types will be inserted in the list
        //  of anyOf to be handled as polymorphic type.
        if (baseSchema.bsonType.size() > 0 && baseSchema.bsonType.size() <= 2) {
            // If there are 1 or 2 entries only, see if it can
            // be reduced to a single type
            List<String> trimmedList =
                    baseSchema
                            .bsonType
                            .stream()
                            .filter(t -> !t.equalsIgnoreCase(BSON_NULL.getBsonName()))
                            .collect(Collectors.toList());

            if (trimmedList.size() == 1) {
                String type = trimmedList.get(0);
                if (BSON_ARRAY.getBsonName().equalsIgnoreCase(type) && (null == baseSchema.items)) {
                    // The bson type is an array of unknowns items.
                    // The bson type is unknown and it's equivalent to any.
                    // Return an empty schema.
                    return new MongoJsonSchema();
                } else {
                    result.bsonType = type;
                }
                return result;
            }
        }

        // We need to add the types to the anyOf list.
        // If the list is null, create a new one.
        if (null == result.anyOf) {
            result.anyOf = new HashSet<MongoJsonSchema>();
        }

        // If BsonType contains a list of types, push down each type
        // into its own anyOf schema
        for (String currType : baseSchema.bsonType) {
            MongoJsonSchema anyOfSchema = new MongoJsonSchema();
            anyOfSchema.bsonType = currType;

            if (BSON_ARRAY.getBsonName().equalsIgnoreCase(currType)) {
                anyOfSchema.items = toMongoJsonSchema(baseSchema.items);
            } else if (BSON_OBJECT.getBsonName().equalsIgnoreCase(currType)) {
                anyOfSchema.properties = toMongoJsonSchemaProperties(baseSchema.properties);
                anyOfSchema.required = baseSchema.required;
                anyOfSchema.additionalProperties = baseSchema.additionalProperties;
            }
            // Add the bson type as a new anyOf schema
            result.anyOf.add(anyOfSchema);
        }

        return result;
    }

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

    private static Map<String, MongoJsonSchema> toMongoJsonSchemaProperties(
            Map<String, JsonSchema> from) {
        if (null == from) {
            return null;
        }
        Map<String, MongoJsonSchema> to = new HashMap<String, MongoJsonSchema>();
        for (Map.Entry<String, JsonSchema> entry : from.entrySet()) {
            to.put(entry.getKey(), toMongoJsonSchema(entry.getValue()));
        }
        return to;
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

    /**
     * Gets the nullability of a column (field) in this schemas list of properties. Its nullability
     * is determined as follows:
     *
     * <ul>
     *   <li>If it is not present in this schema's list of properties:
     *       <ul>
     *         <li>If it is required or this schema allows additional properties, it is considered
     *             unknown nullability
     *         <li>Otherwise, an exception is thrown
     *       </ul>
     *
     *   <li>If it is a scalar schema (i.e. not Any or AnyOf):
     *       <ul>
     *         <li>If its bson type is Null, it is considered nullable
     *         <li>Otherwise, its nullability depends on whether it is required
     *       </ul>
     *
     *   <li>If it is an Any schema, it is considered nullable
     *   <li>If it is an AnyOf schema, it is considered nullable if one of the component schemas in
     *       the AnyOf list is Null
     *       <ul>
     *         <li>This relies on the assumption that schemata returned by MongoSQL are simplified
     *       </ul>
     *
     * </ul>
     *
     * @param columnName The name of the column (or "field") for which to return nullability
     *     information
     * @return The nullability of the argument
     * @throws SQLException If the argued column is not in this schema, or if this schema is invalid
     */
    public int getColumnNullability(String columnName) throws SQLException {
        boolean required = this.required != null && this.required.contains(columnName);

        MongoJsonSchema columnSchema = this.properties.get(columnName);
        if (columnSchema == null) {
            if (required || this.additionalProperties) {
                // Even if it is required, we do not know the schema of the field.
                // If it has bson type Null, it would be nullable; otherwise it would
                // not be nullable. Therefore, we indicate it is unknown nullability.
                return DatabaseMetaData.columnNullableUnknown;
            }
            throw new SQLException(
                    "nullability info requested for invalid column '" + columnName + "'");
        }

        if (columnSchema.isAny()) {
            return DatabaseMetaData.columnNullable;
        }

        int nullable = required ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable;
        if (columnSchema.bsonType != null) {
            return columnSchema.bsonType.equals(BSON_NULL.getBsonName())
                    ? DatabaseMetaData.columnNullable
                    : nullable;
        }

        // Otherwise, the schema must be an AnyOf
        if (columnSchema.anyOf == null) {
            throw new SQLException(
                    "invalid schema: both bsonType and anyOf are null and this is not ANY");
        }

        for (MongoJsonSchema anyOfSchema : columnSchema.anyOf) {
            if (anyOfSchema.bsonType == null) {
                // Schemata returned by MongoSQL must be simplified. Having nested anyOf is invalid.
                throw new SQLException(
                        "invalid schema: anyOf subschema must have bsonType field; nested anyOf must be simplified");
            }

            if (anyOfSchema.bsonType.equals(BSON_NULL.getBsonName())) {
                return DatabaseMetaData.columnNullable;
            }
        }

        return nullable;
    }

    /**
     * Gets the bson type for this schema as a BsonTypeInfo enum value.
     *
     * @return The relevant BsonTypeInfo value
     * @throws SQLException If this schema is invalid
     */
    public BsonTypeInfo getBsonTypeInfo() throws SQLException {
        if (this.bsonType != null) {
            return getBsonTypeInfoByName(this.bsonType);
        }

        if (this.isAny()) {
            return BSON_BSON;
        }

        // Otherwise, the schema must be an AnyOf
        if (this.anyOf == null) {
            throw new SQLException(
                    "invalid schema: both bsonType and anyOf are null and this is not ANY");
        }

        BsonTypeInfo info = null;
        for (MongoJsonSchema anyOfSchema : this.anyOf) {
            if (anyOfSchema.bsonType == null) {
                // Schemata returned by MongoSQL must be simplified. Having nested anyOf is invalid.
                throw new SQLException(
                        "invalid schema: anyOf subschema must have bsonType field; nested anyOf must be simplified");
            }

            if (!anyOfSchema.bsonType.equals(BSON_NULL.getBsonName())) {
                // If info is not null, there must be more than one non-"null" anyOf type, so
                // we default to "bson".
                if (info != null) {
                    info = BSON_BSON;
                } else {
                    info = getBsonTypeInfoByName(anyOfSchema.bsonType);
                }
            }
        }

        return info;
    }
}
