/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import static com.mongodb.jdbc.BsonTypeInfo.*;

import com.mongodb.jdbc.logging.AutoLoggable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.BsonBoolean;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;

@AutoLoggable
public class MySQLDatabaseMetaData extends MongoDatabaseMetaData implements DatabaseMetaData {

    private static com.mongodb.jdbc.MySQLFunctions MySQLFunctions =
            com.mongodb.jdbc.MySQLFunctions.getInstance();

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
        return MySQLFunctions.numericFunctionsString;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return MySQLFunctions.stringFunctionsString;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return MySQLFunctions.systemFunctionsString;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return MySQLFunctions.dateFunctionsString;
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {

        // No procedures so we always return an empty result set.
        BsonValue n = new BsonNull();
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_CAT, PROCEDURE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_SCHEM, PROCEDURE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_NAME, PROCEDURE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", REMARKS, REMARKS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_TYPE, PROCEDURE_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SPECIFIC_NAME, SPECIFIC_NAME, BSON_STRING.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
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
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_CAT, PROCEDURE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_SCHEM, PROCEDURE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PROCEDURE_NAME, PROCEDURE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", COLUMN_NAME, COLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", COLUMN_TYPE, COLUMN_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", PRECISION, PRECISION, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", LENGTH, LENGTH, BSON_INT.getBsonName()));
        metaDoc.columns.add(new MySQLColumnInfo("", "", "", SCALE, SCALE, BSON_INT.getBsonName()));
        metaDoc.columns.add(new MySQLColumnInfo("", "", "", RADIX, RADIX, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", NULLABLE, NULLABLE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", REMARKS, REMARKS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", COLUMN_DEF, COLUMN_DEF, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATA_TYPE, SQL_DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATETIME_SUB, SQL_DATETIME_SUB, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", CHAR_OCTET_LENGTH, CHAR_OCTET_LENGTH, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", ORDINAL_POSITION, ORDINAL_POSITION, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", IS_NULLABLE, IS_NULLABLE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SPECIFIC_NAME, SPECIFIC_NAME, BSON_STRING.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        MySQLResultDoc doc = new MySQLResultDoc();
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TABLE_TYPE, TABLE_TYPE, BSON_STRING.getBsonName()));

        MySQLResultDoc valuesDoc = new MySQLResultDoc();
        valuesDoc.values = new ArrayList<>();
        valuesDoc.values.add(new BsonString("TABLE"));

        docs.add(metaDoc);
        docs.add(valuesDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
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
        // select from DUAL where 1=0, but that does not work, it creates a batch error.
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
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", SCOPE, SCOPE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", COLUMN_NAME, COLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", COLUMN_SIZE, COLUMN_SIZE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", BUFFER_LENGTH, BUFFER_LENGTH, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DECIMAL_DIGITS, DECIMAL_DIGITS, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PSEUDO_COLUMN, PSEUDO_COLUMN, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_CAT, PKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_SCHEM, PKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_NAME, PKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKCOLUMN_NAME, PKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_CAT, FKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_SCHEM, FKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_NAME, FKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKCOLUMN_NAME, FKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", KEY_SEQ, KEY_SEQ, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", UPDATE_RULE, UPDATE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DELETE_RULE, DELETE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", FK_NAME, FK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", PK_NAME, PK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DEFERRABILITY, DEFERRABILITY, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        // We do not have foreign keys, so this will always be empty.
        BsonValue n = new BsonNull();
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_CAT, PKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_SCHEM, PKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_NAME, PKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKCOLUMN_NAME, PKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_CAT, FKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_SCHEM, FKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_NAME, FKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKCOLUMN_NAME, FKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", KEY_SEQ, KEY_SEQ, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", UPDATE_RULE, UPDATE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DELETE_RULE, DELETE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", FK_NAME, FK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", PK_NAME, PK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DEFERRABILITY, DEFERRABILITY, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
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
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_CAT, PKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_SCHEM, PKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKTABLE_NAME, PKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", PKCOLUMN_NAME, PKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_CAT, FKTABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_SCHEM, FKTABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKTABLE_NAME, FKTABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FKCOLUMN_NAME, FKCOLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", KEY_SEQ, KEY_SEQ, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", UPDATE_RULE, UPDATE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DELETE_RULE, DELETE_RULE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", FK_NAME, FK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", PK_NAME, PK_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DEFERRABILITY, DEFERRABILITY, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
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
        ArrayList<MySQLResultDoc> docs = new ArrayList<>(11);
        docs.add(getTypeInfoMetaDoc());

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_BINDATA.getBsonName(), //typeName
                        Types.NULL, //dataType
                        BSON_BINDATA.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_BINDATA.getCaseSensitivity(), //caseSensitive
                        typePredNone, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_BINDATA.getMinScale(), //minScale
                        BSON_BINDATA.getMaxScale(), //maxScale
                        BSON_BINDATA.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_BOOL.getBsonName(), //typeName
                        Types.BIT, //dataType
                        BSON_BOOL.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_BOOL.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        true, //unsigned
                        false, //fixedPrecScale
                        BSON_BOOL.getMinScale(), //minScale
                        BSON_BOOL.getMaxScale(), //maxScale
                        BSON_BOOL.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_DATE.getBsonName(), //typeName
                        Types.TIMESTAMP, //dataType
                        BSON_DATE.getPrecision(), //precision
                        "'", //literalPrefix
                        "'", //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_DATE.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_DATE.getMinScale(), //minScale
                        BSON_DATE.getMaxScale(), //maxScale
                        BSON_DATE.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_DECIMAL.getBsonName(), //typeName
                        Types.DECIMAL, //dataType
                        BSON_DECIMAL.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_DECIMAL.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_DECIMAL.getMinScale(), //minScale
                        BSON_DECIMAL.getMaxScale(), //maxScale
                        BSON_DECIMAL.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_DOUBLE.getBsonName(), //typeName
                        Types.DOUBLE, //dataType
                        BSON_DOUBLE.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_DOUBLE.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_DOUBLE.getMinScale(), //minScale
                        BSON_DOUBLE.getMaxScale(), //maxScale
                        BSON_DOUBLE.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_INT.getBsonName(), //typeName
                        Types.INTEGER, //dataType
                        BSON_INT.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_INT.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_INT.getMinScale(), //minScale
                        BSON_INT.getMaxScale(), //maxScale
                        BSON_INT.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_LONG.getBsonName(), //typeName
                        Types.INTEGER, //dataType
                        BSON_LONG.getPrecision(), //precision
                        null, //literalPrefix
                        null, //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_LONG.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_LONG.getMinScale(), //minScale
                        BSON_LONG.getMaxScale(), //maxScale
                        BSON_LONG.getNumPrecRadix())); //numPrecRadix

        docs.add(
                getTypeInfoValuesDoc(
                        BSON_STRING.getBsonName(), //typeName
                        Types.LONGVARCHAR, //dataType
                        BSON_STRING.getPrecision(), //precision
                        "'", //literalPrefix
                        "'", //literalSuffix
                        ResultSetMetaData.columnNullable, //nullable
                        BSON_STRING.getCaseSensitivity(), //caseSensitive
                        typeSearchable, //searchable
                        false, //unsigned
                        false, //fixedPrecScale
                        BSON_STRING.getMinScale(), //minScale
                        BSON_STRING.getMaxScale(), //maxScale
                        BSON_STRING.getNumPrecRadix())); //numPrecRadix

        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
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
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_CAT, TYPE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_SCHEM, TYPE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", CLASS_NAME, CLASS_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", REMARKS, REMARKS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", BASE_TYPE, BASE_TYPE, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        // We do not have SuperTypes.
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_CAT, TYPE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_SCHEM, TYPE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SUPERTYPE_CAT, SUPERTYPE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SUPERTYPE_SCHEM, SUPERTYPE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SUPERTYPE_NAME, SUPERTYPE_NAME, BSON_STRING.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        // We do not have SuperTables.
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TABLE_CAT, TABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", TABLE_SCHEM, TABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TABLE_NAME, TABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SUPERTABLE_NAME, SUPERTABLE_NAME, BSON_STRING.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern)
            throws SQLException {
        // We do not have UDTs.
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_CAT, TYPE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_SCHEM, TYPE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", ATTR_NAME, ATTR_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", ATTR_TYPE_NAME, ATTR_TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", ATTR_SIZE, ATTR_SIZE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DECIMAL_DIGITS, DECIMAL_DIGITS, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", NUM_PREC_RADIX, NUM_PREC_RADIX, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", NULLABLE, NULLABLE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", REMARKS, REMARKS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", ATTR_DEF, ATTR_DEF, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATA_TYPE, SQL_DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATETIME_SUB, SQL_DATETIME_SUB, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", CHAR_OCTET_LENGTH, CHAR_OCTET_LENGTH, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", ORDINAL_POSITION, ORDINAL_POSITION, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", IS_NULLABLE, IS_NULLABLE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SCOPE_CATALOG, SCOPE_CATALOG, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SCOPE_SCHEMA, SCOPE_SCHEMA, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SCOPE_TABLE, SCOPE_TABLE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SOURCE_DATA_TYPE, SOURCE_DATA_TYPE, BSON_INT.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    //------------------------- JDBC 4.0 -----------------------------------

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return getSchemas();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(new MySQLColumnInfo("", "", "", NAME, NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", MAX_LEN, MAX_LEN, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DEFAULT_VALUE, DEFAULT_VALUE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DESCRIPTION, DESCRIPTION, BSON_STRING.getBsonName()));
        docs.add(metaDoc);

        MySQLResultDoc doc = new MySQLResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("user"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("database user for the connection"));
        docs.add(doc);

        doc = new MySQLResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("password"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("user password for the connection"));
        docs.add(doc);

        doc = new MySQLResultDoc();
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

        doc = new MySQLResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString("database"));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonString(""));
        doc.values.add(new BsonString("database to connect to"));
        docs.add(doc);

        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    private MySQLResultDoc getFunctionMetaDoc() throws SQLException {
        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>(5);
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_CAT", "FUNCTION_CAT", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_NAME", "FUNCTION_NAME", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "REMARKS", "REMARKS", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_TYPE", "FUNCTION_TYPE", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", BSON_STRING.getBsonName()));
        return metaDoc;
    }

    private MySQLResultDoc getFunctionValuesDoc(String functionName, String remarks) {
        MySQLResultDoc doc = new MySQLResultDoc();
        doc.values = new ArrayList<>(5);
        doc.values.add(new BsonString("def"));
        doc.values.add(new BsonNull());
        doc.values.add(new BsonString(functionName));
        // perhaps at some point add comments explaining the function.
        doc.values.add(new BsonString(remarks));
        doc.values.add(new BsonInt32(functionNoTable));
        doc.values.add(new BsonString(functionName));
        return doc;
    }

    private MySQLResultDoc getFunctionColumnMetaDoc() throws SQLException {
        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>(17);
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_CAT", "FUNCTION_CAT", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_SCHEM", "FUNCTION_SCHEM", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "FUNCTION_NAME", "FUNCTION_NAME", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "COLUMN_NAME", "COLUMN_NAME", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "COLUMN_TYPE", "COLUMN_TYPE", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "DATA_TYPE", "DATA_TYPE", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "TYPE_NAME", "TYPE_NAME", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "PRECISION", "PRECISION", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "LENGTH", "LENGTH", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "SCALE", "SCALE", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "RADIX", "RADIX", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "NULLABLE", "NULLABLE", BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", "REMARKS", "REMARKS", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "",
                        "",
                        "",
                        "CHAR_OCTET_LENGTH",
                        "CHAR_OCTET_LENGTH",
                        BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "",
                        "",
                        "",
                        "ORDINAL_POSITION",
                        "ORDINAL_POSITION",
                        BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "IS_NULLABLE", "IS_NULLABLE", BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", "SPECIFIC_NAME", "SPECIFIC_NAME", BSON_STRING.getBsonName()));
        return metaDoc;
    }

    protected MySQLResultDoc getFunctionColumnValuesDoc(
            MongoFunctions.MongoFunction func,
            int i,
            String argName,
            String argType,
            boolean isReturnColumn)
            throws SQLException {

        Map<String, BsonValue> info =
                super.getFunctionParameterValues(func, i, argName, argType, isReturnColumn);
        MySQLResultDoc doc = new MySQLResultDoc();
        doc.values = new ArrayList<>(17);
        doc.values.addAll(info.values());
        return doc;
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
            throws SQLException {

        ArrayList<MySQLResultDoc> docs =
                new ArrayList<MySQLResultDoc>(MySQLFunctions.functions.length);
        docs.add(getFunctionMetaDoc());

        Pattern functionPatternRE = null;
        if (functionNamePattern != null) {
            functionPatternRE = toJavaPattern(functionNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MySQLFunctions.functions) {
            if (functionPatternRE != null && !functionPatternRE.matcher(func.name).matches()) {
                continue;
            }
            MySQLResultDoc doc = getFunctionValuesDoc(func.name, func.comment);
            docs.add(doc);
        }

        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern)
            throws SQLException {
        ArrayList<MySQLResultDoc> docs = new ArrayList<>(MySQLFunctions.functions.length);
        docs.add(getFunctionColumnMetaDoc());

        Pattern functionNamePatternRE = null;
        Pattern columnNamePatternRE = null;
        if (functionNamePattern != null) {
            functionNamePatternRE = toJavaPattern(functionNamePattern);
        }
        if (columnNamePattern != null) {
            columnNamePatternRE = toJavaPattern(columnNamePattern);
        }

        for (MongoFunctions.MongoFunction func : MySQLFunctions.functions) {
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
                MySQLResultDoc doc =
                        getFunctionColumnValuesDoc(func, i, columnName, argType, false);
                docs.add(doc);
            }
            String columnName = "argReturn";
            if (columnNamePatternRE == null || columnNamePatternRE.matcher(columnName).matches()) {
                MySQLResultDoc doc =
                        getFunctionColumnValuesDoc(func, i, "argReturn", func.returnType, true);
                docs.add(doc);
            }
        }

        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    private MySQLResultDoc getTypeInfoMetaDoc() throws SQLException {

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TYPE_NAME, TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", PRECISION, PRECISION, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", LITERAL_PREFIX, LITERAL_PREFIX, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", LITERAL_SUFFIX, LITERAL_SUFFIX, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", CREATE_PARAMS, CREATE_PARAMS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", NULLABLE, NULLABLE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", CASE_SENSITIVE, CASE_SENSITIVE, BSON_BOOL.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", SEARCHABLE, SEARCHABLE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "",
                        "",
                        "",
                        UNSIGNED_ATTRIBUTE,
                        UNSIGNED_ATTRIBUTE,
                        BSON_BOOL.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", FIXED_PREC_SCALE, FIXED_PREC_SCALE, BSON_BOOL.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", AUTO_INCREMENT, AUTO_INCREMENT, BSON_BOOL.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", LOCAL_TYPE_NAME, LOCAL_TYPE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", MINIMUM_SCALE, MINIMUM_SCALE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", MAXIMUM_SCALE, MAXIMUM_SCALE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATA_TYPE, SQL_DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", SQL_DATETIME_SUB, SQL_DATETIME_SUB, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", NUM_PREC_RADIX, NUM_PREC_RADIX, BSON_INT.getBsonName()));

        return metaDoc;
    }

    private MySQLResultDoc getTypeInfoValuesDoc(
            String typeName,
            int dataType,
            Integer precision,
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

        MySQLResultDoc doc = new MySQLResultDoc();
        doc.values = new ArrayList<>();
        doc.values.add(new BsonString(typeName));
        doc.values.add(new BsonInt32(dataType));
        doc.values.add(precision != null ? new BsonInt32(precision) : new BsonInt32(0));
        doc.values.add(literalPrefix != null ? new BsonString(literalPrefix) : n);
        doc.values.add(literalSuffix != null ? new BsonString(literalSuffix) : n);
        doc.values.add(n);
        doc.values.add(new BsonInt32(nullable));
        doc.values.add(new BsonBoolean(caseSensitive));
        doc.values.add(new BsonInt32(searchable));
        doc.values.add(new BsonBoolean(unsigned));
        doc.values.add(new BsonBoolean(fixedPrecScale));
        doc.values.add(new BsonBoolean(false));
        doc.values.add(n);
        doc.values.add(new BsonInt32(minScale));
        doc.values.add(new BsonInt32(maxScale));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonInt32(0));
        doc.values.add(new BsonInt32(numPrecRadix));

        return doc;
    }

    //--------------------------JDBC 4.1 -----------------------------
    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {
        // We do not support pseudoMySQLColumnInfos (hidden columns).
        ArrayList<MySQLResultDoc> docs = new ArrayList<>();

        MySQLResultDoc metaDoc = new MySQLResultDoc();
        metaDoc.columns = new ArrayList<>();
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TABLE_CAT, TABLE_CAT, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", TABLE_SCHEM, TABLE_SCHEM, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", TABLE_NAME, TABLE_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", COLUMN_NAME, COLUMN_NAME, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", DATA_TYPE, DATA_TYPE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", COLUMN_SIZE, COLUMN_SIZE, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", DECIMAL_DIGITS, DECIMAL_DIGITS, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", NUM_PREC_RADIX, NUM_PREC_RADIX, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", COLUMN_USAGE, COLUMN_USAGE, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo("", "", "", REMARKS, REMARKS, BSON_STRING.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", CHAR_OCTET_LENGTH, CHAR_OCTET_LENGTH, BSON_INT.getBsonName()));
        metaDoc.columns.add(
                new MySQLColumnInfo(
                        "", "", "", IS_NULLABLE, IS_NULLABLE, BSON_STRING.getBsonName()));

        docs.add(metaDoc);
        return new MySQLResultSet(conn.getLogger(), new MySQLExplicitCursor(docs), true);
    }

    private enum TypeCase {
        JDBC_TYPE,
        PRECISION,
        SCALE,
        FIXED_BYTES_LENGTH
    }

    private String getDataTypeNumCase(String col) {
        return getTypeCase(col, TypeCase.JDBC_TYPE);
    }

    private String getDataTypePrecCase(String col) {
        return getTypeCase(col, TypeCase.PRECISION);
    }

    private String getDataTypeScaleCase(String col) {
        return getTypeCase(col, TypeCase.SCALE);
    }

    private String getDataTypeBytesCase(String col) {
        return getTypeCase(col, TypeCase.FIXED_BYTES_LENGTH);
    }

    private String getTypeCase(String col, TypeCase typeCase) {
        StringBuilder ret = new StringBuilder("case ");
        ret.append(col);
        ret.append("\n");
        for (BsonTypeInfo typeInfo : BsonTypeInfo.values()) {
            ret.append("when ");
            ret.append("'");
            ret.append(typeInfo.getBsonName());
            ret.append("' then ");
            switch (typeCase) {
                case JDBC_TYPE:
                    ret.append(typeInfo.getJdbcType());
                    break;
                case PRECISION:
                    ret.append(typeInfo.getPrecision());
                    break;
                case SCALE:
                    ret.append(typeInfo.getMaxScale());
                    break;
                case FIXED_BYTES_LENGTH:
                    ret.append(typeInfo.getFixedBytesLength());
                    break;
            }
            ret.append(" \n");
        }
        ret.append("end");
        return ret.toString();
    }
}
