import os
import yaml

test_text = """

package com.mongodb.jdbc.tdvt;

import static org.junit.Assert.*;

import java.sql.*;
import java.util.ArrayList;
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

    static HashSet<ArrayList<String>> buildResultSetSet(ResultSet rs) throws SQLException {
        HashSet<ArrayList<String>> ret = new HashSet<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        while(rs.next()) {
            ArrayList<String> row = new ArrayList<>(count);
            for(int i = 0; i < count; ++i) {
                row.add(rs.getString(i+1));
            }
            ret.add(row);
        }
        return ret;
    }

"""

print(test_text)

types = {
        'varchar': 'string',
        'string': 'string',
        'int': 'int',
        'float': 'double',
        'double': 'double',
        'date': 'date',
        'decimal': 'decimal',
        }

def clean_sql(sql):
    sql = sql.replace("\n"," ").lower()
    sql = sql.replace("--", " ")
    # Need to handle case sensitivity for table names. The queries were originally written for case
    # insensitive MySQL.
    sql = sql.replace("calcs", "Calcs")
    sql = sql.replace("staples", "Staples")
    # Our column names in Staples are all Capitalized, so replace back to the capitalized form. The
    # queries were originally written for case insensitive MySQL.
    for key in [
 "Item Count" , "Ship Priority" , "Order Priority" , "Order Status" , "Order Quantity" , "Sales Total" , "Discount" , "Tax Rate" , "Ship Mode" , "Fill Time" , "Gross Profit" , "Price" , "Ship Handle Cost" , "Employee Name" , "Employee Dept" , "Manager Name" , "Employee Yrs Exp" , "Employee Salary" , "Customer Name" , "Customer State" , "Call Center Region" , "Customer Balance" , "Customer Segment" , "Prod Type1" , "Prod Type2" , "Prod Type3" , "Prod Type4" , "Product Name" , "Product Container" , "Ship Promo" , "Supplier Name" , "Supplier Balance" , "Supplier Region" , "Supplier State" , "Order ID" , "Order Year" , "Order Month" , "Order Day" , "Order Date" , "Order Quarter" , "Product Base Margin" , "Product ID" , "Receive Time" , "Received Date" , "Ship Date" , "Ship Charge" , "Total Cycle Time" , "Product In Stock" , "PID" , "Market Segment "	]:
        sql = sql.replace(key.lower(), key)
    # tdvt has a mode where bool columns end with _. We did not map the columns this way in ADL, so
    # we need to remove those underscores. E.g., we replace bool0_ with bool0.
    for key in ('bool' + str(n) + '_' for n in range(0,4)):
        sql = sql.replace(key, key[0:-1])
    return sql

def make_col_str(col):
    if col == '~':
        return 'null'
    if col is None:
        return 'null'
    if col == 'NULL':
        return 'null'
    if type(col) == str:
        return '"' + col + '"'
    return '"' + str(col) + '"'

def make_test(fName, test):
    # we are only doing tests expected to run correctly.
    if 'expected_error' in test and test['expected_error'] != '':
        return
    # if we had it set to skip, there is probably a reason.
    # we must check directly for True, because python treats many other
    # values as True, such as any non-empty string.
    if 'skip' in test and test['skip'] == True:
        return
    testName = fName + test["id"]
    print("    @Test")
    print("    public void test" + testName + "() throws SQLException {")
    print("        Connection conn = getBasicConnection();")
    print("        Statement stmt = conn.createStatement();")
    sql = clean_sql(test["sql"])
    print('        ResultSet rs = stmt.executeQuery("' + sql + '");')
    print('        ResultSetMetaData rsmd = rs.getMetaData();')
    if 'expected_names' in test:
        for i, name in enumerate(test['expected_names']):
            name = clean_sql(name)
            print('        assertEquals("' + name + '", rsmd.getColumnLabel(' + str(i+1) + '));')
    if 'expected_types' in test and len(test['expected_types']) > 0:
        print('        String tyName;')
        for i, ty in enumerate(test['expected_types']):
            print('        tyName = rsmd.getColumnTypeName(' + str(i+1) + ');')
            # For now, allow the tyName to be null. We will fix this soon when the results from ADL
            # contain the type.
            print('        assertTrue("failed type check", tyName.equals("null") || tyName.equals("' + types[ty] + '"));')
    if 'expected_results' in test and len(test['expected_results']) > 0:
        # Just compare everything as strings.
        print('        HashSet<ArrayList<String>> expected = new HashSet<>(' + str(len(test['expected_results'])) + ');')
        print('        ArrayList<String> expectedRow;')
        for row in test['expected_results']:
            print('        expectedRow = new ArrayList<>(' + str(len(row)) + ');')
            for col in row:
                col_str = make_col_str(col)
                print('        expectedRow.add(' + col_str + ');')
            print('        expected.add(expectedRow);')
        print('        HashSet<ArrayList<String>> rsSet = buildResultSetSet(rs);')
        # We'll leave the printouts here until we are done fixing the failing tests in a future
        # ticket.
        print('        System.out.println("=============================='+ testName +'");')
        print('        System.out.println(expected.toString());')
        print('        System.out.println(rsSet.toString());')
        print('        // This will be false if both HashSets are the same')
        print('        assertFalse("failed result check", rsSet.retainAll(expected));')
    print("    }\n")

def add_cases(fName):
    y = yaml.load(open(fName), Loader=yaml.FullLoader)

    for test in y['testcases']:
        make_test(os.path.basename(fName).split('.')[0].upper(), test)


for f  in map(lambda x: os.path.join("tdvt_test", x), ['calcs.yml', 'logical_calcs.yml', 'logical_staples.yml']):
    add_cases(f)

print("}")
