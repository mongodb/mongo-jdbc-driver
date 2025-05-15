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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mongodb.ConnectionString;
import com.mongodb.jdbc.logging.MongoLogger;
import java.util.Properties;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ConfigurationProvenanceTrackerTest {
    
    private MongoLogger mockLogger;
    
    @BeforeEach
    void setUp() {
        mockLogger = mock(MongoLogger.class);
    }
    
    @Test
    void testLogPropertyProvenance() {
        String paramName = "testParam";
        String paramValue = "testValue";
        String source = "Test source";
        String context = "Test context";
        
        ConfigurationProvenanceTracker.logPropertyProvenance(
                paramName, paramValue, source, context, mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains(paramName));
        assertTrue(message.contains(paramValue));
        assertTrue(message.contains(source));
        assertTrue(message.contains(context));
    }
    
    @Test
    void testLogConnectionStringParameter() {
        ConnectionString connString = new ConnectionString("mongodb://localhost:27017/?ssl=true");
        
        ConfigurationProvenanceTracker.logConnectionStringParameter(
                "ssl", "true", connString, "Test context", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("ssl"));
        assertTrue(message.contains("true"));
        assertTrue(message.contains("MongoDB Connection String"));
    }
    
    @Test
    void testPasswordValuesMasked() {
        ConfigurationProvenanceTracker.logPropertyProvenance(
                "password", "secret", "Test source", "Test context", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("password"));
        assertTrue(message.contains("********"));
        assertFalse(message.contains("secret"));
    }
    
    @Test
    void testLogAllSecuritySystemProperties() {
        ConfigurationProvenanceTracker.logAllSecuritySystemProperties("Test", mockLogger);
        
        verify(mockLogger, times(9)).log(eq(Level.INFO), any());
    }
    
    @Test
    void testGetSystemProperty() {
        System.setProperty("test.property", "test-value");
        
        ConfigurationProvenanceTracker.getSystemProperty("test.property", "Test context", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("test.property"));
        assertTrue(message.contains("test-value"));
        assertTrue(message.contains("System Property"));
        
        System.clearProperty("test.property");
    }
    
    @Test
    void testGetEnvProperty() {
        
        ConfigurationProvenanceTracker.getEnvProperty("NON_EXISTENT_ENV_VAR", "Test context", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("NON_EXISTENT_ENV_VAR"));
        assertTrue(message.contains("Not set / Null"));
        assertTrue(message.contains("Environment variable"));
    }
    
    @Test
    void testLogSslSetting() {
        ConfigurationProvenanceTracker.logSslSetting(
                "ssl.enabled", "true", "SSL configuration", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("ssl.enabled"));
        assertTrue(message.contains("true"));
        assertTrue(message.contains("MongoClientSettings"));
    }
    
    @Test
    void testPropertiesLogging() {
        Properties props = new Properties();
        props.setProperty("testKey", "testValue");
        
        ConfigurationProvenanceTracker.logPropertyProvenance(
                "testKey", "testValue", props, "Test context", mockLogger);
        
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger).log(eq(Level.INFO), messageCaptor.capture());
        
        String message = messageCaptor.getValue();
        assertTrue(message.contains("testKey"));
        assertTrue(message.contains("testValue"));
        assertTrue(message.contains("JDBC Connection Property"));
    }
}
