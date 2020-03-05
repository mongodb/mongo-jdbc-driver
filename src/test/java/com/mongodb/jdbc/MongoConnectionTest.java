package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoConnectionTest {
    private static ConnectionString uri = new ConnectionString("mongodb://localhost:27017/admin");;
    private static String database = "test";
    @InjectMocks
    private static MongoConnection mongoConnection = new MongoConnection(uri, database);
    @Mock
    private static MongoClient mongoClient;
    @Mock
    private static MongoDatabase mongoDatabase;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mongoClient).close();
    }

    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each tests, we need to reset it before each test cases.
    @BeforeEach
    void setupTest() throws NoSuchFieldException {
        FieldSetter.setField(mongoConnection,mongoConnection.getClass().getDeclaredField("mongoClient"), mongoClient);
        FieldSetter.setField(mongoConnection,mongoConnection.getClass().getDeclaredField("isClosed"), false);
        FieldSetter.setField(mongoConnection,mongoConnection.getClass().getDeclaredField("currentDB"), database);
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    interface TestInterface {
        void test() throws SQLException;
    }

    void testExceptionAfterConnectionClosed(TestInterface ti) {
        // create statement after closed throws exception
        mongoConnection.close();
        assertThrows(
                SQLException.class,
                ti::test
        );
    }

    void testNoop(TestInterface ti) {
        assertDoesNotThrow(ti::test);
        testExceptionAfterConnectionClosed(ti::test);
    }

    @Test
    void testCheckConnection() throws SQLException {
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
        testExceptionAfterConnectionClosed(
                () -> mongoConnection.createStatement()
        );
    }

    @Test
    void testSetAutoCommit() {
        testNoop(
                () -> mongoConnection.setAutoCommit(true)
        );
    }

    @Test
    void testCommit() {
        testNoop(
                () -> mongoConnection.commit()
        );
    }

    @Test
    void testRollback() {
        testNoop(
                () -> mongoConnection.rollback()
        );
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
        testNoop(
                () -> mongoConnection.setReadOnly(true)
        );
    }

    @Test
    void testIsReadOnly() throws SQLException {
        testNoop(
                () -> mongoConnection.isReadOnly()
        );
    }

    @Test
    void testSetGetCatalog() throws SQLException {
        assertEquals(database, mongoConnection.getCatalog());
        mongoConnection.setCatalog("test1");
        assertEquals("test1", mongoConnection.getCatalog());

        testExceptionAfterConnectionClosed(
                () -> mongoConnection.setCatalog("test")
        );
        testExceptionAfterConnectionClosed(
                () -> mongoConnection.getCatalog()
        );
    }

    @Test
    void tesSetTransactionIsolation() throws SQLException {
        testNoop(
                () -> mongoConnection.setTransactionIsolation(1)
        );
    }

    @Test
    void tesGetTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, mongoConnection.getTransactionIsolation());
        testExceptionAfterConnectionClosed(
                () -> mongoConnection.getTransactionIsolation()
        );
    }

    @Test
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoConnection.getWarnings());
        testExceptionAfterConnectionClosed(
                () -> mongoConnection.getWarnings()
        );
    }

    @Test
    void testClearWarnings() {
        testNoop(
                () -> mongoConnection.clearWarnings()
        );
    }

    @Test
    void testRollbackJ3() {
        Savepoint sp = mock(Savepoint.class);
        testNoop(
                () -> mongoConnection.rollback(sp)
        );
    }

    @Test
    void testIsValid() throws SQLException {
        assertTrue(mongoConnection.isValid(0));

        assertThrows(
                SQLException.class,
                () -> mongoConnection.isValid(-1)
        );

        // DB operation timeout, return false
        doAnswer( new AnswersWithDelay( 6000,  new Returns(mongoDatabase)))
                .when(mongoClient).getDatabase(anyString());
        assertFalse(mongoConnection.isValid(5));

        // When connection is interrupted
        doThrow(IllegalArgumentException.class).when(mongoClient).getDatabase(anyString());
        assertFalse(mongoConnection.isValid(5));

        mongoConnection.close();
        assertFalse(mongoConnection.isValid(0));
    }
}
