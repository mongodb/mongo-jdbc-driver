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
                new Pair<>(PROCEDURE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME));

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
                new Pair<>(PROCEDURE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PROCEDURE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PRECISION, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(LENGTH, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SCALE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(RADIX, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(NULLABLE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_DEF, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(ORDINAL_POSITION, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SPECIFIC_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        ArrayList<BsonDocument> docs = new ArrayList<>();

        MongoJsonSchema schema = MongoJsonSchema.createEmptyObjectSchema();
        schema.addRequiredScalarKeys(new Pair<>(TABLE_TYPE, BsonTypeInfo.BSON_STRING_TYPE_NAME));

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
                new Pair<>(TABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_CATALOG, BsonTypeInfo.BSON_STRING_TYPE_NAME));

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
                new Pair<>(SCOPE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(BUFFER_LENGTH, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(PSEUDO_COLUMN, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(PKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(FKCOLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(KEY_SEQ, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(UPDATE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DELETE_RULE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(FK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(PK_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DEFERRABILITY, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(PRECISION, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(LITERAL_PREFIX, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(LITERAL_SUFFIX, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(CREATE_PARAMS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(NULLABLE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(CASE_SENSITIVE, BsonTypeInfo.BSON_BOOL_TYPE_NAME),
                new Pair<>(SEARCHABLE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(UNSIGNED_ATTRIBUTE, BsonTypeInfo.BSON_BOOL_TYPE_NAME),
                new Pair<>(FIX_PREC_SCALE, BsonTypeInfo.BSON_BOOL_TYPE_NAME),
                new Pair<>(AUTO_INCREMENT, BsonTypeInfo.BSON_BOOL_TYPE_NAME),
                new Pair<>(LOCAL_TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(MINIMUM_SCALE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(MAXIMUM_SCALE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.BSON_INT_TYPE_NAME));
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
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_BINDATA_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BINARY)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typePredNone)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_BINDATA_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_BINDATA_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_BOOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIT)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_BOOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_BOOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_BOOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_BOOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_BOOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_DATE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.TIMESTAMP)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_DATE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_DATE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_DATE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_DATE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_DATE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_DECIMAL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DECIMAL)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_DECIMAL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_DECIMAL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_DOUBLE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.DOUBLE)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_DOUBLE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_DOUBLE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(TYPE_NAME, new BsonString(BsonTypeInfo.BSON_INT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.INTEGER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_INT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_INT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(BsonTypeInfo.BSON_INT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(BsonTypeInfo.BSON_INT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_INT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_LONG_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.BIGINT)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_LONG_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_LONG_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_LONG_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_LONG_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_LONG_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_STRING_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.LONGVARCHAR)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_STRING_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, new BsonString("'")),
                        new BsonElement(LITERAL_SUFFIX, new BsonString("'")),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_STRING_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_STRING_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_STRING_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_STRING_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_ARRAY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_ARRAY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_ARRAY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_OBJECT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_OBJECT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_OBJECT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_OBJECTID_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_OBJECTID_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_OBJECTID_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_DBPOINTER_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_JAVASCRIPT_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME,
                                new BsonString(BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo
                                                        .BSON_JAVASCRIPTWITHSCOPE_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_MAXKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_MAXKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_MAXKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_MINKEY_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_MINKEY_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_MINKEY_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_REGEX_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_REGEX_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_REGEX_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_REGEX_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_REGEX_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_REGEX_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_SYMBOL_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_SYMBOL_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_SYMBOL_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_TIMESTAMP_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_UNDEFINED_TYPE_NAME)))));

        docs.add(
                createBottomBson(
                        new BsonElement(
                                TYPE_NAME, new BsonString(BsonTypeInfo.BSON_BSON_TYPE_NAME)),
                        new BsonElement(DATA_TYPE, new BsonInt32(Types.OTHER)),
                        new BsonElement(
                                PRECISION,
                                asBsonIntOrNull(
                                        BsonTypeInfo.getPrecision(
                                                BsonTypeInfo.BSON_BSON_TYPE_NAME))),
                        new BsonElement(LITERAL_PREFIX, n),
                        new BsonElement(LITERAL_SUFFIX, n),
                        new BsonElement(CREATE_PARAMS, n),
                        new BsonElement(NULLABLE, new BsonInt32(ResultSetMetaData.columnNullable)),
                        new BsonElement(
                                CASE_SENSITIVE,
                                new BsonBoolean(
                                        BsonTypeInfo.getCaseSensitivity(
                                                BsonTypeInfo.BSON_BSON_TYPE_NAME))),
                        new BsonElement(SEARCHABLE, new BsonInt32(typeSearchable)),
                        new BsonElement(UNSIGNED_ATTRIBUTE, new BsonBoolean(false)),
                        new BsonElement(FIXED_PREC_SCALE, new BsonBoolean(false)),
                        new BsonElement(AUTO_INCREMENT, new BsonBoolean(false)),
                        new BsonElement(LOCAL_TYPE_NAME, n),
                        new BsonElement(
                                MINIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMinScale(
                                                BsonTypeInfo.BSON_BSON_TYPE_NAME))),
                        new BsonElement(
                                MAXIMUM_SCALE,
                                new BsonInt32(
                                        BsonTypeInfo.getMaxScale(
                                                BsonTypeInfo.BSON_BSON_TYPE_NAME))),
                        new BsonElement(SQL_DATA_TYPE, new BsonInt32(0)),
                        new BsonElement(SQL_DATETIME_SUB, new BsonInt32(0)),
                        new BsonElement(
                                NUM_PREC_RADIX,
                                new BsonInt32(
                                        BsonTypeInfo.getNumPrecRadix(
                                                BsonTypeInfo.BSON_BSON_TYPE_NAME)))));

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
                new Pair<>(TYPE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(CLASS_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(BASE_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(TYPE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME));

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
                new Pair<>(TABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SUPERTABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME));

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
                new Pair<>(TYPE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(ATTR_TYPE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_SIZE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(NULLABLE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(ATTR_DEF, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SQL_DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(SQL_DATETIME_SUB, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(ORDINAL_POSITION, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_CATALOG, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_SCHEMA, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SCOPE_TABLE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(SOURCE_DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME));

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
                new Pair<>(NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(MAX_LEN, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DEFAULT_VALUE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DESCRIPTION, BsonTypeInfo.BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, schema);
        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
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
                new Pair<>(TABLE_CAT, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_SCHEM, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(TABLE_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_NAME, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(DATA_TYPE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(COLUMN_SIZE, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(DECIMAL_DIGITS, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(NUM_PREC_RADIX, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(COLUMN_USAGE, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(REMARKS, BsonTypeInfo.BSON_STRING_TYPE_NAME),
                new Pair<>(CHAR_OCTET_LENGTH, BsonTypeInfo.BSON_INT_TYPE_NAME),
                new Pair<>(IS_NULLABLE, BsonTypeInfo.BSON_STRING_TYPE_NAME));

        // All fields in this result set are nested under the bottom namespace.
        MongoJsonSchema botSchema = MongoJsonSchema.createEmptyObjectSchema();
        botSchema.properties.put(BOT_NAME, resultSchema);

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, botSchema);
    }
}
