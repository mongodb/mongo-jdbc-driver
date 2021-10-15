package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class MongoFunction {
    public String name;
    public String returnType;
    public String comment;
    public String[] argTypes;

    public MongoFunction(String name, String returnType, String comment, String[] argTypes) {
        this.name = name;
        this.returnType = returnType;
        this.comment = comment;
        this.argTypes = argTypes;
    }

    public static final MongoFunction[] mySQLFunctions =
            new MongoFunction[] {
                new MongoFunction(
                        "ABS",
                        "numeric",
                        "returns the absolute value of the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "ACOS",
                        "double",
                        "returns the arc cosine of the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "ASCII",
                        "long",
                        "returns the numeric value of the leftmost character of the provided string. Returns 0 if the empty string is provided, and NULL if NULL is provided.",
                        new String[] {"string"}),
                new MongoFunction(
                        "ASIN",
                        "double",
                        "returns the arc sine of the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "ATAN",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "ATAN2",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "CEIL",
                        "long",
                        "returns the smallest integer not less than the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "CHAR",
                        "string",
                        "interprets each argument as an integer and returns a string consisting of the characters given by the code values of those integers. NULL values are skipped.",
                        new String[] {"long"}),
                new MongoFunction(
                        "CHARACTERLENGTH",
                        "long",
                        "returns length of string",
                        new String[] {"string"}),
                new MongoFunction(
                        "COALESCE",
                        null,
                        "returns the first non-null value in the list, or null if there are no non-null values.",
                        new String[] {null}),
                new MongoFunction(
                        "CONCAT",
                        "string",
                        "returns the string that results from concatenating the arguments.",
                        new String[] {"string"}),
                new MongoFunction(
                        "CONCATWS",
                        "string",
                        "returns the provided strings concatenated and separated by the provided separator.",
                        new String[] {"string", "string"}),
                new MongoFunction(
                        "CONNECTIONID", "long", "returns the connection id.", new String[] {}),
                new MongoFunction(
                        "CONV",
                        "string",
                        "converts a number from one numeric base system to another, and returns the result as a string value.",
                        new String[] {"string", "long", "long"}),
                new MongoFunction(
                        "CONVERT",
                        null,
                        "converts the provided expression into a value of the specified type.",
                        new String[] {null, "string"}),
                new MongoFunction(
                        "COS",
                        "double",
                        "returns the cosine of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "COT", "double", "returns the cotangent of x.", new String[] {"numeric"}),
                new MongoFunction(
                        "CURRENTDATE", "date", "returns the current date.", new String[] {}),
                new MongoFunction(
                        "CURRENTTIMESTAMP",
                        "date",
                        "returns the current date and time.",
                        new String[] {}),
                new MongoFunction("CURTIME", "date", "returns the current time.", new String[] {}),
                new MongoFunction(
                        "DATABASE",
                        "string",
                        "returns the current database name as a string.",
                        new String[] {}),
                new MongoFunction(
                        "DATE",
                        "date",
                        "extracts the date part of the provided date or datetime expression.",
                        new String[] {"date"}),
                new MongoFunction(
                        "DATEADD",
                        "date",
                        "adds a specified number of datetime units to the provided datetime value.",
                        new String[] {"date", "string", "string"}),
                new MongoFunction(
                        "DATEDIFF",
                        "long",
                        "returns date1 - date2 expressed as a value in days.",
                        new String[] {"date", "date"}),
                new MongoFunction(
                        "DATEFORMAT",
                        "string",
                        "formats a datetime value according to the provided format string.",
                        new String[] {"date", "string"}),
                new MongoFunction(
                        "DATESUB",
                        "date",
                        "subtracts a specified number of datetime units from the provided datetime value.",
                        new String[] {"date", "string", "string"}),
                new MongoFunction(
                        "DAYNAME",
                        "string",
                        "returns the name of the weekday for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "DAYOFMONTH",
                        "long",
                        "returns the day of the month for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "DAYOFWEEK",
                        "long",
                        "returns the weekday index (Sunday = 1 ... Saturday = 7) for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "DAYOFYEAR",
                        "long",
                        "returns the day of the year for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "DEGREES",
                        "double",
                        "converts the provided radians argument to degrees.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "ELT",
                        "string",
                        "returns the Nth element of the list of strings.",
                        new String[] {"long", "string"}),
                new MongoFunction(
                        "EXP",
                        "double",
                        "returns the value of e raised to the provided power.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "EXTRACT",
                        "long",
                        "returns the value of the specified unit from the provided date.",
                        new String[] {"string", "date"}),
                new MongoFunction(
                        "FIELD",
                        "long",
                        "returns the index of the first argument in the list of the remaining arguments.",
                        new String[] {null}),
                new MongoFunction(
                        "FLOOR",
                        "long",
                        "returns the largest integer value not greater than the provided argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "FROMDAYS",
                        "date",
                        "returns a date, given a day number.",
                        new String[] {"long"}),
                new MongoFunction(
                        "FROMUNIXTIME",
                        "date",
                        "returns a representation of the provided unix timestamp, using the provided format string if one is supplied.",
                        new String[] {"long"}),
                new MongoFunction(
                        "GREATEST",
                        null,
                        "returns the largest argument from the provided list.",
                        new String[] {null, null}),
                new MongoFunction(
                        "HOUR",
                        "long",
                        "returns the hour for the provided time.",
                        new String[] {"date"}),
                new MongoFunction(
                        "IF",
                        null,
                        "returns the second argument if the first is true, and the third argument otherwise.",
                        new String[] {null, null, null}),
                new MongoFunction(
                        "IFNULL",
                        null,
                        "returns the first argument if it is not null, and the second argument otherwise.",
                        new String[] {null, null}),
                new MongoFunction(
                        "INSERT",
                        "string",
                        "returns the string str, with the substring beginning at position pos and len characters long replaced by the string newstr.",
                        new String[] {"string", "long", "long", "string"}),
                new MongoFunction(
                        "INSTR",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str.",
                        new String[] {"string", "string"}),
                new MongoFunction(
                        "INTERVAL",
                        "numeric",
                        "returns 0 if N < N1, 1 if N < N2, and so on.",
                        new String[] {"numeric", "numeric"}),
                new MongoFunction(
                        "LASTDAY",
                        "date",
                        "takes a date or datetime value and returns the corresponding value for the last day of the month.",
                        new String[] {"date"}),
                new MongoFunction(
                        "LCASE",
                        "string",
                        "returns the provided string with all characters changed to lowercase.",
                        new String[] {"string"}),
                new MongoFunction(
                        "LEAST",
                        null,
                        "returns the smallest argument from the provided list.",
                        new String[] {null, null}),
                new MongoFunction(
                        "LEFT",
                        "string",
                        "returns the leftmost n characters from the provided string, or NULL if either argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoFunction(
                        "LENGTH",
                        "string",
                        "return the length of the provided string, measured in bytes.",
                        new String[] {"string"}),
                new MongoFunction(
                        "LN",
                        "double",
                        "returns the natural logarithm of the provided argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "LOCATE",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str, starting at index pos if provided",
                        new String[] {"string", "string"}),
                new MongoFunction(
                        "LOG",
                        "double",
                        "returns the logarithm of the provided argument with the provided base (default e).",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "LOG10",
                        "double",
                        "returns the base-10 logarithm of the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "LOG2",
                        "double",
                        "returns the base-2 logarithm of the provided number.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "LPAD",
                        "string",
                        "returns the string str, left-padded with the string padstr to a length of len characters.",
                        new String[] {"string", "long", "string"}),
                new MongoFunction(
                        "LTRIM",
                        "string",
                        "returns the provided string with leading space characters removed.",
                        new String[] {"string"}),
                new MongoFunction(
                        "MAKEDATE",
                        "date",
                        "returns a date, given year and day-of-year values. day_of_year must be greater than zero, or the result is NULL.",
                        new String[] {"long", "long"}),
                new MongoFunction(
                        "MD5",
                        "string",
                        "returns an MD5 128-bit checksum for the provided string.",
                        new String[] {"string"}),
                new MongoFunction(
                        "MICROSECOND",
                        "long",
                        "returns the microseconds from the provided expression.",
                        new String[] {"date"}),
                new MongoFunction(
                        "MID",
                        "string",
                        "mid(str, pos, len) is a synonym for substring(str, pos, len).",
                        new String[] {"string", "long", "long"}),
                new MongoFunction(
                        "MINUTE",
                        "long",
                        "returns the minute for the provided time.",
                        new String[] {"date"}),
                new MongoFunction(
                        "MOD",
                        "numeric",
                        "returns the remainder of n divided by m.",
                        new String[] {"numeric", "numeric"}),
                new MongoFunction(
                        "MONTH",
                        "long",
                        "returns the month for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "MONTHNAME",
                        "string",
                        "returns the full name of the month for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "NULLIF",
                        null,
                        "returns null if the two arguments are equal, and the first argument otherwise.",
                        new String[] {null, null}),
                new MongoFunction("PI", "double", "returns the value of pi.", new String[] {}),
                new MongoFunction(
                        "POW",
                        "numeric",
                        "returns the value of base raised to the power of exp.",
                        new String[] {"numeric", "numeric"}),
                new MongoFunction(
                        "QUARTER",
                        "long",
                        "returns the quarter of the year for the provided date, from 1 to 4.",
                        new String[] {"date"}),
                new MongoFunction(
                        "RADIANS",
                        "double",
                        "returns the provided degrees argument to radians.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "RAND",
                        "double",
                        "returns a random floating-point value between zero and one, using an integer seed if provided.",
                        new String[] {"long"}),
                new MongoFunction(
                        "REPEAT",
                        "string",
                        "returns a string consisting of the provided string repeated n times. If n is less than 1, returns an empty string. Returns NULL if either argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoFunction(
                        "REPLACE",
                        "string",
                        "returns the string with all occurrences of the string from_str replaced by the string to_str.",
                        new String[] {"string", "string", "string"}),
                new MongoFunction(
                        "REVERSE",
                        "string",
                        "returns the provided string with the order of the characters reversed.",
                        new String[] {"string"}),
                new MongoFunction(
                        "RIGHT",
                        "string",
                        "returns the rightmost n characters from the provided string, or NULL if any argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoFunction(
                        "ROUND",
                        "numeric",
                        "rounds the argument x to d decimal places (or zero places if not specified).",
                        new String[] {"numeric", "long"}),
                new MongoFunction(
                        "RPAD",
                        "string",
                        "returns the string str, right-padded with the string padstr to a length of len characters.",
                        new String[] {"string", "long", "string"}),
                new MongoFunction(
                        "RTRIM",
                        "string",
                        "returns the provided string with trailing space characters removed.",
                        new String[] {"string"}),
                new MongoFunction(
                        "SECOND",
                        "long",
                        "returns the second for the provided time, in the range 0-59.",
                        new String[] {"date"}),
                new MongoFunction(
                        "SIGN",
                        "long",
                        "returns the sign of the argument as -1, 0, or 1 depending on whether it is negative, zero, or positive.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "SIN",
                        "double",
                        "returns the sine of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "SLEEP",
                        "long",
                        "pauses for the number of seconds given by the argument, then returns zero.",
                        new String[] {"decimal"}),
                new MongoFunction(
                        "SPACE",
                        "string",
                        "returns a string consisting of n space characters.",
                        new String[] {"long"}),
                new MongoFunction(
                        "SQRT",
                        "double",
                        "returns the square root of the provided (non-negative) argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "STRTODATE",
                        "date",
                        "takes a string and a date format string, and attempts to parse a date from the string according to the provided format string.",
                        new String[] {"string", "string"}),
                new MongoFunction(
                        "SUBSTRING",
                        "string",
                        "takes a substring from a string",
                        new String[] {"string", "long"}),
                new MongoFunction(
                        "SUBSTRINGINDEX",
                        "string",
                        "returns the substring from string str before count occurrences of the delimiter delim. If count is positive, everything to the left of the final delimiter (counting from the left) is returned. If count is negative, everything to the right of the final delimiter (counting from the right) is returned. SUBSTRING_INDEX() performs a case-sensitive match when searching for delim.",
                        new String[] {"string", "string", "long"}),
                new MongoFunction(
                        "TAN",
                        "double",
                        "returns the tangent of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "TIMEDIFF",
                        "date",
                        "returns expr1 - expr2 expressed as a time value.",
                        new String[] {"date", "date"}),
                new MongoFunction(
                        "TIMETOSEC",
                        "long",
                        "returns the provided time argument, converted to seconds.",
                        new String[] {"date"}),
                new MongoFunction(
                        "TIMESTAMP",
                        "date",
                        "returns the provided argument as a datetime value. With two arguments, adds the provided time expression to the first argument and returns the result as a datetime.",
                        new String[] {"date", "date"}),
                new MongoFunction(
                        "TIMESTAMPADD",
                        "date",
                        "adds the integer expression interval to the date or datetime expression datetime_expr. The unit for interval is given by the unit argument, which should be one of MICROSECOND (microseconds), SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR.",
                        new String[] {"string", "numeric", "date"}),
                new MongoFunction(
                        "TIMESTAMPDIFF",
                        "long",
                        "subtracts the provided timestamps, returning the difference in the specified unit.",
                        new String[] {"string", "date", "date"}),
                new MongoFunction(
                        "TODAYS",
                        "long",
                        "returns the number of days since year zero for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "TOSECONDS",
                        "long",
                        "returns the number of seconds since year zero for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "TRIM",
                        "string",
                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                        new String[] {"string"}),
                new MongoFunction(
                        "TRUNCATE",
                        "numeric",
                        "returns the number x truncated to d decimal places.",
                        new String[] {"numeric", "long"}),
                new MongoFunction(
                        "UCASE",
                        "string",
                        "returns the provided string with all characters changed to uppercase.",
                        new String[] {"string"}),
                new MongoFunction(
                        "UNIXTIMESTAMP",
                        "long",
                        "returns the provided timestamp (defaulting to the current timestamp) as seconds since unix epoch.",
                        new String[] {}),
                new MongoFunction(
                        "USER",
                        "string",
                        "returns the current MySQL user name and host name as a string.",
                        new String[] {}),
                new MongoFunction(
                        "UTCDATE", "date", "returns the current UTC date.", new String[] {}),
                new MongoFunction(
                        "UTCTIMESTAMP",
                        "date",
                        "returns the current UTC date and time.",
                        new String[] {}),
                new MongoFunction(
                        "VERSION",
                        "string",
                        "returns a string that indicates the MySQL server version.",
                        new String[] {}),
                new MongoFunction(
                        "WEEK", "long", "returns the week number for date.", new String[] {"date"}),
                new MongoFunction(
                        "WEEKDAY",
                        "long",
                        "returns the weekday index (Monday = 0 ... Sunday = 6) for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "YEAR",
                        "long",
                        "returns the year for the provided date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "YEARWEEK",
                        "long",
                        "returns the year and week for a date.",
                        new String[] {"date"}),
                new MongoFunction(
                        "AVG",
                        "numeric",
                        "returns the average of elements in a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "COUNT",
                        "numeric",
                        "returns the count of elements in a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "SUM",
                        "numeric",
                        "returns the sum of elements in a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "MIN",
                        "numeric",
                        "returns the minimum element of elements in a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "MAX",
                        "numeric",
                        "returns the maximum element of elements in a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "GROUP_CONCAT",
                        "string",
                        "returns the concatenation of strings from a group into a single string with various options.",
                        new String[] {"string"}),
                new MongoFunction(
                        "STD",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "STDDEV_POP",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "STDDEV",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[] {"numeric"}),
                new MongoFunction(
                        "STDDEV_SAMP",
                        "numeric",
                        "returns cumulative sample standard deviation of a group.",
                        new String[] {"numeric"})
            };

    public static final String[] mySQLFunctionNames;
    public static final String mySQLNumericFunctionsString;
    public static final String mySQLStringFunctionsString;
    public static final String mySQLDateFunctionsString;

    public static final MongoFunction[] mongoSQLFunctions =
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
                            new String[] {"string"}),
            };

    public static final String[] mongoSQLFunctionNames;
    public static final String mongoSQLNumericFunctionsString;
    public static final String mongoSQLStringFunctionsString;
    public static final String mongoSQLDateFunctionsString;

    private static final String NUMERIC_FUNCTIONS_KEY = "numeric";
    private static final String STRING_FUNCTIONS_KEY = "string";
    private static final String DATE_FUNCTIONS_KEY = "date";

    static {
        // MySQL
        Pair<String[], HashMap<String, String>> mySQLFunctionMetadata = buildFunctionMetadata(MongoFunction.mySQLFunctions);
        HashMap<String, String> m = mySQLFunctionMetadata.getSecond();

        mySQLFunctionNames = mySQLFunctionMetadata.getFirst();
        mySQLNumericFunctionsString = m.get(NUMERIC_FUNCTIONS_KEY);
        mySQLStringFunctionsString = m.get(STRING_FUNCTIONS_KEY);
        mySQLDateFunctionsString = m.get(DATE_FUNCTIONS_KEY);

        // MongoSQL
        Pair<String[], HashMap<String, String>> mongoSQLFunctionMetadata = buildFunctionMetadata(MongoFunction.mongoSQLFunctions);
        m = mongoSQLFunctionMetadata.getSecond();

        mongoSQLFunctionNames = mongoSQLFunctionMetadata.getFirst();
        mongoSQLNumericFunctionsString = m.get(NUMERIC_FUNCTIONS_KEY);
        mongoSQLStringFunctionsString = m.get(STRING_FUNCTIONS_KEY);
        mongoSQLDateFunctionsString = m.get(DATE_FUNCTIONS_KEY);
    }

    private static Pair<String[], HashMap<String, String>> buildFunctionMetadata(MongoFunction[] functions) {
        String[] functionNames = new String[functions.length];
        for (int i = 0; i < functionNames.length; ++i) {
            functionNames[i] = functions[i].name;
        }

        LinkedHashSet<String> numericFunctionSet = new LinkedHashSet<>(functionNames.length);
        LinkedHashSet<String> stringFunctionSet = new LinkedHashSet<>(functionNames.length);
        LinkedHashSet<String> dateFunctionSet = new LinkedHashSet<>(functionNames.length);
        for (int i = 0; i < functions.length; ++i) {
            for (String argType : functions[i].argTypes) {
                String name = functions[i].name;
                if (argType == null) {
                    continue;
                }
                switch (argType) {
                    case "string":
                        if (stringFunctionSet.contains(name)) {
                            break;
                        }
                        stringFunctionSet.add(name);
                        break;
                    case "numeric":
                    case "long":
                    case "int":
                    case "double":
                    case "decimal":
                        if (numericFunctionSet.contains(name)) {
                            break;
                        }
                        numericFunctionSet.add(name);
                        break;
                    case "date":
                        if (dateFunctionSet.contains(name)) {
                            break;
                        }
                        dateFunctionSet.add(name);
                        break;
                }
            }
        }

        HashMap<String, String> m = new HashMap<>();

        m.put(NUMERIC_FUNCTIONS_KEY, String.join(",", numericFunctionSet));
        m.put(STRING_FUNCTIONS_KEY, String.join(",", stringFunctionSet));
        m.put(DATE_FUNCTIONS_KEY, String.join(",", dateFunctionSet));

        return new Pair<>(functionNames, m);
    }

    private static class Pair<X, Y> {
        private final X x;
        private final Y y;

        public Pair(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public X getFirst() {
            return this.x;
        }

        public Y getSecond() {
            return this.y;
        }
    }

}
