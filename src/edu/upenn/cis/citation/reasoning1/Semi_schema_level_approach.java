package edu.upenn.cis.citation.reasoning1;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

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
import edu.upenn.cis.citation.aggregation.Aggregation6;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.covering_sets_calculation.Covering_sets_clustering_by_relation;
import edu.upenn.cis.citation.covering_sets_calculation.Covering_sets_no_clustering;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.gen_citation.gen_citation0;
import edu.upenn.cis.citation.output.output2excel;
import edu.upenn.cis.citation.schema_level_reasoning.Schema_reasoning_with_agg;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class Semi_schema_level_approach {
	
	
	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static Vector<Conditions> valid_conditions = new Vector<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, ArrayList<Tuple>> view_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
//	static HashMap<Conditions, Integer> condition_id_mapping = new HashMap<Conditions, Integer>();
	
	public static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
	static HashMap<Conditions, ArrayList<Tuple>> conditions_map = new HashMap<Conditions, ArrayList<Tuple>>();
		
	static String file_name = "tuple_level1.xls";
	
	static HashMap<String, Integer> q_subgoal_id = new HashMap<String, Integer>();
		
	public static HashMap<String, Integer> max_author_num = new HashMap<String, Integer>();
	
	static HashSet<String> str_set = new HashSet<String>();
	
	public static HashMap<String, HashMap<String, String>> view_query_mapping = new HashMap<String, HashMap<String, String>>();
	
	static HashMap<String, HashSet<Integer>> relation_arg_mapping = new HashMap<String, HashSet<Integer>>();
	
	public static ArrayList<Lambda_term[]> query_lambda_str = new ArrayList<Lambda_term[]>();
	
	public static HashMap<String, ArrayList<ArrayList<String>>> author_mapping = new HashMap<String, ArrayList<ArrayList<String>>>(); 
	
	public static HashMap<String, HashSet<Covering_set> > c_view_map = new HashMap<String, HashSet<Covering_set>>();
	
	public static HashMap<String, HashSet<Integer>> signiture_rid_mappings = new HashMap<String, HashSet<Integer>>();
	
	public static HashMap<String, HashSet<Tuple>> signature_view_mappings_mappings = new HashMap<String, HashSet<Tuple>>();
	
	public static HashSet<Covering_set> covering_set_schema_level = new HashSet<Covering_set>();
	
    public static HashSet<Tuple> valid_view_mappings_schema_level = null;
	
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
	
	public static IntList query_ids = new IntList();
	
	public static StringList view_list = new StringList();
	
	static int Resultset_prefix_col_num = 0;
	
	public static HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping = new HashMap<Head_strs, ArrayList<Integer>>();

	
	static ArrayList<Tuple>[] head_variable_view_mapping = null;
	
	static HashMap<String, ArrayList<Integer>> head_variable_query_mapping = new HashMap<String, ArrayList<Integer>>();
	
	public static int tuple_num = 0;
	
	public static int group_num = 0;
	
	public static HashMap<String, Query> citation_queries = new HashMap<String, Query>();

	public static boolean prepare_info = true;
	
	public static boolean agg_intersection = true;
	
	public static boolean test_case = false;
	
	public static int unique_tuple_num = 0;
	
	public static HashSet<Tuple> viewTuples = new HashSet<Tuple>();
	
	public static boolean isclustering = true;
	
	public static double covering_set_time = 0.0; 
//	static HashMap<String, ArrayList<String>> 
	
//	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>(); 
	
	public static HashMap<Tuple, Integer> count_view_mapping_valid_row_numbers()
	{
	  Set<String> signatures = signature_view_mappings_mappings.keySet();
	  
	  HashMap<Tuple, Integer> view_mapping_count_mappings = new HashMap<Tuple, Integer>();
	  
	  for(String signature: signatures)
	  {
	    HashSet<Tuple> view_mappings = signature_view_mappings_mappings.get(signature);
	    
	    HashSet<Integer> rids = signiture_rid_mappings.get(signature);
	    
	    
	    for(Tuple view_mapping: view_mappings)
	    {
	      if(view_mapping_count_mappings.get(view_mapping) == null)
	      {
	        view_mapping_count_mappings.put(view_mapping, rids.size());
	      }
	      else
	      {
	        int old_count = view_mapping_count_mappings.get(view_mapping);
	        
	        old_count += rids.size();
	        
	        view_mapping_count_mappings.put(view_mapping, old_count);
	        
	      }
	    }
	    
	    
	  }
	  
	  return view_mapping_count_mappings;
	  
	}
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		Vector<Query> queries = Load_views_and_citation_queries.get_views("data/test/query", c, pst);
		
		prepare_info = false;
		
		test_case = false;
		
		System.out.println(queries.get(0));
		
//		tuple_reasoning(queries.get(0), c, pst);
		
		Set<String> keys = c_view_map.keySet();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
		  String key = (String) iter.next();
			
			HashSet<Covering_set> c_views = c_view_map.get(key);
			
			System.out.println(c_views);
		}
		
//		Query q = gen_query();
//		
//		
////		String query = "q(object_c_object_id): object_c(), gpcr_c(), interaction_c(), gpcr_c_object_id = object_c_object_id, interaction_c_object_id = gpcr_c_object_id";
////
////		query = get_full_query(query);
////		
////		long start_time = System.currentTimeMillis();
////		
////		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query);
////		
////		output_sql(citation_views);
////		
////		long end_time = System.currentTimeMillis();
////		
////		System.out.println(end_time - start_time);
////		String query = "q(family_c_family_id, family_c_name):family_c()";
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
//		Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning2_full_test.tuple_reasoning(q, c, pst);
//		
////		Vector<String> agg_citations = Tuple_reasoning2_test.tuple_gen_agg_citations(c_views);
//		
//		Vector<Integer> ids = new Vector<Integer>();
//		
////		Vector<String> subset_agg_citations = Tuple_reasoning2_test.tuple_gen_agg_citations(c_views, ids);
//
//		c.close();
	}
	
//	public static HashSet<String> tuple_gen_agg_citations(Query query, ArrayList<Head_strs> heads) throws ClassNotFoundException, SQLException, JSONException
//	{
//		int start_pos = query.head.args.size();
//	
//		
//		HashSet<String> citations = Aggregation3.do_agg_intersection(rs, c_view_map, start_pos, heads, head_strs_rows_mapping, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, false);
//				
//		return citations;
//	}
	
//	public static HashSet<String> tuple_gen_agg_citations(Query query, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, JSONException
//	{
//		
//		start = System.nanoTime();
//		
//		int start_pos = Resultset_prefix_col_num;
//		
//		HashSet<String> citations = null;
//		
//		if(agg_intersection)
//		{			
//			citations = Aggregation6.do_agg_intersection0(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, false);
//		}
//		else
//		{			
//			citations = Aggregation6.do_agg_union0(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, false);
//		}
//				
//		end = System.nanoTime();
//		
//		aggregation_time = (end - start) * 1.0/second2nano;
//		
//		return citations;
//	}
	
//	public static ArrayList<HashSet<citation_view>> cal_covering_sets_schema_level(Query query, Connection c, PreparedStatement pst) throws SQLException
//	{
//	  Aggregation6.clear();
//	  
////	  System.out.println(max_author_num);
//	  
//		if(agg_intersection)
//		{
//		  Aggregation6.cal_covering_set_schema_level_intersection(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//			
//			return new ArrayList<HashSet<citation_view>>();
//			
//		}
//		else
//		{
//			ArrayList<HashSet<citation_view>> views_per_group = Aggregation6.cal_covering_set_schema_level_union(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//			
//			return views_per_group;
//		}
//	}
	
//	 public static HashSet<String> gen_citation_subset_tuples(Vector<Head_strs> names, Query query, Connection c, PreparedStatement pst) throws JSONException, SQLException
//    {
//        if(agg_intersection)
//        {
//            HashSet<String> citations = Aggregation6.do_agg_intersection_subset(rs, tuple_id_list, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);//(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return citations;
//        }
//        else
//        {
//            HashSet<String> citations = Aggregation6.do_agg_union(views_per_group, rs, c_view_map, signiture_rid_mappings, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return citations;
//        }
//    }
	
	   public static HashSet<String> tuple_gen_citation_per_tuple(Vector<String> names, Connection c, PreparedStatement pst) throws JSONException, SQLException
	    {
	      Head_strs head = new Head_strs(names);
	      
	      ArrayList<Integer> id_lists = head_strs_rows_mapping.get(head);
	      
	      Set<String> keys = c_view_map.keySet();

	      HashSet<String> citations = new HashSet<String>();
	      
	      Iterator<String> iter = keys.iterator();
	      
	      int i = 0;
	      
	        
	        while(iter.hasNext())
	        {
	          String curr_tuple_key = iter.next();
	          
	          HashSet<Integer> rids = signiture_rid_mappings.get(curr_tuple_key);
	          
	          HashSet<Covering_set> c_views = c_view_map.get(curr_tuple_key);
	          
	          for(i = 0; i<id_lists.size(); i++)
	          {
	            int tuple_id = id_lists.get(i);
	            
	            if(rids.contains(tuple_id))
	              {
	                
	                rs.absolute(tuple_id);
	                
	                HashSet<String> curr_citations = gen_citations_per_covering_set(c_views, rs, Resultset_prefix_col_num, tuple_id, c, pst);
	                
	                citations.addAll(curr_citations);
	                
//	              i++;
//	             
//	              if(i >= id_lists.size())
//	                  break;
//	              
//	              tuple_id = id_lists.get(i);
	                
	              }
	            
	          }
	          
	          
	          
//	        if(i >= id_lists.size())
//	          break;
	        }
	        
	      return citations;
	    }
	
	   public static HashSet<String> gen_citations_per_covering_set(HashSet<Covering_set> insert_c_view, ResultSet rs, int start_pos, int tuple_id, Connection c, PreparedStatement pst) throws SQLException, JSONException
	    {
	        
//	      HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
	        
	        HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
	      
	        HashSet<String> citations = new HashSet<String>();
	      
	        Set<String> head_sets = lambda_term_id_mapping.keySet();
	        
	        for(Iterator iter = insert_c_view.iterator(); iter.hasNext();)
	        {
	          Covering_set covering_set = (Covering_set) iter.next();
	          
	          for(citation_view view_mapping: covering_set.c_vec)
	          {
	            citation_view view = view_mapping;
	            
	            if(view.has_lambda_term())
	            {
	                citation_view_parametered curr_c_view = (citation_view_parametered) view;
	                
	                Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
	                
	                Vector<String> heads = new Vector<String>();
	                
	                for(int k = 0; k<lambda_terms.size(); k++)
	                {
	                    int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
	                    
	                    heads.add(rs.getString(id + start_pos + 1));
	                    
	                    curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
	                }                
	            }
	          }
	          
	          HashSet<String> curr_citations = gen_citation0.get_citations3(covering_set, c, pst, view_query_mapping, author_mapping, max_author_num, citation_queries, view_author_mapping, populate_db.star_op);
	          
	          citations.addAll(curr_citations);
	          
	        }
	        

	        return citations;
	    }
	   
	public static HashSet<String> gen_citation_schema_level(Connection c, PreparedStatement pst) throws JSONException, SQLException
	{
	  
//		if(agg_intersection)
//		{
//			HashSet<String>	citations = Aggregation6.do_agg_intersection2(covering_set_schema_level, valid_view_mappings_schema_level, rs, c_view_map, Resultset_prefix_col_num, query_lambda_str, author_mapping, max_author_num, query_ids, c, pst, false);
//			
//			return citations;
//		}
//		else
		{
			HashSet<String> citations = Aggregation6.do_agg_union(covering_set_schema_level, valid_view_mappings_schema_level, signature_view_mappings_mappings, signiture_rid_mappings, rs, signiture_rid_mappings, Resultset_prefix_col_num, author_mapping, max_author_num, lambda_term_id_mapping, c, pst, view_query_mapping, citation_queries, false);
			
			return citations;
		}
	}
	
