package edu.upenn.cis.citation.final_test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.reasoning1.Schema_level_approach;
import edu.upenn.cis.citation.reasoning1.Semi_schema_level_approach;
import edu.upenn.cis.citation.reasoning1.Tuple_level_approach;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;
import edu.upenn.cis.citation.user_query.query_storage;

public class final_stress_test_view_num_full2 {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> relations = new Vector<String>();
	
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
		}
		
		System.out.println("original_relation_size::" + original_size);
		
//		System.out.println("annotated_relation_size::" + annotated_relation_size);
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
	
	static HashMap<String, Vector<String>> query_head_variables_mapping(Query query)
	{
		HashMap<String, Vector<String>> head_relation_mapping = new HashMap<String, Vector<String>>();
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg  = (Argument) query.head.args.get(i);
			
			String relation_name = arg.relation_name;
			
			String arg_name = arg.name.substring(relation_name.length() + 1, arg.name.length());
			
			if(head_relation_mapping.containsKey(relation_name))
			{
				head_relation_mapping.get(relation_name).add(arg_name);
			}
			else
			{
				Vector<String> args = new Vector<String>();
				
				args.add(arg_name);
				
				head_relation_mapping.put(relation_name, args);
			}
			
		}
		
		return head_relation_mapping;
	}
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{		
	    
	    int k = Integer.valueOf(args[0]);
	    
	    boolean new_query = Boolean.valueOf(args[1]);
		
		boolean new_rounds = Boolean.valueOf(args[2]);
		
		boolean tuple_level = Boolean.valueOf(args[3]);
		
		boolean schema_level = Boolean.valueOf(args[4]);
		
		boolean new_start = Boolean.valueOf(args[5]);
		
		boolean agg_intersection = Boolean.valueOf(args[6]);
		
		int query_instance_size = Integer.valueOf(args[7]);
		
		String db_name1 = args[8];
		
		String db_name2 = args[9];
		
		String usr_name = args[10];
		
		String passwd = args[11];
		
		String path = args[12];
		
		populate_db.synthetic_example_path = path;
		
		Connection c1 = null;
        
        Connection c2 = null;
          PreparedStatement pst = null;
        Class.forName("org.postgresql.Driver");
        c1 = DriverManager
            .getConnection(populate_db.db_url_prefix + db_name1, usr_name , passwd);
        
        c2 = DriverManager
            .getConnection(populate_db.db_url_prefix + db_name2, usr_name , passwd);
		
		query_generator.query_result_size = query_instance_size;
		
		Query query = stress_test.init_query(k, new_query, path, c1, c2, pst);
		
			relations = get_unique_relation_names(query);
			
//			HashMap<String, Vector<String>> q_head_var_mapping = query_head_variables_mapping(query);
			
			
			Vector<Query> views = null;
	        
	        Vector<Query> new_views = null;
	        
	        if(new_rounds)
	        {
	            
	            views = view_generator.gen_default_views(relations, c2, pst);
	            
	        }
	        else
	        {
	            views = Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c2, pst);
	            
	            if(new_start)
	              new_views = view_generator.gen_one_additional_view(views, relations, query.body.size(), query, c1, c2, pst);
	        }
			
			stress_test.init_views(views, new_views, new_rounds, new_start, query, relations, c1, c2, pst);
			
//			Vector<Query> all_citation_queries = Query_operation.get_all_citation_queries(c2, pst);
			
			Vector<Query> citation_queries = Load_views_and_citation_queries.get_views(populate_db.synthetic_citation_query_files, c2, pst);
			
			HashMap<String, HashMap<String, String>> view_citation_query_mappings = Load_views_and_citation_queries.get_view_citation_query_mappings(populate_db.synthetic_citation_query_view_mapping_files);
			
			c1.close();
			
			c2.close();
			
			stress_test.stress_test(query, views, citation_queries, view_citation_query_mappings, tuple_level, schema_level, agg_intersection, true, db_name1, db_name2, usr_name, passwd);

			
			Vector<Query> user_query = new Vector<Query>();
			
			user_query.add(query);
			
