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

    private int callLevel = 1;
    private String enterStr = ">>";

    pointcut setLogger(MongoLogger logger): set(MongoLogger *.*) && args(logger) && !within(LoggingAspect) ;

    // Around setLogger() advice
    Object around(MongoLogger arg): setLogger(arg) {
        this.logger = arg;
        return proceed(arg);
    }

    before() : execution(public * @AutoLoggable com.mongodb.jdbc.*.*(..)) && !within(LoggingAspect) {
        if (null != logger && null != logger.getLogger()) {

            String sourceName = getSourceName(thisJoinPoint);            ;

            final StringBuilder b = new StringBuilder(enterStr);
            b.append(" ");
            b.append(thisJoinPoint.getSignature().getName());

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
                        char bsonTypeName[] = ((BsonValue)params[i]).getBsonType().toString().toLowerCase().toCharArray();
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
            logger.getLogger().logp(Level.FINER, sourceName, null, b.toString());
        }
    }

    after () throwing (Exception e) : execution(* *.*(..)) && !within(LoggingAspect)
    {
        if (null != logger && null != logger.getLogger()) {
            logger.getLogger().logp(
                    Level.SEVERE, getSourceName(thisJoinPoint),
                    null,
                    "Error in " + thisJoinPoint.getSignature().toShortString(),
                    e);
        }
    }

    private String getSourceName(JoinPoint joinPoint) {
        String sourceName = joinPoint.getSignature().getDeclaringTypeName();
        // Add the statement id
        if (logger.getStatementId() != null)
        {
            sourceName = "[stmt-" + logger.getStatementId() + "] " + sourceName;
        }
        // Add the connection id
        if (logger.getConnectionId() != null)
        {
            sourceName = "[c-" + logger.getConnectionId() + "] " + sourceName;
        }

        return sourceName;
    }
}