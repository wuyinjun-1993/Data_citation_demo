package edu.upenn.cis.citation.gen_citation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Mapping;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.Query_lm_authors;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;

public class gen_citation1 {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    
	    
	}
	
	public static String db_name = "IUPHAR/BPS Guide to PHARMACOLOGY";
	
	public static String get_citation_agg(Vector<citation_view_vector> c_views, int max_num, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping) throws ClassNotFoundException, SQLException, JSONException
	{
		
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		
		HashSet<String> authors = new HashSet<String>();
		
		HashMap<String, HashSet<String>> query_str = new HashMap<String, HashSet<String>>();
		
		for(int k = 0; k<c_views.size(); k++)
		{
			citation_view_vector c_v = c_views.get(k);

			
			for(int i = 0; i<c_v.c_vec.size(); i++)
			{
				
//				System.out.println(c_v.c_vec);
				
				if(c_v.c_vec.get(i).has_lambda_term())
				{								
					authors.addAll(get_authors2((citation_view_parametered)c_v.c_vec.get(i), c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num));
				}
				else
				{
					authors.addAll(get_authors2((citation_view_unparametered)c_v.c_vec.get(i), c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num));
				}
				
			}
		}
		

		
//		for(int p = 1; p<c_views.size(); p++)
//		{
//			c_v = c_views.get(p);
//			
//			for(int i = 0; i<c_v.c_vec.size(); i++)
//			{
//				
//				citation_view c_view = c_v.c_vec.get(i);
//				
//				HashSet<String> curr_str = query_str.get(c_view.get_name());
//				
////				for(int j = 0; j<curr_str.size(); j++)
//				for(Iterator iter = curr_str.iterator(); iter.hasNext();)
//				{
//					if(c_view.has_lambda_term())
//					{
//						
//						pst = c.prepareStatement((String)iter.next());
//						
//						for(int k = 0; k<((citation_view_parametered)c_view).lambda_terms.size(); k++)
//						{
//							pst.setString(k + 1, ((citation_view_parametered)c_view).map.get(((citation_view_parametered)c_view).lambda_terms.get(k)));
//						}
//						
//						ResultSet r = pst.executeQuery();
//						
//						while(r.next())
//						{
//							authors.add(r.getString(1) + " " + r.getString(2));
//						}
//					}
//					else
//					{
//						pst = c.prepareStatement((String)iter.next());
//						
//						ResultSet s = pst.executeQuery();
//						
//						while(s.next())
//						{
//							authors.add(s.getString(1) + " " + s.getString(2));
//						}
//					}
//				}
//				
//			}
//		}
		
		
		
		
		
		c.close();
		
		return combine_blocks(authors, max_num, new Vector<String>());
	}
	
	
	static String combine_blocks(HashSet<String> authors, int max_num, Vector<String> vals) throws JSONException
	{
		
		JSONObject json_obj = new JSONObject();
		
		JSONArray json_arr = new JSONArray();
		
//		String author_str = new String();
		
		int num = 0;
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
//			if(num >= 1)
//				author_str += ",";
//			
//			author_str += iter.next();
			json_arr.put(iter.next());
			
			num++;
			
			if(max_num > 0 && num >= max_num)
			{
//				author_str += ", etc.";
				break;
			}
		}
		
		json_obj.put("author", json_arr);
		
//		String citation = author_str;
//		
//		if(!author_str.isEmpty())
//		{
//			citation = author_str + ".";
//		}
		
//		String value_str = new String();
//		
//		for(int i = 0; i<vals.size(); i++)
//		{
//			if( i >= 1)
//				value_str += ",";
//			
//			value_str += vals.get(i);
//		}
//		
//		citation = value_str;
		
		json_obj.put("Accessed on", getCurrentDate());
		
//		citation += "Accessed on " + getCurrentDate() + ". ";

		json_obj.put("db_name", db_name);
		
//		citation += db_name;
		
		return json_obj.toString();
	}
	
	public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
}
	
	public static String get_citations(citation_view_vector c_vec, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> query_str, int max_num, HashSet<String> authors) throws ClassNotFoundException, SQLException
	{
		String author_str = new String();
		
//		HashSet<String> authors = new HashSet<String>();
		
//		System.out.println(c_vec.toString());
		
		for(int i = 0; i<c_vec.c_vec.size(); i++)
		{
			if(c_vec.c_vec.get(i).has_lambda_term())
			{
				authors.addAll(get_authors((citation_view_parametered)c_vec.c_vec.get(i),c, pst, query_str, max_num));
			}
			else
			{
				authors.addAll(get_authors((citation_view_unparametered)c_vec.c_vec.get(i),c, pst, query_str, max_num));
			}
			
			
			
		}
		
		int num = 0;
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
			if(num >= 1)
				author_str += ",";
			
			author_str += iter.next();
			num++;
			
			if(max_num > 0 && num >= max_num)
			{
				author_str += ", etc.";
				break;
			}
		}
		
		String citation = author_str;
		
		if(!author_str.isEmpty())
		{
			citation = author_str + ".";
		}
		
		String value_str = new String();
		
		for(int i = 0; i<vals.size(); i++)
		{
			if( i >= 1)
				value_str += ",";
			
			value_str += vals.get(i);
		}
		
		citation += value_str + "." + db_name;
		
		return citation;
	}
	
	
	public static String get_citations2(citation_view_vector c_vec, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num, HashSet<String> authors) throws ClassNotFoundException, SQLException, JSONException
	{
//		String author_str = new String();
		
//		HashSet<String> authors = new HashSet<String>();
		
//		System.out.println(c_vec.toString());
		
		JSONObject json_obj = new JSONObject();
		
		
		for(int i = 0; i<c_vec.c_vec.size(); i++)
		{
			if(c_vec.c_vec.get(i).has_lambda_term())
			{
				
				if(authors.size() < max_num)
					authors.addAll(get_authors2((citation_view_parametered)c_vec.c_vec.get(i), c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num));
			}
			else
			{
				if(authors.size() < max_num)
					authors.addAll(get_authors2((citation_view_unparametered)c_vec.c_vec.get(i), c, pst, view_query_mapping, query_lambda_str, author_mapping, max_num));
			}
			
			
			
		}
		

		
		int num = 0;
		
		JSONArray json_author = new JSONArray();
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
//			if(num >= 1)
//				author_str += ",";
//			
//			author_str += iter.next();
			json_author.put(iter.next());

			num ++;
			
			if(max_num > 0 && num >= max_num)
			{
				
				
//				author_str += ", etc.";
				break;
			}
		}
		
		json_obj.put("author", json_author);
		
