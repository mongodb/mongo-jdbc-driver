package com.mongodb.jdbc.integration.testharness;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.integration.MongoIntegrationTest;
import com.mongodb.jdbc.integration.testharness.models.TestData;
import com.mongodb.jdbc.integration.testharness.models.TestDataEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.bson.Document;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DataLoader {
    private static final String TEST_DATA_DIRECTORY = "resources/integration_test/testdata";

    /**
     * Loads integration test data files from dataDirectory to database in specified url
     *
     * @param dataDirectory Directory containing data files to load
     * @param url Database url to load data to
     * @return Count of rows inserted
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public int loadTestData(String dataDirectory, String url) throws FileNotFoundException {
        int insertCounter = 0;
        File folder = new File(dataDirectory);
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongoClient = new MongoClient(uri);

        // Files may be large, read then load one file at a time
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            Yaml yaml = new Yaml(new Constructor(TestData.class));
            if (fileEntry.isFile()) {
                InputStream is = new FileInputStream(fileEntry.getPath());
                TestData datasets = yaml.load(is);

                for (TestDataEntry entry : datasets.dataset) {
                    MongoDatabase database = mongoClient.getDatabase(entry.db);
                    MongoCollection<Document> collection = database.getCollection(entry.collection);
                    for (Map<String, Object> row : entry.docs) {
                        collection.insertOne(new Document(row));
                        insertCounter++;
                    }
                }
            }
        }
        return insertCounter;
    }

    public static void main(String[] args) throws FileNotFoundException {
        DataLoader loader = new DataLoader();
        int rowsInserted = loader.loadTestData(TEST_DATA_DIRECTORY, MongoIntegrationTest.LOCAL_URL);
        System.out.println("Inserted " + rowsInserted + " rows");
    }
}
