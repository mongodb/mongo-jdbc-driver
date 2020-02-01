package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Properties;

public class MongoDriver implements Driver {
    /** All MongoDB SQL URLs must begin with jdbc:mongodb: */
    static final String JDBC = "jdbc:";

    static final String MONGODB_URL_PREFIX = JDBC + "mongodb:";
    static final String MONGODB_SRV_URL_PREFIX = JDBC + "mongodb+srv:";
    static final String USER = "user";
    static final String PASSWORD = "password";
    // database is the database to switch to.
    static final String DATABASE = "database";
	static {
        try {
            DriverManager.registerDriver(new MongoDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        if (info == null) {
            info = new Properties();
        }
        // reuse the code getPropertyInfo to make sure the URI is properly set wrt the passed
        // Properties info value.
        Pair<ConnectionString, DriverPropertyInfo[]> p = getConnectionString(url, info);
        // since the user is calling connect, we should throw an SQLException if we get
        // a prompt back. Inspect the return value to format the SQLException.
        DriverPropertyInfo[] shouldBeEmpty = p.right();
        if (shouldBeEmpty.length != 0) {
            if (shouldBeEmpty[0].name.equals(USER)) {
                throw new SQLException("password specified without user");
            }
            if (shouldBeEmpty[0].name.equals(PASSWORD)) {
                throw new SQLException("user specified without password");
            }
            String[] propertyNames = new String[shouldBeEmpty.length];
            for (int i = 0; i < propertyNames.length; ++i) {
                propertyNames[i] = shouldBeEmpty[i].name;
            }
            throw new SQLException(
                    "unexpected property prompt(s) returned: " + String.join(", ", propertyNames));
        }
        return new MongoConnection(p.left(), info.getProperty(DATABASE));
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(MONGODB_URL_PREFIX) || url.startsWith(MONGODB_SRV_URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
            throws SQLException {
        Pair<ConnectionString, DriverPropertyInfo[]> p = getConnectionString(url, info);
        return p.right();
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    // removePrefix removes a prefix from a String.
    private static String removePrefix(String prefix, String s) {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    private Pair<ConnectionString, DriverPropertyInfo[]> getConnectionString(
            String url, java.util.Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        String actualURL = removePrefix(JDBC, url);
        ConnectionString originalConnectionString;
        try {
            originalConnectionString = new ConnectionString(actualURL);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        String authDatabase = originalConnectionString.getDatabase();
        Pair<String, char[]> clientProperties = extractProperties(originalConnectionString, info);
        String user = clientProperties.left();
        char[] password = clientProperties.right();
        // Attempt to get an options string from the url string, itself, so that we do
        // not need to format the options returned by the ConnectionString.
        String optionString = null;
        String[] optionSplit =
                actualURL.split("[?]"); // split takes a regexp and '?' is a metachar.
        if (optionSplit.length > 1) {
            optionString = optionSplit[1];
        }

        if (user == null && password == null) {
            ConnectionString c =
                    new ConnectionString(
                            buildNewURI(
                                    originalConnectionString,
                                    user,
                                    password,
                                    authDatabase,
                                    optionString));
            return new Pair<>(c, new DriverPropertyInfo[] {});
        }
        if (user == null) {
            // user is null, but password is not, we must prompt for the user.
            // Note: The convention is actually to return DriverPropertyInfo objects
            // with null values, this is not a bug.
            return new Pair<>(null, new DriverPropertyInfo[] {new DriverPropertyInfo(USER, null)});
        }
        if (password == null) {
            // If password is null here, then user name must be non-null,because
            // the both null case is handled above. Prompt for the password.
            return new Pair<>(
                    null, new DriverPropertyInfo[] {new DriverPropertyInfo(PASSWORD, null)});
        }
        // If we are here, we must have both a user and password. So we have a valid URI state,
        // go ahead and construct it and prompt for nothing.
        ConnectionString c =
                new ConnectionString(
                        buildNewURI(
                                originalConnectionString,
                                user,
                                password,
                                authDatabase,
                                optionString));
        return new Pair<>(c, new DriverPropertyInfo[] {});
    }

    private static interface NullCoalesce<T> {
        T coalesce(T left, T right);
    }
    // private helper function to abstract checking consistency between properties and the URI, and
    // grabbing the relevant data.
    //
    // throws SQLException if url and properties disagree on user or password.
    private static Pair<String, char[]> extractProperties(
            ConnectionString clientURI, Properties info) throws SQLException {

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

        // grab the user and password from the URI.
        String uriUser = clientURI.getUsername();
        char[] uriPWD = clientURI.getPassword();
        String uriAuthDatabase = clientURI.getDatabase();
        String propertyUser = info.getProperty(USER);
        String propertyPWDStr = info.getProperty(PASSWORD);
        char[] propertyPWD = propertyPWDStr != null ? propertyPWDStr.toCharArray() : null;
        // handle disagreements on user.
        if (uriUser != null && propertyUser != null && !uriUser.equals(propertyUser)) {
            throw new SQLException(
                    "uri and properties disagree on user: '"
                            + uriUser
                            + ", and "
                            + propertyUser
                            + " respectively");
        }
        // set the user
        String user = s.coalesce(uriUser, propertyUser);
        // handle disagreements on password.
        if (uriPWD != null && propertyPWD != null && !Arrays.equals(uriPWD, propertyPWD)) {
            throw new SQLException("uri and properties disagree on password");
        }
        // set the password
        char[] password = c.coalesce(uriPWD, propertyPWD);
        return new Pair<>(user, password);
    }

    // This is just a clean abstraction around URLEncode.
    private static String sqlURLEncode(String item) throws SQLException {
        try {
            return URLEncoder.encode(item, "utf-8");
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // This function builds a new uri from the original clientURI, adding user, password, options, and
    // database, if necessary.
    private static String buildNewURI(
            ConnectionString originalConnectionString,
            String user,
            char[] password,
            String authDatabase,
            String optionsString)
            throws SQLException {
        // The returned URI should be of the following format:
        //"mongodb://[user:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[authDatabase][?options]]")
        String ret = "mongodb://";
        if (user != null) {
            // Note: if user is not null, we already know that password must also be not null.
            ret += sqlURLEncode(user) + ":" + sqlURLEncode(String.valueOf(password)) + "@";
        }
        // Now add hosts.
        ret += String.join(",", originalConnectionString.getHosts());
        // Now add authDatabase, if necessary.
        if (authDatabase != null) {
            ret += "/" + sqlURLEncode(authDatabase);
        }
        // OptionsString should already be properly encoded, since we got it straight from the url
        // string.
        if (optionsString != null) {
            ret += "?" + optionsString;
        }
        return ret;
    }
}
