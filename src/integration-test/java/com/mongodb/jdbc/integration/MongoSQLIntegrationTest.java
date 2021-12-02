package com.mongodb.jdbc.integration;

import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.Tests;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoSQLIntegrationTest extends MongoIntegrationTest {
    public static final String MONGOSQL = "mongosql";
    public static final String TEST_DIRECTORY = "resources/integration_test/tests";
    private final IntegrationTestUtils utils = new IntegrationTestUtils();

    List<Tests> tests;

    @Override
    @BeforeEach
    public void setupConnection() throws SQLException {
        conn = getBasicConnection(MONGOSQL);
    }

    @BeforeAll
    public void loadTestConfigs() throws IOException {
        tests = utils.loadTestConfigs(TEST_DIRECTORY);
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
