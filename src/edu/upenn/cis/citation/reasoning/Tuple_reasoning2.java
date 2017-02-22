package edu.upenn.cis.citation.reasoning;
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
import edu.upenn.cis.citation.aggregation.Aggregation1;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.output.output2excel;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class Tuple_reasoning2 {
	
	static String query;
	
	static HashMap<String, Tuple> map = new HashMap<String, Tuple>();
	
	static HashMap<String, Vector<Integer>> view_tuple_subgoal_map = new HashMap<String, Vector<Integer>>();
	
	static HashMap<String, String> view_citation_map = new HashMap<String, String>();
	
	static Vector<Conditions> conditions = new Vector<Conditions>();
	
	static HashMap<String, Vector<Tuple>> conditions_map = new HashMap<String, Vector<Tuple>>();
	
	static HashMap<String, HashSet<String>> return_vals = new HashMap<String, HashSet<String>>();//table-vector<lambda_terms>
	
	static HashMap<String, HashMap<String, Integer>> view_lambda_term_map = new HashMap<String, HashMap<String, Integer>>();
	
	static Vector<String[]> table_lambda_terms = new Vector<String[]>();
	
	static HashMap<String, HashMap<String, Integer>> table_lambda_term_seq = new HashMap<String, HashMap<String, Integer>>();
	
	static HashMap<String, HashMap<String, Integer>> citation_condition_id_map = new HashMap<String, HashMap<String, Integer>>();
	
	static HashMap<String, Boolean> view_type_map = new HashMap<String, Boolean>();
	
	static HashMap<String, Integer> query_subgoal_id_map = new HashMap<String, Integer>();
	
	static String file_name = "tuple_level1.xls";
	
	static Vector<Tuple> web_views = new Vector<Tuple>();
	
	static int max_author_num = 0;
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		
//		String query = "q(object_c_object_id): object_c(), gpcr_c(), interaction_c(), gpcr_c_object_id = object_c_object_id, interaction_c_object_id = gpcr_c_object_id";
//
//		query = get_full_query(query);
//		
//		long start_time = System.currentTimeMillis();
//		
//		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query);
//		
//		output_sql(citation_views);
//		
//		long end_time = System.currentTimeMillis();
//		
//		System.out.println(end_time - start_time);
		String query = "q(family_c_family_id, family_c_name):family_c()";
		
		Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
		
		Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning2.tuple_reasoning(query, citation_strs);
		
		Vector<String> agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views);
		
		Vector<Integer> ids = new Vector<Integer>();
		
		Vector<String> subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);

	}
	
	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views, Vector<Integer> ids) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views, ids);
		
		Vector<String> citation_aggs = new Vector<String>();
		
		for(int i = 0; i<agg_res.size(); i++)
		{
			String str = gen_citation1.get_citation_agg(agg_res.get(i));
			
			citation_aggs.add(str);
//			
			System.out.print(agg_res.get(i).get(0).toString() + ":");
			
			System.out.println(str);

		}
		
		return citation_aggs;
	}
	
	public static Vector<Vector<citation_view_vector>> tuple_reasoning(String query, Vector<Vector<String>> citation_strs) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		query = get_full_query(query);
				
		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query, citation_strs);
		
		
		
		return citation_views;
	}
	
	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views);
		
		Vector<String> citation_aggs = new Vector<String>();
		
		for(int i = 0; i<agg_res.size(); i++)
		{
			String str = gen_citation1.get_citation_agg(agg_res.get(i));
			
			citation_aggs.add(str);
//			
			System.out.print(agg_res.get(i).get(0).toString() + ":");
			
			System.out.println(str);

		}
		
		return citation_aggs;
	}
	
	static String get_full_query(String query) throws ClassNotFoundException, SQLException
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
	
	public static void query_lambda_term_table(Vector<String> subgoal_names, Query query, Connection c, PreparedStatement pst) throws SQLException
	{
		String q = "select * from view2lambda_term";
		
//		HashMap<String, String[]> lambda_term_table_map = new HashMap<String, String[]>();
		
		pst = c.prepareStatement(q);
		
		ResultSet rs = pst.executeQuery();
					
		while(rs.next())
		{
			String view_name = rs.getString(1);
			
			String lambda_term = rs.getString(2);
			
			String table_name = rs.getString(3);
			
			if(subgoal_names.contains(table_name))
			{
				HashSet<String> lambda_terms = return_vals.get(table_name); 
				
				if(lambda_terms==null)
				{
					lambda_terms = new HashSet<String>();
					
					lambda_terms.add(lambda_term);
					
					return_vals.put(table_name, lambda_terms);
					
					String [] com = new String[2];
					
					com[0] = table_name;
					
					com[1] = lambda_term;
					
					table_lambda_terms.add(com);
					
					
					HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
					
					if(l_seq == null)
					{
						l_seq = new HashMap<String, Integer>();
						
						l_seq.put(lambda_term, return_vals.get(table_name).size());
						
						table_lambda_term_seq.put(table_name, l_seq);
						
					}
					else
					{
						l_seq.put(lambda_term, return_vals.get(table_name).size());
					}
					
					
					
					HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
					
					if(lambda_term_seq == null)
					{
						lambda_term_seq = new HashMap<String, Integer>();
						
						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
						
						view_lambda_term_map.put(view_name, lambda_term_seq);
					}
					else
					{
						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
					}
					
				}
				else
				{
					if(!lambda_terms.contains(lambda_term))
					{
						lambda_terms.add(lambda_term);
						
						String [] com = new String[2];
						
						com[0] = table_name;
						
						com[1] = lambda_term;
						
						table_lambda_terms.add(com);
						
						HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
						
						if(l_seq == null)
						{
							l_seq = new HashMap<String, Integer>();
							
							l_seq.put(lambda_term, return_vals.get(table_name).size());
							
							table_lambda_term_seq.put(table_name, l_seq);
							
						}
						else
						{
							l_seq.put(lambda_term, return_vals.get(table_name).size());
						}
						
						
						
						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
						
						if(lambda_term_seq == null)
						{
							lambda_term_seq = new HashMap<String, Integer>();
							
							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
							
							view_lambda_term_map.put(view_name, lambda_term_seq);
						}
						else
						{
							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
						}
						
					}
					else
					{
						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
						
						int seq = table_lambda_term_seq.get(table_name).get(lambda_term);
						
						if(lambda_term_seq == null)
						{
							lambda_term_seq = new HashMap<String, Integer>();
							
							lambda_term_seq.put(lambda_term, seq);
							
							view_lambda_term_map.put(view_name, lambda_term_seq);
						}
						else
						{
							lambda_term_seq.put(lambda_term, seq);
						}
					}
				}
				
				
							
				
			}
			
//			lambda_term_table_map.put(rs.getString(1), table_lambda_terms);
		}
	}
	
	public static Vector<Query> get_views_schema()
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
	      Vector<Query> views = new Vector<Query>();
	      
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection(populate_db.db_url,
	        	        populate_db.usr_name,populate_db.passwd);
	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
	         pst = c.prepareStatement("SELECT *  FROM view_table");
	         rs = pst.executeQuery();
	         
	            while (rs.next()) {
	            	
	            	String head_name = rs.getString(1);
	            	
	            	boolean web_view = rs.getBoolean(3);
	            	
	            	if(!web_view)
	            	{
	            		Vector<String> subgoal_names = new Vector<String>();
		            	
		            	Vector<String> subgoal_arguments = new Vector<String>();
		            	
		            	String subgoal_string = get_subgoals(subgoal_names, subgoal_arguments, head_name, c, pst);	            	
		            	
		            	String query = head_name + "(" + rs.getString(2) + "):" + subgoal_string;
		            	
		                Query view = Parse_datalog.parse_query(query);
		               
		                view.lambda_term = get_lambda_terms(head_name, c, pst);
		                check_equality(view, c, pst);
		                views.add(view);
		                
		                view_type_map.put(view.name, false);
	            	}
	            	else
	            	{
	            		
	            		String q = "select renamed_view, subgoal, head_variable from web_view_table where view ='" + head_name + "'";
	            		
	            		pst = c.prepareStatement(q);
	            		
	            		ResultSet r = pst.executeQuery();
	            		
	            		while(r.next())
	            		{
	            			
	            			String curr_name = r.getString(1);
	            			
	            			String curr_subgoal = r.getString(2);
	            			
	            			String head_vars = r.getString(3);
	            			
	            			String subgoal_args = get_args_web_view(curr_subgoal, c, pst);
	            			
	            			String query = curr_name + "(" + head_vars + "):" + curr_subgoal + "(" + subgoal_args + ")";
	            			
	            			Query view = Parse_datalog.parse_query(query);
	 		               
			                view.lambda_term = get_lambda_terms_web_view(head_name, c, pst);
			                check_equality(view, c, pst);
			                
			                view_type_map.put(view.name, true);
			                
			                view.web_view = true;
			                
			                views.add(view);
	            			
	            		}

	            	}

	            }
	  	      c.close();

	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      
	      return views;
	}
	
	static String get_args_web_view(String subgoal, Connection c, PreparedStatement pst) throws SQLException
	{
		String q = "select arguments from subgoal_arguments where subgoal_names ='" + subgoal + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		String subgoal_args = new String();
		
		if(r.next())
		{
			subgoal_args += r.getString(1);
		}
		
		return subgoal_args;
		
	}
	
	public static void check_equality(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String q_conditions = "select conditions from view2conditions where view = '" + view.name + "'";
		
		pst = c.prepareStatement(q_conditions);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		while(r.next())
		{
			
			String condition_str = r.getString(1);
			
			String []strs = null;
			
			Operation op = null;
			
			
			if(condition_str.contains(op_equal.op))
			{
				strs = condition_str.split(op_equal.op);
				
				op = new op_equal();
			}
			else
			{
				if(condition_str.contains(op_less.op))
				{
					strs = condition_str.split(op_less.op);
					
					op = new op_less();
				}
				else
				{
					if(condition_str.contains(op_greater.op))
					{
						strs = condition_str.split(op_greater.op);
						
						op = new op_greater();
					}
					else
					{
						if(condition_str.contains(op_less_equal.op))
						{
							strs = condition_str.split(op_less_equal.op);
							
							op = new op_less_equal();
						}
						else
						{
							if(condition_str.contains(op_greater_equal.op))
							{
								strs = condition_str.split(op_greater_equal.op);
								
								op = new op_greater_equal();
							}
							else
							{
								if(condition_str.contains(op_not_equal.op))
								{
									strs = condition_str.split(op_not_equal.op);
									
									op = new op_not_equal();
								}
							}
						}
					}
				}
				
			}
			
			
			String str1 = strs[0];
			
			String str2 = strs[1];
			

			String []strs1 = str1.split("_");
			
			String subgoal1 = strs1[0] + "_" + strs1[1];
			
			String arg1 = str1.trim();//.substring(subgoal1.length() + 1, str1.length());
			
			String subgoal2 = new String();
			
			String arg2 = new String ();
			
			if(str2.contains("'"))
			{
				arg2 = str2;
			}
			else
			{
				String []strs2 = str2.split("_");
				
				subgoal2 = strs2[0] + "_" + strs2[1];
				
				arg2 = str2.trim();//.substring(subgoal2.length() + 1, str2.length());
			}
				
			Conditions condition = new Conditions(new Argument(arg1, arg1), subgoal1.trim(), op, new Argument(arg2, arg2), subgoal2.trim());
			
			conditions.add(condition);
		}
		
		view.conditions = conditions;
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
	
	public static HashSet pre_processing(Vector<String> subgoal_names, Query q, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Vector<Query> views = get_views_schema();

		
	    q = q.minimize();

	    // construct the canonical db
	    Database canDb = CoreCover.constructCanonicalDB(q);
	    UserLib.myprintln("canDb = " + canDb.toString());

	    // compute view tuples
	    HashSet viewTuples = CoreCover.computeViewTuples(canDb, views);

	    // compute tuple-cores
	    CoreCover.computeTupleCores(viewTuples, q);
	    
	    CoreCover.computeNumVTs(viewTuples);
	    
	    query_lambda_term_table(subgoal_names, q, c, pst);	    
	    
	    
	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
	    {
	    	Tuple tuple = (Tuple) iter.next();
	    	
	    	if(tuple.web_view)
	    	{
	    		web_views.add(tuple);
	    	}
	    	
	    	HashMap map_subgoals = tuple.mapSubgoals;
	    	
	    	Vector<Lambda_term> lambda_terms = tuple.query.lambda_term;
	    	
	    	Set set = map_subgoals.keySet();
	    	
	    	Vector<Integer> ids = new Vector<Integer>();
	    	
	    	for(Iterator it = set.iterator(); it.hasNext();)
	    	{
	    		Subgoal subgoal = (Subgoal)it.next();
	    		
	    		Subgoal map_subgoal = (Subgoal) map_subgoals.get(subgoal);
	    		
	    		int id = query_subgoal_id_map.get(map_subgoal.toString());
	    		
	    		ids.add(id);
	    		
	    		for(int k = 0; k<lambda_terms.size(); k++)
	    		{
	    			if(lambda_terms.get(k).table_name.equals(subgoal.name))
	    			{
	    				for(int p = 0; p<subgoal.args.size(); p++)
	    				{
	    					
	    					Argument arg = (Argument)subgoal.args.get(p);
	    					if(arg.name.equals(lambda_terms.get(k).name))
	    					{
	    						lambda_terms.get(k).table_index = id;
	    						
	    						break;
	    					}
	    				}
	    			}
	    		}
	    		
	    		
	    	}
	    	
	    	
	    	
	    	view_tuple_subgoal_map.put(tuple.toString(), ids);
	    		    	
	    	
	    	
	    	
	    	
	    	String c_name_query = "select citation_view_name from citation_view where view = '" + tuple.name + "'";
	    	
	    	pst = c.prepareStatement(c_name_query);
	    	
	    	ResultSet rs = pst.executeQuery();
	    	
	    	if(rs.next())
	    	{
	    		map.put(tuple.name, tuple);
	    		view_citation_map.put(tuple.name, tuple.name);
	    	}

	    	
	    	for(int i = 0; i<tuple.conditions.size(); i++)
	    	{
//	    		int j = 0;
//	    		
//	    		for(j = 0; j<conditions.size(); j++)
//	    		{
//	    			if(Conditions.compare(conditions.get(j), tuple.conditions.get(i)))
//	    			{
//	    				break;
//	    			}
//	    		}
	    		Conditions condition = tuple.conditions.get(i);
	    		
	    		String condition_str = get_condition_str(tuple, condition);
	    		
	    		condition.expression = condition_str;
	    		
//	    		
	    		if(conditions_map.get(condition_str) == null)
	    		{	    			
	    			Vector<Tuple> str_vec = new Vector<Tuple>();
	    			
	    			str_vec.add(tuple);
	    			
	    			conditions_map.put(condition_str, str_vec);
	    			
	    			conditions.add(condition);
	    		}
	    		else
	    		{
	    			Vector<Tuple> str_vec = conditions_map.get(condition_str);
	    			
	    			if(!str_vec.contains(tuple))
	    				str_vec.add(tuple);
	    			
	    		}
	    		
	    		
	    		HashMap<String, Integer> condition_id_map = citation_condition_id_map.get(tuple.name);
	    		
	    		if(condition_id_map == null)
	    		{
	    			condition_id_map = new HashMap<String, Integer>();
	    			
	    			condition_id_map.put(tuple.conditions.get(i).toString(), i);
	    			
	    			citation_condition_id_map.put(tuple.name, condition_id_map);
	    		}
	    		else
	    		{
	    			condition_id_map.put(tuple.conditions.get(i).toString(), i);
	    		}
	    	}

	    	    	
	    }
	    
	    return viewTuples;
	    
	    
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
	
	public static Vector<Vector<citation_view_vector>> gen_citation_main(String query, Vector<Vector<String>> citation_strs) throws ClassNotFoundException, SQLException
	{
		
		
		
		Query q = Parse_datalog.parse_query(query);
		
		for(int i = 0; i<q.body.size(); i++)
		{
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			query_subgoal_id_map.put(subgoal.toString(),i);
		}
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    
	    Vector<String> subgoal_names = new Vector<String>();
	    
		for(int i = 0; i < q.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) q.body.get(i);
						
			subgoal_names.add(subgoal.name);
							
		}
	    
		HashSet views = pre_processing(subgoal_names, q,c,pst);
		
		Vector<Vector<citation_view_vector>> citation_views = get_citation_views(views, q, c, pst, citation_strs);
		
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
	
	public static Vector<Vector<citation_view_vector>> get_citation_views(HashSet views, Query query,Connection c, PreparedStatement pst, Vector<Vector<String>> citation_strs) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<citation_view_vector>> c_views = query_execution(views, query, c, pst, citation_strs);
				
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
	
	public static Vector<Vector<citation_view_vector>> query_execution(HashSet views, Query query, Connection c, PreparedStatement pst, Vector<Vector<String>> citation_strs) throws SQLException, ClassNotFoundException
	{
				
		Vector<Vector<citation_view_vector>> c_views = new Vector<Vector<citation_view_vector>>();
				
		Vector<String> subgoal_names = new Vector<String>();
				
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
		Vector<Conditions> valid_conditions = remove_conflict_duplicate(query);
		
		Vector<Conditions> condition_seq = new Vector<Conditions>();
		
		HashMap<String, Integer> table_pos_map = new HashMap<String, Integer>();
				
		Vector<int[]> condition_seq_id = new Vector<int []>();
		
		Vector<Integer> start_pos = new Vector<Integer>();
		
		int [] lambda_term_num = new int[1];
		
		String sql = Query_converter.datalog2sql_citation2(query, valid_conditions, condition_seq, return_vals, start_pos, table_pos_map, false, condition_seq_id, lambda_term_num);
		
		
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			subgoal_names.add(subgoal.name);
							
		}
		
		
//		if(valid_conditions.isEmpty())
		citation_strs = reasoning(views, c_views, query, subgoal_names, c, pst, sql, map, start_pos, condition_seq, condition_seq_id, lambda_term_num[0]);

//			reasoning(views, c_views, query, subgoal_names, c, pst, sql, map, valid_conditions, condition_seq, start_pos, table_pos_map, condition_seq_id, lambda_term_num[0]);
//		else
//		{
//			HashSet<Integer> set = new HashSet<Integer>();
//			
//			for(int i = 0; i<valid_conditions.size(); i++)
//			{
//				set.add(i);
//			}
//			
//			HashSet subsets = UserLib.genSubsets(set);
//			
//			for (Iterator iter = subsets.iterator(); iter.hasNext();) 
//			{
//				HashSet subset = (HashSet) iter.next();
//				
//				HashMap<String, Tuple> curr_map = (HashMap<String, Tuple>) map.clone();
//				
//				Vector<Conditions> curr_conditions = new Vector<Conditions>();
//				
//				curr_conditions.addAll(valid_conditions);
//				
//				
//				for(Iterator it = subset.iterator(); it.hasNext();)
//				{
//					Integer index = (Integer) it.next();
//					
//					Vector<String> str_name = conditions_map.get(curr_conditions.get(index));
//					
//					for(int k = 0; k<str_name.size(); k++)
//					{
//						curr_map.remove(view_citation_map.get(str_name.get(k)));
//					}
//					
//					curr_conditions.get(index).negation();
//					
//				}
//				
//				String where = gen_conditions_query (curr_conditions);
//				
//				String curr_sql = new String();
//				
//				if(sql.contains("where"))
//					curr_sql = sql + " and " + where;
//				else
//					curr_sql = sql + " where " + where;
//				
//				reasoning(c_views, query, subgoal_names, c, pst, curr_sql, curr_map);
//				
//				
//			}
//		}
		
		
				
//		Query_converter.post_processing(query);
		
		return c_views;
	}
	
	public static void initial_conditions_all(Query query, Vector<HashMap<String,boolean[]>> conditions_all)
	{
		
		
		
		for(int i = 0; i < query.body.size(); i++)
		{
			HashMap<String, boolean[]> conditions = new HashMap<String, boolean[]>();
			
			Set set = citation_condition_id_map.keySet();
			
			for(Iterator iter = set.iterator(); iter.hasNext();)
			{
				String c_name = (String)iter.next();
				
				HashMap<String, Integer> citation_map = citation_condition_id_map.get(c_name);
				
				int size = citation_map.size();
				
				boolean[] b_values = new boolean[size];
				
//				for(int k = 0; k<size; k++)
//				{
//					b_values.add(false);
//				}
				
				conditions.put(c_name, b_values);
			}
			
			
			conditions_all.add(conditions);
		}
	}
	
	static void reasoning_single_tuple(Vector<Vector<citation_view_vector>> c_views, HashSet views, Query query, Vector<Integer> start_pos, ResultSet rs) throws ClassNotFoundException, SQLException
	{
		HashSet<Rewriting> rewritings = CoreCover.coverQuerySubgoals(views, query);
		
		Vector<citation_view_vector> c_view_vec = new Vector<citation_view_vector>();
		
		for(Iterator iter = rewritings.iterator(); iter.hasNext();)
		{
			Rewriting rw = (Rewriting) iter.next();
			
			
			HashSet<Tuple> v_tuples = rw.viewTuples;
			
			Vector<citation_view> citation_views = new Vector<citation_view>();
			
			for(Iterator it = v_tuples.iterator();it.hasNext();)
			{
				Tuple tuple = (Tuple) it.next();
								
				Vector<Lambda_term> l_terms = tuple.query.lambda_term;
				
				Vector<String> values = new Vector<String>();
				
				
				for(int p = 0; p<l_terms.size(); p++)
				{
					
					int offset = table_lambda_term_seq.get(l_terms.get(p).table_name).get(l_terms.get(p).name);
					
					int index = l_terms.get(p).table_index;
					
					int pos = start_pos.get(index);
					
					int position = pos  + query.head.args.size() + offset;
					
					l_terms.get(p).id = position;
					
					values.add(rs.getString(position));
					
					
				}
				
				if(l_terms.size() == 0)
				{
					citation_view c_v = new citation_view_unparametered(tuple.name);
					
					citation_views.add(c_v);
				}
				else
				{
					citation_view_parametered c_v = new citation_view_parametered(tuple.name, false);
					
					c_v.lambda_terms = l_terms;
					
					c_v.put_paramters(values);
					
					citation_views.add(c_v);
				}
				
				
			}
			
			citation_view_vector c_vector = new citation_view_vector(citation_views);
			
			c_view_vec.add(c_vector);
		}
		
		c_views.add(c_view_vec);
	}
	
	static void remove_invalid_web_view(HashSet curr_views, int pos2, int col_num, ResultSet rs) throws SQLException
	{
		HashMap<Integer, Vector<Tuple>> map_ids = new HashMap<Integer, Vector<Tuple>>();
		for(int j = 0; j<web_views.size(); j++)
		{
			int id = view_tuple_subgoal_map.get(web_views.get(j).toString()).get(0);
			
			if(map_ids.get(id) == null)
			{
				Vector<Tuple> tuples = new Vector<Tuple>();
				
				tuples.add(web_views.get(j));
				
				map_ids.put(id, tuples);
				
			}
			else
			{
				Vector<Tuple> tuples = map_ids.get(id);
				
				tuples.add(web_views.get(j));
			}
			
			
		}
		
		
		for(int i = pos2; i<col_num; i++)
		{
			
			String web_view_str = rs.getString(i + 1);
			
			if(web_view_str != null)
			{
				String[] web_view_seq = web_view_str.split("\\" + populate_db.separator);
				
				Vector<String> web_view_vec = new Vector<String>();
				
				web_view_vec.addAll(Arrays.asList(web_view_seq));
				
				Vector<Tuple> tuples = map_ids.get(i - pos2);
				
				for(int k = 0; k<tuples.size(); k++)
				{
					if(!web_view_vec.contains(tuples.get(k).name))
					{
						curr_views.remove(tuples.get(k));
					}
				}
				
				
			}
			else
			{
				Vector<Tuple> tuples = map_ids.get(i - pos2);
				
				if(tuples != null)
				for(int k = 0; k<tuples.size(); k++)
				{
						curr_views.removeAll(tuples);
				}
			}

		}
	}
	
	static void populate_citation(Vector<citation_view_vector> update_c_view, Vector<String> vals, Connection c, PreparedStatement pst, Vector<Vector<String>> citation_strs, Vector<HashMap<String, Vector<String>>> citation_strings) throws SQLException
	{
		Vector<String> citations = new Vector<String>();
		
		
		for(int p =0; p<update_c_view.size(); p++)
		{
			
			String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings.get(p), max_author_num);

			citations.add(str);
			
//			HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
//			
//			String str = gen_citation1.get_citations(c_unit_combinaton.get(p), vals, c, pst, query_str);
//
//			citations.add(str);
			
//			citation_strings.add(query_str);
			
		}
		
		citation_strs.add(citations);
	}
	
	static void gen_citation(Vector<Vector<citation_view_vector>> c_views, Vector<String> vals, Connection c, PreparedStatement pst, Vector<Vector<String>> citation_strs, Vector<HashMap<String, Vector<String>>> citation_strings) throws ClassNotFoundException, SQLException
	{
		Vector<String> citations = new Vector<String>();
		
		for(int p =0; p<c_views.lastElement().size(); p++)
		{
			
			HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
			
			String str = gen_citation1.get_citations(c_views.lastElement().get(p), vals, c, pst, query_str, max_author_num);

			citations.add(str);
			
			citation_strings.add(query_str);
			
		}
		
		citation_strs.add(citations);
	}
	
	public static Vector<Vector<String>> reasoning(HashSet views, Vector<Vector<citation_view_vector>> c_views, Query query, Vector<String> subgoal_names, Connection c, PreparedStatement pst, String sql, HashMap<String, Tuple> curr_map, Vector<Integer> start_pos, Vector<Conditions> condition_seq, Vector<int[]> condition_seq_id, int lambda_term_num) throws SQLException, ClassNotFoundException
	{
		pst = c.prepareStatement(sql);
		
		ResultSet rs = pst.executeQuery();
		
		ResultSetMetaData r = rs.getMetaData();
		
		int col_num = r.getColumnCount();
						
		String old_value = new String();
		
		Vector<Vector<String>> values = new Vector<Vector<String>>();
		
		Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
		
		Vector<HashMap<String, Vector<String>>> citation_strings = new Vector<HashMap<String, Vector<String>>>();
		
		if(!condition_seq.isEmpty())
		{
			while(rs.next())
			{				
				String curr_str = new String();
				
				Vector<String> vals = new Vector<String>();
				
				for(int i = 0; i<query.head.args.size(); i++)
				{
					vals.add(rs.getString(i+1));
				}
				
				values.add(vals);
				
				int pos1 = query.head.args.size() + lambda_term_num;
				
				for(int i = pos1; i< pos1 + condition_seq.size(); i++)
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
				
//				int pos2 = pos1 + condition_seq.size();
//				
//				for(int i = pos2; i<col_num; i++)
//				{
//					String web_view = rs.getString(i + 1);
//					
//					if(web_view == null)
//						curr_str += web_view;
//				}
				
				if(!curr_str.equals(old_value))
				{
					
					HashSet curr_views = (HashSet) views.clone();
										
					for(int i = pos1; i< pos1 + condition_seq.size(); i++)
					{
						boolean condition = rs.getBoolean(i + 1);
						
						if(!condition)
						{							
							String condition_str = condition_seq.get(i).expression;
							
							Vector<Tuple> invalid_views = conditions_map.get(condition_str);
							
							curr_views.removeAll(invalid_views);
						}
					}
					
					
//					remove_invalid_web_view(curr_views, pos1, col_num,  rs);
					
					
					reasoning_single_tuple(c_views, curr_views, query, start_pos, rs);
					

					gen_citation(c_views, vals, c, pst, citation_strs, citation_strings);
					
				}
				
				else
				{
					
//					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
					
					Vector<citation_view_vector> curr_c_view = c_views.lastElement();
					
					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
					
					for(int i = 0; i<curr_c_view.size(); i++)
					{
						insert_c_view.add(curr_c_view.get(i).clone());
					}
					
					Vector<citation_view_vector> update_c_view = update_valid_citation_combination(insert_c_view, rs);
					
					c_views.add(update_c_view);
					
					populate_citation(update_c_view, vals, c, pst, citation_strs, citation_strings);
				}
			}
		}
		else
		{
			
			boolean first = true;
						
			while(rs.next())
			{

				String curr_str = new String();
				
				Vector<String> vals = new Vector<String>();
				
				for(int i = 0; i<query.head.args.size(); i++)
				{
					vals.add(rs.getString(i+1));
				}
				
				values.add(vals);
				
				int pos1 = query.head.args.size() + lambda_term_num;
								
//				for(int i = pos1; i<col_num; i++)
//				{
//					String web_view = rs.getString(i + 1);
//					
//					if(web_view != null)
//						curr_str += web_view;
//				}
				int pos2 = pos1 + condition_seq.size();

				
				if(first)
				{
					HashSet curr_views = (HashSet) views.clone();
					
					
//					remove_invalid_web_view(curr_views, pos2, col_num,  rs);
					
					reasoning_single_tuple(c_views, curr_views, query, start_pos, rs);
											
					old_value = curr_str;
				
					gen_citation(c_views, vals, c, pst, citation_strs, citation_strings);
					
					first = false;
				}
				
				else
				{
					
//					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
					
					Vector<citation_view_vector> curr_c_view = c_views.lastElement();
					
					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
					
					for(int i = 0; i<curr_c_view.size(); i++)
					{
						insert_c_view.add(curr_c_view.get(i).clone());
					}
					
					Vector<citation_view_vector> update_c_view = update_valid_citation_combination(insert_c_view, rs);
					
					c_views.add(update_c_view);
					
					populate_citation(update_c_view, vals, c, pst, citation_strs, citation_strings);
				}
			}
		}
			
//		output2excel.citation_output(rs, query, values, c_views, file_name, values);
		
		return citation_strs;
		

	}
	
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
	
	
	public static void output_vec_com(Vector<citation_view_vector>c_unit_combinaton)
	{
		for(int i = 0; i<c_unit_combinaton.size(); i++)
		{
			if(i >= 1)
				System.out.print(",");
			
			System.out.print(c_unit_combinaton.get(i));
		}
		System.out.println();
	}
	
	public static Vector<citation_view_vector> update_valid_citation_combination(Vector<citation_view_vector> insert_c_view, ResultSet rs) throws SQLException
	{
		
		for(int i = 0; i<insert_c_view.size(); i++)
		{
			
			citation_view_vector c_vec = insert_c_view.get(i);
			
			for(int j = 0; j<c_vec.c_vec.size(); j++)
			{
				citation_view c_view = c_vec.c_vec.get(j);
				
				if(c_view.has_lambda_term())
				{
					citation_view_parametered curr_c_view = (citation_view_parametered) c_view;
					
					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					for(int k = 0; k<lambda_terms.size(); k++)
					{
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(lambda_terms.get(k).id));
					}
					
				}
				
			}
		}
		
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
		
		return insert_c_view;
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
	
	public static Vector<Vector<citation_view>> get_citation_units(Vector<String[]> c_units, HashSet<String> invalid_citation_unit, HashMap<String, Vector<Integer>> table_pos_map, ResultSet rs, int start_pos) throws ClassNotFoundException, SQLException
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
					
					if(invalid_citation_unit.contains(c_view_name))
						continue;
					
					
					Vector<String> c_view_paras = new Vector<String>();
					
					
					if(view_type_map.get(c_view_name))
					{
						c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
						
						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, true);
						
						int k = 0;
						
						for(k = 0; k<c_view_paras.size(); k++)
						{
							
							String t_name = curr_c_view.lambda_terms.get(k).table_name;
							
							Vector<Integer> positions = table_pos_map.get(t_name);
							
							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(k).name);
							
							int[] final_pos = new int[1];
							
							if(check_value(c_view_paras.get(k), start_pos, positions, rs, offset, final_pos))
							{
								
								curr_c_view.lambda_terms.get(k).id = final_pos[0];
								
								continue;
							}
							else
								break;
							
						}
						
						if(k < c_view_paras.size())
							continue;
						
						
						curr_c_view.put_paramters(c_view_paras);
						
						c_view.add(curr_c_view);
						
					}
					
					else
					{
						String []curr_c_unit = c_unit_str[j].split("\\(")[1].split("\\)");
												
						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
						
						if(curr_c_unit.length == 0)
						{
							int [] final_pos = new int[1];
							
							String t_name = curr_c_view.lambda_terms.get(0).table_name;
							
							Vector<Integer> positions = table_pos_map.get(t_name);
							
							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(0).name);
							
							Vector<Integer> ids = new Vector<Integer>();
							
							Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
							
							for(int p = 0; p<vals.size(); p++)
							{								
								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
								
								Vector<String> val = new Vector<String>();
								
								
								val.add(vals.get(p));
								
								inserted_view.put_paramters(val);
								
								inserted_view.lambda_terms.get(p).id = ids.get(p);
								
								c_view.add(inserted_view);
							}							
						}
						else
						{
							String [] c_v_unit = curr_c_unit[0].split(",");
													
							Vector<Vector<String>> values = new Vector<Vector<String>>();
							
							Vector<Vector<Integer>> id_arrays = new Vector<Vector<Integer>>();
							
							for(int p = 0; p<c_v_unit.length; p++)
							{								
								if(c_v_unit[p].isEmpty())
								{
									int [] final_pos = new int[1];
									
									String t_name = curr_c_view.lambda_terms.get(p).table_name;
									
									Vector<Integer> positions = table_pos_map.get(t_name);
									
									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
									
									Vector<Integer> ids = new Vector<Integer>();
									
									Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
									
									values = update_views(values, vals, id_arrays, ids);
									
									
								}
								else
								{
									
									String t_name = curr_c_view.lambda_terms.get(0).table_name;
									
									Vector<Integer> positions = table_pos_map.get(t_name);
									
									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
									
									int[] final_pos = new int[1];
									
									Vector<Integer> ids = new Vector<Integer>();
									
									get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
									
									id_arrays.add(ids);
									
									if(!values.isEmpty())
									{
										for(int t = 0; t<values.size(); t++)
										{
											Vector<String> v = values.get(t);
											
											v.add(c_v_unit[p]);
										}
									}
									else
									{
										Vector<String> v= new Vector<String>();
										
										v.add(c_v_unit[p]);
										
										values.add(v);
									}
									
									
								}
							}
							
							for(int p = 0; p<values.size(); p++)
							{
								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
								
								Vector<String> val = values.get(p);
								
								Vector<Integer> ids = id_arrays.get(p);
															
								inserted_view.put_paramters(val);
								
								for(int w = 0; w < ids.size(); w++)
								{
									inserted_view.lambda_terms.get(w).id = ids.get(w);
								}
								
								c_view.add(inserted_view);
							}
							
						}
						
						
						
