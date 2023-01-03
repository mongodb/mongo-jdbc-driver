package com.mongodb.jdbc.utils;

import com.mongodb.ConnectionString;
import com.mongodb.jdbc.Pair;
import org.junit.jupiter.api.Test;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

import static com.mongodb.jdbc.MongoDriver.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

  @Test
  void testLocalHost() throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);
    p.setProperty(DATABASE, DB);

    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(localhost, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), DB);
  }

  @Test
  void testLocalHostWithOnlyDBNoPropsDB() throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);

    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(localhostWithOnlyDB, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), "authDB");
  }

  @Test
  void testPropsDBOverridesURIDBNoAuthSource()  throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);
    p.setProperty(DATABASE, DB);

    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(localhostWithOnlyDB, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), DB);
    assertEquals(result.left().getDatabase(), DB);
  }

  @Test
  void testPropsDBWithURIAuthSource()  throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);
    p.setProperty(DATABASE, DB);

    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(onlyAuthSource, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), AUTHDB);
    assertEquals(result.left().getDatabase(), DB);
  }

  @Test
  void testUriDBWithAuthSource() throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);

    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(dbAndAuthSource, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), AUTHDB);
    assertEquals(result.left().getDatabase(), "pouet");
  }

  @Test
  void testPropsOverrideURIDBWithAuthSource() throws Exception {
    Properties p = new Properties();
    p.setProperty(USER_CONN_KEY, USER);
    p.setProperty(PWD_CONN_KEY, PWD);
    p.setProperty(DATABASE, DB);
    Pair<ConnectionString, DriverPropertyInfo[]> result = getConnectionSettings(dbAndAuthSource, p);

    assertEquals(result.left().getCredential().getUserName(), USER);
    assertEquals(result.left().getCredential().getSource(), AUTHDB);
    assertEquals(result.left().getDatabase(), DB);
  }

}
