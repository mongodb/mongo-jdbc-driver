package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static final String BOT_NAME = "";

    public MongoSQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
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
        throw new SQLFeatureNotSupportedException("TODO");
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

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        throw new SQLFeatureNotSupportedException("TODO");
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
        throw new SQLFeatureNotSupportedException("TODO");
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
