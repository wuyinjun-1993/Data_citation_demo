package edu.upenn.cis.citation.aggregation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.sparql.resultset.ResultSetMem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test_opt;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.reasoning1.schema_reasoning;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_min_test;

public class Aggregation5 {
	
	public static ArrayList<HashMap<String, HashSet<String>>> full_citations = new ArrayList<HashMap<String, HashSet<String>>>();
	
	public static HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
	
	public static ArrayList<Covering_set> curr_res = new ArrayList<Covering_set>();
	
	static ArrayList<HashSet<Head_strs>> lambda_values = new ArrayList<HashSet<Head_strs>>();
	
	public static void clear()
	{
	  full_citations.clear();
	  
	  view_author_mapping.clear();
	  
	  curr_res.clear();
	  
	  lambda_values.clear();
	}
	
//	public static void do_aggregate(ArrayList<citation_view_vector> curr_res, ArrayList<citation_view_vector> c_views, int seq, ArrayList<HashMap<String, HashSet<String>> > author_list, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, StringList view_list, HashMap<String, Boolean> full_flag, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
//	{
//		
//		if(seq == 0)
//		{
//			
//			curr_res.addAll(c_views);
//			
//			for(int i = 0; i<c_views.size(); i++)
//			{
//				HashMap<String, HashSet<String>> json_citation = gen_citation1.join_covering_sets(c_views.get(i), c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
//				
//				author_list.add(json_citation);
//				
//			}
//			
//			
//			return;
//			
//		}
//				
//		int i1 = 0;
//		
//		int i2 = 0;
//		
//		for(int i = 0; i<curr_res.size(); i++)
//		{
//			
//			String id1 = curr_res.get(i).view_name_str;
//			
//			int j = 0;
//			
//			citation_view_vector c_vector = null;
//			
//			for(j = 0; j<c_views.size(); j++)
//			{
//				String id2 = c_views.get(j).view_name_str;
//				
//				c_vector = c_views.get(j);
//				
//				if(id1.equals(id2))
//				{
//					break;
//				}
//			}
//			
//			if(j < c_views.size())
//			{				
//							
//				HashMap<String, HashSet<String>> curr_authors = author_list.get(i);
//				
//				if(check_block_full(curr_authors, max_num, full_flag))
//					continue;
//				
//				HashMap<String, HashSet<String>> authors = gen_citation1.join_covering_sets(c_vector, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);				
//				
////				System.out.println(c_vector + ":::::::::::" + authors);
//
//				
//				Set<String> keys = authors.keySet();
//				
//				for(Iterator iter = keys.iterator(); iter.hasNext();)
//				{
//					String key = (String) iter.next();
//					
//					HashSet<String> values = authors.get(key);
//					
//					if(curr_authors.get(key) == null)
//					{
//						curr_authors.put(key, values);
//					}
//					else
//					{
//						
//						curr_authors.get(key).addAll(values);
//					}
//				}
//				
//				author_list.set(i, curr_authors);
//				
////				for(int k = 0; k<c_vector.c_vec.size(); k++)
////				{
////					citation_view c_v = c_vector.c_vec.get(k);
////					
////					
////					int view_index = view_list.find(c_vector.c_vec.get(i).get_name());
////					
////					HashMap<String, HashSet<String>> authors = null;
////					
////					if(c_v.has_lambda_term())
////					{
////						authors = gen_citation1.get_authors3((citation_view_parametered)c_v, c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
////						
////						author_list.get(i).addAll(authors);
////					}
////					else
////					{
////						authors = gen_citation1.get_authors3((citation_view_unparametered)c_v, c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
////						
////						author_list.get(i).addAll(authors);
////					}
////					
////					for(Iterator iter = authors.iterator(); iter.hasNext() && author_list.get(i).size() <= max_num;)
////					{
////						author_list.get(i).add((String) iter.next());
////					}
////				}
//				
//				
//			}
//			else
//			{
//			
//				curr_res.remove(i);
//				
//				author_list.remove(i);
//				
//				i--;
//			}
//		}
//		
//	}
	
	public static void do_aggregate(ArrayList<Covering_set> curr_res, ArrayList<Covering_set> c_views, int seq) throws ClassNotFoundException, SQLException
	{
		
	
		if(seq == 0)
		{
			
			curr_res.addAll(c_views);
			
			return;
			
		}
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			
			String id1 = curr_res.get(i).view_name_str;
			
			String t_id1 = curr_res.get(i).table_name_str;
			
			int j = 0;
						
			for(j = 0; j<c_views.size(); j++)
			{
				String id2 = c_views.get(j).view_name_str;
				
				String t_id2 = c_views.get(j).table_name_str;
								
				if(id1.equals(id2) && t_id1.equals(t_id2))
				{
					break;
				}
			}
			
			if(j >= c_views.size())
			{				
					
				curr_res.remove(i);
				
				i--;			
			}
		}
		
	}
	
	public static void do_aggregate(ArrayList<Covering_set> curr_res, HashSet<Covering_set> c_views, int seq)
	{
		
	
		if(seq == 0)
		{
			
			curr_res.addAll(c_views);
			
			return;
			
		}
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			
			String id1 = curr_res.get(i).view_name_str;
			
			String t_id1 = curr_res.get(i).table_name_str;
			
			int j = 0;
						
			for(Iterator iter = c_views.iterator(); iter.hasNext();)
			{
				
				Covering_set covering_set = (Covering_set) iter.next();
				
				String id2 = covering_set.view_name_str;
				
				String t_id2 = covering_set.table_name_str;
								
				if(id1.equals(id2) && t_id1.equals(t_id2))
				{
					break;
				}
				
				j++;
			}
			
			if(j >= c_views.size())
			{				
					
				curr_res.remove(i);
				
				i--;			
			}
		}
		
	}
	
