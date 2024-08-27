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

package com.mongodb.jdbc.integration.testharness;

import com.mongodb.jdbc.MongoResultSetMetaData;
import com.mongodb.jdbc.integration.ADFIntegrationTest;
import com.mongodb.jdbc.integration.testharness.models.TestEntry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class TestGenerator {
    private static final String GENERATED_TEST_DIR = "resources/generated_test";

    public static void generateBaselineTestFiles(TestEntry testEntry, ResultSet rs)
            throws IOException, SQLException, IllegalAccessException {

        String description = testEntry.description.replace(' ', '_');
        List<String> expectedSqlType = new ArrayList<>();
        List<String> expectedBsonType = new ArrayList();
        List<String> expectedCatalogName = new ArrayList<>();
        List<String> expectedColumnClassName = new ArrayList<>();
        List<String> expectedColumnLabel = new ArrayList<>();
        List<String> expectedIsNullable = new ArrayList<>();
        List<String> expectedSchemaName = new ArrayList<>();
        List<Integer> expectedColumnDisplaySize = new ArrayList<>();
        List<Integer> expectedPrecision = new ArrayList<>();
        List<Integer> expectedScale = new ArrayList<>();
        List<Boolean> expectedIsAutoIncrement = new ArrayList<>();
        List<Boolean> expectedIsCaseSensitive = new ArrayList<>();
        List<Boolean> expectedIsCurrency = new ArrayList<>();
        List<Boolean> expectedIsDefinitelyWritable = new ArrayList<>();
        List<Boolean> expectedIsReadOnly = new ArrayList<>();
        List<Boolean> expectedIsSearchable = new ArrayList<>();
        List<Boolean> expectedIsSigned = new ArrayList<>();
        List<Boolean> expectedIsWritable = new ArrayList<>();

        File directory = new File(GENERATED_TEST_DIR);
        ResultSetMetaData resultSetMetadata = rs.getMetaData();
        MongoResultSetMetaData mongoResultSetMetaData = (MongoResultSetMetaData) resultSetMetadata;
        Map<String, Object> tests = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> testCases = new ArrayList<>();
        Map<String, Object> testCase = new LinkedHashMap<String, Object>();
        testCases.add(testCase);
        tests.put("tests", testCases);

        if (!directory.exists()) {
            directory.mkdir();
        }

        String fileName =
                new SimpleDateFormat("'" + description + "'MMddHHmmss'.yaml'").format(new Date());
        ArrayList<List<Object>> result = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= resultSetMetadata.getColumnCount(); i++) {
                row.add(rs.getObject(i));
            }
            result.add(row);
        }

        // generating expected resultset metadata
        for (int i = 1; i <= resultSetMetadata.getColumnCount(); i++) {
            expectedSqlType.add(TestTypeInfo.typesIntToString(resultSetMetadata.getColumnType(i)));
            expectedCatalogName.add(resultSetMetadata.getCatalogName(i));
            expectedColumnClassName.add(resultSetMetadata.getColumnClassName(i));
            expectedColumnLabel.add(resultSetMetadata.getColumnLabel(i));
            expectedColumnDisplaySize.add(resultSetMetadata.getColumnDisplaySize(i));
            expectedPrecision.add(resultSetMetadata.getPrecision(i));
            expectedScale.add(resultSetMetadata.getScale(i));
            expectedSchemaName.add(resultSetMetadata.getSchemaName(i));
            expectedIsAutoIncrement.add(resultSetMetadata.isAutoIncrement(i));
            expectedIsCaseSensitive.add(resultSetMetadata.isCaseSensitive(i));
            expectedIsCurrency.add(resultSetMetadata.isCurrency(i));
            expectedIsDefinitelyWritable.add(resultSetMetadata.isDefinitelyWritable(i));
            expectedIsNullable.add(
                    TestTypeInfo.nullableIntToString(resultSetMetadata.isNullable(i)));
            expectedIsReadOnly.add(resultSetMetadata.isReadOnly(i));
            expectedIsSearchable.add(resultSetMetadata.isSearchable(i));
            expectedIsSigned.add(resultSetMetadata.isSigned(i));
            expectedIsWritable.add(resultSetMetadata.isWritable(i));
            expectedBsonType.add(mongoResultSetMetaData.getColumnInfo(i).getBsonTypeName());
        }

        testCase.put("description", description);
        testCase.put("db", testEntry.db);
        if (testEntry.meta_function != null) {
            testCase.put("meta_function", testEntry.meta_function);
        } else {
            testCase.put("sql", testEntry.sql);
        }
        testCase.put("expected_result", result);
        testCase.put("row_count", result.size());
        testCase.put("row_count_gte", testEntry.row_count_gte);
        testCase.put("ordered", testEntry.ordered);

        testCase.put("expected_sql_type", expectedSqlType);
        testCase.put("expected_bson_type", expectedBsonType);
        testCase.put("expected_catalog_name", expectedCatalogName);
        testCase.put("expected_column_class_name", expectedColumnClassName);
        testCase.put("expected_column_label", expectedColumnLabel);
        testCase.put("expected_column_display_size", expectedColumnDisplaySize);
        testCase.put("expected_precision", expectedPrecision);
        testCase.put("expected_scale", expectedScale);
        testCase.put("expected_schema_name", expectedSchemaName);
        testCase.put("expected_is_auto_increment", expectedIsAutoIncrement);
        testCase.put("expected_is_case_sensitive", expectedIsCaseSensitive);
        testCase.put("expected_is_currency", expectedIsCurrency);
        testCase.put("expected_is_definitely_writable", expectedIsDefinitelyWritable);
        testCase.put("expected_is_nullable", expectedIsNullable);
        testCase.put("expected_is_read_only", expectedIsReadOnly);
        testCase.put("expected_is_searchable", expectedIsSearchable);
        testCase.put("expected_is_signed", expectedIsSigned);
        testCase.put("expected_is_writable", expectedIsWritable);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setIndicatorIndent(2);
        options.setIndentWithIndicator(true);
        Yaml yaml = new Yaml(options);
        FileWriter writer = new FileWriter(GENERATED_TEST_DIR + "/" + fileName);
        yaml.dump(tests, writer);
    }

    public static void main(String[] args)
            throws SQLException, IOException, InvocationTargetException, IllegalAccessException {
        ADFIntegrationTest integrationTest = new ADFIntegrationTest();
        List<TestEntry> tests =
                IntegrationTestUtils.loadTestConfigs(ADFIntegrationTest.TEST_DIRECTORY);
        for (TestEntry testEntry : tests) {
            try (Connection conn = integrationTest.getBasicConnection(testEntry.db, null)) {
                if (testEntry.skip_reason != null) {
                    continue;
                }
                IntegrationTestUtils.runTest(testEntry, conn, true);
            }
        }
    }
}
