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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import org.junit.jupiter.api.Test;

// Integration tests for X.509 authentication with TLS CA file configuration.
public class AuthX509TlsCaFileIntegrationTest extends AuthX509IntegrationTestBase {
    /**
     * Tests that a connection fails when the TLS CA file is not provided. This verifies that the
     * client cannot connect without proper CA configuration.
     */
    @Test
    public void testTlsCaFilePropertyUnsetFails() throws SQLException {
        assertThrows(
                SQLTimeoutException.class,
                () -> {
                    try (Connection connection =
                            connectWithX509(
                                    "resources/authentication_test/X509/client-unencrypted.pem",
                                    null,
                                    null,
                                    null)) {
                        connection.getMetaData().getDriverVersion();
                    }
                });
    }

    /**
     * Tests that a connection succeeds when the TLS CA file is explicitly set as a property. This
     * verifies the basic functionality of CA file authentication.
     */
    @Test
    public void testTlsCaFilePropertySetSucceeds() throws SQLException {
        String caFilePath = "resources/authentication_test/X509/ca.crt";

        java.io.File caFile = new java.io.File(caFilePath);
        assertTrue(caFile.exists(), "CA file at " + caFilePath + " does not exist");

        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-unencrypted.pem",
                        null,
                        caFilePath,
                        null)) {
            assertNotNull(connection, "Connection should succeed with valid CA file");
            connection.getMetaData().getDriverVersion();
        }
    }

    /**
     * Tests that a connection succeeds when the TLS CA file is specified as a URI option. This
     * verifies the alternative configuration method for CA files.
     */
    @Test
    public void testTlsCaFileUriOptionSetSucceeds() throws SQLException {
        String caFilePath = "resources/authentication_test/X509/ca.crt";

        java.io.File caFile = new java.io.File(caFilePath);
        assertTrue(caFile.exists(), "CA file at " + caFilePath + " does not exist");

        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-unencrypted.pem",
                        null,
                        null,
                        "tlscafile=" + caFilePath)) {
            assertNotNull(connection, "Connection should succeed with valid CA file");
            connection.getMetaData().getDriverVersion();
        }
    }

    /**
     * Tests that a connection succeeds when the TLS CA file contains multiple certificates. This
     * verifies support for CA bundles containing multiple certificates.
     */
    @Test
    public void testTlsCaFileWithMultipleCertificatesSucceeds() throws SQLException {
        String caFilePath =
                "src/test/resources/X509AuthenticationTest/multiple_x509_certificates.pem";

        java.io.File caFile = new java.io.File(caFilePath);
        assertTrue(caFile.exists(), "CA file at " + caFilePath + " does not exist");

        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-unencrypted.pem",
                        null,
                        caFilePath,
                        null)) {
            assertNotNull(connection, "Connection should succeed with valid CA file");
            connection.getMetaData().getDriverVersion();
        }
    }
}
