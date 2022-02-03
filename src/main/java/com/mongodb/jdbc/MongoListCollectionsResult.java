package com.mongodb.jdbc;

public class MongoListCollectionsResult {
    public static final String TABLE = "table";
    public static final String COLLECTION = "collection";

    public String name;
    public String type;

    public void setType(String type) {
        // If mongodb type is collection, map it as TABLE.
        // Otherwise, keep the type as is.
        this.type = type.equalsIgnoreCase(COLLECTION) ? TABLE : type;
    }

}
