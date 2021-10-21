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
import org.bson.conversions.Bson;

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
                            .aggregate(Collections.singletonList(stage), MongoJsonSchemaResult.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            MongoJsonSchema schema = schemaIterable.cursor().next().schema;
            resultSet = new MongoSQLResultSet(this, iterable.cursor(), schema);
            return resultSet;
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        }
    }

    private BsonDocument constructSQLGetResultSchemaDocument(String sql) {
        BsonDocument stage = new BsonDocument();
        BsonDocument sqlDoc = new BsonDocument();
        sqlDoc.put("query", new BsonString(sql));
        sqlDoc.put("sqlGetResultSchema", new BsonInt32(1));
        sqlDoc.put("SchemaVersion", new BsonInt32(1));
        stage.put("$sql", sqlDoc);
        return stage;
    }

    class MongoJsonSchemaResult {
        public int ok;
        public MongoJsonSchema schema;
    }
}
