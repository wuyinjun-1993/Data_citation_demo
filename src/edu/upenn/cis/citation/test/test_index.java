package edu.upenn.cis.citation.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.CoreCover;
import edu.upenn.cis.citation.Corecover.Database;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Corecover.UserLib;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.index.Graph;

public class test_index {
	
	static HashMap<String, Integer> max_author_num = new HashMap<String, Integer>();
	
	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static HashSet<Conditions> valid_conditions = new HashSet<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, Integer> condition_id_mapping = new HashMap<String, Integer>();
	
	public static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, ArrayList<Tuple>> conditions_map = new HashMap<String, ArrayList<Tuple>>();
	
	static HashMap<String, ArrayList<String>> lambda_terms_map = new HashMap<String, ArrayList<String>>();
	
//	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
							
	static String file_name = "tuple_level.xlsx";
	
	static HashMap<Head_strs, Vector<HashSet<String>>> authors = new HashMap<Head_strs, Vector<HashSet<String>>>();
	
	static ArrayList<Lambda_term[]> query_lambda_str = new ArrayList<Lambda_term[]>();
	
	static StringList view_list = new StringList();
	
	static ArrayList<HashMap<String, Integer>> view_query_mapping = null;
	
	public static IntList query_ids = new IntList();
		
	static HashMap<String, Unique_StringList> author_mapping = new HashMap<String, Unique_StringList>(); 
	
	public static HashMap<int[], ArrayList<citation_view_vector> > c_view_map = new HashMap<int[], ArrayList<citation_view_vector>>();
	
	static ResultSet rs = null;
	
	public static int covering_set_num = 0;
	
	static long start = 0;
	
	static long end = 0;
	
	public static double pre_processing_time = 0.0;
	
	public static double query_time = 0.0;
	
	public static double reasoning_time = 0.0;
	
	public static double population_time = 0.0;
	
	public static double aggregation_time = 0.0;
	
	public static long second2nano = 1000000000;
	
	static Tuple [] viewtuples = null;
	
	public static HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping = new HashMap<Head_strs, ArrayList<Integer>>();
	
	static int Resultset_prefix_col_num = 0;
	
	static HashMap<String, ArrayList<Tuple>> view_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
	static HashMap<Argument, ArrayList<Tuple>> head_variable_view_mapping = new HashMap<Argument, ArrayList<Tuple>>();
	
	static HashMap<String, ArrayList<Argument>> head_variable_query_mapping = new HashMap<String, ArrayList<Argument>>();
	
//	static HashMap<String, ArrayList<String>> 
	
//	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
	static ArrayList<Tuple> all_tuples = new ArrayList<Tuple>();
	
	public static int tuple_num = 0;
	
	public static HashMap<Integer, Query> citation_queries = new HashMap<Integer, Query>();
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		
		
//		Connection c = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
//		
//		Vector<Query> queries = Load_views_and_citation_queries.get_views("data/test/query", c, pst);
//
//	    
//		pre_processing(queries.get(0), c, pst);
//		
//		HashSet<Argument> all_covered_head_args = get_max_covered_head_args();
//		
//		System.out.println("all_tuples::" + all_tuples);
//		
//		Graph graph = new Graph(all_tuples, all_covered_head_args);
//		
//		System.out.println(graph.toString());
//		
//		c.close();
	  
//	  Stream<String> a = Stream.of("one", "two");
//	  Stream<String> b = Stream.of("three", "four");
//	  Stream<String> out = Stream.concat(a, b);
//	  out.forEach(System.out::println);
//	  
//	  
//	  ArrayList<String> strings1 = new ArrayList<String>();
//	  
//	  strings1.add("1");
//	  
//	  strings1.add("1");
//	  
//	  strings1.add("2");
//	  
//	  ArrayList<String> strings2 = new ArrayList<String>();
//	  
//	  strings2.add("1");
//	  
//	  strings2.add("2");
//	  
//	  
//	  System.out.println(strings2.containsAll(strings1));
//	  
//	  
//	  
//	  
//	  ArrayList<String> list = new ArrayList<String>();
//	  
//	  for(int i = 0; i<10000000; i++)
//	  {	    	    
//	    list.add(String.valueOf(i));
//	  }
//	  
//ArrayList<String> list1 = new ArrayList<String>();
//      
//      for(int i = 0; i<10000000; i++)
//      {             
//        list1.add(String.valueOf(i));
//      }
//	  
//	  
//	  double start = 0;
//	  
//	  double end = 0;
//	  
//	  double time1 = 0;
//	  
//	  double time2 = 0;
//	  
//start = System.nanoTime();
//      
//      for(int i = 0; i<list1.size(); i++)
//      {
//        list1.set(i, list1.get(i) + "|123");
//      }
//      
//      end = System.nanoTime();
//      
//      time2 = end - start;
//	  
////	  start = System.nanoTime();
////	  
////	  list.parallelStream().forEach((str_list) -> {
////        str_list = str_list + "|123";
////    });
////	  
////	  end = System.nanoTime();
////	  
////	  time1 = end - start;
//	  
//	  
//	  
//	  
//	  System.out.println(time1);
//	  
//	  System.out.println(time2);
	  
