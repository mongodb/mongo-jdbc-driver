package com.mongodb.jdbc.demo;

import java.sql.*;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";
   static final String URL = "jdbc:mongodb://mhuser:pencil@localhost:27017/admin";
   private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

   // Data used for test, in the test.test and test2.test collections:
   //
   //{ "values" : [
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "a", "columnAlias" : "a", "value" : 1 },
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "b", "columnAlias" : "b", "value" : "hello" } ]
   //    }
   //{ "values" : [
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "a", "columnAlias" : "a", "value" : 42 },
   //    { "database" : "myDB", "table" : "foo", "tableAlias" : "foo", "column" : "b", "columnAlias" : "b", "value" : "hello 2" } ]
   //    }
   //
   public static void main(String[] args) {

      try{
         java.util.Properties p = new java.util.Properties();
         // These properties will be added to the URI.
         // Uncomment if you wish to specify user and password.
         // p.setProperty("user", "user");
         // p.setProperty("password", "foo");
         p.setProperty("database", "test");
         System.out.println("Connecting to database test...");
         Connection conn = DriverManager.getConnection(URL, p);

        DatabaseMetaData dbmd = conn.getMetaData();
        System.out.println(dbmd.getDriverVersion());
        System.out.println(dbmd.getDriverMajorVersion());
        System.out.println(dbmd.getDriverMinorVersion());
//         System.out.println("Creating statement...");
//         Statement stmt = conn.createStatement();
//         ResultSet rs = stmt.executeQuery("select * from foo");
//         System.out.println("++++++ Showing contents for test.foo ++++++++");
//         displayResultSet(rs);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }

   public static void displayResultSet(ResultSet rs) throws java.sql.SQLException {
	   Calendar c = new GregorianCalendar();
	   c.setTimeZone(UTC);
       while(rs.next()){
          //Retrieve by column name
          double a = rs.getDouble("a");
		  String as = rs.getString("a");
          String b = rs.getString("b");
		  java.sql.Timestamp bd;
		  try {
		  		bd = rs.getTimestamp("b", c);
				System.out.println("b as a Timestamp is: " + bd);
		  } catch (SQLException e) {
				System.out.println(e);
		  } catch (Exception e) {
				throw new RuntimeException(e);
		  }
          ResultSetMetaData metaData = rs.getMetaData();
		  System.out.println("a is: " + a + " as double"
				  + " b is: " + b + " as string");
		  System.out.println("a as a string is: " + as);
       }
   }
}
