package com.mongodb.jdbc.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/***
 * This class provides the baseline for common things that applications built on top of JDBC use.
 * - Connecting to the database: testConnection
 * - Executing a query and printing out the result set information: testExecute, testExecutePrintMetadata.
 * - Retrieving database metadata information to check the driver information and capability: testDBMeta.
 * - Retrieving catalog information: testGetCatalogs, testGetSchemas, testGetTables, testGetTablePrivileges...
 * - Cancelling a long running query: testCancel
 *
 * It does not cover everything an application can/will do (there is no prepare/execute flow for example).
 *
 * It is expected that you will modify the code here based on your need when you want to try out or debug something.
 * The version in the GitHub repository is only a first minimalistic version to help you get started.
 * This class must live and its content evolve with you.
 *
 * Please note that in order to run one of the helper you need to uncomment the "exclude 'com/mongodb/jdbc/utils/**'" line
 * in the build.gradle file.
 */
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
    void tryConnecting() throws Exception {
        System.out.println(m_dbMeta.getDriverName());
        System.out.println(m_dbMeta.getDriverVersion());

        System.out.println("Connected !");
    }

    @Test
    void tryExecute() throws Exception {
        runExecuteQuery("select * from bar", true);
    }

    @Test
    void tryExecutePrintMetadata() throws Exception {
        runExecuteQuery("select * from foo", false);
    }

    @Test
    void tryGetCatalogs() throws Exception {
        ResultSet rs = m_dbMeta.getCatalogs();
        printRsInfo(rs);
    }

    @Test
    void tryGetTables() throws Exception {
        //ResultSet rs = m_dbMeta.getTables(null, null, null, null);
        ResultSet rs = m_dbMeta.getTables(null, null, "%", new String[]{"table", "view"});
        printRsInfo(rs);
    }

    @Test
    void tryGetColumns() throws Exception {
        //ResultSet rs = m_dbMeta.getColumns(null, null, null, null);
        ResultSet rs = m_dbMeta.getColumns(null, "%", "%", "%");
        printRsInfo(rs);
    }

    @Test
    void tryGetSchemas() throws Exception {
        ResultSet rs = m_dbMeta.getSchemas();
        //ResultSet rs = m_dbMeta.getSchemas(null, "filter");
        printRsInfo(rs);
    }

    @Test
    void tryGetTablePrivileges() throws Exception {
        //ResultSet rs = m_dbMeta.getTablePrivileges(null, null, null);
        ResultSet rs = m_dbMeta.getTablePrivileges(null, "%", "%");
        printRsInfo(rs);
    }

    @Test
    void tryGetTableTypes() throws Exception {
        ResultSet rs = m_dbMeta.getTableTypes();
        printRsInfo(rs);
    }

    @Test
    void tryVersionColumns() throws Exception {
        //ResultSet rs = m_dbMeta.getVersionColumns(null, null, null);
        ResultSet rs = m_dbMeta.getVersionColumns(null, "%", "%");
        printRsInfo(rs);
    }

    @Test
    void tryGetFunctions() throws Exception {
        ResultSet rs = m_dbMeta.getFunctions(null, null, null);
        //ResultSet rs = m_dbMeta.getFunctions(null, "%", "%");

        printRsInfo(rs);
    }

    @Test
    void tryGetFunctionColumns() throws Exception {
        //ResultSet rs = m_dbMeta.getFunctionColumns(null, null, null, null);
        ResultSet rs = m_dbMeta.getFunctionColumns(null, "%", "%", "%");
        printRsInfo(rs);
    }

    @Test
    void tryGetProcedures() throws Exception {
        //ResultSet rs = m_dbMeta.getProcedures(null,null,null);
        ResultSet rs = m_dbMeta.getProcedures(null,"%","%");
        printRsInfo(rs);
    }

    @Test
    void tryGetProcedureColumns() throws Exception {
        //ResultSet rs = m_dbMeta.getProcedureColumns(null,null,null, null);
        ResultSet rs = m_dbMeta.getProcedureColumns(null,"%","%", "%");
        printRsInfo(rs);
    }

    @Test
    void tryGetClientInfoProperties() throws Exception {
        ResultSet rs = m_dbMeta.getClientInfoProperties();
        printRsInfo(rs);
    }

    @Test
    void tryGetAttributes() throws Exception {
        //ResultSet rs = m_dbMeta.getAttributes(null, null,null,null);
        ResultSet rs = m_dbMeta.getAttributes(null, "%","%","%");
        printRsInfo(rs);
    }

    @Test
    void tryGetBestRowIdentifier() throws Exception {
        //ResultSet rs = m_dbMeta.getBestRowIdentifier(null,null,null,0,false);
        ResultSet rs = m_dbMeta.getBestRowIdentifier(null,null,"myTable",0,false);
        printRsInfo(rs);
    }

    @Test
    void tryGetColumnPrivileges() throws Exception {
        //ResultSet rs = m_dbMeta.getColumnPrivileges(null,null,null,null);
        ResultSet rs = m_dbMeta.getColumnPrivileges(null,null,"myTable","%");
        printRsInfo(rs);
    }

    @Test
    void tryGetCrossReference() throws Exception {
        //ResultSet rs = m_dbMeta.getCrossReference(null,null,null,null,null, null);
        ResultSet rs = m_dbMeta.getCrossReference(null,null,"parentTable","foreignCatalog",null,"foreignTable");
        printRsInfo(rs);
    }

    @Test
    void tryGetExportedKeys() throws Exception {
        //ResultSet rs = m_dbMeta.getExportedKeys(null,null,null);
        ResultSet rs = m_dbMeta.getExportedKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void getImportedKeys() throws Exception {
        //ResultSet rs = m_dbMeta.getImportedKeys(null,null,null);
        ResultSet rs = m_dbMeta.getImportedKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void tryGetIndexInfo() throws Exception {
        ResultSet rs = m_dbMeta.getIndexInfo(null,null,null,false, false);
        printRsInfo(rs);
    }

    @Test
    void tryGetPrimaryKeys() throws Exception {
        //ResultSet rs = m_dbMeta.getPrimaryKeys(null,null,null);
        ResultSet rs = m_dbMeta.getPrimaryKeys(null,null,"myTable");
        printRsInfo(rs);
    }

    @Test
    void tryGetPseudoColumns() throws Exception {
        //ResultSet rs = m_dbMeta.getPseudoColumns(null,null,null,null);
        ResultSet rs = m_dbMeta.getPseudoColumns(null,"%","%","%");
        printRsInfo(rs);
    }

    @Test
    void tryGetSuperTables() throws Exception {
        ResultSet rs = m_dbMeta.getSuperTables(null,null,null);
        printRsInfo(rs);
    }

    @Test
    void tryGetSuperTypes() throws Exception {
        ResultSet rs = m_dbMeta.getSuperTypes(null,null,null);
        printRsInfo(rs);
    }

    @Test
    void tryGetTypeInfo() throws Exception {
        ResultSet rs = m_dbMeta.getTypeInfo();
        printRsInfo(rs);
    }

    @Test
    void tryGetUDTs() throws Exception {
        ResultSet rs = m_dbMeta.getUDTs(null,null,null, new int[]{1});
        printRsInfo(rs);
    }

    @Test
    void tryDBMeta() throws Exception {
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
    void tryGetUrl() throws Exception {
        System.out.println(m_dbMeta.getURL());
    }

    @Test
    void trySetCatalog() throws SQLException {
        m_conn.setCatalog("test_set_catalog");
        System.out.println(m_conn.getCatalog());
    }

    //@Test
    // TODO : Cancel is not supported yet
    void tryCancel() throws SQLException {
        // The query must be a long running query for cancel to be effective
        String query = "select * from class";
        System.out.println("-----------------------------------------------------------------------");

        class QueryExecutor implements Runnable
        {
            private boolean m_isExecuting;
            private final String m_query;
            private ResultSet m_result;
            private final Statement m_statement;

            public QueryExecutor(Statement statement, String query) {
                m_statement = statement;
                m_query = query;
                m_isExecuting = false;
            }

            public boolean getIsExecuting() {
                return m_isExecuting;
            }

            public ResultSet getResultSet() {
                return m_result;
            }

            @Override
            public void run()
            {
                try {
                    System.out.println("Executing '" + m_query + "'");
                    m_isExecuting = true;
                    m_result = m_statement.executeQuery(m_query);
                    m_isExecuting = false;
                }
                catch (SQLException e) {
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

        while (!queryExecutor.getIsExecuting()) {
            ;
        }
        if (execThread.isAlive()) {
            try {
                // Still need to wait because the m_isExecuting flag is set  before statement.executeQuery is called.
                // Need to wait until an executor object is created.
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
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
        else {
            System.out.println("The execution finished before cancel");
            queryExecutor.getResultSet().close();
        }

        System.out.println("-----------------------------------------------------------------------");
    }

    /**
     * Execute the given query and print either the resultset contents or its metadata.
     * @param query             The query to execute.
     * @param printRs           A flag to turn on printing the resultset content when true or the resultset metadata when false.
     * @throws Exception        If an error occurs while executing the query or retrieving the values to display.
     */
    private void runExecuteQuery(String query, boolean printRs) throws Exception {
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
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Prints the resultset metadata followed by its contents.
     * @param rs            The result set to display information for.
     * @throws Exception    If an error occurs when retrieving the information to display.
     */
    private void printRsInfo(ResultSet rs) throws Exception {
        try {
            PrintUtils.printResultSetMetadata(rs.getMetaData());
            PrintUtils.printResultSet(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
