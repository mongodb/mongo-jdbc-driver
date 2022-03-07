package com.mongodb.jdbc.logging;

import java.util.logging.Logger;

public class MongoLogger {

    private Logger logger;
    private Integer connectionId;
    private Integer statementId;

    /**
     * Gets a logger to log information before a connection has been made.
     *
     * @param className The classname to find the associated logger.
     */
    public MongoLogger(String className) {
        this.logger = MongoLoggerUtils.getLogger(className, null);
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

    public Logger getLogger() {
        return logger;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public Integer getStatementId() {
        return statementId;
    }
}
