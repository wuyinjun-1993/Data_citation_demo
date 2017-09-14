import java.sql.*;
import java.sql.DriverManager;



public class connect2db {
	
	public static String db_url = "jdbc:postgresql://localhost:5432/test";
	
	public static String usr_name = "wuyinjun";
	
	public static String passwd = "";
	
	public static void main(String args[]) {
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection(db_url,
	        	        usr_name, passwd);
	         
	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
//	         pst = c.prepareStatement("SELECT *  FROM view_table");
	         rs = pst.executeQuery();
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	   }

}
