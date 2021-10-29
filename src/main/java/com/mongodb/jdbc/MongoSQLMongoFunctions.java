package com.mongodb.jdbc;

public class MongoSQLMongoFunctions extends MongoFunctions {
    private static MongoSQLMongoFunctions instance;

    private MongoSQLMongoFunctions(MongoFunction[] functions) {
        super(functions);
    }

    public static MongoSQLMongoFunctions getInstance() {
        if (null == instance) {
            instance =
                    new MongoSQLMongoFunctions(
                            new MongoFunction[] {
                                new MongoFunction(
                                        "BIT_LENGTH",
                                        "long",
                                        "returns length of string in bits",
                                        new String[] {"string"}),
                                new MongoFunction(
                                        "CAST",
                                        null,
                                        "converts the provided expression into a value of the specified type.",
                                        new String[] {null, null, null, null}),
                                new MongoFunction(
                                        "CHAR_LENGTH",
                                        "long",
                                        "returns length of string",
                                        new String[] {"string"},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "COALESCE",
                                        null,
                                        "returns the first non-null value in the list, or null if there are no non-null values.",
                                        new String[] {null}),
                                new MongoFunction(
                                        "CURRENT_TIMESTAMP",
                                        "date",
                                        "returns the current date and time.",
                                        new String[] {"int"},
                                        FunctionCategory.TIME_DATE_FUNC),
                                /**
                                 * Note EXTRACT supports more than YEAR, MONTH, DAY, HOUR, MINUTE,
                                 * SECOND for the unit. It also supports TIMEZONE_HOUR |
                                 * TIMEZONE_MINUTE.ß
                                 */
                                new MongoFunction(
                                        "EXTRACT",
                                        "long",
                                        "returns the value of the specified unit from the provided date.",
                                        new String[] {"string", "date"},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "LOWER",
                                        "string",
                                        "returns the provided string with all characters changed to lowercase.",
                                        new String[] {"string"}),
                                new MongoFunction(
                                        "NULLIF",
                                        null,
                                        "returns null if the two arguments are equal, and the first argument otherwise.",
                                        new String[] {null, null}),
                                new MongoFunction(
                                        "OCTET_LENGTH",
                                        "long",
                                        "returns length of string in bytes",
                                        new String[] {"string"},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "POSITION",
                                        "long",
                                        "returns the position of the first occurrence of substring substr in string str.",
                                        new String[] {"string", "string"},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "SLICE",
                                        null,
                                        "returns a slice of an array.",
                                        new String[] {"array", "int", "int"}),
                                new MongoFunction(
                                        "SIZE",
                                        "numeric",
                                        "returns the size of an array.",
                                        new String[] {"array"}),
                                new MongoFunction(
                                        "SUBSTRING",
                                        "string",
                                        "takes a substring from a string",
                                        new String[] {"string", "long"}),
                                new MongoFunction(
                                        "SUBSTRING",
                                        "string",
                                        "takes a substring from a string",
                                        new String[] {"string", "long", "long"},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "TRIM",
                                        "string",
                                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                                        new String[] {"string"}),
                                new MongoFunction(
                                        "UPPER",
                                        "string",
                                        "returns the provided string with all characters changed to uppercase.",
                                        new String[] {"string"})
                            });
        }

        return instance;
    }
}
