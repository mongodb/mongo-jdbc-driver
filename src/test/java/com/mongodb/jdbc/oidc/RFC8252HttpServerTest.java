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

package com.mongodb.jdbc.oidc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RFC8252HttpServerTest {
    private static final String PRODUCT_DOCS_HREF =
            "href=\"https://www.mongodb.com/docs/atlas/data-federation/query/sql/drivers/odbc/connect\"";
    private static final String ERROR_URI_HREF =
            "href=\"https://www.mongodb.com/docs/atlas/security-oidc\"";
    private static final String PRODUCT_DOCS_TEXT = "Atlas SQL ODBC Driver Documentation";

    private RFC8252HttpServer server;

    /** Reads the response body, using the error stream for 4xx/5xx responses. */
    private static String readBody(HttpURLConnection connection) throws IOException {
        InputStream stream =
                connection.getResponseCode() >= 400
                        ? connection.getErrorStream()
                        : connection.getInputStream();
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        server = new RFC8252HttpServer();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAcceptedResponse() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertNull(connection.getHeaderField("Location"));

        String body = readBody(connection);
        assertTrue(body.contains("Login Successful"));
        assertTrue(body.contains(PRODUCT_DOCS_TEXT));
        assertTrue(body.contains(PRODUCT_DOCS_HREF));
        assertFalse(body.contains("Authentication Failed"));

        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo", oidcResponse.getState());
    }

    @Test
    void testErrorResponse() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?error=1234&error_description=foo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(400, connection.getResponseCode());

        String body = readBody(connection);
        assertTrue(body.contains("Authentication Failed"));
        assertTrue(body.contains(ERROR_URI_HREF));
        assertTrue(body.contains(PRODUCT_DOCS_TEXT));
        assertTrue(body.contains(PRODUCT_DOCS_HREF));

        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getError());
        assertEquals("foo", oidcResponse.getErrorDescription());
    }

    @Test
    void testMissingParameters() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(404, connection.getResponseCode());

        String body = readBody(connection);
        assertTrue(body.contains("Page Not Found"));
        assertTrue(body.contains(PRODUCT_DOCS_TEXT));
        assertTrue(body.contains(PRODUCT_DOCS_HREF));

        OidcResponse oidcResponse = server.getOidcResponse();
        assertNull(oidcResponse.getCode());
        assertNull(oidcResponse.getState());
        assert (oidcResponse.getError().equals("Not found"));
        assert (oidcResponse.getErrorDescription().equals("Not found. Parameters: No parameters"));
    }

    @Test
    void testRedirectUnknownParameters()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/redirect?foo=bar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(404, connection.getResponseCode());

        OidcResponse oidcResponse = server.getOidcResponse();
        assertNull(oidcResponse.getCode());
        assertNull(oidcResponse.getState());
        assert (oidcResponse.getError().equals("Not found"));
        assert (oidcResponse.getErrorDescription().equals("Not found. Parameters: foo=bar"));
    }

    @Test
    void testAmpersandInParameterValue()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo%26bar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo&bar", oidcResponse.getState());
    }

    @Test
    void testEqualsInParameterValue()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo%3Dbar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo=bar", oidcResponse.getState());
    }
}