//	public static HashSet<String> cal_unique_covering_sets() throws SQLException
//	{
//		
////		HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
//		
//		HashSet<String> unique_covering_sets = new HashSet<String>();
//		
//		if(agg_intersection)
//		{
//			ArrayList<citation_view_vector> covering_sets = Aggregation6.curr_res;
//			
//			HashSet<citation_view_vector> all_covering_sets = new HashSet<citation_view_vector>();
//			
//			all_covering_sets.addAll(covering_sets);
//			
//			for(int i = 0; i < tuple_num; i++)
//			{
//				rs.absolute(i + 1);
//				
//				update_valid_citation_combination(all_covering_sets, rs, Resultset_prefix_col_num);
//				
//				for(Iterator it = all_covering_sets.iterator(); it.hasNext();)
//				{
//					citation_view_vector covering_set = (citation_view_vector) it.next();
//					
//					unique_covering_sets.add(covering_set.toString());
//				}
//				
//			}
//		}
//		else
//		{
//			Set<String> keys = c_view_map.keySet();
//			
//			
//			for(Iterator iter = keys.iterator(); iter.hasNext();)
//			{
//				String curr_key = (String) iter.next();
//				
//				HashSet<citation_view_vector> covering_sets = c_view_map.get(curr_key);
//				
//				for(int i = curr_key[0]; i < curr_key[1]; i++)
//				{
//					rs.absolute(i + 1);
//					
//					update_valid_citation_combination(covering_sets, rs, Resultset_prefix_col_num);
//					
//					for(Iterator it = covering_sets.iterator(); it.hasNext();)
//					{
//						citation_view_vector covering_set = (citation_view_vector) it.next();
//						
//						unique_covering_sets.add(covering_set.toString());
//					}
//				}				
//			}
//		}
//		
//		return unique_covering_sets;
//	}
	
//	public static HashSet<String> tuple_gen_agg_citations(Query query, HashMap<String, String> view_citation_mapping) throws ClassNotFoundException, SQLException, JSONException
//	{
//		
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
//		HashSet<String> citations = Aggregation3.do_agg_intersection(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, view_citation_mapping, c, pst, false);
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
	
//	public static Vector<Vector<citation_view_vector>> tuple_reasoning(Query query, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
//	{
////		query = get_full_query(query);
//		
//		
//				
//		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query, citation_strs, citation_view_map2, c, pst);
//		
//		
//		
//		return citation_views;
//	}
	
