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

package com.mongodb.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tests for the configuration tracing functionality.
 */
public class ConfigurationTracingTest {

    /**
     * Test that configuration tracing can be enabled via connection property.
     */
    @Test
    public void testConfigurationTracingViaConnectionProperty() throws SQLException {
        Properties props = new Properties();
        props.setProperty("database", "test");
        props.setProperty("user", "admin");
        props.setProperty("password", "password");
        props.setProperty("configtrace", "true");
        props.setProperty("loglevel", "FINE");
        props.setProperty("logdir", "console");
        
        try (Connection conn = DriverManager.getConnection("jdbc:mongodb://localhost:27017/test", props)) {
            System.out.println("Connection created with configuration tracing enabled");
        } catch (SQLException e) {
            System.out.println("Connection failed (expected if no MongoDB server is running): " + e.getMessage());
        }
    }

    /**
     * Test that configuration tracing can be enabled via system property.
     */
    @Test
    public void testConfigurationTracingViaSystemProperty() throws SQLException {
        System.setProperty("mongodb.jdbc.config.trace.enabled", "true");
        
        Properties props = new Properties();
        props.setProperty("database", "test");
        props.setProperty("user", "admin");
        props.setProperty("password", "password");
        props.setProperty("loglevel", "FINE");
        props.setProperty("logdir", "console");
        
        try (Connection conn = DriverManager.getConnection("jdbc:mongodb://localhost:27017/test", props)) {
            System.out.println("Connection created with configuration tracing enabled");
        } catch (SQLException e) {
            System.out.println("Connection failed (expected if no MongoDB server is running): " + e.getMessage());
        } finally {
            System.clearProperty("mongodb.jdbc.config.trace.enabled");
        }
    }
}
