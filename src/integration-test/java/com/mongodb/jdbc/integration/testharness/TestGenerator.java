package com.mongodb.jdbc.integration.testharness;

import com.mongodb.jdbc.integration.MongoIntegrationTest;
import com.mongodb.jdbc.integration.MongoSQLIntegrationTest;
import com.mongodb.jdbc.integration.testharness.models.Tests;
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

    public static void generateBaselineTestFiles(String description, ResultSet rs)
            throws IOException, SQLException, IllegalAccessException {

        List<String> expectedSqlType = new ArrayList<>();
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

        // generating 'expected_result'
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
        }

        testCase.put("description", description);
        testCase.put("expected_result", result);
        testCase.put("expected_sql_type", expectedSqlType);
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
        IntegrationTestUtils utils = new IntegrationTestUtils();

        // Pattern to generate all tests if argument not provided
        String testPattern = ".*";
        Connection conn = MongoIntegrationTest.getBasicConnection(MongoSQLIntegrationTest.MONGOSQL);
        if (args.length > 0) {
            testPattern = args[0];
        }
        List<Tests> tests = utils.loadTestConfigs(MongoSQLIntegrationTest.TEST_DIRECTORY);
        utils.runTests(tests, conn, true, testPattern);
    }
}
