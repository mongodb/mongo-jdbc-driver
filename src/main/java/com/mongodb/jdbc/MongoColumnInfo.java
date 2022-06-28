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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonType;

public class MongoColumnInfo {
    private final String datasource;
    private final String field;
    private final BsonTypeInfo bsonTypeInfo;
    private final boolean isPolymorphic;
    private final int nullable;

    MongoColumnInfo(String datasource, String field, BsonTypeInfo bsonTypeInfo, int nullability) {
        this.datasource = datasource;
        this.field = field;
        this.bsonTypeInfo = bsonTypeInfo;
        this.nullable = nullability;
        this.isPolymorphic = bsonTypeInfo == BsonTypeInfo.BSON_BSON;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * * Returns true if a column is polymorphic, false otherwise.
     *
     * @return true if a column is polymorphic, false otherwise.
     */
    public boolean isPolymorphic() {
        return isPolymorphic;
    }

    /**
     * Return the column BSON type enum value.
     *
     * @return the column BSON type enum value.
     */
    public BsonType getBsonTypeEnum() {
        return bsonTypeInfo.getBsonType();
    }

    /**
     * Return the column BSON type name.
     *
     * @return the column BSON type name.
     */
    public String getBsonTypeName() {
        return bsonTypeInfo.getBsonName();
    }

    /**
     * Return the column JDBC type.
     *
     * @return the column JDBC type.
     */
    public int getJDBCType() {
        return bsonTypeInfo.getJdbcType();
    }

    /**
     * Return the column nullability.
     *
     * @return the column nullability.
     */
    public int getNullability() {
        return nullable;
    }

    /**
     * Return the column name.
     *
     * @return the column name.
     */
    public String getColumnName() {
        return field;
    }

    /**
     * Return the column alias.
     *
     * @return the column alias.
     */
    public String getColumnAlias() {
        return field;
    }

    /**
     * Return the column's parent table name.
     *
     * @return the column's parent table name.
     */
    public String getTableName() {
        return datasource;
    }

    /**
     * Return the column's parent table alias.
     *
     * @return the column's parent table alias.
     */
    public String getTableAlias() {
        return datasource;
    }

    /**
     * Return the column's parent database.
     *
     * @return the column's parent database.
     */
    public String getDatabase() {
        return "";
    }
}
