package com.mongodb.jdbc;

import com.mongodb.client.MongoDatabase;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Arrays;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;

public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static final String BOT_NAME = "";

    private static com.mongodb.jdbc.MongoSQLFunctions MongoSQLFunctions =
            com.mongodb.jdbc.MongoSQLFunctions.getInstance();

    public MongoSQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
    }

    private BsonDocument createBottomBson(BsonElement... elements) {
        BsonDocument bot = new BsonDocument(Arrays.asList(elements));
        return new BsonDocument(BOT_NAME, bot);
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
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(PROCEDURE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

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

    private BsonDocument toGetTablesDoc(String dbName, MongoListCollectionsResult res) {
        BsonDocument bot = new BsonDocument();
        bot.put(TABLE_CAT, new BsonString(dbName));
        bot.put(TABLE_SCHEM, new BsonString(""));
        bot.put(TABLE_NAME, new BsonString(res.name));
        bot.put(TABLE_TYPE, new BsonString(res.type));
        bot.put(REMARKS, new BsonNull());
        bot.put(TYPE_CAT, new BsonNull());
        bot.put(TYPE_SCHEM, new BsonNull());
        bot.put(TYPE_NAME, new BsonNull());
        bot.put(SELF_REFERENCING_COL_NAME, new BsonNull());
        bot.put(REF_GENERATION, new BsonNull());

        return new BsonDocument(BOT_NAME, bot);
    }

    private BsonDocument toGetTablePrivilegesDoc(String dbName, MongoListCollectionsResult res) {
        BsonDocument bot = new BsonDocument();
        bot.put(TABLE_CAT, new BsonString(dbName));
        bot.put(TABLE_SCHEM, new BsonString(""));
        bot.put(TABLE_NAME, new BsonString(res.name));
        bot.put(GRANTOR, new BsonNull());
        bot.put(GRANTEE, new BsonString(""));
        bot.put(PRIVILEGE, new BsonString("SELECT"));
        bot.put(IS_GRANTABLE, new BsonNull());

        return new BsonDocument(BOT_NAME, bot);
    }

    private Stream<BsonDocument> getTablesFromDB(
            String dbName,
            Pattern tableNamePatternRE,
            List<String> types,
            BiFunction<String, MongoListCollectionsResult, BsonDocument> bsonSerializer) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.registry);

        return db.listCollections(MongoListCollectionsResult.class)
                .into(new ArrayList<>())
                .stream()
                .filter(
                        res ->
                                tableNamePatternRE.matcher(res.name).matches()
                                        && (types == null || types.contains(res.type)))
                .map(res -> bsonSerializer.apply(dbName, res));
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_TYPE, BSON_STRING_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(SELF_REFERENCING_COL_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(REF_GENERATION, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = Pattern.compile(tableNamePattern);
        List<String> typesList = Arrays.asList(types);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            docs =
                    this.conn
                            .mongoClient
                            .listDatabaseNames()
                            .into(new ArrayList<>())
                            .stream()
                            .flatMap(
                                    dbName ->
                                            getTablesFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    typesList,
                                                    this::toGetTablesDoc));
        } else {
            docs = getTablesFromDB(catalog, tableNamePatternRE, typesList, this::toGetTablesDoc);
        }

        // Collect to a sorted list
        List<BsonDocument> docsList =
                // Per JDBC spec, sort by  TABLE_TYPE, TABLE_CAT, TABLE_SCHEM (omitted), and
                // TABLE_NAME. Since we need to sort by TABLE_TYPE first, we do it after
                // converting the MongoListCollectionResults to BSON documents.
                docs.sorted(
                                Comparator.comparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_TYPE))
                                        .thenComparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_CAT))
                                        .thenComparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_NAME)))
                        .collect(Collectors.toList());

        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TABLE_CATALOG, BsonTypeInfo.STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        BsonExplicitCursor c =
                new BsonExplicitCursor(
                        this.conn
                                .mongoClient
                                .listDatabaseNames()
                                .into(new ArrayList<>())
                                .stream()
                                .sorted()
                                .map(
                                        dbName ->
                                                new BsonDocument(
                                                        "",
                                                        new BsonDocument(
                                                                "TABLE_CAT",
                                                                new BsonString(dbName))))
                                .collect(Collectors.toList()));

        return new MongoSQLResultSet(null, c, botSchema);
    }

    /**
     * liftSQLException tries to execute a Supplier that may throw a RuntimeException that wraps a
     * SQLException as the cause. If such an exception is encountered, the inner SQLException is
     * thrown. Otherwise, the Supplier executes as expected.
     *
     * <p>This method is intended for use with Stream API methods that wrap SQLExceptions in
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

    private class GetColumnsDocInfo {
        String dbName;
        String collName;
        String columnName;
        MongoJsonSchema columnSchema;
        boolean isRequired;

        GetColumnsDocInfo(String dbName, String collName, String columnName, MongoJsonSchema columnSchema, boolean isRequired) {
            this.dbName = dbName;
            this.collName = collName;
            this.columnName = columnName;
            this.columnSchema = columnSchema;
            this.isRequired = isRequired;
        }
    }

    private BsonDocument toGetColumnsDoc(GetColumnsDocInfo i) {

        MongoSQLColumnInfo info;
        try {
            info = new MongoSQLColumnInfo(i.collName, i.columnName, i.columnSchema, i.isRequired);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int nullability = info.getNullability();
        String isNullable =
                nullability == ResultSetMetaData.columnNoNulls
                        ? "NO"
                        : nullability == ResultSetMetaData.columnNullable ? "YES" : "";

        BsonDocument bot = new BsonDocument();
        bot.put(TABLE_CAT, new BsonString(i.dbName));
        bot.put(TABLE_SCHEM, new BsonString(""));
        bot.put(TABLE_NAME, new BsonString(i.collName));
        bot.put(COLUMN_NAME, new BsonString(i.columnName));
        bot.put(DATA_TYPE, new BsonInt32(info.getJDBCType()));
        bot.put(TYPE_NAME, new BsonString(info.getTableAlias()));
        bot.put(COLUMN_SIZE, new BsonNull()); // TODO
        bot.put(BUFFER_LENGTH, new BsonInt32(0));
        bot.put(DECIMAL_DIGITS, new BsonNull()); // TODO
        bot.put(NUM_PREC_RADIX, new BsonNull()); // TODO
        bot.put(NULLABLE, new BsonInt32(nullability));
        bot.put(REMARKS, new BsonString(""));
        bot.put(COLUMN_DEF, new BsonNull());
        bot.put(SQL_DATA_TYPE, new BsonInt32(0));
        bot.put(SQL_DATETIME_SUB, new BsonInt32(0));
        bot.put(CHAR_OCTET_LENGTH, new BsonNull()); // TODO
        bot.put(ORDINAL_POSITION, new BsonNull()); // TODO
        bot.put(IS_NULLABLE, new BsonString(isNullable));
        bot.put(SCOPE_CATALOG, new BsonNull());
        bot.put(SCOPE_SCHEMA, new BsonNull());
        bot.put(SCOPE_TABLE, new BsonNull());
        bot.put(SOURCE_DATA_TYPE, new BsonInt32(0));
        bot.put(IS_AUTOINCREMENT, new BsonString(""));
        bot.put(IS_GENERATEDCOLUMN, new BsonInt32(0));

        return new BsonDocument(BOT_NAME, bot);
    }

    private BsonDocument toGetColumnPrivilegesDoc(GetColumnsDocInfo i) {
        BsonDocument bot = new BsonDocument();
        bot.put(TABLE_CAT, new BsonString(i.dbName));
        bot.put(TABLE_SCHEM, new BsonString(""));
        bot.put(TABLE_NAME, new BsonString(i.collName));
        bot.put(COLUMN_NAME, new BsonString(i.columnName));
        bot.put(GRANTOR, new BsonNull());
        bot.put(GRANTEE, new BsonString(""));
        bot.put(PRIVILEGE, new BsonString("SELECT"));
        bot.put(IS_GRANTABLE, new BsonNull());

        return new BsonDocument(BOT_NAME, bot);
    }

    private Stream<BsonDocument> getColumnsFromDB(
            String dbName, Pattern tableNamePatternRE, Pattern columnNamePatternRE, Function<GetColumnsDocInfo, BsonDocument> f) {
        MongoDatabase db = this.conn.getDatabase(dbName).withCodecRegistry(MongoDriver.registry);

        return db.listCollectionNames()
                                .into(new ArrayList<>())
                                .stream()

                                // filter only for collections matching the pattern
                                .filter(collName -> tableNamePatternRE.matcher(collName).matches())

                                // map the collection names into triples of (dbName, collName, collSchema)
                                .map(
                                        collName ->
                                                new Pair<>(
                                                        new Pair<>(dbName, collName),
                                                        db.runCommand(
                                                                new BsonDocument(
                                                                        "sqlGetSchema",
                                                                        new BsonString(collName)),
                                                                MongoJsonSchemaResult.class)))

                                // filter only for collections that have schemas
                                .filter(
                                        p ->
                                                p.right().ok == 1
                                                        && p.right().schema.jsonSchema.isObject())

                                // flatMap the column data into a single stream of BSON docs
                                .flatMap(
                                        p -> {
                                            Pair<String, String> ns = p.left();
                                            MongoJsonSchemaResult res = p.right();
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

                                                    // map the (columnName, columnSchema) pairs into BSON docs
                                                    .map(
                                                            entry -> f.apply(new GetColumnsDocInfo(
                                                                            ns.left(),
                                                                            ns.right(),
                                                                            entry.getKey(),
                                                                            entry.getValue(),
                                                                            res.schema.jsonSchema
                                                                                    .required
                                                                                    .contains(
                                                                                            entry
                                                                                                    .getKey()))));
                                        });
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BSON_INT_TYPE_NAME),
                new Pair<>(BUFFER_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BSON_INT_TYPE_NAME),
                new Pair<>(NULLABLE, BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_DEF, BSON_STRING_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BSON_INT_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(ORDINAL_POSITION, BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_CATALOG, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_SCHEMA, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_TABLE, BSON_STRING_TYPE_NAME),
                new Pair<>(SOURCE_DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(IS_AUTOINCREMENT, BSON_STRING_TYPE_NAME),
                new Pair<>(IS_GENERATEDCOLUMN, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = Pattern.compile(toJavaPattern(tableNamePattern));
        Pattern columnNamePatternRE = Pattern.compile(toJavaPattern(columnNamePattern));

        Stream<BsonDocument> docs;
        if (catalog == null) {
            // If no catalog (database) is specified, get columns for all databases.
            docs =
                    liftSQLException(
                            () ->
                                    this.conn
                                            .mongoClient
                                            .listDatabaseNames()
                                            .into(new ArrayList<>())
                                            .stream()
                                            .flatMap(
                                                    dbName -> getColumnsFromDB(
                                                                    dbName,
                                                                    tableNamePatternRE,
                                                                    columnNamePatternRE,
                                                                    this::toGetColumnsDoc)));

        } else {
            docs = liftSQLException(() ->
                    getColumnsFromDB(catalog, tableNamePatternRE, columnNamePatternRE, this::toGetColumnsDoc)
                            );
        }

        // Collect to a sorted list
        List<BsonDocument> docsList =
                // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME and
                // ORDINAL_POSITION (column name sort order, for us).
                docs.sorted(
                                Comparator.comparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_CAT))
                                        .thenComparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_NAME))
                                        .thenComparing(
                                                (BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(COLUMN_NAME)))
                        .collect(Collectors.toList());

        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(GRANTOR, BSON_STRING_TYPE_NAME),
                new Pair<>(GRANTEE, BSON_STRING_TYPE_NAME),
                new Pair<>(PRIVILEGE, BSON_STRING_TYPE_NAME),
                new Pair<>(IS_GRANTABLE, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = Pattern.compile(tableNamePattern);
        Pattern columnNamePatternRE = Pattern.compile(columnNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            docs =
                    this.conn
                            .mongoClient
                            .listDatabaseNames()
                            .into(new ArrayList<>())
                            .stream()
                            .flatMap(
                                    dbName ->
                                            getColumnsFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    columnNamePatternRE,
                                                    this::toGetColumnPrivilegesDoc));
        } else {
            docs =
                    getColumnsFromDB(
                            catalog, tableNamePatternRE, columnNamePatternRE, this::toGetColumnPrivilegesDoc);
        }

        // Per JDBC spec, sort by  COLUMN_NAME and PRIVILEGE. Since all PRIVILEGEs are the same,
        // we just sort by COLUMN_NAME here.
        List<BsonDocument> docsList =
                docs.sorted(Comparator.comparing((BsonDocument doc) -> doc.getString(COLUMN_NAME)))
                        .collect(Collectors.toList());

        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(GRANTOR, BSON_STRING_TYPE_NAME),
                new Pair<>(GRANTEE, BSON_STRING_TYPE_NAME),
                new Pair<>(PRIVILEGE, BSON_STRING_TYPE_NAME),
                new Pair<>(IS_GRANTABLE, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        // Note: JDBC has Catalogs, Schemas, and Tables: they are three levels of organization.
        // MongoDB only has Databases (Catalogs) and Collections (Tables), so we ignore the
        // schemaPattern argument.
        Pattern tableNamePatternRE = Pattern.compile(tableNamePattern);

        Stream<BsonDocument> docs;
        if (catalog == null) {
            docs =
                    this.conn
                            .mongoClient
                            .listDatabaseNames()
                            .into(new ArrayList<>())
                            .stream()
                            .sorted()
                            .flatMap(
                                    dbName ->
                                            getTablesFromDB(
                                                    dbName,
                                                    tableNamePatternRE,
                                                    null,
                                                    this::toGetTablePrivilegesDoc));
        } else {
            docs =
                    getTablesFromDB(
                            catalog, tableNamePatternRE, null, this::toGetTablePrivilegesDoc);
        }

        // Per JDBC spec, sort by  TABLE_CAT, TABLE_SCHEM (omitted), TABLE_NAME, and
        // PRIVILEGE. Since the stream is already sorted by TABLE_CAT at this point
        // and all PRIVILEGEs are the same, we just sort by TABLE_NAME here.
        List<BsonDocument> docsList =
                docs.sorted(Comparator.comparing((BsonDocument doc) -> doc.getDocument(BOT_NAME).getString(TABLE_NAME)))
                        .collect(Collectors.toList());

        BsonExplicitCursor c = new BsonExplicitCursor(docsList);

        return new MongoSQLResultSet(null, c, botSchema);
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        // TODO
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(SCOPE, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(BUFFER_LENGTH, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(PSEUDO_COLUMN, BsonTypeInfo.INT_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

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
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        // TODO
        throw new SQLFeatureNotSupportedException("TODO");
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

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        // TODO
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(CLASS_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(BASE_TYPE, BsonTypeInfo.INT_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(SUPERTABLE_NAME, BsonTypeInfo.STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

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
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.required.add(BOT_NAME);
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.addRequiredScalarKeys(
                new Pair<>(FUNCTION_CAT, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(FUNCTION_SCHEM, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(FUNCTION_NAME, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.STRING_TYPE_NAME),
                new Pair<>(FUNCTION_TYPE, BsonTypeInfo.INT_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BsonTypeInfo.STRING_TYPE_NAME));
        resultSchema.properties.put(BOT_NAME, botSchema);
        return resultSchema;
    }

    private BsonDocument getFunctionValuesDoc(String functionName, String remarks) {
        BsonDocument root = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        root.put(BOT_NAME, bot);
        bot.put(FUNCTION_CAT, new BsonString("def"));
        bot.put(FUNCTION_SCHEM, new BsonNull());
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
            functionPatternRE = Pattern.compile(toJavaPattern(functionNamePattern));
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
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.required.add(BOT_NAME);
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.addRequiredScalarKeys(
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
        resultSchema.properties.put(BOT_NAME, botSchema);
        return resultSchema;
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
        BsonValue n = new BsonNull();
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
            functionNamePatternRE = Pattern.compile(toJavaPattern(functionNamePattern));
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = Pattern.compile(toJavaPattern(columnNamePattern));
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
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
