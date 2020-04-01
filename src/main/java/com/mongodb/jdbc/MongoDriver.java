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

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.ConnectionString;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Properties;
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
    /** All MongoDB SQL URLs must begin with jdbc:mongodb: */
    static final String JDBC = "jdbc:";

    static final String MONGODB_URL_PREFIX = JDBC + "mongodb:";
    static final String MONGODB_SRV_URL_PREFIX = JDBC + "mongodb+srv:";
    static final String USER = "user";
    static final String PASSWORD = "password";
    static final String CONVERSION_MODE = "conversionMode";
    // database is the database to switch to.
    static final String DATABASE = "database";

    static CodecRegistry registry =
            fromProviders(
                    new BsonValueCodecProvider(),
                    new ValueCodecProvider(),
                    PojoCodecProvider.builder().automatic(true).build());

    static {
        try {
            DriverManager.registerDriver(new MongoDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        DriverPropertyInfo[] driverPropertyInfo = p.right();
        if (driverPropertyInfo.length != 0) {
            if (driverPropertyInfo[0].name.equals(USER)) {
                throw new SQLException("password specified without user");
            }
            if (driverPropertyInfo[0].name.equals(PASSWORD)) {
                throw new SQLException("user specified without password");
            }
            String[] propertyNames = new String[driverPropertyInfo.length];
            for (int i = 0; i < propertyNames.length; ++i) {
                propertyNames[i] = driverPropertyInfo[i].name;
            }
            throw new SQLException(
                    "unexpected driver property info prompt returned: "
                            + String.join(", ", propertyNames));
        }
        return new MongoConnection(
                p.left(), info.getProperty(DATABASE), info.getProperty(CONVERSION_MODE));
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

    private Pair getConnectionString(String url, Properties info) throws SQLException {
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

        Properties normalizedOptions = normalizeOptions(info, optionString);

        if (user == null && password == null) {
            ConnectionString c =
                    new ConnectionString(
                            buildNewURI(
                                    originalConnectionString,
                                    user,
                                    password,
                                    authDatabase,
                                    normalizedOptions));
            Pair pair = new Pair(c, new DriverPropertyInfo[] {});
            return pair;
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
                                normalizedOptions));
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

    // private helper function to filter out unnecessary or invalid options before building URI
    // throws SQLException if optionString is invalid
    private static Properties normalizeOptions(Properties info, String optionString)
            throws SQLException {
        Properties options = new Properties();
        if (optionString != null) {
            String[] optionStrs = optionString.split("&");
            for (String optionStr : optionStrs) {
                String[] kv = optionStr.split("=");
                if (kv.length != 2) {
                    throw new SQLException("Option String is not valid");
                }
                if (kv[0].toLowerCase().equals(USER) || kv[0].toLowerCase().equals(PASSWORD)) {
                    continue;
                }
                options.put(kv[0].toLowerCase(), kv[1]);
            }
        }

        for (String key : info.stringPropertyNames()) {
            String normalizedKey = key.toLowerCase();
            if (normalizedKey.toLowerCase().equals(USER) || normalizedKey.equals(PASSWORD)) {
                continue;
            }
            String val = info.getProperty(key);
            if (options.containsKey(normalizedKey)) {
                if (!options.getProperty(normalizedKey).equals(val)) {
                    throw new SQLException("uri and properties disagree on %s", key);
                }
            } else {
                options.setProperty(normalizedKey, val);
            }
        }
        return options;
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
        ret += String.join(",", originalConnectionString.getHosts());
        // Now add authDatabase, if necessary.
        if (authDatabase != null) {
            ret += "/" + sqlURLEncode(authDatabase);
        }
        // OptionsString should already be properly encoded, since we got it straight from the url
        // string.
        StringBuilder buff = new StringBuilder();
        if (options != null) {
            for (String key : options.stringPropertyNames()) {
                if (!key.equals(USER)
                        && !key.equals(PASSWORD)
                        && !key.equals(CONVERSION_MODE)
                        && !key.equals(DATABASE)) {
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
