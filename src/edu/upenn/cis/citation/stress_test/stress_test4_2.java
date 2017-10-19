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
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test_stable;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_opt;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.user_query.query_storage;
import junit.framework.Assert;

import java.lang.instrument.Instrumentation;
//import net.sourceforge.sizeof.*;


public class stress_test4_2 {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 5;
	
	static int view_max_size = 40;

	static int query_num = 5;

	
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
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		
//		reset();
		
//		long a = 6;
//		
//		
//		
//		 SizeOf.skipStaticField(true); //java.sizeOf will not compute static fields
//		 SizeOf.skipFinalField(true); //java.sizeOf will not compute final fields
//		 SizeOf.skipFlyweightObject(true); //java.sizeOf will not compute well-known flyweight objects
		 //this will print the object size in bytes
		
		query_generator.query_result_size = 10000;
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
					
//		Query query = query_storage.get_query_by_id(1);
		
		int k =  Integer.valueOf(args[0]);
		
		int view_size = Integer.valueOf(args[1]);
		
		boolean new_query = Boolean.valueOf(args[2]);
		
		boolean new_rounds = Boolean.valueOf(args[3]);
		
		boolean tuple_level = Boolean.valueOf(args[4]);
		
		boolean new_start = Boolean.valueOf(args[5]);
		
		if(new_query)
			reset();
		
//		for(int k = 3; k<query_num; k++)
		{
//			reset();
//			
//			Random r = new Random();
//			
//			int size = r.nextInt(query_num);
//			
//			while(size <= 0)
//			{
//				size = r.nextInt(query_num);
//			}
			
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
			
			
			Vector<Query> views = null;
			
			Vector<String> relation_names = get_unique_relation_names(query);
			
			if(new_rounds)
			{
				views = view_generator.generate_store_views_without_predicates(relation_names, view_size, query.body.size(), c, pst);
				
//				view_generator.gen_default_views(relation_names);
				
				for(Iterator iter = views.iterator(); iter.hasNext();)
				{
					Query view = (Query) iter.next();
					
					System.out.println(view);
				}
			}
			else
			{
				views = view_operation.get_all_views();
				
//				for(Iterator iter = views.iterator(); iter.hasNext();)
//				{
//					Query view = (Query) iter.next();
//					
//					System.out.println(view);
//				}
				
				if(new_start)
				{
					System.out.println();
					
					view_generator.initial();
					
					view_generator.gen_one_additional_predicates(views, relation_names, query.body.size(), query, c, pst);
					
					for(Iterator iter = views.iterator(); iter.hasNext();)
					{
						Query view = (Query) iter.next();
						
						System.out.println(view);
					}
				}
			}
			
			
//			for(Iterator iter = views.iterator(); iter.hasNext();)
//			{
//				Query view = (Query) iter.next();
//				
//				view_generator.gen_one_local_predicate(view, c, pst, query);
////				view_generator.gen_one_additional_predicates(views, relation_names, query.body.size(), query);
//				
//				view_operation.delete_view_by_name(view.name);
//				
//				view_operation.add(view, view.name);
//				
//				System.out.println(view);
//
//			}
//			
//			System.out.println(query);
			
			stress_test(query, c, pst, views, relation_names, tuple_level);

		}
		
		
		
		
		
		
		
		c.close();
		
		
	}
	
	static void stress_test(Query query, Connection c, PreparedStatement pst, Vector<Query> views, Vector<String> relation_names, boolean tuple_level) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
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
				
				Tuple_reasoning1_test.tuple_reasoning(query, c, pst);
				
				Tuple_reasoning2_test.tuple_reasoning(query, c, pst);
				
				System.gc();
				
			}
			
			Set<Head_strs> heads = Tuple_reasoning1_test.head_strs_rows_mapping.keySet();
			
			int row = 0;
			
			int size1 = 0;
			
			int size2 = 0;
			
			for(Iterator iter = heads.iterator(); iter.hasNext();)
			{
				Head_strs head = (Head_strs) iter.next();
				
				System.out.println(head);
				
				HashSet<String> curr_citation2 = Tuple_reasoning2_test.gen_citation(head, c, pst);
				
				HashSet<String> curr_citation1 = Tuple_reasoning1_test.gen_citation(head, c, pst);
				
				size1 += curr_citation1.size();
				
				size2 += curr_citation2.size();
				
//				System.out.println();
				
//				if(curr_citation1.toString().equals("[]") && curr_citation2.toString().equals("[{\"db_name\":\"IUPHAR/BPS Guide to PHARMACOLOGY\",\"author\":[]}]"))
//					continue;
				
				if(!curr_citation1.containsAll(curr_citation2) || !curr_citation2.containsAll(curr_citation1))
				{
					System.out.println(curr_citation1);
					
					System.out.println(curr_citation2);
					
					curr_citation2 = Tuple_reasoning2_test.gen_citation(head, c, pst);
					
					curr_citation1 = Tuple_reasoning1_test.gen_citation(head, c, pst);
					
					Assert.assertEquals(true, false);
				}
				
				row ++;
			}
			
			System.out.println(size1);
			
			System.out.println(size2);
			
//			System.out.println(SizeOf.humanReadable(SizeOf.deepSizeOf(citation_strs))); 
			
			end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			Set<Head_strs> h_l = citation_strs.keySet();
			
			double citation_size1 = 0;
			
			row = 0;
			
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
		}
		else
		{
			
			double start_time = System.nanoTime();
			
			for(int k = 0; k<times; k++)
			{
			
				citation_view2 = Tuple_reasoning2_test.tuple_reasoning(query, c, pst);
				
				System.gc();
			}
			
			double end_time = System.nanoTime();
			
			double time = (end_time - start_time);
			
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			
			Set<Head_strs> h_l = citation_strs2.keySet();
			
			double citation_size2 = 0.0;
			
			int row = 0;
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = citation_strs2.get(h_value);
				
				citation_size2 += citations.size();
				
				row ++;
				
			}
			
			if(row !=0)
				citation_size2 = citation_size2 / row;

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
	
	static void reset() throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
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
