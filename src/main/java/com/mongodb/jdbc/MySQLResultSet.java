package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.text.ParseException;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

public class MySQLResultSet extends MongoResultSet<MySQLResultDoc> implements ResultSet {
    private boolean relaxed = true;

    public MySQLResultSet(
            MongoStatement statement, MongoCursor<MySQLResultDoc> cursor, boolean relaxed)
            throws SQLException {
        super(statement);
        Preconditions.checkNotNull(cursor);
        // iterate the cursor to get the metadata doc
        MySQLResultDoc metadataDoc = cursor.next();
        rsMetaData = new MySQLResultSetMetaData(metadataDoc);
        this.cursor = cursor;
        this.relaxed = relaxed;
    }

    // checkNull returns true if the Object o is null. Crucially,
    // it also sets the value of `wasNull`, since that is part
    // of the JDBC API.
    @Override
    protected boolean checkNull(BsonValue o) {
        // reset wasNull from previous check.
        wasNull = false;
        if (o == null) {
            wasNull = true;
            return true;
        }
        BsonType ty = o.getBsonType();
        // In strict mode, we only want to report undefined and null as null.
        if (!relaxed) {
            switch (ty) {
                case NULL:
                case UNDEFINED:
                    wasNull = true;
                    return true;
            }
            return false;
        }
        // in relaxed mode, we want to treat all of these types as null.
        switch (ty) {
            case ARRAY:
            case DB_POINTER:
            case DOCUMENT:
            case END_OF_DOCUMENT:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case NULL:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
            case UNDEFINED:
                wasNull = true;
                return true;
        }
        return false;
    }

    @Override
    protected BsonValue getBsonValue(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        return current.values.get(columnIndex - 1);
    }

    @Override
    protected BsonValue getBsonValue(String columnLabel) throws SQLException {
        return getBsonValue(findColumn(columnLabel));
    }

