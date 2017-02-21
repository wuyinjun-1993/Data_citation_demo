package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class delete_view {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    String view_name = "v7";
	    
//	    rename_view("v4", c, pst, "v2");
	    
	    delete_view(view_name, c, pst);
	    
	    c.close();
	}
	
	public static void delete_view(String view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String []table_names = {"view2conditions", "view2lambda_term", "view2subgoals", "citation_view", "view_table"};
		
		String check_exist = "select exists (select 1 from view_table where view = '"+ view_name+"')";
		
		pst = c.prepareStatement(check_exist);
		
		ResultSet rs = pst.executeQuery();
		
		boolean exists = false;
		
		if(rs.next())
		{
			exists = rs.getBoolean(1);
		}
		
		if(exists)
		{
			String del_citation_query = "select citation_view_name from citation_view where view = '" + view_name + "'";
			
			pst = c.prepareStatement(del_citation_query);
			
			rs = pst.executeQuery();
			
			if(rs.next())
			{
				String citation_view_name = rs.getString(1);
				
				String del_string_query = "delete from citation2query where citation_view_name = '" + citation_view_name + "'";
				
				pst = c.prepareStatement(del_string_query);
				
				pst.execute();
			}
			
			
			
			for(int i = 0; i<table_names.length; i++)
			{
				String del_query = "delete from " + table_names[i] + " where view = '" + view_name + "'";
				
				pst = c.prepareStatement(del_query);
				
				pst.execute();
			}
			
			
			
			
		}
		
		
		
	}
	
	public static void rename_view(String view_name, Connection c, PreparedStatement pst, String new_name) throws SQLException
	{
		
		String []table_names = {"view2conditions", "view2lambda_term", "view2subgoals", "citation_view", "view_table"};
		
		String check_exist = "select exists (select 1 from view_table where view = '"+ view_name+"')";
		
		pst = c.prepareStatement(check_exist);
		
		ResultSet rs = pst.executeQuery();
		
		boolean exists = false;
		
		if(rs.next())
		{
			exists = rs.getBoolean(1);
		}
		
		if(exists)
		{
			for(int i = 0; i<table_names.length; i++)
			{
				String del_query = "update " + table_names[i] + " set view = '" + new_name +"' where view = '" + view_name + "'";
				
				System.out.println(del_query);
				
				pst = c.prepareStatement(del_query);
				
				pst.execute();
			}
			
			
			
			
		}
		
		
		
	}

}
