package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.bson.BsonType;
import org.bson.BsonDocument;

public class MongoSQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {
    private static class NameSpace {
        String datasource;
        String columnLabel;

        NameSpace(String datasource, String columnLabel) {
            this.datasource = datasource;
            this.columnLabel = columnLabel;
        }
    }

    private static class ColumnTypeInfo {
        int jdbcType;
        BsonType bsonType;
        String bsonTypeName;
        boolean nullable;

        ColumnTypeInfo(MongoJsonSchema schema, boolean nullable) throws SQLException {
            if(schema.anyOf != null) {
                for(var anyOfSchema: schem.anyOf) {

                }
            }
            this.bsonTypeName = bsonTypeName;
            this.bsonType = getBsonTypeHelper(bsonTypeName);
            this.jdbcType = getJDBCTypeForBsonType(this.bsonType);
            this.nullable = nullable;
        }
    }

    private static int getJDBCTypeForBsonType(BsonType t) throws SQLException {
        switch (t) {
            case ARRAY:
            case DB_POINTER:
            case DOCUMENT:
            case JAVASCRIPT:
            case JAVASCRIPT_WITH_SCOPE:
            case MAX_KEY:
            case MIN_KEY:
            case OBJECT_ID:
            case REGULAR_EXPRESSION:
            case SYMBOL:
            case TIMESTAMP:
            case UNDEFINED:
                return Types.OTHER;
            case BINARY:
                return Types.BINARY;
            case BOOLEAN:
                return Types.BIT;
            case DATE_TIME:
                return Types.TIMESTAMP;
            case INT32:
                return Types.INTEGER;
            case INT64:
                return Types.BIGINT;
            case NULL:
                return Types.NULL;
            case STRING:
                return  Types.LONGVARCHAR;
            case DECIMAL128:
                return Types.DECIMAL;
            case DOUBLE:
                return Types.DOUBLE;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    // A mapping from columnLabel name to datasource name.
    private Map<String, String> columnLabels;
    // A mapping from index position to NameSpace (datasource, columnLabel).
    private List<NameSpace> columnIndices;
    // A mapping from index position to ColumnTypeInfo.
    private List<ColumnTypeInfo> columnTypeInfo;
    // The metadata JsonSchema
    private MongoJsonSchema schema;

    private static void assertObjectSchema(MongoJsonSchema schema) throws SQLException {
        if(schema.bsonType == null || !schema.equals("object") || schema.properties == null) {
            throw new SQLException("ResultSetMetaData json schema must be object with properties");
        }
    }

    private void processDataSource(String datasource) throws SQLException {
        var datasourceSchema = schema.properties.get(datasource);
        assertObjectSchema(datasourceSchema);

        Object[] columns = datasourceSchema.properties.keySet().toArray();
        Arrays.sort(columns);

        for(var column: columns) {
            var columnAsStr = (String) column;
            var columnSchema = datasourceSchema.properties.get(columnAsStr);
            if(!columnLabels.containsKey(columnAsStr)) {
                columnLabels.put(columnAsStr, datasource);
            }
            columnIndices.add(new NameSpace(datasource, columnAsStr));
            columnTypeInfo.add(new ColumnTypeInfo(columnSchema,
                        datasourceSchema.required == null
                     || !datasourceSchema.required.contains(columnAsStr)));
        }
    };

    public MongoSQLResultSetMetaData(MongoJsonSchema schema) throws SQLException {
        assertObjectSchema(schema);

        columnLabels = new HashMap<String, String>();
        columnIndices = new ArrayList<NameSpace>();
        columnTypeInfo = new ArrayList<ColumnTypeInfo>();
        this.schema = schema;

        Object[] datasources = schema.properties.keySet().toArray();
        Arrays.sort(datasources);

        for(var datasource: datasources) {
            var datasourceAsString = (String) datasource;
            processDataSource(datasourceAsString);
        }
    }

    @Override
    public int getColumnCount() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public int isNullable(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getTableName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public BsonType getBsonType(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }
}
