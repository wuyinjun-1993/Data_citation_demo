package edu.upenn.cis.citation.set_cover_algo;

import java.util.HashSet;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;

public class cost_function {
	
	public static HashSet<String> query_constants = new HashSet<String>();
	
	public static void cal_query_constants(Query query)
	{
		for(int i = 0; i < query.conditions.size(); i++)
		{
			Conditions condition = query.conditions.get(i);
			
			if(condition.arg2.isConst() && condition.op.get_op_name().equals("="))
			{
				query_constants.add(condition.subgoal1 + populate_db.separator + condition.arg1);
			}
		}
	}
	
	public static double cost1(Tuple tuple, Query query)
	{
		
		int c1 = relation_diff(tuple);
		
		int c2 = arg_diff(tuple);
		
		int c3 = lambda_diff(tuple, query);
		
		return (c1 + c2 + c3 + 1) * 1.0;
	}
	
	
	public static int relation_diff(Tuple tuple)
	{
		return Math.abs(tuple.mapSubgoals.size() - tuple.query.body.size());
	}
	
	public static int arg_diff(Tuple tuple)
	{
		return Math.abs(tuple.getArgs().size() - tuple.query.getHead().args.size());
	}
	
	public static int lambda_diff(Tuple tuple, Query query)
	{
		Vector<Lambda_term> l_terms = tuple.lambda_terms;		
		
		HashSet<String> l_term_strs = new HashSet<String>();
		
		for(int i = 0; i<l_terms.size(); i++)
		{
			l_term_strs.add(l_terms.toString());
		}
		
		HashSet<String> l_terms_copy = (HashSet<String>) l_term_strs.clone();
		
		HashSet<String> query_constants_copy = (HashSet<String>) query_constants.clone();
		
		query_constants_copy.removeAll(l_term_strs);
		
		l_terms_copy.removeAll(query_constants);
		
		return query_constants_copy.size() + l_terms_copy.size();
	}

}
