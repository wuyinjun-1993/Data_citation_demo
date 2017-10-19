package edu.upenn.cis.citation.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.aggregation.Aggregation3;
import edu.upenn.cis.citation.aggregation.Aggregation5;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test_opt;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.reasoning1.schema_reasoning;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_min_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning2_min_test;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;
import edu.upenn.cis.citation.user_query.query_storage;
import junit.framework.Assert;

public class test_opt_equality {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> relations = new Vector<String>();
	
	static double []ratio = {0.1, 0.2, 0.5, 0.6, 0.8, 1.0};
		
	static String path = "reasoning_results/";
	
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
	
	public static void get_table_size(Vector<String> relations, Connection c, PreparedStatement pst) throws SQLException
	{
		int original_size = 0;
		
		int annotated_relation_size = 0;
		
		for(int i = 0; i<relations.size(); i++)
		{
			original_size += get_single_table_size(relations.get(i), c, pst);
			
//			annotated_relation_size += get_single_table_size(relations.get(i) + populate_db.suffix, c, pst);
			
			annotated_relation_size += get_single_table_size(relations.get(i), c, pst);
		}
		
		System.out.println("original_relation_size::" + original_size);
		
		System.out.println("annotated_relation_size::" + annotated_relation_size);
	}
	
	static int get_single_table_size(String relation, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT pg_total_relation_size('"+ relation +"')";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			int size =  rs.getInt(1);
			
//			int size_int = Integer.valueOf(size.substring(0, size.indexOf(" ")));
			
			return size;
		}
		
