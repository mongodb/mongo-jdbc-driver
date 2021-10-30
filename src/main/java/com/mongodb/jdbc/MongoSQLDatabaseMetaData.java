package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static final String BOT_NAME = "";

    public MongoSQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
    }

    private BsonDocument createBottomBson(BsonElement... elements) {
        BsonDocument bot = new BsonDocument(Arrays.asList(elements));
        return new BsonDocument(BOT_NAME, bot);
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
        schema.addRequiredScalarKeys(new Pair<>(TABLE_TYPE, BSON_STRING_TYPE_NAME));

        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("TABLE"))));
        docs.add(createBottomBson(new BsonElement(TABLE_TYPE, new BsonString("VIEW"))));

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
                new Pair<>(TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(PRECISION, BSON_INT_TYPE_NAME),
                new Pair<>(LITERAL_PREFIX, BSON_STRING_TYPE_NAME),
                new Pair<>(LITERAL_SUFFIX, BSON_STRING_TYPE_NAME),
                new Pair<>(CREATE_PARAMS, BSON_STRING_TYPE_NAME),
                new Pair<>(NULLABLE, BSON_INT_TYPE_NAME),
                new Pair<>(CASE_SENSITIVE, BSON_BOOL_TYPE_NAME),
                new Pair<>(SEARCHABLE, BSON_INT_TYPE_NAME),
                new Pair<>(UNSIGNED_ATTRIBUTE, BSON_BOOL_TYPE_NAME),
                new Pair<>(FIX_PREC_SCALE, BSON_BOOL_TYPE_NAME),
                new Pair<>(AUTO_INCREMENT, BSON_BOOL_TYPE_NAME),
                new Pair<>(LOCAL_TYPE_NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(MINIMUM_SCALE, BSON_INT_TYPE_NAME),
                new Pair<>(MAXIMUM_SCALE, BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BSON_INT_TYPE_NAME));
        return schema;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        BsonValue n = new BsonNull();
        ArrayList<BsonDocument> docs = new ArrayList<>();
        MongoJsonSchema schema = getTypeInfoJsonSchema();

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BINDATA_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BINARY)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typePredNone)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_BINDATA_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BOOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIT)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_BOOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_BOOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_BOOL_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_BOOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_BOOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_BOOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DATE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.TIMESTAMP)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_DATE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_DATE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_DATE_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_DATE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_DATE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_DATE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DECIMAL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DECIMAL)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_DECIMAL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DOUBLE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DOUBLE)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_DOUBLE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_INT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.INTEGER)),
                        new BsonElement(PRECISION, new BsonInt32(getPrecision(BSON_INT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_INT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_INT_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_INT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_INT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_INT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_LONG_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIGINT)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_LONG_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_LONG_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_LONG_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_LONG_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_LONG_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_LONG_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_STRING_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.LONGVARCHAR)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_STRING_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_STRING_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_STRING_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_STRING_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_STRING_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_STRING_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_ARRAY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_ARRAY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_OBJECT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_OBJECTID_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_OBJECTID_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_DBPOINTER_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(getMinScale(BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(getMaxScale(BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_DBPOINTER_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_JAVASCRIPT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(getMinScale(BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(getMaxScale(BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_JAVASCRIPT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                new BsonInt32(getPrecision(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        getCaseSensitivity(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(
                                        getFixedPrecScale(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(getMinScale(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(getMaxScale(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        getNumPrecRadix(BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MAXKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_MAXKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_MINKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_MINKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_REGEX_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_REGEX_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_REGEX_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_REGEX_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_REGEX_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_REGEX_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_REGEX_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_SYMBOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_SYMBOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_TIMESTAMP_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(getMinScale(BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(getMaxScale(BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_TIMESTAMP_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_UNDEFINED_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(getMinScale(BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(getMaxScale(BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_UNDEFINED_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BSON_BSON_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION, new BsonInt32(getPrecision(BSON_BSON_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(getCaseSensitivity(BSON_BSON_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(
                                FIXED_PREC_SCALE,
                                new BsonBoolean(getFixedPrecScale(BSON_BSON_TYPE_NAME))),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE, new BsonInt32(getMinScale(BSON_BSON_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE, new BsonInt32(getMaxScale(BSON_BSON_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(getNumPrecRadix(BSON_BSON_TYPE_NAME)))));

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
                new Pair<>(NAME, BSON_STRING_TYPE_NAME),
                new Pair<>(MAX_LEN, BSON_STRING_TYPE_NAME),
                new Pair<>(DEFAULT_VALUE, BSON_STRING_TYPE_NAME),
                new Pair<>(DESCRIPTION, BSON_STRING_TYPE_NAME));

        docs.add(
                createBottomBson(
                        new BsonElement(NAME, new BsonString("user")),
                        new BsonElement(MAX_LEN, new BsonInt32(0)),
                        new BsonElement(DEFAULT_VALUE, new BsonString("")),
                        new BsonElement(
                                DESCRIPTION, new BsonString("database user for the connection"))));

        docs.add(
                createBottomBson(
                        new BsonElement(NAME, new BsonString("password")),
                        new BsonElement(MAX_LEN, new BsonInt32(0)),
                        new BsonElement(DEFAULT_VALUE, new BsonString("")),
                        new BsonElement(
                                DESCRIPTION, new BsonString("user password for the connection"))));

        docs.add(
                createBottomBson(
                        new BsonElement(NAME, new BsonString("database")),
                        new BsonElement(MAX_LEN, new BsonInt32(0)),
                        new BsonElement(DEFAULT_VALUE, new BsonString("")),
                        new BsonElement(DESCRIPTION, new BsonString("database to connect to"))));

        docs.add(
                createBottomBson(
                        new BsonElement(NAME, new BsonString("dialect")),
                        new BsonElement(MAX_LEN, new BsonInt32(0)),
                        new BsonElement(DEFAULT_VALUE, new BsonString("mysql")),
                        new BsonElement(
                                DESCRIPTION,
                                new BsonString(
                                        "dialect to use, possible values are mysql or mongosql"))));

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