//			output_queries(user_query, path + "user_queries");
//			
//			output_queries(views, path + "views");
//			
//			output_queries(all_citation_queries, path + "citation_query");
			
		
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
	
	
//	static void stress_test(Query query, Vector<Query> views, boolean tuple_level, boolean schema_level, boolean agg_intersection, boolean isclustering) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
//	{
//		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
//		
//		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();
//
//		
//		String f_name = new String();
//		
//		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
//
//		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
//		
//		Vector<Vector<Covering_set>> citation_view2 = new Vector<Vector<Covering_set>>();
//
//		
////		while(views.size() < view_max_size)
//		if(tuple_level)
//		{
//			
//			Connection c = null;
//		      PreparedStatement pst = null;
//			Class.forName("org.postgresql.Driver");
//		    c = DriverManager
//		        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
//			
//		    
//		    Tuple_level_approach.prepare_info = false;
//		
//		    Tuple_level_approach.agg_intersection = agg_intersection;
//		    
//		    Tuple_level_approach.test_case = true;
//		    
//		    Tuple_level_approach.isclustering = isclustering;
//		    
//		    double end_time = 0;
//
//			double middle_time = 0;
//			
//			double start_time = 0;
//			
//			double time1 = 0;
//			
//			HashSet<String> agg_citations = null;
//						
//			start_time = System.nanoTime();
//						
//			Tuple_level_approach.tuple_reasoning(query, c, pst);
//						
//			time1 = System.nanoTime();
//			
////			ArrayList<HashSet<citation_view>> views_per_group = Tuple_level_approach.cal_covering_sets_schema_level(query, c, pst);
//						
//			middle_time = System.nanoTime();
//			
//			Tuple_level_approach.prepare_citation_information(views, c, pst);
//			
//			agg_citations = Tuple_level_approach.gen_citation_schema_level(c, pst);
//			
////			 Tuple_reasoning1_full_test_opt.tuple_gen_agg_citations(query, c, pst);
//													
//			end_time = System.nanoTime();
//			
//			
////			double time = (end_time - start_time)*1.0;
////			
////			double agg_time = (middle_time - time1) * 1.0/1000000000;
////			
//			double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
////			
////			double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
////			
////			time = time /(times * 1000000000);
////						
////			System.out.print(Tuple_reasoning1_full_test_opt.group_num + "	");
////			
////			System.out.print(Tuple_reasoning1_full_test_opt.tuple_num + "	");
////			
////			System.out.print(time + "s	");
////			
////			System.out.print("total_exe_time::" + time + "	");
////			
//			if(isclustering)
//			{
//			  System.out.println("reasoning_time 1::" + reasoning_time + " ");
//	            
//	          System.out.println("covering_set_time 1::" + Tuple_level_approach.covering_set_time);
//			}
//			else
//			{
//			  System.out.println("reasoning_time 2::" + reasoning_time + " ");
//	            
//	          System.out.println("covering_set_time 2::" + Tuple_level_approach.covering_set_time);
//			}
//			
////			
////			System.out.print("aggregation_time::" + agg_time + "	");
////			
////			System.out.print("citation_gen_time::" + citation_gen_time + "	");
////			
////			System.out.print("covering_sets::" + Aggregation5.curr_res + "	");
////			
////			System.out.print("covering_set_size::" + Aggregation5.curr_res.size() + "	");
////			
////			int distinct_view_size = Aggregation5.cal_distinct_views();
////			
////			System.out.print("distinct_view_size::" + distinct_view_size + "	");
//			
////			Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
////			
////			double citation_size1 = 0;
////			
////			int row = 0;
////			
////			start_time = System.nanoTime();
////			
////			for(Iterator iter = h_l.iterator(); iter.hasNext();)
////			{
////				Head_strs h_value = (Head_strs) iter.next();
////				
////				HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
////				
////				citation_size1 += citations.size();
////				
//////				System.out.println(citations);
////				
////				row ++;
////				
////				if(row >= 10)
////					break;
////				
////			}
////			
////			end_time = System.nanoTime();
////			
////			if(row !=0)
////			citation_size1 = citation_size1 / row;
////			
////			time = (end_time - start_time)/(row * 1.0 * 1000000000);
////			
////			System.out.print(time + "s	");
////			
////			System.out.print(citation_size1 + "	");
////			
////			System.out.print(row + "	");
//			
////			Set<Head_strs> head = citation_view_map1.keySet();
////			
////			int row_num = 0;
////			
////			double origin_citation_size = 0.0;
////			
////			for(Iterator iter = head.iterator(); iter.hasNext();)
////			{
////				Head_strs head_val = (Head_strs) iter.next();
////				
////				Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
////				
////				row_num++;
////				
////				for(int p = 0; p<c_view.size(); p++)
////				{
////					origin_citation_size += c_view.get(p).size();
////				}
////				
////			}
////			
////			
////			
////			if(row_num !=0)
////				origin_citation_size = origin_citation_size / row_num;
////			
//			
//			
////			System.out.print(Tuple_reasoning1_full_test_opt.covering_set_num * 1.0/Tuple_reasoning1_full_test_opt.tuple_num + "	");
////			
////			System.out.print("pre_processing::" + Tuple_reasoning1_full_test_opt.pre_processing_time + "	");
////			
////			System.out.print("query::" + Tuple_reasoning1_full_test_opt.query_time + "	");
////			
////			System.out.print("reasoning::" + Tuple_reasoning1_full_test_opt.reasoning_time + "	");
////			
////			System.out.print("population::" + Tuple_reasoning1_full_test_opt.population_time + "	");
//			
//			
//			
////			time = (end_time - start_time) * 1.0/1000000000;
////			
////			System.out.print("Aggregation_time::" + time + "	");
////			
////			System.out.print("Aggregation_size::" + agg_citations.size() + "	");
//			
////			start_time = System.nanoTime();
////			
////			Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
////			
////			agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
////						
////			end_time = System.nanoTime();
////			
////			time = (end_time - start_time) * 1.0/1000000000;
////			
////			System.out.print("total_reasoning_time::" + time + "	");
//			
////			Vector<String> agg_results = new Vector<String>();
////			
////			agg_results.add(Tuple_reasoning1_full_min_test.covering_sets_query.toString());
////			
//			if(isclustering)
//			  Query_operation.write2file(path + "covering_sets", Aggregation5.curr_res);
//			else
//			  Query_operation.write2file(path + "covering_sets2", Aggregation5.curr_res);
//
//	        Query_operation.write2file(path + "citations", agg_citations);
//
////			for(int k = 0; k<Aggregation5.curr_res.size(); k++)
////			{
////				agg_results.add(Aggregation5.curr_res.get(k).toString());
////			}
//			
//			String aggregate_result = Tuple_level_approach.covering_set_schema_level.toString();
//			
//			System.out.println(aggregate_result);
//						
//			
////			HashSet<String> agg_citations2 = Tuple_reasoning1_full_test.tuple_gen_agg_citations2(query);
////			
////			System.out.println("citation1::" + agg_citations);
////			
////			System.out.println("citation2::" + agg_citations2);
////			
////			if(!agg_citations.equals(agg_citations2))
////			{
////				
////				for(int p = 0; p < Aggregation3.author_lists.size(); p ++)
////				{
////					HashMap<String, HashSet<String>> citation1 = Aggregation3.author_lists.get(p);
////					
////					HashMap<String, HashSet<String>> citation2 = Aggregation5.full_citations.get(p);
////					
////					HashSet<String> author1 = citation1.get("author");
////					
////					HashSet<String> author2 = citation2.get("author");
////					
////					if(!author1.equals(author2))
////					{
////						
////						HashSet<String> curr_authors = new HashSet<String>();
////						
////						curr_authors.addAll(author1);
////						
////						curr_authors.removeAll(author2);
////						
////						System.out.println(Aggregation3.curr_res.get(p));
////						
////						System.out.println(Aggregation5.curr_res.get(p));
////						
////						System.out.println(p);
////						
////						System.out.println(author1.size());
////						
////						System.out.println(author2.size());
////						
////						int y = 0;
////						
////						y++;
////					}
////				}
////				
////				Assert.assertEquals(true, false);
////			}
//						
////			diff_agg_time(tuple_level, query);
//			
////			System.out.println();
//			
////			System.out.println(agg_citations.toString());
//			
//			c.close();
//			
////			System.out.println(agg_citations);
//			
//			
//		}
//		else
//		{
//			
//			if(!schema_level)
//			{
//			  
//			  System.out.println("semi-schema_level");
//			  
//				Connection c = null;
//			      PreparedStatement pst = null;
//				Class.forName("org.postgresql.Driver");
//			    c = DriverManager
//			        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
//				
//			    Semi_schema_level_approach.prepare_info = false;
//			    
//			    Semi_schema_level_approach.agg_intersection = agg_intersection;
//			    
//			    Semi_schema_level_approach.isclustering = isclustering;
//			    
//			    Semi_schema_level_approach.test_case = true;
//			    
//			    double end_time = 0;
//
//				double middle_time = 0;
//				
//				double start_time = 0;
//				
//				double time1 = 0.0;
//				
//				HashSet<String> agg_citations = null;
//							
//				start_time = System.nanoTime();
//				
//				Semi_schema_level_approach.tuple_reasoning(query, c, pst);
//				
//				time1 = System.nanoTime();
//				
////				ArrayList<HashSet<citation_view>> views_per_group = Semi_schema_level_approach.cal_covering_sets_schema_level(query, c, pst);
//				
//				middle_time = System.nanoTime();
//				
//				Semi_schema_level_approach.prepare_citation_information(views, c, pst);
//				
//				agg_citations = Semi_schema_level_approach.gen_citation_schema_level(c, pst);
//				
////				agg_citations = Tuple_reasoning2_full_test2.tuple_gen_agg_citations(query, c, pst);
//														
//				end_time = System.nanoTime();
//				
//				double time = (end_time - start_time)*1.0;
//				
//				double agg_time = (middle_time - time1) * 1.0/1000000000;
//				
//	            double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
////	          
////	          double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
////	          
////	          time = time /(times * 1000000000);
////	                      
////	          System.out.print(Tuple_reasoning1_full_test_opt.group_num + "   ");
////	          
////	          System.out.print(Tuple_reasoning1_full_test_opt.tuple_num + "   ");
////	          
////	          System.out.print(time + "s  ");
////	          
////	          System.out.print("total_exe_time::" + time + "  ");
////	          
//	            if(isclustering)
//	            {
//	              System.out.println("reasoning_time 1::" + reasoning_time + " ");
//	                
//	              System.out.println("covering_set_time 1::" + Semi_schema_level_approach.covering_set_time);
//	            }
//	            else
//	            {
//	              System.out.println("reasoning_time 2::" + reasoning_time + " ");
//	                
//	              System.out.println("covering_set_time 2::" + Semi_schema_level_approach.covering_set_time);
//	            }
//	            
////	          
////	          System.out.print("aggregation_time::" + agg_time + "    ");
////	          
////	          System.out.print("citation_gen_time::" + citation_gen_time + "  ");
////	          
////	          System.out.print("covering_sets::" + Aggregation5.curr_res + "  ");
////	          
////	          System.out.print("covering_set_size::" + Aggregation5.curr_res.size() + "   ");
////	          
////	          int distinct_view_size = Aggregation5.cal_distinct_views();
////	          
////	          System.out.print("distinct_view_size::" + distinct_view_size + "    ");
//	            
////	          Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
////	          
////	          double citation_size1 = 0;
////	          
////	          int row = 0;
////	          
////	          start_time = System.nanoTime();
////	          
////	          for(Iterator iter = h_l.iterator(); iter.hasNext();)
////	          {
////	              Head_strs h_value = (Head_strs) iter.next();
////	              
////	              HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
////	              
////	              citation_size1 += citations.size();
////	              
//////	                System.out.println(citations);
////	              
////	              row ++;
////	              
////	              if(row >= 10)
////	                  break;
////	              
////	          }
////	          
////	          end_time = System.nanoTime();
////	          
////	          if(row !=0)
////	          citation_size1 = citation_size1 / row;
////	          
////	          time = (end_time - start_time)/(row * 1.0 * 1000000000);
////	          
////	          System.out.print(time + "s  ");
////	          
////	          System.out.print(citation_size1 + " ");
////	          
////	          System.out.print(row + "    ");
//	            
////	          Set<Head_strs> head = citation_view_map1.keySet();
////	          
////	          int row_num = 0;
////	          
////	          double origin_citation_size = 0.0;
////	          
////	          for(Iterator iter = head.iterator(); iter.hasNext();)
////	          {
////	              Head_strs head_val = (Head_strs) iter.next();
////	              
////	              Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
////	              
////	              row_num++;
////	              
////	              for(int p = 0; p<c_view.size(); p++)
////	              {
////	                  origin_citation_size += c_view.get(p).size();
////	              }
////	              
////	          }
////	          
////	          
////	          
////	          if(row_num !=0)
////	              origin_citation_size = origin_citation_size / row_num;
////	          
//	            
//	            
////	          System.out.print(Tuple_reasoning1_full_test_opt.covering_set_num * 1.0/Tuple_reasoning1_full_test_opt.tuple_num + " ");
////	          
////	          System.out.print("pre_processing::" + Tuple_reasoning1_full_test_opt.pre_processing_time + "    ");
////	          
////	          System.out.print("query::" + Tuple_reasoning1_full_test_opt.query_time + "  ");
////	          
////	          System.out.print("reasoning::" + Tuple_reasoning1_full_test_opt.reasoning_time + "  ");
////	          
////	          System.out.print("population::" + Tuple_reasoning1_full_test_opt.population_time + "    ");
//	            
//	            
//	            
////	          time = (end_time - start_time) * 1.0/1000000000;
////	          
////	          System.out.print("Aggregation_time::" + time + "    ");
////	          
////	          System.out.print("Aggregation_size::" + agg_citations.size() + "    ");
//	            
////	          start_time = System.nanoTime();
////	          
////	          Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
////	          
////	          agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
////	                      
////	          end_time = System.nanoTime();
////	          
////	          time = (end_time - start_time) * 1.0/1000000000;
////	          
////	          System.out.print("total_reasoning_time::" + time + "    ");
//	            
////	          Vector<String> agg_results = new Vector<String>();
////	          
////	          agg_results.add(Tuple_reasoning1_full_min_test.covering_sets_query.toString());
////	          
//	            if(isclustering)
//	              Query_operation.write2file(path + "covering_sets", Aggregation5.curr_res);
//	            else
//	              Query_operation.write2file(path + "covering_sets2", Aggregation5.curr_res);
//
//	            Query_operation.write2file(path + "citations", agg_citations);
//
////	          for(int k = 0; k<Aggregation5.curr_res.size(); k++)
////	          {
////	              agg_results.add(Aggregation5.curr_res.get(k).toString());
////	          }
//	            
//	            String aggregate_result = Semi_schema_level_approach.covering_set_schema_level.toString();
//	            
//	            System.out.println(aggregate_result);
//			}
//			else
//			{
//				Connection c = null;
//			      PreparedStatement pst = null;
//				Class.forName("org.postgresql.Driver");
//			    c = DriverManager
//			        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
//				
//			    Schema_level_approach.prepare_info = false;
//			    			    
//			    double end_time = 0;
//
//				double middle_time = 0;
//				
//				double start_time = 0;
//				
//				double inter_time = 0;
//				
//				HashSet<String> agg_citations = null;
//							
//				start_time = System.nanoTime();
//				
//				Schema_level_approach.tuple_reasoning(query, c, pst);
//				
//				inter_time = System.nanoTime();
//								
//				Schema_level_approach.execute_query(query, c, pst);
//				
//				middle_time = System.nanoTime();
//				
//				Schema_level_approach.prepare_citation_information(views, c, pst);
//				
//				agg_citations = Schema_level_approach.tuple_gen_agg_citations(query, c, pst);
//														
//				end_time = System.nanoTime();
//				
//				double time = (end_time - start_time)*1.0;
//				
//				double agg_time = (end_time - middle_time) * 1.0/1000000000;
//				
//				double reasoning_time = (inter_time - start_time) * 1.0/1000000000;
//				
//				time = time /(times * 1000000000);
//				
//				double query_time = (middle_time - inter_time) * 1.0/1000000000;
//							
//				System.out.print(Schema_level_approach.tuple_num + "	");
//				
//				System.out.print(time + "s	");
//				
//				System.out.print("total_exe_time::" + time + "	");
//				
//				System.out.print("total_reasoning_time::" + reasoning_time + "	");
//				
//				System.out.print("aggregation_time::" + agg_time + "	");
//				
//				System.out.print("covering_sets::" + Schema_level_approach.covering_set_query + "	");
//				
//				System.out.print("covering_set_size::" + Schema_level_approach.covering_set_query.size() + "	");
//				
//				int distinct_view_size = Aggregation5.cal_distinct_views();
//				
//				System.out.print("distinct_view_size::" + distinct_view_size + "	");
//				
////				Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
////				
////				double citation_size1 = 0;
////				
////				int row = 0;
////				
////				start_time = System.nanoTime();
////				
////				for(Iterator iter = h_l.iterator(); iter.hasNext();)
////				{
////					Head_strs h_value = (Head_strs) iter.next();
////					
////					HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
////					
////					citation_size1 += citations.size();
////					
//////					System.out.println(citations);
////					
////					row ++;
////					
////					if(row >= 10)
////						break;
////					
////				}
////				
////				end_time = System.nanoTime();
////				
////				if(row !=0)
////				citation_size1 = citation_size1 / row;
////				
////				time = (end_time - start_time)/(row * 1.0 * 1000000000);
////				
////				System.out.print(time + "s	");
////				
////				System.out.print(citation_size1 + "	");
////				
////				System.out.print(row + "	");
//				
////				Set<Head_strs> head = citation_view_map1.keySet();
////				
////				int row_num = 0;
////				
////				double origin_citation_size = 0.0;
////				
////				for(Iterator iter = head.iterator(); iter.hasNext();)
////				{
////					Head_strs head_val = (Head_strs) iter.next();
////					
////					Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
////					
////					row_num++;
////					
////					for(int p = 0; p<c_view.size(); p++)
////					{
////						origin_citation_size += c_view.get(p).size();
////					}
////					
////				}
////				
////				
////				
////				if(row_num !=0)
////					origin_citation_size = origin_citation_size / row_num;
////				
//				
//								
//				System.out.print("pre_processing::" + Schema_level_approach.pre_processing_time + "	");
//				
//				System.out.print("query::" + query_time + "	");
//												
//				System.out.println();
//				
//
//				c.close();
//				
////				Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
//				
////				Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
//				
//				
////							
////				reset();
////				
////				Vector<Query> queries = query_generator.gen_queries(j, size_range);
////				
////				Query query = query_generator.gen_query(j, c, pst);
//				
////				System.out.println(queries.get(0));
//
//				
////				Vector<String> relation_names = get_unique_relation_names(queries.get(0));
//				
////				view_generator.generate_store_views(relation_names, num_views);
//				
////				query_storage.store_query(queries.get(0), new Vector<Integer>());
//			}
//			
//			
//			
//			
//			
//		}
//	}
//	
	
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
		
		query = "delete from user_query2head_var";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
//		query = "delete from user_query_head_var_table";
//        
//        pst = c.prepareStatement(query);
//        
//        pst.execute();
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		
	}
	
	static void reset() throws IOException
	{
	    Files.deleteIfExists(Paths.get(populate_db.synthetic_citation_query_files));
	}

}
