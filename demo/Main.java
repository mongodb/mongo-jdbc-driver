import java.sql.*;

import java.math.BigDecimal;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";
   static final String URL = "jdbc:mongodb://localhost/test";

   // Data used for test, in the test.test collection:
   //   > db.test.find()
   //   { "_id" : ObjectId("5d7666e1d8edf51950534f95"), "a" : 1, "b" : 42 }
   //   { "_id" : ObjectId("5d7666eed8edf51950534f96"), "a" : "hello", "b" : "world" }
   //   { "_id" : ObjectId("5d766af1082d543cd8613d05"), "a" : "hello2", "b" : "world2" }
   //   { "_id" : ObjectId("5d767ceaf7a2a6ec17925765"), "a" : "hello", "b" : NumberLong(42) }
   //   { "_id" : ObjectId("5d767ceef7a2a6ec17925766"), "a" : "hello", "b" : 42 }
   //   { "_id" : ObjectId("5d767cfdf7a2a6ec17925767"), "a" : "hello", "b" : NumberDecimal("3.1415926535") }
   //   { "_id" : ObjectId("5d7680d8b816fa2e7c8acf85"), "a" : "hello", "b" : "1234" }
   //   { "_id" : ObjectId("5d7682cb9c9da86c56289c45"), "a" : "hello", "b" : NumberLong("500000000000") }
   //   { "_id" : ObjectId("5d7682cb9c9da86c56289c45"), "a" : "hello", "b" : true }
   //   { "_id" : ObjectId("5d7682cb9c9da86c56289c45"), "a" : "hello", "b" : "true" }
   //   { "_id" : ObjectId("5d7682cb9c9da86c56289c45"), "a" : "hello", "b" : null }
   //   { "_id" : ObjectId("5d76be6ac2889451576bf28a"), "a" : "hello", "b" : ISODate("2019-09-09T21:04:42.568Z") }
   //   { "_id" : ObjectId("5d76be77c2889451576bf28b"), "a" : "hello", "b" : UUID("3b241101-e2bb-4255-8caf-4136c566a962") }
   //   { "_id" : ObjectId("5d76be77c2889451576bf28b"), "a" : "hello", "b" : BinData(15, "aGVsbG9w") }
   public static void main(String[] args) {
      try{
         Class.forName(JDBC_DRIVER);
         System.out.println("Connecting to database...");
         var conn = DriverManager.getConnection(URL, "fake_user", "fake_password");

         System.out.println("Creating statement...");
         var stmt = conn.createStatement();
         var rs = stmt.executeQuery("real queries don't work yet");

         while(rs.next()){
            //Retrieve by column name
            String id  = rs.getString("_id");
            String a = rs.getString("a");
            String b = rs.getString("b");
            var metaData = rs.getMetaData();
            var btype = metaData.getColumnType(2);
            var btypeN = metaData.getColumnTypeName(2);
            System.out.println("b type is: " + btype + " which is named: " + btypeN);
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
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }
}
