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

package com.mongodb.jdbc.utils;

import com.mongodb.MongoException;
import com.mongodb.jdbc.logging.MongoLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.Certificate;
import java.util.logging.Level;
import javax.net.ssl.*;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bson.BsonDocument;

public class X509Authentication {
    private static final BouncyCastleProvider BC_PROVIDER = new BouncyCastleProvider();
    private final MongoLogger logger;

    public X509Authentication(MongoLogger logger) {
        this.logger = logger;
    }

    /**
     * Configures X.509 authentication for MongoDB using a PEM file containing the private key and
     * certificate.
     *
     * @param settingsBuilder The MongoDB client settings builder to apply the SSL configuration.
     *     Must not be null.
     * @param pemPath The path to the PEM file containing the private key and certificate. Can be
     *     null.
     * @param passphrase The passphrase for the private key, if it is encrypted; null if
     *     unencrypted. Can also hold the full PEM contents.
     * @throws Exception If there is an error during configuration or PEM parsing.
     * @throws NullPointerException if settingsBuilder or pemPath are null.
     */
    public void configureX509Authentication(
            com.mongodb.MongoClientSettings.Builder settingsBuilder,
            String pemPath,
            char[] passphrase)
            throws Exception {

        PEMParser pemParser = null;
        char[] privateKeyPassphrase = null;

        // If pemPath is specified, that takes precedence and the passphrase will be used if it is not null
        // Otherwise, the passphrase will be used as PEM contents.  It can be in the form of raw PEM contents or
        // as a JSON representation with keys `pem` and `passphrase`
        if (pemPath != null && !pemPath.trim().isEmpty()) {
            File file = new File(pemPath);
            logger.log(Level.FINE, "Using client certificate for X509 authentication: " + pemPath);
            try {
                pemParser = new PEMParser(new FileReader(file));
            } catch (IOException e) {
                throw new MongoException("Failed to read PEM file: " + e.getMessage(), e);
            }
            privateKeyPassphrase = passphrase;
        } else {
            if (passphrase == null || passphrase.length == 0) {
                throw new MongoException("No PEM path provided and passphrase is empty");
            }
            String passphraseAsPemContent = new String(passphrase);
            PemAuthenticationInput pemAuthenticationInput =
                    parsePemAuthenticationInput(passphraseAsPemContent);
            if (pemAuthenticationInput != null) {
                // JSON input with PEM and optional passphrase
                logger.log(Level.FINE, "Using X.509 credentials from JSON in passphrase field");

                pemParser =
                        new PEMParser(
                                new StringReader(formatPemString(pemAuthenticationInput.pem)));
                privateKeyPassphrase =
                        pemAuthenticationInput.passphrase != null
                                ? pemAuthenticationInput.passphrase.toCharArray()
                                : null;
            } else {
                // Raw PEM content (unencrypted)
                logger.log(Level.FINE, "Using raw PEM content from passphrase field (unencrypted)");

                pemParser =
                        new PEMParser(new StringReader(formatPemString(passphraseAsPemContent)));
                privateKeyPassphrase = null;
            }
        }

        try {
            SSLContext sslContext = createSSLContext(pemParser, privateKeyPassphrase);

            settingsBuilder.applyToSslSettings(
                    sslSettings -> {
                        sslSettings.enabled(true);
                        sslSettings.context(sslContext);
                    });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SSL setup failed: " + e.getMessage());
            throw e;
        } finally {
            if (pemParser != null) {
                try {
                    pemParser.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing PEM parser: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Configures X.509 authentication for MongoDB using a JKS keystore containing the private key and
     * certificate.
     *
     * @param settingsBuilder The MongoDB client settings builder to apply the SSL configuration.
     *     Must not be null.
     * @param keystorePath The path to the JKS keystore file containing the private key and certificate.
     *     Must not be null.
     * @param keystorePassword The password for the keystore. Can be null if keystore is not password protected.
     * @param certificateAlias The alias of the certificate to use from the keystore. Must not be null.
     * @throws Exception If there is an error during configuration, keystore loading, or certificate extraction.
     * @throws NullPointerException if settingsBuilder, keystorePath, or certificateAlias are null.
     */
    public void configureX509AuthenticationFromKeystore(
            com.mongodb.MongoClientSettings.Builder settingsBuilder,
            String keystorePath,
            char[] keystorePassword,
            String certificateAlias)
            throws Exception {

        if (settingsBuilder == null) {
            throw new NullPointerException("settingsBuilder cannot be null");
        }
        if (keystorePath == null || keystorePath.trim().isEmpty()) {
            throw new NullPointerException("keystorePath cannot be null or empty");
        }
        if (certificateAlias == null || certificateAlias.trim().isEmpty()) {
            throw new NullPointerException("certificateAlias cannot be null or empty");
        }

        logger.log(Level.FINE, "Using JKS keystore for X509 authentication: " + keystorePath);
        logger.log(Level.FINE, "Certificate alias: " + certificateAlias);

        try {
            SSLContext sslContext =
                    createSSLContextFromKeystore(keystorePath, keystorePassword, certificateAlias);

            settingsBuilder.applyToSslSettings(
                    sslSettings -> {
                        sslSettings.enabled(true);
                        sslSettings.context(sslContext);
                    });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SSL setup failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Formats a PEM string to handle escaped newlines and ensures correct header placement. Adds
     * required newlines for compatibility with Bouncy Castle PEMParser.
     *
     * @param pem The PEM string to format; may be null.
     * @return A cleaned, normalized PEM string with proper line breaks and headers, or null if the
     *     input was null.
     */
    String formatPemString(String pem) {
        if (pem == null) return null;

        // Replace escaped newlines (common when passed as JSON strings)
        pem = pem.replace("\\\\n", "\n").replace("\\n", "\n").replace("\\r", "\n");

        // Ensure header tags are on separate lines
        pem = pem.replace("-----BEGIN", "\n-----BEGIN");
        pem = pem.replace("-----END", "\n-----END");
        pem = pem.replace(" PRIVATE KEY-----", " PRIVATE KEY-----\n");
        pem = pem.replace(" CERTIFICATE-----", " CERTIFICATE-----\n");

        // These fields are encryption headers used when a PKCS#1 private key is encrypted
        pem = pem.replace("Proc-Type:", "\nProc-Type:");
        pem = pem.replaceAll("DEK-Info:\\s+[A-Z0-9\\-]+,[A-F0-9]+\\s*", "\n$0\n");

        // Remove extra newlines and trim leading/trailing whitespace
        pem = pem.replaceAll("[ \\t]*\n[ \\t]*", "\n").replaceAll("\n+", "\n").trim();

        // Add a final newline (PEMParser expects it)
        if (!pem.endsWith("\n")) {
            pem += "\n";
        }

        return pem;
    }

    private SSLContext createSSLContext(PEMParser pemParser, char[] passphrase) throws Exception {
        PrivateKey privateKey = null;
        Certificate cert = null;

        try {
            Object pemObj = null;

            // Iterate through PEM objects found in the PEM file and process them based on type.
            // Stops after finding both an :
            //  - Encrypted/unencrypted private key
            //  - X.509 certificate
            while (pemParser != null
                    && ((pemObj = pemParser.readObject()) != null)
                    && (cert == null || privateKey == null)) {
                logger.log(
                        Level.FINE,
                        "Processing PEM object of type: " + pemObj.getClass().getName());
                // Initialize the JcaPEMKeyConverter with the Bouncy Castle provider name.
                // This converter is used to transform Bouncy Castle's internal ASN.1 objects
                // into Java Security API (JCA) objects like PrivateKey, PublicKey, etc.
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BC_PROVIDER);

                // Determine the type of PEM object and process accordingly
                if (pemObj instanceof PrivateKeyInfo) {
                    logger.log(Level.FINE, "Found encrypted Private key (PKCS#8)");
                    if (privateKey == null) {
                        // Handles unencrypted PKCS#8 private keys (-----BEGIN PRIVATE KEY-----)
                        PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemObj;
                        privateKey = converter.getPrivateKey(privateKeyInfo);
                        logger.log(
                                Level.FINE,
                                "Successfully loaded unencrypted Private Key (PKCS#8). Algorithm: "
                                        + privateKey.getAlgorithm());
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Already found a private key. This one will be ignored.");
                    }
                } else if (pemObj instanceof PEMKeyPair) {
                    logger.log(Level.FINE, "Found encrypted Private key (PKCS#1)");
                    if (privateKey == null) {
                        // Handles unencrypted PKCS#1 private keys (-----BEGIN RSA PRIVATE KEY----- or -----BEGIN EC PRIVATE KEY-----)
                        PEMKeyPair pemKeyPair = (PEMKeyPair) pemObj;
                        privateKey = converter.getKeyPair(pemKeyPair).getPrivate();
                        logger.log(
                                Level.FINE,
                                "Successfully loaded unencrypted Private Key (PKCS#1). Algorithm: "
                                        + privateKey.getAlgorithm());
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Already found a private key. This one will be ignored.");
                    }
                } else if (pemObj instanceof PKCS8EncryptedPrivateKeyInfo) {
                    logger.log(Level.FINE, "Found encrypted Private key (PKCS#8)");
                    if (privateKey == null) {
                        // Handles encrypted PKCS#8 private keys (-----BEGIN ENCRYPTED PRIVATE KEY-----)
                        PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo =
                                (PKCS8EncryptedPrivateKeyInfo) pemObj;
                        try {
                            // Build a decryptor provider with the user-provided password
                            JcePKCSPBEInputDecryptorProviderBuilder decryptorBuilder =
                                    new JcePKCSPBEInputDecryptorProviderBuilder();
                            // Decrypt the private key info
                            PrivateKeyInfo decryptedInfo =
                                    encryptedPrivateKeyInfo.decryptPrivateKeyInfo(
                                            decryptorBuilder
                                                    .setProvider(BC_PROVIDER)
                                                    .build(passphrase));
                            logger.log(Level.FINE, "Successfully decrypted Private Key (PKCS#8)");
                            // Convert the decrypted info to a Java PrivateKey object
                            privateKey = converter.getPrivateKey(decryptedInfo);
                            logger.log(
                                    Level.FINE,
                                    "Successfully loaded Private Key (PKCS#8). Algorithm: "
                                            + privateKey.getAlgorithm());

                        } catch (Exception e) {
                            // Specific error for incorrect password or decryption failure
                            throw new GeneralSecurityException(
                                    "Incorrect password or decryption error for PKCS#8 key: "
                                            + e.getMessage(),
                                    e);
                        }
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Already found a private key. This one will be ignored.");
                    }
                } else if (pemObj instanceof PEMEncryptedKeyPair) {
                    logger.log(Level.FINE, "Private key is encrypted (PKCS#1)");
                    if (privateKey == null) {
                        // Handles encrypted PKCS#1 private keys (-----BEGIN RSA PRIVATE KEY----- or -----BEGIN EC PRIVATE KEY-----)
                        PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) pemObj;
                        try {
                            // Build a decryptor provider with the user-provided password
                            JcePEMDecryptorProviderBuilder decryptorBuilder =
                                    new JcePEMDecryptorProviderBuilder();
                            // Decrypt the key pair
                            PEMKeyPair decryptedKeyPair =
                                    encryptedKeyPair.decryptKeyPair(
                                            decryptorBuilder
                                                    .setProvider(BC_PROVIDER)
                                                    .build(passphrase));
                            logger.log(Level.FINE, "Successfully decrypted Private Key (PKCS#1)");
                            // Convert the decrypted key pair to a Java KeyPair and get the private key
                            privateKey = converter.getKeyPair(decryptedKeyPair).getPrivate();
                            logger.log(
                                    Level.FINE,
                                    "Successfully loaded Private Key (PKCS#1). Algorithm: "
                                            + privateKey.getAlgorithm());
                        } catch (PEMException e) {
                            // Specific error for incorrect password or decryption failure
                            throw new GeneralSecurityException(
                                    "Incorrect password or decryption error for PKCS#1 key: "
                                            + e.getMessage(),
                                    e);
                        } catch (Exception e) {
                            // Catch any other unexpected errors during decryption
                            throw new MongoException(
                                    "An unexpected error occurred during PKCS#1 key decryption: "
                                            + e.getClass().getSimpleName()
                                            + " - "
                                            + e.getMessage(),
                                    e);
                        }
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Already found a private key. This one will be ignored.");
                    }
                } else if (pemObj instanceof X509CertificateHolder) {
                    logger.log(Level.FINE, "Found X.509 Certificate.");
                    if (cert == null) {
                        // Handles X.509 certificates (-----BEGIN CERTIFICATE-----)
                        X509CertificateHolder certHolder = (X509CertificateHolder) pemObj;
                        logger.log(Level.FINE, "Successfully loaded X.509 Certificate.");
                        logger.log(Level.FINER, "  Subject: " + certHolder.getSubject());
                        logger.log(Level.FINER, "  Issuer: " + certHolder.getIssuer());
                        logger.log(Level.FINER, "  Serial Number: " + certHolder.getSerialNumber());
                        logger.log(
                                Level.FINER, "  Validity information: " + certHolder.getNotAfter());
                        cert =
                                new JcaX509CertificateConverter()
                                        .setProvider(BC_PROVIDER)
                                        .getCertificate(certHolder);
                    } else {
                        logger.log(
                                Level.WARNING,
                                "Already found an X509 certificate. This one will be ignored.");
                    }
                } else {
                    // For any other unrecognized PEM object types
                    logger.log(
                            Level.FINE,
                            "Found PEM object type: "
                                    + pemObj.getClass().getName()
                                    + ". This PEM object type is not used for X509 authentication and will be ignored.");
                }
            }
        } catch (IOException e) {
            // Catches errors related to file reading (e.g., file not found, permission issues)
            logger.log(Level.SEVERE, "Error reading file or PEM parsing issue: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // General catch-all for any other unexpected runtime exceptions during processing
            logger.log(
                    Level.SEVERE,
                    "An unexpected error occurred during PEM object processing: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage());
            throw e;
        }

        // The private key and X509 certificate must be fund for
        StringBuilder missingComponents = new StringBuilder();
        if (privateKey == null) {
            missingComponents.append("Private key not found (encrypted or unencrypted)");
        }
        if (cert == null) {
            if (missingComponents.length() > 0) {
                missingComponents.append(" and ");
            }
            missingComponents.append("X.509 certificate not found");
        }
        if (missingComponents.length() > 0) {
            missingComponents.append(" in the PEM file");
            throw new MongoException(missingComponents.toString());
        }

        return createSSLContextFromKeyAndCert(privateKey, cert);
    }

    private SSLContext createSSLContextFromKeyAndCert(PrivateKey privateKey, Certificate cert)
            throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12", BC_PROVIDER);

        keyStore.load(null, null);
        keyStore.setKeyEntry("mongodb-cert", privateKey, null, new Certificate[] {cert});

        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        // Passphrase not needed for in memory keystore
        kmf.init(keyStore, null);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        // Initialize sslContext and use default trust managers
        sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

        return sslContext;
    }

    private SSLContext createSSLContextFromKeystore(
            String keystorePath, char[] keystorePassword, String certificateAlias)
            throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        try (FileInputStream keystoreStream = new FileInputStream(keystorePath)) {
            keystore.load(keystoreStream, keystorePassword);
            logger.log(Level.FINE, "Successfully loaded JKS keystore from: " + keystorePath);
        } catch (IOException e) {
            throw new MongoException("Failed to read keystore file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MongoException("Failed to load keystore: " + e.getMessage(), e);
        }

        Certificate cert = keystore.getCertificate(certificateAlias);
        if (cert == null) {
            throw new MongoException(
                    "Certificate with alias '" + certificateAlias + "' not found in keystore");
        }
        logger.log(Level.FINE, "Found certificate with alias: " + certificateAlias);

        PrivateKey privateKey;
        try {
            Key key = keystore.getKey(certificateAlias, keystorePassword);
            if (key == null) {
                throw new MongoException(
                        "Private key with alias '" + certificateAlias + "' not found in keystore");
            }
            if (!(key instanceof PrivateKey)) {
                throw new MongoException("Key with alias '" + certificateAlias + "' is not a private key");
            }
            privateKey = (PrivateKey) key;
            logger.log(
                    Level.FINE, "Successfully extracted private key with alias: " + certificateAlias);
        } catch (UnrecoverableKeyException e) {
            throw new MongoException(
                    "Failed to extract private key with alias '"
                            + certificateAlias
                            + "': "
                            + e.getMessage(),
                    e);
        }

        return createSSLContextFromKeyAndCert(privateKey, cert);
    }

    private PemAuthenticationInput parsePemAuthenticationInput(String input) {
        try {
            BsonDocument doc = BsonDocument.parse(input);
            if (doc == null) {
                logger.log(Level.FINE, "Failed to parse JSON: input was null or empty");
                return null;
            }
            if (!doc.containsKey("pem")) {
                logger.log(Level.FINE, "Missing required 'pem' field in JSON input");
                return null;
            }
            String pem = doc.getString("pem").getValue();
            String passphrase = null;
            if (doc.containsKey("passphrase")) {
                passphrase = doc.getString("passphrase").getValue();
            }
            return new PemAuthenticationInput(pem, passphrase);
        } catch (Exception e) {
            // Not valid JSON, malformed, or IO error
            logger.log(
                    Level.FINE,
                    "Failed to parse JSON input for X.509 authentication: "
                            + e.getClass().getSimpleName());
            return null;
        }
    }

    // Parsed input for X.509 authentication when provided as JSON in the password field.
    private static class PemAuthenticationInput {
        final String pem;
        final String passphrase;

        PemAuthenticationInput(String pem, String passphrase) {
            if (pem == null || pem.trim().isEmpty()) {
                throw new IllegalArgumentException("PEM content is required");
            }
            this.pem = pem.trim();
            this.passphrase = passphrase;
        }
    }
}