	  String s1 = "(F1,F2,F3)(F4,F5,F6)";
	  
	  String s2 = ".*\\(1.*3\\).*";
	  
	  System.out.println(s1.matches(s2));
	  
//	  Pattern P1 = Pattern.compile(s1);
//	  
//	  Pattern P2 = Pattern.compile(s2);
//	  
//	  P1.matcher(s2);
	  
	  
	  
	}
	
	static HashSet<Argument> get_max_covered_head_args()
	{
		HashSet<Argument> covered_head_args = new HashSet<Argument>();
		
		for(int i = 0; i<all_tuples.size(); i++)
		{
			Tuple tuple = all_tuples.get(i);
			
			covered_head_args.addAll(tuple.getArgs());
		}
		
		return covered_head_args;
	}
	
	static HashSet<Tuple> check_distinguished_variables(HashSet<Tuple> viewtuples, Query q)
	{
		HashSet<Tuple> update_tuples = new HashSet<Tuple>();
		
		for(Iterator iter = viewtuples.iterator(); iter.hasNext();)
		{			
			Tuple tuple = (Tuple)iter.next();
			
//			System.out.println(tuple);
			
			if(check_head_mapping(tuple.getArgs(), q) || check_condition_mapping(tuple.getArgs(), q))
			{
				update_tuples.add(tuple);
			}
			
		}
		
		return update_tuples;
	}
	
	static boolean check_head_mapping(Vector<Argument> args, Query q)
	{
		
		Vector<Argument> q_head_args = q.head.args;
		
		for(int i = 0; i<args.size(); i++)
		{
			for(int j = 0; j<q_head_args.size(); j++)
			{	
				if(args.get(i).toString().equals(q_head_args.get(j).toString()) && args.get(i).relation_name.equals(q_head_args.get(j).relation_name))
					return true;
			}
		}
		
		return false;
		
	}
	
	static boolean check_condition_mapping(Vector<Argument> args, Query q)
	{
		for(int i = 0; i<args.size(); i++)
		{
			for(int j = 0; j<q.conditions.size(); j++)
			{
				Conditions condition = q.conditions.get(j);
				
//				System.out.print(args.get(i) + ":::" + condition.subgoal1 + "_" + condition.arg1);
				
//				if(!condition.arg2.isConst())
//					System.out.print(condition.subgoal2 + "_" + condition.arg2);
//				
//				System.out.println();
				
				if(args.get(i).toString().equals(condition.subgoal1 + populate_db.separator + condition.arg1) || (condition.subgoal2 != null && condition.subgoal2.isEmpty() && args.get(i).toString().equals(condition.subgoal2 + populate_db.separator + condition.arg2)))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	static void build_view_tuple_mapping(Tuple tuple, HashSet<Subgoal> subgoals)
	{
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			Subgoal subgoal = (Subgoal) iter.next();
			
			String key = tuple.name + populate_db.separator + subgoal.name;
			
			ArrayList<Tuple> curr_tuples = view_tuple_mapping.get(key);
			
			if(curr_tuples == null)
			{
				curr_tuples = new ArrayList<Tuple>();
				
				curr_tuples.add(tuple);
				
				view_tuple_mapping.put(key, curr_tuples);
			}
			else
			{
				curr_tuples.add(tuple);
				
				view_tuple_mapping.put(key, curr_tuples);
			}
			
		}
	}
	
	static void build_head_variable_mapping(Query view, Tuple tuple)
	{
		for(int i = 0; i<view.head.args.size(); i++)
		{
			
			Argument source_attr_name = (Argument) view.head.args.get(i);
			
			Argument target_attr_name = (Argument)tuple.phi.apply(source_attr_name);
			
			ArrayList<Tuple> tuples = head_variable_view_mapping.get(target_attr_name);
			
			if(tuples == null)
			{
				tuples = new ArrayList<Tuple>();
				
				tuples.add(tuple);
			}
			else
				tuples.add(tuple);
			
			head_variable_view_mapping.put(target_attr_name, tuples);
		}
	}
	
	public static HashSet<Tuple> pre_processing(Query q, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
	
		build_query_head_variable_mapping(q);
		
		Vector<Query> views = populate_db.get_views_schema(c, pst);
		
		for(int k = 0; k<views.size(); k++)
		{
			view_list.add(views.get(k).name);
			
			view_mapping.put(views.get(k).name, views.get(k));
		}

		
//	    q = q.minimize();

	    // construct the canonical db
	    Database canDb = CoreCover.constructCanonicalDB(q);
	    UserLib.myprintln("canDb = " + canDb.toString());

	    // compute view tuples
	    HashSet viewTuples = CoreCover.computeViewTuples(canDb, views);
	    
	    viewTuples = check_distinguished_variables(viewTuples, q);
	    	    	    
	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
	    {
	    	Tuple tuple = (Tuple)iter.next();
	    	
	    	all_tuples.add(tuple);
	    	
//	    	System.out.println(tuple);
	    	
	    	HashSet<Subgoal> mapped_relations = tuple.getTargetSubgoals();
	    	
	    	build_view_tuple_mapping(tuple, mapped_relations);
	    	
	    	
	    	build_head_variable_mapping(tuple.query, tuple);
//	    	if(tuple_mapping.get(tuple.name) == null)
//	    	{
//	    		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
//	    		
//	    		tuples.add(tuple);
//	    		
//	    		tuple_mapping.put(tuple.name, tuples);
//	    	}
//	    	else
//	    	{
//	    		ArrayList<Tuple> tuples = tuple_mapping.get(tuple.name);
//	    		
//	    		tuples.add(tuple);
//	    		
//	    		tuple_mapping.put(tuple.name, tuples);
//	    	}
	    	
	    	
	    	for(int i = 0; i<tuple.conditions.size(); i++)
	    	{
	    		
	    		if(tuple.conditions.get(i).arg2.isConst())
	    			continue;
	    		
	    		String condition_str = tuple.conditions.get(i).toString();
	    		
	    		ArrayList<Tuple> curr_tuples = conditions_map.get(condition_str);
	    		
	    		if(curr_tuples == null)
	    		{
	    			curr_tuples =  new ArrayList<Tuple>();
	    			
	    			curr_tuples.add(tuple);
	    			
	    			conditions_map.put(condition_str, curr_tuples);
	    		}
	    		else
	    		{	    			
	    			curr_tuples.add(tuple);
	    			
	    			conditions_map.put(condition_str, curr_tuples);
	    		}
	    			    		
//	    		if(condition_id_mapping.get(condition_str) == null)
	    		{
//	    			condition_id_mapping.put(condition_str, valid_conditions.size());
	    			
	    			valid_conditions.add(tuple.conditions.get(i));
	    		}
	    	}
	    	
	    	for(int i = 0; i<tuple.lambda_terms.size(); i++)
	    	{
	    		String lambda_str = tuple.lambda_terms.get(i).toString();
	    			    		
	    		if(lambda_term_id_mapping.get(lambda_str) == null)
	    		{
	    			lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
	    			
	    			valid_lambda_terms.add(tuple.lambda_terms.get(i));
	    		}
	    	}
	    	
	    }

	    // compute tuple-cores
	    
	    gen_citation1.get_all_query_ids(query_ids, c, pst);
	    
	    view_query_mapping = new ArrayList<HashMap<String, Integer>>(view_list.size);
	    		
		for(int i = 0; i<query_ids.size; i++)
		{
			query_lambda_str.add(null);
		}
	    	    	    
	    return viewTuples;
	    
	    
	}
	
	
	static void build_query_head_variable_mapping(Query query)
	{
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument head_arg = (Argument)query.head.args.get(i);
			
			String head_arg_str = head_arg.toString();
			
			int partition_id = head_arg_str.indexOf(populate_db.separator);
			
			String relation_name = head_arg_str.substring(0, partition_id);
			
			ArrayList<Argument> head_variable_strs = head_variable_query_mapping.get(relation_name);
			
			if(head_variable_strs == null)
			{
				head_variable_strs = new ArrayList<Argument>();
			}
			
			head_variable_strs.add(head_arg);
			
			head_variable_query_mapping.put(relation_name, head_variable_strs);
		}
	}

}
