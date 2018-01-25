package edu.upenn.cis.citation.reasoning2;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.junit.Assert.*;


import edu.upenn.cis.citation.Corecover.*;
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
import edu.upenn.cis.citation.aggregation.Aggregation1;
import edu.upenn.cis.citation.aggregation.Aggregation2;
import edu.upenn.cis.citation.aggregation.Aggregation3;
import edu.upenn.cis.citation.aggregation.Aggregation4;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.Query_lm_authors;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Init_examples;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.output.output2excel;
import edu.upenn.cis.citation.set_cover_algo.cost_function;
import edu.upenn.cis.citation.user_query.query_storage;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class Tuple_reasoning1_full_min_test2 {
		
	static HashMap<String, Integer> max_author_num = new HashMap<String, Integer>();
			
	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static HashSet<Conditions> valid_conditions = new HashSet<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, Integer> condition_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, ArrayList<Tuple>> conditions_map = new HashMap<String, ArrayList<Tuple>>();
	
	static HashMap<String, ArrayList<String>> lambda_terms_map = new HashMap<String, ArrayList<String>>();
	
	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
							
	static String file_name = "tuple_level.xlsx";
	
	static HashMap<Head_strs, Vector<HashSet<String>>> authors = new HashMap<Head_strs, Vector<HashSet<String>>>();
	
	static ArrayList<Lambda_term[]> query_lambda_str = new ArrayList<Lambda_term[]>();
	
	static StringList view_list = new StringList();
	
	static ArrayList<HashMap<String, Integer>> view_query_mapping = null;
	
	public static IntList query_ids = new IntList();
		
	static HashMap<String, Unique_StringList> author_mapping = new HashMap<String, Unique_StringList>(); 
	
	public static HashMap<int[], citation_view_vector > c_view_map = new HashMap<int[], citation_view_vector>();
	
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
	
	static HashMap<String, Double> view_mapping_cost = new HashMap<String, Double>();
	
	static StringList view_mapping_list = new StringList();
	
	public static ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	
//	static ArrayList<int[]> view_mapping_global_valid = new ArrayList<int[]>();
	static int[] view_mapping_global_valid = null;
	
	public static citation_view_vector covering_sets_query = null; 
	
	static int[] entire_interval = new int[2];
	
	static HashMap<Argument, ArrayList<Tuple>> head_variable_view_mapping = new HashMap<Argument, ArrayList<Tuple>>();
	
	static HashMap<String, ArrayList<Argument>> head_variable_query_mapping = new HashMap<String, ArrayList<Argument>>();

	static HashMap<String, ArrayList<Tuple>> view_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
	public static int tuple_num = 0;
	
	public static int group_num = 0;
	
	public static boolean prepare_info = true;
	
	static ArrayList<HashSet<String>> single_subgoal_relations = new ArrayList<HashSet<String>>();
	
	public static HashMap<Integer, Query> citation_queries = new HashMap<Integer, Query>();

	static ArrayList<Vector<Argument>> single_view_head_args = new ArrayList<Vector<Argument>>();
	
	public static boolean test_case = true;
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
	
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		Vector<Query> queries = Load_views_and_citation_queries.get_views("data/test/query", c, pst);
		
		System.out.println(queries.get(0));
		
		tuple_reasoning(queries.get(0), c, pst);
		
		Set<int[]> keys = c_view_map.keySet();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			int [] key = (int []) iter.next();
			
			citation_view_vector c_views = c_view_map.get(key);
			
			System.out.println(key[0] + "," + key[1] + ":::" + c_views);
		}
		
//		
//		ArrayList<Integer> b = new ArrayList<Integer>();
//		
//		for(int i = 0; i<1000; i++)
//		{
//			a.add(i);
//			
//			b.add(i);
//		}
//		
//		ArrayList<Integer> result = new ArrayList<Integer>();
//
//		
//		long start_time = 0;
//		
//		long end_time = 0;
//		
//		long time1 = 0;
//		
//		long time2 = 0;
//		
//		start_time = System.nanoTime();
//		for(int j = 0; j < 1000; j++)
//		{
//			IntStream.range(0, a.size())
//	         .forEach(i -> result.add(a.get(i)*b.get(i)));
//
//		}
//		end_time = System.nanoTime();
//		
//		time1 = end_time - start_time;
//		
//		result.clear();
//		
//		start_time = System.nanoTime();
//		
//		for(int i = 0; i < 1000; i++)
//		{
//			for(int j = 0; j<a.size(); j++)
//			{
//				result.add(a.get(j) * b.get(j));
//			}
//		}
//		
//		end_time = System.nanoTime();
//		
//		time2 = end_time - start_time;
//		
//		System.out.println("t1:::::::::" + time1);
//		
//		System.out.println("t2:::::::::" + time2);
//		int len = 100000;
//		
//		HashSet<String> list1 = new HashSet<String>(len);
//		
//		StringList list2 = new StringList(len);
//		
//		ArrayList<String> list3 = new ArrayList<String>(len);
//		
//		for(int i = 0; i<len; i++)
//		{
//			list1.add(String.valueOf(i));
//			
//			list2.add(String.valueOf(i));
//			
//			list3.add(String.valueOf(i));
//		}
//		
//		Random r = new Random();
//		
//		HashSet<Integer> indexes = new HashSet<Integer>();
//		
//		while(indexes.size() < 1000)
//		{
//			int value = r.nextInt(100000);
//			
//			indexes.add(value);
//		}
//		
//		long t1 = 0;
//		
//		long t2 = 0;
//		
//		long t3 = 0;
//		
//		start = System.nanoTime();
//		
//		for(Iterator iter = indexes.iterator(); iter.hasNext();)
//		{
//			int id = (int) iter.next();
//						
//			list1.contains(String.valueOf(id));
//		}
//		
//		end = System.nanoTime();
//		
//		t1 = end - start;
//		
//		start = System.nanoTime();
//		
//		for(Iterator iter = indexes.iterator(); iter.hasNext();)
//		{
//			int id = (int) iter.next();
//			
//			int value = list2.find(String.valueOf(id));
//		}
//		
//		end = System.nanoTime();
//		
//		t2 = end - start;
//		
//		start = System.nanoTime();
//		
//		for(Iterator iter = indexes.iterator(); iter.hasNext();)
//		{
//			int id = (int) iter.next();
//			
//			int value = list3.indexOf(String.valueOf(id));
//		}
//		
//		end = System.nanoTime();
//		
//		t3 = end - start;
//		
//		System.out.println(t1);
//		
//		System.out.println(t2);
//		
//		System.out.println(t3);
////		
////		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
//		
//		String query = "select * from family";
//		
//		pst = c.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//		
//		ResultSet rs = pst.executeQuery();
//		
//		while(rs.next())
//		{
//			System.out.println(rs.getString(1));
//		}
//		
//		rs.beforeFirst();
//		
//		System.out.println("new::");
//		
//		while(rs.next())
//		{
//			System.out.println(rs.getString(1));
//		}
//		
//		
////		Query q = query_storage.get_query_by_id(1);
//	    
//	    Query q = gen_query();
//		
//		System.out.println(q);
//		
////		String query = "q(name):object(), in_gtip = 'true'";
////		
//		
//		
//		
//		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map3 = null;
//		
//		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = null;
//		
//		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = null;
//		
//		Vector<Head_strs> head_vals = new Vector<Head_strs>();
//		
//		long t0 = 0;
//		
////		long t1 = 0;
////		
////		long t2 = 0;
////		
////		long t3 = 0;
//		
//		long t4 = 0;
//		
//		double time0 = 0;
//		
//		double time1 = 0;
//		
//		double time2 = 0;
//		
//		
//		t0 = System.nanoTime();
//		
////		for(int i = 0; i<100; i++)
////		{
//			
//			citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
//			
//			HashMap<Head_strs, HashSet<String> > citation_strs1 = new HashMap<Head_strs, HashSet<String> >();
//			
//			tuple_reasoning(q, citation_strs1, citation_view_map1, c, pst);
//			
//			HashMap<Head_strs, Vector<HashSet<String>>>author2 = authors;
//			
//			t1 = System.nanoTime();
//			
//			time0 = (t1 - t0)*1.0 / 1000000000;
//			
//			System.out.println("time in tuple level:" + time0);
//			
//			t1 = System.nanoTime();
//			
//			HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1_1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
//			
//			HashMap<Head_strs, HashSet<String> > citation_strs1_1 = new HashMap<Head_strs, HashSet<String> >();
//			
////			Vector<Vector<citation_view_vector>> c_views1_1 = tuple_reasoning(q, citation_strs1_1, citation_view_map1_1, c, pst);
//			
//			t2 = System.nanoTime();
//			
////			HashMap<Head_strs, Vector<HashSet<String>>>author1 = Tuple_reasoning2.authors;
//			
//			compare(citation_view_map1, citation_view_map1_1);
//			
////			compare_authors(author1, author2);
//
////		}
//		
//		
////		t1 = System.nanoTime();
//		
////		System.out.println("method1");
//		
//		
//		
//		
//		
//		
//				
//		
//		
////		System.out.println("method2");
//		
////		for(int i = 0; i<100; i++)
////		{
////			
////			citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
////
////			HashMap<Head_strs, Vector<String> > citation_strs3 = new HashMap<Head_strs, Vector<String> >();
////
////			
////			Vector<Vector<citation_view_vector>> c_views1 = Tuple_reasoning2.tuple_reasoning(q, citation_strs3, citation_view_map2);
////
////		}
//		
//		
//		
//		
//		
//		
////		for(int i = 0; i<100; i++)
////		{
////			
////			HashMap<Head_strs, Vector<String> > citation_strs2 = new HashMap<Head_strs, Vector<String> >();
////			
////			citation_view_map3 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
////
////
////			
////			Vector<Vector<citation_view_vector>> c_views2 = Tuple_reasoning2_opt.tuple_reasoning(q, citation_strs2, citation_view_map3);
////
////		}
////				
//		
//		t3 = System.nanoTime();
//		
//		
//		
////		time1 = (t3 - t2)/ 100;
//		
//		time2 = (t2 - t1)*1.0 / 1000000000;
//		
//		
////		System.out.println("time with opt:" + time1);
//		
//		System.out.println("time with opt:" + time2);
//		
//		c.close();
//		
////		compare(citation_view_map2, citation_view_map3);
////		
////		compare(citation_view_map1, citation_view_map2);
//////		
////		compare(citation_view_map1, citation_view_map3);
//		
//		
////		Vector<String> agg_citations = tuple_gen_agg_citations(c_views);
////		
////		Vector<Integer> ids = new Vector<Integer>();
////		
////		Vector<String> subset_agg_citations = tuple_gen_agg_citations(c_views, ids);
//		
//		

		
		
		
	}
	
	public static void compare_authors(HashMap<Head_strs, Vector<HashSet<String>>> authors1, HashMap<Head_strs, Vector<HashSet<String>>> authors2)
	{
		Set<Head_strs> key_set = authors1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			Vector<HashSet<String>> a1 = authors1.get(h_val);
			
			Vector<HashSet<String>> a2 = authors2.get(h_val);
			
			for(int i = 0; i<a2.size(); i++)
			{
				if(a2.get(i).isEmpty())
				{
					a2.remove(i);
					
					i--;
				}
			}
			
			HashSet<HashSet<String>> author_s1 = new HashSet<HashSet<String>>();
			
			HashSet<HashSet<String>> author_s2 = new HashSet<HashSet<String>>();
			
			for(int k = 0; k<a1.size(); k++)
			{
				author_s1.add(a1.get(k));
			}
			
			for(int k = 0; k<a2.size(); k++)
			{
				author_s2.add(a2.get(k));
			}
			
			if(!author_s1.equals(author_s2))
				assertEquals(1, 0);
			
			
//			System.out.println(h_val);
			
//			do_compare_authors(a1, a2);
		}
	}
	
	
	static void do_compare_authors(Vector<HashSet<String>> a1, Vector<HashSet<String>> a2)
	{
		int i = 0;
		
		int j = 0;
		
		if(a1.size() != a2.size())
		{
			assertEquals(1, 0);
		}
		
		for(i = 0; i<a1.size(); i++)
		{
			
			HashSet<String> a_list1 = a1.get(i);
			
			for(j = 0; j<a2.size(); j++)
			{
				HashSet<String> a_list2 = a2.get(j);
				
				if(a_list1.equals(a_list2))
					break;
				
			}
			
			if(j >= a2.size())
			{
				System.out.println(i + ":::" + j);
				
				System.out.println(a1.get(i));
				
				System.out.println(a2.get(i));
				
				
				
				
				
				assertEquals(1, 0);
			}
		}
	}
	
	public static void compare(HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2)
	{
		
		System.out.println("COMPARE::::");
		
		Set<Head_strs> key_set = citation_view_map1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			Vector<Vector<citation_view_vector>> c_views1 = citation_view_map1.get(h_val);
			
			Vector<Vector<citation_view_vector>> c_views2 = citation_view_map2.get(h_val);
			
//			System.out.println(h_val);
//			
//			System.out.println(c_views1);
//			
//			System.out.println(c_views2);
			
			compare(c_views1, c_views2);
		}
		
		
		
	}
	
	public static void compare_citation(HashMap<Head_strs, HashSet<String>> citation_view_map1, HashMap<Head_strs, HashSet<String>> citation_view_map2)
	{
		
		System.out.println("COMPARE::::");
		
		Set<Head_strs> key_set = citation_view_map1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			HashSet<String> c_views1 = citation_view_map1.get(h_val);
			
			HashSet<String> c_views2 = citation_view_map2.get(h_val);
			
			System.out.println(h_val.toString());
			
			if(!c_views1.equals(c_views2))
			{
				assertEquals(1, 0);

			}
			
//			compare_citation(c_views1, c_views2);
		}
		
		
		
	}
