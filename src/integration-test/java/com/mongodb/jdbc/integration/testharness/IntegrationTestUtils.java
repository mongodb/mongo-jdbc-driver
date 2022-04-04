package com.mongodb.jdbc.integration.testharness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.mongodb.jdbc.ExtJsonValue;
import com.mongodb.jdbc.MongoColumnInfo;
import com.mongodb.jdbc.MongoSQLResultSetMetaData;
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonValue;
import org.bson.Document;
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
        } catch (Exception e) {
            System.err.println("Error processing " + filename);
            throw e;
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

    public static void validateResultSetMetadata(TestEntry test, ResultSetMetaData rsMetaData)
            throws SQLException, IllegalAccessException {
        int columnCount = rsMetaData.getColumnCount();
        if (test.expected_sql_type != null) {
            assertEquals(
                    test.expected_sql_type.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_sql_type' or the data"
                            + " in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                int sqlType = TestTypeInfo.typesStringToInt(test.expected_sql_type.get(i));
                assertEquals(
                        sqlType,
                        rsMetaData.getColumnType(i + 1),
                        "Invalid getColumnType result for column " + (i + 1));
            }
        }
        if (test.expected_catalog_name != null) {
            assertEquals(
                    test.expected_catalog_name.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_catalog_name' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_catalog_name.get(i),
                        rsMetaData.getCatalogName(i + 1),
                        "Invalid getCatalogName result for column " + (i + 1));
            }
        }
        if (test.expected_column_class_name != null) {
            assertEquals(
                    test.expected_column_class_name.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_column_class_name' or"
                            + " the data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_column_class_name.get(i),
                        rsMetaData.getColumnClassName(i + 1),
                        "Invalid getColumnClassName result for column " + (i + 1));
            }
        }
        if (test.expected_column_display_size != null) {
            assertEquals(
                    test.expected_column_display_size.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_column_display_size'"
                            + " or the data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_column_display_size.get(i).intValue(),
                        rsMetaData.getColumnDisplaySize(i + 1),
                        "Invalid getColumnDisplaySize result for column " + (i + 1));
            }
        }
        if (test.expected_column_label != null) {
            assertEquals(
                    test.expected_column_label.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_column_label' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_column_label.get(i),
                        rsMetaData.getColumnLabel(i + 1),
                        "Invalid getColumnLabel result for column " + (i + 1));
            }
        }
        if (test.expected_precision != null) {
            assertEquals(
                    test.expected_precision.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_precision' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_precision.get(i).intValue(),
                        rsMetaData.getPrecision(i + 1),
                        "Invalid getPrecision result for column " + (i + 1));
            }
        }
        if (test.expected_scale != null) {
            assertEquals(
                    test.expected_scale.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_scale' or the data"
                            + " in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_scale.get(i).intValue(),
                        rsMetaData.getScale(i + 1),
                        "Invalid getScale result for column " + (i + 1));
            }
        }
        if (test.expected_schema_name != null) {
            assertEquals(
                    test.expected_schema_name.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_schema_name' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_schema_name.get(i),
                        rsMetaData.getSchemaName(i + 1),
                        "Invalid getSchemaName result for column " + (i + 1));
            }
        }
        if (test.expected_is_auto_increment != null) {
            assertEquals(
                    test.expected_is_auto_increment.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_auto_increment' or"
                            + " the data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_auto_increment.get(i),
                        rsMetaData.isAutoIncrement(i + 1),
                        "Invalid isAutoIncrement result for column " + (i + 1));
            }
        }
        if (test.expected_is_case_sensitive != null) {
            assertEquals(
                    test.expected_is_case_sensitive.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_case_sensitive' or"
                            + " the data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_case_sensitive.get(i),
                        rsMetaData.isCaseSensitive(i + 1),
                        "Invalid isCaseSensitive result for column " + (i + 1));
            }
        }
        if (test.expected_is_currency != null) {
            assertEquals(
                    test.expected_is_currency.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_currency' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_currency.get(i),
                        rsMetaData.isCurrency(i + 1),
                        "Invalid isCurrency result for column " + (i + 1));
            }
        }
        if (test.expected_is_definitely_writable != null) {
            assertEquals(
                    test.expected_is_definitely_writable.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_definitely_writable'"
                            + " or the data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_definitely_writable.get(i),
                        rsMetaData.isDefinitelyWritable(i + 1),
                        "Invalid isDefinitelyWritable result for column " + (i + 1));
            }
        }
        if (test.expected_is_nullable != null) {
            assertEquals(
                    test.expected_is_nullable.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_nullable' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                int expectedNullable =
                        TestTypeInfo.nullableStringToInt(test.expected_is_nullable.get(i));
                assertEquals(
                        expectedNullable,
                        rsMetaData.isNullable(i + 1),
                        "Invalid isNullable result for column " + (i + 1));
            }
        }
        if (test.expected_is_read_only != null) {
            assertEquals(
                    test.expected_is_read_only.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_read_only' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_read_only.get(i),
                        rsMetaData.isReadOnly(i + 1),
                        "Invalid isReadOnly result for column " + (i + 1));
            }
        }
        if (test.expected_is_searchable != null) {
            assertEquals(
                    test.expected_is_searchable.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_searchable' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_searchable.get(i),
                        rsMetaData.isSearchable(i + 1),
                        "Invalid isSearchable result for column " + (i + 1));
            }
        }
        if (test.expected_is_signed != null) {
            assertEquals(
                    test.expected_is_signed.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_signed' or the data"
                            + " in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_signed.get(i),
                        rsMetaData.isSigned(i + 1),
                        "Invalid isSigned result for column " + (i + 1));
            }
        }
        if (test.expected_is_writable != null) {
            assertEquals(
                    test.expected_is_writable.size(),
                    columnCount,
                    "Either the yml test specification is missing entries for 'expected_is_writable' or the"
                            + " data in the DB is not matching the test");
            for (int i = 0; i < columnCount; i++) {
                assertEquals(
                        test.expected_is_writable.get(i),
                        rsMetaData.isWritable(i + 1),
                        "Invalid isWritable result for column " + (i + 1));
            }
        }
        if (test.expected_bson_type != null) {
            assertEquals(test.expected_bson_type.size(), columnCount);
            for (int i = 0; i < columnCount; i++) {
                MongoSQLResultSetMetaData mongoSQLResultSetMetadata =
                        (MongoSQLResultSetMetaData) rsMetaData;
                MongoColumnInfo columnInfo = mongoSQLResultSetMetadata.getColumnInfo(i + 1);
                assertEquals(
                        test.expected_bson_type.get(i),
                        columnInfo.getBsonTypeName(),
                        "Bson type mismatch");
            }
        }
    }

    private static List<Object> processExtendedJson(Map<String, Object> entry) {
        List<Object> expectedResults = new ArrayList<>();
        String entryAsJson = new Document(entry).toJson();
        BsonDocument bsonDoc = BsonDocument.parse(entryAsJson);
        for (int i = 0; i < entry.size(); i++) {
            expectedResults.add(bsonDoc.get(String.valueOf(i)));
        }
        return expectedResults;
    }

    @SuppressWarnings("unchecked")
    private static void validateResultsOrdered(TestEntry testEntry, ResultSet rs)
            throws SQLException {
        Integer actualRowCounter = null;
        List<Object> expectedResults = null;
        if (testEntry.expected_result_extended_json != null || testEntry.expected_result != null) {
            actualRowCounter = 0;
            while (rs.next()) {
                if (testEntry.expected_result_extended_json != null) {
                    assertTrue(
                            testEntry.expected_result_extended_json.size() > actualRowCounter,
                            "Database returned more rows than the expected amount: "
                                    + testEntry.expected_result_extended_json.size());
                    expectedResults =
                            processExtendedJson(
                                    testEntry.expected_result_extended_json.get(actualRowCounter));
                } else {
                    assertTrue(
                            testEntry.expected_result.size() > actualRowCounter,
                            "Database returned more rows than the expected amount: "
                                    + testEntry.expected_result.size());
                    expectedResults =
                            (List<Object>) testEntry.expected_result.get(actualRowCounter);
                }

                String compOutcome = compareRow(expectedResults, rs);
                if (compOutcome != null) {
                    fail("Row " + actualRowCounter + " does not match. " + compOutcome);
                }
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
        List<Object> expectedResults = null;
        if (testEntry.expected_result_extended_json != null || testEntry.expected_result != null) {
            actualRowCounter = 0;
            while (rs.next()) {
                actualRowCounter++;
                boolean found = false;
                if (testEntry.expected_result_extended_json != null) {
                    for (Map<String, Object> entry : testEntry.expected_result_extended_json) {
                        if (compareRow(processExtendedJson(entry), rs) == null) {
                            found = true;
                            break;
                        }
                    }
                } else {
                    for (Object expectedRow : testEntry.expected_result) {
                        if (compareRow((List<Object>) expectedRow, rs) == null) {
                            found = true;
                            break;
                        }
                    }
                }
                assertTrue(found, "Invalid row " + actualRowCounter + ". No match found.");
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

    /**
     * Compare 2 rows and returns either null is the rows are identical or a message describing the
     * difference.
     *
     * @param expectedRow The expected row content.
     * @param actualRow The actual row content.
     * @return either null is the rows are identical or a message describing the difference.
     * @throws SQLException If an error occurs.
     */
    public static String compareRow(List<Object> expectedRow, ResultSet actualRow)
            throws SQLException {
        ResultSetMetaData rsMetadata = actualRow.getMetaData();
        assertEquals(
                expectedRow.size(),
                rsMetadata.getColumnCount(),
                "Columns count mismatch.\nEither the yml test specification is missing columns for "
                        + "'expected_result' or the data in the DB is not matching the test");

        for (int i = 0; i < expectedRow.size(); i++) {
            // Handle expected field being null
            if (expectedRow.get(i) == null) {
                if (actualRow.getObject(i + 1) == null) {
                    continue;
                } else {
                    return "Expected null value for column " + (i + 1) + " but is not";
                }
            }

            int columnType = rsMetadata.getColumnType(i + 1);
            switch (columnType) {
                case Types.BIGINT:
                case Types.SMALLINT:
                case Types.TINYINT:
                case Types.INTEGER:
                    Object expectedInt = expectedRow.get(i);
                    int actualInt = actualRow.getInt(i + 1);
                    int expectedIntVal =
                            (expectedInt instanceof BsonInt32)
                                    ? ((BsonInt32) expectedInt).intValue()
                                    : ((Integer) expectedInt).intValue();
                    if (actualInt != expectedIntVal) {
                        return "Expected numeric value "
                                + actualInt
                                + " but it "
                                + expectedIntVal
                                + "for column "
                                + (i + 1);
                    }
                    break;
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                case Types.NCHAR:
                case Types.CHAR:
                case Types.NVARCHAR:
                case Types.VARCHAR:
                    String expected_str = (String) expectedRow.get(i);
                    String actual_str = actualRow.getString(i + 1);
                    if (!(expected_str).equals(actual_str)) {
                        return "Expected String value "
                                + expected_str
                                + " but is "
                                + actual_str
                                + " for column "
                                + (i + 1);
                    }
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    boolean expected_bool = (Boolean) expectedRow.get(i);
                    boolean actual_bool = actualRow.getBoolean(i + 1);
                    if (expected_bool != actual_bool) {
                        return "Expected boolean value "
                                + expected_bool
                                + " but is "
                                + actual_bool
                                + " for column "
                                + (i + 1);
                    }
                    break;
                case Types.DOUBLE:
                    double expected_double = (double) expectedRow.get(i);
                    double actual_double = actualRow.getDouble(i + 1);
                    if (expected_double != actual_double) {
                        return "Expected double value "
                                + expected_double
                                + " but is "
                                + actual_double
                                + " for column "
                                + (i + 1);
                    }
                    break;
                case Types.NULL:
                    Object expected_null = expectedRow.get(i);
                    Object actual_null = actualRow.getObject(i + 1);
                    if (expected_null != actual_null) {
                        return "Expected Bson Null value "
                                + expected_null
                                + " but is "
                                + actual_null
                                + " for column "
                                + (i + 1);
                    }
                    break;
                case Types.TIMESTAMP:
                    Object expected_date = expectedRow.get(i);
                    Date actual_date = actualRow.getDate(i + 1);
                    if (!expected_date.equals(actual_date)) {
                        return "Expected date value"
                                + expected_date.toString()
                                + " but is "
                                + actual_date.toString()
                                + " for column "
                                + (i + 1);
                    }
                    break;
                case Types.OTHER:
                    Object expected_obj = expectedRow.get(i);
                    if (expected_obj instanceof String) {
                        String actual_obj = actualRow.getString(i + 1);
                        if (!expected_obj.equals(actual_obj)) {
                            return "Expected Bson Other String value "
                                    + expected_obj
                                    + " but is "
                                    + actual_obj
                                    + " for column "
                                    + (i + 1);
                        }
                    } else if (expected_obj instanceof BsonValue) {
                        Object actual_obj = actualRow.getObject(i + 1);
                        ExtJsonValue expectedAsExtJsonValue =
                                new ExtJsonValue((BsonValue) expected_obj);
                        if (!expectedAsExtJsonValue.equals(actual_obj)) {
                            return "Expected Bson Other BsonValue value "
                                    + expected_obj
                                    + " but is "
                                    + actual_obj
                                    + " for column "
                                    + (i + 1);
                        }
                    } else {
                        throw new IllegalArgumentException("unsupported expected value class: " + expected_obj.getClass());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unsupported column type:" + columnType);
            }
        }
        return null;
    }
}
