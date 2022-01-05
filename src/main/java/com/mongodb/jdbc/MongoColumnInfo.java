package com.mongodb.jdbc;

import org.bson.BsonType;

public interface MongoColumnInfo {
    public boolean isPolymorphic();

    public BsonType getBsonTypeEnum();

    public String getBsonTypeName();

    public int getJDBCType();

    public int getNullability();

    public String getColumnName();

    public String getColumnAlias();

    public String getDatabase();

    public String getTableName();

    public String getTableAlias();
}
