

package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
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

    @Test
    public void testCALCSBI_821_bug() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "select num4, floor(calcs.num4) as floor, calcs.num4-floor(calcs.num4) as diff from calcs limit 3");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("num4");
        rsmd.getColumnLabel(1).equals("floor");
        rsmd.getColumnLabel(2).equals("diff");
    }

    @Test
    public void testCALCS0() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(2074921570)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2074921570)(0)");
    }

    @Test
    public void testCALCS1() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2348327946)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2348327946)(0)");
    }

    @Test
    public void testCALCS2() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3062347157)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3062347157)(0)");
    }

    @Test
    public void testCALCS3() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1236088422)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1236088422)(0)");
    }

    @Test
    public void testCALCS4() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1709161123)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1709161123)(0)");
    }

    @Test
    public void testCALCS5() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(941741456)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(941741456)(0)");
    }

    @Test
    public void testCALCS6() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - 1 + DAYOFWEEK(DATE_FORMAT(FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(4070818381)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4070818381)(0)");
    }

    @Test
    public void testCALCS7() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1 + DAYOFWEEK(DATE_FORMAT(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(1209329404)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1209329404)(0)");
    }

    @Test
    public void testCALCS8() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(2284623665)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2284623665)(0)");
    }

    @Test
    public void testCALCS9() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3556637072)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3556637072)(0)");
    }

    @Test
    public void testCALCS10() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(20465857)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(20465857)(0)");
    }

    @Test
    public void testCALCS11() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3365622206)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3365622206)(0)");
    }

    @Test
    public void testCALCS12() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1193407708)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1193407708)(0)");
    }

    @Test
    public void testCALCS13() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3498421513)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3498421513)(0)");
    }

    @Test
    public void testCALCS14() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1756144708)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1756144708)(0)");
    }

    @Test
    public void testCALCS15() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2635020195)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2635020195)(0)");
    }

    @Test
    public void testCALCS16() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2744314424)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2744314424)(0)");
    }

    @Test
    public void testCALCS17() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(232803726)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(232803726)(0)");
    }

    @Test
    public void testCALCS18() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(2176505489)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2176505489)(0)");
    }

    @Test
    public void testCALCS19() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 MINUTE) AS `TEMP(Test)(2741755004)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2741755004)(0)");
    }

    @Test
    public void testCALCS20() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2526477208)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2526477208)(0)");
    }

    @Test
    public void testCALCS21() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2007354609)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2007354609)(0)");
    }

    @Test
    public void testCALCS22() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3928745396)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3928745396)(0)");
    }

    @Test
    public void testCALCS23() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(746880020)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(746880020)(0)");
    }

    @Test
    public void testCALCS24() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2699142763)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2699142763)(0)");
    }

    @Test
    public void testCALCS25() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1634134069)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1634134069)(0)");
    }

    @Test
    public void testCALCS26() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1949844743)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1949844743)(0)");
    }

    @Test
    public void testCALCS27() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3376136658)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3376136658)(0)");
    }

    @Test
    public void testCALCS28() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3672267408)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3672267408)(0)");
    }

    @Test
    public void testCALCS29() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(2406708804)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2406708804)(0)");
    }

    @Test
    public void testCALCS30() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1 + DAYOFWEEK(DATE_FORMAT(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(1073594909)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1073594909)(0)");
    }

    @Test
    public void testCALCS31() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - 1 + DAYOFWEEK(DATE_FORMAT(FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(4016689999)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4016689999)(0)");
    }

    @Test
    public void testCALCS32() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3405047399)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3405047399)(0)");
    }

    @Test
    public void testCALCS33() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(55506858)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(55506858)(0)");
    }

    @Test
    public void testCALCS34() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3460070750)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3460070750)(0)");
    }

    @Test
    public void testCALCS35() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1494289478)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1494289478)(0)");
    }

    @Test
    public void testCALCS36() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3227046355)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3227046355)(0)");
    }

    @Test
    public void testCALCS37() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1233941598)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1233941598)(0)");
    }

    @Test
    public void testCALCS38() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3874232094)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3874232094)(0)");
    }

    @Test
    public void testCALCS39() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1546814749)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1546814749)(0)");
    }

    @Test
    public void testCALCS40() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3692431276)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3692431276)(0)");
    }

    @Test
    public void testCALCS41() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(`Calcs`.`int0`) AS `TEMP(Test)(3910975586)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3910975586)(0)");
    }

    @Test
    public void testCALCS42() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT((`Calcs`.`bool0_` <> 0)) AS `TEMP(Test)(1133866179)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1133866179)(0)");
    }

    @Test
    public void testCALCS43() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(`Calcs`.`date3`) AS `TEMP(Test)(3590771088)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3590771088)(0)");
    }

    @Test
    public void testCALCS44() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(`Calcs`.`num4`) AS `TEMP(Test)(1804085677)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1804085677)(0)");
    }

    @Test
    public void testCALCS45() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(`Calcs`.`str2`) AS `TEMP(Test)(2760211945)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2760211945)(0)");
    }

    @Test
    public void testCALCS46() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(3386714330)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3386714330)(0)");
    }

    @Test
    public void testCALCS47() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(1554877814)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1554877814)(0)");
    }

    @Test
    public void testCALCS48() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(680392169)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(680392169)(0)");
    }

    @Test
    public void testCALCS49() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(792760981)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(792760981)(0)");
    }

    @Test
    public void testCALCS50() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:%s' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(4192719501)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4192719501)(0)");
    }

    @Test
    public void testCALCS51() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:%s' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2927274352)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2927274352)(0)");
    }

    @Test
    public void testCALCS52() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN 2 >= 0 THEN LEFT(`Calcs`.`str1`,2) ELSE NULL END) AS `TEMP(Test)(2443162804)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2443162804)(0)");
    }

    @Test
    public void testCALCS53() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN 3 >= 0 THEN LEFT(`Calcs`.`str2`,3) ELSE NULL END) AS `TEMP(Test)(1954670685)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1954670685)(0)");
    }

    @Test
    public void testCALCS54() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN `Calcs`.`int0` >= 0 THEN LEFT(`Calcs`.`str2`,`Calcs`.`int0`) ELSE NULL END) AS `TEMP(Test)(3664185027)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3664185027)(0)");
    }

    @Test
    public void testCALCS55() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:%s' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3300724379)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3300724379)(0)");
    }

    @Test
    public void testCALCS56() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`date2`) AS `TEMP(Test)(4107590482)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4107590482)(0)");
    }

    @Test
    public void testCALCS57() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(766794695)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(766794695)(0)");
    }

    @Test
    public void testCALCS58() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 1 AS `TEMP(Test)(3095770696)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3095770696)(0)");
    }

    @Test
    public void testCALCS59() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 0 AS `TEMP(Test)(334867691)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(334867691)(0)");
    }

    @Test
    public void testCALCS60() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1224905293)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1224905293)(0)");
    }

    @Test
    public void testCALCS61() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(3044284514)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3044284514)(0)");
    }

    @Test
    public void testCALCS62() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(2383411022)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2383411022)(0)");
    }

    @Test
    public void testCALCS63() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(3392256124)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3392256124)(0)");
    }

    @Test
    public void testCALCS64() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(1426463696)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1426463696)(0)");
    }

    @Test
    public void testCALCS65() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(TIMESTAMP(`Calcs`.`datetime0`)) AS `TEMP(Test)(1770279206)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1770279206)(0)");
    }

    @Test
    public void testCALCS66() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(TIMESTAMP(`Calcs`.`datetime0`)) AS `TEMP(Test)(4279914489)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4279914489)(0)");
    }

    @Test
    public void testCALCS67() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'DATA' AS `TEMP(Test)(2967749075)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2967749075)(0)");
    }

    @Test
    public void testCALCS68() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT UPPER(`Calcs`.`str2`) AS `TEMP(Test)(3516395767)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3516395767)(0)");
    }

    @Test
    public void testCALCS69() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`bool0_` <> 0) AS `TEMP(Test)(3428507074)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3428507074)(0)");
    }

    @Test
    public void testCALCS70() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`bool1_` <> 0) AS `TEMP(Test)(1935567978)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1935567978)(0)");
    }

    @Test
    public void testCALCS71() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`bool2_` <> 0) AS `TEMP(Test)(3179501244)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3179501244)(0)");
    }

    @Test
    public void testCALCS72() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`bool3_` <> 0) AS `TEMP(Test)(1288552116)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1288552116)(0)");
    }

    @Test
    public void testCALCS73() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`date0` AS `TEMP(Test)(1090544928)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1090544928)(0)");
    }

    @Test
    public void testCALCS74() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`date1` AS `TEMP(Test)(1295100109)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1295100109)(0)");
    }

    @Test
    public void testCALCS75() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`date2` AS `TEMP(Test)(2028340584)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2028340584)(0)");
    }

    @Test
    public void testCALCS76() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`date3` AS `TEMP(Test)(550459061)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(550459061)(0)");
    }

    @Test
    public void testCALCS77() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`datetime0` AS `TEMP(Test)(3848052829)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3848052829)(0)");
    }

    @Test
    public void testCALCS78() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`datetime1` AS `TEMP(Test)(1108086785)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1108086785)(0)");
    }

    @Test
    public void testCALCS79() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`key` AS `TEMP(Test)(3382465274)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3382465274)(0)");
    }

    @Test
    public void testCALCS80() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`str0` AS `TEMP(Test)(55415805)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(55415805)(0)");
    }

    @Test
    public void testCALCS81() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`str1` AS `TEMP(Test)(2285743265)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2285743265)(0)");
    }

    @Test
    public void testCALCS82() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`str2` AS `TEMP(Test)(3228347817)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3228347817)(0)");
    }

    @Test
    public void testCALCS83() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`str3` AS `TEMP(Test)(286811776)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(286811776)(0)");
    }

    @Test
    public void testCALCS84() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`time0` AS `TEMP(Test)(4245842207)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4245842207)(0)");
    }

    @Test
    public void testCALCS85() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`time1` AS `TEMP(Test)(665897456)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(665897456)(0)");
    }

    @Test
    public void testCALCS86() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`zzz` AS `TEMP(Test)(1729594319)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1729594319)(0)");
    }

    @Test
    public void testCALCS87() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`int0` AS `TEMP(Test)(3174765981)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3174765981)(0)");
    }

    @Test
    public void testCALCS88() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`int1` AS `TEMP(Test)(2829869592)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2829869592)(0)");
    }

    @Test
    public void testCALCS89() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`int2` AS `TEMP(Test)(551775594)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(551775594)(0)");
    }

    @Test
    public void testCALCS90() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`int3` AS `TEMP(Test)(524492059)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(524492059)(0)");
    }

    @Test
    public void testCALCS91() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num0` AS `TEMP(Test)(3934956185)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3934956185)(0)");
    }

    @Test
    public void testCALCS92() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num1` AS `TEMP(Test)(129981160)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(129981160)(0)");
    }

    @Test
    public void testCALCS93() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num2` AS `TEMP(Test)(1053269056)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1053269056)(0)");
    }

    @Test
    public void testCALCS94() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num3` AS `TEMP(Test)(3320504981)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3320504981)(0)");
    }

    @Test
    public void testCALCS95() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num4` AS `TEMP(Test)(3786834202)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3786834202)(0)");
    }

    @Test
    public void testCALCS96() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL 1 DAY) AS `TEMP(Test)(670684053)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(670684053)(0)");
    }

    @Test
    public void testCALCS97() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 DAY) AS `TEMP(Test)(2728495522)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2728495522)(0)");
    }

    @Test
    public void testCALCS98() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`date2`) THEN NULL	WHEN ISNULL(`Calcs`.`date3`) THEN NULL	ELSE LEAST(`Calcs`.`date2`, `Calcs`.`date3`) END) AS `TEMP(Test)(3951339438)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3951339438)(0)");
    }

    @Test
    public void testCALCS99() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`date2`) AS `TEMP(Test)(1465246653)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1465246653)(0)");
    }

    @Test
    public void testCALCS100() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`datetime0`) AS `TEMP(Test)(2572329321)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2572329321)(0)");
    }

    @Test
    public void testCALCS101() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*4 + (QUARTER(`Calcs`.`date2`) - QUARTER(`Calcs`.`date3`))) AS `TEMP(Test)(4144088821)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4144088821)(0)");
    }

    @Test
    public void testCALCS102() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*4 + (QUARTER(`Calcs`.`datetime0`) - QUARTER(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(2035564840)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2035564840)(0)");
    }

    @Test
    public void testCALCS103() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`date2`) AS `TEMP(Test)(477986140)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(477986140)(0)");
    }

    @Test
    public void testCALCS104() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(2224240773)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2224240773)(0)");
    }

    @Test
    public void testCALCS105() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'Data' AS `TEMP(Test)(535453017)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(535453017)(0)");
    }

    @Test
    public void testCALCS106() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`str1`) THEN NULL	WHEN ISNULL(`Calcs`.`str2`) THEN NULL	ELSE LEAST(`Calcs`.`str1`, `Calcs`.`str2`) END) AS `TEMP(Test)(497224717)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(497224717)(0)");
    }

    @Test
    public void testCALCS107() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`str2`) THEN NULL	WHEN ISNULL(`Calcs`.`str3`) THEN NULL	ELSE LEAST(`Calcs`.`str2`, `Calcs`.`str3`) END) AS `TEMP(Test)(1239505702)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1239505702)(0)");
    }

    @Test
    public void testCALCS108() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(1298877827)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1298877827)(0)");
    }

    @Test
    public void testCALCS109() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(1695139533)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1695139533)(0)");
    }

    @Test
    public void testCALCS110() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(1003104432)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1003104432)(0)");
    }

    @Test
    public void testCALCS111() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*12 + (MONTH(`Calcs`.`date2`) - MONTH(`Calcs`.`date3`))) AS `TEMP(Test)(381839689)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(381839689)(0)");
    }

    @Test
    public void testCALCS112() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*12 + (MONTH(`Calcs`.`datetime0`) - MONTH(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(2416406882)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2416406882)(0)");
    }

    @Test
    public void testCALCS113() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`)) AS `TEMP(Test)(3489013143)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3489013143)(0)");
    }

    @Test
    public void testCALCS114() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(3834106318)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3834106318)(0)");
    }

    @Test
    public void testCALCS115() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("           order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS116() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'bat' AS `TEMP(Test)(3161246105)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3161246105)(0)");
    }

    @Test
    public void testCALCS117() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT REPLACE(`Calcs`.`str2`,'e','o') AS `TEMP(Test)(2953834147)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2953834147)(0)");
    }

    @Test
    public void testCALCS118() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE(`Calcs`.`int1`,0) AS `TEMP(Test)(551720338)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(551720338)(0)");
    }

    @Test
    public void testCALCS119() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN (`Calcs`.`bool0_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool0_` <> 0) THEN 0	ELSE NULL END) AS `TEMP(Test)(2695057561)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2695057561)(0)");
    }

    @Test
    public void testCALCS120() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date0`) - 693961) AS `TEMP(Test)(2234960540)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2234960540)(0)");
    }

    @Test
    public void testCALCS121() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE(`Calcs`.`num2`,0) AS `TEMP(Test)(1665700248)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1665700248)(0)");
    }

    @Test
    public void testCALCS122() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE(`Calcs`.`str2`,0) AS `TEMP(Test)(2779514991)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2779514991)(0)");
    }

    @Test
    public void testCALCS123() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (RIGHT(RTRIM(`Calcs`.`str1`), LENGTH('s')) = 's') AS `TEMP(Test)(1759936097)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1759936097)(0)");
    }

    @Test
    public void testCALCS124() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (RIGHT(RTRIM(`Calcs`.`str2`), LENGTH('een')) = 'een') AS `TEMP(Test)(3179156403)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3179156403)(0)");
    }

    @Test
    public void testCALCS125() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SIN(`Calcs`.`int2`) AS `TEMP(Test)(527156183)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(527156183)(0)");
    }

    @Test
    public void testCALCS126() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SIN(`Calcs`.`num0`) AS `TEMP(Test)(1184030290)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1184030290)(0)");
    }

    @Test
    public void testCALCS127() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(554447598)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(554447598)(0)");
    }

    @Test
    public void testCALCS128() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(2130687817)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2130687817)(0)");
    }

    @Test
    public void testCALCS129() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(903794974)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(903794974)(0)");
    }

    @Test
    public void testCALCS130() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(3917828147)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3917828147)(0)");
    }

    @Test
    public void testCALCS131() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(3641022413)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3641022413)(0)");
    }

    @Test
    public void testCALCS132() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(1193998601)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1193998601)(0)");
    }

    @Test
    public void testCALCS133() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(3641022413)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3641022413)(1)");
    }

    @Test
    public void testCALCS134() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(1193998601)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1193998601)(1)");
    }

    @Test
    public void testCALCS135() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(3800988289)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3800988289)(0)");
    }

    @Test
    public void testCALCS136() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(779479971)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(779479971)(0)");
    }

    @Test
    public void testCALCS137() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(3800988289)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3800988289)(1)");
    }

    @Test
    public void testCALCS138() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(779479971)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(779479971)(1)");
    }

    @Test
    public void testCALCS139() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2793013592)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2793013592)(0)");
    }

    @Test
    public void testCALCS140() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2980130610)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2980130610)(0)");
    }

    @Test
    public void testCALCS141() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT EXP((0.10000000000000001 * `Calcs`.`num0`)) AS `TEMP(Test)(526466750)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(526466750)(0)");
    }

    @Test
    public void testCALCS142() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT EXP(`Calcs`.`int2`) AS `TEMP(Test)(2988208579)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2988208579)(0)");
    }

    @Test
    public void testCALCS143() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(2016952657)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2016952657)(0)");
    }

    @Test
    public void testCALCS144() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(1256216982)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1256216982)(0)");
    }

    @Test
    public void testCALCS145() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 97 AS `TEMP(Test)(415603459)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(415603459)(0)");
    }

    @Test
    public void testCALCS146() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ASCII(`Calcs`.`str2`) AS `TEMP(Test)(526259814)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(526259814)(0)");
    }

    @Test
    public void testCALCS147() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ASCII(`Calcs`.`str1`) AS `TEMP(Test)(4258651616)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4258651616)(0)");
    }

    @Test
    public void testCALCS148() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3415515666)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3415515666)(0)");
    }

    @Test
    public void testCALCS149() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2048935536)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2048935536)(0)");
    }

    @Test
    public void testCALCS150() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2714077903)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2714077903)(0)");
    }

    @Test
    public void testCALCS151() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1800100416)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1800100416)(0)");
    }

    @Test
    public void testCALCS152() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`date0` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`date1` ELSE NULL END) AS `TEMP(Test)(3513628645)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3513628645)(0)");
    }

    @Test
    public void testCALCS153() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`str3` ELSE `Calcs`.`str0` END) AS `TEMP(Test)(1007528555)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1007528555)(0)");
    }

    @Test
    public void testCALCS154() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`num0` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`num1` ELSE `Calcs`.`num2` END) AS `TEMP(Test)(3428504110)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3428504110)(0)");
    }

    @Test
    public void testCALCS155() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`date0` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`date1` ELSE `Calcs`.`date2` END) AS `TEMP(Test)(1581504649)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1581504649)(0)");
    }

    @Test
    public void testCALCS156() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`num0` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`num1` ELSE NULL END) AS `TEMP(Test)(750655768)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(750655768)(0)");
    }

    @Test
    public void testCALCS157() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (((`Calcs`.`bool0_` <> 0) AND (`Calcs`.`bool1_` <> 0)) OR ((NOT (`Calcs`.`bool0_` <> 0)) AND (`Calcs`.`bool2_` <> 0))) AS `TEMP(Test)(1656302737)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1656302737)(0)");
    }

    @Test
    public void testCALCS158() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ISNULL((`Calcs`.`bool0_` <> 0)) AS `TEMP(Test)(4006206882)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4006206882)(0)");
    }

    @Test
    public void testCALCS159() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`str3` ELSE NULL END) AS `TEMP(Test)(4173709053)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4173709053)(0)");
    }

    @Test
    public void testCALCS160() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((CASE WHEN (`Calcs`.`bool0_` <> 0) THEN (CASE	WHEN (`Calcs`.`bool1_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool1_` <> 0) THEN 0	ELSE NULL END) ELSE (CASE	WHEN (`Calcs`.`bool2_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool2_` <> 0) THEN 0	ELSE NULL END) END) = 1) AS `TEMP(Test)(1285160207)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1285160207)(0)");
    }

    @Test
    public void testCALCS161() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`num0` ELSE `Calcs`.`num1` END) AS `TEMP(Test)(898375479)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(898375479)(0)");
    }

    @Test
    public void testCALCS162() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`date0` ELSE `Calcs`.`date1` END) AS `TEMP(Test)(3012038505)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3012038505)(0)");
    }

    @Test
    public void testCALCS163() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`bool0_` <> 0) THEN `Calcs`.`str2` ELSE `Calcs`.`str3` END) AS `TEMP(Test)(490796425)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(490796425)(0)");
    }

    @Test
    public void testCALCS164() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 60) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 60)) AS `TEMP(Test)(2300448284)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2300448284)(0)");
    }

    @Test
    public void testCALCS165() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 60) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 60)) AS `TEMP(Test)(2077207759)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2077207759)(0)");
    }

    @Test
    public void testCALCS166() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT RADIANS(`Calcs`.`int2`) AS `TEMP(Test)(1973795369)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1973795369)(0)");
    }

    @Test
    public void testCALCS167() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT RADIANS(`Calcs`.`num0`) AS `TEMP(Test)(2823743498)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2823743498)(0)");
    }

    @Test
    public void testCALCS168() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOG(`Calcs`.`int2`) AS `TEMP(Test)(2832324438)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2832324438)(0)");
    }

    @Test
    public void testCALCS169() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOG(`Calcs`.`num0`) AS `TEMP(Test)(1125921255)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1125921255)(0)");
    }

    @Test
    public void testCALCS170() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 3600) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 3600)) AS `TEMP(Test)(289918985)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(289918985)(0)");
    }

    @Test
    public void testCALCS171() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(3370976929)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3370976929)(0)");
    }

    @Test
    public void testCALCS172() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(DATE(`Calcs`.`date3`)) - 1 + DAYOFWEEK(DATE_FORMAT(DATE(`Calcs`.`date3`), '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(2942029924)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2942029924)(0)");
    }

    @Test
    public void testCALCS173() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(3904538922)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3904538922)(0)");
    }

    @Test
    public void testCALCS174() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(3904538922)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3904538922)(1)");
    }

    @Test
    public void testCALCS175() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(1590117682)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1590117682)(0)");
    }

    @Test
    public void testCALCS176() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(4199707040)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4199707040)(0)");
    }

    @Test
    public void testCALCS177() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(2589771434)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2589771434)(0)");
    }

    @Test
    public void testCALCS178() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(1875124737)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1875124737)(0)");
    }

    @Test
    public void testCALCS179() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE(`Calcs`.`num4`,0) AS `TEMP(Test)(663412696)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(663412696)(0)");
    }

    @Test
    public void testCALCS180() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE(CONCAT(CONCAT(`Calcs`.`num4`), CONCAT(`Calcs`.`int0`)),0) AS `TEMP(Test)(1616170242)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1616170242)(0)");
    }

    @Test
    public void testCALCS181() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(3854194266)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3854194266)(0)");
    }

    @Test
    public void testCALCS182() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`date2`) AS `TEMP(Test)(3854194266)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3854194266)(1)");
    }

    @Test
    public void testCALCS183() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(621889678)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(621889678)(0)");
    }

    @Test
    public void testCALCS184() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(`Calcs`.`datetime0`) AS `TEMP(Test)(621889678)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(621889678)(1)");
    }

    @Test
    public void testCALCS185() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(302607578)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(302607578)(0)");
    }

    @Test
    public void testCALCS186() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(2001673842)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2001673842)(0)");
    }

    @Test
    public void testCALCS187() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60*60 + (TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)))) AS `TEMP(Test)(3772571288)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3772571288)(0)");
    }

    @Test
    public void testCALCS188() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60*60 + (TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)))) AS `TEMP(Test)(3405329770)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3405329770)(0)");
    }

    @Test
    public void testCALCS189() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(885008067)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(885008067)(0)");
    }

    @Test
    public void testCALCS190() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(3554344781)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3554344781)(0)");
    }

    @Test
    public void testCALCS191() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(DATE(`Calcs`.`date2`)) AS `TEMP(Test)(2085924889)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2085924889)(0)");
    }

    @Test
    public void testCALCS192() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(TIMESTAMP(`Calcs`.`datetime0`)) AS `TEMP(Test)(574618496)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(574618496)(0)");
    }

    @Test
    public void testCALCS193() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 1 AS `TEMP(Test)(3095770696)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3095770696)(0)");
    }

    @Test
    public void testCALCS194() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(DATE(`Calcs`.`date2`)) AS `TEMP(Test)(1165289219)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1165289219)(0)");
    }

    @Test
    public void testCALCS195() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(TIMESTAMP(`Calcs`.`datetime0`)) AS `TEMP(Test)(3278952934)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3278952934)(0)");
    }

    @Test
    public void testCALCS196() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(DATE(`Calcs`.`date2`)) AS `TEMP(Test)(3434755864)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3434755864)(0)");
    }

    @Test
    public void testCALCS197() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(TIMESTAMP(`Calcs`.`datetime0`)) AS `TEMP(Test)(1819497289)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1819497289)(0)");
    }

    @Test
    public void testCALCS198() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COT(`Calcs`.`int2`) AS `TEMP(Test)(2415226193)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS199() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COT(`Calcs`.`num0`) AS `TEMP(Test)(2834009176)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS200() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(COUNT(DISTINCT `Calcs`.`num2`), 0) AS `TEMP(Test)(957319405)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(957319405)(0)");
    }

    @Test
    public void testCALCS201() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(`Calcs`.`datetime0`) AS `TEMP(Test)(3191651815)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3191651815)(0)");
    }

    @Test
    public void testCALCS202() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("           order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS203() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`date2`, '%Y-' ), (3*(QUARTER(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1126788499)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1126788499)(0)");
    }

    @Test
    public void testCALCS204() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-' ), (3*(QUARTER(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3855281255)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3855281255)(0)");
    }

    @Test
    public void testCALCS205() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SUM(`Calcs`.`int0`) AS `TEMP(Test)(645427419)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(645427419)(0)");
    }

    @Test
    public void testCALCS206() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SUM(`Calcs`.`num4`) AS `TEMP(Test)(1450575838)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1450575838)(0)");
    }

    @Test
    public void testCALCS207() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2456153780)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2456153780)(0)");
    }

    @Test
    public void testCALCS208() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(2524080111)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2524080111)(0)");
    }

    @Test
    public void testCALCS209() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(1568799041)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1568799041)(0)");
    }

    @Test
    public void testCALCS210() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`date2`) - ((7 + DAYOFWEEK(`Calcs`.`date2`) - 2) % 7) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1744581337)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1744581337)(0)");
    }

    @Test
    public void testCALCS211() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`date2`) - (DAYOFWEEK(`Calcs`.`date2`) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1635756518)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1635756518)(0)");
    }

    @Test
    public void testCALCS212() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`datetime0`) - ((7 + DAYOFWEEK(`Calcs`.`datetime0`) - 2) % 7) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1985269479)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1985269479)(0)");
    }

    @Test
    public void testCALCS213() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`datetime0`) - (DAYOFWEEK(`Calcs`.`datetime0`) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3887385220)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3887385220)(0)");
    }

    @Test
    public void testCALCS214() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'ta' AS `TEMP(Test)(2843244905)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2843244905)(0)");
    }

    @Test
    public void testCALCS215() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN 3 >= 0 THEN RIGHT(`Calcs`.`str2`,3) ELSE NULL END) AS `TEMP(Test)(868342576)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(868342576)(0)");
    }

    @Test
    public void testCALCS216() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN `Calcs`.`int0` >= 0 THEN RIGHT(`Calcs`.`str2`,`Calcs`.`int0`) ELSE NULL END) AS `TEMP(Test)(427841631)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(427841631)(0)");
    }

    @Test
    public void testCALCS217() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 60) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 60)) AS `TEMP(Test)(2180476504)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2180476504)(0)");
    }

    @Test
    public void testCALCS218() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(3361088979)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3361088979)(0)");
    }

    @Test
    public void testCALCS219() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(299717125)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(299717125)(0)");
    }

    @Test
    public void testCALCS220() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(3076245501)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3076245501)(0)");
    }

    @Test
    public void testCALCS221() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(148436784)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(148436784)(0)");
    }

    @Test
    public void testCALCS222() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LOCATE('e',`Calcs`.`str2`) > 0) AS `TEMP(Test)(1364536471)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1364536471)(0)");
    }

    @Test
    public void testCALCS223() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LOCATE('IND',`Calcs`.`str1`) > 0) AS `TEMP(Test)(1380546255)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1380546255)(0)");
    }

    @Test
    public void testCALCS224() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL 1 DAY) AS `TEMP(Test)(1743407296)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1743407296)(0)");
    }

    @Test
    public void testCALCS225() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 DAY) AS `TEMP(Test)(2988076353)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2988076353)(0)");
    }

    @Test
    public void testCALCS226() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SIGN(`Calcs`.`int2`) AS `TEMP(Test)(3509671532)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3509671532)(0)");
    }

    @Test
    public void testCALCS227() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SIGN(`Calcs`.`num0`) AS `TEMP(Test)(4247289834)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4247289834)(0)");
    }

    @Test
    public void testCALCS228() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(`Calcs`.`int2`,2) AS `TEMP(Test)(3037854782)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3037854782)(0)");
    }

    @Test
    public void testCALCS229() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(CONCAT('      ', `Calcs`.`str2`), '      ') AS `TEMP(Test)(2313738384)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2313738384)(0)");
    }

    @Test
    public void testCALCS230() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'CONST' AS `TEMP(Test)(3972932107)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3972932107)(0)");
    }

    @Test
    public void testCALCS231() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ACOS((`Calcs`.`num0` / 20)) AS `TEMP(Test)(4196263986)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4196263986)(0)");
    }

    @Test
    public void testCALCS232() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1773778045)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1773778045)(0)");
    }

    @Test
    public void testCALCS233() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(382789366)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(382789366)(0)");
    }

    @Test
    public void testCALCS234() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(444902156)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(444902156)(0)");
    }

    @Test
    public void testCALCS235() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(581676997)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(581676997)(0)");
    }

    @Test
    public void testCALCS236() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-' ), (3*(QUARTER(FROM_DAYS(FLOOR(NULL) + 693961))-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1831450015)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1831450015)(0)");
    }

    @Test
    public void testCALCS237() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-' ), (3*(QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(360201683)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(360201683)(0)");
    }

    @Test
    public void testCALCS238() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(872678106)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(872678106)(0)");
    }

    @Test
    public void testCALCS239() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3905701997)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3905701997)(0)");
    }

    @Test
    public void testCALCS240() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3359079369)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3359079369)(0)");
    }

    @Test
    public void testCALCS241() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1326289938)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1326289938)(0)");
    }

    @Test
    public void testCALCS242() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2763829899)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2763829899)(0)");
    }

    @Test
    public void testCALCS243() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(717997108)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(717997108)(0)");
    }

    @Test
    public void testCALCS244() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2963633898)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2963633898)(0)");
    }

    @Test
    public void testCALCS245() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3202209617)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3202209617)(0)");
    }

    @Test
    public void testCALCS246() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(4266496460)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4266496460)(0)");
    }

    @Test
    public void testCALCS247() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:%i:%s' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(4131996060)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4131996060)(0)");
    }

    @Test
    public void testCALCS248() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:%i:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2935754523)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2935754523)(0)");
    }

    @Test
    public void testCALCS249() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`str1`) THEN NULL	WHEN ISNULL(`Calcs`.`str2`) THEN NULL	ELSE GREATEST(`Calcs`.`str1`, `Calcs`.`str2`) END) AS `TEMP(Test)(3052188625)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3052188625)(0)");
    }

    @Test
    public void testCALCS250() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`str3`) THEN NULL	WHEN ISNULL(`Calcs`.`str2`) THEN NULL	ELSE GREATEST(`Calcs`.`str3`, `Calcs`.`str2`) END) AS `TEMP(Test)(2280873463)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2280873463)(0)");
    }

    @Test
    public void testCALCS251() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(2643375604)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2643375604)(0)");
    }

    @Test
    public void testCALCS252() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(2986242609)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2986242609)(0)");
    }

    @Test
    public void testCALCS253() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(1608337423)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1608337423)(0)");
    }

    @Test
    public void testCALCS254() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(925465559)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(925465559)(0)");
    }

    @Test
    public void testCALCS255() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN `Calcs`.`num4` >= 0 THEN RIGHT(`Calcs`.`str0`,`Calcs`.`num4`) ELSE NULL END) AS `TEMP(Test)(3619367444)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS256() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL (3 * 1) MONTH) AS `TEMP(Test)(893348878)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(893348878)(0)");
    }

    @Test
    public void testCALCS257() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL (3 * 1) MONTH) AS `TEMP(Test)(454013980)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(454013980)(0)");
    }

    @Test
    public void testCALCS258() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7)) AS `TEMP(Test)(499182808)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(499182808)(0)");
    }

    @Test
    public void testCALCS259() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(2644944117)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2644944117)(0)");
    }

    @Test
    public void testCALCS260() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7)) AS `TEMP(Test)(3094931040)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3094931040)(0)");
    }

    @Test
    public void testCALCS261() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(2831690081)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2831690081)(0)");
    }

    @Test
    public void testCALCS262() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IF(ISNULL(6), NULL, SUBSTRING(`Calcs`.`str1`,GREATEST(1,FLOOR(6)))) AS `TEMP(Test)(98307893)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(98307893)(0)");
    }

    @Test
    public void testCALCS263() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IF(ISNULL(2), NULL, SUBSTRING(`Calcs`.`str1`,GREATEST(1,FLOOR(2)),FLOOR(4))) AS `TEMP(Test)(3472698691)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3472698691)(0)");
    }

    @Test
    public void testCALCS264() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN `Calcs`.`num4` >= 0 THEN LEFT(`Calcs`.`str0`,`Calcs`.`num4`) ELSE NULL END) AS `TEMP(Test)(1907571572)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS265() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(1667583030)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1667583030)(0)");
    }

    @Test
    public void testCALCS266() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(2537119552)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2537119552)(0)");
    }

    @Test
    public void testCALCS267() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int1` + 0.0) AS `TEMP(Test)(1533389080)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1533389080)(0)");
    }

    @Test
    public void testCALCS268() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE        WHEN (`Calcs`.`bool0_` <> 0) THEN 1.0        WHEN NOT (`Calcs`.`bool0_` <> 0) THEN 0.0        ELSE NULL END) AS `TEMP(Test)(2538631291)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2538631291)(0)");
    }

    @Test
    public void testCALCS269() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date0`) - 693961.0 + TIME_TO_SEC(ADDDATE(`Calcs`.`date0`, INTERVAL 0 SECOND)) / (24.0 * 60.0 * 60.0) ) AS `TEMP(Test)(64617177)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(64617177)(0)");
    }

    @Test
    public void testCALCS270() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num2` + 0.0) AS `TEMP(Test)(2707307071)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2707307071)(0)");
    }

    @Test
    public void testCALCS271() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (0.0 + `Calcs`.`str2`) AS `TEMP(Test)(1394352864)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1394352864)(0)");
    }

    @Test
    public void testCALCS272() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(IFNULL(TIMESTAMP('2010-10-10 10:10:10.4'),STR_TO_DATE('2010-10-10 10:10:10.4','%b %e %Y %l:%i%p'))) AS `TEMP(Test)(2143701310)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2143701310)(0)");
    }

    @Test
    public void testCALCS273() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOWER(`Calcs`.`str2`) AS `TEMP(Test)(1011144549)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1011144549)(0)");
    }

    @Test
    public void testCALCS274() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOWER(`Calcs`.`str1`) AS `TEMP(Test)(2419238545)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2419238545)(0)");
    }

    @Test
    public void testCALCS275() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(2997515538)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2997515538)(0)");
    }

    @Test
    public void testCALCS276() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(4264664103)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4264664103)(0)");
    }

    @Test
    public void testCALCS277() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 3.1415926535897931 AS `TEMP(Test)(356598120)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(356598120)(0)");
    }

    @Test
    public void testCALCS278() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (3.1415926535897931 * `Calcs`.`num0`) AS `TEMP(Test)(1299212312)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1299212312)(0)");
    }

    @Test
    public void testCALCS279() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7) AS `TEMP(Test)(3400925592)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3400925592)(0)");
    }

    @Test
    public void testCALCS280() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(1636919423)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1636919423)(0)");
    }

    @Test
    public void testCALCS281() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7) AS `TEMP(Test)(3400925592)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3400925592)(1)");
    }

    @Test
    public void testCALCS282() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`date2`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`date2`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(1636919423)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1636919423)(1)");
    }

    @Test
    public void testCALCS283() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7) AS `TEMP(Test)(3595934100)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3595934100)(0)");
    }

    @Test
    public void testCALCS284() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(4171408365)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4171408365)(0)");
    }

    @Test
    public void testCALCS285() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + ((7 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 2) % 7) ) / 7) AS `TEMP(Test)(3595934100)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3595934100)(1)");
    }

    @Test
    public void testCALCS286() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Calcs`.`datetime0`) - 1 + DAYOFWEEK(DATE_FORMAT(`Calcs`.`datetime0`, '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(4171408365)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4171408365)(1)");
    }

    @Test
    public void testCALCS287() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`date2`) AS `TEMP(Test)(1706489238)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1706489238)(0)");
    }

    @Test
    public void testCALCS288() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`date2`) AS `TEMP(Test)(3326454598)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3326454598)(0)");
    }

    @Test
    public void testCALCS289() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(1346443059)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1346443059)(0)");
    }

    @Test
    public void testCALCS290() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(2366796649)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2366796649)(0)");
    }

    @Test
    public void testCALCS291() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TAN(`Calcs`.`int2`) AS `TEMP(Test)(1227693937)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1227693937)(0)");
    }

    @Test
    public void testCALCS292() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DEGREES(`Calcs`.`int2`) AS `TEMP(Test)(2688244734)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2688244734)(0)");
    }

    @Test
    public void testCALCS293() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DEGREES(`Calcs`.`num0`) AS `TEMP(Test)(583539797)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(583539797)(0)");
    }

    @Test
    public void testCALCS294() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(1438827077)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1438827077)(0)");
    }

    @Test
    public void testCALCS295() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(331799714)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(331799714)(0)");
    }

    @Test
    public void testCALCS296() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(3561169943)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3561169943)(0)");
    }

    @Test
    public void testCALCS297() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(2283476857)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2283476857)(0)");
    }

    @Test
    public void testCALCS298() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(523796786)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(523796786)(0)");
    }

    @Test
    public void testCALCS299() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(1757347367)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1757347367)(0)");
    }

    @Test
    public void testCALCS300() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961)))*4 + (QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) - QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)))) AS `TEMP(Test)(2892653053)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2892653053)(0)");
    }

    @Test
    public void testCALCS301() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*4 + (QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))) AS `TEMP(Test)(208306356)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(208306356)(0)");
    }

    @Test
    public void testCALCS302() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*12 + (MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))) AS `TEMP(Test)(3602652935)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3602652935)(0)");
    }

    @Test
    public void testCALCS303() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961)))*12 + (MONTH(FROM_DAYS(FLOOR(NULL) + 693961)) - MONTH(FROM_DAYS(FLOOR(NULL) + 693961)))) AS `TEMP(Test)(2736821)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2736821)(0)");
    }

    @Test
    public void testCALCS304() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1)) - (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1) ) )/7) AS `TEMP(Test)(4175150207)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4175150207)(0)");
    }

    @Test
    public void testCALCS305() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1)) - (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1) ) )/7) AS `TEMP(Test)(573134401)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(573134401)(0)");
    }

    @Test
    public void testCALCS306() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(4284829593)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4284829593)(0)");
    }

    @Test
    public void testCALCS307() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(2962792486)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2962792486)(0)");
    }

    @Test
    public void testCALCS308() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(2631483492)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2631483492)(0)");
    }

    @Test
    public void testCALCS309() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(1607049625)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1607049625)(0)");
    }

    @Test
    public void testCALCS310() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(1299959868)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1299959868)(0)");
    }

    @Test
    public void testCALCS311() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(1641185958)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1641185958)(0)");
    }

    @Test
    public void testCALCS312() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24 + FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 3600) - FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 3600)) AS `TEMP(Test)(1258940435)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1258940435)(0)");
    }

    @Test
    public void testCALCS313() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24*60 + FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 60) - FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 60)) AS `TEMP(Test)(401058515)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(401058515)(0)");
    }

    @Test
    public void testCALCS314() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24*60*60 + (TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)))) AS `TEMP(Test)(2833809390)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2833809390)(0)");
    }

    @Test
    public void testCALCS315() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(367110610)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(367110610)(0)");
    }

    @Test
    public void testCALCS316() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(1785761163)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1785761163)(0)");
    }

    @Test
    public void testCALCS317() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT '>  <' AS `TEMP(Test)(3167158121)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3167158121)(0)");
    }

    @Test
    public void testCALCS318() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`date2`, '%Y-' ), (3*(QUARTER(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(4146692480)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4146692480)(0)");
    }

    @Test
    public void testCALCS319() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`date2`, '%Y-' ), (3*(QUARTER(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(560528826)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(560528826)(0)");
    }

    @Test
    public void testCALCS320() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-' ), (3*(QUARTER(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(105511240)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(105511240)(0)");
    }

    @Test
    public void testCALCS321() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-' ), (3*(QUARTER(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(755301458)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(755301458)(0)");
    }

    @Test
    public void testCALCS322() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(1699663235)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1699663235)(0)");
    }

    @Test
    public void testCALCS323() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(1554256126)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1554256126)(0)");
    }

    @Test
    public void testCALCS324() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(2171721785)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2171721785)(0)");
    }

    @Test
    public void testCALCS325() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(3941430330)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3941430330)(0)");
    }

    @Test
    public void testCALCS326() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (STD(`Calcs`.`num4`) * SQRT(count(`Calcs`.`num4`) / (count(`Calcs`.`num4`) - 1))) AS `TEMP(Test)(2430775290)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2430775290)(0)");
    }

    @Test
    public void testCALCS327() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT STD(`Calcs`.`num4`) AS `TEMP(Test)(3542464170)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3542464170)(0)");
    }

    @Test
    public void testCALCS328() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`date2`) AS `TEMP(Test)(3471130809)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3471130809)(0)");
    }

    @Test
    public void testCALCS329() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(482138814)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(482138814)(0)");
    }

    @Test
    public void testCALCS330() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRIM(CONCAT(CONCAT(' ', `Calcs`.`str2`), ' ')) AS `TEMP(Test)(1903992131)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1903992131)(0)");
    }

    @Test
    public void testCALCS331() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER((STD(`Calcs`.`num4`) * SQRT(count(`Calcs`.`num4`) / (count(`Calcs`.`num4`) - 1))), 2) AS `TEMP(Test)(1358865)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1358865)(0)");
    }

    @Test
    public void testCALCS332() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(STD(`Calcs`.`num4`),2) AS `TEMP(Test)(2532468070)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2532468070)(0)");
    }

    @Test
    public void testCALCS333() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(4265410721)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4265410721)(0)");
    }

    @Test
    public void testCALCS334() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(1278698096)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1278698096)(0)");
    }

    @Test
    public void testCALCS335() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(3729248905)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3729248905)(0)");
    }

    @Test
    public void testCALCS336() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(965356852)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(965356852)(0)");
    }

    @Test
    public void testCALCS337() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 4 AS `TEMP(Test)(5037157)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(5037157)(0)");
    }

    @Test
    public void testCALCS338() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LENGTH(`Calcs`.`str2`) AS `TEMP(Test)(382448263)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(382448263)(0)");
    }

    @Test
    public void testCALCS339() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`date2`) AS `TEMP(Test)(653088523)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(653088523)(0)");
    }

    @Test
    public void testCALCS340() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(`Calcs`.`datetime0`) AS `TEMP(Test)(3134852500)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3134852500)(0)");
    }

    @Test
    public void testCALCS341() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(`Calcs`.`int1`, 0) AS `TEMP(Test)(3976315675)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3976315675)(0)");
    }

    @Test
    public void testCALCS342() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(591126205)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(591126205)(0)");
    }

    @Test
    public void testCALCS343() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3034828475)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3034828475)(0)");
    }

    @Test
    public void testCALCS344() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("           order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS345() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE `Calcs`.`int0` WHEN 1 THEN 'test1' WHEN 3 THEN 'test3' ELSE 'testelse' END) AS `TEMP(Test)(4155671032)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4155671032)(0)");
    }

    @Test
    public void testCALCS346() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`int0` = 1) THEN 'yes' ELSE 'no' END) AS `TEMP(Test)(344883989)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(344883989)(0)");
    }

    @Test
    public void testCALCS347() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`int0` = 1) THEN 'yes' WHEN (`Calcs`.`int0` = 3) THEN 'yes3' ELSE 'no' END) AS `TEMP(Test)(1470681487)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1470681487)(0)");
    }

    @Test
    public void testCALCS348() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(`Calcs`.`int0`, 0) AS `TEMP(Test)(404394451)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(404394451)(0)");
    }

    @Test
    public void testCALCS349() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`int0` > 3) THEN 'yes' WHEN NOT (`Calcs`.`int0` > 3) THEN 'no' ELSE NULL END) AS `TEMP(Test)(2582407534)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2582407534)(0)");
    }

    @Test
    public void testCALCS350() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`int0` > 3) THEN 'yes' WHEN NOT (`Calcs`.`int0` > 3) THEN 'no' ELSE 'I dont know' END) AS `TEMP(Test)(485230187)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(485230187)(0)");
    }

    @Test
    public void testCALCS351() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ISNULL(`Calcs`.`int0`) AS `TEMP(Test)(3944872634)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3944872634)(0)");
    }

    @Test
    public void testCALCS352() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'yes' AS `TEMP(Test)(1030668643)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1030668643)(0)");
    }

    @Test
    public void testCALCS353() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`int0` <> 1) THEN 'yes' ELSE 'no' END) AS `TEMP(Test)(1548476355)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1548476355)(0)");
    }

    @Test
    public void testCALCS354() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`num0` WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`num1` ELSE NULL END) AS `TEMP(Test)(2733626226)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2733626226)(0)");
    }

    @Test
    public void testCALCS355() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ISNULL(`Calcs`.`num4`) AS `TEMP(Test)(746449830)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(746449830)(0)");
    }

    @Test
    public void testCALCS356() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ISNULL(`Calcs`.`str2`) AS `TEMP(Test)(4153117630)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4153117630)(0)");
    }

    @Test
    public void testCALCS357() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`str0` > `Calcs`.`str1`) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`str0` > `Calcs`.`str1`) THEN `Calcs`.`str3` ELSE NULL END) AS `TEMP(Test)(661341884)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(661341884)(0)");
    }

    @Test
    public void testCALCS358() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`date0`) THEN NULL	WHEN ISNULL(`Calcs`.`date1`) THEN NULL	ELSE LEAST(`Calcs`.`date0`, `Calcs`.`date1`) END) AS `TEMP(Test)(1970381992)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1970381992)(0)");
    }

    @Test
    public void testCALCS359() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`date0` WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`date1` ELSE `Calcs`.`date2` END) AS `TEMP(Test)(2049518482)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2049518482)(0)");
    }

    @Test
    public void testCALCS360() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOWER(`Calcs`.`str0`) AS `TEMP(Test)(157987442)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(157987442)(0)");
    }

    @Test
    public void testCALCS361() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`str3` ELSE `Calcs`.`str0` END) AS `TEMP(Test)(3250337019)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3250337019)(0)");
    }

    @Test
    public void testCALCS362() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`date0` WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`date1` ELSE NULL END) AS `TEMP(Test)(1454773621)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1454773621)(0)");
    }

    @Test
    public void testCALCS363() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`num0` WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`num1` ELSE `Calcs`.`num2` END) AS `TEMP(Test)(1162317302)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1162317302)(0)");
    }

    @Test
    public void testCALCS364() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(LOWER(`Calcs`.`str0`)) THEN NULL	WHEN ISNULL(`Calcs`.`str2`) THEN NULL	ELSE LEAST(LOWER(`Calcs`.`str0`), `Calcs`.`str2`) END) AS `TEMP(Test)(1389344980)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1389344980)(0)");
    }

    @Test
    public void testCALCS365() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(`Calcs`.`date0`, DATE('2010-04-12')) AS `TEMP(Test)(1229425804)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1229425804)(0)");
    }

    @Test
    public void testCALCS366() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(`Calcs`.`num4`, -1) AS `TEMP(Test)(4224438892)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4224438892)(0)");
    }

    @Test
    public void testCALCS367() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IFNULL(`Calcs`.`str2`, 'i''m null') AS `TEMP(Test)(3314993157)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3314993157)(0)");
    }

    @Test
    public void testCALCS368() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ISNULL(`Calcs`.`date0`) AS `TEMP(Test)(2842042984)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2842042984)(0)");
    }

    @Test
    public void testCALCS369() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN (CASE	WHEN (`Calcs`.`bool1_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool1_` <> 0) THEN 0	ELSE NULL END) ELSE (CASE	WHEN (`Calcs`.`bool2_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool2_` <> 0) THEN 0	ELSE NULL END) END) = 1) AS `TEMP(Test)(4227881224)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4227881224)(0)");
    }

    @Test
    public void testCALCS370() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`num0` ELSE `Calcs`.`num1` END) AS `TEMP(Test)(709594122)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(709594122)(0)");
    }

    @Test
    public void testCALCS371() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN `Calcs`.`date0` ELSE `Calcs`.`date1` END) AS `TEMP(Test)(467266194)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(467266194)(0)");
    }

    @Test
    public void testCALCS372() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`str0` > `Calcs`.`str1`) THEN `Calcs`.`str2` ELSE `Calcs`.`str3` END) AS `TEMP(Test)(2963734906)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2963734906)(0)");
    }

    @Test
    public void testCALCS373() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN 1	WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`num0` WHEN 0 THEN `Calcs`.`num1` ELSE `Calcs`.`num2` END) AS `TEMP(Test)(4143049742)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4143049742)(0)");
    }

    @Test
    public void testCALCS374() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN 1	WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`date0` WHEN 0 THEN `Calcs`.`date1` ELSE `Calcs`.`date2` END) AS `TEMP(Test)(1171954805)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1171954805)(0)");
    }

    @Test
    public void testCALCS375() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`num0` > `Calcs`.`num1`) THEN 1	WHEN NOT (`Calcs`.`num0` > `Calcs`.`num1`) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`str2` WHEN 0 THEN `Calcs`.`str3` ELSE `Calcs`.`str0` END) AS `TEMP(Test)(2451799140)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2451799140)(0)");
    }

    @Test
    public void testCALCS376() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`bool0_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool0_` <> 0) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`num0` WHEN 0 THEN `Calcs`.`num1` ELSE `Calcs`.`num2` END) AS `TEMP(Test)(1574830296)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1574830296)(0)");
    }

    @Test
    public void testCALCS377() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`bool0_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool0_` <> 0) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`date0` WHEN 0 THEN `Calcs`.`date1` ELSE `Calcs`.`date2` END) AS `TEMP(Test)(49931887)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(49931887)(0)");
    }

    @Test
    public void testCALCS378() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (DAYOFWEEK(`Calcs`.`date1`) IN (1, 7)) THEN NULL ELSE `Calcs`.`date1` END) AS `TEMP(Test)(1471931871)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1471931871)(0)");
    }

    @Test
    public void testCALCS379() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE (CASE	WHEN (`Calcs`.`bool0_` <> 0) THEN 1	WHEN NOT (`Calcs`.`bool0_` <> 0) THEN 0	ELSE NULL END) WHEN 1 THEN `Calcs`.`str2` WHEN 0 THEN `Calcs`.`str3` ELSE `Calcs`.`str0` END) AS `TEMP(Test)(166894492)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(166894492)(0)");
    }

    @Test
    public void testCALCS380() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE WHEN (`Calcs`.`str1` = 'CLOCKS') THEN '*Anonymous*' WHEN (`Calcs`.`str1` = 'DVD') THEN '*Public*' ELSE `Calcs`.`str1` END) AS `TEMP(Test)(899461877)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(899461877)(0)");
    }

    @Test
    public void testCALCS381() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`date2`) - (DAYOFWEEK(`Calcs`.`date2`) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1630131013)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1630131013)(0)");
    }

    @Test
    public void testCALCS382() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(`Calcs`.`datetime0`) - (DAYOFWEEK(`Calcs`.`datetime0`) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3937478358)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3937478358)(0)");
    }

    @Test
    public void testCALCS383() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(`Calcs`.`datetime0`) AS `TEMP(Test)(1235924899)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1235924899)(0)");
    }

    @Test
    public void testCALCS384() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 SECOND) AS `TEMP(Test)(621896091)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(621896091)(0)");
    }

    @Test
    public void testCALCS385() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24*60*60 + (TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)))) AS `TEMP(Test)(3711433751)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3711433751)(0)");
    }

    @Test
    public void testCALCS386() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`)) AS `TEMP(Test)(427588088)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(427588088)(0)");
    }

    @Test
    public void testCALCS387() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`)) AS `TEMP(Test)(2526313076)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2526313076)(0)");
    }

    @Test
    public void testCALCS388() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(1540391660)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1540391660)(0)");
    }

    @Test
    public void testCALCS389() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(3579576882)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3579576882)(0)");
    }

    @Test
    public void testCALCS390() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(`Calcs`.`datetime0`) AS `TEMP(Test)(2740605400)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2740605400)(0)");
    }

    @Test
    public void testCALCS391() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(`Calcs`.`datetime0`) AS `TEMP(Test)(356589430)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(356589430)(0)");
    }

    @Test
    public void testCALCS392() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 3600) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 3600)) AS `TEMP(Test)(1898404202)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1898404202)(0)");
    }

    @Test
    public void testCALCS393() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`)))*24 + FLOOR(TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / 3600) - FLOOR(TIME_TO_SEC(ADDDATE(TIMESTAMP(`Calcs`.`date2`), INTERVAL 0 SECOND)) / 3600)) AS `TEMP(Test)(4263325709)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4263325709)(0)");
    }

    @Test
    public void testCALCS394() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ABS(`Calcs`.`num0`) AS `TEMP(Test)(3816473022)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3816473022)(0)");
    }

    @Test
    public void testCALCS395() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num0` AS `TEMP(Test)(965512284)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(965512284)(0)");
    }

    @Test
    public void testCALCS396() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Calcs`.`num1` AS `TEMP(Test)(1826927073)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1826927073)(0)");
    }

    @Test
    public void testCALCS397() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((`Calcs`.`bool0_` <> 0) AND (`Calcs`.`bool1_` <> 0)) AS `TEMP(Test)(3618731173)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3618731173)(0)");
    }

    @Test
    public void testCALCS398() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((`Calcs`.`bool0_` <> 0) AND (`Calcs`.`bool1_` <> 0) OR NOT (`Calcs`.`bool0_` <> 0) AND NOT (`Calcs`.`bool1_` <> 0)) AS `TEMP(Test)(830571724)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(830571724)(0)");
    }

    @Test
    public void testCALCS399() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((`Calcs`.`bool0_` <> 0) AND NOT (`Calcs`.`bool1_` <> 0) OR NOT (`Calcs`.`bool0_` <> 0) AND (`Calcs`.`bool1_` <> 0)) AS `TEMP(Test)(3090944671)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3090944671)(0)");
    }

    @Test
    public void testCALCS400() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((`Calcs`.`bool0_` <> 0) OR (`Calcs`.`bool1_` <> 0)) AS `TEMP(Test)(4182992858)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4182992858)(0)");
    }

    @Test
    public void testCALCS401() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` = DATE('1972-07-04')) AS `TEMP(Test)(397499995)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(397499995)(0)");
    }

    @Test
    public void testCALCS402() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` >= DATE('1975-11-12')) AS `TEMP(Test)(1366787273)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1366787273)(0)");
    }

    @Test
    public void testCALCS403() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` > DATE('1975-11-12')) AS `TEMP(Test)(3193322782)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3193322782)(0)");
    }

    @Test
    public void testCALCS404() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` <= DATE('1975-11-12')) AS `TEMP(Test)(822657216)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(822657216)(0)");
    }

    @Test
    public void testCALCS405() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` < DATE('1975-11-12')) AS `TEMP(Test)(3764753091)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3764753091)(0)");
    }

    @Test
    public void testCALCS406() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`date0`) - TO_DAYS(`Calcs`.`datetime0`)) + (TIME_TO_SEC(ADDDATE(`Calcs`.`date0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) AS `TEMP(Test)(937166222)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(937166222)(0)");
    }

    @Test
    public void testCALCS407() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(DATE('2004-01-01'))) + (TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(DATE('2004-01-01'), INTERVAL 0 SECOND))) / (60 * 60 * 24)) AS `TEMP(Test)(100938644)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(100938644)(0)");
    }

    @Test
    public void testCALCS408() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "SELECT DATE_SUB(DATE_SUB(`Calcs`.`date0`, INTERVAL FLOOR(`Calcs`.`num4`) DAY), INTERVAL 60 * 60 * 24 * (`Calcs`.`num4` - FLOOR(`Calcs`.`num4`)) SECOND) AS `TEMP(Test)(2923065813)(0)` FROM `Calcs` GROUP BY 1 order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2923065813)(0)");
    }

    @Test
    public void testCALCS409() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date0` <> DATE('1975-11-12')) AS `TEMP(Test)(798936259)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(798936259)(0)");
    }

    @Test
    public void testCALCS410() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(`Calcs`.`date0`, INTERVAL FLOOR(`Calcs`.`num4`) DAY), INTERVAL 60 * 60 * 24 * (`Calcs`.`num4` - FLOOR(`Calcs`.`num4`)) SECOND) AS `TEMP(Test)(2067341949)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2067341949)(0)");
    }

    @Test
    public void testCALCS411() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 0 AS `TEMP(Test)(1303362598)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1303362598)(0)");
    }

    @Test
    public void testCALCS412() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int0` % `Calcs`.`int1`) AS `TEMP(Test)(1307456344)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1307456344)(0)");
    }

    @Test
    public void testCALCS413() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int0` / `Calcs`.`int1`) AS `TEMP(Test)(2402101080)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2402101080)(0)");
    }

    @Test
    public void testCALCS414() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int3` / `Calcs`.`int2`) AS `TEMP(Test)(3559262472)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3559262472)(0)");
    }

    @Test
    public void testCALCS415() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(`Calcs`.`int0`,`Calcs`.`num1`) AS `TEMP(Test)(4265403921)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4265403921)(0)");
    }

    @Test
    public void testCALCS416() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (-`Calcs`.`num0`) AS `TEMP(Test)(4188722171)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4188722171)(0)");
    }

    @Test
    public void testCALCS417() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`bool0_` = 0) AS `TEMP(Test)(1413132553)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1413132553)(0)");
    }

    @Test
    public void testCALCS418() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT NULL AS `TEMP(Test)(496893948)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(496893948)(0)");
    }

    @Test
    public void testCALCS419() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` / `Calcs`.`num1`) AS `TEMP(Test)(272703322)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(272703322)(0)");
    }

    @Test
    public void testCALCS420() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` = ABS(`Calcs`.`num0`)) AS `TEMP(Test)(3360366790)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3360366790)(0)");
    }

    @Test
    public void testCALCS421() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` = ABS(`Calcs`.`num0`)) AS `TEMP(Test)(2564078271)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2564078271)(0)");
    }

    @Test
    public void testCALCS422() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` >= `Calcs`.`num1`) AS `TEMP(Test)(1366300770)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1366300770)(0)");
    }

    @Test
    public void testCALCS423() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` > `Calcs`.`num1`) AS `TEMP(Test)(4123004830)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4123004830)(0)");
    }

    @Test
    public void testCALCS424() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` <= `Calcs`.`num1`) AS `TEMP(Test)(1224631717)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1224631717)(0)");
    }

    @Test
    public void testCALCS425() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` < `Calcs`.`num1`) AS `TEMP(Test)(1731699042)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1731699042)(0)");
    }

    @Test
    public void testCALCS426() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` - `Calcs`.`num1`) AS `TEMP(Test)(3781247900)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3781247900)(0)");
    }

    @Test
    public void testCALCS427() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` <> ABS(`Calcs`.`num0`)) AS `TEMP(Test)(4047276454)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4047276454)(0)");
    }

    @Test
    public void testCALCS428() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` <> ABS(`Calcs`.`num0`)) AS `TEMP(Test)(3492695719)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3492695719)(0)");
    }

    @Test
    public void testCALCS429() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("           order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS430() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` + `Calcs`.`num1`) AS `TEMP(Test)(977554451)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(977554451)(0)");
    }

    @Test
    public void testCALCS431() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(`Calcs`.`num0`,`Calcs`.`num1`) AS `TEMP(Test)(637953353)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS432() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`num0` * `Calcs`.`num1`) AS `TEMP(Test)(1861245368)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1861245368)(0)");
    }

    @Test
    public void testCALCS433() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` = (CASE WHEN (`Calcs`.`num3` > 0) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(1635792874)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1635792874)(0)");
    }

    @Test
    public void testCALCS434() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` >= (CASE WHEN (`Calcs`.`num3` > 0) THEN LOWER(`Calcs`.`str0`) WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(1555382477)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1555382477)(0)");
    }

    @Test
    public void testCALCS435() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` > (CASE WHEN (`Calcs`.`num3` > 0) THEN `Calcs`.`str0` WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(3821822049)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3821822049)(0)");
    }

    @Test
    public void testCALCS436() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` <= (CASE WHEN (`Calcs`.`num3` > 0) THEN LOWER(`Calcs`.`str0`) WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(2776534421)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2776534421)(0)");
    }

    @Test
    public void testCALCS437() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` < (CASE WHEN (`Calcs`.`num3` > 0) THEN LOWER(`Calcs`.`str0`) WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(398649381)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(398649381)(0)");
    }

    @Test
    public void testCALCS438() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`str2` <> (CASE WHEN (`Calcs`.`num3` > 0) THEN `Calcs`.`str2` WHEN NOT (`Calcs`.`num3` > 0) THEN `Calcs`.`str3` ELSE NULL END)) AS `TEMP(Test)(119026413)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(119026413)(0)");
    }

    @Test
    public void testCALCS439() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(`Calcs`.`str2`, `Calcs`.`str3`) AS `TEMP(Test)(724155660)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(724155660)(0)");
    }

    @Test
    public void testCALCS440() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 'Pat O''Hanrahan & <Matthew Eldridge]''' AS `TEMP(Test)(627207302)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(627207302)(0)");
    }

    @Test
    public void testCALCS441() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 1 AS `TEMP(Test)(1507734681)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1507734681)(0)");
    }

    @Test
    public void testCALCS442() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ' tail trimmed     ' AS `TEMP(Test)(1321171487)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1321171487)(0)");
    }

    @Test
    public void testCALCS443() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*12 + (MONTH(`Calcs`.`date2`) - MONTH(`Calcs`.`date3`))) AS `TEMP(Test)(2958462977)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2958462977)(0)");
    }

    @Test
    public void testCALCS444() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*12 + (MONTH(`Calcs`.`date2`) - MONTH(`Calcs`.`date3`))) AS `TEMP(Test)(667124691)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(667124691)(0)");
    }

    @Test
    public void testCALCS445() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*12 + (MONTH(`Calcs`.`datetime0`) - MONTH(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(2463700949)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2463700949)(0)");
    }

    @Test
    public void testCALCS446() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*12 + (MONTH(`Calcs`.`datetime0`) - MONTH(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(3778274693)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3778274693)(0)");
    }

    @Test
    public void testCALCS447() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ATAN(`Calcs`.`int2`) AS `TEMP(Test)(3655856496)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3655856496)(0)");
    }

    @Test
    public void testCALCS448() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ATAN(`Calcs`.`num0`) AS `TEMP(Test)(4053915117)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4053915117)(0)");
    }

    @Test
    public void testCALCS449() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ATAN2(`Calcs`.`int2`,1) AS `TEMP(Test)(2745915023)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2745915023)(0)");
    }

    @Test
    public void testCALCS450() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ATAN2(`Calcs`.`num0`,`Calcs`.`num1`) AS `TEMP(Test)(3341395046)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3341395046)(0)");
    }

    @Test
    public void testCALCS451() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(TRUNCATE(`Calcs`.`num4`,0)) AS `TEMP(Test)(1425036653)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1425036653)(0)");
    }

    @Test
    public void testCALCS452() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TIMESTAMP(`Calcs`.`date2`) AS `TEMP(Test)(1486024523)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1486024523)(0)");
    }

    @Test
    public void testCALCS453() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - 693961.0 + TIME_TO_SEC(ADDDATE(`Calcs`.`date2`, INTERVAL 0 SECOND)) / (24.0 * 60.0 * 60.0) ) AS `TEMP(Test)(2671902822)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2671902822)(0)");
    }

    @Test
    public void testCALCS454() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRIM(DATE_FORMAT(`Calcs`.`date2`, '%b %e %Y %l:%i%p')) AS `TEMP(Test)(3929621149)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3929621149)(0)");
    }

    @Test
    public void testCALCS455() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND((TO_DAYS(`Calcs`.`datetime0`) - 693961.0 + TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) / (24.0 * 60.0 * 60.0) ),2) AS `TEMP(Test)(102700322)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(102700322)(0)");
    }

    @Test
    public void testCALCS456() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRIM(DATE_FORMAT(`Calcs`.`datetime0`, '%b %e %Y %l:%i%p')) AS `TEMP(Test)(1103404331)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1103404331)(0)");
    }

    @Test
    public void testCALCS457() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(296025979)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(296025979)(0)");
    }

    @Test
    public void testCALCS458() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(595744937)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(595744937)(0)");
    }

    @Test
    public void testCALCS459() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3907469988)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3907469988)(0)");
    }

    @Test
    public void testCALCS460() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1153873435)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1153873435)(0)");
    }

    @Test
    public void testCALCS461() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL 1 YEAR) AS `TEMP(Test)(858668231)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(858668231)(0)");
    }

    @Test
    public void testCALCS462() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 YEAR) AS `TEMP(Test)(1314023193)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1314023193)(0)");
    }

    @Test
    public void testCALCS463() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(3529528921)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3529528921)(0)");
    }

    @Test
    public void testCALCS464() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(1066073186)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1066073186)(0)");
    }

    @Test
    public void testCALCS465() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(1128710711)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1128710711)(0)");
    }

    @Test
    public void testCALCS466() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(3816818712)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3816818712)(0)");
    }

    @Test
    public void testCALCS467() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961)))*4 + (QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) - QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)))) AS `TEMP(Test)(1220694026)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1220694026)(0)");
    }

    @Test
    public void testCALCS468() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*4 + (QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))) AS `TEMP(Test)(1878304808)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1878304808)(0)");
    }

    @Test
    public void testCALCS469() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - YEAR(FROM_DAYS(FLOOR(NULL) + 693961)))*12 + (MONTH(FROM_DAYS(FLOOR(NULL) + 693961)) - MONTH(FROM_DAYS(FLOOR(NULL) + 693961)))) AS `TEMP(Test)(3201398499)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3201398499)(0)");
    }

    @Test
    public void testCALCS470() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*12 + (MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))) AS `TEMP(Test)(2380792894)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2380792894)(0)");
    }

    @Test
    public void testCALCS471() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1)) - (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1) ) )/7) AS `TEMP(Test)(1799303116)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1799303116)(0)");
    }

    @Test
    public void testCALCS472() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1)) - (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1) ) )/7) AS `TEMP(Test)(3424623419)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3424623419)(0)");
    }

    @Test
    public void testCALCS473() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(496128354)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(496128354)(0)");
    }

    @Test
    public void testCALCS474() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(260207547)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(260207547)(0)");
    }

    @Test
    public void testCALCS475() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(4282303505)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4282303505)(0)");
    }

    @Test
    public void testCALCS476() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(2339877044)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2339877044)(0)");
    }

    @Test
    public void testCALCS477() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961))) AS `TEMP(Test)(3465754358)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3465754358)(0)");
    }

    @Test
    public void testCALCS478() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))) AS `TEMP(Test)(2205674587)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2205674587)(0)");
    }

    @Test
    public void testCALCS479() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24 + FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 3600) - FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 3600)) AS `TEMP(Test)(4062119106)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4062119106)(0)");
    }

    @Test
    public void testCALCS480() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24*60 + FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 60) - FLOOR(TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) / 60)) AS `TEMP(Test)(2509274079)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2509274079)(0)");
    }

    @Test
    public void testCALCS481() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)))*24*60*60 + (TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 0 SECOND)))) AS `TEMP(Test)(508245917)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(508245917)(0)");
    }

    @Test
    public void testCALCS482() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 1 YEAR) AS `TEMP(Test)(1053114602)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1053114602)(0)");
    }

    @Test
    public void testCALCS483() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 YEAR) AS `TEMP(Test)(955333125)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(955333125)(0)");
    }

    @Test
    public void testCALCS484() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL (3 * 1) MONTH) AS `TEMP(Test)(2396988690)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2396988690)(0)");
    }

    @Test
    public void testCALCS485() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL (3 * 1) MONTH) AS `TEMP(Test)(2232502461)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2232502461)(0)");
    }

    @Test
    public void testCALCS486() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 1 MONTH) AS `TEMP(Test)(109946472)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(109946472)(0)");
    }

    @Test
    public void testCALCS487() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 MONTH) AS `TEMP(Test)(2095510626)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2095510626)(0)");
    }

    @Test
    public void testCALCS488() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL (7 * 1) DAY) AS `TEMP(Test)(359186020)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(359186020)(0)");
    }

    @Test
    public void testCALCS489() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL (7 * 1) DAY) AS `TEMP(Test)(3060670302)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3060670302)(0)");
    }

    @Test
    public void testCALCS490() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 1 DAY) AS `TEMP(Test)(592740370)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(592740370)(0)");
    }

    @Test
    public void testCALCS491() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 DAY) AS `TEMP(Test)(4169571243)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4169571243)(0)");
    }

    @Test
    public void testCALCS492() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 1 DAY) AS `TEMP(Test)(2477057371)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2477057371)(0)");
    }

    @Test
    public void testCALCS493() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 DAY) AS `TEMP(Test)(3817976182)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3817976182)(0)");
    }

    @Test
    public void testCALCS494() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 1 DAY) AS `TEMP(Test)(2329360898)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2329360898)(0)");
    }

    @Test
    public void testCALCS495() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 DAY) AS `TEMP(Test)(1469842605)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1469842605)(0)");
    }

    @Test
    public void testCALCS496() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 HOUR) AS `TEMP(Test)(4189387493)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4189387493)(0)");
    }

    @Test
    public void testCALCS497() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 MINUTE) AS `TEMP(Test)(3720439076)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3720439076)(0)");
    }

    @Test
    public void testCALCS498() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), INTERVAL 1 SECOND) AS `TEMP(Test)(2985757783)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2985757783)(0)");
    }

    @Test
    public void testCALCS499() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT RTRIM(CONCAT(CONCAT(' ', `Calcs`.`str2`), ' ')) AS `TEMP(Test)(2277366246)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2277366246)(0)");
    }

    @Test
    public void testCALCS500() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(NOW()) - TO_DAYS(NOW())) AS `TEMP(Test)(3926981592)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3926981592)(0)");
    }

    @Test
    public void testCALCS501() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(CURDATE()) - TO_DAYS(CURDATE())) AS `TEMP(Test)(1915846221)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1915846221)(0)");
    }

    @Test
    public void testCALCS502() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date2`)) + (TIME_TO_SEC(ADDDATE(`Calcs`.`date2`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Calcs`.`date2`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) AS `TEMP(Test)(1152843842)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1152843842)(0)");
    }

    @Test
    public void testCALCS503() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(`Calcs`.`date2`, INTERVAL FLOOR(1) DAY), INTERVAL 60 * 60 * 24 * (1 - FLOOR(1)) SECOND) AS `TEMP(Test)(715809068)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(715809068)(0)");
    }

    @Test
    public void testCALCS504() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(`Calcs`.`date2`, INTERVAL FLOOR(1.5) DAY), INTERVAL 60 * 60 * 24 * (1.5 - FLOOR(1.5)) SECOND) AS `TEMP(Test)(299505631)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(299505631)(0)");
    }

    @Test
    public void testCALCS505() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_SUB(DATE_SUB(`Calcs`.`date2`, INTERVAL FLOOR(1) DAY), INTERVAL 60 * 60 * 24 * (1 - FLOOR(1)) SECOND) AS `TEMP(Test)(709470143)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(709470143)(0)");
    }

    @Test
    public void testCALCS506() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_SUB(DATE_SUB(`Calcs`.`date2`, INTERVAL FLOOR(1.5) DAY), INTERVAL 60 * 60 * 24 * (1.5 - FLOOR(1.5)) SECOND) AS `TEMP(Test)(1620718980)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1620718980)(0)");
    }

    @Test
    public void testCALCS507() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(`Calcs`.`datetime0`)) + (TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Calcs`.`datetime0`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) AS `TEMP(Test)(2141740056)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2141740056)(0)");
    }

    @Test
    public void testCALCS508() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_SUB(DATE_SUB(`Calcs`.`datetime0`, INTERVAL FLOOR(1) DAY), INTERVAL 60 * 60 * 24 * (1 - FLOOR(1)) SECOND) AS `TEMP(Test)(1797652325)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1797652325)(0)");
    }

    @Test
    public void testCALCS509() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(`Calcs`.`datetime0`, INTERVAL FLOOR(1) DAY), INTERVAL 60 * 60 * 24 * (1 - FLOOR(1)) SECOND) AS `TEMP(Test)(2686481578)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2686481578)(0)");
    }

    @Test
    public void testCALCS510() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_SUB(DATE_SUB(`Calcs`.`datetime0`, INTERVAL FLOOR(1.5) DAY), INTERVAL 60 * 60 * 24 * (1.5 - FLOOR(1.5)) SECOND) AS `TEMP(Test)(2341796372)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2341796372)(0)");
    }

    @Test
    public void testCALCS511() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(DATE_ADD(`Calcs`.`datetime0`, INTERVAL FLOOR(1.5) DAY), INTERVAL 60 * 60 * 24 * (1.5 - FLOOR(1.5)) SECOND) AS `TEMP(Test)(4017290474)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4017290474)(0)");
    }

    @Test
    public void testCALCS512() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` = `Calcs`.`datetime0`) AS `TEMP(Test)(3033382267)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3033382267)(0)");
    }

    @Test
    public void testCALCS513() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` > `Calcs`.`datetime0`) AS `TEMP(Test)(4196472080)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4196472080)(0)");
    }

    @Test
    public void testCALCS514() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` >= `Calcs`.`datetime0`) AS `TEMP(Test)(1829388090)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1829388090)(0)");
    }

    @Test
    public void testCALCS515() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` < `Calcs`.`datetime0`) AS `TEMP(Test)(2087345109)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2087345109)(0)");
    }

    @Test
    public void testCALCS516() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` <= `Calcs`.`datetime0`) AS `TEMP(Test)(3187080314)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3187080314)(0)");
    }

    @Test
    public void testCALCS517() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`datetime0` <> `Calcs`.`datetime0`) AS `TEMP(Test)(436529008)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(436529008)(0)");
    }

    @Test
    public void testCALCS518() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` = `Calcs`.`datetime0`) AS `TEMP(Test)(1122166960)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1122166960)(0)");
    }

    @Test
    public void testCALCS519() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` > `Calcs`.`datetime0`) AS `TEMP(Test)(2476649334)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2476649334)(0)");
    }

    @Test
    public void testCALCS520() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` >= `Calcs`.`datetime0`) AS `TEMP(Test)(1267352367)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1267352367)(0)");
    }

    @Test
    public void testCALCS521() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` < `Calcs`.`datetime0`) AS `TEMP(Test)(668774393)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(668774393)(0)");
    }

    @Test
    public void testCALCS522() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` <= `Calcs`.`datetime0`) AS `TEMP(Test)(2801366337)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2801366337)(0)");
    }

    @Test
    public void testCALCS523() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` <> `Calcs`.`datetime0`) AS `TEMP(Test)(6065346)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(6065346)(0)");
    }

    @Test
    public void testCALCS524() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` = `Calcs`.`date2`) AS `TEMP(Test)(4213376628)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4213376628)(0)");
    }

    @Test
    public void testCALCS525() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` > `Calcs`.`date2`) AS `TEMP(Test)(284925583)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(284925583)(0)");
    }

    @Test
    public void testCALCS526() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` >= `Calcs`.`date2`) AS `TEMP(Test)(1365124261)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1365124261)(0)");
    }

    @Test
    public void testCALCS527() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` < `Calcs`.`date2`) AS `TEMP(Test)(4277161941)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4277161941)(0)");
    }

    @Test
    public void testCALCS528() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` <= `Calcs`.`date2`) AS `TEMP(Test)(932571096)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(932571096)(0)");
    }

    @Test
    public void testCALCS529() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`date2` <> `Calcs`.`date2`) AS `TEMP(Test)(3666462064)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3666462064)(0)");
    }

    @Test
    public void testCALCS530() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(402015915)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(402015915)(0)");
    }

    @Test
    public void testCALCS531() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3033426574)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3033426574)(0)");
    }

    @Test
    public void testCALCS532() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`int0`) AS `TEMP(Test)(4016644369)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4016644369)(0)");
    }

    @Test
    public void testCALCS533() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`int1`) THEN NULL	WHEN ISNULL(`Calcs`.`int2`) THEN NULL	ELSE LEAST(`Calcs`.`int1`, `Calcs`.`int2`) END) AS `TEMP(Test)(1701645592)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1701645592)(0)");
    }

    @Test
    public void testCALCS534() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(433583207)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(433583207)(0)");
    }

    @Test
    public void testCALCS535() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1289371916)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1289371916)(0)");
    }

    @Test
    public void testCALCS536() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3917841362)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3917841362)(0)");
    }

    @Test
    public void testCALCS537() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1921815362)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1921815362)(0)");
    }

    @Test
    public void testCALCS538() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LTRIM(CONCAT(CONCAT(' ', `Calcs`.`str2`), ' ')) AS `TEMP(Test)(1106979036)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1106979036)(0)");
    }

    @Test
    public void testCALCS539() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COS(`Calcs`.`int2`) AS `TEMP(Test)(344207442)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(344207442)(0)");
    }

    @Test
    public void testCALCS540() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COS(`Calcs`.`num0`) AS `TEMP(Test)(1355320598)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1355320598)(0)");
    }

    @Test
    public void testCALCS541() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(513464674)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(513464674)(0)");
    }

    @Test
    public void testCALCS542() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3512378422)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3512378422)(0)");
    }

    @Test
    public void testCALCS543() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3084524178)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3084524178)(0)");
    }

    @Test
    public void testCALCS544() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(4202902840)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4202902840)(0)");
    }

    @Test
    public void testCALCS545() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2836269094)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2836269094)(0)");
    }

    @Test
    public void testCALCS546() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3924648662)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3924648662)(0)");
    }

    @Test
    public void testCALCS547() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - 1 + DAYOFWEEK(DATE_FORMAT(FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(1538264184)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1538264184)(0)");
    }

    @Test
    public void testCALCS548() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1 + DAYOFWEEK(DATE_FORMAT(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00')) - 1) / 7) AS `TEMP(Test)(4042104093)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4042104093)(0)");
    }

    @Test
    public void testCALCS549() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(4271712345)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4271712345)(0)");
    }

    @Test
    public void testCALCS550() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(963247111)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(963247111)(0)");
    }

    @Test
    public void testCALCS551() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(738426766)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(738426766)(0)");
    }

    @Test
    public void testCALCS552() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1202522493)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1202522493)(0)");
    }

    @Test
    public void testCALCS553() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1255819744)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1255819744)(0)");
    }

    @Test
    public void testCALCS554() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(1639804515)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1639804515)(0)");
    }

    @Test
    public void testCALCS555() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(299943486)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(299943486)(0)");
    }

    @Test
    public void testCALCS556() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(4177149407)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4177149407)(0)");
    }

    @Test
    public void testCALCS557() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1457324017)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1457324017)(0)");
    }

    @Test
    public void testCALCS558() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ASIN((`Calcs`.`num0` / 20)) AS `TEMP(Test)(1317198372)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1317198372)(0)");
    }

    @Test
    public void testCALCS559() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(DISTINCT `Calcs`.`int0`) AS `TEMP(Test)(1467453495)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1467453495)(0)");
    }

    @Test
    public void testCALCS560() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(DISTINCT (`Calcs`.`bool0_` <> 0)) AS `TEMP(Test)(1408008556)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1408008556)(0)");
    }

    @Test
    public void testCALCS561() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(DISTINCT `Calcs`.`date3`) AS `TEMP(Test)(175600811)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(175600811)(0)");
    }

    @Test
    public void testCALCS562() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(DISTINCT `Calcs`.`num4`) AS `TEMP(Test)(41874160)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(41874160)(0)");
    }

    @Test
    public void testCALCS563() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT COUNT(DISTINCT `Calcs`.`str2`) AS `TEMP(Test)(2954817995)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2954817995)(0)");
    }

    @Test
    public void testCALCS564() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3715775174)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3715775174)(0)");
    }

    @Test
    public void testCALCS565() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2815480624)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2815480624)(0)");
    }

    @Test
    public void testCALCS566() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3738830082)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3738830082)(0)");
    }

    @Test
    public void testCALCS567() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(151653785)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(151653785)(0)");
    }

    @Test
    public void testCALCS568() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1373895161)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1373895161)(0)");
    }

    @Test
    public void testCALCS569() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(543203842)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(543203842)(0)");
    }

    @Test
    public void testCALCS570() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(3325657342)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3325657342)(0)");
    }

    @Test
    public void testCALCS571() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(`Calcs`.`int1`) AS `TEMP(Test)(2617331766)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2617331766)(0)");
    }

    @Test
    public void testCALCS572() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN (`Calcs`.`bool0_` <> 0) THEN '1'	WHEN NOT (`Calcs`.`bool0_` <> 0) THEN '0'	ELSE NULL END) AS `TEMP(Test)(3200082645)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3200082645)(0)");
    }

    @Test
    public void testCALCS573() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(`Calcs`.`num2`) AS `TEMP(Test)(3049448927)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3049448927)(0)");
    }

    @Test
    public void testCALCS574() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SUBSTRING(`Calcs`.`str2`, 1, 1024) AS `TEMP(Test)(3494867617)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3494867617)(0)");
    }

    @Test
    public void testCALCS575() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`date2`) AS `TEMP(Test)(1660803953)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1660803953)(0)");
    }

    @Test
    public void testCALCS576() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`date2`) AS `TEMP(Test)(872696424)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(872696424)(0)");
    }

    @Test
    public void testCALCS577() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(732183378)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(732183378)(0)");
    }

    @Test
    public void testCALCS578() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(`Calcs`.`datetime0`) AS `TEMP(Test)(3816689092)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3816689092)(0)");
    }

    @Test
    public void testCALCS579() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`date2`) AS `TEMP(Test)(2634030884)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2634030884)(0)");
    }

    @Test
    public void testCALCS580() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(4000895377)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4000895377)(0)");
    }

    @Test
    public void testCALCS581() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL 1 MONTH) AS `TEMP(Test)(2799254343)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2799254343)(0)");
    }

    @Test
    public void testCALCS582() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 MONTH) AS `TEMP(Test)(1378354598)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1378354598)(0)");
    }

    @Test
    public void testCALCS583() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3057229987)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3057229987)(0)");
    }

    @Test
    public void testCALCS584() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(4063654893)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4063654893)(0)");
    }

    @Test
    public void testCALCS585() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(2102858309)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2102858309)(0)");
    }

    @Test
    public void testCALCS586() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3270121971)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3270121971)(0)");
    }

    @Test
    public void testCALCS587() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(2692233594)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2692233594)(0)");
    }

    @Test
    public void testCALCS588() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTHNAME(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(1772891037)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1772891037)(0)");
    }

    @Test
    public void testCALCS589() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) - 1 + DAYOFWEEK(DATE_FORMAT(FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(3926284460)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3926284460)(0)");
    }

    @Test
    public void testCALCS590() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT CONCAT(FLOOR((7 + DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1 + DAYOFWEEK(DATE_FORMAT(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00')) - 1) / 7)) AS `TEMP(Test)(1415178918)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1415178918)(0)");
    }

    @Test
    public void testCALCS591() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3608467423)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3608467423)(0)");
    }

    @Test
    public void testCALCS592() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYNAME(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2920782836)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2920782836)(0)");
    }

    @Test
    public void testCALCS593() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3132873078)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3132873078)(0)");
    }

    @Test
    public void testCALCS594() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFMONTH(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2450943592)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2450943592)(0)");
    }

    @Test
    public void testCALCS595() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(FROM_DAYS(FLOOR(NULL) + 693961)) AS `TEMP(Test)(3530921297)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3530921297)(0)");
    }

    @Test
    public void testCALCS596() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(304383277)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(304383277)(0)");
    }

    @Test
    public void testCALCS597() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3871589708)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3871589708)(0)");
    }

    @Test
    public void testCALCS598() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(2462406212)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2462406212)(0)");
    }

    @Test
    public void testCALCS599() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SECOND(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) AS `TEMP(Test)(3443263072)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3443263072)(0)");
    }

    @Test
    public void testCALCS600() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1349416314)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1349416314)(0)");
    }

    @Test
    public void testCALCS601() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d %H:%i:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3032747293)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3032747293)(0)");
    }

    @Test
    public void testCALCS602() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL (7 * 1) DAY) AS `TEMP(Test)(2748179160)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2748179160)(0)");
    }

    @Test
    public void testCALCS603() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL (7 * 1) DAY) AS `TEMP(Test)(3880453047)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3880453047)(0)");
    }

    @Test
    public void testCALCS604() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`date2`) AS `TEMP(Test)(877816921)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(877816921)(0)");
    }

    @Test
    public void testCALCS605() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DAYOFYEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(707037378)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(707037378)(0)");
    }

    @Test
    public void testCALCS606() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*4 + (QUARTER(`Calcs`.`date2`) - QUARTER(`Calcs`.`date3`))) AS `TEMP(Test)(3028875325)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3028875325)(0)");
    }

    @Test
    public void testCALCS607() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`date2`) - YEAR(`Calcs`.`date3`))*4 + (QUARTER(`Calcs`.`date2`) - QUARTER(`Calcs`.`date3`))) AS `TEMP(Test)(3483942593)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3483942593)(0)");
    }

    @Test
    public void testCALCS608() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*4 + (QUARTER(`Calcs`.`datetime0`) - QUARTER(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(4196684004)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4196684004)(0)");
    }

    @Test
    public void testCALCS609() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ((YEAR(`Calcs`.`datetime0`) - YEAR(TIMESTAMP(`Calcs`.`date2`)))*4 + (QUARTER(`Calcs`.`datetime0`) - QUARTER(TIMESTAMP(`Calcs`.`date2`)))) AS `TEMP(Test)(351668681)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(351668681)(0)");
    }

    @Test
    public void testCALCS610() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`date2`, INTERVAL 1 DAY) AS `TEMP(Test)(1139290352)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1139290352)(0)");
    }

    @Test
    public void testCALCS611() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 DAY) AS `TEMP(Test)(748109579)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(748109579)(0)");
    }

    @Test
    public void testCALCS612() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`date2`) - (DAYOFWEEK(`Calcs`.`date2`) - 1)) - (TO_DAYS(`Calcs`.`date3`) - (DAYOFWEEK(`Calcs`.`date3`) - 1) ) )/7) AS `TEMP(Test)(859582235)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(859582235)(0)");
    }

    @Test
    public void testCALCS613() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`datetime0`) - (DAYOFWEEK(`Calcs`.`datetime0`) - 1)) - (TO_DAYS(TIMESTAMP(`Calcs`.`date2`)) - (DAYOFWEEK(TIMESTAMP(`Calcs`.`date2`)) - 1) ) )/7) AS `TEMP(Test)(2079052241)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2079052241)(0)");
    }

    @Test
    public void testCALCS614() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ABS(`Calcs`.`int2`) AS `TEMP(Test)(2102582873)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2102582873)(0)");
    }

    @Test
    public void testCALCS615() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ABS(`Calcs`.`num0`) AS `TEMP(Test)(3816473022)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3816473022)(0)");
    }

    @Test
    public void testCALCS616() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1942031084)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1942031084)(0)");
    }

    @Test
    public void testCALCS617() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`date2`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(308042462)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(308042462)(0)");
    }

    @Test
    public void testCALCS618() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1290354772)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1290354772)(0)");
    }

    @Test
    public void testCALCS619() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( `Calcs`.`datetime0`, '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2022110629)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2022110629)(0)");
    }

    @Test
    public void testCALCS620() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IF(ISNULL(((`Calcs`.`num0` + 5) * 0.29999999999999999)), NULL, SUBSTRING(`Calcs`.`str2`,GREATEST(1,FLOOR(((`Calcs`.`num0` + 5) * 0.29999999999999999))),FLOOR(`Calcs`.`num1`))) AS `TEMP(Test)(1934432200)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1934432200)(0)");
    }

    @Test
    public void testCALCS621() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MINUTE(`Calcs`.`datetime0`) AS `TEMP(Test)(1256004566)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1256004566)(0)");
    }

    @Test
    public void testCALCS622() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (((CASE WHEN (ABS((2) - (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0)))) + SQRT(3.0) * ((ABS((`Calcs`.`int2`) - (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 THEN 1.5 ELSE 0.0 END) - (CASE WHEN ((`Calcs`.`int2`) - (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0) < 0.0) AND ((CASE WHEN (ABS((2) - (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0)))) + SQRT(3.0) * ((ABS((`Calcs`.`int2`) - (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 THEN SQRT(3.0) / 2.0 ELSE 0.0 END) > 0.0) THEN 3.0 ELSE 0.0 END)) + (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0)) AS `TEMP(Test)(2503102272)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2503102272)(0)");
    }

    @Test
    public void testCALCS623() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND( (((CASE WHEN (ABS((2) - (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0)))) + SQRT(3.0) * ((ABS((`Calcs`.`int2`) - (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 THEN SQRT(3.0) / 2.0 ELSE 0.0 END) - (CASE WHEN ((2) - (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0)) < 0.0) AND ((CASE WHEN (ABS((2) - (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0)))) + SQRT(3.0) * ((ABS((`Calcs`.`int2`) - (ROUND( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 THEN SQRT(3.0) / 2.0 ELSE 0.0 END) > 0.0) THEN SQRT(3.0) ELSE 0.0 END)) + (ROUND( ( (2) / SQRT(3.0) ), 0 ) * SQRT(3.0))), 3) AS `TEMP(Test)(2977666156)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2977666156)(0)");
    }

    @Test
    public void testCALCS624() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TIMESTAMP(STR_TO_DATE('1234-06-01', '%Y-%m-%d')) AS `TEMP(Test)(1408155083)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1408155083)(0)");
    }

    @Test
    public void testCALCS625() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TIMESTAMP(STR_TO_DATE('12-06-01', '%y-%m-%d')) AS `TEMP(Test)(54082523)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(54082523)(0)");
    }

    @Test
    public void testCALCS626() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TIMESTAMP(STR_TO_DATE('1234-06-01', '%Y-%m-%d')) AS `TEMP(Test)(2040050501)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2040050501)(0)");
    }

    @Test
    public void testCALCS627() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOG10(`Calcs`.`int2`) AS `TEMP(Test)(114283928)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(114283928)(0)");
    }

    @Test
    public void testCALCS628() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LOG(`Calcs`.`int2`)/LOG(2)) AS `TEMP(Test)(3322085183)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3322085183)(0)");
    }

    @Test
    public void testCALCS629() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOG10(`Calcs`.`num0`) AS `TEMP(Test)(1814892178)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1814892178)(0)");
    }

    @Test
    public void testCALCS630() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LOG(`Calcs`.`num0`)/LOG(2)) AS `TEMP(Test)(3081102343)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3081102343)(0)");
    }

    @Test
    public void testCALCS631() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT 1 AS `TEMP(Test)(3252316215)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3252316215)(0)");
    }

    @Test
    public void testCALCS632() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LEFT(`Calcs`.`str1`, LENGTH('BI')) = 'BI') AS `TEMP(Test)(535799381)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(535799381)(0)");
    }

    @Test
    public void testCALCS633() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (LEFT(`Calcs`.`str1`, LENGTH(`Calcs`.`str2`)) = `Calcs`.`str2`) AS `TEMP(Test)(2377293421)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2377293421)(0)");
    }

    @Test
    public void testCALCS634() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`date2`) AS `TEMP(Test)(3325074545)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3325074545)(0)");
    }

    @Test
    public void testCALCS635() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`date2`) THEN NULL	WHEN ISNULL(`Calcs`.`date3`) THEN NULL	ELSE GREATEST(`Calcs`.`date2`, `Calcs`.`date3`) END) AS `TEMP(Test)(1996265231)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1996265231)(0)");
    }

    @Test
    public void testCALCS636() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`datetime0`) AS `TEMP(Test)(4035984656)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4035984656)(0)");
    }

    @Test
    public void testCALCS637() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(840463993)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(840463993)(0)");
    }

    @Test
    public void testCALCS638() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(1720545932)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1720545932)(0)");
    }

    @Test
    public void testCALCS639() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(840463993)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(840463993)(1)");
    }

    @Test
    public void testCALCS640() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(1720545932)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1720545932)(1)");
    }

    @Test
    public void testCALCS641() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(2707942807)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2707942807)(0)");
    }

    @Test
    public void testCALCS642() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(3474280307)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3474280307)(0)");
    }

    @Test
    public void testCALCS643() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(2707942807)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2707942807)(1)");
    }

    @Test
    public void testCALCS644() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(3474280307)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3474280307)(1)");
    }

    @Test
    public void testCALCS645() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`date2`) AS `TEMP(Test)(1671202742)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1671202742)(0)");
    }

    @Test
    public void testCALCS646() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`date2`) AS `TEMP(Test)(536615588)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(536615588)(0)");
    }

    @Test
    public void testCALCS647() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(1933085624)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1933085624)(0)");
    }

    @Test
    public void testCALCS648() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Calcs`.`datetime0`) AS `TEMP(Test)(2986113344)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2986113344)(0)");
    }

    @Test
    public void testCALCS649() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int2` DIV 2) AS `TEMP(Test)(266359676)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(266359676)(0)");
    }

    @Test
    public void testCALCS650() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int0` DIV `Calcs`.`int1`) AS `TEMP(Test)(2600727600)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2600727600)(0)");
    }

    @Test
    public void testCALCS651() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int0` DIV `Calcs`.`int1`) AS `TEMP(Test)(2600727600)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2600727600)(1)");
    }

    @Test
    public void testCALCS652() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int3` DIV `Calcs`.`int2`) AS `TEMP(Test)(3955107424)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3955107424)(0)");
    }

    @Test
    public void testCALCS653() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (`Calcs`.`int3` DIV `Calcs`.`int2`) AS `TEMP(Test)(3955107424)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3955107424)(1)");
    }

    @Test
    public void testCALCS654() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SQRT(`Calcs`.`int2`) AS `TEMP(Test)(2398974448)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2398974448)(0)");
    }

    @Test
    public void testCALCS655() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT SQRT(`Calcs`.`num0`) AS `TEMP(Test)(634651992)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(634651992)(0)");
    }

    @Test
    public void testCALCS656() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(`Calcs`.`int2`, 2) AS `TEMP(Test)(3898674109)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3898674109)(0)");
    }

    @Test
    public void testCALCS657() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT POWER(`Calcs`.`num0`, 2) AS `TEMP(Test)(1119897860)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1119897860)(0)");
    }

    @Test
    public void testCALCS658() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3311335472)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3311335472)(0)");
    }

    @Test
    public void testCALCS659() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1982106892)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1982106892)(0)");
    }

    @Test
    public void testCALCS660() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-' ), (3*(QUARTER(FROM_DAYS(FLOOR(NULL) + 693961))-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2616948526)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2616948526)(0)");
    }

    @Test
    public void testCALCS661() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( CONCAT( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-' ), (3*(QUARTER(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND))-1)+1), '-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(4099405891)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4099405891)(0)");
    }

    @Test
    public void testCALCS662() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1303420554)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1303420554)(0)");
    }

    @Test
    public void testCALCS663() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-01 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1705284026)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1705284026)(0)");
    }

    @Test
    public void testCALCS664() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - (DAYOFWEEK(DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND)) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2964540366)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2964540366)(0)");
    }

    @Test
    public void testCALCS665() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE(FROM_DAYS( TO_DAYS(FROM_DAYS(FLOOR(NULL) + 693961)) - (DAYOFWEEK(FROM_DAYS(FLOOR(NULL) + 693961)) - 1) ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3523871008)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3523871008)(0)");
    }

    @Test
    public void testCALCS666() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3587526928)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3587526928)(0)");
    }

    @Test
    public void testCALCS667() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(2715649251)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2715649251)(0)");
    }

    @Test
    public void testCALCS668() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3912893816)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3912893816)(0)");
    }

    @Test
    public void testCALCS669() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(453060606)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(453060606)(0)");
    }

    @Test
    public void testCALCS670() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( FROM_DAYS(FLOOR(NULL) + 693961), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(1466575961)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1466575961)(0)");
    }

    @Test
    public void testCALCS671() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d 00:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(265878863)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(265878863)(0)");
    }

    @Test
    public void testCALCS672() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:00:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(3877847632)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3877847632)(0)");
    }

    @Test
    public void testCALCS673() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:%i:00' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(263614731)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(263614731)(0)");
    }

    @Test
    public void testCALCS674() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ADDDATE( DATE_FORMAT( DATE_ADD(FROM_DAYS(FLOOR(NULL) + 693961), INTERVAL 60 * 60 * 24 * (NULL - FLOOR(NULL)) SECOND), '%Y-%m-%d %H:%i:%s' ), INTERVAL 0 SECOND ) AS `TEMP(Test)(864002214)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(864002214)(0)");
    }

    @Test
    public void testCALCS675() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT AVG(`Calcs`.`int0`) AS `TEMP(Test)(3952218057)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3952218057)(0)");
    }

    @Test
    public void testCALCS676() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT AVG(`Calcs`.`num4`) AS `TEMP(Test)(1371989636)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1371989636)(0)");
    }

    @Test
    public void testCALCS677() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT HOUR(`Calcs`.`datetime0`) AS `TEMP(Test)(3233853797)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3233853797)(0)");
    }

    @Test
    public void testCALCS678() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(1876737518)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1876737518)(0)");
    }

    @Test
    public void testCALCS679() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(1437280163)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1437280163)(0)");
    }

    @Test
    public void testCALCS680() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(3178513645)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3178513645)(0)");
    }

    @Test
    public void testCALCS681() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(3727444777)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3727444777)(0)");
    }

    @Test
    public void testCALCS682() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT DATE_ADD(`Calcs`.`datetime0`, INTERVAL 1 HOUR) AS `TEMP(Test)(4261466899)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4261466899)(0)");
    }

    @Test
    public void testCALCS683() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`int0`) AS `TEMP(Test)(56370746)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(56370746)(0)");
    }

    @Test
    public void testCALCS684() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`date3`) AS `TEMP(Test)(277748206)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(277748206)(0)");
    }

    @Test
    public void testCALCS685() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`num4`) AS `TEMP(Test)(4154938655)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4154938655)(0)");
    }

    @Test
    public void testCALCS686() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`str2`) AS `TEMP(Test)(1812249092)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1812249092)(0)");
    }

    @Test
    public void testCALCS687() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`int0`) THEN NULL	WHEN ISNULL(`Calcs`.`int1`) THEN NULL	ELSE GREATEST(`Calcs`.`int0`, `Calcs`.`int1`) END) AS `TEMP(Test)(1523549003)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1523549003)(0)");
    }

    @Test
    public void testCALCS688() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOCATE('ee',`Calcs`.`str2`) AS `TEMP(Test)(3981629397)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3981629397)(0)");
    }

    @Test
    public void testCALCS689() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOCATE('E',`Calcs`.`str1`) AS `TEMP(Test)(257220821)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(257220821)(0)");
    }

    @Test
    public void testCALCS690() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IF(ISNULL(6), NULL, LOCATE('E',`Calcs`.`str1`,GREATEST(1,FLOOR(6)))) AS `TEMP(Test)(282093116)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(282093116)(0)");
    }

    @Test
    public void testCALCS691() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT LOCATE(`Calcs`.`str3`,`Calcs`.`str2`) AS `TEMP(Test)(3096760581)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3096760581)(0)");
    }

    @Test
    public void testCALCS692() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT IF(ISNULL((`Calcs`.`num4` * 0.20000000000000001)), NULL, LOCATE(`Calcs`.`str3`,`Calcs`.`str2`,GREATEST(1,FLOOR((`Calcs`.`num4` * 0.20000000000000001))))) AS `TEMP(Test)(2787932066)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2787932066)(0)");
    }

    @Test
    public void testCALCS693() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(838791689)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(838791689)(0)");
    }

    @Test
    public void testCALCS694() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`date2`) - TO_DAYS(`Calcs`.`date3`)) AS `TEMP(Test)(1647283678)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1647283678)(0)");
    }

    @Test
    public void testCALCS695() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(1719292105)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1719292105)(0)");
    }

    @Test
    public void testCALCS696() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(TIMESTAMP(`Calcs`.`date2`))) AS `TEMP(Test)(1567002572)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1567002572)(0)");
    }

    @Test
    public void testCALCS697() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`int0`) AS `TEMP(Test)(4016644369)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4016644369)(0)");
    }

    @Test
    public void testCALCS698() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`date3`) AS `TEMP(Test)(3378300904)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3378300904)(0)");
    }

    @Test
    public void testCALCS699() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`num4`) AS `TEMP(Test)(512350875)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(512350875)(0)");
    }

    @Test
    public void testCALCS700() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MIN(`Calcs`.`str2`) AS `TEMP(Test)(3910790823)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3910790823)(0)");
    }

    @Test
    public void testCALCS701() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`int0`) THEN NULL	WHEN ISNULL(`Calcs`.`int1`) THEN NULL	ELSE LEAST(`Calcs`.`int0`, `Calcs`.`int1`) END) AS `TEMP(Test)(3683900016)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3683900016)(0)");
    }

    @Test
    public void testCALCS702() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MAX(`Calcs`.`int0`) AS `TEMP(Test)(56370746)(0)`FROM `Calcs`HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(56370746)(0)");
    }

    @Test
    public void testCALCS703() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (CASE	WHEN ISNULL(`Calcs`.`int1`) THEN NULL	WHEN ISNULL(`Calcs`.`int2`) THEN NULL	ELSE GREATEST(`Calcs`.`int1`, `Calcs`.`int2`) END) AS `TEMP(Test)(2763474205)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2763474205)(0)");
    }

    @Test
    public void testCALCS704() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(3969685894)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3969685894)(0)");
    }

    @Test
    public void testCALCS705() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`date2`) AS `TEMP(Test)(3969685894)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3969685894)(1)");
    }

    @Test
    public void testCALCS706() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(4179095987)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4179095987)(0)");
    }

    @Test
    public void testCALCS707() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT YEAR(`Calcs`.`datetime0`) AS `TEMP(Test)(4179095987)(1)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(4179095987)(1)");
    }

    @Test
    public void testCALCS708() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND(`Calcs`.`int2`) AS `TEMP(Test)(366741644)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(366741644)(0)");
    }

    @Test
    public void testCALCS709() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND(`Calcs`.`int2`,2) AS `TEMP(Test)(1240237577)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1240237577)(0)");
    }

    @Test
    public void testCALCS710() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND(`Calcs`.`num0`) AS `TEMP(Test)(3892529067)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3892529067)(0)");
    }

    @Test
    public void testCALCS711() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT ROUND(`Calcs`.`num4`,1) AS `TEMP(Test)(2722044748)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2722044748)(0)");
    }

    @Test
    public void testCALCS712() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`date2`) - ((7 + DAYOFWEEK(`Calcs`.`date2`) - 2) % 7)) - (TO_DAYS(`Calcs`.`date3`) - ((7 + DAYOFWEEK(`Calcs`.`date3`) - 2) % 7) ) )/7) AS `TEMP(Test)(3550551924)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(3550551924)(0)");
    }

    @Test
    public void testCALCS713() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`date2`) - (DAYOFWEEK(`Calcs`.`date2`) - 1)) - (TO_DAYS(`Calcs`.`date3`) - (DAYOFWEEK(`Calcs`.`date3`) - 1) ) )/7) AS `TEMP(Test)(2745903531)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(2745903531)(0)");
    }

    @Test
    public void testCALCS714() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`datetime0`) - ((7 + DAYOFWEEK(`Calcs`.`datetime0`) - 2) % 7)) - (TO_DAYS(TIMESTAMP(`Calcs`.`date2`)) - ((7 + DAYOFWEEK(TIMESTAMP(`Calcs`.`date2`)) - 2) % 7) ) )/7) AS `TEMP(Test)(1341534691)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1341534691)(0)");
    }

    @Test
    public void testCALCS715() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((( TO_DAYS(`Calcs`.`datetime0`) - (DAYOFWEEK(`Calcs`.`datetime0`) - 1)) - (TO_DAYS(TIMESTAMP(`Calcs`.`date2`)) - (DAYOFWEEK(TIMESTAMP(`Calcs`.`date2`)) - 1) ) )/7) AS `TEMP(Test)(1157868287)(0)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("TEMP(Test)(1157868287)(0)");
    }

    @Test
    public void testLOGICAL_CALCS0() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`,  SUM(`Calcs`.`num3`) AS `sum_num3_ok`FROM `Calcs`WHERE ISNULL(`Calcs`.`str2`)GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
        rsmd.getColumnLabel(1).equals("sum_num3_ok");
    }

    @Test
    public void testLOGICAL_CALCS1() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`,  SUM(`Calcs`.`num3`) AS `sum_num3_ok`FROM `Calcs`WHERE ((`Calcs`.`str2` IN ('sixteen')) OR ISNULL(`Calcs`.`str2`))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
        rsmd.getColumnLabel(1).equals("sum_num3_ok");
    }

    @Test
    public void testLOGICAL_CALCS2() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`num2`) AS `sum_num2_ok`,  SUM(`Calcs`.`num2`) AS `$__alias__0`FROM `Calcs`GROUP BY 1ORDER BY `$__alias__0` DESCLIMIT 10     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_CALCS3() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT (CASE WHEN (`Calcs`.`str2` IN ('eleven', 'fifteen', 'five', 'fourteen', 'nine', 'one', 'six', 'sixteen', 'ten', 'three', 'twelve')) THEN 'eleven' ELSE `Calcs`.`str2` END) AS `Str2 (group)`FROM `Calcs`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Str2 (group)");
    }

    @Test
    public void testLOGICAL_CALCS4() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`FROM `Calcs`WHERE ((NOT ((`Calcs`.`str2` >= 'eight') AND (`Calcs`.`str2` <= 'six'))) OR ISNULL(`Calcs`.`str2`))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
    }

    @Test
    public void testLOGICAL_CALCS5() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING (SUM(`Calcs`.`int1`) <= 2)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS6() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING (NOT ISNULL(SUM(`Calcs`.`int1`)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS7() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING (((SUM(`Calcs`.`int1`) >= 0) AND (SUM(`Calcs`.`int1`) <= 2)) OR ISNULL(SUM(`Calcs`.`int1`)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS8() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`FROM `Calcs`WHERE ((NOT (`Calcs`.`str2` IN ('eight', 'eleven', 'fifteen', 'five'))) OR ISNULL(`Calcs`.`str2`))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
    }

    @Test
    public void testLOGICAL_CALCS9() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING (SUM(`Calcs`.`int1`) >= 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS10() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str0` AS `str0`FROM `Calcs`  INNER JOIN (  SELECT `Calcs`.`str0` AS `str0`,    SUM(`Calcs`.`int2`) AS `$__alias__0`  FROM `Calcs`  WHERE ((`Calcs`.`str0` >= 'FURNITURE') AND (`Calcs`.`str0` <= 'TECHNOLOGY'))  GROUP BY 1  ORDER BY `$__alias__0` DESC  LIMIT 2) `t0` ON (`Calcs`.`str0` = `t0`.`str0`)  INNER JOIN (  SELECT `Calcs`.`str1` AS `str1`,    SUM(`Calcs`.`int1`) AS `$__alias__1`  FROM `Calcs`  WHERE (((`Calcs`.`str1` >= 'AIR PURIFIERS') AND (`Calcs`.`str1` <= 'CD-R MEDIA')) OR ((`Calcs`.`str1` >= 'CONFERENCE PHONES') AND (`Calcs`.`str1` <= 'ERICSSON')))  GROUP BY 1  ORDER BY `$__alias__1` DESC  LIMIT 5) `t1` ON (`Calcs`.`str1` = `t1`.`str1`)GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str0");
    }

    @Test
    public void testLOGICAL_CALCS11() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING ((SUM(`Calcs`.`int1`) >= 0) AND (SUM(`Calcs`.`int1`) <= 2))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS12() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT FLOOR((TO_DAYS(`Calcs`.`date0`) - TO_DAYS(`Calcs`.`datetime0`)) / 2) AS `DayDiffs1 (bin)`,  FLOOR((TO_DAYS(`Calcs`.`datetime0`) - TO_DAYS(`Calcs`.`date0`)) / 3) AS `DayDiffs2 (bin)`,  FLOOR((TO_DAYS(`Calcs`.`date0`) - TO_DAYS(`Calcs`.`date1`)) / 4) AS `DayDiffs3 (bin)`,  FLOOR((YEAR(`Calcs`.`date0`) - YEAR(`Calcs`.`datetime0`)) / 2) AS `YearDiffs1 (bin)`,  FLOOR((YEAR(`Calcs`.`datetime0`) - YEAR(`Calcs`.`date0`)) / 3) AS `YearDiffs2 (bin)`,  FLOOR((YEAR(`Calcs`.`date0`) - YEAR(`Calcs`.`date1`)) / 4) AS `YearDiffs3 (bin)`FROM `Calcs`GROUP BY 1,  2,  3,  4,  5,  6     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("DayDiffs1 (bin)");
        rsmd.getColumnLabel(1).equals("DayDiffs2 (bin)");
        rsmd.getColumnLabel(2).equals("DayDiffs3 (bin)");
        rsmd.getColumnLabel(3).equals("YearDiffs1 (bin)");
        rsmd.getColumnLabel(4).equals("YearDiffs2 (bin)");
        rsmd.getColumnLabel(5).equals("YearDiffs3 (bin)");
    }

    @Test
    public void testLOGICAL_CALCS13() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`FROM `Calcs`GROUP BY 1HAVING (SUM(`Calcs`.`int2`) > 1000)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
    }

    @Test
    public void testLOGICAL_CALCS14() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT IFNULL(TIMESTAMP('2016-07-15 10:11:12.123'),STR_TO_DATE('2016-07-15 10:11:12.123','%b %e %Y %l:%i%p')) AS `Calculation_958703807427547136`FROM `Calcs`WHERE (IFNULL(TIMESTAMP('2016-07-15 10:11:12.123'),STR_TO_DATE('2016-07-15 10:11:12.123','%b %e %Y %l:%i%p')) = TIMESTAMP('2016-07-15 10:11:12.123'))HAVING (COUNT(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Calculation_958703807427547136");
    }

    @Test
    public void testLOGICAL_CALCS15() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str2` AS `str2`,  SUM(`Calcs`.`num3`) AS `sum_num3_ok`FROM `Calcs`WHERE ((`Calcs`.`str2` >= 'eight') AND (`Calcs`.`str2` <= 'two'))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str2");
        rsmd.getColumnLabel(1).equals("sum_num3_ok");
    }

    @Test
    public void testLOGICAL_CALCS16() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`str0` AS `str0`,  SUM(`Calcs`.`int2`) AS `sum_int2_ok`FROM `Calcs`  INNER JOIN (  SELECT `Calcs`.`str0` AS `str0`,    SUM(`Calcs`.`int2`) AS `$__alias__0`  FROM `Calcs`  GROUP BY 1  ORDER BY `$__alias__0` DESC  LIMIT 2) `t0` ON (`Calcs`.`str0` = `t0`.`str0`)WHERE (((`Calcs`.`str1` >= 'AIR PURIFIERS') AND (`Calcs`.`str1` <= 'CD-R MEDIA')) OR ((`Calcs`.`str1` >= 'CONFERENCE PHONES') AND (`Calcs`.`str1` <= 'ERICSSON')))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("str0");
        rsmd.getColumnLabel(1).equals("sum_int2_ok");
    }

    @Test
    public void testLOGICAL_CALCS17() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING ((SUM(`Calcs`.`int1`) <= 2) OR ISNULL(SUM(`Calcs`.`int1`)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS18() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`num2`) AS `sum_num2_ok`,  SUM(`Calcs`.`num2`) AS `$__alias__0`FROM `Calcs`GROUP BY 1ORDER BY `$__alias__0` ASCLIMIT 10     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_CALCS19() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING ((SUM(`Calcs`.`int1`) >= 0) OR ISNULL(SUM(`Calcs`.`int1`)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_CALCS20() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      --SELECT `Calcs`.`key` AS `key`,  SUM(`Calcs`.`int1`) AS `sum_int1_ok`FROM `Calcs`GROUP BY 1HAVING ISNULL(SUM(`Calcs`.`int1`))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("key");
        rsmd.getColumnLabel(1).equals("sum_int1_ok");
    }

    @Test
    public void testLOGICAL_STAPLES0() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`WHERE (`Staples`.`Customer Name` = 'Hallie Redmond')GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES1() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE (`Staples`.`Call Center Region` NOT IN ('CENTRAL'))GROUP BY 1,  2HAVING ((AVG(`Staples`.`Discount`) >= 0.051399999999999488) AND (AVG(`Staples`.`Discount`) <= 0.059000000000000587))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type1");
        rsmd.getColumnLabel(1).equals("Prod Type2");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES2() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Order Date` AS `Order Date`,  COUNT(1) AS `cnt_Number of Records_ok`FROM `Staples`WHERE ((`Staples`.`Order Date` >= TIMESTAMP('1998-02-09 00:00:00')) AND (`Staples`.`Order Date` <= TIMESTAMP('1998-07-03 23:59:59')))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Order Date");
        rsmd.getColumnLabel(1).equals("cnt_Number of Records_ok");
    }

    @Test
    public void testLOGICAL_STAPLES3() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`,  YEAR(`Staples`.`Order Date`) AS `yr_Order Date_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Discount` >= -0.001) AND (`Staples`.`Discount` <= 0.215172) AND (YEAR(`Staples`.`Order Date`) IN (2001, 2002)))GROUP BY 1,  2,  5HAVING ((COUNT(`Staples`.`Discount`) >= 0) AND (COUNT(`Staples`.`Discount`) <= 822) AND (SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 4074689.000000041))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("Customer Name");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(3).equals("sum_Order Quantity_ok");
        rsmd.getColumnLabel(4).equals("yr_Order Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES4() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT FLOOR((7 + DAYOFYEAR(`Staples`.`Ship Date`) - 1 + DAYOFWEEK(DATE_FORMAT(`Staples`.`Ship Date`, '%Y-01-01 00:00:00')) - 1) / 7) AS `DATEPART('week',Ship Date)`,  ADDDATE(FROM_DAYS( TO_DAYS(`Staples`.`Ship Date`) - (DAYOFWEEK(`Staples`.`Ship Date`) - 1) ), INTERVAL 0 SECOND ) AS `DATETRUNC('week',Ship Date)`,  `Staples`.`Ship Date` AS `Ship Date`,  DAYOFWEEK(`Staples`.`Ship Date`) AS `wd_Ship Date_ok`FROM `Staples`WHERE (`Staples`.`Ship Date` <= TIMESTAMP('1997-02-01 00:00:00'))GROUP BY 1,  2,  3,  4     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("DATEPART('week',Ship Date)");
        rsmd.getColumnLabel(1).equals("DATETRUNC('week',Ship Date)");
        rsmd.getColumnLabel(2).equals("Ship Date");
        rsmd.getColumnLabel(3).equals("wd_Ship Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES5() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Employee Name` AS `Employee Name`,  AVG(`Staples`.`Employee Salary`) AS `avg_Employee Salary_ok`FROM `Staples`  INNER JOIN (  SELECT `Staples`.`Call Center Region` AS `Call Center Region`,    `Staples`.`Employee Name` AS `Employee Name`  FROM `Staples`  GROUP BY 1,    2  HAVING ((AVG(`Staples`.`Employee Salary`) >= 102499.99999999898) AND (AVG(`Staples`.`Employee Salary`) <= 110000.00000000111))) `t0` ON ((`Staples`.`Call Center Region` = `t0`.`Call Center Region`) AND (`Staples`.`Employee Name` = `t0`.`Employee Name`))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Employee Name");
        rsmd.getColumnLabel(1).equals("avg_Employee Salary_ok");
    }

    @Test
    public void testLOGICAL_STAPLES6() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`PID` AS `PID`,  `Staples`.`Gross Profit` AS `sum_Gross Profit_ok`,  `Staples`.`Sales Total` AS `sum_Sales Total_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` = 'EAST') AND (`Staples`.`Sales Total` >= -3640.23) AND (`Staples`.`Sales Total` <= 24622.400000000001) AND (YEAR(`Staples`.`Order Date`) = 2002))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("PID");
        rsmd.getColumnLabel(1).equals("sum_Gross Profit_ok");
        rsmd.getColumnLabel(2).equals("sum_Sales Total_ok");
    }

    @Test
    public void testLOGICAL_STAPLES7() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`FROM `Staples`GROUP BY 1HAVING ((COUNT(`Staples`.`Discount`) >= 0) AND (COUNT(`Staples`.`Discount`) <= 822) AND (SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 4074689.000000041))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(2).equals("sum_Order Quantity_ok");
    }

    @Test
    public void testLOGICAL_STAPLES8() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type2` AS `Prod Type2`,  MONTH(`Staples`.`Order Date`) AS `mn_Order Date_ok`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE (MONTH(`Staples`.`Order Date`) <= 8)GROUP BY 1,  2     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type2");
        rsmd.getColumnLabel(1).equals("mn_Order Date_ok");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES9() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`GROUP BY 1ORDER BY `Customer Name` DESCLIMIT 10     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_STAPLES10() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING (AVG(`Staples`.`Discount`) < 0.070000000000000007)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES11() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Prod Type1` NOT IN ('FURNITURE')))GROUP BY 1,  2HAVING ((AVG(`Staples`.`Discount`) >= 0.051399999999999488) AND (AVG(`Staples`.`Discount`) <= 0.059000000000000587))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type1");
        rsmd.getColumnLabel(1).equals("Prod Type2");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES12() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Prod Type1` NOT IN ('FURNITURE')) AND ((`Staples`.`Prod Type2` IN ('APPLIANCES')) OR ((`Staples`.`Prod Type2` >= 'BOOKCASES') AND (`Staples`.`Prod Type2` <= 'COMPUTER PERIPHERALS')) OR ((`Staples`.`Prod Type2` >= 'ENVELOPES') AND (`Staples`.`Prod Type2` <= 'TELEPHONES AND COMMUNICATION'))))GROUP BY 1,  2HAVING ((AVG(`Staples`.`Discount`) >= 0.051399999999999488) AND (AVG(`Staples`.`Discount`) <= 0.059000000000000587))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type1");
        rsmd.getColumnLabel(1).equals("Prod Type2");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES13() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`,  SUM(`Staples`.`Price`) AS `sum_Price_ok`FROM `Staples`WHERE ((`Staples`.`Order Date` >= TIMESTAMP('1998-02-09 00:00:00')) AND (`Staples`.`Order Date` <= TIMESTAMP('1998-07-03 23:59:59')))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type2");
        rsmd.getColumnLabel(1).equals("sum_Gross Profit_ok");
        rsmd.getColumnLabel(2).equals("sum_Price_ok");
    }

    @Test
    public void testLOGICAL_STAPLES14() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  AVG(`Staples`.`Customer Balance`) AS `avg_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING ((AVG(`Staples`.`Customer Balance`) >= 252.99999999999747) AND (AVG(`Staples`.`Customer Balance`) <= 3702.7330280000369))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("avg_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES15() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Prod Type1` NOT IN ('FURNITURE')) AND ((`Staples`.`Prod Type2` IN ('APPLIANCES')) OR ((`Staples`.`Prod Type2` >= 'BOOKCASES') AND (`Staples`.`Prod Type2` <= 'COMPUTER PERIPHERALS')) OR ((`Staples`.`Prod Type2` >= 'ENVELOPES') AND (`Staples`.`Prod Type2` <= 'TELEPHONES AND COMMUNICATION'))))GROUP BY 1,  2HAVING ((AVG(`Staples`.`Discount`) >= 0.051399999999999488) AND (AVG(`Staples`.`Discount`) <= 0.059000000000000587))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type1");
        rsmd.getColumnLabel(1).equals("Prod Type2");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES17() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  COUNT(`Staples`.`Customer Balance`) AS `cnt_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING ((COUNT(`Staples`.`Customer Balance`) >= 0) AND (COUNT(`Staples`.`Customer Balance`) <= 577))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("cnt_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES18() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) AS `md_Order Date_ok`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE ((((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) >= 19970102) AND ((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) <= 19970107)) OR (((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) >= 19970109) AND ((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) <= 19970117)) OR (((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) >= 19970119) AND ((((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) <= 20021231)))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("md_Order Date_ok");
        rsmd.getColumnLabel(1).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES19() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES20() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  MIN(`Staples`.`Customer Balance`) AS `min_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING ((MIN(`Staples`.`Customer Balance`) >= -988.00000000000989) AND (MIN(`Staples`.`Customer Balance`) <= -99.999999999999005))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("min_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES21() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`PID` AS `PID`,  `Staples`.`Customer Balance` AS `sum_Customer Balance_ok`,  `Staples`.`Order Quantity` AS `sum_Order Quantity_ok`,  YEAR(`Staples`.`Order Date`) AS `yr_Order Date_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Discount` >= 0.091693999999999998) AND (`Staples`.`Discount` <= 0.214724) AND (YEAR(`Staples`.`Order Date`) IN (2001, 2002)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("PID");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(3).equals("sum_Order Quantity_ok");
        rsmd.getColumnLabel(4).equals("yr_Order Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES22() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT (((YEAR(`Staples`.`Order Date`) * 10000) + (MONTH(`Staples`.`Order Date`) * 100)) + DAYOFMONTH(`Staples`.`Order Date`)) AS `md_Order Date_ok`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("md_Order Date_ok");
        rsmd.getColumnLabel(1).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES23() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`GROUP BY 1,  2HAVING ((AVG(`Staples`.`Discount`) >= 0.051399999999999488) AND (AVG(`Staples`.`Discount`) <= 0.055001000000000549))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Prod Type1");
        rsmd.getColumnLabel(1).equals("Prod Type2");
        rsmd.getColumnLabel(2).equals("sum_Gross Profit_ok");
    }

    @Test
    public void testLOGICAL_STAPLES24() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Customer State` AS `Customer State`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`FROM `Staples`WHERE ((CASE WHEN ((((TO_DAYS(`Staples`.`Received Date`) - TO_DAYS(`Staples`.`Order Date`)) + (TIME_TO_SEC(ADDDATE(`Staples`.`Received Date`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Staples`.`Order Date`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) + 0.0) IN (1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 2, 21, 22, 23, 24, 25, 26, 27, 28, 3, 4, 5, 6, 7, 8, 9)) THEN 1 WHEN ((((TO_DAYS(`Staples`.`Received Date`) - TO_DAYS(`Staples`.`Order Date`)) + (TIME_TO_SEC(ADDDATE(`Staples`.`Received Date`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Staples`.`Order Date`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) + 0.0) IN (121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 137, 138)) THEN 121 WHEN ((((TO_DAYS(`Staples`.`Received Date`) - TO_DAYS(`Staples`.`Order Date`)) + (TIME_TO_SEC(ADDDATE(`Staples`.`Received Date`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Staples`.`Order Date`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) + 0.0) IN (82, 85, 86, 88, 89, 90)) THEN 82 WHEN ((((TO_DAYS(`Staples`.`Received Date`) - TO_DAYS(`Staples`.`Order Date`)) + (TIME_TO_SEC(ADDDATE(`Staples`.`Received Date`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(`Staples`.`Order Date`, INTERVAL 0 SECOND))) / (60 * 60 * 24)) + 0.0) IN (100, 102, 103, 105, 106, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 91, 92, 93, 94, 95, 96, 97, 99)) THEN 91 ELSE -1 END) = 121)GROUP BY 1,  2     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_STAPLES25() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`,  YEAR(`Staples`.`Order Date`) AS `yr_Order Date_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Discount` >= -0.001) AND (`Staples`.`Discount` <= 0.215172) AND (`Staples`.`Order Date` >= TIMESTAMP('1997-01-01 00:00:00')) AND (`Staples`.`Order Date` <= TIMESTAMP('2002-06-01 00:00:00')) AND (YEAR(`Staples`.`Order Date`) IN (2001, 2002)))GROUP BY 1,  2,  5HAVING ((COUNT(`Staples`.`Discount`) >= 0) AND (COUNT(`Staples`.`Discount`) <= 822) AND (SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 4074689.000000041))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("Customer Name");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(3).equals("sum_Order Quantity_ok");
        rsmd.getColumnLabel(4).equals("yr_Order Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES26() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  MAX(`Staples`.`Customer Balance`) AS `max_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING ((MAX(`Staples`.`Customer Balance`) >= 7008.9899999999298) AND (MAX(`Staples`.`Customer Balance`) <= 9000.0000000000891))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("max_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES27() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`PID` AS `PID`,  `Staples`.`Customer Balance` AS `sum_Customer Balance_ok`,  `Staples`.`Order Quantity` AS `sum_Order Quantity_ok`,  YEAR(`Staples`.`Order Date`) AS `yr_Order Date_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Discount` >= 0.091693999999999998) AND (`Staples`.`Discount` <= 0.214724) AND (`Staples`.`Order Date` >= TIMESTAMP('2001-06-01 00:00:00')) AND (`Staples`.`Order Date` <= TIMESTAMP('2002-06-01 00:00:00')) AND (YEAR(`Staples`.`Order Date`) IN (2001, 2002)))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("PID");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(3).equals("sum_Order Quantity_ok");
        rsmd.getColumnLabel(4).equals("yr_Order Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES28() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "SELECT SUM((`Staples`.`Price` / 1.1000000000000001)) AS `sum_Calculation_555068687593533440_ok`FROM `Staples` HAVING (COUNT(1) > 0) order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("sum_Calculation_555068687593533440_ok");
    }

    @Test
    public void testLOGICAL_STAPLES29() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`WHERE (((`Staples`.`Customer Name` >= 'Barbara Fisher') AND (`Staples`.`Customer Name` <= 'Roy Skaria')) OR ((`Staples`.`Customer Name` >= 'Sarah Jordon-Smith') AND (`Staples`.`Customer Name` <= 'Zyzzy Zzuyzyzyk')))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES30() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`GROUP BY 1HAVING ((SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 2384363.5474140239))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES31() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`FROM `Staples`GROUP BY 1HAVING ((AVG(`Staples`.`Discount`) >= 0.049244999999999504) AND (AVG(`Staples`.`Discount`) <= 0.060000000000000595))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(2).equals("sum_Order Quantity_ok");
    }

    @Test
    public void testLOGICAL_STAPLES32() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`  INNER JOIN (  SELECT `Staples`.`Customer Name` AS `Customer Name`,    SUM(`Staples`.`Customer Balance`) AS `$__alias__0`  FROM `Staples`  GROUP BY 1  ORDER BY `$__alias__0` DESC  LIMIT 10) `t0` ON (`Staples`.`Customer Name` = `t0`.`Customer Name`)GROUP BY 1,  2HAVING ((SUM(`Staples`.`Customer Balance`) >= 999999.99999998999) AND (SUM(`Staples`.`Customer Balance`) <= 3186976.0000000317))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("Customer Name");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES33() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`  INNER JOIN (  SELECT `Staples`.`Customer Name` AS `Customer Name`  FROM `Staples`  GROUP BY 1  HAVING (COUNT(`Staples`.`Order Date`) < 1000)) `t0` ON (`Staples`.`Customer Name` = `t0`.`Customer Name`)WHERE ((`Staples`.`Order Date` >= TIMESTAMP('1997-01-01 00:00:00')) AND (`Staples`.`Order Date` <= TIMESTAMP('1999-12-22 13:26:54')) AND (`Staples`.`Ship Mode` NOT IN ('DELIVERY TRUCK')) AND ((YEAR(`Staples`.`Order Date`) IN (2002)) OR ((YEAR(`Staples`.`Order Date`) >= 1998) AND (YEAR(`Staples`.`Order Date`) <= 2000))))GROUP BY 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
    }

    @Test
    public void testLOGICAL_STAPLES34() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` NOT IN ('CENTRAL')) AND (`Staples`.`Discount` >= -0.001) AND (`Staples`.`Discount` <= 0.215172))GROUP BY 1,  2HAVING ((COUNT(`Staples`.`Discount`) >= 0) AND (COUNT(`Staples`.`Discount`) <= 822) AND (SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 4074689.000000041))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("Customer Name");
        rsmd.getColumnLabel(2).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(3).equals("sum_Order Quantity_ok");
    }

    @Test
    public void testLOGICAL_STAPLES35() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`FROM `Staples`GROUP BY 1ORDER BY `Customer Name` DESCLIMIT 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_STAPLES36() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Market Segment` AS `Market Segment`,  `Staples`.`Prod Type1` AS `Prod Type1`,  `Staples`.`Prod Type2` AS `Prod Type2`,  `Staples`.`Prod Type3` AS `Prod Type3`,  `Staples`.`Prod Type4` AS `Prod Type4`,  SUM(`Staples`.`Gross Profit`) AS `sum_Gross Profit_ok`,  SUM(`Staples`.`Sales Total`) AS `sum_Sales Total_ok`FROM `Staples`WHERE ((`Staples`.`Customer State` = 'ALABAMA') AND (NOT ((`Staples`.`Market Segment` = 'CORPORATE') AND (`Staples`.`Prod Type1` = 'TECHNOLOGY') AND (`Staples`.`Prod Type2` = 'TELEPHONES AND COMMUNICATION') AND (`Staples`.`Prod Type3` = 'WIRELESS AND CELLULAR') AND (`Staples`.`Prod Type4` = 'NOKIA'))))GROUP BY 1,  2,  3,  4,  5     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Market Segment");
        rsmd.getColumnLabel(1).equals("Prod Type1");
        rsmd.getColumnLabel(2).equals("Prod Type2");
        rsmd.getColumnLabel(3).equals("Prod Type3");
        rsmd.getColumnLabel(4).equals("Prod Type4");
        rsmd.getColumnLabel(5).equals("sum_Gross Profit_ok");
        rsmd.getColumnLabel(6).equals("sum_Sales Total_ok");
    }

    @Test
    public void testLOGICAL_STAPLES37() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT TRUNCATE((((6 + DAYOFWEEK(DATE_ADD(ADDDATE( DATE_FORMAT( `Staples`.`Order Date`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ), INTERVAL (2 - (CASE WHEN (MONTH(`Staples`.`Order Date`) < 3) THEN 12 ELSE 0 END)) MONTH))) + ((TO_DAYS(`Staples`.`Order Date`) - TO_DAYS(DATE_ADD(ADDDATE( DATE_FORMAT( `Staples`.`Order Date`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ), INTERVAL (2 - (CASE WHEN (MONTH(`Staples`.`Order Date`) < 3) THEN 12 ELSE 0 END)) MONTH))) + (TIME_TO_SEC(ADDDATE(`Staples`.`Order Date`, INTERVAL 0 SECOND)) - TIME_TO_SEC(ADDDATE(DATE_ADD(ADDDATE( DATE_FORMAT( `Staples`.`Order Date`, '%Y-01-01 00:00:00' ), INTERVAL 0 SECOND ), INTERVAL (2 - (CASE WHEN (MONTH(`Staples`.`Order Date`) < 3) THEN 12 ELSE 0 END)) MONTH), INTERVAL 0 SECOND))) / (60 * 60 * 24))) / 7),0) AS `Week #`,  COUNT(DISTINCT `Staples`.`Order Date`) AS `ctd_Order Date_ok`,  YEAR(DATE_ADD(`Staples`.`Order Date`, INTERVAL 10 MONTH)) AS `yr_Order Date_ok`FROM `Staples`GROUP BY 1,  3     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testLOGICAL_STAPLES38() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT MONTH(`Staples`.`Order Date`) AS `mn_Order Date_qk`FROM `Staples`     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("mn_Order Date_qk");
    }

    @Test
    public void testLOGICAL_STAPLES39() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Call Center Region` AS `Call Center Region`,  `Staples`.`Order ID` AS `Order ID`,  `Staples`.`Product Container` AS `Product Container`,  AVG(`Staples`.`Gross Profit`) AS `avg_Gross Profit_ok`,  MONTH(`Staples`.`Order Date`) AS `mn_Order Date_ok`,  SUM(`Staples`.`Price`) AS `sum_Price_ok`,  YEAR(`Staples`.`Order Date`) AS `yr_Order Date_ok`FROM `Staples`WHERE ((`Staples`.`Call Center Region` = 'WEST') AND (`Staples`.`Discount` >= 0.27489599999999997) AND (`Staples`.`Discount` <= 0.39001000000000002) AND (((`Staples`.`Order ID` >= '1') AND (`Staples`.`Order ID` <= '35361')) OR ((`Staples`.`Order ID` >= '35363') AND (`Staples`.`Order ID` <= '9991'))) AND (`Staples`.`Product Container` = 'MEDIUM BOX') AND (MONTH(`Staples`.`Order Date`) IN (1, 4, 7, 10)))GROUP BY 1,  2,  3,  5,  7     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Call Center Region");
        rsmd.getColumnLabel(1).equals("Order ID");
        rsmd.getColumnLabel(2).equals("Product Container");
        rsmd.getColumnLabel(3).equals("avg_Gross Profit_ok");
        rsmd.getColumnLabel(4).equals("mn_Order Date_ok");
        rsmd.getColumnLabel(5).equals("sum_Price_ok");
        rsmd.getColumnLabel(6).equals("yr_Order Date_ok");
    }

    @Test
    public void testLOGICAL_STAPLES40() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Customer Name` AS `Customer Name`,  SUM(`Staples`.`Customer Balance`) AS `sum_Customer Balance_ok`,  SUM(`Staples`.`Order Quantity`) AS `sum_Order Quantity_ok`FROM `Staples`WHERE ((`Staples`.`Discount` >= -0.001) AND (`Staples`.`Discount` <= 0.215172))GROUP BY 1HAVING ((COUNT(`Staples`.`Discount`) >= 0) AND (COUNT(`Staples`.`Discount`) <= 822) AND (SUM(`Staples`.`Customer Balance`) >= -746.0000000000075) AND (SUM(`Staples`.`Customer Balance`) <= 4074689.000000041))     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        rsmd.getColumnLabel(0).equals("Customer Name");
        rsmd.getColumnLabel(1).equals("sum_Customer Balance_ok");
        rsmd.getColumnLabel(2).equals("sum_Order Quantity_ok");
    }

    @Test
    public void testLOGICAL_STAPLES41() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      SELECT `Staples`.`Employee Name` AS `Employee Name`,  AVG(`Staples`.`Employee Salary`) AS `avg_Employee Salary_ok`,  AVG(`Staples`.`Employee Salary`) AS `$__alias__0`FROM `Staples`GROUP BY 1ORDER BY `$__alias__0` DESCLIMIT 99     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }
}