//	public static void do_aggregate(citation_view_vector curr_res, citation_view_vector c_views, int seq, ArrayList<HashMap<String, HashSet<String>> > author_list, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, StringList view_list, HashMap<String, Boolean> full_flag, HashMap<String, String> view_citation_mapping, boolean tuple_level, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
//	{
//		
//	
//		if(seq == 0)
//		{			
////			for(int i = 0; i<c_views.size(); i++)
//			{
//				HashMap<String, HashSet<String>> json_citation = gen_citation1.join_covering_sets(c_views, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
//				
//				author_list.add(json_citation);
//				
//				view_citation_mapping.put(c_views.toString(), json_citation.toString());
//				
//				curr_res
//				
//			}
//			
//			
//			return;
//			
//		}
//				
//		int i1 = 0;
//		
//		int i2 = 0;
//		
//		for(int i = 0; i<curr_res.size(); i++)
//		{
//			
//			String id1 = curr_res.get(i).view_name_str;
//			
//			String t_id1 = curr_res.get(i).table_name_str;
//			
//			int j = 0;
//			
//			citation_view_vector c_vector = null;
//			
//			for(j = 0; j<c_views.size(); j++)
//			{
//				String id2 = c_views.get(j).view_name_str;
//				
//				String t_id2 = c_views.get(j).table_name_str;
//				
//				c_vector = c_views.get(j);
//				
//				if(id1.equals(id2) && t_id1.equals(t_id2))
//				{
//					break;
//				}
//			}
//			
//			if(j < c_views.size())
//			{				
//							
//				HashMap<String, HashSet<String>> curr_authors = author_list.get(i);
//				
//				if(check_block_full(curr_authors, max_num, full_flag))
//					continue;
//				
//				HashMap<String, HashSet<String>> authors = gen_citation1.join_covering_sets(c_vector, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);				
//
//				view_citation_mapping.put(c_vector.toString(), authors.toString());
//
//				
//				Set<String> keys = authors.keySet();
//				
//				for(Iterator iter = keys.iterator(); iter.hasNext();)
//				{
//					String key = (String) iter.next();
//					
//					HashSet<String> values = authors.get(key);
//					
//					if(curr_authors.get(key) == null)
//					{
//						curr_authors.put(key, values);
//					}
//					else
//					{
//						
//						curr_authors.get(key).addAll(values);
//					}
//				}
//				
//				author_list.set(i, curr_authors);
//				
//			}
//			else
//			{
//			
//				curr_res.remove(i);
//				
//				author_list.remove(i);
//				
//				i--;
//			}
//		}
//		
//	}
//	
	static boolean check_block_full(HashMap<String, HashSet<String>> curr_authors, HashMap<String, Integer> max_num, HashMap<String, Boolean> full_flag)
	{
		Set<String> keys = curr_authors.keySet();
		
		boolean full_filled = true;
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String key = (String) iter.next();
			
			if(full_flag.get(key))
			{				
				continue;
			}
			
			if(max_num.get(key) == null)
			{
				full_filled = full_filled & false;
				
				continue;
			}
			
			int curr_max_num = max_num.get(key);
			
			if(curr_max_num <= 0)
			{
				full_filled = full_filled & false;
				
				continue;
			}
			
			HashSet<String> curr_values = curr_authors.get(key);
			
			if(curr_max_num > 0 && curr_values.size() > curr_max_num)
			{
				
				full_filled = full_filled & true;
				
				full_flag.put(key, true);
			}
			else
			{
				full_filled = full_filled & false;
			}
		}
		
		return full_filled;
	}
	
	public static ArrayList<Integer> get_all_selected_row_ids(ArrayList<Head_strs> heads, HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping)
	{
		ArrayList<Integer> all_row_ids = new ArrayList<Integer>();
		
		for(int i = 0; i<heads.size(); i++)
		{
			all_row_ids.addAll(head_strs_rows_mapping.get(heads.get(i)));
		}
		
		
		
		Collections.sort(all_row_ids);
		
		return all_row_ids;
	}
	
	public static HashSet<String> do_agg_intersection(ResultSet rs, HashMap<int[], ArrayList<Covering_set> > c_view_map, int start_pos, ArrayList<Head_strs> heads, HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		HashMap<String, Boolean> full_flag = new HashMap<String, Boolean>();
		
		for(int i = 0; i<populate_db.available_block.length; i++)
		{
			full_flag.put(populate_db.available_block[i], false);
		}
		
		ArrayList<Integer> all_row_ids = get_all_selected_row_ids(heads, head_strs_rows_mapping);
		
		Set<int[]> intervals = c_view_map.keySet();
		
		int index = 0;
		
		rs.beforeFirst();
		
		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		ArrayList<Covering_set> curr_res = new ArrayList<Covering_set>();
				
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			ArrayList<Covering_set> c_view = c_view_map.get(interval);
			
			for(int i = index; i < all_row_ids.size(); i ++)
			{				
				if(all_row_ids.get(i) < interval[1] && all_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(all_row_ids.get(i) + 1);
					
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
						
					do_aggregate(curr_res, c_view, i);
					
//					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
				}
				
				
				if(all_row_ids.get(index) >= interval[1])
				{
					index = i;
					
					break;
				}
			}
		}
		
		
		return gen_citations(author_lists, max_num);
		
	}
	
	static HashSet<String> gen_citations(ArrayList<HashMap<String, HashSet<String>>> author_lists, HashMap<String, Integer> max_num)
	{
		HashSet<String> json_string = new HashSet<String>();
			
		for(int i = 0; i<author_lists.size(); i++)
		{
//			boolean empty = false;
			
			HashMap<String, HashSet<String>> authors = author_lists.get(i);
			
			JSONObject json = gen_citation1.get_json_citation(authors);
			
			if(json == null)
				continue;
		
			json_string.add(json.toString());
		}
		
		return json_string;
	}
	