//	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views) throws ClassNotFoundException, SQLException, JSONException
//	{
//		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggregate(c_views);
//		
//		Vector<String> citation_aggs = new Vector<String>();
//		
//		for(int i = 0; i<agg_res.size(); i++)
//		{
//			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
//			
//			citation_aggs.add(str);
//
//		}
//		
//		return citation_aggs;
//	}
	
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
				
				Vector<String> args = get_args(subgoals[i]);
				
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
	
	public static void gen_citation_all(Vector<Vector<Covering_set>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Covering_set> c_vec = citation_views.get(i);
			
			for(int j = 0; j< c_vec.size(); j++)
			{
				gen_citation(c_vec.get(j));
			}
		}
	}
	
	public static void output_sql(Vector<Vector<Covering_set>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Covering_set> c_vec = citation_views.get(i);
			
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
	
    static void build_query_head_variable_mapping(Query query)
    {
        for(int i = 0; i<query.head.args.size(); i++)
        {
            Argument head_arg = (Argument)query.head.args.get(i);
            
            String head_arg_str = head_arg.toString();
            
            int partition_id = head_arg_str.indexOf(populate_db.separator);
            
            String relation_name = head_arg_str.substring(0, partition_id);
            
            ArrayList<Integer> head_variable_strs = head_variable_query_mapping.get(relation_name);
            
            if(head_variable_strs == null)
            {
                head_variable_strs = new ArrayList<Integer>();
            }
            
            head_variable_strs.add(i);
            
            head_variable_query_mapping.put(relation_name, head_variable_strs);
        }
    }
	
    static void build_head_variable_mapping(Query query, Tuple tuple)
    {
        for(int i = 0; i<tuple.getArgs().size(); i++)
        {
                        
            Argument target_attr_name = (Argument) tuple.getArgs().get(i);
            
            int target_arg_id = query.head.args.indexOf(target_attr_name);
            
            ArrayList<Tuple> tuples = head_variable_view_mapping[target_arg_id];
            
            String relation_name = target_attr_name.relation_name;
            
            if(relation_arg_mapping.get(relation_name) == null)
            {
                HashSet<Integer> args = new HashSet<Integer>();
                
                args.add(target_arg_id);
                
                relation_arg_mapping.put(relation_name, args);
            }
            else
            {
                relation_arg_mapping.get(relation_name).add(target_arg_id);
            }   
            
            if(tuples == null)
            {
                tuples = new ArrayList<Tuple>();
                
                tuples.add(tuple);
            }
            else
                tuples.add(tuple);
            
            head_variable_view_mapping[target_arg_id] = tuples;
        }
    }
	
//	public static HashSet pre_processing(Vector<Query> views, Query q, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, HashMap<String, Integer> with_sub_queries_id_mappings, Vector<String> full_mapping_condition_str, Connection c, PreparedStatement pst) throws SQLException
//	{
//		
//		build_query_head_variable_mapping(q);
//		
//		for(int k = 0; k<views.size(); k++)
//		{
//			view_list.add(views.get(k).name);
//			
//			view_mapping.put(views.get(k).name, views.get(k));
//		}
//
//		HashSet<Conditions> query_negated_conditions = q.get_all_negated_conditions();
//
//		
////	    q = q.minimize();
//
//	    // construct the canonical db
//	    Database canDb = CoreCover.constructCanonicalDB(q);
//	    UserLib.myprintln("canDb = " + canDb.toString());
//
//	    // compute view tuples
//	    viewTuples = CoreCover.computeViewTuples(canDb, views);
//	    
//	    viewTuples = check_distinguished_variables(viewTuples, q);
//	    	    	    
//	    unique_tuple_num = viewTuples.size();
//	    
//	    head_variable_view_mapping = new ArrayList[q.head.args.size()];
//	    
//	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
//	    {
//	    	Tuple tuple = (Tuple)iter.next();
//	    	
////	    	System.out.println(tuple.name + "|" + tuple.mapSubgoals_str);
//	    	
//	    	for(int i = 0; i<tuple.conditions.size(); i++)
//            {
//                Conditions condition = tuple.conditions.get(i);
//                
//                if(condition.get_mapping1 == true && condition.get_mapping2 == true)
//                {
//                  
//                  if(query_negated_conditions.contains(condition))
//                  {
//                    iter.remove();
//                    
//                    continue;
//                  }
//                  
//                  if(!q.conditions.contains(condition) && !valid_conditions.contains(condition))
//                  {
//                    valid_conditions.add(tuple.conditions.get(i));
//                    
//                    String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
//                    
//                    full_mapping_condition_str.add(curr_full_mapping_condition_str);
//                    
//                    ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
//                    
//                    if(curr_tuples == null)
//                    {
//                        curr_tuples =  new ArrayList<Tuple>();
//                        
//                        curr_tuples.add(tuple);
//                        
//                        conditions_map.put(condition, curr_tuples);
//                    }
//                    else
//                    {                   
//                        curr_tuples.add(tuple);
//                        
//                        conditions_map.put(condition, curr_tuples);
//                    }
//                    
//                  }
//                  else
//                  {
//                    if(valid_conditions.contains(condition))
//                    {
////                      valid_conditions.add(tuple.conditions.get(i));
//                      
////                      String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
//                      
////                      full_mapping_condition_str.add(curr_full_mapping_condition_str);
//                      
//                      ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
//                      
//                      if(curr_tuples == null)
//                      {
//                          curr_tuples =  new ArrayList<Tuple>();
//                          
//                          curr_tuples.add(tuple);
//                          
//                          conditions_map.put(condition, curr_tuples);
//                      }
//                      else
//                      {                   
//                          curr_tuples.add(tuple);
//                          
//                          conditions_map.put(condition, curr_tuples);
//                      }
//                      
//                    }
//                  }
//                  
//                  
//                  
//                  
//                }
//                
////                ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
////                
////                if(curr_tuples == null)
////                {
////                    curr_tuples =  new ArrayList<Tuple>();
////                    
////                    curr_tuples.add(tuple);
////                    
////                    conditions_map.put(condition, curr_tuples);
////                }
////                else
////                {                   
////                    curr_tuples.add(tuple);
////                    
////                    conditions_map.put(condition, curr_tuples);
////                }
////                                
////              if(condition_id_mapping.get(condition) == null)
////              {
////                  condition_id_mapping.put(condition, valid_conditions.size());
////                  
////                  valid_conditions.add(tuple.conditions.get(i));
////              }
//            }
//            
//            Vector<String> curr_partial_mapping_expression = Query_converter.get_partial_mapping_boolean_expressions(tuple, tuple.query, with_sub_queries_id_mappings);
//            
//            if(!curr_partial_mapping_expression.isEmpty())
//            {
//              for(String curr_partial_mapping_string : curr_partial_mapping_expression)
//              {
//                if(!partial_mapping_strings.contains(curr_partial_mapping_string))
//                {
//                  partial_mapping_strings.add(curr_partial_mapping_string);
//                  
//                  HashSet<Tuple> curr_view_mappings = new HashSet<Tuple>();
//                  
//                  curr_view_mappings.add(tuple);
//                  
//                  partial_mapping_view_mapping_mappings.put(curr_partial_mapping_string, curr_view_mappings);
//                  
//                }
//                else
//                {
//                  partial_mapping_view_mapping_mappings.get(curr_partial_mapping_string).add(tuple);
//                }
//                
//              }
//              
//              
//              
//            }
//	    	
//	    	build_head_variable_mapping(q, tuple);
//	    	
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
//	    	
//	    	
//	    	
//	    	
//	    	
//	    	for(int i = 0; i<tuple.lambda_terms.size(); i++)
//	    	{
//	    		String lambda_str = tuple.lambda_terms.get(i).toString();
//	    			    		
//	    		if(lambda_term_id_mapping.get(lambda_str) == null)
//	    		{
//	    			lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
//	    			
//	    			valid_lambda_terms.add(tuple.lambda_terms.get(i));
//	    		}
//	    	}
//	    	
//	    }
//
//	    if(prepare_info)
//	    	prepare_citation_information(views, c, pst);
//	    	    
//	    
////	    System.out.println("partial_mapping_conditions:::" + partial_mapping_strings);
//	    
//	    return viewTuples;
//	    
//	    
//	}
//	
//	public static void prepare_citation_information(Vector<Query> views, Connection c, PreparedStatement pst) throws SQLException
//	{
////		gen_citation0.get_all_query_ids(query_ids, c, pst);
////	    
////		for(int i = 0; i<query_ids.size; i++)
////		{
////			query_lambda_str.add(null);
////		}
//	    	    
//	    gen_citation0.init_author_mapping(views, author_mapping, max_author_num, c, pst, citation_queries, view_query_mapping);
//
//	}
	
	public static HashMap<String, String> get_citation_queries(String view_name)
	{
//		int view_id = view_list.find(view_name);
		
		return view_query_mapping.get(view_name);
	}
	
	
	static String get_condition_str(Tuple tuple, Conditions condition)
	{
		
		HashMap map_subgoals = tuple.mapSubgoals;
		
		Argument arg1 = condition.arg1;
		
		String arg1_str = new String();
		
		Argument arg2 = condition.arg2;
		
		String arg2_str = new String();
		
		boolean finish1 = false;
		
		boolean finish2 = false;
		
		if(arg2.isConst())
		{
			finish2 = true;
			
			arg2_str = arg2.name;
		}
		
		for(int i = 0; i<tuple.query.body.size(); i++)
		{
			Subgoal origin_subgoal = (Subgoal) tuple.query.body.get(i);
			
			Subgoal map_subgoal = (Subgoal) tuple.mapSubgoals.get(origin_subgoal);
			
			for(int j = 0; j<origin_subgoal.args.size();j++)
			{
				Argument arg = (Argument) origin_subgoal.args.get(j);
				
				if(arg.name.equals(arg1.name) && !finish1)
				{
					
					Argument map_arg = (Argument)map_subgoal.args.get(j); 
					
					arg1_str = map_arg.name;
				}
				
				if(arg.name.equals(arg2.name) && !finish2)
				{
					
					Argument map_arg = (Argument)map_subgoal.args.get(j); 
					
					arg2_str = map_arg.name;
				}
				
				if(finish1 && finish2)
					break;
				
			}
			
			if(finish1 && finish2)
				break;
			
		}
		
		if(arg2.isConst())
			return condition.subgoal1 + "." + arg1_str + condition.op + arg2_str;
		else
			return condition.subgoal1 + "." + arg1_str + condition.op + condition.subgoal2 + "." + arg2_str; 
		
	}
	
	public static void reset() throws SQLException
	{
		view_mapping.clear();
		
		valid_conditions.clear();
		
		valid_lambda_terms.clear();
		
//		condition_id_mapping.clear();
		
		lambda_term_id_mapping.clear();
		
		conditions_map.clear();
				
		tuple_mapping.clear();
		
		view_query_mapping = new HashMap<String, HashMap<String, String>>();
		
		query_lambda_str.clear();
		
		author_mapping.clear();
		
		if(rs!=null)
			rs.close();
		
		covering_set_num = 0;
		
		pre_processing_time = 0.0;
		
		query_time = 0.0;
		
		reasoning_time = 0.0;
		
		population_time = 0.0;
		
		aggregation_time = 0.0;
		
		max_author_num.put("Contributor", -1);
		
		max_author_num.put("info", -1);
		
		max_author_num.put("Investigator", -1);
		
		max_author_num.put("Program", -1);
		
		max_author_num.put("Funding", -1);
		
		tuple_num = 0;
						
		citation_queries.clear();
		
		covering_set_schema_level.clear();
	      
		if(valid_view_mappings_schema_level != null)
	        valid_view_mappings_schema_level.clear();
		
		view_tuple_mapping.clear();
		
		valid_view_mappings_schema_level = null;
	}
	
	public static void tuple_reasoning(Query q, Vector<Query> views, Vector<Query> cqs, HashMap<String, HashMap<String, String>> view_citation_query_mappings, Connection c, PreparedStatement pst) throws SQLException, JSONException, IOException, InterruptedException
	{
				
		reset();
	    
//		Vector<Query> views = Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c, pst);
		
		start = System.nanoTime();
		
		Vector<String> partial_mapping_strings = new Vector<String>();
		
		HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings = new HashMap<String, HashSet<Tuple>>();
		
		
		HashMap<String, Integer> with_sub_queries_id_mappings = new HashMap<String, Integer>();
		
		
        if(!q.head.has_agg)
        {
          head_variable_view_mapping = new ArrayList[q.head.args.size()];
        }
        else
        {
          head_variable_view_mapping = new ArrayList[q.head.agg_args.size()];
        }
		
		Vector<String> full_mapping_condition_str = new Vector<String>();
		
//		HashSet<Tuple> view_tuples = pre_processing(views, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, c,pst);
		
		HashSet<Tuple> view_tuples = Schema_reasoning_with_agg.pre_processing(false, citation_queries, max_author_num, view_query_mapping, author_mapping, query_ids, query_lambda_str, prepare_info, lambda_term_id_mapping, valid_lambda_terms, conditions_map, valid_conditions, view_tuple_mapping, head_variable_query_mapping, relation_arg_mapping, head_variable_view_mapping, views, cqs, view_citation_query_mappings, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str,q_subgoal_id, c,pst);
		
		String sql = null;
        
        if(!test_case)
            sql = Query_converter.datalog2sql_citation_agg(false, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, view_tuples);
        else
            sql = Query_converter.datalog2sql_citation_agg_test(false, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, view_tuples);
		
//        System.out.println(sql);
//        
//        System.out.println(q);
        
		reasoning(q, partial_mapping_strings, partial_mapping_view_mapping_mappings, c, pst, sql, view_tuples);
				
		
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
	
	public static void gen_citation(Covering_set citation_views) throws ClassNotFoundException, SQLException
	{
		for(citation_view view_mapping: citation_views.c_vec)
		{
			String query = view_mapping.get_full_query();
			
			System.out.println(query);
		}
	}
	
//	public static voi get_citation_views(HashSet<Tuple> views, Query query,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException, IOException, InterruptedException
//	{
//		Vector<Vector<citation_view_vector>> c_views = query_execution(views, query, c, pst);
//				
//		return c_views;
//	}
	
	
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
	
	
//	public static HashSet<Conditions> remove_conflict_duplicate_view_mapping(Query query)
//	{
//		if(valid_conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
//		{
//			return valid_conditions;
//		}
//		else
//		{
//
//			HashSet<Conditions> update_conditions = new HashSet<Conditions>();
//			
//			boolean remove = false;
//			
//			for(Iterator iter = valid_conditions.iterator(); iter.hasNext(); )
//			{
//				remove = false;
//				
//				Conditions v_condition = (Conditions) iter.next();
//				
//				for(int j = 0; j<query.conditions.size(); j++)
//				{
//					if(Conditions.compare(query.conditions.get(j), v_condition) || Conditions.compare(query.conditions.get(j), Conditions.negation(v_condition)))
//					{
//						remove = true;
//												
//						break;
//					}
//				}
//				
//				if(!remove)
//					update_conditions.add(v_condition);
//			}
//			
//			valid_conditions = update_conditions;
//			
//			int id = 0;
//			
//			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
//			{
//				Conditions condition = (Conditions) iter.next();
//				
//				condition_id_mapping.put(condition, id);
//				
//				id ++;
//			}
//			
//			return valid_conditions;
//			
//		}
//	}
	
	
	public static Vector<Vector<Covering_set>> query_execution(HashSet<Tuple> views, Query query, String sql, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException, IOException, InterruptedException
	{
				
		Vector<Vector<Covering_set>> c_views = new Vector<Vector<Covering_set>>();
								
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
//		remove_conflict_duplicate_view_mapping(query);
			
				
//		System.out.println(sql);

		end = System.nanoTime();
		
		pre_processing_time += (end - start) * 1.0/second2nano;
		
		reasoning(query, partial_mapping_strings, partial_mapping_view_mapping_mappings, c, pst, sql, views);
		
//		cal_view_mapping_valid_rows();
		
//		reasoning(views, c_views, query, c, pst, sql, citation_strs, citation_view_map2);
		
		return c_views;
	}
	
	static void cal_view_mapping_valid_rows()
	{
	  Set<String> signatures = signiture_rid_mappings.keySet();
	  
	  HashMap<Tuple, HashSet<Integer>> view_mapping_valid_rid_mappings = new HashMap<Tuple, HashSet<Integer>>();
	  
	  for(String sigature: signatures)
	  {
	    HashSet<Tuple> view_mappings = signature_view_mappings_mappings.get(sigature);
	    
	    HashSet<Integer> rids = signiture_rid_mappings.get(sigature);
	    
	    for(Tuple tuple: view_mappings)
	    {
	      if(view_mapping_valid_rid_mappings.get(tuple) == null)
	      {
	        view_mapping_valid_rid_mappings.put(tuple, rids);
	      }
	      else
	      {
	        view_mapping_valid_rid_mappings.get(tuple).addAll(rids);
	      }
	    }
	    
	  }
	  
	  Set<Tuple> view_mappings = view_mapping_valid_rid_mappings.keySet();
	  
	  for(Tuple view_mapping: view_mappings )
	  {
	    System.out.print(view_mapping.name + view_mapping.mapSubgoals_str + "  ");
	    
	    System.out.println(view_mapping_valid_rid_mappings.get(view_mapping).size());
	  }
	  
//	  return view_mapping_valid_rid_mappings;
	}
	
	static ArrayList<Covering_set> join_views_curr_relation(ArrayList<Tuple> tuples, ArrayList<Covering_set> curr_view_com) throws ClassNotFoundException, SQLException
	{
		if(curr_view_com.isEmpty())
		{
			if(tuples.isEmpty())
				return new ArrayList<Covering_set>();
			else
			{
				ArrayList<Covering_set> new_view_com = new ArrayList<Covering_set>();
				
				for(int i = 0; i<tuples.size(); i++)
				{
					
					Tuple valid_tuple = tuples.get(i);
					
					if(valid_tuple.lambda_terms.size() > 0)
					{
						
						citation_view_parametered c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);
						
						Covering_set curr_views = new Covering_set(c);
						
						new_view_com.add(curr_views);
					}	
					else
					{
						
						citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
						
						Covering_set curr_views = new Covering_set(c);
						
						new_view_com.add(curr_views);
					}
				}
				
				return new_view_com;
			}
		}
		
		else
		{
			ArrayList<Covering_set> new_view_com = new ArrayList<Covering_set>();
			
			for(int i = 0; i<tuples.size(); i++)
			{
				Tuple valid_tuple = tuples.get(i);
				
				citation_view c = null;
				
				if(valid_tuple.lambda_terms.size() > 0)
				{
					
					c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);
				}	
				else
				{
					
					c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
				}
				
				for(int j = 0; j < curr_view_com.size(); j++)
				{
					Covering_set old_view_com = curr_view_com.get(j).clone();
					
					Covering_set view_com = Covering_set.merge(old_view_com, c);
					
					new_view_com.add(view_com);
				}
			}
			
			return new_view_com;
		}
	}
	
	
//	   static void reasoning_single_tuple_no_clustering(HashSet views, Query query, ResultSet rs, HashSet<Covering_set> c_view_template, HashSet<Argument> curr_head_vars)
//	    {
//	        
////	      ArrayList<citation_view_vector> view_com = new ArrayList<citation_view_vector>();
//	        
//	        HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
//	        
//	        Set<Argument> args = head_variable_view_mapping.keySet();
//	        
//	        for(Argument arg: args)
//	        {
//	          ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(arg).clone();
//              
//              curr_tuples.retainAll(views);
//              
//              all_covering_sets = Tuple_reasoning1_full_test_opt.join_views_curr_relation(curr_tuples, all_covering_sets, query.head.args, view_mapping);
//
//	        }
//	        
////	        Set<String> relations = head_variable_query_mapping.keySet();
////	        
////	        for(Iterator iter = relations.iterator(); iter.hasNext();)
////	        {
////	            String relation = (String) iter.next();
////	            
////	            HashSet<Argument> view_head_args = relation_arg_mapping.get(relation);
////	            
////	            if(view_head_args == null)
////	            {
////	                continue;
////	            }
////	            else
////	            {
////	                HashSet<Argument> view_head_args_copy = (HashSet<Argument>) view_head_args.clone();
////	                
//////	              HashSet<citation_view_vector> covering_sets = new HashSet<citation_view_vector>();
////	                
////	                ArrayList<Argument> q_head_args = head_variable_query_mapping.get(relation);
////	                
////	                view_head_args_copy.retainAll(q_head_args);
////	                
////	                for(Iterator it = view_head_args_copy.iterator(); it.hasNext();)
////	                {
////	                    Argument arg = (Argument) it.next();
////	                    
////	                    ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(arg).clone();
////	                    
////	                    curr_tuples.retainAll(views);
////	                    
////	                    all_covering_sets = Tuple_reasoning1_full_test_opt.join_views_curr_relation(curr_tuples, all_covering_sets, query.head.args, view_mapping);
////	                }
////	                
//////	              all_covering_sets = Tuple_reasoning1_full_test_opt.join_operation(all_covering_sets, covering_sets, all_covering_sets.size());
////	            }
////	            
////	            
////	            
////	        }
//	                
//	        c_view_template.addAll(all_covering_sets);
//	        
//	        all_covering_sets.clear();
//	        
//	    }
//	
//	static ArrayList<citation_view_vector> add_one_additional_citation_view(ArrayList<citation_view_vector> c_vectors, ArrayList<Tuple> valid_tuples) throws ClassNotFoundException, SQLException
//	{
//		ArrayList<citation_view_vector> new_c_vectors = new ArrayList<citation_view_vector>();
//		
//		for(int i = 0; i<c_vectors.size(); i++)
//		{
//			
//			
//			for(int r = 0; r<valid_tuples.size(); r++)
//			{
//				
//				citation_view_vector c_vector = c_vectors.get(i).clone();
//				
//				Tuple valid_tuple = valid_tuples.get(r);
//				
//				citation_view c = null;
//				
//				if(valid_tuple.lambda_terms.size() > 0)
//				{
//					
//					c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);
//				}	
//				else
//				{
//					
//					c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
//				}
//				
//				c_vector = c_vector.merge(c);
//				
//				new_c_vectors.add(c_vector);
//			}
//		}
//		
//		return new_c_vectors;
//	}
	
//	static ArrayList<citation_view_vector> add_additional_citation_view(ArrayList<citation_view_vector> c_view_template, citation_view_vector c_vector, HashSet<Argument> curr_head_vars_copy) throws ClassNotFoundException, SQLException
//	{
//		
//		ArrayList<citation_view_vector> c_vectors = new ArrayList<citation_view_vector>();
//		
//		c_vectors.add(c_vector);
//		
//		for(Iterator iter = curr_head_vars_copy.iterator(); iter.hasNext();)
//		{
//			Argument head_arg = (Argument) iter.next();
//			
//			ArrayList<Tuple> valid_tuples = head_variable_view_mapping.get(head_arg);
//			
//			c_vectors = add_one_additional_citation_view(c_vectors, valid_tuples);
//		}
//		
//		for(int i = 0; i<c_vectors.size(); i++)
//		{
//			c_view_template = remove_duplicate(c_view_template, c_vectors.get(i));
//		}
//		
//		return c_view_template;
//	}
	
	
	public static Vector<Covering_set> remove_duplicate_final(Vector<Covering_set> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		Vector<Covering_set> update_combinations = new Vector<Covering_set>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
			boolean contains = false;
			
			Covering_set c_combination = c_combinations.get(i);
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_combinations.get(j);
					
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
	
//	static Vector<String> populate_citation(Vector<citation_view_vector> update_c_view, Vector<String> vals, Connection c, PreparedStatement pst, Vector<HashMap<String, Vector<String>>> citation_strings) throws SQLException
//	{
//		Vector<String> citations = new Vector<String>();
//		
//		
//		for(int p =0; p<update_c_view.size(); p++)
//		{
//			
//			String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings.get(p), max_author_num);
//
//			citations.add(str);
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
	
	static HashSet<Argument> get_all_head_variables(HashSet<Tuple> curr_valid_tuples, Query query)
	{
		HashSet<Argument> head_args = new HashSet<Argument>();
		
		for(Iterator iter = curr_valid_tuples.iterator(); iter.hasNext();)
		{
			Tuple tuple = (Tuple) iter.next();
			
			head_args.addAll(tuple.getArgs());
		}
		
		head_args.retainAll(query.head.args);
		
		return head_args;
	}
	
	public static void reasoning(Query query, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, Connection c, PreparedStatement pst, String sql, HashSet<Tuple> views) throws SQLException, IOException, InterruptedException, JSONException
	{
		
		start = System.nanoTime();
		
		pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

				
//		System.out.println(sql);
		
		rs = pst.executeQuery();
		
		end = System.nanoTime();
		
		query_time += (end - start) * 1.0/second2nano;
		
		int lambda_term_num = valid_lambda_terms.size();

		String old_value = new String();
		
		Vector<Vector<String>> values = new Vector<Vector<String>>(); 
				
				
		HashSet<Covering_set> c_view_template = null;
		
		int start_pos = 0;
		
		int end_pos = -1;
		
		
//		HashSet<Tuple> valid_view_mappings_for_all = null;
		
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
				
				values.add(vals);
				
				Head_strs h_vals = new Head_strs(vals);
				
				vals.clear();
										
				int pos1 = query.head.args.size() + valid_lambda_terms.size();
				
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
				
				for(int i = pos1 + valid_conditions.size(); i<pos1 + valid_conditions.size() + partial_mapping_strings.size(); i++)
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

				if(signiture_rid_mappings.get(curr_str) == null)
				{
				  
				  HashSet<Integer> rids = new HashSet<Integer>();
				  
				  rids.add(tuple_num);
				  
				  signiture_rid_mappings.put(curr_str, rids);
				  
				  start = System.nanoTime();

                  group_num++;
                  
                      c_view_template = new HashSet<Covering_set>();
                                              
                      HashSet<Tuple> curr_views = (HashSet<Tuple>) views.clone();
                                          
                      int i = pos1;
                      
                      for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
                      {
                          boolean condition = rs.getBoolean(i + 1);
                          
                          Conditions curr_condition = (Conditions) iter.next();
                          
                          if(!condition)
                          {                           
                              ArrayList<Tuple> invalid_views = conditions_map.get(curr_condition);
                              
                              curr_views.removeAll(invalid_views);
                          }
                          i++;
                      } 
                      
                      for(String partial_mapping_string: partial_mapping_strings)
                      {
                        boolean condition = rs.getBoolean(i + 1);
                        
                        if(!condition)
                        {                           
                            HashSet<Tuple> invalid_views = partial_mapping_view_mapping_mappings.get(partial_mapping_string);
                            
                            curr_views.removeAll(invalid_views);
                        }
                        i++;
                      }
                                         
//                      System.out.println(curr_str + "|" + valid_conditions + "|" + partial_mapping_strings);
                      
                      if(agg_intersection)
                      {
                        if(valid_view_mappings_schema_level == null)
                        {
                          valid_view_mappings_schema_level = new HashSet<Tuple>();
                          valid_view_mappings_schema_level.addAll(curr_views);
                        }
                        else
                        {
                          valid_view_mappings_schema_level.retainAll(curr_views);
                        }
                      }
                      else
                      {
                        if(valid_view_mappings_schema_level == null)
                        {
                          valid_view_mappings_schema_level = new HashSet<Tuple>();
                          
                          valid_view_mappings_schema_level.addAll(curr_views);
                        }
                        else
                        {
                          valid_view_mappings_schema_level.addAll(curr_views);
                        }
                      }
                      
//                      System.out.println("Group" + group_num);
//                      
//                      for(Tuple view_mapping: curr_views)
//                      {
//                        System.out.println(view_mapping.name);
//                      }
                      
                      signature_view_mappings_mappings.put(curr_str, curr_views);

                      HashSet<Argument> curr_head_vars = get_all_head_variables(curr_views, query);
                      
                      Resultset_prefix_col_num = query.head.args.size();
                      
                      
                      double start_time = System.nanoTime();
                      
                      if(isclustering)
                        Covering_sets_clustering_by_relation.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, curr_views, query, rs, c_view_template, curr_head_vars);
                      else
                        Covering_sets_no_clustering.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, curr_views, query, rs, c_view_template, curr_head_vars);
                      
                      c_view_map.put(curr_str, c_view_template);
                      
                      double end_time = System.nanoTime();
                      
                      covering_set_time = (end_time - start_time) * 1.0/second2nano;
                                              
                      covering_set_num += c_view_template.size();
              
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

                      end = System.nanoTime();
                      
                      reasoning_time += (end - start) * 1.0/second2nano;
				}
				else
				{
					start = System.nanoTime();
										
					covering_set_num += c_view_template.size();
										
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
					
					signiture_rid_mappings.get(curr_str).add(tuple_num);
					
					end = System.nanoTime();
					
					population_time += (end - start) * 1.0/second2nano;
					
				}
			}
			
						
		}
