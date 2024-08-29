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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MongoSQLTranslateLibTest {
    @BeforeEach
    void setup() throws Exception {
        // Reset the mongoSqlTranslateLibraryLoaded flag to false before each test case.
        // This ensures that the flag starts with a known value at the start of the test
        // as it can be set during the static initialization or test interference.
        Field field = MongoDriver.class.getDeclaredField("mongoSqlTranslateLibraryLoaded");
        field.setAccessible(true);
        field.set(null, false);
    }

    @Test
    void testLibraryLoadingFromDriverPath() throws Exception {
        assertNull(
                System.getenv(MongoDriver.MONGOSQL_TRANSLATE_PATH),
                "MONGOSQL_TRANSLATE_PATH should not be set");

        Method initMethod =
                MongoDriver.class.getDeclaredMethod("initializeMongoSqlTranslateLibrary");
        initMethod.setAccessible(true);
        initMethod.invoke(null);

        assertTrue(
                MongoDriver.isMongoSqlTranslateLibraryLoaded(),
                "Library should be loaded successfully from the driver directory");

        MongosqlLibTest test = new MongosqlLibTest();
        test.testRunCommand();
    }

    @Test
    void testLibraryLoadingWithEnvironmentVariable() throws Exception {
        String envPath = System.getenv(MongoDriver.MONGOSQL_TRANSLATE_PATH);
        assertNotNull(envPath, "MONGOSQL_TRANSLATE_PATH should be set");

        // Test initializeMongoSqlTranslateLibrary, with Environment variable set it should find the library
        Method initMethod =
                MongoDriver.class.getDeclaredMethod("initializeMongoSqlTranslateLibrary");
        initMethod.setAccessible(true);
        initMethod.invoke(null);

        assertTrue(
                MongoDriver.isMongoSqlTranslateLibraryLoaded(),
                "Library should be loaded when MONGOSQL_TRANSLATE_PATH is set");

        MongosqlLibTest test = new MongosqlLibTest();
        test.testRunCommand();
    }
}
