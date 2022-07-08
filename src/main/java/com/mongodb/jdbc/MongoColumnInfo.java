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

    public boolean isPolymorphic() {
        return isPolymorphic;
    }

    public BsonType getBsonTypeEnum() {
        return bsonTypeInfo.getBsonType();
    }

    public String getBsonTypeName() {
        return bsonTypeInfo.getBsonName();
    }

    public int getJDBCType() {
        return bsonTypeInfo.getJdbcType();
    }

    public int getNullability() {
        return nullable;
    }

    public String getColumnName() {
        return field;
    }

    public String getColumnAlias() {
        return field;
    }

    public String getTableName() {
        return datasource;
    }

    public String getTableAlias() {
        return datasource;
    }

    public String getDatabase() {
        return "";
    }
}
