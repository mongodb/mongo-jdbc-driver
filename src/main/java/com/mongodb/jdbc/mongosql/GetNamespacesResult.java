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

import java.util.List;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class GetNamespacesResult {
    @BsonProperty("namespaces")
    public final List<Namespace> namespaces;

    @BsonProperty("error")
    public final String error;

    @BsonProperty("error_is_internal")
    public final Boolean errorIsInternal;

    @BsonCreator
    public GetNamespacesResult(
            @BsonProperty("namespaces") List<Namespace> namespaces,
            @BsonProperty("error") String error,
            @BsonProperty("error_is_internal") Boolean errorIsInternal) {
        this.namespaces = namespaces;
        this.error = error != null ? error : "";
        this.errorIsInternal = errorIsInternal != null ? errorIsInternal : false;
    }

    public boolean hasError() {
        return !error.isEmpty();
    }

    public static class Namespace {
        @BsonProperty("database")
        public final String database;

        @BsonProperty("collection")
        public final String collection;

        @BsonCreator
        public Namespace(
                @BsonProperty("database") String database,
                @BsonProperty("collection") String collection) {
            this.database = database;
            this.collection = collection;
        }
    }
}
