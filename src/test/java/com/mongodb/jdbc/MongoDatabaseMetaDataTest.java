package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class MongoDatabaseMetaDataTest {
    protected DatabaseMetaData databaseMetaData;

    protected abstract DatabaseMetaData createDatabaseMetaData();

    /**
     * sortColumns sorts expected column names according to implementation needs. Subclasses should
     * implement this as necessary. For example, the common tests are written with the columns in
     * MySQL expected order, so the MySQL subclass implementation of this method does nothing. The
     * MongoSQL subclass needs to sort the columns alphabetically.
     *
     * @param columns the columns to sort
     */
    protected abstract void sortColumns(String[] columns);

    @BeforeAll
    public void setUp() {
        databaseMetaData = createDatabaseMetaData();
    }

    protected int countRows(ResultSet rs) throws SQLException {
        for (int i = 0; ; ++i) {
            if (!rs.next()) {
                return i;
            }
        }
    }

    /**
     * Calls the databaseMetaData.getFunctions with the given function name pattern and the expected
     * number of rows it should return and verifies that it matches the actual number of rows
     * returned.
     *
     * @param functionNamePattern The function name pattern used to narrow the search.
     * @param expectedNumRows The expected number of rows it should return.
     * @throws SQLException If an error occurs when calling getFunctions.
     */
    protected void testGetFunctionsHelper(String functionNamePattern, int expectedNumRows)
            throws SQLException {
        String[] getFunctionsColumns =
                new String[] {
                    "FUNCTION_CAT",
                    "FUNCTION_SCHEM",
                    "FUNCTION_NAME",
                    "REMARKS",
                    "FUNCTION_TYPE",
                    "SPECIFIC_NAME",
                };
        ResultSet rs = databaseMetaData.getFunctions(null, null, functionNamePattern);
        validateResultSet(rs, expectedNumRows, getFunctionsColumns);
    }

    void validateResultSet(ResultSet rs, int expectedNumRows, String[] expectedColumns)
            throws SQLException {
        sortColumns(expectedColumns);
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < expectedColumns.length; ++i) {
            assertEquals(expectedColumns[i], rsmd.getColumnName(i + 1));
            assertEquals(expectedColumns[i], rsmd.getColumnLabel(i + 1));
        }
        assertEquals(expectedNumRows, countRows(rs));
    }

    // Most DatabaseMetaData tests require connection to an ADL cluster. These are
    // just simple tests for things that return empty result sets.  Since they are
    // the same tests for both MySQLDatabaseMetaData and MongoSQLDatabaseMetaData,
    // these tests are implemented in this abstract class which has two concrete
    // implementations at the end of the file. The concrete implementations have
    // some additional tests of their own.
    @Test
    void testGetProcedures() throws SQLException {
        String[] columns =
                new String[] {
                    "PROCEDURE_CAT",
                    "PROCEDURE_SCHEM",
                    "PROCEDURE_NAME",
                    "REMARKS",
                    "PROCEDURE_TYPE",
                    "SPECIFIC_NAME",
                };

        // we will never have procedures.
        ResultSet rs = databaseMetaData.getProcedures(null, null, "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetProceduresColumns() throws SQLException {
        String[] columns =
                new String[] {
                    "PROCEDURE_CAT",
                    "PROCEDURE_SCHEM",
                    "PROCEDURE_NAME",
                    "COLUMN_NAME",
                    "COLUMN_TYPE",
                    "DATA_TYPE",
                    "TYPE_NAME",
                    "PRECISION",
                    "LENGTH",
                    "SCALE",
                    "RADIX",
                    "NULLABLE",
                    "REMARKS",
                    "COLUMN_DEF",
                    "SQL_DATA_TYPE",
                    "SQL_DATETIME_SUB",
                    "CHAR_OCTET_LENGTH",
                    "ORDINAL_POSITION",
                    "IS_NULLABLE",
                    "SPECIFIC_NAME",
                };

        ResultSet rs = databaseMetaData.getProcedureColumns(null, null, "%", "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetVersionColumns() throws SQLException {
        String[] columns =
                new String[] {
                    "SCOPE",
                    "COLUMN_NAME",
                    "DATA_TYPE",
                    "TYPE_NAME",
                    "COLUMN_SIZE",
                    "BUFFER_LENGTH",
                    "DECIMAL_DIGITS",
                    "PSEUDO_COLUMN",
                };

        ResultSet rs = databaseMetaData.getVersionColumns(null, null, "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetImportedKeys() throws SQLException {
        String[] columns =
                new String[] {
                    "PKTABLE_CAT",
                    "PKTABLE_SCHEM",
                    "PKTABLE_NAME",
                    "PKCOLUMN_NAME",
                    "FKTABLE_CAT",
                    "FKTABLE_SCHEM",
                    "FKTABLE_NAME",
                    "FKCOLUMN_NAME",
                    "KEY_SEQ",
                    "UPDATE_RULE",
                    "DELETE_RULE",
                    "FK_NAME",
                    "PK_NAME",
                    "DEFERRABILITY",
                };

        ResultSet rs = databaseMetaData.getImportedKeys(null, null, "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetExportedKeys() throws SQLException {
        String[] columns =
                new String[] {
                    "PKTABLE_CAT",
                    "PKTABLE_SCHEM",
                    "PKTABLE_NAME",
                    "PKCOLUMN_NAME",
                    "FKTABLE_CAT",
                    "FKTABLE_SCHEM",
                    "FKTABLE_NAME",
                    "FKCOLUMN_NAME",
                    "KEY_SEQ",
                    "UPDATE_RULE",
                    "DELETE_RULE",
                    "FK_NAME",
                    "PK_NAME",
                    "DEFERRABILITY",
                };

        ResultSet rs = databaseMetaData.getExportedKeys(null, null, "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetCrossReference() throws SQLException {
        String[] columns =
                new String[] {
                    "PKTABLE_CAT",
                    "PKTABLE_SCHEM",
                    "PKTABLE_NAME",
                    "PKCOLUMN_NAME",
                    "FKTABLE_CAT",
                    "FKTABLE_SCHEM",
                    "FKTABLE_NAME",
                    "FKCOLUMN_NAME",
                    "KEY_SEQ",
                    "UPDATE_RULE",
                    "DELETE_RULE",
                    "FK_NAME",
                    "PK_NAME",
                    "DEFERRABILITY",
                };

        ResultSet rs = databaseMetaData.getCrossReference(null, null, "%", null, null, "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetUDTs() throws SQLException {
        String[] columns =
                new String[] {
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "CLASS_NAME",
                    "DATA_TYPE",
                    "REMARKS",
                    "BASE_TYPE",
                };

        ResultSet rs = databaseMetaData.getUDTs(null, null, "%", null);
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetSuperTypes() throws SQLException {
        String[] columns =
                new String[] {
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "SUPERTYPE_CAT",
                    "SUPERTYPE_SCHEM",
                    "SUPERTYPE_NAME",
                };

        ResultSet rs = databaseMetaData.getSuperTypes(null, "%", "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetSuperTables() throws SQLException {
        String[] columns =
                new String[] {
                    "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME",
                };

        ResultSet rs = databaseMetaData.getSuperTables(null, "%", "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetAttributes() throws SQLException {
        String[] columns =
                new String[] {
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "ATTR_NAME",
                    "DATA_TYPE",
                    "ATTR_TYPE_NAME",
                    "ATTR_SIZE",
                    "DECIMAL_DIGITS",
                    "NUM_PREC_RADIX",
                    "NULLABLE",
                    "REMARKS",
                    "ATTR_DEF",
                    "SQL_DATA_TYPE",
                    "SQL_DATETIME_SUB",
                    "CHAR_OCTET_LENGTH",
                    "ORDINAL_POSITION",
                    "IS_NULLABLE",
                    "SCOPE_CATALOG",
                    "SCOPE_SCHEMA",
                    "SCOPE_TABLE",
                    "SOURCE_DATA_TYPE",
                };

        ResultSet rs = databaseMetaData.getAttributes(null, null, "%", "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    void testGetPseudoColumns() throws SQLException {
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "DATA_TYPE",
                    "COLUMN_SIZE",
                    "DECIMAL_DIGITS",
                    "NUM_PREC_RADIX",
                    "COLUMN_USAGE",
                    "REMARKS",
                    "CHAR_OCTET_LENGTH",
                    "IS_NULLABLE",
                };

        ResultSet rs = databaseMetaData.getPseudoColumns(null, null, "%", "%");
        validateResultSet(rs, 0, columns);
    }

    @Test
    /** Test the DatabaseMetadata.getFunctions method. */
    abstract void testGetFunctions() throws SQLException;
}

class MySQLDatabaseMetaDataTest extends MongoDatabaseMetaDataTest {
    @Override
    protected DatabaseMetaData createDatabaseMetaData() {
        return new MySQLDatabaseMetaData(null);
    }

    @Override
    protected void sortColumns(String[] columns) {}

    @Test
    void testGetTableTypes() throws SQLException {
        String[] columns =
                new String[] {
                    "TABLE_TYPE",
                };

        ResultSet rs = databaseMetaData.getTableTypes();
        validateResultSet(rs, 1, columns);
    }

    @Test
    void testGetTypeInfo() throws SQLException {
        String[] columns =
                new String[] {
                    "TYPE_NAME",
                    "DATA_TYPE",
                    "PRECISION",
                    "LITERAL_PREFIX",
                    "LITERAL_SUFFIX",
                    "CREATE_PARAMS",
                    "NULLABLE",
                    "CASE_SENSITIVE",
                    "SEARCHABLE",
                    "UNSIGNED_ATTRIBUTE",
                    "FIXED_PREC_SCALE",
                    "AUTO_INCREMENT",
                    "LOCAL_TYPE_NAME",
                    "MINIMUM_SCALE",
                    "MAXIMUM_SCALE",
                    "SQL_DATA_TYPE",
                    "SQL_DATETIME_SUB",
                    "NUM_PREC_RADIX",
                };

        ResultSet rs = databaseMetaData.getTypeInfo();
        validateResultSet(rs, 8, columns);
    }

    @Test
    void testGetClientInfoProperties() throws SQLException {
        String[] columns =
                new String[] {
                    "NAME", "MAX_LEN", "DEFAULT_VALUE", "DESCRIPTION",
                };

        ResultSet rs = databaseMetaData.getClientInfoProperties();
        validateResultSet(rs, 4, columns);
    }

    @Test
    @Override
    void testGetFunctions() throws SQLException {
        // All function(s)
        testGetFunctionsHelper("%", 120);
        // All function(s) with a 'S'
        testGetFunctionsHelper("%S%", 48);
        // All function(s) with a 's'
        testGetFunctionsHelper("%s%", 0);
        // The 'SUBSTRING' function(s)
        testGetFunctionsHelper("SUBSTRING", 2);
        // The 'SUBS(any character)RING' function(s)
        testGetFunctionsHelper("SUBS_RING", 2);
    }
}

class MongoSQLDatabaseMetaDataTest extends MongoDatabaseMetaDataTest {
    @Override
    protected DatabaseMetaData createDatabaseMetaData() {
        return new MongoSQLDatabaseMetaData(null);
    }

    @Override
    protected void sortColumns(String[] columns) {
        Arrays.sort(columns);
    }

    @Test
    void testGetSchemas() throws SQLException {
        String[] columns =
                new String[] {
                    "TABLE_SCHEM", "TABLE_CATALOG",
                };

        // getSchemas()
        ResultSet rs = databaseMetaData.getSchemas();
        validateResultSet(rs, 0, columns);

        // getSchemas(catalog, schemaPattern)
        rs = databaseMetaData.getSchemas(null, null);
        validateResultSet(rs, 0, columns);
    }

    @Test
    @Override
    void testGetFunctions() throws SQLException {
        // All function(s)
        testGetFunctionsHelper("%", 19);
        // All function(s) with a 'S'
        testGetFunctionsHelper("%S%", 9);
        // All function(s) with a 's'
        testGetFunctionsHelper("%s%", 0);
        // The 'SUBSTRING' function(s)
        testGetFunctionsHelper("SUBSTRING", 2);
        // The 'SUBS(any character)RING' function(s)
        testGetFunctionsHelper("SUBS_RING", 2);
    }
}
