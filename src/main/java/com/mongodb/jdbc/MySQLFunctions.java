package com.mongodb.jdbc;

public class MySQLFunctions extends MongoFunctions {

    private static final String ATAN = "ATAN";

    private static MySQLFunctions instance;

    private MySQLFunctions(MongoFunction[] functions) {
        super(functions);
    }

    public static MySQLFunctions getInstance() {
        if (null == instance) {
            instance =
                    new MySQLFunctions(
                            new MongoFunction[] {
                                new MongoFunction(
                                        "ABS",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the absolute value of the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "ACOS",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the arc cosine of the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "ASCII",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the numeric value of the leftmost character of the provided string. Returns 0 if the empty string is provided, and NULL if NULL is provided.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "ASIN",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the arc sine of the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ATAN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the arc tangent of the number provided.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ATAN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the arc tangent of the number(s) provided.",
                                        new String[] {"numeric, numeric"},
                                        FunctionCategory.NUM_FUNC),
                                /**
                                 * Note ATAN2(number) is supported by the driver but not listed as a
                                 * supported Mysql function
                                 * https://dev.mysql.com/doc/refman/8.0/en/numeric-functions.html.
                                 * So we are not listing it here.
                                 */
                                new MongoFunction(
                                        "ATAN2",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the arc tangent of the number(s) provided.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "CEIL",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the smallest integer not less than the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                /**
                                 * Note CHAR(code1, code2, ...) is a valid MySQL function
                                 * https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char
                                 * but we only support CHAR(code)
                                 */
                                new MongoFunction(
                                        "CHAR",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "interprets each argument as an integer and returns a string consisting of the characters given by the code values of those integers. NULL values are skipped.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "CHARACTER_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        COALESCE,
                                        null,
                                        "returns the first non-null value in the list, or null if there are no non-null values.",
                                        new String[] {null}),
                                new MongoFunction(
                                        "CONCAT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string that results from concatenating the arguments.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "CONCATWS",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided strings concatenated and separated by the provided separator.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "CONNECTIONID",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the connection id.",
                                        new String[] {}),
                                new MongoFunction(
                                        "CONV",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "converts a number from one numeric base system to another, and returns the result as a string value.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        "CONVERT",
                                        null,
                                        "converts the provided expression into a value of the specified type.",
                                        new String[] {
                                            null, BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "COS",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the cosine of the provided radians argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "COT",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the cotangent of x.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "CURRENT_DATE",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current date.",
                                        new String[] {},
                                        FunctionCategory.TIME_DATE_FUNC),
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
                                        new String[] {"int"}),
                                new MongoFunction(
                                        "CURTIME",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current time.",
                                        new String[] {},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "DATABASE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the current database name as a string.",
                                        new String[] {},
                                        FunctionCategory.SYSTEM_FUNC),
                                new MongoFunction(
                                        "DATE",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "extracts the date part of the provided date or datetime expression.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "DATEADD",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "adds a specified number of datetime units to the provided datetime value.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "DATEDIFF",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns date1 - date2 expressed as a value in days.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        "DATEFORMAT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "formats a datetime value according to the provided format string.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "DATESUB",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "subtracts a specified number of datetime units from the provided datetime value.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "DAYNAME",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the name of the weekday for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "DAYOFMONTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the day of the month for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "DAYOFWEEK",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the weekday index (Sunday = 1 ... Saturday = 7) for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "DAYOFYEAR",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the day of the year for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "DEGREES",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "converts the provided radians argument to degrees.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "ELT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the Nth element of the list of strings.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "EXP",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the value of e raised to the provided power.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                /**
                                 * Note EXTRACT supports more than YEAR, MONTH, DAY, HOUR, MINUTE,
                                 * SECOND for the unit.
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
                                        "FIELD",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the index of the first argument in the list of the remaining arguments.",
                                        new String[] {null}),
                                new MongoFunction(
                                        "FLOOR",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the largest integer value not greater than the provided argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "FROM_DAYS",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns a date, given a day number.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()}),
                                new MongoFunction(
                                        "FROM_UNIXTIME",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns a representation of the provided unix timestamp, using the provided format string if one is supplied.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()}),
                                new MongoFunction(
                                        "GREATEST",
                                        null,
                                        "With two or more arguments, returns the largest (maximum-valued) argument from the provided list.",
                                        new String[] {null, null}),
                                /**
                                 * Note Even though the function works on DateTime values, MySql
                                 * list the argument as Time
                                 * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_hour
                                 * It seems safe to add the function to the list of JDBC Date and
                                 * Time functions supported.
                                 */
                                new MongoFunction(
                                        "HOUR",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the hour for the provided time.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
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
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str, with the substring beginning at position pos and len characters long replaced by the string newstr.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        },
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "INSTR",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the position of the first occurrence of substring substr in string str.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "INTERVAL",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns 0 if N < N1, 1 if N < N2, and so on.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        "LASTDAY",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "takes a date or datetime value and returns the corresponding value for the last day of the month.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "LCASE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with all characters changed to lowercase.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "LEAST",
                                        null,
                                        "With two or more arguments, returns the smallest (minimum-valued) argument from the provided list.",
                                        new String[] {null, null}),
                                new MongoFunction(
                                        "LEFT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the leftmost n characters from the provided string, or NULL if either argument is NULL.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                /**
                                 * Note This is equivalent to LENGTH(string, OCTETS) in the String
                                 * Functions from the JDBC specification. Because default for JDBC
                                 * is Character, the function will not be listed via
                                 * getStringFunctions.
                                 */
                                new MongoFunction(
                                        "LENGTH",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "return the length of the provided string, measured in bytes.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "LN",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the natural logarithm of the provided argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "LOCATE",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the position of the first occurrence of substring substr in string str, starting at index pos if provided",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "LOG",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of the provided argument with the provided base (default e).",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "LOG10",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the base-10 logarithm of the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "LOG2",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the base-2 logarithm of the provided number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "LPAD",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str, left-padded with the string padstr to a length of len characters.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "LTRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with leading space characters removed.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "MAKEDATE",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns a date, given year and day-of-year values. day_of_year must be greater than zero, or the result is NULL.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        "MD5",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns an MD5 128-bit checksum for the provided string.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "MICROSECOND",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the microseconds from the provided expression.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "MID",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "mid(str, pos, len) is a synonym for substring(str, pos, len).",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                /**
                                 * Note Even though the function works on DateTime values, MySql
                                 * list the argument as Time
                                 * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_minute
                                 * It seems safe to add the function to the list of JDBC Date and
                                 * Time functions supported.
                                 */
                                new MongoFunction(
                                        "MINUTE",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the minute for the provided time.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "MOD",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the remainder of n divided by m.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "MONTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the month for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "MONTHNAME",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the full name of the month for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        NULLIF,
                                        null,
                                        "returns null if the two arguments are equal, and the first argument otherwise.",
                                        new String[] {null, null}),
                                new MongoFunction(
                                        "PI",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the value of pi.",
                                        new String[] {},
                                        FunctionCategory.NUM_FUNC),
                                /**
                                 * Note This will not be reported as a Numeric function because the
                                 * Open Group CLI name (used by JDBC) is POWER.
                                 */
                                new MongoFunction(
                                        "POW",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the value of base raised to the power of exp.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        "QUARTER",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the quarter of the year for the provided date, from 1 to 4.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "RADIANS",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the provided degrees argument to radians.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "RAND",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns a random floating-point value between zero and one, using an integer seed if provided.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "REPEAT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns a string consisting of the provided string repeated n times. If n is less than 1, returns an empty string. Returns NULL if either argument is NULL.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "REPLACE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string with all occurrences of the string from_str replaced by the string to_str.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "REVERSE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with the order of the characters reversed.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "RIGHT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the rightmost n characters from the provided string, or NULL if any argument is NULL.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        "ROUND",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "rounds the argument x to d decimal places (or zero places if not specified).",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "RPAD",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str, right-padded with the string padstr to a length of len characters.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        "RTRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with trailing space characters removed.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                /**
                                 * Note Even though the function works on DateTime values, MySql
                                 * list the argument as Time
                                 * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_second
                                 * It seems safe to add the function to the list of JDBC Date and
                                 * Time functions supported.
                                 */
                                new MongoFunction(
                                        "SECOND",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the second for the provided time, in the range 0-59.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "SIGN",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the sign of the argument as -1, 0, or 1 depending on whether it is negative, zero, or positive.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "SIN",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the sine of the provided radians argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "SLEEP",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "pauses for the number of seconds given by the argument, then returns zero.",
                                        new String[] {"decimal"}),
                                new MongoFunction(
                                        "SPACE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns a string consisting of n space characters.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "SQRT",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the square root of the provided (non-negative) argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "STRTODATE",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "takes a string and a date format string, and attempts to parse a date from the string according to the provided format string.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        SUBSTRING,
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "takes a substring from a string starting at the given position",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        SUBSTRING,
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "takes a substring from a string starting at the given position and with the given lenght",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        "SUBSTRINGINDEX",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the substring from string str before count occurrences of the delimiter delim. If count is positive, everything to the left of the final delimiter (counting from the left) is returned. If count is negative, everything to the right of the final delimiter (counting from the right) is returned. SUBSTRING_INDEX() performs a case-sensitive match when searching for delim.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        "TAN",
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the tangent of the provided radians argument.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "TIMEDIFF",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns expr1 - expr2 expressed as a time value.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        "TIMETOSEC",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the provided time argument, converted to seconds.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "TIMESTAMP",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the provided argument as a datetime value. With two arguments, adds the provided time expression to the first argument and returns the result as a datetime.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                /**
                                 * Note The function supports the Open CLI interval names SQL_TSI_XX
                                 * but also the shorter Mysql names omitting the SQL_TSI_ prefix.
                                 */
                                new MongoFunction(
                                        "TIMESTAMPADD",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "adds the integer expression interval to the date or datetime expression datetime_expr. The unit for interval is given by the unit argument, which should be one of MICROSECOND (microseconds), SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        },
                                        FunctionCategory.TIME_DATE_FUNC),
                                /**
                                 * Note The function supports the Open CLI interval names SQL_TSI_XX
                                 * but also the shorter Mysql names omitting the SQL_TSI_ prefix.
                                 */
                                new MongoFunction(
                                        "TIMESTAMPDIFF",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "subtracts the provided timestamps, returning the difference in the specified unit.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        },
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "TODAYS",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number of days since year zero for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "TOSECONDS",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number of seconds since year zero for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "TRIM",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "TRUNCATE",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number x truncated to d decimal places.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        "UCASE",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with all characters changed to uppercase.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "UNIX_TIMESTAMP",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the provided timestamp (defaulting to the current timestamp) as seconds since unix epoch.",
                                        new String[] {}),
                                new MongoFunction(
                                        "USER",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the current MySQL user name and host name as a string.",
                                        new String[] {},
                                        FunctionCategory.SYSTEM_FUNC),
                                new MongoFunction(
                                        "UTCDATE",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current UTC date.",
                                        new String[] {}),
                                new MongoFunction(
                                        "UTCTIMESTAMP",
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the current UTC date and time.",
                                        new String[] {}),
                                new MongoFunction(
                                        "VERSION",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns a string that indicates the MySQL server version.",
                                        new String[] {}),
                                new MongoFunction(
                                        "WEEK",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the week number for date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "WEEKDAY",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the weekday index (Monday = 0 ... Sunday = 6) for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "YEAR",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the year for the provided date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()},
                                        FunctionCategory.TIME_DATE_FUNC),
                                new MongoFunction(
                                        "YEARWEEK",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the year and week for a date.",
                                        new String[] {BsonTypeInfo.BSON_DATE.getBsonName()}),
                                new MongoFunction(
                                        "AVG",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the average of elements in a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "COUNT",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the count of elements in a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "SUM",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the sum of elements in a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "MIN",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the minimum element of elements in a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "MAX",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the maximum element of elements in a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "GROUP_CONCAT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the concatenation of strings from a group into a single string with various options.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        "STD",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns population standard deviation of a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "STDDEV_POP",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns population standard deviation of a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "STDDEV",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns population standard deviation of a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "STDDEV_SAMP",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns cumulative sample standard deviation of a group.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()})
                            });
        }

        return instance;
    }
}
