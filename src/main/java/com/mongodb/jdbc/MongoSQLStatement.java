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

public class MongoSQLStatement extends MongoStatement<BsonDocument> implements Statement {
    public MongoSQLStatement(MongoConnection conn, String databaseName) throws SQLException {
        super(conn, databaseName);
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        BsonDocument stage = constructQueryDocument(sql, "mongosql");
        try {
            MongoIterable<BsonDocument> iterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(Collections.singletonList(stage), BsonDocument.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            if (fetchSize != 0) {
                iterable = iterable.batchSize(fetchSize);
            }

            resultSet = new MongoSQLResultSet(this, iterable.cursor());
            return resultSet;
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        }
    }
}
