package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.logging.MongoSQLException;
import com.mongodb.jdbc.logging.MongoSQLFeatureNotSupportedException;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MongoConnection implements Connection {
    protected MongoClient mongoClient;
    protected String currentDB;
    protected String url;
    protected String user;
    protected int connectionId;
    protected boolean isClosed;
    protected final Logger logger;
    private static AtomicInteger connectionCounter = new AtomicInteger();


    public MongoConnection(ConnectionString cs, String database) {
        connectionId = connectionCounter.incrementAndGet();
        logger =  MongoLogger.getLogger(this.getClass().getCanonicalName(), connectionId);
        logger.log(Level.FINE, ">> Creating new MongoConnection");
        Preconditions.checkNotNull(cs);
        this.url = cs.getConnectionString();
        this.user = cs.getUsername();
        this.currentDB = database;
        String version =
                MongoDriver.VERSION != null
                        ? MongoDriver.VERSION
                        : new StringBuilder()
                                .append(MongoDriver.MAJOR_VERSION)
                                .append(".")
                                .append(MongoDriver.MINOR_VERSION)
                                .toString();

        mongoClient =
                MongoClients.create(
                        cs,
                        MongoDriverInformation.builder()
                                .driverName(MongoDriver.NAME)
                                .driverVersion(version)
                                .build());
        logger.log(Level.FINE, "Connection created");
        isClosed = false;
    }

    protected void checkConnection() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (isClosed()) {
            throw new MongoSQLException("Connection is closed.", logger);
        }
    }

    String getURL() {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return url;
    }

    String getUser() {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return user;
    }

    String getServerVersion() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();

        BsonDocument command = new BsonDocument();
        command.put("buildInfo", new BsonInt32(1));
        try {
            Document result = mongoClient.getDatabase("admin").runCommand(command);
            return (String) result.get("version");
        } catch (Exception e) {
            throw new MongoSQLException(e, logger);
        }
    }

    protected MongoDatabase getDatabase(String DBName) {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return mongoClient.getDatabase(DBName);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        if (autoCommit) {
            throw new MongoSQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString(), logger);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        return false;
    }

    @Override
    public void commit() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    @Override
    public void rollback() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    @Override
    public void close() {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (isClosed()) {
            return;
        }
        mongoClient.close();
        isClosed = true;
    }

    @Override
    public boolean isClosed() {

        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return isClosed;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        return true;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        currentDB = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        return currentDB;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    // --------------------------JDBC 2.0-----------------------------
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return createStatement();
        } else {
            throw new MongoSQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString(), logger);
        }
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return prepareStatement(sql);
        } else {
            throw new MongoSQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString(), logger);
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    // --------------------------JDBC 3.0-----------------------------

    @Override
    public Statement createStatement(
            int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {
            return createStatement();
        } else {
            throw new MongoSQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString(), logger);
        }
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String columnNames[])
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public int getHoldability() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        checkConnection();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public CallableStatement prepareCall(
            String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Clob createClob() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Blob createBlob() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    private void validateConn() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        Statement statement = createStatement();
        boolean resultExists = statement.execute("SELECT 1");
        if (!resultExists) {
            // no resultSet returned
            throw new MongoSQLException("Connection error", logger);
        }
    }

    class ConnValidation implements Callable<Object> {
        @Override
        public Object call() throws SQLException {
            MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                    getMethodName());
            Statement statement = createStatement();
            boolean resultExists = statement.execute("SELECT 1 from DUAL");
            if (!resultExists) {
                // no resultSet returned
                throw new MongoSQLException("Connection error", logger);
            }
            return null;
        }
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        if (timeout < 0) {
            throw new MongoSQLException("Input is invalid.", logger);
        }

        if (isClosed()) {
            return false;
        }
        // We use createStatement to test the connection. Since we are not allowed
        // to set the timeout adhoc on the calls, we use Executor to run a blocked call with timeout.
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<Object> future = executor.submit(new ConnValidation());
        try {
            if (timeout > 0) {
                future.get(timeout, TimeUnit.SECONDS);
            } else {
                future.get();
            }
        } catch (TimeoutException ex) {
            // handle the timeout
            return false;
        } catch (InterruptedException e) {
            // handle the interrupt
            return false;
        } catch (ExecutionException e) {
            // handle connection error
            return false;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new SQLClientInfoException(null);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new SQLClientInfoException(null);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    // --------------------------JDBC 4.1 -----------------------------

    @Override
    public void setSchema(String schema) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        // JDBC standard says this function is ignored if schemas are not supported.
        // So we do not want to check the connection.
    }

    @Override
    public String getSchema() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        // JDBC standard says this function is ignored if schemas are not supported.
        // So we do not want to check the connection.
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    // JDBC 4.3

    public void beginRequest() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    public void endRequest() throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        throw new MongoSQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString(), logger);
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        MongoLogger.logMethodEntry(logger, Thread.currentThread().getStackTrace()[1].
                getMethodName());
        return (T) this;
    }
}
