package com.mongodb.jdbc.logging;

import org.aspectj.lang.JoinPoint;

/**
 * LogginAspect will intercept execution of MongoLogger.log(...) and call the logp equivalent after prefixing the source
 * name with connection id and statement id if present.
 */
public aspect LoggingAspect pertarget(com.mongodb.jdbc.logging.MongoLogger)
{
    void around(): execution(private void com.mongodb.jdbc.logging.MongoLogger.plog(..)) &&
        !within(LoggingAspect) {
        System.out.println("Call argument " + thisJoinPoint.getArgs());
    }

}