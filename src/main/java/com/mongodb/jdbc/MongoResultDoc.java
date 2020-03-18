package com.mongodb.jdbc;

import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MongoResultDoc {
    public Boolean emptyResultSet;
    public List<Column> values;

    public MongoResultDoc() {}

    public MongoResultDoc(List<Column> cols, boolean isEmpty) {
        values = cols;
        this.emptyResultSet = isEmpty;
    }

    List<Column> getValues() {
        return values;
    }

    boolean isEmpty() {
        return emptyResultSet != null && emptyResultSet;
    }

    // MongoResultDoc[emptyResultSet=,values=[Column{database=, table=, tableAlias=, column=, columnAlias=, value=}]]
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public int columnCount() throws SQLException {
        if (values == null) {
            throw new SQLException("Invalid input");
        }
        return values.size();
    }
}
