/*
 * Copyright 2024-present MongoDB, Inc.
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

package com.mongodb.jdbc.utils;

import com.mongodb.ConnectionString;
import com.mongodb.jdbc.logging.MongoLogger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utility class for tracking and logging the provenance of configuration settings in the MongoDB JDBC driver.
 * This class provides methods to intercept configuration access and determine the source of the configuration value.
 */
public class ConfigurationProvenanceTracker {
    private static final String LOG_PREFIX = "[ConfigProvenance] ";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final String[] SECURITY_SYSTEM_PROPERTIES = {
        "java.security.krb5.conf", 
        "java.security.krb5.realm", 
        "java.security.krb5.kdc",
        "javax.security.auth.useSubjectCredsOnly", 
        "java.security.auth.login.config",
        "javax.net.ssl.keyStore", 
        "javax.net.ssl.trustStore", 
        "javax.net.ssl.keyStorePassword",
        "javax.net.ssl.trustStorePassword"
    };
    
    private static final Map<String, String> propertySources = new HashMap<>();
    
    /**
     * Gets a system property and logs its provenance.
     * @param key The property key
     * @param context Additional context for the log message (e.g., "MongoConnection setup")
     * @return The property value
     */
    public static String getSystemProperty(String key, String context, MongoLogger logger) {
        String value = System.getProperty(key);
        logPropertyProvenance(key, value, determineSystemPropertySource(key), context, logger);
        return value;
    }
    
    /**
     * Gets an environment variable and logs its provenance.
     * @param key The environment variable name
     * @param context Additional context for the log message
     * @return The environment variable value
     */
    public static String getEnvProperty(String key, String context, MongoLogger logger) {
        String value = System.getenv(key);
        String source = "Environment variable";
        logPropertyProvenance(key, value, source, context, logger);
        return value;
    }
    
    /**
     * Logs the provenance of a connection string parameter.
     * @param paramName The parameter name
     * @param paramValue The parameter value
     * @param connectionString The connection string
     * @param context Additional context for the log message
     */
    public static void logConnectionStringParameter(
            String paramName, 
            String paramValue, 
            ConnectionString connectionString, 
            String context,
            MongoLogger logger) {
        String source = "MongoDB Connection String";
        logPropertyProvenance(paramName, paramValue, source, context, logger);
        propertySources.put(paramName, source);
    }
    
    /**
     * Logs the provenance of a property from the Properties object passed to the driver.
     * @param key The property key
     * @param value The property value
     * @param context Additional context for the log message
     */
    public static void logPropertyProvenance(
            String key, 
            String value, 
            Properties properties, 
            String context,
            MongoLogger logger) {
        String source = "JDBC Connection Property";
        logPropertyProvenance(key, value, source, context, logger);
        propertySources.put(key, source);
    }
    
    /**
     * Logs the provenance of an SSL/TLS setting from MongoClientSettings.
     * @param settingName The setting name
     * @param settingValue The setting value
     * @param context Additional context for the log message
     */
    public static void logSslSetting(
            String settingName, 
            String settingValue, 
            String context,
            MongoLogger logger) {
        String source = determineSSLSettingSource(settingName);
        logPropertyProvenance(settingName, settingValue, source, context, logger);
    }
    
    /**
     * General method to log the provenance of any configuration parameter.
     * @param paramName The parameter name
     * @param paramValue The parameter value
     * @param source The determined source of the parameter
     * @param context Additional context for the log message
     */
    public static void logPropertyProvenance(
            String paramName, 
            String paramValue, 
            String source, 
            String context,
            MongoLogger logger) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        
        String displayValue = paramValue;
        if (paramName.toLowerCase().contains("password") || paramName.toLowerCase().contains("pwd")) {
            displayValue = "********";
        }
        
        StringBuilder logMessage = new StringBuilder(LOG_PREFIX);
        logMessage.append("Timestamp: ").append(timestamp);
        logMessage.append(" - Parameter: '").append(paramName).append("'");
        logMessage.append(" - Value: '").append(displayValue != null ? displayValue : "Not set / Null").append("'");
        logMessage.append(" - Source: ").append(source != null ? source : "Unknown");
        
        if (context != null && !context.isEmpty()) {
            logMessage.append(" - Context: ").append(context);
        }
        
        logger.log(Level.INFO, logMessage.toString());
    }
    
    /**
     * Determines the likely source of a system property based on precedence rules.
     * @param key The property key
     * @return The determined source
     */
    private static String determineSystemPropertySource(String key) {
        return "System Property (Likely from -D command line, JDK_JAVA_OPTIONS, or JAVA_TOOL_OPTIONS)";
    }
    
    /**
     * Determines the likely source of an SSL/TLS setting.
     * @param settingName The setting name
     * @return The determined source
     */
    private static String determineSSLSettingSource(String settingName) {
        String systemPropertyKey = null;
        
        if (settingName.contains("keyStore")) {
            systemPropertyKey = "javax.net.ssl.keyStore";
        } else if (settingName.contains("trustStore")) {
            systemPropertyKey = "javax.net.ssl.trustStore";
        }
        
        if (systemPropertyKey != null && System.getProperty(systemPropertyKey) != null) {
            return "System Property (" + systemPropertyKey + ")";
        }
        
        return "MongoClientSettings (programmatic)";
    }
    
    /**
     * Logs the provenance of all security-related system properties.
     * This can be called during initialization to capture the initial state.
     */
    public static void logAllSecuritySystemProperties(String context, MongoLogger logger) {
        for (String property : SECURITY_SYSTEM_PROPERTIES) {
            getSystemProperty(property, context, logger);
        }
    }
}
