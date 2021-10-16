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
        String datasource;
        int index;

        DatasourceAndIndex(String datasource, int index) {
            this.datasource = datasource;
            this.index = index;
        }
    }

    // A mapping from columnLabel name to datasource name and index.
    private Map<String, DatasourceAndIndex> columnLabels;
    // A mapping from index position to NameSpace (datasource, columnLabel).
    private List<NameSpace> columnIndices;
    // A mapping from index position to ColumnTypeInfo.
    private List<MongoSQLColumnInfo> columnInfo;

    // This gets the datasource for a given columnLabel, and is used
    // in MongoSQLResultSet to retrieve data by label.
    String getDatasource(String columnLabel) {
        return columnLabels.get(columnLabel).datasource;
    }

    private void assertObjectSchema(MongoJsonSchema schema) throws SQLException {
        if (!schema.isObject() || schema.properties == null) {
            throw new SQLException("ResultSetMetaData json schema must be object with properties");
        }
    }

    private void processDataSource(MongoJsonSchema schema, String datasource) throws SQLException {
        MongoJsonSchema datasourceSchema = schema.properties.get(datasource);
        assertObjectSchema(datasourceSchema);

        String[] fields = datasourceSchema.properties.keySet().toArray(new String[0]);
        Arrays.sort(fields);

        for (String field : fields) {
            MongoJsonSchema columnSchema = datasourceSchema.properties.get(field);
            columnIndices.add(new NameSpace(datasource, field));
            int subNullability =
                    (datasourceSchema.required == null
                                    || !datasourceSchema.required.contains(field))
                            ? ResultSetMetaData.columnNullable
                            : ResultSetMetaData.columnNoNulls;
            columnInfo.add(new MongoSQLColumnInfo(datasource, field, columnSchema, subNullability));
            if (!columnLabels.containsKey(field)) {
                columnLabels.put(
                        field, new DatasourceAndIndex(datasource, columnIndices.size() - 1));
            }
        }
    };

    public MongoSQLResultSetMetaData(MongoJsonSchema schema) throws SQLException {
        assertObjectSchema(schema);

        columnLabels = new HashMap<String, DatasourceAndIndex>();
        columnIndices = new ArrayList<NameSpace>();
        columnInfo = new ArrayList<MongoSQLColumnInfo>();

        String[] datasources = schema.properties.keySet().toArray(new String[0]);
        Arrays.sort(datasources);

        for (String datasource : datasources) {
            processDataSource(schema, datasource);
        }
    }

    public int getColumnPositionFromLabel(String label) {
        return columnLabels.get(label).index;
    }

    public boolean hasColumnWithLabel(String label) {
        return columnLabels.containsKey(label);
    }

    @Override
    public MongoColumnInfo getColumnInfo(int column) throws SQLException {
        checkBounds(column);
        return columnInfo.get(column - 1);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnIndices.size();
    }
}
