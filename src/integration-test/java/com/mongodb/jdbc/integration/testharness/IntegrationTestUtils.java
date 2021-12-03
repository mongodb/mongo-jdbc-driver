package com.mongodb.jdbc.integration.testharness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.jdbc.integration.MongoIntegrationTest;
import com.mongodb.jdbc.integration.testharness.models.TestEntry;
import com.mongodb.jdbc.integration.testharness.models.Tests;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class IntegrationTestUtils {

    private static Yaml yaml = new Yaml(new Constructor(Tests.class));

    /**
     * loadTestConfigs will process all test yaml files in provided directory.
     *
     * @param directory The directory with test files to traverse
     * @return A List of Tests
     * @throws FileNotFoundException
     */
    public List<Tests> loadTestConfigs(String directory) throws IOException {
        List<Tests> tests = new ArrayList<>();
        final File folder = new File(directory);
        if (!folder.exists()) {
            return tests;
        }
        processDirectory(folder, tests);
        return tests;
    }

    // processDirectory will traverse the subdirectories of 'folder'.
    // Useful to group related test files in a directory
    private void processDirectory(final File folder, List<Tests> tests) throws IOException {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                processDirectory(fileEntry, tests);
            } else {
                tests.add(processTestFile(fileEntry.getPath()));
            }
        }
    }

    private Tests processTestFile(String filename) throws IOException {
        try (InputStream is = new FileInputStream(filename)) {
            return yaml.load(is);
        }
    }

    /**
     * runTests will execute the tests that match the pattern. If generate is 'true' then baseline
     * files will be generated from the test configurations
     *
     * @param testLists List of tests to run
     * @param conn Database connection
     * @param generate Generate Baseline test files instead of running test
     * @param pattern Pattern to match on the test description, matching tests will be run
     * @throws IOException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void runTests(List<Tests> testLists, Connection conn, Boolean generate, String pattern)
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        Pattern p = Pattern.compile(pattern.toUpperCase());
        for (Tests tests : testLists) {
            for (TestEntry testEntry : tests.tests) {
                if (testEntry.skip_reason != null
                        || !p.matcher(testEntry.description.toUpperCase()).matches()) {
                    continue;
                }
                ResultSet rs = null;
                // Run DatabaseMetadata function if it exists, takes precedence over query
                System.out.println("Running test: " + testEntry.description);
                if (testEntry.meta_function == null) {
                    rs = executeQuery(testEntry, conn);
                } else {
                    rs = executeDBMetadataCommand(testEntry, conn.getMetaData());
                }
                assertNotEquals(rs, null);
                if (generate) {
                    TestGenerator.generateBaselineTestFiles(testEntry.description, rs);
                } else {
                    if (testEntry.ordered != null && testEntry.ordered) {
                        validateResultsOrdered(testEntry, rs);
                    } else {
                        validateResultsUnordered(testEntry, rs);
                    }
                    validateResultSetMetadata(testEntry, rs.getMetaData());
                }
            }
        }
    }

    private ResultSet executeQuery(TestEntry entry, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(entry.sql);
    }

    private ResultSet executeDBMetadataCommand(TestEntry entry, DatabaseMetaData databaseMetaData)
            throws SQLException, InvocationTargetException, IllegalAccessException {

        List<Object> metadataFunction = entry.meta_function;
        assertTrue(metadataFunction != null && metadataFunction.size() > 0);
        String functionName = (String) metadataFunction.remove(0);
        switch (functionName.toUpperCase()) {
            case "GETINDEXINFO":
                return executeGetIndexInfo(metadataFunction, databaseMetaData);
            case "GETBESTROWIDENTIFIER":
                return executeGetBestRowIdentifier(metadataFunction, databaseMetaData);
            case "GETTABLES":
                return executeGetTables(metadataFunction, databaseMetaData);
            case "GETUDTS":
                return executeGetUDTs(metadataFunction, databaseMetaData);
            default:
                return executeDBMetadataHelper(functionName, metadataFunction, databaseMetaData);
        }
    }

    // Can process methods that have String only arguments or no arguments
    private ResultSet executeDBMetadataHelper(
            String functionName, List<Object> functionArgs, DatabaseMetaData databaseMetaData)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        Method[] m = DatabaseMetaData.class.getMethods();
        for (Method method : m) {
            if (method.getName().equalsIgnoreCase(functionName)) {
                Class<?>[] types = method.getParameterTypes();
                // Verifying all String types
                for (Class<?> type : types) {
                    if (type != String.class) {
                        throw new IllegalArgumentException(
                                "expected: "
                                        + String.class.getName()
                                        + " found: "
                                        + type.getName());
                    }
                }
                if (method.getReturnType() != ResultSet.class) {
                    throw new IllegalArgumentException(
                            "expected: "
                                    + ResultSet.class.getName()
                                    + " found: "
                                    + method.getReturnType());
                }
                if (method.getParameterCount() == 0) {
                    return (ResultSet) method.invoke(databaseMetaData);

                } else {
                    List<String> stringFunctionArgs =
                            functionArgs
                                    .stream()
                                    .map(object -> Objects.toString(object, null))
                                    .collect(Collectors.toList());

                    return (ResultSet)
                            method.invoke(
                                    databaseMetaData,
                                    (Object[]) stringFunctionArgs.toArray(new String[0]));
                }
            }
        }
        throw new IllegalArgumentException("function '" + functionName + "' not found");
    }

    private ResultSet executeGetIndexInfo(
            List<Object> functionArgs, DatabaseMetaData databaseMetaData) throws SQLException {
        int expectedColumnCount = 5;
        if (functionArgs.size() != expectedColumnCount) {
            throw new IllegalArgumentException(
                    "incorrect argument size, expected: "
                            + expectedColumnCount
                            + " found: "
                            + functionArgs.size());
        }
        return databaseMetaData.getIndexInfo(
                (String) functionArgs.get(0),
                (String) functionArgs.get(1),
                (String) functionArgs.get(2),
                ((Boolean) functionArgs.get(3)),
                ((Boolean) functionArgs.get(4)));
    }

    private ResultSet executeGetBestRowIdentifier(
            List<Object> functionArgs, DatabaseMetaData databaseMetaData) throws SQLException {
        int expectedColumnCount = 5;
        if (functionArgs.size() != expectedColumnCount) {
            throw new IllegalArgumentException(
                    "incorrect argument size, expected: "
                            + expectedColumnCount
                            + " found: "
                            + functionArgs.size());
        }
        return databaseMetaData.getBestRowIdentifier(
                (String) functionArgs.get(0),
                (String) functionArgs.get(1),
                (String) functionArgs.get(2),
                ((Integer) functionArgs.get(3)),
                ((Boolean) functionArgs.get(4)));
    }

    @SuppressWarnings("unchecked")
    private ResultSet executeGetTables(List<Object> functionArgs, DatabaseMetaData databaseMetaData)
            throws SQLException {
        int expectedColumnCount = 4;
        if (functionArgs.size() != expectedColumnCount) {
            throw new IllegalArgumentException(
                    "incorrect argument size, expected: "
                            + expectedColumnCount
                            + " found: "
                            + functionArgs.size());
        }
        return databaseMetaData.getTables(
                (String) functionArgs.get(0),
                (String) functionArgs.get(1),
                (String) functionArgs.get(2),
                functionArgs.get(3) == null
                        ? null
                        : ((List<String>) functionArgs.get(3)).toArray(new String[0]));
    }

    @SuppressWarnings("unchecked")
    private ResultSet executeGetUDTs(List<Object> functionArgs, DatabaseMetaData databaseMetaData)
            throws SQLException {
        int expectedColumnCount = 5;
        if (functionArgs.size() != expectedColumnCount) {
            throw new IllegalArgumentException(
                    "incorrect argument size, expected: "
                            + expectedColumnCount
                            + " found: "
                            + functionArgs.size());
        }
        int[] types =
                functionArgs.get(3) == null
                        ? null
                        : ((List<Integer>) functionArgs.get(3)).stream().mapToInt(i -> i).toArray();
        return databaseMetaData.getUDTs(
                (String) functionArgs.get(0),
                (String) functionArgs.get(1),
                (String) functionArgs.get(2),
                types);
    }

    private void validateResultSetMetadata(TestEntry test, ResultSetMetaData rsMetaData)
            throws SQLException, IllegalAccessException {
        int columnCount = rsMetaData.getColumnCount();
        if (test.expected_sql_type != null) {
            assertEquals(test.expected_sql_type.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                int sqlType = TestTypeInfo.typesStringToInt(test.expected_sql_type.get(i));
                assertEquals(sqlType, rsMetaData.getColumnType(i + 1));
            }
        }
        if (test.expected_catalog_name != null) {
            assertEquals(test.expected_catalog_name.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_catalog_name.get(i), rsMetaData.getCatalogName(i + 1));
            }
        }
        if (test.expected_column_class_name != null) {
            assertEquals(test.expected_column_class_name.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_column_class_name.get(i),
                        rsMetaData.getColumnClassName(i + 1));
            }
        }
        if (test.expected_column_display_size != null) {
            assertEquals(test.expected_column_display_size.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_column_display_size.get(i).intValue(),
                        rsMetaData.getColumnDisplaySize(i + 1));
            }
        }
        if (test.expected_column_label != null) {
            assertEquals(test.expected_column_label.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_column_label.get(i), rsMetaData.getColumnLabel(i + 1));
            }
        }
        if (test.expected_precision != null) {
            assertEquals(test.expected_precision.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_precision.get(i).intValue(), rsMetaData.getPrecision(i + 1));
            }
        }
        if (test.expected_scale != null) {
            assertEquals(test.expected_scale.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_scale.get(i).intValue(), rsMetaData.getScale(i + 1));
            }
        }
        if (test.expected_schema_name != null) {
            assertEquals(test.expected_schema_name.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_schema_name.get(i), rsMetaData.getSchemaName(i + 1));
            }
        }
        if (test.expected_is_auto_increment != null) {
            assertEquals(test.expected_is_auto_increment.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_auto_increment.get(i), rsMetaData.isAutoIncrement(i + 1));
            }
        }
        if (test.expected_is_case_sensitive != null) {
            assertEquals(test.expected_is_case_sensitive.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_case_sensitive.get(i), rsMetaData.isCaseSensitive(i + 1));
            }
        }
        if (test.expected_is_currency != null) {
            assertEquals(test.expected_is_currency.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_is_currency.get(i), rsMetaData.isCurrency(i + 1));
            }
        }
        if (test.expected_is_definitely_writable != null) {
            assertEquals(test.expected_is_definitely_writable.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_definitely_writable.get(i),
                        rsMetaData.isDefinitelyWritable(i + 1));
            }
        }
        if (test.expected_is_nullable != null) {
            assertEquals(test.expected_is_nullable.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                int expectedNullable =
                        TestTypeInfo.nullableStringToInt(test.expected_is_nullable.get(i));
                assertEquals(expectedNullable, rsMetaData.isNullable(i + 1));
            }
        }
        if (test.expected_is_read_only != null) {
            assertEquals(test.expected_is_read_only.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_is_read_only.get(i), rsMetaData.isReadOnly(i + 1));
            }
        }
        if (test.expected_is_searchable != null) {
            assertEquals(test.expected_is_searchable.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_is_searchable.get(i), rsMetaData.isSearchable(i + 1));
            }
        }
        if (test.expected_is_signed != null) {
            assertEquals(test.expected_is_signed.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_is_signed.get(i), rsMetaData.isSigned(i + 1));
            }
        }
        if (test.expected_is_writable != null) {
            assertEquals(test.expected_is_writable.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                assertEquals(test.expected_is_writable.get(i), rsMetaData.isWritable(i + 1));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validateResultsOrdered(TestEntry testEntry, ResultSet rs) throws SQLException {
        Integer actualRowCounter = null;
        if (testEntry.expected_result != null) {
            while (rs.next()) {
                if (actualRowCounter == null) {
                    actualRowCounter = 0;
                }
                assertTrue(
                        compareRow(
                                (List<Object>) testEntry.expected_result.get(actualRowCounter),
                                rs));
                actualRowCounter++;
            }
        }
        if (testEntry.row_count != null) {
            validateRowCount(testEntry.row_count, testEntry, actualRowCounter, rs);
        }
    }

    @SuppressWarnings("unchecked")
    private void validateResultsUnordered(TestEntry testEntry, ResultSet rs) throws SQLException {
        Integer actualRowCounter = null;
        if (testEntry.expected_result != null) {
            while (rs.next()) {
                boolean found = false;
                if (actualRowCounter == null) {
                    actualRowCounter = 0;
                }
                actualRowCounter++;
                for (Object expectedRow : testEntry.expected_result) {
                    if (compareRow((List<Object>) expectedRow, rs)) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
        }
        if (testEntry.row_count != null) {
            validateRowCount(testEntry.row_count, testEntry, actualRowCounter, rs);
        }
    }

    private void validateRowCount(
            int expectedRowCount, TestEntry testEntry, Integer actualRowCounter, ResultSet rs)
            throws SQLException {
        if (actualRowCounter == null) {
            actualRowCounter = MongoIntegrationTest.countRows(rs);
        }
        if (testEntry.rowcount_gte != null && testEntry.rowcount_gte) {
            assertTrue(actualRowCounter >= expectedRowCount);
        } else {
            assertEquals(java.util.Optional.ofNullable(actualRowCounter), expectedRowCount);
        }
    }

    private boolean compareRow(List<Object> expectedRow, ResultSet actualRow) throws SQLException {
        ResultSetMetaData rsMetadata = actualRow.getMetaData();
        assertEquals(expectedRow.size(), rsMetadata.getColumnCount());

        for (int i = 0; i < expectedRow.size(); i++) {
            // Handle expected field being null
            if (expectedRow.get(i) == null) {
                if (actualRow.getObject(i + 1) == null) {
                    break;
                } else {
                    return false;
                }
            }

            int columnType = rsMetadata.getColumnType(i + 1);
            switch (columnType) {
                case Types.BIGINT:
                case Types.SMALLINT:
                case Types.TINYINT:
                case Types.INTEGER:
                    if ((((Integer) expectedRow.get(i)) != actualRow.getInt(i + 1))) {
                        return false;
                    }
                    break;
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                case Types.NCHAR:
                case Types.CHAR:
                case Types.NVARCHAR:
                case Types.VARCHAR:
                    if (!((String) expectedRow.get(i)).equals(actualRow.getString(i + 1))) {
                        return false;
                    }
                    break;
                case Types.BOOLEAN:
                    if (((Boolean) expectedRow.get(i)) != actualRow.getBoolean(i + 1)) {
                        return false;
                    }
                    break;
                case Types.DOUBLE:
                    if ((double) expectedRow.get(i) != actualRow.getDouble(i + 1)) {
                        return false;
                    }
                    break;
                case Types.NULL:
                    if (expectedRow.get(i) != actualRow.getObject(i + 1)) {
                        return false;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unsupported column type:" + columnType);
            }
        }
        return true;
    }
}
