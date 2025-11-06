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

package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.jdbc.oidc.JdbcOidcCallback;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoConnectionTest extends MongoMock {
    static final String localhost = "mongodb://localhost";
    @Mock private MongoConnectionProperties mockConnectionProperties;

    @BeforeAll
    protected void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each test, we need to reset it before each test case.
    @BeforeEach
    void setupTest() throws NoSuchFieldException {
        resetMockObjs();
    }

    @BeforeEach
    void setUp() {
        when(mockConnectionProperties.getConnectionString())
                .thenReturn(new ConnectionString(localhost));
        when(mockConnectionProperties.getDatabase()).thenReturn("test");
    }

    private String getApplicationName(MongoConnection connection) {
        MongoClientImpl mongoClientImpl = (MongoClientImpl) connection.getMongoClient();
        MongoClientSettings mcs = mongoClientImpl.getSettings();
        return mcs.getApplicationName();
    }

    @Test
    void testBuildAppNameWithoutClientInfo() throws Exception {
        when(mockConnectionProperties.getClientInfo()).thenReturn(null);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    @Test
    void testAppNameWithValidClientInfo() throws Exception {
        String clientInfo = "test-client+1.0.0";
        when(mockConnectionProperties.getClientInfo()).thenReturn(clientInfo);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName =
                MongoDriver.NAME + "+" + MongoDriver.getVersion() + "|" + clientInfo;
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    @Test
    void testAppNameWithInvalidClientInfo() throws Exception {
        // Client information has to be in the format 'name+version'
        when(mockConnectionProperties.getClientInfo()).thenReturn("invalid-client-info");

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    interface TestInterface {
        void test() throws SQLException;
    }

    void testExceptionAfterConnectionClosed(TestInterface ti) {
        // create statement after closed throws exception
        mongoConnection.close();
        assertThrows(SQLException.class, ti::test);
    }

    void testNoop(TestInterface ti) {
        assertDoesNotThrow(ti::test);
        testExceptionAfterConnectionClosed(ti::test);
    }

    @Test
    void testCheckConnection() {
        // When initiated
        assertFalse(mongoConnection.isClosed());

        // after calling close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    @Test
    void testCreateStatement() throws SQLException {
        Statement statement = mongoConnection.createStatement();

        // Should be able to create multiple statements.
        Statement statement2 = mongoConnection.createStatement();
        assertNotEquals(statement, statement2);

        // create statement after closed throws exception
        mongoConnection.close();
        testExceptionAfterConnectionClosed(() -> mongoConnection.createStatement());
    }

    @Test
    void testSetAutoCommitTrue() {
        testNoop(() -> mongoConnection.setAutoCommit(true));
    }

    @Test
    void testSetAutoCommitFalse() {
        testNoop(() -> mongoConnection.setAutoCommit(false));
    }

    @Test
    void testGetAutoCommit() throws SQLException {
        assertTrue(mongoConnection.getAutoCommit());
    }

    @Test
    void testCommit() {
        testNoop(() -> mongoConnection.commit());
    }

    @Test
    void testRollback() {
        testNoop(() -> mongoConnection.rollback());
    }

    @Test
    void testCloseAndIsClosed() {
        assertFalse(mongoConnection.isClosed());
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());

        // noop for second close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    @Test
    void testSetReadOnly() {
        testNoop(() -> mongoConnection.setReadOnly(true));
    }

    @Test
    void testIsReadOnly() {
        testNoop(() -> mongoConnection.isReadOnly());
    }

    @Test
    void testSetGetCatalog() throws SQLException {
        assertEquals(database, mongoConnection.getCatalog());
        mongoConnection.setCatalog("test1");
        assertEquals("test1", mongoConnection.getCatalog());

        testExceptionAfterConnectionClosed(() -> mongoConnection.setCatalog("test"));
        testExceptionAfterConnectionClosed(() -> mongoConnection.getCatalog());
    }

    @Test
    void tesSetTransactionIsolation() {
        testNoop(
                () ->
                        mongoConnection.setTransactionIsolation(
                                Connection.TRANSACTION_READ_UNCOMMITTED));
    }

    @Test
    void tesGetTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, mongoConnection.getTransactionIsolation());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getTransactionIsolation());
    }

    @Test
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoConnection.getWarnings());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getWarnings());
    }

    @Test
    void testClearWarnings() {
        testNoop(() -> mongoConnection.clearWarnings());
    }

    @Test
    void testRollbackJ3() {
        Savepoint sp = mock(Savepoint.class);
        testNoop(() -> mongoConnection.rollback(sp));
    }

    // Helper method to create a connection with OIDC auth and test the credential properties
    private void testOidcConnection(
            String connectionString,
            boolean shouldHaveHumanCallback,
            String expectedEnvironment,
            String expectedTokenResource)
            throws Exception {

        ConnectionString cs = new ConnectionString(connectionString);
        when(mockConnectionProperties.getConnectionString()).thenReturn(cs);

        MongoConnection conn = new MongoConnection(null, mockConnectionProperties);

        // Use reflection to access the private mongoClientSettings field
        Field settingsField = MongoConnection.class.getDeclaredField("mongoClientSettings");
        settingsField.setAccessible(true);
        MongoClientSettings settings = (MongoClientSettings) settingsField.get(conn);

        MongoCredential credential = settings.getCredential();

        if (expectedEnvironment != null) {
            assertEquals(
                    expectedEnvironment,
                    credential.getMechanismProperty(MongoCredential.ENVIRONMENT_KEY, null),
                    "ENVIRONMENT value should match");
        }

        if (expectedTokenResource != null) {
            assertEquals(
                    expectedTokenResource,
                    credential.getMechanismProperty(MongoCredential.TOKEN_RESOURCE_KEY, null),
                    "TOKEN_RESOURCE value should match");
        }

        Object callback =
                credential.getMechanismProperty(MongoCredential.OIDC_HUMAN_CALLBACK_KEY, null);
        if (shouldHaveHumanCallback) {
            assertNotNull(callback, "Human flow callback should be set");
            assertTrue(callback instanceof JdbcOidcCallback, "Callback should be JdbcOidcCallback");
        } else {
            assertNull(callback, "Human flow callback should NOT be set for machine flow");
        }
    }

    @Test
    void testOidcHumanFlowWhenMachinePropertiesNotPresent() throws Exception {
        // No auth mechanism properties - should use human flow
        testOidcConnection(localhost + "?authMechanism=MONGODB-OIDC", true, null, null);
    }

    @Test
    void testOidcGcpMachineFlow() throws Exception {
        testOidcConnection(
                localhost
                        + "?authMechanism=MONGODB-OIDC&authMechanismProperties=ENVIRONMENT:gcp,TOKEN_RESOURCE:audience",
                false,
                "gcp",
                "audience");
    }

    @Test
    void testOidcAzureMachineFlow() throws Exception {
        testOidcConnection(
                localhost
                        + "?authMechanism=MONGODB-OIDC&authMechanismProperties=ENVIRONMENT:azure,TOKEN_RESOURCE:audience",
                false,
                "azure",
                "audience");
    }

    @Test
    void testOidcKubernetesMachineFlow() throws Exception {
        testOidcConnection(
                localhost + "?authMechanism=MONGODB-OIDC&authMechanismProperties=ENVIRONMENT:k8s",
                false,
                "k8s",
                null);
    }
}