		return 0;
	}
	
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{		
		Connection c1 = null;
		
		Connection c2 = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c1 = DriverManager
	        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
		
	    c2 = DriverManager
	        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
		
//	    System.out.println(get_single_table_size("family", c, pst));
	    query_generator.query_result_size = 10000;
	    
	    int k = Integer.valueOf(args[0]);
	    
	    int view_size = Integer.valueOf(args[1]);
	    
	    boolean new_query = Boolean.valueOf(args[2]);
		
		boolean new_rounds = Boolean.valueOf(args[3]);
		
		boolean tuple_level = Boolean.valueOf(args[4]);
		
		boolean new_start = Boolean.valueOf(args[5]);
				
		query_generator.init_parameterizable_attributes(c2, pst);
		
//		Query query = query_storage.get_query_by_id(1);
		
//		for(int k = 3; k<=query_num; k++)
		{
			if(new_query)
			{
				System.out.println("new query");
				
				reset(c1, pst);
			}
						
			Query query = null;
			
			try{
				query = query_storage.get_query_by_id(1);
			}
			catch(Exception e)
			{
				query = query_generator.gen_query(k, c1, pst);
				query_storage.store_query(query, new Vector<Integer>());
				System.out.println(query);
			}
			
			relations = get_unique_relation_names(query);
			
			Vector<Query> views = null;
			
			if(new_rounds)
			{
				
				populate_db.renew_table(c1, pst);
				
				get_table_size(relations, c1, pst);
				
//				views = view_generator.gen_default_views(relations, c1, c2, pst);
				
				views = view_generator.generate_store_views_without_predicates(relations, view_size, query.body.size(), c1, c2, pst);
				
				for(Iterator iter = views.iterator(); iter.hasNext();)
				{
					Query view = (Query) iter.next();
					
					System.out.println(view);
				}
				
				get_table_size(relations, c1, pst);
			}
			else
			{
				views = view_operation.get_all_views(c2, pst);
				
				if(new_start)
				{
					System.out.println();
					
					view_generator.initial();
					
					view_generator.gen_one_additional_view(views, relations, query.body.size(), query, c1, c2, pst);
					
					for(Iterator iter = views.iterator(); iter.hasNext();)
					{
						Query view = (Query) iter.next();
						
						System.out.println(view);
					}
					
					get_table_size(relations, c1, pst);
				}
			}
			
			Vector<Query> all_citation_queries = Query_operation.get_all_citation_queries(c2, pst);
			
			c1.close();
			
			c2.close();
			
			stress_test(query, tuple_level);

			
			
			Vector<Query> user_query = new Vector<Query>();
			
			user_query.add(query);
			
			output_queries(user_query, path + "user_queries");
			
			output_queries(views, path + "views");
			
			output_queries(all_citation_queries, path + "citation_query");
			
		}
		

		
		
		
		
		
	}
	
	
	
	static void output_queries(Vector<Query> queries, String file_name) throws IOException
	{
		Vector<String> query_strs = new Vector<String>();
		
		for(int i = 0; i<queries.size(); i++)
		{
			String query_str = Query_operation.covert2data_str(queries.get(i));
			
			query_strs.add(query_str);
		}
		
		Query_operation.write2file(file_name, query_strs);
	}
	
	
	static void stress_test(Query query, boolean tuple_level) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
		
		Vector<Vector<citation_view_vector>> citation_view1 = new Vector<Vector<citation_view_vector>>();

		Vector<Vector<citation_view_vector>> citation_view2 = new Vector<Vector<citation_view_vector>>();

		
		if(tuple_level)
		{						
			
			Connection c = null;
		      PreparedStatement pst = null;
			Class.forName("org.postgresql.Driver");
		    c = DriverManager
		        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
			
		    schema_reasoning.prepare_info = false;
		    
			HashSet<String> agg_citations = null;
			
			double end_time = 0;

			
			double start_time = 0;
			
			start_time = System.nanoTime();
			
			schema_reasoning.tuple_reasoning(query, c, pst);
			
			end_time = System.nanoTime();
			
			schema_reasoning.tuple_gen_agg_citations(query, c, pst);
												
//			Set<int[]> keys = schema_reasoning.c_view_map.keySet();
//			
//			ArrayList<citation_view_vector> covering_sets1 = null;
//			
//			for(Iterator it1 = keys.iterator(); it1.hasNext();)
//			{
//				int [] key = (int[]) it1.next();
//				
//				covering_sets1 = schema_reasoning.c_view_map.get(key);
//			}
			
//			System.out.println(covering_sets1);
//			
//			keys = Tuple_reasoning1_full_test_opt.c_view_map.keySet();
//			
//			HashSet<citation_view_vector2> covering_sets2 = null;
//			
//			for(Iterator it1 = keys.iterator(); it1.hasNext();)
//			{
//				int [] key = (int[]) it1.next();
//				
//				covering_sets2 = Tuple_reasoning1_full_test_opt.c_view_map.get(key);
//			}
//			
//			System.out.println(covering_sets2);
//			
//			System.out.println(covering_sets1.containsAll(covering_sets2));
//			
//			System.out.println(covering_sets2.containsAll(covering_sets1));
//			
//			System.out.println(covering_sets2.size() == covering_sets1.size());
			
			double time = (end_time - start_time)*1.0;
			
			time = time /(times * 1000000000);
//			
			System.out.print(time + "s	");
//			
//			Set<Head_strs> h_l = Tuple_reasoning1_full_test.head_strs_rows_mapping.keySet();
//			
//			double citation_size1 = 0;
//			
//			int row = 0;
//			
//			start_time = System.nanoTime();
//			
//			for(Iterator iter = h_l.iterator(); iter.hasNext();)
//			{
//				Head_strs h_value = (Head_strs) iter.next();
//				
//				HashSet<String> citations = Tuple_reasoning1_full_test.gen_citation(h_value, c, pst);
//				
//				citation_size1 += citations.size();
//								
//				row ++;
//				
//				if(row >= 10)
//					break;
//				
//			}
//			
//			end_time = System.nanoTime();
//			
//			if(row !=0)
//			citation_size1 = citation_size1 / row;
//			
//			time = (end_time - start_time)/(row * 1.0 * 1000000000);
//			
//			System.out.print(time + "s	");
//			
//			System.out.print(citation_size1 + "	");
//			
//			System.out.print(row + "	");
//			
//			System.out.print(Tuple_reasoning1_full_test.covering_set_num * 1.0/Tuple_reasoning1_full_test.tuple_num + "	");
//			
//			System.out.print("pre_processing::" + Tuple_reasoning1_full_test.pre_processing_time + "	");
//			
//			System.out.print("query::" + Tuple_reasoning1_full_test.query_time + "	");
//			
//			System.out.print("reasoning::" + schema_reasoning.reasoning_time + "	");
//			
//			System.out.print("population::" + Tuple_reasoning1_full_test.population_time + "	");
//			
//			System.out.print("Aggregation::" + Tuple_reasoning1_full_test.aggregation_time + "	");
					
			
			System.out.print(schema_reasoning.covering_set_query.toString() + "	");
			
			Vector<String> agg_results = new Vector<String>();
			
			for(Iterator iter = schema_reasoning.covering_set_query.iterator(); iter.hasNext();)
			{
				citation_view_vector covering_set = (citation_view_vector) iter.next();
				 
				agg_results.add(covering_set.toString());
			}
						
			Query_operation.write2file(path + "covering_sets", agg_results);
			
			System.out.println();		
			
			c.close();
			
		}
		else
		{
			
			Connection c = null;
		      PreparedStatement pst = null;
			Class.forName("org.postgresql.Driver");
		    c = DriverManager
		        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
			
			HashSet<String> agg_citations = null;
			
			Tuple_reasoning1_full_test_opt.prepare_info = false;
			
			double end_time = 0;

			
			double start_time = 0;
			
			start_time = System.nanoTime();
			
			Tuple_reasoning1_full_test_opt.tuple_reasoning(query, c, pst);
			
			end_time = System.nanoTime();
			
			Set<int[]> keys = Tuple_reasoning1_full_test_opt.c_view_map.keySet();
			
			HashSet<citation_view_vector> covering_sets2 = null;
			
			for(Iterator it1 = keys.iterator(); it1.hasNext();)
			{
				int [] key = (int[]) it1.next();
				
				covering_sets2 = Tuple_reasoning1_full_test_opt.c_view_map.get(key);
			}
			
			schema_reasoning.tuple_reasoning(query, c, pst);
			
			HashSet<citation_view_vector> minus = schema_reasoning.covering_set_query;
			
			System.out.println(covering_sets2.size());
			
			System.out.println(minus.size());
			
			
			covering_sets2.removeAll(minus);
						
			System.out.println(covering_sets2);
			
			double time = (end_time - start_time)*1.0;
			
			time = time /(times * 1000000000);
//			
			System.out.print(Tuple_reasoning1_full_test_opt.group_num + "	");
			
			System.out.print(Tuple_reasoning1_full_test_opt.tuple_num + "	");
//			
			System.out.print(time + "s	");
//			
//			Set<Head_strs> h_l = Tuple_reasoning1_full_test.head_strs_rows_mapping.keySet();
//			
//			double citation_size1 = 0;
//			
//			int row = 0;
//			
//			start_time = System.nanoTime();
//			
//			for(Iterator iter = h_l.iterator(); iter.hasNext();)
//			{
//				Head_strs h_value = (Head_strs) iter.next();
//				
//				HashSet<String> citations = Tuple_reasoning1_full_test.gen_citation(h_value, c, pst);
//				
//				citation_size1 += citations.size();
//								
//				row ++;
//				
//				if(row >= 10)
//					break;
//				
//			}
//			
//			end_time = System.nanoTime();
//			
//			if(row !=0)
//			citation_size1 = citation_size1 / row;
//			
//			time = (end_time - start_time)/(row * 1.0 * 1000000000);
//			
//			System.out.print(time + "s	");
//			
//			System.out.print(citation_size1 + "	");
//			
//			System.out.print(row + "	");
//			
//			System.out.print(Tuple_reasoning1_full_test.covering_set_num * 1.0/Tuple_reasoning1_full_test.tuple_num + "	");
//			
			System.out.print("pre_processing::" + Tuple_reasoning1_full_test_opt.pre_processing_time + "	");
//			
			System.out.print("query::" + Tuple_reasoning1_full_test_opt.query_time + "	");
//			
			System.out.print("reasoning::" + Tuple_reasoning1_full_test_opt.reasoning_time + "	");
//			
			System.out.print("population::" + Tuple_reasoning1_full_test_opt.population_time + "	");
//			
//			System.out.print("Aggregation::" + Tuple_reasoning1_full_test.aggregation_time + "	");
					
			Vector<String> agg_results = new Vector<String>();
			
			for(Iterator iter = covering_sets2.iterator(); iter.hasNext();)
			{
				citation_view_vector covering_set = (citation_view_vector) iter.next();
				
				agg_results.add(covering_set.toString());
			}
						
			Query_operation.write2file(path + "covering_sets2", agg_results);
			
//			System.out.println(agg_results);
			
			System.out.println();		
			
			
//			output_test_case(query, Tuple_reasoning1_full_test_opt.tuple_mapping);
			
			
			
			c.close();
			
			
		}
	}
	
	static void output_test_case(Query query, HashMap<String, ArrayList<Tuple>> tuple_mappings)
	{
//		HashMap<String, ArrayList<Tuple>> tuple_mappings =  Tuple_reasoning1_full_test_opt.tuple_mapping;
		
		ArrayList<Tuple> all_tuples = new ArrayList<Tuple>();
		
		all_tuples.addAll(tuple_mappings.get("v4"));
		
		all_tuples.addAll(tuple_mappings.get("v6"));
		
		all_tuples.addAll(tuple_mappings.get("v8"));
		
		all_tuples.addAll(tuple_mappings.get("v11"));
		
		all_tuples.addAll(tuple_mappings.get("v14"));
		
		all_tuples.addAll(tuple_mappings.get("v20"));
		
		HashMap<String, Vector<String>> arg_name_mapping = new HashMap<String, Vector<String>>();
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument) query.head.args.get(i);
			
			for(int j = 0; j<all_tuples.size(); j++)
			{
				if(all_tuples.get(j).args.contains(arg))
				{
					Vector<String> names = arg_name_mapping.get(arg.name);
					
					if(names == null)
					{
						names = new Vector<String>();
						
						names.add(all_tuples.get(j).name);
					}
					else
					{
						names.add(all_tuples.get(j).name);
					}
					
					arg_name_mapping.put(arg.name, names);
				}
			}
		}
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument) query.head.args.get(i);
			
			String arg_name = arg.name;
			
			Vector<String> names = arg_name_mapping.get(arg_name);
			
			System.out.println(arg_name + ":" + names);
		}
		
	}
	
	static ArrayList<Head_strs> create_subset_head_strs(Set<Head_strs> head_strs, double ratio)
	{
		ArrayList<Head_strs> subset_head_strs = new ArrayList<Head_strs>();
		
		ArrayList<Head_strs> fullset_head_strs = new ArrayList<Head_strs>(head_strs);
		
		HashSet<Integer> selected_ids = new HashSet<Integer>();
		
		int target_size = (int)(head_strs.size() * ratio);
		
		Random r = new Random();
		
		while(selected_ids.size() < target_size)
		{
			int selected_id = r.nextInt(target_size);
			
			selected_ids.add(selected_id);
		}
		
		for(Iterator iter = selected_ids.iterator(); iter.hasNext();)
		{
			int id = (int) iter.next();
			
			subset_head_strs.add(fullset_head_strs.get(id));
		}
		
		return subset_head_strs;
		
	}
	
	static void diff_agg_time(boolean tuple_level, Query query) throws ClassNotFoundException, JSONException, SQLException
	{
		
		long start = 0;
		
		long end = 0;
		
		double time = 0;
		
		if(tuple_level)
		{
			
			Set<Head_strs> h_l = Tuple_reasoning1_test.head_strs_rows_mapping.keySet();
			
			for(int i = 0; i<ratio.length; i++)
			{
				ArrayList<Head_strs> subset_heads = create_subset_head_strs(h_l, ratio[i]);
				
				start = System.nanoTime();
				
				HashSet<String> citations = Tuple_reasoning1_test.tuple_gen_agg_citations(query, subset_heads);
				
				end = System.nanoTime();
				
				time = (end - start) * 1.0/1000000000;
				
				System.out.print(ratio[i] + "	");
				
				System.out.print("agg:::" + time + "	");
			}
		}
		else
		{
			Set<Head_strs> h_l = Tuple_reasoning2_test.head_strs_rows_mapping.keySet();
			
			for(int i = 0; i<ratio.length; i++)
			{
				ArrayList<Head_strs> subset_heads = create_subset_head_strs(h_l, ratio[i]);
				
				start = System.nanoTime();
				
				HashSet<String> citations = Tuple_reasoning2_test.tuple_gen_agg_citations(query, subset_heads);
				
				end = System.nanoTime();
				
				time = (end - start) * 1.0/1000000000;
				
				System.out.print(ratio[i] + "	");
				
				System.out.print("agg:::" + time + "	");
			}
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
