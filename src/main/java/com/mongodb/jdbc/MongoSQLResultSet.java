/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCursor;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import org.bson.BsonDocument;
import org.bson.BsonType;
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
            case Types.BIGINT:
                return getLong(o);
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return getInt(o);
            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
                return o.asBinary().getData();
            case Types.BIT:
            case Types.BOOLEAN:
                return getBoolean(o);
            case Types.DOUBLE:
            case Types.FLOAT:
                return getDouble(o);
            case Types.DECIMAL:
            case Types.NUMERIC:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case Types.CHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.VARCHAR:
                return getString(o);
            case Types.REAL:
                return getFloat(o);
            case Types.TIMESTAMP:
                return new Timestamp(o.asDateTime().getValue());
            case Types.NULL:
                return null;

            case Types.OTHER:
                if (o.getBsonType() == BsonType.NULL) {
                    return null;
                }
                // These types are wrapped in MongoSQLBsonValue so that
                // if they are stringified via toString() they will be
                // represented as extended JSON.
                return new MongoSQLBsonValue(o);

            case Types.ARRAY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.DATALINK:
            case Types.DATE:
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
            case Types.NCLOB:
            case Types.REF:
            case Types.REF_CURSOR:
            case Types.ROWID:
            case Types.SQLXML:
            case Types.STRUCT:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // not supported
                break;
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
        return new MongoSQLBsonValue(o).toString();
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
