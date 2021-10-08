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

    private static class LabelAndIndex {
        String columnLabel;
        int index;

        LabelAndIndex(String columnLabel, int index) {
            this.columnLabel = columnLabel;
            this.index = index;
        }
    }

    private static class ColumnTypeInfo {
        int jdbcType;
        BsonType bsonType;
        String bsonTypeName;
        int nullable;

        ColumnTypeInfo(MongoJsonSchema schema, boolean nullable) throws SQLException {
            // All schemata except AnyOf and Unsat must have a BsonType (and we do not support
            // Unsat).
            if(schema.bsonType != null) {
                this.bsonTypeName = bsonTypeName;
                this.bsonType = getBsonTypeHelper(bsonTypeName);
                this.jdbcType = getJDBCTypeForBsonType(this.bsonType);
                this.nullable = convertNullable(nullable);
                return;
            }
            // Otherwise, the schema must be an AnyOf.
            constructFromAnyOf(schema, nullable);
        }

        private int convertNullable(boolean nullable) {
             return nullable ?
                    ResultSetMetaData.columnNullable
                    :ResultSetMetaData.columnNoNulls;
        }

        private void constructFromAnyOf(MongoJsonSchema schema, boolean nullable) throws SQLException {
            if(schema.anyOf == null) {
                throw new SQLException("both bsonType and anyOf are null, this is not a valid schema");
            }
            for(MongoJsonSchema anyOfSchema: schema.anyOf) {
                if(anyOfSchema.bsonType == null) {
                    throw new SQLException("anyOf subschema must have bsonType field");
                }
                // Presense of null means this is nullable, whether or not the required keys
                // of the parent object schema indicate this is nullable.
                if(anyOfSchema.bsonType.equals("null")) {
                    nullable = true;
                } else {
                    // If bsonTypeName is not null, there must be more than one non-null anyOf type, so
                    // we default to "bson"
                    bsonTypeName = (bsonTypeName == null)?
                         anyOfSchema.bsonType
                        :"bson";
                }
            }
            this.bsonTypeName = bsonTypeName;
            this.bsonType = getBsonTypeHelper(bsonTypeName);
            this.jdbcType = getJDBCTypeForBsonType(this.bsonType);
            this.nullable = convertNullable(nullable);
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
    private Map<String, LabelAndIndex> columnLabels;
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
        MongoJsonSchema datasourceSchema = schema.properties.get(datasource);
        assertObjectSchema(datasourceSchema);

        Object[] columns = datasourceSchema.properties.keySet().toArray();
        Arrays.sort(columns);

        for(Object column: columns) {
            String columnAsStr = (String) column;
            MongoJsonSchema columnSchema = datasourceSchema.properties.get(columnAsStr);
            columnIndices.add(new NameSpace(datasource, columnAsStr));
            columnTypeInfo.add(new ColumnTypeInfo(columnSchema,
                        datasourceSchema.required == null
                     || !datasourceSchema.required.contains(columnAsStr)));
            if(!columnLabels.containsKey(columnAsStr)) {
                columnLabels.put(columnAsStr, new LabelAndIndex(datasource, columnIndices.size() - 1));
            }
        }
    };

    public MongoSQLResultSetMetaData(MongoJsonSchema schema) throws SQLException {
        assertObjectSchema(schema);

        columnLabels = new HashMap<String, LabelAndIndex>();
        columnIndices = new ArrayList<NameSpace>();
        columnTypeInfo = new ArrayList<ColumnTypeInfo>();
        this.schema = schema;

        Object[] datasources = schema.properties.keySet().toArray();
        Arrays.sort(datasources);

        for(Object datasource: datasources) {
            String datasourceAsString = (String) datasource;
            processDataSource(datasourceAsString);
        }
    }

    public int getColumnPositionFromLabel(String label) {
        return columnLabels.get(label).index;
    }

    public boolean hasColumnWithLabel(String label) {
        return columnLabels.containsKey(label);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnIndices.size();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return columnTypeInfo.get(column - 1).nullable;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return columnIndices.get(column - 1).columnLabel;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return columnIndices.get(column - 1).columnLabel;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return columnIndices.get(column - 1).datasource;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    @Override
    public BsonType getBsonType(int column) throws SQLException {
        return columnTypeInfo.get(column - 1).bsonType;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return columnTypeInfo.get(column - 1).jdbcType;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return columnTypeInfo.get(column - 1).bsonTypeName;
    }
}
