package tuple_reasoning;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import Corecover.*;

import Operation.Conditions;
import Pre_processing.populate_db;

import java.util.Set;
import java.util.Vector;

import citation_view.*;
import datalog.Parse_datalog;
import datalog.Query_converter;
import schema_reasoning.Gen_citation1;

public class Tuple_reasoning2 {
	
	static String query;
	
	static HashMap<String, Tuple> map = new HashMap<String, Tuple>();
	
	static HashMap<String, String> view_citation_map = new HashMap<String, String>();
	
	static Vector<Conditions> conditions = new Vector<Conditions>();
	
	static HashMap<Conditions, Vector<String>> conditions_map = new HashMap<Conditions, Vector<String>>();
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		
		String query = "q(text1, text2):introduction_c(f1,text1), introduction_c(f2,text2), f1 <> f2";

		long start_time = System.currentTimeMillis();
		
		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query);
		
		output_sql(citation_views);
		
		long end_time = System.currentTimeMillis();
		
		System.out.println(end_time - start_time);

	}
	
	public static void gen_citation_all(Vector<Vector<citation_view_vector>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<citation_view_vector> c_vec = citation_views.get(i);
			
			for(int j = 0; j< c_vec.size(); j++)
			{
				gen_citation(c_vec.get(j));
			}
		}
	}
	
	public static void output_sql(Vector<Vector<citation_view_vector>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<citation_view_vector> c_vec = citation_views.get(i);
			
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
	
	
	public static Vector<Query> pre_processing(Query q, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Vector<Query> views = Gen_citation1.get_views_schema();

		
	    q = q.minimize();

	    // construct the canonical db
	    Database canDb = CoreCover.constructCanonicalDB(q);
	    UserLib.myprintln("canDb = " + canDb.toString());

	    // compute view tuples
	    HashSet viewTuples = CoreCover.computeViewTuples(canDb, views);

	    // compute tuple-cores
	    CoreCover.computeTupleCores(viewTuples, q);
	    
	    CoreCover.computeNumVTs(viewTuples);
	    
	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
	    {
	    	Tuple tuple = (Tuple) iter.next();
	    	
	    	String c_name_query = "select citation_view_name from citation_view where view_name = '" + tuple.name + "'";
	    	
	    	pst = c.prepareStatement(c_name_query);
	    	
	    	ResultSet rs = pst.executeQuery();
	    	
	    	if(rs.next())
	    	{
	    		map.put(rs.getString(1), tuple);
	    		view_citation_map.put(tuple.name, rs.getString(1));
	    	}
	    	
	    	Vector<Conditions> update_conditions = new Vector<Conditions>();
	    	
	    	update_conditions.addAll(conditions);
	    	
	    	for(int i = 0; i<tuple.conditions.size(); i++)
	    	{
	    		int j = 0;
	    		
	    		for(j = 0; j<conditions.size(); j++)
	    		{
	    			if(Conditions.compare(conditions.get(j), tuple.conditions.get(i)))
	    			{
	    				break;
	    			}
	    		}
	    		
	    		if(j >= conditions.size())
	    		{
	    			update_conditions.add(tuple.conditions.get(i));
	    			
	    			Vector<String> str_vec = new Vector<String>();
	    			
	    			str_vec.add(tuple.name);
	    			
	    			conditions_map.put(tuple.conditions.get(i), str_vec);
	    		}
	    		else
	    		{
	    			Vector<String> str_vec = conditions_map.get(conditions.get(j));
	    			
	    			str_vec.add(tuple.name);
	    			
	    		}
	    	}
	    	
	    	
	    	conditions = update_conditions;
	    	
	    }
	    
	    return views;
	    
	    
	}
	
	public static Vector<Vector<citation_view_vector>> gen_citation_main(String query) throws ClassNotFoundException, SQLException
	{
		
		
		
		Query q = Parse_datalog.parse_query(query);
		
		
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
	        "postgres","123");
	    
		pre_processing(q,c,pst);
		
		Vector<Vector<citation_view_vector>> citation_views = get_citation_views(q, c, pst);
		
		return citation_views;
		
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
	
	public static void gen_citation(citation_view_vector citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.c_vec.size();i++)
		{
			String query = citation_views.c_vec.get(i).get_full_query();
			
			System.out.println(query);
		}
	}
	
	public static Vector<Vector<citation_view_vector>> get_citation_views(Query query,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<citation_view_vector>> c_views = query_execution(query, c, pst);
				
		return c_views;
	}
	
	
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
	
	public static Vector<Conditions> remove_conflict_duplicate(Query query)
	{
		if(conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
		{
			return conditions;
		}
		else
		{

			Vector<Conditions> update_conditions = new Vector<Conditions>();
			
			boolean remove = false;
			
			for(int i = 0; i<conditions.size(); i++)
			{
				remove = false;
				
				for(int j = 0; j<query.conditions.size(); j++)
				{
					if(Conditions.compare(query.conditions.get(j), conditions.get(i)) || Conditions.compare(query.conditions.get(j), Conditions.negation(conditions.get(i))))
					{
						remove = true;
						break;
					}
				}
				
				if(!remove)
					update_conditions.add(conditions.get(i));
			}
			
			conditions = update_conditions;
			
			return conditions;
			
		}
	}
	
	public static Vector<Vector<citation_view_vector>> query_execution(Query query, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
				
		Vector<Vector<citation_view_vector>> c_views = new Vector<Vector<citation_view_vector>>();
				
		Vector<String> subgoal_names = new Vector<String>();
				
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
		Vector<Conditions> valid_conditions = remove_conflict_duplicate(query);
		
		
		String sql = Query_converter.datalog2sql_citation(query, false);
		
		
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			subgoal_names.add(subgoal.name);
							
		}
		
		
		if(valid_conditions.isEmpty())
			reasoning(c_views, query, subgoal_names, c, pst, sql, map);
		else
		{
			HashSet<Integer> set = new HashSet<Integer>();
			
			for(int i = 0; i<valid_conditions.size(); i++)
			{
				set.add(i);
			}
			
			HashSet subsets = UserLib.genSubsets(set);
			
			for (Iterator iter = subsets.iterator(); iter.hasNext();) 
			{
				HashSet subset = (HashSet) iter.next();
				
				HashMap<String, Tuple> curr_map = (HashMap<String, Tuple>) map.clone();
				
				Vector<Conditions> curr_conditions = new Vector<Conditions>();
				
				curr_conditions.addAll(valid_conditions);
				
				
				for(Iterator it = subset.iterator(); it.hasNext();)
				{
					Integer index = (Integer) it.next();
					
					Vector<String> str_name = conditions_map.get(curr_conditions.get(index));
					
					for(int k = 0; k<str_name.size(); k++)
					{
						curr_map.remove(view_citation_map.get(str_name.get(k)));
					}
					
					curr_conditions.get(index).negation();
					
				}
				
				String where = gen_conditions_query (curr_conditions);
				
				String curr_sql = new String();
				
				if(sql.contains("where"))
					curr_sql = sql + " and " + where;
				else
					curr_sql = sql + " where " + where;
				
				reasoning(c_views, query, subgoal_names, c, pst, curr_sql, curr_map);
				
				
			}
		}
		
		
				
//		Query_converter.post_processing(query);
		
		return c_views;
	}
	
	
	public static void reasoning(Vector<Vector<citation_view_vector>> c_views, Query query, Vector<String> subgoal_names, Connection c, PreparedStatement pst, String sql, HashMap<String, Tuple> curr_map) throws SQLException, ClassNotFoundException
	{
		pst = c.prepareStatement(sql);
		
		ResultSet rs = pst.executeQuery();
		
		ResultSetMetaData r = rs.getMetaData();
		
		
				
		boolean first = true;
		
		while(rs.next())
		{
			Vector<String[]> c_units = new Vector<String[]>();
			for(int i = 0; i<query.body.size();i++)
			{
				String[] c_unit= rs.getString(i+1).split("\\"+ populate_db.separator);
				c_units.add(c_unit);
			}
			
			
			
			if(first)
			{
				Vector<Vector<citation_view>> c_unit_vec = get_citation_units(c_units);
				
				Vector<citation_view_vector> c_unit_combinaton = get_valid_citation_combination(c_unit_vec,subgoal_names, curr_map);
				
				c_views.add(c_unit_combinaton);
				
				first = false;
			
			}
			
			else
			{
				
				HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
				
				Vector<citation_view_vector> curr_c_view = c_views.get(0);
				
				Vector<citation_view_vector> insert_c_view = (Vector<citation_view_vector>) curr_c_view.clone();
				
				Vector<citation_view_vector> update_c_view = update_valid_citation_combination(insert_c_view,c_unit_vec);
				
				c_views.add(update_c_view);
			}
		}

	}
	
	public static Vector<citation_view_vector> update_valid_citation_combination(Vector<citation_view_vector> insert_c_view, HashMap<String,citation_view> c_unit_vec)
	{
		
		Vector<citation_view_vector> update_c_views = new Vector<citation_view_vector>();
		
		for(int i = 0; i<insert_c_view.size();i++)
		{
			citation_view_vector insert_c_view_vec = insert_c_view.get(i);
			
			Vector<citation_view> update_c_view_vec = new Vector<citation_view>();
			
			for(int j = 0; j < insert_c_view_vec.c_vec.size(); j++)
			{
				citation_view c = insert_c_view_vec.c_vec.get(j);
				
				c = c_unit_vec.get(String.valueOf(c.get_index()));
				
				update_c_view_vec.add(c);
			}
			
			update_c_views.add(new citation_view_vector(update_c_view_vec, insert_c_view_vec.index_vec, insert_c_view_vec.view_strs));
		}
		
		return update_c_views;
	}
	
	public static Vector<Vector<citation_view>> get_citation_units(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view>> c_views = new Vector<Vector<citation_view>>();
		
		for(int i = 0; i<c_units.size(); i++)
		{
			Vector<citation_view> c_view = new Vector<citation_view>();
			String [] c_unit_str = c_units.get(i);
			for(int j = 0; j<c_unit_str.length; j++)
			{
				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
				{
					
					String c_view_name = c_unit_str[j].split("\\(")[0];
					
					Vector<String> c_view_paras = new Vector<String>();
					
					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
					
					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
					
					curr_c_view.put_paramters(c_view_paras);
					
					c_view.add(curr_c_view);
				}
				else
				{
					c_view.add(new citation_view_unparametered(c_unit_str[j]));
				}
			}
			
			c_views.add(c_view);
		}
		
		return c_views;
	}
	
	
	public static HashMap<String,citation_view> get_citation_units_unique(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
	{
//		Vector<citation_view> c_views = new Vector<citation_view>();
		
		HashMap<String,citation_view> c_view_map = new HashMap<String, citation_view>();
		
		for(int i = 0; i<c_units.size(); i++)
		{
//			Vector<citation_view> c_view = new Vector<citation_view>();
			String [] c_unit_str = c_units.get(i);
			for(int j = 0; j<c_unit_str.length; j++)
			{
				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
				{
					
					String c_view_name = c_unit_str[j].split("\\(")[0];
					
					Vector<String> c_view_paras = new Vector<String>();
					
					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
					
					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
					
					curr_c_view.put_paramters(c_view_paras);
					
					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
				}
				else
				{
					citation_view_unparametered curr_c_view = new citation_view_unparametered(c_unit_str[j]);
					
					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
				}
			}
			
		}
		
//		Set set = c_view_map.keySet();
//		
//		for(Iterator iter = set.iterator();iter.hasNext();)
//		{
//			String key = (String) iter.next();
//			
//			c_views.add(c_view_map.get(key));
//		}
		
		return c_view_map;
	}
	
	
	
	public static Vector<citation_view_vector> get_valid_citation_combination(Vector<Vector<citation_view>>c_unit_vec, Vector<String> subgoal_names, HashMap<String, Tuple> curr_map)
	{
		HashMap<String, citation_view_vector> c_combinations = new HashMap<String, citation_view_vector>();
		
		Vector<Vector<String>> c_subgoal_names = new Vector<Vector<String>>();
		
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
			
			Vector<citation_view> curr_combination = new Vector<citation_view>();
			
			Vector<Vector<String>> curr_subgoal_name = new Vector<Vector<String>>();
			
			//check validation
			for(int j = 0; j < curr_c_unit_vec.size(); j++)
			{
				citation_view curr_c_unit = curr_c_unit_vec.get(j);
				
				Vector<String> core_str = new Vector<String>();
				
				if(curr_map.get(curr_c_unit.get_name()) == null)
					continue;
				
				HashSet core_set = curr_map.get(curr_c_unit.get_name()).core;
				
				for(Iterator iter = core_set.iterator(); iter.hasNext();)
				{
					Subgoal subgoal = (Subgoal) iter.next();
					
					core_str.add(subgoal.name);
				}

				
				if(subgoal_names.containsAll(curr_c_unit.get_table_names()) && core_str.contains(subgoal_names.get(i)))
				{
					curr_combination.add(curr_c_unit);
					curr_subgoal_name.add(curr_c_unit.get_table_names());
				}
			}
			
			c_combinations = join_operation(c_combinations, curr_combination, i);
			
//			c_combinations = remove_duplicate(c_combinations);
		}
		
		return remove_duplicate_final(c_combinations);
	}
	
	
	public static HashMap<String, citation_view_vector> remove_duplicate(HashMap<String, citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		HashMap<String, citation_view_vector> update_combinations = new HashMap<String, citation_view_vector>();
		
		Set set = c_combinations.keySet();
		
		for(Iterator iter = set.iterator(); iter.hasNext();)
		{
			String str = (String) iter.next();
			
			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(str);
			
			for(Iterator iterator = set.iterator(); iterator.hasNext();)
			{
				String string = (String) iterator.next();
				
				if(str!=string)
				{
					citation_view_vector curr_combination = c_combinations.get(string);
					
					if(c_combination.index_vec.containsAll(curr_combination.index_vec))
					{
						contains = true;
						
						break;
					}
				}
				
			}
			
			if(!contains)
				update_combinations.put(str, c_combination);
		}
		
				
		return update_combinations;
	}
	
	public static Vector<citation_view_vector> remove_duplicate_final(HashMap<String, citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
		
		Set set = c_combinations.keySet();
		
		for(Iterator iter = set.iterator(); iter.hasNext();)
		{
			String str = (String) iter.next();
			
			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(str);
			
			for(Iterator iterator = set.iterator(); iterator.hasNext();)
			{
				String string = (String) iterator.next();
				
				if(str!=string)
				{
					citation_view_vector curr_combination = c_combinations.get(string);
					
					if(c_combination.index_vec.containsAll(curr_combination.index_vec))
					{
						contains = true;
						
						break;
					}
				}
				
			}
			
			if(!contains)
				update_combinations.add(c_combination);
		}
		
				
		return update_combinations;
	}
	
	public static HashMap<String, citation_view_vector> join_operation(HashMap<String, citation_view_vector> c_combinations, Vector<citation_view> insert_citations,  int i)
	{
		if(i == 0)
		{
			for(int k = 0; k<insert_citations.size(); k++)
			{
				Vector<citation_view> c = new Vector<citation_view>();
				
				c.add(insert_citations.get(k));
				
				String str = String.valueOf(insert_citations.get(k).get_index());
				
				c_combinations.put(str, new citation_view_vector(c));
				
			}
			
			return c_combinations;
		}
		else
		{
			HashMap<String, citation_view_vector> updated_c_combinations = new HashMap<String, citation_view_vector>();
			
			Vector<Vector<String>> updated_c_subgoals = new Vector<Vector<String>>();
			
			Set set = c_combinations.keySet();
			
			for(Iterator iter = set.iterator(); iter.hasNext();)
			{
				String string = (String) iter.next();
				
				citation_view_vector curr_combination = c_combinations.get(string);
								
				for(int k = 0; k<insert_citations.size(); k++)
				{
					
					citation_view_vector new_citation_vec = citation_view_vector.merge(curr_combination, insert_citations.get(k));
					
					char [] index_vec = new char[new_citation_vec.index_vec.size()]; 
							
					for(int r = 0; r < new_citation_vec.index_vec.size(); r++)
					{
						index_vec[r] = new_citation_vec.index_vec.get(r);
					}
					
					Arrays.sort(index_vec);
//					
					String str = String.valueOf(index_vec);
					
					updated_c_combinations.put(str, new_citation_vec);
					
//					if(insert_citations.get(k).get_table_names().containsAll(curr_subgoal))
//					{
//						Vector<citation_view> c = new Vector<citation_view>();
//						
//						c.add(insert_citations.get(k));
//						
//						updated_c_combinations.add(c);
//						
//						updated_c_subgoals.add(insert_citations.get(k).get_table_names());
//					}
//					else
//					{
//						if(curr_subgoal.containsAll(insert_citations.get(k).get_table_names()))
//						{
//							updated_c_combinations.add((Vector<citation_view>) curr_combination.clone());
//							
//							updated_c_subgoals.add(curr_subgoal);
//						}
//						
//						else
//						{
//							Vector<citation_view> c = new Vector<citation_view>();
//							
//							Vector<String> str_vec = new Vector<String>();
//							
//							c = (Vector<citation_view>) curr_combination.clone();
//							
//							str_vec = (Vector<String>) curr_subgoal.clone();
//							
//							str_vec.addAll(insert_citations.get(k).get_table_names());
//							
//							c.add(insert_citations.get(k));
//							
//							updated_c_combinations.add(c);
//							
//							updated_c_subgoals.add(str_vec);
//						}
//					}
				}
			}
						
			return updated_c_combinations;
			
		}
	}
	
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
	
	public static Vector<Vector<citation_view>> str2c_view(Vector<Vector<String>> citation_str) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view>> c_view = new Vector<Vector<citation_view>>();
		
		for(int i = 0; i<citation_str.size(); i++)
		{
			Vector<String> curr_c_view_str = citation_str.get(i);
			
			Vector<citation_view> curr_c_view = new Vector<citation_view>();
			
			for(int j = 0; j<curr_c_view_str.size();j++)
			{
				if(citation_str.get(i).contains("(") && citation_str.get(i).contains(")"))
				{
					String citation_view_name = curr_c_view_str.get(i).split("\\(")[0];
					
					String [] citation_parameters = curr_c_view_str.get(i).split("\\(")[1].split("\\)")[0].split(",");
					
					Vector<String> citation_para_vec = new Vector<String>();
					
					citation_para_vec.addAll(Arrays.asList(citation_parameters));
					
					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
					
					inserted_citation_view.put_paramters(citation_para_vec);
										
					curr_c_view.add(inserted_citation_view);
				}
				else
				{
					curr_c_view.add(new citation_view_unparametered(curr_c_view_str.get(i)));
				}
			}
			
			c_view.add(curr_c_view);
		}
		
		return c_view;
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
	
	public static Vector<citation_view> str2c_unit(String [] citation_str) throws ClassNotFoundException, SQLException
	{
		Vector<citation_view> c_view = new Vector<citation_view>();
			
			for(int j = 0; j<citation_str.length;j++)
			{
				if(citation_str[j].contains("(") && citation_str[j].contains(")"))
				{
					String citation_view_name = citation_str[j].split("\\(")[0];
					
					String [] citation_parameters = citation_str[j].split("\\(")[1].split("\\)")[0].split(",");
					
					Vector<String> citation_para_vec = new Vector<String>();
					
					citation_para_vec.addAll(Arrays.asList(citation_parameters));
					
					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
					
					inserted_citation_view.put_paramters(citation_para_vec);
										
					c_view.add(inserted_citation_view);
				}
				else
				{
					c_view.add(new citation_view_unparametered(citation_str[j]));
				}
			}
		
		return c_view;
	}
	

//    static Query parse_query(String query)
//    {
//        
//        String head = query.split(":")[0];
//        
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
//        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        return new Query(head_name, head_subgoal, body_subgoals);
//    }
//    
//    static Query parse_query(String head, String []body, String []lambda_term_str)
//    {
//                
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
////        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        
//        
//        Vector<Argument> lambda_terms = new Vector<Argument>();
//        
//        if(lambda_term_str != null)
//        {
//	        for(int i =0;i<lambda_term_str.length; i++)
//	        {
//	        	lambda_terms.add(new Argument(lambda_term_str[i]));
//	        }
//        
//        }
//        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
//    }


}
