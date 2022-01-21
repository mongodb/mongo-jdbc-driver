package com.mongodb.jdbc.integration;

import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.TestEntry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoSQLIntegrationTest {
    static final String URL = "jdbc:mongodb://localhost";
    public static final String MONGOSQL = "mongosql";
    public static final String TEST_DIRECTORY = "resources/integration_test/tests";
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
}
