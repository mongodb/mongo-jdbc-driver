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

import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.utils.ConfigurationTracer.ConfigSource;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JaasConfigurationTracer provides utilities for tracing JAAS configuration sources and values
 * in the MongoDB JDBC driver.
 */
public class JaasConfigurationTracer {

    private final MongoLogger logger;
    private final ConfigurationTracer configTracer;
    private final boolean enabled;

    private static final Pattern JAAS_CONFIG_PATTERN = Pattern.compile(
            "\\s*(\\w+)\\s*\\{\\s*([^{}]+?)\\s*;\\s*\\};",
            Pattern.DOTALL);

    private static final Pattern LOGIN_MODULE_PATTERN = Pattern.compile(
            "\\s*(\\S+)\\s+\\w+\\s*(?:\\{(.*?)\\})?",
            Pattern.DOTALL);

    /**
     * Creates a new JaasConfigurationTracer with the specified logger and configuration tracer
     *
     * @param logger The logger to use for tracing
     * @param configTracer The configuration tracer to use
     */
    public JaasConfigurationTracer(MongoLogger logger, ConfigurationTracer configTracer) {
        this.logger = logger;
        this.configTracer = configTracer;
        this.enabled = Boolean.parseBoolean(System.getProperty(
                ConfigurationTracer.CONFIG_TRACE_ENABLED_PROP, "false"));
    }

    /**
     * Traces the current JAAS configuration for the given application name
     *
     * @param appName The application name to trace configuration for
     */
    public void traceJaasConfiguration(String appName) {
        if (!enabled) return;
        
        try {
            String configFilePath = System.getProperty("java.security.auth.login.config");
            if (configFilePath != null) {
                traceJaasConfigFile(configFilePath, appName);
            }

            Configuration config = Configuration.getConfiguration();
            if (config != null) {
                traceActiveJaasConfig(config, appName);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error tracing JAAS configuration: " + e.getMessage());
        }
    }

    /**
     * Traces the active JAAS configuration for an application name
     *
     * @param config The JAAS Configuration object
     * @param appName The application name to trace configuration for
     */
    private void traceActiveJaasConfig(Configuration config, String appName) {
        if (config == null || appName == null) return;
        
        try {
            AppConfigurationEntry[] entries = config.getAppConfigurationEntry(appName);
            if (entries == null || entries.length == 0) {
                logger.log(Level.FINE, "No JAAS configuration found for application: " + appName);
                return;
            }
            
            for (AppConfigurationEntry entry : entries) {
                configTracer.traceJaasConfig(
                        appName,
                        entry.getLoginModuleName(),
                        entry.getOptions(),
                        ConfigSource.DEFAULT_VALUE);
                
                logger.log(Level.FINE, String.format(
                        "  JAAS Control Flag: %s",
                        entry.getControlFlag().toString()));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error tracing active JAAS config: " + e.getMessage());
        }
    }

    /**
     * Traces JAAS configuration from a configuration file
     *
     * @param configFilePath Path to the JAAS configuration file
     * @param appName The application name to trace configuration for
     */
    private void traceJaasConfigFile(String configFilePath, String appName) {
        if (configFilePath == null || appName == null) return;
        
        File configFile = new File(configFilePath);
        if (!configFile.exists() || !configFile.isFile()) {
            logger.log(Level.FINE, "JAAS config file does not exist: " + configFilePath);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            StringBuilder contents = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("#") && !line.trim().startsWith("//")) {
                    contents.append(line).append("\n");
                }
            }
            
            Matcher configMatcher = JAAS_CONFIG_PATTERN.matcher(contents.toString());
            while (configMatcher.find()) {
                String configName = configMatcher.group(1);
                
                if (appName.equals(configName)) {
                    String moduleSection = configMatcher.group(2);
                    Matcher moduleMatcher = LOGIN_MODULE_PATTERN.matcher(moduleSection);
                    
                    while (moduleMatcher.find()) {
                        String loginModule = moduleMatcher.group(1);
                        String optionsStr = moduleMatcher.group(2);
                        
                        Map<String, String> options = parseOptions(optionsStr);
                        
                        configTracer.traceJaasConfig(
                                configName,
                                loginModule,
                                options,
                                ConfigSource.SYSTEM_PROPERTY);
                        
                        logger.log(Level.FINE, String.format(
                                "  JAAS Config from file: %s",
                                configFile.getAbsolutePath()));
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading JAAS config file: " + e.getMessage());
        }
    }

    /**
     * Parses JAAS configuration options from a string
     *
     * @param optionsStr The options string to parse
     * @return Map of option names to values
     */
    private Map<String, String> parseOptions(String optionsStr) {
        Map<String, String> options = new HashMap<>();
        
        if (optionsStr == null || optionsStr.trim().isEmpty()) {
            return options;
        }
        
        String[] optionPairs = optionsStr.split(";");
        for (String pair : optionPairs) {
            pair = pair.trim();
            if (pair.isEmpty()) continue;
            
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replaceAll("^\"|\"$", "");
                options.put(key, value);
            }
        }
        
        return options;
    }
}