//						HashMap<String, Integer> lambda_term_map = view_lambda_term_map.get(c_view_name);
//						
//						Set name_set = lambda_term_map.entrySet();
//						
//						for(Iterator iter = name_set.iterator(); iter.hasNext(); )
//						{
//							
//						}
//						
//						if(curr_c_unit.isEmpty())
//						{
//							
//						}
//						else
//						{
//							String [] curr_c_units = curr_c_unit.split(",");
//							
//							for(int t = 0; t<curr_c_units.length; t++)
//							{
//								if(curr_c_units)
//							}
//							
//							if(curr_c_units)
//							
//						}
						
					}
					
					


					
//					curr_c_view.lambda_terms.
					
				}
				else
				{
					if(invalid_citation_unit.contains(c_unit_str[j]))
						continue;
					
					c_view.add(new citation_view_unparametered(c_unit_str[j]));
				}
			}
			
			c_views.add(c_view);
		}
		
		return c_views;
	}
	
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
	
	public static Vector<Vector<citation_view>> get_citation_units_condition(Vector<String[]> c_units, Vector<HashMap<String, boolean[]>> conditions_all, HashMap<String, Vector<Integer>> table_pos_map, ResultSet rs, int start_pos) throws ClassNotFoundException, SQLException
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
					
					if(citation_condition_id_map.get(c_view_name)!=null)
					{
						boolean []b_vals = conditions_all.get(i).get(c_view_name);
						
						int k = 0;
						
						
						for(k = 0; k<b_vals.length; k++)
						{
							if(b_vals[k] == false)
								break;
						}
						
						if(k < b_vals.length)
							continue;
						
						
					}
					
					Vector<String> c_view_paras = new Vector<String>();
					
					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
					
					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
					
					int k = 0;
					
					for(k = 0; k<c_view_paras.size(); k++)
					{
						
						String t_name = curr_c_view.lambda_terms.get(k).table_name;
						
						Vector<Integer> positions = table_pos_map.get(t_name);
						
						int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(k).name);
						
						int[] final_pos = new int[1];
						
						if(check_value(c_view_paras.get(k), start_pos, positions, rs, offset, final_pos))
						{
							
							curr_c_view.lambda_terms.get(k).id = final_pos[0];
							
							continue;
						}
						else
							break;
						
					}
					
					if(k < c_view_paras.size())
						continue;
					
					
					curr_c_view.put_paramters(c_view_paras);

					
//					curr_c_view.lambda_terms.
					
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
