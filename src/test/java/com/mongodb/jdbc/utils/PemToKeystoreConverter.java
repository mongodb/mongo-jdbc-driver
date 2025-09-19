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

import com.mongodb.jdbc.logging.MongoLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * Utility class to convert PEM files to JKS keystore format for testing purposes.
 * This class reads unencrypted PEM files from the test resources directory and
 * creates a JKS keystore with certificates using filenames as aliases.
 */
public class PemToKeystoreConverter {
    private static final Logger LOGGER = Logger.getLogger("PemToKeystoreConverter");
    private static final String TEST_PEM_DIR = "X509AuthenticationTest";
    private static final String KEYSTORE_PASSWORD = "testpass";
    private static final String KEYSTORE_FILENAME = "test-certificates.jks";

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    private static final MongoLogger MONGO_LOGGER = new MongoLogger(LOGGER, 1);

    /**
     * Main method to convert PEM files to JKS keystore.
     * Finds all *unencrypted.pem files in the test resources directory,
     * extracts certificates and private keys, and creates a JKS keystore.
     */
    public static void main(String[] args) throws Exception {
        PemToKeystoreConverter converter = new PemToKeystoreConverter();
        converter.convertPemFilesToKeystore();
    }

    /**
     * Converts all unencrypted PEM files to a JKS keystore.
     */
    public void convertPemFilesToKeystore() throws Exception {
        LOGGER.info("Starting PEM to JKS keystore conversion...");

        java.security.Security.addProvider(new BouncyCastleProvider());

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null, KEYSTORE_PASSWORD.toCharArray());

        ClassLoader classLoader = getClass().getClassLoader();
        File testDir = new File(classLoader.getResource(TEST_PEM_DIR).getFile());

        File[] pemFiles = testDir.listFiles((dir, name) -> name.endsWith("unencrypted.pem"));
        
        if (pemFiles == null || pemFiles.length == 0) {
            LOGGER.warning("No unencrypted PEM files found in " + testDir.getPath());
            return;
        }

        LOGGER.info("Found " + pemFiles.length + " unencrypted PEM files");

        for (File pemFile : pemFiles) {
            processPemFile(pemFile, keystore);
        }

        String keystorePath = testDir.getParent() + File.separator + KEYSTORE_FILENAME;
        try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
            keystore.store(fos, KEYSTORE_PASSWORD.toCharArray());
            LOGGER.info("Keystore saved to: " + keystorePath);
        }

        verifyKeystore(keystorePath);
    }

    /**
     * Processes a single PEM file and adds its certificate and private key to the keystore.
     */
    private void processPemFile(File pemFile, KeyStore keystore) throws Exception {
        String filename = pemFile.getName();
        String alias = filename.substring(0, filename.lastIndexOf(".pem"));
        
        LOGGER.info("Processing file: " + filename + " with alias: " + alias);

        Certificate certificate = null;
        PrivateKey privateKey = null;

        try (FileReader fileReader = new FileReader(pemFile);
             PEMParser pemParser = new PEMParser(fileReader)) {

            Object pemObject;
            while ((pemObject = pemParser.readObject()) != null) {
                if (pemObject instanceof X509CertificateHolder) {
                    X509CertificateHolder certHolder = (X509CertificateHolder) pemObject;
                    JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
                    converter.setProvider("BC");
                    certificate = converter.getCertificate(certHolder);
                    LOGGER.fine("Found X.509 certificate in " + filename);
                } else if (pemObject instanceof PEMKeyPair) {
                    PEMKeyPair keyPair = (PEMKeyPair) pemObject;
                    JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
                    keyConverter.setProvider("BC");
                    privateKey = keyConverter.getPrivateKey(keyPair.getPrivateKeyInfo());
                    LOGGER.fine("Found PKCS#1 private key in " + filename);
                } else if (pemObject instanceof PrivateKeyInfo) {
                    PrivateKeyInfo keyInfo = (PrivateKeyInfo) pemObject;
                    JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
                    keyConverter.setProvider("BC");
                    privateKey = keyConverter.getPrivateKey(keyInfo);
                    LOGGER.fine("Found PKCS#8 private key in " + filename);
                }
            }
        }

        if (certificate == null) {
            throw new Exception("No certificate found in " + filename);
        }
        if (privateKey == null) {
            throw new Exception("No private key found in " + filename);
        }

        Certificate[] certChain = {certificate};
        keystore.setKeyEntry(alias, privateKey, KEYSTORE_PASSWORD.toCharArray(), certChain);
        
        LOGGER.info("Added certificate and private key for alias: " + alias);
    }

    /**
     * Verifies the created keystore by loading it and checking its contents.
     */
    private void verifyKeystore(String keystorePath) throws Exception {
        LOGGER.info("Verifying keystore contents...");

        KeyStore verifyKeystore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            verifyKeystore.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        java.util.Enumeration<String> aliases = verifyKeystore.aliases();
        int count = 0;
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            count++;
            
            Certificate cert = verifyKeystore.getCertificate(alias);
            PrivateKey key = (PrivateKey) verifyKeystore.getKey(alias, KEYSTORE_PASSWORD.toCharArray());
            
            LOGGER.info("Verified alias: " + alias + 
                       " - Certificate: " + (cert != null ? "OK" : "MISSING") +
                       " - Private Key: " + (key != null ? "OK" : "MISSING"));
        }
        
        LOGGER.info("Keystore verification complete. Total aliases: " + count);
    }

    /**
     * Gets the keystore password used for testing.
     */
    public static String getKeystorePassword() {
        return KEYSTORE_PASSWORD;
    }

    /**
     * Gets the keystore filename.
     */
    public static String getKeystoreFilename() {
        return KEYSTORE_FILENAME;
    }
}
