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

import static com.mongodb.jdbc.MongoDatabaseMetaData.filterEmptiesAndInternalDBs;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.ConnectionString;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDatabaseMetaDataTest {
    protected static final ConnectionString uri =
            new ConnectionString("mongodb://localhost:27017/admin");
    protected static final String database = "mock";

    protected DatabaseMetaData databaseMetaData =
            new MongoDatabaseMetaData(
                    new MongoConnection(
                            new MongoConnectionProperties(
                                    uri, database, null, null, null, false, null)));

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
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < expectedColumns.length; ++i) {
            assertEquals(expectedColumns[i], rsmd.getColumnName(i + 1));
            assertEquals(expectedColumns[i], rsmd.getColumnLabel(i + 1));
        }
        assertEquals(expectedNumRows, countRows(rs));
    }

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
    /** Test the DatabaseMetadata.getFunctions method. */
    void testGetFunctions() throws SQLException {
        // All function(s)
        testGetFunctionsHelper("%", 114);
        // All function(s) with a 'S'
        testGetFunctionsHelper("%S%", 31);
        // All function(s) with a 's'
        testGetFunctionsHelper("%s%", 0);
        // The 'SUBSTRING' function(s)
        testGetFunctionsHelper("SUBSTRING", 2);
        // The 'SUBS(any character)RING' function(s)
        testGetFunctionsHelper("SUBS_RING", 2);
    }

    @Test
    void testGetSQLKeywords() throws SQLException {
        final String expectedKeywords =
                "AGGREGATE,"
                        + "ASC,"
                        + "BINDATA,"
                        + "BIT,"
                        + "BOOL,"
                        + "BSON_DATE,"
                        + "BSON_TIMESTAMP,"
                        + "DBPOINTER,"
                        + "DESC,"
                        + "DOCUMENT,"
                        + "ERROR,"
                        + "EXTRACT,"
                        + "FIRST,"
                        + "JAVASCRIPT,"
                        + "JAVASCRIPTWITHSCOPE,"
                        + "LIMIT,"
                        + "LONG,"
                        + "MAXKEY,"
                        + "MINKEY,"
                        + "MISSING,"
                        + "NEXT,"
                        + "NUMBER,"
                        + "OBJECTID,"
                        + "OFFSET,"
                        + "POSITION,"
                        + "REGEX,"
                        + "SUBSTRING,"
                        + "SYMBOL,"
                        + "TRIM,"
                        + "UNDEFINED";

        assertEquals(expectedKeywords, databaseMetaData.getSQLKeywords());
    }

    @Test
    void testGetNumericFunctions() throws SQLException {
        final String expectedFunctions =
                "ABS,"
                        + "COS,"
                        + "DEGREES,"
                        + "FLOOR,"
                        + "MOD,"
                        + "RADIANS,"
                        + "ROUND,"
                        + "SIN,"
                        + "SQRT,"
                        + "TAN";

        assertEquals(expectedFunctions, databaseMetaData.getNumericFunctions());
    }

    @Test
    void testGetStringFunctions() throws SQLException {
        final String expectedFunctions =
                "CHAR_LENGTH," + "OCTET_LENGTH," + "POSITION," + "SUBSTRING";

        assertEquals(expectedFunctions, databaseMetaData.getStringFunctions());
    }

    @Test
    void testGetSystemFunctions() throws SQLException {
        final String expectedFunctions = "";

        assertEquals(expectedFunctions, databaseMetaData.getSystemFunctions());
    }

    @Test
    void testGetTimeDateFunctions() throws SQLException {
        final String expectedFunctions = "CURRENT_TIMESTAMP,EXTRACT";

        assertEquals(expectedFunctions, databaseMetaData.getTimeDateFunctions());
    }

    @Test
    void testToJavaPattern() throws SQLException {
        final String specialCharacters = ".^$*+?(){}|[]\\";
        final String escapedSpecialCharacters = "\\.\\^\\$\\*\\+\\?\\(\\)\\{\\}\\|\\[\\]\\\\";

        assertEquals(
                escapedSpecialCharacters,
                MongoDatabaseMetaData.toJavaPattern(specialCharacters).toString());
        assertEquals("\\\\%", MongoDatabaseMetaData.toJavaPattern("\\%").toString());
        assertEquals(".*", MongoDatabaseMetaData.toJavaPattern("%").toString());
        assertEquals("\\\\_", MongoDatabaseMetaData.toJavaPattern("\\_").toString());
        assertEquals(".", MongoDatabaseMetaData.toJavaPattern("_").toString());
        assertEquals(
                "nothingToEscape",
                MongoDatabaseMetaData.toJavaPattern("nothingToEscape").toString());
        assertEquals(
                "a\\\\_.b\\\\%.*", MongoDatabaseMetaData.toJavaPattern("a\\__b\\%%").toString());
    }

    @Test
    void testFilterEmptiesAndAdmin() {
        final ArrayList<String> input = new ArrayList<>(Arrays.asList("foo", "bar", "", "admin"));
        final ArrayList<String> expected = new ArrayList<>(Arrays.asList("foo", "bar"));
        assertEquals(
                expected,
                input.stream()
                        .filter(dbName -> filterEmptiesAndInternalDBs(dbName))
                        .collect(Collectors.toList()));
    }
}
