package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCursor;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import org.bson.BsonDocument;
import org.bson.BsonMaxKey;
import org.bson.BsonMinKey;
import org.bson.BsonRegularExpression;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

@AutoLoggable
public class MongoSQLResultSet extends MongoResultSet<BsonDocument> implements ResultSet {

    /**
     * Constructor for a MongoSQLResultSet not tied to a statement used for
     * MongoSQLDatabaseMetaData.
     *
     * @param parentLogger The parent connection logger.
     * @param cursor The resultset cursor.
     * @param schema The resultset schema.
     * @throws SQLException
     */
    public MongoSQLResultSet(
            MongoLogger parentLogger, MongoCursor<BsonDocument> cursor, MongoJsonSchema schema)
            throws SQLException {
        super(parentLogger);
        setUpResultset(cursor, schema);
        this.rsMetaData = new MongoSQLResultSetMetaData(schema, false, parentLogger, null);
    }

    /**
     * Constructor for a MongoSQLResultset tied to a connection and statement.
     *
     * @param statement The statement this resultset is related to.
     * @param cursor The resultset cursor.
     * @param schema The resultset schema.
     * @throws SQLException
     */
    public MongoSQLResultSet(
            MongoStatement statement, MongoCursor<BsonDocument> cursor, MongoJsonSchema schema)
            throws SQLException {
        super(statement);
        setUpResultset(cursor, schema);
        this.rsMetaData =
                new MongoSQLResultSetMetaData(
                        schema, true, statement.getParentLogger(), statement.getStatementId());
    }

    private void setUpResultset(MongoCursor<BsonDocument> cursor, MongoJsonSchema schema)
            throws SQLException {
        Preconditions.checkNotNull(cursor);

        // Only sort the columns alphabetically for SQL statement result sets and not for database metadata result sets.
        // The JDBC specification provides the order for each database metadata result set.
        // Because a lot BI tools will access database metadata columns by index, the specification order must be respected.
        this.cursor = cursor;
    }

    // This is only used for testing, and that is why it has package level access, and the
    // tests have been moved into this package.
    BsonDocument getCurrent() {
        return current;
    }

    @Override
    protected boolean checkNull(BsonValue o) {
        // reset wasNull from previous check.
        wasNull = false;
        if (o == null) {
            wasNull = true;
            return true;
        }
        BsonType ty = o.getBsonType();
        switch (ty) {
            case NULL:
            case UNDEFINED:
                wasNull = true;
                return true;
        }
        return false;
    }

    @Override
    protected BsonValue getBsonValue(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        MongoColumnInfo columnInfo = rsMetaData.getColumnInfo(columnIndex);
        BsonDocument datasource = this.current.get(columnInfo.getTableName()).asDocument();
        return datasource.get(columnInfo.getColumnName());
    }

    @Override
    protected BsonValue getBsonValue(String columnLabel) throws SQLException {
        int columnIndex;
        if (rsMetaData.hasColumnWithLabel(columnLabel)) {
            columnIndex = rsMetaData.getColumnPositionFromLabel(columnLabel);
        } else {

            throw new SQLException(String.format("column label '%s' not found", columnLabel));
        }
        return getBsonValue(columnIndex + 1);
    }

    @Override
    protected Object getObject(BsonValue o, int columnType) throws SQLException {
        // If the value is an SQL NULL, the driver returns a Java null.
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
                return o.asBinary();
            case Types.BIT:
                return getBoolean(o);
            case Types.BLOB:
                // not supported
                break;
            case Types.BOOLEAN:
                return getBoolean(o);
            case Types.CHAR:
                return getString(o);
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
                switch (o.getBsonType()) {
                    case ARRAY:
                        return o.asArray();
                    case DOCUMENT:
                        return o.asDocument();
                    case OBJECT_ID:
                        return o.asObjectId();
                    case DB_POINTER:
                        return o.asDBPointer();
                    case INT32:
                        return o.asInt32();
                    case INT64:
                        return o.asInt64();
                    case JAVASCRIPT:
                        return o.asJavaScript();
                    case JAVASCRIPT_WITH_SCOPE:
                        return o.asJavaScriptWithScope();
                    case MAX_KEY:
                        return (BsonMaxKey) o;
                    case MIN_KEY:
                        return (BsonMinKey) o;
                    case REGULAR_EXPRESSION:
                        return (BsonRegularExpression) o;
                    case SYMBOL:
                        return o.asSymbol();
                    case TIMESTAMP:
                        return o.asTimestamp();
                    case UNDEFINED:
                        return (BsonUndefined) o;
                    case NULL:
                        return null;
                    default:
                        return o;
                }
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
    public Object getObject(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
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
        BsonValue out = getBsonValue(columnIndex);
        if (checkNull(out)) {
            return null;
        }
        String columnTypeName = rsMetaData.getColumnTypeName(columnIndex);
        Class<?> type = map.get(columnTypeName);
        if (type == null) {
            return null;
        }
        return type.cast(out);
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        if (checkNull(out)) {
            return null;
        }
        return type.cast(out);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    @Override
    protected byte[] handleBytesConversionFailure(String from) throws SQLException {
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

    @Override
    protected String handleStringConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to string.");
    }

    @Override
    protected String getString(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        switch (o.getBsonType()) {
            case ARRAY:
                return o.asArray().getValues().toString();
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
                return o.asDocument().toString();
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
}
