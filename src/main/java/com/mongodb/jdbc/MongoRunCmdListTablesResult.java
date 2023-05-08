/*
 * Copyright 2023-present MongoDB, Inc.
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

import java.util.ArrayList;

public class MongoRunCmdListTablesResult {
    public CursorInfo cursor;

    public CursorInfo getCursor() {
        return cursor;
    }

    public static class CursorInfo {
        public long id;
        public String ns;
        public ArrayList<MongoListTablesResult> firstBatch;

        public long getId() {
            return id;
        }

        public String getNs() {
            return ns;
        }

        public ArrayList<MongoListTablesResult> getFirstBatch() {
            return firstBatch;
        }
    }
}
