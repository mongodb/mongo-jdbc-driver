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

package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class AuthX509PEMContentsIntegrationTest extends AuthX509IntegrationTestBase {

    /**
     * Tests that a raw unencrypted PEM certificate provided in the 'password' field can be used to
     * successfully authenticate via X.509.
     */
    @Test
    public void testPEMContentsUnencryptedPkcs8InPasswordField() throws SQLException, IOException {
        // Load the one-line raw unencrypted PEM string
        String filePath = "resources/authentication_test/X509/client-unencrypted-pkcs8-string.txt";
        String pemContent = new String(Files.readAllBytes(Paths.get(filePath)));
        try (Connection connection = connectWithX509(null, pemContent)) {
            assertNotNull(
                    connection, "Connection with inline unencrypted PKCS#8 PEM should succeed");
            connection.getMetaData().getDriverVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect with inline unencrypted PKCS#8 PEM", e);
        }
    }

    /**
     * Tests that an encrypted PEM certificate provided in the 'password' field, where the PEM is
     * stored in JSON format, can be used to successfully authenticate via X.509.
     */
    @Test
    public void testPEMContentsEncryptedPkcs8JsonInPasswordField() throws Exception {
        // Load the one-line encrypted PEM string stored in JSON format
        String filePath = "resources/authentication_test/X509/client-encrypted-pkcs8-string.json";
        String pemContent = new String(Files.readAllBytes(Paths.get(filePath)));
        try (Connection connection = connectWithX509(null, pemContent)) {
            assertNotNull(connection, "Connection with inline encrypted PKCS#8 PEM should succeed");
            connection.getMetaData().getDriverVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect with inline encrypted PKCS#8 PEM", e);
        }
    }

    /**
     * Tests that a raw unencrypted PKCS#1 PEM certificate provided in the 'password' field can be
     * used to successfully authenticate via X.509.
     */
    @Test
    public void testPEMContentsUnencryptedPkcs1InPasswordField() throws SQLException, IOException {
        // Load the one-line raw unencrypted PEM string
        String filePath = "resources/authentication_test/X509/client-unencrypted-pkcs1-string.txt";
        String pemContent = new String(Files.readAllBytes(Paths.get(filePath)));
        try (Connection connection = connectWithX509(null, pemContent)) {
            assertNotNull(
                    connection, "Connection with inline unencrypted PKCS#1 PEM should succeed");
            connection.getMetaData().getDriverVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect with inline unencrypted PKCS#1 PEM", e);
        }
    }

    /**
     * Tests that a raw encrypted PKCS#1 PEM certificate provided in the 'password' field can be
     * used to successfully authenticate via X.509.
     */
    @Test
    public void testPEMContentsEncryptedPkcs1JsonInPasswordField()
            throws SQLException, IOException {
        // Load the one-line raw encrypted PEM string
        String filePath = "resources/authentication_test/X509/client-encrypted-pkcs1-string.json";
        String pemContent = new String(Files.readAllBytes(Paths.get(filePath)));
        try (Connection connection = connectWithX509(null, pemContent)) {
            assertNotNull(connection, "Connection with inline encrypted PKCS#1 PEM should succeed");
            connection.getMetaData().getDriverVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect with inline encrypted PKCS#1 PEM", e);
        }
    }
}
