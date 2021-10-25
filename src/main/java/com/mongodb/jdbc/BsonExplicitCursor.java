package com.mongodb.jdbc;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonDocument;

/**
 * BsonExplicitCursor allows for creating an instance of MongoCursor from an explicit list of BSON
 * docs. Useful for testing or for any place static results are necessary.
 */
public class BsonExplicitCursor implements MongoCursor<BsonDocument> {
    private List<BsonDocument> docs;
    private int rowNum = 0;

    public static final BsonExplicitCursor EMPTY_CURSOR = new BsonExplicitCursor(new ArrayList<>());

    public BsonExplicitCursor(List<BsonDocument> docs) {
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
    public BsonDocument next() {
        return docs.get(rowNum++);
    }

    @Override
    public BsonDocument tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
