// This is generated code. To regenerate go to the resources directory:
//     $ cd adl-jdbc-driver/resources
// and run:
//     $ make
package com.mongodb.jdbc;

public class MySQLMongoFunctions extends MongoFunctions {

    private static MySQLMongoFunctions instance;

    private MySQLMongoFunctions(MongoFunction[] functions) {
        super(functions);
    }

    public static MySQLMongoFunctions getInstance()
    {
        if (null == instance)
        {
            instance = new MySQLMongoFunctions(
                new MongoFunction[]{
                    new MongoFunction(
                        "ABS",
                        "numeric",
                        "returns the absolute value of the provided number.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC
                ),
                    new MongoFunction(
                        "ACOS",
                        "double",
                        "returns the arc cosine of the provided number.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "ASCII",
                        "long",
                        "returns the numeric value of the leftmost character of the provided string. Returns 0 if the empty string is provided, and NULL if NULL is provided.",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "ASIN",
                        "double",
                        "returns the arc sine of the provided number.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "ATAN",
                        "double",
                        "returns the arc tangent of the number provided.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "ATAN",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[]{"numeric, numeric"},
                        FunctionCategory.NUM_FUNC),
                /**
                 * Note
                 * ATAN2(number) is supported by the driver but not listed as a supported Mysql function
                 * https://dev.mysql.com/doc/refman/8.0/en/numeric-functions.html. So we are not listing it here.
                */
                    new MongoFunction(
                        "ATAN2",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[]{"numeric", "numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "CEIL",
                        "long",
                        "returns the smallest integer not less than the provided number.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                /**
                 * Note
                 * CHAR(code1, code2, ...) is a valid MySQL function
                 * https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char
                 * but we only support CHAR(code)
                */
                    new MongoFunction(
                        "CHAR",
                        "string",
                        "interprets each argument as an integer and returns a string consisting of the characters given by the code values of those integers. NULL values are skipped.",
                        new String[]{"long"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "CHARACTERLENGTH",
                        "long",
                        "returns length of string",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "COALESCE",
                        null,
                        "returns the first non-null value in the list, or null if there are no non-null values.",
                        new String[]{null}),
                    new MongoFunction(
                        "CONCAT",
                        "string",
                        "returns the string that results from concatenating the arguments.",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "CONCATWS",
                        "string",
                        "returns the provided strings concatenated and separated by the provided separator.",
                        new String[]{"string", "string"}),
                    new MongoFunction(
                        "CONNECTIONID", "long", "returns the connection id.", new String[]{}),
                    new MongoFunction(
                        "CONV",
                        "string",
                        "converts a number from one numeric base system to another, and returns the result as a string value.",
                        new String[]{"string", "long", "long"}),
                    new MongoFunction(
                        "CONVERT",
                        null,
                        "converts the provided expression into a value of the specified type.",
                        new String[]{null, "string"}),
                    new MongoFunction(
                        "COS",
                        "double",
                        "returns the cosine of the provided radians argument.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "COT",
                        "double",
                        "returns the cotangent of x.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "CURRENT_DATE",
                        "date",
                        "returns the current date.",
                        new String[]{},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "CURRENT_TIMESTAMP",
                        "date",
                        "returns the current date and time.",
                        new String[]{},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "CURTIME",
                        "date",
                        "returns the current time.",
                        new String[]{},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "DATABASE",
                        "string",
                        "returns the current database name as a string.",
                        new String[]{},
                        FunctionCategory.SYSTEM_FUNC),
                    new MongoFunction(
                        "DATE",
                        "date",
                        "extracts the date part of the provided date or datetime expression.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "DATEADD",
                        "date",
                        "adds a specified number of datetime units to the provided datetime value.",
                        new String[]{"date", "string", "string"}),
                    new MongoFunction(
                        "DATEDIFF",
                        "long",
                        "returns date1 - date2 expressed as a value in days.",
                        new String[]{"date", "date"}),
                    new MongoFunction(
                        "DATEFORMAT",
                        "string",
                        "formats a datetime value according to the provided format string.",
                        new String[]{"date", "string"}),
                    new MongoFunction(
                        "DATESUB",
                        "date",
                        "subtracts a specified number of datetime units from the provided datetime value.",
                        new String[]{"date", "string", "string"}),
                    new MongoFunction(
                        "DAYNAME",
                        "string",
                        "returns the name of the weekday for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "DAYOFMONTH",
                        "int",
                        "returns the day of the month for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "DAYOFWEEK",
                        "int",
                        "returns the weekday index (Sunday = 1 ... Saturday = 7) for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "DAYOFYEAR",
                        "int",
                        "returns the day of the year for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "DEGREES",
                        "double",
                        "converts the provided radians argument to degrees.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "ELT",
                        "string",
                        "returns the Nth element of the list of strings.",
                        new String[]{"long", "string"}),
                    new MongoFunction(
                        "EXP",
                        "double",
                        "returns the value of e raised to the provided power.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                /**
                 * Note
                 * EXTRACT supports more than YEAR, MONTH, DAY, HOUR, MINUTE, SECOND for the unit.
                */
                    new MongoFunction(
                        "EXTRACT",
                        "long",
                        "returns the value of the specified unit from the provided date.",
                        new String[]{"string", "date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "FIELD",
                        "long",
                        "returns the index of the first argument in the list of the remaining arguments.",
                        new String[]{null}),
                    new MongoFunction(
                        "FLOOR",
                        "long",
                        "returns the largest integer value not greater than the provided argument.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "FROM_DAYS",
                        "date",
                        "returns a date, given a day number.",
                        new String[]{"long"}),
                    new MongoFunction(
                        "FROM_UNIXTIME",
                        "date",
                        "returns a representation of the provided unix timestamp, using the provided format string if one is supplied.",
                        new String[]{"long"}),
                    new MongoFunction(
                        "GREATEST",
                        null,
                        "With two or more arguments, returns the largest (maximum-valued) argument from the provided list.",
                        new String[]{null, null}),
                /**
                 * Note
                 * Even though the function works on DateTime values, MySql list the argument as Time
                 * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_hour
                 * It seems safe to add the function to the list of JDBC Date and Time functions supported.
                */
                    new MongoFunction(
                        "HOUR",
                        "long",
                        "returns the hour for the provided time.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "IF",
                        null,
                        "returns the second argument if the first is true, and the third argument otherwise.",
                        new String[]{null, null, null}),
                    new MongoFunction(
                        "IFNULL",
                        null,
                        "returns the first argument if it is not null, and the second argument otherwise.",
                        new String[]{null, null}),
                    new MongoFunction(
                        "INSERT",
                        "string",
                        "returns the string str, with the substring beginning at position pos and len characters long replaced by the string newstr.",
                        new String[]{"string", "long", "long", "string"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "INSTR",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str.",
                        new String[]{"string", "string"}),
                    new MongoFunction(
                        "INTERVAL",
                        "numeric",
                        "returns 0 if N < N1, 1 if N < N2, and so on.",
                        new String[]{"numeric", "numeric"}),
                    new MongoFunction(
                        "LASTDAY",
                        "date",
                        "takes a date or datetime value and returns the corresponding value for the last day of the month.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "LCASE",
                        "string",
                        "returns the provided string with all characters changed to lowercase.",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "LEAST",
                        null,
                        "With two or more arguments, returns the smallest (minimum-valued) argument from the provided list.",
                        new String[]{null, null}),
                    new MongoFunction(
                        "LEFT",
                        "string",
                        "returns the leftmost n characters from the provided string, or NULL if either argument is NULL.",
                        new String[]{"string", "long"},
                        FunctionCategory.STRING_FUNC),
                /**
                 * Note
                 * This is equivalent to LENGTH(string, OCTETS) in the String Functions from the JDBC specification.
                 * Because default for JDBC is Character, the function will not be listed via getStringFunctions.
                */
                    new MongoFunction(
                        "LENGTH",
                        "string",
                        "return the length of the provided string, measured in bytes.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "LN",
                        "double",
                        "returns the natural logarithm of the provided argument.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "LOCATE",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str, starting at index pos if provided",
                        new String[]{"string", "string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "LOG",
                        "double",
                        "returns the logarithm of the provided argument with the provided base (default e).",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "LOG10",
                        "double",
                        "returns the base-10 logarithm of the provided number.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "LOG2",
                        "double",
                        "returns the base-2 logarithm of the provided number.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "LPAD",
                        "string",
                        "returns the string str, left-padded with the string padstr to a length of len characters.",
                        new String[]{"string", "long", "string"}),
                    new MongoFunction(
                        "LTRIM",
                        "string",
                        "returns the provided string with leading space characters removed.",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "MAKEDATE",
                        "date",
                        "returns a date, given year and day-of-year values. day_of_year must be greater than zero, or the result is NULL.",
                        new String[]{"long", "long"}),
                    new MongoFunction(
                        "MD5",
                        "string",
                        "returns an MD5 128-bit checksum for the provided string.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "MICROSECOND",
                        "long",
                        "returns the microseconds from the provided expression.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "MID",
                        "string",
                        "mid(str, pos, len) is a synonym for substring(str, pos, len).",
                        new String[]{"string", "long", "long"}),
                    /**
                     * Note
                     * Even though the function works on DateTime values, MySql list the argument as Time
                     * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_minute
                     * It seems safe to add the function to the list of JDBC Date and Time functions supported.
                    */
                    new MongoFunction(
                        "MINUTE",
                        "int",
                        "returns the minute for the provided time.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "MOD",
                        "numeric",
                        "returns the remainder of n divided by m.",
                        new String[]{"numeric", "numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "MONTH",
                        "int",
                        "returns the month for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "MONTHNAME",
                        "string",
                        "returns the full name of the month for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "NULLIF",
                        null,
                        "returns null if the two arguments are equal, and the first argument otherwise.",
                        new String[]{null, null}),
                    new MongoFunction(
                        "PI",
                        "double",
                        "returns the value of pi.",
                        new String[]{},
                        FunctionCategory.NUM_FUNC),
                    /**
                     * Note
                     * This will not be reported as a Numeric function because the Open Group CLI name (used by JDBC) is
                     * POWER.
                    */
                    new MongoFunction(
                        "POW",
                        "numeric",
                        "returns the value of base raised to the power of exp.",
                        new String[]{"numeric", "numeric"}),
                    new MongoFunction(
                        "QUARTER",
                        "int",
                        "returns the quarter of the year for the provided date, from 1 to 4.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "RADIANS",
                        "double",
                        "returns the provided degrees argument to radians.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "RAND",
                        "double",
                        "returns a random floating-point value between zero and one, using an integer seed if provided.",
                        new String[]{"long"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "REPEAT",
                        "string",
                        "returns a string consisting of the provided string repeated n times. If n is less than 1, returns an empty string. Returns NULL if either argument is NULL.",
                        new String[]{"string", "long"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "REPLACE",
                        "string",
                        "returns the string with all occurrences of the string from_str replaced by the string to_str.",
                        new String[]{"string", "string", "string"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "REVERSE",
                        "string",
                        "returns the provided string with the order of the characters reversed.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "RIGHT",
                        "string",
                        "returns the rightmost n characters from the provided string, or NULL if any argument is NULL.",
                        new String[]{"string", "long"}),
                    new MongoFunction(
                        "ROUND",
                        "numeric",
                        "rounds the argument x to d decimal places (or zero places if not specified).",
                        new String[]{"numeric", "long"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "RPAD",
                        "string",
                        "returns the string str, right-padded with the string padstr to a length of len characters.",
                        new String[]{"string", "long", "string"}),
                    new MongoFunction(
                        "RTRIM",
                        "string",
                        "returns the provided string with trailing space characters removed.",
                        new String[]{"string"},
                        FunctionCategory.STRING_FUNC),
                    /**
                     * Note
                     * Even though the function works on DateTime values, MySql list the argument as Time
                     * https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_second
                     * It seems safe to add the function to the list of JDBC Date and Time functions supported.
                    */
                    new MongoFunction(
                        "SECOND",
                        "long",
                        "returns the second for the provided time, in the range 0-59.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "SIGN",
                        "long",
                        "returns the sign of the argument as -1, 0, or 1 depending on whether it is negative, zero, or positive.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "SIN",
                        "double",
                        "returns the sine of the provided radians argument.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "SLEEP",
                        "long",
                        "pauses for the number of seconds given by the argument, then returns zero.",
                        new String[]{"decimal"}),
                    new MongoFunction(
                        "SPACE",
                        "string",
                        "returns a string consisting of n space characters.",
                        new String[]{"long"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "SQRT",
                        "double",
                        "returns the square root of the provided (non-negative) argument.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "STRTODATE",
                        "date",
                        "takes a string and a date format string, and attempts to parse a date from the string according to the provided format string.",
                        new String[]{"string", "string"}),
                    new MongoFunction(
                        "SUBSTRING",
                        "string",
                        "takes a substring from a string starting at the given position",
                        new String[]{"string", "long"}),
                    new MongoFunction(
                        "SUBSTRING",
                        "string",
                        "takes a substring from a string starting at the given position and with the given lenght",
                        new String[]{"string", "long", "long"},
                        FunctionCategory.STRING_FUNC),
                    new MongoFunction(
                        "SUBSTRINGINDEX",
                        "string",
                        "returns the substring from string str before count occurrences of the delimiter delim. If count is positive, everything to the left of the final delimiter (counting from the left) is returned. If count is negative, everything to the right of the final delimiter (counting from the right) is returned. SUBSTRING_INDEX() performs a case-sensitive match when searching for delim.",
                        new String[]{"string", "string", "long"}),
                    new MongoFunction(
                        "TAN",
                        "double",
                        "returns the tangent of the provided radians argument.",
                        new String[]{"numeric"},
                        FunctionCategory.NUM_FUNC),
                    new MongoFunction(
                        "TIMEDIFF",
                        "date",
                        "returns expr1 - expr2 expressed as a time value.",
                        new String[]{"date", "date"}),
                    new MongoFunction(
                        "TIMETOSEC",
                        "long",
                        "returns the provided time argument, converted to seconds.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "TIMESTAMP",
                        "date",
                        "returns the provided argument as a datetime value. With two arguments, adds the provided time expression to the first argument and returns the result as a datetime.",
                        new String[]{"date", "date"}),
                    /**
                     * Note
                     * The function supports the Open CLI interval names SQL_TSI_XX but also the shorter Mysql names
                     * omitting the SQL_TSI_ prefix.
                    */
                    new MongoFunction(
                        "TIMESTAMPADD",
                        "date",
                        "adds the integer expression interval to the date or datetime expression datetime_expr. The unit for interval is given by the unit argument, which should be one of MICROSECOND (microseconds), SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR.",
                        new String[]{"string", "numeric", "date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    /**
                     * Note
                     * The function supports the Open CLI interval names SQL_TSI_XX but also the shorter Mysql names
                     * omitting the SQL_TSI_ prefix.
                    */
                    new MongoFunction(
                        "TIMESTAMPDIFF",
                        "long",
                        "subtracts the provided timestamps, returning the difference in the specified unit.",
                        new String[]{"string", "date", "date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "TODAYS",
                        "long",
                        "returns the number of days since year zero for the provided date.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "TOSECONDS",
                        "long",
                        "returns the number of seconds since year zero for the provided date.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "TRIM",
                        "string",
                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "TRUNCATE",
                        "numeric",
                        "returns the number x truncated to d decimal places.",
                        new String[]{"numeric", "long"}),
                    new MongoFunction(
                        "UCASE",
                        "string",
                        "returns the provided string with all characters changed to uppercase.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "UNIX_TIMESTAMP",
                        "long",
                        "returns the provided timestamp (defaulting to the current timestamp) as seconds since unix epoch.",
                        new String[]{}),
                    new MongoFunction(
                        "USER",
                        "string",
                        "returns the current MySQL user name and host name as a string.",
                        new String[]{},
                        FunctionCategory.SYSTEM_FUNC),
                    new MongoFunction(
                        "UTCDATE",
                        "date",
                        "returns the current UTC date.",
                        new String[]{}),
                    new MongoFunction(
                        "UTCTIMESTAMP",
                        "date",
                        "returns the current UTC date and time.",
                        new String[]{}),
                    new MongoFunction(
                        "VERSION",
                        "string",
                        "returns a string that indicates the MySQL server version.",
                        new String[]{}),
                    new MongoFunction(
                        "WEEK",
                        "int",
                        "returns the week number for date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "WEEKDAY",
                        "int",
                        "returns the weekday index (Monday = 0 ... Sunday = 6) for the provided date.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "YEAR",
                        "long",
                        "returns the year for the provided date.",
                        new String[]{"date"},
                        FunctionCategory.TIME_DATE_FUNC),
                    new MongoFunction(
                        "YEARWEEK",
                        "long",
                        "returns the year and week for a date.",
                        new String[]{"date"}),
                    new MongoFunction(
                        "AVG",
                        "numeric",
                        "returns the average of elements in a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "COUNT",
                        "numeric",
                        "returns the count of elements in a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "SUM",
                        "numeric",
                        "returns the sum of elements in a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "MIN",
                        "numeric",
                        "returns the minimum element of elements in a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "MAX",
                        "numeric",
                        "returns the maximum element of elements in a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "GROUP_CONCAT",
                        "string",
                        "returns the concatenation of strings from a group into a single string with various options.",
                        new String[]{"string"}),
                    new MongoFunction(
                        "STD",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "STDDEV_POP",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "STDDEV",
                        "numeric",
                        "returns population standard deviation of a group.",
                        new String[]{"numeric"}),
                    new MongoFunction(
                        "STDDEV_SAMP",
                        "numeric",
                        "returns cumulative sample standard deviation of a group.",
                        new String[]{"numeric"})
                }
            );
        }

        return instance;
    }
}
