package com.mongodb.jdbc;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bson.BsonType;

public interface MongoColumnInfo {
    public static String BSON_ARRAY = "array";
    public static String BSON_BOOL = "bool";
    public static String BSON_BINDATA = "binData";
    public static String BSON_DATE = "date";
    public static String BSON_DBPOINTER = "dbPointer";
    public static String BSON_DECIMAL = "decimal";
    public static String BSON_DOUBLE = "double";
    public static String BSON_INT = "int";
    public static String BSON_JAVASCRIPT = "javascript";
    public static String BSON_JAVASCRIPT_WITH_SCOPE = "javascriptWithScope";
    public static String BSON_LONG = "long";
    public static String BSON_MAXKEY = "maxKey";
    public static String BSON_MINKEY = "minKey";
    public static String BSON_NULL = "null";
    public static String BSON_OBJECT = "object";
    public static String BSON_OBJECTID = "objectId";
    public static String BSON_REGEX = "regex";
    public static String BSON_STRING = "string";
    public static String BSON_SYMBOL = "symbol";
    public static String BSON_TIMESTAMP = "timestamp";
    public static String BSON_UNDEFINED = "undefined";
    public static Set<String> bsonTypes =
            new HashSet<>(
                    Arrays.asList(
                            BSON_ARRAY,
                            BSON_BOOL,
                            BSON_BINDATA,
                            BSON_DATE,
                            BSON_DBPOINTER,
                            BSON_DECIMAL,
                            BSON_DOUBLE,
                            BSON_INT,
                            BSON_JAVASCRIPT,
                            BSON_JAVASCRIPT_WITH_SCOPE,
                            BSON_LONG,
                            BSON_MAXKEY,
                            BSON_MINKEY,
                            BSON_NULL,
                            BSON_OBJECT,
                            BSON_OBJECTID,
                            BSON_REGEX,
                            BSON_STRING,
                            BSON_SYMBOL,
                            BSON_TIMESTAMP,
                            BSON_UNDEFINED));

    public boolean isPolymorphic();

    public BsonType getBsonTypeEnum();

    public String getBsonTypeName();

    public int getJDBCType();

    public int getNullability();

    public String getColumnName();

    public String getColumnAlias();

    public String getDatabase();

    public String getTableName();

    public String getTableAlias();

    /**
     * Converts a bson type name to a BsonType
     *
     * @param typeName is the bson type string as returned from the mongodb aggregation $type
     *     function, this list of viable inputs is in the bsonTypes static Set above.
     * @return BsonType object corresponding to bson type name.
     */
    public static BsonType getBsonTypeHelper(String typeName) throws SQLException {
        if (!bsonTypes.contains(typeName)) {
            throw new SQLException("Unknown bson type name: \"" + typeName + "\"");
        }
        // All type names can be guessed uniquely off a combination of first letter
        // and length except for "minKey" vs "maxKey" and "string" vs "symbol".
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
        // This is unreachible.
        throw new SQLException("Unknown bson type name: \"" + typeName + "\"");
    }
}
