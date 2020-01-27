package com.mongodb.jdbc;

import com.mongodb.MongoClientURI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
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
    static final String DATABASE = "database";

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
    public Connection connect(String url, java.util.Properties info) throws SQLException {
        String loggerString = info.getProperty(LOGGER);
        if (loggerString != null) {
            logger = Logger.getLogger(loggerString);
        } else {
            logger = Logger.getLogger("");
        }
        if (clientURI == null) {
            String actualURL = url.split(JDBC)[1];
            clientURI = new MongoClientURI(actualURL);
        }
        return new MongoConnection(clientURI, logger, info.getProperty(DATABASE));
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
        String actualUrl = url.split(JDBC)[1];
        clientURI = new MongoClientURI(actualUrl);
        // grab the user and pwd from the URI.
        String uriUser = clientURI.getUsername();
        char[] uriPWD = clientURI.getPassword();
        String propertyUser = info.getProperty(USER);
        char[] propertyPWD = info.getProperty(PASSWORD).toCharArray();
        // handle disagreements on username.
        if (uriUser != null && propertyUser != null && !uriUser.equals(propertyUser)) {
            throw new SQLException(
                    "uri and properties disagree on username: '"
                            + uriUser
                            + ", and "
                            + propertyUser
                            + " respectively");
        }
        // handle disagreements on password.
        if (uriPWD != null && propertyPWD != null && !Arrays.equals(uriPWD, propertyPWD)) {
            throw new SQLException("uri and properties disagree on password");
        }
        // handle username specified with no password.
        if (uriUser == null && propertyUser == null) {
            if (uriPWD != null || propertyPWD != null) {
                return new DriverPropertyInfo[] {new DriverPropertyInfo(USER, null)};
            }
            return new DriverPropertyInfo[] {};
        }
        // handle password specified with no username.
        if (uriPWD == null && propertyPWD == null) {
            if (uriUser != null || propertyUser != null) {
                return new DriverPropertyInfo[] {new DriverPropertyInfo(PASSWORD, null)};
            }
            return new DriverPropertyInfo[] {};
        }
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
}
