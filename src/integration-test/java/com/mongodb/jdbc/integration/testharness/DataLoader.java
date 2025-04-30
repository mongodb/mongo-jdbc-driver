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

package com.mongodb.jdbc.integration.testharness;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.Pair;
import com.mongodb.jdbc.integration.testharness.models.TestData;
import com.mongodb.jdbc.integration.testharness.models.TestDataEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;

public class DataLoader {
    public static final String TEST_DATA_DIRECTORY = "resources/integration_test/testdata";
    public static final String LOCAL_MDB_URL =
            "mongodb://localhost:"
                    + System.getenv("MDB_TEST_LOCAL_PORT")
                    + "/?uuidRepresentation=standard";
    public static final String LOCAL_ADF_URL =
            "mongodb://"
                    + System.getenv("ADF_TEST_LOCAL_USER")
                    + ":"
                    + System.getenv("ADF_TEST_LOCAL_PWD")
                    + "@localhost";
    private static Yaml yaml;

    static {
        TagInspector allowGlobalTags =
                new TagInspector() {
                    @Override
                    public boolean isGlobalTagAllowed(Tag tag) {
                        return true;
                    }
                };
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setTagInspector(allowGlobalTags);
        yaml = new Yaml(new Constructor(TestData.class, loaderOptions));
    };

    private List<TestDataEntry> datasets;
    private Set<Pair<String, String>> collections;
    private Set<String> databases;
    private ConnectionString mdbUri;
    private ConnectionString adfUri;

    public DataLoader(String dataDirectory) throws IOException {
        this.datasets = new ArrayList<>();
        this.collections = new HashSet<>();
        this.databases = new HashSet<>();
        this.mdbUri = new ConnectionString(LOCAL_MDB_URL);
        System.out.println(this.mdbUri);
        this.adfUri = new ConnectionString(LOCAL_ADF_URL);
        System.out.println("Local ADF url" + this.adfUri);

        readDataFiles(dataDirectory);
    }

    private void readDataFiles(String dataDirectory) throws IOException {
        File folder = new File(dataDirectory);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (isValidTestDataFile(fileEntry)) {
                try (InputStream is = new FileInputStream(fileEntry.getPath())) {
                    TestData testData = yaml.load(is);
                    for (TestDataEntry entry : testData.dataset) {
                        datasets.add(entry);
                        databases.add(entry.db);
                        if (entry.collection != null) {
                            collections.add(new Pair<>(entry.db, entry.collection));
                        } else if (entry.view == null) {
                            System.out.println(
                                    "One entry in "
                                            + fileEntry.getName()
                                            + " has no collection or view associated.");
                        }
                    }
                }
            }
        }
    }

    private boolean isValidTestDataFile(File file) {
        return file.isFile()
                && (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"));
    }

    /** Drops collections specified in test data files */
    public void dropCollections() {
        try (MongoClient mongoClient = MongoClients.create(mdbUri)) {
            for (Pair<String, String> collection : collections) {
                MongoDatabase database = mongoClient.getDatabase(collection.left());
                database.getCollection(collection.right()).drop();
                System.out.println("Dropped " + collection.left() + "." + collection.right());
            }
        }
    }

    private void setSchema(String database, String collection, Map<String, Object> jsonSchema) {
        System.out.println("Set schema for  " + database + "." + collection);
        BsonDocument command = new BsonDocument();
        BsonDocument schema = new BsonDocument();
        command.put("sqlSetSchema", new BsonString(collection));
        command.put("schema", schema);
        schema.put("version", new BsonInt32(1));

        Document doc = new Document(jsonSchema);
        schema.put(
                "jsonSchema",
                doc.toBsonDocument(
                        BsonDocument.class, MongoClientSettings.getDefaultCodecRegistry()));
        try (MongoClient mongoClient = MongoClients.create(adfUri)) {
            MongoDatabase db = mongoClient.getDatabase(database);
            db.runCommand(command);
        }
    }

    private void generateSchema(String database, String collection) {
        System.out.println("Generate schema for  " + database + "." + collection);
        BsonDocument command = new BsonDocument();
        command.put("sqlGenerateSchema", new BsonInt32(1));
        command.put("setSchemas", new BsonBoolean(true));

        BsonArray coll = new BsonArray();
        coll.add(new BsonString(database + "." + collection));
        command.put("sampleNamespaces", coll);

        try (MongoClient mongoClient = MongoClients.create(adfUri)) {
            MongoDatabase db = mongoClient.getDatabase("admin");
            db.runCommand(command);
        }
    }

    /**
     * Loads integration test data files from dataDirectory to database in specified url
     *
     * @return Count of rows inserted
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void loadTestData() throws IOException {
        try {
            try (MongoClient mongoClient = MongoClients.create(mdbUri)) {
                Map<String, String> views = new HashMap<>();
                for (TestDataEntry entry : datasets) {
                    MongoDatabase database = mongoClient.getDatabase(entry.db);
                    if (entry.collection != null) {
                        loadCollection(entry, database);
                    } else if (entry.view != null) {
                        views.put(entry.db, entry.view);
                    }
                }

                // Generate views schema after all collections have been setup
                // to make sure a view schema is not generated before the
                // collection data and schema are there.
                for (Map.Entry<String, String> view : views.entrySet()) {
                    generateSchema(view.getKey(), view.getValue());
                }
            }
        } catch (MongoException e) {
            dropCollections();
            throw e;
        }
    }

    /**
     * Loads a collection with the information provided in the TestDataEntry.
     *
     * @param entry the collection entry.
     * @param database The database to add the collection to.
     */
    private void loadCollection(TestDataEntry entry, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection(entry.collection);

        if (entry.docsExtJson != null) {
            // Process extended json format
            for (Map<String, Object> row : entry.docsExtJson) {
                Document d = Document.parse(new Document(row).toJson());
                collection.insertOne(new Document(d));
            }
            System.out.println(
                    "Inserted "
                            + entry.docsExtJson.size()
                            + " rows into "
                            + entry.db
                            + "."
                            + entry.collection);
        } else if (entry.docs != null) {
            for (Map<String, Object> row : entry.docs) {
                collection.insertOne(new Document(row));
            }
            System.out.println(
                    "Inserted "
                            + entry.docs.size()
                            + " rows into "
                            + entry.db
                            + "."
                            + entry.collection);
        }
        if (entry.nonuniqueIndexes != null) {
            for (Map<String, Object> index : entry.nonuniqueIndexes) {
                String indexName = collection.createIndex(new Document(index));
                System.out.println(
                        "Created index " + indexName + " on " + entry.db + "." + entry.collection);
            }
        }
        if (entry.schema != null) {
            setSchema(entry.db, entry.collection, entry.schema);
        } else {
            generateSchema(entry.db, entry.collection);
        }
    }

    public static void main(String[] args) throws IOException {
        DataLoader loader = new DataLoader(TEST_DATA_DIRECTORY);
        loader.dropCollections();
        loader.loadTestData();
    }
}
