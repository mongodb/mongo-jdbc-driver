

package com.mongodb.jdbc.integration;

import static org.junit.Assert.*;

import java.sql.*;
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

    @Test
    public void testCALCSBI_821_bug() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "select num4, floor(Calcs.num4) as floor, Calcs.num4-floor(Calcs.num4) as diff from Calcs limit 3");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("num4", rsmd.getColumnLabel(1));
        assertEquals("floor", rsmd.getColumnLabel(2));
        assertEquals("diff", rsmd.getColumnLabel(3));
    }

    @Test
    public void testCALCS0() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(from_days(floor(null) + 693961)) as `temp(test)(2074921570)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2074921570)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS1() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2348327946)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2348327946)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS2() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(from_days(floor(null) + 693961)) as `temp(test)(3062347157)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3062347157)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS3() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1236088422)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1236088422)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS4() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(from_days(floor(null) + 693961)) as `temp(test)(1709161123)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1709161123)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS5() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(941741456)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(941741456)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS6() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(from_days(floor(null) + 693961)) - 1 + dayofweek(date_format(from_days(floor(null) + 693961), '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(4070818381)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4070818381)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS7() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1 + dayofweek(date_format(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(1209329404)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1209329404)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS8() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(from_days(floor(null) + 693961)) as `temp(test)(2284623665)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2284623665)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS9() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3556637072)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3556637072)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS10() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(from_days(floor(null) + 693961)) as `temp(test)(20465857)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(20465857)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS11() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3365622206)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3365622206)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS12() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(from_days(floor(null) + 693961)) as `temp(test)(1193407708)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1193407708)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS13() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3498421513)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3498421513)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS14() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1756144708)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1756144708)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS15() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2635020195)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2635020195)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS16() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2744314424)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2744314424)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS17() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(232803726)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(232803726)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS18() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(2176505489)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2176505489)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS19() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 minute) as `temp(test)(2741755004)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2741755004)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS20() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2526477208)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2526477208)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS21() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2007354609)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2007354609)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS22() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3928745396)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3928745396)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS23() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(746880020)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(746880020)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS24() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2699142763)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2699142763)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS25() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(from_days(floor(null) + 693961)) as `temp(test)(1634134069)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1634134069)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS26() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1949844743)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1949844743)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS27() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(from_days(floor(null) + 693961)) as `temp(test)(3376136658)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3376136658)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS28() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3672267408)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3672267408)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS29() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(from_days(floor(null) + 693961)) as `temp(test)(2406708804)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2406708804)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS30() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1 + dayofweek(date_format(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(1073594909)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1073594909)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS31() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(from_days(floor(null) + 693961)) - 1 + dayofweek(date_format(from_days(floor(null) + 693961), '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(4016689999)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4016689999)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS32() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3405047399)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3405047399)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS33() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(from_days(floor(null) + 693961)) as `temp(test)(55506858)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(55506858)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS34() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3460070750)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3460070750)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS35() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(from_days(floor(null) + 693961)) as `temp(test)(1494289478)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1494289478)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS36() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3227046355)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3227046355)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS37() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(from_days(floor(null) + 693961)) as `temp(test)(1233941598)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1233941598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS38() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3874232094)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3874232094)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS39() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1546814749)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1546814749)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS40() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3692431276)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3692431276)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS41() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(`Calcs`.`int0`) as `temp(test)(3910975586)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3910975586)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS42() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count((`Calcs`.`bool0_` <> 0)) as `temp(test)(1133866179)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1133866179)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS43() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(`Calcs`.`date3`) as `temp(test)(3590771088)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3590771088)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS44() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(`Calcs`.`num4`) as `temp(test)(1804085677)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1804085677)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS45() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(`Calcs`.`str2`) as `temp(test)(2760211945)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2760211945)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS46() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(3386714330)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3386714330)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS47() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(1554877814)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1554877814)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS48() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(680392169)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(680392169)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS49() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(792760981)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(792760981)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS50() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:%s' ), interval 0 second ) as `temp(test)(4192719501)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4192719501)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS51() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:%s' ), interval 0 second ) as `temp(test)(2927274352)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2927274352)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS52() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when 2 >= 0 then left(`Calcs`.`str1`,2) else null end) as `temp(test)(2443162804)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2443162804)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS53() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when 3 >= 0 then left(`Calcs`.`str2`,3) else null end) as `temp(test)(1954670685)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1954670685)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS54() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when `Calcs`.`int0` >= 0 then left(`Calcs`.`str2`,`Calcs`.`int0`) else null end) as `temp(test)(3664185027)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3664185027)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS55() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:%s' ), interval 0 second ) as `temp(test)(3300724379)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3300724379)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS56() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`date2`) as `temp(test)(4107590482)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4107590482)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS57() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`datetime0`) as `temp(test)(766794695)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(766794695)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS58() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 1 as `temp(test)(3095770696)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3095770696)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS59() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 0 as `temp(test)(334867691)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(334867691)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS60() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:00' ), interval 0 second ) as `temp(test)(1224905293)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1224905293)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS61() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(3044284514)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3044284514)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS62() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(2383411022)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2383411022)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS63() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(3392256124)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3392256124)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS64() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(1426463696)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1426463696)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS65() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(timestamp(`Calcs`.`datetime0`)) as `temp(test)(1770279206)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1770279206)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS66() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(timestamp(`Calcs`.`datetime0`)) as `temp(test)(4279914489)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4279914489)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS67() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'data' as `temp(test)(2967749075)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2967749075)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS68() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select upper(`Calcs`.`str2`) as `temp(test)(3516395767)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3516395767)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS69() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`bool0_` <> 0) as `temp(test)(3428507074)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3428507074)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS70() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`bool1_` <> 0) as `temp(test)(1935567978)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1935567978)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS71() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`bool2_` <> 0) as `temp(test)(3179501244)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3179501244)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS72() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`bool3_` <> 0) as `temp(test)(1288552116)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1288552116)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS73() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`date0` as `temp(test)(1090544928)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1090544928)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS74() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`date1` as `temp(test)(1295100109)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1295100109)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS75() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`date2` as `temp(test)(2028340584)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2028340584)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS76() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`date3` as `temp(test)(550459061)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(550459061)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS77() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`datetime0` as `temp(test)(3848052829)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3848052829)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS78() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`datetime1` as `temp(test)(1108086785)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1108086785)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS79() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`key` as `temp(test)(3382465274)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3382465274)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS80() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`str0` as `temp(test)(55415805)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(55415805)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS81() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`str1` as `temp(test)(2285743265)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2285743265)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS82() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`str2` as `temp(test)(3228347817)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3228347817)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS83() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`str3` as `temp(test)(286811776)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(286811776)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS84() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`time0` as `temp(test)(4245842207)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4245842207)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS85() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`time1` as `temp(test)(665897456)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(665897456)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS86() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`zzz` as `temp(test)(1729594319)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1729594319)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS87() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`int0` as `temp(test)(3174765981)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3174765981)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS88() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`int1` as `temp(test)(2829869592)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2829869592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS89() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`int2` as `temp(test)(551775594)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(551775594)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS90() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`int3` as `temp(test)(524492059)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(524492059)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS91() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num0` as `temp(test)(3934956185)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3934956185)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS92() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num1` as `temp(test)(129981160)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(129981160)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS93() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num2` as `temp(test)(1053269056)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1053269056)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS94() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num3` as `temp(test)(3320504981)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3320504981)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS95() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num4` as `temp(test)(3786834202)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3786834202)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS96() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval 1 day) as `temp(test)(670684053)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(670684053)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS97() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 day) as `temp(test)(2728495522)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2728495522)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS98() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`date2`) then null	when isnull(`Calcs`.`date3`) then null	else least(`Calcs`.`date2`, `Calcs`.`date3`) end) as `temp(test)(3951339438)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3951339438)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS99() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`date2`) as `temp(test)(1465246653)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1465246653)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS100() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`datetime0`) as `temp(test)(2572329321)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2572329321)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS101() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*4 + (quarter(`Calcs`.`date2`) - quarter(`Calcs`.`date3`))) as `temp(test)(4144088821)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4144088821)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS102() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*4 + (quarter(`Calcs`.`datetime0`) - quarter(timestamp(`Calcs`.`date2`)))) as `temp(test)(2035564840)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2035564840)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS103() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`date2`) as `temp(test)(477986140)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(477986140)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS104() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`datetime0`) as `temp(test)(2224240773)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2224240773)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS105() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'data' as `temp(test)(535453017)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(535453017)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS106() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`str1`) then null	when isnull(`Calcs`.`str2`) then null	else least(`Calcs`.`str1`, `Calcs`.`str2`) end) as `temp(test)(497224717)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(497224717)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS107() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`str2`) then null	when isnull(`Calcs`.`str3`) then null	else least(`Calcs`.`str2`, `Calcs`.`str3`) end) as `temp(test)(1239505702)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1239505702)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS108() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(1298877827)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1298877827)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS109() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(1695139533)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1695139533)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS110() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(1003104432)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1003104432)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS111() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*12 + (month(`Calcs`.`date2`) - month(`Calcs`.`date3`))) as `temp(test)(381839689)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(381839689)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS112() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*12 + (month(`Calcs`.`datetime0`) - month(timestamp(`Calcs`.`date2`)))) as `temp(test)(2416406882)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2416406882)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS113() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`date2`) - year(`Calcs`.`date3`)) as `temp(test)(3489013143)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3489013143)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS114() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`))) as `temp(test)(3834106318)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3834106318)(0)", rsmd.getColumnLabel(1));
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
                        "      select 'bat' as `temp(test)(3161246105)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3161246105)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS117() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select replace(`Calcs`.`str2`,'e','o') as `temp(test)(2953834147)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2953834147)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS118() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select truncate(`Calcs`.`int1`,0) as `temp(test)(551720338)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(551720338)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS119() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when (`Calcs`.`bool0_` <> 0) then 1	when not (`Calcs`.`bool0_` <> 0) then 0	else null end) as `temp(test)(2695057561)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2695057561)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS120() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date0`) - 693961) as `temp(test)(2234960540)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2234960540)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS121() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select truncate(`Calcs`.`num2`,0) as `temp(test)(1665700248)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1665700248)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS122() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select truncate(`Calcs`.`str2`,0) as `temp(test)(2779514991)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2779514991)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS123() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (right(rtrim(`Calcs`.`str1`), length('s')) = 's') as `temp(test)(1759936097)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1759936097)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS124() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (right(rtrim(`Calcs`.`str2`), length('een')) = 'een') as `temp(test)(3179156403)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3179156403)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS125() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sin(`Calcs`.`int2`) as `temp(test)(527156183)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(527156183)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS126() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sin(`Calcs`.`num0`) as `temp(test)(1184030290)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1184030290)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS127() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(554447598)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(554447598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS128() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(2130687817)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2130687817)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS129() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(903794974)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(903794974)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS130() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(3917828147)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3917828147)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS131() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(3641022413)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3641022413)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS132() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(1193998601)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1193998601)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS133() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(3641022413)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3641022413)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS134() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(1193998601)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1193998601)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS135() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(3800988289)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3800988289)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS136() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(779479971)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(779479971)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS137() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(3800988289)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3800988289)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS138() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(779479971)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(779479971)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS139() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:00:00' ), interval 0 second ) as `temp(test)(2793013592)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2793013592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS140() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:00:00' ), interval 0 second ) as `temp(test)(2980130610)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2980130610)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS141() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select exp((0.10000000000000001 * `Calcs`.`num0`)) as `temp(test)(526466750)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(526466750)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS142() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select exp(`Calcs`.`int2`) as `temp(test)(2988208579)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2988208579)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS143() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(2016952657)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2016952657)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS144() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(1256216982)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1256216982)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS145() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 97 as `temp(test)(415603459)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(415603459)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS146() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ascii(`Calcs`.`str2`) as `temp(test)(526259814)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(526259814)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS147() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ascii(`Calcs`.`str1`) as `temp(test)(4258651616)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4258651616)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS148() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(3415515666)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3415515666)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS149() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(2048935536)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2048935536)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS150() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(2714077903)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2714077903)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS151() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(1800100416)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1800100416)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS152() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`date0` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`date1` else null end) as `temp(test)(3513628645)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3513628645)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS153() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`str2` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`str3` else `Calcs`.`str0` end) as `temp(test)(1007528555)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1007528555)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS154() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`num0` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`num1` else `Calcs`.`num2` end) as `temp(test)(3428504110)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3428504110)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS155() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`date0` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`date1` else `Calcs`.`date2` end) as `temp(test)(1581504649)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1581504649)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS156() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`num0` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`num1` else null end) as `temp(test)(750655768)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(750655768)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS157() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (((`Calcs`.`bool0_` <> 0) and (`Calcs`.`bool1_` <> 0)) or ((not (`Calcs`.`bool0_` <> 0)) and (`Calcs`.`bool2_` <> 0))) as `temp(test)(1656302737)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1656302737)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS158() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select isnull((`Calcs`.`bool0_` <> 0)) as `temp(test)(4006206882)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4006206882)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS159() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`str2` when not (`Calcs`.`bool0_` <> 0) then `Calcs`.`str3` else null end) as `temp(test)(4173709053)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4173709053)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS160() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((case when (`Calcs`.`bool0_` <> 0) then (case	when (`Calcs`.`bool1_` <> 0) then 1	when not (`Calcs`.`bool1_` <> 0) then 0	else null end) else (case	when (`Calcs`.`bool2_` <> 0) then 1	when not (`Calcs`.`bool2_` <> 0) then 0	else null end) end) = 1) as `temp(test)(1285160207)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1285160207)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS161() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`num0` else `Calcs`.`num1` end) as `temp(test)(898375479)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(898375479)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS162() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`date0` else `Calcs`.`date1` end) as `temp(test)(3012038505)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3012038505)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS163() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`bool0_` <> 0) then `Calcs`.`str2` else `Calcs`.`str3` end) as `temp(test)(490796425)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(490796425)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS164() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 60) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 60)) as `temp(test)(2300448284)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2300448284)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS165() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 60) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 60)) as `temp(test)(2077207759)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2077207759)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS166() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select radians(`Calcs`.`int2`) as `temp(test)(1973795369)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1973795369)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS167() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select radians(`Calcs`.`num0`) as `temp(test)(2823743498)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2823743498)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS168() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select log(`Calcs`.`int2`) as `temp(test)(2832324438)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2832324438)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS169() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select log(`Calcs`.`num0`) as `temp(test)(1125921255)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1125921255)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS170() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 3600) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 3600)) as `temp(test)(289918985)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(289918985)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS171() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`date2`) - 1 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(3370976929)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3370976929)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS172() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(date(`Calcs`.`date3`)) - 1 + dayofweek(date_format(date(`Calcs`.`date3`), '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(2942029924)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2942029924)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS173() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(3904538922)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3904538922)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS174() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(3904538922)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3904538922)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS175() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(1590117682)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1590117682)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS176() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(4199707040)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4199707040)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS177() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(2589771434)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2589771434)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS178() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(1875124737)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1875124737)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS179() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select truncate(`Calcs`.`num4`,0) as `temp(test)(663412696)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(663412696)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS180() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select truncate(concat(concat(`Calcs`.`num4`), concat(`Calcs`.`int0`)),0) as `temp(test)(1616170242)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1616170242)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS181() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(3854194266)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3854194266)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS182() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`date2`) as `temp(test)(3854194266)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3854194266)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS183() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(621889678)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(621889678)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS184() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(`Calcs`.`datetime0`) as `temp(test)(621889678)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(621889678)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS185() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(302607578)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(302607578)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS186() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(2001673842)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2001673842)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS187() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60*60 + (time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) - time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)))) as `temp(test)(3772571288)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3772571288)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS188() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60*60 + (time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) - time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)))) as `temp(test)(3405329770)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3405329770)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS189() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(885008067)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(885008067)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS190() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(3554344781)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3554344781)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS191() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(date(`Calcs`.`date2`)) as `temp(test)(2085924889)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2085924889)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS192() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(timestamp(`Calcs`.`datetime0`)) as `temp(test)(574618496)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(574618496)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS193() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 1 as `temp(test)(3095770696)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3095770696)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS194() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(date(`Calcs`.`date2`)) as `temp(test)(1165289219)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1165289219)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS195() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(timestamp(`Calcs`.`datetime0`)) as `temp(test)(3278952934)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3278952934)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS196() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(date(`Calcs`.`date2`)) as `temp(test)(3434755864)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3434755864)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS197() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(timestamp(`Calcs`.`datetime0`)) as `temp(test)(1819497289)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1819497289)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS198() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select cot(`Calcs`.`int2`) as `temp(test)(2415226193)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS199() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select cot(`Calcs`.`num0`) as `temp(test)(2834009176)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS200() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(count(distinct `Calcs`.`num2`), 0) as `temp(test)(957319405)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(957319405)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS201() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(`Calcs`.`datetime0`) as `temp(test)(3191651815)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3191651815)(0)", rsmd.getColumnLabel(1));
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
                        "      select adddate( concat( date_format( `Calcs`.`date2`, '%y-' ), (3*(quarter(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(1126788499)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1126788499)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS204() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( `Calcs`.`datetime0`, '%y-' ), (3*(quarter(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(3855281255)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3855281255)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS205() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sum(`Calcs`.`int0`) as `temp(test)(645427419)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(645427419)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS206() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sum(`Calcs`.`num4`) as `temp(test)(1450575838)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1450575838)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS207() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:00:00' ), interval 0 second ) as `temp(test)(2456153780)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2456153780)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS208() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`date2`) - 1 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(2524080111)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2524080111)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS209() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(1568799041)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1568799041)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS210() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`date2`) - ((7 + dayofweek(`Calcs`.`date2`) - 2) % 7) ), interval 0 second ) as `temp(test)(1744581337)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1744581337)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS211() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`date2`) - (dayofweek(`Calcs`.`date2`) - 1) ), interval 0 second ) as `temp(test)(1635756518)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1635756518)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS212() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`datetime0`) - ((7 + dayofweek(`Calcs`.`datetime0`) - 2) % 7) ), interval 0 second ) as `temp(test)(1985269479)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1985269479)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS213() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`datetime0`) - (dayofweek(`Calcs`.`datetime0`) - 1) ), interval 0 second ) as `temp(test)(3887385220)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3887385220)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS214() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'ta' as `temp(test)(2843244905)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2843244905)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS215() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when 3 >= 0 then right(`Calcs`.`str2`,3) else null end) as `temp(test)(868342576)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(868342576)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS216() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when `Calcs`.`int0` >= 0 then right(`Calcs`.`str2`,`Calcs`.`int0`) else null end) as `temp(test)(427841631)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(427841631)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS217() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 60) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 60)) as `temp(test)(2180476504)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2180476504)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS218() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(3361088979)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3361088979)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS219() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(299717125)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(299717125)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS220() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(3076245501)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3076245501)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS221() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(148436784)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(148436784)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS222() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (locate('e',`Calcs`.`str2`) > 0) as `temp(test)(1364536471)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1364536471)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS223() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (locate('ind',`Calcs`.`str1`) > 0) as `temp(test)(1380546255)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1380546255)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS224() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval 1 day) as `temp(test)(1743407296)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1743407296)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS225() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 day) as `temp(test)(2988076353)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2988076353)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS226() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sign(`Calcs`.`int2`) as `temp(test)(3509671532)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3509671532)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS227() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sign(`Calcs`.`num0`) as `temp(test)(4247289834)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4247289834)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS228() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(`Calcs`.`int2`,2) as `temp(test)(3037854782)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3037854782)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS229() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(concat('      ', `Calcs`.`str2`), '      ') as `temp(test)(2313738384)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2313738384)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS230() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'const' as `temp(test)(3972932107)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3972932107)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS231() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select acos((`Calcs`.`num0` / 20)) as `temp(test)(4196263986)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4196263986)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS232() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(1773778045)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1773778045)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS233() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(382789366)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(382789366)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS234() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(444902156)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(444902156)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS235() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(581676997)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(581676997)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS236() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( from_days(floor(null) + 693961), '%y-' ), (3*(quarter(from_days(floor(null) + 693961))-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(1831450015)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1831450015)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS237() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-' ), (3*(quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(360201683)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(360201683)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS238() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1) ), interval 0 second ) as `temp(test)(872678106)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(872678106)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS239() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1) ), interval 0 second ) as `temp(test)(3905701997)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3905701997)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS240() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3359079369)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3359079369)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS241() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(1326289938)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1326289938)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS242() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2763829899)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2763829899)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS243() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(717997108)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(717997108)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS244() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2963633898)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2963633898)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS245() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3202209617)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3202209617)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS246() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:00:00' ), interval 0 second ) as `temp(test)(4266496460)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4266496460)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS247() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:%i:%s' ), interval 0 second ) as `temp(test)(4131996060)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4131996060)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS248() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:%i:00' ), interval 0 second ) as `temp(test)(2935754523)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2935754523)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS249() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`str1`) then null	when isnull(`Calcs`.`str2`) then null	else greatest(`Calcs`.`str1`, `Calcs`.`str2`) end) as `temp(test)(3052188625)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3052188625)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS250() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`str3`) then null	when isnull(`Calcs`.`str2`) then null	else greatest(`Calcs`.`str3`, `Calcs`.`str2`) end) as `temp(test)(2280873463)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2280873463)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS251() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(2643375604)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2643375604)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS252() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(2986242609)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2986242609)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS253() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(1608337423)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1608337423)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS254() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(925465559)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(925465559)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS255() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when `Calcs`.`num4` >= 0 then right(`Calcs`.`str0`,`Calcs`.`num4`) else null end) as `temp(test)(3619367444)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS256() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval (3 * 1) month) as `temp(test)(893348878)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(893348878)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS257() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval (3 * 1) month) as `temp(test)(454013980)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(454013980)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS258() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`date2`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7)) as `temp(test)(499182808)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(499182808)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS259() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`date2`) - 1 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(2644944117)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2644944117)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS260() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7)) as `temp(test)(3094931040)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3094931040)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS261() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(2831690081)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2831690081)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS262() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select if(isnull(6), null, substring(`Calcs`.`str1`,greatest(1,floor(6)))) as `temp(test)(98307893)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(98307893)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS263() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select if(isnull(2), null, substring(`Calcs`.`str1`,greatest(1,floor(2)),floor(4))) as `temp(test)(3472698691)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3472698691)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS264() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when `Calcs`.`num4` >= 0 then left(`Calcs`.`str0`,`Calcs`.`num4`) else null end) as `temp(test)(1907571572)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS265() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(1667583030)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1667583030)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS266() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(2537119552)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2537119552)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS267() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int1` + 0.0) as `temp(test)(1533389080)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1533389080)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS268() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case        when (`Calcs`.`bool0_` <> 0) then 1.0        when not (`Calcs`.`bool0_` <> 0) then 0.0        else null end) as `temp(test)(2538631291)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2538631291)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS269() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date0`) - 693961.0 + time_to_sec(adddate(`Calcs`.`date0`, interval 0 second)) / (24.0 * 60.0 * 60.0) ) as `temp(test)(64617177)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(64617177)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS270() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num2` + 0.0) as `temp(test)(2707307071)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2707307071)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS271() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (0.0 + `Calcs`.`str2`) as `temp(test)(1394352864)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1394352864)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS272() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(ifnull(timestamp('2010-10-10 10:10:10.4'),str_to_date('2010-10-10 10:10:10.4','%b %e %y %l:%i%p'))) as `temp(test)(2143701310)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2143701310)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS273() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select lower(`Calcs`.`str2`) as `temp(test)(1011144549)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1011144549)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS274() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select lower(`Calcs`.`str1`) as `temp(test)(2419238545)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2419238545)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS275() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(2997515538)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2997515538)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS276() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(4264664103)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4264664103)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS277() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 3.1415926535897931 as `temp(test)(356598120)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(356598120)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS278() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (3.1415926535897931 * `Calcs`.`num0`) as `temp(test)(1299212312)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1299212312)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS279() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`date2`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7) as `temp(test)(3400925592)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3400925592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS280() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`date2`) - 1 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(1636919423)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1636919423)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS281() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`date2`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7) as `temp(test)(3400925592)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3400925592)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS282() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`date2`) - 1 + dayofweek(date_format(`Calcs`.`date2`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(1636919423)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1636919423)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS283() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7) as `temp(test)(3595934100)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3595934100)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS284() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(4171408365)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4171408365)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS285() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + ((7 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 2) % 7) ) / 7) as `temp(test)(3595934100)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3595934100)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS286() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(`Calcs`.`datetime0`) - 1 + dayofweek(date_format(`Calcs`.`datetime0`, '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(4171408365)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4171408365)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS287() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`date2`) as `temp(test)(1706489238)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1706489238)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS288() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`date2`) as `temp(test)(3326454598)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3326454598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS289() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`datetime0`) as `temp(test)(1346443059)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1346443059)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS290() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(`Calcs`.`datetime0`) as `temp(test)(2366796649)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2366796649)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS291() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select tan(`Calcs`.`int2`) as `temp(test)(1227693937)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1227693937)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS292() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select degrees(`Calcs`.`int2`) as `temp(test)(2688244734)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2688244734)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS293() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select degrees(`Calcs`.`num0`) as `temp(test)(583539797)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(583539797)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS294() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(1438827077)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1438827077)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS295() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(331799714)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(331799714)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS296() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(3561169943)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3561169943)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS297() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(2283476857)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2283476857)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS298() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961))) as `temp(test)(523796786)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(523796786)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS299() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(1757347367)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1757347367)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS300() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961)))*4 + (quarter(from_days(floor(null) + 693961)) - quarter(from_days(floor(null) + 693961)))) as `temp(test)(2892653053)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2892653053)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS301() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*4 + (quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))) as `temp(test)(208306356)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(208306356)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS302() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*12 + (month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))) as `temp(test)(3602652935)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3602652935)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS303() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961)))*12 + (month(from_days(floor(null) + 693961)) - month(from_days(floor(null) + 693961)))) as `temp(test)(2736821)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2736821)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS304() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1)) - (to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1) ) )/7) as `temp(test)(4175150207)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4175150207)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS305() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1)) - (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1) ) )/7) as `temp(test)(573134401)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(573134401)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS306() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(4284829593)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4284829593)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS307() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(2962792486)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2962792486)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS308() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(2631483492)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2631483492)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS309() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(1607049625)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1607049625)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS310() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(1299959868)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1299959868)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS311() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(1641185958)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1641185958)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS312() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24 + floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 3600) - floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 3600)) as `temp(test)(1258940435)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1258940435)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS313() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24*60 + floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 60) - floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 60)) as `temp(test)(401058515)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(401058515)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS314() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24*60*60 + (time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) - time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)))) as `temp(test)(2833809390)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2833809390)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS315() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(367110610)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(367110610)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS316() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(1785761163)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1785761163)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS317() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select '>  <' as `temp(test)(3167158121)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3167158121)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS318() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( `Calcs`.`date2`, '%y-' ), (3*(quarter(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(4146692480)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4146692480)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS319() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( `Calcs`.`date2`, '%y-' ), (3*(quarter(`Calcs`.`date2`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(560528826)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(560528826)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS320() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( `Calcs`.`datetime0`, '%y-' ), (3*(quarter(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(105511240)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(105511240)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS321() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( `Calcs`.`datetime0`, '%y-' ), (3*(quarter(`Calcs`.`datetime0`)-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(755301458)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(755301458)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS322() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(1699663235)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1699663235)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS323() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(1554256126)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1554256126)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS324() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(2171721785)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2171721785)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS325() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(3941430330)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3941430330)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS326() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (std(`Calcs`.`num4`) * sqrt(count(`Calcs`.`num4`) / (count(`Calcs`.`num4`) - 1))) as `temp(test)(2430775290)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2430775290)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS327() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select std(`Calcs`.`num4`) as `temp(test)(3542464170)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3542464170)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS328() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`date2`) as `temp(test)(3471130809)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3471130809)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS329() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(`Calcs`.`datetime0`) as `temp(test)(482138814)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(482138814)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS330() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select trim(concat(concat(' ', `Calcs`.`str2`), ' ')) as `temp(test)(1903992131)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1903992131)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS331() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power((std(`Calcs`.`num4`) * sqrt(count(`Calcs`.`num4`) / (count(`Calcs`.`num4`) - 1))), 2) as `temp(test)(1358865)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1358865)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS332() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(std(`Calcs`.`num4`),2) as `temp(test)(2532468070)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2532468070)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS333() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(4265410721)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4265410721)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS334() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(1278698096)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1278698096)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS335() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(3729248905)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3729248905)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS336() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(965356852)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(965356852)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS337() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 4 as `temp(test)(5037157)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(5037157)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS338() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select length(`Calcs`.`str2`) as `temp(test)(382448263)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(382448263)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS339() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`date2`) as `temp(test)(653088523)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(653088523)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS340() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(`Calcs`.`datetime0`) as `temp(test)(3134852500)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3134852500)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS341() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(`Calcs`.`int1`, 0) as `temp(test)(3976315675)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3976315675)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS342() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(591126205)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(591126205)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS343() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3034828475)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3034828475)(0)", rsmd.getColumnLabel(1));
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
                        "      select (case `Calcs`.`int0` when 1 then 'test1' when 3 then 'test3' else 'testelse' end) as `temp(test)(4155671032)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4155671032)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS346() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`int0` = 1) then 'yes' else 'no' end) as `temp(test)(344883989)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(344883989)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS347() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`int0` = 1) then 'yes' when (`Calcs`.`int0` = 3) then 'yes3' else 'no' end) as `temp(test)(1470681487)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1470681487)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS348() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(`Calcs`.`int0`, 0) as `temp(test)(404394451)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(404394451)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS349() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`int0` > 3) then 'yes' when not (`Calcs`.`int0` > 3) then 'no' else null end) as `temp(test)(2582407534)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2582407534)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS350() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`int0` > 3) then 'yes' when not (`Calcs`.`int0` > 3) then 'no' else 'i dont know' end) as `temp(test)(485230187)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(485230187)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS351() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select isnull(`Calcs`.`int0`) as `temp(test)(3944872634)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3944872634)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS352() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'yes' as `temp(test)(1030668643)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1030668643)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS353() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`int0` <> 1) then 'yes' else 'no' end) as `temp(test)(1548476355)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1548476355)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS354() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`num0` when not (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`num1` else null end) as `temp(test)(2733626226)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2733626226)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS355() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select isnull(`Calcs`.`num4`) as `temp(test)(746449830)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(746449830)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS356() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select isnull(`Calcs`.`str2`) as `temp(test)(4153117630)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4153117630)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS357() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`str0` > `Calcs`.`str1`) then `Calcs`.`str2` when not (`Calcs`.`str0` > `Calcs`.`str1`) then `Calcs`.`str3` else null end) as `temp(test)(661341884)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(661341884)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS358() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`date0`) then null	when isnull(`Calcs`.`date1`) then null	else least(`Calcs`.`date0`, `Calcs`.`date1`) end) as `temp(test)(1970381992)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1970381992)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS359() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`date0` when not (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`date1` else `Calcs`.`date2` end) as `temp(test)(2049518482)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2049518482)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS360() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select lower(`Calcs`.`str0`) as `temp(test)(157987442)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(157987442)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS361() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`str2` when not (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`str3` else `Calcs`.`str0` end) as `temp(test)(3250337019)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3250337019)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS362() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`date0` when not (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`date1` else null end) as `temp(test)(1454773621)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1454773621)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS363() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`num0` when not (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`num1` else `Calcs`.`num2` end) as `temp(test)(1162317302)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1162317302)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS364() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(lower(`Calcs`.`str0`)) then null	when isnull(`Calcs`.`str2`) then null	else least(lower(`Calcs`.`str0`), `Calcs`.`str2`) end) as `temp(test)(1389344980)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1389344980)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS365() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(`Calcs`.`date0`, date('2010-04-12')) as `temp(test)(1229425804)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1229425804)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS366() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(`Calcs`.`num4`, -1) as `temp(test)(4224438892)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4224438892)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS367() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ifnull(`Calcs`.`str2`, 'i''m null') as `temp(test)(3314993157)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3314993157)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS368() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select isnull(`Calcs`.`date0`) as `temp(test)(2842042984)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2842042984)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS369() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((case when (`Calcs`.`num0` > `Calcs`.`num1`) then (case	when (`Calcs`.`bool1_` <> 0) then 1	when not (`Calcs`.`bool1_` <> 0) then 0	else null end) else (case	when (`Calcs`.`bool2_` <> 0) then 1	when not (`Calcs`.`bool2_` <> 0) then 0	else null end) end) = 1) as `temp(test)(4227881224)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4227881224)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS370() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`num0` else `Calcs`.`num1` end) as `temp(test)(709594122)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(709594122)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS371() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`num0` > `Calcs`.`num1`) then `Calcs`.`date0` else `Calcs`.`date1` end) as `temp(test)(467266194)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(467266194)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS372() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`str0` > `Calcs`.`str1`) then `Calcs`.`str2` else `Calcs`.`str3` end) as `temp(test)(2963734906)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2963734906)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS373() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`num0` > `Calcs`.`num1`) then 1	when not (`Calcs`.`num0` > `Calcs`.`num1`) then 0	else null end) when 1 then `Calcs`.`num0` when 0 then `Calcs`.`num1` else `Calcs`.`num2` end) as `temp(test)(4143049742)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4143049742)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS374() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`num0` > `Calcs`.`num1`) then 1	when not (`Calcs`.`num0` > `Calcs`.`num1`) then 0	else null end) when 1 then `Calcs`.`date0` when 0 then `Calcs`.`date1` else `Calcs`.`date2` end) as `temp(test)(1171954805)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1171954805)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS375() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`num0` > `Calcs`.`num1`) then 1	when not (`Calcs`.`num0` > `Calcs`.`num1`) then 0	else null end) when 1 then `Calcs`.`str2` when 0 then `Calcs`.`str3` else `Calcs`.`str0` end) as `temp(test)(2451799140)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2451799140)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS376() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`bool0_` <> 0) then 1	when not (`Calcs`.`bool0_` <> 0) then 0	else null end) when 1 then `Calcs`.`num0` when 0 then `Calcs`.`num1` else `Calcs`.`num2` end) as `temp(test)(1574830296)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1574830296)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS377() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`bool0_` <> 0) then 1	when not (`Calcs`.`bool0_` <> 0) then 0	else null end) when 1 then `Calcs`.`date0` when 0 then `Calcs`.`date1` else `Calcs`.`date2` end) as `temp(test)(49931887)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(49931887)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS378() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (dayofweek(`Calcs`.`date1`) in (1, 7)) then null else `Calcs`.`date1` end) as `temp(test)(1471931871)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1471931871)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS379() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case (case	when (`Calcs`.`bool0_` <> 0) then 1	when not (`Calcs`.`bool0_` <> 0) then 0	else null end) when 1 then `Calcs`.`str2` when 0 then `Calcs`.`str3` else `Calcs`.`str0` end) as `temp(test)(166894492)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(166894492)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS380() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case when (`Calcs`.`str1` = 'clocks') then '*anonymous*' when (`Calcs`.`str1` = 'dvd') then '*public*' else `Calcs`.`str1` end) as `temp(test)(899461877)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(899461877)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS381() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`date2`) - (dayofweek(`Calcs`.`date2`) - 1) ), interval 0 second ) as `temp(test)(1630131013)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1630131013)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS382() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(`Calcs`.`datetime0`) - (dayofweek(`Calcs`.`datetime0`) - 1) ), interval 0 second ) as `temp(test)(3937478358)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3937478358)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS383() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(`Calcs`.`datetime0`) as `temp(test)(1235924899)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1235924899)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS384() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 second) as `temp(test)(621896091)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(621896091)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS385() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24*60*60 + (time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) - time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)))) as `temp(test)(3711433751)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3711433751)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS386() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`date2`) - year(`Calcs`.`date3`)) as `temp(test)(427588088)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(427588088)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS387() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`date2`) - year(`Calcs`.`date3`)) as `temp(test)(2526313076)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2526313076)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS388() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`))) as `temp(test)(1540391660)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1540391660)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS389() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`))) as `temp(test)(3579576882)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3579576882)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS390() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(`Calcs`.`datetime0`) as `temp(test)(2740605400)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2740605400)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS391() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(`Calcs`.`datetime0`) as `temp(test)(356589430)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(356589430)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS392() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 3600) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 3600)) as `temp(test)(1898404202)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1898404202)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS393() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`)))*24 + floor(time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / 3600) - floor(time_to_sec(adddate(timestamp(`Calcs`.`date2`), interval 0 second)) / 3600)) as `temp(test)(4263325709)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4263325709)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS394() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select abs(`Calcs`.`num0`) as `temp(test)(3816473022)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3816473022)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS395() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num0` as `temp(test)(965512284)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(965512284)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS396() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select `Calcs`.`num1` as `temp(test)(1826927073)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1826927073)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS397() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((`Calcs`.`bool0_` <> 0) and (`Calcs`.`bool1_` <> 0)) as `temp(test)(3618731173)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3618731173)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS398() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((`Calcs`.`bool0_` <> 0) and (`Calcs`.`bool1_` <> 0) or not (`Calcs`.`bool0_` <> 0) and not (`Calcs`.`bool1_` <> 0)) as `temp(test)(830571724)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(830571724)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS399() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((`Calcs`.`bool0_` <> 0) and not (`Calcs`.`bool1_` <> 0) or not (`Calcs`.`bool0_` <> 0) and (`Calcs`.`bool1_` <> 0)) as `temp(test)(3090944671)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3090944671)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS400() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((`Calcs`.`bool0_` <> 0) or (`Calcs`.`bool1_` <> 0)) as `temp(test)(4182992858)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4182992858)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS401() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` = date('1972-07-04')) as `temp(test)(397499995)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(397499995)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS402() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` >= date('1975-11-12')) as `temp(test)(1366787273)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1366787273)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS403() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` > date('1975-11-12')) as `temp(test)(3193322782)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3193322782)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS404() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` <= date('1975-11-12')) as `temp(test)(822657216)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(822657216)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS405() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` < date('1975-11-12')) as `temp(test)(3764753091)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3764753091)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS406() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`date0`) - to_days(`Calcs`.`datetime0`)) + (time_to_sec(adddate(`Calcs`.`date0`, interval 0 second)) - time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second))) / (60 * 60 * 24)) as `temp(test)(937166222)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(937166222)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS407() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(date('2004-01-01'))) + (time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) - time_to_sec(adddate(date('2004-01-01'), interval 0 second))) / (60 * 60 * 24)) as `temp(test)(100938644)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(100938644)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS408() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "select date_sub(date_sub(`Calcs`.`date0`, interval floor(`Calcs`.`num4`) day), interval 60 * 60 * 24 * (`Calcs`.`num4` - floor(`Calcs`.`num4`)) second) as `temp(test)(2923065813)(0)` from `Calcs` group by 1 order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2923065813)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS409() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date0` <> date('1975-11-12')) as `temp(test)(798936259)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(798936259)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS410() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(`Calcs`.`date0`, interval floor(`Calcs`.`num4`) day), interval 60 * 60 * 24 * (`Calcs`.`num4` - floor(`Calcs`.`num4`)) second) as `temp(test)(2067341949)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2067341949)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS411() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 0 as `temp(test)(1303362598)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1303362598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS412() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int0` % `Calcs`.`int1`) as `temp(test)(1307456344)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1307456344)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS413() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int0` / `Calcs`.`int1`) as `temp(test)(2402101080)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2402101080)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS414() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int3` / `Calcs`.`int2`) as `temp(test)(3559262472)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3559262472)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS415() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(`Calcs`.`int0`,`Calcs`.`num1`) as `temp(test)(4265403921)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4265403921)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS416() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (-`Calcs`.`num0`) as `temp(test)(4188722171)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4188722171)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS417() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`bool0_` = 0) as `temp(test)(1413132553)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1413132553)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS418() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select null as `temp(test)(496893948)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(496893948)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS419() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` / `Calcs`.`num1`) as `temp(test)(272703322)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(272703322)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS420() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` = abs(`Calcs`.`num0`)) as `temp(test)(3360366790)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3360366790)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS421() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` = abs(`Calcs`.`num0`)) as `temp(test)(2564078271)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2564078271)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS422() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` >= `Calcs`.`num1`) as `temp(test)(1366300770)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1366300770)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS423() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` > `Calcs`.`num1`) as `temp(test)(4123004830)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4123004830)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS424() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` <= `Calcs`.`num1`) as `temp(test)(1224631717)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1224631717)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS425() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` < `Calcs`.`num1`) as `temp(test)(1731699042)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1731699042)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS426() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` - `Calcs`.`num1`) as `temp(test)(3781247900)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3781247900)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS427() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` <> abs(`Calcs`.`num0`)) as `temp(test)(4047276454)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4047276454)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS428() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` <> abs(`Calcs`.`num0`)) as `temp(test)(3492695719)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3492695719)(0)", rsmd.getColumnLabel(1));
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
                        "      select (`Calcs`.`num0` + `Calcs`.`num1`) as `temp(test)(977554451)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(977554451)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS431() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(`Calcs`.`num0`,`Calcs`.`num1`) as `temp(test)(637953353)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
    }

    @Test
    public void testCALCS432() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`num0` * `Calcs`.`num1`) as `temp(test)(1861245368)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1861245368)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS433() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` = (case when (`Calcs`.`num3` > 0) then `Calcs`.`str2` when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(1635792874)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1635792874)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS434() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` >= (case when (`Calcs`.`num3` > 0) then lower(`Calcs`.`str0`) when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(1555382477)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1555382477)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS435() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` > (case when (`Calcs`.`num3` > 0) then `Calcs`.`str0` when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(3821822049)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3821822049)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS436() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` <= (case when (`Calcs`.`num3` > 0) then lower(`Calcs`.`str0`) when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(2776534421)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2776534421)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS437() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` < (case when (`Calcs`.`num3` > 0) then lower(`Calcs`.`str0`) when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(398649381)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(398649381)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS438() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`str2` <> (case when (`Calcs`.`num3` > 0) then `Calcs`.`str2` when not (`Calcs`.`num3` > 0) then `Calcs`.`str3` else null end)) as `temp(test)(119026413)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(119026413)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS439() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(`Calcs`.`str2`, `Calcs`.`str3`) as `temp(test)(724155660)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(724155660)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS440() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 'pat o''hanrahan & <matthew eldridge]''' as `temp(test)(627207302)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(627207302)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS441() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 1 as `temp(test)(1507734681)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1507734681)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS442() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ' tail trimmed     ' as `temp(test)(1321171487)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1321171487)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS443() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*12 + (month(`Calcs`.`date2`) - month(`Calcs`.`date3`))) as `temp(test)(2958462977)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2958462977)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS444() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*12 + (month(`Calcs`.`date2`) - month(`Calcs`.`date3`))) as `temp(test)(667124691)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(667124691)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS445() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*12 + (month(`Calcs`.`datetime0`) - month(timestamp(`Calcs`.`date2`)))) as `temp(test)(2463700949)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2463700949)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS446() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*12 + (month(`Calcs`.`datetime0`) - month(timestamp(`Calcs`.`date2`)))) as `temp(test)(3778274693)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3778274693)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS447() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select atan(`Calcs`.`int2`) as `temp(test)(3655856496)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3655856496)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS448() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select atan(`Calcs`.`num0`) as `temp(test)(4053915117)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4053915117)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS449() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select atan2(`Calcs`.`int2`,1) as `temp(test)(2745915023)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2745915023)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS450() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select atan2(`Calcs`.`num0`,`Calcs`.`num1`) as `temp(test)(3341395046)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3341395046)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS451() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(truncate(`Calcs`.`num4`,0)) as `temp(test)(1425036653)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1425036653)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS452() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select timestamp(`Calcs`.`date2`) as `temp(test)(1486024523)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1486024523)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS453() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - 693961.0 + time_to_sec(adddate(`Calcs`.`date2`, interval 0 second)) / (24.0 * 60.0 * 60.0) ) as `temp(test)(2671902822)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2671902822)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS454() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select trim(date_format(`Calcs`.`date2`, '%b %e %y %l:%i%p')) as `temp(test)(3929621149)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3929621149)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS455() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round((to_days(`Calcs`.`datetime0`) - 693961.0 + time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) / (24.0 * 60.0 * 60.0) ),2) as `temp(test)(102700322)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(102700322)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS456() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select trim(date_format(`Calcs`.`datetime0`, '%b %e %y %l:%i%p')) as `temp(test)(1103404331)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1103404331)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS457() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(296025979)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(296025979)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS458() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(595744937)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(595744937)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS459() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(3907469988)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3907469988)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS460() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(1153873435)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1153873435)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS461() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval 1 year) as `temp(test)(858668231)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(858668231)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS462() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 year) as `temp(test)(1314023193)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1314023193)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS463() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(3529528921)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3529528921)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS464() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(1066073186)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1066073186)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS465() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961))) as `temp(test)(1128710711)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1128710711)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS466() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(3816818712)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3816818712)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS467() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961)))*4 + (quarter(from_days(floor(null) + 693961)) - quarter(from_days(floor(null) + 693961)))) as `temp(test)(1220694026)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1220694026)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS468() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*4 + (quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))) as `temp(test)(1878304808)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1878304808)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS469() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(from_days(floor(null) + 693961)) - year(from_days(floor(null) + 693961)))*12 + (month(from_days(floor(null) + 693961)) - month(from_days(floor(null) + 693961)))) as `temp(test)(3201398499)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3201398499)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS470() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*12 + (month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))) as `temp(test)(2380792894)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2380792894)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS471() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1)) - (to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1) ) )/7) as `temp(test)(1799303116)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1799303116)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS472() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1)) - (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1) ) )/7) as `temp(test)(3424623419)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3424623419)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS473() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(496128354)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(496128354)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS474() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(260207547)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(260207547)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS475() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(4282303505)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4282303505)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS476() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(2339877044)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2339877044)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS477() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(from_days(floor(null) + 693961)) - to_days(from_days(floor(null) + 693961))) as `temp(test)(3465754358)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3465754358)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS478() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))) as `temp(test)(2205674587)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2205674587)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS479() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24 + floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 3600) - floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 3600)) as `temp(test)(4062119106)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4062119106)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS480() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24*60 + floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 60) - floor(time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) / 60)) as `temp(test)(2509274079)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2509274079)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS481() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)))*24*60*60 + (time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)) - time_to_sec(adddate(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 0 second)))) as `temp(test)(508245917)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(508245917)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS482() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval 1 year) as `temp(test)(1053114602)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1053114602)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS483() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 year) as `temp(test)(955333125)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(955333125)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS484() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval (3 * 1) month) as `temp(test)(2396988690)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2396988690)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS485() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval (3 * 1) month) as `temp(test)(2232502461)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2232502461)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS486() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval 1 month) as `temp(test)(109946472)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(109946472)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS487() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 month) as `temp(test)(2095510626)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2095510626)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS488() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval (7 * 1) day) as `temp(test)(359186020)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(359186020)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS489() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval (7 * 1) day) as `temp(test)(3060670302)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3060670302)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS490() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval 1 day) as `temp(test)(592740370)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(592740370)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS491() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 day) as `temp(test)(4169571243)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4169571243)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS492() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval 1 day) as `temp(test)(2477057371)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2477057371)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS493() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 day) as `temp(test)(3817976182)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3817976182)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS494() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(from_days(floor(null) + 693961), interval 1 day) as `temp(test)(2329360898)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2329360898)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS495() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 day) as `temp(test)(1469842605)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1469842605)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS496() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 hour) as `temp(test)(4189387493)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4189387493)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS497() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 minute) as `temp(test)(3720439076)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3720439076)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS498() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), interval 1 second) as `temp(test)(2985757783)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2985757783)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS499() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select rtrim(concat(concat(' ', `Calcs`.`str2`), ' ')) as `temp(test)(2277366246)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2277366246)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS500() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(now()) - to_days(now())) as `temp(test)(3926981592)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3926981592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS501() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(curdate()) - to_days(curdate())) as `temp(test)(1915846221)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1915846221)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS502() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date2`)) + (time_to_sec(adddate(`Calcs`.`date2`, interval 0 second)) - time_to_sec(adddate(`Calcs`.`date2`, interval 0 second))) / (60 * 60 * 24)) as `temp(test)(1152843842)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1152843842)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS503() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(`Calcs`.`date2`, interval floor(1) day), interval 60 * 60 * 24 * (1 - floor(1)) second) as `temp(test)(715809068)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(715809068)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS504() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(`Calcs`.`date2`, interval floor(1.5) day), interval 60 * 60 * 24 * (1.5 - floor(1.5)) second) as `temp(test)(299505631)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(299505631)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS505() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_sub(date_sub(`Calcs`.`date2`, interval floor(1) day), interval 60 * 60 * 24 * (1 - floor(1)) second) as `temp(test)(709470143)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(709470143)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS506() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_sub(date_sub(`Calcs`.`date2`, interval floor(1.5) day), interval 60 * 60 * 24 * (1.5 - floor(1.5)) second) as `temp(test)(1620718980)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1620718980)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS507() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((to_days(`Calcs`.`datetime0`) - to_days(`Calcs`.`datetime0`)) + (time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second)) - time_to_sec(adddate(`Calcs`.`datetime0`, interval 0 second))) / (60 * 60 * 24)) as `temp(test)(2141740056)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2141740056)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS508() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_sub(date_sub(`Calcs`.`datetime0`, interval floor(1) day), interval 60 * 60 * 24 * (1 - floor(1)) second) as `temp(test)(1797652325)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1797652325)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS509() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(`Calcs`.`datetime0`, interval floor(1) day), interval 60 * 60 * 24 * (1 - floor(1)) second) as `temp(test)(2686481578)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2686481578)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS510() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_sub(date_sub(`Calcs`.`datetime0`, interval floor(1.5) day), interval 60 * 60 * 24 * (1.5 - floor(1.5)) second) as `temp(test)(2341796372)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2341796372)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS511() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(date_add(`Calcs`.`datetime0`, interval floor(1.5) day), interval 60 * 60 * 24 * (1.5 - floor(1.5)) second) as `temp(test)(4017290474)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4017290474)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS512() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` = `Calcs`.`datetime0`) as `temp(test)(3033382267)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3033382267)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS513() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` > `Calcs`.`datetime0`) as `temp(test)(4196472080)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4196472080)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS514() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` >= `Calcs`.`datetime0`) as `temp(test)(1829388090)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1829388090)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS515() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` < `Calcs`.`datetime0`) as `temp(test)(2087345109)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2087345109)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS516() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` <= `Calcs`.`datetime0`) as `temp(test)(3187080314)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3187080314)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS517() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`datetime0` <> `Calcs`.`datetime0`) as `temp(test)(436529008)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(436529008)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS518() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` = `Calcs`.`datetime0`) as `temp(test)(1122166960)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1122166960)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS519() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` > `Calcs`.`datetime0`) as `temp(test)(2476649334)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2476649334)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS520() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` >= `Calcs`.`datetime0`) as `temp(test)(1267352367)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1267352367)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS521() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` < `Calcs`.`datetime0`) as `temp(test)(668774393)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(668774393)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS522() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` <= `Calcs`.`datetime0`) as `temp(test)(2801366337)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2801366337)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS523() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` <> `Calcs`.`datetime0`) as `temp(test)(6065346)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(6065346)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS524() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` = `Calcs`.`date2`) as `temp(test)(4213376628)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4213376628)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS525() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` > `Calcs`.`date2`) as `temp(test)(284925583)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(284925583)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS526() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` >= `Calcs`.`date2`) as `temp(test)(1365124261)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1365124261)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS527() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` < `Calcs`.`date2`) as `temp(test)(4277161941)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4277161941)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS528() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` <= `Calcs`.`date2`) as `temp(test)(932571096)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(932571096)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS529() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`date2` <> `Calcs`.`date2`) as `temp(test)(3666462064)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3666462064)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS530() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(402015915)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(402015915)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS531() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3033426574)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3033426574)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS532() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`int0`) as `temp(test)(4016644369)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4016644369)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS533() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`int1`) then null	when isnull(`Calcs`.`int2`) then null	else least(`Calcs`.`int1`, `Calcs`.`int2`) end) as `temp(test)(1701645592)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1701645592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS534() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(433583207)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(433583207)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS535() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(1289371916)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1289371916)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS536() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(3917841362)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3917841362)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS537() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(1921815362)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1921815362)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS538() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ltrim(concat(concat(' ', `Calcs`.`str2`), ' ')) as `temp(test)(1106979036)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1106979036)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS539() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select cos(`Calcs`.`int2`) as `temp(test)(344207442)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(344207442)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS540() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select cos(`Calcs`.`num0`) as `temp(test)(1355320598)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1355320598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS541() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(from_days(floor(null) + 693961)) as `temp(test)(513464674)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(513464674)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS542() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3512378422)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3512378422)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS543() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3084524178)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3084524178)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS544() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(from_days(floor(null) + 693961)) as `temp(test)(4202902840)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4202902840)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS545() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2836269094)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2836269094)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS546() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(from_days(floor(null) + 693961)) as `temp(test)(3924648662)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3924648662)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS547() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(from_days(floor(null) + 693961)) - 1 + dayofweek(date_format(from_days(floor(null) + 693961), '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(1538264184)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1538264184)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS548() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((7 + dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1 + dayofweek(date_format(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00')) - 1) / 7) as `temp(test)(4042104093)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4042104093)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS549() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(from_days(floor(null) + 693961)) as `temp(test)(4271712345)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4271712345)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS550() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(963247111)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(963247111)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS551() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(738426766)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(738426766)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS552() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(from_days(floor(null) + 693961)) as `temp(test)(1202522493)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1202522493)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS553() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1255819744)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1255819744)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS554() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(from_days(floor(null) + 693961)) as `temp(test)(1639804515)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1639804515)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS555() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(299943486)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(299943486)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS556() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(4177149407)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4177149407)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS557() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1457324017)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1457324017)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS558() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select asin((`Calcs`.`num0` / 20)) as `temp(test)(1317198372)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1317198372)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS559() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(distinct `Calcs`.`int0`) as `temp(test)(1467453495)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1467453495)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS560() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(distinct (`Calcs`.`bool0_` <> 0)) as `temp(test)(1408008556)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1408008556)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS561() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(distinct `Calcs`.`date3`) as `temp(test)(175600811)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(175600811)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS562() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(distinct `Calcs`.`num4`) as `temp(test)(41874160)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(41874160)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS563() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select count(distinct `Calcs`.`str2`) as `temp(test)(2954817995)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2954817995)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS564() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3715775174)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3715775174)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS565() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2815480624)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2815480624)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS566() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3738830082)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3738830082)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS567() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(151653785)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(151653785)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS568() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(1373895161)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1373895161)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS569() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(543203842)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(543203842)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS570() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(3325657342)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3325657342)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS571() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(`Calcs`.`int1`) as `temp(test)(2617331766)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2617331766)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS572() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when (`Calcs`.`bool0_` <> 0) then '1'	when not (`Calcs`.`bool0_` <> 0) then '0'	else null end) as `temp(test)(3200082645)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3200082645)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS573() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(`Calcs`.`num2`) as `temp(test)(3049448927)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3049448927)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS574() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select substring(`Calcs`.`str2`, 1, 1024) as `temp(test)(3494867617)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3494867617)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS575() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`date2`) as `temp(test)(1660803953)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1660803953)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS576() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`date2`) as `temp(test)(872696424)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(872696424)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS577() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`datetime0`) as `temp(test)(732183378)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(732183378)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS578() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(`Calcs`.`datetime0`) as `temp(test)(3816689092)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3816689092)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS579() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`date2`) as `temp(test)(2634030884)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2634030884)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS580() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`datetime0`) as `temp(test)(4000895377)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4000895377)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS581() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval 1 month) as `temp(test)(2799254343)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2799254343)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS582() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 month) as `temp(test)(1378354598)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1378354598)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS583() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(from_days(floor(null) + 693961)) as `temp(test)(3057229987)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3057229987)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS584() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(4063654893)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4063654893)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS585() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(from_days(floor(null) + 693961)) as `temp(test)(2102858309)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2102858309)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS586() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3270121971)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3270121971)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS587() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(from_days(floor(null) + 693961)) as `temp(test)(2692233594)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2692233594)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS588() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select monthname(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(1772891037)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1772891037)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS589() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(from_days(floor(null) + 693961)) - 1 + dayofweek(date_format(from_days(floor(null) + 693961), '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(3926284460)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3926284460)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS590() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select concat(floor((7 + dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1 + dayofweek(date_format(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00')) - 1) / 7)) as `temp(test)(1415178918)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1415178918)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS591() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(from_days(floor(null) + 693961)) as `temp(test)(3608467423)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3608467423)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS592() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayname(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2920782836)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2920782836)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS593() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(from_days(floor(null) + 693961)) as `temp(test)(3132873078)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3132873078)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS594() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofmonth(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2450943592)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2450943592)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS595() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(from_days(floor(null) + 693961)) as `temp(test)(3530921297)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3530921297)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS596() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(304383277)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(304383277)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS597() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3871589708)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3871589708)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS598() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(2462406212)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2462406212)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS599() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select second(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) as `temp(test)(3443263072)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3443263072)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS600() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:00' ), interval 0 second ) as `temp(test)(1349416314)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1349416314)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS601() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d %h:%i:00' ), interval 0 second ) as `temp(test)(3032747293)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3032747293)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS602() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval (7 * 1) day) as `temp(test)(2748179160)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2748179160)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS603() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval (7 * 1) day) as `temp(test)(3880453047)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3880453047)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS604() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`date2`) as `temp(test)(877816921)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(877816921)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS605() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select dayofyear(`Calcs`.`datetime0`) as `temp(test)(707037378)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(707037378)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS606() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*4 + (quarter(`Calcs`.`date2`) - quarter(`Calcs`.`date3`))) as `temp(test)(3028875325)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3028875325)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS607() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`date2`) - year(`Calcs`.`date3`))*4 + (quarter(`Calcs`.`date2`) - quarter(`Calcs`.`date3`))) as `temp(test)(3483942593)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3483942593)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS608() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*4 + (quarter(`Calcs`.`datetime0`) - quarter(timestamp(`Calcs`.`date2`)))) as `temp(test)(4196684004)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4196684004)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS609() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select ((year(`Calcs`.`datetime0`) - year(timestamp(`Calcs`.`date2`)))*4 + (quarter(`Calcs`.`datetime0`) - quarter(timestamp(`Calcs`.`date2`)))) as `temp(test)(351668681)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(351668681)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS610() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`date2`, interval 1 day) as `temp(test)(1139290352)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1139290352)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS611() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 day) as `temp(test)(748109579)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(748109579)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS612() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`date2`) - (dayofweek(`Calcs`.`date2`) - 1)) - (to_days(`Calcs`.`date3`) - (dayofweek(`Calcs`.`date3`) - 1) ) )/7) as `temp(test)(859582235)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(859582235)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS613() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`datetime0`) - (dayofweek(`Calcs`.`datetime0`) - 1)) - (to_days(timestamp(`Calcs`.`date2`)) - (dayofweek(timestamp(`Calcs`.`date2`)) - 1) ) )/7) as `temp(test)(2079052241)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2079052241)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS614() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select abs(`Calcs`.`int2`) as `temp(test)(2102582873)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2102582873)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS615() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select abs(`Calcs`.`num0`) as `temp(test)(3816473022)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3816473022)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS616() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(1942031084)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1942031084)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS617() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`date2`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(308042462)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(308042462)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS618() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(1290354772)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1290354772)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS619() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( `Calcs`.`datetime0`, '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2022110629)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2022110629)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS620() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select if(isnull(((`Calcs`.`num0` + 5) * 0.29999999999999999)), null, substring(`Calcs`.`str2`,greatest(1,floor(((`Calcs`.`num0` + 5) * 0.29999999999999999))),floor(`Calcs`.`num1`))) as `temp(test)(1934432200)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1934432200)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS621() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select minute(`Calcs`.`datetime0`) as `temp(test)(1256004566)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1256004566)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS622() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (((case when (abs((2) - (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0)))) + sqrt(3.0) * ((abs((`Calcs`.`int2`) - (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 then 1.5 else 0.0 end) - (case when ((`Calcs`.`int2`) - (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0) < 0.0) and ((case when (abs((2) - (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0)))) + sqrt(3.0) * ((abs((`Calcs`.`int2`) - (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 then sqrt(3.0) / 2.0 else 0.0 end) > 0.0) then 3.0 else 0.0 end)) + (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0)) as `temp(test)(2503102272)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2503102272)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS623() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round( (((case when (abs((2) - (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0)))) + sqrt(3.0) * ((abs((`Calcs`.`int2`) - (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 then sqrt(3.0) / 2.0 else 0.0 end) - (case when ((2) - (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0)) < 0.0) and ((case when (abs((2) - (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0)))) + sqrt(3.0) * ((abs((`Calcs`.`int2`) - (round( ( (`Calcs`.`int2`) / 3.0 ), 0 ) * 3.0))) - 1.0) > 0.0 then sqrt(3.0) / 2.0 else 0.0 end) > 0.0) then sqrt(3.0) else 0.0 end)) + (round( ( (2) / sqrt(3.0) ), 0 ) * sqrt(3.0))), 3) as `temp(test)(2977666156)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2977666156)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS624() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select timestamp(str_to_date('1234-06-01', '%y-%m-%d')) as `temp(test)(1408155083)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1408155083)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS625() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select timestamp(str_to_date('12-06-01', '%y-%m-%d')) as `temp(test)(54082523)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(54082523)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS626() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select timestamp(str_to_date('1234-06-01', '%y-%m-%d')) as `temp(test)(2040050501)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2040050501)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS627() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select log10(`Calcs`.`int2`) as `temp(test)(114283928)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(114283928)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS628() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (log(`Calcs`.`int2`)/log(2)) as `temp(test)(3322085183)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3322085183)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS629() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select log10(`Calcs`.`num0`) as `temp(test)(1814892178)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1814892178)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS630() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (log(`Calcs`.`num0`)/log(2)) as `temp(test)(3081102343)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3081102343)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS631() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select 1 as `temp(test)(3252316215)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3252316215)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS632() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (left(`Calcs`.`str1`, length('bi')) = 'bi') as `temp(test)(535799381)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(535799381)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS633() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (left(`Calcs`.`str1`, length(`Calcs`.`str2`)) = `Calcs`.`str2`) as `temp(test)(2377293421)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2377293421)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS634() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`date2`) as `temp(test)(3325074545)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3325074545)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS635() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`date2`) then null	when isnull(`Calcs`.`date3`) then null	else greatest(`Calcs`.`date2`, `Calcs`.`date3`) end) as `temp(test)(1996265231)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1996265231)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS636() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`datetime0`) as `temp(test)(4035984656)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4035984656)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS637() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(840463993)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(840463993)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS638() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(1720545932)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1720545932)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS639() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(840463993)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(840463993)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS640() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(1720545932)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1720545932)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS641() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(2707942807)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2707942807)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS642() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(3474280307)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3474280307)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS643() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(2707942807)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2707942807)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS644() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(3474280307)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3474280307)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS645() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`date2`) as `temp(test)(1671202742)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1671202742)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS646() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`date2`) as `temp(test)(536615588)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(536615588)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS647() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`datetime0`) as `temp(test)(1933085624)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1933085624)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS648() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select month(`Calcs`.`datetime0`) as `temp(test)(2986113344)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2986113344)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS649() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int2` div 2) as `temp(test)(266359676)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(266359676)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS650() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int0` div `Calcs`.`int1`) as `temp(test)(2600727600)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2600727600)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS651() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int0` div `Calcs`.`int1`) as `temp(test)(2600727600)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2600727600)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS652() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int3` div `Calcs`.`int2`) as `temp(test)(3955107424)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3955107424)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS653() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (`Calcs`.`int3` div `Calcs`.`int2`) as `temp(test)(3955107424)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3955107424)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS654() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sqrt(`Calcs`.`int2`) as `temp(test)(2398974448)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2398974448)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS655() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select sqrt(`Calcs`.`num0`) as `temp(test)(634651992)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(634651992)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS656() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(`Calcs`.`int2`, 2) as `temp(test)(3898674109)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3898674109)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS657() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select power(`Calcs`.`num0`, 2) as `temp(test)(1119897860)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1119897860)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS658() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(3311335472)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3311335472)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS659() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-01-01 00:00:00' ), interval 0 second ) as `temp(test)(1982106892)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1982106892)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS660() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( from_days(floor(null) + 693961), '%y-' ), (3*(quarter(from_days(floor(null) + 693961))-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(2616948526)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2616948526)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS661() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( concat( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-' ), (3*(quarter(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second))-1)+1), '-01 00:00:00' ), interval 0 second ) as `temp(test)(4099405891)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4099405891)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS662() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(1303420554)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1303420554)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS663() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-01 00:00:00' ), interval 0 second ) as `temp(test)(1705284026)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1705284026)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS664() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - (dayofweek(date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second)) - 1) ), interval 0 second ) as `temp(test)(2964540366)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2964540366)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS665() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate(from_days( to_days(from_days(floor(null) + 693961)) - (dayofweek(from_days(floor(null) + 693961)) - 1) ), interval 0 second ) as `temp(test)(3523871008)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3523871008)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS666() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3587526928)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3587526928)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS667() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(2715649251)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2715649251)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS668() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(3912893816)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3912893816)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS669() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(453060606)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(453060606)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS670() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( from_days(floor(null) + 693961), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(1466575961)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1466575961)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS671() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d 00:00:00' ), interval 0 second ) as `temp(test)(265878863)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(265878863)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS672() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:00:00' ), interval 0 second ) as `temp(test)(3877847632)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3877847632)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS673() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:%i:00' ), interval 0 second ) as `temp(test)(263614731)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(263614731)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS674() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select adddate( date_format( date_add(from_days(floor(null) + 693961), interval 60 * 60 * 24 * (null - floor(null)) second), '%y-%m-%d %h:%i:%s' ), interval 0 second ) as `temp(test)(864002214)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(864002214)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS675() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select avg(`Calcs`.`int0`) as `temp(test)(3952218057)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3952218057)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS676() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select avg(`Calcs`.`num4`) as `temp(test)(1371989636)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1371989636)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS677() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select hour(`Calcs`.`datetime0`) as `temp(test)(3233853797)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3233853797)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS678() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(1876737518)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1876737518)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS679() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(1437280163)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1437280163)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS680() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(3178513645)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3178513645)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS681() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(3727444777)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3727444777)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS682() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select date_add(`Calcs`.`datetime0`, interval 1 hour) as `temp(test)(4261466899)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4261466899)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS683() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`int0`) as `temp(test)(56370746)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(56370746)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS684() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`date3`) as `temp(test)(277748206)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(277748206)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS685() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`num4`) as `temp(test)(4154938655)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4154938655)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS686() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`str2`) as `temp(test)(1812249092)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1812249092)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS687() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`int0`) then null	when isnull(`Calcs`.`int1`) then null	else greatest(`Calcs`.`int0`, `Calcs`.`int1`) end) as `temp(test)(1523549003)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1523549003)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS688() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select locate('ee',`Calcs`.`str2`) as `temp(test)(3981629397)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3981629397)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS689() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select locate('e',`Calcs`.`str1`) as `temp(test)(257220821)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(257220821)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS690() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select if(isnull(6), null, locate('e',`Calcs`.`str1`,greatest(1,floor(6)))) as `temp(test)(282093116)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(282093116)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS691() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select locate(`Calcs`.`str3`,`Calcs`.`str2`) as `temp(test)(3096760581)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3096760581)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS692() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select if(isnull((`Calcs`.`num4` * 0.20000000000000001)), null, locate(`Calcs`.`str3`,`Calcs`.`str2`,greatest(1,floor((`Calcs`.`num4` * 0.20000000000000001))))) as `temp(test)(2787932066)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2787932066)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS693() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(838791689)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(838791689)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS694() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`date2`) - to_days(`Calcs`.`date3`)) as `temp(test)(1647283678)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1647283678)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS695() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(1719292105)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1719292105)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS696() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (to_days(`Calcs`.`datetime0`) - to_days(timestamp(`Calcs`.`date2`))) as `temp(test)(1567002572)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1567002572)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS697() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`int0`) as `temp(test)(4016644369)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4016644369)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS698() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`date3`) as `temp(test)(3378300904)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3378300904)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS699() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`num4`) as `temp(test)(512350875)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(512350875)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS700() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select min(`Calcs`.`str2`) as `temp(test)(3910790823)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3910790823)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS701() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`int0`) then null	when isnull(`Calcs`.`int1`) then null	else least(`Calcs`.`int0`, `Calcs`.`int1`) end) as `temp(test)(3683900016)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3683900016)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS702() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select max(`Calcs`.`int0`) as `temp(test)(56370746)(0)`from `Calcs`having (count(1) > 0)     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(56370746)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS703() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select (case	when isnull(`Calcs`.`int1`) then null	when isnull(`Calcs`.`int2`) then null	else greatest(`Calcs`.`int1`, `Calcs`.`int2`) end) as `temp(test)(2763474205)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2763474205)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS704() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(3969685894)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3969685894)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS705() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`date2`) as `temp(test)(3969685894)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3969685894)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS706() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(4179095987)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4179095987)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS707() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select year(`Calcs`.`datetime0`) as `temp(test)(4179095987)(1)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(4179095987)(1)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS708() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round(`Calcs`.`int2`) as `temp(test)(366741644)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(366741644)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS709() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round(`Calcs`.`int2`,2) as `temp(test)(1240237577)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1240237577)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS710() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round(`Calcs`.`num0`) as `temp(test)(3892529067)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3892529067)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS711() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select round(`Calcs`.`num4`,1) as `temp(test)(2722044748)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2722044748)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS712() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`date2`) - ((7 + dayofweek(`Calcs`.`date2`) - 2) % 7)) - (to_days(`Calcs`.`date3`) - ((7 + dayofweek(`Calcs`.`date3`) - 2) % 7) ) )/7) as `temp(test)(3550551924)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(3550551924)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS713() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`date2`) - (dayofweek(`Calcs`.`date2`) - 1)) - (to_days(`Calcs`.`date3`) - (dayofweek(`Calcs`.`date3`) - 1) ) )/7) as `temp(test)(2745903531)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(2745903531)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS714() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`datetime0`) - ((7 + dayofweek(`Calcs`.`datetime0`) - 2) % 7)) - (to_days(timestamp(`Calcs`.`date2`)) - ((7 + dayofweek(timestamp(`Calcs`.`date2`)) - 2) % 7) ) )/7) as `temp(test)(1341534691)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1341534691)(0)", rsmd.getColumnLabel(1));
    }

    @Test
    public void testCALCS715() throws SQLException {
        Connection conn = getBasicConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery(
                        "      select floor((( to_days(`Calcs`.`datetime0`) - (dayofweek(`Calcs`.`datetime0`) - 1)) - (to_days(timestamp(`Calcs`.`date2`)) - (dayofweek(timestamp(`Calcs`.`date2`)) - 1) ) )/7) as `temp(test)(1157868287)(0)`from `Calcs`group by 1     order by 1");
        ResultSetMetaData rsmd = rs.getMetaData();
        assertEquals("temp(test)(1157868287)(0)", rsmd.getColumnLabel(1));
    }
}
