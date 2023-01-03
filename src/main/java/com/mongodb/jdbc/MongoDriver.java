/*
 * Copyright 2020-present MongoDB, Inc.
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

package com.mongodb.jdbc;

import static com.mongodb.jdbc.MongoDriver.MongoJDBCProperty.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.ConnectionString;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * The MongoDriver implements the java.sql.Driver interface, which allows for opening Connections to
 * MonogDB databases that have SQL support.
 *
 * @see DriverManager
 * @see Connection
 * @since 1.0.0
 */
public class MongoDriver implements Driver {

    /**
     * The list of connection options specific to the JDBC driver which can only be provided through
     * a Properties Object.
     */
    public enum MongoJDBCProperty {
        DATABASE("database"),
        CLIENT_INFO("clientinfo"),
        LOG_LEVEL("loglevel"),
        LOG_DIR("logdir"),
        EXT_JSON_MODE("extjsonmode");

        private final String propertyName;

        MongoJDBCProperty(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }

    /** All MongoDB SQL URLs must begin with jdbc:mongodb: */
    static final String JDBC = "jdbc:";

    static final String MONGODB_URL_PREFIX = JDBC + "mongodb:";
    static final String MONGODB_SRV_URL_PREFIX = JDBC + "mongodb+srv:";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    static final String MONGODB_PRODUCT_NAME = "MongoDB Atlas";
    static final String MONGO_DRIVER_NAME = MONGODB_PRODUCT_NAME + " SQL interface JDBC Driver";

    static final String NAME;
    static final String VERSION;
    static final int MAJOR_VERSION;
    static final int MINOR_VERSION;
    static final String LEVELS =
            Arrays.toString(
                    new String[] {
                        Level.OFF.getName(),
                        Level.SEVERE.getName(),
                        Level.FINER.getName(),
                        Level.INFO.getName(),
                        Level.FINE.getName(),
                        Level.WARNING.getName()
                    });
    static final String RELAXED = "RELAXED";
    static final String EXTENDED = "EXTENDED";
    public static final String LOG_TO_CONSOLE = "console";
    protected static final String CONNECTION_ERROR_SQLSTATE = "08000";

    // Load logging.properties
    static {
        InputStream stream =
                MongoDriver.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().reset();
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static CodecRegistry registry =
            fromProviders(
                    new BsonValueCodecProvider(),
                    new ValueCodecProvider(),
                    PojoCodecProvider.builder().automatic(true).build());

    static {
        MongoDriver unit = new MongoDriver();
        try {
            DriverManager.registerDriver(unit);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        VERSION = unit.getClass().getPackage().getImplementationVersion();
        if (VERSION != null) {
            String[] verSp = VERSION.split("[.]");
            if (verSp.length < 2) {
                throw new RuntimeException(
                        new SQLException(
                                "version was not specified correctly, must contain at least major and minor parts"));
            }
            MAJOR_VERSION = Integer.parseInt(verSp[0]);
            MINOR_VERSION = Integer.parseInt(verSp[1]);
        } else {
            // final requires this.
            MAJOR_VERSION = 0;
            MINOR_VERSION = 0;
        }
        String name = unit.getClass().getPackage().getImplementationTitle();
        NAME = (name != null) ? name : "mongodb-jdbc";
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Properties lowerCaseprops = new Properties();
        // Normalize all properties key to lower case to make all connection settings case-insensitive
        if (info != null) {
            Enumeration keys = info.propertyNames();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                lowerCaseprops.put(key.toLowerCase(), info.getProperty(key));
            }
        }
        MongoConnection conn = getUnvalidatedConnection(url, lowerCaseprops);
        // the jdbc spec requires that null be returned if a Driver cannot handle the specified URL
        // (cases where multiple jdbc drivers are present and the program is checking which driver
        // to use), so it is possible for conn to be null at this point.
        if (conn != null) {
            try {
                conn.testConnection(conn.getDefaultConnectionValidationTimeoutSeconds());
            } catch (TimeoutException te) {
                throw new SQLTimeoutException("Timeout. Can't connect.");
            } catch (Exception e) {
                throw new SQLException("Connection failed.", e);
            }
        }
        return conn;
    }

    protected MongoConnection getUnvalidatedConnection(String url, Properties info)
            throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        if (info == null) {
            info = new Properties();
        }

        // Ensure that the ConnectionString and Properties are consistent.
        // Reuse the code getPropertyInfo to make sure the URI is properly set wrt the passed
        // Properties info value.
        Pair<ConnectionString, DriverPropertyInfo[]> p = getConnectionSettings(url, info);
        // Since the user is calling connect, we should throw a SQLException if we get a prompt back.
        if (p.right().length != 0) {
            // Inspect the return value to format the SQLException and throw the connection error
            throw new SQLException(reportMissingProperties(p.right()), CONNECTION_ERROR_SQLSTATE);
        }

        return createConnection(p.left(), info);
    }