//		else
//		{
//			
//			boolean first = true;
//						
//			while(rs.next())
//			{				
//				
//				tuple_num ++;
//				
//				Vector<String> vals = new Vector<String>();
//				
//				for(int i = 0; i<query.head.args.size(); i++)
//				{
//					vals.add(rs.getString(i+1));
//				}
//				
//				values.add(vals);
//				
//				Head_strs h_vals = new Head_strs(vals);
//				
//				vals.clear();
//								
//				if(first)
//				{
//				  
//				  HashSet<Integer> rids = new HashSet<Integer>();
//				  
//				  rids.add(tuple_num);
//				  
//				  signiture_rid_mappings.put(new String(), rids);
//					
//					start = System.nanoTime();
//										
//					HashSet curr_views = (HashSet) views.clone();
//					
//					if(agg_intersection)
//                    {
//                      if(valid_view_mappings_schema_level == null)
//                      {
//                        valid_view_mappings_schema_level = new HashSet<Tuple>();
//                        valid_view_mappings_schema_level.addAll(curr_views);
//                      }
//                      else
//                      {
//                        valid_view_mappings_schema_level.retainAll(curr_views);
//                      }
//                    }
//                    else
//                    {
//                      if(valid_view_mappings_schema_level == null)
//                      {
//                        valid_view_mappings_schema_level = new HashSet<Tuple>();
//                        
//                        valid_view_mappings_schema_level.addAll(curr_views);
//                      }
//                      else
//                      {
//                        valid_view_mappings_schema_level.addAll(curr_views);
//                      }
//                    }
//					
//					c_view_template = new HashSet<citation_view_vector>();
//					
//					group_num ++;
//					
//					Resultset_prefix_col_num = query.head.args.size();
//					
//					HashSet<Argument> curr_head_vars = get_all_head_variables(curr_views, query);
//					
//					double start_time = System.nanoTime();
//                    
//                    if(isclustering)
//                      reasoning_single_tuple(curr_views, query, rs, c_view_template, curr_head_vars);
//                    else
//                      reasoning_single_tuple_no_clustering(curr_views, query, rs, c_view_template, curr_head_vars);
//                    
//                    signature_view_mappings_mappings.put(new String(), curr_views);
//                    
//                    c_view_map.put(new String(), c_view_template);
//                    
//                    double end_time = System.nanoTime();
//                    
//                    covering_set_time = (end_time - start_time) * 1.0/second2nano;
//					
//					covering_set_num += c_view_template.size();
//															
//					first = false;
//					
//					if(head_strs_rows_mapping.get(h_vals) == null)
//					{
//						ArrayList<Integer> int_list = new ArrayList<Integer>();
//						
//						int_list.add(tuple_num);
//						
//						head_strs_rows_mapping.put(h_vals, int_list);
//					}
//					else
//					{
//						ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//						
//						int_list.add(tuple_num);
//						
//						head_strs_rows_mapping.put(h_vals, int_list);
//					}
//
//					end = System.nanoTime();
//					
//					reasoning_time += (end - start) * 1.0/second2nano;
//				}
//				
//				else
//				{			
//				  
//				  signiture_rid_mappings.get(new String()).add(tuple_num);
//				  
//					start = System.nanoTime();
//										
//					covering_set_num += c_view_template.size();
//			
//					if(head_strs_rows_mapping.get(h_vals) == null)
//					{
//						ArrayList<Integer> int_list = new ArrayList<Integer>();
//						
//						int_list.add(tuple_num);
//						
//						head_strs_rows_mapping.put(h_vals, int_list);
//					}
//					else
//					{
//						ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//						
//						int_list.add(tuple_num);
//						
//						head_strs_rows_mapping.put(h_vals, int_list);
//					}
//					
//					end = System.nanoTime();
//					
//					population_time += (end - start) * 1.0/second2nano;
//					
//				}
//
//			}
//		}
		
		Set<String> signatures = signature_view_mappings_mappings.keySet();
		
		boolean matched = false;
		
		for(String curr_signature: signatures)
		{
		  HashSet<Tuple> curr_view_mappings = signature_view_mappings_mappings.get(curr_signature);
		  
		  if(valid_view_mappings_schema_level.size() == curr_view_mappings.size() && valid_view_mappings_schema_level.containsAll(curr_view_mappings))
		  {
		    covering_set_schema_level.addAll(c_view_map.get(curr_signature));
		    
		    matched = true;
		    
		    break;
		  }
		  
		}
		
		if(!matched)
		{
		  if(isclustering)
            Covering_sets_clustering_by_relation.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, valid_view_mappings_schema_level, query, rs, covering_set_schema_level, get_all_head_variables(valid_view_mappings_schema_level, query));
          else
            Covering_sets_no_clustering.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, valid_view_mappings_schema_level, query, rs, covering_set_schema_level, get_all_head_variables(valid_view_mappings_schema_level, query));

		}

