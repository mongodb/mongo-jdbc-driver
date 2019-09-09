import java.sql.*;

public class Main {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mongodb.jdbc.MongoDriver";  
   static final String URL = "jdbc:mongodb://localhost/test";

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

            //Display values
            System.out.print("_id: " + id);
            System.out.print(", a: " + a);
            System.out.print(", b: " + b);
            System.out.println();
         }
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
   }
}
