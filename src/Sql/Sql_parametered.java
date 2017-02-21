package Sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import Pre_processing.populate_db;

public class Sql_parametered extends Sql{

	public String sql = new String();
	
	String name = new String();
	
	Vector<String> lambda_terms = new Vector<String>();
	
	public Sql_parametered(String name) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
		this.name = name;
		get_parameters_sql();
		
	}
	
	public Sql_parametered(String name, Vector<String> lambda_terms) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		
		this.lambda_terms = lambda_terms;
		
		get_sql();
		
	}
	
	void get_sql() throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    String lambda_terms_query = "select query from queries where query_id = '" + name + "'";
	    
	    pst = c.prepareStatement(lambda_terms_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
		    this.sql = rs.getString(1);
	    }
	}
	
	void get_parameters_sql() throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String lambda_terms_query = "select lambda_terms,query from queries where query_id = '" + name + "'";
	    
	    pst = c.prepareStatement(lambda_terms_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	lambda_terms.addAll(Arrays.asList(rs.getString(1).split(",")));
	    	this.sql = rs.getString(2);
	    }
	    
	    
	}
	
	@Override
	public String get_full_sql() {
		// TODO Auto-generated method stub
		return sql;
	}

}
