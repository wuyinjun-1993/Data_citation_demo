package edu.upenn.cis.citation.stress_test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import java.util.Random;
import java.util.Set;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.UserLib;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.citation_view_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.datalog.Query_converter;

public class view_generator {
	
	
	
	public static String [] citatable_tables = {"gpcr","object", "ligand", "family", "introduction"};

	static HashMap<string_array, Vector<string_array> > joinable_attribute_lists = new HashMap<string_array, Vector<string_array>>();
	
	static HashMap<String, Vector<String> > foreign_key_relations = new HashMap<String, Vector<String> >();
	
	static String [] available_data_type = {"integer", "text", "boolean", "character varying", "double precision", "real", "character varying", "bigint"};
	
	static Vector<String> available_data_type_vec = new Vector<String>();
	
	static Vector<String> comparable_data_type_vec = new Vector<String>();
	
	static String []comparable_data_type = {"integer", "double precision", "real", "bigint"};
	
	static double citatble_rate = 0.3;
	
	static double local_predicates_rate = 0.7;
	
	static double lambda2head_rate = 0.3;
	
	static double head_var_rate = 0.8;
	
	static double global_predicate_rate = 1;
	
	static int view_nums = 100;
	
	static HashMap<String, Vector<String>> query_table_names = new HashMap<String, Vector<String>>();
	
	static HashMap<String, Vector<Argument>> query_head_names = new HashMap<String, Vector<Argument>>();
	
	static HashMap<String, Vector<Conditions>> query_conditions = new HashMap<String, Vector<Conditions>>();
	
	static int const_range = 20;
	
	static double with_predicate = 0.5;
	
	public static void initial()
	{
		Vector<String> table_names = new Vector<String>();
		
		table_names.add("contributor");
		
		table_names.add("contributor2object");
		
		table_names.add("object");
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("contributor_id", "contributor2object"), "contributor2object", new op_equal(), new Argument("contributor_id", "contributor"), "contributor"));
		
		conditions.add(new Conditions(new Argument("object_id", "contributor2object"), "contributor2object", new op_equal(), new Argument("object_id", "object"), "object"));
		
//		conditions.add("object_object_id = contributor2object_object_id");
		
		Vector<Argument> head_variables = new Vector<Argument>();
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "first_names", "contributor"));
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "surname", "contributor"));
		
		query_table_names.put("object", table_names);
		
		query_head_names.put("object", head_variables);
		
		query_conditions.put("object", conditions);
		
		table_names = new Vector<String>();
		
		table_names.add("contributor");
		
		table_names.add("contributor2object");
		
		table_names.add("gpcr");
		
		conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("contributor_id", "contributor2object"), "contributor2object", new op_equal(), new Argument("contributor_id", "contributor"), "contributor"));
		
		conditions.add(new Conditions(new Argument("object_id", "contributor2object"), "contributor2object", new op_equal(), new Argument("object_id", "gpcr"), "gpcr"));
		
//		conditions.add("object_object_id = contributor2object_object_id");
		
		head_variables = new Vector<Argument>();
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "first_names", "contributor"));
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "surname", "contributor"));
		
		query_table_names.put("gpcr", table_names);
		
		query_head_names.put("gpcr", head_variables);
		
		query_conditions.put("gpcr", conditions);
		
		table_names = new Vector<String>();
		
		table_names.add("contributor");
		
		table_names.add("contributor2family");
		
		table_names.add("family");
		
		conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("contributor_id", "contributor2family"), "contributor2family", new op_equal(), new Argument("contributor_id", "contributor"), "contributor"));
		
		conditions.add(new Conditions(new Argument("family_id", "contributor2family"), "contributor2family", new op_equal(), new Argument("family_id", "family"), "family"));
		
//		conditions.add("object_object_id = contributor2object_object_id");
		
		head_variables = new Vector<Argument>();
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "first_names", "contributor"));
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "surname", "contributor"));
		
		query_table_names.put("family", table_names);
		
		query_head_names.put("family", head_variables);
		
		query_conditions.put("family", conditions);
		
		table_names = new Vector<String>();
		
		table_names.add("contributor");
		
		table_names.add("contributor2intro");
		
		table_names.add("introduction");
		
		conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("contributor_id", "contributor2intro"), "contributor2intro", new op_equal(), new Argument("contributor_id", "contributor"), "contributor"));
		
		conditions.add(new Conditions(new Argument("family_id", "contributor2intro"), "contributor2intro", new op_equal(), new Argument("family_id", "introduction"), "introduction"));
		
//		conditions.add("object_object_id = contributor2object_object_id");
		
		head_variables = new Vector<Argument>();
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "first_names", "contributor"));
		
		head_variables.add(new Argument("contributor" + populate_db.separator + "surname", "contributor"));
		
		query_table_names.put("introduction", table_names);
		
		query_head_names.put("introduction", head_variables);
		
		query_conditions.put("introduction", conditions);
		
		table_names = new Vector<String>();
				
		table_names.add("contributor2ligand");
		
		table_names.add("ligand");
		
		conditions = new Vector<Conditions>();
				
		conditions.add(new Conditions(new Argument("ligand_id", "contributor2ligand"), "contributor2ligand", new op_equal(), new Argument("ligand_id", "ligand"), "ligand"));
		
//		conditions.add("object_object_id = contributor2object_object_id");
		
		head_variables = new Vector<Argument>();
		
		head_variables.add(new Argument("contributor2ligand" + populate_db.separator + "first_names", "contributor2ligand"));
		
		head_variables.add(new Argument("contributor2ligand" + populate_db.separator + "surname", "contributor2ligand"));
		
		query_table_names.put("ligand", table_names);
		
		query_head_names.put("ligand", head_variables);
		
		query_conditions.put("ligand", conditions);
		
