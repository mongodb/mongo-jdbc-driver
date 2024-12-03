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

import java.io.File;
import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class AuthX509IntegrationTest {

    private static final String LOCAL_PORT_ENV_VAR = "LOCAL_MDB_PORT_ENT";
    private static final String PASSWORD_ENV_VAR = "ADF_TEST_LOCAL_PWD";
    private static final String X509_CERT_PATH_PROPERTY = "x509PemPath";

    private static Connection connectWithX509(String pemPath, String passphrase)
            throws SQLException {
        String mongoPort = System.getenv(LOCAL_PORT_ENV_VAR);
        assertNotNull(mongoPort, "Environment variable " + LOCAL_PORT_ENV_VAR + " must be set");

        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        Properties properties = new Properties();
        properties.setProperty("database", "test");

        if (pemPath != null) {
            properties.setProperty(X509_CERT_PATH_PROPERTY, pemPath);
        }
        if (passphrase != null) {
            properties.setProperty("password", passphrase);
        }

        return DriverManager.getConnection(uri, properties);
    }

    /**
     * Tests that when x509PemPath property is set, it takes precedence over the
     * X509_CLIENT_CERT_PATH environment variable. The X509_CLIENT_CERT_PATH points to a valid file,
     * but the property points to an invalid path. Should fail with FileNotFound.
     */
    @Test
    public void testPropertyPrecedenceFailsIfWrong() {
        String certPathEnvVar = System.getenv(MONGODB_JDBC_X509_CLIENT_CERT_PATH);
        assertNotNull(
                certPathEnvVar,
                "Environment variable " + MONGODB_JDBC_X509_CLIENT_CERT_PATH + " must be set");
        File certFile = new File(certPathEnvVar);
        assertTrue(certFile.exists(), "File at " + certPathEnvVar + " does not exist");

        String mongoPort = System.getenv(LOCAL_PORT_ENV_VAR);
        assertNotNull(mongoPort, "Environment variable " + LOCAL_PORT_ENV_VAR + " must be set");

        String wrongPemPath = "invalid-path.pem";
        Properties properties = new Properties();
        properties.setProperty("database", "test");
        properties.setProperty(X509_CERT_PATH_PROPERTY, wrongPemPath);

        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        Exception exception =
                assertThrows(
                        RuntimeException.class, () -> DriverManager.getConnection(uri, properties));

        Throwable cause = exception.getCause();
        assertNotNull(cause, "Expected a cause in the exception");
        assertTrue(
                cause instanceof FileNotFoundException,
                "Expected FileNotFoundException, but got: " + cause.getClass().getName());
    }

    /** Tests that PEM file without passphrase connects */
    @Test
    public void testPropertySetCorrectly() throws SQLException {
        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-unencrypted.pem", null)) {
            assertNotNull(connection, "Connection should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }

    /** Tests that PEM file encrypted with passphrase connects */
    @Test
    public void testEncryptedCertWithPassphrase() throws SQLException {
        String passphrase = System.getenv(PASSWORD_ENV_VAR);

        try (Connection connection =
                connectWithX509(
                        "resources/authentication_test/X509/client-encrypted.pem", passphrase)) {
            assertNotNull(connection, "Connection with encrypted cert should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }

    /** Tests that an incorrect passphrase fails with exception */
    @Test
    public void testEncryptedCertWithIncorrectPassphraseFails() {
        String passphrase = "incorrectPassphrase";

        Exception exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                connectWithX509(
                                        "resources/authentication_test/X509/client-encrypted.pem",
                                        passphrase));

        Throwable cause = exception.getCause();
        assertNotNull(cause, "Expected a cause in the exception");
        assertTrue(
                cause instanceof GeneralSecurityException,
                "Expected GeneralSecurityException, but got: " + cause.getClass().getName());
    }

    /**
     * Tests that without the x509PemPath property set, the X509_CLIENT_CERT_PATH will be used and
     * successfully connects
     */
    @Test
    public void testNoPropertyReliesOnEnvVariable() throws SQLException {
        String certPathEnvVar = System.getenv(MONGODB_JDBC_X509_CLIENT_CERT_PATH);
        assertNotNull(
                certPathEnvVar,
                "Environment variable " + MONGODB_JDBC_X509_CLIENT_CERT_PATH + " must be set");

        String mongoPort = System.getenv(LOCAL_PORT_ENV_VAR);
        assertNotNull(mongoPort, "Environment variable " + LOCAL_PORT_ENV_VAR + " must be set");

        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        Properties properties = new Properties();
        properties.setProperty("database", "test");

        try (Connection connection = DriverManager.getConnection(uri, properties)) {
            assertNotNull(connection, "Connection relying on environment variables should succeed");
            connection.getMetaData().getDriverVersion();
        }
    }
}
