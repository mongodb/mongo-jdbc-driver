package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLException;

import com.mongodb.jdbc.MongoDriver;

public class MongoDatabaseMetaData implements DatabaseMetaData {

    // Actual max size is 16777216, we reserve 216 for other bits of encoding,
    // since this value is used to set limits on literals and field names.
    // This is arbitrary and conservative.
    static final int APPROXIMATE_DOC_SIZE = 16777000;

    //----------------------------------------------------------------------
    // First, a variety of minor information about the target database.
	@Override
    public boolean allProceduresAreCallable() throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

	@Override
    public boolean allTablesAreSelectable() throws SQLException {
        // TODO: permissions stuff here?
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public String getURL() throws SQLException {
        // TODO: return the URL.
        throw new SQLFeatureNotSupportedException("not implemented");
    }


    @Override
    public String getUserName() throws SQLException {
        // TODO: return the user name, if we can get this.
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true; // we are only read-only for now.
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false; // missing and NULL < all other values
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true; // missing and NULL < all other values
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false; // missing and NULL < all other values
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false; // missing and NULL < all other values
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return "MongoDB Atlas Data Lake";
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        // TODO: return the version.
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    @Override
    public String getDriverName() throws SQLException {
        return "MongoDB Atlas Data Lake JDBC Driver";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return MongoDriver.VERSION;
    }

    int getDriverMajorVersion() {
        return MongoDriver.MAJOR_VERSION;
    }

    int getDriverMinorVersion() {
        return MongoDriver.MINOR_VERSION;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        // No files are local on Atlas Data Lake
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        // No files are local on Atlas Data Lake
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        // Return false for now.
        return false;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }


    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return supportsMixedCaseIdentifiers();
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "`";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        // TODO: audit this.
        return "";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        // TODO: audit this.
        return "DIV,ABS,ACOS,ASIN,ATAN,ATAN2,CEIL,CEILING,CONV,COS,COT,CRC32,DEGREES,EXP,FLOOR,GREATEST,LEAST,LN,LOG,"
            + "LOG10,LOG2,MOD,OCT,PI,POW,POWER,RADIANS,RAND,ROUND,SIGN,SIN,SQRT,TAN,TRUNCATE";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        // TODO: audit this.
        return "";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        // TODO: audit this.
        return "";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        // TODO: audit this.
        return "";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        // TODO: audit this.
        return "";
    }


    //--------------------------------------------------------------------
    // Functions describing which features are supported.

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }



    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }


    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsConvert() throws SQLException {
        return true;
    }



    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        // TODO: really audit me.
        return true;
    }


    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }



    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        // We don't support transactions for now.
        return false;
    }


    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        // If this isn't true, it's a bug.
        return true;
    }


    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        // If this isn't true, it's a bug.
        return true;
    }


    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        // If it does not, this is a bug.
        return true;
    }


    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        // If it does not, this is a bug.
        return true;
    }


    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }



    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }


    @Override
    public String getSchemaTerm() throws SQLException {
        return "";
    }


    @Override
    public String getProcedureTerm() throws SQLException {
        return "procedure";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "database";
    }


    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }


    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }


    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        // at least when we support procedure calls. Also A => B and !A ==> true.
        return true;
    }


    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        // at least when we support table definitions. Also A => B and !A ==> true.
        return true;
    }


    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        // at least when we support index definitions. Also A => B and !A ==> true.
        return true;
    }


    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        // at least when we support privilege definitions. Also A => B and !A ==> true.
        return true;
    }



    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }



    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }



    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}


    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }



    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }



    @Override
    public boolean supportsUnion() throws SQLException {
        // For now.
        return false;
    }


    @Override
    public boolean supportsUnionAll() throws SQLException {
        // For now.
        return false;
    }



    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return true;
    }


    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return true;
    }




    //----------------------------------------------------------------------
    // The following group of methods exposes various limitations
    // based on the target database with the current driver.
    // Unless otherwise specified, a result of zero means there is no
    // limit, or the limit is not known.
    int getMaxBinaryLiteralLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }


    int getMaxCharLiteralLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }


    int getMaxColumnNameLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }


    int getMaxColumnsInGroupBy() throws SQLException {
        // TODO: check.
        return 0;
    }



    int getMaxColumnsInIndex() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxColumnsInOrderBy() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxColumnsInSelect() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxColumnsInTable() throws SQLException {
        // TODO: check.
        return 0;
    }



    int getMaxConnections() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxCursorNameLength() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxIndexLength() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxSchemaNameLength() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxProcedureNameLength() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxCatalogNameLength() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxRowSize() throws SQLException {
        // TODO: check.
        return APPROXIMATE_DOC_SIZE;
    }


    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }


    int getMaxStatementLength() throws SQLException {
        // TODO: check.
        return APPROXIMATE_DOC_SIZE;
    }


    int getMaxStatements() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxTableNameLength() throws SQLException {
         return APPROXIMATE_DOC_SIZE;
    }


    int getMaxTablesInSelect() throws SQLException {
        // TODO: check.
        return 0;
    }


    int getMaxUserNameLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }


    //----------------------------------------------------------------------

    int getDefaultTransactionIsolation() throws SQLException {
        return java.sql.Connection.TRANSACTION_NONE;
    }


    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }


    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return level == java.sql.Connection.TRANSACTION_NONE;
    }


    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly()

        throws SQLException;



    @Override
    public boolean dataDefinitionCausesTransactionCommit()

        throws SQLException;



    @Override
    public boolean dataDefinitionIgnoredInTransactions()

        throws SQLException;



    ResultSet getProcedures(String catalog, String schemaPattern,

                            @Override
    public String procedureNamePattern) throws SQLException;



    int procedureResultUnknown  = 0;



    int procedureNoResult               = 1;



    int procedureReturnsResult  = 2;



    ResultSet getProcedureColumns(String catalog,

                                  @Override
    public String schemaPattern,

                                  @Override
    public String procedureNamePattern,

                                  @Override
    public String columnNamePattern) throws SQLException;



    int procedureColumnUnknown = 0;



    int procedureColumnIn = 1;



    int procedureColumnInOut = 2;



    int procedureColumnOut = 4;


    int procedureColumnReturn = 5;



    int procedureColumnResult = 3;



    int procedureNoNulls = 0;



    int procedureNullable = 1;



    int procedureNullableUnknown = 2;




    ResultSet getTables(String catalog, String schemaPattern,

                        @Override
    public String tableNamePattern, String types[]) throws SQLException;



    ResultSet getSchemas() throws SQLException;



    ResultSet getCatalogs() throws SQLException;



    ResultSet getTableTypes() throws SQLException;



    ResultSet getColumns(String catalog, String schemaPattern,

                         @Override
    public String tableNamePattern, String columnNamePattern)

        throws SQLException;



    int columnNoNulls = 0;



    int columnNullable = 1;



    int columnNullableUnknown = 2;



    ResultSet getColumnPrivileges(String catalog, String schema,

                                  @Override
    public String table, String columnNamePattern) throws SQLException;



    ResultSet getTablePrivileges(String catalog, String schemaPattern,

                                 @Override
    public String tableNamePattern) throws SQLException;



    ResultSet getBestRowIdentifier(String catalog, String schema,

                                   @Override
    public String table, int scope, boolean nullable) throws SQLException;



    int bestRowTemporary   = 0;



    int bestRowTransaction = 1;



    int bestRowSession     = 2;



    int bestRowUnknown  = 0;



    int bestRowNotPseudo        = 1;



    int bestRowPseudo   = 2;



    ResultSet getVersionColumns(String catalog, String schema,

                                @Override
    public String table) throws SQLException;



    int versionColumnUnknown    = 0;



    int versionColumnNotPseudo  = 1;



    int versionColumnPseudo     = 2;



    ResultSet getPrimaryKeys(String catalog, String schema,

                             @Override
    public String table) throws SQLException;



    ResultSet getImportedKeys(String catalog, String schema,

                              @Override
    public String table) throws SQLException;



    int importedKeyCascade      = 0;



    int importedKeyRestrict = 1;



    int importedKeySetNull  = 2;



    int importedKeyNoAction = 3;



    int importedKeySetDefault  = 4;



    int importedKeyInitiallyDeferred  = 5;



    int importedKeyInitiallyImmediate  = 6;



    int importedKeyNotDeferrable  = 7;



    ResultSet getExportedKeys(String catalog, String schema,

                              @Override
    public String table) throws SQLException;



    ResultSet getCrossReference(

                                @Override
    public String parentCatalog, String parentSchema, String parentTable,

                                @Override
    public String foreignCatalog, String foreignSchema, String foreignTable

                                ) throws SQLException;



    ResultSet getTypeInfo() throws SQLException;



    int typeNoNulls = 0;



    int typeNullable = 1;



    int typeNullableUnknown = 2;



    int typePredNone = 0;



    int typePredChar = 1;



    int typePredBasic = 2;



    int typeSearchable  = 3;



    ResultSet getIndexInfo(String catalog, String schema, String table,

                           @Override
    public boolean unique, boolean approximate)

        throws SQLException;



    short tableIndexStatistic = 0;



    short tableIndexClustered = 1;



    short tableIndexHashed    = 2;



    short tableIndexOther     = 3;


    //--------------------------JDBC 2.0-----------------------------



    @Override
    public boolean supportsResultSetType(int type) throws SQLException;



    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency)

        throws SQLException;



    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException;



    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException;



    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException;



    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException;



    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException;



    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException;



    @Override
    public boolean updatesAreDetected(int type) throws SQLException;



    @Override
    public boolean deletesAreDetected(int type) throws SQLException;



    @Override
    public boolean insertsAreDetected(int type) throws SQLException;



    @Override
    public boolean supportsBatchUpdates() throws SQLException;



    ResultSet getUDTs(String catalog, String schemaPattern,

                      @Override
    public String typeNamePattern, int[] types)

        throws SQLException;



    Connection getConnection() throws SQLException;


    // ------------------- JDBC 3.0 -------------------------



    @Override
    public boolean supportsSavepoints() throws SQLException;



    @Override
    public boolean supportsNamedParameters() throws SQLException;



    @Override
    public boolean supportsMultipleOpenResults() throws SQLException;



    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException;



    ResultSet getSuperTypes(String catalog, String schemaPattern,

                            @Override
    public String typeNamePattern) throws SQLException;



    ResultSet getSuperTables(String catalog, String schemaPattern,

                             @Override
    public String tableNamePattern) throws SQLException;



    short attributeNoNulls = 0;



    short attributeNullable = 1;



    short attributeNullableUnknown = 2;



    ResultSet getAttributes(String catalog, String schemaPattern,

                            @Override
    public String typeNamePattern, String attributeNamePattern)

        throws SQLException;



    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException;



    int getResultSetHoldability() throws SQLException;



    int getDatabaseMajorVersion() throws SQLException;



    int getDatabaseMinorVersion() throws SQLException;



    int getJDBCMajorVersion() throws SQLException;



    int getJDBCMinorVersion() throws SQLException;



    int sqlStateXOpen = 1;



    int sqlStateSQL = 2;


     /**
     *  A possible return value for the method
     * <code>DatabaseMetaData.getSQLStateType</code> which is used to indicate
     * whether the value returned by the method
     * <code>SQLException.getSQLState</code> is an SQL99 SQLSTATE value.
     * <P>
     * <b>Note:</b>This constant remains only for compatibility reasons. Developers
     * should use the constant <code>sqlStateSQL</code> instead.
     *
     * @since 1.4

    int sqlStateSQL99 = sqlStateSQL;



    int getSQLStateType() throws SQLException;



    @Override
    public boolean locatorsUpdateCopy() throws SQLException;



    @Override
    public boolean supportsStatementPooling() throws SQLException;


    //------------------------- JDBC 4.0 -----------------------------------



    RowIdLifetime getRowIdLifetime() throws SQLException;



    ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException;



    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException;



    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException;

        /**
         * Retrieves a list of the client info properties
         * that the driver supports.  The result set contains the following columns
         *
         * <ol>
         * <li><b>NAME</b> String{@code =>} The name of the client info property<br>
         * <li><b>MAX_LEN</b> int{@code =>} The maximum length of the value for the property<br>
         * <li><b>DEFAULT_VALUE</b> String{@code =>} The default value of the property<br>
         * <li><b>DESCRIPTION</b> String{@code =>} A description of the property.  This will typically
         *                                              contain information as to where this property is
         *                                              stored in the database.
         * </ol>
         * <p>
         * The <code>ResultSet</code> is sorted by the NAME column
         *
         * @return      A <code>ResultSet</code> object; each row is a supported client info
         * property
         *
         *  @exception SQLException if a database access error occurs
         *
         * @since 1.6
         */

        ResultSet getClientInfoProperties() throws SQLException;



    ResultSet getFunctions(String catalog, String schemaPattern,
                           @Override
    public String functionNamePattern) throws SQLException;


    ResultSet getFunctionColumns(String catalog,
                                  @Override
    public String schemaPattern,
                                  @Override
    public String functionNamePattern,
                                  @Override
    public String columnNamePattern) throws SQLException;




    int functionColumnUnknown = 0;



    int functionColumnIn = 1;



    int functionColumnInOut = 2;



    int functionColumnOut = 3;


    int functionReturn = 4;


    int functionColumnResult = 5;




    int functionNoNulls = 0;



    int functionNullable = 1;



    int functionNullableUnknown = 2;



    int functionResultUnknown   = 0;



    int functionNoTable         = 1;


    int functionReturnsTable    = 2;


    //--------------------------JDBC 4.1 -----------------------------

    ResultSet getPseudoColumns(String catalog, String schemaPattern,

                         @Override
    public String tableNamePattern, String columnNamePattern)

        throws SQLException;


    @Override
    public boolean  generatedKeyAlwaysReturned() throws SQLException;


    //--------------------------JDBC 4.2 -----------------------------

    default long getMaxLogicalLobSize() throws SQLException {

        return 0;

    }

    default boolean supportsRefCursors() throws SQLException{
        return false;
    }


    // JDBC 4.3
    default boolean supportsSharding() throws SQLException {

        return false;

    }
}
