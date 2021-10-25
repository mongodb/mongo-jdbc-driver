package com.mongodb.jdbc;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.List;

/**
 * MySQLExplicitCursor allows for creating an instance of MySQLCursor from an explicit list of
 * result docs. Useful for testing or for any place static results are necessary.
 */
public class MySQLExplicitCursor implements MongoCursor<MySQLResultDoc> {
    private List<MySQLResultDoc> docs;
    private int rowNum = 0;

    public MySQLExplicitCursor(List<MySQLResultDoc> docs) {
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
    public MySQLResultDoc next() {
        return docs.get(rowNum++);
    }

    @Override
    public MySQLResultDoc tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
