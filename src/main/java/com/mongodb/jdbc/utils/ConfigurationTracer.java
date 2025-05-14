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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

/**
 * ConfigurationTracer provides utilities for tracing configuration values and their origins
 * in the MongoDB JDBC driver. It can be used to debug configuration issues by showing where
 * each configuration value comes from and how precedence is resolved when multiple sources
 * provide the same configuration option.
 */
public class ConfigurationTracer {

    public static final String CONFIG_TRACE_ENABLED_PROP = "mongodb.jdbc.config.trace.enabled";
    
    public static final String CONFIG_TRACE_ENABLED_CONN_PROP = "configtrace";
    
    private static final Level DEFAULT_TRACE_LEVEL = Level.FINE;

    private static final Set<String> SENSITIVE_PROPERTIES = new HashSet<>(Arrays.asList(
            "password", "pwd", "pass", "secret", "key", "token", "accesstoken", "refreshtoken"
    ));

    /**
     * Configuration source types in order of precedence (highest to lowest)
     */
    public enum ConfigSource {
        CONNECTION_PROPERTY("Connection Property"),
        URL_PARAMETER("URL Parameter"),
        SYSTEM_PROPERTY("System Property"),
        ENVIRONMENT_VARIABLE("Environment Variable"),
        DEFAULT_VALUE("Default Value");
        
        private final String displayName;
        
        ConfigSource(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Represents a configuration value with its source information
     */
    public static class ConfigValue {
        private final String name;
        private final String value;
        private final ConfigSource source;
        private final boolean isSensitive;
        
        public ConfigValue(String name, String value, ConfigSource source) {
            this.name = name;
            this.value = value;
            this.source = source;
            this.isSensitive = isSensitive(name);
        }
        
        public String getName() {
            return name;
        }
        
        public String getValue() {
            return isSensitive ? maskSensitiveValue(value) : value;
        }
        
        public String getRawValue() {
            return value;
        }
        
        public ConfigSource getSource() {
            return source;
        }
        
        public boolean isSensitive() {
            return isSensitive;
        }
        
        @Override
        public String toString() {
            return String.format("%s=%s (from %s)", name, getValue(), source);
        }
    }

    private final MongoLogger logger;
    private final boolean enabled;
    private final Map<String, ConfigValue> tracedConfigs = new HashMap<>();

    /**
     * Creates a new ConfigurationTracer with the specified logger
     *
     * @param logger The logger to use for tracing configuration
     * @param properties Connection properties that may override tracing settings
     */
    public ConfigurationTracer(MongoLogger logger, Properties properties) {
        this.logger = logger;
        
        boolean enabledViaSysProp = Boolean.parseBoolean(System.getProperty(CONFIG_TRACE_ENABLED_PROP, "false"));
        boolean enabledViaConnProp = false;
        
        if (properties != null) {
            enabledViaConnProp = Boolean.parseBoolean(properties.getProperty(CONFIG_TRACE_ENABLED_CONN_PROP, "false"));
        }
        
        this.enabled = enabledViaSysProp || enabledViaConnProp;
        
        if (this.enabled) {
            logger.log(Level.INFO, "MongoDB JDBC Configuration Tracing enabled");
        }
    }

    /**
     * Traces URL parameter configurations from a ConnectionString
     *
     * @param connectionString The ConnectionString to trace
     */
    public void traceUrlParameters(ConnectionString connectionString) {
        if (!enabled) return;
        
        String connString = connectionString.getConnectionString();
        String[] parts = connString.split("\\?", 2);
        
        if (parts.length < 2) return;
        
        String queryString = parts[1];
        String[] pairs = queryString.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                traceConfig(key, value, ConfigSource.URL_PARAMETER);
            }
        }
    }

    /**
     * Traces configurations from connection properties
     *
     * @param properties The connection properties to trace
     */
    public void traceConnectionProperties(Properties properties) {
        if (!enabled || properties == null) return;
        
        for (String name : properties.stringPropertyNames()) {
            if (!name.equals(CONFIG_TRACE_ENABLED_CONN_PROP)) {
                traceConfig(name, properties.getProperty(name), ConfigSource.CONNECTION_PROPERTY);
            }
        }
    }

    /**
     * Traces environment variable configurations
     *
     * @param name The environment variable name
     * @param value The environment variable value
     */
    public void traceEnvironmentVariable(String name, String value) {
        if (!enabled) return;
        
        traceConfig(name, value, ConfigSource.ENVIRONMENT_VARIABLE);
    }

