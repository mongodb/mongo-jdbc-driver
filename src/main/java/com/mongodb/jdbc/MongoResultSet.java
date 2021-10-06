package com.mongodb.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import org.bson.BsonValue;

public abstract class MongoResultSet implements ResultSet {
    // dateFormat cannot be static due to a threading bug in the library.
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected final String ARRAY = "array";
    protected final String BINARY = "binary";
    protected final String BOOLEAN = "boolean";
    protected final String BSON = "bson";
    protected final String DATE = "date";
    protected final String DB_POINTER = "db_pointer";
    protected final String DECIMAL128 = "decimal128";
    protected final String DOCUMENT = "document";
    protected final String DOUBLE = "double";
    protected final String END_OF_DOCUMENT = "end_of_document";
    protected final String INT32 = "int32";
    protected final String INT64 = "int64";
    protected final String JAVASCRIPT = "javascript";
    protected final String JAVASCRIPT_WITH_CODE = "javascript_with_code";
    protected final String MAX_KEY = "max_key";
    protected final String MIN_KEY = "min_key";
    protected final String OBJECT_ID = "objectId";
    protected final String OBJECT = "object";
    protected final String REGEX = "regex";
    protected final String STRING = "string";
    protected final String SYMBOL = "symbol";
    protected final String TIMESTAMP = "timestamp";

    // The one-indexed number of the current row. Will be zero until
    // next() is called for the first time.
    protected int rowNum = 0;

    protected boolean closed = false;
    protected Statement statement;
    protected boolean wasNull = false;
    protected MongoResultSetMetaData rsMetaData;

    public MongoResultSet(Statement statement) {
        this.statement = statement;

        // dateFormat is not thread safe, so we do not want to make it a static field.
        TimeZone UTC = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(UTC);
    }

    protected abstract BsonValue getBsonValue(int columnIndex) throws SQLException;

    protected abstract BsonValue getBsonValue(String columnLabel) throws SQLException;

    protected void checkClosed() throws SQLException {
        if (closed) throw new SQLException("MongoResultSet is closed.");
    }

    @Override
    public boolean wasNull() throws SQLException {
        checkClosed();
        return wasNull;
    }

    // checkNull returns true if the Object o is null. Crucially,
    // it also must set the value of `wasNull`, since that is part
    // of the JDBC API.
    protected abstract boolean checkNull(BsonValue o);

    protected abstract void checkBounds(int i) throws SQLException;

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    protected abstract byte[] handleBytesConversionFailure(String from) throws SQLException;

    protected abstract byte[] getBytes(BsonValue o) throws SQLException;

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBytes(out);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBytes(out);
    }

    protected static ByteArrayInputStream getNewByteArrayInputStream(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        final String encoding = "ASCII";
        try {
            return getNewByteArrayInputStream(getString(columnIndex).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Override
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        final String encoding = "ASCII";
        try {
            return getNewByteArrayInputStream(getString(columnLabel).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        final String encoding = "UTF-8";
        try {
            return getNewByteArrayInputStream(getString(columnIndex).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        final String encoding = "UTF-8";
        try {
            return getNewByteArrayInputStream(getString(columnLabel).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Override
    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getNewByteArrayInputStream(getBytes(columnIndex));
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getNewByteArrayInputStream(getBytes(columnLabel));
    }

    protected abstract String handleStringConversionFailure(String from) throws SQLException;

    protected abstract String getString(BsonValue o) throws SQLException;

    @Override
    public String getString(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getString(out);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getString(out);
    }

    protected abstract boolean handleBooleanConversionFailure(String from) throws SQLException;

    protected abstract boolean getBoolean(BsonValue o) throws SQLException;

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBoolean(out);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBoolean(out);
    }

    protected byte getByte(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (byte) getLong(o);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getByte(out);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getByte(out);
    }

    protected short getShort(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (short) getLong(o);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getShort(out);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getShort(out);
    }

    protected int getInt(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0;
        }
        return (int) getLong(o);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getInt(out);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getInt(out);
    }

    protected abstract long handleLongConversionFailure(String from) throws SQLException;

    protected abstract long getLong(BsonValue o) throws SQLException;

    @Override
    public long getLong(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getLong(out);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getLong(out);
    }

    protected float getFloat(BsonValue o) throws SQLException {
        return (float) getDouble(o);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getFloat(out);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getFloat(out);
    }

    protected abstract double handleDoubleConversionFailure(String from) throws SQLException;

    protected abstract double getDouble(BsonValue o) throws SQLException;

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getDouble(out);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getDouble(out);
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // Advanced features:

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();
        return rsMetaData;
    }

    protected abstract Object getObject(BsonValue o, int columnType) throws SQLException;

    // ----------------------------------------------------------------

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        checkClosed();
        if (!rsMetaData.hasColumnWithLabel(columnLabel)) {
            throw new SQLException("No such column: '" + columnLabel + "'.");
        }
        return rsMetaData.getColumnPositionFromLabel(columnLabel) + 1;
    }

    // --------------------------JDBC 2.0-----------------------------------

    // ---------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    protected abstract BigDecimal handleBigDecimalConversionFailure(String from)
            throws SQLException;

    protected abstract BigDecimal getBigDecimal(BsonValue o) throws SQLException;

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBigDecimal(out);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBigDecimal(out);
    }

    // ---------------------------------------------------------------------
    // Traversal/Positioning
    // ---------------------------------------------------------------------

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean isFirst() throws SQLException {
        checkClosed();
        return rowNum == 1;
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getRow() throws SQLException {
        checkClosed();
        return rowNum;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkClosed();
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getType() throws SQLException {
        checkClosed();
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        checkClosed();
        return ResultSet.CONCUR_READ_ONLY;
    }

    // ---------------------------------------------------------------------
    // Updates
    // ---------------------------------------------------------------------

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Statement getStatement() throws SQLException {
        checkClosed();
        return statement;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    protected Blob getNewBlob(byte[] bytes) throws SQLException {
        if (bytes == null) {
            return null;
        }
        try {
            return new SerialBlob(bytes);
        } catch (SerialException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getNewBlob(getBytes(out));
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getNewBlob(getBytes(out));
    }

    protected Clob getClob(BsonValue o) throws SQLException {
        return new SerialClob(getString(o).toCharArray());
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getClob(out);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getClob(out);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    protected abstract java.util.Date handleUtilDateConversionFailure(String from)
            throws SQLException;

    protected abstract java.util.Date getUtilDate(BsonValue o) throws SQLException;

    protected Date getDate(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Date(utilDate.getTime());
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getDate(out);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getDate(out);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date d = getDate(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        Date d = getDate(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    protected Time getTime(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Time(utilDate.getTime());
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getTime(out);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getTime(out);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time d = getTime(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        Time d = getTime(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    }

    protected Timestamp getTimestamp(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Timestamp(utilDate.getTime());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getTimestamp(out);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getTimestamp(out);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp d = getTimestamp(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        Timestamp d = getTimestamp(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }

    // -------------------------- JDBC 3.0 ----------------------------------------

    @Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ------------------------- JDBC 4.0 -----------------------------------

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    @Override
    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        return new java.io.StringReader(getString(columnIndex));
    }

    @Override
    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        return new java.io.StringReader(getString(columnLabel));
    }

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ---

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ------------------------- JDBC 4.2 -----------------------------------

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
