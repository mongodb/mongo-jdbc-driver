package com.mongodb.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MongoConnection implements Connection {

    public Statement createStatement() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public PreparedStatement prepareStatement(String sql)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean getAutoCommit() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void commit() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void rollback() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void close() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isClosed() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isReadOnly() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setCatalog(String catalog) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String getCatalog() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int TRANSACTION_NONE             = 0;
    public int TRANSACTION_READ_UNCOMMITTED = 1;
    public int TRANSACTION_READ_COMMITTED   = 2;
    public int TRANSACTION_REPEATABLE_READ  = 4;
    public int TRANSACTION_SERIALIZABLE     = 8;

    public void setTransactionIsolation(int level) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getTransactionIsolation() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    //--------------------------JDBC 2.0-----------------------------

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                       int resultSetConcurrency)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
                                  int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.util.Map<String,Class<?>> getTypeMap() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setTypeMap(java.util.Map<String,Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //--------------------------JDBC 3.0-----------------------------


    public void setHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency,
                              int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                       int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
                                  int resultSetConcurrency,
                                  int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[])
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[])
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isValid(int timeout) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setClientInfo(String name, String value)
                throws SQLClientInfoException {
        throw new SQLClientInfoException(null);
    }

    public void setClientInfo(Properties properties)
                throws SQLClientInfoException {
        throw new SQLClientInfoException(null);
    }

    public String getClientInfo(String name)
                throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Properties getClientInfo()
                throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

   //--------------------------JDBC 4.1 -----------------------------

    public void setSchema(String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // JDBC 4.3

    public void beginRequest() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void endRequest() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setShardingKey(ShardingKey shardingKey)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // java.sql.Wrapper impl
    public boolean isWrapperFor(Class< ? > iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}

