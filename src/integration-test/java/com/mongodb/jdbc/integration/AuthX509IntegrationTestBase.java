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

package com.mongodb.jdbc.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.net.ssl.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class AuthX509IntegrationTestBase {
    protected static final String LOCAL_PORT_ENV_VAR = "LOCAL_MDB_PORT_ENT";
    protected static final String PASSWORD_ENV_VAR = "ADF_TEST_LOCAL_PWD";
    protected static final String X509_CERT_PATH_PROPERTY = "x509PemPath";

    protected String mongoPort;
    protected String passwordEnv;

    @BeforeEach
    public void setUp() {
        mongoPort = System.getenv(LOCAL_PORT_ENV_VAR);
        assertNotNull(mongoPort, "Environment variable " + LOCAL_PORT_ENV_VAR + " must be set");
        passwordEnv = System.getenv(PASSWORD_ENV_VAR);
    }

    protected Connection connectWithX509(String pemPath, String passphrase) throws SQLException {
        return connectWithX509(pemPath, passphrase, null, null);
    }

    protected Connection connectWithX509(
            String pemPath, String passphrase, String tlsCaFile, String uriOption)
            throws SQLException {
        String uri =
                "jdbc:mongodb://localhost:"
                        + mongoPort
                        + "/?authSource=$external&authMechanism=MONGODB-X509&tls=true";

        if (uriOption != null) {
            uri = uri + "&" + uriOption;
        }

        Properties properties = new Properties();
        properties.setProperty("database", "test");

        if (pemPath != null) {
            properties.setProperty(X509_CERT_PATH_PROPERTY, pemPath);
        }
        if (passphrase != null) {
            properties.setProperty("password", passphrase);
        }
        if (tlsCaFile != null) {
            properties.setProperty("tlscafile", tlsCaFile);
        }

        return DriverManager.getConnection(uri, properties);
    }
}
