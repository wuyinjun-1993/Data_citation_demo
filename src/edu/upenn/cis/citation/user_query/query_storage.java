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
  
  static String[] user_query_relations = {"user_query2citation", "user_query2subgoals", "user_query2conditions", "user_query2covering_set", "user_query2head_var", "user_query_view_mapping2parameters", "user_query_table"};
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		
		Vector<Integer> int_list = new Vector<Integer>();
		
		int_list.add(1);
		
//		store_query(q, int_list);
	}
	
	public static Query get_query_by_id(int id, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
        
        Vector<Argument> head_var = new Vector<Argument>();
        
//        String id = get_id_head_vars(name, head_var, c, pst);

        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String> ();
        
        HashMap<String, Argument> subgoal_arg_name_mapping = new HashMap<String, Argument>();
        
        Vector<Subgoal> subgoals = get_query_subgoals(id, subgoal_name_mapping, subgoal_arg_name_mapping, c, pst);
        
        head_var = get_head_vars(id, c, pst);
        
        
        Vector<Conditions> conditions = get_query_conditions(id, c, pst);
        
        
//        String predicate = get_query_predicate(id, c, pst);
        
        Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();//get_query_lambda_terms(id, c, pst);
        
//        lambda_terms.add(new Lambda_term(predicate));
        
        Subgoal head = new Subgoal("q" + id, head_var);
        
        Query view = new Query("q" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
                        
        return view;
	}
	
	public static Query get_user_query_by_id(int id) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String> ();
        
        HashMap<String, Argument> subgoal_arg_name_mapping = new HashMap<String, Argument>(); 
        
        Vector<Subgoal> subgoals = get_query_subgoals(id, subgoal_name_mapping, subgoal_arg_name_mapping, c, pst);
        
        Vector<Argument> head_var = new Vector<Argument>();
        
//        String id = get_id_head_vars(name, head_var, c, pst);

        head_var = get_head_vars(id, c, pst);
        
        Vector<Conditions> conditions = get_query_conditions(id, c, pst);
        
//        String predicate = get_query_predicate(id, c, pst);
        
        Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
        
        Subgoal head = new Subgoal("q" + id, head_var);
        
        Query view = new Query("q" + id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
        
        
        c.close();
                
        return view;
	}
	
//	static String get_query_predicate(int id, Connection c, PreparedStatement pst) throws SQLException
//	{
//		String query = "select string from user_query_conditions where query_id = " + id;
//		
//		pst = c.prepareStatement(query);
//		
//		ResultSet rs = pst.executeQuery();
//		
//		if(rs.next())
//		{
//			return rs.getString(1);
//		}
//		
//		return null;
//	}
	
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
	
	static Vector<Subgoal> get_query_subgoals(int name, HashMap<String, String> subgoal_name_mapping, HashMap<String, Argument> subgoal_arg_name_mapping, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
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
			
			Vector<Argument> args = view_operation.get_full_schema(subgoal_name, subgoal_origin_name, subgoal_arg_name_mapping, c, pst);
			
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
	
	static void store_hash_code_query(int id, int hashCode, String version, Connection c, PreparedStatement pst) throws SQLException
	{
	  String sql = "insert into user_query_table values ('" + id + "','" + hashCode + "','" + version + "')";
	  
	  pst = c.prepareStatement(sql);
	  
	  pst.execute();
	  
	}
	
//	public static int store_query(Query query, String version, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
//	{
//	    
//	    query_graph q_graph = new query_graph(query);
//	    
//	    int hashcode = q_graph.hashCode();
//	    
//	    int id = check_existence(hashcode, c, pst);
//	    
//	    if(id <= 0)
//	    {
//	    	int query_num = gen_query_id(c, pst) + 1;
//	    	
//	    	store_hash_code_query(query_num, hashcode, version, c, pst);
//			
//			store_query_head_variables(query.head.args, query_num, c, pst);
//			
//			store_query_conditions(query.conditions, query_num, c, pst);
//			
//			store_query_subgoals(query, query_num, query.body, c, pst);
//			
//			store_query_predicates(query, query_num, c, pst);
//			
//	    }
//	    	    
//	    return id;
//		
//		
//		
//	}
	
	public static int get_query_id(Query query, String version, Connection c, PreparedStatement pst) throws SQLException
	{
	  query_graph q_graph = new query_graph(query, version);
	  
	  int hashcode = q_graph.hashCode();
	  
	  return check_existence(hashcode, version, c, pst);
	}
	
	public static int store_user_query(Query query, String version, Connection c, PreparedStatement pst) throws SQLException
	{
		
	    query_graph q_graph = new query_graph(query, version);
	    
	    int hashcode = q_graph.hashCode();
	    
	    int id = check_existence(hashcode, version, c, pst);
	    
	    if(id <= 0)
	    {
	    	int query_num = gen_query_id(c, pst) + 1;
	    	
	    	store_hash_code_query(query_num, hashcode, version, c, pst);
			
			store_query_head_variables(query.head.args, query_num, c, pst);
			
			store_query_conditions(query.conditions, query_num, c, pst);
			
			store_query_subgoals(query, query_num, query.body, c, pst);
			
			return query_num;
//			store_query_predicates(query, query_num, c, pst);
			
	    }
	    
	    return id;
		
	}
	
	   public static int store_user_query(Query query, String version, boolean test, Connection c, PreparedStatement pst) throws SQLException
	    {
	        
	        query_graph q_graph = new query_graph(query, version);
	        
	        int hashcode = q_graph.hashCode();
	        
	        int id = check_existence(hashcode, version, c, pst);
	        
	        if(id <= 0)
	        {
	            int query_num = gen_query_id(c, pst) + 1;
	            
	            store_hash_code_query(query_num, hashcode, version, c, pst);
	            
	            store_query_head_variables(query.head.args, query_num, c, pst);
	            
	            store_query_conditions(query.conditions, query_num, c, pst);
	            
	            store_query_subgoals(query, query_num, query.body, c, pst);
	            
	            store_query_predicates(query, query_num, c, pst);

	            return query_num;
	            
	        }
	        
	        return id;
	        
	    }
	
	public static  Vector<Integer> store_user_query_ids() throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
	    String query_ids = "select query_id from user_query_table";
	    
	    pst = c.prepareStatement(query_ids);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    Vector<Integer> ids = new Vector<Integer>();
	    
	    while(rs.next())
	    {
	    	ids.add(rs.getInt(1));
	    }
	    
	    
	    
	    
	    c.close();
	    
	    return ids;
		
		
		
	}
	
	public static void store_query_predicates(Query query, int id, Connection c, PreparedStatement pst) throws SQLException
	{
		String sql = "insert into user_query_conditions values ('" + id + "','" + query.lambda_term.get(0) + "')";
		
		pst = c.prepareStatement(sql);
		
		pst.execute();
	}
	
//	public static void update_query_predicates(Query query, int id, Connection c, PreparedStatement pst) throws SQLException
//	{
//		String sql = "update user_query_conditions set string = '" + query.lambda_term.get(0) + "' where query_id = " + id;
//		
//		pst = c.prepareStatement(sql);
//		
//		pst.execute();
//	}
	
	static int check_existence(int hashcode, String version, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT query_id FROM user_query_table WHERE hash_value = " + hashcode + " and version = '" + version + "'";
		
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
	
	static void store_query_head_variables(Vector<Argument> head_args, int query_id, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<head_args.size(); i++)
		{
		    String query = "insert into user_query2head_var values ('" + query_id + "','" + head_args.get(i).name + "')";
	        
	        pst = c.prepareStatement(query);
	        
	        pst.execute();
		}
		
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
	
	
	public static void reset(Connection c, PreparedStatement pst) throws SQLException
	{
	  for(String relation: user_query_relations)
	  {
	    String sql = "delete from " + relation;
	    
	    pst = c.prepareStatement(sql);
	    
	    pst.execute();
	  }
	}

}
