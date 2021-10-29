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
import org.bson.BsonInt32;
import org.bson.BsonString;

public class MongoSQLStatement extends MongoStatement<BsonDocument> implements Statement {
    public MongoSQLStatement(MongoConnection conn, String databaseName) throws SQLException {
        super(conn, databaseName);
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        BsonDocument stage = constructQueryDocument(sql, "mongosql");
        BsonDocument schemaStage = constructSQLGetResultSchemaDocument(sql);
        try {
            MongoIterable<BsonDocument> iterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(Collections.singletonList(stage), BsonDocument.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            if (fetchSize != 0) {
                iterable = iterable.batchSize(fetchSize);
            }

            MongoIterable<MongoJsonSchemaResult> schemaIterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(
                                    Collections.singletonList(schemaStage),
                                    MongoJsonSchemaResult.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            MongoJsonSchemaResult schemaResult = schemaIterable.cursor().next();
            if (schemaResult.ok != 1) {
                throw new SQLException("Invalid schema result from server");
            }
            MongoJsonSchema schema = schemaResult.schema.jsonSchema;
            resultSet = new MongoSQLResultSet(this, iterable.cursor(), schema);
            return resultSet;
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        }
    }

    private BsonDocument constructSQLGetResultSchemaDocument(String sql) {
        BsonDocument stage = new BsonDocument();
        stage.put("query", new BsonString(sql));
        stage.put("sqlGetResultSchema", new BsonInt32(1));
        stage.put("schemaVersion", new BsonInt32(1));
        return stage;
    }
}
