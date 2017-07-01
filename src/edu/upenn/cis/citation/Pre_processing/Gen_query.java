package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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

public class Gen_query {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Query view = view_operation.get_view_by_id(4);
		
		HashMap<String, String> relation_mapping = new HashMap<String, String>(); 
		
		Vector<String[]> head_vars = new Vector<String[]>(); 
		
		Vector<String []> condition_str = new Vector<String []>();
		
		Vector<String[]> lambda_term_str = new Vector<String []> ();
		
		get_query_info(view, relation_mapping, head_vars, condition_str, lambda_term_str);
		
		Query view_2 = gen_query("v4'", relation_mapping, head_vars, condition_str, lambda_term_str);
		
		System.out.println(view);
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			System.out.println(view.lambda_term.get(i));
		}
		
		System.out.println(view_2);
		
		for(int i = 0; i<view_2.lambda_term.size(); i++)
		{
			System.out.println(view_2.lambda_term.get(i));
		}
		
	}
	
	static Vector<Conditions> gen_conditions(Vector<String []> condition_str)
	{
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		for(int i = 0; i<condition_str.size(); i++)
        {
        	
        	String op_str = condition_str.get(i)[2].trim();
        	
        	Operation op = null;
			
			
			if(op_str.equals(op_equal.op))
			{				
				op = new op_equal();
			}
			else
			{
				if(op_str.equals(op_less.op))
				{					
					op = new op_less();
				}
				else
				{
					if(op_str.equals(op_greater.op))
					{						
						op = new op_greater();
					}
					else
					{
						if(op_str.equals(op_less_equal.op))
						{							
							op = new op_less_equal();
						}
						else
						{
							if(op_str.equals(op_greater_equal.op))
							{								
								op = new op_greater_equal();
							}
							else
							{
								if(op_str.equals(op_not_equal.op))
								{									
									op = new op_not_equal();
								}
							}
						}
					}
				}
				
			}
        	
        	Conditions condition = new Conditions(new Argument(condition_str.get(i)[0].trim(), condition_str.get(i)[1].trim()),  condition_str.get(i)[1], op, new Argument(condition_str.get(i)[3].trim(), condition_str.get(i)[4].trim()),  condition_str.get(i)[4]);
        	
        	conditions.add(condition);
        }
		
		return conditions;
	}
	
	static void gen_conditions_str(Vector<Conditions> conditions, Vector<String []> conditions_strs)
	{
		
		for(int i = 0; i<conditions.size(); i++)
		{
			
			Conditions condition = conditions.get(i);
			
			String [] condition_strs = new String[5];
			
			condition_strs[2] = condition.op.toString();
			
			condition_strs[0] = condition.arg1.name;
			
			condition_strs[1] = condition.arg1.relation_name;
			
			condition_strs[3] = condition.arg2.name;
			
			condition_strs[4] = condition.arg2.relation_name;
			
			conditions_strs.add(condition_strs);
		}
				
	}
	
	static Vector<Lambda_term> gen_lambda_terms(Vector<String []> lambda_term_str)
	{
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<lambda_term_str.size(); i++)
		{
			Lambda_term l_term = new Lambda_term(lambda_term_str.get(i)[1] + populate_db.separator + lambda_term_str.get(i)[0], lambda_term_str.get(i)[1]);
			
			lambda_terms.add(l_term);
		}
		
		return lambda_terms;
	}
	
	static void gen_lambda_terms_str(Vector<Lambda_term> lambda_terms, Vector<String []> lambda_terms_str)
	{
		
		for(int i = 0; i<lambda_terms.size(); i++)
		{
			String [] lambda_term_str = new String [2];
			
			lambda_term_str[0] = lambda_terms.get(i).name.substring(lambda_terms.get(i).name.indexOf(populate_db.separator) + 1, lambda_terms.get(i).name.length());
			
			lambda_term_str[1] = lambda_terms.get(i).table_name;
			
			lambda_terms_str.add(lambda_term_str);
		}
	}
	
	static Vector<Subgoal> gen_subgoals(HashMap<String, String> relation_mapping)
	{
		Set<String> keys = relation_mapping.keySet();
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String relation_name = (String)iter.next();
			
			String origin_name = relation_mapping.get(relation_name);
			
			Vector<Argument> args = new Vector<Argument>();
			
			Subgoal subgoal = new Subgoal(relation_name, args);
			
			subgoals.add(subgoal);
		}
		
		return subgoals;
	}
	
	
	static Vector<Subgoal> gen_subgoals_full(HashMap<String, String> relation_mapping) throws SQLException, ClassNotFoundException
	{
		
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
		
		Set<String> keys = relation_mapping.keySet();
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String relation_name = (String)iter.next();
			
			String origin_name = relation_mapping.get(relation_name);
			
			Vector<Argument> args = view_operation.get_full_schema(relation_name, origin_name, c, pst);
			
			Subgoal subgoal = new Subgoal(relation_name, args);
			
			subgoals.add(subgoal);
		}
		
		c.close();
		
		return subgoals;
	}
	
	//each element in head_var array contains two elements (real_table_name and var_name), each element in condition array contains 5 elements (var_name1, relation_name1, operation_name, var_name2, relation_name2), each element in lambda_terms array contains 2 elements (var_name and relation name) 
	public static Query gen_query(String name, HashMap<String, String> relation_mapping, Vector<String[]> head_vars, Vector<String []> condition_str, Vector<String[]> lambda_term_str) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
        Vector<Argument> head_args = new Vector<Argument>();
        
        for(int i = 0; i<head_vars.size(); i++)
        {
        	Argument h_var = new Argument(head_vars.get(i)[1].trim() + populate_db.separator + head_vars.get(i)[0].trim(), head_vars.get(i)[1].trim());
        	
        	head_args.add(h_var);
        }
        
        Subgoal head_subgoal = new Subgoal(name, head_args);
        
        Vector<Conditions> conditions = gen_conditions(condition_str);
        
        Vector<Lambda_term> lambda_terms = gen_lambda_terms(lambda_term_str);
        
        Vector<Subgoal> subgoals = gen_subgoals(relation_mapping);
        
        Query q = new Query(name, head_subgoal, subgoals, lambda_terms, conditions, relation_mapping);
        
        return q;
        
