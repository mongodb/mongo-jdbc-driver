package com.mongodb.jdbc;

import org.bson.BsonValue;

public class Column {
    public String database;
    public String table;
    public String tableAlias;
    public String column;
    public String columnAlias;
    public BsonValue value;

    public Column() {}

    public Column(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            BsonValue value) {
        this.database = database;
        this.table = table;
        this.tableAlias = tableAlias;
        this.column = column;
        this.columnAlias = columnAlias;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Column{"
                + "database='"
                + database
                + '\''
                + ", table='"
                + table
                + '\''
                + ", tableAlias='"
                + tableAlias
                + '\''
                + ", column='"
                + column
                + '\''
                + ", columnAlias='"
                + columnAlias
                + '\''
                + ", value="
                + value
                + '}';
    }
}
