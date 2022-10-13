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

import com.google.common.base.Preconditions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.DisableAutoLogging;
import com.mongodb.jdbc.logging.MongoLogger;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;

@AutoLoggable
public class MongoConnection implements Connection {
    private MongoClientSettings mongoClientSettings;
    protected MongoClient mongoClient;
    protected String currentDB;
    protected String url;
    protected String user;
    protected boolean isClosed;
    private MongoLogger logger;
    protected int connectionId;
    private static AtomicInteger connectionCounter = new AtomicInteger();
    private AtomicInteger stmtCounter = new AtomicInteger();
    private static ConsoleHandler consoleHandler;
    private static Map<String, Integer> handlerCount = new HashMap<String, Integer>();
    private static Map<String, FileHandler> fileHandlers = new HashMap<String, FileHandler>();
    private String logDirPath;
    private boolean extJsonMode;

    public MongoConnection(MongoConnectionProperties connectionProperties) {
        Preconditions.checkNotNull(connectionProperties.getConnectionString());
        this.connectionId = connectionCounter.incrementAndGet();
        // Initializes a parent logger for the connection
        initConnectionLogger(
                connectionId, connectionProperties.getLogLevel(), connectionProperties.getLogDir());
        this.url = connectionProperties.getConnectionString().getConnectionString();
        this.user = connectionProperties.getConnectionString().getUsername();
        this.currentDB = connectionProperties.getDatabase();
        this.extJsonMode = connectionProperties.getExtJsonMode();
        String version =
                MongoDriver.VERSION != null
                        ? MongoDriver.VERSION
                        : new StringBuilder()
                                .append(MongoDriver.MAJOR_VERSION)
                                .append(".")
                                .append(MongoDriver.MINOR_VERSION)
                                .toString();
        StringBuilder appName = new StringBuilder(MongoDriver.NAME).append("+").append(version);

        // Log the driver name and version
        logger.log(Level.INFO, "Connecting using " + MongoDriver.MONGO_DRIVER_NAME + " " + version);

        MongoDriverInformation.Builder mdiBuilder;
        String clientInfo = connectionProperties.getClientInfo();
        String[] clientInfoSplit = (clientInfo == null) ? null : clientInfo.split("\\+");
        if (clientInfoSplit != null && clientInfoSplit.length == 2) {
            appName.append('|').append(clientInfo);
            MongoDriverInformation driverInfoWithClientInfo =
                    MongoDriverInformation.builder()
                            .driverName(clientInfoSplit[0])
                            .driverVersion(clientInfoSplit[1])
                            .build();
            mdiBuilder = MongoDriverInformation.builder(driverInfoWithClientInfo);
        } else {
            mdiBuilder = MongoDriverInformation.builder();
        }
        MongoDriverInformation mongoDriverInformation =
                mdiBuilder.driverName(MongoDriver.NAME).driverVersion(version).build();

        this.mongoClientSettings =
                MongoClientSettings.builder()
                        .applicationName(appName.toString())
                        .applyConnectionString(connectionProperties.getConnectionString())
                        .build();
        mongoClient = MongoClients.create(mongoClientSettings, mongoDriverInformation);

        isClosed = false;
    }

    @DisableAutoLogging
    public MongoLogger getLogger() {
        return logger;
    }

    protected int getNextStatementId() {
        return stmtCounter.incrementAndGet();
    }

