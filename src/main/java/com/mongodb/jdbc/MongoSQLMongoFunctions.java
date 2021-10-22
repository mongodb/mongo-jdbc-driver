package com.mongodb.jdbc;

public class MongoSQLMongoFunctions extends MongoFunctions {
    private static MongoSQLMongoFunctions instance;

    private MongoSQLMongoFunctions(MongoFunction[] functions) {
        super(functions);
    }

    public static MongoSQLMongoFunctions getInstance() {
        if (null == instance) {
            instance = new MongoSQLMongoFunctions(
                new MongoFunction[]{
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
                        new String[] {"string"}),
                    new MongoFunction(
                        "COALESCE",
                        null,
                        "returns the first non-null value in the list, or null if there are no non-null values.",
                        new String[] {null}),
                    new MongoFunction(
                        "CURRENT_TIMESTAMP",
                        "date",
                        "returns the current date and time.",
                        new String[] {"int"}),
                    new MongoFunction(
                        "EXTRACT",
                        "long",
                        "returns the value of the specified unit from the provided date.",
                        new String[] {"string", "date"}),
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
                        new String[] {"string"}),
                    new MongoFunction(
                        "POSITION",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str.",
                        new String[] {"string", "string"}),
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
                        "TRIM",
                        "string",
                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                        new String[] {"string"}),
                    new MongoFunction(
                        "UPPER",
                        "string",
                        "returns the provided string with all characters changed to uppercase.",
                        new String[] {"string"})
                }
            );
        }

        return instance;
    }
}