//		table_names = new Vector<String>();
//		
//		table_names.add("contributor");
//		
//		table_names.add("contributor2ligand");
//		
//		table_names.add("ligand");
//		
//		conditions = new Vector<String>();
//			
//		conditions.add("ligand_ligand_id = contributor2ligand_ligand_id");
//		
//		head_variables = new Vector<String>();
//		
//		head_variables.add("contributor2ligand_first_names");
//		
//		head_variables.add("contributor2ligand_surname");
//		
//		query_table_names.put("ligand", table_names);
//		
//		query_head_names.put("ligand", head_variables);
//		
//		query_conditions.put("ligand", conditions);
		
		
		
	}
	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		
		initial();
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
//	    clear_views(c, pst);
//	    
//	    clear_other_tables(c, pst);
//	    
//	    get_joinable_relations(c, pst);
//	    
//	    HashSet<Query> views = gen_views(c, pst);
//	    
//	    store_views(views, c, pst);
	    
	    
	}
	
	
	public static Vector<Query> generate_store_views(Vector<String> subgoal_names, int num_views, int sizeofquery, Query q, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();
			    
	    clear_views(c, pst, true);
	    
	    clear_other_tables(c, pst);
	    
	    populate_db.renew_table(c, pst);
	    
	    get_joinable_relations(c, pst);
	    
	    Vector<Query> views = gen_views(subgoal_names, num_views, sizeofquery,  c, pst, q);
	    
	    store_views(views, c, pst);
	    
	    return views;
	}
	
	public static Vector<Query> generate_store_views_without_predicates(Vector<String> subgoal_names, int num_views, int sizeofquery, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();
		    
	    clear_views(c1, pst, true);
	    
	    clear_other_tables(c1, pst);
	    
	    clear_views(c2, pst, false);
	    
	    clear_other_tables(c2, pst);
	    
	    populate_db.renew_table(c1, pst);
	    
	    get_joinable_relations(c2, pst);
	    
	    Vector<Query> views = gen_views_without_predicates(subgoal_names, num_views, sizeofquery,  c2, pst);
	    
	    store_views(views, c1, c2, pst);
	    	    
	    return views;
	}
	
	public static Vector<Query> gen_random_views(Vector<String> subgoal_names, int num_views, Query query, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();
		    
	    clear_views(c1, pst, true);
	    
	    clear_other_tables(c1, pst);
	    
	    clear_views(c2, pst, false);
	    
	    clear_other_tables(c2, pst);
	    
	    populate_db.renew_table(c1, pst);
	    
	    get_joinable_relations(c2, pst);
	    
	    Vector<Query> views = gen_random_views(subgoal_names, num_views, query, c2, pst);
	    
	    store_views(views, c1, c2, pst);
	    	    
	    return views;
	}
	
	public static Vector<Query> generate_store_views_without_predicates(Vector<String> subgoal_names, int num_views, int sizeofquery, Connection c1, Connection c2, Connection c3, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();
		    
	    clear_views(c1, pst, true);
	    
	    clear_other_tables(c1, pst);
	    
	    clear_views(c2, pst, false);
	    
	    clear_other_tables(c2, pst);
	    
	    clear_views(c3, pst, false);
	    
	    clear_other_tables(c3, pst);
	    
	    populate_db.renew_table(c1, pst);
	    
	    get_joinable_relations(c2, pst);
	    
	    Vector<Query> views = gen_views_without_predicates(subgoal_names, num_views, sizeofquery,  c2, pst);
	    
	    store_views(views, c1, c2, c3, pst);
	    	    
	    return views;
	}
	
	public static Vector<Query> generate_store_views_without_lambda_terms(Vector<String> subgoal_names, int num_views, int sizeofquery, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();
	    
	    clear_views(c1, pst, true);
	    
	    clear_other_tables(c1, pst);
	    
	    clear_views(c2, pst, false);
	    
	    clear_other_tables(c2, pst);
	    
	    populate_db.renew_table(c1, pst);
	    
	    populate_db.drop_citation_view_cols(c2, pst);
	    
	    get_joinable_relations(c2, pst);
	    
	    Vector<Query> views = gen_views_without_lambda_terms(subgoal_names, num_views, sizeofquery,  c2, pst);
	    
	    store_views_lambda(views, c1, c2, pst);
	    	    
	    return views;
	}
	
	
	public static void clear_views(Connection c, PreparedStatement pst, boolean tuple_level) throws SQLException, ClassNotFoundException
	{
		Vector<Integer> ids = get_all_view_ids(c, pst);
		
		for(int i = 0; i<ids.size(); i++)
		{
			view_operation.delete_view_by_id(ids.get(i), c, pst, tuple_level);
		}
		
		
	}
	
	public static Vector<Query> gen_default_views(Vector<String> relations, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();	    
	    
	    get_joinable_relations(c, pst);
	    
	    clear_views(c, pst, true);
	    
	    clear_other_tables(c, pst);
	    
	    populate_db.renew_table(c, pst);
	    
	    Vector<Query> views = new Vector<Query>();
		
		for(int i = 0; i<relations.size(); i++)
		{
			Query view = generate_default_view(relations.get(i), i + 1, c, c, pst);
			
			views.add(view);
		}
				
		return views;
	}
	
	public static Vector<Query> gen_default_views(Vector<String> relations, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		initial();	    
	    
//	    get_joinable_relations(c1, pst);
	    
	    clear_views(c1, pst, true);
	    
	    clear_other_tables(c1, pst);
	    
	    populate_db.renew_table(c1, pst);
	    
	    get_joinable_relations(c2, pst);
	    
	    clear_views(c2, pst, false);
	    
	    clear_other_tables(c2, pst);
	    
//	    populate_db.renew_table(c2, pst);
	    
	    Vector<Query> views = new Vector<Query>();
		
		for(int i = 0; i<relations.size(); i++)
		{
			Query view = generate_default_view(relations.get(i), i + 1, c1, c2, pst);
			
			views.add(view);
		}
				
		return views;
	}
	
	public static Vector<Query> gen_views_with_n_subgoals(Vector<String> unique_relation_names, int n, int id, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
				
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Query> queries = new Vector<Query>();
		
		relation_names.addAll(unique_relation_names);
		
		HashSet relation_sets = UserLib.genSubsets(relation_names, n);
		
		for(Iterator iter = relation_sets.iterator(); iter.hasNext();)
		{
			HashSet<String> relation_subset = (HashSet<String>) iter.next();
			
			Vector<String> relation_seq = new Vector<String>();
			
			relation_seq.addAll(relation_subset);
			
			Query view = generate_views_with_n_subgoals_without_predicates(relation_seq, id, c, pst);
			
			queries.add(view);
			
			id++;
			
		}
						
		return queries;
		
	}
	
	public static void store_single_view(Query view, int id, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		view_operation.add(view, view.name, c, pst);
		
		citation_view_operation.add_citation_view("c" + id,c ,pst);
		
		citation_view_operation.add_connection_view_with_citations("c" + id, view.name, c, pst);
		
		store_citation_queries(view, id, c, pst);
		
		Query_operation.add_connection_citation_with_query("c" + id, "q" + id , "author", c, pst);
		
	}
	
	public static void store_single_citation_view(Query view, int id, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
//		view_operation.add(view, view.name);
		
		citation_view_operation.add_citation_view("c" + id, c, pst);
		
		citation_view_operation.add_connection_view_with_citations("c" + id, view.name, c, pst);
		
		store_citation_queries(view, id, c, pst);
		
		Query_operation.add_connection_citation_with_query("c" + id, "q" + id , "author", c, pst);
		
	}
	
	public static void store_single_citation_view(Query view, int id, String citation_view_name, String query_name, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
//		view_operation.add(view, view.name);
		
		citation_view_operation.add_citation_view(citation_view_name, c, pst);
		
		citation_view_operation.add_connection_view_with_citations(citation_view_name, view.name, c, pst);
		
		store_citation_queries(view, id, query_name, c, pst);
		
		Query_operation.add_connection_citation_with_query(citation_view_name, query_name , "author", c, pst);
		
	}
	
	public static void store_single_citation_view_lambda(Query view, int id, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
//		view_operation.add(view, view.name);
		
		citation_view_operation.add_citation_view("c" + id, c, pst);
		
		citation_view_operation.add_connection_view_with_citations("c" + id, view.name, c, pst);
		
		store_citation_queries_lambda(view, id, c, pst);
		
		Query_operation.add_connection_citation_with_query("c" + id, "q" + id , "author", c, pst);
		
	}
	
	public static void clear_other_tables(Connection c, PreparedStatement pst) throws SQLException
	{
		String [] tables = {"citation2query", "citation2view", "citation_table", "query2subgoal", "query2lambda_term", "query2conditions", "query2head_variables"};
		
		for(int i = 0; i<tables.length; i++)
		{
			String query = "delete from " + tables[i];
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	
	static Vector<Integer> get_all_view_ids(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Integer> ids = new Vector<Integer>();
		
		
		while(rs.next())
		{
			ids.add(rs.getInt(1));
		}
		
		return ids;
	}
	
	public static void store_views(Vector<Query> views, Connection c1, Connection c2, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		int num = 1;
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			
			Query view = (Query) iter.next();
			
			String name = view.name;
			
			view_operation.add(view, view.name, c1, pst);
			
			view_operation.add(view, view.name, c2, pst, false);
			
			citation_view_operation.add_citation_view("c" + num, c1, pst);
			
			citation_view_operation.add_citation_view("c" + num, c2, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c1, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c2, pst);
			
			store_citation_queries(view, num, "q" + num ,c1, pst);
			
			store_citation_queries(view, num, "q" + num ,c2, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c1, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c2, pst);
			
			num ++;

		}
		
	}
	
	public static void store_views(Vector<Query> views, Connection c1, Connection c2, Connection c3, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		int num = 1;
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			
			Query view = (Query) iter.next();
			
			String name = view.name;
			
			view_operation.add(view, view.name, c1, pst);
			
			view_operation.add(view, view.name, c2, pst, false);
			
			view_operation.add(view, view.name, c3, pst, false);
			
			citation_view_operation.add_citation_view("c" + num, c1, pst);
			
			citation_view_operation.add_citation_view("c" + num, c2, pst);
			
			citation_view_operation.add_citation_view("c" + num, c3, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c1, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c2, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c3, pst);
			
			store_citation_queries(view, num, "q" + num ,c1, pst);
			
			store_citation_queries(view, num, "q" + num ,c2, pst);
			
			store_citation_queries(view, num, "q" + num ,c3, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c1, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c2, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c3, pst);
			
			num ++;

		}
		
	}
	
	public static void store_views(Vector<Query> views, Connection c1, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		int num = 1;
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			
			Query view = (Query) iter.next();
			
			String name = view.name;
			
			view_operation.add(view, view.name, c1, pst);
			
//			view_operation.add(view, view.name, c2, pst);
			
			citation_view_operation.add_citation_view("c" + num, c1, pst);
			
//			citation_view_operation.add_citation_view("c" + num, c2, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c1, pst);
			
//			citation_view_operation.add_connection_view_with_citations("c" + num, name, c2, pst);
			
			store_citation_queries(view, num, c1, pst);
			
//			store_citation_queries(view, num, c2, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c1, pst);
			
//			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c2, pst);
			
			num ++;

		}
		
	}
	
	public static void store_views_lambda(Vector<Query> views, Connection c1, Connection c2, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		int num = 1;
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			
			Query view = (Query) iter.next();
			
			String name = view.name;
			
			view_operation.add(view, view.name, c1, pst);
			
			view.name = name;
			
			view_operation.add(view, view.name, c2, pst, false);
			
			citation_view_operation.add_citation_view("c" + num, c1, pst);
			
			citation_view_operation.add_citation_view("c" + num, c2, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c1, pst);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name, c2, pst);
			
			store_citation_queries_lambda(view, num, c1, pst);
			
			store_citation_queries_lambda(view, num, c2, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c1, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author", c2, pst);
			
			num ++;

		}
		
	}
	
	static void store_citation_queries(Query view, int num, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Vector<String> citable_table_names = new Vector<String>();
		
		citable_table_names.addAll(Arrays.asList(citatable_tables));
		
		Subgoal subgoal = null;
		
		while(true)
		{
			Random r = new Random();
			
			int index = r.nextInt(view.body.size());
			
			
			
			subgoal = (Subgoal)view.body.get(index);
			
			if(citable_table_names.contains(subgoal.name))
				break;
		}
		
		String [] primary_key_type = get_primary_key(subgoal.name, c, pst);
		
		
		Vector<Conditions> conditions = query_conditions.get(subgoal.name);
		
		Vector<Argument> head_vars = query_head_names.get(subgoal.name);
		
		Vector<String> subgoals = query_table_names.get(subgoal.name);
		
		Subgoal head = new Subgoal("q" + num, head_vars);
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		HashMap<String, String> subgoal_mapping = new HashMap<String, String>();
		
		for(int i = 0; i < subgoals.size(); i++)
		{
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(subgoals.get(i), args));
			
			subgoal_mapping.put(subgoals.get(i), subgoals.get(i));
		}
		
		Vector<Lambda_term> l_args = new Vector<Lambda_term>();
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			Lambda_term l = view.lambda_term.get(i);
			
			if(l.table_name.equals(subgoal.name))
			{
				l_args.add(l);
				
				break;
			}
		}
				
		Query q = new Query("q" + num, head, body, l_args, conditions, subgoal_mapping);
		
		Query_operation.add(q, q.name, c, pst);
		
	}
	
	static void store_citation_queries(Query view, int num, String qname, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Vector<String> citable_table_names = new Vector<String>();
		
		citable_table_names.addAll(Arrays.asList(citatable_tables));
		
		Subgoal subgoal = null;
		
		while(true)
		{
			Random r = new Random();
			
			int index = r.nextInt(view.body.size());
			
			
			
			subgoal = (Subgoal)view.body.get(index);
			
			if(citable_table_names.contains(subgoal.name))
				break;
		}
		
		String [] primary_key_type = get_primary_key(subgoal.name, c, pst);
		
		
		Vector<Conditions> conditions = query_conditions.get(subgoal.name);
		
		Vector<Argument> head_vars = query_head_names.get(subgoal.name);
		
		Vector<String> subgoals = query_table_names.get(subgoal.name);
		
		Subgoal head = new Subgoal(qname, head_vars);
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		HashMap<String, String> subgoal_mapping = new HashMap<String, String>();
		
		for(int i = 0; i < subgoals.size(); i++)
		{
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(subgoals.get(i), args));
			
			subgoal_mapping.put(subgoals.get(i), subgoals.get(i));
		}
		
		Vector<Lambda_term> l_args = new Vector<Lambda_term>();
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			Lambda_term l = view.lambda_term.get(i);
			
			if(l.table_name.equals(subgoal.name))
			{
				l_args.add(l);
				
				break;
			}
		}
				
		Query q = new Query(qname, head, body, l_args, conditions, subgoal_mapping);
		
		Query_operation.add(q, qname, c, pst);
		
	}
	
	static void store_citation_queries_lambda(Query view, int num, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Vector<String> citable_table_names = new Vector<String>();
		
		citable_table_names.addAll(Arrays.asList(citatable_tables));
		
		Subgoal subgoal = null;
		
		while(true)
		{
			Random r = new Random();
			
			int index = r.nextInt(view.body.size());
			
			
			
			subgoal = (Subgoal)view.body.get(index);
			
			if(citable_table_names.contains(subgoal.name))
				break;
		}
				
		
		Vector<Conditions> conditions = query_conditions.get(subgoal.name);
		
		Vector<Argument> head_vars = query_head_names.get(subgoal.name);
		
		Vector<String> subgoals = query_table_names.get(subgoal.name);
		
		Subgoal head = new Subgoal("q" + num, head_vars);
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		HashMap<String, String> subgoal_mapping = new HashMap<String, String>();
		
		for(int i = 0; i < subgoals.size(); i++)
		{
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(subgoals.get(i), args));
			
			subgoal_mapping.put(subgoals.get(i), subgoals.get(i));
		}
		
		Vector<Lambda_term> l_args = new Vector<Lambda_term>();
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			Lambda_term l = view.lambda_term.get(i);
			
			if(l.table_name.equals(subgoal.name))
			{
				l_args.add(l);
			}
		}
				
		Query q = new Query("q" + num, head, body, l_args, conditions, subgoal_mapping);
		
		Query_operation.add(q, q.name, c, pst);
		
	}
	
	static void get_joinable_relations(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
	    
	    String query_foreign_keys_head = "SELECT tc.constraint_name, tc.table_name, kcu.column_name, ccu.table_name AS foreign_table_name, ccu.column_name AS foreign_column_name FROM information_schema.table_constraints AS tc JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name "
	    		+ "JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name WHERE constraint_type = 'FOREIGN KEY' AND tc.table_name='";
	    
	    for(int i = 0; i<citatable_tables.length; i++)
	    {
	    	String query_foreign_keys = query_foreign_keys_head + citatable_tables[i] + "'";
	    	
	    	pst = c.prepareStatement(query_foreign_keys);
	    	
	    	ResultSet rs = pst.executeQuery();
	    	
	    	while(rs.next())
	    	{
	    		String table_name = rs.getString(2);
	    		
	    		String col_name = rs.getString(3);
	    		
	    		String foreign_table_name = rs.getString(4);
	    		
	    		String foreign_col_name = rs.getString(5);
	    		
	    		String [] t_arr = {table_name, foreign_table_name};
	    		
	    		string_array table_arr = new string_array(t_arr);
	    		
	    		
	    		if(foreign_key_relations.get(table_name) == null)
	    		{
	    			Vector<String> foregin_relations = new Vector<String>();
	    			
	    			foregin_relations.add(foreign_table_name);
	    			
	    			foreign_key_relations.put(table_name, foregin_relations);
	    		}
	    		else
	    		{
	    			Vector<String> foregin_relations = foreign_key_relations.get(table_name);
	    			
	    			foregin_relations.add(foreign_table_name);
	    			
	    			foreign_key_relations.put(table_name, foregin_relations);
	    		}
	    		
	    		
	    		
	    		
	    		if(joinable_attribute_lists.get(table_arr) == null)
	    		{
	    			
	    			
	    			
	    			Vector<string_array> attr_map_ta = new Vector<string_array>();
	    			
	    			String [] col_arr = {col_name, foreign_col_name};
	    			
	    			string_array attr_arr = new string_array(col_arr);
	    			
	    			attr_map_ta.add(attr_arr);
	    			
	    			joinable_attribute_lists.put(table_arr, attr_map_ta);
	    		}
	    		else
	    		{
	    			
	    			Vector<string_array> attr_map_ta = joinable_attribute_lists.get(table_arr);
	    			
	    			String [] col_arr = {col_name, foreign_col_name};
	    			
	    			string_array attr_arr = new string_array(col_arr);
	    			
	    			attr_map_ta.add(attr_arr);
	    			
	    			joinable_attribute_lists.put(table_arr, attr_map_ta);
	    			
	    		}
	    		
	    		
	    	}
	    }
	}
	
	
	static Vector<Integer> generator_random_numbers(int num)
	{
		int i = 0;
		
		Vector<Integer> random_number = new Vector<Integer>();
		
		Random r = new Random();
		
		while(i < num)
		{
			double val = r.nextGaussian() * 5;
			
			if(val <= 0)
				continue;
			
			int value = (int) Math.ceil(val);
			
			if(value == 0)
				continue;
			
			random_number.add(value);
			
			i++;
		}
		return random_number;
	}
	
	static Vector<Integer> generator_random_numbers(int num, int max_num)
	{
		int i = 0;
		
		Vector<Integer> random_number = new Vector<Integer>();
		
		Random r = new Random();
		
		while(i < num)
		{
			double val = r.nextGaussian() * 5;
			
			if(val <= 0 || val > max_num)
				continue;
			
			int value = (int) Math.ceil(val);
			
			if(value == 0)
				continue;
			
			random_number.add(value);
			
			i++;
		}
		return random_number;
	}
	
	static HashSet<Query> gen_views(Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(view_nums);
		
		HashSet<Query> queries = new HashSet<Query>();
		
		int num = 0;
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_view(num, sizes.get(num), c, pst, q);
						
			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
	static Vector<Query> gen_views(Vector<String> subgoal_names, int num_views, int sizeofquety, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(num_views, sizeofquety);
		
		Vector<Query> queries = new Vector<Query>();
		
		int num = 0;
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_view(subgoal_names, num + 1, sizes.get(num), c, pst, q);
						
			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
//	static Vector<Query> gen_views(Vector<String> subgoal_names, int num_views, int sizeofview, int id, Query q) throws SQLException, ClassNotFoundException
//	{
////		Vector<Integer> sizes = generator_random_numbers(num_views, sizeofquety);
//		
//		Connection c = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
//		
//		Vector<Query> queries = new Vector<Query>();
//		
//		for(int k = 0; k<num_views; k++)
//		{
//			
////			int size = sizes.get(num);
//						
//			Query query = generate_view_fixed_size_without_predicates(subgoal_names, id, sizeofview, c, pst, q);
//			
//			queries.add(query);
//			
//			id++;
//		}
//		
//		c.close();
//		
//		return queries;
//	}
	
	static Vector<Query> gen_views_without_predicates(Vector<String> subgoal_names, int num_views, int sizeofquety, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(num_views, sizeofquety);
		
		Vector<Query> queries = new Vector<Query>();
		
		int num = 0;
		
		if(!sizes.contains(1))
			sizes.set(0, 1);
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_view_without_predicates(subgoal_names, num + 1, sizes.get(num), c, pst);
						
//			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
	static Vector<Query> gen_random_views(Vector<String> subgoal_names, int num_views, Query user_query, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(num_views, user_query.body.size());
		
		Vector<Query> queries = new Vector<Query>();
		
		int num = 0;
		
		if(!sizes.contains(1))
			sizes.set(0, 1);
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_random_views(user_query, subgoal_names, num + 1, sizes.get(num), c, pst);
						
//			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
	static Vector<Query> gen_views_without_lambda_terms(Vector<String> subgoal_names, int num_views, int sizeofquety, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(num_views, sizeofquety);
		
		Vector<Query> queries = new Vector<Query>();
		
		int num = 0;
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_view_without_lambda_terms(subgoal_names, num + 1, size, c, pst);
						
//			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
	static void inputquery_conditions(HashMap<String, int[]> selected_ids, HashMap<String, Integer> indexes)
	{
		
		String file1 = "query_selected_values.txt";
		
		String file2 = "query_selected_values_index_gap.txt";
		
		try (BufferedReader br = new BufferedReader(new FileReader(file1))) {
		    String line;		    
		    
		    int num = 0;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String relation = line.split("::")[0];
		    	
		    	String ids = line.split("::")[1];
		    	
		    	String [] all_ids = ids.split(",");
		    	
		    	int [] all_id_integers = new int[all_ids.length];
		    	
		    	for(int i = 0; i<all_id_integers.length; i++)
		    	{
		    		all_id_integers[i] = Integer.valueOf(all_ids[i]);
		    	}
		    	
		    	selected_ids.put(relation, all_id_integers);
		    }
		    
		    
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file2))) {
		    String line;		    
		    
		    int num = 0;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String relation = line.split("::")[0];
		    	
		    	String ids = line.split("::")[1];
		    	
		    	int id_integer = Integer.valueOf(ids);
		    	
		    	indexes.put(relation, id_integer);
		    }
		    
		    
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void gen_one_additional_predicates(Vector<Query> views, Vector<String> subgoal_names, int sizeofquery, Query query, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
//		Vector<Integer> sizes = generator_random_numbers(1, sizeofquery);				
		Random r = new Random();
		
		HashMap<String, int[]> selected_id = new HashMap<String, int[]>();
		
		HashMap<String, Integer> indexes = new HashMap<String, Integer>();
		
		inputquery_conditions(selected_id, indexes);
		
		int id = 0;
		
		Query view = null;
		
		Vector<Query> useful_views = new Vector<Query>();
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			Query v = (Query) iter.next();
			
			if(v.body.size() == 1)
			{
//				Subgoal subgoal = (Subgoal) v.body.get(0);
//				
//				String subgoal_name = subgoal.name;
//				
//				int [] curr_ids = selected_id.get(subgoal_name);
//				
//				int curr_index = indexes.get(subgoal_name);
//				
//				if(curr_index < curr_ids.length)
				
				
				if(v.conditions.size() >= 1)
				{
					Vector<Integer> good_ids = get_interval(v, selected_id);
					
					if(good_ids.size() == 1)
						continue;
				}
				
					useful_views.add(v);
			}
		}
		
		
		while(true)
		{
			int index_for_change = r.nextInt(useful_views.size());
			
//			for(Iterator iter = useful_views.iterator(); iter.hasNext();)
//			{
//				if(id == index_for_change)
//				{
//					view = (Query) iter.next();
//					
//					
//					
//					break;
//				}
//				
//				id++;
//			}
			
			view = useful_views.get(index_for_change);
			
			if(view.conditions.size() >= 1)
			{
				Vector<Integer> good_ids = get_interval(view, selected_id);
				
//				if(good_ids.size() == 1)
//					continue;
//				else
				{
					if(split_view(good_ids, views, view, c1, c2, pst))
					{
						views.remove(view);
						
						int old_id = view_operation.delete_view_by_name(view.name, c1, pst);
						
						view_operation.delete_view_by_name(view.name, c2, pst, false);
						
						citation_view_operation.delete_citation_views_by_id(old_id, c1, pst);
						
						citation_view_operation.delete_citation_views_by_id(old_id, c2, pst);
						
						Query_operation.delete_query_by_id(old_id, c1, pst);
						
						Query_operation.delete_query_by_id(old_id, c2, pst);
						
						break;
					}
				}
			}
			
			if(split_view(views, view, c1, c2, pst))
			{
				views.remove(view);
				
				int old_id = view_operation.delete_view_by_name(view.name, c1, pst);
				
				view_operation.delete_view_by_name(view.name, c2, pst, false);
				
				citation_view_operation.delete_citation_views_by_id(old_id, c1, pst);
				
				citation_view_operation.delete_citation_views_by_id(old_id, c2, pst);
				
				Query_operation.delete_query_by_id(old_id, c1, pst);
				
				Query_operation.delete_query_by_id(old_id, c2, pst);
				
				break;
			}
		}
				
	}
	
	static Vector<Integer> get_interval_upper(int [] curr_ids, double value)
	{
		Vector<Integer> values = new Vector<Integer>();
		
		
		for(int i = 0; i<curr_ids.length; i++)
		{
			int id = curr_ids[i];
			
			if(id > value)
			{
				values.add(id);
			}
		}
		
		return values;
	}
	
	static Vector<Integer> get_interval_lower(int [] curr_ids, double value)
	{
		Vector<Integer> values = new Vector<Integer>();
		
		
		for(int i = 0; i<curr_ids.length; i++)
		{
			int id = curr_ids[i];
			
			if(id < value)
			{
				values.add(id);
			}
		}
		
		return values;
	}
	
	static Vector<Integer> get_interval(Query v, HashMap<String, int[]> selected_id)
	{
		Subgoal subgoal = (Subgoal) v.body.get(0);
		
		String subgoal_name = subgoal.name;
		
		int [] curr_ids = selected_id.get(subgoal_name);
		
		if(v.conditions.size() == 1)
		{
			String arg = v.conditions.get(0).arg2.name;
			
			arg = arg.replace("'", "");
			
			double value = Double.valueOf(arg);
			
			if(v.conditions.get(0).op.toString().equals(">"))
			{
				Vector<Integer> values = get_interval_upper(curr_ids, value);
				
				return values;
			}
			else
			{
				Vector<Integer> values = get_interval_lower(curr_ids, value);
				
				return values;
			}
		}
		else
		{
			
			String arg1 = v.conditions.get(0).arg2.name.replace("'", "");
			
			double value1 = Double.valueOf(arg1);
			
			String arg2 = v.conditions.get(1).arg2.name.replace("'", "");
			
			double value2 = Double.valueOf(arg2);
			
			Vector<Integer> values1 = null;
			
			Vector<Integer> values2 = null;
			
			
			// > value1 && <= value2
			if(v.conditions.get(0).op.toString().equals(">"))
			{
				values1 = get_interval_upper(curr_ids, value1);
				
				values2 = get_interval_lower(curr_ids, value2);
			}
			else
			{
				values1 = get_interval_lower(curr_ids, value1);
				
				values2 = get_interval_upper(curr_ids, value2);
			}
			
			values1.retainAll(values2);
			
			return values1;
			
		}
	}
	
	static int get_candidate_parameters(Query view)
	{
		int para_size = 0;
		
		for(int j = 0; j < view.body.size(); j++)
		{
			Subgoal subgoal = (Subgoal) view.body.get(j);
			
			para_size += query_generator.parameterizable_attri.get(subgoal.name).size();
		}
		
		return para_size;
	}
	
	public static boolean gen_one_addtional_lambda_term(Vector<Query> views, HashSet<String> lambda_variable_strings, Connection c1, Connection c2, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
			
		int i = 0;
		
		for(i = 0; i<views.size(); i++)
		{
			
			int para_size = get_candidate_parameters(views.get(i));
			
			if(para_size > views.get(i).lambda_term.size())
				break;
		}
		
		if(i >= views.size())
		{
			System.out.println("no other lambda variables available");
			
			return false;
		}
		
		
		Random r = new Random();
		
		int rel_index = 0;
		
		while(true)
		{
			rel_index = r.nextInt(views.size());
			
			int head_size = get_candidate_parameters(views.get(rel_index));//views.get(rel_index).head.args.size();
			
			int lambda_term_size = views.get(rel_index).lambda_term.size();
			
			if(head_size > lambda_term_size)
				break;
			
			
		}
		
		Lambda_term l_term = null;

		
		while(true)
		{
			int lambda_index = r.nextInt(get_candidate_parameters(views.get(rel_index)));
			
			int number = 0;
			
			String subgoal_arg_string = null;
			
			
			for(int k = 0; k<views.get(rel_index).body.size(); k++)
			{
				Subgoal subgoal = (Subgoal)views.get(rel_index).body.get(k);
				
				number += query_generator.parameterizable_attri.get(subgoal.name).size();
				
				if(number > lambda_index)
				{
//					query_generator.parameterizable_attri.get(subgoal.name).get(lambda_index - (number - query_generator.parameterizable_attri.get(subgoal.name).size()));
					
//					selected_arg = (Argument)subgoal.args.get(lambda_index - (number - subgoal.args.size()));
					subgoal_arg_string = subgoal.name + populate_db.separator + query_generator.parameterizable_attri.get(subgoal.name).get(lambda_index - (number - query_generator.parameterizable_attri.get(subgoal.name).size()));
					
					l_term = new Lambda_term(subgoal_arg_string, subgoal.name);
					
					break;
				}
			}
			
			views.get(rel_index).lambda_term.add(l_term);
			
			String lambda_variable_string = views.get(rel_index).name + populate_db.separator + subgoal_arg_string;
			
			if(lambda_variable_strings.contains(lambda_variable_string))
				continue;
			else
			{
				lambda_variable_strings.add(lambda_variable_string);
				
				break;
			}
			
		}
		
		int view_id = view_operation.get_view_id_by_name(views.get(rel_index).name, c2, pst);
		
		view_operation.add_lambda_term(view_id, l_term, c1, pst);
		
		view_operation.add_lambda_term(view_id, l_term, c2, pst);
				
//		int old_id = view_operation.delete_view_by_name(views.get(rel_index).name, c1, pst);
//		
//		view_operation.delete_view_by_name(views.get(rel_index).name, c2, pst, false);
//		
//		citation_view_operation.delete_citation_views_by_id(old_id, c1, pst);
//		
//		citation_view_operation.delete_citation_views_by_id(old_id, c2, pst);
//		
//		Query_operation.delete_query_by_id(old_id, c1, pst);
//		
//		Query_operation.delete_query_by_id(old_id, c2, pst);
//		
//		int view_id = view_operation.get_view_num(c2, pst);
//		
//		
//		String view_name = "v" + view_id;
//		
//		int id = view_operation.add(views.get(rel_index), view_name, c1, pst);
//				
//		view_operation.add(views.get(rel_index), view_name, c2, pst, false);
//				
//		store_single_citation_view_lambda(views.get(rel_index), id, c1, pst);
//		
//		store_single_citation_view_lambda(views.get(rel_index), id, c2, pst);
		
		return true;
		
		
	}
	
	public static boolean gen_one_addtional_lambda_term(Vector<Integer> view_ids, Vector<Query> views, HashSet<String> lambda_variable_strings, Connection c1, Connection c2, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
			
		int i = 0;
		
		for(i = 0; i<views.size(); i++)
		{
			
			int para_size = get_candidate_parameters(views.get(i));
			
			if(para_size > views.get(i).lambda_term.size())
				break;
		}
		
		if(i >= views.size())
		{
			System.out.println("no other lambda variables available");
			
			return false;
		}
		
		
		Random r = new Random();
		
		int rel_index = 0;
		
		while(true)
		{
			rel_index = view_ids.get(r.nextInt(view_ids.size()));
			
			int head_size = get_candidate_parameters(views.get(rel_index));//views.get(rel_index).head.args.size();
			
			int lambda_term_size = views.get(rel_index).lambda_term.size();
			
			if(head_size > lambda_term_size)
				break;
			
			
		}
		
		Lambda_term l_term = null;
		
		while(true)
		{
			int lambda_index = r.nextInt(get_candidate_parameters(views.get(rel_index)));
			
			int number = 0;
			
			String subgoal_arg_string = null;
			
			
			
			for(int k = 0; k<views.get(rel_index).body.size(); k++)
			{
				Subgoal subgoal = (Subgoal)views.get(rel_index).body.get(k);
				
				number += query_generator.parameterizable_attri.get(subgoal.name).size();
				
				if(number > lambda_index)
				{
//					query_generator.parameterizable_attri.get(subgoal.name).get(lambda_index - (number - query_generator.parameterizable_attri.get(subgoal.name).size()));
					
//					selected_arg = (Argument)subgoal.args.get(lambda_index - (number - subgoal.args.size()));
					subgoal_arg_string = subgoal.name + populate_db.separator + query_generator.parameterizable_attri.get(subgoal.name).get(lambda_index - (number - query_generator.parameterizable_attri.get(subgoal.name).size()));
					
					l_term = new Lambda_term(subgoal_arg_string, subgoal.name);
					
					break;
				}
			}
			
			views.get(rel_index).lambda_term.add(l_term);
			
			String lambda_variable_string = views.get(rel_index).name + populate_db.separator + subgoal_arg_string;
			
			if(lambda_variable_strings.contains(lambda_variable_string))
				continue;
			else
			{
				lambda_variable_strings.add(lambda_variable_string);
				
				break;
			}
			
		}
		
		int view_id = view_operation.get_view_id_by_name(views.get(rel_index).name, c2, pst);
		
		view_operation.add_lambda_term(view_id, l_term, c1, pst);
		
		view_operation.add_lambda_term(view_id, l_term, c2, pst);
				
//		int old_id = view_operation.delete_view_by_name(views.get(rel_index).name, c1, pst);
//		
//		view_operation.delete_view_by_name(views.get(rel_index).name, c2, pst, false);
//		
//		citation_view_operation.delete_citation_views_by_id(old_id, c1, pst);
//		
//		citation_view_operation.delete_citation_views_by_id(old_id, c2, pst);
//		
//		Query_operation.delete_query_by_id(old_id, c1, pst);
//		
//		Query_operation.delete_query_by_id(old_id, c2, pst);
//		
//		int view_id = view_operation.get_view_num(c2, pst);
//		
//		
//		String view_name = "v" + view_id;
//		
//		int id = view_operation.add(views.get(rel_index), view_name, c1, pst);
//				
//		view_operation.add(views.get(rel_index), view_name, c2, pst, false);
//				
//		store_single_citation_view_lambda(views.get(rel_index), id, c1, pst);
//		
//		store_single_citation_view_lambda(views.get(rel_index), id, c2, pst);
		
		return true;
		
		
	}
	
	static boolean check_duplicated_conditions(Vector<Query> views, String relation, String attr_name, String constant)
	{
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			Query view = (Query) iter.next();
			
			for(int i = 0; i<view.conditions.size(); i++)
			{
				Conditions condition = view.conditions.get(i);
				
				if(condition.arg2.isConst())
				{
					if(view.subgoal_name_mapping.get(condition.subgoal1).equals(relation) && condition.arg1.name.equals(attr_name) && condition.arg2.name.equals(constant))
						return true;
				}
			}
			
		}
		
		return false;
	}
	
	static Vector<Integer> find_conflict_condition(Query view, String relation, String attr_name, Vector<Integer> list)
	{
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		for(int i = 0; i<view.conditions.size(); i++)
		{
			Conditions condition = view.conditions.get(i);
			
			if(condition.subgoal1.equals(relation) && condition.arg1.name.equals(attr_name) && condition.arg2.isConst())
			{		
				
				conditions.add(condition);
//				{
//								
//					String value_str = condition.arg2.name;
//					
//					int value = Integer.valueOf(value_str.substring(1, value_str.length() - 1));
//					
//					if(condition.op.get_op_name().equals(">"))
//					{
//						return list.size();
//					}
//					else
//					{
//						if(condition.op.get_op_name().equals("<="))
//						{
//							return 0;
//						}
//					}
//				}
			}
		}
		
		
		
		if(conditions.size() == 0)
			return new Vector<Integer>();
		else
		{
			if(conditions.size() == 1)
			{
				if(conditions.get(0).op.get_op_name().equals(">"))
				{
					String value_str = conditions.get(0).arg2.name;
					
					int value = Integer.valueOf(value_str.substring(1, value_str.length() - 1));
					
					int id = list.indexOf(value);
										
					Vector<Integer> ids = new Vector<Integer>();
					
					if(id + 1 >= list.size())
						return null;
					
					ids.add(id + 1);
					
					ids.add(list.size());
					
					return ids;
				}
				else
				{
					if(conditions.get(0).op.get_op_name().equals("<="))
					{
						String value_str = conditions.get(0).arg2.name;
						
						int value = Integer.valueOf(value_str.substring(1, value_str.length() - 1));
						
						int id = list.indexOf(value);
												
						Vector<Integer> ids = new Vector<Integer>();
						
						if(id <= 1)
							return null;
						
						ids.add(0);
						
						ids.add(id);
						
						return ids;
					}
				}
			}
			else
			{
				Vector<Integer> ids = new Vector<Integer>();
				
				if(conditions.get(0).op.get_op_name().equals(">"))
				{
					String value_str1 = conditions.get(0).arg2.name;
					
					String value_str2 = conditions.get(1).arg2.name;
					
					int value1 = Integer.valueOf(value_str1.substring(1, value_str1.length() - 1));
					
					int value2 = Integer.valueOf(value_str2.substring(1, value_str2.length() - 1));
					
					int id1 = list.indexOf(value1);
					
					int id2 = list.indexOf(value2);
																
					if(id1 + 1 >= id2)
					{
						return null;
					}
					
						ids.add(id1 + 1);
						
						ids.add(id2);
					
					return ids;
				}
				else
				{
					String value_str1 = conditions.get(1).arg2.name;
					
					String value_str2 = conditions.get(0).arg2.name;
					
					int value1 = Integer.valueOf(value_str1.substring(1, value_str1.length() - 1));
					
					int value2 = Integer.valueOf(value_str2.substring(1, value_str2.length() - 1));
					
					int id1 = list.indexOf(value1);
					
					int id2 = list.indexOf(value2);
																
					if(id1 + 1 >= id2)
					{
						return null;
					}
					
						ids.add(id1 + 1);
						
						ids.add(id2);
					
					return ids;
				}
			}
		}
		
		return new Vector<Integer>();
		
	}
	
	static void remove_duplicate_predicates(Query view, Vector<Integer> list, String relation, String attr_name)
	{
		Vector<Integer> ids1 = new Vector<Integer>();
		
		Vector<Integer> ids2 = new Vector<Integer>();
		
		int smallest_id = -1;
		
		double smallest_value = -1;
		
		int largest_id = list.size();
		
		double largest_value = list.lastElement();
		
		for(int i = 0; i<view.conditions.size(); i++)
		{
			Conditions condition = view.conditions.get(i);
			
			if(condition.subgoal1.equals(relation) && condition.arg1.name.equals(attr_name) && condition.arg2.isConst())
			{
				String value_str = condition.arg2.name;
				
				double value = Double.valueOf(value_str.substring(1, value_str.length() - 1));
				
				if(condition.op.get_op_name().equals(">"))
				{
					if(value > smallest_value)
					{
						smallest_id = i;
						
						smallest_value = value;
					}
					
					ids1.add(i);
				}
				else
				{
					if(value < largest_value)
					{
						largest_id = i;
						
						largest_value = value;
					}
					
					ids2.add(i);
				}
			}
			
			
			
		}
		
		if(ids1.size() >= 2)
		{
			ids1.removeElement(smallest_id);
			
			view.conditions.removeElementAt(ids1.get(0));
			
		}
		
		if(ids2.size() >= 2)
		{
			ids2.removeElement(largest_id);
			
			view.conditions.removeElementAt(ids2.get(0));
		}
	}
	
	static boolean split_view(Vector<Query> views, Query view, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		String relation = null;
		
		String origin_name = null;
		
		Random r = new Random();
		
		if(view.conditions.isEmpty())
		{
			
			int index = r.nextInt(view.body.size());
			
			Subgoal subgoal = (Subgoal) view.body.get(index);
			
			origin_name = subgoal.name;
			
			relation = view.subgoal_name_mapping.get(subgoal.name);
			
			
		}
		else
		{
			origin_name = view.conditions.get(0).subgoal1;
			
			relation = view.subgoal_name_mapping.get(origin_name);
		}
		
		String [] primary_key_type = get_primary_key(relation, c2, pst);
		
		Vector<Integer> ranges = query_generator.relation_primary_key_ranges.get(relation);
		
		if(ranges == null)
		{
			query_generator.build_relation_primary_key_mapping(c2, pst);
			
			ranges = query_generator.relation_primary_key_ranges.get(relation);
		}
		
		Vector<Integer> conflict_ids = find_conflict_condition(view, origin_name, primary_key_type[0], ranges);
		
		if(conflict_ids == null)
			return false;
		
//		while(conflict_ids == null)
//		{
//			index = r.nextInt(view.body.size());
//			
//			subgoal = (Subgoal) view.body.get(index);
//			
//			relation = view.subgoal_name_mapping.get(subgoal.name);
//			
//			primary_key_type = get_primary_key(relation, c, pst);
//			
//			ranges = query_generator.relation_primary_key_ranges.get(relation);
//			
//			conflict_ids = find_conflict_condition(view, subgoal.name, primary_key_type[0], ranges);
//		}
		
		
		int const_id = 0;
		
		if(conflict_ids.isEmpty())
			const_id = r.nextInt(ranges.size() - 1);
		else
		{
			int range = conflict_ids.get(1) - conflict_ids.get(0);
			
			const_id = conflict_ids.get(0) + r.nextInt(range);
		}
		
		
		
		while(check_duplicated_conditions(views, relation, primary_key_type[0], "'" + ranges.get(const_id) + "'"))
		{
			int range = conflict_ids.get(1) - conflict_ids.get(0);
			
			const_id = conflict_ids.get(0) + r.nextInt(range);
		}
		
		Conditions condition1 = new Conditions(new Argument(primary_key_type[0], origin_name), origin_name, new op_greater(), new Argument("'" + ranges.get(const_id) + "'"), new String());
		
		Conditions condition2 = new Conditions(new Argument(primary_key_type[0], origin_name), origin_name, new op_less_equal(), new Argument("'" + ranges.get(const_id) + "'"), new String());
		
		Query view1 = (Query) view.clone();
		
		
		view1.conditions.add(condition1);
		
		remove_duplicate_predicates(view1, ranges, relation, primary_key_type[0]);

		
		view1.name = view.name + "_1";
				
		Query view2 = (Query) view.clone();
		
		
		view2.name = view.name + "_2";
						
		view2.conditions.add(condition2);
		
		remove_duplicate_predicates(view2, ranges, relation, primary_key_type[0]);

				
		int v1_id = view_operation.add(view1, view1.name, c1, pst);
		
		view1.name = view.name + "_1";

		
		view_operation.add(view1, view1.name, c2, pst, false);
		
		view1.name = view.name + "_1";
		
		String citation_view_name1 = view1.name.replaceFirst("v", "c");

		String query_name1 = view1.name.replaceFirst("v", "q");
		
		store_single_citation_view(view1, v1_id, citation_view_name1, query_name1, c1, pst);
		
		store_single_citation_view(view1, v1_id, citation_view_name1, query_name1, c2, pst);
		
		int v2_id = view_operation.add(view2, view2.name, c1, pst);
		
		view2.name = view.name + "_2";
		
		view_operation.add(view2, view2.name, c2, pst, false);
		
		view2.name = view.name + "_2";

		String citation_view_name2 = view2.name.replaceFirst("v", "c");

		String query_name2 = view2.name.replaceFirst("v", "q");
		
		store_single_citation_view(view2, v2_id, citation_view_name2, query_name2, c1, pst);
		
		store_single_citation_view(view2, v2_id, citation_view_name2, query_name2, c2, pst);
		
		views.add(view1);
		
		views.add(view2);
		
		return true;
	}
	
	static boolean split_view(Vector<Integer> ids, Vector<Query> views, Query view, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		String relation = null;
		
		String origin_name = null;
		
		Random r = new Random();
		
		if(view.conditions.isEmpty())
		{
			
			int index = r.nextInt(view.body.size());
			
			Subgoal subgoal = (Subgoal) view.body.get(index);
			
			origin_name = subgoal.name;
			
			relation = view.subgoal_name_mapping.get(subgoal.name);
			
			
		}
		else
		{
			origin_name = view.conditions.get(0).subgoal1;
			
			relation = view.subgoal_name_mapping.get(origin_name);
		}
		
		String [] primary_key_type = get_primary_key(relation, c2, pst);
		
		Vector<Integer> ranges = query_generator.relation_primary_key_ranges.get(relation);
		
		if(ranges == null)
		{
			query_generator.build_relation_primary_key_mapping(c2, pst);
			
			ranges = query_generator.relation_primary_key_ranges.get(relation);
		}
		
		Vector<Integer> conflict_ids = find_conflict_condition(view, origin_name, primary_key_type[0], ranges);
		
		if(conflict_ids == null)
			return false;
		
//		while(conflict_ids == null)
//		{
//			index = r.nextInt(view.body.size());
//			
//			subgoal = (Subgoal) view.body.get(index);
//			
//			relation = view.subgoal_name_mapping.get(subgoal.name);
//			
//			primary_key_type = get_primary_key(relation, c, pst);
//			
//			ranges = query_generator.relation_primary_key_ranges.get(relation);
//			
//			conflict_ids = find_conflict_condition(view, subgoal.name, primary_key_type[0], ranges);
//		}
		
		
//		int const_id = 0;
//		
//		if(conflict_ids.isEmpty())
//			const_id = r.nextInt(ranges.size() - 1);
//		else
//		{
//			int range = conflict_ids.get(1) - conflict_ids.get(0);
//			
//			const_id = conflict_ids.get(0) + r.nextInt(range);
//		}
//		
//		
//		
//		while(check_duplicated_conditions(views, relation, primary_key_type[0], "'" + ranges.get(const_id) + "'"))
//		{
//			int range = conflict_ids.get(1) - conflict_ids.get(0);
//			
//			const_id = conflict_ids.get(0) + r.nextInt(range);
//		}
		
		int selected_curr_id = r.nextInt(ids.size() - 1);
		
		int lower_value = ids.get(selected_curr_id);
		
		int higher_value = ids.get(selected_curr_id + 1);
		
		int inter_value = (lower_value + higher_value)/2;
		
		
		Conditions condition1 = new Conditions(new Argument(primary_key_type[0], origin_name), origin_name, new op_greater(), new Argument("'" + inter_value + "'"), new String());
		
		Conditions condition2 = new Conditions(new Argument(primary_key_type[0], origin_name), origin_name, new op_less_equal(), new Argument("'" + inter_value + "'"), new String());
		
		Query view1 = (Query) view.clone();
		
		
		view1.conditions.add(condition1);
		
		remove_duplicate_predicates(view1, ranges, relation, primary_key_type[0]);

		
		view1.name = view.name + "_1";
				
		Query view2 = (Query) view.clone();
		
		
		view2.name = view.name + "_2";
						
		view2.conditions.add(condition2);
		
		remove_duplicate_predicates(view2, ranges, relation, primary_key_type[0]);

				
		int v1_id = view_operation.add(view1, view1.name, c1, pst);
		
		view1.name = view.name + "_1";

		
		view_operation.add(view1, view1.name, c2, pst, false);
		
		view1.name = view.name + "_1";
		
		String citation_view_name1 = view1.name.replaceFirst("v", "c");

		String query_name1 = view1.name.replaceFirst("v", "q");
		
		store_single_citation_view(view1, v1_id, citation_view_name1, query_name1, c1, pst);
		
		store_single_citation_view(view1, v1_id, citation_view_name1, query_name1, c2, pst);
		
		int v2_id = view_operation.add(view2, view2.name, c1, pst);
		
		view2.name = view.name + "_2";
		
		view_operation.add(view2, view2.name, c2, pst, false);
		
		view2.name = view.name + "_2";

		String citation_view_name2 = view2.name.replaceFirst("v", "c");

		String query_name2 = view2.name.replaceFirst("v", "q");
		
		store_single_citation_view(view2, v2_id, citation_view_name2, query_name2, c1, pst);
		
		store_single_citation_view(view2, v2_id, citation_view_name2, query_name2, c2, pst);
		
		views.add(view1);
		
		views.add(view2);
		
		return true;
	}
	
	public static void gen_one_additional_view(Vector<Query> views, Vector<String> subgoal_names, int sizeofquery, Query q, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Integer> sizes = generator_random_numbers(1, subgoal_names.size());
				
		int old_num = views.size();
		
		while(views.size() < old_num + 1)
		{
			
			int size = sizes.get(0);
						
			Query query = generate_view_without_predicates(subgoal_names, views.size() + 1, sizes.get(0), c1, pst);
						
//			if(!views.contains(query))
			{
				views.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
		    	
		    	int curr_num = old_num + 1;
		    	
		    	view_operation.add(query, query.name, c1, pst, true);
		    	
		    	view_operation.add(query, query.name, c2, pst, false);
		    	
		    	citation_view_operation.add_citation_view("c" + curr_num, c1, pst);
		    	
		    	citation_view_operation.add_citation_view("c" + curr_num, c2, pst);
				
				citation_view_operation.add_connection_view_with_citations("c" + curr_num, query.name, c1, pst);
				
				citation_view_operation.add_connection_view_with_citations("c" + curr_num, query.name, c2, pst);
				
				store_citation_queries(query, curr_num, c1, pst);
				
				store_citation_queries(query, curr_num, c2, pst);
				
				Query_operation.add_connection_citation_with_query("c" + curr_num, "q" + curr_num , "author", c1, pst);
				
				Query_operation.add_connection_citation_with_query("c" + curr_num, "q" + curr_num , "author", c2, pst);
		    	
			}
			
			System.out.println(query);
			
			
		}		
		
	}
	
	static Query generate_view(int id, int size, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (citatable_tables.length));
			
			String relation = citatable_tables[index];
			
			if(relation_names.contains(relation))
			{
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = new Vector<String> ();
			
			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
			
			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst, q);
					
			local_predicates.addAll(conditions);
			
			int head_size = rand.nextInt((int)(attr_list.size() * head_var_rate + 1)) + 1;
									
			Vector<Argument> head_vars = gen_head_vars(relation, attr_list, head_size, c, pst);
			
			
			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
			
			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "V" + id;
		
		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		return new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
	}
	
	
	static Query generate_view(Vector<String> subgoal_names, int id, int size, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (subgoal_names.size()));
			
			String relation = subgoal_names.get(index);
			
			if(relation_names.contains(relation))
			{
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = new Vector<String> ();
			
			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
			
			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst, q);
					
			local_predicates.addAll(conditions);
			
			int head_size = rand.nextInt((int)(attr_list.size() * head_var_rate + 1)) + 1;
									
			Vector<Argument> head_vars = gen_head_vars(relation, attr_list, head_size, c, pst);
			
			
			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
			
			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		return new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
	}
	
	static Query generate_view_fixed_size_without_predicates(Vector<String> subgoal_names, int id, int size, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (subgoal_names.size()));
			
			String relation = subgoal_names.get(index);
			
			if(relation_names.contains(relation))
			{
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = new Vector<String> ();
			
			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
//			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
//			String [] primary_key_type = get_primary_key(relation, c, pst);
			
//			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst, q);
					
//			local_predicates.addAll(conditions);
			
			int head_size = rand.nextInt((int)(attr_list.size() * head_var_rate + 1)) + 1;
									
			Vector<Argument> head_vars = gen_head_vars(relation, attr_list, head_size, c, pst);
			
			
			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
			
			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
//		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
//		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		return new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
	}
	
	static Vector<Argument> gen_head_vars(String relation, String relation_name, Vector<String> attr_list, int size, Connection c, PreparedStatement pst)
	{
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		Vector<Argument> head_vars = new Vector<Argument>();
		
		Random r = new Random();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt(attr_list.size());
			
//			if(query_generator.parameterizable_attri.get(relation).contains(attr_list.get(index)))
//			if(!id_set.contains(index))
			{
				id_set.add(index);
			}
//			
		}
		
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument l = new Argument(relation_name+ populate_db.separator + attr_list.get(index), relation_name);
			
			head_vars.add(l);
			
		}
		
		return head_vars;
	}
	
	static Query generate_default_view(String subgoal_name, int id, Connection c1, Connection c2, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		maps.put(subgoal_name, subgoal_name);
		
		HashMap<String, String> attr_types = get_attr_types(subgoal_name, c1, pst);
		
		
		Set<String> attr_names = attr_types.keySet();
		
//		Vector<String> attr_list = new Vector<String> ();
//		
//		attr_list.addAll(attr_names);
		
		Vector<String> attr_list = query_generator.parameterizable_attri.get(subgoal_name);
		
		Random rand = new Random();
		
		int size = (attr_list.size());
		
		Vector<Argument> head_vars = gen_all_head_vars(subgoal_name, attr_list, c2, pst);//gen_head_vars(subgoal_name, subgoal_name, attr_list, size, c2, pst);//(subgoal_name, attr_list, c1, pst);
		
		
		Vector<Lambda_term> l_terms = gen_default_lambda_terms(subgoal_name, c1, pst);
		
		heads.addAll(head_vars);
		
//		lambda_terms.addAll(l_terms);
		
		Vector<Argument> args = new Vector<Argument>();
		
		body.add(new Subgoal(subgoal_name, args));
		
		String name = "v" + id;
		
		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		Query view =  new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
		
		view_operation.add(view, view.name, c1, pst, true);
		
		view_operation.add(view, view.name, c2, pst, false);
		
		citation_view_operation.add_citation_view("c" + id, c1, pst);
		
		citation_view_operation.add_citation_view("c" + id, c2, pst);
		
		citation_view_operation.add_connection_view_with_citations("c" + id, name, c1, pst);
		
		citation_view_operation.add_connection_view_with_citations("c" + id, name, c2, pst);
		
		store_citation_queries(view, id, c1, pst);
		
		store_citation_queries(view, id, c2, pst);
		
		Query_operation.add_connection_citation_with_query("c" + id, "q" + id , "author", c1, pst);
		
		Query_operation.add_connection_citation_with_query("c" + id, "q" + id , "author", c2, pst);
		
		
		return view;
	}
	
	static Query generate_view_without_predicates(Vector<String> subgoal_names, int id, int size, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (subgoal_names.size()));
			
			String relation = subgoal_names.get(index);
			
			if(relation_names.contains(relation))
			{
				i--;
				
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = query_generator.parameterizable_attri.get(relation);
			
//			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
			
//			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst);
					
//			local_predicates.addAll(conditions);
			
			int head_size = attr_list.size();
									
//			Vector<Argument> head_vars = gen_default_head_vars(relation, attr_list, c, pst);//gen_head_vars(relation, attr_list, head_size, c, pst);
			
//			Vector<Argument> head_vars = gen_all_head_vars(relation, attr_list, c, pst);//(relation, attr_list, c, pst);//gen_head_vars(relation, attr_list, head_size, c, pst);
			
			Vector<Argument> head_vars = gen_head_vars(relation, relation, attr_list, head_size, c, pst);
			
//			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
						
//			lambda_terms.add(new Lambda_term(relation + populate_db.separator + primary_key_type[0], relation));
			
//			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
//		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
//		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		return new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
	}
	
	static Query generate_random_views(Query query, Vector<String> subgoal_names, int id, int size, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (subgoal_names.size()));
			
			String relation = subgoal_names.get(index);
			
			if(relation_names.contains(relation))
			{
				i--;
				
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = query_generator.parameterizable_attri.get(relation);
			
//			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
			
//			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst);
					
//			local_predicates.addAll(conditions);
			
			int head_size = attr_list.size();
									
//			Vector<Argument> head_vars = gen_default_head_vars(relation, attr_list, c, pst);//gen_head_vars(relation, attr_list, head_size, c, pst);
			
//			Vector<Argument> head_vars = gen_all_head_vars(relation, attr_list, c, pst);//(relation, attr_list, c, pst);//gen_head_vars(relation, attr_list, head_size, c, pst);
			
			Vector<Argument> head_vars = gen_head_vars(relation, relation, attr_list, head_size, c, pst);
			
//			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
						
//			lambda_terms.add(new Lambda_term(relation + populate_db.separator + primary_key_type[0], relation));
			
//			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
//		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
//		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		Query view = new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
		
		double d = Math.random();
		
		if(d >= with_predicate)
		{
			gen_one_local_predicate(view, c, pst, query);
		}
		
		return view;
	}
	
	static Query generate_view_without_lambda_terms(Vector<String> subgoal_names, int id, int size, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt((int) (subgoal_names.size()));
			
			String relation = subgoal_names.get(index);
			
			if(relation_names.contains(relation))
			{
				continue;
			}
			else
			{
				relation_names.add(relation);
				
				maps.put(relation, relation);
			}
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = query_generator.parameterizable_attri.get(relation);//get_attrs(relation, c, pst);
			
//			attr_list.addAll(attr_names);
			
			Random rand = new Random();
			
			int selection_size = rand.nextInt((int)(attr_list.size() * local_predicates_rate + 1));
			
			String [] primary_key_type = get_primary_key(relation, c, pst);
			
//			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst);
					
//			local_predicates.addAll(conditions);
			
			int head_size = rand.nextInt(attr_list.size()) + 1;
									
			Vector<Argument> head_vars = gen_all_head_vars(relation, attr_list, c, pst);//(relation, relation, attr_list, head_size, c, pst);//(relation, attr_list, c, pst);
			
			
//			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
						
//			lambda_terms.add(new Lambda_term(relation + populate_db.separator + primary_key_type[0], relation));
			
//			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
//		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
//		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
//		lambda_terms = get_one_lambda_term(heads, r);
		
		return new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
	}
	
	static Vector<Lambda_term> get_one_lambda_term(Vector<Argument> heads, Random r)
	{
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		while(true)
		{
			int head_index = r.nextInt(heads.size());
			
			Argument arg = heads.get(head_index);
			
			String attr_name = arg.name.substring(arg.relation_name.length() + 1, arg.name.length());
			
			if(query_generator.parameterizable_attri.get(arg.relation_name).contains(attr_name))
			{
				l_terms.add(new Lambda_term(arg.name, arg.relation_name));
				
				break;
			}
			
		}
	
		return l_terms;
	}

	
	static Query generate_views_with_n_subgoals_without_predicates(Vector<String> subgoal_names, int id, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{		
		HashSet<String> relation_names = new HashSet<String>();
		
		Vector<Argument> heads = new Vector<Argument>();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Vector<Conditions> local_predicates = new Vector<Conditions>();
		
		HashMap<String, String> maps = new HashMap<String, String>();
		
		Vector<Subgoal> body = new Vector<Subgoal>();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{			
			String relation = subgoal_names.get(i);
			
			maps.put(relation, relation);
			
			HashMap<String, String> attr_types = get_attr_types(relation, c, pst);
			
			
			Set<String> attr_names = attr_types.keySet();
			
			Vector<String> attr_list = new Vector<String> ();
			
			attr_list.addAll(attr_names);
												
//			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst);
					
//			local_predicates.addAll(conditions);
												
			Vector<Argument> head_vars = gen_default_head_vars(relation, attr_list, c, pst);
			
			
			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation, c, pst);
			
			heads.addAll(head_vars);
			
			lambda_terms.addAll(l_terms);
			
			Vector<Argument> args = new Vector<Argument>();
			
			body.add(new Subgoal(relation, args));
		}
		
		String name = "v" + id;
		
		Vector<Conditions> global_predicates = gen_global_conditions(body);
		
		Vector<Conditions> predicates = new Vector<Conditions>();
		
		predicates.addAll(global_predicates);
		
		predicates.addAll(local_predicates);
		
		Query view = new Query(name, new Subgoal(name, heads), body, lambda_terms, predicates, maps);
		
		
		
		
		return view;
	}

	
	static Vector<String[]> get_foreign_key_pairs(Vector<String> subgoal_names)
	{
		
		Vector<String[]> foreign_key_pairs = new Vector<String []>();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{
			if(foreign_key_relations.get(subgoal_names.get(i)) != null)
			{
				Vector<String> foreign_tables = foreign_key_relations.get(subgoal_names.get(i));
				
				for(int k = 0; k<foreign_tables.size(); k++)
				{
					if(subgoal_names.contains(foreign_tables.get(k)))
					{
						String [] pairs = {subgoal_names.get(i), foreign_key_relations.get(subgoal_names.get(i)).get(k)};
						
						foreign_key_pairs.add(pairs);
					}
				}
				
				
			}
		}
		
		return foreign_key_pairs;
	}
	
	
	static Vector<Conditions> gen_global_conditions(Vector<Subgoal> body)
	{
		Vector<String> subgoal_names = new Vector<String>();
		
		Vector<Conditions> global_predicates = new Vector<Conditions>();
		
		for(int i = 0; i<body.size(); i++)
		{
			subgoal_names.add(body.get(i).name);
		}
		
		Vector<String[]> foreign_key_pairs =  get_foreign_key_pairs(subgoal_names);
		
		Random rand = new Random();
		
		int size = rand.nextInt((int)(foreign_key_pairs.size() * global_predicate_rate + 1));
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		for(int i = 0; i<size; i++)
		{
			int index = rand.nextInt(foreign_key_pairs.size());
			
			id_set.add(index);
		}
		
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			int index = (int) iter.next();
			
			String [] foreign_key_pair = foreign_key_pairs.get(index);
			
			string_array relation_array = new string_array(foreign_key_pair);
			
//			System.out.println(joinable_attribute_lists.toString());
//			
//			System.out.println(relation_array.toString());
			
			Vector<string_array> attr_array = joinable_attribute_lists.get(relation_array);
			
			for(int k = 0; k<attr_array.size(); k++)
			{
				global_predicates.add(new Conditions(new Argument(attr_array.get(k).array[0], foreign_key_pair[0]), foreign_key_pair[0], new op_equal(), new Argument(attr_array.get(k).array[1], foreign_key_pair[1]), foreign_key_pair[1]));
			}
			
			
		}
		
		return global_predicates;
	}
	
	static Vector<String> get_all_attrs(String relation, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT column_name"
				+ " FROM information_schema.columns "
				+ " WHERE table_name = '" + relation + "' ";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> attr_names = new Vector<String>();
		
		while(rs.next())
		{
			attr_names.add(rs.getString(1));
		}
		
		return attr_names;
	}
	
	static Vector<Argument> gen_default_head_vars(String relation_name, Vector<String> attr_list, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Vector<Argument> head_vars = new Vector<Argument>();
		
		Vector<String> attr_names = get_all_attributes(relation_name, c, pst);
		
		for(int i = 0; i<attr_names.size(); i++)
		{
			Argument l = new Argument(relation_name + populate_db.separator + attr_names.get(i), relation_name);
			
			head_vars.add(l);
		}
		
		return head_vars;
	}
	
	static Vector<Argument> gen_head_vars(String relation_name, Vector<String> attr_list, int size, Connection c, PreparedStatement pst)
	{
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		Vector<Argument> head_vars = new Vector<Argument>();
		
		Random r = new Random();
		
		for(int i = 0; i<size; i++)
		{
			int index = 0;
			if(i == 0)
			{
				index = 0;
				
				id_set.add(index);			
			}
			else
			{
				index = r.nextInt(attr_list.size());
				
				if(index != 0)
					id_set.add(index);			
			}
			
		}
		
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument l = new Argument(relation_name + populate_db.separator + attr_list.get(index), relation_name);
			
			head_vars.add(l);
			
		}
		
		return head_vars;
	}
	
	static Vector<Argument> gen_all_head_vars(String relation_name, Vector<String> attr_list, Connection c, PreparedStatement pst)
	{		
		Vector<Argument> head_vars = new Vector<Argument>();

		for(int i = 0; i<attr_list.size(); i++)
		{			
			Argument l = new Argument(relation_name + populate_db.separator + attr_list.get(i), relation_name);
			
			head_vars.add(l);
			
		}
		
		return head_vars;
	}
	
	static Vector<Lambda_term> gen_lambda_terms(Vector<Argument> head_vars, String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Random rand = new Random();
		
		int size = rand.nextInt(head_vars.size()) + 1;
		
		
		
		HashSet<Integer> int_list = new HashSet<Integer>();
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<size; i++)
		{
			int index = rand.nextInt(head_vars.size());
			
			Argument arg = head_vars.get(index);
			
			if(!check_null_value(relation_name, arg.name, c, pst))
			{
				int_list.add(index);
			}
			
		}
		
		if(int_list.isEmpty())
		{
			String [] primary_key_type = get_primary_key(relation_name, c, pst);
			
			int i = 0;
			
			for(i = 0; i<head_vars.size(); i++)
			{
				Argument arg = head_vars.get(i);
				
				if(arg.relation_name.equals(relation_name) && arg.name.equals(relation_name + populate_db.separator + primary_key_type[0]))
				{
					break;
				}
				
			}
			
			if(i >= head_vars.size())
				head_vars.add(new Argument(relation_name + populate_db.separator + primary_key_type[0], relation_name));
			
			l_terms.add(new Lambda_term(relation_name + populate_db.separator + primary_key_type[0], relation_name));
			
		}
		else
		{
			for(Iterator iter = int_list.iterator(); iter.hasNext();)
			{
				Integer index = (Integer) iter.next();
				
				Argument arg = head_vars.get(index);
				
				if(arg.name.equals("introduction" + populate_db.separator +"text"))
				{
					if(int_list.size() == 1)
					{
						String [] primary_key_type = get_primary_key(relation_name, c, pst);
						
						int i = 0;
						
						for(i = 0; i<head_vars.size(); i++)
						{
							Argument a = head_vars.get(i);
							
							if(a.relation_name.equals(relation_name) && a.name.equals(relation_name + populate_db.separator + primary_key_type[0]))
							{
								break;
							}
							
						}
						
						if(i >= head_vars.size())
							head_vars.add(new Argument(relation_name + populate_db.separator + primary_key_type[0], relation_name));
						
						l_terms.add(new Lambda_term(relation_name + populate_db.separator + primary_key_type[0], relation_name));
					}
					else
						continue;
				}
				else
					l_terms.add(new Lambda_term(arg.name, arg.relation_name));
			}
		}
		
		
		
		return l_terms;
		
	}
	
	static Vector<Lambda_term> gen_default_lambda_terms(String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		
		String [] primary_key_type = get_primary_key(relation_name, c, pst);
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();

		l_terms.add(new Lambda_term(relation_name + populate_db.separator + primary_key_type[0], relation_name));
		
		return l_terms;
		
	}
	
	static boolean check_null_value(String relation_name, String arg_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String attr_name = arg_name.substring(arg_name.indexOf(populate_db.separator) + 1, arg_name.length());
		
//		String query = "SELECT exists (SELECT 1 FROM " + relation_name + " WHERE " + attr_name + " = '' or " + attr_name + " is null LIMIT 1)";
		
		String query = "SELECT exists (SELECT 1 FROM " + relation_name + " WHERE " + attr_name + " is null LIMIT 1)";
		
//		System.out.println(query);
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			boolean b = rs.getBoolean(1);
			
			if(b)
				return true;
			
			else
				return false;
		}
		
		return false;
		
		
		
		
		
	}
	
	static String [] get_primary_key(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT"
				+ " c.column_name, c.data_type"
				+ " FROM information_schema.table_constraints tc"
				+ " JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name)"
				+ " JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema AND tc.table_name = c.table_name AND ccu.column_name = c.column_name"
				+ " where constraint_type = 'PRIMARY KEY' and tc.table_name = '"+ table_name  +"'";
		
		pst =c.prepareStatement(query);
		
		String [] results = new String [2];
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			results[0] = rs.getString(1);
			
			results[1] = rs.getString(2);
		}
		
		return results;
		
	}
	
	static String get_attr_type(String table_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select data_type from information_schema.columns "
				+ "where table_name = '"+ table_name +"' and column_name = '" + attr_name +"'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			return rs.getString(1);
		}
		
		return null;
		
	}
	
	static Vector<Conditions> gen_local_predicates(int selection_size, HashMap<String, String> attr_types, Vector<String> attr_list, String relation_name, String [] primary_key_type, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		
		Random r = new Random();
		
		
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		
		if(comparable_data_type_vec.size() == 0)
			comparable_data_type_vec.addAll(Arrays.asList(comparable_data_type));
		
		
		double value = r.nextDouble();
		
		if(value < local_predicates_rate)
		{			
			String attr_name = primary_key_type[0];
			
			String type = primary_key_type[1];
			
			if(comparable_data_type_vec.contains(type))
			{
				Conditions insert_condition = do_gen_condition_comparable(relation_name, attr_name, c, pst, q);
				
				if(insert_condition != null)
					conditions.add(insert_condition);
			}
			else
			{
				Conditions insert_condition = do_gen_condition_uncomparable(relation_name, attr_name, c, pst, q);
				
				if(insert_condition != null)
					conditions.add(insert_condition);
			}
		}
		
		return conditions;
	}
	
	static Vector<String> get_all_attributes(String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
//		String query = "SELECT column_name "
//				+ " FROM information_schema.columns "
//				+ " WHERE table_name='" + relation_name + "'";
		
		String query = "SELECT Col.Column_Name from "
				+ "    INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab,"
				+ "    INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col "
				+ " WHERE    Col.Constraint_Name = Tab.Constraint_Name "
				+ "   AND Col.Table_Name = Tab.Table_Name"
				+ "    AND Constraint_Type = 'PRIMARY KEY'     "
				+ "AND Col.Table_Name = '"+ relation_name +"'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> attr_names = new Vector<String>();
		
		while(rs.next())
		{
			attr_names.add(rs.getString(1));
		}
		
		return attr_names;
	}
	
	public static boolean gen_one_local_predicate(Query view, Connection c, PreparedStatement pst, Query query) throws SQLException
	{		
		
		Random r = new Random();

		int index = r.nextInt(view.body.size());
		
		Subgoal subgoal = (Subgoal) view.body.get(index);
		
		String [] primary_keys = get_primary_key(subgoal.name, c, pst);
		
		HashMap<String, int[]> selected_id = new HashMap<String, int[]>();
		
		HashMap<String, Integer> indexes = new HashMap<String, Integer>();
		
		inputquery_conditions(selected_id, indexes);
		
		int [] ids = selected_id.get(subgoal.name);
		
		int value_id = r.nextInt(ids.length - 2) + 1;
		
		int order = r.nextInt();
		
		Operation op = null;
		
		if(order % 2 == 0)
		{
			op = new op_greater();
		}
		else
		{
			op = new op_less_equal();
		}
		
		Argument arg1 = new Argument(primary_keys[0], subgoal.name);
		
		Argument arg2 = new Argument("'" + ids[value_id] + "'");
		
		Conditions condition = new Conditions(arg1, subgoal.name, op, arg2, new String());
		
		view.conditions.add(condition);
		
//		if(comparable_data_type_vec.size() == 0)
//			comparable_data_type_vec.addAll(Arrays.asList(comparable_data_type));
//
//		Vector<String []> available_attr = new Vector<String[]>();
//		
//		for(int i = 0; i<view.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal)view.body.get(i);
//			
//			Vector<String> attr_names = get_all_attributes(view.subgoal_name_mapping.get(subgoal.name), c, pst);
//			
//			for(int j = 0; j<attr_names.size(); j++)
//			{
//				int k = 0;
//								
//				for(k = 0; k<view.conditions.size(); k++)
//				{
//					Conditions condition = view.conditions.get(k);
//					
//					if(condition.subgoal1.equals(subgoal.name) && condition.arg1.name.equals(attr_names.get(j)))
//					{
//						break;
//					}
//					
//					if(condition.subgoal2.equals(subgoal.name) && condition.arg2.name.equals(attr_names.get(j)))
//					{
//						break;
//					}
//				}
//				
//				if(k >= view.conditions.size())
//				{
//					String [] table_attr = {subgoal.name, attr_names.get(j)};
//					
//					available_attr.add(table_attr);
//				}
//			}
//		}
//		
//		if(available_attr.size() == 0)
//			return false;
//		
//		else
//		{
//			Random r = new Random();
//
//			int index = r.nextInt(available_attr.size());
//			
//			String [] table_attr = available_attr.get(index);
//			
//			String type = get_attr_type(view.subgoal_name_mapping.get(table_attr[0]), table_attr[1], c, pst);
//			
//			if(comparable_data_type_vec.contains(type))
//			{
//				Conditions insert_condition = do_gen_condition_comparable(table_attr[0], table_attr[1], c, pst, query);
//				
//				if(insert_condition != null)
//				{
//					view.conditions.add(insert_condition);
//				}
//				else
//					return false;
//			}
//			else
//			{
//				Conditions insert_condition = do_gen_condition_uncomparable(table_attr[0], table_attr[1], c, pst, query);
//				
//				if(insert_condition != null)
//				{
//					view.conditions.add(insert_condition);
//				}
//				else
//					return false;
//			}
//		}
		
		
		
		return true;
	}
	
	
	static Vector<String> get_all_values(String relation_name, String attr_name, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		
		Conditions selected_condition = null;
		
		for(int i = 0; i<q.conditions.size(); i++)
		{
			Conditions condition = q.conditions.get(i);
			
			if(condition.arg2.isConst() && q.subgoal_name_mapping.get(condition.subgoal1).equals(relation_name))
			{
				selected_condition = new Conditions(condition.arg1, q.subgoal_name_mapping.get(condition.subgoal1), condition.op, condition.arg2, condition.subgoal2);
				
				break;
			}
		}
		

		
		Vector<String> all_values = new Vector<String>();
		
		String query = "select distinct " + attr_name + " from " + relation_name + " where " + attr_name + " IS NOT NULL ";
		
		if(selected_condition != null)
		{
			query += " and " + Query_converter.get_single_condition_str(selected_condition);
		}
		
		pst = c.prepareStatement(query);
		
				
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			
			String value = rs.getString(1);
			
			if(value != null && !value.isEmpty())
				all_values.add(rs.getString(1));
		}
		
		return all_values;
	}
	
	static Conditions do_gen_condition_comparable(String relation_name, String attr_name, Connection c, PreparedStatement pst, Query query) throws SQLException
	{
		Random r = new Random();
		
		int op_id = r.nextInt(6);
		
		Operation op = null;
		
		switch(op_id)
		{
		case 0:
		{
			op = (Operation) new op_equal();
			
			break;			
		}
		case 1:
		{
			op = (Operation) new op_not_equal();
			
			break;
		}
		case 2:
		{
			op = (Operation) new op_less();
			
			break;
		}
		case 3:
		{
			op = (Operation) new op_less_equal();
			
			break;
		}
		case 4:
		{
			op = (Operation) new op_greater();
			
			break;
		}
		case 5:
		{
			op = (Operation) new op_greater_equal();
			
			break;
		}
		default: break;
		}
		
		Vector<String> all_values = get_all_values(relation_name, attr_name, c, pst, query);
		
		if(all_values.size() == 0)
			return null;
		
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
		if(all_values.get(index).length() < 20)	
			return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + r.nextInt(const_range) + "'"), subgoal2);
		else
			return null;

	}
	
	static Conditions do_gen_condition_uncomparable(String relation_name, String attr_name, Connection c, PreparedStatement pst, Query q) throws SQLException
	{
		Random r = new Random();
		
		Operation op = new op_equal();
		
		Vector<String> all_values = get_all_values(relation_name, attr_name, c, pst, q);
		
		if(all_values.size() == 0)
			return null;
				
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
		if(all_values.get(index).length() < 20)
			return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);
		else
			return null;

	}
	
	static HashMap<String, String> get_attr_types(String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> attr_names = new Vector<String>();
		
		String query_attr_name = "SELECT column_name, data_type "
				+ "FROM information_schema.columns "
				+ "WHERE table_name = '"+ relation_name + "' "
				+ "ORDER BY ordinal_position";
		pst = c.prepareStatement(query_attr_name);
		
		ResultSet rs = pst.executeQuery();
		
		HashMap<String, String> attr_type_mapping = new HashMap<String, String>();
		
		while(rs.next())
		{
			String attr_name = rs.getString(1);
			
			String type = rs.getString(2);
			
			if(available_data_type_vec.size() == 0)
			{
				available_data_type_vec.addAll(Arrays.asList(available_data_type));
			}
			
			if(available_data_type_vec.contains(type))
			{
				attr_type_mapping.put(attr_name, type);
			}
		}
		
		return attr_type_mapping;
	}
	
	static Vector<String> get_attrs(String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> attr_names = new Vector<String>();
		
		String query_attr_name = "SELECT column_name "
				+ "FROM information_schema.columns "
				+ "WHERE table_name = '"+ relation_name + "' "
				+ "ORDER BY ordinal_position";
		pst = c.prepareStatement(query_attr_name);
		
		ResultSet rs = pst.executeQuery();
		
		HashMap<String, String> attr_type_mapping = new HashMap<String, String>();
		
		while(rs.next())
		{
			attr_names.add(rs.getString(1));
		}
		
		return attr_names;
	}
	
	private static class string_array
	{
		public String [] array;
		
		
		public string_array(String [] array) {
			// TODO Auto-generated constructor stub
			this.array = array;
		}
		
		@Override
		public String toString()
		{
			
			String str = new String();
			
			for(int i = 0; i<array.length; i++)
			{
				if(i >= 1)
					str += ",";
				
				str += array[i];
			}
			
			return str;
		}
		
		@Override
		public int hashCode()
		{
			return this.toString().hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			string_array ta = (string_array) obj;
			
			if(ta.hashCode() == this.hashCode())
			{
				return true;
			}
			
			return false;
			
		}
	}
	
	

}
