package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public abstract class MongoDatabaseMetaData implements DatabaseMetaData {
    protected MongoConnection conn;
    protected String serverVersion;

    public MongoDatabaseMetaData(MongoConnection conn) {
        this.conn = conn;
    }

    public static String escapeString(String value) {
        String escaped = value.replace("'", "''");
        return escaped.replace("\\", "\\\\");
    }

    public static String toJavaPattern(String sqlPattern) {
        return sqlPattern.replaceAll("%", ".*").replaceAll("_", ".");
    }

    // Actual max size is 16777216, we reserve 216 for other bits of encoding,
    // since this value is used to set limits on literals and field names.
    // This is arbitrary and conservative.
    static final int APPROXIMATE_DOC_SIZE = 16777000;

    //----------------------------------------------------------------------
    // First, a variety of minor information about the target database.
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return true;
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
        if (serverVersion != null) {
            return serverVersion;
        }
        serverVersion = conn.getServerVersion();
        return serverVersion;
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
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return supportsMixedCaseIdentifiers();
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "`";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        // Retrieves all the "extra" characters that can be used in unquoted identifier names (those beyond a-z, A-Z, 0-9 and _).
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
        switch (toType) {
            case Types.ARRAY:
                return false;
            case Types.BLOB:
            case Types.BINARY:
            case Types.BIT:
            case Types.TIMESTAMP:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.INTEGER:
            case Types.LONGVARCHAR:
            case Types.NULL:
                return true;
        }
        return false;
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
        return "schema";
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        // We do not support procedures.
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
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
        return true;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        // at least when we support data manipulation calls. Also A => B and !A ==> true.
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
        return 255;
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

    public static String[] typeNames =
            new String[] {
                "null",
                "document",
                "binData",
                "numeric",
                "string",
                // Keep this for now or Tableau cannot figure out the types properly.
                "varchar",
                "long",
                // Keep this for now or Tableau cannot figure out the types properly.
                "bigint",
                "tinyint",
                "int",
                "datetime",
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
            case "int":
            case "bigint":
            case "tinyint":
                return Types.INTEGER;
            case "date":
            case "datetime":
                return Types.TIMESTAMP;
            case "double":
                return Types.DOUBLE;
            case "decimal":
                return Types.DECIMAL;
        }
        return 0;
    }

    public static Integer typePrec(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 34;
            case "double":
                return 15;
            case "long":
            case "bigint":
                return 19;
            case "int":
                return 10;
            case "decimal":
                return 34;
        }
        return 0;
    }

    public static Integer typeScale(String typeName) {
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
        return null;
    }

    public static Integer typeBytes(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 16;
            case "double":
                return 8;
            case "long":
            case "bigint":
            case "date":
            case "datatime":
                return 8;
            case "int":
                return 4;
            case "decimal":
                return 16;
            case "tinyint":
            case "bool":
                return 1;
        }
        return null;
    }

    public static Integer typeRadix(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName) {
            case "numeric":
                return 10;
            case "double":
                return 2;
            case "long":
            case "bigint":
                return 2;
            case "int":
                return 2;
            case "decimal":
                return 10;
        }
        return 0;
    }

    protected String getTypeCase(String col, HashMap<String, Integer> outs) {
        StringBuilder ret = new StringBuilder("case ");
        ret.append(col);
        ret.append("\n");
        for (String name : typeNames) {
            Integer out = outs.get(name);
            String range = out == null ? "NULL" : out.toString();
            ret.append("when ");
            ret.append("'");
            ret.append(name);
            ret.append("' then ");
            ret.append(range);
            ret.append(" \n");
        }
        ret.append("end");
        return ret.toString();
    }

    protected String getDataTypeNumCase(String col) {
        return getTypeCase(col, typeNums);
    }

    protected String getDataTypePrecCase(String col) {
        return getTypeCase(col, typePrecs);
    }

    protected String getDataTypeScaleCase(String col) {
        return getTypeCase(col, typeScales);
    }

    protected String getDataTypeBytesCase(String col) {
        return getTypeCase(col, typeBytes);
    }

    protected MongoResultDoc getTypeInfoMetaDoc() {

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "PRECISION", "PRECISION", "int"));
        metaDoc.columns.add(new Column("", "", "", "LITERAL_PREFIX", "LITERAL_PREFIX", "string"));
        metaDoc.columns.add(new Column("", "", "", "LITERAL_SUFFIX", "LITERAL_SUFFIX", "string"));
        metaDoc.columns.add(new Column("", "", "", "CREATE_PARAMS", "CREATE_PARAMS", "string"));
        metaDoc.columns.add(new Column("", "", "", "NULLABLE", "NULLABLE", "int"));
        metaDoc.columns.add(new Column("", "", "", "CASE_SENSITIVE", "CASE_SENSITIVE", "bool"));
        metaDoc.columns.add(new Column("", "", "", "SEARCHABLE", "SEARCHABLE", "int"));
        metaDoc.columns.add(
                new Column("", "", "", "UNSIGNED_ATTRIBUTE", "UNSIGNED_ATTRIBUTE", "bool"));
        metaDoc.columns.add(new Column("", "", "", "FIXED_PREC_SCALE", "FIXED_PREC_SCALE", "bool"));
        metaDoc.columns.add(new Column("", "", "", "AUTO_INCREMENT", "AUTO_INCREMENT", "bool"));
        metaDoc.columns.add(new Column("", "", "", "LOCAL_TYPE_NAME", "LOCAL_TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "MINIMUM_SCALE", "MINIMUM_SCALE", "int"));
        metaDoc.columns.add(new Column("", "", "", "MAXIMUM_SCALE", "MAXIMUM_SCALE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATA_TYPE", "SQL_DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATETIME_SUB", "SQL_DATETIME_SUB", "int"));
        metaDoc.columns.add(new Column("", "", "", "NUM_PREC_RADIX", "NUM_PREC_RADIX", "int"));

        return metaDoc;
    }

    protected MongoResultDoc getTypeInfoValuesDoc(
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
        doc.values.add(new BsonString(typeName));
        doc.values.add(new BsonInt32(dataType));
        doc.values.add(new BsonInt32(precision));
        doc.values.add(literalPrefix != null ? new BsonString(literalPrefix) : n);
        doc.values.add(literalSuffix != null ? new BsonString(literalSuffix) : n);
        doc.values.add(n);
        doc.values.add(new BsonInt32(nullable));
        doc.values.add(new BsonBoolean(caseSensitive));
        doc.values.add(new BsonInt32(searchable));
        doc.values.add(new BsonBoolean(unsigned));
        doc.values.add(new BsonBoolean(fixedPrecScale));
        doc.values.add(new BsonBoolean(false));
        doc.values.add(n);
        doc.values.add(new BsonInt32(minScale));
        doc.values.add(new BsonInt32(maxScale));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonInt32(numPrecRadix));

        return doc;
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
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        // This is related to using stored procedure escape syntax, which we do not support.
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        // No writes.
        return false;
    }

    protected MongoResultDoc getFunctionMetaDoc() {
        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>(5);
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_CAT", "FUNCTION_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_NAME", "FUNCTION_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_TYPE", "FUNCTION_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", "string"));
        return metaDoc;
    }

    protected MongoResultDoc getFunctionValuesDoc(String functionName, String remarks) {
        MongoResultDoc doc = new MongoResultDoc();
        doc.values = new ArrayList<>(5);
        doc.values.add(new BsonString("def"));
        doc.values.add(new BsonNull());
        doc.values.add(new BsonString(functionName));
        // perhaps at some point add comments explaining the function.
        doc.values.add(new BsonString(remarks));
        doc.values.add(new BsonInt32(functionNoTable));
        doc.values.add(new BsonString(functionName));
        return doc;
    }

    protected BsonValue bsonInt32(Integer i) {
        if (i == null) {
            return new BsonNull();
        }
        return new BsonInt32(i);
    }

    protected MongoResultDoc getFunctionColumnMetaDoc() {
        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>(17);
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_CAT", "FUNCTION_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "FUNCTION_NAME", "FUNCTION_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_NAME", "COLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_TYPE", "COLUMN_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PRECISION", "PRECISION", "int"));
        metaDoc.columns.add(new Column("", "", "", "LENGTH", "LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "SCALE", "SCALE", "int"));
        metaDoc.columns.add(new Column("", "", "", "RADIX", "RADIX", "int"));
        metaDoc.columns.add(new Column("", "", "", "NULLABLE", "NULLABLE", "int"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(
                new Column("", "", "", "CHAR_OCTET_LENGTH", "CHAR_OCTET_LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "ORDINAL_POSITION", "ORDINAL_POSITION", "int"));
        metaDoc.columns.add(new Column("", "", "", "IS_NULLABLE", "IS_NULLABLE", "string"));
        metaDoc.columns.add(new Column("", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", "string"));
        return metaDoc;
    }

    protected MongoResultDoc getFunctionColumnValuesDoc(
            MongoFunction func, int i, String argName, String argType, boolean isReturnColumn) {
        BsonValue n = new BsonNull();
        String functionName = func.name;
        MongoResultDoc doc = new MongoResultDoc();
        doc.values = new ArrayList<>(17);
        doc.values.add(new BsonString("def"));
        doc.values.add(n);
        doc.values.add(new BsonString(functionName));
        doc.values.add(new BsonString(argName));
        doc.values.add(new BsonInt32(isReturnColumn ? functionReturn : functionColumnIn));
        doc.values.add(new BsonInt32(typeNum(argType)));
        doc.values.add(argType == null ? n : new BsonString(argType));
        doc.values.add(bsonInt32(typePrec(argType)));
        doc.values.add(bsonInt32(typeBytes(argType)));
        doc.values.add(bsonInt32(typeScale(argType)));
        doc.values.add(bsonInt32(typeRadix(argType)));
        doc.values.add(new BsonInt32(functionNullable));
        doc.values.add(new BsonString(func.comment));
        doc.values.add(bsonInt32(typeBytes(argType)));
        doc.values.add(new BsonInt32(i));
        doc.values.add(new BsonString("YES"));
        doc.values.add(new BsonString(functionName));
        return doc;
    }

    //--------------------------JDBC 4.1 -----------------------------

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