//		System.out.print(group_num + "	");
//		
//		System.out.print(tuple_num + "	");
	
	}
	
//	public static HashSet<String> gen_citation(Head_strs h_vals, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
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
//			HashSet<citation_view_vector> c_views = c_view_map.get(interval);
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
////					System.out.println(c_views.toString());
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//					
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping));
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
//			ArrayList<citation_view_vector> c_views = c_view_map.get(interval);
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
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping));
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
//	public static HashSet<String> gen_citation(Head_strs h_vals, HashMap<Head_strs, HashSet<String>> c_view_maps, HashSet<String> covering_set_strs, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
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
//			HashSet<citation_view_vector> c_views = c_view_map.get(interval);
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
//					for(int k = 0; k<c_views.size(); k++)
//					{
//						
//						String c_view_index = new String();
//						
//						for(int p = 0; p<c_views.get(k).c_vec.size(); p++)
//						{
//							c_view_index += c_views.get(k).c_vec.get(p).get_name() + populate_db.separator + c_views.get(k).c_vec.get(p).get_table_name_string();
//						}
//						
//						covering_set_strs.add(c_view_index);
//					}
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
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping));
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

//	static HashSet<String> gen_citation(ArrayList<Covering_set> c_views, Connection c, PreparedStatement pst, ArrayList<HashMap<String, Integer>> view_query_mapping) throws ClassNotFoundException, SQLException, JSONException
//	{
//		HashSet<String> citations = new HashSet<String>();
//						
//		JSONObject json_obj = new JSONObject();
//		
//		HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
//		
//		for(int p =0; p<c_views.size(); p++)
//		{								
//			HashSet<String> str = gen_citation1.get_citations3(c_views.get(p), c, pst, view_list, view_query_mapping, author_mapping, max_author_num, query_ids, query_lambda_str, view_author_mapping, populate_db.star_op);
//			
//			citations.addAll(str);
//		}
//	
//		return citations;
//	}

	
	
	
//	public static void reasoning(HashSet views, Vector<Vector<citation_view_vector>> c_views, Query query, Connection c, PreparedStatement pst, String sql, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2) throws SQLException, ClassNotFoundException, JSONException
//	{
//		pst = c.prepareStatement(sql);
//				
//		ResultSet rs = pst.executeQuery();
//		
//		ResultSetMetaData r = rs.getMetaData();
//		
//		int col_num = r.getColumnCount();
//						
//		String old_value = new String();
//		
//				
//		Vector<Vector<String>> values = new Vector<Vector<String>>();
//		
////		Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
//		
//		Vector<HashMap<String, Vector<String>>> citation_strings = new Vector<HashMap<String, Vector<String>>>();
//		
//		int group_num = 0;
//		
//		int tuple_num = 0;
//		
//		Vector<citation_view_vector> c_view_template = null;
//		
//		if(!valid_conditions.isEmpty())
//		{
//			
//			while(rs.next())
//			{				
//				tuple_num ++;
//				
//				String curr_str = new String();
//				
//				Vector<String> vals = new Vector<String>();
//				
//				for(int i = 0; i<query.head.args.size(); i++)
//				{
//					vals.add(rs.getString(i+1));
//				}
//				
//				values.add(vals);
//				
//				Head_strs h_vals = new Head_strs(vals);
//				
//				vals.clear();
//								
//				int pos1 = query.head.args.size() + valid_lambda_terms.size();
//				
//				for(int i = pos1; i< pos1 + valid_conditions.size(); i++)
//				{
//					boolean condition = rs.getBoolean(i + 1);
//					
//					if(!condition)
//					{
//						curr_str += "0";
//					}
//					else
//					{
//						
//						curr_str += "1";
//					}
//				}
//				
//				if(!curr_str.equals(old_value))
//				{
//										
////					System.out.println(h_vals);
//					
//					group_num++;
//					
//					c_view_template = new Vector<citation_view_vector>();
//					
//					citation_strings = new Vector<HashMap<String, Vector<String>>>();
//					
//					HashSet curr_views = (HashSet) views.clone();
//										
//					int i = pos1;
//					
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
//							Vector<Tuple> invalid_views = conditions_map.get(condition_str);
//							
//							curr_views.removeAll(invalid_views);
//						}
//						i++;
//					}	
//					
//					
////					remove_invalid_web_view(curr_views, pos1, col_num,  rs);
//					
////					System.out.println(curr_str);
//
//					
//					HashSet<citation_view_vector> c_v = reasoning_single_tuple(curr_views, query, rs, c_view_template);
//					
//					Vector<citation_view_vector> c_vec = new Vector<citation_view_vector>();
//					
//					c_vec.addAll(c_v);
//					
//					c_v.clear();
//
//					old_value = curr_str;
//					
////					output_vec_com(c_vec);
//					
//					c_views.add(c_vec);
//					
////					Vector<String> citations = new Vector<String>();
//					
//					HashSet<String> citations = gen_citation(c_vec, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
//					
////					HashSet<String> citations = new HashSet<String>();
//					
//					if(citation_strs.get(h_vals) == null)
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
////						
////						curr_c_view_vector.add(c_vec);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						citation_strs.put(h_vals, citations);
//					}
//					else
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
//						
//						HashSet<String> curr_citations = citation_strs.get(h_vals);
//						
////						curr_c_view_vector.add(c_vec);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						curr_citations.addAll(citations);
//						
//						citations.clear();
//						
//						citation_strs.put(h_vals, curr_citations);
//					}
//					
//				}
//				
//				else
//				{
//					
////					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
//					
//					Vector<citation_view_vector> curr_c_view = c_view_template;
//					
//					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
//					
//					for(int i = 0; i<curr_c_view.size(); i++)
//					{
//						insert_c_view.add(curr_c_view.get(i).clone());
//					}
//					
//					HashSet<citation_view_vector> update_c = update_valid_citation_combination(insert_c_view, rs);
//					
//					Vector<citation_view_vector> update_c_view = new Vector<citation_view_vector>();
//					
//					update_c_view.addAll(update_c);
//					
//					update_c.clear();
//					
////					output_vec_com(update_c_view);
//					
//					c_views.add(update_c_view);
//					
////					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
//					
//					HashSet<String> citations = gen_citation(update_c_view, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
//					
////					HashSet<String> citations = new HashSet<String>();
//					
//					if(citation_strs.get(h_vals) == null)
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
////						
////						curr_c_view_vector.add(update_c_view);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						citation_strs.put(h_vals, citations);
//					}
//					else
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
//						
//						HashSet<String> curr_citations = citation_strs.get(h_vals);
//						
////						curr_c_view_vector.add(update_c_view);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						curr_citations.addAll(citations);
//						
//						citations.clear();
//						
//						citation_strs.put(h_vals, curr_citations);
//					}
//				}
//			}
//		}
//		else
//		{
//			
//			boolean first = true;
//
//			while(rs.next())
//			{
//				
////				System.out.println(rid);
//				tuple_num ++;
//				
//				String curr_str = new String();
//				
//				Vector<String> vals = new Vector<String>();
//				
//				for(int i = 0; i<query.head.args.size(); i++)
//				{
//					vals.add(rs.getString(i+1));
//				}
//				
//				values.add(vals);
//				
//				Head_strs h_vals = new Head_strs(vals);
//				
//				vals.clear();
//				
//				int pos1 = query.head.args.size() + valid_lambda_terms.size();
//
//				int pos2 = pos1 + valid_conditions.size();
//
//				
//				if(first)
//				{
//					
////					System.out.println(h_vals);
//					
//					HashSet curr_views = (HashSet) views.clone();
//					
//					c_view_template = new Vector<citation_view_vector>();
//					
//					group_num ++;
////					remove_invalid_web_view(curr_views, pos2, col_num,  rs);
//					
//					HashSet<citation_view_vector> c_v = reasoning_single_tuple(curr_views, query, rs, c_view_template);
//					
//					Vector<citation_view_vector> curr_c_views = new Vector<citation_view_vector>();
//					
//					curr_c_views.addAll(c_v);
//					
//					c_v.clear();
//											
////					output_vec_com(curr_c_views);
//					
//					c_views.add(curr_c_views);
//					
//					old_value = curr_str;
//				
////					Vector<String> citations = gen_citation(curr_c_views, vals, c, pst, citation_strings);
//					HashSet<String> citations = gen_citation(curr_c_views, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
//					
////					HashSet<String> citations = new HashSet<String>();
//					
//					first = false;
//					
//					if(citation_strs.get(h_vals) == null)
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
////						
////						curr_c_view_vector.add(curr_c_views);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						citation_strs.put(h_vals, citations);
//					}
//					else
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
//						
//						HashSet<String> curr_citations = citation_strs.get(h_vals);
//						
////						curr_c_view_vector.add(curr_c_views);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						curr_citations.addAll(citations);
//						
//						citations.clear();
//						
//						citation_strs.put(h_vals, curr_citations);
//					}
//					
//					
//				}
//				
//				else
//				{
//					
////					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
//					
//					Vector<citation_view_vector> curr_c_view = c_view_template;
//					
//					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
//					
//					for(int i = 0; i<curr_c_view.size(); i++)
//					{
//						insert_c_view.add(curr_c_view.get(i).clone());
//					}
//					
//					HashSet<citation_view_vector> update_c = update_valid_citation_combination(insert_c_view, rs);
//					
//					Vector<citation_view_vector> update_c_view = new Vector<citation_view_vector>();
//					
//					update_c_view.addAll(update_c);
//					
//					update_c.clear();
//					
//					c_views.add(update_c_view);
//					
//					HashSet<String> citations = gen_citation(update_c_view, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
//					
////					HashSet<String> citations = new HashSet<String>();
//					
////					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
//					
//					
//					if(citation_strs.get(h_vals) == null)
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
////						
////						curr_c_view_vector.add(update_c_view);
////						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
////						
//						citation_strs.put(h_vals, citations);
//					}
//					else
//					{
////						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
//						
//						HashSet<String> curr_citations = citation_strs.get(h_vals);
//						
////						curr_c_view_vector.add(update_c_view);
//						
//						curr_citations.addAll(citations);
//						
//						citations.clear();
//						
////						citation_view_map2.put(h_vals, curr_c_view_vector);
//						
//						citation_strs.put(h_vals, curr_citations);
//					}
//				}
//			}
//		}
//		
//		if(c_view_template != null)
//			c_view_template.clear();
//			
////		output2excel.citation_output(rs, query, values, c_views, file_name, values);
//		System.out.print(group_num + "	");
//		
//		System.out.print(tuple_num + "	");
//		
//
//	}
	
