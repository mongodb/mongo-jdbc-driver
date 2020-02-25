package com.mongodb.jdbc;

import com.mongodb.client.MongoCursor;
import java.io.InputStream;
import java.io.Reader;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

public class MongoResultSet implements ResultSet {
    private static final String UNKNOWN_BSON_TYPE = "unknown bson type";
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final char[] HEX_VALUES = "0123456789abcdef".toCharArray();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private int rowNum = 0;
    private boolean closed = false;
    private MongoCursor<Row> cursor;
    private Row current;
    private HashMap<String, Integer> columnPositionCache;
    private boolean wasNull;

    public MongoResultSet(MongoCursor<Row> cursor) {
        this.cursor = cursor;
    }

    public Row getCurrent() {
        return current;
    }

    public boolean next() throws SQLException {
        boolean result;
        result = cursor.hasNext();
        if (result) {
            current = cursor.next();
            ++rowNum;
        }
        return result;
    }

    public void close() throws SQLException {
        cursor.close();
        closed = true;
    }

    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    private void checkBounds(int i) throws SQLException {
        if (i > current.size()) {
            throw new SQLException("index out of bounds: '" + i + "'");
        }
    }

    private void checkKey(String key) throws SQLException {
        if (columnPositionCache == null) {
            buildColumnPositionCache();
        }
        if (current == null || !columnPositionCache.containsKey(key)) {
            throw new SQLException("no such column: '" + key + "'");
        }
    }

    private void buildColumnPositionCache() {
        if (current == null || current.size() == 0) {
            columnPositionCache = new HashMap<>();
            return;
        }
        columnPositionCache = new HashMap<>(current.size());
        int i = 0;
        for (Column c : current.values) {
            columnPositionCache.put(c.columnAlias, i++);
        }
    }

    // checkNull returns true if the Object o is null. Crucially,
    // it also sets the value of `wasNull`, since that is part
    // of the JDBC API.
    private boolean checkNull(Object o) {
        if (o == null) {
            wasNull = true;
            return true;
        }
        return false;
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private static String bytesToHex(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; ++j) {
            // & 0xFF appears pointless, but this is the only way
            // to make something unsigned in java.
            int v = bytes[j] & 0xFF;
            buf[j * 2] = HEX_VALUES[v >>> 4];
            buf[j * 2 + 1] = HEX_VALUES[v & 0x0F];
        }
        return new String(buf);
    }

