package com.mongodb.jdbc.integration;


import com.mongodb.jdbc.MongoConnection;
import com.mongodb.jdbc.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;

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

        MongoConnection conn = (MongoConnection) DriverManager.getConnection(fullURI, p);
        conn.close();
    }

    /**
     * Gets information from the environment to create a connection to a local mongod.
     *
     * @param typeEnvVar Either "MDB_LOCAL_PORT_COM" or "MDB_LOCAL_PORT_ENT"
     * @return A (jdbc_uri, properties) pair with which to create a MongoConnection
     */
    private Pair<String, Properties> createLocalMongodConnInfo(String typeEnvVar) {
        String mongoPort = System.getenv(typeEnvVar);
        assertNotNull(mongoPort, typeEnvVar + " variable not set in environment");

        String uri = "jdbc:mongodb://localhost:" + mongoPort + "/test";

        String user = System.getenv("MDB_LOCAL_USER");
        assertNotNull(user, "MDB_LOCAL_USER variable not set in environment");
        String pwd = System.getenv("MDB_LOCAL_PWD");
        assertNotNull(pwd, "MDB_LOCAL_PWD variable not set in environment");

        Properties p = new java.util.Properties();
        p.setProperty("user", user);
        p.setProperty("password", pwd);
        p.setProperty("authSource", "admin");
        p.setProperty("database", "test");

        return new Pair<>(uri, p);
    }

    /** Tests that the driver rejects the community edition of the server. */
    @Test
    public void testConnectionToCommunityServerFails() {
        Pair<String, Properties> info = createLocalMongodConnInfo("LOCAL_MDB_PORT_COM");

        assertThrows(
                java.sql.SQLException.class,
                () -> {
                    MongoConnection conn =
                            (MongoConnection) DriverManager.getConnection(info.left(), info.right());
                });
    }

    /** Tests that the driver connects to the enterprise edition of the server. */
    @Test
    public void testConnectionToEnterpriseServerSucceeds() throws SQLException {
        Pair<String, Properties> info = createLocalMongodConnInfo("LOCAL_MDB_PORT_ENT");
        MongoConnection conn = (MongoConnection) DriverManager.getConnection(info.left(), info.right());
        conn.close();
    }
}
