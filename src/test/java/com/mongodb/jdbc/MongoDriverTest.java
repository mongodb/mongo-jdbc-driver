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

import static com.mongodb.jdbc.MongoDriver.MongoJDBCProperty.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.client.MongoClient;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
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

    static final String localhost = "mongodb://localhost";
    static final String USER = "AzureDiamond";
    static final String PWD = "hunter2";
    static final String DB = "foo";
    static final String basicURL = "jdbc:mongodb://localhost";
    static final String userNoPWDURL = "jdbc:mongodb://foo@localhost/admin";
    static final String userURL = "jdbc:mongodb://foo:bar@localhost";
    static final String jdbcUserURL = "jdbc:mongodb://jdbc:bar@localhost";
    static final String dbInURL = "jdbc:mongodb://localhost/foo?authSource=admin";
    static final String userOIDCURL =
            "jdbc:mongodb://foo@localhost/admin?authMechanism=MONGODB-OIDC";
    static final String oidcURL =
            "jdbc:mongodb://localhost/foo?authMechanism=MONGODB-OIDC&authSource=admin";
    static final String userPwdOIDCURL =
            "jdbc:mongodb://foo:bar@localhost?authMechanism=MONGODB-OIDC";

    // Even though ADF does not support replSets, this tests that we handle these URLs properly
    // for the future.
    static final String replURL = "jdbc:mongodb://foo:bar@localhost:27017,localhost:28910";

    private static final String CURRENT_DIR =
            Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String NOT_LOGGING_TO_FILE_ERROR = "Not logging to files.";

    // Using an atomicInteger in case Junit ran with parallel execution enabled
    private static AtomicInteger connectionCounter = new AtomicInteger();

    private static final String USER_CONN_KEY = "user";
    private static final String PWD_CONN_KEY = "password";
    private static final String DB_CONN_KEY = "database";

    private static final Properties mandatoryProperties = setProperties();

    private static Properties setProperties() {
        Properties props = new Properties();
        props.setProperty(DATABASE.getPropertyName(), "test");

        return props;
    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBasicURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Missing mandatory 'DATABASE.getPropertyName()' property
        missingConnectionSettings(d, basicURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, basicURL, p);

        // user without password should throw.
        p = (Properties) mandatoryProperties.clone();
        p.setProperty(USER_CONN_KEY, "user");
        missingConnectionSettings(d, basicURL, p);

        // once property is set, it should be fine.
        p.setProperty(PWD_CONN_KEY, "pwd");
        assertNotNull(d.getUnvalidatedConnection(basicURL, p));
    }

    @Test
    void testURIDBURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, "AzureDiamond");
        p.setProperty(PWD_CONN_KEY, "hunter2");
        assertNotNull(d.getUnvalidatedConnection(dbInURL, p));
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

        assertNotNull(d.getUnvalidatedConnection(jdbcUserURL, mandatoryProperties));

        // changing user name from `jdbc` should throw.
        Properties p = (Properties) mandatoryProperties.clone();
        p.setProperty(USER_CONN_KEY, "jdbc2");
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(jdbcUserURL, p),
                "The connection should fail because of a user mismatch between the URI and the properties");
    }

    @Test
    void testUserURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        missingConnectionSettings(d, userURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, userURL, p);
        p = (Properties) mandatoryProperties.clone();
        assertNotNull(d.getUnvalidatedConnection(userURL, p));

        // This is not a mismatch, because we assume that if an auth DATABASE.getPropertyName() is missing
        // in the URI, even though default is admin, the user would prefer whatever is in
        // the passed Properties.
        p.setProperty("authDATABASE.getPropertyName()", "admin2");
        assertNotNull(d.getUnvalidatedConnection(userURL, p));

        Properties p2 = (Properties) mandatoryProperties.clone();
        p2.setProperty(USER_CONN_KEY, "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(userURL, p2),
                "The connection should fail because of a user mismatch between the URI and the properties");

        Properties p3 = (Properties) mandatoryProperties.clone();
        p3.setProperty(PWD_CONN_KEY, "dfasdfds");
        // pwd mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(userURL, p3),
                "The connection should fail because of a password mismatch between the URI and the properties");
    }

    @Test
    void testReplURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        missingConnectionSettings(d, replURL, null);

        Properties p = new Properties();
        missingConnectionSettings(d, replURL, p);
        p = (Properties) mandatoryProperties.clone();
        assertNotNull(d.getUnvalidatedConnection(replURL, p));

        Properties p2 = (Properties) mandatoryProperties.clone();
        p2.setProperty(USER_CONN_KEY, "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(replURL, p2),
                "The connection should fail because of a user mismatch between the URI and the properties");

        Properties p3 = (Properties) mandatoryProperties.clone();
        p3.setProperty(PWD_CONN_KEY, "dfasdfds");
        // pwd mismatch should throw.
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(replURL, p3),
                "The connection should fail because of a password mismatch between the URI and the properties");
    }

    @Test
    void testMongoDBOIDCAuthMechanismWithUsername() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();

        assertNotNull(d.getUnvalidatedConnection(userOIDCURL, p));
    }

    @Test
    void testMongoDBOIDCAuthMechanismWithoutUsername() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();

        assertNotNull(d.getUnvalidatedConnection(oidcURL, p));
    }

    @Test
    void testMongoDBOIDCAuthMechanismWithPassword() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();

        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(userPwdOIDCURL, p),
                "The connection should fail because password is not allowed with MONGODB-OIDC authentication.");
    }

    @Test
    void testGetPropertyInfo() throws SQLException {
        MongoDriver d = new MongoDriver();

        // Should not throw, even with null for Properties.
        DriverPropertyInfo[] res = d.getPropertyInfo(basicURL, null);
        assertEquals(1, res.length);
        assertEquals(DATABASE.getPropertyName(), res[0].name);

        Properties p = new Properties();
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(1, res.length);
        assertEquals(DATABASE.getPropertyName(), res[0].name);

        p.setProperty(USER_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(2, res.length);
        assertEquals(DATABASE.getPropertyName(), res[0].name);
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
        assertEquals(DATABASE.getPropertyName(), res[0].name);
        assertEquals("user", res[1].name);

        p = (Properties) mandatoryProperties.clone();
        p.setProperty(PWD_CONN_KEY, "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(1, res.length);
        assertEquals("user", res[0].name);
    }

    @Test
    void testInvalidLoggingLevel() throws Exception {
        // Default connection settings.
        // No logging. No files are created, nothing is logged.
        Properties props = new Properties();
        props.setProperty(LOG_LEVEL.getPropertyName(), "NOT_A_LOG_LEVEL.getPropertyName()");
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
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.SEVERE.getName());
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
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.SEVERE.getName());
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
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.FINER.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        File logFile = getLogFile(props);
        conn.getMetaData();
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoConnection: >> getMetaData()",
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
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.FINER.getName());
        File specialLogDir = new File(new File(".").getAbsolutePath(), "customLogDir");
        if (!specialLogDir.exists()) {
            specialLogDir.mkdir();
        }
        props.setProperty(LOG_DIR.getPropertyName(), specialLogDir.getAbsolutePath());

        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);
        File logFile = getLogFile(props);
        conn.getMetaData();
        checkLogContent(
                logFile,
                "[FINER] [c-"
                        + conn.connectionId
                        + "] com.mongodb.jdbc.MongoConnection: >> getMetaData()",
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
        props.setProperty(LOG_DIR.getPropertyName(), specialLogDir.getAbsolutePath());
        assertThrows(
                SQLException.class,
                () -> createConnectionAndVerifyLogFileExists(props),
                "Expected to fail because the logging directory does not exist.");
    }

    @Test
    void testLogDirIsConsole() throws Exception {

        Properties props = new Properties();
        props.setProperty(LOG_DIR.getPropertyName(), MongoDriver.LOG_TO_CONSOLE);
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.FINER.getName());
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
        props.setProperty(LOG_LEVEL.getPropertyName(), Level.FINER.getName());
        MongoConnection conn = createConnectionAndVerifyLogFileExists(props);

        // Create a first connection with level FINER
        Properties props2 = new Properties(props);
        props2.setProperty(LOG_LEVEL.getPropertyName(), Level.INFO.getName());
        MongoConnection conn2 = createConnectionAndVerifyLogFileExists(props2);

        // Validate log content
        File logFile = getLogFile(props);
        File logFile2 = getLogFile(props2);
        assertEquals(logFile.getAbsolutePath(), logFile2.getAbsolutePath());
        String connInitPattern =
                "[INFO] [c-{connectionId}] com.mongodb.jdbc.MongoConnection initConnectionLogger: Connecting using MongoDB Atlas SQL interface JDBC Driver";
        checkLogContent(
                logFile,
                connInitPattern.replace("{connectionId}", String.valueOf(conn.connectionId)),
                1);

        checkLogContent(
                logFile,
                connInitPattern.replace("{connectionId}", String.valueOf(conn2.connectionId)),
                1);

        conn.getMetaData();
        String connGetMetadataPattern =
                "[FINER] [c-{connectionId}] com.mongodb.jdbc.MongoConnection: >> getMetaData()";

        checkLogContent(
                logFile,
                connGetMetadataPattern.replace("{connectionId}", String.valueOf(conn.connectionId)),
                1);
        checkLogContent(
                logFile,
                connGetMetadataPattern.replace(
                        "{connectionId}", String.valueOf(conn2.connectionId)),
                0);

        try {
            conn2.getTypeMap(); // Call will fail with a SQLFeatureNotSupportedException
            fail();
        } catch (SQLFeatureNotSupportedException e) {
            // Expected. Keep going.
        }

        String errPattern =
                "[SEVERE] [c-{connectionId}] com.mongodb.jdbc.MongoConnection: Error in MongoConnection.getTypeMap()";
        checkLogContent(
                logFile,
                errPattern.replace("{connectionId}", String.valueOf(conn.connectionId)),
                0);

        checkLogContent(
                logFile,
                errPattern.replace("{connectionId}", String.valueOf(conn2.connectionId)),
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
        props.setProperty(LOG_DIR.getPropertyName(), logDirPath);
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
        loggingTestProps.setProperty(DATABASE.getPropertyName(), "admin");

        MongoConnection connection = d.getUnvalidatedConnection(userURL, loggingTestProps);
        assertNotNull(connection);

        if (null != loggingTestProps.getProperty(LOG_LEVEL.getPropertyName())
                && !loggingTestProps
                        .getProperty(LOG_LEVEL.getPropertyName())
                        .equals(Level.OFF.getName())
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
                        loggingTestProps.getProperty(LOG_DIR.getPropertyName())
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
            return loggingTestProps.getProperty(LOG_DIR.getPropertyName()) == null
                    || loggingTestProps
                            .getProperty(LOG_DIR.getPropertyName())
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
            File logDir = new File(props.getProperty(LOG_DIR.getPropertyName()));
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

    @Test
    void testClientInfoProperty() throws Exception {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        Connection c;
        p.setProperty(DATABASE.getPropertyName(), "test");

        // ClientInfo not set succeeds
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);

        // Invalid ClientInfo property results in Exception
        p.setProperty(CLIENT_INFO.getPropertyName(), "InvalidFormat");
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(basicURL, p),
                "The connection should fail because expected format is <name>+<version>.");

        p.setProperty(CLIENT_INFO.getPropertyName(), "name+version");
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);
    }

    @Test
    void testExtJsonModeProperty() throws Exception {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        Connection c;
        p.setProperty(DATABASE.getPropertyName(), "test");

        // ExtJsonMode not set succeeds
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);

        // Invalid ExtJsonMode property results in Exception
        p.setProperty(EXT_JSON_MODE.getPropertyName(), "InvalidMode");
        assertThrows(
                SQLException.class,
                () -> d.getUnvalidatedConnection(basicURL, p),
                "The connection should fail because expected input is RELAXED or EXTENDED.");

        p.setProperty(EXT_JSON_MODE.getPropertyName(), "EXTENDED");
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);

        p.setProperty(EXT_JSON_MODE.getPropertyName(), "RELAXED");
        c = d.getUnvalidatedConnection(basicURL, p);
        assertNotNull(c);
    }

    @Test
    void testClientCaching() throws Exception {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        p.setProperty(DATABASE.getPropertyName(), "test");

        // Create three connections with the same properties
        MongoConnection conn1 = d.getUnvalidatedConnection(basicURL, p);
        MongoConnection conn2 = d.getUnvalidatedConnection(basicURL, p);
        MongoConnection conn3 = d.getUnvalidatedConnection(basicURL, p);

        // Verify that the connections share the same MongoClient instance
        assertSame(conn1.getMongoClient(), conn2.getMongoClient());
        assertSame(conn1.getMongoClient(), conn3.getMongoClient());
    }

    @Test
    void testClientCachingWithDifferentKeyedProperties() throws Exception {
        MongoDriver d = new MongoDriver();

        Properties p1 = new Properties();
        p1.setProperty(DATABASE.getPropertyName(), "test");
        p1.setProperty(CLIENT_INFO.getPropertyName(), "app1+1.0");
        MongoConnection conn1 = d.getUnvalidatedConnection(basicURL, p1);

        Properties p2 = new Properties();
        p2.setProperty(DATABASE.getPropertyName(), "test");
        p2.setProperty(CLIENT_INFO.getPropertyName(), "app2+1.0");
        // Create connection with different client info
        MongoConnection conn2 = d.getUnvalidatedConnection(basicURL, p2);

        assertNotSame(conn1.getMongoClient(), conn2.getMongoClient());
    }

    @Test
    void testClientCachingWithDifferentUnkeyedProperties() throws Exception {
        // Testing with different JSON modes, a property that is not part of the unique key used
        // for caching.
        MongoDriver d = new MongoDriver();

        // Create connections with different extended JSON mode
        Properties p1 = new Properties();
        p1.setProperty(DATABASE.getPropertyName(), "test");
        p1.setProperty(EXT_JSON_MODE.getPropertyName(), "RELAXED");

        Properties p2 = new Properties();
        p2.setProperty(DATABASE.getPropertyName(), "test");
        p2.setProperty(EXT_JSON_MODE.getPropertyName(), "EXTENDED");

        MongoConnection conn1 = d.getUnvalidatedConnection(basicURL, p1);
        MongoConnection conn2 = d.getUnvalidatedConnection(basicURL, p2);

        assertSame(conn1.getMongoClient(), conn2.getMongoClient());
    }

    @Test
    void testMongoDBX509AuthMechanismWithoutPassphrase() throws SQLException {
        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        p.setProperty(DATABASE.getPropertyName(), "test");

        String x509URL = "jdbc:mongodb://localhost/test?authMechanism=MONGODB-X509";
        assertThrows(
                IllegalStateException.class,
                () -> d.getUnvalidatedConnection(x509URL, p),
                "Expected to fail because x509Passphrase is missing.");
    }

    @Test
    void testMongoDBX509WithPemPathAndPassphrase() throws SQLException {
        String passphrase = "passphrase";

        MongoDriver d = new MongoDriver();
        Properties p = new Properties();
        p.setProperty(DATABASE.getPropertyName(), "test");
        p.setProperty(
                MongoDriver.MongoJDBCProperty.X509_PEM_PATH.getPropertyName(), "valid-path.pem");
        p.setProperty("password", passphrase);

        String x509URL = "jdbc:mongodb://localhost/test?authMechanism=MONGODB-X509";

        MongoDriver.MongoConnectionConfig config = d.getConnectionSettings(x509URL, p);

        assertNotNull(config.connectionString, "Connection string should be created.");
        assertNull(config.connectionString.getUsername(), "Username should be null.");
        assertNull(config.connectionString.getPassword(), "Password should be null.");
        assertEquals(
                AuthenticationMechanism.MONGODB_X509,
                config.connectionString.getCredential().getAuthenticationMechanism(),
                "Authentication mechanism should be MONGODB_X509.");
        assertArrayEquals(
                passphrase.toCharArray(),
                config.x509Passphrase,
                "x509Passphrase should match the provided value.");
    }

    private int getClientCacheSize() throws NoSuchFieldException, IllegalAccessException {
        Field cacheField = MongoDriver.class.getDeclaredField("mongoClientCache");
        cacheField.setAccessible(true);
        ConcurrentHashMap<Integer, WeakReference<MongoClient>> cache =
                (ConcurrentHashMap<Integer, WeakReference<MongoClient>>) cacheField.get(null);

        Field lockField = MongoDriver.class.getDeclaredField("mongoClientCacheLock");
        lockField.setAccessible(true);
        ReadWriteLock lock = (ReadWriteLock) lockField.get(null);

        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void clearClientCache() throws NoSuchFieldException, IllegalAccessException {
        Field cacheField = MongoDriver.class.getDeclaredField("mongoClientCache");
        cacheField.setAccessible(true);
        ConcurrentHashMap<Integer, WeakReference<MongoClient>> cache =
                (ConcurrentHashMap<Integer, WeakReference<MongoClient>>) cacheField.get(null);

        Field lockField = MongoDriver.class.getDeclaredField("mongoClientCacheLock");
        lockField.setAccessible(true);
        ReadWriteLock lock = (ReadWriteLock) lockField.get(null);

        lock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    void testDisableClientCacheAux(String value)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        MongoDriver d = new MongoDriver();
        clearClientCache();
        Properties p = new Properties();
        p.setProperty(DATABASE.getPropertyName(), "test");
        p.setProperty(MongoDriver.MongoJDBCProperty.DISABLE_CLIENT_CACHE.getPropertyName(), value);

        // Create two connections with the same properties
        MongoConnection conn1 = d.getUnvalidatedConnection(basicURL, p);
        MongoConnection conn2 = d.getUnvalidatedConnection(basicURL, p);

        assertEquals(
                0,
                getClientCacheSize(),
                "Client cache should be empty when DISABLE_CLIENT_CACHE is set to true.");
        // Verify that the connections do not share the same MongoClient instance
        assertNotSame(conn1.getMongoClient(), conn2.getMongoClient());
    }

    void testNoDisableClientCacheAux(String value)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        MongoDriver d = new MongoDriver();
        clearClientCache();
        Properties p = new Properties();
        p.setProperty(DATABASE.getPropertyName(), "test");
        p.setProperty(MongoDriver.MongoJDBCProperty.DISABLE_CLIENT_CACHE.getPropertyName(), value);

        // Create two connections with the same properties
        MongoConnection conn1 = d.getUnvalidatedConnection(basicURL, p);
        MongoConnection conn2 = d.getUnvalidatedConnection(basicURL, p);

        // Verify that the connections do not share the same MongoClient instance
        assertEquals(
                getClientCacheSize(),
                1,
                "Client cache should be empty when DISABLE_CLIENT_CACHE is set to true.");
        assertEquals(conn1.getMongoClient(), conn2.getMongoClient());
    }

    @Test
    void testDisableClientCache()
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        testDisableClientCacheAux("true");
        testDisableClientCacheAux("TRUE");
        testDisableClientCacheAux("TrUe");
        testDisableClientCacheAux("1");
        testDisableClientCacheAux("YeS");

        testNoDisableClientCacheAux("false");
        testNoDisableClientCacheAux("FALSE");
        testNoDisableClientCacheAux("0");
    }

    @Test
    void testNullPropValue() throws Exception {
        // Create a new Properties object.
        Properties p = new Properties();
        MongoDriver d = new MongoDriver();
        // Put a key-value pair into the Properties object, with the value being an object.
        p.put(USER_CONN_KEY, new Pair<Double, Boolean>(6.0, true));
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DB_CONN_KEY, DB);

        assertThrows(SQLException.class, () -> d.connect("jdbc:" + localhost, p));

        try {
            d.connect("jdbc:" + localhost, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testNullPropKey() throws Exception {
        // Create a new Properties object.
        Properties p = new Properties();
        MongoDriver d = new MongoDriver();
        // Put a key-value pair into the Properties object, with the value being an object.
        p.put(new Pair<Double, Boolean>(6.0, true), USER);
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DB_CONN_KEY, DB);

        assertThrows(SQLException.class, () -> d.connect("jdbc:" + localhost, p));

        try {
            d.connect("jdbc:" + localhost, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
