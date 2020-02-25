package com.mongodb.jdbc;

import java.sql.RowId;

public class MongoRowId implements RowId {
    private int columnPos = 0;

    public MongoRowId(final int columnPos) {
        this.columnPos = columnPos;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof MongoRowId)) return false;
        return columnPos == ((MongoRowId) o).columnPos;
    }

    private static byte getByteAtPos(int value, int pos) {
        return (byte) ((value >>> pos) & 0xff);
    }

    // We will use little Endian because we aren't barbarians :)
    // This is a meta joke.
    public byte[] getBytes() {
        return new byte[] {
            getByteAtPos(columnPos, 0),
            getByteAtPos(columnPos, 8),
            getByteAtPos(columnPos, 16),
            getByteAtPos(columnPos, 24)
        };
    }

    public int hashCode() {
        return columnPos;
    }

    public String toString() {
        return "MongoRowId(value=" + columnPos + ")";
    }
}
