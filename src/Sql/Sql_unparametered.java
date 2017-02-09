package Sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Sql_unparametered extends Sql{

	public String name;
	
	public String sql;
	
	public Sql_unparametered(String name) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
		this.name = name;
		get_sql();
		
	}
	
	void get_sql() throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String lambda_terms_query = "select query from queries where query_id = '" + name + "'";
	    
	    pst = c.prepareStatement(lambda_terms_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
		    this.sql = rs.getString(1);
	    }
	}
	
	
	@Override
	public String get_full_sql() {
		// TODO Auto-generated method stub
		return sql;
	}
	
	

}
