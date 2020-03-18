package com.mongodb.jdbc;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.List;

public class MongoTestCursor implements MongoCursor<Row> {
    private List<Row> rows;
    private int rowNum = 0;

    public MongoTestCursor(List<Row> rows) {
        this.rows = rows;
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
        return rowNum < rows.size();
    }

    @Override
    public Row next() {
        return rows.get(rowNum++);
    }

    @Override
    public Row tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
