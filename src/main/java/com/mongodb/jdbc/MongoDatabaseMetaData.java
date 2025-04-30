/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.mongosql.MongoSQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

@AutoLoggable
public class MongoDatabaseMetaData implements DatabaseMetaData {
    private static final BsonInt32 BSON_ZERO_INT_VALUE = new BsonInt32(0);
    private static final BsonInt32 BSON_TYPE_SEARCHABLE_INT_VALUE = new BsonInt32(typeSearchable);
    private static final BsonInt32 BSON_OTHER_INT_VALUE = new BsonInt32(Types.OTHER);
    private static final BsonInt32 BSON_COLUMN_NULLABLE_INT_VALUE =
            new BsonInt32(ResultSetMetaData.columnNullable);
    private static final BsonString BSON_EMPTY_STR_VALUE = new BsonString("");
    private static final BsonString BSON_YES_STR_VALUE = new BsonString("YES");
    private static final BsonString BSON_NO_STR_VALUE = new BsonString("NO");

    private static final String BOT_NAME = "";
    private static final String INDEX_KEY_KEY = "key";
    private static final String INDEX_NAME_KEY = "name";

    private static final List<String> UNIQUE_KEY_PATH = Collections.singletonList("unique");

    private static final String PROCEDURE_CAT = "PROCEDURE_CAT";
    private static final String PROCEDURE_SCHEM = "PROCEDURE_SCHEM";
    private static final String PROCEDURE_NAME = "PROCEDURE_NAME";
    private static final String PROCEDURE_TYPE = "PROCEDURE_TYPE";
    private static final String REMARKS = "REMARKS";
    private static final String SPECIFIC_NAME = "SPECIFIC_NAME";
    private static final String FUNCTION_CAT = "FUNCTION_CAT";
    private static final String FUNCTION_SCHEM = "FUNCTION_SCHEM";
    private static final String FUNCTION_NAME = "FUNCTION_NAME";
    private static final String FUNCTION_TYPE = "FUNCTION_TYPE";

    private static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    private static final String CASE_SENSITIVE = "CASE_SENSITIVE";
    private static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
    private static final String COLUMN_DEF = "COLUMN_DEF";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String COLUMN_TYPE = "COLUMN_TYPE";
    private static final String CREATE_PARAMS = "CREATE_PARAMS";
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String DEFAULT_VALUE = "DEFAULT_VALUE";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String FIXED_PREC_SCALE = "FIXED_PREC_SCALE";
    private static final String FIX_PREC_SCALE = "FIX_PREC_SCALE";
    private static final String IS_NULLABLE = "IS_NULLABLE";
    private static final String LENGTH = "LENGTH";
    private static final String LITERAL_PREFIX = "LITERAL_PREFIX";
    private static final String LITERAL_SUFFIX = "LITERAL_SUFFIX";
    private static final String LOCAL_TYPE_NAME = "LOCAL_TYPE_NAME";
    private static final String MAXIMUM_SCALE = "MAXIMUM_SCALE";
    private static final String MAX_LEN = "MAX_LEN";
    private static final String MINIMUM_SCALE = "MINIMUM_SCALE";
    private static final String NAME = "NAME";
    private static final String NULLABLE = "NULLABLE";
    private static final String ORDINAL_POSITION = "ORDINAL_POSITION";
    private static final String PRECISION = "PRECISION";
    private static final String RADIX = "RADIX";
    private static final String SCALE = "SCALE";
    private static final String SEARCHABLE = "SEARCHABLE";
    private static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";
    private static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
    private static final String TABLE_TYPE = "TABLE_TYPE";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String UNSIGNED_ATTRIBUTE = "UNSIGNED_ATTRIBUTE";

    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_CATALOG = "TABLE_CATALOG";

    private static final String SCOPE = "SCOPE";
    private static final String COLUMN_SIZE = "COLUMN_SIZE";
    private static final String BUFFER_LENGTH = "BUFFER_LENGTH";
    private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    private static final String PSEUDO_COLUMN = "PSEUDO_COLUMN";

    private static final String PKTABLE_CAT = "PKTABLE_CAT";
    private static final String PKTABLE_SCHEM = "PKTABLE_SCHEM";
    private static final String PKTABLE_NAME = "PKTABLE_NAME";
    private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
    private static final String FKTABLE_CAT = "FKTABLE_CAT";
    private static final String FKTABLE_SCHEM = "FKTABLE_SCHEM";
    private static final String FKTABLE_NAME = "FKTABLE_NAME";
    private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
    private static final String KEY_SEQ = "KEY_SEQ";
    private static final String UPDATE_RULE = "UPDATE_RULE";
    private static final String DELETE_RULE = "DELETE_RULE";
    private static final String FK_NAME = "FK_NAME";
    private static final String PK_NAME = "PK_NAME";
    private static final String DEFERRABILITY = "DEFERRABILITY";

    private static final String TYPE_CAT = "TYPE_CAT";
    private static final String TYPE_SCHEM = "TYPE_SCHEM";
    private static final String CLASS_NAME = "CLASS_NAME";
    private static final String BASE_TYPE = "BASE_TYPE";

    private static final String SUPERTYPE_CAT = "SUPERTYPE_CAT";
    private static final String SUPERTYPE_SCHEM = "SUPERTYPE_SCHEM";
    private static final String SUPERTYPE_NAME = "SUPERTYPE_NAME";

    private static final String TABLE_CAT = "TABLE_CAT";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String SUPERTABLE_NAME = "SUPERTABLE_NAME";

    private static final String ATTR_NAME = "ATTR_NAME";
    private static final String ATTR_TYPE_NAME = "ATTR_TYPE_NAME";
    private static final String ATTR_SIZE = "ATTR_SIZE";
    private static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";
    private static final String ATTR_DEF = "ATTR_DEF";
    private static final String SCOPE_CATALOG = "SCOPE_CATALOG";
    private static final String SCOPE_SCHEMA = "SCOPE_SCHEMA";
    private static final String SCOPE_TABLE = "SCOPE_TABLE";
    private static final String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";
    private static final String COLUMN_USAGE = "COLUMN_USAGE";

    private static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private static final String IS_GENERATEDCOLUMN = "IS_GENERATEDCOLUMN";

    private static final String SELF_REFERENCING_COL_NAME = "SELF_REFERENCING_COL_NAME";
    private static final String REF_GENERATION = "REF_GENERATION";

    private static final String GRANTOR = "GRANTOR";
    private static final String GRANTEE = "GRANTEE";
    private static final String PRIVILEGE = "PRIVILEGE";
    private static final String IS_GRANTABLE = "IS_GRANTABLE";

    private static final String NON_UNIQUE = "NON_UNIQUE";
    private static final String INDEX_QUALIFIER = "INDEX_QUALIFIER";
    private static final String INDEX_NAME = "INDEX_NAME";
    private static final String TYPE = "TYPE";
    private static final String ASC_OR_DESC = "ASC_OR_DESC";
    private static final String CARDINALITY = "CARDINALITY";
    private static final String PAGES = "PAGES";
    private static final String FILTER_CONDITION = "FILTER_CONDITION";

