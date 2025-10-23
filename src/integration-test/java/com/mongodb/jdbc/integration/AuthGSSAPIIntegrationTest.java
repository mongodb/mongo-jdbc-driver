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
        props.setProperty("gssapilogincontextname", "mongodb.gssapi");
        props.setProperty("gssapiserverauth", "true");

        try (Connection conn = DriverManager.getConnection(mongoUri, props)) {
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet catalogs = dbmd.getCatalogs();
            while (catalogs.next()) {
                System.out.println("Catalog: " + catalogs.getString(1));
            }
            fail("Should not succeed - expected $documents error");

        } catch (SQLException e) {
            // Traverse the cause chain
            Throwable current = e;
            boolean hasDocumentsError = false;

            while (current != null) {
                String msg = current.getMessage();
                System.out.println("Error Message: " + msg);
                if (msg != null && msg.contains("$documents")) {
                    hasDocumentsError = true;
                    break;
                }
                current = current.getCause();
            }

            assertTrue(
                    hasDocumentsError,
                    "Expected error to contain '$documents' in the exception chain");
        }
    }
}
