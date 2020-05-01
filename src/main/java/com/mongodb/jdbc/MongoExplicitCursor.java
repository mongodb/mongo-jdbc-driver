package com.mongodb.jdbc;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.List;

/**
 * MongoExplicitCursor allows for creating an instance of MongoCursor from an explicit list of
 * result docs. Useful for testing or for any place static results are necessary.
 */
public class MongoExplicitCursor implements MongoCursor<MongoResultDoc> {
    private List<MongoResultDoc> docs;
    private int rowNum = 0;

    public MongoExplicitCursor(List<MongoResultDoc> docs) {
        this.docs = docs;
    }

    @Override
    public void close() {}

    @Override
    public ServerAddress getServerAddress() {
        return new ServerAddress("127.0.0.1");
    }

    @Override
    public ServerCursor getServerCursor() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return rowNum < docs.size();
    }

    @Override
    public MongoResultDoc next() {
        return docs.get(rowNum++);
    }

    @Override
    public MongoResultDoc tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
