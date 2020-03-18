package com.mongodb.jdbc;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.List;

public class MongoTestCursor implements MongoCursor<MongoResultDoc> {
    private List<MongoResultDoc> mongoResultDocs;
    private int rowNum = 0;

    public MongoTestCursor(List<MongoResultDoc> mongoResultDocs) {
        this.mongoResultDocs = mongoResultDocs;
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
        return rowNum < mongoResultDocs.size();
    }

    @Override
    public MongoResultDoc next() {
        return mongoResultDocs.get(rowNum++);
    }

    @Override
    public MongoResultDoc tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
