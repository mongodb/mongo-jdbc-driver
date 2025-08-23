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

package com.mongodb.jdbc.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.MongoClientSettings;
import com.mongodb.jdbc.logging.MongoLogger;
import java.io.File;
import java.util.logging.*;
import org.junit.jupiter.api.Test;

// Test loading different private key formats and checking if the
// X.509 authentication configuration is successful or fails as expected.
public class X509AuthenticationTest {
    private static final Logger LOGGER = Logger.getLogger("DummyLogger");

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL); // Log all messages that reach this handler
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    private static final MongoLogger MONGO_LOGGER = new MongoLogger(LOGGER, 1);
    private static final X509Authentication x509Authentication =
            new X509Authentication(MONGO_LOGGER);
    private static final MongoClientSettings.Builder SETTINGS_BUILDER =
            MongoClientSettings.builder();
    private static final String TEST_PEM_DIR = "X509AuthenticationTest";
    char[] passphrase = "pencil".toCharArray(); //System.getenv("ADF_TEST_LOCAL_PWD").toCharArray();

    // Helper method to configure X.509 authentication
    // with a given PEM file and passphrase.
    // It asserts that the configuration is successful.
    private void configureX509AuthSuccess(String pemFileName, char[] passphrase) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(classLoader.getResource(TEST_PEM_DIR + "/" + pemFileName).getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        x509Authentication.configureX509Authentication(
                SETTINGS_BUILDER, pemFile.getPath(), passphrase);
    }

    // Helper method to configure X.509 authentication
    // with a given PEM file and passphrase.
    // It asserts that the configuration fails with the expected error message.
    // This is used to test cases where the PEM file is expected to be invalid.
    private void configureX509AuthFailure(
            String pemFileName, char[] passphrase, String expectedErrorMessage) {
        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(classLoader.getResource(TEST_PEM_DIR + "/" + pemFileName).getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        try {
            x509Authentication.configureX509Authentication(
                    SETTINGS_BUILDER, pemFile.getPath(), "invalid".toCharArray());
            fail("Expected failure but got success");
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains(expectedErrorMessage),
                    "Message \""
                            + e.getMessage()
                            + "\""
                            + " doesn't contain \""
                            + expectedErrorMessage
                            + "\"");
        }
    }

    @Test
    public void testX509AuthenticationWithPKCS1Unencrypted() throws Exception {
        configureX509AuthSuccess("pkcs1_unencrypted.pem", null);
    }

    @Test
    public void testX509AuthenticationWithPKCS8Unencrypted() throws Exception {
        configureX509AuthSuccess("pkcs8_unencrypted.pem", null);
    }

    @Test
    public void testX509AuthenticationWithPKCS1Encrypted() throws Exception {
        configureX509AuthSuccess("pkcs1_encrypted.pem", passphrase);
    }

    @Test
    public void testX509AuthenticationWithPKCS8Encrypted() throws Exception {
        configureX509AuthSuccess("pkcs8_encrypted.pem", passphrase);
    }

    @Test
    public void testX509AuthenticationWithPKCS1EncryptedInvalidPassphrase() {
        configureX509AuthFailure(
                "pkcs1_encrypted.pem",
                "invalid".toCharArray(),
                "Incorrect password or decryption error for PKCS#1 key");
    }

    @Test
    public void testX509AuthenticationWithPKCS8EncryptedInvalidPassphrase() {
        configureX509AuthFailure(
                "pkcs8_encrypted.pem",
                "invalid".toCharArray(),
                "Incorrect password or decryption error for PKCS#8 key");
    }

    @Test
    public void testX509AuthenticationWithNoX509Certificate() {
        configureX509AuthFailure(
                "no_x509_certificate.pem", null, "X.509 certificate not found in the PEM file");
    }

    @Test
    public void testX509AuthenticationWithNoPrivateKey() {
        configureX509AuthFailure(
                "no_private_key.pem",
                null,
                "Private key not found (encrypted or unencrypted) in the PEM file");
    }

    @Test
    public void testX509AuthenticationWithCorruptedPrivateKey() {
        configureX509AuthFailure(
                "corrupted_key.pem", null, "problem parsing ENCRYPTED PRIVATE KEY");
    }

    @Test
    public void testX509AuthenticationWithCorruptedX509Certificate() {
        configureX509AuthFailure("corrupted_x509_certificate.pem", null, "problem parsing cert");
    }

    @Test
    public void testX509AuthenticationWithPublicKeyOnly() {
        configureX509AuthFailure(
                "public_key.pem",
                null,
                "Private key not found (encrypted or unencrypted) and X.509 certificate not found in the PEM file");
    }

    @Test
    public void testX509AuthenticationWithExtraPublicKeyIgnored() throws Exception {
        configureX509AuthSuccess("pkcs8_unencrypted_with_public_key.pem", null);
    }

    @Test
    public void testX509AuthenticationWithUnrecognizedPemObject() throws Exception {
        configureX509AuthFailure(
                "crl.pem",
                null,
                "Private key not found (encrypted or unencrypted) and X.509 certificate not found in the PEM file");
    }

    @Test
    public void testX509AuthenticationWithExtraCrlIgnored() throws Exception {
        configureX509AuthSuccess("pkcs8_unencrypted_with_crl.pem", null);
    }

    @Test
    public void testX509AuthenticationWithMultiplePrivateKeys() throws Exception {
        configureX509AuthSuccess("multiple_private_keys.pem", null);
    }

    @Test
    public void testX509AuthenticationWithMultipleX509Certificates() throws Exception {
        configureX509AuthSuccess("multiple_x509_certificates.pem", null);
    }

    @Test
    public void testX509AuthenticationInvalidPemPath() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String dir = classLoader.getResource(TEST_PEM_DIR).getPath();
        String expectedErrorMessage = "doesntExist.pem (No such file or directory)";
        try {
            x509Authentication.configureX509Authentication(
                    SETTINGS_BUILDER, dir + "/doesntExist.pem", passphrase);

            fail("Expected failure but got success");
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains(expectedErrorMessage),
                    "Message \""
                            + e.getMessage()
                            + "\""
                            + " doesn't contain \""
                            + expectedErrorMessage
                            + "\"");
        }
    }

    @Test
    public void testX509AuthenticationNotAValidPem() throws Exception {
        configureX509AuthFailure(
                "no_pem_objects.pem",
                null,
                "Private key not found (encrypted or unencrypted) and X.509 certificate not found in the PEM file");
    }

    @Test
    public void testNoNewlinesInInput_ReconstructsProperly() {
        // Input has NO newlines — everything is on one line
        String input = "-----BEGIN CERTIFICATE-----MIIC...ABC-----END CERTIFICATE-----";

        // Expected: newlines inserted before BEGIN, after END, and at end
        String expected = "-----BEGIN CERTIFICATE-----\nMIIC...ABC\n-----END CERTIFICATE-----\n";

        String result = x509Authentication.formatPemString(input);

        assertEquals(expected, result);
    }

    @Test
    public void testFormatPemString_FlatPrivateKeyBlock_NoNewlines() {
        // Full private key block — but completely flat (no newlines)
        String input =
                "-----BEGIN ENCRYPTED PRIVATE KEY----- MIIEvPz -----END ENCRYPTED PRIVATE KEY-----";

        String expected =
                "-----BEGIN ENCRYPTED PRIVATE KEY-----\n MIIEvPz \n-----END ENCRYPTED PRIVATE KEY-----\n";

        String result = x509Authentication.formatPemString(input);

        assertEquals(expected, result);
    }

    @Test
    public void testFormatPemString_FlatEncryptedPrivateKeyBlock_NoNewlines() {
        // Full private key block — but completely flat (no newlines)
        String input = "-----BEGIN PRIVATE KEY-----MIIEvPz-----END PRIVATE KEY-----";

        String expected = "-----BEGIN PRIVATE KEY-----\nMIIEvPz\n-----END PRIVATE KEY-----\n";

        String result = x509Authentication.formatPemString(input);

        assertEquals(expected, result);
    }

    @Test
    public void testFormatPemString_FlatPrivateKeyAndCertificate_Minimal() {
        String input =
                "-----BEGIN PRIVATE KEY-----MIIEvPz-----END PRIVATE KEY-----"
                        + "-----BEGIN CERTIFICATE-----MIICQz-----END CERTIFICATE-----";

        String expected =
                "-----BEGIN PRIVATE KEY-----\nMIIEvPz\n-----END PRIVATE KEY-----\n"
                        + "-----BEGIN CERTIFICATE-----\nMIICQz\n-----END CERTIFICATE-----\n";

        String result = x509Authentication.formatPemString(input);

        assertEquals(expected, result);
    }
}
