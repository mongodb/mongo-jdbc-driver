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

package com.mongodb.jdbc.logging;

import com.mongodb.jdbc.MongoJsonSchema;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;

public class MongoLogger {
    private static final String ENTRY_PREFIX = ">> ";

    private Logger logger;
    private Integer connectionId;
    private Integer statementId;
    private QueryDiagnostics queryDiagnostics = new QueryDiagnostics();

    /**
     * Gets a logger, tied to a connection. Used for logging after a connection has been created.
     *
     * @param logger The logger.
     * @param connectionId The connection id.
     */
    public MongoLogger(Logger logger, int connectionId) {
        this.logger = logger;
        this.connectionId = connectionId;
    }

    /**
     * Gets a logger, tied to a connection and a statement. Used for logging after a statement has
     * been created.
     *
     * @param className The classname to find the associated logger.
     * @param parentLogger The parent logger.
     * @param statementId The statement id.
     */
    public MongoLogger(String className, MongoLogger parentLogger, int statementId) {
        createLogger(className, parentLogger);
        this.statementId = statementId;
    }

    /**
     * Gets a logger, tied to a connection but no statement. This is used for logging
     * DatabaseMetadata for example.
     *
     * @param className The classname to find the associated logger.
     * @param parentLogger The parent logger.
     */
    public MongoLogger(String className, MongoLogger parentLogger) {
        createLogger(className, parentLogger);
    }

    /**
     * Create a logger for this class and attach it to the provided parent logger.
     *
     * @param className The classname to find the associated logger.
     * @param parentLogger The parent logger.
     */
    private void createLogger(String className, MongoLogger parentLogger) {
        String loggername =
                (parentLogger.connectionId == null)
                        ? className
                        : parentLogger.connectionId + "_" + className;
        this.logger = Logger.getLogger(loggername);
        logger.setLevel(parentLogger.logger.getLevel());

        // This is a work-around for the simpler logic of calling `logger.setParent(parent); logger.setUseParentHandlers(true);`
        // after configuring the parent logger handlers for the connection.
        // This is to avoid issue with log managers which are restricting use of setParent like JBoss Log Manager for example.
        for (Handler handler : logger.getHandlers()) {
            // Clean the handler list to avoid any duplication of logs from transitive handlers
            logger.removeHandler(handler);
        }
        for (Handler handler : parentLogger.logger.getHandlers()) {
            // Add all parent handlers
            logger.addHandler(handler);
        }
        this.connectionId = parentLogger.connectionId;
    }

    /**
     * Log a method entry. This is a convenience method that can be used to log entry to a method. A
     * LogRecord with message "{@literal >>} callSignature", log level FINER, and the given
     * sourceName is logged.
     *
     * @param sourceName Name of class that issued the logging request
     * @param callSignature The call signature, method and arguments, to log.
     */
    protected void logMethodEntry(String sourceName, String callSignature) {
        if ((null != logger) && logger.isLoggable(Level.FINER)) {
            logger.logp(
                    Level.FINER,
                    addConnectionStatementIdsToSourceName(sourceName),
                    null,
                    ENTRY_PREFIX + callSignature);
        }
    }

    protected void logError(String sourceName, String msg, Throwable thrown) {
        if ((null != logger) && logger.isLoggable(Level.SEVERE)) {
            if (thrown instanceof SQLException) {
                StringBuilder sb = new StringBuilder();
                sb.append(msg);
                sb.append("\n");
                sb.append("SQL diagnostics: ");
                sb.append(getQueryDiagnostics());
                msg = sb.toString();
            }

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
        if ((null != logger) && logger.isLoggable(level)) {
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
        if ((null != logger) && logger.isLoggable(level)) {
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
        if ((null != logger) && logger.isLoggable(level)) {
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

    public QueryDiagnostics getQueryDiagnostics() {
        return queryDiagnostics;
    }

    public void setQueryDiagnostics(QueryDiagnostics queryDiagnostics) {
        this.queryDiagnostics = queryDiagnostics;
    }

    public void setResultSetSchema(MongoJsonSchema resultSetSchema) {
        this.getQueryDiagnostics().setResultSetSchema(resultSetSchema);
    }

    public void setNamespacesSchema(BsonDocument namespacesSchema) {
        this.getQueryDiagnostics().setQueryCatalog(namespacesSchema);
    }

    public void setSqlQuery(String sql) {
        this.getQueryDiagnostics().setSqlQuery(sql);
    }

    public void setPipeline(List<BsonDocument> pipeline) {
        this.getQueryDiagnostics().setPipeline(new BsonArray(pipeline));
    }
}
