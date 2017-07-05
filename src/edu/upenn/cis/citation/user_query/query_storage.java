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
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;

public class query_storage {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Query q = gen_query();
		
		Vector<Integer> int_list = new Vector<Integer>();
		
		int_list.add(1);
		
		store_query(q, int_list);
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
	
	public static Query get_query_by_id(int id) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        Vector<Argument> head_var = new Vector<Argument>();
        
//        String id = get_id_head_vars(name, head_var, c, pst);

        head_var = get_head_vars(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String> ();
        
        Vector<Conditions> conditions = get_query_conditions(id, c, pst);
        
        Vector<Subgoal> subgoals = get_query_subgoals(id, subgoal_name_mapping, c, pst);
        
        String predicate = get_query_predicate(id, c, pst);
        
        Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();//get_query_lambda_terms(id, c, pst);
        
        lambda_terms.add(new Lambda_term(predicate));
        
        Subgoal head = new Subgoal("q" + id, head_var);
        
        Query view = new Query("q" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
        
        
        c.close();
                
        return view;
	}
	
	public static Query get_user_query_by_id(int id) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        Vector<Argument> head_var = new Vector<Argument>();
        
//        String id = get_id_head_vars(name, head_var, c, pst);

        head_var = get_head_vars(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String> ();
        
        Vector<Conditions> conditions = get_query_conditions(id, c, pst);
        
        Vector<Subgoal> subgoals = get_query_subgoals(id, subgoal_name_mapping, c, pst);
        
//        String predicate = get_query_predicate(id, c, pst);
        
        Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
        
        Subgoal head = new Subgoal("q" + id, head_var);
        
        Query view = new Query("q" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
        
        
        c.close();
                
        return view;
	}
	
	static String get_query_predicate(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select string from user_query_conditions where query_id = " + id;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			return rs.getString(1);
		}
		
		return null;
	}
	
	public static Vector<Integer> get_query_list_by_id(int id) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        Vector<Argument> head_var = new Vector<Argument>();
        
        String query = "select rids from user_query_table where query_id = '" + id + "'";
        
        pst = c.prepareStatement(query);
        
        ResultSet rs = pst.executeQuery();
        
        Vector<Integer> int_list = new Vector<Integer>();
        
        if(rs.next())
        {
        	String values = rs.getString(1);
        	
        	if(values == null || values.isEmpty())
        		return int_list;
        	
        	String [] value_list = values.split(",");
        	
        	for(int i = 0; i<value_list.length; i++)
        	{
        		int int_val = Integer.valueOf(value_list[i]);
        		
        		int_list.add(int_val);
        	}
        	
        	
        }
        
        return int_list;
        
//        String id = get_id_head_vars(name, head_var, c, pst)
	}
	