    // Actual max size is 16777216, we reserve 216 for other bits of encoding,
    // since this value is used to set limits on literals and field names.
    // This is arbitrary and conservative.
    private static final int APPROXIMATE_DOC_SIZE = 16777000;
    private static final String FUNC_DEFAULT_CATALOG = "def";
    private static final String YES = "YES";

    private static final List<SortableBsonDocument.SortSpec> GET_TABLES_SORT_SPECS =
            Arrays.asList(
                    new SortableBsonDocument.SortSpec(
                            TABLE_TYPE, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            TABLE_CAT, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            TABLE_NAME, SortableBsonDocument.ValueType.String));

    private static final List<SortableBsonDocument.SortSpec> GET_TABLE_PRIVILEGES_SORT_SPECS =
            Arrays.asList(
                    new SortableBsonDocument.SortSpec(
                            TABLE_CAT, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            TABLE_NAME, SortableBsonDocument.ValueType.String));

    private static final List<SortableBsonDocument.SortSpec> GET_COLUMNS_SORT_SPECS =
            Arrays.asList(
                    new SortableBsonDocument.SortSpec(
                            TABLE_CAT, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            TABLE_NAME, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            ORDINAL_POSITION, SortableBsonDocument.ValueType.Int));

    private static final List<SortableBsonDocument.SortSpec> GET_COLUMN_PRIVILEGES_SORT_SPECS =
            Collections.singletonList(
                    new SortableBsonDocument.SortSpec(
                            COLUMN_NAME, SortableBsonDocument.ValueType.String));

    private static final List<SortableBsonDocument.SortSpec> GET_PRIMARY_KEYS_SORT_SPECS =
            Collections.singletonList(
                    new SortableBsonDocument.SortSpec(
                            COLUMN_NAME, SortableBsonDocument.ValueType.String));

    private static final List<SortableBsonDocument.SortSpec> GET_INDEX_INFO_SORT_SPECS =
            Arrays.asList(
                    new SortableBsonDocument.SortSpec(
                            NON_UNIQUE, SortableBsonDocument.ValueType.Boolean),
                    new SortableBsonDocument.SortSpec(
                            INDEX_NAME, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            ORDINAL_POSITION, SortableBsonDocument.ValueType.Int));

    private static final com.mongodb.jdbc.MongoFunctions MongoFunctions =
            com.mongodb.jdbc.MongoFunctions.getInstance();

    public static final Pattern DISALLOWED_COLLECTION_NAMES =
            Pattern.compile("(system\\.(namespace|indexes|profiles|js|views))|__sql_schemas");

    public static final Pattern DISALLOWED_DB_NAMES = Pattern.compile("admin|config|local|system");

    private final MongoConnection conn;
    private String serverVersion;
    private MongoLogger logger;

    public MongoDatabaseMetaData(MongoConnection conn) {
        this.conn = conn;
        logger = new MongoLogger(this.getClass().getCanonicalName(), conn.getLogger());
    }

    // For all methods in this class, the fields in the result set are nested
    // under the bottom namespace. This helper method takes result set fields
    // and nests them appropriately.
    private BsonDocument createBottomBson(BsonElement... elements) {
        BsonDocument bot = new BsonDocument(Arrays.asList(elements));
        return new BsonDocument(BOT_NAME, bot);
    }

    // This helper method nests result fields under the bottom namespace, and also
    // ensures the BsonDocument returned is sortable based on argued criteria.
    private SortableBsonDocument createSortableBottomBson(
            List<SortableBsonDocument.SortSpec> sortSpecs, BsonElement... elements) {
        BsonDocument bot = new BsonDocument(Arrays.asList(elements));
        return new SortableBsonDocument(sortSpecs, BOT_NAME, bot);
    }

    // For all methods in this class, the fields in the result set are nested
    // under the bottom namespace. This helper method takes result schema fields
    // and nests them appropriately.
    private final MongoJsonSchema createBottomSchema(
            MongoJsonSchema.ScalarProperties... resultSchemaFields) {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addScalarKeys(resultSchemaFields);

        MongoJsonSchema bot = MongoJsonSchema.createEmptyObjectSchema();
        bot.required.add(BOT_NAME);
        bot.properties.put(BOT_NAME, resultSchema);
        return bot;
    }

    private BsonValue asBsonIntOrNull(Integer i) {
        return asBsonIntOrDefault(i, null);
    }

