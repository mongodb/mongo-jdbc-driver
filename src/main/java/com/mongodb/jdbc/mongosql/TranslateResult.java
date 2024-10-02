/*
 * Copyright 2024-present MongoDB, Inc.
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

package com.mongodb.jdbc.mongosql;

import com.mongodb.jdbc.JsonSchema;
import com.mongodb.jdbc.MongoJsonSchema;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class TranslateResult extends BaseResult {
    public final String targetDb;
    public final String targetCollection;
    public final List<BsonDocument> pipeline;
    public final MongoJsonSchema resultSetSchema;
    public final List<List<String>> selectOrder;

    @BsonCreator
    public TranslateResult(
            @BsonProperty("target_db") String targetDb,
            @BsonProperty("target_collection") String targetCollection,
            @BsonProperty("pipeline") List<BsonDocument> pipeline,
            @BsonProperty("result_set_schema") JsonSchema resultSetSchema,
            @BsonProperty("select_order") List<List<String>> selectOrder,
            @BsonProperty("error") String error,
            @BsonProperty("error_is_internal") Boolean errorIsInternal) {
        super(error, errorIsInternal);
        this.targetDb = targetDb;
        this.targetCollection = targetCollection;
        this.pipeline = pipeline;
        this.resultSetSchema =
                (resultSetSchema != null)
                        ? MongoJsonSchema.toSimplifiedMongoJsonSchema(resultSetSchema)
                        : null;
        this.selectOrder = selectOrder;
    }
}
