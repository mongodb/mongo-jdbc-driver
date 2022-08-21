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

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonDocument;

/**
 * BsonExplicitCursor allows for creating an instance of MongoCursor from an explicit list of BSON
 * docs. Useful for testing or for any place static results are necessary.
 */
public class BsonExplicitCursor implements MongoCursor<BsonDocument> {
    private List<BsonDocument> docs;
    private int rowNum = 0;

    public static final BsonExplicitCursor EMPTY_CURSOR = new BsonExplicitCursor(new ArrayList<>());

    public BsonExplicitCursor(List<BsonDocument> docs) {
        this.docs = docs;
    }

    @Override
    public void close() {}

    @Override
    public ServerAddress getServerAddress() {
        return new ServerAddress("127.0.0.1");
    }

    @Override
    public ServerCursor getServerCursor() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return rowNum < docs.size();
    }

    @Override
    public BsonDocument next() {
        return docs.get(rowNum++);
    }

    @Override
    public int available() {
        return docs.size() - rowNum;
    }

    @Override
    public BsonDocument tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
