import java.sql.*;

import com.mongodb.jdbc.MongoConnection;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";  

   public static void main(String[] args) {
      try{
         System.out.println("Connecting to database...");
         var conn = new MongoConnection("");

         System.out.println("Creating statement...");
         var stmt = conn.createStatement();
         var rs = stmt.executeQuery("real queries don't work yet");

         while(rs.next()){
            //Retrieve by column name
            String id  = rs.getString("_id");
            String a = rs.getString("a");
            String b = rs.getString("b");

            //Display values
            System.out.print("_id: " + id);
            System.out.print(", a: " + a);
            System.out.print(", b: " + b);
            System.out.println();
         }
      } catch (Exception e) {
       	System.out.println("Got exception :" + e);
      }
   }
}
