package edu.upenn.cis.citation.stress_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
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

public class stress_test2 {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 40;
	
	static int num_views = 5;
	
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
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
		
		
		
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
		
		Vector<Vector<citation_view_vector>> citation_view1 = new Vector<Vector<citation_view_vector>>();

		Vector<Vector<citation_view_vector>> citation_view2 = new Vector<Vector<citation_view_vector>>();
		
		reset();
		
//		Vector<Query> queries = query_generator.gen_queries(j, size_range);
		
		Query query = query_generator.gen_query(4, c, pst);
//		
//		System.out.println(queries.get(0));

		
		Vector<String> relation_names = get_unique_relation_names(query);
		
		Vector<Query> views = view_generator.generate_store_views(relation_names, num_views, query.body.size(), query);
		
//		query_storage.store_query(queries.get(0), new Vector<Integer>());
		
		System.out.println("start");
		
		for(int j = 4; j<size_upper_bound; j++)
		{
			
//			Vector<Query> queries = new Vector<Query>();
//			
//			Query query = query_storage.get_query_by_id(1);
////			
//			queries.add(query);
			

			
//			for(int i = 0; i<queries.size(); i++)
//			{
//				
				Query q = query;

				
//				if(i == 0)
//				{
//					System.out.println(q);
//					
//					System.out.println(Query_converter.datalog2sql(q));
//								
//					System.out.println(q.body.size());
//				}
				
				System.out.print(q.conditions + "	");
				
				double end_time = 0;

				
				double start_time = System.nanoTime();
				
				
				for(int k = 0; k<times; k++)
				{
					
					citation_view_map1.clear();
					
					citation_strs.clear();
					
					Tuple_reasoning1_test.tuple_reasoning(q, citation_strs,  citation_view_map1, c, pst);
					
				}
				
				
				
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
				
				Set<Head_strs> head = citation_view_map1.keySet();
				
				int row_num = 0;
				
				double origin_citation_size = 0.0;
				
				for(Iterator iter = head.iterator(); iter.hasNext();)
				{
					Head_strs head_val = (Head_strs) iter.next();
					
					Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
					
					row_num++;
					
					for(int p = 0; p<c_view.size(); p++)
					{
						origin_citation_size += c_view.get(p).size();
					}
					
				}
				
				
				
				if(row_num !=0)
					origin_citation_size = origin_citation_size / row_num;
				
				System.out.print(row_num + "	");
				
				System.out.print(origin_citation_size + "	");
				
				
				
				
				
				
				
				
				start_time = System.nanoTime();
				
				for(int k = 0; k<times; k++)
				{
					citation_view_map2.clear();
					
					citation_strs2.clear();
					
					Tuple_reasoning2_test.tuple_reasoning(q, citation_strs2, citation_view_map2, c, pst);
				}
				
				end_time = System.nanoTime();
				
				time = (end_time - start_time);
				
				
				time = time /(times * 1.0 *1000000000);
				
				System.out.print(time + "s	");
				
				
				h_l = citation_strs2.keySet();
				
				double citation_size2 = 0.0;
				
				row = 0;
				
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

				
				head = citation_view_map2.keySet();
				
				row_num = 0;
				
				origin_citation_size = 0.0;
				
				for(Iterator iter = head.iterator(); iter.hasNext();)
				{
					Head_strs head_val = (Head_strs) iter.next();
					
					Vector<Vector<citation_view_vector>> c_view = citation_view_map2.get(head_val);
					
					row_num++;
					
					for(int p = 0; p<c_view.size(); p++)
					{
						origin_citation_size += c_view.get(p).size();
					}
					
				}
				
				
				
				if(row_num !=0)
					origin_citation_size = origin_citation_size / row_num;
				
				System.out.print(row_num + "	");
				
				System.out.print(origin_citation_size + "	");
				
				
				Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
				
//				Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
				
				System.out.println();
				
				view_generator.gen_one_additional_view(views, relation_names, query.body.size(), query);

//			}
		}
		
		c.close();
		
		
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
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}

}
