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

import static com.mongodb.jdbc.MongoConnection.MONGODB_JDBC_X509_CLIENT_CERT_PATH;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class AuthX509PEMPathIntegrationTest extends AuthX509IntegrationTestBase {

    /**
     * Tests that when x509PemPath property is set, it takes precedence over the
     * X509_CLIENT_CERT_PATH environment variable. The X509_CLIENT_CERT_PATH points to a valid file,
     * but the property points to an invalid path. Should fail with FileNotFound.
     */
    @Test
    public void testPEMPathPropertyPrecedenceFailsIfWrong() {
        String certPathEnvVar = System.getenv(MONGODB_JDBC_X509_CLIENT_CERT_PATH);
        assertNotNull(
                certPathEnvVar,
                "Environment variable " + MONGODB_JDBC_X509_CLIENT_CERT_PATH + " must be set");
        java.io.File certFile = new java.io.File(certPathEnvVar);
        assertTrue(certFile.exists(), "File at " + certPathEnvVar + " does not exist");

        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        Properties properties = new java.util.Properties();
        properties.setProperty("database", "test");
        properties.setProperty("x509PemPath", "invalid-path.pem");

        SQLException exception =
                assertThrows(
                        SQLException.class, () -> DriverManager.getConnection(uri, properties));
        Throwable cause = exception.getCause();
        assertNotNull(exception.getCause(), "Expected a cause in the exception");
        assertTrue(
                cause instanceof SQLException,
                "Expected SQLException, but got: " + cause.getClass().getName());
    }

    /** Tests that PEM file without passphrase connects */
    @Test
    public void testPEMPathPropertySetCorrectly() throws SQLException {
        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-unencrypted.pem", null)) {
            assertNotNull(connection, "Connection should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }

    /** Tests that PEM file encrypted with passphrase connects */
    @Test
    public void testPEMPathEncryptedCertWithPassphrase() throws SQLException {
        assertNotNull(passwordEnv, "Environment variable " + PASSWORD_ENV_VAR + " must be set");
        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-encrypted.pem", passwordEnv)) {
            assertNotNull(connection, "Connection with encrypted cert should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }

    /** Tests that an incorrect passphrase fails with exception */
    @Test
    public void testPEMPathEncryptedCertWithIncorrectPassphraseFails() {
        SQLException exception =
                assertThrows(
                        SQLException.class,
                        () ->
                                connectWithX509(
                                        "resources/authentication_test/X509/client-encrypted.pem",
                                        "incorrectPassphrase"));
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Expected a cause in the exception");
        assertTrue(
                cause instanceof SQLException,
                "Expected SQLException, but got: " + cause.getClass().getName());
    }

    /**
     * Tests that without the x509PemPath property set, the X509_CLIENT_CERT_PATH will be used and
     * successfully connects
     */
    @Test
    public void testPEMPathNoPropertyReliesOnEnvVariable() throws SQLException {
        String certPathEnvVar = System.getenv(MONGODB_JDBC_X509_CLIENT_CERT_PATH);
        assertNotNull(
                certPathEnvVar,
                "Environment variable " + MONGODB_JDBC_X509_CLIENT_CERT_PATH + " must be set");

        Properties properties = new java.util.Properties();
        properties.setProperty("database", "test");

        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        try (Connection connection = DriverManager.getConnection(uri, properties)) {
            assertNotNull(connection, "Connection relying on environment variables should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }
}
