package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.ArrayList;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static final String BOT_NAME = "";

    public MongoSQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        // These come from keywords from the mongosql parser, minus the keywords from SQL 2003.
        return "AGGREGATE,"
                + "BINDATA,"
                + "BIT,"
                + "BOOL,"
                + "BSON_DATE,"
                + "BSON_TIMESTAMP,"
                + "CHAR VARYING,"
                + "DBPOINTER,"
                + "DOCUMENT,"
                + "ERROR,"
                + "FETCH FIRST,"
                + "FETCH NEXT,"
                + "JAVASCRIPT,"
                + "JAVASCRIPTWITHSCOPE,"
                + "LIMIT,"
                + "LONG,"
                + "MAXKEY,"
                + "MINKEY,"
                + "MISSING,"
                + "NOT IN,"
                + "NOT LIKE,"
                + "OBJECTID,"
                + "OFFSET,"
                + "REGEX,"
                + "ROWS ONLY,"
                + "STRING,"
                + "SYMBOL,"
                + "UNDEFINED";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getStringFunctions() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(PROCEDURE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BSON_STRING_TYPE_NAME));

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
                new Pair<>(PROCEDURE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PRECISION, BSON_INT_TYPE_NAME),
                new Pair<>(LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(SCALE, BSON_INT_TYPE_NAME),
                new Pair<>(RADIX, BSON_INT_TYPE_NAME),
                new Pair<>(NULLABLE, BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_DEF, BSON_STRING_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BSON_INT_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(ORDINAL_POSITION, BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BSON_STRING_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addRequiredScalarKeys(new Pair<>("TABLE_TYPE", BSON_STRING_TYPE_NAME));

        BsonDocument tableDoc = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        tableDoc.put("", bot);
        bot.put("TABLE_TYPE", new BsonString("TABLE"));
        docs.add(tableDoc);
        BsonDocument viewDoc = new BsonDocument();
        bot = new BsonDocument();
        viewDoc.put("", bot);
        bot.put("TABLE_TYPE", new BsonString("VIEW"));
        docs.add(viewDoc);

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);

        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), botSchema);
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String types[])
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_CATALOG, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(SCOPE, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BSON_INT_TYPE_NAME),
                new Pair<>(BUFFER_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BSON_INT_TYPE_NAME),
                new Pair<>(PSEUDO_COLUMN, BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BSON_INT_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    private MongoJsonSchema getTypeInfoJsonSchema() {

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addRequiredScalarKeys(
                new Pair<>("TYPE_NAME", BSON_STRING_TYPE_NAME),
                new Pair<>("DATA_TYPE", BSON_INT_TYPE_NAME),
                new Pair<>("PRECISION", BSON_INT_TYPE_NAME),
                new Pair<>("LITERAL_PREFIX", BSON_STRING_TYPE_NAME),
                new Pair<>("LITERAL_SUFFIX", BSON_STRING_TYPE_NAME),
                new Pair<>("CREATE_PARAMS", BSON_STRING_TYPE_NAME),
                new Pair<>("NULLABLE", BSON_INT_TYPE_NAME),
                new Pair<>("CASE_SENSITIVE", BSON_BOOL_TYPE_NAME),
                new Pair<>("SEARCHABLE", BSON_INT_TYPE_NAME),
                new Pair<>("UNSIGNED_ATTRIBUTE", BSON_BOOL_TYPE_NAME),
                new Pair<>("FIX_PREC_SCALE", BSON_BOOL_TYPE_NAME),
                new Pair<>("AUTO_INCREMENT", BSON_BOOL_TYPE_NAME),
                new Pair<>("LOCAL_TYPE_NAME", BSON_STRING_TYPE_NAME),
                new Pair<>("MINIMUM_SCALE", BSON_INT_TYPE_NAME),
                new Pair<>("MAXIMUM_SCALE", BSON_INT_TYPE_NAME),
                new Pair<>("SQL_DATA_TYPE", BSON_INT_TYPE_NAME),
                new Pair<>("SQL_DATETIME_SUB", BSON_INT_TYPE_NAME),
                new Pair<>("NUM_PREC_RADIX", BSON_INT_TYPE_NAME));
        return schema;
    }

    private BsonDocument getTypeInfoValuesBson(
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

        BsonDocument doc = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        doc.put("", bot);
        bot.put("TYPE_NAME", new BsonString(typeName));
        bot.put("DATA_TYPE", new BsonInt32(dataType));
        bot.put("PRECISION", new BsonInt32(precision));
        bot.put("LITERAL_PREFIX", literalPrefix != null ? new BsonString(literalPrefix) : n);
        bot.put("LITERAL_SUFFIX", literalSuffix != null ? new BsonString(literalSuffix) : n);
        bot.put("CREATE_PARAMS", n);
        bot.put("NULLABLE", new BsonInt32(nullable));
        bot.put("CASE_SENSITIVE", new BsonBoolean(caseSensitive));
        bot.put("SEARCHABLE", new BsonInt32(searchable));
        bot.put("UNSIGNED_ATTRIBUTE", new BsonBoolean(unsigned));
        bot.put("FIXED_PREC_SCALE", new BsonBoolean(fixedPrecScale));
        bot.put("AUTO_INCREMENT", new BsonBoolean(false));
        bot.put("LOCAL_TYPE_NAME", n);
        bot.put("MINIMUM_SCALE", new BsonInt32(minScale));
        bot.put("MAXIMUM_SCALE", new BsonInt32(maxScale));
        bot.put("SQL_DATA_TYPE", new BsonInt32(0));
        bot.put("SQL_DATETIME_SUB", new BsonInt32(0));
        bot.put("NUM_PREC_RADIX", new BsonInt32(numPrecRadix));

        return doc;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();
        MongoJsonSchema schema = getTypeInfoJsonSchema();

        docs.add(
                getTypeInfoValuesBson(
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
                getTypeInfoValuesBson(
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
                getTypeInfoValuesBson(
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
                getTypeInfoValuesBson(
                        "decimal", //typeName
                        Types.DECIMAL, //dataType
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
                getTypeInfoValuesBson(
                        "double", //typeName
                        Types.DOUBLE, //dataType
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
                getTypeInfoValuesBson(
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
                getTypeInfoValuesBson(
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
                getTypeInfoValuesBson(
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

        docs.add(
                getTypeInfoValuesBson(
                        "array", //typeName
                        Types.NULL, //dataType
                        unknownLength, //precision
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
                getTypeInfoValuesBson(
                        "object", //typeName
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
                getTypeInfoValuesBson(
                        "objectId", //typeName
                        Types.NULL, //dataType
                        24, //precision
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
                getTypeInfoValuesBson(
                        "dbPointer", //typeName
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
                getTypeInfoValuesBson(
                        "javascript", //typeName
                        Types.NULL, //dataType
                        unknownLength, //precision
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
                getTypeInfoValuesBson(
                        "javascriptWithScope", //typeName
                        Types.NULL, //dataType
                        unknownLength, //precision
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
                getTypeInfoValuesBson(
                        "maxKey", //typeName
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
                getTypeInfoValuesBson(
                        "minKey", //typeName
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
                getTypeInfoValuesBson(
                        "regex", //typeName
                        Types.NULL, //dataType
                        unknownLength, //precision
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
                getTypeInfoValuesBson(
                        "symbol", //typeName
                        Types.NULL, //dataType
                        unknownLength, //precision
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
                getTypeInfoValuesBson(
                        "timestamp", //typeName
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
                getTypeInfoValuesBson(
                        "undefined", //typeName
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
                getTypeInfoValuesBson(
                        "bson", //typeName
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

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), botSchema);
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TYPE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(CLASS_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(BASE_TYPE, BSON_INT_TYPE_NAME));

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
                new Pair<>(TYPE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_NAME, BSON_STRING_TYPE_NAME));

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
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTABLE_NAME, BSON_STRING_TYPE_NAME));

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
                new Pair<>(TYPE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(ATTR_TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_SIZE, BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BSON_INT_TYPE_NAME),
                new Pair<>(NULLABLE, BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_DEF, BSON_STRING_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BSON_INT_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(ORDINAL_POSITION, BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_CATALOG, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_SCHEMA, BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_TABLE, BSON_STRING_TYPE_NAME),
                new Pair<>(SOURCE_DATA_TYPE, BSON_INT_TYPE_NAME));

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
                new Pair<>("NAME", BSON_STRING_TYPE_NAME),
                new Pair<>("MAX_LEN", BSON_STRING_TYPE_NAME),
                new Pair<>("DEFAULT_VALUE", BSON_STRING_TYPE_NAME),
                new Pair<>("DESCRIPTION", BSON_STRING_TYPE_NAME));

        BsonDocument doc = new BsonDocument();
        BsonDocument bot = new BsonDocument();
        doc.put("", bot);

        bot.put("NAME", new BsonString("user"));
        bot.put("MAX_LEN", new BsonInt32(0));
        bot.put("DEFAULT_VALUE", new BsonString(""));
        bot.put("DESCRIPTION", new BsonString("database user for the connection"));
        docs.add(doc);

        doc = new BsonDocument();
        bot = new BsonDocument();
        doc.put("", bot);
        bot.put("NAME", new BsonString("password"));
        bot.put("MAX_LEN", new BsonInt32(0));
        bot.put("DEFAULT_VALUE", new BsonString(""));
        bot.put("DESCRIPTION", new BsonString("user password for the connection"));
        docs.add(doc);

        doc = new BsonDocument();
        bot = new BsonDocument();
        doc.put("", bot);
        bot.put("NAME", new BsonString("database"));
        bot.put("MAX_LEN", new BsonInt32(0));
        bot.put("DEFAULT_VALUE", new BsonString(""));
        bot.put("DESCRIPTION", new BsonString("database to connect to"));
        docs.add(doc);

        doc = new BsonDocument();
        bot = new BsonDocument();
        doc.put("", bot);
        bot.put("NAME", new BsonString("dialect"));
        bot.put("MAX_LEN", new BsonInt32(0));
        bot.put("DEFAULT_VALUE", new BsonString("mysql"));
        bot.put(
                "DESCRIPTION",
                new BsonString("dialect to use, possible values are mysql or mongosql"));
        docs.add(doc);

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(null, new BsonExplicitCursor(docs), botSchema);
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TABLE_CAT, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_USAGE, BSON_STRING_TYPE_NAME),
                new Pair<>(REMARKS, BSON_STRING_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
