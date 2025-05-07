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

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.mongosql.GetMongosqlTranslateVersionResult;
import com.mongodb.jdbc.mongosql.MongoSQLException;
import com.mongodb.jdbc.mongosql.MongoSQLTranslate;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoSQLTranslateLibTest {

    /** Helper function to call the runCommand endpoint of the translation library. */
    private static void testRunCommand() throws MongoSQLException, MongoSerializationException {
        MongoLogger mongoLogger = new MongoLogger(Logger.getLogger("Logger"), 1);
        MongoSQLTranslate mongosqlTranslate = new MongoSQLTranslate(mongoLogger);

        GetMongosqlTranslateVersionResult result = mongosqlTranslate.getMongosqlTranslateVersion();

        assertNotNull(result);
        assertNotNull(result.version);
    }

    @BeforeEach
    void setup() throws Exception {
        // Reset the mongoSqlTranslateLibraryLoaded flag to false before each test case.
        // This ensures that the flag starts with a known value at the start of the test
        // as it can be set during the static initialization or test interference.
        Field mongoSqlTranslateLibraryLoadedField =
                MongoDriver.class.getDeclaredField("mongoSqlTranslateLibraryLoaded");
        mongoSqlTranslateLibraryLoadedField.setAccessible(true);
        mongoSqlTranslateLibraryLoadedField.set(null, false);

        Field mongoSqlTranslateLibraryPathField =
                MongoDriver.class.getDeclaredField("mongoSqlTranslateLibraryPath");
        mongoSqlTranslateLibraryPathField.setAccessible(true);
        mongoSqlTranslateLibraryPathField.set(null, null);

        Field mongoSqlTranslateLibraryLoadingError =
                MongoDriver.class.getDeclaredField("mongoSqlTranslateLibraryLoadingError");
        mongoSqlTranslateLibraryPathField.setAccessible(true);
        mongoSqlTranslateLibraryPathField.set(null, null);
    }

    @Test
    void testLibraryLoadingFromDriverPath() throws Exception {
        assertNull(
                System.getenv(MongoDriver.MONGOSQL_TRANSLATE_PATH),
                "MONGOSQL_TRANSLATE_PATH should not be set");

        Method initMethod = MongoDriver.class.getDeclaredMethod("loadMongoSqlTranslateLibrary");
        initMethod.setAccessible(true);
        initMethod.invoke(null);

        assertTrue(
                MongoDriver.isMongoSqlTranslateLibraryLoaded(),
                "Library should be loaded successfully from the driver directory");
        String tempDir = System.getProperty("java.io.tmpdir");
        assertTrue(
                MongoDriver.getMongoSqlTranslateLibraryPath().contains(tempDir),
                "Expected library path to contain '"
                        + tempDir
                        + "' but didn't. Actual path is "
                        + MongoDriver.getMongoSqlTranslateLibraryPath());

        // The library was loaded successfully. Now, let's make sure that we can call the runCommand endpoint.
        testRunCommand();
    }

    @Test
    void testLibraryLoadingWithEnvironmentVariable() throws Exception {
        String envPath = System.getenv(MongoDriver.MONGOSQL_TRANSLATE_PATH);
        assertNotNull(envPath, "MONGOSQL_TRANSLATE_PATH should be set");

        // Test loadMongoSqlTranslateLibrary, with Environment variable set it should find the library
        Method initMethod = MongoDriver.class.getDeclaredMethod("loadMongoSqlTranslateLibrary");
        initMethod.setAccessible(true);
        initMethod.invoke(null);

        assertNull(MongoDriver.getMongoSqlTranslateLibraryLoadError());

        assertTrue(
                MongoDriver.isMongoSqlTranslateLibraryLoaded(),
                "Library should be loaded when MONGOSQL_TRANSLATE_PATH is set");

        assertTrue(
                MongoDriver.getMongoSqlTranslateLibraryPath()
                        .contains("resources/MongoSqlLibraryTest"),
                "Expected library path to contain 'resources/MongoSqlLibraryTest' but didn't. Actual path is "
                        + MongoDriver.getMongoSqlTranslateLibraryPath());

        // The library was loaded successfully. Now, let's make sure that we can call the runCommand endpoint.
        testRunCommand();
    }

    @Test
    void testLibraryLoadingWithInvalidEnvironmentVariableFallback() throws Exception {
        String envPath = System.getenv(MongoDriver.MONGOSQL_TRANSLATE_PATH);
        assertNotNull(envPath, "MONGOSQL_TRANSLATE_PATH should be set");

        // Test loadMongoSqlTranslateLibrary, with invalid Environment variable set should fallback to driver directory
        Method initMethod = MongoDriver.class.getDeclaredMethod("loadMongoSqlTranslateLibrary");
        initMethod.setAccessible(true);
        initMethod.invoke(null);

        assertNotNull(MongoDriver.getMongoSqlTranslateLibraryLoadError());

        assertTrue(
                MongoDriver.getMongoSqlTranslateLibraryLoadError()
                        .getMessage()
                        .contains("java.lang.UnsatisfiedLinkError: Can't load library"),
                "Expected error to be a loading error but is "
                        + MongoDriver.getMongoSqlTranslateLibraryLoadError().getMessage());

        // The library must be loaded and it should be the one from inside the driver.
        assertTrue(
                MongoDriver.isMongoSqlTranslateLibraryLoaded(),
                "Library should be loaded successfully from the driver directory");
        String tempDir = System.getProperty("java.io.tmpdir");
        assertTrue(
                MongoDriver.getMongoSqlTranslateLibraryPath().contains(tempDir),
                "Expected library path to contain '"
                        + tempDir
                        + "' but didn't. Actual path is "
                        + MongoDriver.getMongoSqlTranslateLibraryPath());

        // The library was loaded successfully. Now, let's make sure that we can call the runCommand endpoint.
        testRunCommand();
    }
}
