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

import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoLoggable
public class MySQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {
    protected List<MySQLColumnInfo> columnInfo;
    protected Map<String, Integer> columnPositions;

    public MySQLResultSetMetaData(
            MySQLResultDoc metadataDoc, MongoLogger parentLogger, Integer statementId)
            throws SQLException {
        super(parentLogger, statementId);
        columnInfo = metadataDoc.columns;

        columnPositions = new HashMap<>(columnInfo.size());
        int i = 0;
        for (MySQLColumnInfo c : columnInfo) {
            c.init();
            columnPositions.put(c.columnAlias, i++);
        }
    }

    public int getColumnPositionFromLabel(String label) {
        return columnPositions.get(label);
    }

    public boolean hasColumnWithLabel(String label) {
        return columnPositions.containsKey(label);
    }

    @Override
    public MongoColumnInfo getColumnInfo(int column) throws SQLException {
        checkBounds(column);
        return columnInfo.get(column - 1);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnInfo.size();
    }
}
