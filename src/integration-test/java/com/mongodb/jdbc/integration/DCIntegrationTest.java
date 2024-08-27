package com.mongodb.jdbc.integration;


import com.mongodb.jdbc.MongoConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DCIntegrationTest {

    /** Tests that the driver can work with SRV-style URIs. */
    @Test
    public void testConnectWithSRVURI() throws SQLException {
        String mongoHost = System.getenv("SRV_TEST_HOST");
        assertNotNull(mongoHost, "SRV_TEST_HOST variable not set in environment");
        String mongoURI =
                "mongodb+srv://"
                        + mongoHost
                        + "/?readPreference=secondaryPreferred&connectTimeoutMS=300000";
        String fullURI = "jdbc:" + mongoURI;

        String user = System.getenv("SRV_TEST_USER");
        assertNotNull(user, "SRV_TEST_USER variable not set in environment");
        String pwd = System.getenv("SRV_TEST_PWD");
        assertNotNull(pwd, "SRV_TEST_PWD variable not set in environment");
        String authSource = System.getenv("SRV_TEST_AUTH_DB");
        assertNotNull(authSource, "SRV_TEST_AUTH_DB variable not set in environment");

        Properties p = new java.util.Properties();
        p.setProperty("user", user);
        p.setProperty("password", pwd);
        p.setProperty("authSource", authSource);
        p.setProperty("database", "test");

        // TODO: SQL-2294: Support direct cluster mode (This should no longer expect an exception after that).
        assertThrows(
                java.sql.SQLException.class,
                () -> {
                    MongoConnection conn =
                            (MongoConnection) DriverManager.getConnection(fullURI, p);
                });
    }

    // todo:
    //   - test community cluster expects SQLException
    //   - test enterprise cluster expects successful connection
}
