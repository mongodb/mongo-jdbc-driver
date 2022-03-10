package com.mongodb.jdbc.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for identifying all classes which should log their public method entries. Used in
 * conjunction with LoggingAspect to provide auto-logging of public methods entry.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AutoLoggable {}
