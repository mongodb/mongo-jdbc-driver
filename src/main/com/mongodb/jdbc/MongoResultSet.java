package com.mongodb.jdbc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.io.Reader;
import java.io.InputStream;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;

import com.mongodb.client.FindIterable;

public class MongoResultSet implements ResultSet {
    private FindIterable cursor;

    public MongoResultSet(FindIterable cursor) {
        this.cursor = cursor;
    }

    public boolean next() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void close() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean wasNull() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // Methods for accessing results by column index

    public String getString(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getInt(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated(since="1.2")
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Date getDate(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Time getTime(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated(since="1.2")
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getBinaryStream(int columnIndex)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    // Methods for accessing results by column label

    public String getString(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte getByte(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public short getShort(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getInt(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public long getLong(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public float getFloat(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public double getDouble(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated(since="1.2")
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Date getDate(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Time getTime(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated(since="1.2")
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getBinaryStream(String columnLabel)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    // Advanced features:

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //----------------------------------------------------------------

    public int findColumn(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    //--------------------------JDBC 2.0-----------------------------------

    //---------------------------------------------------------------------
    // Getters and Setters
    //---------------------------------------------------------------------

    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //---------------------------------------------------------------------
    // Traversal/Positioning
    //---------------------------------------------------------------------

    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean absolute( int row ) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean relative( int rows ) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //---------------------------------------------------------------------
    // Properties
    //---------------------------------------------------------------------

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

    public int getType() throws SQLException {
		return ResultSet.TYPE_FORWARD_ONLY;
	}

    public int getConcurrency() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //---------------------------------------------------------------------
    // Updates
    //---------------------------------------------------------------------

    public boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTimestamp(int columnIndex, java.sql.Timestamp x)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex,
                           java.io.InputStream x,
                           int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex,
                           java.io.InputStream x,
                           int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex,
                            java.io.Reader x,
                            int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTime(String columnLabel, java.sql.Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTimestamp(String columnLabel, java.sql.Timestamp x)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(String columnLabel,
                           java.io.InputStream x,
                           int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel,
                            java.io.InputStream x,
                            int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel,
                            java.io.Reader reader,
                            int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Statement getStatement() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Object getObject(int columnIndex, java.util.Map<String,Class<?>> map)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Object getObject(String columnLabel, java.util.Map<String,Class<?>> map)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal)
      throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //-------------------------- JDBC 3.0 ----------------------------------------

    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.net.URL getURL(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //------------------------- JDBC 4.0 -----------------------------------

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isClosed() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public String getNString(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public String getNString(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNCharacterStream(int columnIndex,
                            java.io.Reader x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNCharacterStream(String columnLabel,
                            java.io.Reader reader,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex,
                            java.io.InputStream x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex,
                            java.io.InputStream x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex,
                            java.io.Reader x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(String columnLabel,
                            java.io.InputStream x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel,
                            java.io.InputStream x,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel,
                            java.io.Reader reader,
                            long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(int columnIndex,  Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(String columnLabel,  Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(int columnIndex,  Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(String columnLabel,  Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //---

    public void updateNCharacterStream(int columnIndex,
                             java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNCharacterStream(String columnLabel,
                             java.io.Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex,
                           java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex,
                            java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex,
                             java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }
    public void updateAsciiStream(String columnLabel,
                           java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel,
                            java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel,
                             java.io.Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(int columnIndex,  Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(String columnLabel,  Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(int columnIndex,  Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(String columnLabel,  Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //------------------------- JDBC 4.1 -----------------------------------

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    //------------------------- JDBC 4.2 -----------------------------------

    public void updateObject(int columnIndex, Object x,
             SQLType targetSqlType, int scaleOrLength)  throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(String columnLabel, Object x,
            SQLType targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(String columnLabel, Object x,
            SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
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
