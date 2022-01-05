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
    private static Set<String> nullTypes;
    private BsonType bsonTypeEnum;
    private int jdbcType;

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

    public MySQLColumnInfo() {}

    public MySQLColumnInfo(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            String bsonType)
            throws SQLException {
        this.database = database;
        this.table = table;
        this.tableAlias = tableAlias;
        this.column = column;
        this.columnAlias = columnAlias;
        this.bsonType = bsonType;
    }

    public void init() throws SQLException {
        // the BsonType depends on the original bsonType field sent
        // over the wire. This matters for ObjectID, becuase it has
        // a fixed length of 24 not an unknown length like other strings.
        bsonTypeEnum = BsonTypeInfo.getBsonTypeInfoByName(bsonType).getBsonType();
        jdbcType = getJDBCTypeForBsonType(bsonTypeEnum);
        if (nullTypes.contains(bsonType)) {
            bsonType = "null";
        }
        if ("objectId".equals(bsonType)) {
            // we will return string for objectIds, but the length is known
            // to be 24, unlike all other strings.
            bsonType = "string";
        }
    }

    private static int getJDBCTypeForBsonType(BsonType bsonTypeEnum) throws SQLException {
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
    public boolean isPolymorphic() {
        return false;
    }

    @Override
    public BsonType getBsonTypeEnum() {
        return bsonTypeEnum;
    }

    @Override
    public String getBsonTypeName() {
        return bsonType;
    }

    @Override
    public int getJDBCType() {
        return jdbcType;
    }

    @Override
    public int getNullability() {
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
