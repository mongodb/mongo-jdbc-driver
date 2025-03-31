/*
 * Copyright 2025-present MongoDB, Inc.
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

package com.mongodb.jdbc.logging;

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.MongoJsonSchema;
import com.mongodb.jdbc.utils.BsonUtils;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class QueryDiagnostics {
    private static final Codec<QueryDiagnostics> CODEC =
            MongoDriver.getCodecRegistry().get(QueryDiagnostics.class);

    @BsonProperty private String sqlQuery;
    @BsonProperty private BsonDocument queryCatalog;
    @BsonProperty private MongoJsonSchema resultSetSchema;
    @BsonProperty private BsonArray pipeline;

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public void setQueryCatalog(BsonDocument queryCatalog) {
        this.queryCatalog = queryCatalog;
    }

    public void setResultSetSchema(MongoJsonSchema resultSetSchema) {
        this.resultSetSchema = resultSetSchema;
    }

    public void setPipeline(BsonArray pipeline) {
        this.pipeline = pipeline;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public BsonDocument getQueryCatalog() {
        return queryCatalog;
    }

    public MongoJsonSchema getResultSetSchema() {
        return resultSetSchema;
    }

    public BsonArray getPipeline() {
        return pipeline;
    }

    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
