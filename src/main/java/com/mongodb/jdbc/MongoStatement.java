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

import com.google.common.base.Preconditions;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.mongosql.GetNamespacesResult;
import com.mongodb.jdbc.mongosql.MongoSQLException;
import com.mongodb.jdbc.mongosql.MongoSQLTranslate;
import com.mongodb.jdbc.mongosql.TranslateResult;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.bson.*;

@AutoLoggable
public class MongoStatement implements Statement {
    private static final BsonInt32 BSON_ONE_INT_VALUE = new BsonInt32(1);

    // Likely, the actual mongo sql command will not
    // need a database or collection, since those
    // must be parsed from the query.
    private MongoDatabase currentDB;
    private MongoResultSet resultSet;
    private MongoConnection conn;
    protected boolean isClosed = false;
    protected boolean closeOnCompletion = false;
    private int fetchSize = 0;
    private int maxQuerySec = 0;
    private String currentDBName;
    private MongoLogger logger;
    private int statementId;

    public MongoStatement(MongoConnection conn, String databaseName) throws SQLException {
        Preconditions.checkNotNull(conn);
        Preconditions.checkNotNull(databaseName);
        this.statementId = conn.getNextStatementId();
        logger = new MongoLogger(this.getClass().getCanonicalName(), conn.getLogger(), statementId);
        this.conn = conn;
        currentDBName = databaseName;

        try {
            currentDB = conn.getDatabase(databaseName);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Database name %s is invalid", databaseName);
        }
    }

    protected MongoLogger getParentLogger() {
        return conn.getLogger();
    }

    protected int getStatementId() {
        return statementId;
    }

    protected BsonDocument constructQueryDocument(String sql) {
        BsonDocument stage = new BsonDocument();
        BsonDocument sqlDoc = new BsonDocument();
        sqlDoc.put("statement", new BsonString(sql));
        stage.put("$sql", sqlDoc);
        return stage;
    }

    protected void checkClosed() throws SQLException {
        if (isClosed) {
            throw new SQLException("Connection is closed.");
        }
    }

    private BsonDocument constructSQLGetResultSchemaDocument(String sql) {
        BsonDocument command = new BsonDocument();
        command.put("sqlGetResultSchema", BSON_ONE_INT_VALUE);
        command.put("query", new BsonString(sql));
        command.put("schemaVersion", BSON_ONE_INT_VALUE);
        return command;
    }

    // ----------------------------------------------------------------------

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void close() {
        // closing an already closed Statement is a no-op.
        if (isClosed) {
            return;
        }
        isClosed = true;
        closeExistingResultSet();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        checkClosed();
    }

