package com.mongodb.jdbc;

import java.util.Map;

/**
 * Serialize the result of a sqlGetSchema command : https://docs.mongodb.com/datalake/reference/cli/sql/sqlgetschema/.
 * Example:
 * Documents :
 * {"a": {"b": {"c": [1, 2, 3]}}, "s": 1}
 * {"a": {"b": {"c": [4, 5, 6]}}, "s": 2}
 * {"a": {"b": [7, 8, 9]}, "s": 3}
 * {"a": {"b": {"c": []}}, "s": 4}
 * {"a": {"b": {"c": "hello"}}, "s": 5}
 * {"a": {"b": {"c": {"d": 1}}}, "s": 6}
 * {"a": {"b": {"c": null}}}
 * {"s": 7}
 *
 * sqlGetSchema result :
 * {
 *       "ok" : 1,
 *       "metadata" : {
 *         "description" : "set using sqlGenerateSchema with setSchemas = true"
 *       },
 *       "schema" : {
 *         "version" : NumberLong(1),
 *         "jsonSchema" : {
 *               "bsonType" : [
 *                 "object"
 *               ],
 *               "properties" : {
 *                 "a" : {
 *                       "bsonType" : [
 *                         "object"
 *                       ],
 *                       "properties" : {
 *                         "b" : {
 *                               "bsonType" : [
 *                                 "object",
 *                                       "array"
 *                                 ],
 *                                 "properties" : {
 *                                       "c" : {
 *                                         "bsonType" : [
 *                                               "array",
 *                                               "string",
 *                                               "object",
 *                                               "null"
 *                                         ],
 *                                         "properties" : {
 *                                               "d" : {
 *                                                 "bsonType" : [
 *                                                       "int"
 *                                                 ]
 *                                               }
 *                                         },
 *                                         "items" : [
 *                                               {
 *                                                 "bsonType" : [
 *                                                       "int"
 *                                                 ]
 *                                               }
 *                                         ]
 *                                       }
 *                                 },
 *                                 "items" : [
 *                                       {
 *                                         "bsonType" : [
 *                                               "int"
 *                                         ]
 *                                       }
 *                                 ]
 *                               }
 *                         }
 *                       },
 *                       "s" : {
 *                         "bsonType" : [
 *                               "int",
 *                               "object"
 *                         ]
 *                       }
 *                 }
 *               }
 *         }
 *       }
 */
public class MongoJsonSchemaResult {

    public int ok;
    public Map<String, String> metadata;
    public MongoVersionedJsonSchema schema;
}
