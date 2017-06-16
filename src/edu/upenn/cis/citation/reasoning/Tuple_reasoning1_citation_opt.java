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
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import static org.junit.Assert.*;


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
import edu.upenn.cis.citation.aggregation.Aggregation1;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.output.output2excel;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class Tuple_reasoning1_citation_opt {
		
	static int max_author_num = 0;
			
	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static HashSet<Conditions> valid_conditions = new HashSet<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, Integer> condition_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, Vector<String>> conditions_map = new HashMap<String, Vector<String>>();
	
	static HashMap<String, Vector<String>> lambda_terms_map = new HashMap<String, Vector<String>>();
	
	static HashMap<String, Vector<Tuple>> tuple_mapping = new HashMap<String, Vector<Tuple>>();
							
	static String file_name = "tuple_level.xlsx";
	
	static HashMap<Head_strs, Vector<HashSet<String>>> authors = new HashMap<Head_strs, Vector<HashSet<String>>>();
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		
		Vector<String> s1 = new Vector<String>();
		
		Vector<String> s2 = new Vector<String>();
		
		s1.add("1");
		
		s1.add("1");
		
		s1.add("2");
		
		s2.add("1");
		
		s2.add("2");
		
		s2.add("2");
		
//		s1.removeAll(s2);
		
		System.out.println(s1.equals(s2));
		
		
		
		
		
		
		
		
		
		
		Query q = gen_query();
		

		
//		String query = "q(name):object(), in_gtip = 'true'";
//		
		
		
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map3 = null;
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = null;
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = null;
		
		Vector<Head_strs> head_vals = new Vector<Head_strs>();
		
		long t0 = 0;
		
		long t1 = 0;
		
		long t2 = 0;
		
		long t3 = 0;
		
		long t4 = 0;
		
		long time0 = 0;
		
		long time1 = 0;
		
		long time2 = 0;
		
		
		t0 = System.nanoTime();
		
		for(int i = 0; i<100; i++)
		{
			
			citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
			
			HashMap<Head_strs, Vector<String> > citation_strs1 = new HashMap<Head_strs, Vector<String> >();
			
			Vector<Vector<citation_view_vector>> c_views = tuple_reasoning(q, citation_strs1, head_vals, file_name, citation_view_map1);
		}
		
		
//		t1 = System.nanoTime();
		
//		System.out.println("method1");
		
		
		
		
		
		
				
		t1 = System.nanoTime();
		
//		System.out.println("method2");
		
		for(int i = 0; i<100; i++)
		{
			
			citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

			HashMap<Head_strs, Vector<String> > citation_strs3 = new HashMap<Head_strs, Vector<String> >();

			
			Vector<Vector<citation_view_vector>> c_views1 = Tuple_reasoning2.tuple_reasoning(q, citation_strs3, citation_view_map2);

		}
		
		
		
		
		t2 = System.nanoTime();
		
		for(int i = 0; i<100; i++)
		{
			
			HashMap<Head_strs, Vector<String> > citation_strs2 = new HashMap<Head_strs, Vector<String> >();
			
			citation_view_map3 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();


			
			Vector<Vector<citation_view_vector>> c_views2 = Tuple_reasoning2_opt.tuple_reasoning(q, citation_strs2, citation_view_map3);

		}
				
		
		t3 = System.nanoTime();
		
		time0 = (t1 - t0) / 100;
		
		time1 = (t3 - t2)/ 100;
		
		time2 = (t2 - t1) / 100;
		
		System.out.println("time in tuple level:" + time0);
		
		System.out.println("time with opt:" + time1);
		
		System.out.println("time without opt:" + time2);
		
		compare(citation_view_map2, citation_view_map3);
		
		compare(citation_view_map1, citation_view_map2);
//		
		compare(citation_view_map1, citation_view_map3);
		
		
//		Vector<String> agg_citations = tuple_gen_agg_citations(c_views);
//		
//		Vector<Integer> ids = new Vector<Integer>();
//		
//		Vector<String> subset_agg_citations = tuple_gen_agg_citations(c_views, ids);
		
		

	}
	
	static void compare(HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2)
	{
		
		System.out.println("COMPARE::::");
		
		Set<Head_strs> key_set = citation_view_map1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			Vector<Vector<citation_view_vector>> c_views1 = citation_view_map1.get(h_val);
			
			Vector<Vector<citation_view_vector>> c_views2 = citation_view_map2.get(h_val);
			
			System.out.println(h_val.toString());
			
			compare(c_views1, c_views2);
		}
		
		
		
	}
	
	static void compare(Vector<Vector<citation_view_vector>> c_views, Vector<Vector<citation_view_vector>> c_views1)
	{
		for(int i = 0; i<c_views.size(); i++)
		{
			int j = 0;
			
//			System.out.println(c_views.get(i));
			
			for(j = 0; j<c_views1.size(); j++)
			{
				if(do_compare(c_views.get(i), c_views1.get(j)))
					break;
			}
			
			if(j >= c_views1.size())
			{
				System.out.println("errrror");
				
				System.out.println(c_views.get(i).toString());
				
				System.out.println(c_views1.get(0).toString());
				
				assertEquals(1, 0);
			}
		}
	}
	
	
	static boolean do_compare(Vector<citation_view_vector> c1, Vector<citation_view_vector> c2)
	{
		if(c1.size() != c2.size())
			return false;
		
		for(int i = 0; i<c1.size(); i++)
		{
			int j = 0;
			
//			System.out.println("id1:::" + c1.get(i).index_str);

			
			for(j = 0; j<c2.size(); j++)
			{
				
				
//				System.out.println("id2:::" + c2.get(j).index_str);
				
				if(c1.get(i).equals(c2.get(j)))
				{
					break;
				}
			}
			if(j >= c2.size())
			{
				System.out.println("id1:::" + c1.get(i));
				return false;
			}
		}
		return true;
	}
	
	
	static Query gen_query() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family_family_id", "family"));
				
		
