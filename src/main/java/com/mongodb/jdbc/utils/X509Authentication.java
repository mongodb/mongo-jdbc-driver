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
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
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

public class X509Authentication {
    private static final BouncyCastleProvider BC_PROVIDER = new BouncyCastleProvider();
    private final MongoLogger logger;

    public X509Authentication(MongoLogger logger) {
        this.logger = logger;
    }

    public void configureX509Authentication(
            com.mongodb.MongoClientSettings.Builder settingsBuilder,
            String pemPath,
            char[] passphrase) {

        logger.log(Level.FINE, "Using client certificate for X509 authentication: " + pemPath);
        if (passphrase != null && passphrase.length > 0) {
            logger.log(Level.FINE, "Client certificate passphrase has been specified");
        }
        try {
            SSLContext sslContext = createSSLContext(pemPath, passphrase);

            settingsBuilder.applyToSslSettings(
                    sslSettings -> {
                        sslSettings.enabled(true);
                        sslSettings.context(sslContext);
                    });
        } catch (Exception e) {
            throw new RuntimeException("SSL setup failed", e);
        }
    }

    private SSLContext createSSLContext(String pemPath, char[] passphrase) throws Exception {
        PrivateKey privateKey = null;
        Certificate cert = null;

        try (PEMParser pemParser = new PEMParser(new FileReader(pemPath))) {
            Object pemObj = null;

            // Iterate through PEM objects found in the PEM file and process them based on type:
            //  - Encrypted/unencrypted private keys
            //  - X.509 certificates
            while ((pemObj = pemParser.readObject()) != null) {

                // Initialize the JcaPEMKeyConverter with the Bouncy Castle provider name.
                // This converter is used to transform Bouncy Castle's internal ASN.1 objects
                // into Java Security API (JCA) objects like PrivateKey, PublicKey, etc.
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BC_PROVIDER);

                // Determine the type of PEM object and process accordingly
                if (pemObj instanceof PrivateKeyInfo) {
                    // Handles unencrypted PKCS#8 private keys (-----BEGIN PRIVATE KEY-----)
                    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemObj;
                    privateKey = converter.getPrivateKey(privateKeyInfo);
                    logger.log(Level.FINE, "Successfully loaded unencrypted Private Key (PKCS#8). Algorithm: " + privateKey.getAlgorithm());
                } else if (pemObj instanceof PEMKeyPair) {
                    // Handles unencrypted PKCS#1 private keys (-----BEGIN RSA PRIVATE KEY----- or -----BEGIN EC PRIVATE KEY-----)
                    PEMKeyPair pemKeyPair = (PEMKeyPair) pemObj;
                    privateKey = converter.getKeyPair(pemKeyPair).getPrivate();
                    logger.log(Level.FINE, "Successfully loaded unencrypted Private Key (PKCS#1). Algorithm: " + privateKey.getAlgorithm());
                } else if (pemObj instanceof PKCS8EncryptedPrivateKeyInfo) {
                    logger.log(Level.FINE, "Private key is encrypted (PKCS#8)");
                    // Handles encrypted PKCS#8 private keys (-----BEGIN ENCRYPTED PRIVATE KEY-----)
                    PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = (PKCS8EncryptedPrivateKeyInfo) pemObj;
                    try {
                        // Build a decryptor provider with the user-provided password
                        JcePKCSPBEInputDecryptorProviderBuilder decryptorBuilder = new JcePKCSPBEInputDecryptorProviderBuilder();
                        // Decrypt the private key info
                        PrivateKeyInfo decryptedInfo = encryptedPrivateKeyInfo.decryptPrivateKeyInfo(decryptorBuilder.setProvider(BC_PROVIDER).build(passphrase));
                        // Convert the decrypted info to a Java PrivateKey object
                        privateKey = converter.getPrivateKey(decryptedInfo);
                        logger.log(Level.FINE, "Successfully decrypted and loaded Private Key (PKCS#8). Algorithm: " + privateKey.getAlgorithm());
                    } catch (Exception e) {
                        // Specific error for incorrect password or decryption failure
                        throw new GeneralSecurityException("Incorrect password or decryption error for PKCS#8 key: " + e.getMessage(), e);
                    }
                } else if (pemObj instanceof PEMEncryptedKeyPair) {
                    logger.log(Level.FINE, "Private key is encrypted (PKCS#1)");
                    // Handles encrypted PKCS#1 private keys (-----BEGIN RSA PRIVATE KEY----- or -----BEGIN EC PRIVATE KEY-----)
                    PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) pemObj;
                    try {
                        // Build a decryptor provider with the user-provided password
                        JcePEMDecryptorProviderBuilder decryptorBuilder = new JcePEMDecryptorProviderBuilder();
                        // Decrypt the key pair
                        PEMKeyPair decryptedKeyPair = encryptedKeyPair.decryptKeyPair(decryptorBuilder.build(passphrase));
                        // Convert the decrypted key pair to a Java KeyPair and get the private key
                        privateKey = converter.getKeyPair(decryptedKeyPair).getPrivate();
                        logger.log(Level.FINE, "Successfully decrypted and loaded Private Key (PKCS#1). Algorithm: " + privateKey.getAlgorithm());
                    } catch (PEMException e) {
                        // Specific error for incorrect password or decryption failure
                        throw new GeneralSecurityException("Incorrect password or decryption error for PKCS#1 key: " + e.getMessage(), e);
                    } catch (Exception e) {
                        // Catch any other unexpected errors during decryption
                        throw new MongoException("An unexpected error occurred during PKCS#1 key decryption: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
                    }
                } else if (pemObj instanceof X509CertificateHolder) {
                    // Handles X.509 certificates (-----BEGIN CERTIFICATE-----)
                    X509CertificateHolder certHolder = (X509CertificateHolder) pemObj;
                    logger.log(Level.FINE, "Successfully loaded X.509 Certificate.");
                    logger.log(Level.FINER, "  Subject: " + certHolder.getSubject());
                    logger.log(Level.FINER, "  Issuer: " + certHolder.getIssuer());
                    logger.log(Level.FINER, "  Serial Number: " + certHolder.getSerialNumber());
                    logger.log(Level.FINER, "  Validity information: " + certHolder.getNotAfter());
                    cert = new JcaX509CertificateConverter()
                            .setProvider(BC_PROVIDER)
                            .getCertificate(certHolder);
                } else if (pemObj instanceof SubjectPublicKeyInfo) {
                    // Handles public keys (-----BEGIN PUBLIC KEY-----)
                    SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemObj;
                    PublicKey publicKey = converter.getPublicKey(publicKeyInfo);
                    logger.log(Level.FINER,"Successfully loaded Public Key. Algorithm: " + publicKey.getAlgorithm());
                } else {
                    // For any other unrecognized PEM object types
                    throw new MongoException("Unsupported PEM object type found: " + pemObj.getClass().getName() + ". Cannot process this type.");
                }
            }
        } catch (IOException e) {
            // Catches errors related to file reading (e.g., file not found, permission issues)
            logger.log(Level.SEVERE,"Error reading file or PEM parsing issue: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // General catch-all for any other unexpected runtime exceptions during processing
            logger.log(Level.SEVERE,"An unexpected error occurred during PEM object processing: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }

        if (privateKey == null) {
            throw new MongoException("Failed to read private key from PEM file (encrypted or unencrypted)");
        }
        if (cert == null) {
            throw new MongoException("Failed to read X509 certificate from PEM file");
        }

        return createSSLContextFromKeyAndCert(privateKey, cert);
    }

    private SSLContext createSSLContextFromKeyAndCert(PrivateKey privateKey, Certificate cert)
            throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
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
}
