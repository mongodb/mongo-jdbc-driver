/*
 * Copyright 2022-present MongoDB, Inc.
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

package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import java.io.File;
import java.util.logging.Level;

public class MongoConnectionProperties {
    private ConnectionString connectionString;
    private String database;
    private Level logLevel;
    private File logDir;
    private String clientInfo;
    private boolean extJsonMode;
    private String x509PemPath;

    public MongoConnectionProperties(
            ConnectionString connectionString,
            String database,
            Level logLevel,
            File logDir,
            String clientInfo,
            boolean extJsonMode,
            String x509PemPath) {
        this.connectionString = connectionString;
        this.database = database;
        this.logLevel = logLevel;
        this.logDir = logDir;
        this.clientInfo = clientInfo;
        this.extJsonMode = extJsonMode;
        this.x509PemPath = x509PemPath;
    }

    public ConnectionString getConnectionString() {
        return connectionString;
    }

    public String getDatabase() {
        return database;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public File getLogDir() {
        return logDir;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public boolean getExtJsonMode() {
        return extJsonMode;
    }

    public String getX509PemPath() {
        return x509PemPath;
    }

    /*
     * Generate a unique key for the connection properties. This key is used to identify the connection properties in the
     * connection cache. Properties that do not differentiate a specific client such as the log level are not included in the key.
     */
    public Integer generateKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(connectionString.toString());
        if (clientInfo != null) {
            keyBuilder.append(":clientInfo=").append(clientInfo);
        }
        return keyBuilder.toString().hashCode();
    }
}