//		head_args.add(new Argument("family1_family_id", "family1"));
//		
//		head_args.add(new Argument("family2_family_id", "family2"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		
		head_args.add(new Argument("introduction_family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
//		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
		subgoals.add(new Subgoal("family", args1));
		
//		subgoals.add(new Subgoal("family1", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		subgoals.add(new Subgoal("introduction", args3));
		
//		subgoals.add(new Subgoal("introduction1", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
		
//		subgoal_name_mapping.put("family1", "family");
//		
//		subgoal_name_mapping.put("family2", "family");
		
//		subgoal_name_mapping.put("family3", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");
//
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");


		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views);
		
		Vector<String> citation_aggs = new Vector<String>();
		
		for(int i = 0; i<agg_res.size(); i++)
		{
//			output_vec_com(agg_res.get(i));
			
			String str = gen_citation1.get_citation_agg(agg_res.get(i));
			
			citation_aggs.add(str);
//			
			System.out.print(agg_res.get(i).get(0).toString() + ":");
			
			System.out.println(str);

		}
		
		return citation_aggs;
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
//			System.out.print(agg_res.get(i).get(0).toString() + ":");
//			
//			System.out.println(str);

		}
		
		return citation_aggs;
	}
	
	
	public static Vector<Vector<citation_view_vector>> tuple_reasoning(Query query, HashMap<Head_strs, Vector<String> > citation_strs, Vector<Head_strs> head_vals, String f_name, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
//		query = get_full_query(query);
//		
//		System.out.println(query);
		
		file_name = f_name;
				
		Vector<Vector<citation_view_vector>> citation_views = tuple_reasoning(query, citation_strs, head_vals, citation_view_map1);
		
		
		
		return citation_views;
	}
	
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
				
				Vector<String> args = Parse_datalog.get_columns(subgoals[i]);
				
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
	
