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

import com.mongodb.jdbc.logging.MongoLogger;
import java.io.FileReader;
import java.security.*;
import java.security.cert.Certificate;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
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
            Object pemObj;

            // Iterate through PEM objects found in the PEM file and process them based on type:
            //  - Encrypted/unencrypted private keys
            //  - X.509 certificates
            while ((pemObj = pemParser.readObject()) != null) {
                try {
                    if (passphrase != null
                            && passphrase.length > 0
                            && pemObj instanceof PKCS8EncryptedPrivateKeyInfo) {
                        privateKey =
                                new JcaPEMKeyConverter()
                                        .setProvider(BC_PROVIDER)
                                        .getPrivateKey(
                                                ((PKCS8EncryptedPrivateKeyInfo) pemObj)
                                                        .decryptPrivateKeyInfo(
                                                                new JcePKCSPBEInputDecryptorProviderBuilder()
                                                                        .setProvider(BC_PROVIDER)
                                                                        .build(passphrase)));
                    } else if (pemObj instanceof PrivateKeyInfo) {
                        privateKey =
                                new JcaPEMKeyConverter()
                                        .setProvider(BC_PROVIDER)
                                        .getPrivateKey((PrivateKeyInfo) pemObj);
                    }
                } catch (Exception e) {
                    throw new GeneralSecurityException(
                            "Failed to process private key from PEM file", e);
                }

                if (pemObj instanceof X509CertificateHolder) {
                    cert =
                            new JcaX509CertificateConverter()
                                    .setProvider(BC_PROVIDER)
                                    .getCertificate((X509CertificateHolder) pemObj);
                }
            }
        }

        if (privateKey == null) {
            throw new IllegalStateException(
                    "Failed to read private key from PEM file (encrypted or unencrypted)");
        }
        if (cert == null) {
            throw new IllegalStateException("Failed to read certificate from PEM file");
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
