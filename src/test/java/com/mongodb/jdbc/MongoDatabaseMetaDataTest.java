package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class MongoDatabaseMetaDataTest {
    protected DatabaseMetaData databaseMetaData;

    protected abstract DatabaseMetaData createDatabaseMetaData();

    @BeforeEach
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

    // Most DatabaseMetaData tests require connection to an ADL cluster. These are
    // just simple tests for things that return empty result sets.  Since they are
    // the same tests for both MySQLDatabaseMetaData and MongoSQLDatabaseMetaData,
    // these tests are implemented in this abstract class which has two concrete
    // implementations at the end of the file. The concrete implementations have
    // some additional tests of their own.
    @Test
    void testGetProcedures() throws SQLException {
        // we will never have procedures.
        ResultSet rs = databaseMetaData.getProcedures(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "PROCEDURE_CAT",
                    "PROCEDURE_SCHEM",
                    "PROCEDURE_NAME",
                    "REMARKS",
                    "PROCEDURE_TYPE",
                    "SPECIFIC_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetProceduresColumns() throws SQLException {
        ResultSet rs = databaseMetaData.getProcedureColumns(null, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetVersionColumns() throws SQLException {
        ResultSet rs = databaseMetaData.getVersionColumns(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetImportedKeys() throws SQLException {
        ResultSet rs = databaseMetaData.getImportedKeys(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            System.out.println(i);
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetExportedKeys() throws SQLException {
        ResultSet rs = databaseMetaData.getExportedKeys(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetCrossReference() throws SQLException {
        ResultSet rs = databaseMetaData.getCrossReference(null, null, null, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetUDTs() throws SQLException {
        ResultSet rs = databaseMetaData.getUDTs(null, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetSuperTypes() throws SQLException {
        ResultSet rs = databaseMetaData.getSuperTypes(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "SUPERTYPE_CAT",
                    "SUPERTYPE_SCHEM",
                    "SUPERTYPE_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetSuperTables() throws SQLException {
        ResultSet rs = databaseMetaData.getSuperTables(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "SUPERTABLE_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetAttributes() throws SQLException {
        ResultSet rs = databaseMetaData.getAttributes(null, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }

    @Test
    void testGetPseudoColumns() throws SQLException {
        ResultSet rs = databaseMetaData.getPseudoColumns(null, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }
}

class MySQLDatabaseMetaDataTest extends MongoDatabaseMetaDataTest {
    @Override
    protected DatabaseMetaData createDatabaseMetaData() {
        return new MySQLDatabaseMetaData(null);
    }

    @Test
    void testGetTableTypes() throws SQLException {
        ResultSet rs = databaseMetaData.getTableTypes();
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                        "TABLE_TYPE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(countRows(rs), 1);
    }

    @Test
    void testGetTypeInfo() throws SQLException {
        ResultSet rs = databaseMetaData.getTypeInfo();
        ResultSetMetaData rsmd = rs.getMetaData();
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
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(countRows(rs), 8);
    }

    @Test
    void testGetClientInfoProperties() throws SQLException {
        ResultSet rs = databaseMetaData.getClientInfoProperties();
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                        "NAME", "MAX_LEN", "DEFAULT_VALUE", "DESCRIPTION",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(countRows(rs), 4);
    }

    @Test
    void testGetFunctions() throws SQLException {
        ResultSet rs = databaseMetaData.getFunctions(null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                        "FUNCTION_CAT",
                        "FUNCTION_SCHEM",
                        "FUNCTION_NAME",
                        "REMARKS",
                        "FUNCTION_TYPE",
                        "SPECIFIC_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(countRows(rs), 117);
        rs = databaseMetaData.getFunctions(null, null, "%S%");
        assertEquals(countRows(rs), 46);
        rs = databaseMetaData.getFunctions(null, null, "%s%");
        assertEquals(countRows(rs), 0);
    }
}

class MongoSQLDatabaseMetaDataTest extends MongoDatabaseMetaDataTest {
    @Override
    protected DatabaseMetaData createDatabaseMetaData() {
        return new MongoSQLDatabaseMetaData(null);
    }

    @Test
    void testGetSchemas() throws SQLException {
        // getSchemas()
        ResultSet rs = databaseMetaData.getSchemas();
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                        "TABLE_SCHEM",
                        "TABLE_CATALOG",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());

        // getSchemas(catalog, schemaPattern)
        rs = databaseMetaData.getSchemas(null, null);
        rsmd = rs.getMetaData();
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnName(i + 1), columns[i]);
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertFalse(rs.next());
    }
}
