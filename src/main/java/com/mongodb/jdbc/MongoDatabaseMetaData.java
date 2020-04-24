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
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class MongoDatabaseMetaData implements DatabaseMetaData {

    MongoConnection conn;

    public MongoDatabaseMetaData(MongoConnection conn) {
        this.conn = conn;
    }

    public static String escapeString(String value) {
        String escaped = value.replace("'", "''");
        return escaped.replace("\\", "\\\\");
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
        return conn.getServerVersion();
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
        // These come directly from the mongosqld keywords file.
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

    private static final String[] systemFunctionNames;
    private static final String systemFunctionNamesString;
    private static final String numericFunctionsString;
    private static final String stringFunctionsString;
    private static final String dateFunctionsString;

    static {
        systemFunctionNames = new String[MongoSystemFunction.systemFunctions.length];
        for (int i = 0; i < systemFunctionNames.length; ++i) {
            systemFunctionNames[i] = MongoSystemFunction.systemFunctions[i].name;
        }
        systemFunctionNamesString = String.join(",", systemFunctionNames);
        ArrayList<String> numericFunctions = new ArrayList<>(systemFunctionNames.length);
        ArrayList<String> stringFunctions = new ArrayList<>(systemFunctionNames.length);
        ArrayList<String> dateFunctions = new ArrayList<>(systemFunctionNames.length);

        HashSet<String> numericFunctionSet = new HashSet<>(systemFunctionNames.length);
        HashSet<String> stringFunctionSet = new HashSet<>(systemFunctionNames.length);
        HashSet<String> dateFunctionSet = new HashSet<>(systemFunctionNames.length);
        for (int i = 0; i < MongoSystemFunction.systemFunctions.length; ++i) {
            for (String argType : MongoSystemFunction.systemFunctions[i].argTypes) {
                String name = MongoSystemFunction.systemFunctions[i].name;
                if (argType == null) {
                    continue;
                }
                switch (argType) {
                    case "string":
                        if (stringFunctionSet.contains(name)) {
                            break;
                        }
                        stringFunctions.add(name);
                        stringFunctionSet.add(name);
                        break;
                    case "numeric":
                    case "long":
                    case "int":
                    case "double":
                    case "decimal":
                        if (numericFunctionSet.contains(name)) {
                            break;
                        }
                        numericFunctions.add(name);
                        numericFunctionSet.add(name);
                        break;
                    case "date":
                        if (dateFunctionSet.contains(name)) {
                            break;
                        }
                        dateFunctions.add(name);
                        dateFunctionSet.add(name);
                        break;
                }
            }
        }
        numericFunctionsString = String.join(",", numericFunctions);
        stringFunctionsString = String.join(",", stringFunctions);
        dateFunctionsString = String.join(",", dateFunctions);
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return numericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return stringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return systemFunctionNamesString;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return dateFunctionsString;
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

    private String patternCond(String colName, String pattern) {
        if (pattern == null) {
            return " 1 "; //just return a true condition.
        }
        return " " + colName + " like '" + escapeString(pattern) + "' ";
    }

    private String equalsCond(String colName, String literal) {
        if (literal == null) {
            return " 1 "; //just return a true condition.
        }
        return " " + colName + " = '" + escapeString(literal) + "' ";
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String types[])
            throws SQLException {
        Statement stmt = conn.createStatement();
        // We ignore types because we only have one kind of table type.
        return stmt.executeQuery(
                "select "
                        + "    TABLE_CATALOG as TABLE_CAT, "
                        + "    TABLE_SCHEMA as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    TABLE_TYPE, "
                        + "    NULL as REMARKS, "
                        + "    NULL as TYPE_CAT, "
                        + "    NULL as TYPE_SCHEM, "
                        + "    NULL as TYPE_NAME, "
                        + "    NULL as SELF_REFERENCING_COL_NAME, "
                        + "    NULL as REF_GENERATION "
                        + "from INFORMATION_SCHEMA.TABLES "
                        + "where"
                        + patternCond("TABLE_SCHEMA", schemaPattern)
                        + "    and"
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + " order by TABLE_TYPE, TABLE_CAT, TABLE_SCHEMA, TABLE_NAME");
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    SCHEMA_NAME as TABLE_SCHEM, "
                        + "    CATALOG_NAME as TABLE_CAT "
                        + "from INFORMATION_SCHEMA.SCHEMATA "
                        + "order by TABLE_CAT, TABLE_SCHEM");
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        MongoResultDoc doc = new MongoResultDoc();
        ArrayList<MongoResultDoc> docs = new ArrayList<>(1);
        doc.values = new ArrayList<>(1);
        doc.values.add(new Column("", "", "", "TABLE_CAT", "TABLE_CAT", new BsonString("def")));
        docs.add(doc);
        return new MongoResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        MongoResultDoc doc = new MongoResultDoc();
        ArrayList<MongoResultDoc> docs = new ArrayList<>(1);
        doc.values = new ArrayList<>(1);
        doc.values.add(new Column("", "", "", "TABLE_TYPE", "TABLE_TYPE", new BsonString("TABLE")));
        docs.add(doc);
        return new MongoResultSet(null, new MongoExplicitCursor(docs), true);
    }

    public static String[] typeNames =
            new String[] {
                "null",
                "document",
                "binData",
                "numeric",
                "string",
                "varchar",
                "long",
                "int",
                "bigint",
                "date",
                "date",
                "double",
                "decimal",
            };

    public static final HashMap<String, Integer> typeNums = new HashMap<>();
    public static final HashMap<String, Integer> typePrecs = new HashMap<>();
    public static final HashMap<String, Integer> typeScales = new HashMap<>();
    public static final HashMap<String, Integer> typeBytes = new HashMap<>();

    static {
        for (String name : typeNames) {
            typeNums.put(name, typeNum(name));
            typePrecs.put(name, typePrec(name));
            typeScales.put(name, typeScale(name));
            typeBytes.put(name, typeBytes(name));
        }
    }

    public static int typeNum(String typeName) {
        if (typeName == null) {
            return Types.NULL;
        }
        switch (typeName) {
            case "null":
                return Types.NULL;
            case "bool":
                return Types.BIT;
            case "document":
                return Types.NULL;
            case "binData":
                return Types.BINARY;
            case "numeric":
                return Types.NUMERIC;
            case "string":
            case "varchar":
                return Types.LONGVARCHAR;
            case "long":
            case "bigint":
            case "int":
                return Types.INTEGER;
            case "date":
                return Types.TIMESTAMP;
            case "double":
                return Types.DOUBLE;
            case "decimal":
                return Types.DECIMAL;
        }
        return 0;
    }

    public static int typePrec(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 34;
            case "double":
                return 15;
            case "bigint":
            case "long":
                return 19;
            case "int":
                return 10;
            case "decimal":
                return 34;
        }
        return 0;
    }

    public static int typeScale(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 34;
            case "double":
                return 15;
            case "decimal":
                return 34;
        }
        return 0;
    }

    public static int typeBytes(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 16;
            case "double":
                return 8;
            case "bigint":
            case "long":
                return 8;
            case "int":
                return 4;
            case "decimal":
                return 16;
            case "bool":
                return 1;
        }
        return 0;
    }

    public static int typeRadix(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 10;
            case "double":
                return 2;
            case "long":
                return 2;
            case "bigint":
            case "int":
                return 2;
            case "decimal":
                return 10;
        }
        return 0;
    }

    private String getTypeCase(String col, HashMap<String, Integer> outs) {
        StringBuilder ret = new StringBuilder("case ");
        ret.append(col);
        ret.append("\n");
        for (String name : typeNames) {
            ret.append("when ");
            ret.append("'");
            ret.append(name);
            ret.append("' then ");
            ret.append(outs.get(name).toString());
            ret.append(" \n");
        }
        ret.append("end");
        return ret.toString();
    }

    private String getDataTypeNumCase(String col) {
        return getTypeCase(col, typeNums);
    }

    private String getDataTypePrecCase(String col) {
        return getTypeCase(col, typePrecs);
    }

    private String getDataTypeScaleCase(String col) {
        return getTypeCase(col, typeScales);
    }

    private String getDataTypeBytesCase(String col) {
        return getTypeCase(col, typeBytes);
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {

        final String nullableCase =
                ""
                        + "case IS_NULLABLE "
                        + "     when 'YES'     then "
                        + ResultSetMetaData.columnNullable
                        + "     when 'NO'      then "
                        + ResultSetMetaData.columnNoNulls
                        + " end";

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_CATALOG as TABLE_CAT, "
                        + "    TABLE_SCHEMA as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    COLUMN_NAME, "
                        + getDataTypeNumCase("DATA_TYPE")
                        + " as DATA_TYPE, "
                        + "    DATA_TYPE as TYPE_NAME, "
                        + "    CHARACTER_MAXIMUM_LENGTH as COLUMN_SIZE, "
                        + "    0 as BUFFER_LENGTH, "
                        + getDataTypeScaleCase("DATA_TYPE")
                        + " as DECIMAL_DIGITS, "
                        + getDataTypePrecCase("DATA_TYPE")
                        + "    as NUM_PREC_RADIX, "
                        + nullableCase
                        + " as NULLABLE, "
                        + "    '' as REMARKS, "
                        + "    NULL as COLUMN_DEF, "
                        + "    0 as SQL_DATA_TYPE, "
                        + "    0 as SQL_DATETIME_SUB, "
                        + getDataTypeBytesCase("DATA_TYPE")
                        + "    as CHAR_OCTET_LENGTH, "
                        + "    ORDINAL_POSITION, "
                        + "    IS_NULLABLE, "
                        + "    NULL as SCOPE_CATALOG, "
                        + "    NULL as SCOPE_SCHEMA, "
                        + "    NULL as SCOPE_TABLE, "
                        + "    0 as SOURCE_DATA_TYPE, "
                        + "    0 as IS_AUTOINCREMENT, "
                        + "    0 as IS_GENERATEDCOLUMN "
                        + "from INFORMATION_SCHEMA.COLUMNS "
                        + "where "
                        + patternCond("TABLE_SCHEMA", schemaPattern)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + "    and "
                        + patternCond("COLUMN_NAME", columnNamePattern));
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_CATALOG as TABLE_CAT, "
                        + "    TABLE_SCHEMA as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    COLUMN_NAME, "
                        + "    GRANTOR, "
                        + "    GRANTEE, "
                        + "    PRIVILEGE_TYPE as PRIVILEGE, "
                        + "    IS_GRANTABLE "
                        + "from INFORMATION_SCHEMA.COLUMN_PRIVILEGES "
                        + "where "
                        + patternCond("TABLE_SCHEMA", schemaPattern)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + "    and "
                        + patternCond("COLUMN_NAME", columnNamePattern));
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_CATALOG as TABLE_CAT, "
                        + "    TABLE_SCHEMA as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    GRANTOR, "
                        + "    GRANTEE, "
                        + "    PRIVILEGE_TYPE as PRIVILEGE, "
                        + "    IS_GRANTABLE "
                        + "from INFORMATION_SCHEMA.TABLE_PRIVILEGES "
                        + "where "
                        + patternCond("TABLE_SCHEMA", schemaPattern)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern));
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "NULL as SCOPE, "
                        + "c.COLUMN_NAME AS COLUMN_NAME, "
                        + getDataTypeNumCase("c.DATA_TYPE")
                        + " as DATA_TYPE, "
                        + "c.DATA_TYPE as TYPE_NAME, "
                        + getDataTypePrecCase("c.DATA_TYPE")
                        + " as COLUMN_SIZE, "
                        + "NULL as BUFFER_LENGTH, "
                        + getDataTypeScaleCase("c.DATA_TYPE")
                        + " as DECIMAL_DIGITS, "
                        + bestRowNotPseudo
                        + " as PSEUDO_COLUMN"
                        + " from INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu inner join INFORMATION_SCHEMA.COLUMNS as c "
                        + "on kcu.TABLE_SCHEMA = c.TABLE_SCHEMA "
                        + "and kcu.TABLE_NAME = c.TABLE_NAME "
                        + "and kcu.COLUMN_NAME = c.COLUMN_NAME "
                        + " inner join INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc "
                        + "on c.TABLE_SCHEMA = tc.TABLE_SCHEMA "
                        + "and c.TABLE_NAME = tc.TABLE_NAME "
                        + "where "
                        + equalsCond("kcu.TABLE_SCHEMA", schema)
                        + " and "
                        + equalsCond("kcu.TABLE_NAME", table)
                        + "  and tc.CONSTRAINT_TYPE = 'PRIMARY KEY'");
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
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "kcu.TABLE_CATALOG as TABLE_CAT, "
                        + "kcu.TABLE_SCHEMA as TABLE_SCHEM, "
                        + "kcu.TABLE_NAME as TABLE_NAME, "
                        + "kcu.COLUMN_NAME AS COLUMN_NAME, "
                        + "kcu.ORDINAL_POSITION as KEY_SEQ, "
                        + "kcu.CONSTRAINT_NAME as PK_NAME "
                        + "from INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu inner join INFORMATION_SCHEMA.TABLE_CONSTRAINTS as c "
                        + "on kcu.TABLE_SCHEMA = c.TABLE_SCHEMA "
                        + "and kcu.TABLE_NAME = c.TABLE_NAME "
                        + "where "
                        + equalsCond("kcu.TABLE_SCHEM", schema)
                        + "  and "
                        + equalsCond("kcu.TABLE_NAME", table)
                        + "  and c.CONSTRAINT_TYPE = 'PRIMARY KEY'");
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

    private MongoResultDoc getTypeInfoDoc(
            String typeName,
            int dataType,
            int precision,
            String literalPrefix,
            String literalSuffix,
            int nullable,
            boolean caseSensitive,
            int searchable,
            boolean unsigned,
            boolean fixedPrecScale,
            int minScale,
            int maxScale,
            int numPrecRadix) {
        BsonValue n = new BsonNull();
        MongoResultDoc doc = new MongoResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", new BsonString(typeName)));
        doc.values.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", new BsonInt32(dataType)));
        doc.values.add(new Column("", "", "", "PRECISION", "PRECISION", new BsonInt32(precision)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "LITERAL_PREFIX",
                        "LITERAL_PREFIX",
                        literalPrefix != null ? new BsonString(literalPrefix) : n));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "LITERAL_SUFFIX",
                        "LITERAL_SUFFIX",
                        literalSuffix != null ? new BsonString(literalSuffix) : n));
        doc.values.add(new Column("", "", "", "CREATE_PARAMS", "CREATE_PARAMS", n));
        doc.values.add(new Column("", "", "", "NULLABLE", "NULLABLE", new BsonInt32(nullable)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "CASE_SENSITIVE",
                        "CASE_SENSITIVE",
                        new BsonBoolean(caseSensitive)));
        doc.values.add(
                new Column("", "", "", "SEARCHABLE", "SEARCHABLE", new BsonInt32(searchable)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "UNSIGNED_ATTRIBUTE",
                        "UNSIGNED_ATTRIBUTE",
                        new BsonBoolean(unsigned)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "FIXED_PREC_SCALE",
                        "FIXED_PREC_SCALE",
                        new BsonBoolean(fixedPrecScale)));
        doc.values.add(
                new Column("", "", "", "AUTO_INCREMENT", "AUTO_INCREMENT", new BsonBoolean(false)));
        doc.values.add(new Column("", "", "", "LOCAL_TYPE_NAME", "LOCAL_TYPE_NAME", n));
        doc.values.add(
                new Column("", "", "", "MINIMUM_SCALE", "MINIMUM_SCALE", new BsonInt32(minScale)));
        doc.values.add(
                new Column("", "", "", "MAXIMUM_SCALE", "MAXIMUM_SCALE", new BsonInt32(maxScale)));
        doc.values.add(new Column("", "", "", "SQL_DATA_TYPE", "SQL_DATA_TYPE", new BsonInt32(0)));
        doc.values.add(
                new Column("", "", "", "SQL_DATETIME_SUB", "SQL_DATETIME_SUB", new BsonInt32(0)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "NUM_PREC_RADIX",
                        "NUM_PREC_RADIX",
                        new BsonInt32(numPrecRadix)));
        return doc;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        ArrayList<MongoResultDoc> docs = new ArrayList<>(11);

        docs.add(
                getTypeInfoDoc(
                        "binData", //typeName
                        Types.NULL, //dataType
                        0, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typePredNone, //seachable
                        false, //unsigned
                        false, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        0)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "bool", //typeName
                        Types.BIT, //dataType
                        1, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        true, //unsigned
                        false, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        0)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "date", //typeName
                        Types.TIMESTAMP, //dataType
                        24, //precision
                        "'", //literalPrefix
                        "'", //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        false, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        0)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "decimal", //typeName
                        Types.TIMESTAMP, //dataType
                        34, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        false, //fixedPrecScale
                        34, //minScale
                        34, //maxScale
                        10)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "double", //typeName
                        Types.TIMESTAMP, //dataType
                        15, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        false, //fixedPrecScale
                        15, //minScale
                        15, //maxScale
                        2)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "int", //typeName
                        Types.INTEGER, //dataType
                        10, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        true, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        2)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "long", //typeName
                        Types.INTEGER, //dataType
                        19, //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        false, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        true, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        2)); //numPrecRadix

        docs.add(
                getTypeInfoDoc(
                        "string", //typeName
                        Types.LONGVARCHAR, //dataType
                        0, //precision
                        "'", //literalPrefix
                        "'", //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        true, //caseSensitive
                        typeSearchable, //seachable
                        false, //unsigned
                        false, //fixedPrecScale
                        0, //minScale
                        0, //maxScale
                        0)); //numPrecRadix

        return new MongoResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "tc.TABLE_CATALOG as TABLE_CAT, "
                        + "tc.TABLE_SCHEMA as TABLE_SCHEM, "
                        + "tc.TABLE_NAME as TABLE_NAME, "
                        + "tc.CONSTRAINT_TYPE not in  ('PRIMARY KEY', 'UNIQUE') as NON_UNIQUE, "
                        + "tc.CONSTRAINT_CATALOG as INDEX_QUALIFIER, "
                        + "tc.CONSTRAINT_NAME as INDEX_NAME, "
                        + "tc.CONSTRAINT_TYPE as TYPE, "
                        + "kcu.ORDINAL_POSITION as ORDINAL_POSITION, "
                        + "kcu.COLUMN_NAME AS COLUMN_NAME, "
                        + "'A' as ASC_OR_DESC, "
                        + "NULL as CARDINALITY, "
                        + "NULL as PAGES, "
                        + "NULL as FILTER_CONDITION "
                        + "from INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc inner join "
                        + "     INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu "
                        + "  on  tc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA "
                        + "  and tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME "
                        + "  and tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA "
                        + "  and tc.TABLE_NAME = kcu.TABLE_NAME "
                        + "  and "
                        + equalsCond("tc.TABLE_SCHEMA", schema)
                        + "  and "
                        + equalsCond("tc.TABLE_NAME", table)
                        + ((unique)
                                ? "where tc.CONSTRAINT_TYPE in ('PRIMARY KEY', 'UNIQUE')"
                                : ""));
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
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
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
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    SCHEMA_NAME as TABLE_SCHEM, "
                        + "    CATALOG_NAME as TABLE_CAT "
                        + "from INFORMATION_SCHEMA.SCHEMATA "
                        + "where "
                        + patternCond("SCHEMA_NAME", schemaPattern)
                        + " order by TABLE_CAT, SCHEMA_NAME");
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
        row.values.add(new Column("", "", "", "NAME", "NAME", new BsonString("user")));
        row.values.add(new Column("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(
                new Column("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString("database user for the connection")));
        rows.add(row);

        row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(new Column("", "", "", "NAME", "NAME", new BsonString("password")));
        row.values.add(new Column("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(
                new Column("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "DESCRIPTION",
                        "DESCRIPTION",
                        new BsonString("user password for the connection")));
        rows.add(row);

        row = new MongoResultDoc();
        row.values = new ArrayList<>();
        row.values.add(new Column("", "", "", "NAME", "NAME", new BsonString("conversionMode")));
        row.values.add(new Column("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(
                new Column("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                new Column(
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
        row.values.add(new Column("", "", "", "NAME", "NAME", new BsonString("database")));
        row.values.add(new Column("", "", "", "MAX_LEN", "MAX_LEN", new BsonInt32(0)));
        row.values.add(
                new Column("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", new BsonString("")));
        row.values.add(
                new Column(
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

        ArrayList<MongoResultDoc> docs = new ArrayList<>(systemFunctionNames.length);
        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = Pattern.compile(functionNamePattern.replaceAll("%", ".*"));
        }
        for (MongoSystemFunction systemFunc : MongoSystemFunction.systemFunctions) {
            String functionName = systemFunc.name;
            String remarks = systemFunc.comment;
            if (!functionPatternRE.matcher(functionName).matches()) {
                continue;
            }
            MongoResultDoc doc = new MongoResultDoc();
            doc.values = new ArrayList<>(5);
            doc.values.add(
                    new Column("", "", "", "FUNCTION_CAT", "FUNCTION_CAT", new BsonString("def")));
            doc.values.add(
                    new Column("", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", new BsonNull()));
            doc.values.add(
                    new Column(
                            "",
                            "",
                            "",
                            "FUNCTION_NAME",
                            "FUNCTION_NAME",
                            new BsonString(functionName)));
            doc.values.add(
                    new Column(
                            "",
                            "",
                            "",
                            "REMARKS",
                            "REMARKS",
                            // perhaps at some point add comments explaining the function.
                            new BsonString(remarks)));
            doc.values.add(
                    new Column(
                            "",
                            "",
                            "",
                            "FUNCTION_TYPE",
                            "DEFAULT_VALUE",
                            new BsonInt32(functionNoTable)));
            doc.values.add(
                    new Column(
                            "",
                            "",
                            "",
                            "SPECIFIC_NAME",
                            "SPECIFIC_NAME",
                            new BsonString(functionName)));
            docs.add(doc);
        }

        return new MongoResultSet(null, new MongoExplicitCursor(docs), true);
    }

    private MongoResultDoc getFunctionColumnDoc(
            MongoSystemFunction systemFunc,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn) {
        BsonValue n = new BsonNull();
        String functionName = systemFunc.name;
        MongoResultDoc doc = new MongoResultDoc();
        doc.values = new ArrayList<>(17);
        doc.values.add(
                new Column("", "", "", "FUNCTION_CAT", "FUNCTION_CAT", new BsonString("def")));
        doc.values.add(new Column("", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", n));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "FUNCTION_NAME",
                        "FUNCTION_NAME",
                        new BsonString(functionName)));
        doc.values.add(
                new Column("", "", "", "COLUMN_NAME", "COLUMN_NAME", new BsonString(argName)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "COLUMN_TYPE",
                        "COLUMN_TYPE",
                        new BsonInt32(isReturnColumn ? functionReturn : functionColumnIn)));
        doc.values.add(
                new Column("", "", "", "DATA_TYPE", "DATA_TYPE", new BsonInt32(typeNum(argType))));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "TYPE_NAME",
                        "TYPE_NAME",
                        argType == null ? n : new BsonString(argType)));
        doc.values.add(
                new Column("", "", "", "PRECISION", "PRECISION", new BsonInt32(typePrec(argType))));
        doc.values.add(
                new Column("", "", "", "LENGTH", "LENGTH", new BsonInt32(typeBytes(argType))));
        doc.values.add(new Column("", "", "", "SCALE", "SCALE", new BsonInt32(typeBytes(argType))));
        doc.values.add(new Column("", "", "", "RADIX", "RADIX", new BsonInt32(typeRadix(argType))));
        doc.values.add(
                new Column("", "", "", "NULLABLE", "NULLABLE", new BsonInt32(functionNullable)));
        doc.values.add(
                new Column("", "", "", "REMARKS", "REMARKS", new BsonString(systemFunc.comment)));
        doc.values.add(
                new Column(
                        "",
                        "",
                        "",
                        "CHAR_OCTET_LENGTH",
                        "CHAR_OCTET_LENGTH",
                        new BsonInt32(typeBytes(argType))));
        doc.values.add(
                new Column("", "", "", "ORDINAL_POSITION", "ORDINAL_POSITION", new BsonInt32(i)));
        doc.values.add(new Column("", "", "", "IS_NULLABLE", "IS_NULLABLE", new BsonString("YES")));
        doc.values.add(
                new Column(
                        "", "", "", "SPECIFIC_NAME", "IS_NULLABLE", new BsonString(functionName)));
        return doc;
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        ArrayList<MongoResultDoc> docs = new ArrayList<>(systemFunctionNames.length);
        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = Pattern.compile(functionNamePattern.replaceAll("%", ".*"));
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = Pattern.compile(columnNamePattern.replaceAll("%", ".*"));
        }
        for (MongoSystemFunction systemFunc : MongoSystemFunction.systemFunctions) {
            String functionName = systemFunc.name;
            if (functionNamePatternRE != null
                    && !functionNamePatternRE.matcher(functionName).matches()) {
                continue;
            }
            int i = 0;
            for (String argType : systemFunc.argTypes) {
                // We don't have better names for our arguments, for the most part.
                ++i;
                String columnName = "arg" + i;
                if (columnNamePatternRE != null
                        && !columnNamePatternRE.matcher(columnName).matches()) {
                    continue;
                }
                MongoResultDoc doc =
                        getFunctionColumnDoc(systemFunc, i, columnName, argType, false);
                docs.add(doc);
            }
            String columnName = "argReturn";
            if (columnNamePatternRE == null || columnNamePatternRE.matcher(columnName).matches()) {
                MongoResultDoc doc =
                        getFunctionColumnDoc(
                                systemFunc, i, "argReturn", systemFunc.returnType, true);
                docs.add(doc);
            }
        }
        return new MongoResultSet(null, new MongoExplicitCursor(docs), true);
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
