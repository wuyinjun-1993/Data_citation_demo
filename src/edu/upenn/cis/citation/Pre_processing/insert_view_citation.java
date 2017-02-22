package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import java_cup.non_terminal;

public class insert_view_citation {
	
	static String [] blocks = {"author", "access", "modified"};
	
	static HashMap<String, HashMap<String, String>> conditions_map;
	
	static void initialize()
	{
		
		conditions_map = new HashMap<String, HashMap<String, String>>();
		
		
		HashMap<String, String> c1 = new HashMap<String, String>();
		
		c1.put("object_c", "object_c_object_id = contributor2object_c_object_id");
		
		c1.put("gpcr_c", "gpcr_c_object_id = contributor2object_c_object_id");
		
//		c1.put("receptor_c", "receptor_c_object_id = contributor2object_c_object_id");
		
		conditions_map.put("contributor2object_c", c1);
		
		HashMap<String, String> c2 = new HashMap<String, String>();
		
		c2.put("family_c", "family_c_family_id = contributor2family_c_family_id");
		
		conditions_map.put("contributor2family_c", c2);
		
		
		HashMap<String, String> c3 = new HashMap<String, String>();
		
		c3.put("introduction_c", "contributor2intro_c_family_id = introduction_c_family_id");
		
		conditions_map.put("contributor2intro_c", c3);
		
		HashMap<String, String> c4 = new HashMap<String, String>();

		c4.put("ligand_c", "ligand_c_ligand_id = contributor2ligand_c_ligand_id");
		
		conditions_map.put("contributor2ligand_c", c4);

	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
//		String view = "q(ligand_c_ligand_id, interaction_c_object_id): ligand_c(), interaction_c(), interaction_c_ligand_id = ligand_c_ligand_id";
//		
//		view = get_full_query(view);
//		
//		Query v = Parse_datalog.parse_query(view);
//		
//		Lambda_term l = new Lambda_term("ligand_c_ligand_id", "ligand_c");
//		
//		v.lambda_term.add(l);
		
		initialize();
		
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String view_name = "v1";
		
		String citation_name = "c1";
		
		int query_id = 2;
		
//		insert_view(view_name, blocks[0], );
		
		insert_citation(c, pst, "v1", blocks[0], 2, "c1");
		
		insert_citation(c, pst, "v2", blocks[0], 1, "c2");
		
		insert_citation(c, pst, "v3", blocks[0], 2, "c3");
		
		insert_citation(c, pst, "v4", blocks[0], 1, "c4");
		
		insert_citation(c, pst, "v5", blocks[0], 7, "c5");
		
		insert_citation(c, pst, "v6", blocks[0], 1, "c6");
		
		insert_citation(c, pst, "v6", blocks[0], 3, "c6");
		
		insert_citation(c, pst, "v7", blocks[0], 2, "c7");
		
		insert_citation(c, pst, "v8", blocks[0], 2, "c8");
		
		insert_citation(c, pst, "v9", blocks[0], 7, "c9");
		
		
	}
	
	static void insert_citation(Connection c, PreparedStatement pst, String view_name, String block_name, int query_id, String citation_name) throws SQLException
	{
		
		boolean exist = check_citation_exist(citation_name, c, pst);
//		
//		String citation_name = "c" + num;
		if(!exist)
		{
			String insert_string = "insert into citation_view values ('"+ citation_name   +"','" + view_name + "')";
			
			pst = c.prepareStatement(insert_string);
			
			pst.execute();
		}
		
		exist = check_citation_query_exist(citation_name, block_name, query_id, c, pst);
		
		if(!exist)
		{
			String i_str = "insert into citation2query values ('" + citation_name + "','" + block_name + "','" + query_id;
			
			Vector<String> query_table_names =  get_table_name_citation(c, pst, query_id);
			
			Vector<String> view_table_names =get_table_name_view(c, pst, view_name);
			
			int i = 0;
			
			for(i = 0; i < query_table_names.size(); i++)
			{
				if(view_table_names.contains(query_table_names.get(i)))
				{
					break;
				}
			}
			
			if(i < query_table_names.size())
			{
				i_str += "')";
				
				pst = c.prepareStatement(i_str);
				
				pst.execute();
			}
			else
			{
				String condition = get_condition_str(query_table_names, view_table_names, c, pst);
				
				i_str += "','" + condition + "')";
				
				pst = c.prepareStatement(i_str);
				
				pst.execute();
				
			}
		}
		
		
		
		
	}
	
	static String get_condition_str(Vector<String> query_table_names, Vector<String> view_table_names, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<query_table_names.size(); i++)
		{
			boolean find = false;
			
			
			for(int j = 0; j<view_table_names.size(); j++)
			{
				HashMap<String, String> map = conditions_map.get(query_table_names.get(i));
				
				if(map != null)
				{
					String condition = map.get(view_table_names.get(j));
					
					if(condition != null)
						return condition;
					
				}
				
			}
			
			
		}
		return null;
	}
	
	static Vector<String> get_args(Connection c, PreparedStatement pst, String table_name) throws SQLException
	{
		String query = "select arguments from subgoal_arguments where subgoal_names = '" + table_name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		String [] arg_names = null;
		
		Vector<String> arguments = new Vector<String>();
		
		if(rs.next())
		{
			arg_names = rs.getString(1).split(",");
		}
		
		for(int i = 0; i<arg_names.length; i++)
		{
			arguments.add(arg_names[i].substring(table_name.length() + 1, arg_names[i].length()));
		}
		
		return arguments;
	}
	
	
	static boolean check_citation_exist(String citation_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String num_q = "select exists (select 1 from citation_view where citation_view_name = '" + citation_view_name + "')";
		
		pst = c.prepareStatement(num_q);
		
		ResultSet rs = pst.executeQuery();
		
		boolean exist = false;
		
		if(rs.next())
		{
			exist = rs.getBoolean(1);
		}
		
		return exist;
	}
	
	static boolean check_citation_query_exist(String citation_view_name, String block, int query_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String num_q = "select exists (select 1 from citation2query where citation_view_name = '" + citation_view_name + "' and citation_block = '" + block + "' and query_id = '" + query_id + "')";
		
		pst = c.prepareStatement(num_q);
		
		ResultSet rs = pst.executeQuery();
		
		boolean exist = false;
		
		if(rs.next())
		{
			exist = rs.getBoolean(1);
		}
		
		return exist;
	}
	
	
	static Vector<String> get_table_name_citation(Connection c, PreparedStatement pst, int query_id) throws SQLException
	{
		Vector<String> table_names = new Vector<String>();
		
		String q = "select query from query_table where query_id = '" + query_id + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			String query = rs.getString(1);
			
			String[] names = query.split(":")[1].trim().split(",");
			
			for(int i = 0; i<names.length; i++)
			{
				names[i] = names[i].trim();
				
				
				names[i] = names[i].substring(0, names[i].length() - 2);
				
				table_names.add(names[i]);
			}
		}
		
		return table_names;
		
	}
	
	static Vector<String> get_table_name_view(Connection c, PreparedStatement pst, String view_name) throws SQLException
	{
		Vector<String> table_names = new Vector<String>();
		
		String q = "select subgoal_names from view2subgoals where view = '" + view_name + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			String query = rs.getString(1);
			
			table_names.add(query);
			
		}
		
		return table_names;
		
	}
	
	

}
