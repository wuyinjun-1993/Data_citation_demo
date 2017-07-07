package edu.upenn.cis.citation.aggregation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1_test;

public class Aggregation2 {
	
	public static void do_aggregate(Vector<citation_view_vector> curr_res, Vector<citation_view_vector> c_views, int seq, Vector<HashSet<String> > author_list, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		if(seq == 0)
		{
			
//			Vector<HashSet<String>> author_list = new Vector<HashSet<String>>();
			
			curr_res.addAll(c_views);
			
			for(int i = 0; i<curr_res.size(); i++)
			{
				
				citation_view_vector c_view = curr_res.get(i);
				
				HashSet<String> all_authors = new HashSet<String>();
				
				for(int j = 0; j<c_view.c_vec.size(); j++)
				{
					citation_view c_v = c_view.c_vec.get(j);
					
					HashSet<String> authors = null;
					
					if(c_v.has_lambda_term())
					{
						authors = gen_citation1.get_authors2((citation_view_parametered)c_v, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num);
						
						all_authors.addAll(authors);
					}
					else
					{
						authors = gen_citation1.get_authors2((citation_view_unparametered)c_v, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num);
						
						all_authors.addAll(authors);
					}
					
					for(Iterator iter = authors.iterator(); iter.hasNext() && all_authors.size() <= max_num;)
					{
						all_authors.add((String) iter.next());
					}
				}
				
				author_list.add(all_authors);

			}
			
			return;
			
		}
		
		
		
		Vector<Vector<citation_view_vector>> agg_res = new Vector<Vector<citation_view_vector>>();
		
		int i1 = 0;
		
		int i2 = 0;
		
		for(int i = 0; i<curr_res.size(); i++)
		{
			
			String id1 = curr_res.get(i).view_name_str;
			
			int j = 0;
			
			citation_view_vector c_vector = null;
			
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
				for(int k = 0; k<c_vector.c_vec.size(); k++)
				{
					citation_view c_v = c_vector.c_vec.get(k);
					
					HashSet<String> authors = null;
					
					if(c_v.has_lambda_term())
					{
						authors = gen_citation1.get_authors2((citation_view_parametered)c_v, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num);
						
						author_list.get(i).addAll(authors);
					}
					else
					{
						authors = gen_citation1.get_authors2((citation_view_unparametered)c_v, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num);
						
						author_list.get(i).addAll(authors);
					}
					
					for(Iterator iter = authors.iterator(); iter.hasNext() && author_list.get(i).size() <= max_num;)
					{
						author_list.get(i).add((String) iter.next());
					}
				}
				
				
			}
			else
			{
				curr_res.removeElementAt(i);
				
				author_list.removeElementAt(i);
				
				i--;
			}
		}
		
	}
	
	public static Vector<String> do_agg_intersection(ResultSet rs, HashMap<int[], Vector<citation_view_vector> > c_view_map, Vector<Integer> selected_row_ids, int start_pos, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
	{
		Set<int[]> intervals = c_view_map.keySet();
		
		int index = 0;
		
		rs.beforeFirst();
		
		Vector<HashSet<String>> author_lists = new Vector<HashSet<String>>();
		
		Vector<citation_view_vector> curr_res = new Vector<citation_view_vector>();
		
		HashSet<String> citation_list = new HashSet<String>();
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			Vector<citation_view_vector> c_view = c_view_map.get(interval);
			
			for(int i = index; i < selected_row_ids.size(); i ++)
			{
				
				
				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
					rs.absolute(selected_row_ids.get(i));
					
					Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
					
					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
				}
				
				
				if(selected_row_ids.get(index) >= interval[1])
				{
					index = i;
					
					break;
				}
			}
		}
		
		Vector<String> json_string = new Vector<String>();
		
		HashSet<String> unique_string = new HashSet<String>();
		
		for(int i = 0; i<author_lists.size(); i++)
		{
			JSONObject json = new JSONObject();
			
			HashSet<String> authors = author_lists.get(i);
			
			JSONArray jarray = new JSONArray();

			int num = 0;
			
			for(Iterator iter = authors.iterator(); iter.hasNext();)
			{
				jarray.put((String)iter.next());
				
				num++;
				
				if(max_num > 0 && num >= max_num)
					break;
			}
			
			json.put("author", jarray);
			
			if(!unique_string.contains(json.toString()))
			{
				json_string.add(json.toString());
				
				unique_string.add(json.toString());
			}
		}
		
		return json_string;
	}
	
	public static Vector<String> do_agg_intersection(ResultSet rs, HashMap<int[], Vector<citation_view_vector> > c_view_map, int start_pos, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
	{
		Set<int[]> intervals = c_view_map.keySet();
		
		int index = 0;
		
		rs.beforeFirst();
		
		Vector<HashSet<String>> author_lists = new Vector<HashSet<String>>();
		
		Vector<citation_view_vector> curr_res = new Vector<citation_view_vector>();
		
		HashSet<String> citation_list = new HashSet<String>();
		
		for(Iterator iter = intervals.iterator(); iter.hasNext();)
		{
			int [] interval = (int[]) iter.next();
			
			Vector<citation_view_vector> c_view = c_view_map.get(interval);
			
			for(int i = interval[0]; i < interval[1]; i ++)
			{
				
				
//				if(selected_row_ids.get(i) < interval[1] && selected_row_ids.get(i) >= interval[0])
				{
					rs.absolute(i + 1);
					
					Tuple_reasoning1_test.update_valid_citation_combination(c_view, rs, start_pos);
					
					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
				}
			}
		}
		
		Vector<String> json_string = new Vector<String>();
		
		HashSet<String> unique_string = new HashSet<String>();
		
		for(int i = 0; i<author_lists.size(); i++)
		{
			JSONObject json = new JSONObject();
			
			HashSet<String> authors = author_lists.get(i);
			
			JSONArray jarray = new JSONArray();

			int num = 0;
			
			for(Iterator iter = authors.iterator(); iter.hasNext();)
			{
				
				
				jarray.put((String)iter.next());
				
				num++;
				
				if(max_num > 0 && num >= max_num)
					break;
			}
			
			json.put("author", jarray);
			
			if(!unique_string.contains(json.toString()))
			{
				json_string.add(json.toString());
				
				unique_string.add(json.toString());
			}
			
			json.put("db_name", gen_citation1.db_name);
			
			json.put("Accessed on", gen_citation1.getCurrentDate());
		}
		
		
		
		return json_string;
	}

}