//	public static Vector<String> do_agg_intersection(Vector<Head_strs> heads, HashMap<Head_strs, ArrayList<ArrayList<citation_view_vector>>> heads_citation_views, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
//	{
//		
//		ArrayList<citation_view_vector> curr_res = new ArrayList<citation_view_vector>();
//		
//		Vector<HashSet<String>> author_lists = new Vector<HashSet<String>>();
//		
//		int seq = 0;
//		
//		for(int i = 0; i<heads.size(); i++)
//		{
//			ArrayList<ArrayList<citation_view_vector>> citation_views = heads_citation_views.get(heads.get(i));
//			
//			for(int j = 0; j<citation_views.size(); j++)
//			{
//				do_aggregate(curr_res, citation_views.get(j), seq, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping, , c, pst);
//				
//				seq ++;
//			}
//			
//		}
//		
//		return gen_citations(author_lists, max_num);
//	}
//	
	
	//query_ids, view_list, 
	public static HashSet<String> do_agg_intersection(ResultSet rs, HashMap<int[], ArrayList<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		HashMap<String, Boolean> full_flag = new HashMap<String, Boolean>();
		
		for(int i = 0; i<populate_db.available_block.length; i++)
		{
			full_flag.put(populate_db.available_block[i], false);
		}
		
		Set<int[]> intervals = c_view_map.keySet();
				
		rs.beforeFirst();
		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		int i = 0;
						
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			ArrayList<Covering_set> c_view = c_view_map.get(interval);
			
//			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(i + 1);
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
					
					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
			
			i++;
		}
		
		ArrayList<String> view_keys = new ArrayList<String>();
		
		ArrayList<citation_view> single_views = get_single_citation_views(curr_res, view_keys);
		
		ArrayList<String> single_view_names = get_citation_view_names(single_views);
						
		int tuple_num = (tuple_level) ? Tuple_reasoning1_full_test_opt.tuple_num : Tuple_reasoning2_full_test2.tuple_num;
		
		for(i = 0; i<tuple_num; i++)
		{
			rs.absolute(i + 1);
			
			if(tuple_level)
				Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values);
			else
				Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values);
			
//			convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
			
			
		}
		
		
		
		ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, false, c, pst);
				
		full_citations = gen_citations_covering_set_level(citations, curr_res, single_view_names, view_keys);
		
		return gen_citations(full_citations, max_num);
		