//		String citation = author_str;
//		
//		if(!author_str.isEmpty())
//		{
//			citation = author_str + ".";
//		}
		
		authors.clear();
		
//		String value_str = new String();
//		
//		for(int i = 0; i<vals.size(); i++)
//		{
//			if( i >= 1)
//				value_str += ",";
//			
//			value_str += vals.get(i);
//		}
		json_obj.put("db_name", db_name);
		
		return json_obj.toString();
	}
	
	
	public static String get_citations3(citation_view_vector c_vec, Connection c, PreparedStatement pst, StringList view_list, IntList[] view_query_mapping, HashMap<String, Unique_StringList> author_mapping, int max_num, HashSet<String> authors, JSONObject json_obj, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, HashSet<String>> view_author_mapping) throws ClassNotFoundException, SQLException, JSONException
	{
//		String author_str = new String();
		
//		HashSet<String> authors = new HashSet<String>();
		
//		System.out.println(c_vec.toString());
		
		
		
		
		for(int i = 0; i<c_vec.c_vec.size(); i++)
		{
			
			int view_index = view_list.find(c_vec.c_vec.get(i).get_name());
			
			
			if(c_vec.c_vec.get(i).has_lambda_term())
			{
				if(authors.size() < max_num)
					authors.addAll(get_authors3((citation_view_parametered)c_vec.c_vec.get(i), c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping));
			}
			else
			{
				if(authors.size() < max_num)
					authors.addAll(get_authors3((citation_view_unparametered)c_vec.c_vec.get(i), c, pst, view_index, view_query_mapping, author_mapping, max_num, query_ids, query_lambda_str, view_author_mapping));
			}
			
			
			
		}
		

		
		int num = 0;
		
		JSONArray json_author = new JSONArray();
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
//			if(num >= 1)
//				author_str += ",";
//			
//			author_str += iter.next();
			json_author.put(iter.next());

			num ++;
			
			if(max_num > 0 && num >= max_num)
			{
				
				
//				author_str += ", etc.";
				break;
			}
		}
		
		
		json_obj.put("author", json_author);
		
//		String citation = author_str;
//		
//		if(!author_str.isEmpty())
//		{
//			citation = author_str + ".";
//		}
		
		authors.clear();
		
