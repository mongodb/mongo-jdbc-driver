package com.mongodb.jdbc.integration.testharness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class IntegrationTestUtils {
    public static int countRows(ResultSet rs) throws SQLException {
        for (int i = 0; ; ++i) {
            if (!rs.next()) {
                return i;
            }
        }
    }

    private static Yaml yaml = new Yaml(new Constructor(Tests.class));

    /**
     * loadTestConfigs will process all test yaml files in provided directory.
     *
     * @param directory The directory with test files to traverse
     * @return A List of TestEntry
     * @throws FileNotFoundException
     */
    public static List<TestEntry> loadTestConfigs(String directory) throws IOException {
        List<Tests> tests = new ArrayList<>();
        List<TestEntry> testEntries = new ArrayList<>();
        final File folder = new File(directory);
        if (!folder.exists()) {
            return testEntries;
        }
        processDirectory(folder, tests);

        for (Tests testList : tests) {
            testEntries.addAll(testList.tests);
        }
        return testEntries;
    }

    // processDirectory will traverse the subdirectories of 'folder'.
    // Useful to group related test files in a directory
    private static void processDirectory(final File folder, List<Tests> tests) throws IOException {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                processDirectory(fileEntry, tests);
            } else {
                tests.add(processTestFile(fileEntry.getPath()));
            }
        }
    }

    private static Tests processTestFile(String filename) throws IOException {
        try (InputStream is = new FileInputStream(filename)) {
            return yaml.load(is);
        }
    }

    /**
     * runTest will execute the passed in testEntry. If generate is 'true' then baseline files will
     * be generated from the test configuration
     *
     * @param testEntry Test to run
     * @param conn Database connection
     * @param generate Generate Baseline test files instead of running test
     * @throws IOException
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void runTest(TestEntry testEntry, Connection conn, Boolean generate)
            throws IOException, SQLException, IllegalAccessException, InvocationTargetException {
        ResultSet rs = null;
        // Run DatabaseMetadata function if it exists, takes precedence over query
        System.out.println("Running test: " + testEntry.description);
        try {
            if (testEntry.meta_function == null) {
                rs = executeQuery(testEntry, conn);
            } else {
                rs = executeDBMetadataCommand(testEntry, conn.getMetaData());
            }
            assertNotEquals(rs, null);
            if (generate) {
                TestGenerator.generateBaselineTestFiles(testEntry, rs);
            } else {
                if (testEntry.ordered != null && testEntry.ordered) {
                    validateResultsOrdered(testEntry, rs);
                } else {
                    validateResultsUnordered(testEntry, rs);
                }
                validateResultSetMetadata(testEntry, rs.getMetaData());
            }
        } finally {
            if (rs != null) {
                Statement statement = rs.getStatement();
                if (statement != null) {
                    statement.close();
                }
            }
        }
    }

    private static ResultSet executeQuery(TestEntry entry, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(entry.sql);
    }

    @SuppressWarnings("unchecked")
    private static ResultSet executeDBMetadataCommand(
            TestEntry entry, DatabaseMetaData databaseMetaData)
            throws SQLException, InvocationTargetException, IllegalAccessException {

        List<Object> metadataFunction = entry.meta_function;
        assertTrue(
                metadataFunction != null && metadataFunction.size() > 0,
                "expected a DatabaseMetaData method but found none");
        String functionName = (String) metadataFunction.get(0);
        Method[] m = DatabaseMetaData.class.getMethods();

        RuntimeException possibleException = null;
        for (Method method : m) {
            if (method.getName().equalsIgnoreCase(functionName)) {
                Class<?>[] types = method.getParameterTypes();
                if (method.getReturnType() != ResultSet.class) {
                    throw new IllegalArgumentException(
                            "expected: "
                                    + ResultSet.class.getName()
                                    + " found: "
                                    + method.getReturnType());
                }
                if (method.getParameterCount() != metadataFunction.size() - 1) {
                    // Some methods may be overloaded. For such methods, we do not
                    // want to throw immediately when we encounter an incorrect
                    // argument count; instead we store the error and throw at the end
                    possibleException =
                            new IllegalArgumentException(
                                    "expected parameter count: "
                                            + method.getParameterCount()
                                            + " found: "
                                            + (metadataFunction.size() - 1));
                    continue;
                }
                if (method.getParameterCount() == 0) {
                    return (ResultSet) method.invoke(databaseMetaData);
                }
                Object[] parameters = new Object[method.getParameterCount()];

                for (int i = 0; i < parameters.length; i++) {
                    Object currentMetaInput = metadataFunction.get(i + 1);
                    if (currentMetaInput == null) {
                        parameters[i] = null;
                        continue;
                    }
                    Class<?> parameterType = types[i];
                    if (parameterType.isArray()) {
                        if (parameterType.getComponentType() == String.class) {
                            parameters[i] =
                                    ((List<String>) currentMetaInput)
                                            .stream()
                                            .map(object -> Objects.toString(object, null))
                                            .toArray(String[]::new);
                        } else if (parameterType.getComponentType() == int.class) {
                            parameters[i] =
                                    ((List<Integer>) currentMetaInput)
                                            .stream()
                                            .mapToInt(j -> j)
                                            .toArray();
                        }
                    } else if (parameterType == String.class) {
                        parameters[i] = (String) currentMetaInput;
                    } else if (parameterType == int.class) {
                        parameters[i] = (Integer) currentMetaInput;
                    } else if (parameterType == boolean.class) {
                        parameters[i] = (Boolean) currentMetaInput;
                    }
                }
                return (ResultSet) method.invoke(databaseMetaData, parameters);
            }
        }

        if (possibleException != null) {
            throw possibleException;
        }

        throw new IllegalArgumentException("function '" + functionName + "' not found");
    }

    private static void validateResultSetMetadata(TestEntry test, ResultSetMetaData rsMetaData)
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
    private static void validateResultsOrdered(TestEntry testEntry, ResultSet rs)
            throws SQLException {
        Integer actualRowCounter = null;
        if (testEntry.expected_result != null) {
            actualRowCounter = 0;
            while (rs.next()) {
                assertTrue(
                        compareRow(
                                (List<Object>) testEntry.expected_result.get(actualRowCounter),
                                rs));
                actualRowCounter++;
            }
        }
        if (testEntry.row_count != null) {
            validateRowCount(testEntry, actualRowCounter, rs);
        }
    }

    @SuppressWarnings("unchecked")
    private static void validateResultsUnordered(TestEntry testEntry, ResultSet rs)
            throws SQLException {
        Integer actualRowCounter = null;
        if (testEntry.expected_result != null) {
            actualRowCounter = 0;
            while (rs.next()) {
                boolean found = false;
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
            validateRowCount(testEntry, actualRowCounter, rs);
        }
    }

    private static void validateRowCount(
            TestEntry testEntry, Integer actualRowCounter, ResultSet rs) throws SQLException {
        if (actualRowCounter == null) {
            actualRowCounter = IntegrationTestUtils.countRows(rs);
        }
        if (testEntry.row_count_gte != null && testEntry.row_count_gte) {
            assertTrue(actualRowCounter >= testEntry.row_count);
        } else {
            assertEquals(actualRowCounter, testEntry.row_count);
        }
    }

    private static boolean compareRow(List<Object> expectedRow, ResultSet actualRow)
            throws SQLException {
        ResultSetMetaData rsMetadata = actualRow.getMetaData();
        assertEquals(expectedRow.size(), rsMetadata.getColumnCount());

        for (int i = 0; i < expectedRow.size(); i++) {
            // Handle expected field being null
            if (expectedRow.get(i) == null) {
                if (actualRow.getObject(i + 1) == null) {
                    continue;
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
                case Types.BIT:
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
                    // TODO: SQL-632 Support Types.OTHER
                    // This comparison needs to be improved to correctly handle Types.OTHER
                    /*
                    case Types.OTHER:
                        if (expectedRow.get(i) != actualRow.getObject(i + 1)) {
                            return false;
                        }
                     */
                default:
                    throw new IllegalArgumentException("unsupported column type:" + columnType);
            }
        }
        return true;
    }
}
