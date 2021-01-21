package com.mongodb.jdbc;

public class Column {
    public String database;
    public String table;
    public String tableAlias;
    public String column;
    public String columnAlias;
    public String bsonType;

    public Column() {}

    public Column(
            String database,
            String table,
            String tableAlias,
            String column,
            String columnAlias,
            String bsonType) {
        this.database = database;
        this.table = table;
        this.tableAlias = tableAlias;
        this.column = column;
        this.columnAlias = columnAlias;
        this.bsonType = bsonType;
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
                + ", bsonType="
                + bsonType
                + '}';
    }
}
