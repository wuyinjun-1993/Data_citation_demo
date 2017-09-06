package edu.upenn.cis.citation.stress_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_citation_opt;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_citation_opt2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_citation_opt;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.user_query.query_storage;

public class stress_test3 {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
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
		{
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
				
				views = view_generator.gen_default_views(relations);
				
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
					
					view_generator.gen_one_additional_view(views, relations, query.body.size(), query);
					
					for(Iterator iter = views.iterator(); iter.hasNext();)
					{
						Query view = (Query) iter.next();
						
						System.out.println(view);
					}
				}
			}
			
			stress_test(query, c, pst, views, tuple_level);

		}
		

		
		
		
		c.close();
		
		
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
		
			System.gc();
						
			double end_time = 0;

			
			double start_time = System.nanoTime();
			
			
			for(int k = 0; k<times; k++)
			{
				
				citation_view_map1.clear();
				
				citation_strs.clear();
				
//				citation_view1.clear();
				
				Tuple_reasoning1_test.tuple_reasoning(query, citation_strs,  citation_view_map1, c, pst);
				
//				System.gc();
				
			}
			
			
			
//			System.out.println(SizeOf.humanReadable(SizeOf.deepSizeOf(citation_strs))); 
			
			end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			Set<Head_strs> h_l = Tuple_reasoning1_test.head_strs_rows_mapping.keySet();
			
			double citation_size1 = 0;
			
			int row = 0;
			
			start_time = System.nanoTime();
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = Tuple_reasoning1_test.gen_citation(h_value, c, pst);
				
				citation_size1 += citations.size();
				
				row ++;
				
			}
			
			end_time = System.nanoTime();
			
			if(row !=0)
			citation_size1 = citation_size1 / row;
			
			time = (end_time - start_time)/(row * 1.0 * 1000000000);
			
			System.out.print(time + "s	");
			
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
		}
		else
		{
			
			double start_time = System.nanoTime();
			
			for(int k = 0; k<times; k++)
			{
				citation_view_map2.clear();
				
				citation_strs2.clear();
				
				citation_view2.clear();
				
				citation_view2 = Tuple_reasoning2_test.tuple_reasoning(query, citation_strs2, citation_view_map2, c, pst);
				
				System.gc();
			}
			
			double end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			
			Set<Head_strs> h_l = Tuple_reasoning2_test.head_strs_rows_mapping.keySet();
			
			double citation_size2 = 0.0;
			
			int row = 0;
			
			start_time = System.nanoTime();
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = Tuple_reasoning2_test.gen_citation(h_value, c, pst);
				
				citation_size2 += citations.size();
				
				row ++;
				
			}
			
			end_time = System.nanoTime();
			
			if(row !=0)
				citation_size2 = citation_size2 / row;
			
			time = (end_time - start_time)/(row * 1.0 * 1000000000);
			
			System.out.print(time + "s	");

			System.out.print(citation_size2 + "	");

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
			
			System.out.print(Tuple_reasoning2_test.covering_set_num * 1.0/row + "	");
			
			System.out.print("pre_processing::" + Tuple_reasoning2_test.pre_processing_time + "	");
			
			System.out.print("query::" + Tuple_reasoning2_test.query_time + "	");
			
			System.out.print("reasoning::" + Tuple_reasoning2_test.reasoning_time + "	");
			
			System.out.print("population::" + Tuple_reasoning2_test.population_time + "	");
			
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
