package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import org.bson.BsonType;

public class MySQLColumnInfo implements MongoColumnInfo {
    public String database;
    public String table;
    public String tableAlias;
    public String column;
    public String columnAlias;
    public String bsonType;
    private BsonType bsonTypeEnum;
    private int jdbcType;
    private static Set<String> nullTypes;

    static {
        nullTypes = new HashSet<String>();
        nullTypes.add("dbPointer");
        nullTypes.add("javascript");
        nullTypes.add("javascriptWithScope");
        nullTypes.add("maxKey");
        nullTypes.add("minKey");
        nullTypes.add("object");
        nullTypes.add("regex");
        nullTypes.add("symbol");
        nullTypes.add("timestamp");
        nullTypes.add("undefined");
    }

    public MySQLColumnInfo(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            String bsonType) throws SQLException {
        this.database = database;
        this.table = table;
        this.tableAlias = tableAlias;
        this.column = column;
        this.columnAlias = columnAlias;
        this.bsonType = bsonType;
        this.bsonTypeEnum = MongoColumnInfo.getBsonTypeHelper(bsonType);
        this.jdbcType = convertToJdbcType(bsonTypeEnum);
    }

    private static int convertToJdbcType(BsonType bsonTypeEnum) throws SQLException {
        switch (bsonTypeEnum) {
            case ARRAY:
                return Types.ARRAY;
            case BINARY:
                return Types.BLOB;
            case BOOLEAN:
                return Types.BIT;
            case DATE_TIME:
                return Types.TIMESTAMP;
            case DB_POINTER:
                return Types.NULL;
            case DECIMAL128:
                return Types.DECIMAL;
            case DOCUMENT:
                return Types.NULL;
            case DOUBLE:
                return Types.DOUBLE;
            case INT32:
                return Types.INTEGER;
            case INT64:
                return Types.INTEGER;
            case JAVASCRIPT:
                return Types.NULL;
            case JAVASCRIPT_WITH_SCOPE:
                return Types.NULL;
            case MAX_KEY:
                return Types.NULL;
            case MIN_KEY:
                return Types.NULL;
            case NULL:
                return Types.NULL;
            case OBJECT_ID:
                return Types.LONGVARCHAR;
            case REGULAR_EXPRESSION:
                return Types.NULL;
            case STRING:
                return Types.LONGVARCHAR;
            case SYMBOL:
                return Types.NULL;
            case TIMESTAMP:
                return Types.NULL;
            case UNDEFINED:
                return Types.NULL;
        }
        throw new SQLException("Unknown BSON type: " + bsonTypeEnum + ".");
    }

    @Override
    public String toString() {
        return "Column{"
                + "database='"
                + database
                + '\''
                + ", table='"
                + table
                + '\''
                + ", tableAlias='"
                + tableAlias
                + '\''
                + ", column='"
                + column
                + '\''
                + ", columnAlias='"
                + columnAlias
                + '\''
                + ", bsonType="
                + bsonType
                + '}';
    }

    @Override
	public boolean isPolymorphic(){
        return false;
	}

    @Override
	public BsonType getBsonType() throws SQLException {
        return bsonTypeEnum;
	}

    @Override
	public String getBsonTypeName(){
        if(nullTypes.contains(bsonType)) {
            return "null";
        }
        if("objectId".equals(bsonType)) {
            return "string";
        }
        return bsonType;
	}

    @Override
	public int getJdbcType(){
        return jdbcType;
    }

    @Override
	public int getNullability(){
        return ResultSetMetaData.columnNullableUnknown;
	}

    @Override
    public String getColumnName() {
        return column;
    }

    @Override
    public String getColumnAlias() {
        return columnAlias;
    }

    @Override
    public String getTableName() {
        return table;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public String getDatabase() {
        return database;
    }
}