//	static void compare_citation(HashSet<String> c_views, HashSet<String> c_views1)
//	{
//		for(int i = 0; i<c_views.size(); i++)
//		{
//			int j = 0;
//			
////			System.out.println(c_views.get(i));
//			
//			for(j = 0; j<c_views1.size(); j++)
//			{
//				if(c_views.get(i).equals(c_views1.get(j)))
//					break;
//			}
//			
//			if(j >= c_views1.size())
//			{
////				System.out.println("errrror");
////				
////				System.out.println(c_views.get(i).toString());
////				
////				System.out.println(c_views1.get(0).toString());
//				
//			}
//		}
//	}
	
	
//	public static void compare(HashMap<Head_strs, Vector<String>> citation_view_map1, HashMap<Head_strs, Vector<String>> citation_view_map2)
//	{
//		
//		System.out.println("COMPARE::::");
//		
//		Set<Head_strs> key_set = citation_view_map1.keySet();
//		
//		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
//		{
//			Head_strs h_val = (Head_strs) h_val_iter.next();
//	
//			Vector<String> c_views1 = citation_view_map1.get(h_val);
//			
//			Vector<String> c_views2 = citation_view_map2.get(h_val);
//			
////			System.out.println(h_val.toString());
//			
//			compare(c_views1, c_views2);
//		}
//		
//		
//		
//	}
	
	static void compare(Vector<Vector<citation_view_vector>> c_views, Vector<Vector<citation_view_vector>> c_views1)
	{
		for(int i = 0; i<c_views.size(); i++)
		{
			int j = 0;
			
//			System.out.println(c_views.get(i));
			
			for(j = 0; j<c_views1.size(); j++)
			{
				if(do_compare(c_views.get(i), c_views1.get(j)))
					break;
			}
			
			if(j >= c_views1.size())
			{
				assertEquals(1, 0);
//				System.out.println("errrror");
//				
//				System.out.println(c_views.get(i).toString());
//				
//				System.out.println(c_views1.get(0).toString());
				
				assertEquals(1, 0);
			}
		}
	}
	
	
	static boolean do_compare(Vector<citation_view_vector> c1, Vector<citation_view_vector> c2)
	{
		if(c1.size() != c2.size())
			return false;
		
		for(int i = 0; i<c1.size(); i++)
		{
			int j = 0;
			
//			System.out.println("id1:::" + c1.get(i).index_str);

			
			for(j = 0; j<c2.size(); j++)
			{
				
				
//				System.out.println("id2:::" + c2.get(j).index_str);
				
				if(c1.get(i).equals(c2.get(j)))
				{
					break;
				}
			}
			if(j >= c2.size())
			{
//				System.out.println("id1:::" + c1.get(i));
				return false;
			}
		}
		return true;
	}
	
	
	static Query gen_query() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("gpcr" + populate_db.separator + "object_id", "gpcr"));
				
		
		head_args.add(new Argument("gpcr1" + populate_db.separator + "object_id", "gpcr1"));
