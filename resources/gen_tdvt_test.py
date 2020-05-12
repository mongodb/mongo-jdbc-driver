import os
import yaml

test_text = """

package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.HashSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(TDVTTest.class)
public class TDVTTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/test";
    static final String URL_WITH_USER_AND_PW =
            "jdbc:mongodb://"
                    + System.getenv("ADL_TEST_USER")
                    + ":"
                    + System.getenv("ADL_TEST_PWD")
                    + "@"
                    + System.getenv("ADL_TEST_HOST")
                    + "/test";


    static Connection getBasicConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "looker");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        return DriverManager.getConnection(URL, p);
    }

"""

print(test_text)

"""
  - expected_results:
      - [~, ~, ~]
      - [10.85, 10, 0.8499999999999996]
      - [-13.47, -14, 0.5299999999999994]
    expected_names: [num4, floor, diff]
    expected_types: [float, float, float]
    id: 'BI-821-bug'
    sql: 'select num4, floor(calcs.num4) as floor, calcs.num4-floor(calcs.num4) as diff from calcs limit 3'
"""

def make_test(fName, test):
    print("    @Test")
    print("    public void test" + fName + test["id"] + "() throws SQLException {")
    print("        Connection conn = getBasicConnection();")
    print("        Statement stmt = conn.createStatement();")
    sql = test["sql"].replace("\n","")
    print('        ResultSet rs = stmt.executeQuery("' + sql + '");')
    print('        ResultSetMetaData rsmd = rs.getMetaData();')
    if 'expected_names' in test:
        for i, name in enumerate(test['expected_names']):
            print('        rsmd.getColumnLabel(' + str(i) + ').equals("' + name + '");')
    print("    }\n")

def add_cases(fName):
    y = yaml.load(open(fName), Loader=yaml.FullLoader)

    for test in y['testcases']:
        make_test(os.path.basename(fName).split('.')[0].upper(), test)


for f  in map(lambda x: os.path.join("tdvt_test", x), ['calcs.yml', 'logical_calcs.yml', 'logical_staples.yml']):
    add_cases(f)

print("}")