    private BsonValue asBsonIntOrDefault(Integer i, Integer defaultVal) {
        if (i == null) {
            return defaultVal == null ? new BsonNull() : new BsonInt32(defaultVal);
        }
        return new BsonInt32(i);
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        // These come from keywords from the mongosql-rs parser, minus the Standard SQL-2003 Reserved keywords
        return "AGGREGATE,"
                + "ASC,"
                + "BINDATA,"
                + "BIT,"
                + "BOOL,"
                + "BSON_DATE,"
                + "BSON_TIMESTAMP,"
                + "DBPOINTER,"
                + "DESC,"
                + "DOCUMENT,"
                + "ERROR,"
                + "EXTRACT,"
                + "FIRST,"
                + "JAVASCRIPT,"
                + "JAVASCRIPTWITHSCOPE,"
                + "LIMIT,"
                + "LONG,"
                + "MAXKEY,"
                + "MINKEY,"
                + "MISSING,"
                + "NEXT,"
                + "NUMBER,"
                + "OBJECTID,"
                + "OFFSET,"
                + "POSITION,"
                + "REGEX,"
                + "SUBSTRING,"
                + "SYMBOL,"
                + "TRIM,"
                + "UNDEFINED";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return MongoFunctions.numericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return MongoFunctions.stringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return MongoFunctions.systemFunctionsString;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return MongoFunctions.dateFunctionsString;
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SPECIFIC_NAME, BSON_STRING));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PROCEDURE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PRECISION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SCALE, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(RADIX, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(NULLABLE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_DEF, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SQL_DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SQL_DATETIME_SUB, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(CHAR_OCTET_LENGTH, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(ORDINAL_POSITION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(IS_NULLABLE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SPECIFIC_NAME, BSON_STRING));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addScalarKeys(new MongoJsonSchema.ScalarProperties(TABLE_TYPE, BSON_STRING));

        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("TABLE"))));
        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("VIEW"))));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);

        return new MongoResultSet(conn.getLogger(), new BsonExplicitCursor(docs), botSchema);
    }

    // MHOUSE-7119: ADF quickstarts return empty strings and the admin database, so we filter them out
    static boolean filterEmptiesAndInternalDBs(String dbName) {
        return !dbName.isEmpty() && !DISALLOWED_DB_NAMES.matcher(dbName).matches();
    }

    // Helper for getting a stream of all database names.
    private Stream<String> getDatabaseNames() {
        return this.conn
                .mongoClient
                .listDatabaseNames()
                .into(new ArrayList<>())
                .stream()
                .filter(dbName -> filterEmptiesAndInternalDBs(dbName));
    }

    // Helper for getting a list of collection names from the db
    // Using runCommand instead of listCollections as listCollections does not support authorizedCollections option
    private ArrayList<MongoListTablesResult> getCollectionsFromRunCommand(MongoDatabase db) {
        MongoRunCmdListTablesResult mongoRunCmdListTablesResult =
                db.runCommand(
                        new Document("listCollections", 1)
                                .append("authorizedCollections", true)
                                .append("nameOnly", true),
                        MongoRunCmdListTablesResult.class);
        return mongoRunCmdListTablesResult.getCursor().getFirstBatch();
    }

    // Helper for getting a stream of MongoListCollectionsResults from the argued db that match
    // the argued filter.
    private Stream<MongoListTablesResult> getTableDataFromDB(
            String dbName, Function<MongoListTablesResult, Boolean> filter) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.REGISTRY);
        return getCollectionsFromRunCommand(db).stream().filter(filter::apply);
    }

    // Helper for creating BSON documents for the getTables method. Intended for use
    // with the getTableDataFromDB helper method which is shared between getTables and
    // getTablePrivileges.
    private BsonDocument toGetTablesDoc(String dbName, MongoListTablesResult res) {
        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_TYPE, TABLE_CAT, TABLE_SCHEM (omitted), and
                // TABLE_NAME.
                GET_TABLES_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(dbName)),
                new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                new BsonElement(TABLE_NAME, new BsonString(res.name)),
                new BsonElement(TABLE_TYPE, new BsonString(res.type)),
                new BsonElement(REMARKS, BsonNull.VALUE),
                new BsonElement(TYPE_CAT, BsonNull.VALUE),
                new BsonElement(TYPE_SCHEM, BsonNull.VALUE),
                new BsonElement(TYPE_NAME, BsonNull.VALUE),
                new BsonElement(SELF_REFERENCING_COL_NAME, BsonNull.VALUE),
                new BsonElement(REF_GENERATION, BsonNull.VALUE));
    }

    // Helper for creating BSON documents for the getTablePrivileges method. Intended
    // for use with the getTableDataFromDB helper method which is shared between getTables
    // and getTablePrivileges.
    private BsonDocument toGetTablePrivilegesDoc(String dbName, MongoListTablesResult res) {
        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME, and
                // PRIVILEGE. Since all PRIVILEGEs are the same, we also omit that.
                GET_TABLE_PRIVILEGES_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(dbName)),
                new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                new BsonElement(TABLE_NAME, new BsonString(res.name)),
                new BsonElement(GRANTOR, BsonNull.VALUE),
                new BsonElement(GRANTEE, BSON_EMPTY_STR_VALUE),
                new BsonElement(PRIVILEGE, new BsonString("SELECT")),
                new BsonElement(IS_GRANTABLE, BsonNull.VALUE));
    }

    // Helper for getting table data for all tables from a specific database. Used by
    // getTables and getTablePrivileges. The caller specifies how to serialize the table
    // info into BSON documents for the result set.
    private Stream<BsonDocument> getTableDataFromDB(
            String dbName,
            Pattern tableNamePatternRE,
            List<String> types,
            BiFunction<String, MongoListTablesResult, BsonDocument> bsonSerializer) {

        // Filter out __sql_schemas, system.namespaces, system.indexes,system.profile,system.js,system.views
        return this.getTableDataFromDB(
                        dbName,
                        res ->
                                // Don't list system collections
                                (!DISALLOWED_COLLECTION_NAMES.matcher(res.name).matches())
                                        && (tableNamePatternRE == null
                                                || tableNamePatternRE.matcher(res.name).matches())
                                        && (types == null
                                                || types.contains(res.type.toLowerCase())))
                .map(res -> bsonSerializer.apply(dbName, res));
    }

    private List<String> toTableTypeList(String[] types) {
        List<String> l = null;
        if (types != null) {
            l = Arrays.asList(types);
            l.replaceAll(String::toLowerCase);
        }
        return l;
    }

    public static Pattern toJavaPattern(String sqlPattern) {
        return sqlPattern == null
                ? null
                : Pattern.compile(
                        sqlPattern
                                .replaceAll("([.^$*+?(){}|\\[\\]\\\\])", "\\\\$1")
                                .replaceAll("(?<!\\\\)%", ".*")
                                .replaceAll("(?<!\\\\)_", "."));
    }

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
        return MongoDriver.MONGODB_PRODUCT_NAME;
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return conn.getServerVersion();
    }

    @Override
    public String getDriverName() throws SQLException {
        return MongoDriver.MONGO_DRIVER_NAME;
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
        return conn.getServerMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return conn.getServerMinorVersion();
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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(TABLE_TYPE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(TYPE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(
                                SELF_REFERENCING_COL_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(REF_GENERATION, BSON_STRING, false));

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = toJavaPattern(tableNamePattern);
        List<String> typesList = toTableTypeList(types);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get tables for all databases.
            docs =
                    this.getDatabaseNames()
                            .flatMap(
                                    dbName ->
                                            getTableDataFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    typesList,
                                                    this::toGetTablesDoc));
        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs = getTableDataFromDB(catalog, tableNamePatternRE, typesList, this::toGetTablesDoc);
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(TABLE_CATALOG, BSON_STRING, false));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING));

        BsonExplicitCursor c =
                new BsonExplicitCursor(
                        this.getDatabaseNames()
                                .sorted()
                                .map(
                                        dbName ->
                                                createBottomBson(
                                                        new BsonElement(
                                                                TABLE_CAT, new BsonString(dbName))))
                                .collect(Collectors.toList()));

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    /**
     * liftSQLException tries to execute a Supplier that may throw a RuntimeException that wraps a
     * SQLException as the cause. If such an exception is encountered, the inner SQLException is
     * thrown. Otherwise, the Supplier executes as expected.
     *
     * <p>This method is intended for use with higher order functions that wrap SQLExceptions in
     * RuntimeExceptions to appease the compiler. We want to propagate those SQLExceptions all the
     * way out to the caller so this method is a way of recovering them.
     *
     * @param f The Supplier function to execute. This function may throw a RuntimeException that
     *     wraps a SQLException.
     * @param <T> The return type of the Supplier.
     * @return The return value of the Supplier.
     * @throws SQLException If a RuntimeException that wraps a SQLException is caught.
     */
    private static <T> T liftSQLException(Supplier<T> f) throws SQLException {
        try {
            return f.get();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw e;
        }
    }

    // Helper class for representing all info needed to serialize column data for the
    // getColumns and getColumnPrivileges methods. Intended for use with toGetColumnsDoc
    // and toGetColumnPrivilegesDoc helpers.
    private static class GetColumnsDocInfo {
        String dbName;
        String tableName;
        String columnName;
        BsonTypeInfo columnBsonTypeInfo;
        int nullability;
        int idx;

        GetColumnsDocInfo(
                String dbName,
                String tableName,
                String columnName,
                MongoJsonSchema parentSchema,
                MongoJsonSchema columnSchema,
                int idx) {
            this.dbName = dbName;
            this.tableName = tableName;
            this.columnName = columnName;
            this.idx = idx;

            try {
                this.columnBsonTypeInfo = columnSchema.getBsonTypeInfo();
                this.nullability = parentSchema.getColumnNullability(columnName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Helper for creating BSON documents for the getColumns method. Intended for use
    // with the getColumnsFromDB helper method which is shared between getColumns and
    // getColumnPrivileges.
    private BsonDocument toGetColumnsDoc(GetColumnsDocInfo i) {
        BsonValue isNullable =
                i.nullability == columnNoNulls
                        ? BSON_NO_STR_VALUE
                        : i.nullability == columnNullable
                                ? BSON_YES_STR_VALUE
                                : BSON_EMPTY_STR_VALUE;

        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME and
                // ORDINAL_POSITION.
                GET_COLUMNS_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(i.dbName)),
                new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                new BsonElement(TABLE_NAME, new BsonString(i.tableName)),
                new BsonElement(COLUMN_NAME, new BsonString(i.columnName)),
                new BsonElement(DATA_TYPE, new BsonInt32(i.columnBsonTypeInfo.getJdbcType())),
                new BsonElement(TYPE_NAME, new BsonString(i.columnBsonTypeInfo.getBsonName())),
                new BsonElement(COLUMN_SIZE, BsonNull.VALUE),
                new BsonElement(BUFFER_LENGTH, BSON_ZERO_INT_VALUE),
                new BsonElement(
                        DECIMAL_DIGITS, asBsonIntOrNull(i.columnBsonTypeInfo.getDecimalDigits())),
                new BsonElement(
                        NUM_PREC_RADIX, new BsonInt32(i.columnBsonTypeInfo.getNumPrecRadix())),
                new BsonElement(NULLABLE, new BsonInt32(i.nullability)),
                new BsonElement(REMARKS, BSON_EMPTY_STR_VALUE),
                new BsonElement(COLUMN_DEF, BsonNull.VALUE),
                new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                new BsonElement(
                        CHAR_OCTET_LENGTH,
                        asBsonIntOrNull(i.columnBsonTypeInfo.getCharOctetLength())),
                new BsonElement(ORDINAL_POSITION, new BsonInt32(i.idx)),
                new BsonElement(IS_NULLABLE, isNullable),
                new BsonElement(SCOPE_CATALOG, BsonNull.VALUE),
                new BsonElement(SCOPE_SCHEMA, BsonNull.VALUE),
                new BsonElement(SCOPE_TABLE, BsonNull.VALUE),
                new BsonElement(SOURCE_DATA_TYPE, BSON_ZERO_INT_VALUE),
                new BsonElement(IS_AUTOINCREMENT, BSON_NO_STR_VALUE),
                new BsonElement(IS_GENERATEDCOLUMN, BSON_EMPTY_STR_VALUE));
    }

    // Helper for creating BSON documents for the getColumnPrivileges methods. Intended
    // for use with the getColumnsFromDB helper method which is shared between getColumns
    // and getColumnPrivileges.
    private BsonDocument toGetColumnPrivilegesDoc(GetColumnsDocInfo i) {
        return createSortableBottomBson(
                // Per JDBC spec, sort by  COLUMN_NAME and PRIVILEGE. Since all PRIVILEGEs are the same,
                // we just sort by COLUMN_NAME here.
                GET_COLUMN_PRIVILEGES_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(i.dbName)),
                new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                new BsonElement(TABLE_NAME, new BsonString(i.tableName)),
                new BsonElement(COLUMN_NAME, new BsonString(i.columnName)),
                new BsonElement(GRANTOR, BsonNull.VALUE),
                new BsonElement(GRANTEE, BSON_EMPTY_STR_VALUE),
                new BsonElement(PRIVILEGE, new BsonString("SELECT")),
                new BsonElement(IS_GRANTABLE, BsonNull.VALUE));
    }

    // Helper for ensuring a sqlGetSchema result is a valid collection schema. As in,
    // it has ok: 1, has a jsonSchema, and the jsonSchema is an object schema.
    private boolean isValidSchema(MongoJsonSchemaResult res) {
        return res.ok == 1
                && res.schema.mongoJsonSchema != null
                && res.schema.mongoJsonSchema.isObject();
    }

    // Helper for getting column data for all columns from all tables from a specific
    // database. Used by getColumns and getColumnPrivileges. The caller specifies how
    // to serialize the column info into BSON documents for the result set.
    private Stream<BsonDocument> getColumnsFromDB(
            String dbName,
            Pattern tableNamePatternRE,
            Pattern columnNamePatternRE,
            Function<GetColumnsDocInfo, BsonDocument> bsonSerializer) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.REGISTRY);
        return getCollectionsFromRunCommand(db)
                .stream()
                .map(collection -> collection.name)
                .collect(Collectors.toList())
                .stream()

                // filter only for collections matching the pattern, and exclude the `__sql_schemas` collection
                .filter(
                        tableName ->
                                // Don't list system collections
                                (!DISALLOWED_COLLECTION_NAMES.matcher(tableName).matches())
                                        && (tableNamePatternRE == null
                                                || tableNamePatternRE.matcher(tableName).matches()))

                // map the collection names into triples of (dbName, tableName, tableSchema)
                .map(
                        tableName -> {
                            try {
                                return new Pair<>(
                                        new Pair<>(dbName, tableName),
                                        getSchemaByClusterType(db, tableName));
                            } catch (MongoSQLException | MongoSerializationException e) {
                                throw new RuntimeException(
                                        "Error retrieving schema for: " + dbName + "." + tableName,
                                        e);
                            }
                        })

                // filter only for collections that have schemas
                .filter(p -> isValidSchema(p.right()))

                // flatMap the column data into a single stream of BSON docs
                .flatMap(
                        p -> {
                            Pair<String, String> ns = p.left();
                            MongoJsonSchemaResult res = p.right();
                            AtomicInteger idx = new AtomicInteger();
                            return res.schema
                                    .mongoJsonSchema
                                    .properties
                                    .entrySet()
                                    .stream()

                                    // filter only for columns matching the pattern
                                    .filter(
                                            entry ->
                                                    columnNamePatternRE == null
                                                            || columnNamePatternRE
                                                                    .matcher(entry.getKey())
                                                                    .matches())

                                    // sort by column name since ordinal position is
                                    // based on column sort order
                                    .sorted(Map.Entry.comparingByKey())

                                    // map the (columnName, columnSchema) pairs into BSON docs
                                    .map(
                                            entry ->
                                                    bsonSerializer.apply(
                                                            new GetColumnsDocInfo(
                                                                    ns.left(),
                                                                    ns.right(),
                                                                    entry.getKey(),
                                                                    res.schema.mongoJsonSchema,
                                                                    entry.getValue(),
                                                                    idx.getAndIncrement())));
                        });
    }

    private MongoJsonSchemaResult getSchemaByClusterType(MongoDatabase db, String tableName)
            throws MongoSQLException, MongoSerializationException {
        if (conn.getClusterType() == MongoConnection.MongoClusterType.AtlasDataFederation) {
            return db.runCommand(
                    new BsonDocument("sqlGetSchema", new BsonString(tableName)),
                    MongoJsonSchemaResult.class);
        } else if (conn.getClusterType() == MongoConnection.MongoClusterType.Enterprise) {
            return conn.getMongosqlTranslate().getSchema(db, tableName);
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported cluster type: " + conn.getClusterType());
        }
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_SIZE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(BUFFER_LENGTH, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(DECIMAL_DIGITS, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(NUM_PREC_RADIX, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(NULLABLE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(COLUMN_DEF, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SQL_DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SQL_DATETIME_SUB, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(CHAR_OCTET_LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(ORDINAL_POSITION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(IS_NULLABLE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SCOPE_CATALOG, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SCOPE_SCHEMA, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SCOPE_TABLE, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SOURCE_DATA_TYPE, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(IS_AUTOINCREMENT, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(IS_GENERATEDCOLUMN, BSON_STRING));

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = toJavaPattern(tableNamePattern);
        Pattern columnNamePatternRE = toJavaPattern(columnNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get columns for all databases.
            docs =
                    liftSQLException(
                            () ->
                                    this.getDatabaseNames()
                                            .flatMap(
                                                    dbName ->
                                                            getColumnsFromDB(
                                                                    dbName,
                                                                    tableNamePatternRE,
                                                                    columnNamePatternRE,
                                                                    this::toGetColumnsDoc)));

        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs =
                    liftSQLException(
                            () ->
                                    getColumnsFromDB(
                                            catalog,
                                            tableNamePatternRE,
                                            columnNamePatternRE,
                                            this::toGetColumnsDoc));
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(GRANTOR, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(GRANTEE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PRIVILEGE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(IS_GRANTABLE, BSON_STRING, false));

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = toJavaPattern(tableNamePattern);
        Pattern columnNamePatternRE = toJavaPattern(columnNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get column privileges for all databases.
            docs =
                    liftSQLException(
                            () ->
                                    this.getDatabaseNames()
                                            .flatMap(
                                                    dbName ->
                                                            getColumnsFromDB(
                                                                    dbName,
                                                                    tableNamePatternRE,
                                                                    columnNamePatternRE,
                                                                    this
                                                                            ::toGetColumnPrivilegesDoc)));
        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs =
                    liftSQLException(
                            () ->
                                    getColumnsFromDB(
                                            catalog,
                                            tableNamePatternRE,
                                            columnNamePatternRE,
                                            this::toGetColumnPrivilegesDoc));
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(GRANTOR, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(GRANTEE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PRIVILEGE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(IS_GRANTABLE, BSON_STRING, false));

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = toJavaPattern(tableNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get table privileges for all databases.
            docs =
                    this.getDatabaseNames()
                            .flatMap(
                                    dbName ->
                                            getTableDataFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    null,
                                                    this::toGetTablePrivilegesDoc));
        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs =
                    getTableDataFromDB(
                            catalog, tableNamePatternRE, null, this::toGetTablePrivilegesDoc);
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    private Stream<BsonDocument> getFirstUniqueIndexDocsForTable(
            String dbName,
            String tableName,
            BiFunction<Pair<String, String>, Document, List<BsonDocument>> serializer) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.REGISTRY);
        ListIndexesIterable<Document> i = db.getCollection(tableName).listIndexes();
        List<BsonDocument> docs = new ArrayList<>();

        for (Document d : i) {
            Boolean isUnique = d.getEmbedded(UNIQUE_KEY_PATH, Boolean.class);
            if (isUnique == null || !isUnique) {
                continue;
            }

            // Get result set rows from first unique index
            docs.addAll(serializer.apply(new Pair<>(dbName, tableName), d));

            // Break after we find the first unique index
            break;
        }

        return docs.stream();
    }

    // Helper for getting a ResultSet based on the first unique index for the argued table. The
    // result set documents are produced by the serializer function. Given a (dbName, tableName)
    // pair and a Document representing the first unique index, the serializer function creates
    // a list of BsonDocuments corresponding to the rows of the result set. This method is shared
    // between getBestRowIdentifier and getPrimaryKeys, which both return data based on the first
    // unique index.
    private ResultSet getFirstUniqueIndexResultSet(
            String catalog,
            String table,
            MongoJsonSchema botSchema,
            BiFunction<Pair<String, String>, Document, List<BsonDocument>> serializer) {
        try {
            Stream<BsonDocument> docs;
            if (catalog == null) {
                // If no catalog (database) is specified, get first unique index for all databases that have a
                // collection with the argued table name.
                docs =
                        this.getDatabaseNames()
                                .flatMap(
                                        dbName ->
                                                getTableDataFromDB(
                                                                dbName,
                                                                res -> res.name.equals(table))
                                                        .flatMap(
                                                                r ->
                                                                        getFirstUniqueIndexDocsForTable(
                                                                                dbName,
                                                                                r.name,
                                                                                serializer)));
            } else if (catalog.isEmpty()) {
                // If catalog (database) is empty, we will return an empty result set because
                // MongoDB does not support tables (collections) without databases.
                docs = Stream.empty();
            } else {
                docs = getFirstUniqueIndexDocsForTable(catalog, table, serializer);
            }

            // Collect to list.
            List<BsonDocument> docsList = docs.collect(Collectors.toList());
            BsonExplicitCursor c = new BsonExplicitCursor(docsList);

            return new MongoResultSet(conn.getLogger(), c, botSchema);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper for getting the rows for the getBestRowIdentifier result set. Given a
    // (dbName, tableName) and an index doc, it produces a list of BsonDocuments where
    // each document corresponds to a row in the result set representing a column in
    // the index. This method is intended for use with the getFirstUniqueIndexResultSet
    // method.
    private List<BsonDocument> toGetBestRowIdentifierDocs(
            Pair<String, String> namespace, Document indexInfo) {
        List<BsonDocument> docs = new ArrayList<>();

        // We've found the first unique index. At this point, we get the schema for this
        // collection and create a result set based on this index's keys.
        MongoJsonSchemaResult r;
        try {
            r = getSchemaByClusterType(conn.getDatabase(namespace.left()), namespace.right());
        } catch (MongoSQLException | MongoSerializationException e) {
            throw new RuntimeException(
                    "Error retrieving schema for collection: " + namespace.right(), e);
        }

        Document keys = indexInfo.get(INDEX_KEY_KEY, Document.class);
        try {
            for (String key : keys.keySet()) {
                docs.add(
                        toGetBestRowIdentifierDoc(
                                key,
                                r.schema.mongoJsonSchema.properties.get(key).getBsonTypeInfo()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return docs;
    }

    // Helper for creating a result set BsonDocument for an index column for the
    // getBestRowIdentifier method.
    private BsonDocument toGetBestRowIdentifierDoc(
            String columnName, BsonTypeInfo columnBsonTypeInfo) {
        return createBottomBson(
                new BsonElement(SCOPE, BsonNull.VALUE),
                new BsonElement(COLUMN_NAME, new BsonString(columnName)),
                new BsonElement(DATA_TYPE, new BsonInt32(columnBsonTypeInfo.getJdbcType())),
                new BsonElement(TYPE_NAME, new BsonString(columnBsonTypeInfo.getBsonName())),
                new BsonElement(COLUMN_SIZE, asBsonIntOrNull(columnBsonTypeInfo.getPrecision())),
                new BsonElement(BUFFER_LENGTH, BsonNull.VALUE),
                new BsonElement(
                        DECIMAL_DIGITS, asBsonIntOrNull(columnBsonTypeInfo.getDecimalDigits())),
                new BsonElement(PSEUDO_COLUMN, new BsonInt32(bestRowNotPseudo)));
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(SCOPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_SIZE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(BUFFER_LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DECIMAL_DIGITS, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(PSEUDO_COLUMN, BSON_INT));

        // As in other methods, we ignore the schema argument. Here, we also ignore the
        // scope and nullable arguments.
        return liftSQLException(
                () ->
                        getFirstUniqueIndexResultSet(
                                catalog, table, botSchema, this::toGetBestRowIdentifierDocs));
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(SCOPE, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_SIZE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(BUFFER_LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DECIMAL_DIGITS, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(PSEUDO_COLUMN, BSON_INT));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(PKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(KEY_SEQ, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(UPDATE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DELETE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(FK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(DEFERRABILITY, BSON_INT));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(PKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(KEY_SEQ, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(UPDATE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DELETE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(FK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(DEFERRABILITY, BSON_INT));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(PKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FKTABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FKCOLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(KEY_SEQ, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(UPDATE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DELETE_RULE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(FK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(PK_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(DEFERRABILITY, BSON_INT));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    // Helper for getting the rows for the getPrimaryKeys result set. Given a (dbName, tableName)
    // and an index doc, it produces a list of BsonDocuments where each document corresponds to a
    // row in the result set representing a column in the index. This method is intended for use
    // with the getFirstUniqueIndexResultSet method.
    private List<BsonDocument> toGetPrimaryKeysDocs(
            Pair<String, String> namespace, Document indexInfo) {
        Document keys = indexInfo.get(INDEX_KEY_KEY, Document.class);
        String indexName = indexInfo.getString(INDEX_NAME_KEY);
        AtomicInteger pos = new AtomicInteger();

        return keys.keySet()
                .stream()
                .map(
                        key ->
                                createSortableBottomBson(
                                        // Per JDBC spec, sort by COLUMN_NAME.
                                        GET_PRIMARY_KEYS_SORT_SPECS,
                                        new BsonElement(
                                                TABLE_CAT, new BsonString(namespace.left())),
                                        new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                                        new BsonElement(
                                                TABLE_NAME, new BsonString(namespace.right())),
                                        new BsonElement(COLUMN_NAME, new BsonString(key)),
                                        new BsonElement(
                                                KEY_SEQ, new BsonInt32(pos.incrementAndGet())),
                                        new BsonElement(PK_NAME, new BsonString(indexName))))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(KEY_SEQ, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(PK_NAME, BSON_STRING, false));
        // As in other methods, we ignore the schema argument.
        return liftSQLException(
                () ->
                        getFirstUniqueIndexResultSet(
                                catalog, table, botSchema, this::toGetPrimaryKeysDocs));
    }

    private MongoJsonSchema getTypeInfoJsonSchema() {

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addScalarKeys(
                new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(PRECISION, BSON_INT),
                new MongoJsonSchema.ScalarProperties(LITERAL_PREFIX, BSON_STRING, false),
                new MongoJsonSchema.ScalarProperties(LITERAL_SUFFIX, BSON_STRING, false),
                new MongoJsonSchema.ScalarProperties(CREATE_PARAMS, BSON_STRING, false),
                new MongoJsonSchema.ScalarProperties(NULLABLE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(CASE_SENSITIVE, BSON_BOOL),
                new MongoJsonSchema.ScalarProperties(SEARCHABLE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(UNSIGNED_ATTRIBUTE, BSON_BOOL),
                new MongoJsonSchema.ScalarProperties(FIX_PREC_SCALE, BSON_BOOL),
                new MongoJsonSchema.ScalarProperties(AUTO_INCREMENT, BSON_BOOL),
                new MongoJsonSchema.ScalarProperties(LOCAL_TYPE_NAME, BSON_STRING, false),
                new MongoJsonSchema.ScalarProperties(MINIMUM_SCALE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(MAXIMUM_SCALE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(SQL_DATA_TYPE, BSON_INT),
                new MongoJsonSchema.ScalarProperties(SQL_DATETIME_SUB, BSON_INT),
                new MongoJsonSchema.ScalarProperties(NUM_PREC_RADIX, BSON_INT));
        return schema;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();
        MongoJsonSchema schema = getTypeInfoJsonSchema();

        // The following BSON Types are mostly ordered to follow the javadoc (i.e., they are ordered by DATA_TYPE).
        // However, instead of ordering all the BSON Types with DATA_TYPE == 1111 by how closely they map to the
        // corresponding JDBC SQL type (as the javadocs say), we order them alphabetically since "closest to JDBC
        // SQL type" is meaningless in this case (as all 1111 types are inaccurate).
        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_LONG.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIGINT)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_LONG.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_LONG.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_LONG.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_LONG.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_LONG.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BINDATA.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BINARY)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BINDATA.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BINDATA.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typePredNone)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BINDATA.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BINDATA.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BINDATA.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_STRING.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.LONGVARCHAR)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_STRING.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_STRING.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_STRING.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_STRING.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_STRING.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_NULL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(BSON_NULL.getJdbcType())),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_NULL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_NULL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_NULL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_NULL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_NULL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DECIMAL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DECIMAL)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DECIMAL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DECIMAL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DECIMAL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DECIMAL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DECIMAL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_INT.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.INTEGER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_INT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_INT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_INT.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_INT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_INT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DOUBLE.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DOUBLE)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DOUBLE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DOUBLE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DOUBLE.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DOUBLE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DOUBLE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BOOL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(BSON_BOOL.getJdbcType())),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BOOL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BOOL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BOOL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BOOL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BOOL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DATE.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.TIMESTAMP)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DATE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DATE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DATE.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DATE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DATE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_ARRAY.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_ARRAY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_ARRAY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_ARRAY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_ARRAY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_ARRAY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BSON.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BSON.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BSON.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BSON.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BSON.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BSON.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DBPOINTER.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DBPOINTER.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_DBPOINTER.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DBPOINTER.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DBPOINTER.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DBPOINTER.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_JAVASCRIPT.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_JAVASCRIPT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_JAVASCRIPT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(BSON_JAVASCRIPT.getMinScale())),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(BSON_JAVASCRIPT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_JAVASCRIPT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BSON_JAVASCRIPTWITHSCOPE.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(BSON_JAVASCRIPTWITHSCOPE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_JAVASCRIPTWITHSCOPE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getMinScale())),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MAXKEY.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_MAXKEY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_MAXKEY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_MAXKEY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_MAXKEY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_MAXKEY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MINKEY.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_MINKEY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_MINKEY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_MINKEY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_MINKEY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_MINKEY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECT.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_OBJECT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_OBJECT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_OBJECT.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_OBJECT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_OBJECT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECTID.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_OBJECTID.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_OBJECTID.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_OBJECTID.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_OBJECTID.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_OBJECTID.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_REGEX.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_REGEX.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_REGEX.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_REGEX.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_REGEX.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_REGEX.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_SYMBOL.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_SYMBOL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_SYMBOL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_SYMBOL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_SYMBOL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_SYMBOL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_TIMESTAMP.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_TIMESTAMP.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_TIMESTAMP.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_TIMESTAMP.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_TIMESTAMP.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_TIMESTAMP.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_UNDEFINED.getBsonName())),
                        new BsonElement(DATA_TYPE, BSON_OTHER_INT_VALUE),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_UNDEFINED.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, BsonNull.VALUE),
                        new BsonElement(LITERAL_SUFFIX, BsonNull.VALUE),
                        new BsonElement(CREATE_PARAMS, BsonNull.VALUE),
                        new BsonElement(NULLABLE, BSON_COLUMN_NULLABLE_INT_VALUE),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_UNDEFINED.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, BSON_TYPE_SEARCHABLE_INT_VALUE),
                        new BsonElement(UNSIGNED_ATTRIBUTE, BsonBoolean.FALSE),
                        new BsonElement(FIXED_PREC_SCALE, BsonBoolean.FALSE),
                        new BsonElement(AUTO_INCREMENT, BsonBoolean.FALSE),
                        new BsonElement(LOCAL_TYPE_NAME, BsonNull.VALUE),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_UNDEFINED.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_UNDEFINED.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, BSON_ZERO_INT_VALUE),
                        new BsonElement(SQL_DATETIME_SUB, BSON_ZERO_INT_VALUE),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_UNDEFINED.getNumPrecRadix()))));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoResultSet(conn.getLogger(), new BsonExplicitCursor(docs), botSchema);
    }

    // Helper for creating stream of bson documents from the columns in the indexInfo doc.
    private Stream<BsonDocument> toGetIndexInfoDocs(
            String dbName, String tableName, Document indexInfo) {
        Boolean isUnique = indexInfo.getEmbedded(UNIQUE_KEY_PATH, Boolean.class);
        BsonValue nonUnique = new BsonBoolean(isUnique == null || !isUnique);
        BsonValue indexName = new BsonString(indexInfo.getString(INDEX_NAME_KEY));

        Document keys = indexInfo.get(INDEX_KEY_KEY, Document.class);
        AtomicInteger pos = new AtomicInteger();

        return keys.keySet()
                .stream()
                .filter(
                        key -> {
                            // If the index is not an integer (e.g., a geospatial index), `keys.getInteger(key)`
                            // will throw a ClassCastException. In this case, we skip the index because the
                            // sort sequence is not supported by JDBC.
                            try {
                                keys.getInteger(key);
                            } catch (ClassCastException e) {
                                return false;
                            }
                            return true;
                        })
                .map(
                        key -> {
                            BsonValue ascOrDesc =
                                    new BsonString(keys.getInteger(key) > 0 ? "A" : "D");

                            return createSortableBottomBson(
                                    // Per JDBC spec, sort by  NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
                                    // Since TYPE is the same for every index, we omit it here.
                                    GET_INDEX_INFO_SORT_SPECS,
                                    new BsonElement(TABLE_CAT, new BsonString(dbName)),
                                    new BsonElement(TABLE_SCHEM, BsonNull.VALUE),
                                    new BsonElement(TABLE_NAME, new BsonString(tableName)),
                                    new BsonElement(NON_UNIQUE, nonUnique),
                                    new BsonElement(INDEX_QUALIFIER, BsonNull.VALUE),
                                    new BsonElement(INDEX_NAME, indexName),
                                    new BsonElement(TYPE, new BsonInt32(tableIndexOther)),
                                    new BsonElement(
                                            ORDINAL_POSITION, new BsonInt32(pos.incrementAndGet())),
                                    new BsonElement(COLUMN_NAME, new BsonString(key)),
                                    new BsonElement(ASC_OR_DESC, ascOrDesc),
                                    new BsonElement(CARDINALITY, BsonNull.VALUE),
                                    new BsonElement(PAGES, BsonNull.VALUE),
                                    new BsonElement(FILTER_CONDITION, BsonNull.VALUE));
                        });
    }

    // Helper for getting stream of bson documents for indexes in the argued table. This is
    // used for creating the result set for getIndexInfo method.
    private Stream<BsonDocument> getIndexesFromTable(
            String dbName, String tableName, boolean unique) {
        return this.conn
                .getDatabase(dbName)
                .getCollection(tableName)
                .listIndexes()
                .into(new ArrayList<>())
                .stream()
                .filter(
                        d -> {
                            Boolean isUnique = d.getEmbedded(UNIQUE_KEY_PATH, Boolean.class);

                            // If unique is false, include all indexes. If it is true, include
                            // only indexes that are marked as unique.
                            return !unique || (isUnique != null && isUnique);
                        })
                .flatMap(d -> toGetIndexInfoDocs(dbName, tableName, d));
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(NON_UNIQUE, BSON_BOOL),
                        new MongoJsonSchema.ScalarProperties(INDEX_QUALIFIER, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(INDEX_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(ORDINAL_POSITION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(ASC_OR_DESC, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(CARDINALITY, BSON_LONG),
                        new MongoJsonSchema.ScalarProperties(PAGES, BSON_LONG),
                        new MongoJsonSchema.ScalarProperties(FILTER_CONDITION, BSON_LONG, false));

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get indexes for all databases that have a
            // collection with the argued table name.
            docs =
                    this.getDatabaseNames()
                            .flatMap(
                                    dbName ->
                                            this.getTableDataFromDB(
                                                            dbName, res -> res.name.equals(table))
                                                    .flatMap(
                                                            r ->
                                                                    getIndexesFromTable(
                                                                            dbName, r.name,
                                                                            unique)));
        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs = getIndexesFromTable(catalog, table, unique);
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoResultSet(conn.getLogger(), c, botSchema);
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TYPE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(CLASS_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(BASE_TYPE, BSON_INT, false));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TYPE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SUPERTYPE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SUPERTYPE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SUPERTYPE_NAME, BSON_STRING));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SUPERTABLE_NAME, BSON_STRING));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TYPE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(ATTR_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(ATTR_TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(ATTR_SIZE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DECIMAL_DIGITS, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(NUM_PREC_RADIX, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(NULLABLE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(ATTR_DEF, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SQL_DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SQL_DATETIME_SUB, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(CHAR_OCTET_LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(ORDINAL_POSITION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(IS_NULLABLE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SCOPE_CATALOG, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SCOPE_SCHEMA, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SCOPE_TABLE, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(SOURCE_DATA_TYPE, BSON_INT, false));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    //------------------------- JDBC 4.0 -----------------------------------

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return getSchemas();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addScalarKeys(
                new MongoJsonSchema.ScalarProperties(NAME, BSON_STRING),
                new MongoJsonSchema.ScalarProperties(MAX_LEN, BSON_STRING),
                new MongoJsonSchema.ScalarProperties(DEFAULT_VALUE, BSON_STRING),
                new MongoJsonSchema.ScalarProperties(DESCRIPTION, BSON_STRING));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    private MongoJsonSchema getFunctionJsonSchema() {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(FUNCTION_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FUNCTION_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FUNCTION_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(FUNCTION_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SPECIFIC_NAME, BSON_STRING));

        return botSchema;
    }

    private BsonDocument getFunctionValuesDoc(String functionName, String remarks) {
        BsonDocument root = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        root.put(BOT_NAME, bot);
        bot.put(FUNCTION_CAT, new BsonString(FUNC_DEFAULT_CATALOG));
        bot.put(FUNCTION_SCHEM, BsonNull.VALUE);
        bot.put(FUNCTION_NAME, new BsonString(functionName));
        bot.put(REMARKS, new BsonString(remarks));
        bot.put(FUNCTION_TYPE, new BsonInt32(functionNoTable));
        bot.put(SPECIFIC_NAME, new BsonString(functionName));
        return root;
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>(MongoFunctions.functions.length);
        MongoJsonSchema schema = getFunctionJsonSchema();

        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = toJavaPattern(functionNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MongoFunctions.functions) {
            if (functionPatternRE != null && !functionPatternRE.matcher(func.name).matches()) {
                continue;
            }
            BsonDocument doc = getFunctionValuesDoc(func.name, func.comment);
            docs.add(doc);
        }

        return new MongoResultSet(conn.getLogger(), new BsonExplicitCursor(docs), schema);
    }

    private MongoJsonSchema getFunctionColumnJsonSchema() {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(FUNCTION_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FUNCTION_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(FUNCTION_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(TYPE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(PRECISION, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(SCALE, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(RADIX, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(NULLABLE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(CHAR_OCTET_LENGTH, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(ORDINAL_POSITION, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(IS_NULLABLE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(SPECIFIC_NAME, BSON_STRING));
        return botSchema;
    }

    /**
     * Returns information for a given function argument.
     *
     * @param func The function name.
     * @param i The parameter index.
     * @param argName The parameter name.
     * @param argType The parameter type.
     * @param isReturnColumn Is the parameter a return parameter.
     * @return the information for the given parameter.
     */
    private Map<String, BsonValue> getFunctionParameterValues(
            MongoFunctions.MongoFunction func,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn)
            throws SQLException {
        Map<String, BsonValue> info =
                new LinkedHashMap<
                        String,
                        BsonValue>(); // Using a LinkedHashMap to conserve the insertion order
        BsonTypeInfo bsonTypeInfo =
                argType == null ? BSON_UNDEFINED : BsonTypeInfo.getBsonTypeInfoByName(argType);
        info.put(FUNCTION_CAT, new BsonString(FUNC_DEFAULT_CATALOG));
        info.put(FUNCTION_SCHEM, BsonNull.VALUE);
        info.put(FUNCTION_NAME, new BsonString(func.name));

        info.put(COLUMN_NAME, new BsonString(argName));
        info.put(COLUMN_TYPE, asBsonIntOrNull(isReturnColumn ? functionReturn : functionColumnIn));
        info.put(DATA_TYPE, asBsonIntOrNull(bsonTypeInfo.getJdbcType()));
        info.put(
                TYPE_NAME,
                new BsonString(bsonTypeInfo == BSON_UNDEFINED ? "" : bsonTypeInfo.getBsonName()));

        info.put(PRECISION, asBsonIntOrNull(bsonTypeInfo.getPrecision()));
        // Note : LENGTH is only reported in getFunctionColumns and getProcedureColumns and is not flagged as 'may be null'
        // so for unknown length we are defaulting to 0.
        info.put(LENGTH, asBsonIntOrDefault(bsonTypeInfo.getFixedBytesLength(), 0));
        info.put(SCALE, asBsonIntOrNull(bsonTypeInfo.getDecimalDigits()));
        info.put(RADIX, new BsonInt32(bsonTypeInfo.getNumPrecRadix()));

        info.put(NULLABLE, new BsonInt32(functionNullable));
        info.put(REMARKS, new BsonString(func.comment));
        info.put(CHAR_OCTET_LENGTH, asBsonIntOrNull(bsonTypeInfo.getCharOctetLength()));

        info.put(ORDINAL_POSITION, new BsonInt32(i));
        info.put(IS_NULLABLE, new BsonString(YES));

        info.put(SPECIFIC_NAME, new BsonString(func.name));

        return info;
    }

    private BsonDocument getFunctionColumnValuesDoc(
            MongoFunctions.MongoFunction func,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn)
            throws SQLException {

        Map<String, BsonValue> info =
                getFunctionParameterValues(func, i, argName, argType, isReturnColumn);
        BsonDocument root = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        root.put(BOT_NAME, bot);
        String functionName = func.name;
        bot.putAll(info);
        return root;
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {

        ArrayList<BsonDocument> docs = new ArrayList<>(MongoFunctions.functions.length);
        MongoJsonSchema schema = getFunctionColumnJsonSchema();

        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = toJavaPattern(functionNamePattern);
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = toJavaPattern(columnNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MongoFunctions.functions) {
            if (functionNamePatternRE != null
                    && !functionNamePatternRE.matcher(func.name).matches()) {
                continue;
            }
            int i = 0;
            for (String argType : func.argTypes) {
                // We don't have better names for our arguments, for the most part.
                ++i;
                String columnName = "arg" + i;
                if (columnNamePatternRE != null
                        && !columnNamePatternRE.matcher(columnName).matches()) {
                    continue;
                }
                BsonDocument doc = getFunctionColumnValuesDoc(func, i, columnName, argType, false);
                docs.add(doc);
            }
            String columnName = "argReturn";
            if (columnNamePatternRE == null || columnNamePatternRE.matcher(columnName).matches()) {
                BsonDocument doc =
                        getFunctionColumnValuesDoc(func, i, "argReturn", func.returnType, true);
                docs.add(doc);
            }
        }

        return new MongoResultSet(conn.getLogger(), new BsonExplicitCursor(docs), schema);
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_CAT, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(TABLE_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_NAME, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(DATA_TYPE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(COLUMN_SIZE, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(DECIMAL_DIGITS, BSON_INT, false),
                        new MongoJsonSchema.ScalarProperties(NUM_PREC_RADIX, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(COLUMN_USAGE, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(REMARKS, BSON_STRING, false),
                        new MongoJsonSchema.ScalarProperties(CHAR_OCTET_LENGTH, BSON_INT),
                        new MongoJsonSchema.ScalarProperties(IS_NULLABLE, BSON_STRING));

        return new MongoResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
