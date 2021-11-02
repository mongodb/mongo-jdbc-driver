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
