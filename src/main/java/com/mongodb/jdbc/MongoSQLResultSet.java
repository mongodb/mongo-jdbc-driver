package com.mongodb.jdbc;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCursor;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class MongoSQLResultSet extends MongoResultSet<BsonDocument> implements ResultSet {
    public MongoSQLResultSet(Statement statement, MongoCursor<BsonDocument> cursor, MongoJsonSchema schema) throws SQLException {
        super(statement);
        Preconditions.checkNotNull(cursor);

        this.rsMetaData = new MongoSQLResultSetMetaData(schema);
        this.cursor = cursor;
    }

    public MongoSQLResultSet(Statement statement, MongoCursor<BsonDocument> cursor) {
        super(statement);
        this.cursor = cursor;
    }

    // This is only used for testing, and that is why it has package level access, and the
    // tests have been moved into this package.
    BsonDocument getCurrent() {
        return current;
    }

    @Override
    protected boolean checkNull(BsonValue o) {
        // TODO, actually check if null
        throw new RuntimeException("TODO");
    }

    @Override
    protected BsonValue getBsonValue(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected BsonValue getBsonValue(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected Object getObject(BsonValue o, int columnType) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected byte[] handleBytesConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected byte[] getBytes(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected String handleStringConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected String getString(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected boolean handleBooleanConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected boolean getBoolean(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected long handleLongConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected long getLong(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected double handleDoubleConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected double getDouble(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected BigDecimal handleBigDecimalConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected BigDecimal getBigDecimal(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected java.util.Date handleUtilDateConversionFailure(String from) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    protected java.util.Date getUtilDate(BsonValue o) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
