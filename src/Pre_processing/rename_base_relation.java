package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class rename_base_relation {
	
	public static String[] base_relations = {"contributor_c", "contributor2family_c", "contributor2object_c", "contributor2intro_c", "contributor2ligand_c", "introduction_c", "gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c"};

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		 Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	   
	    create_table(c, pst, base_relations);
	    
	    
	    
	    
	    
	}
	
	
	public static void create_table(Connection c, PreparedStatement pst, String[] base_relations) throws SQLException
	{
		
		for(int i = 0; i<base_relations.length; i++)
		{
			

			
//			change_col_names(i, col_names, c, pst);
			String query = "SELECT EXISTS ( SELECT 1  FROM   information_schema.tables WHERE  table_name = '" + base_relations[i] + "')";

			pst = c.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next())
			{
				boolean exist = rs.getBoolean(1);
				
//				if(exist)
//				{
//					query = "drop table " + base_relations[i];
//					
//					pst = c.prepareStatement(query);
//					
//					pst.execute();
//					
//				}
				
				if(!exist)
				{
					Vector<String> col_names = get_column_names(c, pst, i);

					gen_table(i, col_names, c, pst);
				}
				

				
				
			}
			
			
		}
		
	}
	
	public static Vector<String> get_column_names(Connection c, PreparedStatement pst, int i) throws SQLException
	{
		
		String origin_name = base_relations[i].substring(0, base_relations[i].length() - 2);
		
		String query = "select column_name from information_schema.columns where table_name = '" + origin_name +"'";
		
		pst = c.prepareStatement(query);
		
		ResultSet r = pst.executeQuery();
		
		Vector<String> col_names = new Vector<String>();
		
		while(r.next())
		{
			col_names.add(r.getString(1));
		}
		
		return col_names;
		
		
	}
	
//	public static void change_col_names(int i, Vector<String> col_names, Connection c, PreparedStatement pst) throws SQLException
//	{
//		for(int k = 0; k<col_names.size(); k++)
//		{
//			String update_sql = "alter table " + base_relations[k] + " rename " + col_names.get(k) + " to " + base_relations[k] + "_" +col_names.get(k);
//			
//			pst = c.prepareStatement(update_sql);
//			
//			pst.execute();
//			
//		}
//	}
	
	
	public static void gen_table(int i, Vector<String> col_names, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "create table " + base_relations[i] +" as select ";
		
		String origin_name = base_relations[i].substring(0, base_relations[i].length() - 2);
		
		
		for(int k = 0; k<col_names.size(); k++)
		{
			if(k >= 1)
				query += ",";
			
			query += col_names.get(k) + " as " + base_relations[i] + "_" + col_names.get(k); 
		}
		
		query += " from " + origin_name;
		
		pst = c.prepareStatement(query);
		
		pst.execute();
				
	}

}
