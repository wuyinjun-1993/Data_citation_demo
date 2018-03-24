package edu.upenn.cis.citation.aggregation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
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
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_full_min_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_min_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning2_full_min_test;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning2_min_test;

public class Aggregation4 {
	public static HashMap<String, HashSet<String>> full_citations = new HashMap<String, HashSet<String>>();

	
	public static HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
	
	public static void do_aggregate(ArrayList<Covering_set> curr_res, ArrayList<Covering_set> c_views, int seq, ArrayList<HashMap<String, HashSet<String>> > author_list, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, StringList view_list, HashMap<String, Boolean> full_flag, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		if(seq == 0)
		{
			
			curr_res.addAll(c_views);
			
			for(int i = 0; i<c_views.size(); i++)
			{
				HashMap<String, HashSet<String>> json_citation = gen_citation1.join_covering_sets(c_views.get(i), c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
				
				author_list.add(json_citation);
				
			}
			
			
			return;
			
		}
				
		int i1 = 0;
		
		int i2 = 0;
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			
			String id1 = curr_res.get(i).view_name_str;
			
			int j = 0;
			
			Covering_set c_vector = null;
			
			for(j = 0; j<c_views.size(); j++)
			{
				String id2 = c_views.get(j).view_name_str;
				
				c_vector = c_views.get(j);
				
				if(id1.equals(id2))
				{
					break;
				}
			}
			
			if(j < c_views.size())
			{				
							
				HashMap<String, HashSet<String>> curr_authors = author_list.get(i);
				
				if(check_block_full(curr_authors, max_num, full_flag))
					continue;
				
				HashMap<String, HashSet<String>> authors = gen_citation1.join_covering_sets(c_vector, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);				
				
//				System.out.println(c_vector + ":::::::::::" + authors);

				
				Set<String> keys = authors.keySet();
				
				for(Iterator iter = keys.iterator(); iter.hasNext();)
				{
					String key = (String) iter.next();
					
					HashSet<String> values = authors.get(key);
					
					if(curr_authors.get(key) == null)
					{
						curr_authors.put(key, values);
					}
					else
					{
						
						curr_authors.get(key).addAll(values);
					}
				}
				
				author_list.set(i, curr_authors);
				
//				for(int k = 0; k<c_vector.c_vec.size(); k++)
//				{
//					citation_view c_v = c_vector.c_vec.get(k);
//					
//					
//					int view_index = view_list.find(c_vector.c_vec.get(i).get_name());
//					
//					HashMap<String, HashSet<String>> authors = null;
//					
//					if(c_v.has_lambda_term())
//					{
//						authors = gen_citation1.get_authors3((citation_view_parametered)c_v, c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
//						
//						author_list.get(i).addAll(authors);
//					}
//					else
//					{
//						authors = gen_citation1.get_authors3((citation_view_unparametered)c_v, c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
//						
//						author_list.get(i).addAll(authors);
//					}
//					
//					for(Iterator iter = authors.iterator(); iter.hasNext() && author_list.get(i).size() <= max_num;)
//					{
//						author_list.get(i).add((String) iter.next());
//					}
//				}
				
				
			}
			else
			{
			
				curr_res.remove(i);
				
				author_list.remove(i);
				
				i--;
			}
		}
		
	}
	
	public static void do_aggregate(ArrayList<Covering_set> curr_res, ArrayList<Covering_set> c_views, int seq, ArrayList<HashMap<String, HashSet<String>> > author_list, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, StringList view_list, HashMap<String, Boolean> full_flag, HashMap<String, String> view_citation_mapping, boolean tuple_level, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	
		if(seq == 0)
		{
			
			curr_res.addAll(c_views);
			
			for(int i = 0; i<c_views.size(); i++)
			{
				HashMap<String, HashSet<String>> json_citation = gen_citation1.join_covering_sets(c_views.get(i), c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
				
				author_list.add(json_citation);
				
				view_citation_mapping.put(c_views.get(i).toString(), json_citation.toString());
				
			}
			
			
			return;
			
		}
				
		int i1 = 0;
		
		int i2 = 0;
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			
			String id1 = curr_res.get(i).view_name_str;
			
			String t_id1 = curr_res.get(i).table_name_str;
			
			int j = 0;
			
			Covering_set c_vector = null;
			
			for(j = 0; j<c_views.size(); j++)
			{
				String id2 = c_views.get(j).view_name_str;
				
				String t_id2 = c_views.get(j).table_name_str;
				
				c_vector = c_views.get(j);
				
				if(id1.equals(id2) && t_id1.equals(t_id2))
				{
					break;
				}
			}
			
			if(j < c_views.size())
			{				
							
				HashMap<String, HashSet<String>> curr_authors = author_list.get(i);
				
				if(check_block_full(curr_authors, max_num, full_flag))
					continue;
				
				HashMap<String, HashSet<String>> authors = gen_citation1.join_covering_sets(c_vector, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);				

				view_citation_mapping.put(c_vector.toString(), authors.toString());

				
				Set<String> keys = authors.keySet();
				
				for(Iterator iter = keys.iterator(); iter.hasNext();)
				{
					String key = (String) iter.next();
					
					HashSet<String> values = authors.get(key);
					
					if(curr_authors.get(key) == null)
					{
						curr_authors.put(key, values);
					}
					else
					{
						
						curr_authors.get(key).addAll(values);
					}
				}
				
				author_list.set(i, curr_authors);
				
			}
			else
			{
			
				curr_res.remove(i);
				
				author_list.remove(i);
				
				i--;
			}
		}
		
	}
	