    @Override
    public int getMaxRows() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkClosed();
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        checkClosed();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        checkClosed();
        return maxQuerySec;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        checkClosed();
        maxQuerySec = seconds;
    }

    // Close any existing resultsets associated with this statement.
    protected void closeExistingResultSet() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException ignored) {
            // The cursor might have already been closed by the server. Ignore exceptiong
        } finally {
            resultSet = null;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        checkClosed();
    }

    // ----------------------- Multiple Results --------------------------

    @Override
    public boolean execute(String sql) throws SQLException {
        executeQuery(sql);
        return resultSet != null;
    }

    private ResultSet executeAtlasDataFederationQuery(String sql) throws SQLException {
        BsonDocument getSchemaCmd = constructSQLGetResultSchemaDocument(sql);

        BsonDocument sqlStage = constructQueryDocument(sql);
        MongoIterable<BsonDocument> iterable =
                currentDB
                        .aggregate(Collections.singletonList(sqlStage), BsonDocument.class)
                        .maxTime(maxQuerySec, TimeUnit.SECONDS);

        if (fetchSize != 0) {
            iterable = iterable.batchSize(fetchSize);
        }

        MongoCursor<BsonDocument> cursor = iterable.cursor();
        MongoJsonSchemaResult schemaResult =
                currentDB
                        .withCodecRegistry(MongoDriver.registry)
                        .runCommand(getSchemaCmd, MongoJsonSchemaResult.class);
        MongoJsonSchema schema = schemaResult.schema.mongoJsonSchema;
        List<List<String>> selectOrder = schemaResult.selectOrder;

        resultSet =
                new MongoResultSet(
                        this,
                        cursor,
                        schema,
                        selectOrder,
                        conn.getExtJsonMode(),
                        conn.getUuidRepresentation());

        return resultSet;
    }

    private ResultSet executeDirectClusterQuery(String sql)
            throws SQLException, MongoSQLException, MongoSerializationException {
        MongoSQLTranslate mongoSQLTranslate = conn.getMongosqlTranslate();
        String dbName = currentDB.getName();

        GetNamespacesResult namespaceResult =
                mongoSQLTranslate.getNamespaces(currentDB.getName(), sql);
        List<GetNamespacesResult.Namespace> collections = namespaceResult.namespaces;
        if (collections == null || collections.isEmpty()) {
            throw new SQLException("No collections found for the current database: " + dbName);
        }

        BsonDocument catalogDoc =
                mongoSQLTranslate.buildCatalogDocument(currentDB, dbName, collections);
        TranslateResult translateResponse = mongoSQLTranslate.translate(sql, dbName, catalogDoc);

        MongoIterable<BsonDocument> iterable =
                currentDB
                        .getCollection(translateResponse.targetCollection)
                        .aggregate(translateResponse.pipeline, BsonDocument.class)
                        .maxTime(maxQuerySec, TimeUnit.SECONDS);

        if (fetchSize != 0) {
            iterable = iterable.batchSize(fetchSize);
        }

        resultSet =
                new MongoResultSet(
                        this,
                        iterable.cursor(),
                        translateResponse.resultSetSchema,
                        translateResponse.selectOrder,
                        conn.getExtJsonMode(),
                        conn.getUuidRepresentation());

        return resultSet;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeExistingResultSet();

        try {
            if (conn.getClusterType() == MongoConnection.MongoClusterType.AtlasDataFederation) {
                return executeAtlasDataFederationQuery(sql);
            } else if (conn.getClusterType() == MongoConnection.MongoClusterType.Enterprise) {
                return executeDirectClusterQuery(sql);
            } else {
                throw new SQLException("Unsupported cluster type: " + conn.clusterType);
            }
        } catch (MongoExecutionTimeoutException e) {
            throw new SQLTimeoutException(e);
        } catch (MongoSQLException | MongoSerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkClosed();
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkClosed();
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        checkClosed();
        // We only support one SQL query every time and no stored procedure support
        return false;
    }

    // --------------------------JDBC 2.0-----------------------------

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkClosed();
        fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkClosed();
        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        checkClosed();
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        checkClosed();
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void cancel() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkClosed();
        return conn;
    }

    // --------------------------JDBC 3.0-----------------------------

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkClosed();
        if (current != CLOSE_CURRENT_RESULT
                && current != KEEP_CURRENT_RESULT
                && current != CLOSE_ALL_RESULTS) {
            throw new SQLException("Invalid input.");
        }
        if (current == KEEP_CURRENT_RESULT || current == CLOSE_ALL_RESULTS) {
            throw new SQLFeatureNotSupportedException(
                    Thread.currentThread().getStackTrace()[1].toString());
        }

        if (current == CLOSE_CURRENT_RESULT) {
            closeExistingResultSet();
        }

        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int executeUpdate(String sql, String columnNames[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        checkClosed();
        if (autoGeneratedKeys == NO_GENERATED_KEYS) {
            return execute(sql);
        }
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean execute(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean execute(String sql, String columnNames[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        checkClosed();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        checkClosed();
        return false;
    }

    // --------------------------JDBC 4.1 -----------------------------

    @Override
    public void closeOnCompletion() throws SQLException {
        checkClosed();
        closeOnCompletion = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        checkClosed();
        return closeOnCompletion;
    }

    // --------------------------JDBC 4.2 -----------------------------

    @Override
    public long getLargeUpdateCount() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public long executeLargeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public long executeLargeUpdate(String sql, String columnNames[]) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // java.sql.Wrapper impl
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
