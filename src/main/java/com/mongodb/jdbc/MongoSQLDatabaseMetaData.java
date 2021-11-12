package com.mongodb.jdbc;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoDatabase;
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
    private final MongoJsonSchema createBottomSchema(Pair<String, String>... resultSchemaFields) {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(resultSchemaFields);

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
                        new Pair<>(PROCEDURE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PROCEDURE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PROCEDURE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PROCEDURE_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
                        new Pair<>(PROCEDURE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PROCEDURE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PROCEDURE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PRECISION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SCALE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(RADIX, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NULLABLE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_DEF, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ORDINAL_POSITION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_NULLABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addRequiredScalarKeys(new Pair<>(TABLE_TYPE, BsonTypeInfo.STRING_TYPE_NAME));

        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("TABLE"))));
        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("VIEW"))));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);

        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), botSchema);
    }

    // Helper for getting a stream of all database names.
    private Stream<String> getDatabaseNames() {
        return this.conn.mongoClient.listDatabaseNames().into(new ArrayList<>()).stream();
    }

    // Helper for getting a stream of MongoListCollectionsResults from the argued db that match
    // the argued filter.
    private Stream<MongoListCollectionsResult> getTableDataFromDB(
            String dbName, Function<MongoListCollectionsResult, Boolean> filter) {
        return this.conn
                .getDatabase(dbName)
                .withCodecRegistry(MongoDriver.registry)
                .listCollections(MongoListCollectionsResult.class)
                .into(new ArrayList<>())
                .stream()
                .filter(filter::apply);
    }

    // Helper for creating BSON documents for the getTables method. Intended for use
    // with the getTableDataFromDB helper method which is shared between getTables and
    // getTablePrivileges.
    private BsonDocument toGetTablesDoc(String dbName, MongoListCollectionsResult res) {
        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_TYPE, TABLE_CAT, TABLE_SCHEM (omitted), and
                // TABLE_NAME.
                GET_TABLES_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(dbName)),
                new BsonElement(TABLE_SCHEM, new BsonString("")),
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
    private BsonDocument toGetTablePrivilegesDoc(String dbName, MongoListCollectionsResult res) {
        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME, and
                // PRIVILEGE. Since all PRIVILEGEs are the same, we also omit that.
                GET_TABLE_PRIVILEGES_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(dbName)),
                new BsonElement(TABLE_SCHEM, new BsonString("")),
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
            BiFunction<String, MongoListCollectionsResult, BsonDocument> bsonSerializer) {

        return this.getTableDataFromDB(
                        dbName,
                        res ->
                                tableNamePatternRE.matcher(res.name).matches()
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
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_TYPE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SELF_REFERENCING_COL_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(REF_GENERATION, BsonTypeInfo.STRING_TYPE_NAME));

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

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_CATALOG, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME));

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

        return new MongoSQLResultSet(null, c, botSchema);
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
        MongoJsonSchema columnSchema;
        boolean isRequired;
        int idx;

        GetColumnsDocInfo(
                String dbName,
                String tableName,
                String columnName,
                MongoJsonSchema columnSchema,
                boolean isRequired,
                int idx) {
            this.dbName = dbName;
            this.tableName = tableName;
            this.columnName = columnName;
            this.columnSchema = columnSchema;
            this.isRequired = isRequired;
            this.idx = idx;
        }
    }

    // Helper for creating BSON documents for the getColumns method. Intended for use
    // with the getColumnsFromDB helper method which is shared between getColumns and
    // getColumnPrivileges.
    private BsonDocument toGetColumnsDoc(GetColumnsDocInfo i) {
        String bsonType;
        int nullability;
        BsonValue dataType;
        BsonValue decimalDigits = BsonNull.VALUE;
        BsonValue numPrecRadix;
        BsonValue charOctetLength = BsonNull.VALUE;

        try {
            Pair<String, Integer> typeAndNullability =
                    BsonTypeInfo.getBsonTypeNameAndNullability(i.columnSchema, i.isRequired);
            bsonType = typeAndNullability.left();
            nullability = typeAndNullability.right();

            dataType = new BsonInt32(BsonTypeInfo.getJDBCType(bsonType));

            Integer d = BsonTypeInfo.getDecimalDigits(bsonType);
            if (d != null) {
                decimalDigits = new BsonInt32(d);
            }

            numPrecRadix = new BsonInt32(BsonTypeInfo.getNumPrecRadix(bsonType));

            Integer c = BsonTypeInfo.getCharOctetLength(bsonType);
            if (c != null) {
                charOctetLength = new BsonInt32(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        BsonValue isNullable =
                new BsonString(
                        nullability == columnNoNulls
                                ? "NO"
                                : nullability == columnNullable ? "YES" : "");

        return createSortableBottomBson(
                // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME and
                // ORDINAL_POSITION.
                GET_COLUMNS_SORT_SPECS,
                new BsonElement(TABLE_CAT, new BsonString(i.dbName)),
                new BsonElement(TABLE_SCHEM, new BsonString("")),
                new BsonElement(TABLE_NAME, new BsonString(i.tableName)),
                new BsonElement(COLUMN_NAME, new BsonString(i.columnName)),
                new BsonElement(DATA_TYPE, dataType),
                new BsonElement(TYPE_NAME, new BsonString(bsonType)),
                new BsonElement(COLUMN_SIZE, BsonNull.VALUE),
                new BsonElement(BUFFER_LENGTH, new BsonInt32(0)),
                new BsonElement(DECIMAL_DIGITS, decimalDigits),
                new BsonElement(NUM_PREC_RADIX, numPrecRadix),
                new BsonElement(NULLABLE, new BsonInt32(nullability)),
                new BsonElement(REMARKS, new BsonString("")),
                new BsonElement(COLUMN_DEF, BsonNull.VALUE),
                new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                new BsonElement(CHAR_OCTET_LENGTH, charOctetLength),
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
                new BsonElement(TABLE_SCHEM, new BsonString("")),
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
        return res.ok == 1 && res.schema.jsonSchema != null && res.schema.jsonSchema.isObject();
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
                .filter(tableName -> tableNamePatternRE.matcher(tableName).matches())

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
                                    .jsonSchema
                                    .properties
                                    .entrySet()
                                    .stream()

                                    // filter only for columns matching the pattern
                                    .filter(
                                            entry ->
                                                    columnNamePatternRE
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
                                                                    entry.getValue(),
                                                                    res.schema.jsonSchema.required
                                                                            .contains(
                                                                                    entry.getKey()),
                                                                    idx.getAndIncrement())));
                        });
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(BUFFER_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NULLABLE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_DEF, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ORDINAL_POSITION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_NULLABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_CATALOG, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_SCHEMA, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_TABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SOURCE_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_AUTOINCREMENT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(IS_GENERATEDCOLUMN, BsonTypeInfo.STRING_TYPE_NAME));

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

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(GRANTOR, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(GRANTEE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PRIVILEGE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(IS_GRANTABLE, BsonTypeInfo.STRING_TYPE_NAME));

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = toJavaPattern(tableNamePattern);
        Pattern columnNamePatternRE = toJavaPattern(columnNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get column privileges for all databases.
            docs =
                    this.getDatabaseNames()
                            .flatMap(
                                    dbName ->
                                            getColumnsFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    columnNamePatternRE,
                                                    this::toGetColumnPrivilegesDoc));
        } else if (catalog.isEmpty()) {
            // If catalog (database) is empty, we will return an empty result set because
            // MongoDB does not support tables (collections) without databases.
            docs = Stream.empty();
        } else {
            docs =
                    getColumnsFromDB(
                            catalog,
                            tableNamePatternRE,
                            columnNamePatternRE,
                            this::toGetColumnPrivilegesDoc);
        }

        // Collect to sorted list.
        List<BsonDocument> docsList = docs.sorted().collect(Collectors.toList());
        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(GRANTOR, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(GRANTEE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PRIVILEGE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(IS_GRANTABLE, BsonTypeInfo.STRING_TYPE_NAME));

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

        return new MongoSQLResultSet(null, c, botSchema);
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

            return new MongoSQLResultSet(null, c, botSchema);
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
        for (String key : keys.keySet()) {
            docs.add(
                    toGetBestRowIdentifierDoc(
                            key,
                            r.schema.jsonSchema.properties.get(key),
                            r.schema.jsonSchema.required.contains(key)));
        }

        return docs;
    }

    // Helper for creating a result set BsonDocument for an index column for the
    // getBestRowIdentifier method.
    private BsonDocument toGetBestRowIdentifierDoc(
            String columnName, MongoJsonSchema columnSchema, boolean isRequired) {
        Pair<String, Integer> typeAndNullability;
        String bsonType;
        BsonValue dataType;
        BsonValue columnSize = BsonNull.VALUE;
        BsonValue decimalDigits = BsonNull.VALUE;

        try {
            typeAndNullability =
                    BsonTypeInfo.getBsonTypeNameAndNullability(columnSchema, isRequired);

            bsonType = typeAndNullability.left();

            dataType = new BsonInt32(BsonTypeInfo.getJDBCType(bsonType));

            Integer s = BsonTypeInfo.getPrecision(bsonType);
            if (s != null) {
                columnSize = new BsonInt32(s);
            }

            Integer d = BsonTypeInfo.getDecimalDigits(bsonType);
            if (d != null) {
                decimalDigits = new BsonInt32(d);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createBottomBson(
                new BsonElement(SCOPE, BsonNull.VALUE),
                new BsonElement(COLUMN_NAME, new BsonString(columnName)),
                new BsonElement(DATA_TYPE, dataType),
                new BsonElement(TYPE_NAME, new BsonString(bsonType)),
                new BsonElement(COLUMN_SIZE, columnSize),
                new BsonElement(BUFFER_LENGTH, BsonNull.VALUE),
                new BsonElement(DECIMAL_DIGITS, decimalDigits),
                new BsonElement(PSEUDO_COLUMN, new BsonInt32(bestRowNotPseudo)));
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(SCOPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(BUFFER_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(PSEUDO_COLUMN, BsonTypeInfo.INT_TYPE_NAME));

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
                        new Pair<>(SCOPE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(BUFFER_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(PSEUDO_COLUMN, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(PKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(KEY_SEQ, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(UPDATE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DELETE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(FK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DEFERRABILITY, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(PKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(KEY_SEQ, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(UPDATE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DELETE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(FK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DEFERRABILITY, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
                        new Pair<>(PKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(KEY_SEQ, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(UPDATE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DELETE_RULE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(FK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PK_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DEFERRABILITY, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
                                        new BsonElement(TABLE_SCHEM, new BsonString("")),
                                        new BsonElement(
                                                TABLE_NAME, new BsonString(namespace.right())),
                                        new BsonElement(COLUMN_NAME, new BsonString(key)),
                                        new BsonElement(
                                                KEY_SEQ, new BsonInt32(pos.getAndIncrement())),
                                        new BsonElement(PK_NAME, new BsonString(indexName))))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(KEY_SEQ, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(PK_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        // As in other methods, we ignore the schema argument.
        return liftSQLException(
                () ->
                        getFirstUniqueIndexResultSet(
                                catalog, table, botSchema, this::toGetPrimaryKeysDocs));
    }

    private MongoJsonSchema getTypeInfoJsonSchema() {

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addRequiredScalarKeys(
                new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(PRECISION, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(LITERAL_PREFIX, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(LITERAL_SUFFIX, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(CREATE_PARAMS, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(NULLABLE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(CASE_SENSITIVE, BsonTypeInfo.BOOL_TYPE_NAME),
                new Pair<>(SEARCHABLE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(UNSIGNED_ATTRIBUTE, BsonTypeInfo.BOOL_TYPE_NAME),
                new Pair<>(FIX_PREC_SCALE, BsonTypeInfo.BOOL_TYPE_NAME),
                new Pair<>(AUTO_INCREMENT, BsonTypeInfo.BOOL_TYPE_NAME),
                new Pair<>(LOCAL_TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(MINIMUM_SCALE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(MAXIMUM_SCALE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.INT_TYPE_NAME));
        return schema;
    }

    private BsonValue asBsonIntOrNull(Integer value) {
        if (value == null) {
            return new BsonNull();
        }
        return new BsonInt32(value);
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        BsonValue n = new BsonNull();
        ArrayList<BsonDocument> docs = new ArrayList<>();
        MongoJsonSchema schema = getTypeInfoJsonSchema();

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.BINDATA_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BINARY)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.BINDATA_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BINDATA_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typePredNone)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.BINDATA_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.BINDATA_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BINDATA_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.BOOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIT)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.BOOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BOOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.BOOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.BOOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BOOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.DATE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.TIMESTAMP)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.DATE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.DATE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.DATE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.DATE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.DATE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.DECIMAL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DECIMAL)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.DECIMAL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.DECIMAL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.DECIMAL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.DECIMAL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.DECIMAL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.DOUBLE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DOUBLE)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.DOUBLE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.DOUBLE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.DOUBLE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.DOUBLE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.DOUBLE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.INT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.INTEGER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.INT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.INT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.INT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.INT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.INT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.LONG_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIGINT)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.LONG_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.LONG_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.LONG_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.LONG_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.LONG_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.STRING_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.LONGVARCHAR)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.STRING_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.STRING_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.STRING_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.STRING_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.STRING_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.ARRAY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.ARRAY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.ARRAY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.ARRAY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.ARRAY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.ARRAY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.OBJECT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.OBJECT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.OBJECT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.OBJECT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.OBJECT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.OBJECT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.OBJECTID_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.OBJECTID_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.OBJECTID_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.OBJECTID_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.OBJECTID_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.OBJECTID_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.DBPOINTER_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.DBPOINTER_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.DBPOINTER_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.DBPOINTER_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.DBPOINTER_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.DBPOINTER_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.JAVASCRIPT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.JAVASCRIPT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME,
                                new BsonString(BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.JAVASCRIPTWITHSCOPE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.MAXKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.MAXKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.MAXKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.MAXKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.MAXKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.MAXKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.MINKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.MINKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.MINKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.MINKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.MINKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.MINKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.REGEX_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.REGEX_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.REGEX_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.REGEX_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.REGEX_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.REGEX_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.SYMBOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.SYMBOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.SYMBOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.SYMBOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.SYMBOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.SYMBOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.TIMESTAMP_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.TIMESTAMP_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.TIMESTAMP_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.TIMESTAMP_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.UNDEFINED_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.UNDEFINED_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.UNDEFINED_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.UNDEFINED_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.UNDEFINED_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.UNDEFINED_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.BSON_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(BsonTypeInfo.BSON_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.BSON_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.BSON_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_TYPE_NAME)))));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), botSchema);
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
                                    new BsonElement(TABLE_SCHEM, new BsonString("")),
                                    new BsonElement(TABLE_NAME, new BsonString(tableName)),
                                    new BsonElement(NON_UNIQUE, nonUnique),
                                    new BsonElement(INDEX_QUALIFIER, BsonNull.VALUE),
                                    new BsonElement(INDEX_NAME, indexName),
                                    new BsonElement(TYPE, new BsonInt32(tableIndexOther)),
                                    new BsonElement(
                                            ORDINAL_POSITION, new BsonInt32(pos.getAndIncrement())),
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
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(NON_UNIQUE, BsonTypeInfo.BOOL_TYPE_NAME),
                        new Pair<>(INDEX_QUALIFIER, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(INDEX_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ORDINAL_POSITION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(ASC_OR_DESC, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(CARDINALITY, BsonTypeInfo.LONG_TYPE_NAME),
                        new Pair<>(PAGES, BsonTypeInfo.LONG_TYPE_NAME),
                        new Pair<>(FILTER_CONDITION, BsonTypeInfo.LONG_TYPE_NAME));

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

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(CLASS_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(BASE_TYPE, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SUPERTYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SUPERTYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SUPERTYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SUPERTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
                        new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(ATTR_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ATTR_TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(ATTR_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NULLABLE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(ATTR_DEF, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ORDINAL_POSITION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_NULLABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_CATALOG, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_SCHEMA, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SCOPE_TABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SOURCE_DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
        schema.addRequiredScalarKeys(
                new Pair<>(NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(MAX_LEN, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(DEFAULT_VALUE, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(DESCRIPTION, BsonTypeInfo.STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    private MongoJsonSchema getFunctionJsonSchema() {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(FUNCTION_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FUNCTION_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FUNCTION_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FUNCTION_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return botSchema;
    }

    private BsonDocument getFunctionValuesDoc(String functionName, String remarks) {
        BsonDocument root = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        root.put(BOT_NAME, bot);
        bot.put(FUNCTION_CAT, new BsonString("def"));
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

        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), schema);
    }

    private MongoJsonSchema getFunctionColumnJsonSchema() {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(FUNCTION_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FUNCTION_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(FUNCTION_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(PRECISION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(SCALE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(RADIX, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NULLABLE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(ORDINAL_POSITION, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_NULLABLE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        return botSchema;
    }

    private BsonDocument getFunctionColumnValuesDoc(
            MongoFunctions.MongoFunction func,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn) {
        BsonDocument root = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        root.put(BOT_NAME, bot);
        BsonValue n = BsonNull.VALUE;
        String functionName = func.name;
        bot.put(FUNCTION_CAT, new BsonString("def"));
        bot.put(FUNCTION_SCHEM, n);
        bot.put(FUNCTION_NAME, new BsonString(functionName));

        bot.put(COLUMN_NAME, new BsonString(argName));
        bot.put(COLUMN_TYPE, new BsonInt32(isReturnColumn ? functionReturn : functionColumnIn));
        bot.put(DATA_TYPE, new BsonInt32(typeNum(argType)));
        bot.put(TYPE_NAME, argType == null ? n : new BsonString(argType));

        bot.put(PRECISION, new BsonInt32(typePrec(argType)));
        bot.put(LENGTH, new BsonInt32(typeBytes(argType)));
        bot.put(SCALE, new BsonInt32(typeScale(argType)));
        bot.put(RADIX, new BsonInt32(typeBytes(argType)));

        bot.put(NULLABLE, new BsonInt32(functionNullable));
        bot.put(REMARKS, new BsonString(func.comment));
        bot.put(CHAR_OCTET_LENGTH, bsonInt32(typeBytes(argType)));

        bot.put(ORDINAL_POSITION, new BsonInt32(i));
        bot.put(IS_NULLABLE, new BsonString("YES"));

        bot.put(SPECIFIC_NAME, new BsonString(functionName));
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

        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), schema);
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema botSchema =
                createBottomSchema(
                        new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(COLUMN_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(COLUMN_USAGE, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                        new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                        new Pair<>(IS_NULLABLE, BsonTypeInfo.STRING_TYPE_NAME));

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
