package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
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
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;

public class MongoConnection implements Connection {
    private MongoClient mongoClient;
    private String currentDB;
    private String url;
    private String user;
    private boolean isClosed;
    private boolean relaxed;

    public MongoConnection(ConnectionString cs, String database, String conversionMode) {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Preconditions.checkNotNull(cs);
        this.url = cs.getConnectionString();
        this.user = cs.getUsername();
        this.currentDB = database;
        mongoClient = MongoClients.create(cs);
        relaxed = conversionMode == null || !conversionMode.equals("strict");
        isClosed = false;
    }

    private void checkConnection() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Connection is closed.");
        }
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

    @Override
    public Statement createStatement() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        try {
            return new MongoStatement(this, currentDB, relaxed);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        }
    }

    protected MongoDatabase getDatabase(String DBName) {
        return mongoClient.getDatabase(DBName);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void commit() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public void rollback() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public void close() {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (isClosed()) {
            return;
        }
        mongoClient.close();
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isClosed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new MongoDatabaseMetaData(this);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        return true;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.write("\nSet: " + catalog + "\n");
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        currentDB = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        return currentDB;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    Thread.currentThread().getStackTrace().toString());
        }
    }

    @Override
    public PreparedStatement prepareStatement(
            String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // --------------------------JDBC 3.0-----------------------------

    @Override
    public void setHoldability(int holdability) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getHoldability() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

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
    public CallableStatement prepareCall(
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
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public Clob createClob() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Blob createBlob() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public NClob createNClob() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    private void validateConn() throws SQLException {
        Statement statement = createStatement();
        boolean resultExists = statement.execute("SELECT 1 from DUAL");
        if (!resultExists) {
            // no resultSet returned
            throw new SQLException("Connection error");
        }
    }

    class ConnValidation implements Callable<Object> {
        @Override
        public Object call() throws SQLException {
            try {
                MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
                MongoDriver.b.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Statement statement = createStatement();
            boolean resultExists = statement.execute("SELECT 1 from DUAL");
            if (!resultExists) {
                // no resultSet returned
                throw new SQLException("Connection error");
            }
            return null;
        }
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (timeout < 0) {
            throw new SQLException("Input is invalid.");
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
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLClientInfoException(null);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLClientInfoException(null);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // --------------------------JDBC 4.1 -----------------------------

    @Override
    public void setSchema(String schema) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
    }

    @Override
    public String getSchema() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkConnection();
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // JDBC 4.3

    public void beginRequest() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    public void endRequest() throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            MongoDriver.b.write(Thread.currentThread().getStackTrace()[1].toString());
            MongoDriver.b.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (T) this;
    }
}
