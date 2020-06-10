import os
import yaml

test_text = """

package com.mongodb.jdbc.tdvt;

import static org.junit.Assert.*;

import java.lang.Math;
import java.sql.*;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(TDVTTest.class)
public class TDVTTest {
    static final String URL = "jdbc:mongodb://" + System.getenv("ADL_TEST_HOST") + "/tdvt";
    static Connection conn;

    static {
        try {
            conn =  getBasicConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Connection getBasicConnection() throws SQLException {
        java.util.Properties p = new java.util.Properties();
        p.setProperty("user", System.getenv("ADL_TEST_USER"));
        p.setProperty("password", System.getenv("ADL_TEST_PWD"));
        p.setProperty("database", "tdvt");
        p.setProperty("authSource", System.getenv("ADL_TEST_AUTH_DB"));
        p.setProperty("ssl", "true");
        return DriverManager.getConnection(URL, p);
    }

    static Object getTestObject(int i, ResultSetMetaData rsmd, ResultSet rs) throws SQLException {
        Object ret;
        switch (rsmd.getColumnType(i)) {
            case Types.BIT:
                ret = rs.getBoolean(i);
                return rs.wasNull()?null:ret;
            case Types.NULL:
                return null;
            case Types.DOUBLE:
            case Types.DECIMAL:
                ret = rs.getDouble(i);
                return rs.wasNull()?null:ret;
            case Types.INTEGER:
                ret = rs.getLong(i);
                return rs.wasNull()?null:ret;
            default:
                ret = rs.getString(i);
                return rs.wasNull()?null:ret;
        }
    }

    static boolean compareResults(
            ArrayList<ArrayList<Object>> expected, ArrayList<ArrayList<Object>> result) {
        if (expected.size() != result.size()) {
            System.out.println(
                    "Expected and result differ in length, "
                            + expected.size()
                            + " and "
                            + result.size()
                            + ", respectively");
            return false;
        }
        for (int rowIdx = 0; rowIdx < expected.size(); ++rowIdx) {
            ArrayList<Object> expectedRow = expected.get(rowIdx);
            ArrayList<Object> resultRow = result.get(rowIdx);
            if (expectedRow.size() != resultRow.size()) {
                System.out.println(
                        "Expected row and result row differ in length, "
                                + expectedRow.size()
                                + " and "
                                + resultRow.size()
                                + ", respectively");
                return false;
            }
            for (int colIdx = 0; colIdx < expectedRow.size(); ++colIdx) {
                Object expectedVal = expectedRow.get(colIdx);
                Object resultVal = resultRow.get(colIdx);
                if (expectedVal == null) {
                    if (resultVal != null) {
                        System.out.println(
                                "At position: "
                                        + rowIdx
                                        + ", "
                                        + colIdx
                                        + " expected value: "
                                        + expectedVal
                                        + " != "
                                        + resultVal);
                        return false;
                    }
                } else if (resultVal instanceof Double) {
                    if (Math.abs((double) resultVal - (double) expectedVal) > 0.005) {
                        System.out.println(
                                "At position: "
                                        + rowIdx
                                        + ", "
                                        + colIdx
                                        + " expected value: "
                                        + expectedVal
                                        + " != "
                                        + resultVal);
                        return false;
                    }
                } else if (!expectedVal.equals(resultVal)) {
                    System.out.println(
                            "At position: "
                                    + rowIdx
                                    + ", "
                                    + colIdx
                                    + " expected value: "
                                    + expectedVal
                                    + " != "
                                    + resultVal);
                    return false;
                }
            }
        }
        return true;
    }

    static ArrayList<ArrayList<Object>> buildResultSetSet(ResultSet rs) throws SQLException {
        ArrayList<ArrayList<Object>> ret = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        while(rs.next()) {
            ArrayList<Object> row = new ArrayList<>(count);
            for(int i = 1; i <= count; ++i) {
                Object result = getTestObject(i, rsmd, rs);
                row.add(result);
            }
            ret.add(row);
        }
        return ret;
    }

"""

print(test_text)

