package com.mongodb.jdbc.logging;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoSQLFeatureNotSupportedException extends SQLFeatureNotSupportedException
{

    public MongoSQLFeatureNotSupportedException(String message, Logger logger) {
        super(message);
        logger.log(Level.SEVERE, message, this);
    }
}
