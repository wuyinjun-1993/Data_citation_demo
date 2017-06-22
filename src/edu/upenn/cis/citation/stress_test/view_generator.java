package edu.upenn.cis.citation.stress_test;

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

import java.util.Random;
import java.util.Set;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
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

public class view_generator {
	
	
	
	public static String [] citatable_tables = {"gpcr","object", "ligand", "family", "introduction"};

	static HashMap<string_array, Vector<string_array> > joinable_attribute_lists = new HashMap<string_array, Vector<string_array>>();
	
	static HashMap<String, Vector<String> > foreign_key_relations = new HashMap<String, Vector<String> >();
	
	static String [] available_data_type = {"integer", "text", "boolean", "character varying", "double precision", "real", "character varying", "bigint"};
	
	static Vector<String> available_data_type_vec = new Vector<String>();
	
	static Vector<String> comparable_data_type_vec = new Vector<String>();
	
	static String []comparable_data_type = {"integer", "double precision", "real", "bigint"};
	
	static double citatble_rate = 0.3;
	
	static double local_predicates_rate = 0.5;
	
	static double lambda2head_rate = 0.3;
	
	static double head_var_rate = 0.3;
	
	static double global_predicate_rate = 1;
	
	static int view_nums = 100;
	
	static HashMap<String, Vector<String>> query_table_names = new HashMap<String, Vector<String>>();
	
	static HashMap<String, Vector<Argument>> query_head_names = new HashMap<String, Vector<Argument>>();
	
	static HashMap<String, Vector<Conditions>> query_conditions = new HashMap<String, Vector<Conditions>>();
	
	static void initial()
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
		
		head_variables.add(new Argument("contributor_first_names", "contributor"));
		
		head_variables.add(new Argument("contributor_surname", "contributor"));
		
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
		
		head_variables.add(new Argument("contributor_first_names", "contributor"));
		
		head_variables.add(new Argument("contributor_surname", "contributor"));
		
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
		
		head_variables.add(new Argument("contributor_first_names", "contributor"));
		
		head_variables.add(new Argument("contributor_surname", "contributor"));
		
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
		
		head_variables.add(new Argument("contributor_first_names", "contributor"));
		
		head_variables.add(new Argument("contributor_surname", "contributor"));
		
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
		
		head_variables.add(new Argument("contributor2ligand_first_names", "contributor2ligand"));
		
		head_variables.add(new Argument("contributor2ligand_surname", "contributor2ligand"));
		
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
	    
	    clear_views(c, pst);
	    
	    clear_other_tables(c, pst);
	    
	    get_joinable_relations(c, pst);
	    
	    HashSet<Query> views = gen_views(c, pst);
	    