//		String value_str = new String();
//		
//		for(int i = 0; i<vals.size(); i++)
//		{
//			if( i >= 1)
//				value_str += ",";
//			
//			value_str += vals.get(i);
//		}
		json_obj.put("db_name", db_name);
		
		
		return json_obj.toString();
	}
	
	
	public static String get_citations(citation_view_vector c_vec, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> query_str, int max_num) throws ClassNotFoundException, SQLException
	{
		String author_str = new String();
		
		HashSet<String> authors = new HashSet<String>();
		
		
		for(int i = 0; i<c_vec.c_vec.size(); i++)
		{
			if(c_vec.c_vec.get(i).has_lambda_term())
			{
				authors.addAll(get_authors((citation_view_parametered)c_vec.c_vec.get(i),c, pst, query_str, max_num));
			}
			else
			{
				authors.addAll(get_authors((citation_view_unparametered)c_vec.c_vec.get(i),c, pst, query_str, max_num));
			}
			
			
			
		}
		
		int num = 0;
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
			if(num >= 1)
				author_str += ",";
			
			author_str += iter.next();
			num++;
			
			if(max_num > 0 && num >= max_num)
			{
				author_str += ", etc.";
				break;
			}
		}
		
		String citation = author_str;
		
		if(!author_str.isEmpty())
		{
			citation = author_str + ".";
		}
		
		String value_str = new String();
		
		for(int i = 0; i<vals.size(); i++)
		{
			if( i >= 1)
				value_str += ",";
			
			value_str += vals.get(i);
		}
		
		citation += value_str + "." + db_name;
		
		return citation;
	}
	
	
	public static String populate_citation(citation_view_vector c_views, Vector<String> values, Connection c, PreparedStatement pst, HashMap<String, Vector<String>> query_str, int max_num) throws SQLException
	{
		
		HashSet<String> authors = new HashSet<String>();
		
		for(int i = 0; i<c_views.c_vec.size(); i++)
		{
			
			citation_view c_view = c_views.c_vec.get(i);
			
			Vector<String> curr_str = query_str.get(c_view.get_name());
			
			for(int j = 0; j<curr_str.size(); j++)
			{
				if(c_view.has_lambda_term())
				{
					
					pst = c.prepareStatement(curr_str.get(j));
					
					for(int k = 0; k<((citation_view_parametered)c_view).lambda_terms.size(); k++)
					{
						pst.setString(k + 1, ((citation_view_parametered)c_view).map.get(((citation_view_parametered)c_view).lambda_terms.get(k)));
					}
					
					ResultSet rs = pst.executeQuery();
					
					while(rs.next())
					{
						authors.add(rs.getString(1) + " " + rs.getString(2));
					}
				}
				else
				{
					pst = c.prepareStatement(curr_str.get(j));
					
					ResultSet rs = pst.executeQuery();
					
					while(rs.next())
					{
						authors.add(rs.getString(1) + " " + rs.getString(2));
					}
				}
			}
			
		}
		
		int num = 0;
		
		String author_str = new String();
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
			if(num >= 1)
				author_str += ",";
			
			author_str += iter.next();
			num++;
			if(max_num > 0 && num >= max_num)
			{
				author_str += ", etc.";
				break;
			}
			
		}
		
		String citation = author_str;
		
		if(!author_str.isEmpty())
		{
			citation = author_str + ".";
		}
		
		String value_str = new String();
		
		for(int i = 0; i<values.size(); i++)
		{
			if( i >= 1)
				value_str += ",";
			
			value_str += values.get(i);
		}
		
		citation += value_str + "." + db_name;
		
		return citation;
	}
	
	public static String populate_citation(citation_view_vector c_views, Vector<String> values, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> query_str, int max_num, HashSet<String> authors) throws SQLException, ClassNotFoundException
	{
		
//		HashSet<String> authors = new HashSet<String>();
		
		
		for(int i = 0; i<c_views.c_vec.size(); i++)
		{
			
			citation_view c_view = c_views.c_vec.get(i);
			
//			Vector<Integer> query_ids = get_query_id(c_view.get_name(), c, pst);
			
			HashSet<String> curr_str = query_str.get(c_view.get_name());
			
//			for(int j = 0; j<curr_str.size(); j++)
			for(Iterator iter = curr_str.iterator(); iter.hasNext();)
			{
				
				String sql_template = (String) iter.next();
				
				String[] sql_with_lambdas = sql_template.split("\\" + populate_db.separator);
				
//				Query citation_query = Query_operation.get_query_by_id(query_ids.get(j), c, pst);
				
//				if(citation_query.lambda_term.size() > 0)
				if(sql_with_lambdas.length > 1)
				{
					
					pst = c.prepareStatement(sql_with_lambdas[0]);
					
					for(int k = 1; k<sql_with_lambdas.length; k++)
					{
						
						String l_term = sql_with_lambdas[k];
						
						String table_name = l_term.substring(0, l_term.indexOf(populate_db.separator));
						
						String arg_name = l_term.substring(l_term.indexOf(populate_db.separator), l_term.length());
						
						citation_view_parametered curr_c_view = (citation_view_parametered)c_view;
						
						HashMap mapping = curr_c_view.view_tuple.mapSubgoals_str;
						
						String new_l_name = ((String) mapping.get(table_name)) + arg_name;
						
//						System.out.println(curr_c_view.map.get(l_term.toString()));
						
						pst.setString(k, curr_c_view.map.get(new_l_name));
					}
					
					ResultSet rs = pst.executeQuery();
					
					while(rs.next())
					{
						authors.add(rs.getString(1) + " " + rs.getString(2));
					}
				}
				else
				{
					pst = c.prepareStatement(sql_with_lambdas[0]);
					
					ResultSet rs = pst.executeQuery();
					
					while(rs.next())
					{
						authors.add(rs.getString(1) + " " + rs.getString(2));
					}
				}
			}
			
		}
		
		int num = 0;
		
		String author_str = new String();
		
		for(Iterator iter = authors.iterator(); iter.hasNext();)
		{
			
			if(num >= 1)
				author_str += ",";
			
			author_str += iter.next();
			num++;
			if(max_num > 0 && num >= max_num)
			{
				author_str += ", etc.";
				break;
			}
			
		}
		
		String citation = author_str;
		
		if(!author_str.isEmpty())
		{
			citation = author_str + ".";
		}
		
		String value_str = new String();
		
		for(int i = 0; i<values.size(); i++)
		{
			if( i >= 1)
				value_str += ",";
			
			value_str += values.get(i);
		}
		
		citation += value_str + "." + db_name;
		
		return citation;
	}
	
	
	public static Vector<Integer> get_query_id(String c_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Integer> query_ids = new Vector<Integer>();
		
		int id = Integer.valueOf(c_view_name.substring(1));
		
		String query = "select citation2query.query_id from citation2view, citation2query where citation2view.citation_view_id = citation2query.citation_view_id and citation2view.view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			query_ids.add(rs.getInt(1));
		}
		
		return query_ids;
		
	}
	
	public static ArrayList<Integer> get_query_id2(String c_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		ArrayList<Integer> query_ids = new ArrayList<Integer>();
		
		int id = Integer.valueOf(c_view_name.substring(1));
		
		String query = "select citation2query.query_id from citation2view, citation2query where citation2view.citation_view_id = citation2query.citation_view_id and citation2view.view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			query_ids.add(rs.getInt(1));
		}
		
		return query_ids;
		
	}
	
	public static IntList get_query_id3(String c_view_name, IntList all_query_ids, Connection c, PreparedStatement pst) throws SQLException
	{
		IntList query_ids = new IntList();
		
		int id = Integer.valueOf(c_view_name.substring(1));
		
		String query = "select citation2query.query_id from citation2view, citation2query where citation2view.citation_view_id = citation2query.citation_view_id and citation2view.view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			int query_id = rs.getInt(1);
			
			int query_id_id = all_query_ids.find(query_id);
			
			query_ids.add(query_id_id);
		}
		
		return query_ids;
		
	}
	
	public static HashSet<String> get_authors(citation_view_parametered c_view, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> query_str, int max_num) throws SQLException, ClassNotFoundException
	{
		HashSet<String> authors = new HashSet<String>();
		
		Vector<Integer> query_ids = get_query_id(c_view.name, c, pst);
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
		String lambda_term_str = new String();
		
		for(int i = 0; i<c_view.lambda_terms.size(); i++)
		{
			if(i >= 1)
				lambda_term_str += " and ";
			
			lambda_term_str += c_view.view.subgoal_name_mapping.get(c_view.lambda_terms.get(i).table_name) + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
		}
		
		HashSet<String> curr_sql = new HashSet<String>();
		
		for(int k = 0; k<query_ids.size(); k++)
		{
			Query q = Query_operation.get_query_by_id(query_ids.get(k), c, pst);
			
//			System.out.println(q);
			
			String sql = Query_converter.datalog2sql_citation_query(q, max_num);
			
			String lambda_term_q_str = new String();
			
			for(int i = 0; i<q.lambda_term.size(); i++)
			{
				if(i >= 1)
					lambda_term_q_str += " and ";
				
				String l_name = q.lambda_term.get(i).name;
				
				lambda_term_q_str += q.lambda_term.get(i).table_name + "." + l_name.substring(l_name.indexOf(populate_db.separator) + 1, l_name.length()) + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
			}
			
			
			if(q.conditions.size() > 0)
			{
				sql += " and " + lambda_term_q_str;
//				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
//				{
//					sql += " " + query_components.get(i)[1];
//				}
				
			}
			
			else
			{
				sql += " where " + lambda_term_q_str;
				
//				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
//				{
//					sql += " " + query_components.get(i)[1];
//				}
			}
			
			pst = c.prepareStatement(sql);

			
			String sql_template = sql;
			
			for(int i = 0; i<q.lambda_term.size(); i++)
			{
				sql_template += populate_db.separator + q.lambda_term.get(i).toString();
			}
			
			curr_sql.add(sql_template);
			
//			System.out.println("sql:::" + c_view.toString() + ":::" + sql);
			
			for(int j = 0; j<q.lambda_term.size(); j++)
			{
				
				HashMap subgoal_mapping = c_view.view_tuple.mapSubgoals_str;
				
				Lambda_term l_term = q.lambda_term.get(j);
				
				l_term.update_table_name((String)subgoal_mapping.get(l_term.table_name));
				
				String temp =  c_view.map.get(l_term.toString());
				
//				System.out.println(l_term.toString());
//								
//				System.out.println("l_term::" + l_term.toString());
//				
//				System.out.println(c_view.map.toString());
//				
//				System.out.println(temp);
				
				pst.setString(j + 1, temp);
				
			}
			
//			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				authors.add(rs.getString(1) + " " + rs.getString(2));
			}
			
		}
		
		