	public static void do_aggregate_min(Covering_set c_views, int seq, HashMap<String, HashSet<String>> author_list, ArrayList<HashMap<String, Integer>> view_query_mapping, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, StringList view_list, HashMap<String, Boolean> full_flag, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	
//		if(seq == 0)
//		{			
//			HashMap<String, HashSet<String>> json_citation = gen_citation1.join_covering_sets(c_views, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);
//									
//			curr_res = c_views;
//			
//			return;
//			
//		}			
									
		if(!author_list.isEmpty() && check_block_full(author_list, max_num, full_flag))
			return;
		
		HashMap<String, HashSet<String>> authors = gen_citation1.join_covering_sets(c_views, c, pst, view_list, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping);				

		
		Set<String> keys = authors.keySet();
		
		for(Iterator iter = keys.iterator(); iter.hasNext();)
		{
			String key = (String) iter.next();
			
			HashSet<String> values = authors.get(key);
			
			if(author_list.get(key) == null)
			{
				author_list.put(key, values);
			}
			else
			{
				
				author_list.get(key).addAll(values);
			}
		}
				
			
		
	}
	
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
	
//	public static HashSet<String> do_agg_intersection(ResultSet rs, HashMap<int[], ArrayList<citation_view_vector> > c_view_map, Vector<Integer> selected_row_ids, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
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
//			for(int i = index; i < selected_row_ids.size(); i ++)
//			{
//				
//				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
//				{
//					rs.absolute(selected_row_ids.get(i) + 1);
//					
//					Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					
//					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, c, pst);
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//				}
//				
//				
//				if(selected_row_ids.get(index) >= interval[1])
//				{
//					index = i;
//					
//					break;
//				}
//			}
//		}
//		
//		
//		return gen_citations(author_lists, max_num);
//		
//	}
//	
	static HashSet<String> gen_citations(HashMap<String, HashSet<String>> author_lists, HashMap<String, Integer> max_num)
	{
		HashSet<String> json_string = new HashSet<String>();
			
//		for(int i = 0; i<author_lists.size(); i++)
		{
//			boolean empty = false;
			
//			HashMap<String, HashSet<String>> authors = author_lists.get(i);
			
			JSONObject json = gen_citation1.get_json_citation(author_lists);
			
			if(json == null)
				return json_string;
			
//			Set<String> keys = authors.keySet();
//						
//			for(Iterator iter = keys.iterator(); iter.hasNext();)
//			{
//				
//				JSONArray jarray = new JSONArray();
//				
//				String key = (String) iter.next();
//				
//				HashSet<String> values = authors.get(key);
//				
//				
//				
//				if(key.equals("author") && values.isEmpty())
//				{
//					empty = true;
//					
//					break;
//				}
////				
//				for(Iterator it = values.iterator(); it.hasNext();)
//				{
//					jarray.put((String)it.next());
//				}
//				
//
//				json.put(key, jarray);
//			}
//			
//			if(empty)
//				continue;
			
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
//	public static HashSet<String> do_agg_intersection(ResultSet rs, HashMap<int[], ArrayList<citation_view_vector> > c_view_map, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
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
////				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
//				{
//					rs.absolute(i + 1);
//					
//					if(tuple_level)
//						Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
//					else
//						Tuple_reasoning2_test.update_valid_citation_combination(c_view, rs, start_pos);
//					
//					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, c, pst);
//					
////					System.out.println(curr_res);
//				}
//			}
//		}
//				
//		return gen_citations(author_lists, max_num);
//	}
//	
	
	
	
	public static HashSet<String> do_agg_intersection_min(ResultSet rs, Covering_set c_views, int[] intervals, int start_pos, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		HashMap<String, Boolean> full_flag = new HashMap<String, Boolean>();
		
		for(int i = 0; i<populate_db.available_block.length; i++)
		{
			full_flag.put(populate_db.available_block[i], false);
		}
		
//		Set<int[]> intervals = c_view_map.keySet();
		
		int index = 0;
		
		rs.beforeFirst();
		
		HashMap<String, HashSet<String>> author_lists = new HashMap<String, HashSet<String>>();
		
		ArrayList<String> view_keys = new ArrayList<String>();
		
		ArrayList<citation_view> single_views = get_single_citation_views(c_views, view_keys);
		
		ArrayList<String> single_view_names = Aggregation5.get_citation_view_names(single_views);
		
		ArrayList<HashSet<Head_strs>> lambda_values = new ArrayList<HashSet<Head_strs>>();
		
		int tuple_num = (tuple_level) ? Tuple_reasoning1_full_min_test.tuple_num : Tuple_reasoning2_full_min_test.tuple_num;
		
		for(int i = 0; i<tuple_num; i++)
		{
			rs.absolute(i + 1);
			
			if(tuple_level)
				Tuple_reasoning1_full_min_test.get_views_parameters(single_views, rs, start_pos, lambda_values);
			else
				Tuple_reasoning2_full_min_test.get_views_parameters(single_views, rs, start_pos, lambda_values);
			
//			convert_covering_set2citation(i, curr_res, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, query_ids, view_list, c, pst, tuple_level);
			
			
		}
		
		
		ArrayList<HashMap<String, HashSet<String>>> citations = gen_citation_view_level(single_views, lambda_values, tuple_level, c, pst);

		full_citations = gen_citations_covering_set_level(citations, c_views, single_view_names, view_keys);

//		citation_view_vector curr_res = null;
		
//		HashSet<String> citation_list = new HashSet<String>();
		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
////			int [] interval = (int[]) iter.next();
////			
////			citation_view_vector c_view = c_view_map.get(interval);
//			
//			for(int i = intervals[0]; i < intervals[1]; i ++)
//			{
//				
//				
////				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
//				{
//					rs.absolute(i + 1);
//					
//					if(tuple_level)
//						Tuple_reasoning1_full_min_test.update_valid_citation_combination(c_views, rs, start_pos);
//					else
//						Tuple_reasoning2_full_min_test.update_valid_citation_combination(c_views, rs, start_pos);
//					
//
//					
//					do_aggregate_min(c_views, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, c, pst);
//					
////					System.out.println(c_views.toString());
//				}
//			}
//		}
		
//		System.out.println(curr_res);
		
		return gen_citations(full_citations, max_num);
	}
	
	public static int[] count_view_mapping_predicates_lambda_terms(Covering_set covering_set)
	{
		
		HashSet<String> view_mapping_string = new HashSet<String>();
		
		HashSet<String> lambda_string = new HashSet<String> ();
		
		HashSet<String> predicate_string = new HashSet<String>();
		
//		for(int i = 0; i<curr_res.size(); i++)
		{
//			citation_view_vector covering_set = curr_res.get(i);
			
			for(citation_view view_mapping: covering_set.c_vec)
			{
				citation_view v = view_mapping;
				
				String view_name = v.get_name();
				
				String table_names = v.get_table_name_string();
				
				String index_string = view_name + populate_db.separator + table_names;
				
				
				Vector<Conditions> conditions = v.get_view_tuple().conditions;
				
				Vector<Lambda_term> l_terms = v.get_view_tuple().lambda_terms;
				
				for(int k = 0; k<conditions.size(); k++)
				{
					predicate_string.add(conditions.get(k).toString());
				}
				
				for(int k = 0; k<l_terms.size(); k++)
				{
					lambda_string.add(l_terms.get(k).toString());
				}
				
//				if(view_mapping_string.contains(index_string))
				{
					view_mapping_string.add(index_string);
				}
			}
		}
		
		int [] nums = new int[3];
		
		nums[0] = view_mapping_string.size();
		
		nums[1] = lambda_string.size();
		
		nums[2] = predicate_string.size();
		
		return nums;
	}
	
	static HashMap<String, HashSet<String>> gen_citations_covering_set_level(ArrayList<HashMap<String, HashSet<String>>> citations, Covering_set curr_res, ArrayList<String> single_view_names, ArrayList<String> view_keys)
	{		
		
			Covering_set c_vector = curr_res;
			
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
					
		return curr_full_citations;
	}
	

	static ArrayList<HashMap<String, HashSet<String>>> gen_citation_view_level(ArrayList<citation_view> single_views, ArrayList<HashSet<Head_strs>> lambda_values, boolean tuple_level, Connection c, PreparedStatement pst) throws SQLException
	{
		ArrayList<HashMap<String, HashSet<String>>> all_citations = new ArrayList<HashMap<String, HashSet<String>>>();
		
		for(int i = 0; i<single_views.size(); i++)
		{
			HashMap<String, HashSet<String>> citations = gen_citation_view_level(single_views.get(i), lambda_values.get(i), tuple_level, c, pst);
			
			all_citations.add(citations);
		}
		
		return all_citations;
		
	}
	
	static HashMap<String, HashSet<String>> gen_citation_view_level(citation_view single_view, HashSet<Head_strs> lambda_values, boolean tuple_level, Connection c, PreparedStatement pst) throws SQLException
	{
		HashMap<String, Integer> citation_queries_ids = null;
		
		HashMap<Integer, Query> citation_queries = null;
		
		HashMap<String, HashSet<String>> citations = new HashMap<String, HashSet<String>>();
		
		IntList query_ids = null;
		
		if(tuple_level)
		{
			citation_queries_ids = Tuple_reasoning1_full_min_test.get_citation_queries(single_view.get_name());
			
			citation_queries = Tuple_reasoning1_full_min_test.citation_queries;
			
			query_ids = Tuple_reasoning1_full_min_test.query_ids;
		}
		else
		{
			citation_queries_ids = Tuple_reasoning2_full_min_test.get_citation_queries(single_view.get_name());
			
			citation_queries = Tuple_reasoning2_full_min_test.citation_queries;
			
			query_ids = Tuple_reasoning2_full_min_test.query_ids;
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
				
				if(l_values[k] != null)
					l_values[k] += "'" + head_value.head_vals.get(k) + "'";
				else
					l_values[k] = "'" + head_value.head_vals.get(k) + "'";
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
		
		while(rs.next())
		{
			if(block_name.equals("author"))
			{
				values.add(rs.getString(1) + " " + rs.getString(2));
			}
			
			citations.put(block_name, values);
		}
		
		
	}
	
	
	
	static ArrayList<citation_view> get_single_citation_views(Covering_set curr_res, ArrayList<String> view_keys)
	{
		ArrayList<citation_view> views = new ArrayList<citation_view>();
		
		HashSet<String> view_names = new HashSet<String>();
		
		for(citation_view view_mapping: curr_res.c_vec)
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
		
		
		
		return views;
	}
	
	static Covering_set get_intersected_set(HashMap<int[], Covering_set > c_view_map, ArrayList<Integer> selected_rows, Covering_set covering_set_schema)
	{
		Set<int[]> intervals = c_view_map.keySet();
		
		boolean hit = false;
		
		Covering_set selected_covering_set = null;
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			for(int i = 0; i<selected_rows.size(); i++)
			{
				if(selected_rows.get(i) < interval[1] && selected_rows.get(i) >= interval[0])
				{
					hit = true;
					
					selected_covering_set = c_view_map.get(interval);
				}
				else
				{
					if(hit)
						return covering_set_schema;
				}
			}
		}
		
		return selected_covering_set;
	}
	
	public static HashSet<String> do_agg_intersection_min(ResultSet rs, HashMap<int[], Covering_set > c_view_map, Covering_set c_views, int start_pos, ArrayList<Head_strs> heads, HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping, ArrayList<HashMap<String, Integer>> view_query_mapping, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, Unique_StringList> author_mapping, HashMap<String, Integer> max_num, IntList query_ids, StringList view_list, Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException, JSONException
	{
		
		HashMap<String, Boolean> full_flag = new HashMap<String, Boolean>();
		
		for(int i = 0; i<populate_db.available_block.length; i++)
		{
			full_flag.put(populate_db.available_block[i], false);
		}
		
		Set<int[]> intervals = c_view_map.keySet();
		
		ArrayList<Integer> selected_row_ids = Aggregation3.get_all_selected_row_ids(heads, head_strs_rows_mapping);
		
		Covering_set selected_covering_set = get_intersected_set(c_view_map, selected_row_ids, c_views);
		
		
		int index = 0;
		
		rs.beforeFirst();
		
		HashMap<String, HashSet<String>> author_lists = new HashMap<String, HashSet<String>>();
		
		Covering_set curr_res = null;
		
		HashSet<String> citation_list = new HashSet<String>();
		
		for(int i = 0; i<selected_row_ids.size(); i++)
		{
			rs.absolute(selected_row_ids.get(i) + 1);
			
			if(tuple_level)
				Tuple_reasoning1_min_test.update_valid_citation_combination(selected_covering_set, rs, start_pos);
			else
				Tuple_reasoning2_min_test.update_valid_citation_combination(selected_covering_set, rs, start_pos);
			

			
			do_aggregate_min(selected_covering_set, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, c, pst);

		}
		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			citation_view_vector c_view = c_view_map.get(interval);
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
//						Tuple_reasoning1_min_test.update_valid_citation_combination(c_views, rs, start_pos);
//					else
//						Tuple_reasoning2_min_test.update_valid_citation_combination(c_views, rs, start_pos);
//					
//
//					
//					do_aggregate_min(curr_res, c_views, i, author_lists, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_list, full_flag, tuple_level, c, pst);
//					
////					System.out.println(curr_res);
//				}
//			}
//		}
		
		System.out.println(curr_res);
		
		return gen_citations(author_lists, max_num);
	}

}
