package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MongoDriver implements Driver {
    static final String MONGODB_URL_PREFIX = "jdbc:mongodb:";

    static {
        try {
            DriverManager.registerDriver(new MongoDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection connect(String url, java.util.Properties info) throws SQLException {
        return new MongoConnection(url);
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(MONGODB_URL_PREFIX);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 1;
    }

    // It will take us quite a while to achieve full jdbc compliance (full writes, transaction
    // support, etc), and it actually requires
    // passing certification.
    public boolean jdbcCompliant() {
        return false;
    }

    // ------------------------- JDBC 4.1 -----------------------------------

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }
}
