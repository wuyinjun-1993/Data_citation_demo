package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import schema_reasoning.Gen_citation1;

public class populate_web_view_vec {
	
	
	public static String db_name = "iuphar_org";

	public static String[] base_relations = {"gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c"};

	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    add_column(c, pst);
	    populate(c, pst);
		
	}
	
	public static void add_column(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
//		HashSet<String> table_names = get_table_name(Gen_citation1.get_views_schema());
	      ResultSet rs = null;

//		for(Iterator<String> iter = table_names.iterator();iter.hasNext();)
	      
	    for(int k = 0; k<base_relations.length; k++)
		{
//			String table_name = iter.next();
	    	String table_name = base_relations[k];
	    	
	    	String [] col_name = {"web_view_vec"};
	    	
	    	for(int p = 0; p<col_name.length; p++)
	    	{
	    		String test_col = "SELECT column_name FROM information_schema.columns WHERE table_name='" + table_name + "' and column_name='"+ col_name[p]+ "'";
			    pst = c.prepareStatement(test_col);
			    rs = pst.executeQuery();
			    
			    if(rs.next())
			    {
			    	if(rs.getRow()!=0)
			    	{
			    		String remove_sql = "alter table "+ table_name +" drop column " + col_name[p];
			    		
			    		pst = c.prepareStatement(remove_sql);
			    		
			    		pst.execute();
			    	}
			    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN "+ col_name[p] +" text";
		    	    pst = c.prepareStatement(add_col);
		    	    pst.execute();
			    }
			    else
			    {
			    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN "+ col_name[p]+ " text";
		    	    pst = c.prepareStatement(add_col);
		    	    pst.execute();
			    }
	    	}
	    	
//	    	initialize(table_name, c, pst);
			
		}
	 	
	}
	
	static void populate(Connection c, PreparedStatement pst) throws SQLException
	{
		
		String view_table_q = "select renamed_view, subgoal from web_view_table";
		
		pst = c.prepareStatement(view_table_q);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			String renamed_view = rs.getString(1);
			
			String table_name = rs.getString(2);
			
			assign_values(renamed_view, table_name, c, pst);
			
		}
		
		
		
	}
	
	
	static Vector<String> retrive_col_name(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String table_name_q = "select column_name from information_schema.columns where table_name = '"+ table_name +"'";
		
		pst = c.prepareStatement(table_name_q);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> col_names = new Vector<String>();
		
		while(rs.next())
		{
			col_names.add(rs.getString(1));
		}
		
		return col_names;
	}
	
	static void assign_values(String renamed_view, String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String base_table_q = "select * from "+ table_name;
		
		Vector<String> col_names = retrive_col_name(table_name, c, pst);
		
		pst = c.prepareStatement(base_table_q);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			String []citation_views = rs.getString(col_names.size()-1).split("\\" + populate_db.separator);
			
			String web_view_vec = rs.getString(col_names.size());
			
			int i = 0;
			
			for(i = 0; i<citation_views.length; i++)
			{
				if(citation_views[i].contains(renamed_view))
				{
					break;
				}
			}
			
			if(i < citation_views.length)
			{
				
				if(web_view_vec == null)
				{
					web_view_vec = renamed_view;
				}
				
				else
				{
					web_view_vec += "," + renamed_view;
				}
				
				update_value(table_name, rs, col_names.size(), web_view_vec, col_names,  c,  pst);
				
			}
			
			

		}
		
	}
	
	static void update_value(String table_name, ResultSet rs, int num, String web_vec_value, Vector<String> col_names, Connection c, PreparedStatement pst) throws SQLException
	{
		String update_sql = "update " + table_name + " set web_view_vec = '"+ web_vec_value +"' where ";
		
		String where = new String();
		
		for(int i = 0; i<num - 2; i++)
		{	
			String item = rs.getString(col_names.get(i));
			
			if(item!= null && item.contains("'"))
			{
				item = item.replaceAll("'", "''");
			}
			
//			if(item == null)
//			{
//				key_sql = key_sql + args.get(j).name + "= null";
//			}
			if(item != null && !item.isEmpty())
			{
				if(item.contains("."))
				{
					try{
						float f = Float.valueOf(item);
					}
					catch(NumberFormatException e)
					{
						if( i >= 1)
							where += " and ";
						
						where += col_names.get(i) + "='" + item + "'";
					}
				}
				else
				{
					if( i >= 1)
						where += " and ";
					
					where += col_names.get(i) + "='" + item + "'";
			
				}
		
				
//				if( k >= 1)
//					where_values += " and ";
//				
//				where_values += arg.name + "='" + item + "'";
			}
			
			
//			where += col_names.get(i) + "=" + rs.getString(i + 1);
		}
		
		update_sql += where;
		
		pst = c.prepareStatement(update_sql);
		
		pst.execute();
		
		
	}
	
	

}