    // Methods for accessing results
    private String throwStringConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to string");
    }

    // Everything here follows the conventions of $convert to string in mongodb
    // except for some special handling for binary.
    private String getString(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwStringConversionException("array");
            case BINARY:
                // Should we support any of this?
                //BsonBinary b = o.asBinary();
                //switch (b.getType()) {
                // case 0x3: Should we support this line?
                //    case 0x4:
                //    	return b.asUuid().toString();
                //}
                // return bytesToHex(b.getData())
                return throwStringConversionException("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? "true" : "false";
            case DATE_TIME:
                Date d = new Date(o.asDateTime().getValue());
                Calendar c = new GregorianCalendar();
                c.setTimeZone(UTC);
                c.setTime(d);
                StringBuilder sb = new StringBuilder(24);
                sb.append(c.get(Calendar.YEAR));
                sb.append("-");
                sb.append(c.get(Calendar.MONTH) + 1); //sigh
                sb.append("-");
                sb.append(c.get(Calendar.DAY_OF_MONTH));
                sb.append("T");
                sb.append(c.get(Calendar.HOUR_OF_DAY));
                sb.append(":");
                sb.append(c.get(Calendar.MINUTE));
                sb.append(":");
                sb.append(c.get(Calendar.SECOND));
                sb.append(".");
                sb.append(c.get(Calendar.MILLISECOND));
                sb.append("Z");
                return sb.toString();
            case DB_POINTER:
                return throwStringConversionException("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().toString();
            case DOCUMENT:
                return throwStringConversionException("document");
            case DOUBLE:
                return Double.toString(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return throwStringConversionException("end_of_document");
            case INT32:
                return Integer.toString(o.asInt32().getValue());
            case INT64:
                return Long.toString(o.asInt64().getValue());
            case JAVASCRIPT:
                return throwStringConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwStringConversionException("javascript_with_code");
            case MAX_KEY:
                return throwStringConversionException("max_key");
            case MIN_KEY:
                return throwStringConversionException("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                return o.asObjectId().getValue().toString();
            case REGULAR_EXPRESSION:
                return throwStringConversionException("regex");
            case STRING:
                return o.asString().getValue();
            case SYMBOL:
                return throwStringConversionException("symbol");
            case TIMESTAMP:
                return throwStringConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return throwStringConversionException(UNKNOWN_BSON_TYPE);
    }

    public String getString(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        return "";
        //        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        //        return getString(out);
    }

    public String getString(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getString(out);
    }

    private boolean throwBooleanConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to boolean");
    }

    private boolean getBoolean(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return false;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwBooleanConversionException("array");
            case BINARY:
                return throwBooleanConversionException("binary");
            case BOOLEAN:
                return o.asBoolean().getValue();
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue() != 0;
            case DB_POINTER:
                return throwBooleanConversionException("db_pointer");
            case DECIMAL128:
                {
                    Decimal128 v = o.asDecimal128().getValue();
                    return v != Decimal128.POSITIVE_ZERO && v != Decimal128.NEGATIVE_ZERO;
                }
            case DOCUMENT:
                return throwBooleanConversionException("document");
            case DOUBLE:
                return o.asBoolean().getValue();
            case END_OF_DOCUMENT:
                return throwBooleanConversionException("end_of_document");
            case INT32:
                return o.asInt32().getValue() != 0;
            case INT64:
                return o.asInt64().getValue() != 0;
            case JAVASCRIPT:
                return throwBooleanConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwBooleanConversionException("javascript_with_code");
            case MAX_KEY:
                return throwBooleanConversionException("max_key");
            case MIN_KEY:
                return throwBooleanConversionException("min_key");
            case NULL:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
            case OBJECT_ID:
                return throwBooleanConversionException("objectId");
            case REGULAR_EXPRESSION:
                return throwBooleanConversionException("regex");
            case STRING:
                // mongodb $convert converts all strings to true, even the empty string.
                return true;
            case SYMBOL:
                return throwBooleanConversionException("symbol");
            case TIMESTAMP:
                return throwBooleanConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
        }
        return throwBooleanConversionException(UNKNOWN_BSON_TYPE);
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBoolean(out);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getBoolean(out);
    }

    private byte getByte(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (byte) getLong(o);
    }

    public byte getByte(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getByte(out);
    }

    public byte getByte(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getByte(out);
    }

    private short getShort(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (short) getLong(o);
    }

    public short getShort(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getShort(out);
    }

    public short getShort(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getShort(out);
    }

    private int getInt(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0;
        }
        return (int) getLong(o);
    }

    public int getInt(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getInt(out);
    }

    public int getInt(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getInt(out);
    }

    private long throwLongConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to integral type");
    }

    private long getLong(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0L;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwLongConversionException("array");
            case BINARY:
                return throwLongConversionException("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1 : 0;
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue();
            case DB_POINTER:
                return throwLongConversionException("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().longValue();
            case DOCUMENT:
                return throwLongConversionException("document");
            case DOUBLE:
                return (long) o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return throwLongConversionException("end_of_document");
            case INT32:
                return (long) o.asInt32().getValue();
            case INT64:
                return o.asInt64().getValue();
            case JAVASCRIPT:
                return throwLongConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwLongConversionException("javascript_with_code");
            case MAX_KEY:
                return throwLongConversionException("max_key");
            case MIN_KEY:
                return throwLongConversionException("min_key");
            case NULL:
                return 0L;
            case OBJECT_ID:
                return throwLongConversionException("objectId");
            case REGULAR_EXPRESSION:
                return throwLongConversionException("regex");
            case STRING:
                return Long.parseLong(o.asString().getValue());
            case SYMBOL:
                return throwLongConversionException("symbol");
            case TIMESTAMP:
                return throwLongConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getLong
                // returns 0.0 for null values.
                return 0L;
        }
        return throwLongConversionException(UNKNOWN_BSON_TYPE);
    }

    public long getLong(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getLong(out);
    }

    public long getLong(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getLong(out);
    }

    private float getFloat(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (float) getDouble(o);
    }

    public float getFloat(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getFloat(out);
    }

    public float getFloat(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getFloat(out);
    }

    private double throwDoubleConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to double");
    }

    private double getDouble(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0.0;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwDoubleConversionException("array");
            case BINARY:
                return throwDoubleConversionException("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1.0 : 0.0;
            case DATE_TIME:
                // This is what $convert does.
                return (double) o.asDateTime().getValue();
            case DB_POINTER:
                return throwDoubleConversionException("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().doubleValue();
            case DOCUMENT:
                return throwDoubleConversionException("document");
            case DOUBLE:
                return o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return throwDoubleConversionException("end_of_document");
            case INT32:
                return (double) o.asInt32().getValue();
            case INT64:
                return (double) o.asInt64().getValue();
            case JAVASCRIPT:
                return throwDoubleConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwDoubleConversionException("javascript_with_code");
            case MAX_KEY:
                return throwDoubleConversionException("max_key");
            case MIN_KEY:
                return throwDoubleConversionException("min_key");
            case NULL:
                return 0.0;
            case OBJECT_ID:
                return throwDoubleConversionException("objectId");
            case REGULAR_EXPRESSION:
                return throwDoubleConversionException("regex");
            case STRING:
                return Double.parseDouble(o.asString().getValue());
            case SYMBOL:
                return throwDoubleConversionException("symbol");
            case TIMESTAMP:
                return throwDoubleConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getDouble
                // returns 0.0 for null values.
                return 0.0;
        }
        return throwDoubleConversionException(UNKNOWN_BSON_TYPE);
    }

    public double getDouble(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getDouble(out);
    }

    public double getDouble(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getDouble(out);
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
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
        return new MongoResultSetMetaData(current);
    }

    public Object getObject(int columnIndex) throws SQLException {
        return current.values.get(columnIndex - 1);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return current.values.get(columnPositionCache.get(columnLabel));
    }

    // ----------------------------------------------------------------

    public int findColumn(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // --------------------------JDBC 2.0-----------------------------------

    // ---------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------

    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private BigDecimal throwBigDecimalConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to BigDecimal");
    }

    private BigDecimal getBigDecimal(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return BigDecimal.ZERO;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwBigDecimalConversionException("array");
            case BINARY:
                return throwBigDecimalConversionException("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
            case DATE_TIME:
                // This is what $convert does.
                return new BigDecimal(o.asDateTime().getValue());
            case DB_POINTER:
                return throwBigDecimalConversionException("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case DOCUMENT:
                return throwBigDecimalConversionException("document");
            case DOUBLE:
                return new BigDecimal(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return throwBigDecimalConversionException("end_of_document");
            case INT32:
                return new BigDecimal(o.asInt32().getValue());
            case INT64:
                return new BigDecimal(o.asInt64().getValue());
            case JAVASCRIPT:
                return throwBigDecimalConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwBigDecimalConversionException("javascript_with_code");
            case MAX_KEY:
                return throwBigDecimalConversionException("max_key");
            case MIN_KEY:
                return throwBigDecimalConversionException("min_key");
            case NULL:
                return BigDecimal.ZERO;
            case OBJECT_ID:
                return throwBigDecimalConversionException("objectId");
            case REGULAR_EXPRESSION:
                return throwBigDecimalConversionException("regex");
            case STRING:
                return new BigDecimal(o.asString().getValue());
            case SYMBOL:
                return throwBigDecimalConversionException("symbol");
            case TIMESTAMP:
                return throwBigDecimalConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBigDecimal
                // returns 0.0 for null values.
                return BigDecimal.ZERO;
        }
        return throwBigDecimalConversionException(UNKNOWN_BSON_TYPE);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getBigDecimal(out);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBigDecimal(out);
    }

    // ---------------------------------------------------------------------
    // Traversal/Positioning
    // ---------------------------------------------------------------------

    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean isFirst() throws SQLException {
        return rowNum == 0;
    }

    public boolean isLast() throws SQLException {
        return !cursor.hasNext();
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
        return rowNum;
    }

    public boolean absolute(int row) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean relative(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
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
        return ResultSet.CONCUR_READ_ONLY;
    }

    // ---------------------------------------------------------------------
    // Updates
    // ---------------------------------------------------------------------

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

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
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

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
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

    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private Blob throwBlobConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to blob");
    }

    private Blob getBlob(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        // we only allow getting Strings and Binaries as Blobs so that
        // we can conveniently ignore Endianess issues. Null and undefined
        // are still supported because Blob's can be null.
        switch (o.getBsonType()) {
            case ARRAY:
                return throwBlobConversionException("array");
            case BINARY:
                return new SerialBlob(o.asBinary().getData());
            case BOOLEAN:
                return throwBlobConversionException("boolean");
            case DATE_TIME:
                return throwBlobConversionException("date");
            case DB_POINTER:
                return throwBlobConversionException("db_pointer");
            case DECIMAL128:
                return throwBlobConversionException("decimal128");
            case DOCUMENT:
                return throwBlobConversionException("document");
            case DOUBLE:
                return throwBlobConversionException("double");
            case END_OF_DOCUMENT:
                return throwBlobConversionException("end_of_document");
            case INT32:
                return throwBlobConversionException("int32");
            case INT64:
                return throwBlobConversionException("int64");
            case JAVASCRIPT:
                return throwBlobConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwBlobConversionException("javascript_with_code");
            case MAX_KEY:
                return throwBlobConversionException("max_key");
            case MIN_KEY:
                return throwBlobConversionException("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                return throwBlobConversionException("objectId");
            case REGULAR_EXPRESSION:
                return throwBlobConversionException("regex");
            case STRING:
                return new SerialBlob(o.asString().getValue().getBytes());
            case SYMBOL:
                return throwBlobConversionException("symbol");
            case TIMESTAMP:
                return throwBlobConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return throwBlobConversionException(UNKNOWN_BSON_TYPE);
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBlob(out);
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getBlob(out);
    }

    private Clob getClob(BsonValue o) throws SQLException {
        return new SerialClob(getString(o).toCharArray());
    }

    public Clob getClob(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getClob(out);
    }

    public Clob getClob(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getClob(out);
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private java.util.Date throwDatetimeConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to datetime");
    }

    private java.util.Date getUtilDate(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return throwDatetimeConversionException("array");
            case BINARY:
                return throwDatetimeConversionException("binary");
            case BOOLEAN:
                return throwDatetimeConversionException("boolean");
            case DATE_TIME:
                {
                    return new java.util.Date(o.asDateTime().getValue());
                }
            case DB_POINTER:
                return throwDatetimeConversionException("db_pointer");
            case DECIMAL128:
                return new Date(o.asDecimal128().longValue());
            case DOCUMENT:
                return throwDatetimeConversionException("document");
            case DOUBLE:
                return new Date((long) o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return throwDatetimeConversionException("end_of_document");
            case INT32:
                return new Date(o.asInt32().getValue());
            case INT64:
                return new Date(o.asInt64().getValue());
            case JAVASCRIPT:
                return throwDatetimeConversionException("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return throwDatetimeConversionException("javascript_with_code");
            case MAX_KEY:
                return throwDatetimeConversionException("max_key");
            case MIN_KEY:
                return throwDatetimeConversionException("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                // Date and java.util.Date are the same thing (long milliseconds since
                // epoch), but there's no way to convert directly.
                return o.asObjectId().getValue().getDate();
            case REGULAR_EXPRESSION:
                return throwDatetimeConversionException("regex");
            case STRING:
                try {
                    return dateFormat.parse(o.asString().getValue());
                } catch (ParseException e) {
                    throw new SQLException(e);
                }
            case SYMBOL:
                return throwDatetimeConversionException("symbol");
            case TIMESTAMP:
                return throwDatetimeConversionException("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return throwDatetimeConversionException(UNKNOWN_BSON_TYPE);
    }

    private Date getDate(BsonValue o) throws SQLException {
        return (o == null) ? null : new Date(getUtilDate(o).getTime());
    }

    public Date getDate(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getDate(out);
    }

    public Date getDate(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getDate(out);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date d = getDate(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        Date d = getDate(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    private Time throwTimeConversionException(String from) throws SQLException {
        throw new SQLException(from + " cannot be converted to time");
    }

    private Time getTime(BsonValue o) throws SQLException {
        return (o == null) ? null : new Time(getUtilDate(o).getTime());
    }

    public Time getTime(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getTime(out);
    }

    public Time getTime(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getTime(out);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time d = getTime(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        Time d = getTime(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    }

    private Timestamp getTimestamp(BsonValue o) throws SQLException {
        return (o == null) ? null : new Timestamp(getUtilDate(o).getTime());
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getTimestamp(out);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getTimestamp(out);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp d = getTimestamp(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        Timestamp d = getTimestamp(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }

    // -------------------------- JDBC 3.0 ----------------------------------------

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

    // ------------------------- JDBC 4.0 -----------------------------------

    public RowId getRowId(int columnIndex) throws SQLException {
        return new MongoRowId(columnIndex);
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        return new MongoRowId(columnPositionCache.get(columnLabel));
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
        return closed;
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
        return getString(columnIndex);
    }

    public String getNString(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        return new java.io.StringReader(getString(columnIndex));
    }

    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        return new java.io.StringReader(getString(columnLabel));
    }

    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ---

    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return type.cast(current.values.get(columnIndex - 1));
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        checkKey(columnLabel);
        return type.cast(current.values.get(columnPositionCache.get(columnLabel)));
    }

    // ------------------------- JDBC 4.2 -----------------------------------

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    // java.sql.Wrapper impl
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
