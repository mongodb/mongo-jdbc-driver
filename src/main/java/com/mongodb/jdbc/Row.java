package com.mongodb.jdbc;

import java.util.List;

public class Row {
    public List<Column> values;

    @Override
    public String toString() {
        return "Row{" + "values=" + values + '}';
    }

    public int size() {
        if (values == null) return 0;
        return values.size();
    }
}
