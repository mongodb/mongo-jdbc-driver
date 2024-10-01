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

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class GetMongosqlTranslateVersionResult {

    public String version;
    public String error;
    public Boolean errorIsInternal;

    @BsonCreator
    public GetMongosqlTranslateVersionResult(
            @BsonProperty("version") final String version,
            @BsonProperty("error") final String error,
            @BsonProperty("error_is_internal") final Boolean errorIsInternal) {

        this.version = version;
        this.error = error != null ? error : "";
        this.errorIsInternal = errorIsInternal != null ? errorIsInternal : false;
    }

    public boolean hasError() {
        return !error.isEmpty();
    }
}
