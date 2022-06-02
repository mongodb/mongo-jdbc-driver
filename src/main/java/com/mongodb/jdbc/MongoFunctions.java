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

public abstract class MongoFunctions {
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

    protected MongoFunctions(MongoFunction[] functions) {
        this.functions = functions;
        initCategorizedFunctionsList();
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