    protected void checkConnection() throws SQLException {
        if (isClosed) {
            throw new SQLException("Connection is closed.");
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkConnection();
        try {
            return new MongoStatement(this, currentDB);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    protected int getDefaultConnectionValidationTimeoutSeconds() {
        return this.mongoClientSettings.getSocketSettings().getConnectTimeout(TimeUnit.SECONDS);
    }

    boolean getExtJsonMode() {
        return extJsonMode;
    }

    String getURL() {
        return url;
    }

    String getUser() {
        return user;
    }

    String getServerVersion() throws SQLException {
        checkConnection();

        BsonDocument command = new BsonDocument();
        command.put("buildInfo", new BsonInt32(1));
        try {
            Document result = mongoClient.getDatabase("admin").runCommand(command);
            return (String) result.get("version");
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    protected MongoDatabase getDatabase(String DBName) {
        return mongoClient.getDatabase(DBName);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MongoDatabaseMetaData(this);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        try {
            return new MongoPreparedStatement(sql, new MongoStatement(this, currentDB));
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkConnection();
        if (autoCommit) {
            throw new SQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString());
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkConnection();
        return false;
    }

    @Override
    public void commit() throws SQLException {
        checkConnection();
    }

    @Override
    public void rollback() throws SQLException {
        checkConnection();
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }
        mongoClient.close();

        // Decrement fileHandlerCount and delete entry
        // if no more connections are using it.
        synchronized (this) {
            if ((null != handlerCount) && handlerCount.containsKey(logDirPath)) {
                handlerCount.put(logDirPath, handlerCount.get(logDirPath) - 1);
                if (handlerCount.get(logDirPath) == 0) {
                    // Remove the FileHandler and remove this entry too
                    if (null != fileHandlers) {
                        fileHandlers.remove(logDirPath);
                    }
                    handlerCount.remove(logDirPath);
                }
            }
        }

        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkConnection();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkConnection();
        return true;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkConnection();
        currentDB = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        checkConnection();
        return currentDB;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkConnection();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkConnection();
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkConnection();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkConnection();
    }

    // --------------------------JDBC 2.0-----------------------------
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return createStatement();
        } else {
            throw new SQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString());
        }
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return prepareStatement(sql);
        } else {
            throw new SQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString());
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // --------------------------JDBC 3.0-----------------------------

    @Override
    public Statement createStatement(
            int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return createStatement();
        } else {
            throw new SQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString());
        }
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String columnNames[])
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkConnection();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    class ConnValidation implements Callable<Void> {
        @Override
        public Void call() throws SQLException {
            Statement statement = createStatement();
            boolean resultExists = statement.execute("SELECT 1");
            if (!resultExists) {
                // no resultSet returned
                throw new SQLException("Connection error");
            }
            return null;
        }
    }

    /**
     * Executes a dummy query to test the connection.
     *
     * @param timeout The query timeout.
     * @throws Exception If an error occurs.
     */
    protected void testConnection(int timeout)
            throws SQLException, InterruptedException, ExecutionException, TimeoutException {
        if (timeout < 0) {
            throw new SQLException("Input is invalid.");
        }

        if (isClosed) {
            throw new SQLException("Connection is closed.");
        }

        // We use createStatement to test the connection. Since we are not allowed
        // to set the timeout adhoc on the calls, we use Executor to run a blocked call with timeout.
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Void> future = executor.submit(new ConnValidation());

        try {
            if (timeout > 0) {
                future.get(timeout, TimeUnit.SECONDS);
            } else {
                future.get();
            }
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        try {
            testConnection(timeout);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            // Only propagate the SQLException
            return false;
        }
        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new SQLClientInfoException(null);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException(null);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // --------------------------JDBC 4.1 -----------------------------

    @Override
    public void setSchema(String schema) throws SQLException {
        // JDBC standard says this function is ignored if schemas are not supported.
        // So we do not want to check the connection.
    }

    @Override
    public String getSchema() throws SQLException {
        // JDBC standard says this function is ignored if schemas are not supported.
        // So we do not want to check the connection.
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // JDBC 4.3

    public void beginRequest() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    public void endRequest() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }

    private void initConnectionLogger(Integer connection_id, Level logLevel, File logDir) {
        Logger logger =
                Logger.getLogger(connection_id + "_" + MongoConnection.class.getCanonicalName());
        try {
            if (logLevel != null) {
                // If log level is not OFF, create a new handler.
                // Otherwise, don't bother.
                if (logLevel != Level.OFF) {
                    // If a log directory is provided, get the file handler to log messages
                    // in that directory or create a new one if none exist yet.
                    if (logDir != null) {
                        logDirPath = logDir.getAbsolutePath();
                        synchronized (this) {
                            if (!fileHandlers.containsKey(logDirPath)) {
                                String logPath = logDirPath + File.separator + "connection.log";
                                FileHandler fileHandler = new FileHandler(logPath);
                                fileHandler.setLevel(logLevel);
                                fileHandler.setFormatter(new SimpleFormatter());
                                fileHandlers.put(logDirPath, fileHandler);
                                if (handlerCount.containsKey(logDirPath)) {
                                    handlerCount.put(logDirPath, handlerCount.get(logDirPath) + 1);
                                } else {
                                    handlerCount.put(logDirPath, Integer.valueOf(1));
                                }
                            }
                            logger.addHandler(fileHandlers.get(logDirPath));
                        }
                    }
                    // If no directory is provided, send the message to the console
                    else {
                        if (consoleHandler == null) {
                            consoleHandler = new ConsoleHandler();
                            consoleHandler.setFormatter(new SimpleFormatter());
                            consoleHandler.setLevel(logLevel);
                        }
                        logger.addHandler(consoleHandler);
                    }
                }

                // Set the overall logger level too
                logger.setLevel(logLevel);
            }
        } catch (IOException e) {
            // Can't log the error since it can't open the log file
            e.printStackTrace();
        }

        this.logger = new MongoLogger(logger, connectionId);
    }
}
