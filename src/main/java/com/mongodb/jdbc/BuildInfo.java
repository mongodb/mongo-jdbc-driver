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

package com.mongodb.jdbc;

import java.util.Optional;
import java.util.Set;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class BuildInfo {
    public String version;
    public Set<String> modules;
    public int ok;
    public DataLake dataLake;

    @BsonCreator
    public BuildInfo(
            @BsonProperty("version") String version,
            @BsonProperty("modules") Set<String> modules,
            @BsonProperty("ok") int ok,
            @BsonProperty("dataLake") DataLake dataLake) {
        this.version = version;
        this.modules = modules;
        this.ok = ok;
        this.dataLake = dataLake;
    }

    public Optional<DataLake> getDataLakePresence() {
        return Optional.ofNullable(dataLake);
    }

    public String getDataLakeVersion() {
        return getDataLakePresence().map(dl -> "." + dl.version).orElse("");
    }

    public String getDataLakeMongoSQLVersion() {
        return getDataLakePresence().map(dl -> "." + dl.mongoSQLVersion).orElse("");
    }

    // Override toString for logging
    @Override
    public String toString() {
        return "BuildInfo{"
                + "version='"
                + version
                + '\''
                + ", modules="
                + modules
                + ", ok="
                + ok
                + ", dataLake="
                + dataLake
                + '}';
    }
}
