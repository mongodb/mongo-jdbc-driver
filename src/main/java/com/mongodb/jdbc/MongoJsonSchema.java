/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import com.mongodb.jdbc.utils.BsonUtils;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class MongoJsonSchema {

    private static final Codec<MongoJsonSchema> CODEC =
            MongoDriver.getCodecRegistry().get(MongoJsonSchema.class);

    public static class ScalarProperties {
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

    /**
     * Converts a deserialized jsonSchema into a MongoJsonSchema. The MongoJsonSchema instance is
     * semantically equivalent to the base jsonSchema, but bsonType has to be a single type
     * otherwise the types will get pushed down in the anyOf list. After the conversion is done,
     * anyOfs are flattened.
     *
     * @param baseSchema The base json schema.
     * @return the corresponding MongoJsonSchema.
     */
    public static MongoJsonSchema toSimplifiedMongoJsonSchema(JsonSchema baseSchema) {
        MongoJsonSchema unsimplifiedSchema = toMongoJsonSchema(baseSchema);
        return flattenNestedAnyOfs(unsimplifiedSchema);
    }

    /**
     * Converts a deserialized jsonSchema into a MongoJsonSchema. The MongoJsonSchema instance is
     * semantically equivalent to the base jsonSchema, but bsonType has to be a single type
     * otherwise the types will get pushed down in the anyOf list.
     *
     * @param baseSchema The base json schema.
     * @return the corresponding MongoJsonSchema.
     */
    private static MongoJsonSchema toMongoJsonSchema(JsonSchema baseSchema) {
        if (null == baseSchema) {
            return null;
        }

        MongoJsonSchema result = new MongoJsonSchema();
        result.properties = toMongoJsonSchemaProperties(baseSchema.properties);
        if (null != baseSchema.anyOf) {
            result.anyOf = new HashSet<MongoJsonSchema>();
            for (JsonSchema baseAnyOf : baseSchema.anyOf) {
                result.anyOf.add(toSimplifiedMongoJsonSchema(baseAnyOf));
            }
        }
        result.required = baseSchema.required;
        if (baseSchema.items != null) {
            result.items = toMongoJsonSchemaItems(baseSchema.items);
        }
        result.additionalProperties =
                toMongoJsonSchemaAdditionalProperties(baseSchema.additionalProperties);

        if (baseSchema.bsonType != null) {
            Set<String> bsonTypes = polymorphicBsonTypeToStringSet(baseSchema.bsonType);
            //  If there are many types in the set and it can not be reduced to one type after eliminating any Null
            //  type in the list, the types will be inserted in the list of anyOf to be handled as polymorphic type.
            if (bsonTypes.size() > 0 && bsonTypes.size() <= 2) {
                List<String> trimmedList =
                        bsonTypes
                                .stream()
                                .filter(t -> !t.equalsIgnoreCase(BSON_NULL.getBsonName()))
                                .collect(Collectors.toList());

                if (trimmedList.size() == 1) {
                    String type = trimmedList.get(0);
                    if (BSON_ARRAY.getBsonName().equalsIgnoreCase(type)
                            && (null == baseSchema.items)) {
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

            // We'll need to add the types from bsontype set to the anyOf list.
            // If the list is null, create a new one.
            if (null == result.anyOf) {
                result.anyOf = new HashSet<MongoJsonSchema>();
            }

            /**
             * Push down each bsontype from a bsontype array into a separate bsonType in an anyOf
             * schema. For example:
             *
             * <pre>
             * "y": {
             *     "bsonType": ["string", "int"]
             *  }
             * </pre>
             *
             * will become
             *
             * <pre>
             *  "y": {
             *      "anyOf": [
             *          {"bsonType": "string"},
             *          {"bsonType": "int"}
             *      ]
             *  }
             * </pre>
             */
            for (String currType : bsonTypes) {
                MongoJsonSchema anyOfSchema = new MongoJsonSchema();
                anyOfSchema.bsonType = currType;

                if (BSON_ARRAY.getBsonName().equalsIgnoreCase(currType)) {
                    // Move the items down with the anyOf schema for the bsontype Array
                    // because they go together
                    anyOfSchema.items = toMongoJsonSchemaItems(baseSchema.items);
                } else if (BSON_OBJECT.getBsonName().equalsIgnoreCase(currType)) {
                    // Move the object related properties down with the anyof schema for the 'object'
                    anyOfSchema.properties = toMongoJsonSchemaProperties(baseSchema.properties);
                    anyOfSchema.required = baseSchema.required;
                    anyOfSchema.additionalProperties =
                            toMongoJsonSchemaAdditionalProperties(baseSchema.additionalProperties);
                    result.properties = null;
                    result.required = null;
                    result.additionalProperties = false;
                }
                // Add the bsontype information as a new anyOf schema
                if (!result.anyOf.contains(anyOfSchema)) {
                    result.anyOf.add(anyOfSchema);
                }
            }
        }

        return result;
    }

    /**
     * Converts a polymorphic items field which can either be a JsonSchema or an Array of JsonSchema
     * to a set of MongoJsonSchema.
     *
     * @param polymorphicItems The original polymorphic field.
     * @return any, represented by an empty MongoJsonSchema.
     * @throws BsonInvalidOperationException If the BsonValue is neither JsonSchema or an Array.
     */
    private static MongoJsonSchema toMongoJsonSchemaItems(BsonValue polymorphicItems)
            throws BsonInvalidOperationException {
        MongoJsonSchema result = null;
        if (polymorphicItems == null) {
            return null;
        }

        // The only expected types for Items are BsonArray or BsonDocument
        if (!(polymorphicItems.isArray() || polymorphicItems.isDocument())) {
            throw new BsonInvalidOperationException(
                    "Value expected to be of type "
                            + BsonType.ARRAY
                            + " or "
                            + BsonType.DOCUMENT
                            + " but  is of unexpected type "
                            + polymorphicItems.getBsonType());
        }

        return new MongoJsonSchema();
    }

    /**
     * Converts a polymorphic additionalProperties which can either be a boolean or a Document to a
     * boolean.
     *
     * @param polymorphicAdditionalProperties The original polymorphic additionalProperties field.
     * @return the corresponding boolean value.
     * @throws BsonInvalidOperationException If the BsonValue is neither a Boolean or a Document.
     */
    private static boolean toMongoJsonSchemaAdditionalProperties(
            BsonValue polymorphicAdditionalProperties) throws BsonInvalidOperationException {
        if (polymorphicAdditionalProperties == null) {
            // By default, additional properties is false
            return false;
        }

        // The only expected types for additionalProperties are Document or Boolean
        if (!(polymorphicAdditionalProperties.isBoolean()
                || polymorphicAdditionalProperties.isDocument())) {
            throw new BsonInvalidOperationException(
                    "Value expected to be of type "
                            + BsonType.BOOLEAN
                            + " or "
                            + BsonType.DOCUMENT
                            + " but is of unexpected type "
                            + polymorphicAdditionalProperties.getBsonType());
        }

        // If additionalProperties is a document, return "true", otherwise return the boolean value.
        return polymorphicAdditionalProperties.isDocument()
                ? true
                : polymorphicAdditionalProperties.asBoolean().getValue();
    }

    /**
     * Converts a polymorphic bsonType which can either be a BsonArray or a BsonString to a set of
     * Strings.
     *
     * @param polymorphicBsonType The original polymorphic type.
     * @return the corresponding String set.
     * @throws BsonInvalidOperationException If the BsonValue is neither a BsonArray or a
     *     BsonString.
     */
    private static Set<String> polymorphicBsonTypeToStringSet(BsonValue polymorphicBsonType)
            throws BsonInvalidOperationException {
        Set<String> result;
        if (polymorphicBsonType.isArray()) {
            result =
                    polymorphicBsonType
                            .asArray()
                            .stream()
                            .map(val -> val.asString().getValue())
                            .collect(Collectors.toSet());
        } else if (polymorphicBsonType.isString()) {
            result = new HashSet<String>();
            result.add(polymorphicBsonType.asString().getValue());
        } else {
            throw new BsonInvalidOperationException(
                    "Value expected to be of type "
                            + BsonType.ARRAY
                            + " or "
                            + BsonType.STRING
                            + " but  is of unexpected type "
                            + polymorphicBsonType.getBsonType());
        }

        return result;
    }

    /**
     * Flattens nested anyOf.
     *
     * @param ioSchema The schema to simplify. ioSchema will be modified directly.
     * @return the simplified schema for convenience.
     */
    private static MongoJsonSchema flattenNestedAnyOfs(MongoJsonSchema ioSchema) {
        if (null == ioSchema) {
            return null;
        }

        MongoJsonSchema result = ioSchema;
        if (result.anyOf != null && !result.anyOf.isEmpty()) {
            result.anyOf =
                    result.anyOf
                            .stream()
                            .flatMap(
                                    anyOf -> {
                                        if (anyOf == null) {
                                            return Stream.empty();
                                        } else if (anyOf.anyOf != null && !anyOf.anyOf.isEmpty()) {
                                            return anyOf.anyOf.stream();
                                        } else {
                                            return Stream.of(anyOf);
                                        }
                                    })
                            .collect(Collectors.toSet());
        }

        // Last step is to reduce a single anyOf to the corresponding bsonType, properties, items, etc...
        if (result.anyOf != null && result.anyOf.size() == 1 && result.bsonType == null) {
            MongoJsonSchema singleAnyOf = result.anyOf.toArray(new MongoJsonSchema[0])[0];
            result.bsonType = singleAnyOf.bsonType;
            result.properties = singleAnyOf.properties;
            result.items = singleAnyOf.items;
            result.required = singleAnyOf.required;
            result.additionalProperties = singleAnyOf.additionalProperties;
            result.anyOf = null;
        }

        return result;
    }

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

    private static Map<String, MongoJsonSchema> toMongoJsonSchemaProperties(
            Map<String, JsonSchema> from) {
        if (null == from) {
            return null;
        }
        Map<String, MongoJsonSchema> to = new HashMap<String, MongoJsonSchema>();
        for (Map.Entry<String, JsonSchema> entry : from.entrySet()) {
            to.put(entry.getKey(), toSimplifiedMongoJsonSchema(entry.getValue()));
        }
        return to;
    }

    /**
     * Adds scalar properties to a MongoJsonSchema. Below is an example for adding a scalar
     * property:
     *
     * <pre>
     * {
     *   "bsonType": "object",
     *   "properties": {
     *     "bar": { "bsonType": "int" }
     *   }
     * }
     * </pre>
     *
     * will become
     *
     * <pre>
     * {
     *   "bsonType": "object",
     *   "properties": {
     *     "bar": { "bsonType": "int" },
     *     "foo": { "bsonType": "bool" }
     *   },
     *   "required": [foo]
     * }
     * </pre>
     *
     * @param scalarProperties Contains the basic info (name, bsonType and required flag) for each
     *     key. Each property is converted into a scalar MongoJsonSchema and added to this parent
     *     schema.
     */
    @SafeVarargs
    public final void addScalarKeys(ScalarProperties... scalarProperties) {
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
                && additionalProperties == other.additionalProperties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bsonType, properties, anyOf, required, items, additionalProperties);
    }

    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this);
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
    @BsonIgnore
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
