package com.mongodb.jdbc.integration;

import com.mongodb.jdbc.integration.testharness.IntegrationTestUtils;
import com.mongodb.jdbc.integration.testharness.models.Tests;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MongoSQLIntegrationTest extends MongoIntegrationTest {
    public static final String MONGOSQL = "mongosql";
    public static final String testDirectory = "resources/integration_test/tests";
    private final IntegrationTestUtils utils = new IntegrationTestUtils();

    @Test
    public void testRunQueryTests()
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        Connection conn = getBasicConnection(MONGOSQL);
        String queryTestPrefix = "^Query.*";
        List<Tests> tests = utils.loadTestConfigs(testDirectory);
        utils.runTests(tests, conn, false, queryTestPrefix);
    }

    @Test
    public void testRunDBMetadataTests()
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        Connection conn = getBasicConnection(MONGOSQL);
        String dbMetadataPrefix = "^DatabaseMetaData.*";
        List<Tests> tests = utils.loadTestConfigs(testDirectory);
        utils.runTests(tests, conn, false, dbMetadataPrefix);
    }
}
