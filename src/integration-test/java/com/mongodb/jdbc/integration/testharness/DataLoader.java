package com.mongodb.jdbc.integration.testharness;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.Pair;
import com.mongodb.jdbc.integration.MongoIntegrationTest;
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

import org.bson.Document;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DataLoader {
    public static final String TEST_DATA_DIRECTORY = "resources/integration_test/testdata";
    private static Yaml yaml = new Yaml(new Constructor(TestData.class));

    private List<TestDataEntry> datasets;
    private Set<Pair<String, String>> collections;
    private String url;

    public DataLoader(String dataDirectory, String url) throws IOException {
        this.datasets = new ArrayList<>();
        this.collections = new HashSet<>();
        this.url = url;

        readDataFiles(dataDirectory);
    }

    private void readDataFiles(String dataDirectory) throws IOException {
        File folder = new File(dataDirectory);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isFile()) {
                try (InputStream is = new FileInputStream(fileEntry.getPath())) {
                    TestData testData = yaml.load(is);
                    for (TestDataEntry entry : testData.dataset) {
                        datasets.add(entry);
                        collections.add(new Pair<>(entry.db, entry.collection));
                    }
                }
            }
        }
    }

    /**
     * Drops collections specified in test data files
     */
    public void dropCollections() {
        MongoClientURI uri = new MongoClientURI(this.url);
        try(MongoClient mongoClient = new MongoClient(uri)) {
            for (Pair<String, String>collection : collections) {
                MongoDatabase database = mongoClient.getDatabase(collection.left());
                database.getCollection(collection.right()).drop();
                System.out.println("Dropped " + collection.left() + "." + collection.right());
            }
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
            MongoClientURI uri = new MongoClientURI(this.url);
            try (MongoClient mongoClient = new MongoClient(uri)) {
                for (TestDataEntry entry : datasets) {
                    int count = 0;
                    MongoDatabase database = mongoClient.getDatabase(entry.db);
                    MongoCollection<Document> collection =
                            database.getCollection(entry.collection);
                    for (Map<String, Object> row : entry.docs) {
                        collection.insertOne(new Document(row));
                        count++;
                    }
                    System.out.println("Inserted " + count + " documents into " + entry.db + "." + entry.collection);
                }
            }
        } catch (MongoException ex) {
            dropCollections();
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        DataLoader loader = new DataLoader(TEST_DATA_DIRECTORY, MongoIntegrationTest.LOCAL_URL);
        loader.dropCollections();
        loader.loadTestData();
    }
}
