package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MongoJsonSchema {
    public static class ScalarProperties
    {
        protected String name;
        protected boolean isRequired = true;
        protected BsonTypeInfo type;

        public ScalarProperties(String name, BsonTypeInfo type, boolean isRequired) {
            this.name = name;
            this.isRequired = isRequired;
            this.type = type;
        }

        public ScalarProperties(String name, BsonTypeInfo type) {
            this.name = name;
            this.type = type;
        }
    }

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
        ret.properties = new LinkedHashMap<>();
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
    public final void addRequiredScalarKeys(ScalarProperties... scalarProperties) {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        if (required == null) {
            required = new HashSet<>();
        }
        for (ScalarProperties prop : scalarProperties) {
            if (prop.isRequired) {
                required.add(prop.name);
            }
            properties.put(prop.name, createScalarSchema(prop.type.getBsonName()));
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