types = {
        None: 'null',
        'null': 'null',
        'varchar': 'string',
        'string': 'string',
        'bool': 'bool',
        'int': 'long',
        'long': 'long',
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
 "Item Count" , "Ship Priority" , "Order Priority" , "Order Status" , "Order Quantity" , "Sales Total" , "Discount" , "Tax Rate" , "Ship Mode" , "Fill Time" , "Gross Profit" , "Price" , "Ship Handle Cost" , "Employee Name" , "Employee Dept" , "Manager Name" , "Employee Yrs Exp" , "Employee Salary" , "Customer Name" , "Customer State" , "Call Center Region" , "Customer Balance" , "Customer Segment" , "Prod Type1" , "Prod Type2" , "Prod Type3" , "Prod Type4" , "Product Name" , "Product Container" , "Ship Promo" , "Supplier Name" , "Supplier Balance" , "Supplier Region" , "Supplier State" , "Order ID" , "Order Year" , "Order Month" , "Order Day" , "Order Date" , "Order Quarter" , "Product Base Margin" , "Product ID" , "Receive Time" , "Received Date" , "Ship Date" , "Ship Charge" , "Total Cycle Time" , "Product In Stock" , "PID" , "Market Segment ", "LOCATE('E'"]:
        sql = sql.replace(key.lower(), key)
    # tdvt has a mode where bool columns end with _. We did not map the columns this way in ADL, so
    # we need to remove those underscores. E.g., we replace bool0_ with bool0.
    for key in ('bool' + str(n) + '_' for n in range(0,4)):
        sql = sql.replace(key, key[0:-1])
    return sql

def make_col_str(ty, col):
    ty = types[ty]
    if col == '~':
        return 'null'
    if col is None:
        return 'null'
    if col == 'NULL':
        return 'null'
    if ty == 'null':
        return 'null'
    if ty == 'string' or ty == 'date':
        return '"' + str(col) + '"'
    if ty == 'bool':
        if col == '0' or col == 0:
            return 'false'
        return 'true'
    if ty == 'long':
        return str(int(col)) + 'l'
    if ty == 'double' or ty == 'decimal':
        return str(float(col)) + 'd'

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
    print('        System.out.println("=============================='+ testName +'");')
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
            print('        if(!tyName.equals("' + types[ty] + '")) {')
            print('            System.out.println(tyName + " == " + "' + types[ty] + '");')
            print('        }')
            # For now, allow the tyName to be null. We will fix this soon when the results from ADL
            # contain the type.
            print('        assertTrue("failed type check", tyName.equals("' + types[ty] + '"));')
    if 'expected_results' in test and len(test['expected_results']) > 0:
        # Just compare everything as strings.
        print('        ArrayList<ArrayList<Object>> expected = new ArrayList<>(' + str(len(test['expected_results'])) + ');')
        print('        ArrayList<Object> expectedRow;')
        for row in test['expected_results']:
            print('        expectedRow = new ArrayList<>(' + str(len(row)) + ');')
            for i, col in enumerate(row):
                col_str = make_col_str(test['expected_types'][i], col)
                print('        expectedRow.add(' + col_str + ');')
            print('        expected.add(expectedRow);')
        print('        ArrayList<ArrayList<Object>> rsSet = buildResultSetSet(rs);')
        # We'll leave the printouts here until we are done fixing the failing tests in a future
        # ticket.
        print('        if (!compareResults(expected, rsSet)) {')
        print('            // This will be false if both ArrayLists are the same')
        print('            System.out.println(expected.toString());')
        print('            System.out.println(rsSet.toString());')
        print('            assertTrue("failed result check", false);')
        print('        }')
    print("    }\n")

def add_cases(fName):
    y = yaml.load(open(fName), Loader=yaml.FullLoader)

    for test in y['testcases']:
        make_test(os.path.basename(fName).split('.')[0].upper(), test)

for f  in map(lambda x: os.path.join("tdvt_test", x), ['calcs.yml', 'logical_calcs.yml', 'logical_staples.yml']):
    add_cases(f)

print("}")
