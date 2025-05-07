# Introduction

## Overview

The MongoDB Atlas SQL JDBC driver supports connections from a SQL compliant
client, enabling you to query your data in MongoDB with SQL queries.

### Features

Key Features: Native object and array support.

Compatibility: The MongoDB Atlas SQL JDBC driver is compatible with MongoDB versions greater
than or equal to 6.0.6.

## System Requirements

### Hardware Requirements

The MongoDB Atlas SQL JDBC driver has no hard system requirements itself. Any system
capable of running a modern SQL tool (such as Tableau) is capable of running
the MongoDB Atlas SQL JDBC driver.

### Software Requirements

#### Supported Operating Systems

The MongoDB JDBC driver is compatible with:
- Windows x86_64
- macOS x86_64 and macOS aarch64 architectures
- linux x86_64 and linux arm64 architectures

#### Dependencies

- OpenSSL 3.0 or greater is required for secure connections.

## Installation

1. [Download the JAR](https://central.sonatype.com/artifact/org.mongodb/mongodb-jdbc).
2. Install and Configure the JDBC Driver.
Copy the JAR file to the appropriate directory. For example, to install the JDBC driver for Tableau on MacOS,
copy the file to `~/Library/Tableau/Drivers`.

## Usage

### Escape Fields

It may be required to select a field with a name that conflicts with an operator
keyword. To do so, surround the field name with backticks.

For example, to select a field called "select" from the table "SQL":

```sh
SELECT `select` from SQL
```

### Databases

The driver will use the database specified in the following order:
1. Query
2. JDBC Connection String/DSN

For example, if your JDBC connection string or DSN contains the DATABASE value **Store1**,
the query `SELECT * FROM Sales` will query the Sales collection in the Store1 database.

You may also specify the database in the query. The following query will target the Sales collection
in the Store2 database.

```sql
SELECT * FROM Store2.Sales
```

### Collection/Table

The driver treats MongoDB collections and views as tables. See [Databases](#databases)
for more information about specifying a collection to query.

### Field/Column

The driver maps documents fields to column names. Note that by default, the driver
does not flatten objects or unwind arrays. Instead, it returns these types, as well
as ObjectID, UUID, and other complex data types in their JSON form.

Given the following document in the users collection:

```json
{
    "name": "Jon Snow",
    "username": "AzureDiamond",
    "favorites": ["irc", "hunter2"],
    "address": {
        "street": "1234 Password Way",
        "city": "Anywhere",
        "state": "CA",
        "zip": 90510
    }
}
```

The query `SELECT * FROM users WHERE username='AzureDiamond'` will return the following:

| name |  username  | favorites  |  address  |
|------|------------|------------|-----------|
| "Jon Snow" | "AzureDiamond" | "[\"irc\", \"hunter2\"]" | "{\"street\": \"1234 Password Way\"}..." |

Note: In the previous example, the output of address was shortened for brevity. In actual results, the full address
will be returned in string JSON form.

### Work with Objects

Building on the previous example, if you only wish to get the zip field from the address object, the query
SELECT name, username, favorite, address.zip FROM users WHERE username='AzureDiamond' will return the following columns:
- name, username, favorites, address.zip

If you require the entire object in a flattened state, use the FLATTEN operator.
The query SELECT * from FLATTEN(users) will return the following columns, with the values mapped appropriately:
- name, username, favorites, address_street, address_city, address_state, address_zip

### Work with Arrays

Arrays can be unwound using the UNWIND operator, which expands array elements into individual rows in the result set.

#### UNWIND Syntax

```sql
UNWIND(<datasource> WITH PATH | PATHS => <paths>,
                         INDEX => <name>,
                         OUTER => <bool>)
```

Where:
- `datasource` is any valid data source
- `PATH` or `PATHS` specifies the field path(s) to unwind. If specified in parentheses, multiple paths may be expressed.
- `INDEX` is the name to assign to the index column
- `OUTER` indicates whether documents with null, missing, or empty array values are preserved

#### Single Path Unwinding

For a simple array, you can unwind a single path:

```sql
SELECT * FROM UNWIND(users WITH PATH => users.favorites) WHERE username = 'AzureDiamond'
```

This will result in multiple rows in the result set, each with an entry from the `favorites` array:

- "Jon Snow", "AzureDiamond", "irc", ...
- "Jon Snow", "AzureDiamond", "hunter2", ...

#### Multiple Path Unwinding

The UNWIND operator allows unwinding multiple arrays in a single operation using compound array identifiers:

```sql
SELECT a.b.c AS c, b.d AS d
FROM UNWIND(collection WITH PATHS => (a[].b[], b[]))
```

This unwinding operation is equivalent to:

```sql
SELECT a.b.c AS c, b.d AS d
FROM UNWIND(UNWIND(UNWIND(collection WITH PATH => a) WITH PATH => a.b) WITH PATH => b)
```

#### Compound Array Identifiers

Square brackets (`[]`) after a field name indicate where an unwind operation should occur. For nested arrays, you can specify brackets at each level:

```sql
-- Unwind the array 'a', then unwind the nested array 'b' within each element of 'a'
SELECT a.b.c AS c FROM UNWIND(collection WITH PATHS => (a[].b[]))
```

For anonymous inner arrays, you can use multiple consecutive brackets:

```sql
-- Unwind the array 'g', then unwind each anonymous inner array
SELECT * FROM UNWIND(collection WITH PATHS => (g[][]))
```

#### Index Option

You can track the position of elements in the original array using the INDEX option:

```sql
-- Global index for all unwound arrays
SELECT a.b.c AS c, a_ix, b_ix
FROM UNWIND(collection WITH PATHS => (a[].b[], b[]), INDEX => ix)

-- Specific index for each unwound array
SELECT a.b.c AS c, a_ix, b_ix
FROM UNWIND(collection WITH PATHS => (a[INDEX => a_ix].b[], b[INDEX => b_ix]))
```

#### OUTER Option

The OUTER option preserves documents that have null, missing, or empty array values:

```sql
-- Apply OUTER option to all unwound arrays
SELECT * FROM UNWIND(collection WITH PATHS => (a[].b[]), OUTER => TRUE)

-- Apply OUTER option to a specific unwound array
SELECT * FROM UNWIND(collection WITH PATHS => (a[OUTER => TRUE].b[]))
```

#### Examples

Given a document in collection "foo":

```json
{
  "a": [
    { "b": [ { "c": 1 } ] }
  ],
  "b": [
    { "d": 42 }
  ],
  "w": {
    "x": [ 3 ],
    "y": [ { "z": [ 4 ] } ]
  }
}
```

To extract nested values from multiple arrays:

```sql
-- Unwind multiple arrays in a single operation
SELECT w.x as x, w.y.z as z
FROM UNWIND(foo WITH PATHS => (w.x[], w.y[].z[]))
```

Result:

```sh
x | z
--+--
3 | 4

```

To unwind arrays with index tracking:

```sql
SELECT a.b.c AS c, a_ix, b_ix, b.d AS d
FROM UNWIND(foo WITH PATHS => (a[INDEX => a_ix].b[], b[INDEX => b_ix]))
```

Result:

```sh
c | a_ix | b_ix | d
--+------+------+----
1 |    0 |    0 | 42
```

#### Important Notes

1. Compound array identifiers must be enclosed in parentheses:

   ```sql
   -- Error: compound array identifier not enclosed in parentheses
   SELECT * FROM UNWIND(foo WITH PATH => a[].b[])

   -- Correct
   SELECT * FROM UNWIND(foo WITH PATHS => (a[].b[]))
   ```

2. You must unwind parent arrays before accessing nested arrays:

   ```sql
   -- Error: a.b[] is invalid because a hasn't been unwound first
   SELECT * FROM UNWIND(foo WITH PATH => (a.b[]))

   -- Correct
   SELECT * FROM UNWIND(foo WITH PATHS => (a[].b[]))
   ```

3. The PATH and PATHS keywords are interchangeable.

### Convert Data Types

Convert data types using the CAST() operator, or the :: shorthand.

```sql
SELECT CAST(saleDate AS string), saleDate
FROM Sales;

SELECT saleDate::string, saleDate
FROM Sales;
```

### String Literals

Use single quotes for string literals:

```sql
SELECT * FROM Sales WHERE customer.gender = 'M' LIMIT 2;
```

Notice that 'M' is enclosed in single quotes.

### Query Syntax

Retrieve data using the SELECT statement:

```sql
SELECT * FROM Sales LIMIT 2;
SELECT purchaseMethod, customer, items FROM Sales LIMIT 2;
```

Note: Combining \* with specific column names (e.g., SELECT *, FieldA FROM Table) is not supported and will produce an error.

### CASE

Use the CASE expression for conditional logic:

```sql
SELECT
  CASE
    WHEN customer.age <= 20 THEN '20 years old or younger'
    WHEN customer.age > 20 AND customer.age <= 30 THEN '21-30 year olds'
    WHEN customer.age > 30 AND customer.age <= 40 THEN '31-40 year olds'
    WHEN customer.age > 40 AND customer.age <= 50 THEN '41-50 year olds'
    WHEN customer.age > 50 AND customer.age <= 60 THEN '51-60 year olds'
    WHEN customer.age > 60 AND customer.age <= 70 THEN '61-70 year olds'
    WHEN customer.age > 70 THEN '70 years and older'
    ELSE 'Other'
  END AS ageRange,
  customer.age,
  customer.gender,
  customer.email
FROM Sales;
```

This example categorizes customer ages using a CASE expression with dot notation for nested fields.

### FROM

Specify the collection or table in the FROM clause:

```sql
SELECT * FROM Sales LIMIT 2;
```

### JOIN

Perform joins between collections:

```sql
SELECT
  b.ProductSold,
  CAST(b._id AS string) AS ID,
  (b.Price * b.Quantity) AS totalAmount
FROM
  (SELECT * FROM Sales a WHERE customer.gender = 'F') a
INNER JOIN
  Transactions b
ON
  (CAST(a._id AS string) = CAST(b._id AS string));
```

Best Practices: Filter or limit data as much as possible to improve query execution speed.
Supported Joins: INNER JOIN, (CROSS) JOIN, LEFT OUTER JOIN, and RIGHT OUTER JOIN.

### UNION ALL

Combine result sets using UNION ALL:

```sql
SELECT * FROM Sales
UNION ALL
SELECT * FROM Transactions;
```

Note: UNION (which removes duplicates) is not supported. Only UNION ALL is supported.

### Nested Selects

Use subqueries with aliases:

```sql
SELECT *
FROM (SELECT * FROM Sales) AS subSelect;
```

Note: MongoSQL requires nested selects to have an alias, although this is not a SQL-92 requirement.

### WHERE

Filter records using the WHERE clause:

```sql
SELECT * FROM Sales WHERE customer.gender = 'M';
SELECT * FROM Sales WHERE customer.age > 20;
```

### LIKE

Use LIKE for pattern matching:

```sql
SELECT purchaseMethod FROM Sales WHERE purchaseMethod LIKE 'In%';
```

### ESCAPE

Specify an escape character in LIKE patterns:

```sql
SELECT customer FROM Sales WHERE customer.email LIKE '%_%' ESCAPE '_';
```

Note: Escape characters indicate that any wildcard character following the escape character should be treated as a regular character.

### GROUP BY

Group records using GROUP BY:

```sql
SELECT customer.age AS customerAge, COUNT(*)
FROM Sales
GROUP BY customer.age;
```

### HAVING

Filter grouped records with HAVING:

```sql
SELECT customer.gender AS customerGender, customer.age AS customerAge, COUNT(*)
FROM Sales
GROUP BY customer.gender, customer.age
HAVING COUNT(*) > 1;
```

### ORDER BY

Order results using ORDER BY:

```sql
SELECT customer.gender AS customerGender, COUNT(*)
FROM Sales
GROUP BY customer.gender
ORDER BY customerGender;
```

### LIMIT and OFFSET

Limit the number of records and specify an offset:

```sql
SELECT * FROM Sales LIMIT 3;
SELECT couponUsed FROM Sales OFFSET 2;
```

### AS

Alias columns and expressions using AS:

```sql
SELECT couponUsed AS Coupons FROM Sales OFFSET 2;
SELECT customer.age AS customerAge, COUNT(*)
FROM Sales
GROUP BY customer.age;
```

Note: Alias assignments work as expected. When using aggregates with nested fields, the syntax may require attention.

### Arithmetic Operators

Perform calculations using arithmetic operators:

- Addition (+)
- Subtraction (-)
- Multiplication (*)
- Division (/)
- Modulus (MOD function)

Example:

```sql
SELECT ProductSold, Price, Quantity, (Price * Quantity) AS TotalCost
FROM Transactions
LIMIT 2;
```

Modulus:

```sql
SELECT MOD(Value1, Value2) FROM TableName;
```

### Comparison Operators

Use comparison operators in conditions:

- Equals (=)
- Not Equal (!= or <>)
- Greater Than (>)
- Greater Than or Equal (>=)
- Less Than (<)
- Less Than or Equal (<=)

Examples:

```sql
SELECT * FROM Sales WHERE customer.age > 20;
SELECT * FROM Sales WHERE customer.gender = 'F';
```

### Logical/Boolean Operators

Combine conditions using logical operators:

- AND
- OR
- NOT

Examples:

```sql
SELECT * FROM Sales WHERE customer.age > 20 AND customer.gender = 'M';
SELECT * FROM Sales WHERE customer.age = 20 OR customer.gender = 'M';
SELECT * FROM Sales WHERE customer.age > 20 AND NOT customer.gender = 'M';
```

### Aggregate Expressions

Use aggregate functions for calculations:

#### SUM()

```sql
SELECT ProductSold, SUM(Price)
FROM Transactions
GROUP BY ProductSold;
```

#### AVG()

```sql
SELECT ProductSold, AVG(Price)
FROM Transactions
GROUP BY ProductSold;
```

#### COUNT()

```sql
SELECT ProductSold, COUNT(Price)
FROM Transactions
GROUP BY ProductSold;
```

#### MIN()

```sql
SELECT ProductSold, MIN(Price)
FROM Transactions
GROUP BY ProductSold;
```

#### MAX()

```sql
SELECT ProductSold, MAX(Price)
FROM Transactions
GROUP BY ProductSold;
```

#### COUNT(DISTINCT)

```sql
SELECT COUNT(DISTINCT purchaseMethod) FROM Sales;
```

Note: May not work if the aggregated field is not comparable (e.g., documents, arrays).

#### SUM(DISTINCT)

```sql
SELECT ProductSold, SUM(DISTINCT Price)
FROM Transactions
GROUP BY ProductSold;
```

### Scalar Functions

#### String Functions

##### Concatenation

Use || to concatenate strings:

```sql
SELECT purchaseMethod || ' ' || storeLocation AS purchaseDetails
FROM Sales;
```

##### SUBSTRING()

Extract a substring from a string:

```sql
SELECT ProductSold, SUBSTRING(ProductSold, 0, 2)
FROM Transactions;
```

Note: Uses zero-based indexing.

##### UPPER() and LOWER()

Convert strings to uppercase or lowercase:

```sql
SELECT ProductSold, UPPER(SUBSTRING(ProductSold, 0, 2))
FROM Transactions;

SELECT ProductSold, LOWER(SUBSTRING(ProductSold, 0, 2))
FROM Transactions;
```

##### TRIM()

Remove leading and trailing spaces or specified characters:

```sql
SELECT TRIM(purchaseMethod)
FROM Sales;

SELECT TRIM('In' FROM purchaseMethod)
FROM Sales;
```

##### CHAR_LENGTH()

Get the length of a string:

```sql
SELECT CHAR_LENGTH(ProductSold)
FROM Transactions;
```

##### POSITION()

Find the position of a substring:

```sql
SELECT purchaseMethod, POSITION('i' IN purchaseMethod)
FROM Sales;
```

Returns -1 if the substring is not found.

##### LEFT() and RIGHT()

###### LEFT():

Use SUBSTRING with a starting position of 0.

```sql
SELECT ProductSold, SUBSTRING(ProductSold, 0, 2)
FROM Transactions;
```

###### RIGHT():

Use a combination of SUBSTRING and CHAR_LENGTH minus the length from the SUBSTRING
argument.

```sql
SELECT SUBSTRING(ProductSold, CHAR_LENGTH(ProductSold) - 2, 2)
FROM Transactions;
```

Combines SUBSTRING() with CHAR_LENGTH() to get characters from the end of the string.

#### Date and Time Functions

##### DATETRUNC()

Truncate a timestamp to a specified unit:

```sql
SELECT DATETRUNC(DAY, saleDate)
FROM Sales;
```

Supported Units: YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, WEEK, DAY_OF_YEAR, ISO_WEEK, ISO_WEEKDAY.

##### DATEADD()

Add an interval to a timestamp:

```sql
SELECT DATEADD(YEAR, 1, saleDate), saleDate
FROM Sales;
```

##### DATEDIFF()

Calculate the difference between two timestamps:

```sql
SELECT DATEDIFF(YEAR, CURRENT_TIMESTAMP, saleDate), saleDate
FROM Sales;
```

##### EXTRACT()

Extract a part of a timestamp:

```sql
SELECT EXTRACT(YEAR FROM saleDate), saleDate
FROM Sales;
```

##### CASTING TO/FROM DATE, TIMESTAMP

```sql
SELECT CAST(EXTRACT(YEAR FROM saleDate) AS integer), saleDate
FROM Sales;

SELECT CAST('1975-01-23' AS TIMESTAMP) AS Birthdate, saleDate
FROM Sales;
```

Note: MongoDB supports only the TIMESTAMP type.

##### CURRENT_TIMESTAMP

Get the current timestamp:

```sql
SELECT CURRENT_TIMESTAMP
FROM Sales;
```

##### ISO_WEEKDAY

Get the ISO day of the week:

```sql
SELECT EXTRACT(ISO_WEEKDAY FROM saleDate), saleDate
FROM Sales;
```

#### Numeric Functions

##### TO/FROM EPOCH

To Epoch:

```sql
SELECT CAST(saleDate AS LONG)
FROM Sales;
```

From Epoch:

```sql
SELECT CAST(epochValue AS TIMESTAMP)
FROM SomeTable;
```

#### Unsupported Functions

- SIMILAR TO
- RANDOM
- Timezone Conversion
  Not Supported: MongoDB stores dates in UTC.
- GROUP_CONCAT

### Additional Notes

- Polymorphic Schemas:
Be cautious when using aggregate functions on fields with polymorphic schemas or non-comparable types like documents and arrays.
The term "polymorphic" schema is used to refer to a field/column that can have multiple types, e.g. int and string.

- Escape Characters:
Use the ESCAPE clause to specify custom escape characters in LIKE patterns.

- Alias Assignments:
Required when using nested selects or derived tables.

- Date Functions:
MongoSQL supports various date functions, but only the TIMESTAMP data type is available.

## Additional Features

### Security Features

The MongoDB Atlas SQL JDBC driver supports all authentication mechanisms
supported by MongoDB (x509, OAuth, LDAP, etc...). See [authentication mechanisms](https://www.mongodb.com/docs/manual/core/authentication/#authentication-mechanisms)
for a full list of supported authentication mechanisms.

If [configured](https://www.mongodb.com/docs/manual/tutorial/configure-ssl/), the MongoDB Atlas SQL JDBC driver supports TLS/SSL connections.

### Logging and Diagnostics

The driver emits log messages that your preferred BI tool can intercept and log. For tool specific logging
help, see your tool vendor's documentation.

## Troubleshooting

### Common Issues

- Enterprise edition detected, but mongosqltranslate library not found.

The translation library was unable to be located. The translation library is included in the JAR and copied to a
system location determined by your JVM. Ensure your user has write access to this location.

### Debugging Tips

Often times, an error from MongoDB that can't be translated
into an JDBC error will be in the logs. Look for ERROR and WARN entries.

## Uninstallation

Delete the JAR file.

## Appendix

### Error Codes Reference

There are many error codes available to help trouble shoot queries and operations.
You can reference them [in the official MongoDB documentation](https://www.mongodb.com/docs/atlas/data-federation/query/sql/errors/).
