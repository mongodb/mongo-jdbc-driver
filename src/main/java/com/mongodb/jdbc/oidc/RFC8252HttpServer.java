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

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RFC8252HttpServer class implements an OIDC (OpenID Connect) server based on RFC 8252. It
 * handles the OIDC authorization code flow by providing endpoints for the callback and redirection.
 * The server listens on a specified port (default is 27017) and processes incoming HTTP requests.
 */
public class RFC8252HttpServer {
    public static final int DEFAULT_REDIRECT_PORT = 27097;

    // SQL-2008: make sure this page exists and possibly update the link if the
    // docs team has a preference
    private static final String LOGIN_ERROR_URI =
            "https://www.mongodb.com/docs/atlas/security-oidc";
    private static final String PRODUCT_DOCS_LINK =
            "https://www.mongodb.com/docs/sql-interface/install-driver/";
    private static final String PRODUCT_DOCS_NAME = "Atlas SQL ODBC Driver";

    // OIDC response parameters
    private static final String CODE = "code";
    private static final String LOCATION = "Location";
    private static final String STATE = "state";

    // template variables
    private static final String PRODUCT_DOCS_LINK_KEY = "product_docs_link";
    private static final String PRODUCT_DOCS_NAME_KEY = "product_docs_name";
    private static final String ERROR_URI_KEY = "error_uri";
    private static final String ERROR_KEY = "error";
    private static final String ERROR_DESCRIPTION_KEY = "error_description";

    // server endpoints
    private static final String ACCEPTED_ENDPOINT = "/accepted";
    private static final String CALLBACK_ENDPOINT = "/callback";
    private static final String REDIRECT_ENDPOINT = "/redirect";

    // templates, loaded from src/main/resources/templates/<name>.html
    private static final String ACCEPTED_TEMPLATE = "OIDCAcceptedTemplate";
    private static final String ERROR_TEMPLATE = "OIDCErrorTemplate";
    private static final String NOT_FOUND_TEMPLATE = "OIDCNotFoundTemplate";

    private HttpServer server;
    private final Template acceptedTemplate;
    private final Template errorTemplate;
    private final Template notFoundTemplate;
    private final BlockingQueue<OidcResponse> oidcResponseQueue;

    public RFC8252HttpServer() {
        acceptedTemplate = compileTemplate(ACCEPTED_TEMPLATE);
        errorTemplate = compileTemplate(ERROR_TEMPLATE);
        notFoundTemplate = compileTemplate(NOT_FOUND_TEMPLATE);
        oidcResponseQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Starts the HTTP server and sets up the necessary contexts and handlers.
     *
     * @throws IOException if an I/O error occurs while creating or starting the server
     */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(DEFAULT_REDIRECT_PORT), 0);

