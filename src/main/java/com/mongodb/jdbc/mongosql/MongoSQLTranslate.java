/*
 * Copyright 2024-present MongoDB, Inc.
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

package com.mongodb.jdbc.mongosql;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.MongoJsonSchema;
import com.mongodb.jdbc.MongoJsonSchemaResult;
import com.mongodb.jdbc.MongoSerializationException;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.utils.BsonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.conversions.Bson;

@AutoLoggable
public class MongoSQLTranslate {
    public static final String SQL_SCHEMAS_COLLECTION = "__sql_schemas";
    private final MongoLogger logger;

    /** Native method to send commands via JNI. */
    public native byte[] runCommand(byte[] command, int length);

    public static final String ERROR_KEY = "error";
    public static final String IS_INTERNAL_ERROR_KEY = "error_is_internal";

    public MongoSQLTranslate(MongoLogger logger) {
        this.logger = logger;
    }

    /**
     * Executes the JNI runCommand and returns the response as a POJO.
     *
     * @param command The command to be executed.
     * @param responseClass The class of the response POJO.
     * @return The response POJO.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     * @throws MongoSQLException If an error occurs during command execution.
     */
    public <T> T runCommand(BsonDocument command, Class<T> responseClass)
            throws MongoSerializationException, MongoSQLException {

        byte[] commandBytes = BsonUtils.serialize(command);
        byte[] responseBytes = runCommand(commandBytes, commandBytes.length);
        BsonDocument responseDoc = BsonUtils.deserialize(responseBytes);

        BsonDocumentReader reader = new BsonDocumentReader(responseDoc);
        BsonValue error = responseDoc.get(ERROR_KEY);
        if (error != null) {
            String errorMessage =
                    String.format(
                            responseDoc.getBoolean(IS_INTERNAL_ERROR_KEY).getValue()
                                    ? "Internal error: %s"
                                    : "Error executing command: %s",
                            error.asString().getValue());
            throw new MongoSQLException(errorMessage);
        }

        return MongoDriver.getCodecRegistry()
                .get(responseClass)
                .decode(reader, DecoderContext.builder().build());
    }

    /**
     * Retrieves the version of the mongosqltranslate library.
     *
     * @return GetMongosqlTranslateVersionResult containing the version string.
     * @throws MongoSQLException If an error occurs during command execution.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     */
    public GetMongosqlTranslateVersionResult getMongosqlTranslateVersion()
            throws MongoSQLException, MongoSerializationException {
        BsonDocument command =
                new BsonDocument("command", new BsonString("getMongosqlTranslateVersion"))
                        .append("options", new BsonDocument());

        GetMongosqlTranslateVersionResult versionResult =
                runCommand(command, GetMongosqlTranslateVersionResult.class);

        logger.log(Level.INFO, "mongosqlTranslateVersion: " + versionResult.version);
        return versionResult;
    }

    /**
     * Checks if the JDBC driver version is compatible with the mongosqltranslate library.
     *
     * @return CheckDriverVersionResult containing the compatibility status.
     * @throws MongoSQLException If an error occurs during command execution.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     */
    public CheckDriverVersionResult checkDriverVersion()
            throws MongoSQLException, MongoSerializationException {
        BsonDocument options =
                new BsonDocument("driverVersion", new BsonString(MongoDriver.getVersion()))
                        .append("odbcDriver", new BsonBoolean(false));
        BsonDocument command =
                new BsonDocument("command", new BsonString("checkDriverVersion"))
                        .append("options", options);

        CheckDriverVersionResult checkDriverVersionResult =
                runCommand(command, CheckDriverVersionResult.class);

        logger.log(
                Level.INFO, "Driver Compatibility Status: " + checkDriverVersionResult.compatible);
        return checkDriverVersionResult;
    }

    /**
     * Executes a translate command based on the provided SQL and returns the response.
     *
     * @param sql The SQL query to translate.
     * @param dbName The database name.
     * @param schemaCatalog schema catalog
     * @return TranslateResult
     * @throws MongoSQLException If the command execution fails.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     */
    public TranslateResult translate(String sql, String dbName, BsonDocument schemaCatalog)
            throws MongoSQLException, MongoSerializationException {

        // Setting excludeNamespaces to default value false and relaxSchemaChecking to default value true.
        // These options are not currently handled in the JDBC driver
        BsonDocument options =
                new BsonDocument("sql", new BsonString(sql))
                        .append("db", new BsonString(dbName))
                        .append("excludeNamespaces", new BsonBoolean(false))
                        .append("relaxSchemaChecking", new BsonBoolean(true))
                        .append("schemaCatalog", schemaCatalog);
        BsonDocument translateCommand =
                new BsonDocument("command", new BsonString("translate")).append("options", options);

        return runCommand(translateCommand, TranslateResult.class);
    }

    /**
     * Retrieves the namespaces involved in the given SQL query.
     *
     * @param dbName The name of the database.
     * @param sql The SQL query.
     * @return GetNamespacesResult containing the namespaces.
     * @throws MongoSQLException If an error occurs during command execution.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     */
    public GetNamespacesResult getNamespaces(String dbName, String sql)
            throws MongoSQLException, MongoSerializationException {
        BsonDocument options =
                new BsonDocument("sql", new BsonString(sql)).append("db", new BsonString(dbName));
        BsonDocument command =
                new BsonDocument("command", new BsonString("getNamespaces"))
                        .append("options", options);

        return runCommand(command, GetNamespacesResult.class);
    }

    /**
     * Builds a catalog document containing the schema information for the specified collections.
     *
     * @param collections The list of collections to retrieve the schemas for.
     * @param dbName The name of the database where the collections must be.
     * @param mongoDatabase The current database for this connection.
     * @return the schema catalog for all the specified collections. The catalog document format is
     *     : { "dbName": { "collection1" : "Schema1", "collection2" : "Schema2", ... }}
     */
    public BsonDocument buildCatalogDocument(
            MongoDatabase mongoDatabase,
            String dbName,
            List<GetNamespacesResult.Namespace> collections)
            throws MongoSQLException {

        // There is no collection tied to the query
        // For example "SELECT 1"
        if (collections == null || collections.isEmpty()) {
            MongoJsonSchema emptyObjectSchema = MongoJsonSchema.createEmptyObjectSchema();
            // Create a catalog with an empty collection name and an empty schema
            // {"test": {"": {}}}
            return new BsonDocument(dbName, new BsonDocument("", new BsonDocument()));
        }

        // Create an aggregation pipeline to fetch the schema information for the specified collections.
        // The pipeline uses $in to query all the specified collections and projects them into the desired format:
        // "dbName": { "collection1" : "Schema1", "collection2" : "Schema2", ... }
        List<String> collectionNames =
                collections.stream().map(ns -> ns.collection).collect(Collectors.toList());

        // Filter documents where _id is in the list of collection names
        Bson matchStage = Aggregates.match(Filters.in("_id", collectionNames));

        // Include only the 'schema' and '_id' fields
        Bson projectStage =
                Aggregates.project(
                        Projections.fields(
                                Projections.include("schema"), Projections.include("_id")));

        // Accumulate collection names and their schemas into an array
        Bson groupStage =
                Aggregates.group(
                        null,
                        Accumulators.push(
                                "collections",
                                new BsonDocument("collectionName", new BsonString("$_id"))
                                        .append("schema", new BsonString("$schema"))));

        // Convert the 'collections' array into a document mapping each collection name to its schema
        // under the database name
        Bson finalProjectStage =
                Aggregates.project(
                        Projections.fields(
                                Projections.excludeId(),
                                Projections.computed(
                                        dbName,
                                        new BsonDocument(
                                                "$arrayToObject",
                                                new BsonArray(
                                                        Arrays.asList(
                                                                new BsonDocument(
                                                                        "$map",
                                                                        new BsonDocument(
                                                                                        "input",
                                                                                        new BsonString(
                                                                                                "$collections"))
                                                                                .append(
                                                                                        "as",
                                                                                        new BsonString(
                                                                                                "coll"))
                                                                                .append(
                                                                                        "in",
                                                                                        new BsonDocument(
                                                                                                        "k",
                                                                                                        new BsonString(
                                                                                                                "$$coll.collectionName"))
                                                                                                .append(
                                                                                                        "v",
                                                                                                        new BsonString(
                                                                                                                "$$coll.schema"))))))))));
        List<Bson> pipeline =
                Arrays.asList(matchStage, projectStage, groupStage, finalProjectStage);

        MongoCollection<BsonDocument> collection =
                mongoDatabase.getCollection(SQL_SCHEMAS_COLLECTION, BsonDocument.class);
        AggregateIterable<BsonDocument> result = collection.aggregate(pipeline);

        BsonDocument catalog = null;
        boolean foundResult = false;

        for (BsonDocument doc : result) {
            if (foundResult) {
                throw new MongoSQLException(
                        "Multiple results returned while getting schema; expected only one.");
            }
            catalog = doc;
            foundResult = true;
        }
        if (!foundResult) {
            logger.log(
                    Level.SEVERE,
                    "No schema information found for any of the requested collections. Will use empty schemas. Hint: Generate schemas for your collections.");
            BsonDocument schemas = new BsonDocument();
            for (String collectionName : collectionNames) {
                schemas.append(collectionName, new BsonDocument());
            }
            catalog = new BsonDocument(dbName, schemas);
        }

        // Check that all expected collections are present in the result
        BsonDocument resultCollections = catalog.getDocument(dbName);
        List<String> returnedCollections = new ArrayList<>(resultCollections.keySet());
        List<String> missingCollections =
                collections
                        .stream()
                        .map(ns -> ns.collection)
                        .filter(c -> !returnedCollections.contains(c))
                        .collect(Collectors.toList());

        if (!missingCollections.isEmpty()) {
            throw new MongoSQLException(
                    "Could not retrieve schema for collections: " + missingCollections);
        }

        return catalog;
    }

    /**
     * Retrieves the schema of a specific collection from the MongoDB database.
     *
     * @param mongoDatabase MongoDB database instance.
     * @param collectionName Name of the collection to retrieve the schema.
     * @return MongoJsonSchemaResult schema result of the collection.
     * @throws MongoSQLException If no schema is found for the given collection or an error occurs
     *     during command execution.
     * @throws MongoSerializationException If an error occurs during serialization or
     *     deserialization.
     */
    public MongoJsonSchemaResult getSchema(MongoDatabase mongoDatabase, String collectionName)
            throws MongoSerializationException, MongoSQLException {

        // The pipeline formats the output to match the structure required by MongoJsonSchemaResult
        List<Bson> pipeline =
                Arrays.asList(
                        Aggregates.match(Filters.eq("_id", collectionName)),
                        Aggregates.project(
                                Projections.fields(
                                        Projections.exclude("_id"),
                                        Projections.computed("schema.jsonSchema", "$schema"),
                                        Projections.computed("schema.version", new BsonInt32(1)))),
                        Aggregates.addFields(new Field<>("ok", new BsonInt32(1))));

        MongoCollection<BsonDocument> schemasCollection =
                mongoDatabase.getCollection(SQL_SCHEMAS_COLLECTION, BsonDocument.class);
        AggregateIterable<BsonDocument> result = schemasCollection.aggregate(pipeline);

        BsonDocument resultDoc = result.first();

        if (resultDoc == null) {
            logger.log(
                    Level.SEVERE,
                    "No schema information returned for the requested collections. Using an empty schema.");
            resultDoc = new BsonDocument();
        }

        BsonDocumentReader reader = new BsonDocumentReader(resultDoc);
        MongoJsonSchemaResult mongoJsonSchemaResult =
                MongoDriver.getCodecRegistry()
                        .get(MongoJsonSchemaResult.class)
                        .decode(reader, DecoderContext.builder().build());
        return mongoJsonSchemaResult;
    }
}