//	public static void query_lambda_term_table(Vector<String> subgoal_names, Query query, Connection c, PreparedStatement pst) throws SQLException
//	{
//		String q = "select * from view2lambda_term";
//		
////		HashMap<String, String[]> lambda_term_table_map = new HashMap<String, String[]>();
//		
//		pst = c.prepareStatement(q);
//		
//		ResultSet rs = pst.executeQuery();
//					
//		while(rs.next())
//		{
//			String view_name = rs.getString(1);
//			
//			String lambda_term = rs.getString(2);
//			
//			String table_name = rs.getString(3);
//			
//			if(subgoal_names.contains(table_name))
//			{
//				HashSet<String> lambda_terms = return_vals.get(table_name); 
//				
//				if(lambda_terms==null)
//				{
//					lambda_terms = new HashSet<String>();
//					
//					lambda_terms.add(lambda_term);
//					
//					return_vals.put(table_name, lambda_terms);
//					
//					String [] com = new String[2];
//					
//					com[0] = table_name;
//					
//					com[1] = lambda_term;
//					
//					table_lambda_terms.add(com);
//					
//					
//					HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//					
//					if(l_seq == null)
//					{
//						l_seq = new HashMap<String, Integer>();
//						
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						table_lambda_term_seq.put(table_name, l_seq);
//						
//					}
//					else
//					{
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//					
//					
//					HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//					
//					if(lambda_term_seq == null)
//					{
//						lambda_term_seq = new HashMap<String, Integer>();
//						
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						view_lambda_term_map.put(view_name, lambda_term_seq);
//					}
//					else
//					{
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//				}
//				else
//				{
//					if(!lambda_terms.contains(lambda_term))
//					{
//						lambda_terms.add(lambda_term);
//						
//						String [] com = new String[2];
//						
//						com[0] = table_name;
//						
//						com[1] = lambda_term;
//						
//						table_lambda_terms.add(com);
//						
//						HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//						
//						if(l_seq == null)
//						{
//							l_seq = new HashMap<String, Integer>();
//							
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							table_lambda_term_seq.put(table_name, l_seq);
//							
//						}
//						else
//						{
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//						
//						
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//					}
//					else
//					{
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						int seq = table_lambda_term_seq.get(table_name).get(lambda_term);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, seq);
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, seq);
//						}
//					}
//				}
//				
//				
//							
//				
//			}
//			
////			lambda_term_table_map.put(rs.getString(1), table_lambda_terms);
//		}
//	}
//	
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
	            	
	            	String view_id = rs.getString(1);
	            	
	            	views.add(view_operation.get_view_by_id(view_id));
	            	
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
				
			Conditions condition = new Conditions(new Argument(arg1.trim(), arg1.trim()), subgoal1.trim(), op, new Argument(arg2.trim(), arg2.trim()), subgoal2.trim());
			
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
				
				if(args.get(i).toString().equals(condition.subgoal1 + "_" + condition.arg1) || (condition.subgoal2 != null && condition.subgoal2.isEmpty() && args.get(i).toString().equals(condition.subgoal2 + "_" + condition.arg2)))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static Vector<Query> pre_processing(Query q, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		Vector<Query> views = populate_db.get_views_schema(c, pst);
		
		for(int k = 0; k<views.size(); k++)
		{
			view_mapping.put(views.get(k).name, views.get(k));
		}

		
//	    q = q.minimize();

	    // construct the canonical db
	    Database canDb = CoreCover.constructCanonicalDB(q);
	    UserLib.myprintln("canDb = " + canDb.toString());

	    // compute view tuples
	    HashSet viewTuples = CoreCover.computeViewTuples(canDb, views);
	    
	    viewTuples = check_distinguished_variables(viewTuples, q);
	    	    	    
	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
	    {
	    	Tuple tuple = (Tuple)iter.next();
	    	
	    	if(tuple_mapping.get(tuple.name) == null)
	    	{
	    		Vector<Tuple> tuples = new Vector<Tuple>();
	    		
	    		tuples.add(tuple);
	    		
	    		tuple_mapping.put(tuple.name, tuples);
	    	}
	    	else
	    	{
	    		Vector<Tuple> tuples = tuple_mapping.get(tuple.name);
	    		
	    		tuples.add(tuple);
	    		
	    		tuple_mapping.put(tuple.name, tuples);
	    	}
	    	
	    	
	    	for(int i = 0; i<tuple.conditions.size(); i++)
	    	{
	    		String condition_str = tuple.conditions.get(i).toString();
	    			    		
//	    		if(condition_id_mapping.get(condition_str) == null)
	    		{
//	    			condition_id_mapping.put(condition_str, valid_conditions.size());
	    			
	    			valid_conditions.add(tuple.conditions.get(i));
	    		}
	    	}
	    	
	    	for(int i = 0; i<tuple.lambda_terms.size(); i++)
	    	{
	    		String lambda_str = tuple.lambda_terms.get(i).toString();
	    			    		
	    		if(lambda_term_id_mapping.get(lambda_str) == null)
	    		{
	    			lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
	    			
	    			valid_lambda_terms.add(tuple.lambda_terms.get(i));
	    		}
	    	}
	    	
	    }

	    // compute tuple-cores
	    
	    return views;
	    
	    
	}
		
	public static Vector<Vector<citation_view_vector>> tuple_reasoning(Query q, HashMap<Head_strs, Vector<String> > citation_strs,Vector<Head_strs> head_vals, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		
//		Query q = Parse_datalog.parse_query(query, valid_subgoal_id);
		
//		Query alt_q = gen_alternative_query(q, valid_subgoal_id);
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    
		pre_processing(q ,c,pst);
		
		Vector<Vector<citation_view_vector>> citation_views = get_citation_views(q, c, pst, citation_strs, head_vals, citation_view_map1);
		
		c.close();
		
		
		
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
	
	public static Vector<Vector<citation_view_vector>> get_citation_views(Query query,Connection c, PreparedStatement pst, HashMap<Head_strs, Vector<String> > citation_strs, Vector<Head_strs> head_vals, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		Vector<Vector<citation_view_vector>> c_views = query_execution(query, c, pst, citation_strs, head_vals, citation_view_map1);
				
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
	
//	public static Vector<Conditions> remove_conflict_duplicate(Query query)
//	{
//		if(conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
//		{
//			return conditions;
//		}
//		else
//		{
//
//			Vector<Conditions> update_conditions = new Vector<Conditions>();
//			
//			boolean remove = false;
//			
//			for(int i = 0; i<conditions.size(); i++)
//			{
//				remove = false;
//				
//				for(int j = 0; j<query.conditions.size(); j++)
//				{
//					if(Conditions.compare(query.conditions.get(j), conditions.get(i)) || Conditions.compare(query.conditions.get(j), Conditions.negation(conditions.get(i))))
//					{
//						remove = true;
//						break;
//					}
//				}
//				
//				if(!remove)
//					update_conditions.add(conditions.get(i));
//			}
//			
//			conditions = update_conditions;
//			
//			return conditions;
//			
//		}
//	}
	
	public static HashSet<Conditions> remove_conflict_duplicate_view_mapping(Query query)
	{
		if(valid_conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
		{
			return valid_conditions;
		}
		else
		{

			HashSet<Conditions> update_conditions = new HashSet<Conditions>();
			
			boolean remove = false;
			
			for(Iterator iter = valid_conditions.iterator(); iter.hasNext(); )
			{
				remove = false;
				
				Conditions v_condition = (Conditions) iter.next();
				
				for(int j = 0; j<query.conditions.size(); j++)
				{
					if(Conditions.compare(query.conditions.get(j), v_condition) || Conditions.compare(query.conditions.get(j), Conditions.negation(v_condition)))
					{
						remove = true;
												
						break;
					}
				}
				
				if(!remove)
					update_conditions.add(v_condition);
			}
			
			valid_conditions = update_conditions;
			
			int id = 0;
			
			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
			{
				Conditions condition = (Conditions) iter.next();
				
				condition_id_mapping.put(condition.toString(), id);
				
				id ++;
			}
			
			return valid_conditions;
			
		}
	}
	
	public static Vector<Vector<citation_view_vector>> query_execution(Query query, Connection c, PreparedStatement pst, HashMap<Head_strs, Vector<String> > citation_strs, Vector<Head_strs> head_vals, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
				
		Vector<Vector<citation_view_vector>> c_views = new Vector<Vector<citation_view_vector>>();
								
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
		remove_conflict_duplicate_view_mapping(query);
					
		String sql = Query_converter.datalog2sql_citation(query, valid_lambda_terms, valid_conditions);
		
		reasoning(c_views, query, c, pst, sql, head_vals, citation_view_map1, citation_strs);
		
		return c_views;
	}
	
//	public static void initial_conditions_all(Query query, Vector<HashMap<String,boolean[]>> conditions_all)
//	{
//		
//		
//		
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			HashMap<String, boolean[]> conditions = new HashMap<String, boolean[]>();
//			
//			Set set = citation_condition_id_map.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
//			{
//				String c_name = (String)iter.next();
//				
//				HashMap<String, Integer> citation_map = citation_condition_id_map.get(c_name);
//				
//				int size = citation_map.size();
//				
//				boolean[] b_values = new boolean[size];
//				
////				for(int k = 0; k<size; k++)
////				{
////					b_values.add(false);
////				}
//				
//				conditions.put(c_name, b_values);
//			}
//			
//			
//			conditions_all.add(conditions);
//		}
//	}
//	
	public static void reasoning(Vector<Vector<citation_view_vector>> c_views, Query query, Connection c, PreparedStatement pst, String sql, Vector<Head_strs> head_vals, HashMap<Head_strs, Vector<Vector<citation_view_vector> > > citation_view_map1, HashMap<Head_strs, Vector<String> > citation_strs) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		pst = c.prepareStatement(sql);
				
		ResultSet rs = pst.executeQuery();
					
		int lambda_term_num = valid_lambda_terms.size();

		String old_value = new String();
		
		Vector<Vector<String>> values = new Vector<Vector<String>>(); 
		
//		HashMap<Head_strs, Vector<String> > citation_strs = new HashMap<Head_strs, Vector<String> >();

		Vector<HashMap<String, Vector<String>>> citation_strings = new Vector<HashMap<String, Vector<String>>>();

		if(!valid_conditions.isEmpty())
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
				
				Head_strs h_vals = new Head_strs(vals);
				
//				System.out.println(h_vals);
								
				int pos1 = query.body.size() + query.head.args.size() + lambda_term_num;
				
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
				
				
				if(!curr_str.equals(old_value))
				{
					
					citation_strings = new Vector<HashMap<String, Vector<String>>>();
					
					Vector<String[]> c_units = new Vector<String[]>();
					for(int i = query.head.args.size(); i<query.body.size() + query.head.args.size();i++)
					{
						String[] c_unit= rs.getString(i+1).split("\\"+ populate_db.separator);
						c_units.add(c_unit);
					}
										
					Vector<Vector<citation_view>> c_unit_vec = get_citation_units_condition(c_units, rs, query.body.size() + query.head.args.size(), query);
					
//					output_vec(c_unit_vec);				
					
					Vector<citation_view_vector> c_unit_combinaton = get_valid_citation_combination(c_unit_vec,query);

					c_views.add(c_unit_combinaton);
					
//					Vector<String> citations = gen_citation(c_unit_combinaton, vals, c, pst, citation_strings);
					
					Vector<String> citations = new Vector<String>();
//					
//					for(int p =0; p<c_unit_combinaton.size(); p++)
//					{
//						
//						HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
//						
//						String str = gen_citation1.get_citations(c_unit_combinaton.get(p), vals, c, pst, query_str, max_author_num);
//
//						citations.add(str);
//						
//						citation_strings.add(query_str);
//						
//					}
					
					
						
//					first = false;
					old_value = curr_str;
					
					if(citation_view_map1.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(c_unit_combinaton);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map1.get(h_vals);
						
						Vector<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(c_unit_combinaton);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citation_strs.put(h_vals, curr_citations);
					}
					
					gen_citations(citation_strs, c_unit_combinaton, curr_str, query, c, pst);
					
//					output2excel.citation_output_row(rs, query, vals, c_unit_combinaton, file_name, tuple_num, citations);

									
				}
				
				else
				{
//					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
					
					Vector<citation_view_vector> curr_c_view = c_views.lastElement();
					
//					output_vec_com(curr_c_view);
					
					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
					
					for(int i = 0; i<curr_c_view.size(); i++)
					{
//						System.out.println("view::" + curr_c_view.get(i));
						
						insert_c_view.add(curr_c_view.get(i).clone());
					}
					
					Vector<citation_view_vector> update_c_view = update_valid_citation_combination(insert_c_view, rs, query.head.args.size() + query.body.size());
					
//					output_vec_com(update_c_view);
					
					c_views.add(update_c_view);
					
//					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
					
					Vector<String> citations = new Vector<String>();
//					
//					
//					for(int p =0; p<update_c_view.size(); p++)
//					{
//						
//						String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings.get(p), max_author_num);
//
//						citations.add(str);
//						
//					}
					
					
					
					if(citation_view_map1.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map1.get(h_vals);
						
						Vector<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(update_c_view);
						
						curr_citations.addAll(citations);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, curr_citations);
					}
					
//					output2excel.citation_output_row(rs, query, vals, update_c_view, file_name, tuple_num, citations);

				}
			}
		}
		else
		{
			
			boolean first = true;
			
			while(rs.next())
			{				
				Vector<String> vals = new Vector<String>();
				
				for(int i = 0; i<query.head.args.size(); i++)
				{
					vals.add(rs.getString(i+1));
				}
				
				values.add(vals);
				
				Head_strs h_vals = new Head_strs(vals);
				
				if(first)
				{
					citation_strings.clear();
					
					Vector<String[]> c_units = new Vector<String[]>();
					for(int i = query.head.args.size(); i<query.body.size() + query.head.args.size();i++)
					{
						String[] c_unit= rs.getString(i+1).split("\\"+ populate_db.separator);
						c_units.add(c_unit);
					}
					

					
					Vector<Vector<citation_view>> c_unit_vec = get_citation_units_condition(c_units, rs, query.body.size() + query.head.args.size(), query);
					
					Vector<citation_view_vector> c_unit_combinaton = get_valid_citation_combination(c_unit_vec,query);
					
//					Vector<String> citations = gen_citation(c_unit_combinaton, vals, c, pst, citation_strings);
					
					Vector<String> citations = new Vector<String>();
//					
//					for(int p =0; p<c_unit_combinaton.size(); p++)
//					{
//						
//						HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
//						
//						String str = gen_citation1.get_citations(c_unit_combinaton.get(p), vals, c, pst, query_str, max_author_num);
//
//						citations.add(str);
//						
//						citation_strings.add(query_str);
//						
//					}
					
					
								
					c_views.add(c_unit_combinaton);
											

					first = false;
					
					if(citation_view_map1.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(c_unit_combinaton);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map1.get(h_vals);
						
						Vector<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(c_unit_combinaton);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citation_strs.put(h_vals, curr_citations);
					}
					
					String curr_str = new String();
					
					gen_citations(citation_strs, c_unit_combinaton, curr_str, query, c, pst);
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
					
					Vector<citation_view_vector> update_c_view = update_valid_citation_combination(insert_c_view, rs, query.head.args.size() + query.body.size());
					
					c_views.add(update_c_view);
					
//					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
					
					Vector<String> citations = new Vector<String>();
//					
//					
//					for(int p =0; p<update_c_view.size(); p++)
//					{
//						
//						String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings.get(p), max_author_num);
//
//						citations.add(str);
//									
//					}
					
					
					
					if(citation_view_map1.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map1.get(h_vals);
						
						Vector<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map1.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citation_strs.put(h_vals, curr_citations);
					}
					
				}

			}
		}
		
