package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mongodb.jdbc.MongoConnection;
import com.mongodb.jdbc.MongoDriver;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public abstract class MongoIntegrationTest {
    private static final String CURRENT_DIR =
            Paths.get(".").toAbsolutePath().normalize().toString();

    /**
     * Creates a new connection.
     *
     * @param extraProps Extra properties on top of the default ones that the class implementating
     *     it is using.
     * @return The connection.
     * @throws SQLException If the connection can not be created.
     */
    public abstract MongoConnection getBasicConnection(Properties extraProps) throws SQLException;

    /** Simple callable used to spawn a new statement and execute a query. */
    public class SimpleQueryExecutor implements Callable<Void> {
        private final Connection conn;
        private final String query;

        public SimpleQueryExecutor(Connection conn, String query) {
            this.conn = conn;
            this.query = query;
        }

        @Override
        public Void call() throws Exception {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery(query);
            }
            return null;
        }
    }

    /**
     * Verifies that concurrent connections and statements are not impacting the logging
     * capabilities. Concurrent connection must be able to create log files with no issues.
     * Concurrent statements must be able to write their messages in the same log file.
     */
    @Test
    public void testLoggingWithParallelConnectionAndStatementExec() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();

        // Connection with no logging.
        MongoConnection noLogging = connect(null);
        // Connection only logging exceptions.
        MongoConnection logErrorOnly = connect(Level.SEVERE);
        // Connection logging all public method entries of the JDBC interface.
        MongoConnection logEntries = connect(Level.FINER);
        try {
            addSimpleQueryExecTasks(tasks, noLogging);
            addSimpleQueryExecTasks(tasks, logErrorOnly);
            addSimpleQueryExecTasks(tasks, logEntries);
            executor.invokeAll(tasks);
        } finally {
            executor.awaitTermination(1, TimeUnit.SECONDS);

            // Verify that there is only one log file
            List<File> logFiles =
                    Files.list(Paths.get(CURRENT_DIR))
                            .map(Path::toFile)
                            .filter(
                                    p ->
                                            p.isFile()
                                                    && p.getName()
                                                            .matches("connection.log(.\\d+)*"))
                            .collect(Collectors.toList());

            assertEquals(
                    1, logFiles.size(), "Expected only one log file, but found " + logFiles.size());

            if (noLogging != null) {
                cleanUp(noLogging);
            }
            if (logErrorOnly != null) {
                cleanUp(logErrorOnly);
            }
            if (logEntries != null) {
                cleanUp(logEntries);
            }
        }
    }

    /**
     * Connect with the given logging level.
     *
     * @param logLevel The log level or null if not logging.
     * @return the connection.
     * @throws SQLException If an error occurs during the connection process.
     */
    private MongoConnection connect(Level logLevel) throws SQLException {
        Properties loggingProps = new Properties();
        if (null != logLevel) {
            loggingProps.setProperty(MongoDriver.LOG_LEVEL, logLevel.getName());
        }

        // Log files will be created in the current directory
        loggingProps.setProperty(MongoDriver.LOG_DIR, CURRENT_DIR);
        return getBasicConnection(loggingProps);
    }

    /**
     * Add taks to execute a valid and an invalid statement via the given connection.
     *
     * @param tasks The tasks list to add new tasks to.
     * @param conn The connection to use to create new statements.
     * @throws SQLException If an error occurs when creating a new statement.
     */
    private void addSimpleQueryExecTasks(List<Callable<Void>> tasks, Connection conn)
            throws SQLException {
        // Connection with no logging and a valid query to execute.
        tasks.add(new MongoIntegrationTest.SimpleQueryExecutor(conn, "SELECT 1"));
        // Connection with no logging and an invalid query to execute.
        tasks.add(new MongoIntegrationTest.SimpleQueryExecutor(conn, "INVALID QUERY TO EXECUTE"));
    }

    /**
     * Clean-up after the logging test. It will close the connection and delete the log file if it
     * exists.
     *
     * @param conn The connection.
     */
    private void cleanUp(MongoConnection conn) {
        try {
            conn.close();
            File logFile = new File(CURRENT_DIR + File.separator + "connection.log");
            if (logFile.exists()) {
                logFile.delete();
            }
        } catch (Exception e) {
            // Ignore clean-up exceptions
            System.out.println("Clean-up error ignored.");
            e.printStackTrace();
        }
    }
}
