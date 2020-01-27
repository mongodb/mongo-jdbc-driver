package com.mongodb.jdbc.demo;

import java.sql.*;
import java.math.BigDecimal;

import com.mongodb.jdbc.*;

import java.util.Properties;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";
   static final String URL = "jdbc:mongodb://localhost";

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

         System.out.println("Creating statement...");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("real queries don't work yet");
         System.out.println("++++++ Showing contents for test.test ++++++++");
         displayResultSet(rs);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }

   public static void displayResultSet(ResultSet rs) throws java.sql.SQLException {
       while(rs.next()){
          //Retrieve by column name
          int a = rs.getInt("a");
          String b = rs.getString("b");
          ResultSetMetaData metaData = rs.getMetaData();
		  System.out.println("a is: " + a + " with type " + metaData.getColumnType(1)
				  + "b is: " + b + "with type " + metaData.getColumnType(2));
       }
   }
}
