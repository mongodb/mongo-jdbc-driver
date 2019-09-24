package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLFeatureNotSupportedException;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoStatement implements Statement {
    // Likely, the actual mongo sql command will not
    // need a database or collection, since those
    // must be parsed from the query.
    private MongoCollection col;

    public MongoStatement(MongoClient client, String currentDB) {
        // TODO: obviously don't just connect to `test.test`.
        col = client.getDatabase(currentDB).getCollection("test");
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        // TODO: actually use query.
        MongoCursor<Document> cur = col.find().iterator();
        return new MongoResultSet(cur);
    }

    public int executeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void close() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //----------------------------------------------------------------------

    public int getMaxFieldSize() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setMaxFieldSize(int max) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getMaxRows() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setMaxRows(int max) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getQueryTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void cancel() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setCursorName(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //----------------------- Multiple Results --------------------------

    public boolean execute(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public ResultSet getResultSet() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getUpdateCount() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean getMoreResults() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    //--------------------------JDBC 2.0-----------------------------


    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getFetchDirection() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getFetchSize() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getResultSetConcurrency() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getResultSetType()  throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void addBatch( String sql ) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Connection getConnection()  throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

  //--------------------------JDBC 3.0-----------------------------

    public boolean getMoreResults(int current) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int executeUpdate(String sql, String columnNames[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean execute(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean execute(String sql, String columnNames[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getResultSetHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isClosed() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void setPoolable(boolean poolable)
                throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isPoolable()
                throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //--------------------------JDBC 4.1 -----------------------------

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    //--------------------------JDBC 4.2 -----------------------------

    public long getLargeUpdateCount() throws SQLException {
        throw new SQLFeatureNotSupportedException("getLargeUpdateCount not implemented");
    }

    public void setLargeMaxRows(long max) throws SQLException {
        throw new SQLFeatureNotSupportedException("setLargeMaxRows not implemented");
    }

    public long getLargeMaxRows() throws SQLException {
        return 0;
    }

    public long[] executeLargeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("executeLargeBatch not implemented");
    }

    public long executeLargeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
    }

    public long executeLargeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
    }

    public long executeLargeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
    }

    public long executeLargeUpdate(String sql, String columnNames[])
            throws SQLException {
        throw new SQLFeatureNotSupportedException("executeLargeUpdate not implemented");
    }

    // JDBC 4.3

    public String enquoteLiteral(String val)  throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isSimpleIdentifier(String identifier) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String enquoteNCharLiteral(String val)  throws SQLException {
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