	    store_views(views, c, pst);
	    
	    
	}
	
	
	static void clear_views(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Integer> ids = get_all_view_ids(c, pst);
		
		for(int i = 0; i<ids.size(); i++)
		{
			view_operation.delete_view_by_id(ids.get(i));
		}
		
		
	}
	
	static void clear_other_tables(Connection c, PreparedStatement pst) throws SQLException
	{
		String [] tables = {"citation2query", "citation2view", "citation_table", "query2conditions", "query2lambda_term", "query2subgoal", "query2head_variables"};
		
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
	
	static void store_views(HashSet<Query> views, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		int num = 1;
		
		for(Iterator iter = views.iterator(); iter.hasNext();)
		{
			
			Query view = (Query) iter.next();
			
			String name = view.name;
			
			view_operation.add(view, view.name);
			
			citation_view_operation.add_citation_view("c" + num);
			
			citation_view_operation.add_connection_view_with_citations("c" + num, name);
			
			store_citation_queries(view, num, c, pst);
			
			Query_operation.add_connection_citation_with_query("c" + num, "q" + num , "author");
			
			num ++;

		}
		
	}
	
	static void store_citation_queries(Query view, int num, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Random r = new Random();
		
		int index = r.nextInt(view.body.size());
		
		
		
		Subgoal subgoal = (Subgoal)view.body.get(index);
		
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
		
		Query_operation.add(q, q.name);
		
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
			double val = r.nextGaussian() * 5 + 1;
			
			if(val < 0)
				continue;
			
			int value = (int) Math.round(val);
			
			if(value == 0)
				continue;
			
			random_number.add(value);
			
			i++;
		}
		return random_number;
	}
	
	static HashSet<Query> gen_views(Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Integer> sizes = generator_random_numbers(view_nums);
		
		HashSet<Query> queries = new HashSet<Query>();
		
		int num = 0;
		
		while(queries.size() < sizes.size())
		{
			
			int size = sizes.get(num);
						
			Query query = generate_view(num, sizes.get(num), c, pst);
						
			if(!queries.contains(query))
			{
				queries.add(query);
				
		    	System.out.println(query.lambda_term + "," + query.toString());
				
				num ++;
			}
			
			
		}
		
		return queries;
	}
	
	static Query generate_view(int id, int size, Connection c, PreparedStatement pst) throws SQLException
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
			
			Vector<Conditions> conditions = gen_local_predicates(selection_size, attr_types, attr_list, relation, primary_key_type, c, pst);
					
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
	
	static Vector<Argument> gen_head_vars(String relation_name, Vector<String> attr_list, int size, Connection c, PreparedStatement pst)
	{
		
		HashSet<Integer> id_set = new HashSet<Integer>();
		
		Vector<Argument> head_vars = new Vector<Argument>();
		
		Random r = new Random();
		
		for(int i = 0; i<size; i++)
		{
			int index = r.nextInt(attr_list.size());
			
			
			id_set.add(index);
			
			
				
			
		}
		
		for(Iterator iter = id_set.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument l = new Argument(relation_name + "_" + attr_list.get(index), relation_name);
			
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
				
				if(arg.relation_name.equals(relation_name) && arg.name.equals(relation_name + "_" + primary_key_type[0]))
				{
					break;
				}
				
			}
			
			if(i >= head_vars.size())
				head_vars.add(new Argument(relation_name + "_" + primary_key_type[0], relation_name));
			
			l_terms.add(new Lambda_term(relation_name + "_" + primary_key_type[0], relation_name));
			
		}
		else
		{
			for(Iterator iter = int_list.iterator(); iter.hasNext();)
			{
				Integer index = (Integer) iter.next();
				
				Argument arg = head_vars.get(index);
				
				l_terms.add(new Lambda_term(arg.name, arg.relation_name));
			}
		}
		
		
		
		return l_terms;
		
	}
	
	static boolean check_null_value(String relation_name, String arg_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String attr_name = arg_name.substring(arg_name.indexOf("_") + 1, arg_name.length());
		
//		String query = "SELECT exists (SELECT 1 FROM " + relation_name + " WHERE " + attr_name + " = '' or " + attr_name + " is null LIMIT 1)";
		
		String query = "SELECT exists (SELECT 1 FROM " + relation_name + " WHERE " + attr_name + " is null LIMIT 1)";
		
		System.out.println(query);
		
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
	
	static Vector<Conditions> gen_local_predicates(int selection_size, HashMap<String, String> attr_types, Vector<String> attr_list, String relation_name, String [] primary_key_type, Connection c, PreparedStatement pst) throws SQLException
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
				Conditions insert_condition = do_gen_condition_comparable(relation_name, attr_name, c, pst);
				
				if(insert_condition != null)
					conditions.add(insert_condition);
			}
			else
			{
				Conditions insert_condition = do_gen_condition_uncomparable(relation_name, attr_name, c, pst);
				
				if(insert_condition != null)
					conditions.add(insert_condition);
			}
		}
		
		return conditions;
	}
	
	
	static Vector<String> get_all_values(String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> all_values = new Vector<String>();
		
		String query = "select distinct " + attr_name + " from " + relation_name + " where " + attr_name + " IS NOT NULL " + " order by " + attr_name;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			all_values.add(rs.getString(1));
		}
		
		return all_values;
	}
	
	static Conditions do_gen_condition_comparable(String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
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
		
		Vector<String> all_values = get_all_values(relation_name, attr_name, c, pst);
		
		if(all_values.size() == 0)
			return null;
		
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
		if(all_values.get(index).length() < 100)	
			return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);
		else
			return null;

	}
	
	static Conditions do_gen_condition_uncomparable(String relation_name, String attr_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Random r = new Random();
		
		Operation op = new op_equal();
		
		Vector<String> all_values = get_all_values(relation_name, attr_name, c, pst);
		
		if(all_values.size() == 0)
			return null;
				
		int index = r.nextInt(all_values.size());
		
		String subgoal2 = new String();
		
		if(all_values.get(index).length() < 100)
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
