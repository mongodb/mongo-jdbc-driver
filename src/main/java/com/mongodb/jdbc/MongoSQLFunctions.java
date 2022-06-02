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

public class MongoSQLFunctions extends MongoFunctions {
    private static MongoSQLFunctions instance;

    private MongoSQLFunctions(MongoFunction[] functions) {
        super(functions);
    }

    public static MongoSQLFunctions getInstance() {
        if (null == instance) {
            instance =
                    new MongoSQLFunctions(
                            new MongoFunction[] {
                                new MongoFunction(
                                        "BIT_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string in bits",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "CHAR_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        CURRENT_TIMESTAMP,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current date and time.",
                                        new String[] {},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        CURRENT_TIMESTAMP,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current date and time.",
                                        // Timestamp precision
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()}),
                                /**
                                 * Note EXTRACT supports more than YEAR, MONTH, DAY, HOUR, MINUTE,
                                 * SECOND for the unit. It also supports TIMEZONE_HOUR |
                                 * TIMEZONE_MINUTE.
                                 */
                                new MongoFunction(
                                        EXTRACT,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the value of the specified unit from the provided date.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        },
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "LOWER",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with all characters changed to lowercase.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "OCTET_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string in bytes",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "POSITION",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the position of the first occurrence of substring substr in string str.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "SIZE",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the size of an array.",
                                        new String[] {BsonTypeInfo.BSON_ARRAY.getBsonName()}),
                                new MongoFunction(
                                        SUBSTRING,
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "takes a substring from a string",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        SUBSTRING,
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "takes a substring from a string",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "TRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "TRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "TRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "UPPER",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with all characters changed to uppercase.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()})
                            });
        }

        return instance;
    }
}