//		return new HashSet<String>();
	}
	
	public static HashSet<String> do_agg_intersection2(ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, JSONException
	{
		
		
		
		
		return gen_citation_entire_query(rs, tuple_level, false, curr_res, start_pos, max_num, c, pst);
//		return new HashSet<String>();
	}
	
	public static HashSet<String> do_agg_intersection_subset(ResultSet rs, ArrayList<Integer> tuple_ids, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, JSONException
    {
        
        
        
        
        return gen_citation_subset(rs, tuple_ids, tuple_level, false, curr_res, start_pos, max_num, c, pst);
//      return new HashSet<String>();
    }
	
	public static HashSet<String> do_agg_intersection0(ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		Set<int[]> intervals = c_view_map.keySet();
		
		rs.beforeFirst();
		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		int i = 0;
						
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			HashSet<Covering_set> c_view = c_view_map.get(interval);
			
//			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(i + 1);
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
					
					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
			
			i++;
		}
		
		
		return gen_citation_entire_query(rs, tuple_level, false, curr_res, start_pos, max_num, c, pst);
//		return new HashSet<String>();
	}
	
	public static HashSet<String> gen_citation_entire_query(ResultSet rs, boolean tuple_level, boolean schema_level, ArrayList<Covering_set> covering_sets, int start_pos, HashMap<String, Integer> max_num, Connection c, PreparedStatement pst) throws SQLException
	{
		ArrayList<String> view_keys = new ArrayList<String>();
		
		ArrayList<citation_view> single_views = get_single_citation_views(covering_sets, view_keys);
		
		ArrayList<String> single_view_names = get_citation_view_names(single_views);
						
		int tuple_num = 0;
		
		if(tuple_level)
		{
			tuple_num = Tuple_reasoning1_full_test_opt.tuple_num;
		}
		else
		{
			if(!schema_level)
			{
				tuple_num = Tuple_reasoning2_full_test2.tuple_num;
			}
			else
			{
				tuple_num = schema_reasoning.tuple_num;
			}
		}
		
		for(int i = 0; i<tuple_num; i++)
		{
			rs.absolute(i + 1);
			
			if(tuple_level)
				Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values);
			else
			{
				if(!schema_level)
					Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values);
				else
					schema_reasoning.get_views_parameters(single_views, rs, start_pos, lambda_values);
			}
			
//			convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
			
			
		}
		
		
		
		ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, schema_level, c, pst);
				
		full_citations = gen_citations_covering_set_level(citations, covering_sets, single_view_names, view_keys);
		
		return gen_citations(full_citations, max_num);
	}

	public static HashSet<String> gen_citation_subset(ResultSet rs, ArrayList<Integer> id_list, boolean tuple_level, boolean schema_level, ArrayList<Covering_set> covering_sets, int start_pos, HashMap<String, Integer> max_num, Connection c, PreparedStatement pst) throws SQLException
    {
        ArrayList<String> view_keys = new ArrayList<String>();
        
        ArrayList<citation_view> single_views = get_single_citation_views(covering_sets, view_keys);
        
        ArrayList<String> single_view_names = get_citation_view_names(single_views);
                        
//        int tuple_num = 0;
//        
//        if(tuple_level)
//        {
//            tuple_num = Tuple_reasoning1_full_test_opt.tuple_num;
//        }
//        else
//        {
//            if(!schema_level)
//            {
//                tuple_num = Tuple_reasoning2_full_test2.tuple_num;
//            }
//            else
//            {
//                tuple_num = schema_reasoning.tuple_num;
//            }
//        }
        
        for(int i = 0; i<id_list.size(); i++)
        {
            rs.absolute(id_list.get(i));
            
            if(tuple_level)
                Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values);
            else
            {
                if(!schema_level)
                    Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values);
                else
                    schema_reasoning.get_views_parameters(single_views, rs, start_pos, lambda_values);
            }
            
