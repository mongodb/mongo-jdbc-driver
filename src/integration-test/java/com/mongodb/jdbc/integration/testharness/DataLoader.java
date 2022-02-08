package com.mongodb.jdbc.integration.testharness;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DataLoader {
    public static final String TEST_DATA_DIRECTORY = "resources/integration_test/testdata";
    public static final String LOCAL_MDB_URL =
            "mongodb://localhost:" + System.getenv("MDB_TEST_LOCAL_PORT");
    public static final String LOCAL_ADL_URL =
            "mongodb://"
                    + System.getenv("ADL_TEST_LOCAL_USER")
                    + ":"
                    + System.getenv("ADL_TEST_LOCAL_PWD")
                    + "@localhost";
    private static Yaml yaml = new Yaml(new Constructor(TestData.class));

    private List<TestDataEntry> datasets;
    private Set<Pair<String, String>> collections;
    private Set<String> databases;
    private MongoClientURI mdbUri;
    private MongoClientURI adlUri;

    public DataLoader(String dataDirectory) throws IOException {
        this.datasets = new ArrayList<>();
        this.collections = new HashSet<>();
        this.databases = new HashSet<>();
        this.mdbUri = new MongoClientURI(LOCAL_MDB_URL);
        this.adlUri = new MongoClientURI(LOCAL_ADL_URL);

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
                        collections.add(new Pair<>(entry.db, entry.collection));
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
        try (MongoClient mongoClient = new MongoClient(mdbUri)) {
            for (Pair<String, String> collection : collections) {
                MongoDatabase database = mongoClient.getDatabase(collection.left());
                database.getCollection(collection.right()).drop();
                System.out.println("Dropped " + collection.left() + "." + collection.right());
            }
        }
    }

    private void setSchema(String database, String collection, Map<String, Object> jsonSchema) {
        BsonDocument command = new BsonDocument();
        BsonDocument schema = new BsonDocument();
        command.put("sqlSetSchema", new BsonString(collection));
        command.put("schema", schema);
        schema.put("version", new BsonInt32(1));

        Document doc = new Document(jsonSchema);
        schema.put(
                "jsonSchema",
                doc.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()));
        try (MongoClient mongoClient = new MongoClient(adlUri)) {
            MongoDatabase db = mongoClient.getDatabase(database);
            db.runCommand(command);
        }
    }

    private void generateSchema(String database, String collection) {
        BsonDocument command = new BsonDocument();
        command.put("sqlGenerateSchema", new BsonInt32(1));
        command.put("setSchemas", new BsonBoolean(true));

        BsonArray coll = new BsonArray();
        coll.add(new BsonString(database + "." + collection));
        command.put("sampleNamespaces", coll);

        try (MongoClient mongoClient = new MongoClient(adlUri)) {
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
            try (MongoClient mongoClient = new MongoClient(mdbUri)) {
                for (TestDataEntry entry : datasets) {
                    MongoDatabase database = mongoClient.getDatabase(entry.db);
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
                                    "Created index "
                                            + indexName
                                            + " on "
                                            + entry.db
                                            + "."
                                            + entry.collection);
                        }
                    }
                    if (entry.schema != null) {
                        setSchema(entry.db, entry.collection, entry.schema);
                    } else {
                        generateSchema(entry.db, entry.collection);
                    }
                }
            }
        } catch (MongoException e) {
            dropCollections();
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        DataLoader loader = new DataLoader(TEST_DATA_DIRECTORY);
        loader.dropCollections();
        loader.loadTestData();
    }
}
