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

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.utils.BsonUtils;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class CheckDriverVersionResult {

    private static final Codec<CheckDriverVersionResult> CODEC =
            MongoDriver.getCodecRegistry().get(CheckDriverVersionResult.class);

    @BsonProperty("compatible")
    public final Boolean compatible;

    @BsonCreator
    public CheckDriverVersionResult(@BsonProperty("compatible") Boolean compatible) {
        this.compatible = (compatible != null) ? compatible : false;
    }

    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