//		for(int i = 0; i<query_components.size(); i++)
//		{
//			String query = query_components.get(i)[0];
//			
//			for(int j = 0; j<subgoals.size(); j++)
//			{
//				query += "," + subgoals.get(j) + "()";
//			}
//			
//			query += "," + query_components.get(i)[2];
//			
//			for(int j = 0; j<conditions.size(); j++)
//			{
//				query += "," + conditions.get(j);
//			}
//			
//			query += "," + query_components.get(i)[3];
//			
//			query = Tuple_reasoning1.get_full_query(query);
//			
//			Query q = Parse_datalog.parse_query(query);
//			
//			String sql = Query_converter.datalog2sql(q);
//			
//			if(q.conditions.size() > 0)
//			{
//				sql += " and " + lambda_term_str;
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//				
//			}
//			
//			else
//			{
//				sql += " where " + lambda_term_str;
//				
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//			}
//			
//			pst = c.prepareStatement(sql);
//
//			curr_sql.add(sql);
//			
//			for(int j = 0; j<c_view.lambda_terms.size(); j++)
//			{
//				String temp =  c_view.map.get(c_view.lambda_terms.get(i));
//				
//				pst.setString(j + 1, temp);
//				
//			}
//			
////			pst = c.prepareStatement(sql);
//			
//			ResultSet rs = pst.executeQuery();
//			
//			while(rs.next())
//			{
//				authors.add(rs.getString(1) + " " + rs.getString(2));
//			}
//			
//			
//		}
		
		query_str.put(c_view.name, curr_sql);
		return authors;
		
	}
	
	public static HashSet<String> get_authors2(citation_view_parametered c_view, Connection c, PreparedStatement pst, HashMap<String, Vector<Integer>> view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num) throws SQLException, ClassNotFoundException
	{
		HashSet<String> authors = new HashSet<String>();
		
		
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
//		String lambda_term_str = new String();
//		
//		for(int i = 0; i<c_view.lambda_terms.size(); i++)
//		{
//			if(i >= 1)
//				lambda_term_str += " and ";
//			
//			lambda_term_str += c_view.view.subgoal_name_mapping.get(c_view.lambda_terms.get(i).table_name) + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//		}
//		
//		HashSet<String> curr_sql = new HashSet<String>();
		Vector<Integer> curr_query_ids = view_query_mapping.get(c_view.name);
				
		if(curr_query_ids == null)
		{						
			curr_query_ids = get_query_id(c_view.name, c, pst);
			
			view_query_mapping.put(c_view.name, curr_query_ids);
			
			for(int k = 0; k<curr_query_ids.size(); k++)
			{
				HashMap<Head_strs, HashSet<String>> curr_author_mapping = author_mapping.get(curr_query_ids.get(k));
				
				if(curr_author_mapping == null)
				{
					
					curr_author_mapping = new HashMap<Head_strs, HashSet<String>>();
					
					Query q = Query_operation.get_query_by_id(curr_query_ids.get(k), c, pst);
					
					Vector<Lambda_term> l_terms = q.lambda_term;
					
					query_lambda_str.put(curr_query_ids.get(k), l_terms);
					
//					HashMap<Head_strs, HashSet<String>> curr_query_author_mapping = new HashMap<Head_strs, HashSet<String>>();
					
//					System.out.println(q);
					
					String sql = Query_converter.datalog2sql_citation_query(q);
					
					pst = c.prepareStatement(sql);
					
					ResultSet rs = pst.executeQuery();
					
					ResultSetMetaData meta = rs.getMetaData();
					
					int col_num = meta.getColumnCount();
					
					while(rs.next())
					{
						String author_name = rs.getString(1) + " " + rs.getString(2);
						
						Vector<String> head_strs = new Vector<String>();
						
						for(int i = 0; i<col_num -2; i++)
						{
							head_strs.add(rs.getString(i + 3));
						}
						
						Head_strs h_str = new Head_strs(head_strs);
						
						head_strs.clear();
						
						HashSet<String> author_set = curr_author_mapping.get(h_str); 
						
						if(author_set == null)
						{
							author_set = new HashSet<String>();
							
							author_set.add(author_name);
							
							curr_author_mapping.put(h_str, author_set);
						}
						else
						{
							
							if((max_num > 0 && author_set.size() <= max_num) || max_num == 0)
							{
								author_set.add(author_name);
								
								curr_author_mapping.put(h_str, author_set);
							}
						}
						
						
						
						
					}
					
								
					
				}
				
				author_mapping.put(curr_query_ids.get(k), curr_author_mapping);	
				
			}
		}
						
//		for(Iterator it = query_id_set.iterator(); it.hasNext();)
		for(int k = 0; k<curr_query_ids.size(); k++)
		{
			HashMap<Head_strs, HashSet<String>> curr_authors =  author_mapping.get(curr_query_ids.get(k));
			
			Vector<Lambda_term> l_terms = query_lambda_str.get(curr_query_ids.get(k));
			
			HashMap subgoal_mapping = c_view.view_tuple.mapSubgoals_str;
			
			Vector<String> head_strs = new Vector<String>();
			
			for(int i = 0; i<l_terms.size(); i++)
			{
				
				Lambda_term l_term = (Lambda_term) l_terms.get(i);
				
				String table_name = (String)subgoal_mapping.get(l_term.table_name);
								
				String temp =  c_view.map.get(table_name + populate_db.separator + l_term.name.substring(l_term.name.indexOf(populate_db.separator) + 1, l_term.name.length()));
				
				head_strs.add(temp);
			}
			
			Head_strs h_str = new Head_strs(head_strs);
			
			head_strs.clear();
			
			HashSet<String> curr_author_list = curr_authors.get(h_str);
			
			h_str.clear();
			
			if(curr_author_list != null)
				authors.addAll(curr_author_list);
			
		}
		
		return authors;
		
	}
	
	public static HashSet<String> get_authors3(citation_view_parametered c_view, Connection c, PreparedStatement pst, int view_id, IntList[] view_query_mapping, HashMap<String, Unique_StringList> author_mapping, int max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, HashSet<String>> view_author_mapping) throws SQLException, ClassNotFoundException
	{
		if(view_author_mapping.get(c_view.toString()) != null)
		{
			return view_author_mapping.get(c_view.toString());
		}
		
		
		HashSet<String> authors = new HashSet<String>();
		
		
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
//		String lambda_term_str = new String();
//		
//		for(int i = 0; i<c_view.lambda_terms.size(); i++)
//		{
//			if(i >= 1)
//				lambda_term_str += " and ";
//			
//			lambda_term_str += c_view.view.subgoal_name_mapping.get(c_view.lambda_terms.get(i).table_name) + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//		}
//		
//		HashSet<String> curr_sql = new HashSet<String>();
//		ArrayList<Integer> curr_query_ids = view_query_mapping.get(c_view.name);
//				
//		if(curr_query_ids == null)
//		{						
//			curr_query_ids = get_query_id2(c_view.name, c, pst);
//			
//			view_query_mapping.put(c_view.name, curr_query_ids);
//			
//			for(int k = 0; k<curr_query_ids.size(); k++)
//			{
//				HashMap<Head_strs, HashSet<String>> curr_author_mapping = author_mapping.get(curr_query_ids.get(k));
//				
//				if(curr_author_mapping == null)
//				{
//					
//					curr_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//					
//					Query q = Query_operation.get_query_by_id(curr_query_ids.get(k), c, pst);
//					
//					ArrayList<Lambda_term> l_terms = new ArrayList<Lambda_term>();
//					
//					l_terms.addAll(q.lambda_term);
//					
//					query_lambda_str.put(curr_query_ids.get(k), l_terms);
//					
////					HashMap<Head_strs, HashSet<String>> curr_query_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//					
////					System.out.println(q);
//					
//					String sql = Query_converter.datalog2sql_citation_query(q);
//					
//					pst = c.prepareStatement(sql);
//					
//					ResultSet rs = pst.executeQuery();
//					
//					ResultSetMetaData meta = rs.getMetaData();
//					
//					int col_num = meta.getColumnCount();
//					
//					while(rs.next())
//					{
//						String author_name = rs.getString(1) + " " + rs.getString(2);
//						
//						Vector<String> head_strs = new Vector<String>();
//						
//						for(int i = 0; i<col_num -2; i++)
//						{
//							head_strs.add(rs.getString(i + 3));
//						}
//						
//						Head_strs h_str = new Head_strs(head_strs);
//						
//						head_strs.clear();
//						
//						HashSet<String> author_set = curr_author_mapping.get(h_str); 
//						
//						if(author_set == null)
//						{
//							author_set = new HashSet<String>();
//							
//							author_set.add(author_name);
//							
//							curr_author_mapping.put(h_str, author_set);
//						}
//						else
//						{
//							
//							if((max_num > 0 && author_set.size() <= max_num) || max_num == 0)
//							{
//								author_set.add(author_name);
//								
//								curr_author_mapping.put(h_str, author_set);
//							}
//						}
//						
//						
//						
//						
//					}
//					
//								
//					
//				}
//				
//				author_mapping.put(curr_query_ids.get(k), curr_author_mapping);	
//				
//			}
//		}
						
//		for(Iterator it = query_id_set.iterator(); it.hasNext();)
		IntList curr_query_ids = view_query_mapping[view_id];
				
		for(int k = 0; k<curr_query_ids.size; k++)
		{
						
//			int query_id_index = query_ids.list[];
			
//			Query_lm_authors curr_authors =  author_mapping[curr_query_ids.list[k]];
						
			Lambda_term[] l_terms = query_lambda_str.get(curr_query_ids.list[k]);
						
			HashMap subgoal_mapping = c_view.view_tuple.mapSubgoals_str;
			
			Vector<String> head_strs = new Vector<String>();
			
			for(int i = 0; i<l_terms.length; i++)
			{
				
				Lambda_term l_term = (Lambda_term) l_terms[i];
								
				String table_name = (String)subgoal_mapping.get(l_term.table_name);
								
				String temp =  c_view.map.get(table_name + populate_db.separator + l_term.name.substring(l_term.name.indexOf(populate_db.separator) + 1, l_term.name.length()));
				
				head_strs.add(temp);
			}
			
			Head_strs h_str = new Head_strs(head_strs);
			
			head_strs.clear();
			
			Unique_StringList curr_author_list = author_mapping.get(c_view.name + populate_db.separator + h_str.toString() + populate_db.separator + curr_query_ids.list[k]);
			
			h_str.clear();
			
			if(curr_author_list != null)
			{
				for(int p = 0; p<curr_author_list.size; p++)
				{
					authors.add(curr_author_list.list[p]);
				}
				
				
//				authors.addAll(Arrays.asList(Arrays.copyOfRange(curr_author_list.list, 0, curr_author_list.size)));
			}
			
		}
		
		view_author_mapping.put(c_view.toString(), authors);
		
		return authors;
		
	}
	
	public static void get_all_query_ids(IntList query_ids, Connection c, PreparedStatement pst) throws SQLException
	{
		String sql = "select query_id from query2head_variables";
		
		pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		ResultSet rs = pst.executeQuery();
		
		
		int num = 0;
		
		while(rs.next())
		{
			
			
			num ++;
			
//			query_ids.add(id);
		}
		
		rs.beforeFirst();
		
		query_ids.init(num);
		
		num = 0;
		
		while(rs.next())
		{
			int id = rs.getInt(1);
			
			query_ids.add(id);
		}
	}
	
	public static void init_author_mapping(StringList view_list, IntList[] view_query_mapping, IntList query_ids, HashMap<String, Unique_StringList> author_mappings, int max_num, Connection c, PreparedStatement pst, ArrayList<Lambda_term[]> query_lambda_str) throws ClassNotFoundException, SQLException
	{
		
//		get_all_query_ids(query_ids, c, pst);
				
		for(int i = 0; i < view_list.size; i++)
		{
			IntList curr_query_ids = get_query_id3(view_list.list[i], query_ids, c, pst);
			
			view_query_mapping[i] = curr_query_ids;
			
			for(int j = 0; j<curr_query_ids.size; j++)
			{
				HashMap<Head_strs, Unique_StringList> curr_author_mapping = new HashMap<Head_strs, Unique_StringList>(); 
				
				Query q = Query_operation.get_query_by_id(query_ids.list[curr_query_ids.list[j]], c, pst);
				
				Lambda_term [] l_terms = new Lambda_term [q.lambda_term.size()];
				
				for(int k = 0; k<q.lambda_term.size(); k++)
				{
					l_terms[k] = q.lambda_term.get(k);
				}
				
//				.addAll(q.lambda_term);
				
				query_lambda_str.add(l_terms);
				
				String sql = Query_converter.datalog2sql_citation_query(q);
				
				pst = c.prepareStatement(sql);
				
				ResultSet rs = pst.executeQuery();
				
				ResultSetMetaData meta = rs.getMetaData();
				
				int col_num = meta.getColumnCount();
								
				while(rs.next())
				{
					String author_name = rs.getString(1) + " " + rs.getString(2);
					
					Vector<String> head_strs = new Vector<String>();
					
					for(int k = 0; k<col_num -2; k++)
					{
						head_strs.add(rs.getString(k + 3));
					}
					
					Head_strs h_str = new Head_strs(head_strs);
										
					head_strs.clear();
					
					Unique_StringList author_set = curr_author_mapping.get(h_str); 
					
					if(author_set == null)
					{
						author_set = new Unique_StringList();
						
						author_set.add(author_name);
						
						curr_author_mapping.put(h_str, author_set);
					}
					else
					{
						
						if((max_num > 0 && author_set.size <= max_num) || max_num == 0)
						{
							author_set.add(author_name);
									
							curr_author_mapping.put(h_str, author_set);
						}
					}					
				}
				
				Set<Head_strs> head_strs = curr_author_mapping.keySet();
				
				for(Iterator iter = head_strs.iterator(); iter.hasNext();)
				{
					Head_strs h_str = (Head_strs) iter.next();
					
					String key = view_list.list[i] + populate_db.separator + h_str.toString() + populate_db.separator + curr_query_ids.list[j];
										
					author_mappings.put(key, curr_author_mapping.get(h_str));
				}
				
				
//				author_mapping.add(curr_author_mapping);
			
			}
		}
		
		
//		
//		for(int j = 0; j<query_ids.size; j++)
//		{
//			HashMap<Head_strs, Unique_StringList> curr_author_mapping = new HashMap<Head_strs, Unique_StringList>(); 
//			
//			Query q = Query_operation.get_query_by_id(query_ids.list[j], c, pst);
//			
//			Lambda_term [] l_terms = new Lambda_term [q.lambda_term.size()];
//			
//			for(int k = 0; k<q.lambda_term.size(); k++)
//			{
//				l_terms[k] = q.lambda_term.get(k);
//			}
//			
////			.addAll(q.lambda_term);
//			
//			query_lambda_str.add(l_terms);
//			
//			String sql = Query_converter.datalog2sql_citation_query(q);
//			
//			pst = c.prepareStatement(sql);
//			
//			ResultSet rs = pst.executeQuery();
//			
//			ResultSetMetaData meta = rs.getMetaData();
//			
//			int col_num = meta.getColumnCount();
//			
//			HashSet<Head_strs> h_lists = new HashSet<Head_strs>();
//			
//			while(rs.next())
//			{
//				String author_name = rs.getString(1) + " " + rs.getString(2);
//				
//				Vector<String> head_strs = new Vector<String>();
//				
//				for(int i = 0; i<col_num -2; i++)
//				{
//					head_strs.add(rs.getString(i + 3));
//				}
//				
//				Head_strs h_str = new Head_strs(head_strs);
//				
//				h_lists.add(h_str);
//				
//				head_strs.clear();
//				
//				Unique_StringList author_set = curr_author_mapping.get(h_str); 
//				
//				if(author_set == null)
//				{
//					author_set = new Unique_StringList();
//					
//					author_set.add(author_name);
//					
//					curr_author_mapping.put(h_str, author_set);
//				}
//				else
//				{
//					
//					if((max_num > 0 && author_set.size <= max_num) || max_num == 0)
//					{
//						author_set.add(author_name);
//								
//						curr_author_mapping.put(h_str, author_set);
//					}
//				}
//				
//				
//				
//				
//			}
//			
////			author_mapping.add(curr_author_mapping);
//			
//			author_mappings[j] = new Query_lm_authors();
//			
//			author_mappings[j].init(h_lists.size());
//			
//			ArrayList<Head_strs> h_arr = new ArrayList<Head_strs>();
//			
//			h_arr.addAll(h_lists);
//			
//			author_mappings[j].addAll(curr_author_mapping, h_arr);
//		}
		
		
		
//		for(int k = 0; k<curr_query_ids.size(); k++)
//		{
//			HashMap<Head_strs, HashSet<String>> curr_author_mapping = author_mapping.get(curr_query_ids.get(k));
//			
//			if(curr_author_mapping == null)
//			{
//				
//				curr_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//				
//				Query q = Query_operation.get_query_by_id(curr_query_ids.get(k), c, pst);
//				
//				ArrayList<Lambda_term> l_terms = new ArrayList<Lambda_term>();
//				
//				l_terms.addAll(q.lambda_term);
//				
//				query_lambda_str.put(curr_query_ids.get(k), l_terms);
//				
////				HashMap<Head_strs, HashSet<String>> curr_query_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//				
////				System.out.println(q);
//				
//				String sql = Query_converter.datalog2sql_citation_query(q);
//				
//				pst = c.prepareStatement(sql);
//				
//				ResultSet rs = pst.executeQuery();
//				
//				ResultSetMetaData meta = rs.getMetaData();
//				
//				int col_num = meta.getColumnCount();
//				
//				while(rs.next())
//				{
//					String author_name = rs.getString(1) + " " + rs.getString(2);
//					
//					Vector<String> head_strs = new Vector<String>();
//					
//					for(int i = 0; i<col_num -2; i++)
//					{
//						head_strs.add(rs.getString(i + 3));
//					}
//					
//					Head_strs h_str = new Head_strs(head_strs);
//					
//					head_strs.clear();
//					
//					HashSet<String> author_set = curr_author_mapping.get(h_str); 
//					
//					if(author_set == null)
//					{
//						author_set = new HashSet<String>();
//						
//						author_set.add(author_name);
//						
//						curr_author_mapping.put(h_str, author_set);
//					}
//					else
//					{
//						
//						if((max_num > 0 && author_set.size() <= max_num) || max_num == 0)
//						{
//							author_set.add(author_name);
//							
//							curr_author_mapping.put(h_str, author_set);
//						}
//					}
//					
//					
//					
//					
//				}
//				
//							
//				
//			}
//			
//			author_mapping.put(curr_query_ids.get(k), curr_author_mapping);	
//			
//		}
	}
	
	
	public static HashSet<String> get_authors3(citation_view_unparametered c_view, Connection c, PreparedStatement pst, int view_id, IntList[] view_query_mapping, HashMap<String, Unique_StringList> author_mapping, int max_num, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, HashMap<String, HashSet<String>> view_author_mapping) throws SQLException, ClassNotFoundException
	{
		if(view_author_mapping.get(c_view.toString()) != null)
		{
			return view_author_mapping.get(c_view.toString());
		}
		
		HashSet<String> authors = new HashSet<String>();
		
		
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
//		String lambda_term_str = new String();
//		
//		for(int i = 0; i<c_view.lambda_terms.size(); i++)
//		{
//			if(i >= 1)
//				lambda_term_str += " and ";
//			
//			lambda_term_str += c_view.view.subgoal_name_mapping.get(c_view.lambda_terms.get(i).table_name) + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//		}
//		
//		HashSet<String> curr_sql = new HashSet<String>();
		
//		ArrayList<Integer> curr_query_ids = view_query_mapping.get(c_view.name);
//		
//		if(curr_query_ids == null)
//		{						
//			curr_query_ids = get_query_id2(c_view.name, c, pst);
//			
//			view_query_mapping.put(c_view.name, curr_query_ids);
//			
//			for(int k = 0; k<curr_query_ids.size(); k++)
//			{
//				HashMap<Head_strs, HashSet<String>> curr_author_mapping = author_mapping.get(curr_query_ids.get(k));
//				
//				if(curr_author_mapping == null)
//				{
//					
//					curr_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//					
//					Query q = Query_operation.get_query_by_id(curr_query_ids.get(k), c, pst);
//					
//					ArrayList<Lambda_term> l_terms = new ArrayList<Lambda_term>();
//					
//					l_terms.addAll(q.lambda_term);
//					
//					query_lambda_str.put(curr_query_ids.get(k), l_terms);
//					
////					HashMap<Head_strs, HashSet<String>> curr_query_author_mapping = new HashMap<Head_strs, HashSet<String>>();
//					
////					System.out.println(q);
//					
//					String sql = Query_converter.datalog2sql_citation_query(q, max_num);
//					
//					pst = c.prepareStatement(sql);
//					
//					ResultSet rs = pst.executeQuery();
//					
//					ResultSetMetaData meta = rs.getMetaData();
//					
//					int col_num = meta.getColumnCount();
//					
//					while(rs.next())
//					{
//						String author_name = rs.getString(1) + " " + rs.getString(2);
//						
//						Vector<String> head_strs = new Vector<String>();
//						
//						for(int i = 0; i<col_num -2; i++)
//						{
//							head_strs.add(rs.getString(i + 3));
//						}
//						
//						Head_strs h_str = new Head_strs(head_strs);
//						
//						head_strs.clear();
//						
//						HashSet<String> author_set = curr_author_mapping.get(h_str); 
//						
//						if(author_set == null)
//						{
//							author_set = new HashSet<String>();
//							
//							author_set.add(author_name);
//							
//							curr_author_mapping.put(h_str, author_set);
//						}
//						else
//						{
//							if((max_num > 0 && author_set.size() <= max_num) || max_num == 0)
//							{
//								author_set.add(author_name);
//								
//								curr_author_mapping.put(h_str, author_set);
//							}
//						}
//						
//						
//						
//						
//					}
//					
//								
//					
//				}
//				
//				author_mapping.put(curr_query_ids.get(k), curr_author_mapping);	
//				
//			}
//		}
						
//		for(Iterator it = query_id_set.iterator(); it.hasNext();)
		
		IntList curr_query_ids = view_query_mapping[view_id];
		
		for(int k = 0; k<curr_query_ids.size; k++)
		{
			
//			int query_id_index = query_ids.find(curr_query_ids.list[k]);
						
//			ArrayList<Lambda_term> l_terms = query_lambda_str.get(curr_query_ids.list[k]);
						
			Vector<String> head_strs = new Vector<String>();
			
			Head_strs h_str = new Head_strs(head_strs);
			
			Unique_StringList curr_author_list = author_mapping.get(c_view.name + populate_db.separator + h_str + populate_db.separator + curr_query_ids.list[k]);
			
			if(curr_author_list != null)
			{
				for(int p = 0; p<curr_author_list.size; p++)
				{
					authors.add(curr_author_list.list[p]);
				}
					
				
				
//				authors.addAll(Arrays.asList(Arrays.copyOfRange(curr_author_list.list, 0, curr_author_list.size)));
			}
			
		}
		
		view_author_mapping.put(c_view.toString(), authors);
		
		return authors;
		
	}
	
	
	public static HashSet<String> get_authors2(citation_view_unparametered c_view, Connection c, PreparedStatement pst, HashMap<String, Vector<Integer>> view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping, int max_num) throws SQLException, ClassNotFoundException
	{
		HashSet<String> authors = new HashSet<String>();
		
		
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
//		String lambda_term_str = new String();
//		
//		for(int i = 0; i<c_view.lambda_terms.size(); i++)
//		{
//			if(i >= 1)
//				lambda_term_str += " and ";
//			
//			lambda_term_str += c_view.view.subgoal_name_mapping.get(c_view.lambda_terms.get(i).table_name) + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//		}
//		
//		HashSet<String> curr_sql = new HashSet<String>();
		
		Vector<Integer> curr_query_ids = view_query_mapping.get(c_view.name);
		
		if(curr_query_ids == null)
		{						
			curr_query_ids = get_query_id(c_view.name, c, pst);
			
			view_query_mapping.put(c_view.name, curr_query_ids);
			
			for(int k = 0; k<curr_query_ids.size(); k++)
			{
				HashMap<Head_strs, HashSet<String>> curr_author_mapping = author_mapping.get(curr_query_ids.get(k));
				
				if(curr_author_mapping == null)
				{
					
					curr_author_mapping = new HashMap<Head_strs, HashSet<String>>();
					
					Query q = Query_operation.get_query_by_id(curr_query_ids.get(k), c, pst);
					
					Vector<Lambda_term> l_terms = q.lambda_term;
					
					query_lambda_str.put(curr_query_ids.get(k), l_terms);
					
//					HashMap<Head_strs, HashSet<String>> curr_query_author_mapping = new HashMap<Head_strs, HashSet<String>>();
					
//					System.out.println(q);
					
					String sql = Query_converter.datalog2sql_citation_query(q, max_num);
					
					pst = c.prepareStatement(sql);
					
					ResultSet rs = pst.executeQuery();
					
					ResultSetMetaData meta = rs.getMetaData();
					
					int col_num = meta.getColumnCount();
					
					while(rs.next())
					{
						String author_name = rs.getString(1) + " " + rs.getString(2);
						
						Vector<String> head_strs = new Vector<String>();
						
						for(int i = 0; i<col_num -2; i++)
						{
							head_strs.add(rs.getString(i + 3));
						}
						
						Head_strs h_str = new Head_strs(head_strs);
						
						head_strs.clear();
						
						HashSet<String> author_set = curr_author_mapping.get(h_str); 
						
						if(author_set == null)
						{
							author_set = new HashSet<String>();
							
							author_set.add(author_name);
							
							curr_author_mapping.put(h_str, author_set);
						}
						else
						{
							if((max_num > 0 && author_set.size() <= max_num) || max_num == 0)
							{
								author_set.add(author_name);
								
								curr_author_mapping.put(h_str, author_set);
							}
						}
						
						
						
						
					}
					
								
					
				}
				
				author_mapping.put(curr_query_ids.get(k), curr_author_mapping);	
				
			}
		}
						
//		for(Iterator it = query_id_set.iterator(); it.hasNext();)
		for(int k = 0; k<curr_query_ids.size(); k++)
		{
			HashMap<Head_strs, HashSet<String>> curr_authors =  author_mapping.get(curr_query_ids.get(k));
			
			Vector<Lambda_term> l_terms = query_lambda_str.get(curr_query_ids.get(k));
						
			Vector<String> head_strs = new Vector<String>();
			
			Head_strs h_str = new Head_strs(head_strs);
			
			HashSet<String> curr_author_list = curr_authors.get(h_str);
			
			if(curr_author_list != null)
				authors.addAll(curr_author_list);
			
		}
		
		return authors;
		
	}
	
	
	public static HashSet<String> get_authors(citation_view_unparametered c_view, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> query_str, int max_num) throws SQLException, ClassNotFoundException
	{
//		HashSet<String> authors = new HashSet<String>();
//		
//		Vector<String> subgoals = get_subgoals(c_view.name, c, pst);
//		
//		Vector<String> conditions = get_conditions(c_view.name, c, pst);
//		
//		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
//		
//		Vector<String> curr_sql = new Vector<String>();
//		
//		for(int i = 0; i<query_components.size(); i++)
//		{
//			String query = query_components.get(i)[0];
//			
//			for(int j = 0; j<subgoals.size(); j++)
//			{
//				query += "," + subgoals.get(j) + "()";
//			}
//			
//			query += "," + query_components.get(i)[2];
//			
//			for(int j = 0; j<conditions.size(); j++)
//			{
//				query += "," + conditions.get(j);
//			}
//			
//			query += "," + query_components.get(i)[3];
//			
//			query = Tuple_reasoning1.get_full_query(query);
//			
//			Query q = Parse_datalog.parse_query(query);
//						
//			String sql = Query_converter.datalog2sql(q);
//			
//			curr_sql.add(sql);
//			
//			
//			pst = c.prepareStatement(sql);
//			
//			ResultSet rs = pst.executeQuery();
//			
//			while(rs.next())
//			{
//				authors.add(rs.getString(1) + " " + rs.getString(2));
//			}
//			
//			
//		}
//		
//		query_str.put(c_view.name, curr_sql);
//		
//		return authors;
		

		HashSet<String> authors = new HashSet<String>();
		
		Vector<Integer> query_ids = get_query_id(c_view.name, c, pst);
		
		
//		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
		String lambda_term_str = new String();
		
//		for(int i = 0; i<c_view.lambda_terms.size(); i++)
//		{
//			if(i >= 1)
//				lambda_term_str += " and ";
//			
//			lambda_term_str += c_view.lambda_terms.get(i).table_name + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//		}
		
		HashSet<String> curr_sql = new HashSet<String>();
		
		for(int k = 0; k<query_ids.size(); k++)
		{
			Query q = Query_operation.get_query_by_id(query_ids.get(k), c, pst);
			
//			System.out.println(q);
			
			String sql = Query_converter.datalog2sql_citation_query(q, max_num);
			
//			String lambda_term_q_str = new String();
			
//			for(int i = 0; i<c_view.lambda_terms.size(); i++)
//			{
//				if(i >= 1)
//					lambda_term_q_str += " and ";
//				
//				String l_name = q.lambda_term.get(i).name;
//				
//				lambda_term_q_str += q.lambda_term.get(i).table_name + "." + l_name.substring(l_name.indexOf("_") + 1, l_name.length()) + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
//			}
			
			
//			if(q.conditions.size() > 0)
//			{
//				sql += " and " + lambda_term_q_str;
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//				
//			}
//			
//			else
//			{
//				sql += " where " + lambda_term_q_str;
//				
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//			}
			
			pst = c.prepareStatement(sql);

			curr_sql.add(sql);
			
//			System.out.println("sql:::" + c_view.toString() + "::" + sql);
			
//			for(int j = 0; j<q.lambda_term.size(); j++)
//			{
//				String temp =  c_view.map.get(q.lambda_term.get(j).toString());
//				
//				pst.setString(j + 1, temp);
//				
//			}
			
//			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				authors.add(rs.getString(1) + " " + rs.getString(2));
			}
			
		}
		
		