//		output2excel.citation_output(rs, query, values, c_views, file_name, citation_strs);
//		return citation_strs;

	}
	
	static void gen_citations(HashMap<Head_strs, Vector<String>> citation_strs, Vector<citation_view_vector> c_views, String curr_str, Query q, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<c_views.size(); i++)
		{
			do_gen_citations(citation_strs, c_views.get(i), curr_str, q, c, pst);
		}
	}
	
	static void do_gen_citations(HashMap<Head_strs, Vector<String>> citation_strs, citation_view_vector c_views, String curr_str, Query q, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{		
		Vector<String> queries = Query_converter.group_sub_query_citations(c_views, valid_conditions, curr_str, q, authors);
		
		for(int i = 0; i<queries.size(); i++)
		{
			pst = c.prepareStatement(queries.get(i));
			
			ResultSet rs = pst.executeQuery();
			
			ResultSetMetaData rs_meta = rs.getMetaData();
			
			int col_num = rs_meta.getColumnCount();
			
			while(rs.next())
			{
				Vector<String> head_vals = new Vector<String> ();
				
				for(int k = 0; k<col_num - 1; k++)
				{
					head_vals.add(rs.getString(k + 1));
				}
				
				
				String author_str = rs.getString(col_num);
				
				String citation = gen_full_citaiton(head_vals, author_str);
				
				Head_strs h_str = new Head_strs(head_vals);
				
				if(citation_strs.get(h_str) == null)
				{
					Vector<String> citations = new Vector<String>();
					
					citations.add(citation);
					
					citation_strs.put(h_str, citations);
				}
				else
				{
					Vector<String> citations = citation_strs.get(h_str);
					
					citations.add(citation);
					
					citation_strs.put(h_str, citations);
				}
			}
			
//			pst = c.prepareStatement(citation_queries.get(i));
//			
//			rs = pst.executeQuery();
//			
//			rs_meta = rs.getMetaData();
//			
//			col_num = rs_meta.getColumnCount();
//			
//			while(rs.next())
//			{
//				Vector<String> head_vals = new Vector<String> ();
//				
//				for(int k = 0; k<col_num - 2; k++)
//				{
//					head_vals.add(rs.getString(k + 1));
//				}
//				
//				
//				String author_str = rs.getString(col_num - 1) + " " + rs.getString(col_num);
//				
//				String citation = gen_full_citaiton(head_vals, author_str);
//				
//				Head_strs h_str = new Head_strs(head_vals);
//				
//				if(authors.get(h_str) == null)
//				{
//					Vector<String> citations = new Vector<String>();
//					
//					citations.add(citation);
//					
//					authors.put(h_str, citations);
//				}
//				else
//				{
//					Vector<String> citations = authors.get(h_str);
//					
//					citations.add(citation);
//					
//					authors.put(h_str, citations);
//				}
//			}
			
			
		}
	}
	
	static String gen_full_citaiton(Vector<String> head_vals, String author_str)
	{
		String citation = author_str;
		
		String value_str = new String();
		
		for(int i = 0; i<head_vals.size(); i++)
		{
			if( i >= 1)
				value_str += ",";
			
			value_str += head_vals.get(i);
		}
		
		citation += value_str + "." + gen_citation1.db_name;
		
		return citation;
	}
	
	static Vector<String> gen_citation(Vector<citation_view_vector> c_views, Vector<String> vals, Connection c, PreparedStatement pst, Vector<HashMap<String, Vector<String>>> citation_strings) throws ClassNotFoundException, SQLException
	{
		Vector<String> citations = new Vector<String>();
		
		for(int p =0; p<c_views.size(); p++)
		{
			
			HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
			
			String str = gen_citation1.get_citations(c_views.get(p), vals, c, pst, query_str, max_author_num);

			citations.add(str);
			
			citation_strings.add(query_str);
			
		}
		
//		citation_strs.add(citations);
		
		return citations;
	}
	
	static Vector<String> populate_citation(Vector<citation_view_vector> update_c_view, Vector<String> vals, Connection c, PreparedStatement pst, Vector<HashMap<String, Vector<String>>> citation_strings) throws SQLException
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
		
//		citation_strs.add(citations);
		
		return citations;
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
			
			System.out.print("view_combination:::" + c_unit_combinaton.get(i));
		}
		System.out.println();
	}
	
	public static Vector<citation_view_vector> update_valid_citation_combination(Vector<citation_view_vector> insert_c_view, ResultSet rs, int start_pos) throws SQLException
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
						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
				}
				
			}
			
			c_vec = new citation_view_vector(c_vec.c_vec);
			
			insert_c_view.remove(i);
			
			insert_c_view.add(i, c_vec);
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
			String [] lambda_term_names = r.getString(1).split(populate_db.separator);
			
			String [] table_name = r.getString(2).split(populate_db.separator);
			
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
////					if(view_type_map.get(c_view_name))
//					{
//						c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//						
//						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
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
////					else
////					{
////						String []curr_c_unit = c_unit_str[j].split("\\(")[1].split("\\)");
////												
////						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
////						
////						if(curr_c_unit.length == 0)
////						{
////							int [] final_pos = new int[1];
////							
////							String t_name = curr_c_view.lambda_terms.get(0).table_name;
////							
////							Vector<Integer> positions = table_pos_map.get(t_name);
////							
////							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(0).name);
////							
////							Vector<Integer> ids = new Vector<Integer>();
////							
////							Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////							
////							for(int p = 0; p<vals.size(); p++)
////							{								
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = new Vector<String>();
////								
////								
////								val.add(vals.get(p));
////								
////								inserted_view.put_paramters(val);
////								
////								inserted_view.lambda_terms.get(p).id = ids.get(p);
////								
////								c_view.add(inserted_view);
////							}							
////						}
////						else
////						{
////							String [] c_v_unit = curr_c_unit[0].split(",");
////													
////							Vector<Vector<String>> values = new Vector<Vector<String>>();
////							
////							Vector<Vector<Integer>> id_arrays = new Vector<Vector<Integer>>();
////							
////							for(int p = 0; p<c_v_unit.length; p++)
////							{								
////								if(c_v_unit[p].isEmpty())
////								{
////									int [] final_pos = new int[1];
////									
////									String t_name = curr_c_view.lambda_terms.get(p).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									values = update_views(values, vals, id_arrays, ids);
////									
////									
////								}
////								else
////								{
////									
////									String t_name = curr_c_view.lambda_terms.get(0).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									int[] final_pos = new int[1];
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									id_arrays.add(ids);
////									
////									if(!values.isEmpty())
////									{
////										for(int t = 0; t<values.size(); t++)
////										{
////											Vector<String> v = values.get(t);
////											
////											v.add(c_v_unit[p]);
////										}
////									}
////									else
////									{
////										Vector<String> v= new Vector<String>();
////										
////										v.add(c_v_unit[p]);
////										
////										values.add(v);
////									}
////									
////									
////								}
////							}
////							
////							for(int p = 0; p<values.size(); p++)
////							{
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = values.get(p);
////								
////								Vector<Integer> ids = id_arrays.get(p);
////															
////								inserted_view.put_paramters(val);
////								
////								for(int w = 0; w < ids.size(); w++)
////								{
////									inserted_view.lambda_terms.get(w).id = ids.get(w);
////								}
////								
////								c_view.add(inserted_view);
////							}
////							
////						}
////						
////						
////						
//////						HashMap<String, Integer> lambda_term_map = view_lambda_term_map.get(c_view_name);
//////						
//////						Set name_set = lambda_term_map.entrySet();
//////						
//////						for(Iterator iter = name_set.iterator(); iter.hasNext(); )
//////						{
//////							
//////						}
//////						
//////						if(curr_c_unit.isEmpty())
//////						{
//////							
//////						}
//////						else
//////						{
//////							String [] curr_c_units = curr_c_unit.split(",");
//////							
//////							for(int t = 0; t<curr_c_units.length; t++)
//////							{
//////								if(curr_c_units)
//////							}
//////							
//////							if(curr_c_units)
//////							
//////						}
////						
////					}
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
	
	static boolean correct_tuple(Tuple tuple, Subgoal subgoal)
	{
		boolean correct = false;
		
//		System.out.println("query:::" + tuple.query);
				
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();  
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			
			Subgoal curr_subgoal = (Subgoal)iter.next();
			
//			System.out.println("1::::::" + subgoal.toString());
//			
//			System.out.println("2::::::" + curr_subgoal.toString());
			
			if(curr_subgoal.toString().equals(subgoal.toString()))
				return true;
		}
		
		return correct;
	}
	
	public static Vector<Vector<citation_view>> get_citation_units_condition(Vector<String[]> c_units, ResultSet rs, int start_pos, Query q) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view>> c_views = new Vector<Vector<citation_view>>();
		