//          convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
            
            
        }
        
        
        
        ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, schema_level, c, pst);
                
        full_citations = gen_citations_covering_set_level(citations, covering_sets, single_view_names, view_keys);
        
        return gen_citations(full_citations, max_num);
    }
	
	public static ArrayList<Integer> get_valid_tuple_ids(HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping, Vector<Head_strs> names)
	{
	  
	  ArrayList<Integer> valid_id_lists = new ArrayList<Integer>();
	  
	  for(int i = 0; i<names.size(); i++)
	  {
//	    Head_strs head_str = new Head_strs(names.get(i));
	    
	    ArrayList<Integer> id_lists = head_strs_rows_mapping.get(names.get(i));
	    
	    valid_id_lists.addAll(id_lists);
	  }
	  
	  Collections.sort(valid_id_lists, new IntegerComparator());
	  
	  return valid_id_lists;
	  
	}
	
	static class IntegerComparator implements Comparator<Integer> {
      public int compare(Integer o1, Integer o2) {
          int result = o1 - o2;

          if (result == 0) {
              return (o1 < o2) ? -1 : 1;
          } else {
              return result;
          }
      }
  }
	
	public static void cal_covering_set_schema_level_intersection(ResultSet rs, ArrayList<Integer> tuple_ids, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException
    {
	  
	    curr_res.clear();
	  
        Set<int[]> intervals = c_view_map.keySet();
        
        rs.beforeFirst();
        
//      ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
        
        int i = 0;
            
        Iterator iter = intervals.iterator();
        
        int [] interval = null;
        
        int group_num = 0;
        
        while(iter.hasNext())
        {
          interval = (int[]) iter.next();
          
          boolean match = false;
          
          while(tuple_ids.get(i) >= interval[0] && tuple_ids.get(i) < interval[1])
          {
            match = true;
            
            i++;
            
            if(i >= tuple_ids.size())
              break;
          }
          
          if(match)
          {
            HashSet<Covering_set> c_view = c_view_map.get(interval);
            
            do_aggregate(curr_res, c_view, group_num);
            
            group_num ++;
          }
          
          if(i >= tuple_ids.size())
            break;
        }
          
    }

	
	public static void cal_covering_set_schema_level_intersection(ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException
	{
		Set<int[]> intervals = c_view_map.keySet();
		
		rs.beforeFirst();
		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		int i = 0;
						
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			HashSet<Covering_set> c_view = c_view_map.get(interval);
			
//			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(i + 1);
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
					
					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
			
			i++;
		}
	}

	
	public static int[] count_view_mapping_predicates_lambda_terms(HashSet<Tuple> tuples)
	{
		
		HashSet<String> view_mapping_string = new HashSet<String>();
		
		HashSet<String> lambda_string = new HashSet<String> ();
		
		HashSet<String> predicate_string = new HashSet<String>();
		
		for(Iterator iter = tuples.iterator(); iter.hasNext();)
		{
			Tuple tuple = (Tuple) iter.next();
			
			Vector<Conditions> conditions = tuple.conditions;
			
			Vector<Lambda_term> l_terms = tuple.lambda_terms;
			
			for(int k = 0; k<conditions.size(); k++)
			{
				predicate_string.add(conditions.get(k).toString());
			}
			
			for(int k = 0; k<l_terms.size(); k++)
			{
				lambda_string.add(l_terms.get(k).toString());
			}
		}
		
//		for(int i = 0; i<curr_res.size(); i++)
//		{
//			citation_view_vector covering_set = curr_res.get(i);
//			
//			for(int j = 0; j<covering_set.c_vec.size(); j++)
//			{
//				citation_view v = covering_set.c_vec.get(j);
//				
//				String view_name = v.get_name();
//				
//				String table_names = v.get_table_name_string();
//				
//				String index_string = view_name + populate_db.separator + table_names;
//				
//				
//				Vector<Conditions> conditions = v.get_view_tuple().conditions;
//				
//				Vector<Lambda_term> l_terms = v.get_view_tuple().lambda_terms;
//				
//				for(int k = 0; k<conditions.size(); k++)
//				{
//					predicate_string.add(conditions.get(k).toString());
//				}
//				
//				for(int k = 0; k<l_terms.size(); k++)
//				{
//					lambda_string.add(l_terms.get(k).toString());
//				}
//				
////				if(view_mapping_string.contains(index_string))
//				{
//					view_mapping_string.add(index_string);
//				}
//			}
//		}
		
		int [] nums = new int[3];
		
		nums[0] = tuples.size();
		
		nums[1] = lambda_string.size();
		
		nums[2] = predicate_string.size();
		
		return nums;
	}
	
	public static ArrayList<HashSet<citation_view>> cal_covering_set_schema_level_union(ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException
	{
		Set<int[]> intervals = c_view_map.keySet();
		
		rs.beforeFirst();
		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		int i = 0;
		
		int tuple_num = (tuple_level) ? Tuple_reasoning1_full_test_opt.tuple_num : Tuple_reasoning2_full_test2.tuple_num;

		int group_num = (tuple_level) ? Tuple_reasoning1_full_test_opt.group_num : Tuple_reasoning2_full_test2.group_num;
		
		HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
		
						
		ArrayList<HashSet<citation_view>> views_per_group = new ArrayList<HashSet<citation_view>>();
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			HashSet<Covering_set> c_view = c_view_map.get(interval);
			
			HashSet<citation_view> views = new HashSet<citation_view>();
			
			for(Iterator it = c_view.iterator(); it.hasNext();)
			{
				Covering_set view = (Covering_set) it.next();
				
				views.addAll(view.c_vec);
			}
			
			views_per_group.add(views);
			
			all_covering_sets.addAll(c_view);
//			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(i + 1);
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
					
//					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
		}
		
		curr_res.addAll(all_covering_sets);
		
		return views_per_group;
	}
	
	public static HashSet<String> do_agg_union(ArrayList<HashSet<citation_view>> views_per_group, ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, JSONException
	{
		
		Set<int[]> intervals = c_view_map.keySet();
		
		ArrayList<String> view_keys = new ArrayList<String>();
		
		ArrayList<citation_view> single_views = get_single_citation_views(curr_res, view_keys);
		
		ArrayList<String> single_view_names = get_citation_view_names(single_views);
				
		initialize_view_lambda_sets(lambda_values, single_views.size());
				
		int num = 0;
		
		int i = 0;
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
						
			ArrayList<Integer> view_ids = get_valid_view_id(single_views, views_per_group.get(num));
			
			for(i = interval[0]; i < interval[1]; i ++)
			{
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
					rs.absolute(i + 1);
					
					if(tuple_level)
						Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values, view_ids);
					else
						Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values, view_ids);
					
//					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
			
			num ++;
			
		}
		
		
//		for(i = 0; i<tuple_num; i++)
//		{
//			rs.absolute(i + 1);
//			
//			if(tuple_level)
//				Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values);
//			else
//				Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values);
//			
////			convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
//			
//			
//		}
		
		
		
		ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, false, c, pst);
				
		full_citations = gen_citations_covering_set_level(citations, curr_res, single_view_names, view_keys);
		
		return gen_citations(full_citations, max_num);
		
//		return new HashSet<String>();
	}
	
	public static HashSet<String> do_agg_union0(ResultSet rs, HashMap<int[], HashSet<Covering_set> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		Set<int[]> intervals = c_view_map.keySet();
		
		rs.beforeFirst();
		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
		
		int i = 0;
		
		int tuple_num = (tuple_level) ? Tuple_reasoning1_full_test_opt.tuple_num : Tuple_reasoning2_full_test2.tuple_num;

		int group_num = (tuple_level) ? Tuple_reasoning1_full_test_opt.group_num : Tuple_reasoning2_full_test2.group_num;
		
		HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
		
						
		ArrayList<HashSet<citation_view>> views_per_group = new ArrayList<HashSet<citation_view>>();
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			HashSet<Covering_set> c_view = c_view_map.get(interval);
			
			HashSet<citation_view> views = new HashSet<citation_view>();
			
			for(Iterator it = c_view.iterator(); it.hasNext();)
			{
				Covering_set view = (Covering_set) it.next();
				
				views.addAll(view.c_vec);
			}
			
			views_per_group.add(views);
			
			all_covering_sets.addAll(c_view);
//			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
//					rs.absolute(i + 1);
					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
					
//					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
		}
		
		curr_res.addAll(all_covering_sets);
		
		ArrayList<String> view_keys = new ArrayList<String>();
		
		ArrayList<citation_view> single_views = get_single_citation_views(curr_res, view_keys);
		
		ArrayList<String> single_view_names = get_citation_view_names(single_views);
				
		initialize_view_lambda_sets(lambda_values, single_views.size());
				
		int num = 0;
				
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
						
			ArrayList<Integer> view_ids = get_valid_view_id(single_views, views_per_group.get(num));
			
			for(i = interval[0]; i < interval[1]; i ++)
			{
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
					rs.absolute(i + 1);
					
					if(tuple_level)
						Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values, view_ids);
					else
						Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values, view_ids);
					