    /**
     * Provides feedback regarding the missing properties.
     *
     * @param missingRequiredProperties The list of required properties a connection needs to be
     *     successful.
     * @return the format error message to return to the user.
     */
    private String reportMissingProperties(DriverPropertyInfo[] missingRequiredProperties) {
        List<String> propertyNames = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("There are issues with your connection settings : ");
        for (DriverPropertyInfo info : missingRequiredProperties) {
            if (info.name.equals(USER)) {
                sb.append("Password specified without user. Please provide '");
                sb.append(USER);
                sb.append("' property value.\n");
            } else if (info.name.equals(PASSWORD)) {
                sb.append("User specified without password. Please provide '");
                sb.append(PASSWORD);
                sb.append("' property value.\n");
            } else if (info.name.equals(DATABASE.getPropertyName())) {
                sb.append("Mandatory property '");
                sb.append(DATABASE.getPropertyName());
                sb.append("' is missing.\n");
            } else {
                propertyNames.add(info.name);
            }
        }
        if (!propertyNames.isEmpty()) {
            sb.append("Unexpected driver property info : ");
            sb.append(String.join(", ", propertyNames));
            sb.append("\n");
        }

        return sb.toString();
    }

    private MongoConnection createConnection(ConnectionString cs, Properties info)
            throws SQLException {
        // Database from the properties must be present
        String database = info.getProperty(DATABASE.getPropertyName());

        // Default log level is OFF
        String logLevelVal = info.getProperty(LOG_LEVEL.getPropertyName(), Level.OFF.getName());
        Level logLevel;
        try {
            logLevel = Level.parse(logLevelVal.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SQLException(
                    "Invalid "
                            + LOG_LEVEL.getPropertyName()
                            + " property value : "
                            + logLevelVal
                            + ". Valid values are : "
                            + LEVELS
                            + ".");
        }
        String logDirVal = info.getProperty(LOG_DIR.getPropertyName());
        if ((logDirVal != null) && LOG_TO_CONSOLE.equalsIgnoreCase(logDirVal.trim())) {
            // If logDir is "console" then remove the value since the logger
            // will default to a console handler if no logDir is specified
            logDirVal = null;
        }
        File logDir = (logDirVal == null) ? null : new File(logDirVal);
        if (logDir != null && !logDir.isDirectory()) {
            throw new SQLException(
                    "Invalid "
                            + LOG_DIR.getPropertyName()
                            + " property value : "
                            + logDirVal
                            + ". It must be a directory.");
        }
        String clientInfo = info.getProperty(CLIENT_INFO.getPropertyName());
        if (clientInfo != null && clientInfo.split("\\+").length != 2) {
            throw new SQLException(
                    "Invalid "
                            + CLIENT_INFO.getPropertyName()
                            + " property value : "
                            + clientInfo
                            + ". Expected format <name>+<version>.");
        }

        String extJsonModeVal = info.getProperty(EXT_JSON_MODE.getPropertyName());
        boolean extJsonMode = false;
        if (extJsonModeVal != null) {
            extJsonModeVal = extJsonModeVal.toUpperCase().trim();
            if (extJsonModeVal == EXTENDED) {
                extJsonMode = true;
            } else if (extJsonModeVal != RELAXED) {
                throw new SQLException("Invalid JSON mode: " + extJsonModeVal);
            }
        }

        MongoConnectionProperties mongoConnectionProperties =
                new MongoConnectionProperties(
                        cs, database, logLevel, logDir, clientInfo, extJsonMode);

        return new MongoConnection(mongoConnectionProperties);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(MONGODB_URL_PREFIX) || url.startsWith(MONGODB_SRV_URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
            throws SQLException {
        Pair<ConnectionString, DriverPropertyInfo[]> p = getConnectionSettings(url, info);
        return p.right();
    }

    @Override
    public int getMajorVersion() {
        return 1;
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

    private static class ParseResult {
        String user;
        char[] password;
        Properties normalizedOptions;

        ParseResult(String u, char[] p, Properties options) {
            user = u;
            password = p;
            normalizedOptions = options;
        }
    }

    // getConnectionString constructs a valid MongoDB connection string which will be used as an input to the mongoClient.
    // If there are required fields missing, those fields will be returned in DriverPropertyInfo[] with a null connectionString
    public static Pair<ConnectionString, DriverPropertyInfo[]> getConnectionSettings(
            String url, Properties info) throws SQLException {
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


        ParseResult result = normalizeConnectionOptions(originalConnectionString, info);
        String user = result.user;
        char[] password = result.password;

        List<DriverPropertyInfo> mandatoryConnectionProperties = new ArrayList<>();

        // A database to connect to is required. If they have not specified one, look in the connection string for a
        // database. The specified database in the connect window will always override the uri database.
        if ((!info.containsKey(DATABASE.getPropertyName())
                        || info.getProperty(DATABASE.getPropertyName()).isEmpty())
                && originalConnectionString.getDatabase() != null) {
            info.setProperty(DATABASE.getPropertyName(), originalConnectionString.getDatabase());
        }
        if (!info.containsKey(DATABASE.getPropertyName())
                || info.getProperty(DATABASE.getPropertyName()).isEmpty()) {
            mandatoryConnectionProperties.add(
                    new DriverPropertyInfo(DATABASE.getPropertyName(), null));
        }
        String authDatabase = info.getProperty(DATABASE.getPropertyName());

        if (user == null && password != null) {
            // user is null, but password is not, we must prompt for the user.
            // Note: The convention is actually to return DriverPropertyInfo objects
            // with null values, this is not a bug.
            mandatoryConnectionProperties.add(new DriverPropertyInfo(USER, null));
        }
        if (password == null && user != null) {
            // password is null, but user is not, we must prompt for the password.
            mandatoryConnectionProperties.add(new DriverPropertyInfo(PASSWORD, null));
        }

        // If mandatoryConnectionProperties is not empty, we stop here because we are missing connection information
        if (mandatoryConnectionProperties.size() > 0) {
            return new Pair<>(
                    null,
                    mandatoryConnectionProperties.toArray(
                            new DriverPropertyInfo[mandatoryConnectionProperties.size()]));
        }

        // If we are here, we must have all the required connection information. So we have a valid URI state,
        // go ahead and construct it and prompt for nothing.
        ConnectionString c =
                new ConnectionString(
                        buildNewURI(
                                originalConnectionString.getHosts(),
                                user,
                                password,
                                authDatabase,
                                result.normalizedOptions));
        return new Pair<>(c, new DriverPropertyInfo[] {});
    }

    private static interface NullCoalesce<T> {
        T coalesce(T left, T right);
    }

    // private helper function to abstract checking consistency between properties and the URI, and
    // grabbing the username, password and the consolidated connection arguments
    //
    // throws SQLException if url and options properties disagree on the value
    private static ParseResult normalizeConnectionOptions(
            ConnectionString clientURI, Properties info) throws SQLException {

        if (info == null) {
            info = new Properties();
        }
        // The coalesce function takes the first non-null argument, returning null only
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
        if (user != null) {
            // Make sure the `info` reflects the URL for USER because MongoDatabaseMetaData needs to
            // know this.
            info.setProperty(USER, user);
        }
        // handle disagreements on password.
        if (uriPWD != null && propertyPWD != null && !Arrays.equals(uriPWD, propertyPWD)) {
            throw new SQLException("uri and properties disagree on password");
        }
        // set the password
        char[] password = c.coalesce(uriPWD, propertyPWD);

        String optionString = null;
        String[] optionSplit =
                clientURI
                        .getConnectionString()
                        .split("[?]"); // split takes a regexp and '?' is a metachar.
        if (optionSplit.length > 1) {
            optionString = optionSplit[1];
        }

        Properties options = new Properties();
        if (optionString != null) {
            String[] optionStrs = optionString.split("&");
            for (String optionStr : optionStrs) {
                String[] kv = optionStr.split("=");
                if (kv.length != 2) {
                    throw new SQLException("Option String is not valid");
                }
                String normalizedKey = kv[0].toLowerCase();
                if (normalizedKey.equals(USER) || normalizedKey.equals(PASSWORD)) {
                    continue;
                }
                options.put(normalizedKey, kv[1]);
            }
        }

        for (String key : info.stringPropertyNames()) {
            String normalizedKey = key.toLowerCase();
            if (normalizedKey.equals(USER) || normalizedKey.equals(PASSWORD)) {
                continue;
            }
            String val = info.getProperty(key);
            if (options.containsKey(normalizedKey)) {
                if (!options.getProperty(normalizedKey).equals(val)) {
                    throw new SQLException("uri and properties disagree on " + key);
                }
            } else {
                options.setProperty(normalizedKey, val);
            }
        }

        return new ParseResult(user, password, options);
    }

    // This is just a clean abstraction around URLEncode.
    private static String sqlURLEncode(String item) throws SQLException {
        try {
            return URLEncoder.encode(item, "utf-8");
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * Return true if the given key is a JDBC specific property, false otherwise.
     *
     * @param key The key to check.
     * @return true if the given key is a JDBC specific property, false otherwise.
     */
    private static boolean isMongoJDBCProperty(String key) {
        return Stream.of(MongoJDBCProperty.values())
                .anyMatch(v -> v.getPropertyName().equalsIgnoreCase(key));
    }

    /**
     * This function builds a new uri from the original clientURI, adding user, password, options,
     * and database, if necessary.
     *
     * @param hosts the list of MongoDB host names
     * @param user the auth username
     * @param password the auth password
     * @param authDatabase the authentication database to use if authSource option is not specified
     * @param options the consolidated tag/value pairs from the url query string and connection
     *     arguments user provided,
     */
    private static String buildNewURI(
            List<String> hosts,
            String user,
            char[] password,
            String authDatabase,
            Properties options)
            throws SQLException {
        // The returned URI should be of the following format:
        //"mongodb://[user:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[authDatabase][?options]]")
        String ret = "mongodb://";
        if (user != null) {
            // Note: if user is not null, we already know that password must also be not null.
            ret += sqlURLEncode(user) + ":" + sqlURLEncode(String.valueOf(password)) + "@";
        }
        // Now add hosts.
        ret += String.join(",", hosts);
        // Now add authDatabase, if necessary.
        if (authDatabase != null) {
            ret += "/" + sqlURLEncode(authDatabase);
        } else {
            ret += "/";
        }

        StringBuilder buff = new StringBuilder();
        if (options != null) {
            for (String key : options.stringPropertyNames()) {

                // Only add keys which are part of the standard MongoDB URI (except user and password) and skip JDBC
                // //specific properties which can't be specified via the connection string
                if (!key.equals(USER) && !key.equals(PASSWORD) && !isMongoJDBCProperty(key)) {
                    if (buff.length() > 0) {
                        buff.append("&");
                    }
                    try {
                        buff.append(key)
                                .append("=")
                                .append(URLEncoder.encode(options.getProperty(key), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new SQLException(e);
                    }
                }
            }
        }
        if (buff.length() > 0) {
            ret += "?" + buff.toString();
        }
        return ret;
    }
}
