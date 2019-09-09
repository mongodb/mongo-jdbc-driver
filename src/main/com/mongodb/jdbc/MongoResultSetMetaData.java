package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.bson.Document;

public class MongoResultSetMetaData implements ResultSetMetaData {
	private Document doc;

	public MongoResultSetMetaData(Document doc) {
		this.doc = doc;
	}

    public int getColumnCount()throws SQLException {
		return doc.size();
	}

    public boolean isAutoIncrement(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isCaseSensitive(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isSearchable(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isCurrency(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int isNullable(int column)throws SQLException {
        // TODO?: use java schema validators to possibly
        // return false. Might be dangerous since validators
        // can be subverted.
        return columnNullableUnknown;
    }

    public boolean isSigned(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getColumnDisplaySize(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getColumnLabel(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getColumnName(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getSchemaName(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getPrecision(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getScale(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getTableName(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getCatalogName(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public int getColumnType(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public String getColumnTypeName(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isReadOnly(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isWritable(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    public boolean isDefinitelyWritable(int column)throws SQLException {
        throw new SQLException("unimplemented");
    }

    //--------------------------JDBC 2.0-----------------------------------

    public String getColumnClassName(int column)throws SQLException {
        throw new SQLException("unimplemented");
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