//		
		head_args.add(new Argument("object" + populate_db.separator + "object_id", "object"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		
//		head_args.add(new Argument("introduction_family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1_family_id", "introduction1"));
////				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("gpcr1", "gpcr", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("object", "object", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
////		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
		subgoals.add(new Subgoal("gpcr", args1));
		
		subgoals.add(new Subgoal("gpcr1", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		subgoals.add(new Subgoal("object", args3));
		
//		subgoals.add(new Subgoal("gpcr", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_less_equal(), new Argument("3"), new String()));
//		
		conditions.add(new Conditions(new Argument("object_id", "gpcr1"), "gpcr1", new op_less_equal(), new Argument("4"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_less_equal(), new Argument("3"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		subgoal_name_mapping.put("gpcr1", "gpcr");
//		
		subgoal_name_mapping.put("gpcr", "gpcr");
		
//		subgoal_name_mapping.put("family3", "family");
				
//		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");
////
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");


		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
//	
	
//	public static Query gen_query() throws SQLException, ClassNotFoundException
//	{
//		Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager
//           .getConnection(populate_db.db_url,
//       	        populate_db.usr_name,populate_db.passwd);
//		
//        PreparedStatement pst = null;
//        
//		Vector<Argument> head_args = new Vector<Argument>();
//		
//		head_args.add(new Argument("introduction_family_id", "introduction"));
//		
//		head_args.add(new Argument("object_in_gtip", "object"));
//				
//		head_args.add(new Argument("object_structural_info_comments", "object"));
//		
//		head_args.add(new Argument("object_only_iuphar", "object"));
//		
//		head_args.add(new Argument("object_in_cgtp", "object"));
//		
//		head_args.add(new Argument("object_grac_comments", "object"));
//		
////		head_args.add(new Argument("gpcr_object_id", "gpcr"));
//		
//		
//		
////		head_args.add(new Argument("gpcr1_object_id", "object"));
//////		
////		head_args.add(new Argument("object_object_id", "gpcr"));
//				
//		
////		head_args.add(new Argument("family3_family_id", "family3"));
//		
////		head_args.add(new Argument("introduction_family_id", "introduction"));
//
//		
////		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//////				
////		head_args.add(new Argument("introduction2_family_id", "introduction2"));
//		
////		head_args.add(new Argument("introduction3_family_id", "introduction3"));
//		
//		Subgoal head = new Subgoal("q", head_args);
//		
//		Vector<Subgoal> subgoals = new Vector<Subgoal>();
//		
////		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
//		
//		Vector<Argument> args2 = view_operation.get_full_schema("introduction", "introduction", c, pst);
//		
//		
////		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
//		
////		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
//		
//		Vector<Argument> args3 = view_operation.get_full_schema("object", "object", c, pst);
//		
////		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//////		
////		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
//		
////		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);
//
//		
////		subgoals.add(new Subgoal("gpcr", args1));
//		
//		subgoals.add(new Subgoal("introduction", args2));
////		
////		subgoals.add(new Subgoal("family2", args6));
//		
////		subgoals.add(new Subgoal("family3", args7));
//				
//		subgoals.add(new Subgoal("object", args3));
//		
////		subgoals.add(new Subgoal("gpcr", args4));
////		
////		subgoals.add(new Subgoal("introduction2", args5));
//		
////		subgoals.add(new Subgoal("introduction3", args8));
//		
//		Vector<Conditions> conditions = new Vector<Conditions>();
//		
////		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_less_equal(), new Argument("5"), new String()));
////		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("3"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
//		
////		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
////		
//		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_less_equal(), new Argument("6"), new String()));
//		
////		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));
//
////		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));
//
//		
////		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
//		
//		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
//		
//		subgoal_name_mapping.put("object", "object");
//		
//		subgoal_name_mapping.put("introduction", "introduction");
////		
////		subgoal_name_mapping.put("gpcr", "gpcr");
//		
////		subgoal_name_mapping.put("family3", "family");
//				
////		subgoal_name_mapping.put("introduction", "introduction");
//		
////		subgoal_name_mapping.put("introduction1", "introduction");
//////
////		subgoal_name_mapping.put("introduction2", "introduction");
//		
////		subgoal_name_mapping.put("introduction3", "introduction");
//
//
//		
//		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
//		
////		System.out.println(q);
//		
//		c.close();
//		
//		return q;
//		
//		
//	}
//	
	public static Query gen_query(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("contributor2ligand_first_names", "contributor2ligand"));
		
		head_args.add(new Argument("contributor2ligand_surname", "contributor2ligand"));
				
//		head_args.add(new Argument("object_structural_info_comments", "object"));
//		
//		head_args.add(new Argument("object_only_iuphar", "object"));
//		
//		head_args.add(new Argument("object_in_cgtp", "object"));
//		
//		head_args.add(new Argument("object_grac_comments", "object"));
		
//		head_args.add(new Argument("gpcr_object_id", "gpcr"));
		
		
		
//		head_args.add(new Argument("gpcr1_object_id", "object"));
////		
//		head_args.add(new Argument("object_object_id", "gpcr"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		
//		head_args.add(new Argument("introduction_family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1_family_id", "introduction1"));
////				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
//		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("contributor2ligand", "contributor2ligand", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("ligand", "ligand", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
////		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
//		subgoals.add(new Subgoal("gpcr", args1));
		
		subgoals.add(new Subgoal("contributor2ligand", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		subgoals.add(new Subgoal("ligand", args3));
		
//		subgoals.add(new Subgoal("gpcr", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_less_equal(), new Argument("5"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("3"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "contributor2ligand"), "contributor2ligand"));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("contributor2ligand", "contributor2ligand");
		
		subgoal_name_mapping.put("ligand", "ligand");
//		
//		subgoal_name_mapping.put("gpcr", "gpcr");
		
//		subgoal_name_mapping.put("family3", "family");
				
//		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");
////
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");

		Vector<Lambda_term> l_term = new Vector<Lambda_term>();;
		
		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
//		System.out.println(q);
				
		return q;
		
		
	}
	
	
//	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views) throws ClassNotFoundException, SQLException, JSONException
//	{
//		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggregate(c_views);
//		
//		Vector<String> citation_aggs = new Vector<String>();
//		
//		for(int i = 0; i<agg_res.size(); i++)
//		{
////			output_vec_com(agg_res.get(i));
//			
//			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
//			
//			citation_aggs.add(str);
////			
//			System.out.print(agg_res.get(i).get(0).toString() + ":");
//			
//			System.out.println(str);
//
//		}
//		
//		return citation_aggs;
//	}
	
	public static HashSet<String> tuple_gen_agg_citations(Query query, ArrayList<Head_strs> heads) throws ClassNotFoundException, SQLException, JSONException
	{
		int start_pos = query.head.args.size() + query.body.size();
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		HashSet<String> citations = Aggregation4.do_agg_intersection_min(rs, c_view_map, covering_sets_query, start_pos, heads, head_strs_rows_mapping, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
		
		c.close();
		
		return citations;
	}
	
	public static HashSet<String> tuple_gen_agg_citations(Query query, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, JSONException
	{
		start = System.nanoTime();
		
		int start_pos = Resultset_prefix_col_num;
		
	    
	    //rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst
		HashSet<String> citations = Aggregation4.do_agg_intersection_min(rs, covering_sets_query, entire_interval, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
				
		end = System.nanoTime();
		
		aggregation_time = (end - start) * 1.0/second2nano;
		
		return citations;
	}
	
//	public static HashSet<String> tuple_gen_agg_citations(Query query, HashMap<String, String> view_citation_mapping) throws ClassNotFoundException, SQLException, JSONException
//	{
//		start = System.nanoTime();
//		
//		int start_pos = Resultset_prefix_col_num;
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
//		
//	    
//	    //rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst
//		HashSet<String> citations = Aggregation3.do_agg_intersection(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, view_citation_mapping, c, pst, true);
//		
//		c.close();
//		
//		end = System.nanoTime();
//		
//		aggregation_time = (end - start) * 1.0/second2nano;
//		
//		return citations;
//	}
	
//	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views, Vector<Integer> ids) throws ClassNotFoundException, SQLException, JSONException
//	{
//		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views, ids);
//		
//		Vector<String> citation_aggs = new Vector<String>();
//		
//		for(int i = 0; i<agg_res.size(); i++)
//		{
//			
//			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
//			
//			citation_aggs.add(str);
////			
////			System.out.print(agg_res.get(i).get(0).toString() + ":");
////			
////			System.out.println(str);
//
//		}
//		
//		return citation_aggs;
//	}
	
	
	public static void tuple_reasoning(Query query, String f_name, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
//		query = get_full_query(query);
//		
//		System.out.println(query);
		
		file_name = f_name;
				
		tuple_reasoning(query, c, pst);
		
		
		
//		return citation_views;
	}
	
	public static void reset() throws SQLException
	{
		view_mapping.clear();
		
		valid_conditions.clear();
		
		valid_lambda_terms.clear();
		
		condition_id_mapping.clear();
		
		lambda_term_id_mapping.clear();
		
		conditions_map.clear();
		
		lambda_terms_map.clear();
		
		tuple_mapping.clear();
										
		authors.clear();
		
		view_query_mapping = null;
				
		author_mapping.clear();
		
		query_lambda_str.clear();
		
		c_view_map.clear();
		
		if(rs != null)
			rs.close();
		
		covering_set_num = 0;
		
		Resultset_prefix_col_num = 0;
		
		pre_processing_time = 0.0;
		
		query_time = 0.0;
		
		reasoning_time = 0.0;
		
		population_time = 0.0;
		
		aggregation_time = 0.0;
		
		max_author_num.put("author", -1);
		
		max_author_num.put("info", -1);
		
		max_author_num.put("Investigator", -1);
		
		max_author_num.put("Program", -1);
		
		max_author_num.put("Funding", -1);
		
		view_mapping_cost.clear();
		
		view_mapping_list = new StringList();
		
		covering_sets_query = null;
		
		entire_interval[0] = 0;
		
		tuple_num = 0;
		
		group_num = 0;
		
		head_strs_rows_mapping.clear();
		
		citation_queries.clear();
	}
	
	public static String get_full_query(String query) throws ClassNotFoundException, SQLException
	{
		
		
		String head = query.split(":")[0];
		
		String []subgoals = query.split(":")[1].split(",");
		
		String subgoal_str = new String();
		
		boolean start = false;
		
		for(int i = 0; i<subgoals.length; i++)
		{
			if(subgoals[i].contains("()"))
			{
				if(start)
				{
					subgoal_str += ",";
				}
				
				subgoals[i] = subgoals[i].substring(0, subgoals[i].length() - 2).trim();
				
				subgoal_str += subgoals[i] + "(";
				
				Vector<String> args = Parse_datalog.get_columns(subgoals[i]);
				
				for(int k = 0; k<args.size(); k++)
				{
					if(k >= 1)
						subgoal_str += ",";
					
					subgoal_str += args.get(k);
				}
				
				subgoal_str += ")";
				
				start = true;
			}
			else
			{
				subgoal_str += "," + subgoals[i];
			}
		}
		
		String update_q = head + ":" + subgoal_str;
		
		return update_q;
	}
	
	static Vector<String> get_args(String table_name) throws SQLException, ClassNotFoundException
	{
		 Class.forName("org.postgresql.Driver");
         Connection c = DriverManager
            .getConnection(populate_db.db_url,
        	        populate_db.usr_name,populate_db.passwd);
         
//         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
         
         
         String get_arg_sql = "select arguments from subgoal_arguments where subgoal_names = '" + table_name + "'";
         
         PreparedStatement pst = c.prepareStatement(get_arg_sql);
         
         ResultSet rs = pst.executeQuery();
         
         
         Vector<String> args = new Vector<String>();
         
         while(rs.next())
         {
        	 args.add(rs.getString(1));
         }
         
         
         c.close();
         
         return args;
	}
	
	public static void gen_citation_all(Vector<Vector<citation_view_vector>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<citation_view_vector> c_vec = citation_views.get(i);
			
			for(int j = 0; j< c_vec.size(); j++)
			{
				gen_citation(c_vec.get(j));
			}
		}
	}
	
	public static void output_sql(Vector<Vector<citation_view_vector>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<citation_view_vector> c_vec = citation_views.get(i);
			
			for(int j = 0; j<c_vec.size(); j++)
			{
				if(j >= 1)
					System.out.print(",");
				System.out.print(c_vec.get(j).toString());
//				gen_citation(c_vec.get(j));
			}
			
			System.out.println();
		}
	}
	
//	public static void query_lambda_term_table(Vector<String> subgoal_names, Query query, Connection c, PreparedStatement pst) throws SQLException
//	{
//		String q = "select * from view2lambda_term";
//		
////		HashMap<String, String[]> lambda_term_table_map = new HashMap<String, String[]>();
//		
//		pst = c.prepareStatement(q);
//		
//		ResultSet rs = pst.executeQuery();
//					
//		while(rs.next())
//		{
//			String view_name = rs.getString(1);
//			
//			String lambda_term = rs.getString(2);
//			
//			String table_name = rs.getString(3);
//			
//			if(subgoal_names.contains(table_name))
//			{
//				HashSet<String> lambda_terms = return_vals.get(table_name); 
//				
//				if(lambda_terms==null)
//				{
//					lambda_terms = new HashSet<String>();
//					
//					lambda_terms.add(lambda_term);
//					
//					return_vals.put(table_name, lambda_terms);
//					
//					String [] com = new String[2];
//					
//					com[0] = table_name;
//					
//					com[1] = lambda_term;
//					
//					table_lambda_terms.add(com);
//					
//					
//					HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//					
//					if(l_seq == null)
//					{
//						l_seq = new HashMap<String, Integer>();
//						
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						table_lambda_term_seq.put(table_name, l_seq);
//						
//					}
//					else
//					{
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//					
//					
//					HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//					
//					if(lambda_term_seq == null)
//					{
//						lambda_term_seq = new HashMap<String, Integer>();
//						
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						view_lambda_term_map.put(view_name, lambda_term_seq);
//					}
//					else
//					{
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//				}
//				else
//				{
//					if(!lambda_terms.contains(lambda_term))
//					{
//						lambda_terms.add(lambda_term);
//						
//						String [] com = new String[2];
//						
//						com[0] = table_name;
//						
//						com[1] = lambda_term;
//						
//						table_lambda_terms.add(com);
//						
//						HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//						
//						if(l_seq == null)
//						{
//							l_seq = new HashMap<String, Integer>();
//							
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							table_lambda_term_seq.put(table_name, l_seq);
//							
//						}
//						else
//						{
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//						
//						
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//					}
//					else
//					{
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						int seq = table_lambda_term_seq.get(table_name).get(lambda_term);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, seq);
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, seq);
//						}
//					}
//				}
//				
//				
//							
//				
//			}
//			
////			lambda_term_table_map.put(rs.getString(1), table_lambda_terms);
//		}
//	}
//	
	
	static String get_args_web_view(String subgoal, Connection c, PreparedStatement pst) throws SQLException
	{
		String q = "select arguments from subgoal_arguments where subgoal_names ='" + subgoal + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		String subgoal_args = new String();
		
		if(r.next())
		{
			subgoal_args += r.getString(1);
		}
		
		return subgoal_args;
		
	}
	
		
	static String get_subgoals (Vector<String> subgoal_names, Vector<String> subgoal_arguments, String view_id, Connection c, PreparedStatement pst ) throws SQLException
	{
		String subgoal_query = "select s.subgoal_names, a.arguments from view2subgoals s join subgoal_arguments a using (subgoal_names) where s.view = '" + view_id + "'";
		
		pst = c.prepareStatement(subgoal_query);
		
		ResultSet rs = pst.executeQuery();
				
		String subgoal_str = new String();
		
		int num = 0;
		
		while(rs.next())
		{
			if(num >= 1)
			{
				subgoal_str += ",";
			}
			
			subgoal_str += rs.getString(1) + "(";
			
			subgoal_str += rs.getString(2) + ")";
			
			num++;
		}
		
		return subgoal_str;
		
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
	
	static HashSet<String> get_relation_names(HashMap<String, String> subgoal_mapping)
	{
		Set<String> keys = subgoal_mapping.keySet();
		
		HashSet<String> relations = new HashSet<String>();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String key = (String)iter.next();
			
			relations.add(subgoal_mapping.get(key));
		}
		
		return relations;
	}
	
	public static HashSet<Tuple> pre_processing(Vector<Query> views, Query q, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		
		for(int k = 0; k<views.size(); k++)
		{
			view_list.add(views.get(k).name);
			
			view_mapping.put(views.get(k).name, views.get(k));
		}

		build_query_head_variable_mapping(q);
//	    q = q.minimize();

	    // construct the canonical db
	    Database canDb = CoreCover.constructCanonicalDB(q);
	    UserLib.myprintln("canDb = " + canDb.toString());

	    // compute view tuples
	    HashSet viewTuples = CoreCover.computeViewTuples(canDb, views);
	    
	    viewTuples = check_distinguished_variables(viewTuples, q);
	    	    
//	    HashMap<String, Tuple> tuple_mapping = new HashMap<String, Tuple>();
	    
	    cost_function.cal_query_constants(q);
	    
	    view_mapping_global_valid = new int[viewTuples.size()];
	    
	    for(int i = 0; i<view_mapping_global_valid.length; i++)
	    {
	    	view_mapping_global_valid[i] = 1;
	    }
	    
	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
	    {
	    	Tuple tuple = (Tuple)iter.next();
	    	
	    	build_head_variable_mapping(tuple.query, tuple);
	    	
	    	build_view_tuple_mapping(tuple, tuple.getTargetSubgoals());
	    		    	
	    	double cost = cost_function.cost1(tuple, q);
	    	
	    	view_mapping_cost.put(tuple.name + populate_db.separator + tuple.mapSubgoals_str, cost);
	    	
	    	int list_id = view_mapping_list.add(tuple.name + populate_db.separator + tuple.mapSubgoals_str);
	    	
	    	tuples.add(list_id, tuple);
	    	
	    	single_subgoal_relations.add(list_id, get_relation_names(tuple.mapSubgoals_str));
	    	
	    	single_view_head_args.add(list_id, tuple.getArgs());
	    		    	
	    	if(tuple_mapping.get(tuple.name) == null)
	    	{
	    		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	    		
	    		tuples.add(tuple);
	    		
	    		tuple_mapping.put(tuple.name, tuples);
	    	}
	    	else
	    	{
	    		ArrayList<Tuple> tuples = tuple_mapping.get(tuple.name);
	    		
	    		tuples.add(tuple);
	    		
	    		tuple_mapping.put(tuple.name, tuples);
	    	}
	    	
	    	
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
	    if(prepare_info)
	    	prepare_citation_information(c, pst);
	    	    
	    return viewTuples;
	    
	    
	}
	
	public static void prepare_citation_information(Connection c, PreparedStatement pst) throws SQLException
	{
		gen_citation1.get_all_query_ids(query_ids, c, pst);
	    
	    view_query_mapping = new ArrayList<HashMap<String, Integer>>(view_list.size);
	    		
		for(int i = 0; i<query_ids.size; i++)
		{
			query_lambda_str.add(null);
		}
	    	    
	    gen_citation1.init_author_mapping(view_list, view_query_mapping, query_ids, author_mapping, max_author_num, c, pst, query_lambda_str, citation_queries);

	}
	
	public static HashMap<String, Integer> get_citation_queries(String view_name)
	{
		int view_id = view_list.find(view_name);
		
		return view_query_mapping.get(view_id);
	}
		
	public static void tuple_reasoning(Query q, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		
//		Query q = Parse_datalog.parse_query(query, valid_subgoal_id);
		
//		Query alt_q = gen_alternative_query(q, valid_subgoal_id);
				
		reset();
		
		Vector<Query> views = populate_db.get_views_schema(c, pst);
		
		start = System.nanoTime();
		
		HashSet<Tuple> viewTuples = pre_processing(views, q ,c,pst);
		
		get_citation_views(q, viewTuples, c, pst);
				
		
		
//		return citation_views;
		
	}
	
	public static void output (Vector<Vector<Vector<citation_view>>> citation_views)
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Vector<citation_view>> citation_view_vec = citation_views.get(i);
			
			for(int j = 0; j<citation_view_vec.size();j++)
			{
				Vector<citation_view> c_view = citation_view_vec.get(j);
				
				if(j > 0)
					System.out.print(",");
				
				for(int k = 0; k<c_view.size();k++)
				{
					citation_view  cv = c_view.get(k);
					if(k > 0)
						System.out.print("*");
					System.out.print(cv);
				}
				
				
			}
			
			System.out.println();
			
		}
	}
	
	public static Vector<citation_view> gen_citation_arr(Vector<Query> views)
	{
		return new Vector<citation_view>(views.size());
	}
	
	public static void gen_citation(citation_view_vector citation_views) throws ClassNotFoundException, SQLException
	{
		for(citation_view curr_view_mapping:citation_views.c_vec)
		{
			String query = curr_view_mapping.get_full_query();
			
			System.out.println(query);
		}
	}
	
	public static void get_citation_views(Query query, HashSet<Tuple> viewTuples, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
		query_execution(query, c, pst, viewTuples);
				
	}
	
	
	public static String gen_conditions_query (Vector<Conditions> conditions)
	{
		String where = new String();
		
//		boolean start = false;
		
		for(int i = 0; i<conditions.size(); i++)
		{
//			if(!start)
//			{
//				where += " where";
//				start = true;
//			}
			
			if(i >= 1)
				where +=" and ";
			
			Conditions condition = conditions.get(i);
			
			if(condition.arg2.type != 1)
				where += condition.subgoal1 + "." + condition.arg1.origin_name + condition.op + condition.subgoal2 + "." + condition.arg2.origin_name;
			else
				where += condition.subgoal1 + "." + condition.arg1.origin_name + condition.op + condition.arg2;
		}
		
		return where;
	}
	
//	public static Vector<Conditions> remove_conflict_duplicate(Query query)
//	{
//		if(conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
//		{
//			return conditions;
//		}
//		else
//		{
//
//			Vector<Conditions> update_conditions = new Vector<Conditions>();
//			
//			boolean remove = false;
//			
//			for(int i = 0; i<conditions.size(); i++)
//			{
//				remove = false;
//				
//				for(int j = 0; j<query.conditions.size(); j++)
//				{
//					if(Conditions.compare(query.conditions.get(j), conditions.get(i)) || Conditions.compare(query.conditions.get(j), Conditions.negation(conditions.get(i))))
//					{
//						remove = true;
//						break;
//					}
//				}
//				
//				if(!remove)
//					update_conditions.add(conditions.get(i));
//			}
//			
//			conditions = update_conditions;
//			
//			return conditions;
//			
//		}
//	}
	
	public static HashSet<Conditions> remove_conflict_duplicate_view_mapping(Query query)
	{
		if(valid_conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
		{
			
//			int id = 0;
//			
//			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
//			{
//				Conditions condition = (Conditions) iter.next();
//				
//				condition_id_mapping.put(condition.toString(), id);
//				
//				id ++;
//			}
			
			return valid_conditions;
		}
		else
		{

			HashSet<Conditions> update_conditions = new HashSet<Conditions>();
			
			boolean remove = false;
			
			for(Iterator iter = valid_conditions.iterator(); iter.hasNext(); )
			{
				remove = false;
				
				Conditions v_condition = (Conditions) iter.next();
				
				for(int j = 0; j<query.conditions.size(); j++)
				{
					if(Conditions.compare(query.conditions.get(j), v_condition) || Conditions.compare(query.conditions.get(j), Conditions.negation(v_condition)))
					{
						remove = true;
												
						break;
					}
				}
				
				if(!remove)
					update_conditions.add(v_condition);
			}
			
			valid_conditions = update_conditions;
			
//			int id = 0;
//			
//			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
//			{
//				Conditions condition = (Conditions) iter.next();
//				
//				condition_id_mapping.put(condition.toString(), id);
//				
//				id ++;
//			}
			
			return valid_conditions;
			
		}
	}
	
	public static void query_execution(Query query, Connection c, PreparedStatement pst, HashSet<Tuple> viewTuples) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
				
//		Vector<Vector<citation_view_vector>> c_views = new Vector<Vector<citation_view_vector>>();
								
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
		remove_conflict_duplicate_view_mapping(query);
					
		String sql = null;
		
		if(test_case)
			sql = Query_converter.datalog2sql_citation_test(query, valid_lambda_terms, valid_conditions);
		else
			sql = Query_converter.datalog2sql_citation(query, valid_lambda_terms, valid_conditions, c, pst);
						
		end = System.nanoTime();
		
		pre_processing_time = (end - start) * 1.0/second2nano;
		
		reasoning(query, c, pst, sql, viewTuples);
		
//		return c_views;
	}
	
//	public static void initial_conditions_all(Query query, Vector<HashMap<String,boolean[]>> conditions_all)
//	{
//		
//		
//		
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			HashMap<String, boolean[]> conditions = new HashMap<String, boolean[]>();
//			
//			Set set = citation_condition_id_map.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
//			{
//				String c_name = (String)iter.next();
//				
//				HashMap<String, Integer> citation_map = citation_condition_id_map.get(c_name);
//				
//				int size = citation_map.size();
//				
//				boolean[] b_values = new boolean[size];
//				
////				for(int k = 0; k<size; k++)
////				{
////					b_values.add(false);
////				}
//				
//				conditions.put(c_name, b_values);
//			}
//			
//			
//			conditions_all.add(conditions);
//		}
//	}
//	
	
	static HashMap<String, ArrayList<Tuple>> get_valid_view_mapping(int pos1) throws SQLException
	{
		HashMap<String, ArrayList<Tuple>> curr_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
		
		int i = pos1;
		
		curr_tuple_mapping.putAll(tuple_mapping);
		
		if(valid_conditions.isEmpty())
		{
			
			
			return curr_tuple_mapping;
		}
		
		for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
		{
			boolean condition = rs.getBoolean(i + 1);
			
			Conditions curr_condition = (Conditions) iter.next();
			
			if(!condition)
			{							
				String condition_str = curr_condition.toString();
				
				ArrayList<Tuple> invalid_views = conditions_map.get(condition_str);
				
				for(int k = 0; k<invalid_views.size(); k++)
				{
					Tuple tuple = invalid_views.get(k);
					
					ArrayList<Tuple> curr_tuples = curr_tuple_mapping.get(tuple.name);//new ArrayList<Tuple>();
					
					if(curr_tuples == null)
					{
						curr_tuples = new ArrayList<Tuple>();
						
						curr_tuples.addAll(tuple_mapping.get(tuple.name));
					}
					
					
					curr_tuples.remove(tuple);
					
					curr_tuple_mapping.put(tuple.name, curr_tuples);
				}							
			}
			i++;
		}	
		
		return curr_tuple_mapping;
	}
	
	public static void reasoning(Query query, Connection c, PreparedStatement pst, String sql, HashSet<Tuple> viewTuples) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
		start = System.nanoTime();
		
		pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
//		System.out.println(sql);
				
		rs = pst.executeQuery();

		end = System.nanoTime();
		
		query_time = (end - start) * 1.0/second2nano;
		
		int lambda_term_num = valid_lambda_terms.size();

		String old_value = new String();
		
//		HashMap<Head_strs, Vector<String> > citation_strs = new HashMap<Head_strs, Vector<String> >();
						
		citation_view_vector c_view_template = null;
		
		int start_pos = 0;
		
		int end_pos = -1;
		
		
//		if(!valid_conditions.isEmpty())
		{		
			while(rs.next())
			{				
				tuple_num ++;
				
				String curr_str = new String();
								
				Vector<String> vals = new Vector<String>();
				
				for(int i = 0; i<query.head.args.size(); i++)
				{
					vals.add(rs.getString(i+1));
				}
				
				Head_strs h_vals = new Head_strs(vals);
				
				int pos1 = query.body.size() + query.head.args.size() + lambda_term_num;
				
				for(int i = pos1; i< pos1 + valid_conditions.size(); i++)
				{
					boolean condition = rs.getBoolean(i + 1);
					
					if(!condition)
					{
				
						curr_str += "0";
					}
					else
					{					
						
						curr_str += "1";
					}
				}
				
				ArrayList<String[]> c_units = new ArrayList<String[]>();
				for(int i = query.head.args.size(); i<query.body.size() + query.head.args.size();i++)
				{
					String citation_vec = rs.getString(i + 1);
					
					curr_str += "||" + citation_vec;
					
					if(citation_vec == null)
					{
						citation_vec = new String ();
					}
					
					String[] c_unit= citation_vec.split("\\"+ populate_db.separator);
					c_units.add(c_unit);
				}

				
				
				if(!curr_str.equals(old_value))
				{		
										
					start = System.nanoTime();
					
					group_num ++;
					
//					System.out.println(h_vals);
					if(c_view_template != null)
					{
//						c_view_template.clear();
						end_pos = tuple_num;
						
						int [] interval = {start_pos, end_pos};
						
						
						c_view_map.put(interval, c_view_template);
						
						start_pos = tuple_num;
					}
					
//					c_view_template = new ArrayList<citation_view_vector>();
										
//					HashMap<String, ArrayList<Tuple>> curr_tuple_mapping = (HashMap<String, ArrayList<Tuple>>) tuple_mapping.clone();
//					
//					int i = pos1;

					HashMap<String, ArrayList<Tuple>> curr_tuple_mapping = get_valid_view_mapping(pos1);
					
					
//					for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
//					{
//						boolean condition = rs.getBoolean(i + 1);
//						
//						Conditions curr_condition = (Conditions) iter.next();
//						
//						if(!condition)
//						{							
//							String condition_str = curr_condition.toString();
//							
//							ArrayList<Tuple> invalid_views = conditions_map.get(condition_str);
//							
//							for(int k = 0; k<invalid_views.size(); k++)
//							{
//								Tuple tuple = invalid_views.get(k);
//								
//								ArrayList<Tuple> curr_tuples = new ArrayList<Tuple>();
//								
//								curr_tuples.addAll(curr_tuple_mapping.get(tuple.name));
//								
//								curr_tuples.remove(tuple);
//								
//								curr_tuple_mapping.put(tuple.name, curr_tuples);
//							}
//							
//							
//							
//						}
//						i++;
//					}	
					
//					citation_strings = new HashMap<String, HashSet<String>>();
					

										
//					start_pos = query.body.size() + query.head.args.size();
					
					Resultset_prefix_col_num = query.body.size() + query.head.args.size();
					
					ArrayList<int[]> int_matrix = new ArrayList<int[]>();

					HashSet<String> covered_relations = new HashSet<String>();
					
					HashSet<Argument> covered_head_args = new HashSet<Argument>();
					
					
					ArrayList<Integer> tuple_ids = get_citation_units_condition(c_units, curr_tuple_mapping, query.body.size() + query.head.args.size(), query, covered_relations, covered_head_args);
					
//					output_vec(c_unit_vec);				
					
					ArrayList<Integer> citation_view_ids = greedy_algorithm(tuple_ids, covered_relations, covered_head_args);
										
					c_view_template = get_valid_citation_combination(citation_view_ids);

					old_value = curr_str;
					
					if(head_strs_rows_mapping.get(h_vals) == null)
					{
						ArrayList<Integer> int_list = new ArrayList<Integer>();
						
						int_list.add(tuple_num);
						
						head_strs_rows_mapping.put(h_vals, int_list);
					}
					else
					{
						ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
						
						int_list.add(tuple_num);
						
						head_strs_rows_mapping.put(h_vals, int_list);
					}
					
					
					c_units.clear();

					end = System.nanoTime();
					
					reasoning_time += (end - start) * 1.0/second2nano;
									
				}
				
				else
				{					
					start = System.nanoTime();
									
					if(head_strs_rows_mapping.get(h_vals) == null)
					{
						ArrayList<Integer> int_list = new ArrayList<Integer>();
						
						int_list.add(tuple_num);
						
						head_strs_rows_mapping.put(h_vals, int_list);
					}
					else
					{
						ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
						
						int_list.add(tuple_num);
						
						head_strs_rows_mapping.put(h_vals, int_list);
					}
					
					end = System.nanoTime();
					
					population_time += (end - start) * 1.0/second2nano;
					
				}
			}
		}

		if(c_view_template != null)
		{			
			end_pos = tuple_num;
			
			
			int [] interval = {start_pos, end_pos};
			
			
			c_view_map.put(interval, c_view_template);
			
		}

		
		ArrayList<Integer> valid_tuple_ids = new ArrayList<Integer>();
		
		for(int i = 0; i<view_mapping_global_valid.length; i++)
		{
			if(view_mapping_global_valid[i] == 1)
				valid_tuple_ids.add(i);
		}
		
		HashSet<String> curr_all_relation_names = get_curr_all_relation_names(valid_tuple_ids);
		
		HashSet<Argument> curr_all_head_args = get_curr_all_head_args(valid_tuple_ids, query);
		
		ArrayList<Integer> valid_view_mapping_ids = greedy_algorithm(valid_tuple_ids, curr_all_relation_names, curr_all_head_args);
		
		covering_sets_query = get_valid_citation_combination(valid_view_mapping_ids);
		
		entire_interval[1] = end_pos;
		
//		compute_citation_view_query();
		
		System.out.print(group_num + "	");
		
		System.out.print(tuple_num + "	");
	}
	
	static HashSet<String> get_curr_all_relation_names(ArrayList<Integer> valid_view_mapping_ids)
	{
		HashSet<String> relation_names = new HashSet<String>();
		
		for(int i = 0; i<valid_view_mapping_ids.size(); i++)
		{
			HashSet<String> curr_relation_names = tuples.get(valid_view_mapping_ids.get(i)).get_relations();
			
			relation_names.addAll(curr_relation_names);
		}
		
		return relation_names;
	}
	
	static HashSet<Argument> get_curr_all_head_args(ArrayList<Integer> valid_view_mapping_ids, Query query)
	{
		HashSet<Argument> all_head_vars = new HashSet<Argument>();
		
		for(int i = 0; i<valid_view_mapping_ids.size(); i++)
		{
			all_head_vars.addAll(tuples.get(valid_view_mapping_ids.get(i)).getArgs());
		}
		
		all_head_vars.retainAll(query.head.args);
		
		return all_head_vars;
	}
	
	static void remove_empty_covering_sets()
	{
//		for(int i = 0; i<view_mapping_global_valid.size(); i++)
//		{
//			int [] arr = view_mapping_global_valid.get(i);
//			
//			int j = 0;
//			
//			for(j = 0; j < arr.length; j++)
//			{
//				if(arr[j] == 1)
//					break;
//			}
//			
//			if(j >= arr.length)
//			{
//				view_mapping_global_valid.remove(i);
//				
//				i--;
//			}
//		}
	}
	
//	static void compute_citation_view_query() throws ClassNotFoundException, SQLException
//	{
//		remove_empty_covering_sets();
//		
//		ArrayList<Integer> id_list = greedy_algorithm(view_mapping_global_valid);
//		
//		covering_sets_query = get_valid_citation_combination(id_list);
//		
//	}
	
	static void remove_uncovered_ones(ArrayList<int[]> int_matrix)
	{
		for(int i = 0; i<int_matrix.size(); i++)
		{
			int [] array = int_matrix.get(i);
			
			int sum = IntStream.of(array).parallel().sum();
			
			if(sum == 0)
			{
				int_matrix.remove(i);
				
				i--;
			}
			
		}
	}
	
	static ArrayList<Integer> greedy_algorithm(ArrayList<Integer> valid_tuple_ids, HashSet<String> curr_all_relation_names, HashSet<Argument> curr_all_head_args)
	{
		
		Collections.sort(valid_tuple_ids);
		
		HashSet<String> all_relations_copy = (HashSet<String>) curr_all_relation_names.clone();
		
		HashSet<Argument> all_head_args_copy = (HashSet<Argument>) curr_all_head_args.clone();
		
//		ArrayList<HashSet<String>> single_relation_names_copy = new ArrayList<HashSet<String>>();;
//		
//		for(int i = 0; i < single_subgoal_relations.size(); i++)
//		{
//			single_relation_names_copy.add((HashSet<String>)single_subgoal_relations.get(i).clone());
//		}
		
		ArrayList<Integer> select_set = new ArrayList<Integer>();
		
		while(!all_relations_copy.isEmpty() || !all_head_args_copy.isEmpty())
		{
			double min_cost = Double.MAX_VALUE;
			
			int index = -1;
			
			for(int i = 0; i < valid_tuple_ids.size(); i++)
			{
				double cost = view_mapping_cost.get(view_mapping_list.list[valid_tuple_ids.get(i)]);
				
				HashSet<String> intersection = (HashSet<String>) single_subgoal_relations.get(valid_tuple_ids.get(i)).clone();
				
				Vector<Argument> intersection_args = (Vector<Argument>) single_view_head_args.get(valid_tuple_ids.get(i)).clone();
				
				intersection.retainAll(all_relations_copy);
				
				intersection_args.retainAll(all_head_args_copy);
				
				double curr_cost = cost/(intersection.size() + intersection_args.size());
				
				if(curr_cost < min_cost)
				{
					index = i;
					
					min_cost = curr_cost;
				}
			}
			
			all_relations_copy.removeAll(single_subgoal_relations.get(valid_tuple_ids.get(index)));
			
			all_head_args_copy.removeAll(single_view_head_args.get(valid_tuple_ids.get(index)));
			
			select_set.add(valid_tuple_ids.get(index));
			
//			System.out.println("index::" + index);
			
//			for(int i = 0; i<int_matrix.size(); i++)
//			{
//				if(int_matrix.get(i)[index] == 1)
//				{
//					
//					sum_array = update_sum_array(sum_array, int_matrix.get(i));
//					
//					int_matrix.remove(i);
//					
//					
//					
//					i--;
//				}
//			}
//			
//			select_set.add(index);
		}
		
//		for(int i = 0; i<tuple_relations.size(); i++)
//		{
//			
//		}
//		
//		
//		
//		int [] sum_array = get_sum(int_matrix);
//		
//		ArrayList<Integer> select_set = new ArrayList<Integer>();
//		
//		while(!int_matrix.isEmpty())
//		{
//			
//			double min_cost = Double.MAX_VALUE;
//			
//			int index = -1;
//			
//			for(int i = 0; i < view_mapping_list.size; i++)
//			{
//				double cost = view_mapping_cost.get(view_mapping_list.list[i]);
//				
//				double curr_cost = cost/sum_array[i];
//				
//				if(curr_cost < min_cost)
//				{
//					index = i;
//					
//					min_cost = curr_cost;
//				}
//			}
//			
//			for(int i = 0; i<int_matrix.size(); i++)
//			{
//				if(int_matrix.get(i)[index] == 1)
//				{
//					
//					sum_array = update_sum_array(sum_array, int_matrix.get(i));
//					
//					int_matrix.remove(i);
//					
//					
//					
//					i--;
//				}
//			}
//			
//			select_set.add(index);
//		}
		
		return select_set;
	}
	
	static ArrayList<Integer> greedy_algorithm(ArrayList<int[]> int_matrix)
	{
		
		remove_uncovered_ones(int_matrix);
		
		int [] sum_array = get_sum(int_matrix);
		
		ArrayList<Integer> select_set = new ArrayList<Integer>();
		
		while(!int_matrix.isEmpty())
		{
			
			double min_cost = Double.MAX_VALUE;
			
			int index = -1;
			
			for(int i = 0; i < view_mapping_list.size; i++)
			{
				double cost = view_mapping_cost.get(view_mapping_list.list[i]);
				
				double curr_cost = cost/sum_array[i];
				
				if(curr_cost < min_cost)
				{
					index = i;
					
					min_cost = curr_cost;
				}
			}
			
			for(int i = 0; i<int_matrix.size(); i++)
			{
				if(int_matrix.get(i)[index] == 1)
				{
					
					sum_array = update_sum_array(sum_array, int_matrix.get(i));
					
					int_matrix.remove(i);
					
					
					
					i--;
				}
			}
						
			select_set.add(index);
		}
		
		return select_set;
	}
	
	static int[] update_sum_array(int []sum_array, int [] deleted_array)
	{
		int[] updated_sum_array = new int[sum_array.length] ;
		
		for(int i = 0; i < sum_array.length; i++)
		{
			updated_sum_array[i] = sum_array[i] - deleted_array[i];
		}
		
		return updated_sum_array;
	}
	
	static int[] get_sum(ArrayList<int[]> int_matrix)
	{
		int[] sum_list = new int[int_matrix.get(0).length];
		
		for(int i = 0; i<int_matrix.size(); i++)
		{
			
			for(int j = 0; j<sum_list.length; j++)
			{
				sum_list[j] += int_matrix.get(i)[j];
			}
			
			
		}
		
		return sum_list;
		
		
	}
	
	public static HashSet<String> gen_citation(Head_strs h_vals, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
	{
		int index = 0;
		
		rs.beforeFirst();
		
		ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
		
//		System.out.println(int_list.toString());
		
		Set<int[]> intervals = c_view_map.keySet();
		
		HashSet<String> citations = new HashSet<String>();
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			citation_view_vector c_views = c_view_map.get(interval);
			
//			if(c_views.size() == 0)
//				citations.add("{\"db_name\":\"IUPHAR/BPS Guide to PHARMACOLOGY\",\"author\":[]}");
			
			for(int i = index; i < int_list.size(); i ++)
			{
				
				
				if(int_list.get(i) <= interval[1] && int_list.get(i) > interval[0])
				{
					rs.absolute(int_list.get(i));
					
					update_valid_citation_combination(c_views, rs, Resultset_prefix_col_num);
					
//					System.out.println(c_views.toString());
					
//					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
					
					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping, populate_db.star_op, populate_db.plus_op));
				}
				
				
				if(int_list.get(i) >= interval[1])
				{
					index = i;
					
					break;
				}
			}
		}
		
		return citations;
	}

//	public static HashSet<String> gen_citation(Head_strs h_vals, HashMap<Head_strs, HashSet<String>> c_view_maps, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
//	{
//		int index = 0;
//		
//		rs.beforeFirst();
//		
//		ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//		
////		System.out.println(int_list.toString());
//		
//		Set<int[]> intervals = c_view_map.keySet();
//		
//		HashSet<String> citations = new HashSet<String>();
//		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			citation_view_vector c_views = c_view_map.get(interval);
//
//			for(int i = index; i < int_list.size(); i ++)
//			{
//				
//				
//				if(int_list.get(i) <= interval[1] && int_list.get(i) > interval[0])
//				{
//					rs.absolute(int_list.get(i));
//					
//					update_valid_citation_combination(c_views, rs, Resultset_prefix_col_num);
//					
//					
//					
//					if(c_view_maps.get(h_vals) == null)
//					{
//						
//						HashSet<String> c_view_values = new HashSet<String>();
//						
//						for(int k = 0; k<c_views.size(); k++)
//						{
//							String v_str = c_views.get(k).toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					else
//					{
//						HashSet<String> c_view_values = c_view_maps.get(h_vals);
//						
//						for(int k = 0; k<c_views.size(); k++)
//						{
//							String v_str = c_views.get(k).toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					
//					
//					
////					System.out.println(c_views.toString());
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//					
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping, populate_db.star_op, populate_db.plus_op));
//				}
//				
//				
//				if(int_list.get(i) >= interval[1])
//				{
//					index = i;
//					
//					break;
//				}
//			}
//		}
//		
//		return citations;
//	}
//	
	static HashSet<String> gen_citation(citation_view_vector c_views, Connection c, PreparedStatement pst, ArrayList<HashMap<String, Integer>> view_query_mapping, String star_op, String plus_op) throws ClassNotFoundException, SQLException, JSONException
	{
		HashSet<String> citations = new HashSet<String>();
				
		HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
		
//		for(int p =0; p<c_views.size(); p++)
		{
									
			HashSet<String> str = gen_citation1.get_citations3(c_views, c, pst, view_list, view_query_mapping, author_mapping, max_author_num, query_ids, query_lambda_str, view_author_mapping, star_op);
			
			citations.addAll(str);		
		}
		
		return citations;
	}
	
//	static HashSet<String> populate_citation(Vector<citation_view_vector> update_c_view, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> citation_strings, Head_strs h_vals) throws SQLException, ClassNotFoundException
//	{
//		HashSet<String> citations = new HashSet<String>();
//		
//		HashSet<String> author_list = new HashSet<String>();
//		
//		for(int p =0; p<update_c_view.size(); p++)
//		{
//			
//			author_list = new HashSet<String>();
//			
//			String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings, max_author_num, author_list);
//
//			citations.add(str);
//			
//			if(authors.get(h_vals) == null)
//			{
//				Vector<HashSet<String>> author_l = new Vector<HashSet<String>>();
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//				
//			}
//			else
//			{
//				Vector<HashSet<String>> author_l = authors.get(h_vals);
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//			}
//			
//			
//			
////			HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
////			
////			String str = gen_citation1.get_citations(c_unit_combinaton.get(p), vals, c, pst, query_str);
////
////			citations.add(str);
//			
////			citation_strings.add(query_str);
//			
//		}
//		
////		citation_strs.add(citations);
//		
//		return citations;
//	}
//	
	public static void output_vec(Vector<Vector<citation_view>> c_unit_vec)
	{
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> c_vec = c_unit_vec.get(i);
			
			System.out.print("[");
			
			for(int j = 0; j<c_vec.size(); j++)
			{
				citation_view c = c_vec.get(j);
				
				if(j>=1)
					System.out.print(",");
				
				System.out.print(c.toString());
			}
			System.out.print("]");
		}
		
		System.out.println();
	}
	
	
	public static void output_vec_com(Vector<citation_view_vector>c_unit_combinaton)
	{
		for(int i = 0; i<c_unit_combinaton.size(); i++)
		{
			if(i >= 1)
				System.out.print(",");
			
			System.out.print("view_combination:::" + c_unit_combinaton.get(i));
		}
		System.out.println();
	}
	
	public static void update_valid_citation_combination(citation_view_vector insert_c_view, ResultSet rs, int start_pos) throws SQLException
	{
		
//		HashSet<citation_view_vector> update_c_views = new HashSet<citation_view_vector>();
		
//		for(int i = 0; i<insert_c_view.size(); i++)
		{
			
			citation_view_vector c_vec = insert_c_view;
			
			for(citation_view curr_view_mapping:c_vec.c_vec)
			{
				citation_view c_view = curr_view_mapping;
				
				if(c_view.has_lambda_term())
				{
					citation_view_parametered curr_c_view = (citation_view_parametered) c_view;
					
					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					for(int k = 0; k<lambda_terms.size(); k++)
					{
						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
				}
				
			}
			
//			c_vec = new citation_view_vector(c_vec.c_vec);
			
//			update_c_views.add(c_vec);
			
//			insert_c_view.remove(i);
//			
//			insert_c_view.add(i, c_vec);
		}
		
//		insert_c_view.clear();
		
//		return update_c_views;
		
		
				
//		insert_c_view = remove_duplicate_final(insert_c_view);
//
//		insert_c_view = remove_duplicate(insert_c_view);
		
//		Vector<citation_view_vector> update_c_views = new Vector<citation_view_vector>();
//		
//		for(int i = 0; i<insert_c_view.size();i++)
//		{
//			citation_view_vector insert_c_view_vec = insert_c_view.get(i);
//			
//			Vector<citation_view> update_c_view_vec = new Vector<citation_view>();
//			
//			for(int j = 0; j < insert_c_view_vec.c_vec.size(); j++)
//			{
//				citation_view c = insert_c_view_vec.c_vec.get(j);
//				
//				c = c_unit_vec.get(String.valueOf(c.get_index()));
//				
//				update_c_view_vec.add(c);
//			}
//			
//			update_c_views.add(new citation_view_vector(update_c_view_vec, insert_c_view_vec.index_vec, insert_c_view_vec.view_strs));
//		}
		
//		return insert_c_view;
	}
	
	public static int compute_distinct_num_covering_sets(HashMap<String, HashSet<String>> view_strs) throws SQLException
	{
		
		citation_view_vector covering_set_copy = covering_sets_query.clone();
		
		HashSet<String> covering_set_strs = new HashSet<String>();
		
		for(int i = entire_interval[0]; i < entire_interval[1]; i ++)
		{
			
			
//			if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
			{
				rs.absolute(i + 1);
				
				update_valid_citation_combination(covering_set_copy, rs, Resultset_prefix_col_num);
				
				covering_set_strs.add(covering_set_copy.toString());
				
				for(citation_view curr_view_mapping:covering_set_copy.c_vec)
				{
					citation_view view = curr_view_mapping;
					
					if(view_strs.get(view.get_name()) == null)
					{
						HashSet<String> views = new HashSet<String>();
						
						views.add(view.toString());
						
						view_strs.put(view.get_name(), views);
					}
					else
					{
						view_strs.get(view.get_name()).add(view.toString());
					}
				}
				
//				System.out.println(c_views.toString());
			}
		}
		
		return covering_set_strs.size();
		
	}
	
	public static void get_views_parameters(ArrayList<citation_view> insert_c_view, ResultSet rs, int start_pos, ArrayList<HashSet<Head_strs>> values) throws SQLException
	{
	
		for(int i = 0; i<insert_c_view.size(); i++)
		{
			
			citation_view c_vec = insert_c_view.get(i);
			
//			for(int j = 0; j<c_vec.c_vec.size(); j++)
//			{
//				citation_view c_view = c_vec.c_vec.get(j);
				
				if(c_vec.has_lambda_term())
				{
					citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
					
					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					Vector<String> heads = new Vector<String>();
					
					for(int k = 0; k<lambda_terms.size(); k++)
					{
						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
						
						heads.add(rs.getString(id + start_pos + 1));
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
					Head_strs head_values = new Head_strs(heads);
					
					if(values.size() > i)
					{
						values.get(i).add(head_values);
					}
					else
					{
						HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
						
						curr_head_values.add(head_values);
						
						values.add(curr_head_values);
					}
					
				}
				else
				{
					if(values.size() > i)
					{
						continue;
					}
					else
					{
						values.add(new HashSet<Head_strs>());
					}
				}
				
//			}
		}
	}
	
	static Vector<Lambda_term> get_lambda_terms(String c_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		String q = "select lambda_term, table_name from view2lambda_term where view = '" + c_view_name + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		if(r.next())
		{
			String [] lambda_term_names = r.getString(1).split(populate_db.separator);
			
			String [] table_name = r.getString(2).split(populate_db.separator);
			
			for(int i = 0; i<lambda_term_names.length; i++)
			{
				Lambda_term l_term = new Lambda_term(lambda_term_names[i], table_name[i]);
				
				lambda_terms.add(l_term);
				
			}
			
			
		}
		
		return lambda_terms;
		
		
		
	}
	
//	public static Vector<Vector<citation_view>> get_citation_units(Vector<String[]> c_units, HashSet<String> invalid_citation_unit, HashMap<String, Vector<Integer>> table_pos_map, ResultSet rs, int start_pos) throws ClassNotFoundException, SQLException
//	{
//		Vector<Vector<citation_view>> c_views = new Vector<Vector<citation_view>>();
//		
//		for(int i = 0; i<c_units.size(); i++)
//		{
//			Vector<citation_view> c_view = new Vector<citation_view>();
//			String [] c_unit_str = c_units.get(i);
//			for(int j = 0; j<c_unit_str.length; j++)
//			{
//				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
//				{
//					
//					String c_view_name = c_unit_str[j].split("\\(")[0];
//					
//					if(invalid_citation_unit.contains(c_view_name))
//						continue;
//					
//					
//					Vector<String> c_view_paras = new Vector<String>();
//					
//					
////					if(view_type_map.get(c_view_name))
//					{
//						c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//						
//						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
//						
//						int k = 0;
//						
//						for(k = 0; k<c_view_paras.size(); k++)
//						{
//							
//							String t_name = curr_c_view.lambda_terms.get(k).table_name;
//							
//							Vector<Integer> positions = table_pos_map.get(t_name);
//							
//							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(k).name);
//							
//							int[] final_pos = new int[1];
//							
//							if(check_value(c_view_paras.get(k), start_pos, positions, rs, offset, final_pos))
//							{
//								
//								curr_c_view.lambda_terms.get(k).id = final_pos[0];
//								
//								continue;
//							}
//							else
//								break;
//							
//						}
//						
//						if(k < c_view_paras.size())
//							continue;
//						
//						
//						curr_c_view.put_paramters(c_view_paras);
//						
//						c_view.add(curr_c_view);
//						
//					}
//					
////					else
////					{
////						String []curr_c_unit = c_unit_str[j].split("\\(")[1].split("\\)");
////												
////						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
////						
////						if(curr_c_unit.length == 0)
////						{
////							int [] final_pos = new int[1];
////							
////							String t_name = curr_c_view.lambda_terms.get(0).table_name;
////							
////							Vector<Integer> positions = table_pos_map.get(t_name);
////							
////							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(0).name);
////							
////							Vector<Integer> ids = new Vector<Integer>();
////							
////							Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////							
////							for(int p = 0; p<vals.size(); p++)
////							{								
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = new Vector<String>();
////								
////								
////								val.add(vals.get(p));
////								
////								inserted_view.put_paramters(val);
////								
////								inserted_view.lambda_terms.get(p).id = ids.get(p);
////								
////								c_view.add(inserted_view);
////							}							
////						}
////						else
////						{
////							String [] c_v_unit = curr_c_unit[0].split(",");
////													
////							Vector<Vector<String>> values = new Vector<Vector<String>>();
////							
////							Vector<Vector<Integer>> id_arrays = new Vector<Vector<Integer>>();
////							
////							for(int p = 0; p<c_v_unit.length; p++)
////							{								
////								if(c_v_unit[p].isEmpty())
////								{
////									int [] final_pos = new int[1];
////									
////									String t_name = curr_c_view.lambda_terms.get(p).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									values = update_views(values, vals, id_arrays, ids);
////									
////									
////								}
////								else
////								{
////									
////									String t_name = curr_c_view.lambda_terms.get(0).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									int[] final_pos = new int[1];
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									id_arrays.add(ids);
////									
////									if(!values.isEmpty())
////									{
////										for(int t = 0; t<values.size(); t++)
////										{
////											Vector<String> v = values.get(t);
////											
////											v.add(c_v_unit[p]);
////										}
////									}
////									else
////									{
////										Vector<String> v= new Vector<String>();
////										
////										v.add(c_v_unit[p]);
////										
////										values.add(v);
////									}
////									
////									
////								}
////							}
////							
////							for(int p = 0; p<values.size(); p++)
////							{
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = values.get(p);
////								
////								Vector<Integer> ids = id_arrays.get(p);
////															
////								inserted_view.put_paramters(val);
////								
////								for(int w = 0; w < ids.size(); w++)
////								{
////									inserted_view.lambda_terms.get(w).id = ids.get(w);
////								}
////								
////								c_view.add(inserted_view);
////							}
////							
////						}
////						
////						
////						
//////						HashMap<String, Integer> lambda_term_map = view_lambda_term_map.get(c_view_name);
//////						
//////						Set name_set = lambda_term_map.entrySet();
//////						
//////						for(Iterator iter = name_set.iterator(); iter.hasNext(); )
//////						{
//////							
//////						}
//////						
//////						if(curr_c_unit.isEmpty())
//////						{
//////							
//////						}
//////						else
//////						{
//////							String [] curr_c_units = curr_c_unit.split(",");
//////							
//////							for(int t = 0; t<curr_c_units.length; t++)
//////							{
//////								if(curr_c_units)
//////							}
//////							
//////							if(curr_c_units)
//////							
//////						}
////						
////					}
//					
//					
//
//
//					
////					curr_c_view.lambda_terms.
//					
//				}
//				else
//				{
//					if(invalid_citation_unit.contains(c_unit_str[j]))
//						continue;
//					
//					c_view.add(new citation_view_unparametered(c_unit_str[j]));
//				}
//			}
//			
//			c_views.add(c_view);
//		}
//		
//		return c_views;
//	}
//	
	static Vector<Vector<String>> update_views(Vector<Vector<String>> values, Vector<String> v, Vector<Vector<Integer>> id_arrays, Vector<Integer> ids)
	{
		Vector<Vector<String>> update_values = new Vector<Vector<String>>();
		
		Vector<Vector<Integer>> update_indexes = new Vector<Vector<Integer>>();
		
		if(values.isEmpty())
		{
			update_values.add(v);
			
			id_arrays.add(ids);
			
			return update_values;
		}
		
		
		for(int i = 0; i<values.size(); i++)
		{
			
			Vector<String> value = values.get(i);
			
			Vector<Integer> curr_ids = id_arrays.get(i);
			
			for(int j = 0; j<v.size(); j++)
			{
				Vector<String> curr_values = new Vector<String>();
				
				Vector<Integer> c_ids = new Vector<Integer>();
				
				curr_values.addAll(value);
				
				c_ids.addAll(curr_ids);
				
				curr_values.add(v.get(j));
				
				c_ids.add(ids.get(j));
				
				update_indexes.add(c_ids);
				
				update_values.add(curr_values);
			}
		}
		
		id_arrays = update_indexes;
		
		return update_values;
	}
	
	static Vector<String> get_curr_c_view(Vector<Integer> positions, int start_pos, int offset, int [] final_pos, Vector<Integer> ids, ResultSet rs) throws SQLException
	{
		
		Vector<String> values = new Vector<String>();
		
		for(int i = 0; i<positions.size(); i++)
		{
			int position = start_pos + positions.get(i) + offset;
			
			String value = rs.getString(position);
			
			ids.add(position);
			
			values.add(value);
			
		}
		
		return values;
	}
	
	static boolean correct_tuple(Tuple tuple, Subgoal subgoal)
	{
		boolean correct = false;
		
//		System.out.println("query:::" + tuple.query);
				
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();  
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			
			Subgoal curr_subgoal = (Subgoal)iter.next();
			
//			System.out.println("1::::::" + subgoal.toString());
//			
//			System.out.println("2::::::" + curr_subgoal.toString());
			
			if(curr_subgoal.toString().equals(subgoal.toString()))
				return true;
		}
		
		return correct;
	}
	
	public static void get_citation_units_condition(ArrayList<String[]> c_units, HashMap<String, ArrayList<Tuple>> curr_tuple_mapping, int start_pos, Query q, ArrayList<int[]> int_matrix) throws ClassNotFoundException, SQLException
	{		
//		boolean parameterized = true;
				
		for(int i = 0; i<c_units.size(); i++)
		{			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			String [] c_unit_str = c_units.get(i);
			
			int [] bool_ids = new int[view_mapping_list.size]; 
			
			ArrayList<Argument> head_args = head_variable_query_mapping.get(subgoal.name);
			
			if(head_args == null)
				continue;
						
			HashSet<Argument> curr_rel_head_args = new HashSet<Argument>();
			
			ArrayList<Tuple> curr_rel_tuples = new ArrayList<Tuple>();
			
			for(int j = 0; j<c_unit_str.length; j++)
			{
				
//				String c_view_name = new String();
				
				String c_view_name = c_unit_str[j].split("\\(")[0];
					
				String key = c_view_name + populate_db.separator + subgoal.name;
				
				ArrayList<Tuple> available_tuples = view_tuple_mapping.get(key);
				
				ArrayList<Tuple> valid_tuples = curr_tuple_mapping.get(c_view_name);
				
				if(valid_tuples == null)
					continue;
				
				ArrayList<Tuple> valid_tuples_copy = (ArrayList<Tuple>) valid_tuples.clone();
				
				valid_tuples_copy.retainAll(available_tuples);
				
				curr_rel_tuples.addAll(valid_tuples_copy);
				
				for(int k = 0; k<valid_tuples_copy.size(); k++)
				{
					Tuple valid_tuple = valid_tuples_copy.get(k);
					
					curr_rel_head_args.addAll(valid_tuple.getArgs());
					
//					if(valid_tuple.mapSubgoals_str.get(q.subgoal_name_mapping.get(subgoal.name)).equals(subgoal.name))
//					{
//						
						int id = view_mapping_list.find(valid_tuple.name + populate_db.separator + valid_tuple.mapSubgoals_str);
						
						bool_ids[id] = 1;
						
//						for(int p = 0; p<valid_tuple.getArgs().size(); p++)
//						{
//							Argument curr_head_arg = (Argument) valid_tuple.getArgs().get(p);
//							
//							if(head_args.contains(curr_head_arg))
//							{
//								valid_arg_tuple_mapping.
//							}
//						}
//					}
					
				}
					
//					if(available_tuples != null)
//					{
////						Vector<Tuple> valid_tuples = new Vector<Tuple> ();
//						
//						Tuple valid_tuple = null;
//						
//						
//						
//					}
			}
			
			int_matrix.add(bool_ids);
			
			curr_rel_head_args.retainAll(head_args);
			
			for(Iterator iter = curr_rel_head_args.iterator(); iter.hasNext();)
			{
				
				int [] bool_ids_arg = new int[view_mapping_list.size]; 
				
				Argument curr_head_arg = (Argument) iter.next();
				
				ArrayList<Tuple> valid_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(curr_head_arg).clone();
				
				valid_tuples.retainAll(curr_rel_tuples);
				
				for(int p = 0; p < valid_tuples.size(); p++)
				{
					int id = view_mapping_list.find(valid_tuples.get(p).name + populate_db.separator + valid_tuples.get(p).mapSubgoals_str);
					
					bool_ids_arg[id] = 1;
				}
				
				int_matrix.add(bool_ids_arg);
			}
			
			
		}
		
//		if(view_mapping_global_valid.isEmpty())
//		{
//			view_mapping_global_valid.addAll(int_matrix);
//		}
//		else
//		{
//			for(int i = 0; i<view_mapping_global_valid.size(); i++)
//			{
//				
//				int[] curr_view_mapping_global_valid = view_mapping_global_valid.get(i);
//				
//				for(int j = 0; j<curr_view_mapping_global_valid.length; j++)
//				{
//					curr_view_mapping_global_valid[j] = (curr_view_mapping_global_valid[j] + int_matrix.get(i)[j])/2;
//				}
//				
//				
//				
//				view_mapping_global_valid.set(i, curr_view_mapping_global_valid);
//			}
//		}
		
	}
	
	public static ArrayList<Integer> get_citation_units_condition(ArrayList<String[]> c_units, HashMap<String, ArrayList<Tuple>> curr_tuple_mapping, int start_pos, Query q, HashSet<String> curr_relation_names, HashSet<Argument> curr_covered_args) throws ClassNotFoundException, SQLException
	{		
//		boolean parameterized = true;
		
		HashSet<Tuple> all_valid_tuples = new HashSet<Tuple>();		
		
		for(int i = 0; i<c_units.size(); i++)
		{			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			String [] c_unit_str = c_units.get(i);
						
			ArrayList<Argument> head_args = head_variable_query_mapping.get(subgoal.name);
			
			if(head_args == null)
				continue;
						
			HashSet<Argument> curr_rel_head_args = new HashSet<Argument>();
			
			HashSet<Tuple> curr_rel_tuples = new HashSet<Tuple>();

			
			for(int j = 0; j<c_unit_str.length; j++)
			{
				
//				String c_view_name = new String();
				
				String c_view_name = c_unit_str[j].split("\\(")[0];
					
				String key = c_view_name + populate_db.separator + subgoal.name;
				
				ArrayList<Tuple> available_tuples = view_tuple_mapping.get(key);
				
				ArrayList<Tuple> valid_tuples = curr_tuple_mapping.get(c_view_name);
				
				if(valid_tuples == null)
					continue;
				
				ArrayList<Tuple> valid_tuples_copy = (ArrayList<Tuple>) valid_tuples.clone();
				
				valid_tuples_copy.retainAll(available_tuples);
				
				curr_rel_tuples.addAll(valid_tuples_copy);
				
				for(int k = 0; k<valid_tuples_copy.size(); k++)
				{
					Tuple valid_tuple = valid_tuples_copy.get(k);
					
					curr_rel_head_args.addAll(valid_tuple.getArgs());
//					
////					if(valid_tuple.mapSubgoals_str.get(q.subgoal_name_mapping.get(subgoal.name)).equals(subgoal.name))
////					{
////						
//						int id = view_mapping_list.find(valid_tuple.name + populate_db.separator + valid_tuple.mapSubgoals_str);
//						
//						bool_ids[id] = 1;
//						
////						for(int p = 0; p<valid_tuple.getArgs().size(); p++)
////						{
////							Argument curr_head_arg = (Argument) valid_tuple.getArgs().get(p);
////							
////							if(head_args.contains(curr_head_arg))
////							{
////								valid_arg_tuple_mapping.
////							}
////						}
////					}
//					
				}
					
//					if(available_tuples != null)
//					{
////						Vector<Tuple> valid_tuples = new Vector<Tuple> ();
//						
//						Tuple valid_tuple = null;
//						
//						
//						
//					}
			}
						
			curr_rel_head_args.retainAll(head_args);
			
			curr_covered_args.addAll(curr_rel_head_args);
			
			if(!curr_rel_tuples.isEmpty())
			{
				curr_relation_names.add(subgoal.name);
			}
			
			all_valid_tuples.addAll(curr_rel_tuples);
			
//			for(Iterator iter = curr_rel_head_args.iterator(); iter.hasNext();)
//			{
//				
//				int [] bool_ids_arg = new int[view_mapping_list.size]; 
//				
//				Argument curr_head_arg = (Argument) iter.next();
//				
//				ArrayList<Tuple> valid_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(curr_head_arg).clone();
//				
//				valid_tuples.retainAll(curr_rel_tuples);
//				
//				for(int p = 0; p < valid_tuples.size(); p++)
//				{
//					int id = view_mapping_list.find(valid_tuples.get(p).name + populate_db.separator + valid_tuples.get(p).mapSubgoals_str);
//					
//					bool_ids_arg[id] = 1;
//				}
//				
//				int_matrix.add(bool_ids_arg);
//			}
			
			
		}
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for(Iterator iter = all_valid_tuples.iterator(); iter.hasNext();)
		{
			Tuple tuple = (Tuple) iter.next();
			
			String key = tuple.name + populate_db.separator + tuple.mapSubgoals_str;
			
			int id = view_mapping_list.find(key);
			
			view_mapping_global_valid[id] += 1;
			
			ids.add(id);
		}
		
		for(int i = 0; i<view_mapping_global_valid.length; i++)
		{
			view_mapping_global_valid[i] = view_mapping_global_valid[i]/2;
		}
		
		return ids;
	}
	
	
	static boolean check_value(String paras, int start_pos, Vector<Integer> positions, ResultSet rs, int offset, int[] final_pos) throws SQLException
	{
		for(int i = 0; i<positions.size(); i++)
		{
			int position = start_pos + positions.get(i) + offset;
			
			String value = rs.getString(position);
			
			if(value.equals(paras))
			{
				final_pos[0] = position;
				return true;
			}
			
		}
		
		return false;
	}
	
//	public static HashMap<String,citation_view> get_citation_units_unique(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
//	{
////		Vector<citation_view> c_views = new Vector<citation_view>();
//		
//		HashMap<String,citation_view> c_view_map = new HashMap<String, citation_view>();
//		
//		for(int i = 0; i<c_units.size(); i++)
//		{
////			Vector<citation_view> c_view = new Vector<citation_view>();
//			String [] c_unit_str = c_units.get(i);
//			for(int j = 0; j<c_unit_str.length; j++)
//			{
//				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
//				{
//					
//					String c_view_name = c_unit_str[j].split("\\(")[0];
//					
//					Vector<String> c_view_paras = new Vector<String>();
//					
//					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//					
//					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
//					
//					curr_c_view.put_paramters(c_view_paras);
//					
//					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
//				}
//				else
//				{
//					citation_view_unparametered curr_c_view = new citation_view_unparametered(c_unit_str[j]);
//					
//					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
//				}
//			}
//			
//		}
//		
////		Set set = c_view_map.keySet();
////		
////		for(Iterator iter = set.iterator();iter.hasNext();)
////		{
////			String key = (String) iter.next();
////			
////			c_views.add(c_view_map.get(key));
////		}
//		
//		return c_view_map;
//	}
//	
	
	
	public static citation_view_vector get_valid_citation_combination(ArrayList<Integer> citation_view_ids) throws ClassNotFoundException, SQLException
	{
//		HashSet<citation_view_vector> c_combinations = new HashSet<citation_view_vector>();
		
		Vector<citation_view> c_view_vec = new Vector<citation_view>();
		
		for(int i = 0; i<citation_view_ids.size(); i++)
		{
			Tuple valid_tuple = tuples.get(citation_view_ids.get(i));
			
			if(valid_tuple.lambda_terms.size() > 0)
				{
//					Vector<String> lambda_term_values = new Vector<String>(valid_tuple.lambda_terms.size());
					
//					for(int p = 0; p<valid_tuple.lambda_terms.size(); p++)
//					{
//						int l_id = lambda_term_id_mapping.get(valid_tuple.lambda_terms.get(p).toString());
//						
//						int pos = start_pos + l_id + 1;
//						
//						lambda_term_values.add(rs.getString(pos));
//						
//					}
					
					citation_view_parametered c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);

//					citation_view_mapping.put(key, c);
					
					c_view_vec.add(c);
				}	
				else
				{
					
					citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
					
//					citation_view_mapping.put(key, c);
					
					c_view_vec.add(c);
				}
		}
		
		citation_view_vector view_combinations = new citation_view_vector(c_view_vec);
		
		return view_combinations;
	}
	
	static void remove_duplicate_view_combinations(ArrayList<citation_view_vector> c_view_template)
	{
		for(int i = 0; i<c_view_template.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = c_view_template.get(i);
			

			
			for(int j = 0; j<c_view_template.size(); j++)
			{
//				String string = (String) iterator.next();
				

//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_view_template.get(j);
					
					
//					if(c_combination.c_vec.size() == 3)
//					{
//						
//						System.out.println(c_combination);
//						
//						System.out.println(curr_combination);
//						
//						int k = 0;
//						
//						k++;
//					}
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && table_names_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					{
						
						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_view_template.remove(i);
							
							i--;
							
							break;
						}
					}
					
					if(c_combination.index_str.equals(curr_combination.index_str) && c_combination.table_name_str.equals(curr_combination.table_name_str))
					{
						c_view_template.remove(i);
						
						i--;
						
						break;
					}
				}
				
			}
		}
		
	}
	
	static void remove_duplicate_view_combinations_final(ArrayList<citation_view_vector> c_view_template)
	{
		for(int i = 0; i<c_view_template.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = c_view_template.get(i);
			

			
			for(int j = 0; j<c_view_template.size(); j++)
			{
//				String string = (String) iterator.next();
				

//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_view_template.get(j);
					
					
					
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && table_names_contains(c_combination, curr_combination) && c_combination.c_vec.size() > curr_combination.c_vec.size()))
					{
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_view_template.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
	}
	
	public static Vector<citation_view_vector> remove_duplicate_final(Vector<citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
				
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
						
			citation_view_vector c_combination = c_combinations.get(i);
			
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
					if(c_combination.index_str.equals(curr_combination.index_str))
					{
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
				
		return c_combinations;
	}
	
	public static Vector<citation_view_vector> remove_duplicate_final2(Vector<citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
						
			citation_view_vector c_combination = c_combinations.get(i);
			
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
					if(c_combination.index_str.equals(curr_combination.index_str))
					{
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(i);
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					{
						
						
							c_combinations.remove(i);
							
							i--;
							
							break;
					}
					
				}
				
			}
		}
				
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		
		
				
		return c_combinations;
	}
	
	
	public static Vector<citation_view_vector> remove_duplicate(Vector<citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
//		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(i);
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination)))
					{
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
				
		return c_combinations;
	}
	
	static boolean table_names_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		String s1 = ".*";
		
		String s2 = c_vec1.table_name_str;
		
		for(citation_view curr_view_mapping:c_vec2.c_vec)
		{
			
			String str = curr_view_mapping.get_table_name_string();
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			s1 += str + ".*";
			
		}
		
		return s2.matches(s1);
	}
	
	static boolean table_names_equals(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		String s1 = c_vec2.table_name_str;
		
		String s2 = c_vec1.table_name_str;
		
		return s1.equals(s2);
	}
	
	static boolean view_vector_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		
		String s1 = ".*";
		
		String s2 = c_vec1.index_str;
		
		for(int i = 0; i<c_vec2.index_vec.size(); i++)
		{
			String str = c_vec2.index_vec.get(i);
			
			str = str.replaceAll("\\(", "\\\\(");
			
			str = str.replaceAll("\\)", "\\\\)");
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			str = str.replaceAll("\\/", "\\\\/");
			
			s1 += str + ".*";
		}
		
		return s2.matches(s1);
		