    @Override
    protected Object getObject(BsonValue o, int columnType) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (columnType) {
            case Types.ARRAY:
                // not supported
                break;
            case Types.BIGINT:
                return getInt(o);
            case Types.BINARY:
                // not supported
                break;
            case Types.BIT:
                return getBoolean(o);
            case Types.BLOB:
                // not supported
                break;
            case Types.BOOLEAN:
                return getBoolean(o);
            case Types.CHAR:
                // not supported
                break;
            case Types.CLOB:
                // not supported
                break;
            case Types.DATALINK:
                // not supported
                break;
            case Types.DATE:
                // not supported
                break;
            case Types.DECIMAL:
                return getBigDecimal(o);
            case Types.DISTINCT:
                // not supported
                break;
            case Types.DOUBLE:
                return getDouble(o);
            case Types.FLOAT:
                return getFloat(o);
            case Types.INTEGER:
                return getInt(o);
            case Types.JAVA_OBJECT:
                // not supported
                break;
            case Types.LONGNVARCHAR:
                return getString(o);
            case Types.LONGVARBINARY:
                // not supported
                break;
            case Types.LONGVARCHAR:
                return getString(o);
            case Types.NCHAR:
                return getString(o);
            case Types.NCLOB:
                // not supported
                break;
            case Types.NULL:
                return null;
            case Types.NUMERIC:
                return getDouble(o);
            case Types.NVARCHAR:
                return getString(o);
            case Types.OTHER:
                // not supported
                break;
            case Types.REAL:
                // not supported
                break;
            case Types.REF:
                // not supported
                break;
            case Types.REF_CURSOR:
                // not supported
                break;
            case Types.ROWID:
                // not supported
                break;
            case Types.SMALLINT:
                return getInt(o);
            case Types.SQLXML:
                // not supported
                break;
            case Types.STRUCT:
                // not supported
                break;
            case Types.TIME:
                // not supported
                break;
            case Types.TIME_WITH_TIMEZONE:
                // not supported
                break;
            case Types.TIMESTAMP:
                return getTimestamp(o);
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // not supported
                break;
            case Types.TINYINT:
                return getInt(o);
            case Types.VARBINARY:
                // not supported
                break;
            case Types.VARCHAR:
                return getString(o);
        }
        throw new SQLException("getObject not supported for column type " + columnType);
    }

    @Override
    protected byte[] handleBytesConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException("The " + from + " type cannot be converted to blob.");
    }

    @Override
    protected byte[] getBytes(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        // we only allow getting Strings and Binaries as Bytes so that
        // we can conveniently ignore Endianess issues. Null and undefined
        // are still supported because Bytes's can be null.
        switch (o.getBsonType()) {
            case ARRAY:
                return handleBytesConversionFailure(ARRAY);
            case BINARY:
                return o.asBinary().getData();
            case BOOLEAN:
                return handleBytesConversionFailure(BOOLEAN);
            case DATE_TIME:
                return handleBytesConversionFailure(DATE);
            case DB_POINTER:
                return handleBytesConversionFailure(DB_POINTER);
            case DECIMAL128:
                return handleBytesConversionFailure(DECIMAL128);
            case DOCUMENT:
                return handleBytesConversionFailure(DOCUMENT);
            case DOUBLE:
                return handleBytesConversionFailure(DOUBLE);
            case END_OF_DOCUMENT:
                return handleBytesConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return handleBytesConversionFailure(INT32);
            case INT64:
                return handleBytesConversionFailure(INT64);
            case JAVASCRIPT:
                return handleBytesConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleBytesConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleBytesConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleBytesConversionFailure(MIN_KEY);
            case NULL:
                return null;
            case OBJECT_ID:
                return handleBytesConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleBytesConversionFailure(REGEX);
            case STRING:
                return handleBytesConversionFailure(STRING);
            case SYMBOL:
                return handleBytesConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleBytesConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    // Methods for accessing results
    @Override
    protected String handleStringConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException("The " + from + " type cannot be converted to string.");
    }

    // Everything here follows the conventions of $convert to string in mongodb
    // except for some special handling for binary.
    protected String getString(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleStringConversionFailure(ARRAY);
            case BINARY:
                return handleStringConversionFailure(BINARY);
            case BOOLEAN:
                return o.asBoolean().getValue() ? "true" : "false";
            case DATE_TIME:
                Date d = new Date(o.asDateTime().getValue());
                return dateFormat.format(d);
            case DB_POINTER:
                return handleStringConversionFailure(DB_POINTER);
            case DECIMAL128:
                return o.asDecimal128().getValue().toString();
            case DOCUMENT:
                return handleStringConversionFailure(DOCUMENT);
            case DOUBLE:
                return Double.toString(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleStringConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return Integer.toString(o.asInt32().getValue());
            case INT64:
                return Long.toString(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleStringConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleStringConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleStringConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleStringConversionFailure(MIN_KEY);
            case NULL:
                return null;
            case OBJECT_ID:
                return o.asObjectId().getValue().toString();
            case REGULAR_EXPRESSION:
                return handleStringConversionFailure(REGEX);
            case STRING:
                return o.asString().getValue();
            case SYMBOL:
                return handleStringConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleStringConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    protected boolean handleBooleanConversionFailure(String from) throws SQLException {
        if (relaxed) return false;
        throw new SQLException("The " + from + " type cannot be converted to boolean.");
    }

    @Override
    protected boolean getBoolean(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return false;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleBooleanConversionFailure(ARRAY);
            case BINARY:
                return handleBooleanConversionFailure(BINARY);
            case BOOLEAN:
                return o.asBoolean().getValue();
            case DATE_TIME:
                return handleBooleanConversionFailure(DATE);
            case DB_POINTER:
                return handleBooleanConversionFailure(DB_POINTER);
            case DECIMAL128:
                {
                    Decimal128 v = o.asDecimal128().getValue();
                    return v != Decimal128.POSITIVE_ZERO && v != Decimal128.NEGATIVE_ZERO;
                }
            case DOCUMENT:
                return handleBooleanConversionFailure(DOCUMENT);
            case DOUBLE:
                return o.asDouble().getValue() != 0.0;
            case END_OF_DOCUMENT:
                return handleBooleanConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return o.asInt32().getValue() != 0;
            case INT64:
                return o.asInt64().getValue() != 0;
            case JAVASCRIPT:
                return handleBooleanConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleBooleanConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleBooleanConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleBooleanConversionFailure(MIN_KEY);
            case NULL:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
            case OBJECT_ID:
                return handleBooleanConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleBooleanConversionFailure(REGEX);
            case STRING:
                // mongodb $convert converts all strings to true, even the empty string.
                return true;
            case SYMBOL:
                return handleBooleanConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleBooleanConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    protected long handleLongConversionFailure(String from) throws SQLException {
        if (relaxed) return 0L;
        throw new SQLException("The " + from + " type cannot be converted to integral type.");
    }

    @Override
    protected long getLong(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0L;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleLongConversionFailure(ARRAY);
            case BINARY:
                return handleLongConversionFailure(BINARY);
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1 : 0;
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue();
            case DB_POINTER:
                return handleLongConversionFailure(DB_POINTER);
            case DECIMAL128:
                return o.asDecimal128().longValue();
            case DOCUMENT:
                return handleLongConversionFailure(DOCUMENT);
            case DOUBLE:
                return (long) o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return handleLongConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return (long) o.asInt32().getValue();
            case INT64:
                return o.asInt64().getValue();
            case JAVASCRIPT:
                return handleLongConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleLongConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleLongConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleLongConversionFailure(MIN_KEY);
            case NULL:
                return 0L;
            case OBJECT_ID:
                return handleLongConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleLongConversionFailure(REGEX);
            case STRING:
                try {
                    return Long.parseLong(o.asString().getValue());
                } catch (NumberFormatException e) {
                    if (relaxed) return 0;
                    throw new SQLException(e);
                }
            case SYMBOL:
                return handleLongConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleLongConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getLong
                // returns 0.0 for null values.
                return 0L;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    protected double handleDoubleConversionFailure(String from) throws SQLException {
        if (relaxed) return 0.0;
        throw new SQLException("The " + from + " type cannot be converted to double.");
    }

    @Override
    protected double getDouble(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0.0;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleDoubleConversionFailure(ARRAY);
            case BINARY:
                return handleDoubleConversionFailure(BINARY);
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1.0 : 0.0;
            case DATE_TIME:
                // This is what $convert does.
                return (double) o.asDateTime().getValue();
            case DB_POINTER:
                return handleDoubleConversionFailure(DB_POINTER);
            case DECIMAL128:
                return o.asDecimal128().doubleValue();
            case DOCUMENT:
                return handleDoubleConversionFailure(DOCUMENT);
            case DOUBLE:
                return o.asDouble().getValue();
            case END_OF_DOCUMENT:
                return handleDoubleConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return (double) o.asInt32().getValue();
            case INT64:
                return (double) o.asInt64().getValue();
            case JAVASCRIPT:
                return handleDoubleConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleDoubleConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleDoubleConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleDoubleConversionFailure(MIN_KEY);
            case NULL:
                return 0.0;
            case OBJECT_ID:
                return handleDoubleConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleDoubleConversionFailure(REGEX);
            case STRING:
                try {
                    return Double.parseDouble(o.asString().getValue());
                } catch (NumberFormatException e) {
                    if (relaxed) return 0.0;
                    throw new SQLException(e);
                }
            case SYMBOL:
                return handleDoubleConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleDoubleConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getDouble
                // returns 0.0 for null values.
                return 0.0;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    protected BigDecimal handleBigDecimalConversionFailure(String from) throws SQLException {
        if (relaxed) return BigDecimal.ZERO;
        throw new SQLException("The " + from + " type cannot be converted to BigDecimal.");
    }

    @Override
    protected BigDecimal getBigDecimal(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return BigDecimal.ZERO;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleBigDecimalConversionFailure(ARRAY);
            case BINARY:
                return handleBigDecimalConversionFailure(BINARY);
            case BOOLEAN:
                return o.asBoolean().getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
            case DATE_TIME:
                // This is what $convert does.
                return new BigDecimal(o.asDateTime().getValue());
            case DB_POINTER:
                return handleBigDecimalConversionFailure(DB_POINTER);
            case DECIMAL128:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case DOCUMENT:
                return handleBigDecimalConversionFailure(DOCUMENT);
            case DOUBLE:
                return new BigDecimal(o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleBigDecimalConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return new BigDecimal(o.asInt32().getValue());
            case INT64:
                return new BigDecimal(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleBigDecimalConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleBigDecimalConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleBigDecimalConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleBigDecimalConversionFailure(MIN_KEY);
            case NULL:
                return BigDecimal.ZERO;
            case OBJECT_ID:
                return handleBigDecimalConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleBigDecimalConversionFailure(REGEX);
            case STRING:
                try {
                    return new BigDecimal(o.asString().getValue());
                } catch (NumberFormatException | ArithmeticException e) {
                    if (relaxed) return BigDecimal.ZERO;
                    throw new SQLException(e);
                }
            case SYMBOL:
                return handleBigDecimalConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleBigDecimalConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBigDecimal
                // returns 0.0 for null values.
                return BigDecimal.ZERO;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    protected java.util.Date handleUtilDateConversionFailure(String from) throws SQLException {
        if (relaxed) return null;
        throw new SQLException("The " + from + " type cannot be converted to java.util.Date");
    }

    @Override
    protected java.util.Date getUtilDate(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return handleUtilDateConversionFailure(ARRAY);
            case BINARY:
                return handleUtilDateConversionFailure(BINARY);
            case BOOLEAN:
                return handleUtilDateConversionFailure(BOOLEAN);
            case DATE_TIME:
                return new java.util.Date(o.asDateTime().getValue());
            case DB_POINTER:
                return handleUtilDateConversionFailure(DB_POINTER);
            case DECIMAL128:
                return new Date(o.asDecimal128().longValue());
            case DOCUMENT:
                return handleUtilDateConversionFailure(DOCUMENT);
            case DOUBLE:
                return new Date((long) o.asDouble().getValue());
            case END_OF_DOCUMENT:
                return handleUtilDateConversionFailure(END_OF_DOCUMENT);
            case INT32:
                return new Date(o.asInt32().getValue());
            case INT64:
                return new Date(o.asInt64().getValue());
            case JAVASCRIPT:
                return handleUtilDateConversionFailure(JAVASCRIPT);
            case JAVASCRIPT_WITH_SCOPE:
                return handleUtilDateConversionFailure(JAVASCRIPT_WITH_CODE);
            case MAX_KEY:
                return handleUtilDateConversionFailure(MAX_KEY);
            case MIN_KEY:
                return handleUtilDateConversionFailure(MIN_KEY);
            case NULL:
                return null;
            case OBJECT_ID:
                return handleUtilDateConversionFailure(OBJECT_ID);
            case REGULAR_EXPRESSION:
                return handleUtilDateConversionFailure(REGEX);
            case STRING:
                try {
                    return dateFormat.parse(o.asString().getValue());
                } catch (ParseException e) {
                    if (relaxed) return null;
                    throw new SQLException(e);
                }
            case SYMBOL:
                return handleUtilDateConversionFailure(SYMBOL);
            case TIMESTAMP:
                return handleUtilDateConversionFailure(TIMESTAMP);
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
        }
        throw new SQLException("Unknown BSON type: " + o.getBsonType() + ".");
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        // If the value is Null, no need to try to convert to a Java Object
        if (checkNull(out)) {
            return null;
        }
        int columnType = rsMetaData.getColumnType(columnIndex);
        return getObject(out, columnType);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        int columnIndex = findColumn(columnLabel);
        return getObject(columnIndex);
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

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
}
