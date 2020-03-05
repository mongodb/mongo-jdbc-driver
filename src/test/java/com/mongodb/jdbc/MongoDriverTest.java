package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.MongoDriver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDriverTest {
    static final String basicURL = "jdbc:mongodb://localhost";
    static final String authDBURL = "jdbc:mongodb://localhost/admin";
    static final String userNoPWDURL = "jdbc:mongodb://foo@localhost/admin";
    static final String userURL = "jdbc:mongodb://foo:bar@localhost";
    static final String jdbcUserURL = "jdbc:mongodb://jdbc:bar@localhost";
    // Even though ADL does not support replSets, this tests that we handle these URLs properly
    // for the future.
    static final String replURL = "jdbc:mongodb://foo:bar@localhost:27017,localhost:28910/admin";

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBasicURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Should not return null or throw, even with null properties.
        assertNotNull(d.connect(basicURL, null));

        Properties p = new Properties();
        assertNotNull(d.connect(basicURL, p));

        // user without password should throw.
        p.setProperty("user", "user");
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(basicURL, p);
                    }
                });

        // once property is set, it should be fine.
        p.setProperty("password", "pwd");
        assertNotNull(d.connect(basicURL, p));
    }

    @Test
    void testDBURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Should not return null or throw, even with null properties.
        assertNotNull(d.connect(authDBURL, null));

        Properties p = new Properties();
        assertNotNull(d.connect(authDBURL, p));

        p.setProperty("database", "admin2");

        // Database is not the same as the authDatabase in the uri.
        // So this is safe and should not throw.
        assertNotNull(d.connect(authDBURL, p));
    }

    @Test
    void testuserNoPWDURL() throws SQLException {
        MongoDriver d = new MongoDriver();

        // This will throw because the java driver will fail
        // to parse the URI.
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(userNoPWDURL, null);
                    }
                });
    }

    @Test
    void testJDBCURL() throws SQLException {
        MongoDriver d = new MongoDriver();

        assertNotNull(d.connect(jdbcUserURL, null));

        // changing user name from `jdbc` should throw.
        Properties p = new Properties();
        p.setProperty("user", "jdbc2");
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(jdbcUserURL, p);
                    }
                });
    }

    @Test
    void testUserURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Should not return null or throw, even with null properties.
        assertNotNull(d.connect(userURL, null));

        Properties p = new Properties();
        assertNotNull(d.connect(userURL, p));

        // This is not a mismatch, because we assume that if a auth database is missing
        // in the URI, even though default is admin, the user would prefer whatever is in
        // the passed Properties.
        p.setProperty("authDatabase", "admin2");
        assertNotNull(d.connect(userURL, p));

        Properties p2 = new Properties();
        p2.setProperty("user", "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(userURL, p2);
                    }
                });

        Properties p3 = new Properties();
        p3.setProperty("password", "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(userURL, p3);
                    }
                });
    }

    @Test
    void testReplURL() throws SQLException {
        MongoDriver d = new MongoDriver();
        // Should not return null or throw, even with null properties.
        assertNotNull(d.connect(replURL, null));

        Properties p = new Properties();
        assertNotNull(d.connect(replURL, p));

        Properties p2 = new Properties();
        p2.setProperty("user", "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(replURL, p2);
                    }
                });

        Properties p3 = new Properties();
        p3.setProperty("password", "dfasdfds");
        // user mismatch should throw.
        assertThrows(
                SQLException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        d.connect(replURL, p3);
                    }
                });
    }

    @Test
    void testGetPropertyInfo() throws SQLException {
        MongoDriver d = new MongoDriver();

        // Should not throw, even with null for Properties.
        DriverPropertyInfo[] res = d.getPropertyInfo(basicURL, null);
        assertEquals(res.length, 0);

        Properties p = new Properties();
        p.setProperty("user", "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(res.length, 1);
        assertEquals(res[0].name, "password");

        p = new Properties();
        p.setProperty("password", "hello");
        res = d.getPropertyInfo(basicURL, p);
        assertEquals(res.length, 1);
        assertEquals(res[0].name, "user");
    }
}