//        
//		
		
		
	}
	
	public static Query gen_query_full(String name, HashMap<String, String> relation_mapping, Vector<String[]> head_vars, Vector<String []> condition_str, Vector<String[]> lambda_term_str) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
        Vector<Argument> head_args = new Vector<Argument>();
        
        for(int i = 0; i<head_vars.size(); i++)
        {
        	Argument h_var = new Argument(head_vars.get(i)[1].trim() + populate_db.separator + head_vars.get(i)[0].trim(), head_vars.get(i)[1].trim());
        	
        	head_args.add(h_var);
        }
        
        Subgoal head_subgoal = new Subgoal(name, head_args);
        
        Vector<Conditions> conditions = gen_conditions(condition_str);
        
        Vector<Lambda_term> lambda_terms = gen_lambda_terms(lambda_term_str);
        
        Vector<Subgoal> subgoals = gen_subgoals_full(relation_mapping);
        
        Query q = new Query(name, head_subgoal, subgoals, lambda_terms, conditions, relation_mapping);
        
        return q;
        
//        
//		
		
		
	}
	
	
	//each element in head_var array contains two elements (real_table_name and var_name), each element in condition array contains 5 elements (var_name1, relation_name1, operation_name, var_name2, relation_name2), each element in lambda_terms array contains 2 elements (var_name and relation name) 

	
	public static void get_query_info(Query query, HashMap<String, String> relation_mapping, Vector<String[]> head_vars, Vector<String []> condition_str, Vector<String[]> lambda_term_str)
	{
		
		Set<String> key_set = query.subgoal_name_mapping.keySet();

		for(Iterator key_set_iter = key_set.iterator(); key_set_iter.hasNext();)
		{
			String key = (String) key_set_iter.next();
			
			relation_mapping.put(key, query.subgoal_name_mapping.get(key));
		}
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			
			Argument arg = (Argument)query.head.args.get(i);
			
			String rel_name = arg.relation_name;
			
			String arg_name = arg.name.substring(arg.name.indexOf(populate_db.separator) + 1, arg.name.length());
			
			String [] rel_args = {arg_name, rel_name};
			
			head_vars.add(rel_args);
		}
		
		gen_conditions_str(query.conditions, condition_str);
		
		gen_lambda_terms_str(query.lambda_term, lambda_term_str);
        
//        
//		
		
		
	}

	
	

}
