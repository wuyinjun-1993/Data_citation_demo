package edu.upenn.cis.citation.user_query;

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
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;

public class query_storage {
	
	public static void main(String [] args)
	{
		
	}
	
	static Query gen_query() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family_family_id", "family"));
				
		
//		head_args.add(new Argument("family1_family_id", "family1"));
//		
//		head_args.add(new Argument("family2_family_id", "family2"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		
		head_args.add(new Argument("introduction_family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
//		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
		subgoals.add(new Subgoal("family", args1));
		
//		subgoals.add(new Subgoal("family1", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		subgoals.add(new Subgoal("introduction", args3));
		
//		subgoals.add(new Subgoal("introduction1", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
		
//		subgoal_name_mapping.put("family1", "family");
//		
//		subgoal_name_mapping.put("family2", "family");
		
//		subgoal_name_mapping.put("family3", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");
//
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");


		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	
	public static void store_query(Query query, Vector<Integer> id_list) throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
	    query_graph q_graph = new query_graph(query);
	    
	    int hashcode = q_graph.hashCode();
	    
	    if(!check_existence(hashcode, c, pst))
	    {
	    	int query_num = gen_query_id(c, pst) + 1;
			
			String query_id = "q" + query_num;

			store_query_head_variables(query.head.args, id_list, query_id, c, pst);
			
			store_query_conditions(query.conditions, query_id, c, pst);
	    }
	    else
	    {
	    	
	    }
		
		
		
	}
	
	static boolean check_existence(int hashcode, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT exists (SELECT 1 FROM user_query_table WHERE hash_value = " + hashcode + " LIMIT 1)";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			boolean b = rs.getBoolean(1);
			
			return b;
		}
		
		return false;
	}
	
	static void store_query_conditions(Vector<Conditions> conditions, String query_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query_base = "insert into user_query2conditions values ('" + query_id + "','";
		
		for(int i = 0; i<conditions.size(); i++)
		{
			String query = query_base + conditions.get(i).toString() + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
		
		
	}
	
	static String vector2string(Vector<Integer> id_list)
	{
		String str = new String();
		
		for(int i = 0; i<id_list.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			str += id_list.get(i);
		}
		
		return str;
	}
	
	static void store_query_head_variables(Vector<Argument> head_args, Vector<Integer> int_list, String query_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String head_var_str = new String();
		
		for(int i = 0; i<head_args.size(); i++)
		{
			if(i >= 1)
				head_var_str += ",";
			
			head_var_str += head_args.get(i).name.trim();
		}
		
		String id_list = vector2string(int_list);
		
		String query = "insert into user_query_table values ('" + query_id + "','" + head_var_str + "','" + id_list + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static int gen_query_id(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select conut(*) from user_query_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int curr_query_num = 0;
		
		if(rs.next())
		{
			curr_query_num = rs.getInt(1);
		}
		
		return curr_query_num;
	}
	
	static void store_query_subgoals(String query_id, Vector<Subgoal> subgoals, Connection c, PreparedStatement pst) throws SQLException
	{
		String query_base = "insert into user_query2subgoals values ('" + query_id + "','";
		
		for(int i = 0; i<subgoals.size(); i++)
		{
			String query = query_base + subgoals.get(i).name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	

}
