package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
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

    @Test
    public void basicDatabaseMetaDataTest() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
        DatabaseMetaData dbmd = conn.getMetaData();
        assertEquals(System.getenv("ADL_TEST_USER"), dbmd.getUserName());
        assertEquals(
                "mongodb://"
                        + System.getenv("ADL_TEST_USER")
                        + ":"
                        + System.getenv("ADL_TEST_PWD")
                        + "@"
                        + System.getenv("ADL_TEST_HOST")
                        + "/test?authsource=admin&ssl=true",
                dbmd.getURL());
        System.out.println(dbmd.getURL());
    }

    static final String catalogPattern = "%FORMATION_S%";
    static final String tableNamePattern = "%COL%";
    static final String columnNamePattern = "%_MAXI%";
    static final String columnNamePattern2 = "%_FOO%";

    @Test
    public void databaseMetaDataGetTablesTest() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        assertEquals(23, countRows(rs));
        rs = dbmd.getTables(catalogPattern, null, tableNamePattern, null);
        assertEquals(7, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetCatalogsTest() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        assertEquals(186, countRows(rs));
        rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, null);
        assertEquals(67, countRows(rs));
        rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, columnNamePattern);
        assertEquals(1, countRows(rs));
        rs = dbmd.getColumns(catalogPattern, null, tableNamePattern, columnNamePattern2);
        assertEquals(0, countRows(rs));
    }

    @Test
    public void databaseMetaDataGetColumnsPrivilegesTest() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(URL, p);
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
