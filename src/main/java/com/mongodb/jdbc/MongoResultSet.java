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
    private boolean relaxed = true;

    public MongoResultSet(MongoCursor<Row> cursor, boolean relaxed) {
        this.cursor = cursor;
        this.relaxed = relaxed;
    }

    // This is only used for testing, and that is why is has package level access, and the
    // tests have been moved into this package.
    Row getCurrent() {
        return current;
    }

    @Override
    public boolean next() throws SQLException {
        boolean result;
        result = cursor.hasNext();
        if (result) {
            current = cursor.next();
            ++rowNum;
        }
        return result;
    }

    @Override
    public void close() throws SQLException {
        cursor.close();
        closed = true;
    }

    @Override
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
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
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
    private String handleStringConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException(from + " cannot be converted to string");
    }

    private String zeroPad(int datum, int len) {
        String datStr = String.valueOf(datum);
        int padFactor = len - datStr.length();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < padFactor; ++i) {
            ret.append("0");
        }
        ret.append(datStr);
        return ret.toString();
    }

    // Everything here follows the conventions of $convert to string in mongodb
    // except for some special handling for binary.
    private String getString(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleStringConversionFailure("array");
            case BINARY:
                // Should we support any of this?
                //BsonBinary b = o.asBinary();
                //switch (b.getType()) {
                // case 0x3: Should we support this line?
                //    case 0x4:
                //    	return b.asUuid().toString();
                //}
                // return bytesToHex(b.getData())
                return handleStringConversionFailure("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? "true" : "false";
            case DATE_TIME:
                Date d = new Date(o.asDateTime().getValue());
                Calendar c = new GregorianCalendar();
                c.setTimeZone(UTC);
                c.setTime(d);
                StringBuilder sb = new StringBuilder(24);
                int datum = c.get(Calendar.YEAR);
                sb.append(zeroPad(datum, 4));
                sb.append("-");
                datum = c.get(Calendar.MONTH) + 1; //sigh
                sb.append(zeroPad(datum, 2));
                sb.append("-");
                datum = c.get(Calendar.DAY_OF_MONTH);
                sb.append(zeroPad(datum, 2));
                sb.append("T");
                datum = c.get(Calendar.HOUR_OF_DAY);
                sb.append(zeroPad(datum, 2));
                sb.append(":");
                datum = c.get(Calendar.MINUTE);
                sb.append(zeroPad(datum, 2));
                sb.append(":");
                datum = c.get(Calendar.SECOND);
                sb.append(zeroPad(datum, 2));
                sb.append(".");
                datum = c.get(Calendar.MILLISECOND);
                sb.append(zeroPad(datum, 2));
                sb.append("Z");
                return sb.toString();
            case DB_POINTER:
                return handleStringConversionFailure("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().getValue().toString();
            case DOCUMENT:
                return handleStringConversionFailure("document");
            case DOUBLE:
                return Double.toString(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleStringConversionFailure("end_of_document");
            case INT32:
                return Integer.toString(o.asInt32().getValue());
            case INT64:
                return Long.toString(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleStringConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleStringConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleStringConversionFailure("max_key");
            case MIN_KEY:
                return handleStringConversionFailure("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                return o.asObjectId().getValue().toString();
            case REGULAR_EXPRESSION:
                return handleStringConversionFailure("regex");
            case STRING:
                return o.asString().getValue();
            case SYMBOL:
                return handleStringConversionFailure("symbol");
            case TIMESTAMP:
                return handleStringConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return handleStringConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getString(out);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getString(out);
    }

    private boolean handleBooleanConversionFailure(String from) throws SQLException {
        if (relaxed) return false;
        throw new SQLException(from + " cannot be converted to boolean");
    }

    private boolean getBoolean(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return false;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleBooleanConversionFailure("array");
            case BINARY:
                return handleBooleanConversionFailure("binary");
            case BOOLEAN:
                return o.asBoolean().getValue();
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue() != 0;
            case DB_POINTER:
                return handleBooleanConversionFailure("db_pointer");
            case DECIMAL128:
                {
                    Decimal128 v = o.asDecimal128().getValue();
                    return v != Decimal128.POSITIVE_ZERO && v != Decimal128.NEGATIVE_ZERO;
                }
            case DOCUMENT:
                return handleBooleanConversionFailure("document");
            case DOUBLE:
                return o.asDouble().getValue() != 0.0;
            case END_OF_DOCUMENT:
                return handleBooleanConversionFailure("end_of_document");
            case INT32:
                return o.asInt32().getValue() != 0;
            case INT64:
                return o.asInt64().getValue() != 0;
            case JAVASCRIPT:
                return handleBooleanConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleBooleanConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleBooleanConversionFailure("max_key");
            case MIN_KEY:
                return handleBooleanConversionFailure("min_key");
            case NULL:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
            case OBJECT_ID:
                return handleBooleanConversionFailure("objectId");
            case REGULAR_EXPRESSION:
                return handleBooleanConversionFailure("regex");
            case STRING:
                // mongodb $convert converts all strings to true, even the empty string.
                return true;
            case SYMBOL:
                return handleBooleanConversionFailure("symbol");
            case TIMESTAMP:
                return handleBooleanConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
        }
        return handleBooleanConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBoolean(out);
    }

    @Override
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

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getByte(out);
    }

    @Override
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

    @Override
    public short getShort(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getShort(out);
    }

    @Override
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

    @Override
    public int getInt(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getInt(out);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getInt(out);
    }

    private long handleLongConversionFailure(String from) throws SQLException {
        if (relaxed) return 0L;
        throw new SQLException(from + " cannot be converted to integral type");
    }

    private long getLong(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0L;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleLongConversionFailure("array");
            case BINARY:
                return handleLongConversionFailure("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1 : 0;
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue();
            case DB_POINTER:
                return handleLongConversionFailure("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().longValue();
            case DOCUMENT:
                return handleLongConversionFailure("document");
            case DOUBLE:
                return (long) o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return handleLongConversionFailure("end_of_document");
            case INT32:
                return (long) o.asInt32().getValue();
            case INT64:
                return o.asInt64().getValue();
            case JAVASCRIPT:
                return handleLongConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleLongConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleLongConversionFailure("max_key");
            case MIN_KEY:
                return handleLongConversionFailure("min_key");
            case NULL:
                return 0L;
            case OBJECT_ID:
                return handleLongConversionFailure("objectId");
            case REGULAR_EXPRESSION:
                return handleLongConversionFailure("regex");
            case STRING:
                return Long.parseLong(o.asString().getValue());
            case SYMBOL:
                return handleLongConversionFailure("symbol");
            case TIMESTAMP:
                return handleLongConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getLong
                // returns 0.0 for null values.
                return 0L;
        }
        return handleLongConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getLong(out);
    }

    @Override
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

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getFloat(out);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getFloat(out);
    }

    private double handleDoubleConversionFailure(String from) throws SQLException {
        if (relaxed) return 0.0;
        throw new SQLException(from + " cannot be converted to double");
    }

    private double getDouble(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0.0;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleDoubleConversionFailure("array");
            case BINARY:
                return handleDoubleConversionFailure("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1.0 : 0.0;
            case DATE_TIME:
                // This is what $convert does.
                return (double) o.asDateTime().getValue();
            case DB_POINTER:
                return handleDoubleConversionFailure("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().doubleValue();
            case DOCUMENT:
                return handleDoubleConversionFailure("document");
            case DOUBLE:
                return o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return handleDoubleConversionFailure("end_of_document");
            case INT32:
                return (double) o.asInt32().getValue();
            case INT64:
                return (double) o.asInt64().getValue();
            case JAVASCRIPT:
                return handleDoubleConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleDoubleConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleDoubleConversionFailure("max_key");
            case MIN_KEY:
                return handleDoubleConversionFailure("min_key");
            case NULL:
                return 0.0;
            case OBJECT_ID:
                return handleDoubleConversionFailure("objectId");
            case REGULAR_EXPRESSION:
                return handleDoubleConversionFailure("regex");
            case STRING:
                return Double.parseDouble(o.asString().getValue());
            case SYMBOL:
                return handleDoubleConversionFailure("symbol");
            case TIMESTAMP:
                return handleDoubleConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getDouble
                // returns 0.0 for null values.
                return 0.0;
        }
        return handleDoubleConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getDouble(out);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getDouble(out);
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // Advanced features:

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MongoResultSetMetaData(current);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return current.values.get(columnIndex - 1);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return current.values.get(columnPositionCache.get(columnLabel));
    }

    // ----------------------------------------------------------------

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // --------------------------JDBC 2.0-----------------------------------

    // ---------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private BigDecimal handleBigDecimalConversionFailure(String from) throws SQLException {
        if (relaxed) return BigDecimal.ZERO;
        throw new SQLException(from + " cannot be converted to BigDecimal");
    }

    private BigDecimal getBigDecimal(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return BigDecimal.ZERO;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleBigDecimalConversionFailure("array");
            case BINARY:
                return handleBigDecimalConversionFailure("binary");
            case BOOLEAN:
                return o.asBoolean().getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
            case DATE_TIME:
                // This is what $convert does.
                return new BigDecimal(o.asDateTime().getValue());
            case DB_POINTER:
                return handleBigDecimalConversionFailure("db_pointer");
            case DECIMAL128:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case DOCUMENT:
                return handleBigDecimalConversionFailure("document");
            case DOUBLE:
                return new BigDecimal(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleBigDecimalConversionFailure("end_of_document");
            case INT32:
                return new BigDecimal(o.asInt32().getValue());
            case INT64:
                return new BigDecimal(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleBigDecimalConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleBigDecimalConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleBigDecimalConversionFailure("max_key");
            case MIN_KEY:
                return handleBigDecimalConversionFailure("min_key");
            case NULL:
                return BigDecimal.ZERO;
            case OBJECT_ID:
                return handleBigDecimalConversionFailure("objectId");
            case REGULAR_EXPRESSION:
                return handleBigDecimalConversionFailure("regex");
            case STRING:
                return new BigDecimal(o.asString().getValue());
            case SYMBOL:
                return handleBigDecimalConversionFailure("symbol");
            case TIMESTAMP:
                return handleBigDecimalConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBigDecimal
                // returns 0.0 for null values.
                return BigDecimal.ZERO;
        }
        return handleBigDecimalConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getBigDecimal(out);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBigDecimal(out);
    }

    // ---------------------------------------------------------------------
    // Traversal/Positioning
    // ---------------------------------------------------------------------

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean isFirst() throws SQLException {
        return rowNum == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        return !cursor.hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public int getRow() throws SQLException {
        return rowNum;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    // ---------------------------------------------------------------------
    // Updates
    // ---------------------------------------------------------------------

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private Blob handleBlobConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
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
                return handleBlobConversionFailure("array");
            case BINARY:
                return new SerialBlob(o.asBinary().getData());
            case BOOLEAN:
                return handleBlobConversionFailure("boolean");
            case DATE_TIME:
                return handleBlobConversionFailure("date");
            case DB_POINTER:
                return handleBlobConversionFailure("db_pointer");
            case DECIMAL128:
                return handleBlobConversionFailure("decimal128");
            case DOCUMENT:
                return handleBlobConversionFailure("document");
            case DOUBLE:
                return handleBlobConversionFailure("double");
            case END_OF_DOCUMENT:
                return handleBlobConversionFailure("end_of_document");
            case INT32:
                return handleBlobConversionFailure("int32");
            case INT64:
                return handleBlobConversionFailure("int64");
            case JAVASCRIPT:
                return handleBlobConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleBlobConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleBlobConversionFailure("max_key");
            case MIN_KEY:
                return handleBlobConversionFailure("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                return new SerialBlob(o.asObjectId().getValue().toString().getBytes());
            case REGULAR_EXPRESSION:
                return handleBlobConversionFailure("regex");
            case STRING:
                return new SerialBlob(o.asString().getValue().getBytes());
            case SYMBOL:
                return handleBlobConversionFailure("symbol");
            case TIMESTAMP:
                return handleBlobConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return handleBlobConversionFailure(UNKNOWN_BSON_TYPE);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getBlob(out);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getBlob(out);
    }

    private Clob getClob(BsonValue o) throws SQLException {
        return new SerialClob(getString(o).toCharArray());
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getClob(out);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
        return getClob(out);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    private java.util.Date handleDatetimeConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException(from + " cannot be converted to datetime");
    }

    private java.util.Date getUtilDate(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleDatetimeConversionFailure("array");
            case BINARY:
                return handleDatetimeConversionFailure("binary");
            case BOOLEAN:
                return handleDatetimeConversionFailure("boolean");
            case DATE_TIME:
                {
                    return new java.util.Date(o.asDateTime().getValue());
                }
            case DB_POINTER:
                return handleDatetimeConversionFailure("db_pointer");
            case DECIMAL128:
                return new Date(o.asDecimal128().longValue());
            case DOCUMENT:
                return handleDatetimeConversionFailure("document");
            case DOUBLE:
                return new Date((long) o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleDatetimeConversionFailure("end_of_document");
            case INT32:
                return new Date(o.asInt32().getValue());
            case INT64:
                return new Date(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleDatetimeConversionFailure("javascript");
            case JAVASCRIPT_WITH_SCOPE:
                return handleDatetimeConversionFailure("javascript_with_code");
            case MAX_KEY:
                return handleDatetimeConversionFailure("max_key");
            case MIN_KEY:
                return handleDatetimeConversionFailure("min_key");
            case NULL:
                return null;
            case OBJECT_ID:
                // Date and java.util.Date are the same thing (long milliseconds since
                // epoch), but there's no way to convert directly.
                return o.asObjectId().getValue().getDate();
            case REGULAR_EXPRESSION:
                return handleDatetimeConversionFailure("regex");
            case STRING:
                try {
                    return dateFormat.parse(o.asString().getValue());
                } catch (ParseException e) {
                    throw new SQLException(e);
                }
            case SYMBOL:
                return handleDatetimeConversionFailure("symbol");
            case TIMESTAMP:
                return handleDatetimeConversionFailure("timestamp");
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        return handleDatetimeConversionFailure(UNKNOWN_BSON_TYPE);
    }

    private Date getDate(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Date(utilDate.getTime());
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getDate(out);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
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

    private Time handleTimeConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException(from + " cannot be converted to time");
    }

    private Time getTime(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Time(utilDate.getTime());
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getTime(out);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
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

    private Timestamp getTimestamp(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Timestamp(utilDate.getTime());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        BsonValue out = current.values.get(columnPositionCache.get(columnLabel)).value;
        return getTimestamp(out);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        BsonValue out = current.values.get(columnIndex - 1).value;
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
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ------------------------- JDBC 4.0 -----------------------------------

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return new MongoRowId(columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        checkKey(columnLabel);
        return new MongoRowId(columnPositionCache.get(columnLabel));
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
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
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ---

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return type.cast(current.values.get(columnIndex - 1));
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        checkKey(columnLabel);
        return type.cast(current.values.get(columnPositionCache.get(columnLabel)));
    }

    // ------------------------- JDBC 4.2 -----------------------------------

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject not implemented");
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
