package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.UUID;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class MongoResultSetMetaData implements ResultSetMetaData {
    private Row row;

    public MongoResultSetMetaData(Row row) {
        this.row = row;
    }

    public int getColumnCount() throws SQLException {
        return row.size();
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public int isNullable(int column) throws SQLException {
        return columnNullableUnknown;
    }

    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        // TODO: Format as string and get length?
        throw new SQLException("unimplemented");
    }

    // getColumnLabel is for print out versus simple name, MongoDB makes no distinction.
    public String getColumnLabel(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getColumnName(int column) throws SQLException {
        return getColumnLabel(column);
    }

    public String getSchemaName(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getPrecision(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getScale(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getTableName(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getCatalogName(int column) throws SQLException {
        return getColumnLabel(column);
    }

    private Object getObject(int column) throws SQLException {
        if (column > row.size()) {
            throw new SQLException("index out of bounds: '" + column + "'");
        }
        return row.values.get(column - 1).value;
    }

    public int getColumnType(int column) throws SQLException {
        Object o = getObject(column);
        // TODO: figure out how to handle missing bson types.
        if (o == null) {
            return Types.NULL;
        }
        if (o instanceof BsonDouble) {
            return Types.DOUBLE;
        }
        if (o instanceof BsonString) {
            return Types.LONGVARCHAR;
        }
        // Row
        if (o instanceof Binary) {
            return Types.BLOB;
        }
        if (o instanceof UUID) {
            return Types.BLOB;
        }
        // Undefined
        if (o instanceof ObjectId) {
            return Types.LONGVARCHAR;
        }
        if (o instanceof Boolean) {
            return Types.BIT;
        }
        if (o instanceof Date) {
            return Types.TIMESTAMP;
        }
        // Regex
        if (o instanceof Integer) {
            return Types.INTEGER;
        }
        // (BSON) Timestamp
        if (o instanceof Long) {
            return Types.INTEGER;
        }
        if (o instanceof Decimal128) {
            return Types.DECIMAL;
        }
        // MinKey
        // MaxKey
        throw new SQLException("unknown mongo type: " + o.getClass().getName());
    }

    public String getColumnTypeName(int column) throws SQLException {
        Object o = getObject(column);
        // TODO: figure out how to handle missing bson types.
        if (o == null) {
            return "null";
        }
        if (o instanceof Double) {
            return "double";
        }
        if (o instanceof String) {
            return "string";
        }
        // Embedded Document
        if (o instanceof Binary) {
            return "binData";
        }
        if (o instanceof UUID) {
            return "binData";
        }
        // Undefined
        if (o instanceof ObjectId) {
            return "objectId";
        }
        if (o instanceof Boolean) {
            return "bool";
        }
        if (o instanceof Date) {
            return "date";
        }
        // Regex
        if (o instanceof Integer) {
            return "int";
        }
        // (BSON) Timestamp
        if (o instanceof Long) {
            return "long";
        }
        if (o instanceof Decimal128) {
            return "decimal";
        }
        // MinKey
        // MaxKey
        throw new SQLException("unknown mongo type: " + o.getClass().getName());
    }

    public boolean isReadOnly(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isWritable(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new SQLException("unimplemented");
    }

    // --------------------------JDBC 2.0-----------------------------------

    public String getColumnClassName(int column) throws SQLException {
        Object o = getObject(column);
        if (o == null) {
            // I guess?
            return "java.lang.Object";
        }
        return o.getClass().getName();
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