//	static HashSet<String> gen_citation(Vector<citation_view_vector> c_views, Vector<String> vals, Connection c, PreparedStatement pst, Head_strs h_vals, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping) throws ClassNotFoundException, SQLException, JSONException
//	{
//		HashSet<String> citations = new HashSet<String>();
//		
//		HashSet<String> author_list = new HashSet<String>();
//		
//		
//		for(int p =0; p<c_views.size(); p++)
//		{
//			
//			author_list = new HashSet<String>();
//						
//			String str = gen_citation1.get_citations2(c_views.get(p), vals, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_author_num, author_list);
//			
////			System.out.println(c_views.get(p));
////			
////			System.out.println(str);
//			
//			citations.add(str);
//			
////			Vector<citation_view> c_vec = c_views.get(p).c_vec;
////			
////			for(int k = 0; k<c_vec.size(); k++)
////			{
////				HashSet<String> query_strs = citation_strings.get(c_vec.get(k).get_name());
////								
////				if(query_strs == null)
////				{
////					
////					
////					citation_strings.put(c_vec.get(k).get_name(), curr_query_str);
////				}
////				else
////				{
////					query_strs.addAll(curr_query_str);
////					
////					citation_strings.put(c_vec.get(k).get_name(), query_strs);
////				}
////				
////				
////				
////			}
//						
////			if(authors.get(h_vals) == null)
////			{
////				Vector<HashSet<String>> author_l = new Vector<HashSet<String>>();
////				
////				author_l.add(author_list);
////				
////				authors.put(h_vals, author_l);
////				
////			}
////			else
////			{
////				Vector<HashSet<String>> author_l = authors.get(h_vals);
////				
////				author_l.add(author_list);
////				
////				authors.put(h_vals, author_l);
////			}
//			
//		}
//		
////		citation_strs.add(citations);
//		
//		return citations;
//	}
	
	
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
	
	
	public static void output_vec_com(Vector<Covering_set>c_unit_combinaton)
	{
		for(int i = 0; i<c_unit_combinaton.size(); i++)
		{
			if(i >= 1)
				System.out.print(",");
			
			System.out.print("view_combination:::" + c_unit_combinaton.get(i));
		}
		System.out.println();
	}
	
	public static void update_valid_citation_combination(HashSet<Covering_set> insert_c_view, ResultSet rs, int start_pos) throws SQLException
	{
		
//		HashSet<citation_view_vector> update_c_views = new HashSet<citation_view_vector>();
		
		for(Iterator iter = insert_c_view.iterator(); iter.hasNext();)
		{
			
			Covering_set c_vec = (Covering_set) iter.next();
			
			for(citation_view view_mapping: c_vec.c_vec)
			{
				citation_view c_view = view_mapping;
				
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

	   public static void get_views_parameters(HashSet<Tuple> insert_c_view, ResultSet rs, int start_pos, HashMap<Tuple, ArrayList<Head_strs>> values) throws SQLException
	    {
	        
//	      HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
	        
	        Set<String> head_sets = lambda_term_id_mapping.keySet();
	        
	        for(Tuple view_mapping: insert_c_view)
	        {
	            
//	          citation_view c_vec = insert_c_view.get(i);
	            
//	          for(int j = 0; j<c_vec.c_vec.size(); j++)
//	          {
//	              citation_view c_view = c_vec.c_vec.get(j);
	                
	                if(view_mapping.lambda_terms.size() > 0)
	                {
//	                  citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
	                    
//	                  Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
	                    
	                    Vector<String> heads = new Vector<String>();
	                    
	                    for(int k = 0; k<view_mapping.lambda_terms.size(); k++)
	                    {
	                        int id = lambda_term_id_mapping.get(view_mapping.lambda_terms.get(k).toString());
	                        
	                        heads.add(rs.getString(id + start_pos + 1));
	                        
//	                      curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
	                    }
	                    
	                    Head_strs head_values = new Head_strs(heads);
	                    
	                    if(values.get(view_mapping) == null)
	                    {
	                      
	                      ArrayList<Head_strs> curr_head_values = new ArrayList<Head_strs>();
	                      
	                      curr_head_values.add(head_values);
	                      
	                        values.put(view_mapping, curr_head_values);
	                    }
	                    else
	                    {
//	                      HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//	                      
//	                      curr_head_values.add(head_values);
	                        
	                        values.get(view_mapping).add(head_values);//add(curr_head_values);
	                    }
	                    
	                }
//	              else
//	              {
//	                  if(values.size() > i)
//	                  {
//	                      continue;
//	                  }
//	                  else
//	                  {
//	                      values.add(new HashSet<Head_strs>());
//	                  }
//	              }
	                
//	          }
	        }
	    }
	
//	public static void get_views_parameters(ArrayList<citation_view> insert_c_view, ResultSet rs, int start_pos, ArrayList<HashSet<Head_strs>> values) throws SQLException
//	{
//		
////		HashSet<citation_view_vector> update_c_views = new HashSet<citation_view_vector>();
//		
////		Vector<String> heads = new Vector<String>();
////		
////		Set<String> head_sets = lambda_term_id_mapping.keySet();
////		
////		for(Iterator iter = head_sets.iterator(); iter.hasNext();)
////		{
////			String lambda_terms = (String) iter.next();
////			
////			int id = lambda_term_id_mapping.get(lambda_terms);
////			
////			heads.add(rs.getString(id + start_pos + 1));
////		}
////		
////		Head_strs lambda_values = new Head_strs(heads);
////		
////		values.add(lambda_values);
//		
//		for(int i = 0; i<insert_c_view.size(); i++)
//		{
//			
//			citation_view c_vec = insert_c_view.get(i);
//			
////			for(int j = 0; j<c_vec.c_vec.size(); j++)
////			{
////				citation_view c_view = c_vec.c_vec.get(j);
//				
//				if(c_vec.has_lambda_term())
//				{
//					citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
//					
//					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
//					
//					Vector<String> heads = new Vector<String>();
//					
//					for(int k = 0; k<lambda_terms.size(); k++)
//					{
//						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
//						
//						heads.add(rs.getString(id + start_pos + 1));
//						
//						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
//					}
//					
//					Head_strs head_values = new Head_strs(heads);
//					
//					if(values.size() > i)
//					{
//						values.get(i).add(head_values);
//					}
//					else
//					{
//						HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//						
//						curr_head_values.add(head_values);
//						
//						values.add(curr_head_values);
//					}
//					
//				}
//				else
//				{
//					if(values.size() > i)
//					{
//						continue;
//					}
//					else
//					{
//						values.add(new HashSet<Head_strs>());
//					}
//				}
//				
////			}
//		}
//	}
	
	public static void get_views_parameters(ArrayList<citation_view> insert_c_view, ResultSet rs, int start_pos, ArrayList<HashSet<Head_strs>> values, ArrayList<Integer> view_ids) throws SQLException
	{
		
		
		for(int j = 0; j<view_ids.size(); j++)
		{
			
			int i = view_ids.get(j);
			
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
					
//					if(values.size() > i)
					{
						values.get(i).add(head_values);
					}
//					else
//					{
//						HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//						
//						curr_head_values.add(head_values);
//						
//						values.add(curr_head_values);
//					}
					
				}
//				else
//				{
//					if(values.size() > i)
//					{
//						continue;
//					}
//					else
//					{
//						values.add(new HashSet<Head_strs>());
//					}
//				}
				
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
			String [] lambda_term_names = r.getString(1).split("\\" + populate_db.separator);
			
			String [] table_name = r.getString(2).split("\\" + populate_db.separator);
			
			for(int i = 0; i<lambda_term_names.length; i++)
			{
				Lambda_term l_term = new Lambda_term(lambda_term_names[i], table_name[i]);
				
				lambda_terms.add(l_term);
				
			}
			
			
		}
		
		return lambda_terms;
		
		
		
	}
	
	static Vector<Lambda_term> get_lambda_terms_web_view(String c_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		String q = "select vl.lambda_term, vl.table_name from view2lambda_term vl join web_view_table w using (view) where view = '" + c_view_name + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		if(r.next())
		{
			String [] lambda_term_names = r.getString(1).split("\\" + populate_db.separator);
			
			String [] table_name = r.getString(2).split("\\" + populate_db.separator);
			
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
//					if(view_type_map.get(c_view_name))
//					{
//						c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//						
//						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, true);
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
//					else
//					{
//						String []curr_c_unit = c_unit_str[j].split("\\(")[1].split("\\)");
//												
//						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
//						
//						if(curr_c_unit.length == 0)
//						{
//							int [] final_pos = new int[1];
//							
//							String t_name = curr_c_view.lambda_terms.get(0).table_name;
//							
//							Vector<Integer> positions = table_pos_map.get(t_name);
//							
//							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(0).name);
//							
//							Vector<Integer> ids = new Vector<Integer>();
//							
//							Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
//							
//							for(int p = 0; p<vals.size(); p++)
//							{								
//								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
//								
//								Vector<String> val = new Vector<String>();
//								
//								
//								val.add(vals.get(p));
//								
//								inserted_view.put_paramters(val);
//								
//								inserted_view.lambda_terms.get(p).id = ids.get(p);
//								
//								c_view.add(inserted_view);
//							}							
//						}
//						else
//						{
//							String [] c_v_unit = curr_c_unit[0].split(",");
//													
//							Vector<Vector<String>> values = new Vector<Vector<String>>();
//							
//							Vector<Vector<Integer>> id_arrays = new Vector<Vector<Integer>>();
//							
//							for(int p = 0; p<c_v_unit.length; p++)
//							{								
//								if(c_v_unit[p].isEmpty())
//								{
//									int [] final_pos = new int[1];
//									
//									String t_name = curr_c_view.lambda_terms.get(p).table_name;
//									
//									Vector<Integer> positions = table_pos_map.get(t_name);
//									
//									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
//									
//									Vector<Integer> ids = new Vector<Integer>();
//									
//									Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
//									
//									values = update_views(values, vals, id_arrays, ids);
//									
//									
//								}
//								else
//								{
//									
//									String t_name = curr_c_view.lambda_terms.get(0).table_name;
//									
//									Vector<Integer> positions = table_pos_map.get(t_name);
//									
//									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
//									
//									int[] final_pos = new int[1];
//									
//									Vector<Integer> ids = new Vector<Integer>();
//									
//									get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
//									
//									id_arrays.add(ids);
//									
//									if(!values.isEmpty())
//									{
//										for(int t = 0; t<values.size(); t++)
//										{
//											Vector<String> v = values.get(t);
//											
//											v.add(c_v_unit[p]);
//										}
//									}
//									else
//									{
//										Vector<String> v= new Vector<String>();
//										
//										v.add(c_v_unit[p]);
//										
//										values.add(v);
//									}
//									
//									
//								}
//							}
//							
//							for(int p = 0; p<values.size(); p++)
//							{
//								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
//								
//								Vector<String> val = values.get(p);
//								
//								Vector<Integer> ids = id_arrays.get(p);
//															
//								inserted_view.put_paramters(val);
//								
//								for(int w = 0; w < ids.size(); w++)
//								{
//									inserted_view.lambda_terms.get(w).id = ids.get(w);
//								}
//								
//								c_view.add(inserted_view);
//							}
//							
//						}
//						
//						
//						
////						HashMap<String, Integer> lambda_term_map = view_lambda_term_map.get(c_view_name);
////						
////						Set name_set = lambda_term_map.entrySet();
////						
////						for(Iterator iter = name_set.iterator(); iter.hasNext(); )
////						{
////							
////						}
////						
////						if(curr_c_unit.isEmpty())
////						{
////							
////						}
////						else
////						{
////							String [] curr_c_units = curr_c_unit.split(",");
////							
////							for(int t = 0; t<curr_c_units.length; t++)
////							{
////								if(curr_c_units)
////							}
////							
////							if(curr_c_units)
////							
////						}
//						
//					}
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
	
	static Vector<String []> update_values(Vector<String []> values, Vector<String> v, int pos, int size, Vector<int[]> value_ids, Vector<Integer> curr_id_vec)
	{
		Vector<String[]> update_values = new Vector<String[]>();
		
		Vector<int[]> update_id_values = new Vector<int[]>();
				
		if(values.isEmpty())
		{
			
			for(int i = 0; i<v.size(); i++)
			{
				String [] strs = new String[size];
				
				int[] ids = new int[size];
				
				strs[0] = v.get(i);
				
				ids[0] = curr_id_vec.get(i);
				
				update_values.add(strs);
				
				value_ids.add(ids);
			}
						
			return update_values;
		}
		
		
		for(int i = 0; i<values.size(); i++)
		{
			
			String[] value = values.get(i);
			
			int [] ids = value_ids.get(i);
						
			for(int j = 0; j<v.size(); j++)
			{
				String[] curr_values =new String[value.length];
				
				int [] curr_ids = new int[value.length];
							
				System.arraycopy(value, 0, curr_values, 0, value.length);//curr_values.addAll(value);
				
				System.arraycopy(ids, 0, curr_values, 0, value.length);
								
				curr_values[pos] = v.get(j);
				
				curr_ids[pos] = curr_id_vec.get(j);
				
				update_id_values.add(curr_ids);
												
				update_values.add(curr_values);
			}
		}
		
		
		value_ids = update_id_values;
				
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
	
//	public static Vector<Vector<citation_view>> get_citation_units_condition(Vector<String[]> c_units, Vector<HashMap<String, boolean[]>> conditions_all, HashMap<String, Vector<Integer>> table_pos_map, ResultSet rs, int start_pos) throws ClassNotFoundException, SQLException
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
//					if(citation_condition_id_map.get(c_view_name)!=null)
//					{
//						boolean []b_vals = conditions_all.get(i).get(c_view_name);
//						
//						int k = 0;
//						
//						
//						for(k = 0; k<b_vals.length; k++)
//						{
//							if(b_vals[k] == false)
//								break;
//						}
//						
//						if(k < b_vals.length)
//							continue;
//						
//						
//					}
//					
//					Vector<String> c_view_paras = new Vector<String>();
//					
//					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//					
//					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
//					
//					int k = 0;
//					
//					for(k = 0; k<c_view_paras.size(); k++)
//					{
//						
//						String t_name = curr_c_view.lambda_terms.get(k).table_name;
//						
//						Vector<Integer> positions = table_pos_map.get(t_name);
//						
//						int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(k).name);
//						
//						int[] final_pos = new int[1];
//						
//						if(check_value(c_view_paras.get(k), start_pos, positions, rs, offset, final_pos))
//						{
//							
//							curr_c_view.lambda_terms.get(k).id = final_pos[0];
//							
//							continue;
//						}
//						else
//							break;
//						
//					}
//					
//					if(k < c_view_paras.size())
//						continue;
//					
//					
//					curr_c_view.put_paramters(c_view_paras);
//
//					
////					curr_c_view.lambda_terms.
//					
//					c_view.add(curr_c_view);
//				}
//				else
//				{
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
//	
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
	
	
//	public static Vector<citation_view_vector> get_valid_citation_combination(Vector<Vector<citation_view>>c_unit_vec, Vector<String> subgoal_names, HashMap<String, Tuple> curr_map)
//	{
//		HashMap<String, citation_view_vector> c_combinations = new HashMap<String, citation_view_vector>();
//		
//		Vector<Vector<String>> c_subgoal_names = new Vector<Vector<String>>();
//		
//		for(int i = 0; i<c_unit_vec.size(); i++)
//		{
//			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
//			
//			Vector<citation_view> curr_combination = new Vector<citation_view>();
//			
//			Vector<Vector<String>> curr_subgoal_name = new Vector<Vector<String>>();
//			
//			//check validation
//			for(int j = 0; j < curr_c_unit_vec.size(); j++)
//			{
//				citation_view curr_c_unit = curr_c_unit_vec.get(j);
//				
//				Vector<String> core_str = new Vector<String>();
//				
//				if(curr_map.get(curr_c_unit.get_name()) == null)
//					continue;
//				
////				Vector<Subgoal> core_set = curr_map.get(curr_c_unit.get_name()).core;
////				
////				for(int r = 0; r<core_set.size(); r++)
////				{
////					Subgoal subgoal = core_set.get(r);
////					
////					core_str.add(subgoal.name);
////				}
//				
//				HashSet core_set = curr_map.get(curr_c_unit.get_name()).core;
//				
//				for(Iterator iter = core_set.iterator(); iter.hasNext();)
//				{
//					Subgoal subgoal = (Subgoal) iter.next();
//					
//					core_str.add(subgoal.name);
//				}
//
//				
//				if(subgoal_names.containsAll(curr_c_unit.get_table_names()) && core_str.contains(subgoal_names.get(i)))
//				{
//					curr_combination.add(curr_c_unit);
//					curr_subgoal_name.add(curr_c_unit.get_table_names());
//				}
//			}
//			
//			c_combinations = join_operation(c_combinations, curr_combination, i);
//			
////			c_combinations = remove_duplicate(c_combinations);
//		}
//		
//		return remove_duplicate_final(c_combinations);
//	}
	
	
//	public static HashMap<String, citation_view_vector> remove_duplicate(HashMap<String, citation_view_vector> c_combinations)
//	{
////		Vector<Boolean> retains = new Vector<Boolean>();
//		
////		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
//		
//		HashMap<String, citation_view_vector> update_combinations = new HashMap<String, citation_view_vector>();
//		
//		Set set = c_combinations.keySet();
//		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
//		{
//			String str = (String) iter.next();
//			
//			boolean contains = false;
//			
//			citation_view_vector c_combination = c_combinations.get(str);
//			
//			for(Iterator iterator = set.iterator(); iterator.hasNext();)
//			{
//				String string = (String) iterator.next();
//				
//				if(str!=string)
//				{
//					citation_view_vector curr_combination = c_combinations.get(string);
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
//				update_combinations.put(str, c_combination);
//		}
//		
//				
//		return update_combinations;
//	}
	
//	public static Vector<citation_view_vector> remove_duplicate_final(HashMap<String, citation_view_vector> c_combinations)
//	{
////		Vector<Boolean> retains = new Vector<Boolean>();
//		
////		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
//		
//		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
//		
//		Set set = c_combinations.keySet();
//		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
//		{
//			String str = (String) iter.next();
//			
//			boolean contains = false;
//			
//			citation_view_vector c_combination = c_combinations.get(str);
//			
//			for(Iterator iterator = set.iterator(); iterator.hasNext();)
//			{
//				String string = (String) iterator.next();
//				
//				if(str!=string)
//				{
//					citation_view_vector curr_combination = c_combinations.get(string);
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
	
//	public static HashMap<String, citation_view_vector> join_operation(HashMap<String, citation_view_vector> c_combinations, Vector<citation_view> insert_citations,  int i)
//	{
//		if(i == 0)
//		{
//			for(int k = 0; k<insert_citations.size(); k++)
//			{
//				Vector<citation_view> c = new Vector<citation_view>();
//				
//				c.add(insert_citations.get(k));
//				
//				String str = String.valueOf(insert_citations.get(k).get_index());
//				
//				c_combinations.put(str, new citation_view_vector(c));
//				
//			}
//			
//			return c_combinations;
//		}
//		else
//		{
//			HashMap<String, citation_view_vector> updated_c_combinations = new HashMap<String, citation_view_vector>();
//			
//			Vector<Vector<String>> updated_c_subgoals = new Vector<Vector<String>>();
//			
//			Set set = c_combinations.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
//			{
//				String string = (String) iter.next();
//				
//				citation_view_vector curr_combination = c_combinations.get(string);
//								
//				for(int k = 0; k<insert_citations.size(); k++)
//				{
//					
//					citation_view_vector new_citation_vec = citation_view_vector.merge(curr_combination, insert_citations.get(k));
//					
//					ArrayList<String> str_list = new_citation_vec.index_vec;
//					
//					Collections.sort(str_list, String.CASE_INSENSITIVE_ORDER);
//					
//					String str = new String();
//					
//					for(int r = 0; r < str_list.size(); r++)
//					{
//						str += str_list.get(r);
//					}
//					
//						
//					
//					
//					updated_c_combinations.put(str, new_citation_vec);
//					
////					if(insert_citations.get(k).get_table_names().containsAll(curr_subgoal))
////					{
////						Vector<citation_view> c = new Vector<citation_view>();
////						
////						c.add(insert_citations.get(k));
////						
////						updated_c_combinations.add(c);
////						
////						updated_c_subgoals.add(insert_citations.get(k).get_table_names());
////					}
////					else
////					{
////						if(curr_subgoal.containsAll(insert_citations.get(k).get_table_names()))
////						{
////							updated_c_combinations.add((Vector<citation_view>) curr_combination.clone());
////							
////							updated_c_subgoals.add(curr_subgoal);
////						}
////						
////						else
////						{
////							Vector<citation_view> c = new Vector<citation_view>();
////							
////							Vector<String> str_vec = new Vector<String>();
////							
////							c = (Vector<citation_view>) curr_combination.clone();
////							
////							str_vec = (Vector<String>) curr_subgoal.clone();
////							
////							str_vec.addAll(insert_citations.get(k).get_table_names());
////							
////							c.add(insert_citations.get(k));
////							
////							updated_c_combinations.add(c);
////							
////							updated_c_subgoals.add(str_vec);
////						}
////					}
//				}
//			}
//						
//			return updated_c_combinations;
//			
//		}
//	}
	
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

}