//		for(int i = 0; i<query_components.size(); i++)
//		{
//			String query = query_components.get(i)[0];
//			
//			for(int j = 0; j<subgoals.size(); j++)
//			{
//				query += "," + subgoals.get(j) + "()";
//			}
//			
//			query += "," + query_components.get(i)[2];
//			
//			for(int j = 0; j<conditions.size(); j++)
//			{
//				query += "," + conditions.get(j);
//			}
//			
//			query += "," + query_components.get(i)[3];
//			
//			query = Tuple_reasoning1.get_full_query(query);
//			
//			Query q = Parse_datalog.parse_query(query);
//			
//			String sql = Query_converter.datalog2sql(q);
//			
//			if(q.conditions.size() > 0)
//			{
//				sql += " and " + lambda_term_str;
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//				
//			}
//			
//			else
//			{
//				sql += " where " + lambda_term_str;
//				
////				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
////				{
////					sql += " " + query_components.get(i)[1];
////				}
//			}
//			
//			pst = c.prepareStatement(sql);
//
//			curr_sql.add(sql);
//			
//			for(int j = 0; j<c_view.lambda_terms.size(); j++)
//			{
//				String temp =  c_view.map.get(c_view.lambda_terms.get(i));
//				
//				pst.setString(j + 1, temp);
//				
//			}
//			
////			pst = c.prepareStatement(sql);
//			
//			ResultSet rs = pst.executeQuery();
//			
//			while(rs.next())
//			{
//				authors.add(rs.getString(1) + " " + rs.getString(2));
//			}
//			
//			
//		}
		
		query_str.put(c_view.name, curr_sql);
		return authors;
		
	
		
	}
	
	public static Vector<String[]> get_query(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select query_table.query, query_table.suffix, query_table.condition, citation2query.conditions" + 
	" from citation_view join citation2query using (citation_view_name) join query_table using (query_id) where citation_view.view = '" + name +"'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String []> query_components = new Vector<String[]>();
		
		String[] query_component = new String[4];
		
		while(rs.next())
		{
			query_component[0] = rs.getString(1);
			
			query_component[1] = rs.getString(2);
			
			query_component[2] = rs.getString(3);
			
			query_component[3] = rs.getString(4);
		}
		
		query_components.add(query_component);
		
		return query_components;
	}
	
	
	
	public static Vector<String> get_subgoals(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select subgoal_names from view2subgoals where view = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> table_names = new Vector<String>();
		
		while(rs.next())
		{
			table_names.add(rs.getString(1));
		}
		
		return table_names;
	}
	
	public static Vector<String> get_conditions(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select conditions from view2conditions where view = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> conditions = new Vector<String>();
		
		while(rs.next())
		{
			conditions.add(rs.getString(1));
		}
		
		return conditions;
	}

}
