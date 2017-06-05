package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class populate_citation_view_table {
	
	public static String db_name = "iuphar_org";

	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    populate(c, pst);
		
	}
	
	static void populate(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select * from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			boolean web_view = rs.getBoolean(3);
			
			String view_name = rs.getString(1);
			
			if(!web_view)
			{
				
				
				String head_vars = rs.getString(2);
				
				String citation_view_name = view_name.replaceFirst("v", "c");
				
				String insert_q = "insert into citation_view values ('" + citation_view_name + "','" + view_name + "','" + head_vars + "')";
				
				pst = c.prepareStatement(insert_q);
				
				pst.execute();
			}
			
			else
			{
				String sub_q = "select * from web_view_table where view = '" + view_name + "'";
				
				pst = c.prepareStatement(sub_q);
				
				ResultSet r = pst.executeQuery();
				
				while(r.next())
				{
					String insert_view_name = r.getString(2);
					
					String subgoal = r.getString(3);
					
					String head_vars = r.getString(4);
					
					String citation_view_name = insert_view_name.replaceFirst("v", "c"); 
					
					String insert_q = "insert into citation_view values ('" + citation_view_name + "','" + insert_view_name + "','" + head_vars + "')";

					pst = c.prepareStatement(insert_q);
					
					pst.execute();
				}
			}
		}
	}


}
