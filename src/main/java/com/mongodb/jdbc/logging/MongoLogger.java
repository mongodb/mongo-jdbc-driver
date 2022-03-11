package com.mongodb.jdbc.logging;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
            plog(logger, level, msg);
        }
    }

    /**
     * Log a message, which is only to be constructed if the logging level is such that the message
     * will actually be logged.
     *
     * <p>If the logger is currently enabled for the given message level then the message is
     * constructed by invoking the provided supplier function and forwarded to all the registered
     * output Handler objects.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param msgSupplier A function, which when called, produces the desired log message
     * @since 1.8
     */
    public void log(Level level, Supplier<String> msgSupplier) {
        if (null != logger) {
            plog(logger, level, msgSupplier);
        }
    }

    /**
     * Log a message, with one object parameter.
     *
     * <p>If the logger is currently enabled for the given message level then a corresponding
     * LogRecord is created and forwarded to all the registered output Handler objects.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param param1 parameter to the message
     */
    public void log(Level level, String msg, Object param1) {
        if (null != logger) {
            plog(logger, level, msg, param1);
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
            plog(logger, level, msg, params);
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
            plog(logger, level, msg, thrown);
        }
    }

    /**
     * Log a lazily constructed message, with associated Throwable information.
     *
     * <p>If the logger is currently enabled for the given message level then the message is
     * constructed by invoking the provided supplier function. The message and the given {@link
     * Throwable} are then stored in a {@link LogRecord} which is forwarded to all registered output
     * handlers.
     *
     * <p>Note that the thrown argument is stored in the LogRecord thrown property, rather than the
     * LogRecord parameters property. Thus it is processed specially by output Formatters and is not
     * treated as a formatting parameter to the LogRecord message property.
     *
     * @param level One of the message level identifiers, e.g., SEVERE
     * @param thrown Throwable associated with log message.
     * @param msgSupplier A function, which when called, produces the desired log message
     * @since 1.8
     */
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        if (null != logger) {
            plog(logger, level, thrown, msgSupplier);
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

    /**
     * -----------------------------------------------------------------------------------------------
     * Below are the private counterparts of all the log(..) methods. This way, the public facing methods
     * can check that the logger is not null and we can override the private method and add the
     * connection and statement id as the sourceclass prefix.
     * -----------------------------------------------------------------------------------------------
     */
    private void plog(Logger logger, Level level, String msg) {
        // Method body is in LoggingAspect
    }

    private void plog(Logger logger, Level level, Supplier<String> msgSupplier) {
        // Method body is in LoggingAspect
    }

    private void plog(Logger logger, Level level, String msg, Object param1) {
        // Method body is in LoggingAspect
    }

    private void plog(Logger logger, Level level, String msg, Object params[]) {
        // Method body is in LoggingAspect
    }

    private void plog(Logger logger, Level level, String msg, Throwable thrown) {
        // Method body is in LoggingAspect
    }

    private void plog(Logger logger, Level level, Throwable thrown, Supplier<String> msgSupplier) {
        // Method body is in LoggingAspect
    }
}
