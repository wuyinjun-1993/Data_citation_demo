package edu.upenn.cis.citation.stress_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.aggregation.Aggregation3;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test_stable;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_full_min_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning2_full_min_test;
import edu.upenn.cis.citation.user_query.query_storage;
import junit.framework.Assert;

public class stress_test3_2 {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 6;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> get_unique_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
		
		HashSet<String> relations = new HashSet<String>();
		
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relations.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
		
		relation_names.addAll(relations);
		
		return relation_names;
		
	}
	
	static Vector<String> get_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
				
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relation_names.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
				
		return relation_names;
		
	}
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		
//		reset();
		
		query_generator.query_result_size = 10000;
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
		
		
	    int k = Integer.valueOf(args[0]);
	    
	    boolean new_query = Boolean.valueOf(args[1]);
		
		boolean new_rounds = Boolean.valueOf(args[2]);
		
		boolean tuple_level = Boolean.valueOf(args[3]);
		
		boolean new_start = Boolean.valueOf(args[4]);
		
				
//		Query query = query_storage.get_query_by_id(1);
		
//		for(int k = 3; k<=query_num; k++)
			if(new_query)
			{
				System.out.println("new query");
				
				reset(c, pst);
			}
						
			Query query = null;
			
			try{
				query = query_storage.get_query_by_id(1);
			}
			catch(Exception e)
			{
				query = query_generator.gen_query(k, c, pst);
				query_storage.store_query(query, new Vector<Integer>());
				System.out.println(query);
			}
			
			Vector<String> relations = get_unique_relation_names(query);
			
			Vector<Query> views = null;
			
			if(new_rounds)
			{
				
				views = view_generator.gen_default_views(relations, c, pst);
				
//				views = view_generator.generate_store_views_without_predicates(relation_names, view_size, query.body.size());
				
				for(Iterator iter = views.iterator(); iter.hasNext();)
				{
					Query view = (Query) iter.next();
					
					System.out.println(view);
				}
			}
			else
			{
				views = view_operation.get_all_views();
				
				if(new_start)
				{
					System.out.println();
					
					view_generator.initial();
					
					view_generator.gen_one_additional_view(views, relations, query.body.size(), query, c, pst);
					
					
				}
				
				for(Iterator iter = views.iterator(); iter.hasNext();)
				{
					Query view = (Query) iter.next();
					
					System.out.println(view);
				}
			}
			
			stress_test(query, c, pst, views, tuple_level);

		}
	
	static void stress_test(Query query, Connection c, PreparedStatement pst, Vector<Query> views, boolean tuple_level) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
		
		Vector<Vector<citation_view_vector>> citation_view1 = new Vector<Vector<citation_view_vector>>();

		Vector<Vector<citation_view_vector>> citation_view2 = new Vector<Vector<citation_view_vector>>();

		
//		while(views.size() < view_max_size)
		if(tuple_level)
		{						
			double end_time = 0;

			
			double start_time = System.nanoTime();
			
			
			for(int k = 0; k<times; k++)
			{
				
//				citation_view1.clear();
				
				Tuple_reasoning1_test.tuple_reasoning(query, c, pst);
				
				Tuple_reasoning1_test_stable.tuple_reasoning(query, citation_strs2, citation_view_map1, c, pst);
				
				
//				System.gc();
				
			}
			
			Set<Head_strs> heads = citation_strs2.keySet();
			
			for(Iterator iter = heads.iterator(); iter.hasNext();)
			{
				Head_strs head = (Head_strs) iter.next();
				
//				System.out.println(head);
				
				HashSet<String> curr_citation2 = citation_strs2.get(head);
				
				HashSet<String> curr_citation1 = Tuple_reasoning1_test.gen_citation(head, c, pst);
				
//				System.out.println();
				
				if(curr_citation1.toString().equals("[]") && curr_citation2.toString().equals("[{\"db_name\":\"IUPHAR/BPS Guide to PHARMACOLOGY\",\"author\":[]}]"))
					continue;
				
				if(curr_citation1.toString().length() != curr_citation2.toString().length())
				{
					System.out.println(curr_citation1);
					
					System.out.println(curr_citation2);
					
					Assert.assertEquals(true, false);
				}
			}
			
			
			
//			System.out.println(SizeOf.humanReadable(SizeOf.deepSizeOf(citation_strs))); 
			
			end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			Set<Head_strs> h_l = citation_strs.keySet();
			
			double citation_size1 = 0;
			
			int row = 0;
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = citation_strs.get(h_value);
				
				citation_size1 += citations.size();
				
				row ++;
				
			}
			
			if(row !=0)
			citation_size1 = citation_size1 / row;
			
			System.out.print(citation_size1 + "	");
			
			System.out.print(row + "	");
			
