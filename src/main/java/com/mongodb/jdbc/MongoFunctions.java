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

import java.util.HashSet;
import java.util.Set;

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
    protected static final String ABS = "ABS";
    protected static final String CEIL = "CEIL";
    protected static final String COS = "COS";
    protected static final String DEGREES = "DEGREES";
    protected static final String FLOOR = "FLOOR";
    protected static final String LOG = "LOG";
    protected static final String MOD = "MOD";
    protected static final String POW = "POW";
    protected static final String RADIANS = "RADIANS";
    protected static final String ROUND = "ROUND";
    protected static final String SIN = "SIN";
    protected static final String SQRT = "SQRT";
    protected static final String TAN = "TAN";
    protected static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    protected static final String DATEADD = "DATEADD";
    protected static final String DATEDIFF = "DATEDIFF";
    protected static final String DATETRUNC = "DATETRUNC";
    protected static final String EXTRACT = "EXTRACT";
    protected static final String SUBSTRING = "SUBSTRING";

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
                                        ABS,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns the absolute value of the given number.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ABS,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the absolute value of the given number.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ABS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the absolute value of the given number.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ABS,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the absolute value of the given number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "BIT_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string in bits",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        CEIL,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns a number to the nearest whole number of equal or greater value.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()}),
                                new MongoFunction(
                                        CEIL,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns a number to the nearest whole number of equal or greater value.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()}),
                                new MongoFunction(
                                        CEIL,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns a number to the nearest whole number of equal or greater value.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()}),
                                new MongoFunction(
                                        CEIL,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns a number to the nearest whole number of equal or greater value.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()}),
                                new MongoFunction(
                                        "CHAR_LENGTH",
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns length of string",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()},
                                        FunctionCategory.STRING_FUNC),
                                new MongoFunction(
                                        COS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the cosine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        COS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the cosine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        COS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the cosine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        COS,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the cosine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
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
                                new MongoFunction(
                                        DATEADD,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the specified date with the specified number interval added to the specified date_part of that date.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        DATEADD,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the specified date with the specified number interval added to the specified date_part of that date.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        DATEDIFF,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the difference between date1 and date2 expressed in units of date_part.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        DATEDIFF,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "returns the difference between date1 and date2 expressed in units of date_part.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        DATETRUNC,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "truncates the specified date to the accuracy specified by date_part.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName()
                                        }),
                                new MongoFunction(
                                        DATETRUNC,
                                        BsonTypeInfo.BSON_DATE.getBsonName(),
                                        "truncates the specified date to the accuracy specified by date_part.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_DATE.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName()
                                        }),
                                new MongoFunction(
                                        DEGREES,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from radians to degrees.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        DEGREES,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from radians to degrees.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        DEGREES,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from radians to degrees.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        DEGREES,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the given number converted from radians to degrees.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
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
                                        FLOOR,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns a number to the nearest whole number of equal or lesser value.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        FLOOR,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns a number to the nearest whole number of equal or lesser value.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        FLOOR,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns a number to the nearest whole number of equal or lesser value.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        FLOOR,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns a number to the nearest whole number of equal or lesser value.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        LOG,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the logarithm of a number for the given base.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        "LOWER",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns the provided string with all characters changed to lowercase.",
                                        new String[] {BsonTypeInfo.BSON_STRING.getBsonName()}),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        MOD,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "divides number by divisor and returns the remainder.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
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
                                        POW,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName()
                                        }),
                                new MongoFunction(
                                        POW,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the number raised to the specified exponent.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName()
                                        }),
                                new MongoFunction(
                                        RADIANS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from degrees to radians.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        RADIANS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from degrees to radians.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        RADIANS,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the given number converted from degrees to radians.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        RADIANS,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the given number converted from degrees to radians.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_INT.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_INT.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_LONG.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_LONG.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        ROUND,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "rounds number to a specified number of digits.",
                                        new String[] {
                                            BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                            BsonTypeInfo.BSON_LONG.getBsonName()
                                        },
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SIN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the sine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SIN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the sine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SIN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the sine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SIN,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the sine of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        "SIZE",
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the size of an array.",
                                        new String[] {BsonTypeInfo.BSON_ARRAY.getBsonName()}),
                                new MongoFunction(
                                        "SPLIT",
                                        BsonTypeInfo.BSON_STRING.getBsonName(),
                                        "returns a substring from a string, using a delimiter character to divide the string into a sequence of tokens.",
                                        new String[] {
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_STRING.getBsonName(),
                                            BsonTypeInfo.BSON_INT.getBsonName()
                                        }),
                                new MongoFunction(
                                        SQRT,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the square root of a positive number.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SQRT,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the square root of a positive number.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SQRT,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the square root of a positive number.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        SQRT,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the square root of a positive number.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
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
                                        TAN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the tangent of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_INT.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        TAN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the tangent of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_LONG.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        TAN,
                                        BsonTypeInfo.BSON_DOUBLE.getBsonName(),
                                        "returns the tangent of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DOUBLE.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
                                new MongoFunction(
                                        TAN,
                                        BsonTypeInfo.BSON_DECIMAL.getBsonName(),
                                        "returns the tangent of an angle specified in radians.",
                                        new String[] {BsonTypeInfo.BSON_DECIMAL.getBsonName()},
                                        FunctionCategory.NUM_FUNC),
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

        Set<String> seenFunctions = new HashSet<>();

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

            if (null != currBuilder && !seenFunctions.contains(currFunc.name)) {
                if (currBuilder.length() > 0) {
                    currBuilder.append(',');
                }
                currBuilder.append(currFunc.name);
                seenFunctions.add(currFunc.name);
            }
        }
        numericFunctionsString = numericFunctionsBuilder.toString();
        stringFunctionsString = stringFunctionsBuilder.toString();
        dateFunctionsString = dateTimeFunctionsBuilder.toString();
        systemFunctionsString = systemFunctionsBuilder.toString();
    }
}
