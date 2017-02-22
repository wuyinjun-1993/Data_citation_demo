package edu.upenn.cis.citation.gen_citation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1;

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
	
	static String db_name = "IUPHAR/BPS Guide to PHARMACOLOGY";
	
	public static String get_citation_agg(Vector<citation_view_vector> c_views) throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		citation_view_vector c_v = c_views.get(0);
		
		HashSet<String> authors = new HashSet<String>();
		
		HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
		
		for(int i = 0; i<c_v.c_vec.size(); i++)
		{
			
			
			
			if(c_v.c_vec.get(i).has_lambda_term())
			{
				authors.addAll(get_authors((citation_view_parametered)c_v.c_vec.get(i),c, pst, query_str));
			}
			else
			{
				authors.addAll(get_authors((citation_view_unparametered)c_v.c_vec.get(i),c, pst, query_str));
			}
			
		}
		
		for(int p = 1; p<c_views.size(); p++)
		{
			c_v = c_views.get(p);
			
			for(int i = 0; i<c_v.c_vec.size(); i++)
			{
				
				citation_view c_view = c_v.c_vec.get(i);
				
				Vector<String> curr_str = query_str.get(c_view.get_name());
				
				for(int j = 0; j<curr_str.size(); j++)
				{
					if(c_view.has_lambda_term())
					{
						
						pst = c.prepareStatement(curr_str.get(j));
						
						for(int k = 0; k<((citation_view_parametered)c_view).lambda_terms.size(); k++)
						{
							pst.setString(k + 1, ((citation_view_parametered)c_view).map.get(((citation_view_parametered)c_view).lambda_terms.get(i)));
						}
						
						ResultSet r = pst.executeQuery();
						
						while(r.next())
						{
							authors.add(r.getString(1) + " " + r.getString(2));
						}
					}
					else
					{
						pst = c.prepareStatement(curr_str.get(j));
						
						ResultSet s = pst.executeQuery();
						
						while(s.next())
						{
							authors.add(s.getString(1) + " " + s.getString(2));
						}
					}
				}
				
			}
		}
		
		
		
		
		
		c.close();
		
		return combine_blocks(authors, 30, new Vector<String>());
	}
	
	
	static String combine_blocks(HashSet<String> authors, int max_num, Vector<String> vals)
	{
		
		String author_str = new String();
		
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
	
	public static String get_citations(citation_view_vector c_vec, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, Vector<String>> query_str, int max_num) throws ClassNotFoundException, SQLException
	{
		String author_str = new String();
		
		HashSet<String> authors = new HashSet<String>();
		
		
		for(int i = 0; i<c_vec.c_vec.size(); i++)
		{
			if(c_vec.c_vec.get(i).has_lambda_term())
			{
				authors.addAll(get_authors((citation_view_parametered)c_vec.c_vec.get(i),c, pst, query_str));
			}
			else
			{
				authors.addAll(get_authors((citation_view_unparametered)c_vec.c_vec.get(i),c, pst, query_str));
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
						pst.setString(k + 1, ((citation_view_parametered)c_view).map.get(((citation_view_parametered)c_view).lambda_terms.get(i)));
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
	
	
	public static HashSet<String> get_authors(citation_view_parametered c_view, Connection c, PreparedStatement pst, HashMap<String, Vector<String>> query_str) throws SQLException, ClassNotFoundException
	{
		HashSet<String> authors = new HashSet<String>();
		
		Vector<String> subgoals = get_subgoals(c_view.name, c, pst);
		
		Vector<String> conditions = get_conditions(c_view.name, c, pst);
		
		
		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		
		String lambda_term_str = new String();
		
		for(int i = 0; i<c_view.lambda_terms.size(); i++)
		{
			if(i >= 1)
				lambda_term_str += " and ";
			
			lambda_term_str += c_view.lambda_terms.get(i).table_name + "." + c_view.lambda_terms.get(i).name + "::text= ?";// + c_view.map.get(c_view.lambda_terms.get(i)) + "'"; 
		}
		
		Vector<String> curr_sql = new Vector<String>();
		
		for(int i = 0; i<query_components.size(); i++)
		{
			String query = query_components.get(i)[0];
			
			for(int j = 0; j<subgoals.size(); j++)
			{
				query += "," + subgoals.get(j) + "()";
			}
			
			query += "," + query_components.get(i)[2];
			
			for(int j = 0; j<conditions.size(); j++)
			{
				query += "," + conditions.get(j);
			}
			
			query += "," + query_components.get(i)[3];
			
			query = Tuple_reasoning1.get_full_query(query);
			
			Query q = Parse_datalog.parse_query(query);
			
			String sql = Query_converter.datalog2sql(q);
			
			if(q.conditions.size() > 0)
			{
				sql += " and " + lambda_term_str;
//				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
//				{
//					sql += " " + query_components.get(i)[1];
//				}
				
			}
			
			else
			{
				sql += " where " + lambda_term_str;
				
//				if(query_components.get(i)[1] != null && !query_components.get(i)[1].isEmpty())
//				{
//					sql += " " + query_components.get(i)[1];
//				}
			}
			
			pst = c.prepareStatement(sql);

			curr_sql.add(sql);
			
			for(int j = 0; j<c_view.lambda_terms.size(); j++)
			{
				String temp =  c_view.map.get(c_view.lambda_terms.get(i));
				
				pst.setString(j + 1, temp);
				
			}
			
//			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				authors.add(rs.getString(1) + " " + rs.getString(2));
			}
			
			
		}
		
		query_str.put(c_view.name, curr_sql);
		return authors;
		
	}
	
	
	
	public static HashSet<String> get_authors(citation_view_unparametered c_view, Connection c, PreparedStatement pst, HashMap<String, Vector<String>> query_str) throws SQLException, ClassNotFoundException
	{
		HashSet<String> authors = new HashSet<String>();
		
		Vector<String> subgoals = get_subgoals(c_view.name, c, pst);
		
		Vector<String> conditions = get_conditions(c_view.name, c, pst);
		
		
		Vector<String []> query_components= get_query(c_view.name, c, pst);
		
		Vector<String> curr_sql = new Vector<String>();
		
		for(int i = 0; i<query_components.size(); i++)
		{
			String query = query_components.get(i)[0];
			
			for(int j = 0; j<subgoals.size(); j++)
			{
				query += "," + subgoals.get(j) + "()";
			}
			
			query += "," + query_components.get(i)[2];
			
			for(int j = 0; j<conditions.size(); j++)
			{
				query += "," + conditions.get(j);
			}
			
			query += "," + query_components.get(i)[3];
			
			query = Tuple_reasoning1.get_full_query(query);
			
			Query q = Parse_datalog.parse_query(query);
						
			String sql = Query_converter.datalog2sql(q);
			
			curr_sql.add(sql);
			
			
			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				authors.add(rs.getString(1) + " " + rs.getString(2));
			}
			
			
		}
		
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
