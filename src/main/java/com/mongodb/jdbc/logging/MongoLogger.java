package com.mongodb.jdbc.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoLogger {
    private static final String ENTRY_PREFIX = ">> ";

    private Logger logger;
    private Integer connectionId;
    private Integer statementId;

    /**
     * Gets a logger to log information before a connection has been made.
     *
     * @param logger The logger.
     */
    public MongoLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Gets a logger, tied to a connection. Used for logging after a connection has been created.
     *
     * @param className The classname to find the associated logger.
     * @param connectionId The connection id.
     */
    public MongoLogger(String className, int connectionId) {
        this.logger = MongoLoggerUtils.getLogger(className, connectionId);
        this.connectionId = connectionId;
    }

    /**
     * Gets a logger, tied to a connection and a statement. Used for logging after a statement has
     * been created.
     *
     * @param className The classname to find the associated logger.
     * @param connectionId The connection id.
     * @param statementId The statement id.
     */
    public MongoLogger(String className, int connectionId, int statementId) {
        this.logger = MongoLoggerUtils.getLogger(className, connectionId);
        this.connectionId = connectionId;
        this.statementId = statementId;
    }

    /**
     * Log a method entry. This is a convenience method that can be used to log entry to a method. A
     * LogRecord with message ">> callSignature", log level FINER, and the given sourceName is
     * logged.
     *
     * @param sourceName Name of class that issued the logging request
     * @param callSignature The call signature, method and arguments, to log.
     */
    protected void logMethodEntry(String sourceName, String callSignature) {
        if (null != logger) {
            logger.logp(
                    Level.FINER,
                    addConnectionStatementIdsToSourceName(sourceName),
                    null,
                    ENTRY_PREFIX + callSignature);
        }
    }

    protected void logError(String sourceName, String msg, Throwable thrown) {
        if (null != logger) {
            logger.logp(
                    Level.SEVERE,
                    addConnectionStatementIdsToSourceName(sourceName),
                    null,
                    msg,
                    thrown);
        }
    }

    /**
     * Log a message, with no arguments.
     *
     * <p>If the logger is currently enabled for the given message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param msg The string message (or a key in the message catalog)
     */
    public void log(Level level, String msg) {
        if (null != logger) {
            // Get access to caller
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement ste = stacktrace[2];
            String sourceClassName = ste.getClassName();
            String methodName = ste.getMethodName();

            logger.logp(
                    level, addConnectionStatementIdsToSourceName(sourceClassName), methodName, msg);
        }
    }

    /**
     * Log a message, with an array of object arguments.
     *
     * <p>If the logger is currently enabled for the given message level then a corresponding
     * LogRecord is created and forwarded to all the registered output Handler objects.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param params array of parameters to the message
     */
    public void log(Level level, String msg, Object params[]) {
        if (null != logger) {
            // Get access to caller
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement ste = stacktrace[2];
            String sourceClassName = ste.getClassName();
            String methodName = ste.getMethodName();

            logger.logp(
                    level,
                    addConnectionStatementIdsToSourceName(sourceClassName),
                    methodName,
                    msg,
                    params);
        }
    }

    /**
     * Log a message, with associated Throwable information.
     *
     * <p>If the logger is currently enabled for the given message level then the given arguments
     * are stored in a LogRecord which is forwarded to all registered output handlers.
     *
     * <p>Note that the thrown argument is stored in the LogRecord thrown property, rather than the
     * LogRecord parameters property. Thus it is processed specially by output Formatters and is not
     * treated as a formatting parameter to the LogRecord message property.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param thrown Throwable associated with log message.
     */
    public void log(Level level, String msg, Throwable thrown) {
        if (null != logger) {
            // Get access to caller
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement ste = stacktrace[2];
            String sourceClassName = ste.getClassName();
            String methodName = ste.getMethodName();

            logger.logp(
                    level,
                    addConnectionStatementIdsToSourceName(sourceClassName),
                    methodName,
                    msg,
                    thrown);
        }
    }

    /**
     * Add the connection and statement ids before the source name if they are available.
     *
     * @param sourceName The source name.
     * @return the source name with the connection and statement ids suffixes.
     */
    protected String addConnectionStatementIdsToSourceName(String sourceName) {
        // Add the statement id
        if (statementId != null) {
            sourceName = "[stmt-" + statementId + "] " + sourceName;
        }
        // Add the connection id
        if (connectionId != null) {
            sourceName = "[c-" + connectionId + "] " + sourceName;
        }

        return sourceName;
    }
}
