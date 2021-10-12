package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoSQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {
    private static class NameSpace {
        String datasource;
        String columnLabel;

        NameSpace(String datasource, String columnLabel) {
            this.datasource = datasource;
            this.columnLabel = columnLabel;
        }
    }

    private static class DatasourceAndIndex {
        String columnLabel;
        int index;

        DatasourceAndIndex(String columnLabel, int index) {
            this.columnLabel = columnLabel;
            this.index = index;
        }
    }

    // A mapping from columnLabel name to datasource name and index.
    private Map<String, DatasourceAndIndex> columnLabels;
    // A mapping from index position to NameSpace (datasource, columnLabel).
    private List<NameSpace> columnIndices;
    // A mapping from index position to ColumnTypeInfo.
    private List<MongoSQLColumnTypeInfo> columnTypeInfo;

    private static void assertObjectSchema(MongoJsonSchema schema) throws SQLException {
        if (schema.bsonType == null
                || !schema.bsonType.equals("object")
                || schema.properties == null) {
            throw new SQLException("ResultSetMetaData json schema must be object with properties");
        }
    }

    private void processDataSource(MongoJsonSchema schema, String datasource) throws SQLException {
        MongoJsonSchema datasourceSchema = schema.properties.get(datasource);
        assertObjectSchema(datasourceSchema);

        Object[] columns = datasourceSchema.properties.keySet().toArray();
        Arrays.sort(columns);

        for (Object column : columns) {
            String columnAsStr = (String) column;
            MongoJsonSchema columnSchema = datasourceSchema.properties.get(columnAsStr);
            columnIndices.add(new NameSpace(datasource, columnAsStr));
            int subNullability =
                    (datasourceSchema.required == null
                                    || !datasourceSchema.required.contains(columnAsStr))
                            ? ResultSetMetaData.columnNullable
                            : ResultSetMetaData.columnNoNulls;
            columnTypeInfo.add(new MongoSQLColumnTypeInfo(columnSchema, subNullability));
            if (!columnLabels.containsKey(columnAsStr)) {
                columnLabels.put(
                        columnAsStr, new DatasourceAndIndex(datasource, columnIndices.size() - 1));
            }
        }
    };

    public MongoSQLResultSetMetaData(MongoJsonSchema schema) throws SQLException {
        assertObjectSchema(schema);

        columnLabels = new HashMap<String, DatasourceAndIndex>();
        columnIndices = new ArrayList<NameSpace>();
        columnTypeInfo = new ArrayList<MongoSQLColumnTypeInfo>();

        Object[] datasources = schema.properties.keySet().toArray();
        Arrays.sort(datasources);

        for (Object datasource : datasources) {
            String datasourceAsString = (String) datasource;
            processDataSource(schema, datasourceAsString);
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
    public ExtendedBsonType getExtendedBsonType(int column) throws SQLException {
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
