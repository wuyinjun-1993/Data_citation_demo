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
	
	static Vector<Lambda_term> gen_lambda_terms(Vector<String []> lambda_term_str)
	{
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<lambda_term_str.size(); i++)
		{
			Lambda_term l_term = new Lambda_term(lambda_term_str.get(i)[1] + "_" + lambda_term_str.get(i)[0], lambda_term_str.get(i)[1]);
			
			lambda_terms.add(l_term);
		}
		
		return lambda_terms;
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
        	Argument h_var = new Argument(head_vars.get(i)[1].trim() + "_" + head_vars.get(i)[0].trim(), head_vars.get(i)[1].trim());
        	
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


}
