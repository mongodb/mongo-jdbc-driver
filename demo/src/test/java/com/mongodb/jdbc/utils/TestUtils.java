package com.mongodb.jdbc.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class TestUtils {

    protected Connection m_conn;
    protected DatabaseMetaData m_dbMeta;

    protected abstract Connection connect() throws Exception;

    @BeforeEach
    private void execConnection() throws Exception {
        m_conn = connect();
        m_dbMeta = m_conn.getMetaData();
    }

    @AfterEach
    private void execDeconnection() throws Exception {
        m_conn.close();
    }

    @Test
    void testConnection() throws Exception {
        System.out.println(m_dbMeta.getDriverName());
        System.out.println(m_dbMeta.getDriverVersion());

        System.out.println("Connected !");
    }

    @Test
    void testExecute() throws Exception
    {
        runExecuteQuery("select * from bar", true);
    }

    @Test
    void testExecutePrintMetadata() throws Exception
    {
        runExecuteQuery("select * from bar", false);
    }

    @Test
    void testGetCatalogs() throws Exception
    {
        ResultSet rs = m_dbMeta.getCatalogs();
        printRsInfo(rs);
    }

    @Test
    void testGetTables() throws Exception
    {
        //ResultSet rs = m_dbMeta.getTables(null, null, null, null);
        ResultSet rs = m_dbMeta.getTables(null, null, "%", new String[]{"table", "view"});
        printRsInfo(rs);
    }

    @Test
    void testGetColumns() throws Exception
    {
        //ResultSet rs = m_dbMeta.getColumns(null, null, null, null);
        ResultSet rs = m_dbMeta.getColumns(null, "%", "%", "%");
        printRsInfo(rs);
    }

    @Test
    void testGetSchemas() throws Exception
    {
        ResultSet rs = m_dbMeta.getSchemas();
        //ResultSet rs = m_dbMeta.getSchemas(null, "filter");
        printRsInfo(rs);
    }

    @Test
    void testGetTablePrivileges() throws Exception
    {
        //ResultSet rs = m_dbMeta.getTablePrivileges(null, null, null);
        ResultSet rs = m_dbMeta.getTablePrivileges(null, "%", "%");
        printRsInfo(rs);
    }

    @Test
    void testGetTableTypes() throws Exception
    {
        ResultSet rs = m_dbMeta.getTableTypes();
        printRsInfo(rs);
    }

    @Test
    void testVersionColumns() throws Exception
    {
        //ResultSet rs = m_dbMeta.getVersionColumns(null, null, null);
        ResultSet rs = m_dbMeta.getVersionColumns(null, "%", "%");
        printRsInfo(rs);
    }

    @Test
    void testFunctions() throws Exception
    {
        ResultSet rs = m_dbMeta.getFunctions(null, null, null);
        //ResultSet rs = m_dbMeta.getFunctions(null, "%", "%");

        printRsInfo(rs);
    }

    @Test
    void testFunctionColumns() throws Exception
    {
        //ResultSet rs = m_dbMeta.getFunctionColumns(null, null, null, null);
        ResultSet rs = m_dbMeta.getFunctionColumns(null, "%", "%", "%");
        printRsInfo(rs);
    }

    @Test
    void testGetProcedures() throws Exception
    {
        //ResultSet rs = m_dbMeta.getProcedures(null,null,null);
        ResultSet rs = m_dbMeta.getProcedures(null,"%","%");
        printRsInfo(rs);
    }

    @Test
    void testGetProcedureColumns() throws Exception
    {
        //ResultSet rs = m_dbMeta.getProcedureColumns(null,null,null, null);
        ResultSet rs = m_dbMeta.getProcedureColumns(null,"%","%", "%");
        printRsInfo(rs);
    }

    @Test
    void testGetClientInfoProperties() throws Exception
    {
        ResultSet rs = m_dbMeta.getClientInfoProperties();
        printRsInfo(rs);
    }

    @Test
    void testGetAttributes() throws Exception
    {
        //ResultSet rs = m_dbMeta.getAttributes(null, null,null,null);
        ResultSet rs = m_dbMeta.getAttributes(null, "%","%","%");
        printRsInfo(rs);
    }

    @Test
    void testGetBestRowIdentifier() throws Exception
    {
        //ResultSet rs = m_dbMeta.getBestRowIdentifier(null,null,null,0,false);
        ResultSet rs = m_dbMeta.getBestRowIdentifier(null,null,"myTable",0,false);
        printRsInfo(rs);
    }

    @Test
    void testGetColumnPrivileges() throws Exception
    {
        //ResultSet rs = m_dbMeta.getColumnPrivileges(null,null,null,null);
        ResultSet rs = m_dbMeta.getColumnPrivileges(null,null,"myTable","%");
        printRsInfo(rs);
    }

    @Test
    void testGetCrossReference() throws Exception
    {
        //ResultSet rs = m_dbMeta.getCrossReference(null,null,null,null,null, null);
        ResultSet rs = m_dbMeta.getCrossReference(null,null,"parentTable","foreignCatalog",null,"foreignTable");
        printRsInfo(rs);
    }

    @Test
    void testGetExportedKeys() throws Exception
    {
        //ResultSet rs = m_dbMeta.getExportedKeys(null,null,null);
        ResultSet rs = m_dbMeta.getExportedKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void getImportedKeys() throws Exception
    {
        //ResultSet rs = m_dbMeta.getImportedKeys(null,null,null);
        ResultSet rs = m_dbMeta.getImportedKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void testGetIndexInfo() throws Exception
    {
        ResultSet rs = m_dbMeta.getIndexInfo(null,null,null,false, false);
        printRsInfo(rs);
    }

    @Test
    void testGetPrimaryKeys() throws Exception
    {
        //ResultSet rs = m_dbMeta.getPrimaryKeys(null,null,null);
        ResultSet rs = m_dbMeta.getPrimaryKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void testGetPseudoColumns() throws Exception
    {
        //ResultSet rs = m_dbMeta.getPseudoColumns(null,null,null,null);
        ResultSet rs = m_dbMeta.getPseudoColumns(null,"%","%","%");
        printRsInfo(rs);
    }

    @Test
    void testGetSuperTables() throws Exception
    {
        ResultSet rs = m_dbMeta.getSuperTables(null,null,null);
        printRsInfo(rs);
    }

    @Test
    void testGetSuperTypes() throws Exception
    {
        ResultSet rs = m_dbMeta.getSuperTypes(null,null,null);
        printRsInfo(rs);
    }

    @Test
    void testGetTypeInfo() throws Exception
    {
        ResultSet rs = m_dbMeta.getTypeInfo();
        printRsInfo(rs);
    }

    @Test
    void testGetUDTs() throws Exception
    {
        ResultSet rs = m_dbMeta.getUDTs(null,null,null, new int[]{1});
        printRsInfo(rs);
    }

    @Test
    void testDBMeta() throws Exception {
        System.out
                .println("-----------------------------------------------------------------------");
        System.out
                .println("-----------------------------------------------------------------------");
        System.out.println("API databases metadata");
        System.out.println("Driver Name: " + m_dbMeta.getDriverName());
        System.out.println("Driver Version: " + m_dbMeta.getDriverVersion());
        System.out.println("Driver Major Version: "
                + m_dbMeta.getDriverMajorVersion());
        System.out.println("Driver Minor Version: "
                + m_dbMeta.getDriverMinorVersion());
        System.out.println("Database Product Name: "
                + m_dbMeta.getDatabaseProductName());
        System.out.println("Database Product version: "
                + m_dbMeta.getDatabaseProductVersion());
        System.out.println("Database Major version: "
                + m_dbMeta.getDatabaseMajorVersion());
        System.out.println("Database Minor version : "
                + m_dbMeta.getDatabaseMinorVersion());
        System.out.println("Supports Mixed Case Quoted Identifiers: "
                + m_dbMeta.supportsMixedCaseQuotedIdentifiers());
        System.out.println("Supports Mixed Case Identifiers: "
                + m_dbMeta.supportsMixedCaseIdentifiers());
        System.out.println("Stores UpperCase Quoted Identifiers: "
                + m_dbMeta.storesUpperCaseQuotedIdentifiers());
        System.out.println("Stores LowerCase Quoted Identifiers: "
                + m_dbMeta.storesLowerCaseQuotedIdentifiers());
        System.out.println("Stores Mixed Case Quoted Identifiers: "
                + m_dbMeta.storesMixedCaseQuotedIdentifiers());
        System.out.println("Stores Upper Case Identifiers: "
                + m_dbMeta.storesUpperCaseIdentifiers());
        System.out.println("Stores Lower Case Identifiers: "
                + m_dbMeta.storesLowerCaseIdentifiers());
        System.out.println("Stores Mixed Case Identifiers: "
                + m_dbMeta.storesMixedCaseIdentifiers());
        System.out.println("Identifier Quote String: "
                + m_dbMeta.getIdentifierQuoteString());
        System.out.println("Is ReadOnly: " + m_dbMeta.isReadOnly());
        System.out.println("Supports ResultSet Concurrency: "
                + m_dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.FETCH_UNKNOWN));

        System.out.println("get Identifier Quote String: "
                + m_dbMeta.getJDBCMajorVersion());
        System.out.println("get Identifier Quote String: "
                + m_dbMeta.getTimeDateFunctions());
        System.out.println("get Identifier Quote String: "
                + m_dbMeta.getStringFunctions());
        System.out.println("get Identifier Quote String: "
                + m_dbMeta.getNumericFunctions());
    }

    @Test
    void testGetUrl() throws Exception
    {
        System.out.println(m_dbMeta.getURL());
    }

    @Test
    void testSetCatalog() throws SQLException
    {
        m_conn.setCatalog("test_set_catalog");
        System.out.println(m_conn.getCatalog());
    }

    @Test
    // Note : Cancel is not supported yet
    void testCancel() throws SQLException
    {
        // The query must be a long running query for cancel to be effective
        String query = "select * from class";
        System.out.println("-----------------------------------------------------------------------");

        class QueryExecutor implements Runnable
        {
            private boolean m_isExecuting;
            private final String m_query;
            private ResultSet m_result;
            private final Statement m_statement;

            public QueryExecutor(Statement statement, String query)
            {
                m_statement = statement;
                m_query = query;
                m_isExecuting = false;
            }

            public boolean getIsExecuting()
            {
                return m_isExecuting;
            }

            public ResultSet getResultSet()
            {
                return m_result;
            }

            @Override
            public void run()
            {
                try
                {
                    System.out.println("Executing '" + m_query + "'");
                    m_isExecuting = true;
                    m_result = m_statement.executeQuery(m_query);
                    m_isExecuting = false;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                finally {
                    try {
                        m_result.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Statement statement = m_conn.createStatement();
        ResultSet result = null;

        QueryExecutor queryExecutor = new QueryExecutor(statement, query);
        Thread execThread = new Thread(queryExecutor);
        execThread.start();

        while (!queryExecutor.getIsExecuting())
        {
            ;
        }
        if (execThread.isAlive())
        {
            try
            {
                // Still need to wait because the m_isExecuting flag is set  before statement.executeQuery is called.
                // Need to wait until an executor object is created.
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Calling cancel");
            statement.cancel();
            System.out.println("The query cancelled successfully.");

            // Try to use the statement again
            queryExecutor = new QueryExecutor(statement, query);
            execThread = new Thread(queryExecutor);
            execThread.start();
        }
        else
        {
            System.out.println("The execution finished before cancel");
            queryExecutor.getResultSet().close();
        }

        System.out.println("-----------------------------------------------------------------------");
    }

    private void runExecuteQuery(String query, boolean printRs) throws Exception
    {
        try {
            Statement stmt = m_conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (printRs) {
                PrintUtils.printResultSet(rs);
            }
            else {
                PrintUtils.printResultSetMetadata(rs.getMetaData());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private void printRsInfo(ResultSet rs) throws Exception
    {
        try
        {
            PrintUtils.printResultSetMetadata(rs.getMetaData());
            PrintUtils.printResultSet(rs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
