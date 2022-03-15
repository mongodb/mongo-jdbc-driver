package com.mongodb.jdbc.logging;

import com.mongodb.MongoException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import org.aspectj.lang.JoinPoint;
import org.bson.BsonValue;

public aspect LoggingAspect perthis(execution(com.mongodb.jdbc.*.new(..)))
{
    private MongoLogger logger = null;

    pointcut setLogger(MongoLogger logger): set(MongoLogger *.*) && args(logger) && !within(LoggingAspect) ;

    // Around setLogger() advice
    Object around(MongoLogger arg): setLogger(arg) {
        this.logger = arg;
        return proceed(arg);
    }

    before() : (execution(public * @AutoLoggable com.mongodb.jdbc.*.*(..))||
            execution(@AutoLoggable public * com.mongodb.jdbc.*.*(..))) &&
            !@annotation(com.mongodb.jdbc.logging.DisableAutoLogging) &&
            !within(LoggingAspect) {
        if (null != logger) {
            final StringBuilder b = new StringBuilder(thisJoinPoint.getSignature().getName());
            Object[] params = thisJoinPoint.getArgs();
            if (params.length > 0) {
                b.append("(");
                for (int i = 0; i < params.length; i++) {
                    // Obfuscate String and BsonValue parameters
                    if (params[i] instanceof String) {
                        b.append("***");
                    }
                    else if (params[i] instanceof BsonValue) {
                        b.append("Bson");
                        char bsonTypeName[] =
                                ((BsonValue)params[i]).getBsonType().toString().toLowerCase().toCharArray();
                        bsonTypeName[0] = Character.toUpperCase(bsonTypeName[0]);
                        b.append(bsonTypeName.toString());
                        b.append("{***}");
                    }
                    else if (params[i] instanceof Properties) {
                        b.append(((Properties)params[i]).stringPropertyNames());
                    }
                    else
                    {
                        b.append(params[i]);
                    }
                    b.append(", ");
                }
                b.delete(b.length()-2, b.length());
                b.append(")");
            }
            else
            {
                b.append("()");
            }
            logger.logMethodEntry(thisJoinPoint.getSignature().getDeclaringTypeName(), b.toString());
        }
    }

    after () throwing (Exception e) : execution(* *.*(..)) && !within(LoggingAspect)
            {
                if (null != logger) {
                    logger.logError(
                            thisJoinPoint.getSignature().getDeclaringTypeName(),
                            "Error in " + thisJoinPoint.getSignature().toShortString(),
                            e);
                }
            }
}
