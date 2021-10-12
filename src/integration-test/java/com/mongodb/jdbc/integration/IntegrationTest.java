package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.HashSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntegrationTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/test";
    static final String URL_WITH_USER_AND_PW =
            "jdbc:mongodb://"
                    + System.getenv("ADL_TEST_USER")
                    + ":"
                    + System.getenv("ADL_TEST_PWD")
                    + "@"
                    + System.getenv("ADL_TEST_HOST")
                    + "/test";

    private int countRows(ResultSet rs) throws SQLException {
        for (int i = 0; ; ++i) {
            if (!rs.next()) {
                return i;
            }
        }
    }

    static Connection getBasicConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        return DriverManager.getConnection(URL, p);
    }

    @Test
    public void basicDatabaseMetaDataTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        assertEquals(System.getenv("ADL_TEST_USER"), dbmd.getUserName());
        // It appears that the url arguments are, in some cases, put in
        // different order, so we check them for set equality instead
        // of linear equality.
        HashSet<String> args = new HashSet<>();
        args.add("authsource=admin");
        args.add("ssl=true");
        String[] urlSp = dbmd.getURL().split("[?]");
        String baseUrl = urlSp[0];
        String urlArgs = urlSp[1];
        assertEquals(
                "mongodb://"
                        + System.getenv("ADL_TEST_USER")
                        + ":"
                        + System.getenv("ADL_TEST_PWD")
                        + "@"
                        + System.getenv("ADL_TEST_HOST")
                        + "/test",
                baseUrl);
        // Check the url args for set equality, now:
        for (String arg : urlArgs.split("&")) {
            assertTrue(args.contains(arg));
        }
        System.out.println(dbmd.getURL());
    }

    static final String catalogPattern = "%FORMATION_S%";
    static final String tableNamePattern = "%COL%";
    static final String columnNamePattern = "%_MAXI%";
    static final String columnNamePattern2 = "%_FOO%";

    @Test
    public void databaseASSERTIONS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.ASSERTIONS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                    "IS_DEFERRABLE",
                    "INITIALLY_DEFERRED",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseCHARACTER_SETS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.CHARACTER_SETS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CHARACTER_SET_CATALOG",
                    "CHARACTER_SET_SCHEMA",
                    "CHARACTER_SET_NAME",
                    "FORM_OF_USE",
                    "NUMBER_OF_CHARACTERS",
                    "DEFAULT_COLLATE_CATALOG",
                    "DEFAULT_COLLATE_SCHEMA",
                    "DEFAULT_COLLATE_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertTrue(countRows(rs) >= 31);
    }

    @Test
    public void databaseCHECK_CONSTRAINTS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.CHECK_CONSTRAINTS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG", "CONSTRAINT_SCHEMA", "CONSTRAINT_NAME", "CHECK_CLAUSE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseCOLUMNS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.COLUMNS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "ORDINAL_POSITION",
                    "COLUMN_DEFAULT",
                    "IS_NULLABLE",
                    "DATA_TYPE",
                    "CHARACTER_MAXIMUM_LENGTH",
                    "CHARACTER_OCTET_LENGTH",
                    "NUMERIC_PRECISION",
                    "NUMERIC_PRECISION_RADIX",
                    "NUMERIC_SCALE",
                    "DATETIME_PRECISION",
                    "CHARACTER_SET_CATALOG",
                    "CHARACTER_SET_SCHEMA",
                    "CHARACTER_SET_NAME",
                    "COLLATION_CATALOG",
                    "COLLATION_SCHEMA",
                    "COLLATION_NAME",
                    "DOMAIN_CATALOG",
                    "DOMAIN_SCHEMA",
                    "DOMAIN_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertTrue(countRows(rs) >= 299);
    }

    @Test
    public void databaseCOLUMN_DOMAIN_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.COLUMN_DOMAIN_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "DOMAIN_CATALOG",
                    "DOMAIN_SCHEMA",
                    "DOMAIN_NAME",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseCOLUMN_PRVILEGES_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.COLUMN_PRIVILEGES");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "GRANTOR",
                    "GRANTEE",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "PRIVILEGE_TYPE",
                    "IS_GRANTABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseCONSTRAINT_COLUMN_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery("select * from INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseCONSTRAINT_TABLE_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.CONSTRAINT_TABLE_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseDOMAINS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.DOMAINS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "DOMAIN_CATALOG",
                    "DOMAIN_SCHEMA",
                    "DOMAIN_NAME",
                    "DATA_TYPE",
                    "CHARACTER_MAXIMUM_LENGTH",
                    "CHARACTER_OCTET_LENGTH",
                    "COLLATION_CATALOG",
                    "COLLATION_SCHEMA",
                    "COLLATION_NAME",
                    "CHARACTER_SET_CATALOG",
                    "CHARACTER_SET_SCHEMA",
                    "CHARACTER_SET_NAME",
                    "NUMERIC_PRECISION",
                    "NUMERIC_PRECISION_RADIX",
                    "NUMERIC_SCALE",
                    "DATETIME_PRECISION",
                    "DOMAIN_DEFAULT",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseDOMAIN_CONSTRAINTS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.DOMAIN_CONSTRAINTS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                    "DOMAIN_CATALOG",
                    "DOMAIN_SCHEMA",
                    "DOMAIN_NAME",
                    "IS_DEFERRABLE",
                    "INITIALLY_DEFERRED",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseKEY_COLUMN_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.KEY_COLUMN_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "ORDINAL_POSITION",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseREFERENTIAL_CONSTRAINTS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery("select * from INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                    "UNIQUE_CONSTRAINT_CATALOG",
                    "UNIQUE_CONSTRAINT_SCHEMA",
                    "UNIQUE_CONSTRAINT_NAME",
                    "MATCH_OPTION",
                    "UPDATE_RULE",
                    "DELETE_RULE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseSCHEMATA_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.SCHEMATA");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CATALOG_NAME",
                    "SCHEMA_NAME",
                    "SCHEMA_OWNER",
                    "DEFAULT_CHARACTER_SET_CATALOG",
                    "DEFAULT_CHARACTER_SET_SCHEMA",
                    "DEFAULT_CHARACTER_SET_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertTrue(countRows(rs) >= 3);
    }

    @Test
    public void databaseSQL_LANGUAGES_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.SQL_LANGUAGES");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "SQL_LANGUAGE_SOURCE",
                    "SQL_LANGUAGE_YEAR",
                    "SQL_LANGUAGE_CONFORMANCE",
                    "SQL_LANGUAGE_INTEGRITY",
                    "SQL_LANGUAGE_IMPLEMENTATION",
                    "SQL_LANGUAGE_BINDING_STYLE",
                    "SQL_LANGUAGE_PROGRAMMING_LANGUAGE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseTABLES_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLES");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CATALOG", "TABLE_SCHEMA", "TABLE_NAME", "TABLE_TYPE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertTrue(countRows(rs) >= 29);
    }

    @Test
    public void databaseTABLE_CONSTRAINTS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRAINT_NAME",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "CONSTRAINT_TYPE",
                    "IS_DEFERRABLE",
                    "INITIALLY_DEFERRED",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertTrue(countRows(rs) >= 6);
    }

    @Test
    public void databaseTABLE_PRIVILEGES_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TABLE_PRIVILEGES");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "GRANTOR",
                    "GRANTEE",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "PRIVILEGE_TYPE",
                    "IS_GRANTABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseTRANSLATIONS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.TRANSLATIONS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TRANSLATION_CATALOG",
                    "TRANSLATION_SCHEMA",
                    "TRANSLATION_NAME",
                    "SOURCE_CHARACTER_SET_CATALOG",
                    "SOURCE_CHARACTER_SET_SCHEMA",
                    "SOURCE_CHARACTER_SET_NAME",
                    "TARGET_CHARACTER_SET_CATALOG",
                    "TARGET_CHARACTER_SET_SCHEMA",
                    "TARGET_CHARACTER_SET_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseUSAGE_PRIVILEGES_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.USAGE_PRIVILEGES");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "GRANTOR",
                    "GRANTEE",
                    "OBJECT_CATALOG",
                    "OBJECT_SCHEMA",
                    "OBJECT_NAME",
                    "OBJECT_TYPE",
                    "PRIVILEGE_TYPE",
                    "IS_GRANTABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseVIEWS_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.VIEWS");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "VIEW_DEFINITION",
                    "CHECK_OPTION",
                    "IS_UPDATABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseVIEW_COLUMN_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.VIEW_COLUMN_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "VIEW_CATALOG",
                    "VIEW_SCHEMA",
                    "VIEW_NAME",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseVIEW_TABLE_USAGE_TABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.VIEW_TABLE_USAGE");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "VIEW_CATALOG",
                    "VIEW_SCHEMA",
                    "VIEW_NAME",
                    "TABLE_CATALOG",
                    "TABLE_SCHEMA",
                    "TABLE_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseHeterogeneousDataTABLETest() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select num4 from tdvt.Calcs");
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "num4",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        double[] values = {
            0.0, 10.85, -13.47, -6.05, 8.32, 10.71, 0.0, -10.24, 4.77, 0.0, 19.39, 3.82, 3.38, 0.0,
            -14.21, 6.75, 0.0,
        };
        {
            int i = 0;
            while (rs.next()) {
                double diff = values[i++] - rs.getDouble(1);
                assertTrue(diff <= 0.01 && diff >= -0.01);
            }
        }
        // This will fail for now.
        assertEquals(Types.DOUBLE, rsmd.getColumnType(1));
    }

    @Test
    public void databaseMetaDataGetTablesTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(catalogPattern, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "TABLE_TYPE",
                    "REMARKS",
                    "TYPE_CAT",
                    "TYPE_SCHEM",
                    "TYPE_NAME",
                    "SELF_REFERENCING_COL_NAME",
                    "REF_GENERATION",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertEquals(23, countRows(rs));
        // rs = dbmd.getTables(catalogPattern, null, tableNamePattern, null);
        // assertEquals(7, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetCatalogsTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getCatalogs();
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // We should have at least INFORMATION_SCHEMA.
        assertTrue(countRows(rs) > 1);
    }

    @Test
    public void databaseMetaDataGetColumnsTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getColumns(catalogPattern, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "DATA_TYPE",
                    "TYPE_NAME",
                    "COLUMN_SIZE",
                    "BUFFER_LENGTH",
                    "DECIMAL_DIGITS",
                    "NUM_PREC_RADIX",
                    "NULLABLE",
                    "REMARKS",
                    "COLUMN_DEF",
                    "SQL_DATA_TYPE",
                    "SQL_DATETIME_SUB",
                    "CHAR_OCTET_LENGTH",
                    "ORDINAL_POSITION",
                    "IS_NULLABLE",
                    "SCOPE_CATALOG",
                    "SCOPE_SCHEMA",
                    "SCOPE_TABLE",
                    "SOURCE_DATA_TYPE",
                    "IS_AUTOINCREMENT",
                    "IS_GENERATEDCOLUMN",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        // Disabled: SQL-539
        // assertEquals(186, countRows(rs));
        // rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, null);
        // assertEquals(67, countRows(rs));
        // rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, columnNamePattern);
        // assertEquals(1, countRows(rs));
        rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, columnNamePattern2);
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetColumnsPrivilegesTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getColumnPrivileges(catalogPattern, null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "COLUMN_NAME",
                    "GRANTOR",
                    "GRANTEE",
                    "PRIVILEGE",
                    "IS_GRANTABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
        rs = dbmd.getColumnPrivileges(catalogPattern, null, tableNamePattern, null);
        assertEquals(0, countRows(rs));
        rs = dbmd.getColumnPrivileges(catalogPattern, null, tableNamePattern, columnNamePattern);
        assertEquals(0, countRows(rs));
        rs = dbmd.getColumnPrivileges(catalogPattern, null, tableNamePattern, columnNamePattern2);
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetTablePrivilegesTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTablePrivileges(catalogPattern, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "GRANTOR",
                    "GRANTEE",
                    "PRIVILEGE",
                    "IS_GRANTABLE",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
        rs = dbmd.getTablePrivileges(catalogPattern, null, tableNamePattern);
        assertEquals(0, countRows(rs));
    }

    String schema = "INFORMATION_SCHEMA";
    String table = "COLUMNS";

    @Test
    public void databaseMetaDataGetBestRowIdentifierTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getBestRowIdentifier(null, schema, table, 0, true);
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
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetPrimaryKeysTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getPrimaryKeys(null, schema, table);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetIndexInfoTest() throws SQLException {
        Connection conn = getBasicConnection();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getIndexInfo(null, schema, table, false, false);
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns =
                new String[] {
                    "TABLE_CAT",
                    "TABLE_SCHEM",
                    "TABLE_NAME",
                    "NON_UNIQUE",
                    "INDEX_QUALIFIER",
                    "INDEX_NAME",
                    "TYPE",
                    "ORDINAL_POSITION",
                    "COLUMN_NAME",
                    "ASC_OR_DESC",
                    "CARDINALITY",
                    "PAGES",
                    "FILTER_CONDITION",
                };
        for (int i = 0; i < columns.length; ++i) {
            assertEquals(rsmd.getColumnLabel(i + 1), columns[i]);
        }
        assertEquals(0, countRows(rs));
    }

    @Test
    public void testConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertTrue(conn.isValid(15));
    }

    @Test
    public void nullInfo() throws SQLException {
        // Make sure we don't get an NPE with null properties.
        Connection conn = DriverManager.getConnection(URL, null);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badUserName() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", "baduser");
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badPassword() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", "badPass");
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badAuthDB() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", "badDB");
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }

    @Test
    public void badAuthMethod() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        Connection conn = DriverManager.getConnection(URL, p);
        assertFalse(conn.isValid(15));
    }
}
