package com.mongodb.jdbc;

import com.mongodb.MongoClientURI;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The interface that every driver class must implement.
 *
 * <p>The Java SQL framework allows for multiple database drivers.
 *
 * <p>Each driver should supply a class that implements the Driver interface.
 *
 * <p>This Driver implements the JDBC driver for the SQL interface to MongoDB.
 *
 * @see DriverManager
 * @see Connection
 * @see DriverAction
 * @since 1.1
 */
public class MongoDriver implements Driver {
    /** All MongoDB SQL URLs must begine with jdbc:mongodb: */
    static final String JDBC = "jdbc:";

    static final String MONGODB_URL_PREFIX = JDBC + "mongodb:";
    static final String LOGGER = "logger";
    static final String USER = "user";
    static final String PASSWORD = "password";
    // database is the database to switch to.
    static final String DATABASE = "database";
    // authDatabase is the database to authenticate against.
    static final String AUTH_DATABASE = "authDatabase";

    private Logger logger;
    private DriverPropertyInfo[] propertyInfo;
    private MongoClientURI clientURI;

    static {
        try {
            DriverManager.registerDriver(new MongoDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to make a database connection to the given URL. The driver will return "null" if it
     * realizes it is the wrong kind of driver to connect to the given URL. This will be common, as
     * when the JDBC driver manager is asked to connect to a given URL it passes the URL to each
     * loaded driver in turn.
     *
     * <p>The driver will throw a <code>SQLException</code> if it is the right driver to connect to
     * the given URL but has trouble connecting to the database.
     *
     * <p>The {@code Properties} argument can be used to pass arbitrary string tag/value pairs as
     * connection arguments. Normally at least "user" and "password" properties should be included
     * in the {@code Properties} object.
     *
     * <p><B>Note:</B> If a property is specified as part of the {@code url} and is also specified
     * in the {@code Properties} object, it is implementation-defined as to which value will take
     * precedence. For maximum portability, an application should only specify a property once. The
     * MongoDB driver prefers the properties to the values in the URL.
     *
     * @param url the URL of the database to which to connect
     * @param info a list of arbitrary string tag/value pairs as connection arguments. Normally at
     *     least a "user" and "password" property should be included.
     * @return a <code>Connection</code> object that represents a connection to the URL
     * @exception SQLException if a database access error occurs or the url is {@code null}
     * @see getPropertyInfo for a list of possible properties.
     */
    public Connection connect(String url, Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        String loggerString = info.getProperty(LOGGER);
        if (loggerString != null) {
            logger = Logger.getLogger(loggerString);
        } else {
            logger = Logger.getLogger("");
        }
        // reuse the code getPropertyInfo to make sure the URI is properly set wrt the passed
        // Properties info value.
        DriverPropertyInfo[] shouldBeEmpty = getPropertyInfo(url, info);
        // since the user is calling connect, we should throw an SQLException if we get
        // a prompt back. Inspect the return value to format the SQLException.
        if (shouldBeEmpty.length != 0) {
            if (shouldBeEmpty[0].name.equals(USER)) {
                throw new SQLException("password specified without username");
            }
            if (shouldBeEmpty[0].name.equals(PASSWORD)) {
                throw new SQLException("username specified without password");
            }
        }
        return new MongoConnection(this.clientURI, logger, info.getProperty(DATABASE));
    }

    /**
     * Retrieves whether the driver thinks that it can open a connection to the given URL.
     *
     * @param url the URL of the database
     * @return <code>true</code> if this URL starts with <code>jdbc:mongodb:</code>; <code>false
     *     </code> otherwise
     * @exception SQLException if a database access error occurs or the url is {@code null}
     */
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(MONGODB_URL_PREFIX);
    }

    /**
     * Gets information about the possible properties for this driver.
     *
     * <p>The <code>getPropertyInfo</code> method is intended to allow a generic GUI tool to
     * discover what properties it should prompt a human for in order to get enough information to
     * connect to a database. Note that depending on the values the human has supplied so far,
     * additional values may become necessary, so it may be necessary to iterate though several
     * calls to the <code>getPropertyInfo</code> method.
     *
     * @param url the URL of the database to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on connect open
     * @return an array of <code>DriverPropertyInfo</code> objects describing possible properties.
     *     This array may be an empty array if no properties are required.
     * @exception SQLException if a database access error occurs
     */
    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
            throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        String actualURL = removePrefix(JDBC, url);
        MongoClientURI originalClientURI;
        try {
            originalClientURI = new MongoClientURI(actualURL);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        Triple<String, char[], String> clientProperties =
                extractProperties(originalClientURI, info);
        String username = clientProperties.left();
        char[] pwd = clientProperties.middle();
        String database = clientProperties.right();
        // Attempt to get an options string from the url string, itself, so that we do
        // not need to format the options returned by the MongoClientURI.
        String optionString = null;
        String[] optionSplit =
                actualURL.split("[?]"); // split takes a regexp and '?' is a metachar.
        if (optionSplit.length > 1) {
            optionString = optionSplit[1];
        }
        // Handle username specified with no password.
        if (username == null) {
            if (pwd != null) {
                // username is null, but password is not, we must prompt for the username.
                // Note: The convention is actually to return DriverPropertyInfo objects
                // with null values, this is not a bug.
                return new DriverPropertyInfo[] {new DriverPropertyInfo(USER, null)};
            }
            // Here, username and password are both null, which is a valid URI that needs no more
            // info. Construct the clientURI and prompt for nothing.
            this.clientURI =
                    new MongoClientURI(
                            buildNewURI(originalClientURI, username, pwd, database, optionString));
            return new DriverPropertyInfo[] {};
        }
        // Handle password specified with no username.
        if (pwd == null) {
            // If pwd is null here, then user name must be non-null,because
            // the both null case is handled above. Prompt for the password.
            return new DriverPropertyInfo[] {new DriverPropertyInfo(PASSWORD, null)};
        }
        // If we are here, we must have both a username and password. So we have a valid URI state,
        // go ahead and construct it and prompt for nothing.
        this.clientURI =
                new MongoClientURI(
                        buildNewURI(originalClientURI, username, pwd, database, optionString));
        return new DriverPropertyInfo[] {};
    }

    /**
     * Retrieves the driver's major version number.
     *
     * @return this driver's major version number
     */
    public int getMajorVersion() {
        return 0;
    }

    /**
     * Gets the driver's minor version number.
     *
     * @return this driver's minor version number
     */
    public int getMinorVersion() {
        return 0;
    }

    /**
     * Reports whether this driver is a genuine JDBC Compliant&trade; driver. A driver may only
     * report <code>true</code> here if it passes the JDBC compliance tests; otherwise it is
     * required to return <code>false</code>.
     *
     * <p>JDBC compliance requires full support for the JDBC API and full support for SQL 92 Entry
     * Level.
     *
     * <p>As the MongoDB JDBC driver is a read-only driver, it cannot be JDBC compliant.
     *
     * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code> otherwise
     */
    public boolean jdbcCompliant() {
        return false;
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    /**
     * Return the parent Logger of all the Loggers used by this driver. This should be the Logger
     * farthest from the root Logger that is still an ancestor of all of the Loggers used by this
     * driver. Configuring this Logger will affect all of the log messages generated by the driver.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this driver
     * @throws SQLFeatureNotSupportedException if the driver does not use {@code java.util.logging}.
     * @since 1.7
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger;
    }

    // removePrefix removes a prefix from a String.
    private static String removePrefix(String prefix, String s) {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    private static interface NullCoalesce<T> {
        T coalesce(T left, T right);
    }
    // private helper function to abstract checking consistency between properties and the URI, and
    // grabbing the relevant data.
    //
    // throws SQLException if url and properties disagree on username or password.
    private static Triple<String, char[], String> extractProperties(
            MongoClientURI clientURI, Properties info) throws SQLException {

        // The coalesce function takse the first non-null argument, returning null only
        // if both arguments are null. The java type system requires us to write this twice,
        // once for each type we care about, unless we prefer to use Objects and cast, but I avoid
        // that.
        NullCoalesce<String> s =
                (left, right) -> {
                    if (left == null) {
                        return right;
                    }
                    return left;
                };

        NullCoalesce<char[]> c =
                (left, right) -> {
                    if (left == null) {
                        return right;
                    }
                    return left;
                };

        // grab the user and pwd from the URI.
        String uriUser = clientURI.getUsername();
        char[] uriPWD = clientURI.getPassword();
        String uriDatabase = clientURI.getDatabase();
        String propertyUser = info.getProperty(USER);
        String propertyPWDStr = info.getProperty(PASSWORD);
        char[] propertyPWD = propertyPWDStr != null ? propertyPWDStr.toCharArray() : null;
        String propertyDatabase = info.getProperty(AUTH_DATABASE);
        // handle disagreements on username.
        System.out.println(uriUser + ":" + propertyUser);
        if (uriUser != null && propertyUser != null && !uriUser.equals(propertyUser)) {
            throw new SQLException(
                    "uri and properties disagree on username: '"
                            + uriUser
                            + ", and "
                            + propertyUser
                            + " respectively");
        }
        // set the username
        String username = s.coalesce(uriUser, propertyUser);
        // handle disagreements on password.
        if (uriPWD != null && propertyPWD != null && !Arrays.equals(uriPWD, propertyPWD)) {
            throw new SQLException("uri and properties disagree on password");
        }
        // set the pwd
        char[] pwd = c.coalesce(uriPWD, propertyPWD);
        // handle disagreements on authentication database.
        if (uriDatabase != null
                && propertyDatabase != null
                && !uriDatabase.equals(propertyDatabase)) {
            throw new SQLException(
                    "uri and properties disagree on authentication database: '"
                            + uriDatabase
                            + ", and "
                            + propertyDatabase
                            + " respectively");
        }
        // set the authDatabase.
        String authDatabase = s.coalesce(uriDatabase, propertyDatabase);
        return new Triple<>(username, pwd, authDatabase);
    }

    // This is just a clean abstraction around URLEncode.
    private static String sqlURLEncode(String item) throws SQLException {
        try {
            return URLEncoder.encode(item, "utf-8");
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // This function builds a new uri from the original clientURI, adding username, password, options, and
    // database, if necessary.
    private static String buildNewURI(
            MongoClientURI originalClientURI,
            String username,
            char[] pwd,
            String database,
            String optionsString)
            throws SQLException {
        // The returned URI should be of the following format:
        //"mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]")
        String ret = "mongodb://";
        if (username != null) {
            // Note: if username is not null, we already know that pwd must also be not null.
            ret += sqlURLEncode(username) + ":" + sqlURLEncode(String.valueOf(pwd)) + "@";
        }
        // Now add hosts.
        ret += String.join(",", originalClientURI.getHosts());
        // Now add database, if necessary.
        if (database != null) {
            ret += "/" + sqlURLEncode(database);
        }
        // OptionsString should already be properly encoded, since we got it straight from the url
        // string.
        if (optionsString != null) {
            ret += "?" + optionsString;
        }
        return ret;
    }
}
