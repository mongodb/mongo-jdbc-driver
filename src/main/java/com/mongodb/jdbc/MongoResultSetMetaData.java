/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.logging.QueryDiagnostics;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bson.BsonType;
import org.bson.BsonValue;

@AutoLoggable
public class MongoResultSetMetaData implements ResultSetMetaData {

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
    private Map<String, List<DatasourceAndIndex>> columnLabels;
    // A mapping from index position to NameSpace (datasource, columnLabel).
    private List<NameSpace> columnIndices;
    // A mapping from index position to ColumnTypeInfo.
    private List<MongoColumnInfo> columnInfo;
    protected final int UNKNOWN_LENGTH = 0;
    protected MongoLogger logger;

    /**
     * Constructor.
     *
     * @param schema The resultset schema.
     * @param sortFieldsAlphabetically Flag to set the fields sort order. True if fields must be
     *     sorted alphabetically. False otherwise.
     * @param parentLogger The parent connection logger.
     * @param statementId The statement id for the logger or null if this resultset is not tied to a
     *     statement.
     */
    public MongoResultSetMetaData(
            MongoJsonSchema schema,
            List<List<String>> selectOrder,
            boolean sortFieldsAlphabetically,
            MongoLogger parentLogger,
            Integer statementId,
            QueryDiagnostics queryDiagnostics)
            throws SQLException {
        this.logger =
                (statementId == null)
                        ? new MongoLogger(this.getClass().getCanonicalName(), parentLogger)
                        : new MongoLogger(
                                this.getClass().getCanonicalName(), parentLogger, statementId);
        if (queryDiagnostics != null) {
            logger.setQueryDiagnostics(queryDiagnostics);
        } else {
            logger.setResultSetSchema(schema);
        }

        assertDatasourceSchema(schema);

        columnLabels = new HashMap<String, List<DatasourceAndIndex>>();
        columnIndices = new ArrayList<NameSpace>();
        columnInfo = new ArrayList<MongoColumnInfo>();

        if (selectOrder == null || selectOrder.isEmpty()) {
            String[] datasources = schema.properties.keySet().toArray(new String[0]);
            Arrays.sort(datasources);

            for (String datasource : datasources) {
                processDataSource(schema, datasource, sortFieldsAlphabetically);
            }
        } else {
            processSelectOrder(selectOrder, schema);
        }
    }

    private void assertDatasourceSchema(MongoJsonSchema schema) throws SQLException {
        // A Datasource Schema must be an Object Schema, and unlike Object Schemata in general,
        // the properties field cannot be null.
        if (!schema.isObject() || schema.properties == null) {
            throw new SQLException("ResultSetMetaData json schema must be object with properties");
        }
    }

    private void processDataSource(
            MongoJsonSchema schema, String datasource, boolean sortFieldsAlphabetically)
            throws SQLException {
        MongoJsonSchema datasourceSchema = schema.properties.get(datasource);
        assertDatasourceSchema(datasourceSchema);

        List<String> fields = null;
        if (sortFieldsAlphabetically) {
            fields =
                    datasourceSchema
                            .properties
                            .keySet()
                            .stream()
                            .sorted()
                            .collect(Collectors.toList());
        } else {
            fields = datasourceSchema.properties.keySet().stream().collect(Collectors.toList());
        }

        for (String field : fields) {
            processColumnInfo(datasource, field, datasourceSchema);
        }
    };

    private void processSelectOrder(List<List<String>> selectOrder, MongoJsonSchema schema)
            throws SQLException {

        // reset columnIndices and columnInfo to empty lists and populate in select order
        columnIndices = new ArrayList<NameSpace>();
        columnInfo = new ArrayList<MongoColumnInfo>();
        for (List<String> column : selectOrder) {
            String datasource = column.get(0);
            String field = column.get(1);
            MongoJsonSchema datasourceSchema = schema.properties.get(datasource);
            assertDatasourceSchema(datasourceSchema);
            processColumnInfo(datasource, field, datasourceSchema);
        }
    }

    private void processColumnInfo(
            String datasource, String field, MongoJsonSchema datasourceSchema) throws SQLException {
        MongoJsonSchema columnSchema = datasourceSchema.properties.get(field);
        BsonTypeInfo columnBsonTypeInfo = columnSchema.getBsonTypeInfo();
        int nullability = datasourceSchema.getColumnNullability(field);
        columnIndices.add(new NameSpace(datasource, field));
        columnInfo.add(new MongoColumnInfo(datasource, field, columnBsonTypeInfo, nullability));
        if (!columnLabels.containsKey(field)) {
            List<DatasourceAndIndex> datasourceAndIndexList = new ArrayList<>();
            datasourceAndIndexList.add(
                    new DatasourceAndIndex(datasource, columnIndices.size() - 1));
            columnLabels.put(field, datasourceAndIndexList);
        } else {
            columnLabels
                    .get(field)
                    .add(new DatasourceAndIndex(datasource, columnIndices.size() - 1));
        }
    }

