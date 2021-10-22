package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class MongoSQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

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
                new Pair<>(PROCEDURE_CAT, "string"),
                new Pair<>(PROCEDURE_SCHEM, "string"),
                new Pair<>(PROCEDURE_NAME, "string"),
                new Pair<>(REMARKS, "string"),
                new Pair<>(PROCEDURE_TYPE, "int"),
                new Pair<>(SPECIFIC_NAME, "string")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
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
                new Pair<>(PROCEDURE_CAT, "string"),
                new Pair<>(PROCEDURE_SCHEM, "string"),
                new Pair<>(PROCEDURE_NAME, "string"),
                new Pair<>(COLUMN_NAME, "string"),
                new Pair<>(COLUMN_TYPE, "int"),
                new Pair<>(DATA_TYPE, "int"),
                new Pair<>(TYPE_NAME, "string"),
                new Pair<>(PRECISION, "int"),
                new Pair<>(LENGTH, "int"),
                new Pair<>(SCALE, "int"),
                new Pair<>(RADIX, "int"),
                new Pair<>(NULLABLE, "int"),
                new Pair<>(REMARKS, "string"),
                new Pair<>(COLUMN_DEF, "string"),
                new Pair<>(SQL_DATA_TYPE, "int"),
                new Pair<>(SQL_DATETIME_SUB, "int"),
                new Pair<>(CHAR_OCTET_LENGTH, "int"),
                new Pair<>(ORDINAL_POSITION, "int"),
                new Pair<>(IS_NULLABLE, "string"),
                new Pair<>(SPECIFIC_NAME, "string")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
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
                new Pair<>(TABLE_SCHEM, "string"),
                new Pair<>(TABLE_CATALOG, "string")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
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
                new Pair<>(SCOPE, "string"),
                new Pair<>(COLUMN_NAME, "string"),
                new Pair<>(DATA_TYPE, "int"),
                new Pair<>(TYPE_NAME, "string"),
                new Pair<>(COLUMN_SIZE, "int"),
                new Pair<>(BUFFER_LENGTH, "int"),
                new Pair<>(DECIMAL_DIGITS, "int"),
                new Pair<>(PSEUDO_COLUMN, "int")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(PKTABLE_CAT, "string"),
                new Pair<>(PKTABLE_SCHEM, "string"),
                new Pair<>(PKTABLE_NAME, "string"),
                new Pair<>(PKCOLUMN_NAME, "string"),
                new Pair<>(FKTABLE_CAT, "string"),
                new Pair<>(FKTABLE_SCHEM, "string"),
                new Pair<>(FKTABLE_NAME, "string"),
                new Pair<>(FKCOLUMN_NAME, "string"),
                new Pair<>(KEY_SEQ, "int"),
                new Pair<>(UPDATE_RULE, "int"),
                new Pair<>(DELETE_RULE, "int"),
                new Pair<>(FK_NAME, "string"),
                new Pair<>(PK_NAME, "string"),
                new Pair<>(DEFERRABILITY, "int")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(PKTABLE_CAT, "string"),
                new Pair<>(PKTABLE_SCHEM, "string"),
                new Pair<>(PKTABLE_NAME, "string"),
                new Pair<>(PKCOLUMN_NAME, "string"),
                new Pair<>(FKTABLE_CAT, "string"),
                new Pair<>(FKTABLE_SCHEM, "string"),
                new Pair<>(FKTABLE_NAME, "string"),
                new Pair<>(FKCOLUMN_NAME, "string"),
                new Pair<>(KEY_SEQ, "int"),
                new Pair<>(UPDATE_RULE, "int"),
                new Pair<>(DELETE_RULE, "int"),
                new Pair<>(FK_NAME, "string"),
                new Pair<>(PK_NAME, "string"),
                new Pair<>(DEFERRABILITY, "int")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
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
                new Pair<>(PKTABLE_CAT, "string"),
                new Pair<>(PKTABLE_SCHEM, "string"),
                new Pair<>(PKTABLE_NAME, "string"),
                new Pair<>(PKCOLUMN_NAME, "string"),
                new Pair<>(FKTABLE_CAT, "string"),
                new Pair<>(FKTABLE_SCHEM, "string"),
                new Pair<>(FKTABLE_NAME, "string"),
                new Pair<>(FKCOLUMN_NAME, "string"),
                new Pair<>(KEY_SEQ, "int"),
                new Pair<>(UPDATE_RULE, "int"),
                new Pair<>(DELETE_RULE, "int"),
                new Pair<>(FK_NAME, "string"),
                new Pair<>(PK_NAME, "string"),
                new Pair<>(DEFERRABILITY, "int")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
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
                new Pair<>(TYPE_CAT, "string"),
                new Pair<>(TYPE_SCHEM, "string"),
                new Pair<>(TYPE_NAME, "string"),
                new Pair<>(CLASS_NAME, "string"),
                new Pair<>(DATA_TYPE, "int"),
                new Pair<>(REMARKS, "string"),
                new Pair<>(BASE_TYPE, "int")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        MongoJsonSchema resultSchema = MongoJsonSchema.createEmptyObjectSchema();
        resultSchema.addRequiredScalarKeys(
                new Pair<>(TYPE_CAT, "string"),
                new Pair<>(TYPE_SCHEM, "string"),
                new Pair<>(TYPE_NAME, "string"),
                new Pair<>(SUPERTYPE_CAT, "string"),
                new Pair<>(SUPERTYPE_SCHEM, "string"),
                new Pair<>(SUPERTYPE_NAME, "string")
        );

        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
//        TODO: SQL-535 use commented return statement instead
//        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR, schema);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
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
        return new MongoSQLResultSet(null, BsonExplicitCursor.EMPTY_CURSOR);
    }
}
