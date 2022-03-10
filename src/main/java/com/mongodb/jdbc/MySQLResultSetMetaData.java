package com.mongodb.jdbc;

import com.mongodb.jdbc.logging.AutoLoggable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoLoggable
public class MySQLResultSetMetaData extends MongoResultSetMetaData implements ResultSetMetaData {
    protected List<MySQLColumnInfo> columnInfo;
    protected Map<String, Integer> columnPositions;

    public MySQLResultSetMetaData(MySQLResultDoc metadataDoc, int connectionId, Integer statementId)
            throws SQLException {
        super(connectionId, statementId);
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
