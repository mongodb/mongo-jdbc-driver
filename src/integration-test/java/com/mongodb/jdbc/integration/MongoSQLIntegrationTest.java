package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.jdbc.MongoSQLResultSetMetaData;
import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.TestEntry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bson.BsonDbPointer;
import org.bson.BsonDocument;
import org.bson.BsonJavaScript;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonObjectId;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonSymbol;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoSQLIntegrationTest {
    static final String URL = "jdbc:mongodb://localhost";
    public static final String MONGOSQL = "mongosql";
    public static final String TEST_DIRECTORY = "resources/integration_test/tests";
    public static final String TEST_DATA_OTHER_DIRECTORY =
            "resources/integration_test/testdata_other";

    private List<TestEntry> testEntries;

    public Connection getBasicConnection(String db) throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("dialect", MONGOSQL);
        p.setProperty("user", System.getenv("ADL_TEST_LOCAL_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_LOCAL_PWD"));
        p.setProperty("authSource", System.getenv("ADL_TEST_LOCAL_AUTH_DB"));
        p.setProperty("database", db);
        p.setProperty("ssl", "false");
        return DriverManager.getConnection(URL, p);
    }

    @BeforeAll
    public void loadTestConfigs() throws IOException {
        testEntries = IntegrationTestUtils.loadTestConfigs(TEST_DIRECTORY);
    }

    @TestFactory
    Collection<DynamicTest> runIntegrationTests() throws SQLException {
        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (TestEntry testEntry : testEntries) {
            if (testEntry.skip_reason != null) {
                continue;
            }
            dynamicTests.add(
                    DynamicTest.dynamicTest(
                            testEntry.description,
                            () -> {
                                try (Connection conn = getBasicConnection(testEntry.db)) {
                                    IntegrationTestUtils.runTest(testEntry, conn, false);
                                }
                            }));
        }
        return dynamicTests;
    }

    @Test
    public void testTypesOther() throws SQLException, IOException, IllegalAccessException {
        List<Object> row = new ArrayList<>();
        ObjectId objID = new ObjectId("000000000000000000000001");
        BsonObjectId bsonObjectID = new BsonObjectId(objID);
        BsonDbPointer dbPointer = new BsonDbPointer("namespace", objID);
        BsonJavaScript javaScript = new BsonJavaScript("function(){ }");
        BsonRegularExpression regularExpression = new BsonRegularExpression("a(bc)*");
        BsonSymbol symbol = new BsonSymbol("symbol");
        BsonTimestamp timestamp = new BsonTimestamp(100, 0);
        BsonUndefined undefined = new BsonUndefined();
        BsonDocument object = new BsonDocument();
        object.append("foo", new BsonString("bar"));
        BsonJavaScriptWithScope javaScriptWithScope =
                new BsonJavaScriptWithScope("function(){ }", object);

        row.add(0);
        row.add(dbPointer);
        row.add(javaScript);
        row.add(javaScriptWithScope);
        row.add(new BsonMaxKey());
        row.add(new BsonMinKey());
        row.add(object);
        row.add(bsonObjectID);
        row.add(regularExpression);
        row.add(symbol);
        row.add(timestamp);

        // Skip Reason -
        //row.add(undefined);

        /*
        // Skip Reason - SQL-697
        // Getting result set schema throwing error: data did not match any variant of untagged enum BsonType
        BsonArray array = new BsonArray();
        array.add(new BsonInt32(1));
        array.add(new BsonInt32(2));
        array.add(new BsonInt32(3));
        row.add(array);
         */

        TestEntry testEntry = new TestEntry();
        testEntry.expected_column_label =
                new ArrayList(
                        Arrays.asList(
                                "_id",
                                "dbPointer",
                                "javascript",
                                "javascriptWithScope",
                                "maxKey",
                                "minKey",
                                "object",
                                "objectId",
                                "regularExpression",
                                "symbol",
                                "timestamp"));
        testEntry.expected_bson_type =
                new ArrayList(
                        Arrays.asList(
                                "int",
                                "dbPointer",
                                "javascript",
                                "javascriptWithScope",
                                "maxKey",
                                "minKey",
                                "object",
                                "objectId",
                                "regex",
                                "symbol",
                                "timestamp"));
        testEntry.expected_sql_type =
                new ArrayList(
                        Arrays.asList(
                                "INTEGER", "OTHER", "OTHER", "OTHER", "OTHER", "OTHER", "OTHER",
                                "OTHER", "OTHER", "OTHER", "OTHER"));

        try (Connection conn = getBasicConnection("integration_test")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from types_other");
            MongoSQLResultSetMetaData resultSetMetadata =
                    (MongoSQLResultSetMetaData) rs.getMetaData();
            rs.next();
            assertTrue(IntegrationTestUtils.compareRow(row, rs));
            IntegrationTestUtils.validateResultSetMetadata(testEntry, resultSetMetadata);
        }
    }
}
