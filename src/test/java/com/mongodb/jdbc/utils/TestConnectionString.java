/*
 * Copyright 2023-present MongoDB, Inc.
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

package com.mongodb.jdbc.utils;

import static com.mongodb.jdbc.MongoDriver.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class TestConnectionString {
    static final String localhost = "mongodb://localhost";
    static final String localhostWithOnlyDB = "mongodb://localhost/authDB";
    static final String onlyAuthSource = "mongodb://localhost/?authSource=authDB";
    static final String dbAndAuthSource = "mongodb://localhost/pouet?authSource=authDB";
    static final String USER_CONN_KEY = "user";
    static final String PWD_CONN_KEY = "password";
    static final String USER = "AzureDiamond";
    static final String PWD = "hunter2";
    static final String DATABASE = "database";
    static final String DB = "foo";
    static final String AUTHDB = "authDB";
    static final String POUET = "pouet";

    @Test
    void testLocalHost() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DATABASE, DB);

        MongoConnectionConfig result = getConnectionSettings(localhost, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(DB, result.connectionString.getCredential().getSource());
    }

    @Test
    void testLocalHostWithOnlyDBNoPropsDB() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);

        MongoConnectionConfig result = getConnectionSettings(localhostWithOnlyDB, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(AUTHDB, result.connectionString.getCredential().getSource());
    }

    @Test
    void testPropsDBOverridesURIDBNoAuthSource() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DATABASE, DB);

        MongoConnectionConfig result = getConnectionSettings(localhostWithOnlyDB, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(DB, result.connectionString.getCredential().getSource());
        assertEquals(DB, result.connectionString.getDatabase());
    }

    @Test
    void testPropsDBWithURIAuthSource() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DATABASE, DB);

        MongoConnectionConfig result = getConnectionSettings(onlyAuthSource, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(AUTHDB, result.connectionString.getCredential().getSource());
        assertEquals(DB, result.connectionString.getDatabase());
    }

    @Test
    void testUriDBWithAuthSource() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);

        MongoConnectionConfig result = getConnectionSettings(dbAndAuthSource, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(AUTHDB, result.connectionString.getCredential().getSource());
        assertEquals(POUET, result.connectionString.getDatabase());
    }

    @Test
    void testPropsOverrideURIDBWithAuthSource() throws Exception {
        Properties p = new Properties();
        p.setProperty(USER_CONN_KEY, USER);
        p.setProperty(PWD_CONN_KEY, PWD);
        p.setProperty(DATABASE, DB);

        MongoConnectionConfig result = getConnectionSettings(dbAndAuthSource, p);

        assertEquals(USER, result.connectionString.getCredential().getUserName());
        assertEquals(AUTHDB, result.connectionString.getCredential().getSource());
        assertEquals(DB, result.connectionString.getDatabase());
    }
}
