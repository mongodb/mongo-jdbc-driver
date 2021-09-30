package com.mongodb.jdbc;

import org.bson.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
        return MongoFunction.numericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return MongoFunction.stringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "DATABASE,USER,SYSTEM_USER,SESSION_USER,VERSION";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return MongoFunction.dateFunctionsString;
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

        ArrayList<MongoResultDoc> docs = new ArrayList<>(MongoFunction.functionNames.length);
        docs.add(getFunctionMetaDoc());

        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = Pattern.compile(functionNamePattern.replaceAll("%", ".*"));
        }

        for (MongoFunction func : MongoFunction.functions) {
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
        ArrayList<MongoResultDoc> docs = new ArrayList<>(MongoFunction.functionNames.length);
        docs.add(getFunctionColumnMetaDoc());

        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = Pattern.compile(functionNamePattern.replaceAll("%", ".*"));
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = Pattern.compile(columnNamePattern.replaceAll("%", ".*"));
        }

        for (MongoFunction func : MongoFunction.functions) {
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
}
