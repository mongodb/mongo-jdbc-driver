package com.mongodb.jdbc;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.client.MongoIterable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class MySQLStatement extends MongoStatement implements Statement {
    private boolean relaxed;

    public MySQLStatement(MongoConnection conn, String databaseName, boolean relaxed)
            throws SQLException {
        super(conn, databaseName);
        this.relaxed = relaxed;
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        BsonDocument stage = new BsonDocument();
        BsonDocument sqlDoc = new BsonDocument();
        sqlDoc.put("statement", new BsonString(sql));
        sqlDoc.put("formatVersion", formatVersion);
        sqlDoc.put("format", new BsonString("jdbc"));
        sqlDoc.put("dialect", new BsonString("mysql"));
        stage.put("$sql", sqlDoc);
        try {
            MongoIterable<MongoResultDoc> iterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(Collections.singletonList(stage), MongoResultDoc.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            if (fetchSize != 0) {
                iterable = iterable.batchSize(fetchSize);
            }

            resultSet = new MySQLResultSet(this, iterable.cursor(), relaxed);
            return resultSet;
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        }
    }
}
