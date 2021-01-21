package com.mongodb.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import org.bson.BsonType;

public class MongoResultSetMetaData implements ResultSetMetaData {
    private List<Column> columns;
    private HashMap<String, Integer> columnPositions;
    private final int unknownLength = 0;

    public MongoResultSetMetaData(MongoResultDoc metadataDoc) {
        columns = metadataDoc.columns;

        columnPositions = new HashMap<>(columns.size());
        int i = 0;
        for (Column c : columns) {
            columnPositions.put(c.columnAlias, i++);
        }
    }

    private void checkBounds(int i) throws SQLException {
        if (i > getColumnCount()) {
            throw new SQLException("Index out of bounds: '" + i + "'.");
        }
    }

    public int getColumnPositionFromLabel(String label) {
        return columnPositions.get(label);
    }

    public boolean hasColumnWithLabel(String label) {
        return columnPositions.containsKey(label);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        checkBounds(column);
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        BsonType t = getBsonType(column);
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
    public int isNullable(int column) throws SQLException {
        checkBounds(column);
        return columnNullableUnknown;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        BsonType t = getBsonType(column);
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
        BsonType t = getBsonType(column);
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
    public String getColumnLabel(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).columnAlias;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).column;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        checkBounds(column);
        return "";
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        BsonType t = getBsonType(column);
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
        BsonType t = getBsonType(column);
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

    @Override
    public String getTableName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column).tableAlias;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        checkBounds(column);
        return columns.get(column - 1).database;
    }

    public BsonType getBsonType(int column) throws SQLException {
        checkBounds(column);
        String typeName = columns.get(column - 1).bsonType;
        return getBsonTypeHelper(typeName);
    }

    static BsonType getBsonTypeHelper(String typeName) throws SQLException {
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

    @Override
    public int getColumnType(int column) throws SQLException {
        BsonType t = getBsonType(column);
        switch (t) {
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
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        BsonType t = getBsonType(column);
        switch (t) {
                // we will return the same names as the mongodb $type function:
            case ARRAY:
                return "array";
            case BINARY:
                return "binData";
            case BOOLEAN:
                return "bool";
            case DATE_TIME:
                return "date";
            case DB_POINTER:
                return "null";
            case DECIMAL128:
                return "decimal";
            case DOCUMENT:
                return "null";
            case DOUBLE:
                return "double";
            case END_OF_DOCUMENT:
                return "null";
            case INT32:
                return "int";
            case INT64:
                return "long";
            case JAVASCRIPT:
                return "null";
            case JAVASCRIPT_WITH_SCOPE:
                return "null";
            case MAX_KEY:
                return "null";
            case MIN_KEY:
                return "null";
            case NULL:
                return "null";
            case OBJECT_ID:
                return "string";
            case REGULAR_EXPRESSION:
                return "null";
            case STRING:
                return "string";
            case SYMBOL:
                return "null";
            case TIMESTAMP:
                return "null";
            case UNDEFINED:
                return "null";
        }
        throw new SQLException("unknown bson type: " + t);
    }

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

    // --------------------------JDBC 2.0-----------------------------------

    @Override
    public String getColumnClassName(int column) throws SQLException {
        checkBounds(column);

        String intClassName = int.class.getName();
        String booleanClassName = boolean.class.getName();
        String stringClassName = String.class.getName();
        String floatClassName = float.class.getName();
        String doubleClassName = double.class.getName();
        String bigDecimalClassName = BigDecimal.class.getName();
        String timestampClassName = Timestamp.class.getName();

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
                // not supported
                break;
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
}