//			Set<Head_strs> head = citation_view_map1.keySet();
//			
//			int row_num = 0;
//			
//			double origin_citation_size = 0.0;
//			
//			for(Iterator iter = head.iterator(); iter.hasNext();)
//			{
//				Head_strs head_val = (Head_strs) iter.next();
//				
//				Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//				
//				row_num++;
//				
//				for(int p = 0; p<c_view.size(); p++)
//				{
//					origin_citation_size += c_view.get(p).size();
//				}
//				
//			}
//			
//			
//			
//			if(row_num !=0)
//				origin_citation_size = origin_citation_size / row_num;
//			
			
			
			System.out.print(Tuple_reasoning1_test.covering_set_num * 1.0/row + "	");
			
			System.out.println("pre_processing::" + Tuple_reasoning1_test.pre_processing_time + "	");
			
			System.out.println("query::" + Tuple_reasoning1_test.query_time + "	");
			
			System.out.println("reasoning::" + Tuple_reasoning1_test.reasoning_time + "	");
			
			System.out.println("population::" + Tuple_reasoning1_test.population_time + "	");
			
			HashSet<String> agg_citation1 = Tuple_reasoning1_test.tuple_gen_agg_citations(query);
			
			Vector<String> agg_citation2 = Tuple_reasoning1_test_stable.tuple_gen_agg_citations(query);
			
			System.out.println(agg_citation1);
			
			System.out.println(agg_citation2);
			
			for(int i = 0; i<agg_citation2.size(); i++)
			{
				if(agg_citation1.contains(agg_citation2.get(i)))
				{
					continue;
				}
				else
				{
					Assert.assertEquals(true, false);
				}
			}
			
		}
		else
		{
			
			double start_time = System.nanoTime();
			
			for(int k = 0; k<times; k++)
			{
				
				Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
				
				Tuple_reasoning2_full_test2.tuple_reasoning(query, c, pst);
				
//				System.gc();
			}
			
			double end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			
			Set<Head_strs> heads = Tuple_reasoning2_full_test2.head_strs_rows_mapping.keySet();
			
			int row = 0;
			
			int size1 = 0;
			
			int size2 = 0;
			
			HashMap<Head_strs, HashSet<String>> head_values1 = new HashMap<Head_strs, HashSet<String>>();
			
			HashMap<Head_strs, HashSet<String>> head_values2 = new HashMap<Head_strs, HashSet<String>>();
			
			for(Iterator iter = heads.iterator(); iter.hasNext();)
			{
				Head_strs head = (Head_strs) iter.next();
				
//				System.out.println(head);
				
				HashSet<String> covering_set_str1 = new HashSet<String>();
				
				HashSet<String> covering_set_str2 = new HashSet<String>();
				
				
				
				HashSet<String> curr_citation2 = Tuple_reasoning2_full_test2.gen_citation(head, head_values2, covering_set_str2,  c, pst);
				
				HashSet<String> curr_citation1 = Tuple_reasoning1_full_test.gen_citation(head, head_values1, covering_set_str1, c, pst);
				
				System.out.println(covering_set_str1);
				
				System.out.println(covering_set_str2);
				
				System.out.println(curr_citation1);
				
				System.out.println(curr_citation2);
//				
				size1 += curr_citation1.size();
				
				size2 += curr_citation2.size();
				
//				System.out.println();
				
//				if(curr_citation1.toString().equals("[]") && curr_citation2.toString().equals("[{\"db_name\":\"IUPHAR/BPS Guide to PHARMACOLOGY\",\"author\":[]}]"))
//					continue;
				
				if(!covering_set_str1.equals(covering_set_str2)|| !curr_citation1.containsAll(curr_citation2) || !curr_citation2.containsAll(curr_citation1))
				{
					System.out.println(curr_citation1);
					
					System.out.println(curr_citation2);
					
					System.out.println(covering_set_str1);
					
					System.out.println(covering_set_str2);
					
					curr_citation2 = Tuple_reasoning2_full_test2.gen_citation(head, head_values2, covering_set_str2, c, pst);
					
					curr_citation1 = Tuple_reasoning1_full_test.gen_citation(head, head_values1, covering_set_str1, c, pst);
					
					Assert.assertEquals(true, false);
				}
				
				if(row > 10)
					break;
				
				row ++;
			}
			
//			Set<int[]> keys = Tuple_reasoning2_full_min_test.c_view_map.keySet();
//			
//			citation_view_vector covering_set_query1 = Tuple_reasoning1_full_min_test.covering_sets_query;
//			
//			citation_view_vector covering_set_query2 = Tuple_reasoning2_full_min_test.covering_sets_query;
//			
//			System.out.println(Tuple_reasoning1_full_min_test.covering_sets_query);
//			
//			System.out.println(Tuple_reasoning2_full_min_test.covering_sets_query);
//			
//			if(!covering_set_query1.toString().equals(covering_set_query2.toString()))
//			{
//				Assert.assertEquals(true, false);
//			}
			
//			for(Iterator iter = keys.iterator(); iter.hasNext();)
//			{
//				int [] key = (int []) iter.next();
//				
//				citation_view_vector covering_sets1 = Tuple_reasoning2_full_min_test.c_view_map.get(key);
//				
//				citation_view_vector covering_sets2 = Tuple_reasoning1_full_min_test.c_view_map.get(key);
//				
//				System.out.println(covering_sets1);
//				
//				System.out.println(covering_sets2);
//				
//			}
			
//			Set<Head_strs> keys = head_values1.keySet();
//			
//			for(Iterator iter = keys.iterator(); iter.hasNext();)
//			{
//				
//				Head_strs key = (Head_strs) iter.next();
//				
//				HashSet<String> values1 = head_values1.get(key);
//				
//				HashSet<String> values2 = head_values2.get(key);
//				
//				System.out.println(values1 + ":::" + values2);
//				
//				if(!values1.containsAll(values2))
//					Assert.assertEquals(true, false);
//				
//				if(!values2.containsAll(values1))
//					Assert.assertEquals(true, false);
//				
//			}
			
//			if(row !=0)
//				citation_size2 = citation_size2 / row;
//
//			System.out.print(citation_size2 + "	");

			System.out.print(row + "	");
			
//			Set<Head_strs> head = citation_view_map2.keySet();
//			
//			int row_num = 0;
//			
//			double origin_citation_size = 0.0;
//			
//			for(Iterator iter = head.iterator(); iter.hasNext();)
//			{
//				Head_strs head_val = (Head_strs) iter.next();
//				
//				Vector<Vector<citation_view_vector>> c_view = citation_view_map2.get(head_val);
//				
//				row_num++;
//				
//				for(int p = 0; p<c_view.size(); p++)
//				{
//					origin_citation_size += c_view.get(p).size();
//				}
//				
//			}
//			
//			
//			
//			if(row_num !=0)
//				origin_citation_size = origin_citation_size / row_num;
//						
//			System.out.print(origin_citation_size + "	");
			
			double average_size1 = size1 * 1.0 /row;
			
			double average_size2 = size2 * 1.0 /row;
			
			System.out.println(average_size1 + ":::" + average_size2);
			
			System.out.print(Tuple_reasoning2_full_test2.covering_set_num * 1.0/Tuple_reasoning2_full_test2.tuple_num + "	");
			
			System.out.print("pre_processing::" + Tuple_reasoning2_full_test2.pre_processing_time + "	");
			
			System.out.print("query::" + Tuple_reasoning2_full_test2.query_time + "	");
			
			System.out.print("reasoning::" + Tuple_reasoning2_full_test2.reasoning_time + "	");
			
			System.out.print("population::" + Tuple_reasoning2_full_test2.population_time + "	");
			
			System.out.println();
			
//			HashMap<String, String> views_citation_mapping1 = new HashMap<String, String>();
//			
//			HashMap<String, String> views_citation_mapping2 = new HashMap<String, String>();
//			
//			HashSet<String> agg_citation1 = Tuple_reasoning1_test.tuple_gen_agg_citations(query);
//			
//			Aggregation3.view_author_mapping.clear();
//			
//			HashSet<String> agg_citation2 = Tuple_reasoning2_test.tuple_gen_agg_citations(query);
//			
//			
//			Set<String> key_strs = views_citation_mapping1.keySet();
//			
//			for(Iterator iter = key_strs.iterator(); iter.hasNext();)
//			{
//				String key_str = (String)iter.next();
//				
//				String value1 = views_citation_mapping1.get(key_str);
//				
//				String value2 = views_citation_mapping2.get(key_str);
//				
//				if(!value1.equals(value2))
//				{
//					System.out.println("error::");
//					
//					System.out.println(key_str);
//					
//					System.out.println(value1);
//					
//					System.out.println(value2);
//					
//					Assert.assertEquals(true, false);
//				}
//			}
//			
//			for(Iterator iter = agg_citation2.iterator(); iter.hasNext();)
//			{
//				String value = (String) iter.next();
//				
//				if(!agg_citation1.contains(value))
//				{
//					System.out.println(agg_citation1);
//					
//					System.out.println(agg_citation2);
//					
//					Assert.assertEquals(true, false);
//				}
//			}
			
//			Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
			
//			Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
			
			
//						
//			reset();
//			
//			Vector<Query> queries = query_generator.gen_queries(j, size_range);
//			
//			Query query = query_generator.gen_query(j, c, pst);
			
//			System.out.println(queries.get(0));

			
//			Vector<String> relation_names = get_unique_relation_names(queries.get(0));
			
//			view_generator.generate_store_views(relation_names, num_views);
			
//			query_storage.store_query(queries.get(0), new Vector<Integer>());
			
			
		}
	}
	
	
	static void reset(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		String query = "delete from user_query2conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query2subgoals";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		
	}

}