	static Vector<Subgoal> get_query_subgoals(int name, HashMap<String, String> subgoal_name_mapping, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String q_subgoals = "select subgoal_name, subgoal_origin_name from user_query2subgoals where query_id = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1).trim();
			
			String subgoal_origin_name = r.getString(2).trim();
			
//			Vector<String> arg_strs = Parse_datalog.get_columns(subgoal_name);
			
			subgoal_name_mapping.put(subgoal_name, subgoal_origin_name);
			
			Vector<Argument> args = view_operation.get_full_schema(subgoal_name, subgoal_origin_name, c, pst);
			
//			for(int k = 0; k<arg_strs.size(); k++)
//			{
//				args.add(new Argument(arg_strs.get(k), subgoal_name));
//			}
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	
	static Vector<Conditions> get_query_conditions(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_conditions = "select conditions from user_query2conditions where query_id = '" + id + "'";
		
		pst = c.prepareStatement(q_conditions);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		while(r.next())
		{
			
			String condition_str = r.getString(1);
			
			String []strs = null;
			
			Operation op = null;
			
			
			if(condition_str.contains(op_equal.op))
			{
				strs = condition_str.split(op_equal.op);
				
				op = new op_equal();
			}
			else
			{
				if(condition_str.contains(op_not_equal.op))
				{
					strs = condition_str.split(op_not_equal.op);
					
					op = new op_not_equal();
				}
				
				else
				{
					if(condition_str.contains(op_greater_equal.op))
					{
						strs = condition_str.split(op_greater_equal.op);
						
						op = new op_greater_equal();
					}
					else
					{
						if(condition_str.contains(op_less_equal.op))
						{
							strs = condition_str.split(op_less_equal.op);
							
							op = new op_less_equal();
						}
						else
						{

							if(condition_str.contains(op_greater.op))
							{
								strs = condition_str.split(op_greater.op);
								
								op = new op_greater();
							}
							else
							{
								if(condition_str.contains(op_less.op))
								{
									strs = condition_str.split(op_less.op);
									
									op = new op_less();
								}
							}
						}
					}
				}
				
			}
			
			
			String str1 = strs[0];
			
			String str2 = strs[1];
			
			String relation_name1 = str1.substring(0, str1.indexOf(populate_db.separator)).trim();
			
			String relation_name2 = new String();
			
			String arg1 = str1.substring(str1.indexOf(populate_db.separator) + 1, str1.length()).trim();

			String arg2 = new String ();
			
			Conditions condition;
			
			if(str2.contains("'"))
			{
				arg2 = str2.trim();
				
				condition = new Conditions(new Argument(arg1, relation_name1), relation_name1, op, new Argument(arg2), relation_name2);
			}
			else
			{
				arg2 = str2.substring(str2.indexOf(populate_db.separator) + 1, str2.length()).trim();
				
//				subgoal2 = strs2[0] + "_" + strs2[1];
				
				relation_name2 = str2.substring(0, str2.indexOf(populate_db.separator)).trim();
				
				condition = new Conditions(new Argument(arg1, relation_name1), relation_name1, op, new Argument(arg2, relation_name2), relation_name2);

			}
			
			conditions.add(condition);
		}
		return conditions;
		
	}
	
	
	static Vector<Argument> get_head_vars(int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select head_variable from user_query_table where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		String head_var_str = new String();
		
		if(rs.next())
		{			
			head_var_str = rs.getString(1).trim();
		}
		
		String [] head_var_strs = head_var_str.split(",");
		
		Vector<Argument> head_var = new Vector<Argument>();
		
		for(int i = 0; i<head_var_strs.length; i++)
		{
			
			String []values = split_relation_attr_name(head_var_strs[i]);
			
			Argument arg = new Argument(head_var_strs[i].trim(), values[0]);
			
			head_var.add(arg);
		}
		
		return head_var;
	}
	
	static String[] split_relation_attr_name(String head_var_str)
	{
		head_var_str = head_var_str.trim();
		
		String relation_name = head_var_str.substring(0, head_var_str.indexOf(populate_db.separator));
		
		String attr_name = head_var_str.substring(head_var_str.indexOf(populate_db.separator) + 1, head_var_str.length());
		
		String [] values = {relation_name, attr_name};
		
		return values;
	}
	
	
	public static int store_query(Query query, Vector<Integer> id_list) throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
	    query_graph q_graph = new query_graph(query);
	    
	    int hashcode = q_graph.hashCode();
	    
	    int id = check_existence(hashcode, c, pst);
	    
	    if(id <= 0)
	    {
	    	int query_num = gen_query_id(c, pst) + 1;
			
			store_query_head_variables(query.head.args, id_list, query_num, hashcode, c, pst);
			
			store_query_conditions(query.conditions, query_num, c, pst);
			
			store_query_subgoals(query, query_num, query.body, c, pst);
			
			store_query_predicates(query, query_num, c, pst);
			
	    }
	    
	    return id;
		
		
		
	}
	
	public static int store_user_query(Query query, Vector<Integer> id_list) throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
	    query_graph q_graph = new query_graph(query);
	    
	    int hashcode = q_graph.hashCode();
	    
	    int id = check_existence(hashcode, c, pst);
	    
	    if(id <= 0)
	    {
	    	int query_num = gen_query_id(c, pst) + 1;
			
			store_query_head_variables(query.head.args, id_list, query_num, hashcode, c, pst);
			
			store_query_conditions(query.conditions, query_num, c, pst);
			
			store_query_subgoals(query, query_num, query.body, c, pst);
			
//			store_query_predicates(query, query_num, c, pst);
			
	    }
	    
	    return id;
		
		
		
	}
	
	static void store_query_predicates(Query query, int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String sql = "insert into user_query_conditions values ('" + id + "','" + query.lambda_term.get(0) + "')";
		
		pst = c.prepareStatement(sql);
		
		pst.execute();
	}
	
	static int check_existence(int hashcode, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT query_id FROM user_query_table WHERE hash_value = " + hashcode;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			int id = rs.getInt(1);
			
			return id;
		}
		
		return -1;
	}
	
	static void store_query_conditions(Vector<Conditions> conditions, int query_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query_base = "insert into user_query2conditions values ('" + query_id + "','";
		
		for(int i = 0; i<conditions.size(); i++)
		{
			String query = query_base + conditions.get(i).toStringinsql() + "')";
			
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
	
	static void store_query_head_variables(Vector<Argument> head_args, Vector<Integer> int_list, int query_id, int hashcode, Connection c, PreparedStatement pst) throws SQLException
	{
		String head_var_str = new String();
		
		for(int i = 0; i<head_args.size(); i++)
		{
			if(i >= 1)
				head_var_str += ",";
			
			head_var_str += head_args.get(i).name.trim();
		}
		
		String id_list = vector2string(int_list);
		
		String query = "insert into user_query_table values ('" + query_id + "','" + head_var_str + "','" + id_list + "','" + hashcode + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static int gen_query_id(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select max(query_id) from user_query_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int curr_query_num = 0;
		
		if(rs.next())
		{
			curr_query_num = rs.getInt(1);
		}
		
		return curr_query_num;
	}
	
	static void store_query_subgoals(Query user_query, int query_id, Vector<Subgoal> subgoals, Connection c, PreparedStatement pst) throws SQLException
	{
		String query_base = "insert into user_query2subgoals values ('" + query_id + "','";
		
		for(int i = 0; i<subgoals.size(); i++)
		{
			String query = query_base + subgoals.get(i).name + "','" + user_query.subgoal_name_mapping.get(subgoals.get(i).name) + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	

}
