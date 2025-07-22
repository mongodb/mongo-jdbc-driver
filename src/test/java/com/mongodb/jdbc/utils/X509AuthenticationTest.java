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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.junit.jupiter.api.Test;

// Test loading different private key format
public class X509AuthenticationTest {
    private static final Logger LOGGER = Logger.getLogger("DummyLogger");

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL); // Log all messages that reach this handler
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(Level.FINE);
    }

    private static final MongoLogger MONGO_LOGGER = new MongoLogger(LOGGER, 1);
    private static final MongoClientSettings.Builder SETTINGS_BUILDER =
            MongoClientSettings.builder();
    private static final String TEST_PEM_DIR = "X509AuthenticationTest";
    char[] passphrase = "pencil".toCharArray(); //System.getenv("ADF_TEST_LOCAL_PWD").toCharArray();

    @Test
    public void testX509AuthenticationWithPKCS1Unencrypted() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(
                        classLoader.getResource(TEST_PEM_DIR + "/pkcs1_unencrypted.pem").getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        x509Authentication.configureX509Authentication(SETTINGS_BUILDER, pemFile.getPath(), null);
    }

    @Test
    public void testX509AuthenticationWithPKCS8Unencrypted() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(
                        classLoader.getResource(TEST_PEM_DIR + "/pkcs8_unencrypted.pem").getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        x509Authentication.configureX509Authentication(SETTINGS_BUILDER, pemFile.getPath(), null);
    }

    @Test
    public void testX509AuthenticationWithPKCS1Encrypted() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(classLoader.getResource(TEST_PEM_DIR + "/pkcs1_encrypted.pem").getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        x509Authentication.configureX509Authentication(
                SETTINGS_BUILDER, pemFile.getPath(), passphrase);
    }

    @Test
    public void testX509AuthenticationWithPKCS8Encrypted() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(classLoader.getResource(TEST_PEM_DIR + "/pkcs8_encrypted.pem").getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        x509Authentication.configureX509Authentication(
                SETTINGS_BUILDER, pemFile.getPath(), passphrase);
    }

    @Test
    public void testX509AuthenticationWithNoX509Certificate() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(
                        classLoader
                                .getResource(TEST_PEM_DIR + "/no_x509_certificate.pem")
                                .getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        try {
            x509Authentication.configureX509Authentication(
                    SETTINGS_BUILDER, pemFile.getPath(), null);
        } catch (Exception e) {
            assertTrue(
                    e.getCause()
                            .getMessage()
                            .contains("X.509 certificate not found in the PEM file"),
                    e.getCause().getMessage()
                            + " doesn't contain \"X.509 certificate not found in the PEM file\"");
        }
    }

    @Test
    public void testX509AuthenticationWithNoPrivateKey() {
        X509Authentication x509Authentication = new X509Authentication(MONGO_LOGGER);

        ClassLoader classLoader = getClass().getClassLoader();
        File pemFile =
                new File(classLoader.getResource(TEST_PEM_DIR + "/no_private_key.pem").getFile());
        assertFalse(pemFile.isDirectory(), pemFile.getPath() + " is not a file.");
        try {
            x509Authentication.configureX509Authentication(
                    SETTINGS_BUILDER, pemFile.getPath(), null);
        } catch (Exception e) {
            assertTrue(
                    e.getCause()
                            .getMessage()
                            .contains(
                                    "Private key not found in the PEM file (encrypted or unencrypted)"),
                    e.getCause().getMessage()
                            + " doesn't contain \"Private key not found in the PEM file (encrypted or unencrypted)\"");
        }
    }
}
