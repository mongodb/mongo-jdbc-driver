package com.mongodb.jdbc;

import org.bson.BsonValue;

public class Column {
    public String database;
    public String table;
    public String tableAlias;
    public String column;
    public String columnAlias;
    public BsonValue value;

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
