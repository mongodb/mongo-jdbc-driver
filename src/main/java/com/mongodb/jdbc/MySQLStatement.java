package com.mongodb.jdbc;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.client.MongoIterable;
import com.mongodb.jdbc.logging.AutoLoggable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.bson.BsonInt32;

@AutoLoggable
public class MySQLStatement extends MongoStatement<MySQLResultDoc> implements Statement {
    private boolean relaxed;
    private final BsonInt32 formatVersion = new BsonInt32(2);

    public MySQLStatement(MongoConnection conn, String databaseName, boolean relaxed)
            throws SQLException {
        super(conn, databaseName);
        this.relaxed = relaxed;
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        BsonDocument stage = constructQueryDocument(sql, "mysql", formatVersion);
        try {
            MongoIterable<MySQLResultDoc> iterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(Collections.singletonList(stage), MySQLResultDoc.class)
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
