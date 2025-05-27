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

package com.mongodb.jdbc;

import static com.mongodb.jdbc.MongoDriver.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.ConnectionString;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
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

    // Tests for the work-around required to be able to parse URI when the username and password is mandatory but provided as part of the properties.
    @Test
    void testBuildConnectionStringWithMissingUidPwdForAllAuthMech() throws Exception {
        for (AuthenticationMechanism authMech : AuthenticationMechanism.values()) {
            String url =
                    localhostWithOnlyDB
                            + "?authSource=$external&authMechanism="
                            + authMech.getMechanismName();
            System.out.println(url);

            Properties p = new Properties();
            p.setProperty(USER_CONN_KEY, USER);
            if (authMech != AuthenticationMechanism.MONGODB_OIDC
                    && authMech != AuthenticationMechanism.MONGODB_X509) {
                p.setProperty(PWD_CONN_KEY, PWD);
            }

            ConnectionString result = buildConnectionString(url, p);

            // For PLAIN,SCRAM-SHA-1, SCRAM-SHA-256 AND GSSAPI the uri must be augmented with the username and password provided in the properties
            if (Arrays.asList(
                            AuthenticationMechanism.PLAIN,
                            AuthenticationMechanism.SCRAM_SHA_1,
                            AuthenticationMechanism.SCRAM_SHA_256,
                            AuthenticationMechanism.GSSAPI)
                    .contains(authMech)) {
                assertNotEquals(
                        result.getConnectionString(),
                        url,
                        "The original URL should have been augmented with the username and password information but it wasn't.");
                assertNotNull(result.getCredential());
                assertNotNull(result.getCredential().getAuthenticationMechanism());
                assertEquals(
                        authMech.getMechanismName(),
                        result.getCredential().getAuthenticationMechanism().getMechanismName());
                if (null != result.getCredential().getUserName()) {
                    assertEquals(USER, result.getCredential().getUserName());
                }
                if (null != result.getCredential().getPassword()) {
                    assertEquals(PWD, new String(result.getCredential().getPassword()));
                }
            } else {
                assertEquals(
                        result.getConnectionString(),
                        url,
                        "The original URL should stay unchanged");
            }
        }
    }

    /**
     * Validate that the MONGODB_URI_PATTERN is correct and work as expected.
     *
     * @param uri The uri to test.
     * @param shouldMatch True, if the uri should match the pattern. False otherwise.
     * @param hasUidPWd True, if the uri contains a username and/or password. False otherwise.
     * @param expectedAuthMech The expected authentication mechanism extracted from the uri.
     */
    void testPatternsHelper(
            String uri, boolean shouldMatch, boolean hasUidPWd, String expectedAuthMech) {
        Matcher uri_matcher = MONGODB_URI_PATTERN.matcher(uri);
        boolean match = uri_matcher.find();

        assertEquals(
                match,
                shouldMatch,
                "The URI "
                        + uri
                        + " matching result is not as expected. Expected: "
                        + shouldMatch
                        + " , Actual: "
                        + match);
        if (shouldMatch) {
            String uidpwd = uri_matcher.group("uidpwd");
            String options = uri_matcher.group("options");
            if (hasUidPWd) {
                assertNotNull(uidpwd, "No UID/PWD detected when expected. URI = " + uri);
            } else {
                assertNull(uidpwd, "UID/PWD detected when none expected. URI = " + uri);
            }

            if (options != null) {
                Matcher authMec_matcher = AUTH_MECH_TO_AUGMENT_PATTERN.matcher(options);
                match = authMec_matcher.find();
                assertEquals(
                        match,
                        expectedAuthMech != null,
                        "The authentication mechanism matching result is not as expected. Expected: "
                                + (expectedAuthMech != null)
                                + " , Actual: "
                                + match);
                if (match && expectedAuthMech != null) {
                    assertTrue(
                            match,
                            "No authentication mechanism was found in the URI when "
                                    + expectedAuthMech
                                    + " was expected.");
                    String authMech = authMec_matcher.group("authMech");
                    assertEquals(
                            authMech,
                            expectedAuthMech,
                            "Expected authentication mechanism "
                                    + expectedAuthMech
                                    + " but got "
                                    + authMech);
                }
            }
        }
    }

    @Test
    void testPatterns() {
        // Non matching URIs
        testPatternsHelper("mongodb:/localhost", false, false, null);
        testPatternsHelper("blabla", false, false, null);

        // No user name or password
        testPatternsHelper("mongodb://localhost", true, false, null);
        testPatternsHelper("mongodb://localhost?connectTimeoutms=600000", true, false, null);
        testPatternsHelper(
                "mongodb+srv://localhost?authSource=$external&connectTimeoutms=600000",
                true,
                false,
                null);

        // User name or password
        testPatternsHelper("mongodb://toto@localhost", true, true, null);
        testPatternsHelper("mongodb+srv://toto:tutu@localhost", true, true, null);
        testPatternsHelper(
                "mongodb+srv://toto@localhost?connectTimeoutms=600000", true, true, null);
        testPatternsHelper(
                "mongodb://toto:tutu@localhost?connectTimeoutms=600000", true, true, null);

        // No user name or password, with auth mech
        for (String authMech : MECHANISMS_TO_AUGMENT) {
            testPatternsHelper(
                    "mongodb://localhost?authSource=$external&authMechanism=" + authMech,
                    true,
                    false,
                    authMech);
        }
        testPatternsHelper(
                "mongodb://localhost?authSource=$external&authMechanism=MONGODB-OIDC",
                true,
                false,
                null);
        testPatternsHelper(
                "mongodb://localhost?authSource=$external&authMechanism=SCRAM_SHA_1",
                true,
                false,
                null);

        for (String authMech : MECHANISMS_TO_AUGMENT) {
            testPatternsHelper(
                    "mongodb://localhost?authSource=$external&authMechanism="
                            + authMech
                            + "&connectTimeoutms=600000",
                    true,
                    false,
                    authMech);
        }
        testPatternsHelper(
                "mongodb://localhost?authSource=$external&authMechanism=MONGODB-OIDC&connectTimeoutms=600000",
                true,
                false,
                null);
        for (String authMech : MECHANISMS_TO_AUGMENT) {
            testPatternsHelper(
                    "mongodb+srv://localhost?authMechanism="
                            + authMech
                            + "&authSource=$external&connectTimeoutms=600000",
                    true,
                    false,
                    authMech);
        }
        testPatternsHelper(
                "mongodb+srv://localhost?authMechanism=MONGODB-OIDC&authSource=$external&connectTimeoutms=600000",
                true,
                false,
                null);

        for (String authMech : MECHANISMS_TO_AUGMENT) {
            testPatternsHelper(
                    "mongodb://localhost?authMechanism=" + authMech, true, false, authMech);
        }
        testPatternsHelper("mongodb://localhost?authMechanism=MONGODB-OIDC", true, false, null);
    }
}