//					do_aggregate(curr_res, c_view, i);
					
//					System.out.println(curr_res);
				}
			}
			
			num ++;
			
		}
		
		
//		for(i = 0; i<tuple_num; i++)
//		{
//			rs.absolute(i + 1);
//			
//			if(tuple_level)
//				Tuple_reasoning1_full_test_opt.get_views_parameters(single_views, rs, start_pos, lambda_values);
//			else
//				Tuple_reasoning2_full_test2.get_views_parameters(single_views, rs, start_pos, lambda_values);
//			
////			convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
//			
//			
//		}
		
		
		
		ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, false, c, pst);
				
		full_citations = gen_citations_covering_set_level(citations, curr_res, single_view_names, view_keys);
		
		return gen_citations(full_citations, max_num);
		
//		return new HashSet<String>();
	}
	
	public static int cal_distinct_views()
	{
		int size = 0;
		
		for(int i = 0; i < lambda_values.size(); i++)
		{
			if(lambda_values.get(i).size() == 0)
			{
				size += 1;
			}
			else
			{
				size += lambda_values.get(i).size();
			}
		}
		
		return size;
	}
	
	static void initialize_view_lambda_sets(ArrayList<HashSet<Head_strs>> lambda_values, int size)
	{
		for(int i = 0; i<size; i++)
		{
			lambda_values.add(new HashSet<Head_strs>());
		}
	}
	
	static ArrayList<Integer> get_valid_view_id(ArrayList<citation_view> all_views, HashSet<citation_view> curr_views)
	{
		ArrayList<Integer> view_ids = new ArrayList<Integer>();
		
		for(Iterator iter = curr_views.iterator(); iter.hasNext();)
		{
			citation_view view = (citation_view) iter.next();
			
			view_ids.add(all_views.indexOf(view));
		}
		
		return view_ids;
	}
	
	public static ArrayList<String> get_citation_view_names(ArrayList<citation_view> single_views)
	{
		ArrayList<String> names = new ArrayList<String>();
		
		for(int i = 0; i<single_views.size(); i++)
		{
			names.add(single_views.get(i).get_name());
		}
		
		return names;
	}
	
	static ArrayList<HashMap<String, HashSet<String>>> gen_citations_covering_set_level(ArrayList<HashMap<String, HashSet<String>>> citations, ArrayList<Covering_set> curr_res, ArrayList<String> single_view_names, ArrayList<String> view_keys)
	{
		ArrayList<HashMap<String, HashSet<String>>> full_citations = new ArrayList<HashMap<String, HashSet<String>>>();
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			Covering_set c_vector = curr_res.get(i);
			
			HashMap<String, HashSet<String>> curr_full_citations = new HashMap<String, HashSet<String>>();
			
			for(citation_view view_mapping: c_vector.c_vec)
			{
				
				String view_key = view_mapping.get_name() + populate_db.separator + view_mapping.get_table_name_string();
				
				int id = view_keys.indexOf(view_key);
				
				HashMap<String, HashSet<String>> curr_citations = citations.get(id);
				
				if(curr_full_citations.isEmpty())
				{
					Set<String> keys = curr_citations.keySet();
					
					for(Iterator iter = keys.iterator(); iter.hasNext();)
					{
						String key = (String)iter.next();
						
						HashSet<String> curr_values = new HashSet<String>();
						
						curr_values.addAll(curr_citations.get(key));
						
						curr_full_citations.put(key, curr_values);
					}
					
				}
				else
				{
					Set<String> keys = curr_citations.keySet();
					
					for(Iterator iter = keys.iterator(); iter.hasNext();)
					{
						String key = (String)iter.next();
						
						HashSet<String> curr_values = (HashSet<String>) curr_citations.get(key).clone();
						
						if(curr_full_citations.containsKey(key))
						{
							curr_full_citations.get(key).addAll(curr_values);
						}
						else
						{
							curr_full_citations.put(key, curr_values);
						}
					}
				}
			}
			
			full_citations.add(curr_full_citations);
		}
		
		
		
		return full_citations;
	}
	
	static ArrayList<HashMap<String, HashSet<String>>> gen_citation_view_level(ArrayList<citation_view> single_views, ArrayList<HashSet<Head_strs>> lambda_values, boolean tuple_level, boolean schema_level, Connection c, PreparedStatement pst) throws SQLException
	{
		ArrayList<HashMap<String, HashSet<String>>> all_citations = new ArrayList<HashMap<String, HashSet<String>>>();
		
		for(int i = 0; i<single_views.size(); i++)
		{
			HashMap<String, HashSet<String>> citations = gen_citation_view_level(single_views.get(i), lambda_values.get(i), tuple_level, schema_level, c, pst);
			
			all_citations.add(citations);
		}
		
		return all_citations;
		
	}
	
	static HashMap<String, HashSet<String>> gen_citation_view_level(citation_view single_view, HashSet<Head_strs> lambda_values, boolean tuple_level, boolean schema_level, Connection c, PreparedStatement pst) throws SQLException
	{
		HashMap<String, Integer> citation_queries_ids = null;
		
		HashMap<Integer, Query> citation_queries = null;
		
		HashMap<String, HashSet<String>> citations = new HashMap<String, HashSet<String>>();
		
		IntList query_ids = null;
		
		if(tuple_level)
		{
			citation_queries_ids = Tuple_reasoning1_full_test_opt.get_citation_queries(single_view.get_name());
			
			citation_queries = Tuple_reasoning1_full_test_opt.citation_queries;
			
			query_ids = Tuple_reasoning1_full_test_opt.query_ids;
		}
		else
		{
			
			if(!schema_level)
			{
				citation_queries_ids = Tuple_reasoning2_full_test2.get_citation_queries(single_view.get_name());
				
				citation_queries = Tuple_reasoning2_full_test2.citation_queries;
				
				query_ids = Tuple_reasoning2_full_test2.query_ids;
			}
			else
			{
				citation_queries_ids = schema_reasoning.get_citation_queries(single_view.get_name());
				
				citation_queries = schema_reasoning.citation_queries;
				
				query_ids = schema_reasoning.query_ids;
			}
			
		}
		
		Set<String> keys = citation_queries_ids.keySet();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{			
			String block_name = (String)iter.next();
			
			Query q = citation_queries.get(query_ids.list[citation_queries_ids.get(block_name)]);
			
			String query_base = Query_converter.datalog2sql(q);
			
			if(q.lambda_term.size() > 0)
			{
				citation_view_parametered curr_view = (citation_view_parametered) single_view;
				
				Vector<Integer> l_term_ids = new Vector<Integer>();
				
				for(int k = 0; k<q.lambda_term.size(); k++)
				{
					
					int id = curr_view.view.lambda_term.indexOf(q.lambda_term.get(k));
					
					l_term_ids.add(id);
				}
				
				String query = get_full_query_string(query_base, q, lambda_values, l_term_ids);
								
				gen_single_citations(query, block_name, citations, c, pst);
			}
			else
			{
				gen_single_citations(query_base, block_name, citations, c, pst);
			}
		}
		
		return citations;
	}
	
	static String get_full_query_string(String query_base, Query q, HashSet<Head_strs> lambda_values, Vector<Integer> l_term_ids)
	{
		String [] l_values = new String[l_term_ids.size()];
		
		
		
		String query = query_base;
		
		int num = 0;
		
		HashSet<Head_strs> cq_lambda_values = new HashSet<Head_strs>();
		
		for(Iterator iter = lambda_values.iterator(); iter.hasNext();)
		{
			Head_strs head_value = (Head_strs) iter.next();
			
			Vector<String> curr_cq_lambda_values = new Vector<String>();
			
			for(int k = 0; k<l_term_ids.size(); k++)
			{
				curr_cq_lambda_values.add(head_value.head_vals.get(l_term_ids.get(k)));
				
//				if(num >= 1)
//					l_values[k] += ",";
//				
//				if(l_values[k] != null)
//					l_values[k] += "'" + head_value.head_vals.get(l_term_ids.get(k)) + "'";
//				else
//					l_values[k] = "'" + head_value.head_vals.get(l_term_ids.get(k)) + "'";
			}
			
			Head_strs curr_cq_head_strs = new Head_strs(curr_cq_lambda_values);
			
			cq_lambda_values.add(curr_cq_head_strs);
		}
		
		for(Iterator iter = cq_lambda_values.iterator(); iter.hasNext();)
		{
			Head_strs head_value = (Head_strs) iter.next();
						
			for(int k = 0; k<head_value.head_vals.size(); k++)
			{				
				if(num >= 1)
					l_values[k] += ",";
				
				String value = head_value.head_vals.get(k);
				
				if(value!= null && value.contains("'"))
				{
					value = value.replaceAll("'", "''");
				}
				
				
				if(l_values[k] != null)
					l_values[k] += "'" + value + "'";
				else
					l_values[k] = "'" + value + "'";
			}
			
			num ++;
		}
		
		if(q.conditions == null || q.conditions.size() == 0)
		{
			query += " where (";
		}
		else
		{
			query += " and (";
		}
		
		for(int k = 0; k<l_term_ids.size(); k++)
		{
			
			if(k >= 1)
				query += ",";
			
			String l_name = q.lambda_term.get(k).name;
			
			String table_name = l_name.substring(0, l_name.indexOf(populate_db.separator));
			
			String attr_name = l_name.substring(l_name.indexOf(populate_db.separator) + 1, l_name.length());
			
			query += "cast (" + table_name + "." + attr_name + " as text)";
		}
		
		query += ") in (select * from unnest(";
		
		for(int k = 0; k<l_term_ids.size(); k++)
		{
			if(k >= 1)
				query += ",";
			
			query += "ARRAY[" + l_values[k] + "]";
		}
		
		query += "))";
		
		return query;
	}
	
	static void gen_single_citations(String sql, String block_name, HashMap<String, HashSet<String>> citations, Connection c, PreparedStatement pst) throws SQLException
	{
		pst = c.prepareStatement(sql);
		
		ResultSet rs = pst.executeQuery();
		
		HashSet<String> values = new HashSet<String>();
		
		ResultSetMetaData meta = rs.getMetaData();
		
		int col_num = meta.getColumnCount();
		
		while(rs.next())
		{
			
			String value = new String();
			
			for(int i = 0; i<col_num; i++)
			{
				if(i >= 1)
					value += " ";
				
				value += rs.getString(i + 1);
			}
			
			values.add(value);
			
//			if(block_name.equals("author"))
//			{
//				values.add(rs.getString(1) + " " + rs.getString(2));
//			}
			
		}
		
		citations.put(block_name, values);
		
	}
	
	static ArrayList<citation_view> get_single_citation_views(ArrayList<Covering_set> curr_res, ArrayList<String> view_keys)
	{
		ArrayList<citation_view> views = new ArrayList<citation_view>();
		
		HashSet<String> view_names = new HashSet<String>();
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			for(citation_view view_mapping: curr_res.get(i).c_vec)
			{
				
				citation_view c = view_mapping;
				
				String key = c.get_name() + populate_db.separator + c.get_table_name_string();
				
				if(!view_names.contains(key))
				{
					
					views.add(c);
					
					view_names.add(key);
					
					view_keys.add(key);
				}
				
			}
		}
		
		
		
		return views;
	}
	
	static ArrayList<citation_view> get_single_citation_views(HashSet<Covering_set> curr_res, ArrayList<String> view_keys)
	{
		ArrayList<citation_view> views = new ArrayList<citation_view>();
		
		HashSet<String> view_names = new HashSet<String>();
		
		for(Iterator iter = curr_res.iterator(); iter.hasNext();)
		{
			
			Covering_set covering_set = (Covering_set) iter.next();
			
			for(citation_view view_mapping: covering_set.c_vec)
			{
				
				citation_view c = view_mapping;
				
				String key = c.get_name() + populate_db.separator + c.get_table_name_string();
				
				if(!view_names.contains(key))
				{
					
					views.add(c);
					
					view_names.add(key);
					
					view_keys.add(key);
				}
				
			}
		}
		
		
		
		return views;
	}
	
	
	static void convert_covering_set2citation(int seq, ArrayList<Covering_set> curr_res, ArrayList<HashMap<String, HashSet<String>>> author_lists, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws ClassNotFoundException, SQLException
	{
		
		if(seq == 0)
		{
			for(int i = 0; i<curr_res.size(); i++)
			{
				Covering_set covering_set = curr_res.get(i);
				
				HashMap<String, HashSet<String>> citations = gen_citation1.join_covering_sets(covering_set, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
				
				author_lists.add(citations);
			}
		}
		else
		{
			for(int i = 0; i<curr_res.size(); i++)
			{
				Covering_set covering_set = curr_res.get(i);
				
				HashMap<String, HashSet<String>> citations = gen_citation1.join_covering_sets(covering_set, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
				
				merge_citations(author_lists.get(i), citations);
			}
		}
		
	}
	
	static void merge_citations(HashMap<String, HashSet<String>> citations1, HashMap<String, HashSet<String>> citations2)
	{
		Set<String> keys = citations2.keySet();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String key = (String) iter.next();
			
			HashSet<String> values = citations2.get(key);
			
			if(citations2.containsKey(key))
			{
				citations2.get(key).addAll(values);
			}
			else
			{
				citations2.put(key, values);
			}
		}
	}
	
//	public static HashSet<String> do_agg_intersection(ResultSet rs, HashMap<int[], ArrayList<citation_view_vector>> c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, HashMap<String, String> view_citation_mapping, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
//	{
//		
//		HashMap<String, Boolean> full_flag = new HashMap<String, Boolean>();
//		
//		for(int i = 0; i<populate_db.available_block.length; i++)
//		{
//			full_flag.put(populate_db.available_block[i], false);
//		}
//		
//		Set<int[]> intervals = c_view_map.keySet();
//		
//		int index = 0;
//		
//		rs.beforeFirst();
//		
//		ArrayList<HashMap<String, HashSet<String>>> author_lists = new ArrayList<HashMap<String, HashSet<String>>>();
//		
//		ArrayList<citation_view_vector> curr_res = new ArrayList<citation_view_vector>();
//		
//		HashSet<String> citation_list = new HashSet<String>();
//		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			ArrayList<citation_view_vector> c_view = c_view_map.get(interval);
//			
//			for(int i = interval[0]; i < interval[1]; i ++)
//			{
//				
//				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
//				{
//					rs.absolute(i + 1);
//					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
//					
//
//					
//					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, view_citation_mapping, tuple_level, c, pst);
//					
////					System.out.println(curr_res);
//				}
//			}
//		}
//		
//		System.out.println(curr_res);
//		
//		return gen_citations(author_lists, max_num);
//	}

}