//		Vector<String> c1 = new Vector<String>();
//		
//		c1.addAll(c_vec1.index_vec);
//		
//		
//		Vector<String> c2 = new Vector<String>();
//		
//		c2.addAll(c_vec2.index_vec);
//		
//		for(int i = 0; i < c2.size(); i++)
//		{
//			int id = c1.indexOf(c2.get(i));
//			
//			if(id >= 0)
//			{
//				c1.remove(id);
//				
//				c2.remove(i);
//				
//				i --;
//			}
//			
//		}
//		
//		if(c2.size() == 0)
//			return true;
//		else
//			return false;
	}
//	
//	public static Vector<citation_view_vector> remove_duplicate_final(Vector<citation_view_vector> c_combinations)
//	{
////		Vector<Boolean> retains = new Vector<Boolean>();
//		
////		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
//		
//		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
//		
////		Set set = c_combinations.keySet();
//		
////		for(Iterator iter = set.iterator(); iter.hasNext();)
//		for(int i = 0; i<c_combinations.size(); i++)
//		{
////			String str = (String) iter.next();
//			
//			boolean contains = false;
//			
//			citation_view_vector c_combination = c_combinations.get(i);
//			
////			for(Iterator iterator = set.iterator(); iterator.hasNext();)
//			for(int j = 0; j<c_combinations.size(); j++)
//			{
////				String string = (String) iterator.next();
//				
//				if(i!=j)
//				{
//					citation_view_vector curr_combination = c_combinations.get(j);
//					
//					if(c_combination.index_vec.containsAll(curr_combination.index_vec))
//					{
//						contains = true;
//						
//						break;
//					}
//				}
//				
//			}
//			
//			if(!contains)
//				update_combinations.add(c_combination);
//		}
//		
//				
//		return update_combinations;
//	}
//	
	public static ArrayList<citation_view_vector> join_operation(ArrayList<citation_view_vector> c_combinations, ArrayList<citation_view> insert_citations, int i)
	{
		if(i == 0)
		{
			for(int k = 0; k<insert_citations.size(); k++)
			{
				Vector<citation_view> c = new Vector<citation_view>();
				
				c.add(insert_citations.get(k));
				
//				String str = String.valueOf(insert_citations.get(k).get_index());
				
				c_combinations.add(new citation_view_vector(c));
				
			}
			
			return c_combinations;
		}
		else
		{
//			HashMap<String, citation_view_vector> updated_c_combinations = new HashMap<String, citation_view_vector>();
			
			ArrayList<citation_view_vector> updated_c_combinations = new ArrayList<citation_view_vector>();
			
			
			
//			citation_view_vector update_vec = new citation_view_vector(insert_citations);
//			
//			
//			c_combinations.add(update_vec);
//			
//			return c_combinations;
//			Set set = c_combinations.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iter.next();
				
				citation_view_vector curr_combination = c_combinations.get(j);
								
				for(int k = 0; k<insert_citations.size(); k++)
				{
					
					citation_view_vector new_citation_vec = citation_view_vector.merge(curr_combination, insert_citations.get(k));
			
					updated_c_combinations.add(new_citation_vec);
				}
			}
						
			return updated_c_combinations;
			
		}
	}
	
	public static void rename_column(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column citation_view to "+ view.name + "_citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
	public static void rename_column_back(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column "+ view.name +"_citation_view to citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
//	public static Vector<Vector<citation_view>> str2c_view(Vector<Vector<String>> citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<Vector<citation_view>> c_view = new Vector<Vector<citation_view>>();
//		
//		for(int i = 0; i<citation_str.size(); i++)
//		{
//			Vector<String> curr_c_view_str = citation_str.get(i);
//			
//			Vector<citation_view> curr_c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<curr_c_view_str.size();j++)
//			{
//				if(citation_str.get(i).contains("(") && citation_str.get(i).contains(")"))
//				{
//					String citation_view_name = curr_c_view_str.get(i).split("\\(")[0];
//					
//					String [] citation_parameters = curr_c_view_str.get(i).split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					curr_c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					curr_c_view.add(new citation_view_unparametered(curr_c_view_str.get(i)));
//				}
//			}
//			
//			c_view.add(curr_c_view);
//		}
//		
//		return c_view;
//	}
//	
//	
	public static Vector<String> intersection(Vector<String> c1, Vector<String> c2)
	{
		Vector<String> c = new Vector<String>();
		
		for(int i = 0; i<c1.size(); i++)
		{
			if(c2.contains(c1.get(i)))
				c.add(c1.get(i));
		}
		
		return c;
	}
	
	public static Vector<Vector<String>> get_citation(Subgoal subgoal,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<String>> c_view = new Vector<Vector<String>>();
		
		String table_name = subgoal.name + "_c";
		
		String schema_query = "select column_name from INFORMATION_SCHEMA.COLUMNS where table_name = '" + table_name + "'";
		
		
		pst = c.prepareStatement(schema_query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		boolean constant_variable = false;
		
		String citation_view_query = "select citation_view from " + table_name;
		
		while(rs.next())
		{
			String column_name = rs.getString(1);
			
			Argument arg = (Argument) subgoal.args.get(num++);
			
			if(arg.type == 1)
			{
				if(constant_variable == false)
				{
					constant_variable = true;
					
					citation_view_query += " where ";
				}
				
				citation_view_query += column_name + "=" + arg.name;
			}
			
			if(num >= subgoal.args.size())
				break;
		}
		
		pst = c.prepareStatement(citation_view_query);
		
		rs = pst.executeQuery();
		
		while(rs.next())
		{
			String[] citation_str = rs.getString(1).split("\\"+populate_db.separator);
			
			
			
			Vector<String> curr_c_view = new Vector<String>();
			
			curr_c_view.addAll(Arrays.asList(citation_str));
			
			c_view.add(curr_c_view);
		}
		
		return c_view;
	}
	
//	public static Vector<citation_view> str2c_unit(String [] citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<citation_view> c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<citation_str.length;j++)
//			{
//				if(citation_str[j].contains("(") && citation_str[j].contains(")"))
//				{
//					String citation_view_name = citation_str[j].split("\\(")[0];
//					
//					String [] citation_parameters = citation_str[j].split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					c_view.add(new citation_view_unparametered(citation_str[j]));
//				}
//			}
//		
//		return c_view;
//	}
	

//    static Query parse_query(String query)
//    {
//        
//        String head = query.split(":")[0];
//        
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
//        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        return new Query(head_name, head_subgoal, body_subgoals);
//    }
//    
//    static Query parse_query(String head, String []body, String []lambda_term_str)
//    {
//                
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
////        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        
//        
//        Vector<Argument> lambda_terms = new Vector<Argument>();
//        
//        if(lambda_term_str != null)
//        {
//	        for(int i =0;i<lambda_term_str.length; i++)
//	        {
//	        	lambda_terms.add(new Argument(lambda_term_str[i]));
//	        }
//        
//        }
//        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
//    }


}
