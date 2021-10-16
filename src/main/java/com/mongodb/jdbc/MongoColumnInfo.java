package com.mongodb.jdbc;

import java.sql.SQLException;
import org.bson.BsonType;

public interface MongoColumnInfo {
    public boolean isPolymorphic() throws SQLException;

    public BsonType getBsonType() throws SQLException;

    public String getBsonTypeName() throws SQLException;

    public int getJDBCType() throws SQLException;

    public int getNullability() throws SQLException;

    public String getColumnName() throws SQLException;

    public String getColumnAlias() throws SQLException;

    public String getDatabase() throws SQLException;

    public String getTableName() throws SQLException;

    public String getTableAlias() throws SQLException;

    public static BsonType getBsonTypeHelper(String typeName) throws SQLException {
        // bsonType strings as represented by the $type function:
        // "array"
        // "bool"
        // "binData"
        // "date"
        // "dbPointer"
        // "decimal"
        // "double"
        // "int"
        // "javascript"
        // "javascriptWithScope"
        // "long"
        // "maxKey"
        // "minKey"
        // "null"
        // "object"
        // "objectId"
        // "regex"
        // "string"
        // "symbol"
        // "timestamp"
        // "undefined"
        //
        // This function will not always throw an exception for an unknown type name. Type
        // names returned from ADL are assumed correct.
        // Fortunately all type names can be guessed uniquely off a combination of first letter
        // and length except for "minKey" vs "maxKey" and "string" vs "symbol", again, assuming
        // all returned names are correct.
        switch (typeName.charAt(0)) {
            case 'a':
                return BsonType.ARRAY;
            case 'b':
                switch (typeName.length()) {
                    case 4:
                        return BsonType.BOOLEAN;
                    case 7:
                        return BsonType.BINARY;
                }
                break;
            case 'd':
                switch (typeName.length()) {
                    case 4:
                        return BsonType.DATE_TIME;
                    case 6:
                        return BsonType.DOUBLE;
                    case 7:
                        return BsonType.DECIMAL128;
                    case 9:
                        return BsonType.DB_POINTER;
                }
                break;
            case 'i':
                return BsonType.INT32;
            case 'j':
                switch (typeName.length()) {
                    case 10:
                        return BsonType.JAVASCRIPT;
                    case 19:
                        return BsonType.JAVASCRIPT_WITH_SCOPE;
                }
                break;
            case 'l':
                return BsonType.INT64;
            case 'm':
                switch (typeName.charAt(1)) {
                    case 'a':
                        return BsonType.MAX_KEY;
                    case 'i':
                        return BsonType.MIN_KEY;
                }
                break;
            case 'n':
                return BsonType.NULL;
            case 'o':
                switch (typeName.length()) {
                    case 6: // "object"
                        return BsonType.DOCUMENT;
                    case 8:
                        return BsonType.OBJECT_ID;
                }
                break;
            case 'r':
                return BsonType.REGULAR_EXPRESSION;
            case 's':
                switch (typeName.charAt(1)) {
                    case 't':
                        return BsonType.STRING;
                    case 'y':
                        return BsonType.SYMBOL;
                }
                break;
            case 't':
                return BsonType.TIMESTAMP;
            case 'u':
                return BsonType.UNDEFINED;
        }
        throw new SQLException("Unknown bson type name: \"" + typeName + "\"");
    }
}