        server.createContext(CALLBACK_ENDPOINT, new CallbackHandler());
        server.createContext(REDIRECT_ENDPOINT, new CallbackHandler());
        server.createContext(ACCEPTED_ENDPOINT, new AcceptedHandler());
        server.setExecutor(null);
        server.start();
    }

    /**
     * Attempts to retrieve an OIDC response from the queue, waiting up to a default timeout of 300
     * seconds.
     *
     * @return the OIDC response, if available within the default timeout period
     * @throws InterruptedException if no response is available within the default timeout period
     */
    public OidcResponse getOidcResponse() throws InterruptedException, OidcTimeoutException {
        return getOidcResponse(Duration.ofSeconds(300));
    }

    /**
     * Attempts to retrieve an OIDC response from the queue, waiting up to the specified timeout. If
     * no response is available within the timeout period, an InterruptedException is thrown.
     *
     * @param timeout the maximum time to wait for an OIDC response, in seconds
     * @return the OIDC response, if available within the timeout period
     * @throws InterruptedException if no response is available within the timeout period or if the
     *     current thread is interrupted while waiting
     */
    public OidcResponse getOidcResponse(Duration timeout)
            throws OidcTimeoutException, InterruptedException {
        if (timeout == null) {
            return getOidcResponse();
        }
        OidcResponse response = oidcResponseQueue.poll(timeout.getSeconds(), TimeUnit.SECONDS);
        if (response == null) {
            throw new OidcTimeoutException("Timeout waiting for OIDC response");
        }
        return response;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * Loads and compiles the named Mustache template from the classpath. The compiler HTML-escapes
     * all {{variable}} substitutions and throws if a template references a missing variable.
     *
     * @param name the template name, without the "/templates/" prefix or ".html" suffix
     * @return the compiled template
     * @throws IllegalStateException if the template resource does not exist
     * @throws UncheckedIOException if an I/O error occurs while reading the template
     */
    private static Template compileTemplate(String name) {
        String path = "/templates/" + name + ".html";
        InputStream in = RFC8252HttpServer.class.getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("Template not found: " + path);
        }
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return Mustache.compiler().compile(reader);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read template: " + path, e);
        }
    }

    /** HTTP handler for handling the callback and redirect endpoints. */
    private class CallbackHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> queryParams = parseQueryParams(exchange);
            OidcResponse oidcResponse = new OidcResponse();

            if (queryParams.containsKey(CODE)) {
                oidcResponse.setCode(queryParams.get(CODE));
                oidcResponse.setState(queryParams.getOrDefault(STATE, ""));
                if (!putOidcResponse(exchange, oidcResponse)) {
                    return;
                }
                // This will hide the code and state from the URL bar by doing a redirect
                // to the /accepted page rather than rendering the accepted page directly
                exchange.getResponseHeaders().set(LOCATION, ACCEPTED_ENDPOINT);
                sendResponse(exchange, "", HttpURLConnection.HTTP_MOVED_TEMP);
            } else if (queryParams.containsKey(ERROR_KEY)) {
                oidcResponse.setError(queryParams.get(ERROR_KEY));
                oidcResponse.setErrorDescription(
                        queryParams.getOrDefault(ERROR_DESCRIPTION_KEY, "Unknown error"));
                if (!putOidcResponse(exchange, oidcResponse)) {
                    return;
                }
                Map<String, String> context = new HashMap<>();
                context.put(ERROR_URI_KEY, LOGIN_ERROR_URI);
                context.put(PRODUCT_DOCS_LINK_KEY, PRODUCT_DOCS_LINK);
                context.put(PRODUCT_DOCS_NAME_KEY, PRODUCT_DOCS_NAME);
                context.put(ERROR_KEY, queryParams.get(ERROR_KEY));
                context.put(
                        ERROR_DESCRIPTION_KEY,
                        queryParams.getOrDefault(ERROR_DESCRIPTION_KEY, "Unknown error"));
                String errorHtml = errorTemplate.execute(context);
                sendResponse(exchange, errorHtml, HttpURLConnection.HTTP_BAD_REQUEST);

            } else {
                oidcResponse.setError("Not found");
                String allParams =
                        queryParams
                                .entrySet()
                                .stream()
                                .map(entry -> entry.getKey() + "=" + entry.getValue())
                                .reduce((param1, param2) -> param1 + ", " + param2)
                                .orElse("No parameters");
                oidcResponse.setErrorDescription("Not found. Parameters: " + allParams);
                if (!putOidcResponse(exchange, oidcResponse)) {
                    return;
                }
                Map<String, String> context = new HashMap<>();
                context.put(PRODUCT_DOCS_LINK_KEY, PRODUCT_DOCS_LINK);
                context.put(PRODUCT_DOCS_NAME_KEY, PRODUCT_DOCS_NAME);
                String notFoundHtml = notFoundTemplate.execute(context);
                sendResponse(exchange, notFoundHtml, HttpURLConnection.HTTP_NOT_FOUND);
            }
        }
    }

    /** HTTP handler for handling the accepted endpoint. */
    private class AcceptedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> context = new HashMap<>();
            context.put(PRODUCT_DOCS_LINK_KEY, PRODUCT_DOCS_LINK);
            context.put(PRODUCT_DOCS_NAME_KEY, PRODUCT_DOCS_NAME);
            String acceptedHtml = acceptedTemplate.execute(context);
            sendResponse(exchange, acceptedHtml, HttpURLConnection.HTTP_OK);
        }
    }

    /**
     * Parses the query parameters from the HTTP exchange.
     *
     * @param exchange the HTTP exchange
     * @return a map containing the parsed query parameters
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    private Map<String, String> parseQueryParams(HttpExchange exchange)
            throws UnsupportedEncodingException {
        Map<String, String> queryParams = new HashMap<>();
        String rawQuery = exchange.getRequestURI().getRawQuery();

        if (rawQuery != null) {
            String[] params = rawQuery.split("&");
            for (String param : params) {
                int equalsIndex = param.indexOf('=');
                if (equalsIndex > 0) {
                    String key = param.substring(0, equalsIndex);
                    String encodedValue = param.substring(equalsIndex + 1);
                    String value = URLDecoder.decode(encodedValue, "UTF-8");
                    queryParams.put(key, value);
                } else {
                    queryParams.put(param, "");
                }
            }
        }
        return queryParams;
    }

    /**
     * Puts the OIDC response into the blocking queue. If the queue is full, an error response is
     * sent to the client and the HttpExchange is closed.
     *
     * @param exchange the HTTP exchange
     * @param oidcResponse the OIDC response to put into the queue
     * @return true if the response was successfully put into the queue, false otherwise
     * @throws IOException if an I/O error occurs while sending a response
     */
    private boolean putOidcResponse(HttpExchange exchange, OidcResponse oidcResponse)
            throws IOException {
        try {
            oidcResponseQueue.put(oidcResponse);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // sendResponse will close the exchange
            sendResponse(exchange, "<html><body><h1>Internal Server Error</h1></body></html>", 500);
            return false;
        }
    }

    /**
     * Sends an HTTP response with the specified content and status code.
     *
     * @param exchange the HTTP exchange
     * @param response the response content
     * @param statusCode the HTTP status code
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendResponse(HttpExchange exchange, String response, int statusCode)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        try {
            exchange.sendResponseHeaders(
                    statusCode, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(RFC8252HttpServer.class.getName());
            logger.log(Level.SEVERE, "Error sending response", e);
            throw e;
        } finally {
            exchange.close();
        }
    }
}