    /**
     * Traces system property configurations
     *
     * @param name The system property name
     * @param value The system property value
     */
    public void traceSystemProperty(String name, String value) {
        if (!enabled) return;
        
        traceConfig(name, value, ConfigSource.SYSTEM_PROPERTY);
    }

    /**
     * Traces default value configurations
     *
     * @param name The configuration name
     * @param value The default value
     */
    public void traceDefaultValue(String name, String value) {
        if (!enabled) return;
        
        traceConfig(name, value, ConfigSource.DEFAULT_VALUE);
    }

    /**
     * Traces JAAS configuration
     *
     * @param configName The JAAS configuration name
     * @param loginModuleClassName The login module class name
     * @param options JAAS configuration options
     * @param source The source of the JAAS configuration
     */
    public void traceJaasConfig(String configName, String loginModuleClassName, 
                                Map<String, ?> options, ConfigSource source) {
        if (!enabled) return;
        
        logger.log(DEFAULT_TRACE_LEVEL, String.format(
                "JAAS Configuration for '%s': LoginModule=%s, Source=%s",
                configName, loginModuleClassName, source));
        
        if (options != null) {
            for (Map.Entry<String, ?> entry : options.entrySet()) {
                String value = entry.getValue() != null ? entry.getValue().toString() : "null";
                boolean isSensitive = isSensitive(entry.getKey());
                
                logger.log(DEFAULT_TRACE_LEVEL, String.format(
                        "  JAAS Option: %s=%s",
                        entry.getKey(),
                        isSensitive ? maskSensitiveValue(value) : value));
            }
        }
    }

    /**
     * Records a configuration value and its source
     *
     * @param name The configuration name
     * @param value The configuration value
     * @param source The source of the configuration
     */
    private void traceConfig(String name, String value, ConfigSource source) {
        if (!enabled) return;
        
        ConfigValue existingValue = tracedConfigs.get(name);
        ConfigValue newValue = new ConfigValue(name, value, source);
        
        if (existingValue == null) {
            tracedConfigs.put(name, newValue);
            logConfig(newValue, null);
        } else if (source.ordinal() < existingValue.getSource().ordinal()) {
            tracedConfigs.put(name, newValue);
            logConfig(newValue, existingValue);
        }
    }

    /**
     * Logs a configuration value and its source
     *
     * @param newValue The new configuration value
     * @param overriddenValue The configuration value that was overridden (if any)
     */
    private void logConfig(ConfigValue newValue, ConfigValue overriddenValue) {
        if (overriddenValue == null) {
            logger.log(DEFAULT_TRACE_LEVEL, String.format(
                    "Config: %s=%s (from %s)",
                    newValue.getName(),
                    newValue.getValue(),
                    newValue.getSource()));
        } else {
            logger.log(DEFAULT_TRACE_LEVEL, String.format(
                    "Config: %s=%s (from %s) overrides %s (from %s)",
                    newValue.getName(),
                    newValue.getValue(),
                    newValue.getSource(),
                    overriddenValue.getValue(),
                    overriddenValue.getSource()));
        }
    }

    /**
     * Dumps all traced configurations to the log
     */
    public void dumpTracedConfigs() {
        if (!enabled || tracedConfigs.isEmpty()) return;
        
        logger.log(Level.INFO, "=== MongoDB JDBC Configuration Trace Summary ===");
        
        for (ConfigValue value : tracedConfigs.values()) {
            logger.log(Level.INFO, value.toString());
        }
        
        logger.log(Level.INFO, "=== End Configuration Trace Summary ===");
    }

    /**
     * Checks if a property name indicates a sensitive value that should be masked
     *
     * @param name The property name to check
     * @return true if the property contains sensitive information, false otherwise
     */
    private static boolean isSensitive(String name) {
        if (name == null) return false;
        
        String lowerName = name.toLowerCase();
        
        for (String sensitiveKey : SENSITIVE_PROPERTIES) {
            if (lowerName.contains(sensitiveKey)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Masks a sensitive value with asterisks
     *
     * @param value The sensitive value to mask
     * @return The masked value
     */
    private static String maskSensitiveValue(String value) {
        if (value == null || value.isEmpty()) return value;
        
        return "********";
    }
}
