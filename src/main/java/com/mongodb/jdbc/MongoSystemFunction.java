package com.mongodb.jdbc;

public class MongoSystemFunction {
    public String name;
    public String returnType;
    public String comment;
    public String[] argTypes;

    public MongoSystemFunction(String name, String returnType, String comment, String[] argTypes) {
        this.name = name;
        this.returnType = returnType;
        this.comment = comment;
        this.argTypes = argTypes;
    }

    public static final MongoSystemFunction[] systemFunctions =
            new MongoSystemFunction[] {
                new MongoSystemFunction(
                        "ABS",
                        "numeric",
                        "returns the absolute value of the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "ACOS",
                        "double",
                        "returns the arc cosine of the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "ASCII",
                        "long",
                        "returns the numeric value of the leftmost character of the provided string. Returns 0 if the empty string is provided, and NULL if NULL is provided.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "ASIN",
                        "double",
                        "returns the arc sine of the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "ATAN",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "ATAN2",
                        "double",
                        "returns the arc tangent of the number(s) provided.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "CEIL",
                        "long",
                        "returns the smallest integer not less than the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "CHAR",
                        "string",
                        "interprets each argument as an integer and returns a string consisting of the characters given by the code values of those integers. NULL values are skipped.",
                        new String[] {"long"}),
                new MongoSystemFunction(
                        "CHARACTERLENGTH",
                        "long",
                        "returns length of string",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "COALESCE",
                        null,
                        "returns the first non-null value in the list, or null if there are no non-null values.",
                        new String[] {null}),
                new MongoSystemFunction(
                        "CONCAT",
                        "string",
                        "returns the string that results from concatenating the arguments.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "CONCATWS",
                        "string",
                        "returns the provided strings concatenated and separated by the provided separator.",
                        new String[] {"string", "string"}),
                new MongoSystemFunction(
                        "CONNECTIONID", "long", "returns the connection id.", new String[] {}),
                new MongoSystemFunction(
                        "CONV",
                        "string",
                        "converts a number from one numeric base system to another, and returns the result as a string value.",
                        new String[] {"string", "long", "long"}),
                new MongoSystemFunction(
                        "CONVERT",
                        null,
                        "converts the provided expression into a value of the specified type.",
                        new String[] {null, "string"}),
                new MongoSystemFunction(
                        "COS",
                        "double",
                        "returns the cosine of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "COT", "double", "returns the cotangent of x.", new String[] {"numeric"}),
                new MongoSystemFunction(
                        "CURRENTDATE", "date", "returns the current date.", new String[] {}),
                new MongoSystemFunction(
                        "CURRENTTIMESTAMP",
                        "date",
                        "returns the current date and time.",
                        new String[] {}),
                new MongoSystemFunction(
                        "CURTIME", "date", "returns the current time.", new String[] {}),
                new MongoSystemFunction(
                        "DATABASE",
                        "string",
                        "returns the current database name as a string.",
                        new String[] {}),
                new MongoSystemFunction(
                        "DATE",
                        "date",
                        "extracts the date part of the provided date or datetime expression.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "DATEADD",
                        "date",
                        "adds a specified number of datetime units to the provided datetime value.",
                        new String[] {"date", "string", "string"}),
                new MongoSystemFunction(
                        "DATEDIFF",
                        "long",
                        "returns date1 - date2 expressed as a value in days.",
                        new String[] {"date", "date"}),
                new MongoSystemFunction(
                        "DATEFORMAT",
                        "string",
                        "formats a datetime value according to the provided format string.",
                        new String[] {"date", "string"}),
                new MongoSystemFunction(
                        "DATESUB",
                        "date",
                        "subtracts a specified number of datetime units from the provided datetime value.",
                        new String[] {"date", "string", "string"}),
                new MongoSystemFunction(
                        "DAYNAME",
                        "string",
                        "returns the name of the weekday for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "DAYOFMONTH",
                        "long",
                        "returns the day of the month for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "DAYOFWEEK",
                        "long",
                        "returns the weekday index (Sunday = 1 ... Saturday = 7) for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "DAYOFYEAR",
                        "long",
                        "returns the day of the year for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "DEGREES",
                        "double",
                        "converts the provided radians argument to degrees.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "ELT",
                        "string",
                        "returns the Nth element of the list of strings.",
                        new String[] {"long", "string"}),
                new MongoSystemFunction(
                        "EXP",
                        "double",
                        "returns the value of e raised to the provided power.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "EXTRACT",
                        "long",
                        "returns the value of the specified unit from the provided date.",
                        new String[] {"string", "date"}),
                new MongoSystemFunction(
                        "FIELD",
                        "long",
                        "returns the index of the first argument in the list of the remaining arguments.",
                        new String[] {null}),
                new MongoSystemFunction(
                        "FLOOR",
                        "long",
                        "returns the largest integer value not greater than the provided argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "FROMDAYS",
                        "date",
                        "returns a date, given a day number.",
                        new String[] {"long"}),
                new MongoSystemFunction(
                        "FROMUNIXTIME",
                        "date",
                        "returns a representation of the provided unix timestamp, using the provided format string if one is supplied.",
                        new String[] {"long"}),
                new MongoSystemFunction(
                        "GREATEST",
                        null,
                        "returns the largest argument from the provided list.",
                        new String[] {null, null}),
                new MongoSystemFunction(
                        "HOUR",
                        "long",
                        "returns the hour for the provided time.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "IF",
                        null,
                        "returns the second argument if the first is true, and the third argument otherwise.",
                        new String[] {null, null, null}),
                new MongoSystemFunction(
                        "IFNULL",
                        null,
                        "returns the first argument if it is not null, and the second argument otherwise.",
                        new String[] {null, null}),
                new MongoSystemFunction(
                        "INSERT",
                        "string",
                        "returns the string str, with the substring beginning at position pos and len characters long replaced by the string newstr.",
                        new String[] {"string", "long", "long", "string"}),
                new MongoSystemFunction(
                        "INSTR",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str.",
                        new String[] {"string", "string"}),
                new MongoSystemFunction(
                        "INTERVAL",
                        "numeric",
                        "returns 0 if N < N1, 1 if N < N2, and so on.",
                        new String[] {"numeric", "numeric"}),
                new MongoSystemFunction(
                        "LASTDAY",
                        "date",
                        "takes a date or datetime value and returns the corresponding value for the last day of the month.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "LCASE",
                        "string",
                        "returns the provided string with all characters changed to lowercase.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "LEAST",
                        null,
                        "returns the smallest argument from the provided list.",
                        new String[] {null, null}),
                new MongoSystemFunction(
                        "LEFT",
                        "string",
                        "returns the leftmost n characters from the provided string, or NULL if either argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoSystemFunction(
                        "LENGTH",
                        "string",
                        "return the length of the provided string, measured in bytes.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "LN",
                        "double",
                        "returns the natural logarithm of the provided argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "LOCATE",
                        "long",
                        "returns the position of the first occurrence of substring substr in string str, starting at index pos if provided",
                        new String[] {"string", "string"}),
                new MongoSystemFunction(
                        "LOG",
                        "double",
                        "returns the logarithm of the provided argument with the provided base (default e).",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "LOG10",
                        "double",
                        "returns the base-10 logarithm of the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "LOG2",
                        "double",
                        "returns the base-2 logarithm of the provided number.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "LPAD",
                        "string",
                        "returns the string str, left-padded with the string padstr to a length of len characters.",
                        new String[] {"string", "long", "string"}),
                new MongoSystemFunction(
                        "LTRIM",
                        "string",
                        "returns the provided string with leading space characters removed.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "MAKEDATE",
                        "date",
                        "returns a date, given year and day-of-year values. day_of_year must be greater than zero, or the result is NULL.",
                        new String[] {"long", "long"}),
                new MongoSystemFunction(
                        "MD5",
                        "string",
                        "returns an MD5 128-bit checksum for the provided string.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "MICROSECOND",
                        "long",
                        "returns the microseconds from the provided expression.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "MID",
                        "string",
                        "mid(str, pos, len) is a synonym for substring(str, pos, len).",
                        new String[] {"string", "long", "long"}),
                new MongoSystemFunction(
                        "MINUTE",
                        "long",
                        "returns the minute for the provided time.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "MOD",
                        "numeric",
                        "returns the remainder of n divided by m.",
                        new String[] {"numeric", "numeric"}),
                new MongoSystemFunction(
                        "MONTH",
                        "long",
                        "returns the month for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "MONTHNAME",
                        "string",
                        "returns the full name of the month for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "NULLIF",
                        null,
                        "returns null if the two arguments are equal, and the first argument otherwise.",
                        new String[] {null, null}),
                new MongoSystemFunction(
                        "PI", "double", "returns the value of pi.", new String[] {}),
                new MongoSystemFunction(
                        "POW",
                        "numeric",
                        "returns the value of base raised to the power of exp.",
                        new String[] {"numeric", "numeric"}),
                new MongoSystemFunction(
                        "QUARTER",
                        "long",
                        "returns the quarter of the year for the provided date, from 1 to 4.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "RADIANS",
                        "double",
                        "returns the provided degrees argument to radians.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "RAND",
                        "double",
                        "returns a random floating-point value between zero and one, using an integer seed if provided.",
                        new String[] {"long"}),
                new MongoSystemFunction(
                        "REPEAT",
                        "string",
                        "returns a string consisting of the provided string repeated n times. If n is less than 1, returns an empty string. Returns NULL if either argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoSystemFunction(
                        "REPLACE",
                        "string",
                        "returns the string with all occurrences of the string from_str replaced by the string to_str.",
                        new String[] {"string", "string", "string"}),
                new MongoSystemFunction(
                        "REVERSE",
                        "string",
                        "returns the provided string with the order of the characters reversed.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "RIGHT",
                        "string",
                        "returns the rightmost n characters from the provided string, or NULL if any argument is NULL.",
                        new String[] {"string", "long"}),
                new MongoSystemFunction(
                        "ROUND",
                        "numeric",
                        "rounds the argument x to d decimal places (or zero places if not specified).",
                        new String[] {"numeric", "long"}),
                new MongoSystemFunction(
                        "RPAD",
                        "string",
                        "returns the string str, right-padded with the string padstr to a length of len characters.",
                        new String[] {"string", "long", "string"}),
                new MongoSystemFunction(
                        "RTRIM",
                        "string",
                        "returns the provided string with trailing space characters removed.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "SECOND",
                        "long",
                        "returns the second for the provided time, in the range 0-59.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "SIGN",
                        "long",
                        "returns the sign of the argument as -1, 0, or 1 depending on whether it is negative, zero, or positive.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "SIN",
                        "double",
                        "returns the sine of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "SLEEP",
                        "long",
                        "pauses for the number of seconds given by the argument, then returns zero.",
                        new String[] {"decimal"}),
                new MongoSystemFunction(
                        "SPACE",
                        "string",
                        "returns a string consisting of n space characters.",
                        new String[] {"long"}),
                new MongoSystemFunction(
                        "SQRT",
                        "double",
                        "returns the square root of the provided (non-negative) argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "STRTODATE",
                        "date",
                        "takes a string and a date format string, and attempts to parse a date from the string according to the provided format string.",
                        new String[] {"string", "string"}),
                new MongoSystemFunction(
                        "SUBSTRING",
                        "string",
                        "takes a substring from a string",
                        new String[] {"string", "long"}),
                new MongoSystemFunction(
                        "SUBSTRINGINDEX",
                        "string",
                        "returns the substring from string str before count occurrences of the delimiter delim. If count is positive, everything to the left of the final delimiter (counting from the left) is returned. If count is negative, everything to the right of the final delimiter (counting from the right) is returned. SUBSTRING_INDEX() performs a case-sensitive match when searching for delim.",
                        new String[] {"string", "string", "long"}),
                new MongoSystemFunction(
                        "TAN",
                        "double",
                        "returns the tangent of the provided radians argument.",
                        new String[] {"numeric"}),
                new MongoSystemFunction(
                        "TIMEDIFF",
                        "date",
                        "returns expr1 - expr2 expressed as a time value.",
                        new String[] {"date", "date"}),
                new MongoSystemFunction(
                        "TIMETOSEC",
                        "long",
                        "returns the provided time argument, converted to seconds.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "TIMESTAMP",
                        "date",
                        "returns the provided argument as a datetime value. With two arguments, adds the provided time expression to the first argument and returns the result as a datetime.",
                        new String[] {"date", "date"}),
                new MongoSystemFunction(
                        "TIMESTAMPADD",
                        "date",
                        "adds the integer expression interval to the date or datetime expression datetime_expr. The unit for interval is given by the unit argument, which should be one of MICROSECOND (microseconds), SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, or YEAR.",
                        new String[] {"string", "numeric", "date"}),
                new MongoSystemFunction(
                        "TIMESTAMPDIFF",
                        "long",
                        "subtracts the provided timestamps, returning the difference in the specified unit.",
                        new String[] {"string", "date", "date"}),
                new MongoSystemFunction(
                        "TODAYS",
                        "long",
                        "returns the number of days since year zero for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "TOSECONDS",
                        "long",
                        "returns the number of seconds since year zero for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "TRIM",
                        "string",
                        "returns the string str with all remstr prefixes and/or suffixes removed.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "TRUNCATE",
                        "numeric",
                        "returns the number x truncated to d decimal places.",
                        new String[] {"numeric", "long"}),
                new MongoSystemFunction(
                        "UCASE",
                        "string",
                        "returns the provided string with all characters changed to uppercase.",
                        new String[] {"string"}),
                new MongoSystemFunction(
                        "UNIXTIMESTAMP",
                        "long",
                        "returns the provided timestamp (defaulting to the current timestamp) as seconds since unix epoch.",
                        new String[] {}),
                new MongoSystemFunction(
                        "USER",
                        "string",
                        "returns the current MySQL user name and host name as a string.",
                        new String[] {}),
                new MongoSystemFunction(
                        "UTCDATE", "date", "returns the current UTC date.", new String[] {}),
                new MongoSystemFunction(
                        "UTCTIMESTAMP",
                        "date",
                        "returns the current UTC date and time.",
                        new String[] {}),
                new MongoSystemFunction(
                        "VERSION",
                        "string",
                        "returns a string that indicates the MySQL server version.",
                        new String[] {}),
                new MongoSystemFunction(
                        "WEEK", "long", "returns the week number for date.", new String[] {"date"}),
                new MongoSystemFunction(
                        "WEEKDAY",
                        "long",
                        "returns the weekday index (Monday = 0 ... Sunday = 6) for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "YEAR",
                        "long",
                        "returns the year for the provided date.",
                        new String[] {"date"}),
                new MongoSystemFunction(
                        "YEARWEEK",
                        "long",
                        "returns the year and week for a date.",
                        new String[] {"date"})
            };
}
