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
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.datalog.Parse_datalog;

public class Query_operation {
	
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Query q = gen_sample_view();
		
		System.out.println(q);
		
		add(q, "q10");
		
//		delete_query_by_id("q10");
		
	}
	
	public static void delete_query_by_id(String id) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
//        String id = get_view_id(name, c, pst);
        
//        String id = name;
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, subgoal_name_mapping, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
        delete_citation_view(id, c, pst);
        
        delete_head_variables(id, c, pst);
        
        c.close();
        
//        populate_db.delete(id, subgoals, subgoal_name_mapping, has_lambda);

	}
	
	static Vector<Subgoal> get_view_subgoals(String name, HashMap<String, String> subgoal_name_mapping, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_subgoals = "select subgoal_names, subgoal_origin_names from query2subgoal where query_id = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1);
			
			String subgoal_origin_name = r.getString(2);
			
			Vector<Argument> args = new Vector<Argument>();
			
			subgoal_name_mapping.put(subgoal_name, subgoal_origin_name);
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	static void delete_citation_view(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2query where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static boolean delete_lambda_terms(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		
		
		String query = "select count(*) from query2lambda_term where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		boolean has_lambda_term = false;
		
		if(rs.next())
		{
			int num = rs.getInt(1);
			
			if(num > 0)
				has_lambda_term = true;
		}
		
		query = "delete from query2lambda_term where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		return has_lambda_term;
		
	}
	
	static void delete_conditions(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from query2conditions where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_subgoals(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from query2subgoal where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_head_variables(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from query2head_variables where query_id = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	
	
	public static Query get_query_by_id(String id) throws SQLException, ClassNotFoundException
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
        
        Vector<Lambda_term> lambda_terms = get_query_lambda_terms(id, c, pst);
        
        Subgoal head = new Subgoal(id, head_var);
        
        Query view = new Query(id, head, subgoals,lambda_terms, conditions, subgoal_name_mapping);
        
        
        c.close();
                
        return view;
	}
	
	
	static Query gen_sample_view() throws ClassNotFoundException, SQLException
	{
//		Vector<Argument> head_args = new Vector<Argument>();
////		
//		head_args.add(new Argument("family_type", "family"));
//		
//		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
//		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
//		
//		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
//		
		Vector<Argument> args3 = new Vector<Argument> ();//view_operation.get_full_schema("introduction", "introduction", c, pst);
		
//		subgoals.add(new Subgoal("family", args1));
		
//		subgoals.add(new Subgoal("family1", args2));
				
//		subgoals.add(new Subgoal("introduction", args3));
//		
//		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("5"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("5"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("5"), new String()));
		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
				
		subgoal_name_mapping.put("contributor", "contributor");
		
		subgoal_name_mapping.put("contributor2intro", "contributor2intro");
		
		subgoal_name_mapping.put("introduction", "introduction");

		Vector<String []> head_vars = new Vector<String []>();
		
		String [] head_v1 = {"first_names", "contributor"};
		
		String [] head_v2 = {"surname", "contributor"};
		
		head_vars.add(head_v1);
		
		head_vars.add(head_v2);
		
		Vector<String []> condition_str = new Vector<String []>();
		
		Vector<String []> lambda_term_str = new Vector<String []>();
		
		String [] l_str = {"family_id", "introduction"};
		
		lambda_term_str.add(l_str);
		
		String [] c_str1 = {"contributor_id", "contributor2intro", "=", "contributor_id", "contributor"};
		
		String [] c_str2 = {"family_id", "contributor2intro", "=", "family_id", "introduction"};
		
		condition_str.add(c_str1);
		
		condition_str.add(c_str2);
		
//		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
//		
//		System.out.println(q);
						
		return Gen_query.gen_query("q10", subgoal_name_mapping, head_vars, condition_str, lambda_term_str);
	}
	
	public static void add(Query v, String name) throws ClassNotFoundException, SQLException
	{
		insert_query(v, name);
		
//		Vector<String> subgoal_names = new Vector<String>();
//		
//		Vector<Subgoal> subgoals = v.body;
//		
//		for(int i = 0; i<subgoals.size(); i++)
//		{
//			subgoal_names.add(subgoals.get(i).name);
//		}
//		
//		create_view.create_views(id, subgoal_names, v.conditions, null, null);

	}
	
	static void insert_query(Query query, String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
//        update_table(view, c, pst);
        
        int seq = get_query_num(c, pst) + 1;
        
        query.name = name;//"v" + seq;
        
        insert_query_head_vars(seq, query, c, pst);
        
        insert_query_conditions(seq, query, c, pst);
        
        insert_query_lambda_term(seq, query, c, pst);
        
        insert_query_subgoals(seq, query, c, pst);
                
        c.close();
        
	}
	
	static int get_query_num(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select count(*) from query2head_variables";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		if(rs.next())
		{
			num = rs.getInt(1);
		}
		
		return num;
	}
	
	static void insert_query_head_vars(int seq, Query q, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_name = q.name;
		
		String head_vars_str = new String();
		
		for(int i = 0; i<q.head.args.size(); i++)
		{
			Argument arg = (Argument)q.head.args.get(i);
			
			if(i >= 1)
				head_vars_str += ",";
			
			head_vars_str += arg.name;
		}
		
		String query = "insert into query2head_variables values ('q" + seq + "','" + head_vars_str + "','" + q_name + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static void insert_query_subgoals(int seq, Query q, Connection c, PreparedStatement pst) throws SQLException
	{		
		for(int i = 0; i<q.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)q.body.get(i);
			
			String query = "insert into query2subgoal values ('q" + seq + "','" + subgoal.name + "','" + q.subgoal_name_mapping.get(subgoal.name) +"')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	
	static void insert_query_lambda_term(int seq, Query q, Connection c, PreparedStatement pst) throws SQLException
	{		
		String lambda_term_str = new String();
		
		String lambda_term_table_name = new String();
		
		for(int i = 0; i<q.lambda_term.size(); i++)
		{
			if(i >= 1)
			{
				lambda_term_str += populate_db.separator;
				
				lambda_term_table_name += populate_db.separator;
			}
			
			lambda_term_str += q.lambda_term.get(i).toString();
			
			lambda_term_table_name += q.lambda_term.get(i).table_name;
		}
		
		if(!q.lambda_term.isEmpty())
		{
			String query = "insert into query2lambda_term values ('q" + seq + "','" + lambda_term_str + "','" + lambda_term_table_name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
		

		
		
	}
	
	static void insert_query_conditions(int seq, Query q, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<q.conditions.size(); i++)
		{			
			String query = "insert into query2conditions values ('q" + seq + "','" + q.conditions.get(i).toString() + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
			
		}
	}
	
	static String get_id_head_vars(String name, Vector<Argument> head_var, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select query_id, head_variables from query2head_variables where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		String id = new String();
		
		String head_var_str = new String();
		
		if(rs.next())
		{
			id = rs.getString(1).trim();
			
			head_var_str = rs.getString(2).trim();
		}
		
		String [] head_var_strs = head_var_str.split(",");
		
		for(int i = 0; i<head_var_strs.length; i++)
		{
			Argument arg = new Argument(head_var_strs[i].trim());
			
			head_var.add(arg);
		}
		
		return id;
			
	}
	
	static Vector<Argument> get_head_vars(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select head_variables from query2head_variables where query_id = '" + id + "'";
		
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
		
		String relation_name = head_var_str.substring(0, head_var_str.indexOf("_"));
		
		String attr_name = head_var_str.substring(head_var_str.indexOf("_") + 1, head_var_str.length());
		
		String [] values = {relation_name, attr_name};
		
		return values;
	}
	
	static Vector<Conditions> get_query_conditions(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_conditions = "select conditions from query2conditions where query_id = '" + id + "'";
		
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
				if(condition_str.contains(op_less.op))
				{
					strs = condition_str.split(op_less.op);
					
					op = new op_less();
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
						if(condition_str.contains(op_less_equal.op))
						{
							strs = condition_str.split(op_less_equal.op);
							
							op = new op_less_equal();
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
								if(condition_str.contains(op_not_equal.op))
								{
									strs = condition_str.split(op_not_equal.op);
									
									op = new op_not_equal();
								}
							}
						}
					}
				}
				
			}
			
			
			String str1 = strs[0];
			
			String str2 = strs[1];
			
			String relation_name1 = str1.substring(0, str1.indexOf("_"));
			
			String relation_name2 = new String();
			
			String arg1 = str1.substring(str1.indexOf("_") + 1, str1.length()).trim();

			String arg2 = new String ();
			
			Conditions condition;
			
			if(str2.contains("'"))
			{
				arg2 = str2.trim();
				
				condition = new Conditions(new Argument(arg1, relation_name1), relation_name1, op, new Argument(arg2), relation_name2);
			}
			else
			{
				arg2 = str2.substring(str2.indexOf("_") + 1, str2.length()).trim();
				
//				subgoal2 = strs2[0] + "_" + strs2[1];
				
				relation_name2 = str2.substring(0, str2.indexOf("_"));
				
				condition = new Conditions(new Argument(arg1, relation_name1), relation_name1, op, new Argument(arg2, relation_name2), relation_name2);

			}
			
			conditions.add(condition);
		}
		return conditions;
		
	}
	
	static Vector<Subgoal> get_query_subgoals(String name, HashMap<String, String> subgoal_name_mapping, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String q_subgoals = "select subgoal_names, subgoal_origin_names from query2subgoal where query_id = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1);
			
			String subgoal_origin_name = r.getString(2);
			
//			Vector<String> arg_strs = Parse_datalog.get_columns(subgoal_name);
			
			subgoal_name_mapping.put(subgoal_name, subgoal_origin_name);
			
			Vector<Argument> args = new Vector<Argument>();
			
//			for(int k = 0; k<arg_strs.size(); k++)
//			{
//				args.add(new Argument(arg_strs.get(k), subgoal_name));
//			}
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	static Vector<Lambda_term> get_query_lambda_terms(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select lambda_term, table_name from query2lambda_term where query_id = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		while(rs.next())
		{
			String lambda_str = rs.getString(1);
			
			String table_name = rs.getString(2);
			
			Lambda_term l_term = new Lambda_term(lambda_str, table_name);
			
			lambda_terms.add(l_term);
		}
		
		return lambda_terms;
		
	}
	

}
