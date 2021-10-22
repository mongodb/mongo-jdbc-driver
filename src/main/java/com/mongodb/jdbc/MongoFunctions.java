package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.LinkedHashSet;

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

        protected MongoFunction(String name, String returnType, String comment, String[] argTypes, FunctionCategory category) {
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

    protected MongoFunctions(MongoFunction[] functions)
    {
        this.functions = functions;
        initCategorizedFunctionsList();
    }

    // Build the list of numeric, string, dateTime and system functions.
    private void initCategorizedFunctionsList()
    {
        StringBuilder numericFunctionBuilder = new StringBuilder();
        StringBuilder stringFunctionBuilder = new StringBuilder();
        StringBuilder dateTimeFunctionBuilder = new StringBuilder();
        StringBuilder systemFunctionBuilder = new StringBuilder();

        StringBuilder currBuilder = null;
        for (MongoFunction currFunc :  functions) {
            switch (currFunc.functionCategory)
            {
                case NUM_FUNC:
                {
                    currBuilder = numericFunctionBuilder;
                    numericFunctionBuilder.append(currFunc.name);
                    break;
                }

                case STRING_FUNC:
                {
                    currBuilder = stringFunctionBuilder;
                    stringFunctionBuilder.append(currFunc.name);
                    break;
                }

                case SYSTEM_FUNC:
                {
                    currBuilder = systemFunctionBuilder;
                    systemFunctionBuilder.append(currFunc.name);
                    break;
                }

                case TIME_DATE_FUNC:
                {
                    currBuilder = dateTimeFunctionBuilder;

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
                if (0 < currBuilder.length()) {
                    currBuilder.append(',');
                }
                currBuilder.append(currFunc.name);
            }
        }
        numericFunctionsString = numericFunctionBuilder.toString();
        stringFunctionsString = stringFunctionBuilder.toString();
        dateFunctionsString = dateTimeFunctionBuilder.toString();
        systemFunctionsString = systemFunctionBuilder.toString();
    }
}
