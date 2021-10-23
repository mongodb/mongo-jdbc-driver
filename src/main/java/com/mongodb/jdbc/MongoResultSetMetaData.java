package com.mongodb.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import org.bson.BsonType;
import org.bson.BsonValue;

public abstract class MongoResultSetMetaData implements ResultSetMetaData {
    protected final int unknownLength = 0;

    protected void checkBounds(int i) throws SQLException {
        if (i > getColumnCount()) {
            throw new SQLException("Index out of bounds: '" + i + "'.");
        }
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        checkBounds(column);
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        checkBounds(column);
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        checkBounds(column);
        return false;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        checkBounds(column);
        return "";
    }

    public abstract MongoColumnInfo getColumnInfo(int column) throws SQLException;

    public abstract boolean hasColumnWithLabel(String label) throws SQLException;

    public abstract int getColumnPositionFromLabel(String label) throws SQLException;

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        checkBounds(column);
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        checkBounds(column);
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        checkBounds(column);
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return getColumnInfo(column).getNullability();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnInfo(column).getColumnAlias();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnInfo(column).getColumnName();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return getColumnInfo(column).getTableAlias();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return getColumnInfo(column).getDatabase();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getColumnInfo(column).getJDBCType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getColumnInfo(column).getBsonTypeName();
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

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return true;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
            case BINARY:
            case BOOLEAN:
            case DATE_TIME:
            case DB_POINTER:
            case DECIMAL128:
            case DOCUMENT:
            case DOUBLE:
            case INT32:
            case INT64:
            case MAX_KEY:
            case MIN_KEY:
            case NULL:
            case OBJECT_ID:
            case TIMESTAMP:
            case UNDEFINED:
                return false;
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case REGULAR_EXPRESSION:
            case STRING:
            case SYMBOL:
                return true;
        }
        return false;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return true;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case DOUBLE:
            case DECIMAL128:
            case INT32:
            case INT64:
                return true;
            case ARRAY:
            case BINARY:
            case BOOLEAN:
            case DATE_TIME:
            case DB_POINTER:
            case DOCUMENT:
            case MAX_KEY:
            case MIN_KEY:
            case NULL:
            case OBJECT_ID:
            case TIMESTAMP:
            case UNDEFINED:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case REGULAR_EXPRESSION:
            case STRING:
            case SYMBOL:
                return false;
        }
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return unknownLength;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
                return unknownLength;
            case BINARY:
                return unknownLength;
            case BOOLEAN:
                return 1;
            case DATE_TIME:
                //24 characters to display.
                return 24;
            case DB_POINTER:
                return 0;
            case DECIMAL128:
                return 34;
            case DOCUMENT:
                return unknownLength;
            case DOUBLE:
                return 15;
            case INT32:
                return 10;
            case INT64:
                return 19;
            case JAVASCRIPT:
                return unknownLength;
            case JAVASCRIPT_WITH_SCOPE:
                return unknownLength;
            case MAX_KEY:
                return 0;
            case MIN_KEY:
                return 0;
            case NULL:
                return 0;
            case OBJECT_ID:
                return 24;
            case REGULAR_EXPRESSION:
                return unknownLength;
            case STRING:
                return unknownLength;
            case SYMBOL:
                return unknownLength;
            case TIMESTAMP:
                return unknownLength;
            case UNDEFINED:
                return 0;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return unknownLength;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
                return unknownLength;
            case BINARY:
                return unknownLength;
            case BOOLEAN:
                return 1;
            case DATE_TIME:
                return 24;
            case DB_POINTER:
                return 0;
            case DECIMAL128:
                return 34;
            case DOCUMENT:
                return unknownLength;
            case DOUBLE:
                return 15;
            case INT32:
                return 10;
            case INT64:
                return 19;
            case JAVASCRIPT:
                return unknownLength;
            case JAVASCRIPT_WITH_SCOPE:
                return unknownLength;
            case MAX_KEY:
                return 0;
            case MIN_KEY:
                return 0;
            case NULL:
                return 0;
            case OBJECT_ID:
                return 24;
            case REGULAR_EXPRESSION:
                return unknownLength;
            case STRING:
                return unknownLength;
            case SYMBOL:
                return unknownLength;
            case TIMESTAMP:
                return 0;
            case UNDEFINED:
                return 0;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public int getScale(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return unknownLength;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
            case BINARY:
            case BOOLEAN:
            case DATE_TIME:
            case DB_POINTER:
            case DOCUMENT:
            case INT32:
            case INT64:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case NULL:
            case OBJECT_ID:
            case REGULAR_EXPRESSION:
            case STRING:
            case SYMBOL:
            case TIMESTAMP:
            case UNDEFINED:
                return 0;
            case DECIMAL128:
                return 34;
            case DOUBLE:
                return 15;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    // --------------------------JDBC 2.0-----------------------------------
    @Override
    public String getColumnClassName(int column) throws SQLException {
        String intClassName = int.class.getName();
        String booleanClassName = boolean.class.getName();
        String stringClassName = String.class.getName();
        String floatClassName = float.class.getName();
        String doubleClassName = double.class.getName();
        String bigDecimalClassName = BigDecimal.class.getName();
        String timestampClassName = Timestamp.class.getName();
        String bsonClassName = BsonValue.class.getName();

        int columnType = getColumnType(column);
        switch (columnType) {
            case Types.ARRAY:
                // not supported
                break;
            case Types.BIGINT:
                return intClassName;
            case Types.BINARY:
                // not supported
                break;
            case Types.BIT:
                return booleanClassName;
            case Types.BLOB:
                // not supported
                break;
            case Types.BOOLEAN:
                return booleanClassName;
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
                return bigDecimalClassName;
            case Types.DISTINCT:
                // not supported
                break;
            case Types.DOUBLE:
                return doubleClassName;
            case Types.FLOAT:
                return floatClassName;
            case Types.INTEGER:
                return intClassName;
            case Types.JAVA_OBJECT:
                // not supported
                break;
            case Types.LONGNVARCHAR:
                return stringClassName;
            case Types.LONGVARBINARY:
                // not supported
                break;
            case Types.LONGVARCHAR:
                return stringClassName;
            case Types.NCHAR:
                return stringClassName;
            case Types.NCLOB:
                // not supported
                break;
            case Types.NULL:
                return null;
            case Types.NUMERIC:
                return doubleClassName;
            case Types.NVARCHAR:
                return stringClassName;
            case Types.OTHER:
                return bsonClassName;
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
                return intClassName;
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
                return timestampClassName;
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // not supported
                break;
            case Types.TINYINT:
                return intClassName;
            case Types.VARBINARY:
                // not supported
                break;
            case Types.VARCHAR:
                return stringClassName;
        }
        throw new SQLException("getObject not supported for column type " + columnType);
    }
}
