package com.mongodb.jdbc.demo;

import java.sql.*;

import java.math.BigDecimal;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";
   static final String URL = "jdbc:mongodb://localhost/test";

   // Data used for test, in the test.test and test2.test collections:
   //   > db.test.insert([
   //      { "a" : 1, "b" : 42 },
   //      { "a" : "hello", "b" : "world" },
   //      { "a" : "hello2", "b" : "world2" },
   //      { "a" : "hello", "b" : NumberLong(42) },
   //      { "a" : "hello", "b" : 42 },
   //      { "a" : "hello", "b" : NumberDecimal("3.1415926535") },
   //      { "a" : "hello", "b" : "1234" },
   //      { "a" : "hello", "b" : NumberLong("500000000000") },
   //      { "a" : "hello", "b" : true },
   //      { "a" : "hello", "b" : "true" },
   //      { "a" : "hello", "b" : null },
   //      { "a" : "hello", "b" : ISODate("2019-09-09T21:04:42.568Z") },
   //      { "a" : "hello", "b" : UUID("3b241101-e2bb-4255-8caf-4136c566a962") },
   //      { "a" : "hello", "b" : BinData(15, "aGVsbG9w") }
   //	  ])
   public static void main(String[] args) {
      try{
         Class.forName(JDBC_DRIVER);
         System.out.println("Connecting to database...");
         var conn = DriverManager.getConnection(URL, "fake_user", "fake_password");

         System.out.println("Creating statement...");
         var stmt = conn.createStatement();
         var rs = stmt.executeQuery("real queries don't work yet");
		 System.out.println("++++++ Showing contents for test.test ++++++++");
		 displayResultSet(rs);

		 conn.setCatalog("test2");
		 stmt = conn.createStatement();
		 rs = stmt.executeQuery("real queries still don't work yet");
		 System.out.println("++++++ Showing contents for test2.test ++++++++");
		 displayResultSet(rs);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }

   public static void displayResultSet(ResultSet rs) throws java.sql.SQLException {
       while(rs.next()){
          //Retrieve by column name
          String id  = rs.getString("_id");
          String a = rs.getString("a");
          String b = rs.getString("b");
          var metaData = rs.getMetaData();
          var btype = metaData.getColumnType(2);
          var btypeN = metaData.getColumnTypeName(2);
	      var btypeC = metaData.getColumnClassName(2);
          System.out.println("b type is: " + btype + " which is named: " + btypeN + " and has java class name: " + btypeC);
          try {
              int bi = rs.getInt("b");
              System.out.println("b was convertable to int: " + bi);
          } catch (Exception e) {
              System.out.println("b was not an int, b was: " + b);
          }

          try {
              long bl = rs.getLong("b");
              System.out.println("b was convertable to long: " + bl);
          } catch (Exception e) {
              System.out.println("b was not an long, b was: " + b);
          }

          try {
              double bd = rs.getDouble("b");
              System.out.println("b was convertable to double: " + bd);
          } catch (Exception e) {
              System.out.println("b was not an double, b was: " + b);
          }

          try {
              BigDecimal bbd = rs.getBigDecimal("b");
              System.out.println("b was convertable to BigDecimal: " + bbd);
          } catch (Exception e) {
              System.out.println("b was not an BigDecimal, b was: " + b);
          }

          try {
              boolean bb = rs.getBoolean("b");
              System.out.println("b was convertable to boolean: " + bb);
          } catch (Exception e) {
              System.out.println("b was not an boolean, b was: " + b);
          }

          //Display values
          System.out.print("_id: " + id);
          System.out.print(", a: " + a);
          System.out.println(", b: " + b);
          System.out.println("================");
       }
   }
}
