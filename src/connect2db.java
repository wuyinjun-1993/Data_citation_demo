import java.sql.*;
import java.sql.DriverManager;




public class connect2db {
	public static void main(String args[]) {
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	            "wuyinjun","123");
	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
	         pst = c.prepareStatement("SELECT *  FROM view_table");
	         rs = pst.executeQuery();
	         int num=1;
	         
	            while (rs.next()) {
	                System.out.print(rs.getString(1));
	                System.out.print('|');
	                System.out.print(rs.getString(2));
	                if(rs.getString(2).length()==0)
	                {
	                	int y=0;
	                	y++;
	                }
	                System.out.print('|');
	                System.out.println(rs.getString(3));
	            }
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	   }

}
