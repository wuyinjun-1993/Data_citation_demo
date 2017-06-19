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
import edu.upenn.cis.citation.Pre_processing.populate_db;

public class view_generator {
	
	
	
	public static String [] citatable_tables = {"gpcr","object","interaction","ligand","pathophysiology","interaction_affinity_refs","gtip_process","process_assoc","disease", "family", "introduction"};

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
	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
	    get_joinable_relations(c, pst);
	    
	    HashSet<Query> views = gen_views(c, pst);
	    
	    for(Iterator iter = views.iterator(); iter.hasNext();)
	    {
	    	Query q = (Query) iter.next();
	    	
	    }
	    
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
			
			Vector<Conditions> conditions = gen_conditions(selection_size, attr_types, attr_list, relation, c, pst);
					
			local_predicates.addAll(conditions);
			
			int head_size = rand.nextInt((int)(attr_list.size() * head_var_rate + 1)) + 1;
									
			Vector<Argument> head_vars = gen_head_vars(relation, attr_list, head_size, c, pst);
			
			heads.addAll(head_vars);
			
			Vector<Lambda_term> l_terms = gen_lambda_terms(head_vars, relation);
			
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
			
			Argument l = new Argument(attr_list.get(index), relation_name);
			
			head_vars.add(l);
			
		}
		
		return head_vars;
	}
	
	static Vector<Lambda_term> gen_lambda_terms(Vector<Argument> head_vars, String relation_name)
	{
		
		Random rand = new Random();
		
		int size = rand.nextInt(head_vars.size() + 1);
		
		
		
		HashSet<Integer> int_list = new HashSet<Integer>();
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<size; i++)
		{
			int index = rand.nextInt(head_vars.size());
			
			int_list.add(index);
		}
		
		for(Iterator iter = int_list.iterator(); iter.hasNext();)
		{
			Integer index = (Integer) iter.next();
			
			Argument arg = head_vars.get(index);
			
			l_terms.add(new Lambda_term(arg.relation_name + "_" + arg.name, arg.relation_name));
		}
		
		return l_terms;
		
	}
	
	static Vector<Conditions> gen_conditions(int selection_size, HashMap<String, String> attr_types, Vector<String> attr_list, String relation_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		Random r = new Random();
		
		
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		
		if(comparable_data_type_vec.size() == 0)
			comparable_data_type_vec.addAll(Arrays.asList(comparable_data_type));
		
		for(int i = 0; i<selection_size; i++)
		{
			int index = r.nextInt(attr_list.size());
			
			String attr_name = attr_list.get(index);
			
			String type = attr_types.get(attr_name);
			
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
		
		return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);

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
		
		return new Conditions(new Argument(attr_name, relation_name) , relation_name, op, new Argument("'" + all_values.get(index) + "'"), subgoal2);

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
