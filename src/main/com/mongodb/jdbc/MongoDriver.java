package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MongoDriver implements Driver {

    public Connection connect(String url, java.util.Properties info)
        throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }

    public boolean acceptsURL(String url) throws SQLException {
        throw new SQLFeatureNotSupportedException("not implemented");
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


    public boolean jdbcCompliant() {
        return false;
    }

    //------------------------- JDBC 4.1 -----------------------------------

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("not implemented");
    }
}

