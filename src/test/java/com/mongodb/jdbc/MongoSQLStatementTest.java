package com.mongodb.jdbc;

import static java.sql.Statement.CLOSE_CURRENT_RESULT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoSQLStatementTest extends MongoSQLMock {
    private static MongoSQLStatement mongoStatement;

    static {
        try {
            mongoStatement = new MongoSQLStatement(mongoConnection, database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    protected void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each test, we need to reset it before each test case.
    @BeforeEach
    void setupTest() throws NoSuchFieldException, SQLException {
        resetMockObjs();
        mongoStatement = new MongoSQLStatement(mongoConnection, database);
    }

    void testExceptionAfterConnectionClosed(MongoSQLConnectionTest.TestInterface ti)
            throws SQLException {
        // create statement after closed throws exception
        mongoStatement.close();
        assertThrows(SQLException.class, ti::test);
    }

    void testNoop(MongoSQLConnectionTest.TestInterface ti) throws SQLException {
        assertDoesNotThrow(ti::test);
        testExceptionAfterConnectionClosed(ti::test);
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    interface TestInterface {
        void test() throws SQLException;
    }

    @Test
    void testExecuteQueryEmptyResult() throws SQLException {
        AtomicInteger rowCnt = new AtomicInteger();

        when(mongoCursor.hasNext()).thenAnswer(invocation -> rowCnt.get() < 0);

        when(mongoCursor.next())
                .thenAnswer(
                        invocation -> {
                            rowCnt.incrementAndGet();
                            return generateRow();
                        });
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(8, metaData.getColumnCount());

        rs.next();
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });
        assertFalse(rs.next());
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });

        assertTrue(rs.isLast());
    }

    @Test
    void testExecuteQuery() throws SQLException {
        AtomicInteger rowCnt = new AtomicInteger();
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());
        when(mongoCursor.hasNext()).thenAnswer(invocation -> rowCnt.get() < 1);

        when(mongoCursor.next())
                .thenAnswer(
                        invocation -> {
                            if (rowCnt.incrementAndGet() == 1) {
                                return generateRow();
                            }
                            return generateRow();
                        });

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(8, metaData.getColumnCount());
        // need to call next() first
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });

        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
        assertEquals("a", rs.getString(2));
        assertFalse(rs.next());
        assertTrue(rs.isLast());
    }

    @Test
    void testCloseForEmptyStatement() throws SQLException {
        assertFalse(mongoStatement.isClosed());
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());

        // noop for second close()
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
    }

    @Test
    void testCloseForExecutedStatement() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());

        assertFalse(mongoStatement.isClosed());
        ResultSet rs = mongoStatement.executeQuery("select * from test");
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
        assertTrue(rs.isClosed());

        // noop for second close()
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
    }

    @Test
    void testGetMaxFieldSize() throws SQLException {
        assertEquals(0, mongoStatement.getMaxFieldSize());
        testExceptionAfterConnectionClosed(() -> mongoStatement.setMaxFieldSize(0));
    }

    @Test
    void testSetMaxFieldoSize() throws SQLException {
        testNoop(() -> mongoStatement.setMaxFieldSize(0));
    }

    @Test
    void testgetMaxRows() throws SQLException {
        assertEquals(0, mongoStatement.getMaxRows());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getMaxRows());
    }

    @Test
    void testSetMaxRows() throws SQLException {
        testNoop(() -> mongoStatement.setMaxRows(0));
    }

    @Test
    void testSetEscapeProcessing() throws SQLException {
        testNoop(() -> mongoStatement.setEscapeProcessing(true));
    }

    @Test
    void testSetGetQueryTimeout() throws SQLException {
        int timeout = 123;
        mongoStatement.setQueryTimeout(timeout);
        assertEquals(timeout, mongoStatement.getQueryTimeout());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setQueryTimeout(timeout));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getQueryTimeout());
    }

    @Test
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoStatement.getWarnings());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getWarnings());
    }

    @Test
    void testClearWarnings() throws SQLException {
        testNoop(() -> mongoStatement.clearWarnings());
    }

    @Test
    void testSetCursorName() throws SQLException {
        testNoop(() -> mongoStatement.setCursorName(""));
    }

    @Test
    void testGetResultSet() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());

        assertNull(mongoStatement.getResultSet());
        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertEquals(rs, mongoStatement.getResultSet());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSet());
    }

    @Test
    void testGetUpdateCount() throws SQLException {
        assertEquals(-1, mongoStatement.getUpdateCount());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getUpdateCount());
    }

    @Test
    void testGetMoreResults() throws SQLException {
        assertEquals(false, mongoStatement.getMoreResults());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getMoreResults());
    }

    @Test
    void testSetGetFetchSize() throws SQLException {
        int fetchSize = 10;
        mongoStatement.setFetchSize(fetchSize);
        assertEquals(fetchSize, mongoStatement.getFetchSize());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setFetchSize(0));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getFetchSize());
    }

    @Test
    void testGetResultSetConcurrency() throws SQLException {
        assertEquals(ResultSet.CONCUR_READ_ONLY, mongoStatement.getResultSetConcurrency());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSetConcurrency());
    }

    @Test
    void testGetResultSetType() throws SQLException {
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, mongoStatement.getResultSetType());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSetType());
    }

    @Test
    void testGetConnection() throws SQLException {
        assertEquals(mongoConnection, mongoStatement.getConnection());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getConnection());
    }

    @Test
    void testGetMoreResultsWithInstructions() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(rs.isClosed());
        mongoStatement.getMoreResults(CLOSE_CURRENT_RESULT);
        assertTrue(rs.isClosed());

        testExceptionAfterConnectionClosed(
                () -> mongoStatement.getMoreResults(CLOSE_CURRENT_RESULT));
    }

    @Test
    void testSetPoolable() throws SQLException {
        testNoop(() -> mongoStatement.setPoolable(true));
    }

    @Test
    void testIsPoolable() throws SQLException {
        assertEquals(false, mongoStatement.isPoolable());
        testExceptionAfterConnectionClosed(() -> mongoStatement.isPoolable());
    }

    @Test
    void testSetGetCloseOnComplete() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoSchemaCursor.hasNext()).thenReturn(true);
        when(mongoSchemaCursor.next()).thenReturn(generateSchema());

        // When not close on complete
        assertFalse(mongoStatement.isCloseOnCompletion());
        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(mongoStatement.isClosed());
        rs.close();
        assertFalse(mongoStatement.isClosed());

        // close on complete
        mongoStatement.closeOnCompletion();
        assertTrue(mongoStatement.isCloseOnCompletion());
        rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(mongoStatement.isClosed());
        rs.close();
        assertTrue(mongoStatement.isClosed());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setFetchSize(0));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getFetchSize());
    }

    @Test
    void testGetLargeMaxRows() throws SQLException {
        assertEquals(0, mongoStatement.getLargeMaxRows());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getLargeMaxRows());
    }
}
