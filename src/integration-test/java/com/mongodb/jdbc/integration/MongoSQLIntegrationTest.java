package com.mongodb.jdbc.integration;

import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.Tests;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoSQLIntegrationTest extends MongoIntegrationTest {
    public static final String MONGOSQL = "mongosql";
    public static final String testDirectory = "resources/integration_test/tests";
    private final IntegrationTestUtils utils = new IntegrationTestUtils();
    private Connection conn;
    List<Tests> tests;

    @BeforeAll
    public void loadTestConfigs() throws FileNotFoundException {
        tests = utils.loadTestConfigs(testDirectory);
    }

    @BeforeEach
    public void setupConnection() throws SQLException {
        conn = getBasicConnection(MONGOSQL);
    }

    @AfterEach
    public void cleanupTest() throws SQLException {
        conn.close();
    }

    @Test
    public void testRunQueryTests()
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        String queryTestPrefix = "^Query.*";
        utils.runTests(tests, conn, false, queryTestPrefix);
    }

    @Test
    public void testRunDBMetadataTests()
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        String dbMetadataPrefix = "^DatabaseMetaData.*";
        utils.runTests(tests, conn, false, dbMetadataPrefix);
    }
}
