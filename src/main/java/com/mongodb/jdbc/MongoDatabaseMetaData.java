package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

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
        return true;
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
        // TODO: return the version. Need to discuss.
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

    @Override
    public int getDriverMajorVersion() {
        return MongoDriver.MAJOR_VERSION;
    }

    @Override
    public int getDriverMinorVersion() {
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
        return "ADDDATE,"
                + "AUTO_INCREMENT,"
                + "BINLOG,"
                + "BOOL,"
                + "BTREE,"
                + "CHANGE,"
                + "CHANNEL,"
                + "CHARSET,"
                + "CODE,"
                + "COLUMNS,"
                + "COMMENT,"
                + "COMMITTED,"
                + "DATABASE,"
                + "DATABASES,"
                + "DATETIME,"
                + "DATE_ADD,"
                + "DATE_SUB,"
                + "DAY_HOUR,"
                + "DAY_MICROSECOND,"
                + "DAY_MINUTE,"
                + "DAY_SECOND,"
                + "DBS,"
                + "DISABLE,"
                + "DIV,"
                + "DUAL,"
                + "ENABLE,"
                + "ENGINE,"
                + "ENGINES,"
                + "ENUM,"
                + "ERRORS,"
                + "EVENT,"
                + "EVENTS,"
                + "EXPLAIN,"
                + "EXTENDED,"
                + "FIELDS,"
                + "FLUSH,"
                + "FN,"
                + "FORCE,"
                + "FORMAT,"
                + "FULLTEXT,"
                + "GRANTS,"
                + "GROUP_CONCAT,"
                + "HASH,"
                + "HOSTS,"
                + "HOUR_MICROSECOND,"
                + "HOUR_MINUTE,"
                + "HOUR_SECOND,"
                + "IGNORE,"
                + "INDEX,"
                + "INDEXES,"
                + "JSON,"
                + "KEYS,"
                + "KILL,"
                + "LIMIT,"
                + "LOCK,"
                + "LOGS,"
                + "LONGTEXT,"
                + "LOW_PRIORITY,"
                + "MASTER,"
                + "MEDIUMBLOB,"
                + "MEDIUMTEXT,"
                + "MICROSECOND,"
                + "MINUS,"
                + "MINUTE_MICROSECOND,"
                + "MINUTE_SECOND,"
                + "MOD,"
                + "MODIFY,"
                + "MUTEX,"
                + "OBJECTID,"
                + "OFF,"
                + "OFFSET,"
                + "OJ,"
                + "PARTITIONS,"
                + "PLUGINS,"
                + "PROCESSLIST,"
                + "PROFILE,"
                + "PROFILES,"
                + "PROXY,"
                + "QUARTER,"
                + "QUERY,"
                + "REGEXP,"
                + "RELAYLOG,"
                + "RENAME,"
                + "REPEATABLE,"
                + "RLIKE,"
                + "SAMPLE,"
                + "SCHEMAS,"
                + "SECOND_MICROSECOND,"
                + "SEPARATOR,"
                + "SERIAL,"
                + "SERIALIZABLE,"
                + "SHOW,"
                + "SIGNED,"
                + "SLAVE,"
                + "SQL_BIGINT,"
                + "SQL_DATE,"
                + "SQL_DOUBLE,"
                + "SQL_TIMESTAMP,"
                + "SQL_TSI_DAY,"
                + "SQL_TSI_HOUR,"
                + "SQL_TSI_MINUTE,"
                + "SQL_TSI_MONTH,"
                + "SQL_TSI_QUARTER,"
                + "SQL_TSI_SECOND,"
                + "SQL_TSI_WEEK,"
                + "SQL_TSI_YEAR,"
                + "SQL_VARCHAR,"
                + "STATUS,"
                + "STORAGE,"
                + "STRAIGHT_JOIN,"
                + "SUBDATE,"
                + "SUBSTR,"
                + "TABLES,"
                + "TEXT,"
                + "TIMESTAMPADD,"
                + "TIMESTAMPDIFF,"
                + "TINYINT,"
                + "TINYTEXT,"
                + "TRADITIONAL,"
                + "TRIGGERS,"
                + "UNCOMMITTED,"
                + "UNLOCK,"
                + "UNSIGNED,"
                + "USE,"
                + "UTC_DATE,"
                + "UTC_TIMESTAMP,"
                + "VARIABLES,"
                + "WARNINGS,"
                + "WEEK,"
                + "XOR,"
                + "YEAR_MONTH";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return "ACOS,"
                + "ASIN,"
                + "ATAN,"
                + "ATAN2,"
                + "CEIL,"
                + "COS,"
                + "COT,"
                + "DEGREES,"
                + "EXP,"
                + "FLOOR,"
                + "LN,"
                + "LOG,"
                + "LOG10,"
                + "LOG2,"
                + "MOD,"
                + "PI,"
                + "POW,"
                + "ROUND,"
                + "SIGN,"
                + "SIN,"
                + "SQRT,"
                + "TAN,"
                + "TRUNCATE";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return "ASCII,"
                + "CHAR,"
                + "CHARACTERLENGTH,"
                + "CONCAT,"
                + "CONCATWS,"
                + "ELT,"
                + "INSERT,"
                + "INSTR,"
                + "INTERVAL,"
                + "LEFT,"
                + "LENGTH,"
                + "LOCATE,"
                + "LPAD,"
                + "LTRIM,"
                + "MD5,"
                + "MID,"
                + "REPEAT,"
                + "REPLACE,"
                + "REVERSE,"
                + "RIGHT,"
                + "RPAD,"
                + "RTRIM,"
                + "SPACE,"
                + "SUBSTRING,"
                + "SUBSTRINGINDEX,"
                + "TRIM,"
                + "UCASE";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "ACOS,"
                + "ASCII,"
                + "ASIN,"
                + "ATAN,"
                + "ATAN2,"
                + "CEIL,"
                + "CHAR,"
                + "CHARACTERLENGTH,"
                + "COALESCE,"
                + "CONCAT,"
                + "CONCATWS,"
                + "CONNECTIONID,"
                + "CONV,"
                + "CONVERT,"
                + "COS,"
                + "COT,"
                + "CURRENTDATE,"
                + "CURRENTTIMESTAMP,"
                + "CURTIME,"
                + "DATABASE,"
                + "DATE,"
                + "DATEADD,"
                + "DATEDIFF,"
                + "DATEFORMAT,"
                + "DATESUB,"
                + "DAYNAME,"
                + "DAYOFMONTH,"
                + "DAYOFWEEK,"
                + "DAYOFYEAR,"
                + "DEGREES,"
                + "ELT,"
                + "EXP,"
                + "EXTRACT,"
                + "FIELD,"
                + "FLOOR,"
                + "FROMDAYS,"
                + "FROMUNIXTIME,"
                + "GREATEST,"
                + "HOUR,"
                + "IF,"
                + "IFNULL,"
                + "INSERT,"
                + "INSTR,"
                + "INTERVAL,"
                + "LASTDAY,"
                + "LCASE,"
                + "LEAST,"
                + "LEFT,"
                + "LENGTH,"
                + "LN,"
                + "LOCATE,"
                + "LOG,"
                + "LOG10,"
                + "LOG2,"
                + "LPAD,"
                + "LTRIM,"
                + "MAKEDATE,"
                + "MD5,"
                + "MICROSECOND,"
                + "MID,"
                + "MINUTE,"
                + "MOD,"
                + "MONTH,"
                + "MONTHNAME,"
                + "NOPUSHDOWN,"
                + "NULLIF,"
                + "PI,"
                + "POW,"
                + "QUARTER,"
                + "RADIANS,"
                + "RAND,"
                + "REPEAT,"
                + "REPLACE,"
                + "REVERSE,"
                + "RIGHT,"
                + "ROUND,"
                + "RPAD,"
                + "RTRIM,"
                + "SECOND,"
                + "SIGN,"
                + "SIN,"
                + "SLEEP,"
                + "SPACE,"
                + "SQRT,"
                + "STRTODATE,"
                + "SUBSTRING,"
                + "SUBSTRINGINDEX,"
                + "TAN,"
                + "TIMEDIFF,"
                + "TIMETOSEC,"
                + "TIMESTAMP,"
                + "TIMESTAMPADD,"
                + "TIMESTAMPDIFF,"
                + "TODAYS,"
                + "TOSECONDS,"
                + "TRIM,"
                + "TRUNCATE,"
                + "UCASE,"
                + "UNIXTIMESTAMP,"
                + "USER,"
                + "UTCDATE,"
                + "UTCTIMESTAMP,"
                + "VERSION,"
                + "WEEK,"
                + "WEEKDAY,"
                + "YEAR,"
                + "YEARWEEK";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "CURRENTDATE,"
                + "CURRENTTIMESTAMP,"
                + "CURTIME,"
                + "DATE,"
                + "DATEADD,"
                + "DATEDIFF,"
                + "DATEFORMAT,"
                + "DATESUB,"
                + "DAYNAME,"
                + "DAYOFMONTH,"
                + "DAYOFWEEK,"
                + "DAYOFYEAR,"
                + "EXTRACT,"
                + "FROMDAYS,"
                + "FROMUNIXTIME,"
                + "LASTDAY,"
                + "MAKEDATE,"
                + "MICROSECOND,"
                + "MINUTE,"
                + "MONTH,"
                + "MONTHNAME,"
                + "QUARTER,"
                + "SECOND,"
                + "STRTODATE,"
                + "TIMEDIFF,"
                + "TIMETOSEC,"
                + "TIMESTAMP,"
                + "TIMESTAMPADD,"
                + "TIMESTAMPDIFF,"
                + "TODAYS,"
                + "TOSECONDS,"
                + "UNIXTIMESTAMP,"
                + "UTCDATE,"
                + "UTCTIMESTAMP,"
                + "WEEK,"
                + "WEEKDAY,"
                + "YEAR,"
                + "YEARWEEK";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
		// Retrieves all the "extra" characters that can be used in unquoted identifier names (those beyond a-z, A-Z, 0-9 and _).
        return ".";
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
		// We do not support schemata.
        return "";
    }

    @Override
    public String getProcedureTerm() throws SQLException {
		// We do not support procedures.
        return "";
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
		// We don't support schemata.
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
		// We don't support schemata.
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
		// We don't support schemata.
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		// We don't support schemata.
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		// We don't support schemata.
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
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
        return true;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        // For now.
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		// Though we don't support commit.
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		// Though we don't support rollback.
        return true;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		// Though we don't support commit.
        return true;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		// Though we don't support rollback.
        return true;
    }

    //----------------------------------------------------------------------
    // The following group of methods exposes various limitations
    // based on the target database with the current driver.
    // Unless otherwise specified, a result of zero means there is no
    // limit, or the limit is not known.
    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
		// No specific max size, though it would be limited by max document size.
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        // MongoDB has no limit in 4.2+. Datalake doesn't support indexes, yet,
		// but returning 0 is fine.
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
		// The only limit would be based on document size.
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
		// The only limit would be based on document size.
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return APPROXIMATE_DOC_SIZE;
    }

    //----------------------------------------------------------------------

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
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
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return new MongoResultSet(null, new MongoCursor<Row>(), true);
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String types[])
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schema, String table, String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getCrossReference(
            String parentCatalog,
            String parentSchema,
            String parentTable,
            String foreignCatalog,
            String foreignSchema,
            String foreignTable)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    //--------------------------JDBC 2.0-----------------------------
    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    // ------------------- JDBC 3.0 -------------------------

    @Override
    public boolean supportsSavepoints() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public int getSQLStateType() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    //------------------------- JDBC 4.0 -----------------------------------

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    // java.sql.Wrapper impl
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
