/*
 * Copyright 2025-present MongoDB, Inc.
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

package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthGSSAPIIntegrationTest {
    private String mongoUri;

    @BeforeEach
    public void setUp() {
        mongoUri = System.getenv("MONGODB_URI");
        if (mongoUri == null || mongoUri.isEmpty()) {
            throw new RuntimeException("MONGODB_URI must be set for GSSAPI test");
        }
    }

    @Test
    public void testGSSAPIConnectionBasicSucceeds() {
        Properties props = new Properties();
        props.setProperty("database", "test");
        props.setProperty("jaasconfigpath", "./resources/authentication_test/GSSAPI/jaas.config");
        System.setProperty(
                "java.security.auth.login.config",
                "./resources/authentication_test/GSSAPI/jaas.config");
        props.setProperty("gssapilogincontextname", "mongodb.gssapi");
        props.setProperty("gssapiserverauth", "true");

        // Logging Properties to help future debugging
        props.setProperty("loglevel", "FINER");
        // Set to true if you see Kerberos specific logs
        System.setProperty("sun.security.krb5.debug", "false");
        System.setProperty("javax.net.debug", "all");

        // JDBC Driver logging does default to Console output, but it defaults logs to System.err instead of System.out.
        // Set System.err logging to go to System.out so we can see relevant debug logs for integration test debugging.
        System.setErr(System.out);

        try (Connection conn = DriverManager.getConnection(mongoUri, props)) {
            // MongoConnection.java implements isValid() by calling SELECT 1. This should be sufficient for just validating GSSAPI connects.
            conn.isValid(5);
        } catch (SQLException e) {
            // Traverse the cause chain
            Throwable current = e;

            while (current != null) {
                String msg = current.getMessage();
                System.out.println("Error Message: " + msg);
                current = current.getCause();
            }
        }
    }
}
