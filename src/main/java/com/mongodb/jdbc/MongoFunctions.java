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

public class MongoFunctions {
    public enum FunctionCategory {
        STRING_FUNC,
        NUM_FUNC,
        TIME_DATE_FUNC,
        SYSTEM_FUNC,
        CONV_FUNC,
        UNCATEGORIZED_FUNC
    }

    public static class MongoFunction {
        public String name;
        public String returnType;
        public String comment;
        public String[] argTypes;
        public FunctionCategory functionCategory;

        protected MongoFunction(
                String name,
                String returnType,
                String comment,
                String[] argTypes,
                FunctionCategory category) {
            this.name = name;
            this.returnType = returnType;
            this.comment = comment;
            this.argTypes = argTypes;
            this.functionCategory = category;
        }

        protected MongoFunction(String name, String returnType, String comment, String[] argTypes) {
            this.name = name;
            this.returnType = returnType;
            this.comment = comment;
            this.argTypes = argTypes;
            this.functionCategory = FunctionCategory.UNCATEGORIZED_FUNC;
        }
    }

    private static MongoFunctions instance;
    public MongoFunction[] functions;
    public String numericFunctionsString;
    public String stringFunctionsString;
    public String dateFunctionsString;
    public String systemFunctionsString;

    // Common and repeated function names.
    protected static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    protected static final String SUBSTRING = "SUBSTRING";
    protected static final String COALESCE = "COALESCE";
    protected static final String EXTRACT = "EXTRACT";
    protected static final String NULLIF = "NULLIF";

    private MongoFunctions(MongoFunction[] functions) {
        this.functions = functions;
        initCategorizedFunctionsList();
    }

    public static MongoFunctions getInstance() {
        if (null == instance) {
            instance =
                    new MongoFunctions(
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

    // Build the list of numeric, string, dateTime and system functions.
    private void initCategorizedFunctionsList() {
        StringBuilder numericFunctionsBuilder = new StringBuilder();
        StringBuilder stringFunctionsBuilder = new StringBuilder();
        StringBuilder dateTimeFunctionsBuilder = new StringBuilder();
        StringBuilder systemFunctionsBuilder = new StringBuilder();

        StringBuilder currBuilder = null;
        for (MongoFunction currFunc : functions) {
            switch (currFunc.functionCategory) {
                case NUM_FUNC:
                    {
                        currBuilder = numericFunctionsBuilder;
                        break;
                    }

                case STRING_FUNC:
                    {
                        currBuilder = stringFunctionsBuilder;
                        break;
                    }

                case SYSTEM_FUNC:
                    {
                        currBuilder = systemFunctionsBuilder;
                        break;
                    }

                case TIME_DATE_FUNC:
                    {
                        currBuilder = dateTimeFunctionsBuilder;

                        break;
                    }

                case CONV_FUNC:
                case UNCATEGORIZED_FUNC:
                default:
                    {
                        currBuilder = null;
                        // Nothing to do
                        break;
                    }
            }

            if (null != currBuilder) {
                if (currBuilder.length() > 0) {
                    currBuilder.append(',');
                }
                currBuilder.append(currFunc.name);
            }
        }
        numericFunctionsString = numericFunctionsBuilder.toString();
        stringFunctionsString = stringFunctionsBuilder.toString();
        dateFunctionsString = dateTimeFunctionsBuilder.toString();
        systemFunctionsString = systemFunctionsBuilder.toString();
    }
}
