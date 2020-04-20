package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class MongoDatabaseMetaData implements DatabaseMetaData {

    static Column newColumn(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            BsonValue value) {
        Column c = new Column();
        c.database = database;
        c.table = table;
        c.tableAlias = tableAlias;
        c.column = column;
        c.columnAlias = columnAlias;
        c.value = value;
        return c;
    }

    MongoConnection conn;

    public MongoDatabaseMetaData(MongoConnection conn) {
        this.conn = conn;
    }

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
        return conn.getURL();
    }

    @Override
    public String getUserName() throws SQLException {
        return conn.getUser();
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
        // This is what ADL currently returns.
        return "3.6.0";
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
        // No procedures so we always return an empty result set.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern)
            throws SQLException {
        // No procedures so we always return an empty result set.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String types[])
            throws SQLException {
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        // We will never support schemata.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        MongoResultDoc row = new MongoResultDoc();
        ArrayList<MongoResultDoc> rows = new ArrayList<>(1);
        row.values.add(newColumn("", "", "", "TABLE_TYPE", "TABLE_TYPE", new BsonString("TABLE")));
        rows.add(row);
        return new MongoResultSet(null, new MongoExplicitCursor(rows), true);
    }

    private final String dataTypeCase =
            ""
                    + "case DATA_TYPE "
                    + "    when 'array'    then "
                    + Types.NULL
                    + "    when 'binData'  then "
                    + Types.NULL
                    + "    when 'bool'     then "
                    + Types.BIT
                    + "    when 'date'     then "
                    + Types.TIMESTAMP
                    + "    when 'null'     then "
                    + Types.NULL
                    + "    when 'decimal'  then "
                    + Types.DECIMAL
                    + "    when 'document' then "
                    + Types.NULL
                    + "    when 'double'   then "
                    + Types.DOUBLE
                    + "    when 'int'      then "
                    + Types.INTEGER
                    + "    when 'long'     then "
                    + Types.INTEGER
                    + "    when 'string'   then "
                    + Types.LONGVARCHAR
                    + "end";

    private final String nullableCase =
            ""
                    + "case IS_NULLABLE "
                    + "     when 'YES'     then "
                    + ResultSetMetaData.columnNullable
                    + "     when 'NO'      then "
                    + ResultSetMetaData.columnNoNulls
                    + "end";

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "select "
                                + "    TABLE_CATALOG as TABLE_CAT,"
                                + "    TABLE_SCHEMA as TABLE_SCHEM,"
                                + "    TABLE_NAME,"
                                + "    COLUMN_NAME,"
                                + dataTypeCase
                                + " as DATA_TYPE,"
                                + "    DATA_TYPE as TYPE_NAME ,"
                                + "    CHARACTER_MAXIMUM_LENGTH as COLUMN_SIZE,"
                                + "    0 as BUFFER_LENGTH,"
                                + "    NUMERIC_SCALE as DECIMAL_DIGITS,"
                                + "    NUMERIC_PRECISION as NUM_PREC_RADIX,"
                                + nullableCase
                                + " as NULLABLE,"
                                + "    COLUMN_COMMENT as REMARKS,"
                                + "    NULL as COLUMN_DEF,"
                                + "    0 as SQL_DATA_TYPE,"
                                + "    0 as SQL_DATETIME_SUB,"
                                + "    CHARACTER_MAXIMUM_LENGTH as CHAR_OCTET_LENGTH,"
                                + "    ORDINAL_POSITION,"
                                + "    IS_NULLABLE,"
                                + "    NULL as SCOPE_CATALOG,"
                                + "    NULL as SCOPE_SCHEMA,"
                                + "    NULL as SCOPE_TABLE,"
                                + "    0 as SOURCE_DATA_TYPE,"
                                + "    0 as IS_AUTOINCREMENT,"
                                + "    0 as IS_GENERATEDCOLUMN,"
                                + "from COLUMNS"
                                + "where "
                                + "    TABLE_SCHEMA like "
                                + schemaPattern
                                + "    AND COLUMN_NAME like "
                                + columnNamePattern);
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schema, String table, String columnNamePattern)
            throws SQLException {
        // TODO: Make this work with ADL, need information schema.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        // TODO: Make this work with ADL, need information schema.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        MongoResultDoc row = new MongoResultDoc();
        ArrayList<MongoResultDoc> rows = new ArrayList<>(1);
        row.values = new ArrayList<>();
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "SCOPE",
                        "SCOPE",
                        new BsonString("bestMongoResultDocSeassion")));
        row.values.add(newColumn("", "", "", "COLUMN_NAME", "COLUMN_NAME", new BsonString("_id")));
        row.values.add(
                newColumn("", "", "", "DATA_TYPE", "DATA_TYPE", new BsonInt32(Types.LONGVARCHAR)));
        row.values.add(newColumn("", "", "", "TYPE_NAME", "TYPE_NAME", new BsonString("string")));
        row.values.add(newColumn("", "", "", "COLUMN_SIZE", "COLUMN_SIZE", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "BUFFER_LENGTH", "BUFFER_LENGTH", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "DECIMAL_DIGITS", "DECIMAL_DIGITS", new BsonNull()));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "PSEUDO_COLUMN",
                        "PSEUDO_COLUMN",
                        new BsonInt32(bestRowNotPseudo)));
        return new MongoResultSet(null, new MongoExplicitCursor(rows), true);
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        // We do not have updates, so this will always be empty.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoResultDoc row = new MongoResultDoc();
        ArrayList<MongoResultDoc> rows = new ArrayList<>(1);
        row.values = new ArrayList<>();
        row.values.add(newColumn("", "", "", "TABLE_CAT", "TABLE_CAT", new BsonString(catalog)));
        row.values.add(newColumn("", "", "", "TABLE_SCHEM", "TABLE_SCHEM", new BsonNull()));
        row.values.add(newColumn("", "", "", "TABLE_NAME", "TABLE_NAME", new BsonString(table)));
        row.values.add(newColumn("", "", "", "COLUMN_NAME", "COLUMN_NAME", new BsonString("_id")));
        row.values.add(newColumn("", "", "", "KEY_SEQ", "KEY_SEQ", new BsonInt32(1)));
        row.values.add(newColumn("", "", "", "PK_NAME", "PK_NAME", new BsonNull()));
        return new MongoResultSet(null, new MongoExplicitCursor(rows), true);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
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
        // We do not have foreign keys, so this will always be empty.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        // TODO!
        throw new SQLFeatureNotSupportedException("Not implemented");
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        // We do not have indexes.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    //--------------------------JDBC 2.0-----------------------------
    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY && concurrency == ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        // We do not have updates.
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        // We do not have deletes.
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        // We do not have inserts.
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        // We do not have updates.
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        // We do not have deletes.
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        // We do not have inserts.
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        // We do not have updates.
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        // We do not have deletes.
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        // We do not have inserts.
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        // We do not have updates.
        return false;
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        // We do not have UDTs.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return conn;
    }

    // ------------------- JDBC 3.0 -------------------------

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        // This is related to keys generated automatically on inserts,
        // and we do not support inserts.
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        // We do not have UDTs.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        // We do not have SuperTables.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        // We do not have UDTs.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
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
        return MongoDriver.MAJOR_VERSION;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return MongoDriver.MINOR_VERSION;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 2;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        // This is what postgres returns.
        return sqlStateSQL;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        // It does not matter what return here. But we don't have locators
        // or allow them to be updated.
        return false;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    //------------------------- JDBC 4.0 -----------------------------------

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        // We will never support schemata.
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        // This is related to using stored procedure escape syntax, which we do not support.
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        // No writes.
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        ArrayList<MongoResultDoc> rows = new ArrayList<>(4);

        MongoResultDoc row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(newColumn("", "", "", "NAME", "NAME", new BsonString("user")));
        row.values.add(newColumn("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString("database user for the connection")));
        rows.add(row);

        row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(newColumn("", "", "", "NAME", "NAME", new BsonString("password")));
        row.values.add(newColumn("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString("user password for the connection")));
        rows.add(row);

        row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(newColumn("", "", "", "NAME", "NAME", new BsonString("conversionMode")));
        row.values.add(newColumn("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString(
                                "conversionMode can be strict or relaxed. When strict, "
                                        + "failing conversions result in Exceptions. When relaxed, "
                                        + "failing conversions result in NULL values.")));
        rows.add(row);

        row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(newColumn("", "", "", "NAME", "NAME", new BsonString("database")));
        row.values.add(newColumn("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(newColumn("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                newColumn(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString("database to connect to")));
        rows.add(row);

        return new MongoResultSet(null, new MongoExplicitCursor(rows), true);
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {
        // TODO: Finish (need to figure out what patterns should look like)"
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        // TODO: Finish.
        throw new SQLFeatureNotSupportedException("Not implemented.");
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        // We do not support pseudoColumns (hidden columns).
        return new MongoResultSet(
                null, new MongoExplicitCursor(new ArrayList<MongoResultDoc>()), true);
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        // We do not have generated keys.
        return false;
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
