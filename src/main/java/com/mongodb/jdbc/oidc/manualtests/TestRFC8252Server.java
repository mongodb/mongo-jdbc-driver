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

package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.OidcResponse;
import com.mongodb.jdbc.oidc.OidcTimeoutException;
import com.mongodb.jdbc.oidc.RFC8252HttpServer;
import java.io.IOException;
/**
 * Main class to start the RFC8252HttpServer and wait for the OIDC response Used for testing the
 * serving of the HTML pages and the OIDC response
 */
public class TestRFC8252Server {
    public static void main(String[] args) {
        int port = RFC8252HttpServer.DEFAULT_REDIRECT_PORT;
        RFC8252HttpServer server = new RFC8252HttpServer();
        try {
            server.start();
            System.out.println("Server started on port " + port);

            // Wait for the OIDC response
            OidcResponse oidcResponse = server.getOidcResponse();
            System.out.println("Server Result:\n" + oidcResponse.toString());

            Thread.sleep(2000);
        } catch (IOException | OidcTimeoutException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
