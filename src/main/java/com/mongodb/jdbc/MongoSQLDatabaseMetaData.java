package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.jdbc.logging.AutoLoggable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static final String BOT_NAME = "";
    private static final String INDEX_KEY_KEY = "key";
    private static final String INDEX_NAME_KEY = "name";

    private static final List<String> UNIQUE_KEY_PATH = Arrays.asList("options", "unique");

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
                            NON_UNIQUE, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            INDEX_NAME, SortableBsonDocument.ValueType.String),
                    new SortableBsonDocument.SortSpec(
                            ORDINAL_POSITION, SortableBsonDocument.ValueType.Int));

    private static final com.mongodb.jdbc.MongoSQLFunctions MongoSQLFunctions =
            com.mongodb.jdbc.MongoSQLFunctions.getInstance();

    public MongoSQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
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
    @SafeVarargs
    private final MongoJsonSchema createBottomSchema(
            MongoJsonSchema.ScalarProperties... resultSchemaFields) {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addScalarKeys(resultSchemaFields);

        MongoJsonSchema bot = MongoJsonSchema.createEmptyObjectSchema();
        bot.required.add(BOT_NAME);
        bot.properties.put(BOT_NAME, resultSchema);
        return bot;
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
        return MongoSQLFunctions.numericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return MongoSQLFunctions.stringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return MongoSQLFunctions.systemFunctionsString;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return MongoSQLFunctions.dateFunctionsString;
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), new BsonExplicitCursor(docs), botSchema);
    }

    // Helper for getting a stream of all database names.
    private Stream<String> getDatabaseNames() {
        return this.conn.mongoClient.listDatabaseNames().into(new ArrayList<>()).stream();
    }

    // Helper for getting a stream of MongoListCollectionsResults from the argued db that match
    // the argued filter.
    private Stream<MongoListTablesResult> getTableDataFromDB(
            String dbName, Function<MongoListTablesResult, Boolean> filter) {
        return this.conn
                .getDatabase(dbName)
                .withCodecRegistry(MongoDriver.registry)
                .listCollections(MongoListTablesResult.class)
                .into(new ArrayList<>())
                .stream()
                .filter(filter::apply);
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
                new BsonElement(GRANTEE, new BsonString("")),
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

        return this.getTableDataFromDB(
                        dbName,
                        res ->
                                (tableNamePatternRE == null
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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new MongoJsonSchema.ScalarProperties(TABLE_SCHEM, BSON_STRING),
                        new MongoJsonSchema.ScalarProperties(TABLE_CATALOG, BSON_STRING, false));

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
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
                new BsonString(
                        i.nullability == columnNoNulls
                                ? "NO"
                                : i.nullability == columnNullable ? "YES" : "");

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
                new BsonElement(BUFFER_LENGTH, new BsonInt32(0)),
                new BsonElement(
                        DECIMAL_DIGITS, asBsonIntOrNull(i.columnBsonTypeInfo.getDecimalDigits())),
                new BsonElement(
                        NUM_PREC_RADIX, new BsonInt32(i.columnBsonTypeInfo.getNumPrecRadix())),
                new BsonElement(NULLABLE, new BsonInt32(i.nullability)),
                new BsonElement(REMARKS, new BsonString("")),
                new BsonElement(COLUMN_DEF, BsonNull.VALUE),
                new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                new BsonElement(
                        CHAR_OCTET_LENGTH,
                        asBsonIntOrNull(i.columnBsonTypeInfo.getCharOctetLength())),
                new BsonElement(ORDINAL_POSITION, new BsonInt32(i.idx)),
                new BsonElement(IS_NULLABLE, isNullable),
                new BsonElement(SCOPE_CATALOG, BsonNull.VALUE),
                new BsonElement(SCOPE_SCHEMA, BsonNull.VALUE),
                new BsonElement(SCOPE_TABLE, BsonNull.VALUE),
                new BsonElement(SOURCE_DATA_TYPE, new BsonInt32(0)),
                new BsonElement(IS_AUTOINCREMENT, new BsonString("NO")),
                new BsonElement(IS_GENERATEDCOLUMN, new BsonString("")));
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
                new BsonElement(GRANTEE, new BsonString("")),
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
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.registry);

        return db.listCollectionNames()
                .into(new ArrayList<>())
                .stream()

                // filter only for collections matching the pattern
                .filter(
                        tableName ->
                                tableNamePatternRE == null
                                        || tableNamePatternRE.matcher(tableName).matches())

                // map the collection names into triples of (dbName, tableName, tableSchema)
                .map(
                        tableName ->
                                new Pair<>(
                                        new Pair<>(dbName, tableName),
                                        db.runCommand(
                                                new BsonDocument(
                                                        "sqlGetSchema", new BsonString(tableName)),
                                                MongoJsonSchemaResult.class)))

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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
    }

    private Stream<BsonDocument> getFirstUniqueIndexDocsForTable(
            String dbName,
            String tableName,
            BiFunction<Pair<String, String>, Document, List<BsonDocument>> serializer) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.registry);
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

            return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
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
        MongoJsonSchemaResult r =
                this.conn
                        .getDatabase(namespace.left())
                        .runCommand(
                                new BsonDocument("sqlGetSchema", new BsonString(namespace.right())),
                                MongoJsonSchemaResult.class);

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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
        BsonValue n = new BsonNull();
        ArrayList<BsonDocument> docs = new ArrayList<>();
        MongoJsonSchema schema = getTypeInfoJsonSchema();

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BINDATA.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BINARY)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BINDATA.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BINDATA.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typePredNone)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BINDATA.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BINDATA.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BINDATA.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BOOL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIT)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BOOL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BOOL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BOOL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BOOL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BOOL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DATE.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.TIMESTAMP)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DATE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DATE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DATE.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DATE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DATE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DECIMAL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DECIMAL)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DECIMAL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DECIMAL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DECIMAL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DECIMAL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DECIMAL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DOUBLE.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DOUBLE)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DOUBLE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_DOUBLE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DOUBLE.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DOUBLE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DOUBLE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_INT.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.INTEGER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_INT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_INT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_INT.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_INT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_INT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_LONG.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIGINT)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_LONG.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_LONG.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_LONG.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_LONG.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_LONG.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_STRING.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.LONGVARCHAR)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_STRING.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_STRING.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_STRING.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_STRING.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_STRING.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_ARRAY.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_ARRAY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_ARRAY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_ARRAY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_ARRAY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_ARRAY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECT.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_OBJECT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_OBJECT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_OBJECT.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_OBJECT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_OBJECT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECTID.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_OBJECTID.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_OBJECTID.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_OBJECTID.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_OBJECTID.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_OBJECTID.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DBPOINTER.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_DBPOINTER.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_DBPOINTER.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_DBPOINTER.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_DBPOINTER.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_DBPOINTER.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_JAVASCRIPT.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_JAVASCRIPT.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_JAVASCRIPT.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(BSON_JAVASCRIPT.getMinScale())),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(BSON_JAVASCRIPT.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_JAVASCRIPT.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BSON_JAVASCRIPTWITHSCOPE.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(BSON_JAVASCRIPTWITHSCOPE.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_JAVASCRIPTWITHSCOPE.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getMinScale())),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(BSON_JAVASCRIPTWITHSCOPE.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MAXKEY.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_MAXKEY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_MAXKEY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_MAXKEY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_MAXKEY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_MAXKEY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MINKEY.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_MINKEY.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_MINKEY.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_MINKEY.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_MINKEY.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_MINKEY.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_REGEX.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_REGEX.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_REGEX.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_REGEX.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_REGEX.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_REGEX.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_SYMBOL.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_SYMBOL.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_SYMBOL.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_SYMBOL.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_SYMBOL.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_SYMBOL.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_TIMESTAMP.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_TIMESTAMP.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_TIMESTAMP.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_TIMESTAMP.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_TIMESTAMP.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_TIMESTAMP.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_UNDEFINED.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_UNDEFINED.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(BSON_UNDEFINED.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_UNDEFINED.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_UNDEFINED.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_UNDEFINED.getNumPrecRadix()))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BSON.getBsonName())),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(PRECISION, asBsonIntOrNull(BSON_BSON.getPrecision())),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE, new BsonBoolean(BSON_BSON.getCaseSensitivity())),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(MINIMUM_SCALE, new BsonInt32(BSON_BSON.getMinScale())),
                        new BsonElement(MAXIMUM_SCALE, new BsonInt32(BSON_BSON.getMaxScale())),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX, new BsonInt32(BSON_BSON.getNumPrecRadix()))));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(conn.getLogger(), new BsonExplicitCursor(docs), botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), c, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
        ArrayList<BsonDocument> docs = new ArrayList<>(MongoSQLFunctions.functions.length);
        MongoJsonSchema schema = getFunctionJsonSchema();

        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = toJavaPattern(functionNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MongoSQLFunctions.functions) {
            if (functionPatternRE != null && !functionPatternRE.matcher(func.name).matches()) {
                continue;
            }
            BsonDocument doc = getFunctionValuesDoc(func.name, func.comment);
            docs.add(doc);
        }

        return new MongoSQLResultSet(conn.getLogger(), new BsonExplicitCursor(docs), schema);
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

    private BsonDocument getFunctionColumnValuesDoc(
            MongoFunctions.MongoFunction func,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn)
            throws SQLException {

        Map<String, BsonValue> info =
                super.getFunctionParameterValues(func, i, argName, argType, isReturnColumn);
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

        ArrayList<BsonDocument> docs = new ArrayList<>(MongoSQLFunctions.functions.length);
        MongoJsonSchema schema = getFunctionColumnJsonSchema();

        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = toJavaPattern(functionNamePattern);
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = toJavaPattern(columnNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MongoSQLFunctions.functions) {
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

        return new MongoSQLResultSet(conn.getLogger(), new BsonExplicitCursor(docs), schema);
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

        return new MongoSQLResultSet(conn.getLogger(), BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
