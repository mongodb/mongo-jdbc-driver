package com.mongodb.jdbc.logging;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoSQLException extends SQLException {

    public MongoSQLException(String message, Logger logger) {
        super(message);
        logger.log(Level.SEVERE, message, this);
    }

    public MongoSQLException(Exception e, Logger logger) {
        super(e);
        logger.log(Level.SEVERE, e.getMessage(), e.getCause());
    }
}
