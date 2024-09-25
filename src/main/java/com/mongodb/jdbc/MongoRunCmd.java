/*
 * Copyright 2024-present MongoDB, Inc.
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

import com.mongodb.jdbc.logging.MongoLogger;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import org.bson.Document;

public class MongoRunCmd {

    private final MongoConnection connection;
    private final MongoLogger logger;

    /** Native method to send commands via JNI. */
    public native byte[] runCommand(byte[] command, int length) throws SQLException;

    /**
     * Constructs a MongoRunCmd instance associated with a MongoConnection, used to obtain logger.
     *
     * @param connection The MongoConnection instance.
     */
    public MongoRunCmd(MongoConnection connection) {
        this.connection = connection;
        this.logger = connection.getLogger();
    }

    /**
     * Executes a BSON command and returns the response as a Document.
     *
     * @param command The BSON command to execute.
     * @return The response Document.
     * @throws SQLException If the command execution fails.
     */
    public Document runCommand(Document command) throws SQLException {
        try {
            byte[] commandBytes = BsonUtils.serialize(command);
            byte[] responseBytes = runCommand(commandBytes, commandBytes.length);
            Document responseDoc = BsonUtils.deserialize(responseBytes);

            if (responseDoc.containsKey("error")) {
                String errorMsg = responseDoc.getString("error");
                logger.log(Level.SEVERE, "Error executing command: " + errorMsg);
                throw new SQLException("Error executing command: " + errorMsg);
            }

            return responseDoc;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException during runCommand execution.", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception during runCommand execution.", e);
            throw new SQLException("Unexpected error executing command.", e);
        }
    }

    /**
     * Retrieves the version of the mongosqltranslate library.
     *
     * @return The version string.
     * @throws SQLException If an error occurs during command execution.
     */
    public String getMongosqlTranslateVersion() throws SQLException {
        Document command =
                new Document("command", "getMongosqlTranslateVersion")
                        .append("options", new Document());

        Document responseDoc = runCommand(command);

        String version = responseDoc.getString("version");
        logger.log(Level.INFO, "mongosqlTranslateVersion: " + version);
        return version;
    }

    /**
     * Checks if the JDBC driver version is compatible with the mongosqltranslate library.
     *
     * @return True if compatible, false otherwise.
     * @throws SQLException If an error occurs during command execution.
     */
    public boolean checkDriverVersion() throws SQLException {
        Document options = new Document("driverVersion", MongoDriver.getVersion()).append("odbcDriver", false);
        Document command = new Document("command", "checkDriverVersion").append("options", options);

        Document responseDoc = runCommand(command);

        boolean isCompatible = responseDoc.getBoolean("compatible", false);
        logger.log(Level.INFO, "Driver Compatibility Status: " + isCompatible);
        return isCompatible;
    }

    /**
     * Retrieves the namespaces involved in the given SQL query.
     *
     * @param dbName The name of the database.
     * @param sql The SQL query.
     * @return A map of database names to their respective collection names.
     * @throws SQLException Error that occurs during command execution or parsing.
     */
    public Map<String, List<String>> getNamespaces(String dbName, String sql) throws SQLException {
        Document options = new Document("sql", sql).append("db", dbName);

        Document command = new Document("command", "getNamespaces").append("options", options);

        Document responseDoc = runCommand(command);

        if (responseDoc.containsKey("error")) {
            String errorMsg = responseDoc.getString("error");
            throw new SQLException("Error fetching namespaces: " + errorMsg);
        }

        Map<String, List<String>> namespacesMap = new HashMap<>();

        if (responseDoc.containsKey("namespaces")) {
            List<Document> namespaceDocs = responseDoc.getList("namespaces", Document.class);
            for (Document namespaceDoc : namespaceDocs) {
                String database = namespaceDoc.getString("database");
                String collection = namespaceDoc.getString("collection");

                namespacesMap.computeIfAbsent(database, k -> new ArrayList<>()).add(collection);
            }
        } else {
            throw new SQLException("No namespaces found in the response.");
        }

        return namespacesMap;
    }

    /**
     * Executes a translate command based on the provided SQL and returns the response.
     *
     * @param sql The SQL query to translate.
     * @param dbName The name of the database.
     * @param excludeNamespaces Whether to exclude namespaces.
     * @param relaxSchemaChecking Whether to relax schema checking.
     * @param catalogDocument Schema catalog
     * @return The translate response Document.
     * @throws SQLException If the command execution fails.
     */
    public Document translate(
            String sql,
            String dbName,
            boolean excludeNamespaces,
            boolean relaxSchemaChecking,
            Document catalogDocument)
            throws SQLException {
        Document options =
                new Document("sql", sql)
                        .append("db", dbName)
                        .append("excludeNamespaces", excludeNamespaces)
                        .append("relaxSchemaChecking", relaxSchemaChecking)
                        .append("schemaCatalog", catalogDocument);
        Document translateCommand = new Document("command", "translate").append("options", options);

        return runCommand(translateCommand);
    }
}
