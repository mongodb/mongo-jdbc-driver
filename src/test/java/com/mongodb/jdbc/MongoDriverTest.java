package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDriverTest {
    static final String basicURL = "jdbc:mongodb://localhost";
    static final String authDBURL = "jdbc:mongodb://localhost/admin";
    static final String userNoPWDURL = "jdbc:mongodb://foo@localhost/admin";
    static final String userURL = "jdbc:mongodb://foo:bar@localhost";
    static final String jdbcUserURL = "jdbc:mongodb://jdbc:bar@localhost";
    // Even though ADL does not support replSets, this tests that we handle these URLs properly
    // for the future.
    static final String replURL = "jdbc:mongodb://foo:bar@localhost:27017,localhost:28910/admin";

    private static final String CURRENT_DIR =
            Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String NOT_LOGGING_TO_FILE_ERROR = "Not logging to files.";

    // Using an atomicInteger in case Junit ran with parallel execution enabled
    private static AtomicInteger connectionCounter = new AtomicInteger();

    private static final String USER_CONN_KEY = "user";
    private static final String PWD_CONN_KEY = "password";

    private static final Properties mandatoryProperties = setProperties();

    private static Properties setProperties() {
        Properties props = new Properties();
        props.setProperty(MongoDriver.DATABASE, "test");

        return props;
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBasicURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Missing mandatory 'database' property
        missingConnectionSettings(d, basicURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, basicURL, p);

        // user without password should throw.
        p = (Properties) mandatoryProperties.clone();
        p.setProperty(USER_CONN_KEY, "user");
        missingConnectionSettings(d, basicURL, p);

        // once property is set, it should be fine.
        p.setProperty(PWD_CONN_KEY, "pwd");
        assertNotNull(d.connect(basicURL, p));
    }

    @Test
    void testDBURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        missingConnectionSettings(d, authDBURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, authDBURL, p);

        p.setProperty("database", "admin2");

        // Database is not the same as the authDatabase in the uri.
        // So this is safe and should not throw.
        assertNotNull(d.getUnvalidatedConnection(authDBURL, p));
    }

    private void missingConnectionSettings(
            MongoDriver d, String connectionUrl, Properties properties) {
        // Should not return null or throw, even with null properties.
        try {
            d.getUnvalidatedConnection(connectionUrl, properties);
            fail("The connection should fail because a mandatory connection setting is missing.");
        } catch (SQLException e) {
            // Expected failure
            assertEquals(
                    MongoDriver.CONNECTION_ERROR_SQLSTATE,
                    e.getSQLState(),
                    "Expect SQL state "
                            + MongoDriver.CONNECTION_ERROR_SQLSTATE
                            + " but got "
                            + e.getSQLState());
        }
    }

    @Test
    void testuserNoPWDURL() throws SQLException {
        MongoDriver d = new MongoDriver();

        // This will throw because the java driver will fail
        // to parse the URI.
        try {
            d.getUnvalidatedConnection(userNoPWDURL, mandatoryProperties);
            fail("The connection should fail because the URI is not valid.");
        } catch (SQLException e) {
            // Expected failure
            assertEquals(java.lang.IllegalArgumentException.class, e.getCause().getClass());
        }
    }

    @Test
    void testJDBCURL() throws SQLException {
        MongoDriver d = new MongoDriver();

        assertNotNull(d.connect(jdbcUserURL, mandatoryProperties));

        // changing user name from `jdbc` should throw.
        Properties p = (Properties) mandatoryProperties.clone();
        p.setProperty(USER_CONN_KEY, "jdbc2");
        assertThrows(
                SQLException.class,
                () -> d.connect(jdbcUserURL, p),
                "The connection should fail because of a user mismatch between the URI and the properties");
    }

    @Test
    void testUserURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        missingConnectionSettings(d, userURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, userURL, p);
        p = (Properties) mandatoryProperties.clone();
        assertNotNull(d.connect(userURL, p));

        // This is not a mismatch, because we assume that if an auth database is missing
        // in the URI, even though default is admin, the user would prefer whatever is in
        // the passed Properties.
        p.setProperty("authDatabase", "admin2");
        assertNotNull(d.connect(userURL, p));

        Properties p2 = (Properties) mandatoryProperties.clone();
        p2.setProperty(USER_CONN_KEY, "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.connect(userURL, p2),
                "The connection should fail because of a user mismatch between the URI and the properties");

        Properties p3 = (Properties) mandatoryProperties.clone();
        p3.setProperty(PWD_CONN_KEY, "dfasdfds");
        // pwd mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.connect(userURL, p3),
                "The connection should fail because of a password mismatch between the URI and the properties");
    }

    @Test
    void testReplURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        missingConnectionSettings(d, replURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, replURL, p);
        p = (Properties) mandatoryProperties.clone();
        assertNotNull(d.connect(replURL, p));

        Properties p2 = (Properties) mandatoryProperties.clone();
        p2.setProperty(USER_CONN_KEY, "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.connect(replURL, p2),
                "The connection should fail because of a user mismatch between the URI and the properties");

        Properties p3 = (Properties) mandatoryProperties.clone();
        p3.setProperty(PWD_CONN_KEY, "dfasdfds");
        // pwd mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.connect(replURL, p3),
                "The connection should fail because of a password mismatch between the URI and the properties");
    }

    @Test
    void testGetPropertyInfo() throws SQLException {
        MongoDriver d = new MongoDriver();

        // Should not throw, even with null for Properties.
        DriverPropertyInfo[] res = d.getPropertyInfo(basicURL, null);
        assertEquals(1, res.length);
        assertEquals(MongoDriver.DATABASE, res[0].name);

        Properties p = new Properties();
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(1, res.length);
        assertEquals(MongoDriver.DATABASE, res[0].name);

        p.setProperty(USER_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(2, res.length);
        assertEquals(MongoDriver.DATABASE, res[0].name);
        assertEquals("password", res[1].name);

        p = (Properties) mandatoryProperties.clone();
        p.setProperty(USER_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(1, res.length);
        assertEquals("password", res[0].name);

        p = new Properties();
        p.setProperty(PWD_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(2, res.length);
        assertEquals(MongoDriver.DATABASE, res[0].name);
        assertEquals("user", res[1].name);

        p = (Properties) mandatoryProperties.clone();
        p.setProperty(PWD_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(1, res.length);
        assertEquals("user", res[0].name);
    }

    @Test
    void testDialectProperty() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = (Properties) mandatoryProperties.clone();
        Connection c;

        // dialect not set defaults to MongoSQLConnection
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);
        assertTrue(c instanceof MongoSQLConnection);

        // dialect set to "mysql" results in MySQLConnection
        p.setProperty("dialect", "MySQL");
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);
        assertTrue(c instanceof MySQLConnection);

        // dialect set to "mongosql" results in MongoSQLConnection
        p.setProperty("dialect", "MongoSQL");
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);
        assertTrue(c instanceof MongoSQLConnection);

        // dialect set to "mongosql" and conversionMode set results in Exception
        p.setProperty("conversionMode", "relaxed");
        assertThrows(
                SQLException.class,
                () -> d.connect(basicURL, p),
                "The connection should fail because conversionMode does not apply to mongosql dialect");

        // dialect set to invalid dialect results in Exception
        p.remove("conversionMode");
        p.setProperty("dialect", "invalid");
        assertThrows(
                SQLException.class,
                () -> d.connect(basicURL, p),
                "The connection should fail because dialect is invalid");
    }

    @Test
    void testInvalidLoggingLevel() throws Exception {
        // Default connection settings.
        // No logging. No files are created, nothing is logged.
        Properties props = new Properties();
        props.setProperty(MongoDriver.LOG_LEVEL, "NOT_A_LOG_LEVEL");
        assertThrows(
                SQLException.class,
                () -> createConnectionAndVerifyLogFileExists(props),
                "Expected connection to fail because log level is invalid.");
    }

    @Test
    void testLoggingOff() throws Exception {
        // Default connection settings.
        // No logging. No files are created, nothing is logged.
        Properties props = new Properties();
        setLogDir(props);
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);

        // Clean-up
        cleanupLoggingTest(conn, props);
    }

    @Test
    void testLoggingSevere() throws Exception {
        // Creates a log file for the connection. Only logs error.
        // Connection is successful, the log file will be empty.
        Properties props = new Properties();
        setLogDir(props);
        props.setProperty(MongoDriver.LOG_LEVEL, Level.SEVERE.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        File logFile = getLogFile(props);
        conn.getMetaData();
        // The file is still empty because no exception was thrown.
        assertTrue(logFile.length() == 0);
        // Clean-up
        cleanupLoggingTest(conn, props);
    }

    @Test
    void testLoggingSevereWithError() throws Exception {
        // Creates a log file for the connection. Only logs error.
        // Connection is successful, the log file will be empty.
        Properties props = new Properties();
        setLogDir(props);
        props.setProperty(MongoDriver.LOG_LEVEL, Level.SEVERE.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        try {
            conn.getTypeMap(); // Call will fail with a SQLFeatureNotSupportedException
            fail(
                    "A SQLFeatureNotSupportedException was expected, but the call to getTypeMap() succeeded.");
        } catch (SQLFeatureNotSupportedException e) {
            // Expected. Keep going.
        }
        File logFile = getLogFile(props);
        // The file now contains the log entry for the exception
        assertTrue(logFile.length() > 0);
        checkLogContent(
                logFile,
                "[SEVERE] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoConnection: Error in MongoConnection.getTypeMap()",
                1);
        // Clean-up
        cleanupLoggingTest(conn, props);
    }

    @Test
    void testLoggingFiner() throws Exception {
        // Creates a log file for the connection. Log public method entries.
        // Connection is successful, the log file will contain logs.
        Properties props = new Properties();
        setLogDir(props);
        props.setProperty(MongoDriver.LOG_LEVEL, Level.FINER.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        File logFile = getLogFile(props);
        conn.getMetaData();
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection: >> getMetaData()",
                1);

        // Clean-up
        cleanupLoggingTest(conn, props);
    }

    @Test
    void testCustomLogDir() throws Exception {
        // Creates a log file for the connection in the custom directory.
        // Log public method entries.
        // Connection is successful, the log file will contain logs.
        Properties props = new Properties();
        props.setProperty(MongoDriver.LOG_LEVEL, Level.FINER.getName());
        File specialLogDir = new File(new File(".").getAbsolutePath(), "customLogDir");
        if (!specialLogDir.exists()) {
            specialLogDir.mkdir();
        }
        props.setProperty(MongoDriver.LOG_DIR, specialLogDir.getAbsolutePath());

        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        File logFile = getLogFile(props);
        conn.getMetaData();
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection: >> getMetaData()",
                1);
        // Clean-up
        cleanupLoggingTest((MongoConnection) conn, props);
    }

    @Test
    void testInvalidCustomLogDir() throws Exception {
        // Set the custom log dir to an invalid path
        // Connection is not successful.
        Properties props = new Properties();
        File specialLogDir = new File(new File("."), "ThisIsNotAValidPath");
        props.setProperty(MongoDriver.LOG_DIR, specialLogDir.getAbsolutePath());
        assertThrows(
                SQLException.class,
                () -> createConnectionAndVerifyLogFileExists(props),
                "Expected to fail because the logging directory does not exist.");
    }

    @Test
    void testLogDirIsConsole() throws Exception {

        Properties props = new Properties();
        props.setProperty(MongoDriver.LOG_DIR, MongoDriver.LOG_TO_CONSOLE);
        props.setProperty(MongoDriver.LOG_LEVEL, Level.FINER.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        try {
            getLogFile(props);
            fail("There should be no log file since we are logging to console.");
        } catch (Exception e) {
            assertEquals(NOT_LOGGING_TO_FILE_ERROR, e.getMessage());
        }

        // Clean-up
        cleanupLoggingTest(conn, props);
    }

    @Test
    void testMultipleConnectionSameLogDir() throws Exception {
        // Set the custom log dir to an invalid path
        // Connection is not successful.
        Properties props = new Properties();
        setLogDir(props);
        // Create a first connection with level FINER
        props.setProperty(MongoDriver.LOG_LEVEL, Level.FINER.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);

        // Create a first connection with level FINER
        Properties props2 = new Properties(props);
        props2.setProperty(MongoDriver.LOG_LEVEL, Level.INFO.getName());
        MongoConnection conn2 = createConnectionAndVerifyLogFileExists(props2);

        // Validate log content
        File logFile = getLogFile(props);
        File logFile2 = getLogFile(props2);
        assertEquals(logFile.getAbsolutePath(), logFile2.getAbsolutePath());
        checkLogContent(
                logFile,
                "[INFO] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection <init>: Dialect is MongoSQL",
                1);

        checkLogContent(
                logFile,
                "[INFO] [c-"
                        + conn2.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection <init>: Dialect is MongoSQL",
                1);

        conn.getMetaData();
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection: >> getMetaData()",
                1);
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn2.connectionId
                        + "] com.mongodb.jdbc.MongoSQLConnection: >> getMetaData()",
                0);

        try {
            conn2.getTypeMap(); // Call will fail with a SQLFeatureNotSupportedException
            fail();
        } catch (SQLFeatureNotSupportedException e) {
            // Expected. Keep going.
        }

        checkLogContent(
                logFile,
                "[SEVERE] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoConnection: Error in MongoConnection.getTypeMap()",
                0);

        checkLogContent(
                logFile,
                "[SEVERE] [c-"
                        + conn2.connectionId
                        + "] com.mongodb.jdbc.MongoConnection: Error in MongoConnection.getTypeMap()",
                1);

        // Clean-up
        cleanupLoggingTest(conn, props);
        cleanupLoggingTest(conn2, props2);
    }

    /**
     * Set the LogDir property.
     *
     * @param props The properties to add LogDir to.
     */
    private void setLogDir(Properties props) {
        String logDirPath = CURRENT_DIR + File.separator + connectionCounter.incrementAndGet();
        File logDir = new File(logDirPath);
        logDir.mkdir();
        props.setProperty(MongoDriver.LOG_DIR, logDirPath);
    }

    /**
     * Check that the log file contains the expect number of lines and the filtered line.
     *
     * @param logFile The log file to verify.
     * @param filter The filter to apply on the log file to filter log lines.
     * @param expectedFilteredLineCount The expected number of filtered log lines.
     * @throws IOException If an error occurs reading the log files.
     */
    private void checkLogContent(File logFile, String filter, int expectedFilteredLineCount)
            throws IOException {
        // The file now contains the log entry for getMetadata
        assertTrue(logFile.length() > 0);
        long logLinesCount =
                Files.lines(Paths.get(logFile.getAbsolutePath()))
                        .filter(s -> s.contains(filter))
                        .count();
        assertEquals(expectedFilteredLineCount, logLinesCount);
    }

    /**
     * Creates a new Connection and check if the related log file exist if it should.
     *
     * @param loggingTestProps The logging properties.
     * @throws Exception If an error occurs.
     */
    private MongoConnection createConnectionAndVerifyLogFileExists(Properties loggingTestProps)
            throws Exception {
        MongoDriver d = new MongoDriver();
        loggingTestProps.setProperty("dialect", "MongoSQL");
        loggingTestProps.setProperty("database", "admin");

        MongoConnection connection = d.getUnvalidatedConnection(userURL, loggingTestProps);
        assertNotNull(connection);

        if (null != loggingTestProps.getProperty(MongoDriver.LOG_LEVEL)
                && !loggingTestProps.getProperty(MongoDriver.LOG_LEVEL).equals(Level.OFF.getName())
                && !logToConsole(loggingTestProps)) {
            assertTrue(getLogFile(loggingTestProps).exists());
        }
        return connection;
    }

    /**
     * Get the log file which will be associated with the connection when/if logging is turned on.
     *
     * @param loggingTestProps The connection settings related to logging.
     * @return The log file.
     * @throws Exception If the connection is not logging to files (either console or no logging).
     */
    private File getLogFile(Properties loggingTestProps) throws Exception {
        if (loggingTestProps == null || logToConsole(loggingTestProps)) {
            throw new Exception(NOT_LOGGING_TO_FILE_ERROR);
        }

        File logFile =
                new File(
                        loggingTestProps.getProperty(MongoDriver.LOG_DIR)
                                + File.separator
                                + "connection.log");
        return logFile;
    }

    /**
     * Check if the connection is going to log to Console or a file.
     *
     * @param loggingTestProps The connection settings related to logging.
     * @return True if logging to console, false if logging to a file.
     * @throws Exception If the connection is not logging.
     */
    private boolean logToConsole(Properties loggingTestProps) throws Exception {
        if (loggingTestProps == null) {
            throw new Exception("Logging not enabled.");
        } else {
            return loggingTestProps.getProperty(MongoDriver.LOG_DIR) == null
                    || loggingTestProps
                            .getProperty(MongoDriver.LOG_DIR)
                            .equalsIgnoreCase(MongoDriver.LOG_TO_CONSOLE);
        }
    }

    /**
     * Close the connection and remove the log file if it exists.
     *
     * @param conn The connection.
     * @param props The connection settings.
     */
    private void cleanupLoggingTest(MongoConnection conn, Properties props) {
        try {
            conn.close();
            File logDir = new File(props.getProperty(MongoDriver.LOG_DIR));
            if (logDir.exists()) {
                for (File file : logDir.listFiles()) {
                    // Delete log file before delete directory because
                    // the directory must be empty for delete to work
                    file.delete();
                }
                logDir.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Ignore clean-up error if any
        }
    }
}
