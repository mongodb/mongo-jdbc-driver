package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class MySQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    public MySQLDatabaseMetaData(MongoConnection conn) {
        super(conn);
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        // These come directly from the mongosqld keywords file, minus the keywords from SQL2003.
        // See resources/keywords.py
        return "ADDDATE,"
                + "AUTO_INCREMENT,"
                + "BINLOG,"
                + "BOOL,"
                + "BTREE,"
                + "CHANGE,"
                + "CHANNEL,"
                + "CHARSET,"
                + "CODE,"
                + "COLUMNS,"
                + "COMMENT,"
                + "DATABASE,"
                + "DATABASES,"
                + "DATETIME,"
                + "DATE_ADD,"
                + "DATE_SUB,"
                + "DAY_HOUR,"
                + "DAY_MICROSECOND,"
                + "DAY_MINUTE,"
                + "DAY_SECOND,"
                + "DBS,"
                + "DISABLE,"
                + "DIV,"
                + "DUAL,"
                + "ENABLE,"
                + "ENGINE,"
                + "ENGINES,"
                + "ENUM,"
                + "ERRORS,"
                + "EVENT,"
                + "EVENTS,"
                + "EXPLAIN,"
                + "EXTENDED,"
                + "FIELDS,"
                + "FLUSH,"
                + "FN,"
                + "FORCE,"
                + "FORMAT,"
                + "FULLTEXT,"
                + "GRANTS,"
                + "GROUP_CONCAT,"
                + "HASH,"
                + "HOSTS,"
                + "HOUR_MICROSECOND,"
                + "HOUR_MINUTE,"
                + "HOUR_SECOND,"
                + "IGNORE,"
                + "INDEX,"
                + "INDEXES,"
                + "JSON,"
                + "KEYS,"
                + "KILL,"
                + "LIMIT,"
                + "LOCK,"
                + "LOGS,"
                + "LONGTEXT,"
                + "LOW_PRIORITY,"
                + "MASTER,"
                + "MEDIUMBLOB,"
                + "MEDIUMTEXT,"
                + "MICROSECOND,"
                + "MINUS,"
                + "MINUTE_MICROSECOND,"
                + "MINUTE_SECOND,"
                + "MODIFY,"
                + "MUTEX,"
                + "OBJECTID,"
                + "OFF,"
                + "OFFSET,"
                + "OJ,"
                + "PARTITIONS,"
                + "PLUGINS,"
                + "PROCESSLIST,"
                + "PROFILE,"
                + "PROFILES,"
                + "PROXY,"
                + "QUARTER,"
                + "QUERY,"
                + "REGEXP,"
                + "RELAYLOG,"
                + "RENAME,"
                + "RLIKE,"
                + "SAMPLE,"
                + "SCHEMAS,"
                + "SECOND_MICROSECOND,"
                + "SEPARATOR,"
                + "SERIAL,"
                + "SHOW,"
                + "SIGNED,"
                + "SLAVE,"
                + "SQL_BIGINT,"
                + "SQL_DATE,"
                + "SQL_DOUBLE,"
                + "SQL_TIMESTAMP,"
                + "SQL_TSI_DAY,"
                + "SQL_TSI_HOUR,"
                + "SQL_TSI_MINUTE,"
                + "SQL_TSI_MONTH,"
                + "SQL_TSI_QUARTER,"
                + "SQL_TSI_SECOND,"
                + "SQL_TSI_WEEK,"
                + "SQL_TSI_YEAR,"
                + "SQL_VARCHAR,"
                + "STATUS,"
                + "STORAGE,"
                + "STRAIGHT_JOIN,"
                + "SUBDATE,"
                + "SUBSTR,"
                + "TABLES,"
                + "TEXT,"
                + "TIMESTAMPADD,"
                + "TIMESTAMPDIFF,"
                + "TINYINT,"
                + "TINYTEXT,"
                + "TRADITIONAL,"
                + "TRIGGERS,"
                + "UNLOCK,"
                + "UNSIGNED,"
                + "USE,"
                + "UTC_DATE,"
                + "UTC_TIMESTAMP,"
                + "VARIABLES,"
                + "WARNINGS,"
                + "WEEK,"
                + "XOR,"
                + "YEAR_MONTH";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return MongoFunction.mySQLNumericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return MongoFunction.mySQLStringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "DATABASE,USER,SYSTEM_USER,SESSION_USER,VERSION";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return MongoFunction.mySQLDateFunctionsString;
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {

        // No procedures so we always return an empty result set.
        BsonValue n = new BsonNull();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_CAT", "PROCEDURE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_SCHEM", "PROCEDURE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_NAME", "PROCEDURE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_TYPE", "PROCEDURE_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", "string"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog,
            String schemaPattern,
            String procedureNamePattern,
            String columnNamePattern)
            throws SQLException {

        // No procedures so we always return an empty result set.
        BsonValue n = new BsonNull();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_CAT", "PROCEDURE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_SCHEM", "PROCEDURE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "PROCEDURE_NAME", "PROCEDURE_NAME", "string"));
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
        metaDoc.columns.add(new Column("", "", "", "COLUMN_DEF", "COLUMN_DEF", "string"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATA_TYPE", "SQL_DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATETIME_SUB", "SQL_DATETIME_SUB", "int"));
        metaDoc.columns.add(
                new Column("", "", "", "CHAR_OCTET_LENGTH", "CHAR_OCTET_LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "ORDINAL_POSITION", "ORDINAL_POSITION", "int"));
        metaDoc.columns.add(new Column("", "", "", "IS_NULLABLE", "IS_NULLABLE", "string"));
        metaDoc.columns.add(new Column("", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", "string"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        MongoResultDoc doc = new MongoResultDoc();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TABLE_TYPE", "TABLE_TYPE", "string"));

        MongoResultDoc valuesDoc = new MongoResultDoc();
        valuesDoc.values = new ArrayList<>();
        valuesDoc.values.add(new BsonString("TABLE"));

        docs.add(metaDoc);
        docs.add(valuesDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String types[])
            throws SQLException {

        Statement stmt = conn.createStatement();
        // What JDBC calls catalog is the SCHEMA column in the TABLES table. It's annoying, but it's
        // what works the best with Tableau, and what the MySQL/MariaDB JDBC drivers do.  So even
        // though we call it SCHEMA in the INFORMATION_SCHEMA, we will use the catalog argument to
        // filter here.  We ignore types because we only have one kind of table type (e.g., no
        // views).
        return stmt.executeQuery(
                "select "
                        + "    TABLE_SCHEMA as TABLE_CAT, "
                        + "    '' as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    TABLE_TYPE, "
                        + "    NULL as REMARKS, "
                        + "    NULL as TYPE_CAT, "
                        + "    NULL as TYPE_SCHEM, "
                        + "    NULL as TYPE_NAME, "
                        + "    NULL as SELF_REFERENCING_COL_NAME, "
                        + "    NULL as REF_GENERATION "
                        + "from INFORMATION_SCHEMA.TABLES "
                        + "where"
                        + patternCond("TABLE_SCHEMA", catalog)
                        + "    and"
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + " order by TABLE_TYPE, TABLE_CAT, TABLE_SCHEMA, TABLE_NAME");
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        Statement stmt = conn.createStatement();
        // This is a hack for now. We need an empty resultset. Better would be to
        // select from DUAL where 1=0, but that does not work, it creates a batch errror.
        return stmt.executeQuery(
                "select "
                        + "    '' as TABLE_SCHEM, "
                        + "    '' as TABLE_CATALOG "
                        + "from INFORMATION_SCHEMA.TABLE_PRIVILEGES ");
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    SCHEMA_NAME as TABLE_CAT "
                        + "from INFORMATION_SCHEMA.SCHEMATA "
                        + " order by TABLE_CAT");
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {

        final String nullableCase =
                ""
                        + "case IS_NULLABLE "
                        + "     when 'YES'     then "
                        + ResultSetMetaData.columnNullable
                        + "     when 'NO'      then "
                        + ResultSetMetaData.columnNoNulls
                        + " end";

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_SCHEMA as TABLE_CAT, "
                        + "    '' as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    COLUMN_NAME, "
                        + getDataTypeNumCase("DATA_TYPE")
                        + " as DATA_TYPE, "
                        + "    DATA_TYPE as TYPE_NAME, "
                        + "    CHARACTER_MAXIMUM_LENGTH as COLUMN_SIZE, "
                        + "    0 as BUFFER_LENGTH, "
                        + getDataTypeScaleCase("DATA_TYPE")
                        + " as DECIMAL_DIGITS, "
                        + getDataTypePrecCase("DATA_TYPE")
                        + "    as NUM_PREC_RADIX, "
                        + nullableCase
                        + " as NULLABLE, "
                        + "    '' as REMARKS, "
                        + "    NULL as COLUMN_DEF, "
                        + "    0 as SQL_DATA_TYPE, "
                        + "    0 as SQL_DATETIME_SUB, "
                        + getDataTypeBytesCase("DATA_TYPE")
                        + "    as CHAR_OCTET_LENGTH, "
                        + "    ORDINAL_POSITION, "
                        + "    IS_NULLABLE, "
                        + "    NULL as SCOPE_CATALOG, "
                        + "    NULL as SCOPE_SCHEMA, "
                        + "    NULL as SCOPE_TABLE, "
                        + "    0 as SOURCE_DATA_TYPE, "
                        + "    0 as IS_AUTOINCREMENT, "
                        + "    0 as IS_GENERATEDCOLUMN "
                        + "from INFORMATION_SCHEMA.COLUMNS "
                        + "where "
                        + patternCond("TABLE_SCHEMA", catalog)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + "    and "
                        + patternCond("COLUMN_NAME", columnNamePattern));
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_SCHEMA as TABLE_CAT, "
                        + "    '' as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    COLUMN_NAME, "
                        + "    GRANTOR, "
                        + "    GRANTEE, "
                        + "    PRIVILEGE_TYPE as PRIVILEGE, "
                        + "    IS_GRANTABLE "
                        + "from INFORMATION_SCHEMA.COLUMN_PRIVILEGES "
                        + "where "
                        + patternCond("TABLE_SCHEMA", catalog)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern)
                        + "    and "
                        + patternCond("COLUMN_NAME", columnNamePattern));
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "    TABLE_SCHEMA as TABLE_CAT, "
                        + "    '' as TABLE_SCHEM, "
                        + "    TABLE_NAME, "
                        + "    GRANTOR, "
                        + "    GRANTEE, "
                        + "    PRIVILEGE_TYPE as PRIVILEGE, "
                        + "    IS_GRANTABLE "
                        + "from INFORMATION_SCHEMA.TABLE_PRIVILEGES "
                        + "where "
                        + patternCond("TABLE_SCHEMA", catalog)
                        + "    and "
                        + patternCond("TABLE_NAME", tableNamePattern));
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "NULL as SCOPE, "
                        + "c.COLUMN_NAME AS COLUMN_NAME, "
                        + getDataTypeNumCase("c.DATA_TYPE")
                        + " as DATA_TYPE, "
                        + "c.DATA_TYPE as TYPE_NAME, "
                        + getDataTypePrecCase("c.DATA_TYPE")
                        + " as COLUMN_SIZE, "
                        + "NULL as BUFFER_LENGTH, "
                        + getDataTypeScaleCase("c.DATA_TYPE")
                        + " as DECIMAL_DIGITS, "
                        + bestRowNotPseudo
                        + " as PSEUDO_COLUMN"
                        + " from INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu inner join INFORMATION_SCHEMA.COLUMNS as c "
                        + "on kcu.TABLE_SCHEMA = c.TABLE_SCHEMA "
                        + "and kcu.TABLE_NAME = c.TABLE_NAME "
                        + "and kcu.COLUMN_NAME = c.COLUMN_NAME "
                        + " inner join INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc "
                        + "on c.TABLE_SCHEMA = tc.TABLE_SCHEMA "
                        + "and c.TABLE_NAME = tc.TABLE_NAME "
                        + "where "
                        + equalsCond("kcu.TABLE_SCHEMA", catalog)
                        + " and "
                        + equalsCond("kcu.TABLE_NAME", table)
                        + "  and tc.CONSTRAINT_TYPE = 'PRIMARY KEY'");
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        // We do not have updates, so this will always be empty.
        BsonValue n = new BsonNull();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "SCOPE", "SCOPE", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_NAME", "COLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_SIZE", "COLUMN_SIZE", "int"));
        metaDoc.columns.add(new Column("", "", "", "BUFFER_LENGTH", "BUFFER_LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "DECIMAL_DIGITS", "DECIMAL_DIGITS", "int"));
        metaDoc.columns.add(new Column("", "", "", "PSEUDO_COLUMN", "PSEUDO_COLUMN", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_CAT", "PKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_SCHEM", "PKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_NAME", "PKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKCOLUMN_NAME", "PKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_CAT", "FKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_SCHEM", "FKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_NAME", "FKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKCOLUMN_NAME", "FKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "KEY_SEQ", "KEY_SEQ", "int"));
        metaDoc.columns.add(new Column("", "", "", "UPDATE_RULE", "UPDATE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DELETE_RULE", "DELETE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "FK_NAME", "FK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PK_NAME", "PK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DEFERRABILITY", "DEFERRABILITY", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        BsonValue n = new BsonNull();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_CAT", "PKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_SCHEM", "PKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_NAME", "PKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKCOLUMN_NAME", "PKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_CAT", "FKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_SCHEM", "FKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_NAME", "FKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKCOLUMN_NAME", "FKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "KEY_SEQ", "KEY_SEQ", "int"));
        metaDoc.columns.add(new Column("", "", "", "UPDATE_RULE", "UPDATE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DELETE_RULE", "DELETE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "FK_NAME", "FK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PK_NAME", "PK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DEFERRABILITY", "DEFERRABILITY", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
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
        // We do not have foreign keys, so this will always be empty.
        BsonValue n = new BsonNull();
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_CAT", "PKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_SCHEM", "PKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKTABLE_NAME", "PKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PKCOLUMN_NAME", "PKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_CAT", "FKTABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_SCHEM", "FKTABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKTABLE_NAME", "FKTABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "FKCOLUMN_NAME", "FKCOLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "KEY_SEQ", "KEY_SEQ", "int"));
        metaDoc.columns.add(new Column("", "", "", "UPDATE_RULE", "UPDATE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DELETE_RULE", "DELETE_RULE", "int"));
        metaDoc.columns.add(new Column("", "", "", "FK_NAME", "FK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "PK_NAME", "PK_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DEFERRABILITY", "DEFERRABILITY", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "kcu.TABLE_SCHEMA as TABLE_CAT, "
                        + "'' as TABLE_SCHEM, "
                        + "kcu.TABLE_NAME as TABLE_NAME, "
                        + "kcu.COLUMN_NAME AS COLUMN_NAME, "
                        + "kcu.ORDINAL_POSITION as KEY_SEQ, "
                        + "kcu.CONSTRAINT_NAME as PK_NAME "
                        + "from INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu inner join INFORMATION_SCHEMA.TABLE_CONSTRAINTS as c "
                        + "on kcu.TABLE_SCHEMA = c.TABLE_SCHEMA "
                        + "and kcu.TABLE_NAME = c.TABLE_NAME "
                        + "where "
                        + equalsCond("kcu.TABLE_SCHEMA", catalog)
                        + "  and "
                        + equalsCond("kcu.TABLE_NAME", table)
                        + "  and c.CONSTRAINT_TYPE = 'PRIMARY KEY'");
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        ArrayList<MongoResultDoc> docs = new ArrayList<>(11);
        docs.add(getTypeInfoMetaDoc());

        docs.add(
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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
                getTypeInfoValuesDoc(
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

        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(
                "select "
                        + "tc.TABLE_SCHEMA as TABLE_CAT, "
                        + "'' as TABLE_SCHEM, "
                        + "tc.TABLE_NAME as TABLE_NAME, "
                        + "tc.CONSTRAINT_TYPE not in  ('PRIMARY KEY', 'UNIQUE') as NON_UNIQUE, "
                        + "tc.CONSTRAINT_CATALOG as INDEX_QUALIFIER, "
                        + "tc.CONSTRAINT_NAME as INDEX_NAME, "
                        + "tc.CONSTRAINT_TYPE as TYPE, "
                        + "kcu.ORDINAL_POSITION as ORDINAL_POSITION, "
                        + "kcu.COLUMN_NAME AS COLUMN_NAME, "
                        + "'A' as ASC_OR_DESC, "
                        + "NULL as CARDINALITY, "
                        + "NULL as PAGES, "
                        + "NULL as FILTER_CONDITION "
                        + "from INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc inner join "
                        + "     INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu "
                        + "  on  tc.CONSTRAINT_SCHEMA = kcu.CONSTRAINT_SCHEMA "
                        + "  and tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME "
                        + "  and tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA "
                        + "  and tc.TABLE_NAME = kcu.TABLE_NAME "
                        + "  and "
                        + equalsCond("tc.TABLE_SCHEMA", catalog)
                        + "  and "
                        + equalsCond("tc.TABLE_NAME", table)
                        + ((unique)
                                ? "where tc.CONSTRAINT_TYPE in ('PRIMARY KEY', 'UNIQUE')"
                                : ""));
    }

    private String patternCond(String colName, String pattern) {
        if (pattern == null) {
            return " 1 "; //just return a true condition.
        }
        return " " + colName + " like '" + escapeString(pattern) + "' ";
    }

    private String equalsCond(String colName, String literal) {
        if (literal == null) {
            return " 1 "; //just return a true condition.
        }
        return " " + colName + " = '" + escapeString(literal) + "' ";
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        // We do not have UDTs.
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TYPE_CAT", "TYPE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_SCHEM", "TYPE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "CLASS_NAME", "CLASS_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(new Column("", "", "", "BASE_TYPE", "BASE_TYPE", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        // We do not have SuperTypes.
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TYPE_CAT", "TYPE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_SCHEM", "TYPE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "SUPERTYPE_CAT", "SUPERTYPE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "SUPERTYPE_SCHEM", "SUPERTYPE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "SUPERTYPE_NAME", "SUPERTYPE_NAME", "string"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        // We do not have SuperTables.
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TABLE_CAT", "TABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "TABLE_SCHEM", "TABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "TABLE_NAME", "TABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "SUPERTABLE_NAME", "SUPERTABLE_NAME", "string"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        // We do not have UDTs.
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TYPE_CAT", "TYPE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_SCHEM", "TYPE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "TYPE_NAME", "TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "ATTR_NAME", "ATTR_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "ATTR_TYPE_NAME", "ATTR_TYPE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "ATTR_SIZE", "ATTR_SIZE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DECIMAL_DIGITS", "DECIMAL_DIGITS", "int"));
        metaDoc.columns.add(new Column("", "", "", "NUM_PREC_RADIX", "NUM_PREC_RADIX", "int"));
        metaDoc.columns.add(new Column("", "", "", "NULLABLE", "NULLABLE", "int"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(new Column("", "", "", "ATTR_DEF", "ATTR_DEF", "string"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATA_TYPE", "SQL_DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "SQL_DATETIME_SUB", "SQL_DATETIME_SUB", "int"));
        metaDoc.columns.add(
                new Column("", "", "", "CHAR_OCTET_LENGTH", "CHAR_OCTET_LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "ORDINAL_POSITION", "ORDINAL_POSITION", "int"));
        metaDoc.columns.add(new Column("", "", "", "IS_NULLABLE", "IS_NULLABLE", "string"));
        metaDoc.columns.add(new Column("", "", "", "SCOPE_CATALOG", "SCOPE_CATALOG", "string"));
        metaDoc.columns.add(new Column("", "", "", "SCOPE_SCHEMA", "SCOPE_SCHEMA", "string"));
        metaDoc.columns.add(new Column("", "", "", "SCOPE_TABLE", "SCOPE_TABLE", "string"));
        metaDoc.columns.add(new Column("", "", "", "SOURCE_DATA_TYPE", "SOURCE_DATA_TYPE", "int"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    //------------------------- JDBC 4.0 -----------------------------------

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return getSchemas();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "NAME", "NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "MAX_LEN", "MAX_LEN", "int"));
        metaDoc.columns.add(new Column("", "", "", "DEFAULT_VALUE", "DEFAULT_VALUE", "string"));
        metaDoc.columns.add(new Column("", "", "", "DESCRIPTION", "DESCRIPTION", "string"));
        docs.add(metaDoc);

        MongoResultDoc doc = new MongoResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("user"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("database user for the connection"));
        docs.add(doc);

        doc = new MongoResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("password"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("user password for the connection"));
        docs.add(doc);

        doc = new MongoResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("conversionMode"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(
                new BsonString(
                        "conversionMode can be strict or relaxed. When strict, "
                                + "failing conversions result in Exceptions. When relaxed, "
                                + "failing conversions result in NULL values."));
        docs.add(doc);

        doc = new MongoResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("database"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("database to connect to"));
        docs.add(doc);

        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {

        ArrayList<MongoResultDoc> docs = new ArrayList<>(MongoFunction.mySQLFunctionNames.length);
        docs.add(getFunctionMetaDoc());

        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = Pattern.compile(toJavaPattern(functionNamePattern));
        }

        for (MongoFunction func : MongoFunction.mySQLFunctions) {
            String functionName = func.name;
            String remarks = func.comment;
            if (functionPatternRE != null && !functionPatternRE.matcher(functionName).matches()) {
                continue;
            }
            MongoResultDoc doc = getFunctionValuesDoc(func.name, func.comment);
            docs.add(doc);
        }

        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        ArrayList<MongoResultDoc> docs = new ArrayList<>(MongoFunction.mySQLFunctionNames.length);
        docs.add(getFunctionColumnMetaDoc());

        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = Pattern.compile(toJavaPattern(functionNamePattern));
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = Pattern.compile(toJavaPattern(columnNamePattern));
        }

        for (MongoFunction func : MongoFunction.mySQLFunctions) {
            String functionName = func.name;
            if (functionNamePatternRE != null
                    && !functionNamePatternRE.matcher(functionName).matches()) {
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
                MongoResultDoc doc =
                        getFunctionColumnValuesDoc(func, i, columnName, argType, false);
                docs.add(doc);
            }
            String columnName = "argReturn";
            if (columnNamePatternRE == null || columnNamePatternRE.matcher(columnName).matches()) {
                MongoResultDoc doc =
                        getFunctionColumnValuesDoc(func, i, "argReturn", func.returnType, true);
                docs.add(doc);
            }
        }

        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        // We do not support pseudoColumns (hidden columns).
        ArrayList<MongoResultDoc> docs = new ArrayList<>();

        MongoResultDoc metaDoc = new MongoResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new Column("", "", "", "TABLE_CAT", "TABLE_CAT", "string"));
        metaDoc.columns.add(new Column("", "", "", "TABLE_SCHEM", "TABLE_SCHEM", "string"));
        metaDoc.columns.add(new Column("", "", "", "TABLE_NAME", "TABLE_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_NAME", "COLUMN_NAME", "string"));
        metaDoc.columns.add(new Column("", "", "", "DATA_TYPE", "DATA_TYPE", "int"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_SIZE", "COLUMN_SIZE", "int"));
        metaDoc.columns.add(new Column("", "", "", "DECIMAL_DIGITS", "DECIMAL_DIGITS", "int"));
        metaDoc.columns.add(new Column("", "", "", "NUM_PREC_RADIX", "NUM_PREC_RADIX", "string"));
        metaDoc.columns.add(new Column("", "", "", "COLUMN_USAGE", "COLUMN_USAGE", "string"));
        metaDoc.columns.add(new Column("", "", "", "REMARKS", "REMARKS", "string"));
        metaDoc.columns.add(
                new Column("", "", "", "CHAR_OCTET_LENGTH", "CHAR_OCTET_LENGTH", "int"));
        metaDoc.columns.add(new Column("", "", "", "IS_NULLABLE", "IS_NULLABLE", "string"));

        docs.add(metaDoc);
        return new MySQLResultSet(null, new MongoExplicitCursor(docs), true);
    }
}
