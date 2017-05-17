package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Operation.*;

public class create_view {

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
	    
	    check_create_views();
		
	}
	
	
	
	public static void check_create_views() throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		
		String query= "select * from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			String view_name = rs.getString(1);
			
			String q_subgoals = "select subgoal_names from view2subgoals where view = '" + view_name + "'";
			
			pst = c.prepareStatement(q_subgoals);
			
			ResultSet r = pst.executeQuery();
			
			Vector<String> subgoal_names = new Vector<String>();
			
			while(r.next())
			{
				subgoal_names.add(r.getString(1));
			}
			
			Vector<Conditions> conditions = view_operation.get_view_conditions(view_name, c, pst);
			
			
			create_views(view_name, subgoal_names, conditions, c, pst);
		}
		
		c.close();
				
				
		
		
	}
	
	public static void create_views(String view_name, Vector<String> subgoal_names, Vector<Conditions> conditions, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		boolean close_in_the_end = false;
		
		if(c == null)
		{
			Class.forName("org.postgresql.Driver");
		    c = DriverManager
		        .getConnection(populate_db.db_url,
		    	        populate_db.usr_name,populate_db.passwd);
		    
		    close_in_the_end = true;
		}
		
		
		
		Vector<String> col_names = new Vector<String>();
		
		String table_str = new String();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{
			String query_table_col = "select arguments from subgoal_arguments where subgoal_names = '" + subgoal_names.get(i) + "'";
			
			pst = c.prepareStatement(query_table_col);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next())
			{
				
				String [] cols = rs.getString(1).split(",");
				
				String col_str = new String();
				
				
				for(int j = 0; j<cols.length; j++)
				{
					
					int length = cols[j].length();
					
					int prefix_len = subgoal_names.get(i).length();
					
					String real_col = cols[j].substring(prefix_len + 1, length);
					
					col_str = subgoal_names.get(i) + "." + cols[j];
					
					col_names.add(col_str);
				}
				
			}
			
			if(i >= 1)
				table_str += ",";
			
			table_str += subgoal_names.get(i);

		}
		
		String col_str = new String();
		
		for(int k = 0; k<col_names.size(); k++)
		{
			if(k >= 1)
				col_str += ",";
			
			col_str += col_names.get(k);
		}
		
		String condition_str = new String();
		
		for(int k = 0; k<conditions.size(); k++)
		{
			if(k >= 1)
				condition_str += " and ";
			
			condition_str += conditions.get(k);
		}
		
		
		String query = new String();
		
		query = "SELECT EXISTS ( SELECT 1  FROM   information_schema.tables WHERE  table_name = '" + view_name + "')";

		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			boolean exists = rs.getBoolean(1);
			
			if(exists)
			{
				String q = "drop view "+ view_name;
				
				pst = c.prepareStatement(q);
				
				pst.execute();
				
				q = "drop table "+ view_name + "_table";
				
				pst = c.prepareStatement(q);
				
				pst.execute();
				
				
				
			}
		}
		
		
		
		if(conditions.isEmpty())
			query = "create view " + view_name + " as select " + col_str + " from " + table_str;
		else
			query = "create view " + view_name + " as select " + col_str + " from " + table_str + " where " + condition_str;
				
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		pst = c.prepareStatement("create table " + view_name + "_table as select * from " + view_name);
		
		pst.execute();
		
		pst = c.prepareStatement("alter table " + view_name + "_table add column citation_view text");
		
		pst.execute();
		
		if(close_in_the_end)
			c.close();
	}
}
