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
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/tdvt";


    static Connection getBasicConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "tdvt");
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

def clean_sql(sql):
    sql = sql.replace("\n","").lower()
    # Need to handle case sensitivity
    sql = sql.replace("calcs", "Calcs")
    sql = sql.replace("staples", "Staples")
    for key in [
 "Item Count" , "Ship Priority" , "Order Priority" , "Order Status" , "Order Quantity" , "Sales Total" , "Discount" , "Tax Rate" , "Ship Mode" , "Fill Time" , "Gross Profit" , "Price" , "Ship Handle Cost" , "Employee Name" , "Employee Dept" , "Manager Name" , "Employee Yrs Exp" , "Employee Salary" , "Customer Name" , "Customer State" , "Call Center Region" , "Customer Balance" , "Customer Segment" , "Prod Type1" , "Prod Type2" , "Prod Type3" , "Prod Type4" , "Product Name" , "Product Container" , "Ship Promo" , "Supplier Name" , "Supplier Balance" , "Supplier Region" , "Supplier State" , "Order ID" , "Order Year" , "Order Month" , "Order Day" , "Order Date" , "Order Quarter" , "Product Base Margin" , "Product ID" , "Receive Time" , "Received Date" , "Ship Date" , "Ship Charge" , "Total Cycle Time" , "Product In Stock" , "PID" , "Market Segment "	]:
        sql = sql.replace(key.lower(), key)
    return sql

def make_test(fName, test):
    print("    @Test")
    print("    public void test" + fName + test["id"] + "() throws SQLException {")
    print("        Connection conn = getBasicConnection();")
    print("        Statement stmt = conn.createStatement();")
    sql = clean_sql(test["sql"])
    print('        ResultSet rs = stmt.executeQuery("' + sql + '");')
    print('        ResultSetMetaData rsmd = rs.getMetaData();')
    if 'expected_names' in test:
        for i, name in enumerate(test['expected_names']):
            name = clean_sql(name)
            print('        assertEquals("'+ name +'", rsmd.getColumnLabel(' + str(i+1) + '));')
    print("    }\n")

def add_cases(fName):
    y = yaml.load(open(fName), Loader=yaml.FullLoader)

    for test in y['testcases']:
        make_test(os.path.basename(fName).split('.')[0].upper(), test)


for f  in map(lambda x: os.path.join("tdvt_test", x), ['calcs.yml', 'logical_calcs.yml', 'logical_staples.yml']):
    add_cases(f)
    break

print("}")