//		boolean parameterized = true;
		
		for(int i = 0; i<c_units.size(); i++)
		{
			Vector<citation_view> c_view = new Vector<citation_view>();
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			String [] c_unit_str = c_units.get(i);
			for(int j = 0; j<c_unit_str.length; j++)
			{
				
//				String c_view_name = new String();
				
				String c_view_name = c_unit_str[j].split("\\(")[0];
				
				
//				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
//				{
//					parameterized = true;
//					c_view_name = c_unit_str[j].split("\\(")[0];
//				}
//				else
//				{
//					c_view_name = c_unit_str[j];
//					parameterized = false;
//					
//				}
					
					
					Vector<Tuple> available_tuples = tuple_mapping.get(c_view_name);
					
					if(available_tuples != null)
					{
						Vector<Tuple> valid_tuples = new Vector<Tuple> ();
						
						for(int k = 0; k<available_tuples.size(); k++)
						{
							Tuple curr_tuple = available_tuples.get(k);
							
							if(correct_tuple(curr_tuple, subgoal))
							{
								
								valid_tuples.add(curr_tuple);
							}
						}
						
						int k = 0;
						
						for(int p = 0; p < valid_tuples.size(); p++)
						{
							Tuple valid_tuple = valid_tuples.get(p);
							for(k = 0; k < valid_tuple.conditions.size(); k++)
							{
								int condition_id = condition_id_mapping.get(valid_tuple.conditions.get(k).toString());
								
								int pos = start_pos + valid_lambda_terms.size() + condition_id + 1;
								
								boolean b = rs.getBoolean(pos);
								
								if(b)
									continue;
								else
									break;
								
							}
							
							if(k < valid_tuple.conditions.size())
								continue;
						
							if(valid_tuple.lambda_terms.size() > 0)
							{
								Vector<String> lambda_term_values = new Vector<String>();
								
								for(k = 0; k<valid_tuple.lambda_terms.size(); k++)
								{
									int l_id = lambda_term_id_mapping.get(valid_tuple.lambda_terms.get(k).toString());
									
									int pos = start_pos + l_id + 1;
									
									lambda_term_values.add(rs.getString(pos));
									
								}
								
								citation_view_parametered c = new citation_view_parametered(c_view_name, view_mapping.get(c_view_name), valid_tuple, lambda_term_values);
			
								c_view.add(c);
							}	
							else
							{
								
								c_view.add(new citation_view_unparametered(c_unit_str[j], valid_tuple));
							}

							
						}
						

							
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
		
							
		//					curr_c_view.lambda_terms.
							
							
							
//						}	
//						else
//						{
//							
//							c_view.add(new citation_view_unparametered(c_unit_str[j], valid_tuple));
//						}
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
	
	
	public static Vector<citation_view_vector> get_valid_citation_combination(Vector<Vector<citation_view>>c_unit_vec, Query query)
	{
		Vector<citation_view_vector> c_combinations = new Vector<citation_view_vector>();
				
		Vector<String> subgoal_names = new Vector<String>();
		
//		int valid_subgoal_num = 0;
		
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
			
			Subgoal subgoal = (Subgoal) query.body.get(i); 
			
			if(curr_c_unit_vec.size() != 0)
			{
				subgoal_names.add(subgoal.name);
//				valid_subgoal_num ++;
			}
			
		}
		
		
//		boolean final_col = false;
		
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
			
			if(curr_c_unit_vec.size() == 0)
				continue;
//			else
//			{
//				valid_subgoal_num --;
//				
//				if(valid_subgoal_num == 0)
//					final_col = true;
//			}
			
			c_combinations = join_operation(c_combinations, curr_c_unit_vec, subgoal_names, i);
			
			c_combinations = remove_duplicate(c_combinations);
		}
		
		c_combinations = remove_duplicate_final(c_combinations);
		
		return (c_combinations);
	}
	
	public static Vector<citation_view_vector> remove_duplicate_final(Vector<citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(i);
			
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
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
	
	
	public static Vector<citation_view_vector> remove_duplicate(Vector<citation_view_vector> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
//		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(i);
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					citation_view_vector curr_combination = c_combinations.get(j);
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					{
						
						if(c_combination.table_names.equals(curr_combination.table_names))
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
	
	static boolean view_vector_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		
		Vector<String> c1 = new Vector<String>();
		
		c1.addAll(c_vec1.index_vec);
		
		
		Vector<String> c2 = new Vector<String>();
		
		c2.addAll(c_vec2.index_vec);
		
		for(int i = 0; i < c2.size(); i++)
		{
			int id = c1.indexOf(c2.get(i));
			
			if(id >= 0)
			{
				c1.remove(id);
				
				c2.remove(i);
				
				i --;
			}
			
		}
		
		if(c2.size() == 0)
			return true;
		else
			return false;
	}
//	
//	public static Vector<citation_view_vector> remove_duplicate_final(Vector<citation_view_vector> c_combinations)
//	{
////		Vector<Boolean> retains = new Vector<Boolean>();
//		
////		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
//		
//		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
//		
////		Set set = c_combinations.keySet();
//		
////		for(Iterator iter = set.iterator(); iter.hasNext();)
//		for(int i = 0; i<c_combinations.size(); i++)
//		{
////			String str = (String) iter.next();
//			
//			boolean contains = false;
//			
//			citation_view_vector c_combination = c_combinations.get(i);
//			
////			for(Iterator iterator = set.iterator(); iterator.hasNext();)
//			for(int j = 0; j<c_combinations.size(); j++)
//			{
////				String string = (String) iterator.next();
//				
//				if(i!=j)
//				{
//					citation_view_vector curr_combination = c_combinations.get(j);
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
//	
	public static Vector<citation_view_vector> join_operation(Vector<citation_view_vector> c_combinations, Vector<citation_view> insert_citations,  Vector<String> subgoal_names, int i)
	{
		if(i == 0)
		{
			for(int k = 0; k<insert_citations.size(); k++)
			{
				Vector<citation_view> c = new Vector<citation_view>();
				
				c.add(insert_citations.get(k));
				
				String str = String.valueOf(insert_citations.get(k).get_index());
				
				c_combinations.add(new citation_view_vector(c));
				
			}
			
			return c_combinations;
		}
		else
		{
//			HashMap<String, citation_view_vector> updated_c_combinations = new HashMap<String, citation_view_vector>();
			
			Vector<citation_view_vector> updated_c_combinations = new Vector<citation_view_vector>();
			
			
			
//			citation_view_vector update_vec = new citation_view_vector(insert_citations);
//			
//			
//			c_combinations.add(update_vec);
//			
//			return c_combinations;
//			Set set = c_combinations.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iter.next();
				
				citation_view_vector curr_combination = c_combinations.get(j);
								
				for(int k = 0; k<insert_citations.size(); k++)
				{
					
					citation_view_vector new_citation_vec = citation_view_vector.merge(curr_combination, insert_citations.get(k));
					
//					System.out.print(curr_combination.index_str);
//					
//					for(Iterator iter = curr_combination.table_names.iterator(); iter.hasNext();)
//					{
//						System.out.print("+" + iter.next());
//					}
//					
//					System.out.println();
					
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
					
						
					
					
					updated_c_combinations.add(new_citation_vec);
					
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
	
//	public static Vector<Vector<citation_view>> str2c_view(Vector<Vector<String>> citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<Vector<citation_view>> c_view = new Vector<Vector<citation_view>>();
//		
//		for(int i = 0; i<citation_str.size(); i++)
//		{
//			Vector<String> curr_c_view_str = citation_str.get(i);
//			
//			Vector<citation_view> curr_c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<curr_c_view_str.size();j++)
//			{
//				if(citation_str.get(i).contains("(") && citation_str.get(i).contains(")"))
//				{
//					String citation_view_name = curr_c_view_str.get(i).split("\\(")[0];
//					
//					String [] citation_parameters = curr_c_view_str.get(i).split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					curr_c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					curr_c_view.add(new citation_view_unparametered(curr_c_view_str.get(i)));
//				}
//			}
//			
//			c_view.add(curr_c_view);
//		}
//		
//		return c_view;
//	}
//	
//	
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
	
//	public static Vector<citation_view> str2c_unit(String [] citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<citation_view> c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<citation_str.length;j++)
//			{
//				if(citation_str[j].contains("(") && citation_str[j].contains(")"))
//				{
//					String citation_view_name = citation_str[j].split("\\(")[0];
//					
//					String [] citation_parameters = citation_str[j].split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					c_view.add(new citation_view_unparametered(citation_str[j]));
//				}
//			}
//		
//		return c_view;
//	}
	

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
