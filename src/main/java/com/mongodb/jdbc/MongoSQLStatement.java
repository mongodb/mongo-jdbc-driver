/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.bson.BsonString;

@AutoLoggable
public class MongoSQLStatement extends MongoStatement<BsonDocument> implements Statement {
    private final BsonInt32 formatVersion = new BsonInt32(1);

    public MongoSQLStatement(MongoConnection conn, String databaseName) throws SQLException {
        super(conn, databaseName);
    }

    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        BsonDocument stage = constructQueryDocument(sql, "mongosql", formatVersion);
        BsonDocument getSchemaCmd = constructSQLGetResultSchemaDocument(sql);
        try {
            MongoIterable<BsonDocument> iterable =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .aggregate(Collections.singletonList(stage), BsonDocument.class)
                            .maxTime(maxQuerySec, TimeUnit.SECONDS);
            if (fetchSize != 0) {
                iterable = iterable.batchSize(fetchSize);
            }

            MongoJsonSchemaResult schemaResult =
                    currentDB
                            .withCodecRegistry(MongoDriver.registry)
                            .runCommand(getSchemaCmd, MongoJsonSchemaResult.class);

            MongoJsonSchema schema = schemaResult.schema.mongoJsonSchema;
            resultSet = new MongoSQLResultSet(this, iterable.cursor(), schema);
            return resultSet;
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        }
    }

    private BsonDocument constructSQLGetResultSchemaDocument(String sql) {
        BsonDocument command = new BsonDocument();
        command.put("sqlGetResultSchema", new BsonInt32(1));
        command.put("query", new BsonString(sql));
        command.put("schemaVersion", new BsonInt32(1));
        return command;
    }
}
