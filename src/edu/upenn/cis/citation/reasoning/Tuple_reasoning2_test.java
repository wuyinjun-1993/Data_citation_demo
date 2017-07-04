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
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.aggregation.Aggregation1;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import edu.upenn.cis.citation.output.output2excel;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class Tuple_reasoning2_test {
	
	
	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static HashSet<Conditions> valid_conditions = new HashSet<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, Integer> condition_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<String, Vector<Tuple>> tuple_mapping = new HashMap<String, Vector<Tuple>>();
	
	static HashMap<String, Vector<Tuple>> conditions_map = new HashMap<String, Vector<Tuple>>();
		
	static String file_name = "tuple_level1.xls";
		
	static int max_author_num = 10;
	
	static HashSet<String> str_set = new HashSet<String>();
	
	static HashMap<String, Vector<Integer> > view_query_mapping = new HashMap<String, Vector<Integer>>();
	
	
	
	static HashMap<Integer, Vector<Lambda_term>> query_lambda_str = new HashMap<Integer, Vector<Lambda_term>>();
	
	static HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping = new HashMap<Integer, HashMap<Head_strs, HashSet<String>>>();

	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException
	{
		
		Query q = gen_query();
		
		
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
//		String query = "q(family_c_family_id, family_c_name):family_c()";
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String> >();
		
		Vector<Vector<String>> head_vals = new Vector<Vector<String>>();
		
		Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning2_test.tuple_reasoning(q, citation_strs, citation_view_map2, c, pst);
		
		Vector<String> agg_citations = Tuple_reasoning2_test.tuple_gen_agg_citations(c_views);
		
		Vector<Integer> ids = new Vector<Integer>();
		
		Vector<String> subset_agg_citations = Tuple_reasoning2_test.tuple_gen_agg_citations(c_views, ids);

		c.close();
	}
	
	static Query gen_query() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family" + populate_db.separator + "family_id", "family"));
				
		
//		head_args.add(new Argument("family1" + populate_db.separator + "family_id", "family1"));
		
		head_args.add(new Argument("introduction" + populate_db.separator + "family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1" + populate_db.separator + "family_id", "introduction1"));
				
//		head_args.add(new Argument("introduction2" + populate_db.separator + "family_id", "introduction2"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
//		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
		subgoals.add(new Subgoal("family", args1));
		
//		subgoals.add(new Subgoal("family1", args2));
				
		subgoals.add(new Subgoal("introduction", args3));
		
//		subgoals.add(new Subgoal("introduction1", args4));
		
//		subgoals.add(new Subgoal("introduction2", args5));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_equal(), new Argument("family_id", "introduction"), "introduction"));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
		
//		subgoal_name_mapping.put("family1", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");

//		subgoal_name_mapping.put("introduction2", "introduction");

		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	
	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views, Vector<Integer> ids) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views, ids);
		
		Vector<String> citation_aggs = new Vector<String>();
		
		for(int i = 0; i<agg_res.size(); i++)
		{
			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
			
			citation_aggs.add(str);
//			
			System.out.print(agg_res.get(i).get(0).toString() + ":");
			
			System.out.println(str);

		}
		
		return citation_aggs;
	}
	
	public static Vector<Vector<citation_view_vector>> tuple_reasoning(Query query, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
//		query = get_full_query(query);
				
		Vector<Vector<citation_view_vector>> citation_views = gen_citation_main(query, citation_strs, citation_view_map2, c, pst);
		
		
		
		return citation_views;
	}
	
	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector>> c_views) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view_vector>> agg_res = Aggregation1.aggegate(c_views);
		
		Vector<String> citation_aggs = new Vector<String>();
		
		for(int i = 0; i<agg_res.size(); i++)
		{
			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
			
			citation_aggs.add(str);
//			
			System.out.print(agg_res.get(i).get(0).toString() + ":");
			
			System.out.println(str);

		}
		
		return citation_aggs;
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
	
	
	public static HashSet pre_processing(Query q, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
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
	    		
	    		Vector<Tuple> curr_tuples = conditions_map.get(condition_str);
	    		
	    		if(curr_tuples == null)
	    		{
	    			curr_tuples =  new Vector<Tuple>();
	    			
	    			curr_tuples.add(tuple);
	    			
	    			conditions_map.put(condition_str, curr_tuples);
	    		}
	    		else
	    		{	    			
	    			curr_tuples.add(tuple);
	    			
	    			conditions_map.put(condition_str, curr_tuples);
	    		}
	    			    		
	    		if(condition_id_mapping.get(condition_str) == null)
	    		{
	    			condition_id_mapping.put(condition_str, valid_conditions.size());
	    			
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
	
	public static void reset()
	{
		view_mapping.clear();
		
		valid_conditions.clear();
		
		valid_lambda_terms.clear();
		
		condition_id_mapping.clear();
		
		lambda_term_id_mapping.clear();
		
		conditions_map.clear();
				
		tuple_mapping.clear();
		
		view_query_mapping.clear();
		
		query_lambda_str.clear();
		
		author_mapping.clear();
												
	}
	
	public static Vector<Vector<citation_view_vector>> gen_citation_main(Query q, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
				
		reset();
	    
		HashSet views = pre_processing(q,c,pst);
		
		Vector<Vector<citation_view_vector>> citation_views = get_citation_views(views, q, c, pst, citation_strs, citation_view_map2);
				
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
	
	public static Vector<Vector<citation_view_vector>> get_citation_views(HashSet views, Query query,Connection c, PreparedStatement pst, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<citation_view_vector>> c_views = query_execution(views, query, c, pst, citation_strs, citation_view_map2);
				
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
	
	
	public static Vector<Vector<citation_view_vector>> query_execution(HashSet views, Query query, Connection c, PreparedStatement pst, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2) throws SQLException, ClassNotFoundException
	{
				
		Vector<Vector<citation_view_vector>> c_views = new Vector<Vector<citation_view_vector>>();
								
//		Query_converter.post_processing(query);
		
//		Query_converter.pre_processing(query);
		
		remove_conflict_duplicate_view_mapping(query);
			
		String sql = Query_converter.datalog2sql_citation2_test(query, valid_conditions, valid_lambda_terms);

		reasoning(views, c_views, query, c, pst, sql, citation_strs, citation_view_map2);
		
		return c_views;
	}
	
	
	static HashSet<citation_view_vector> reasoning_single_tuple(HashSet views, Query query, ResultSet rs, Vector<citation_view_vector> c_view_template) throws ClassNotFoundException, SQLException
	{
		HashSet<Rewriting> rewritings = CoreCover.coverQuerySubgoals(views, query);
		
		HashSet<citation_view_vector> c_view_vec = new HashSet<citation_view_vector>();
		
		int num = 0;
		
		for(Iterator iter = rewritings.iterator(); iter.hasNext();)
		{
			
//			num ++;
//			
//			System.out.println(num);
//			
//			if(num == 96)
//			{
//				int y = 0;
//				y++;
//			}
			
			Rewriting rw = (Rewriting) iter.next();
			
//			System.out.println(rw.toString());
			
			HashSet<Tuple> v_tuples = rw.viewTuples;
			
			Vector<citation_view> citation_views = new Vector<citation_view>();
			
			for(Iterator it = v_tuples.iterator();it.hasNext();)
			{
				Tuple tuple = (Tuple) it.next();
								
				Vector<Lambda_term> l_terms = tuple.lambda_terms;
				
				Vector<String> values = new Vector<String>();
				
				
				for(int p = 0; p<l_terms.size(); p++)
				{
					
					int offset = lambda_term_id_mapping.get(l_terms.get(p).toString());
				
					int position = query.head.args.size() + offset + 1;
					
					l_terms.get(p).id = position;
					
					values.add(rs.getString(position));
					
					
				}
				
				if(l_terms.size() == 0)
				{
					citation_view c_v = new citation_view_unparametered(tuple.name, tuple);
					
					citation_views.add(c_v);
				}
				else
				{
					citation_view_parametered c_v = new citation_view_parametered(tuple.name, view_mapping.get(tuple.name), tuple, values);
					
					c_v.lambda_terms = l_terms;
					
					c_v.put_paramters(values);
					
					citation_views.add(c_v);
				}
				
				
			}
			
			citation_view_vector c_vector = new citation_view_vector(citation_views);
						
//			c_view_vec.add(c_vector);
//			if(c_vector.toString().equals("v4(2,1,1)*v16(1,1,2)*v16(1,1,2)*v16(1,2,2)*v3(1,1,2,1)*v4(1,1,1)"))
//			{
//				System.out.println(c_vector.table_name_str);
//			}
			
			c_view_template = remove_duplicate(c_view_template, c_vector);
			
//			c_view_template.add(c_vector);
		}
		

//		System.out.println("::::::::::::::::");
//		
//		for(Iterator iter = str_set.iterator(); iter.hasNext();)
//		{
//			String string = (String) iter.next();
//			
//			System.out.println(string);
//		}
//		
//		str_set.clear();
		
//		Tuple_reasoning1.remove_duplicate_view_combinations(c_view_template);
		
		Tuple_reasoning1.remove_duplicate_view_combinations_final(c_view_template);
		
		c_view_vec.addAll(c_view_template);
				
//		c_view_vec = remove_duplicate_final(c_view_vec);
//		
//		c_view_vec = remove_duplicate(c_view_vec);
		
		return (c_view_vec);
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
					if((Tuple_reasoning1.view_vector_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
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
	
	public static Vector<citation_view_vector> remove_duplicate(Vector<citation_view_vector> c_combinations, citation_view_vector c_view)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		Vector<citation_view_vector> update_combinations = new Vector<citation_view_vector>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		int i = 0;
		
		for(i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
			boolean contains = false;
			
			citation_view_vector c_combination = c_combinations.get(i);
			
//			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
//				if(i != j)
				{
					citation_view_vector curr_combination = c_view;
					
					if(c_combination.toString().equals("v4(2,1,1)*v16(1,1,2)*v16(1,1,2)*v16(1,2,2)*v3(1,1,2,1)*v4(1,1,1)") && c_combination.table_name_str.equals("[family, gpcr, object][family, ligand3, introduction][family, ligand, introduction][family, ligand9, introduction][family, object5, gpcr, introduction2][family, object5, gpcr]"))
					{
						
//						str_set.add(c_combination.table_name_str);
//						
//						System.out.println(c_combination.index_str);
//						
//						System.out.println(curr_combination.index_str);
//						
//						System.out.println(c_combination.table_name_str);
//						
//						System.out.println(curr_combination.table_name_str);
//						
						int y = 0;
						
						y++;
					}
					
					if(curr_combination.toString().equals("v4(2,1,1)*v16(1,1,2)*v16(1,1,2)*v16(1,2,2)*v3(1,1,2,1)*v4(1,1,1)") && c_combination.table_name_str.equals("[family, gpcr, object][family, ligand3, introduction][family, ligand, introduction][family, ligand9, introduction][family, object5, gpcr, introduction2][family, object5, gpcr]"))
					{
						
//						str_set.add(curr_combination.table_name_str);
//						
//						System.out.println(c_combination.index_str);
//						
//						System.out.println(curr_combination.index_str);
//						
//						System.out.println(c_combination.table_name_str);
//						
//						System.out.println(curr_combination.table_name_str);
						
						int y = 0;
						
						y++;
					}
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					if(Tuple_reasoning1.view_vector_contains(c_combination, curr_combination)&& Tuple_reasoning1.table_names_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size())
					{
						
//						if(c_combination.toString().equals("v4(2,1,1)*v16(1,1,2)*v16(1,1,2)*v16(1,2,2)*v3(1,1,2,1)*v4(1,1,1)"))
//						{
//							System.out.println(str_set.remove(c_combination.table_name_str));
//						}
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
//							break;
						}
					}
					
					if(Tuple_reasoning1.view_vector_contains(curr_combination, c_combination) && Tuple_reasoning1.table_names_contains(curr_combination, c_combination) && curr_combination.index_vec.size() > c_combination.index_vec.size())
					{
						
						
//						if(curr_combination.toString().equals("v4(2,1,1)*v16(1,1,2)*v16(1,1,2)*v16(1,2,2)*v3(1,1,2,1)*v4(1,1,1)"))
//						{
//							System.out.println(str_set.remove(curr_combination.table_name_str));
//						}
						
						break;
					}
					
					if(curr_combination.index_str.equals(c_combination.index_str) && curr_combination.table_name_str.equals(c_combination.table_name_str))
						break;
				}
				
			}
		}
		
		
		if(i >= c_combinations.size())
			c_combinations.add(c_view);
		
				
		return c_combinations;
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
	
	
	public static void reasoning(HashSet views, Vector<Vector<citation_view_vector>> c_views, Query query, Connection c, PreparedStatement pst, String sql, HashMap<Head_strs, HashSet<String> > citation_strs, HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2) throws SQLException, ClassNotFoundException
	{
		pst = c.prepareStatement(sql);
				
		ResultSet rs = pst.executeQuery();
		
		ResultSetMetaData r = rs.getMetaData();
		
		int col_num = r.getColumnCount();
						
		String old_value = new String();
		
				
		Vector<Vector<String>> values = new Vector<Vector<String>>();
		
//		Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
		
		Vector<HashMap<String, Vector<String>>> citation_strings = new Vector<HashMap<String, Vector<String>>>();
		
		int group_num = 0;
		
		int tuple_num = 0;
		
		Vector<citation_view_vector> c_view_template = null;
		
		if(!valid_conditions.isEmpty())
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
				
//				if(h_vals.toString().equals("2 1 1 2 1 1 2 1 1"))
//				{
//					int y= 0;
//					
//					y++;
//				}
//				
//				if(h_vals.toString().equals("1 1 1 2 2 1 1 1 1"))
//				{
//					int y= 0;
//					
//					y++;
//				}
								
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
				
				if(!curr_str.equals(old_value))
				{
										
//					System.out.println(h_vals);
					
					group_num++;
					
					c_view_template = new Vector<citation_view_vector>();
					
					citation_strings = new Vector<HashMap<String, Vector<String>>>();
					
					HashSet curr_views = (HashSet) views.clone();
										
					int i = pos1;
					
					for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
					{
						boolean condition = rs.getBoolean(i + 1);
						
						Conditions curr_condition = (Conditions) iter.next();
						
						if(!condition)
						{							
							String condition_str = curr_condition.toString();
							
							Vector<Tuple> invalid_views = conditions_map.get(condition_str);
							
							curr_views.removeAll(invalid_views);
						}
						i++;
					}	
					
					
//					remove_invalid_web_view(curr_views, pos1, col_num,  rs);
					
//					System.out.println(curr_str);

					
					HashSet<citation_view_vector> c_v = reasoning_single_tuple(curr_views, query, rs, c_view_template);
					
					Vector<citation_view_vector> c_vec = new Vector<citation_view_vector>();
					
					c_vec.addAll(c_v);
					
					c_v.clear();

					old_value = curr_str;
					
//					output_vec_com(c_vec);
					
					c_views.add(c_vec);
					
//					Vector<String> citations = new Vector<String>();
					
					HashSet<String> citations = gen_citation(c_vec, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
					
//					HashSet<String> citations = new HashSet<String>();
					
					if(citation_view_map2.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(c_vec);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
						
						HashSet<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(c_vec);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citations.clear();
						
						citation_strs.put(h_vals, curr_citations);
					}
					
				}
				
				else
				{
					
//					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
					
					Vector<citation_view_vector> curr_c_view = c_view_template;
					
					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
					
					for(int i = 0; i<curr_c_view.size(); i++)
					{
						insert_c_view.add(curr_c_view.get(i).clone());
					}
					
					HashSet<citation_view_vector> update_c = update_valid_citation_combination(insert_c_view, rs);
					
					Vector<citation_view_vector> update_c_view = new Vector<citation_view_vector>();
					
					update_c_view.addAll(update_c);
					
					update_c.clear();
					
//					output_vec_com(update_c_view);
					
					c_views.add(update_c_view);
					
//					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
					
					HashSet<String> citations = gen_citation(update_c_view, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
					
//					HashSet<String> citations = new HashSet<String>();
					
					if(citation_view_map2.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
						
						HashSet<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citations.clear();
						
						citation_strs.put(h_vals, curr_citations);
					}
				}
			}
		}
		else
		{
			
			boolean first = true;

			while(rs.next())
			{
				
//				System.out.println(rid);
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

				int pos2 = pos1 + valid_conditions.size();

				
				if(first)
				{
					
//					System.out.println(h_vals);
					
					HashSet curr_views = (HashSet) views.clone();
					
					c_view_template = new Vector<citation_view_vector>();
					
					group_num ++;
//					remove_invalid_web_view(curr_views, pos2, col_num,  rs);
					
					HashSet<citation_view_vector> c_v = reasoning_single_tuple(curr_views, query, rs, c_view_template);
					
					Vector<citation_view_vector> curr_c_views = new Vector<citation_view_vector>();
					
					curr_c_views.addAll(c_v);
					
					c_v.clear();
											
//					output_vec_com(curr_c_views);
					
					c_views.add(curr_c_views);
					
					old_value = curr_str;
				
//					Vector<String> citations = gen_citation(curr_c_views, vals, c, pst, citation_strings);
					HashSet<String> citations = gen_citation(curr_c_views, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
					
//					HashSet<String> citations = new HashSet<String>();
					
					first = false;
					
					if(citation_view_map2.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(curr_c_views);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
						
						HashSet<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(curr_c_views);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						curr_citations.addAll(citations);
						
						citations.clear();
						
						citation_strs.put(h_vals, curr_citations);
					}
					
					
				}
				
				else
				{
					
//					HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
					
					Vector<citation_view_vector> curr_c_view = c_view_template;
					
					Vector<citation_view_vector> insert_c_view = new Vector<citation_view_vector>();
					
					for(int i = 0; i<curr_c_view.size(); i++)
					{
						insert_c_view.add(curr_c_view.get(i).clone());
					}
					
					HashSet<citation_view_vector> update_c = update_valid_citation_combination(insert_c_view, rs);
					
					Vector<citation_view_vector> update_c_view = new Vector<citation_view_vector>();
					
					update_c_view.addAll(update_c);
					
					update_c.clear();
					
					c_views.add(update_c_view);
					
					HashSet<String> citations = gen_citation(update_c_view, vals, c, pst, h_vals, view_query_mapping, query_lambda_str, author_mapping);
					
//					HashSet<String> citations = new HashSet<String>();
					
//					Vector<String> citations = populate_citation(update_c_view, vals, c, pst, citation_strings);
					
					
					if(citation_view_map2.get(h_vals) == null)
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = new Vector<Vector<citation_view_vector>>();
						
						curr_c_view_vector.add(update_c_view);
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, citations);
					}
					else
					{
						Vector<Vector<citation_view_vector>> curr_c_view_vector = citation_view_map2.get(h_vals);
						
						HashSet<String> curr_citations = citation_strs.get(h_vals);
						
						curr_c_view_vector.add(update_c_view);
						
						curr_citations.addAll(citations);
						
						citations.clear();
						
						citation_view_map2.put(h_vals, curr_c_view_vector);
						
						citation_strs.put(h_vals, curr_citations);
					}
				}
			}
		}
		
		if(c_view_template != null)
			c_view_template.clear();
			
//		output2excel.citation_output(rs, query, values, c_views, file_name, values);
		System.out.print(group_num + "	");
		
		System.out.print(tuple_num + "	");
		

	}
	
	static HashSet<String> gen_citation(Vector<citation_view_vector> c_views, Vector<String> vals, Connection c, PreparedStatement pst, Head_strs h_vals, HashMap<String, Vector<Integer> > view_query_mapping, HashMap<Integer, Vector<Lambda_term>> query_lambda_str, HashMap<Integer, HashMap<Head_strs, HashSet<String>>> author_mapping) throws ClassNotFoundException, SQLException
	{
		HashSet<String> citations = new HashSet<String>();
		
		HashSet<String> author_list = new HashSet<String>();
		
		
		for(int p =0; p<c_views.size(); p++)
		{
			
			author_list = new HashSet<String>();
						
			String str = gen_citation1.get_citations2(c_views.get(p), vals, c, pst, view_query_mapping, query_lambda_str, author_mapping, max_author_num, author_list);
			
//			System.out.println(c_views.get(p));
//			
//			System.out.println(str);
			
			citations.add(str);
			
//			Vector<citation_view> c_vec = c_views.get(p).c_vec;
//			
//			for(int k = 0; k<c_vec.size(); k++)
//			{
//				HashSet<String> query_strs = citation_strings.get(c_vec.get(k).get_name());
//								
//				if(query_strs == null)
//				{
//					
//					
//					citation_strings.put(c_vec.get(k).get_name(), curr_query_str);
//				}
//				else
//				{
//					query_strs.addAll(curr_query_str);
//					
//					citation_strings.put(c_vec.get(k).get_name(), query_strs);
//				}
//				
//				
//				
//			}
						
//			if(authors.get(h_vals) == null)
//			{
//				Vector<HashSet<String>> author_l = new Vector<HashSet<String>>();
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//				
//			}
//			else
//			{
//				Vector<HashSet<String>> author_l = authors.get(h_vals);
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//			}
			
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
	
	public static HashSet<citation_view_vector> update_valid_citation_combination(Vector<citation_view_vector> insert_c_view, ResultSet rs) throws SQLException
	{
		
		HashSet<citation_view_vector> update_c_view = new HashSet<citation_view_vector>();
		
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
			
			c_vec = new citation_view_vector(c_vec.c_vec);
			
//			insert_c_view.remove(i);
			
			update_c_view.add(c_vec);
		}
//		insert_c_view = remove_duplicate_final(insert_c_view);
//
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
		
		return update_c_view;
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