    // This gets the datasource for a given columnLabel.
    // It is only used in our unit tests.
    protected String getDatasource(String columnLabel) throws Exception {
        List<DatasourceAndIndex> columnsForLabel = columnLabels.get(columnLabel);
        if (columnsForLabel.size() > 1) {
            throw new Exception(
                    String.format(
                            "Multiple columns with the label '%s' exist. Use indexes to avoid ambiguity.",
                            columnLabel));
        } else {
            return columnsForLabel.get(0).datasource;
        }
    }

    protected void checkBounds(int i) throws SQLException {
        if (i > getColumnCount()) {
            throw new SQLException("Index out of bounds: '" + i + "'.");
        }
    }

    public MongoColumnInfo getColumnInfo(int column) throws SQLException {
        checkBounds(column);
        return columnInfo.get(column - 1);
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        checkBounds(column);
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
    public String getSchemaName(int column) throws SQLException {
        checkBounds(column);
        return "";
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnIndices.size();
    }

    public boolean hasColumnWithLabel(String label) {
        return columnLabels.containsKey(label);
    }

    public int getColumnPositionFromLabel(String label) throws Exception {
        List<DatasourceAndIndex> columnsForLabel = columnLabels.get(label);
        if (columnsForLabel.size() > 1) {
            throw new Exception(
                    "Multiple columns with the label '"
                            + label
                            + "' exist. Use indexes to avoid ambiguity.");
        } else {
            return columnsForLabel.get(0).index;
        }
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

    @Override
    public int isNullable(int column) throws SQLException {
        return getColumnInfo(column).getNullability();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnInfo(column).getColumnAlias();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnInfo(column).getColumnName();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return getColumnInfo(column).getTableAlias();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return getColumnInfo(column).getDatabase();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getColumnInfo(column).getJDBCType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getColumnInfo(column).getBsonTypeName();
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

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return true;
        }
        BsonType t = ci.getBsonTypeEnum();
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
    public boolean isSigned(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return true;
        }
        BsonType t = ci.getBsonTypeEnum();
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
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return UNKNOWN_LENGTH;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
                return UNKNOWN_LENGTH;
            case BINARY:
                return UNKNOWN_LENGTH;
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
                return UNKNOWN_LENGTH;
            case DOUBLE:
                return 15;
            case INT32:
                return 10;
            case INT64:
                return 19;
            case JAVASCRIPT:
                return UNKNOWN_LENGTH;
            case JAVASCRIPT_WITH_SCOPE:
                return UNKNOWN_LENGTH;
            case MAX_KEY:
                return 0;
            case MIN_KEY:
                return 0;
            case NULL:
                return 0;
            case OBJECT_ID:
                return 24;
            case REGULAR_EXPRESSION:
                return UNKNOWN_LENGTH;
            case STRING:
                return UNKNOWN_LENGTH;
            case SYMBOL:
                return UNKNOWN_LENGTH;
            case TIMESTAMP:
                return UNKNOWN_LENGTH;
            case UNDEFINED:
                return 0;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return UNKNOWN_LENGTH;
        }
        BsonType t = ci.getBsonTypeEnum();
        switch (t) {
            case ARRAY:
                return UNKNOWN_LENGTH;
            case BINARY:
                return UNKNOWN_LENGTH;
            case BOOLEAN:
                return 1;
            case DATE_TIME:
                return 24;
            case DB_POINTER:
                return 0;
            case DECIMAL128:
                return 34;
            case DOCUMENT:
                return UNKNOWN_LENGTH;
            case DOUBLE:
                return 15;
            case INT32:
                return 10;
            case INT64:
                return 19;
            case JAVASCRIPT:
                return UNKNOWN_LENGTH;
            case JAVASCRIPT_WITH_SCOPE:
                return UNKNOWN_LENGTH;
            case MAX_KEY:
                return 0;
            case MIN_KEY:
                return 0;
            case NULL:
                return 0;
            case OBJECT_ID:
                return 24;
            case REGULAR_EXPRESSION:
                return UNKNOWN_LENGTH;
            case STRING:
                return UNKNOWN_LENGTH;
            case SYMBOL:
                return UNKNOWN_LENGTH;
            case TIMESTAMP:
                return 0;
            case UNDEFINED:
                return 0;
        }
        throw new SQLException("unknown bson type: " + t);
    }

    @Override
    public int getScale(int column) throws SQLException {
        MongoColumnInfo ci = getColumnInfo(column);
        if (ci.isPolymorphic()) {
            return UNKNOWN_LENGTH;
        }
        BsonType t = ci.getBsonTypeEnum();
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

    // --------------------------JDBC 2.0-----------------------------------
    @Override
    public String getColumnClassName(int column) throws SQLException {
        String intClassName = int.class.getName();
        String booleanClassName = boolean.class.getName();
        String stringClassName = String.class.getName();
        String floatClassName = float.class.getName();
        String doubleClassName = double.class.getName();
        String bigDecimalClassName = BigDecimal.class.getName();
        String timestampClassName = Timestamp.class.getName();
        String bsonClassName = BsonValue.class.getName();

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
                return bsonClassName;
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
}
