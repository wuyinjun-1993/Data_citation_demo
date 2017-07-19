package edu.upenn.cis.citation.Pre_processing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

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
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;

public class populate_db {
	
	//psql --host=datacitation.cn7s3bpawoj2.us-east-1.rds.amazonaws.com --port=5432 --username=postgres --password
	//psql --host=citedb.cx9xmwhyomib.us-east-1.rds.amazonaws.com --port=5432 --username=postgres --dbname=postgres
	
	public static String separator = "|";

//	public static String[] base_relations = {"gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c", "family_c", "introduction_c"};

//	public static String[] base_tables = {"gpcr","object","interaction","ligand","pathophysiology","interaction_affinity_refs","gtip_process","process_assoc","disease", "family", "introduction"};

	public static String db_url = "jdbc:postgresql://localhost:5432/test";

//	public static String db_url = "jdbc:postgresql://citedb.cx9xmwhyomib.us-east-1.rds.amazonaws.com:5432/iuphar";
	public static String []default_table_names = {"view_table", "view2subgoals", "view2lambda_term", "view2conditions", "citation_table", "citation2view", "citation2query", "query2head_variables", "query2conditions", "query2head_variables", "query2subgoal", "user_query_table", "user_query2conditions", "user_query2subgoals", "user_query_conditions"};
//	public static String db_url = "jdbc:postgresql://citedb.cx9xmwhyomib.us-east-1.rds.amazonaws.com:5432/iuphar";
	


	public static String usr_name = "postgres";
	
	public static String passwd = "12345678";
	
	public static String suffix = "_c";
	
	public static String star_op = " union ";	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		
		
//	      initial();
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(db_url, usr_name , passwd);
//		
//		
//		delete_view_single_table("v2", "family_c", c, pst);
		
//	    delete_views(c, pst);
	    
		
		initial();
		
		
	}
	
	public static HashMap<String, String> get_primary_key_type(String table_name, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
	    
//	    Vector<String> table_names = get_table_list(c, pst);
	    
//	    for(int i = 0; i<table_names.size(); i++)
//	    {
	    	String query = "SELECT a.attname, format_type(a.atttypid, a.atttypmod) AS data_type"
		    		+ " FROM   pg_index i"
		    		+ " JOIN   pg_attribute a ON a.attrelid = i.indrelid"
		    		+ " AND a.attnum = ANY(i.indkey)"
		    		+ " WHERE  i.indrelid = '"+ table_name + "'::regclass"
		    		+ " AND    i.indisprimary";
	    	
//	    	pst = c.prepareStatement(query);
	    	
	    	Statement stmt = c.createStatement(
	    		    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE
	    		);
	    	
	    	ResultSet rs = stmt.executeQuery(query);
	    	
	    	HashMap<String, String> primary_keys = new HashMap<String, String>();
	    	
	    	while(rs.next())
	    	{
	    		primary_keys.put(rs.getString(1), rs.getString(2));
	    	}
	    	
	    	return primary_keys;
	    	
//	    	ResultSet rs = pst.executeQuery();
	    	
//	    	if(!table_names.get(i).endsWith("_c"))
//	    	{
//
//	    		rs.last();
//	    		
//	    		int size = rs.getRow();
//	    		
//	    		rs.beforeFirst();
//	    		
//	    		if(size == 0)
//	    		{
//	    			System.out.println(table_names.get(i));
//	    			
//	    			continue;
//	    		}
//	    		
////	    		System.out.print(size + " ");
////	    		
////	    		System.out.print(table_names.get(i));
////		    	
////		    	System.out.print(":");
////		    	
////		    	while(rs.next())
////		    	{
////		    		System.out.print(" ");
////		    		
////		    		System.out.print(rs.getString(1));
////		    	}
////		    	
////		    	System.out.println();
//	    	}
	    	
	    	
//	    }
	    
	    
	    
	    
	    
	    
	    
	}
	
	public static Vector<String> get_primary_key(String table_name, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
	    
//	    Vector<String> table_names = get_table_list(c, pst);
	    
//	    for(int i = 0; i<table_names.size(); i++)
//	    {
	    	String query = "SELECT a.attname"
		    		+ " FROM   pg_index i"
		    		+ " JOIN   pg_attribute a ON a.attrelid = i.indrelid"
		    		+ " AND a.attnum = ANY(i.indkey)"
		    		+ " WHERE  i.indrelid = '"+ table_name + "'::regclass"
		    		+ " AND    i.indisprimary";
	    	
//	    	pst = c.prepareStatement(query);
	    	
	    	Statement stmt = c.createStatement(
	    		    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE
	    		);
	    	
	    	ResultSet rs = stmt.executeQuery(query);
	    	
	    	Vector<String> primary_keys = new Vector<String>();
	    	
	    	while(rs.next())
	    	{
	    		primary_keys.add(rs.getString(1));
	    	}
	    	
	    	return primary_keys;
	    	
//	    	ResultSet rs = pst.executeQuery();
	    	
//	    	if(!table_names.get(i).endsWith("_c"))
//	    	{
//
//	    		rs.last();
//	    		
//	    		int size = rs.getRow();
//	    		
//	    		rs.beforeFirst();
//	    		
//	    		if(size == 0)
//	    		{
//	    			System.out.println(table_names.get(i));
//	    			
//	    			continue;
//	    		}
//	    		
////	    		System.out.print(size + " ");
////	    		
////	    		System.out.print(table_names.get(i));
////		    	
////		    	System.out.print(":");
////		    	
////		    	while(rs.next())
////		    	{
////		    		System.out.print(" ");
////		    		
////		    		System.out.print(rs.getString(1));
////		    	}
////		    	
////		    	System.out.println();
//	    	}
	    	
	    	
//	    }
	    
	    
	    
	    
	    
	    
	    
	}
	
	
	static Vector<String> get_table_list(Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<String> table_list = new Vector<String>();
		
		String query = "SELECT table_name FROM information_schema.tables WHERE table_schema='public'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		while(rs.next())
		{
			table_list.add(rs.getString(1));
		}
		
		return table_list;
		
	}
	
	static void delete_views(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Vector<Query> views = get_views_schema(c, pst);
		
		for(int i = 0; i<views.size(); i++)
		{
			String query = "drop table " + views.get(i).name + "_table";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
			
			query = "drop view " + views.get(i).name;
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
		
	}
	
	static Vector<String> get_all_relations(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT table_name"
				+ "  FROM information_schema.tables"
				+ " WHERE table_schema='public'"
				+ "   AND table_type='BASE TABLE'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> relations = new Vector<String>();
		
		Vector<String> default_relations = new Vector<String>();
		
		default_relations.addAll(Arrays.asList(default_table_names));
		
		while(rs.next())
		{
			
			String relation = rs.getString(1);
			
			if(!default_relations.contains(relation))
			{
				relations.add(rs.getString(1));
			}
			
		}
		
		return relations;
	}
	
	static boolean check_exist_table(String relation, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT EXISTS ("
				+ "   SELECT 1"
				+ "   FROM   information_schema.tables"
				+ "   WHERE  table_name = '" + relation + "'"
				+ "   )";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			boolean b = rs.getBoolean(1);
			
			return b;
		}
		
		return false;
	}
	
	public static void renew_table(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		Vector<String> relation_names = get_all_relations(c, pst);
		
		for(int i = 0; i<relation_names.size(); i++)
		{
			String query = new String();

			if(relation_names.get(i).endsWith(suffix + suffix))
			{
				query = "drop table " + relation_names.get(i) + " cascade";
				
				pst = c.prepareStatement(query);
				
//				System.out.println(query);
				
				pst.execute();
			}
			
			if(relation_names.get(i).endsWith(suffix))
				continue;
			
			if(check_exist_table(relation_names.get(i) + suffix, c, pst))
			{
				query = "drop table " + relation_names.get(i) + suffix + " cascade";
				
				pst = c.prepareStatement(query);
				
//				System.out.println(query);
				
				pst.execute();
			}
			
			
			
			HashMap<String, String> primary_keys = get_primary_key_type(relation_names.get(i), c, pst);
			
			if(primary_keys.isEmpty())
				continue;
			
			Set<String> keys = primary_keys.keySet();
			
			query = "create table " + relation_names.get(i) + suffix + "(";
			
			int num = 0;
			
			String primary_key_string = new String();
			
			for(Iterator iter = keys.iterator();iter.hasNext();)
			{				
				String key = (String) iter.next();
				
				query += key + " " + primary_keys.get(key) + ",";
				
				if(num >= 1)
					primary_key_string += ",";
				
				primary_key_string += key;
				
				num ++;
				
			}
			
			query += "citation_view text, ";
			
			query += "primary key (" + primary_key_string + "), foreign key (" + primary_key_string + ") references " + relation_names.get(i) + "(" + primary_key_string + ") on update cascade )";
			
//			System.out.println(query);
			
			pst = c.prepareStatement(query);
			
			pst.execute();
			
			initial_new_table(relation_names.get(i), c, pst, primary_key_string);
			
			
		}
	}
	
	static void initial_new_table(String table_name, Connection c, PreparedStatement pst, String primary_key_string) throws SQLException
	{
		
		String query = "insert into " + table_name + suffix + "(" + primary_key_string + ") select " + primary_key_string + " from " + table_name;
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	public static void initial() throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(db_url, usr_name , passwd);
//		add_column(c,pst);
	    renew_table(c, pst);
	    
		populate_db(c, pst);
		c.close();
	}
	
	public static void update(Query view) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(db_url, usr_name , passwd);
//	    add_column(c,pst);
//		renew_table(c, pst);

		populate_db(view, c, pst);
		c.close();
	    
		
	}
	
	public static void delete(String id, Vector<Subgoal> subgoals, HashMap<String, String> subgoal_name_mapping, boolean has_lambda) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(db_url, usr_name , passwd);
//	    add_column(c,pst);
	    
	    for(int i = 0; i<subgoals.size(); i++)
	    {
	    	delete_view_single_table(id, subgoal_name_mapping.get(subgoals.get(i).name), has_lambda, c, pst);
	    }
	    
		c.close();
	}
	
	static void delete_view_single_table(String id, String relation, boolean has_lambda, Connection c, PreparedStatement pst) throws SQLException
	{
//		String query = "select * from " + relation + " where citation_view like '%" + id + "%'";
//		
//		pst = c.prepareStatement(query);
//		
//		ResultSet rs = pst.executeQuery();
//		
//		ResultSetMetaData rs_meta = rs.getMetaData();
//		
//		while(rs.next())
		{			
//			String old_citation_view = rs.getString("citation_view");
//			
//			String [] old_citation_views = old_citation_view.split("\\" + separator);
//			
//			
//			
//			int num = 0;
//			
//			for(int j = 0; j<old_citation_views.length; j++)
//			{
//				if(!old_citation_views[j].contains(id))
//				{
//					if(num >= 1)
//						new_citation_view += separator;
//					
//					new_citation_view += old_citation_views[j];
//					
//					num ++;
//				}
//			}
//			
			String new_citation_view = new String();
			
			if(has_lambda)
			{
				new_citation_view = id + "(\\((.*?)\\))";
			}
			else
			{
				new_citation_view = id;
			}
			
			String update_q = "update " + relation + populate_db.suffix + " set citation_view = regexp_replace(citation_view, '"+ new_citation_view +"','')";
			
//			System.out.println(update_q);
			
			pst = c.prepareStatement(update_q);
			
			pst.execute();
			
			update_q = "update " + relation + populate_db.suffix + " set citation_view = regexp_replace(citation_view, '\\|\\|','|')";
			
//			System.out.println(update_q);
			
			pst = c.prepareStatement(update_q);
			
			pst.execute();
			
			update_q = "update " + relation + populate_db.suffix + " set citation_view = regexp_replace(citation_view, '^\\|','')";
			
//			System.out.println(update_q);
			
			pst = c.prepareStatement(update_q);
			
			pst.execute();
			
			update_q = "update " + relation + populate_db.suffix + " set citation_view = regexp_replace(citation_view, '\\|$','')";
			
//			System.out.println(update_q);
			
			pst = c.prepareStatement(update_q);
			
			pst.execute();
			
//			String where = new String();
//			
//			for(int i = 0; i< col_num; i++)
//			{
//				String col_name = rs_meta.getColumnLabel(i + 1);
//				
//				String value = rs.getString(col_name);
//				
//				if(value == null || value.isEmpty())
//					continue;
//				
//				if(value.contains("."))
//				{
//					try{
//						float f = Float.valueOf(value);
//						continue;
//					}
//					catch(NumberFormatException e)
//					{
//						
//					}
//				}				
//				
//				
//				
//
//				
//				if(value.contains("'"))
//				{
//					value = value.replaceAll("'", "''");
//				}
//				
//				if(i >= 1)
//					where += " and ";
//				
//				where += col_name + "= '" + value + "'"; 
//			}
//			
//			update_q += where;
			
//			rs_meta.getColumnName(column)
//			
//			String update_q = "update " + relation + " set citation_view = '" + 
//			
		}
		
	}
	
	
	public static HashSet<String> get_table_name(Vector<Query> views)
	{
		HashSet<String> subgoals = new HashSet<String>();
		for(int i = 0; i<views.size(); i++)
		{
			Query view =views.get(i);
			for(int j = 0; j <view.body.size(); j++)
			{
				Subgoal subgoal = (Subgoal) view.body.get(j);
				subgoals.add(subgoal.name);
			}
		}
		
		return subgoals;
	}
	
	
	static void initialize(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String inialization_q = "update " + table_name + " set web_view_vec = " + "'t'";
		
		pst = c.prepareStatement(inialization_q);
		
		pst.execute();
	}
	
	public static void add_column_view(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
	    String test_col = "SELECT table_name FROM information_schema.columns WHERE table_name='"+view.name + "_table'";
	    
	    pst = c.prepareStatement(test_col);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	if(rs.getRow()!=0)
	    	{
	    		String update_table = "drop table " + view.name + "_table";
	    		
	    	    pst = c.prepareStatement(update_table);

	    	    pst.execute();
	    	}
	    }
		
		String create_view_table_sql = "create table "+ view.name + "_table as " + "select * from " + view.name;
		
	    pst = c.prepareStatement(create_view_table_sql);
	    
	    pst.execute();
	    
	    String add_column_sql = "alter table " + view.name + "_table add column citation_view text";
	    
	    pst = c.prepareStatement(add_column_sql);
	    
	    pst.execute();
	    
	}

	public static void populate_db(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
	 	Vector<Query> views = get_views_schema(c, pst);
	 	
 		HashMap<String, String> table_name_view = new HashMap<String, String>();

//		String get_tables = "select renamed_view, subgoal from web_view_table";
		
//		pst = c.prepareStatement(get_tables);
		
//		ResultSet r = pst.executeQuery();
		
//		while(r.next())
//		{
//			table_name_view.put(r.getString(2), r.getString(1));
//		}
		
		for(int i = 0; i<views.size();i++)
		{
			Query view = views.get(i);
			
//			Vector<Boolean> web_view = new Vector<Boolean> ();
			
			
			
//			Vector<citation_view> citation_view = get_citation_view(view);
			
			
			gen_citation_view_query(view, table_name_view, c, pst);
		}
	      
	    
	}
	
	public static void populate_db(Query view, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
//	 	Vector<Query> views = get_views_schema(c, pst);
//	 	
 		HashMap<String, String> table_name_view = new HashMap<String, String>();
//
//		String get_tables = "select renamed_view, subgoal from web_view_table";
//		
//		pst = c.prepareStatement(get_tables);
//		
//		ResultSet r = pst.executeQuery();
//		
//		while(r.next())
//		{
//			table_name_view.put(r.getString(2), r.getString(1));
//		}
//		
//		for(int i = 0; i<views.size();i++)
//		{
//			Query view = views.get(i);
//			
//			Vector<Boolean> web_view = new Vector<Boolean> ();
//			
//			
			
//			Vector<citation_view> citation_view = get_citation_view(view);
			
			
			gen_citation_view_query(view, table_name_view, c, pst);
//		}
	      
	    
	}
	
	public static Vector<Query> get_views_schema(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String query= "select * from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Query> views = new Vector<Query>();
		
		while(rs.next())
		{			
			Integer view_id = rs.getInt(1);
			
			Query v = view_operation.get_view_by_id(view_id, c, pst);
			
//			String q_subgoals = "select subgoal_names from view2subgoals where view = '" + view_name + "'";
//			
//			String head_vars = rs.getString(2);
//			
//			
//			
//			pst = c.prepareStatement(q_subgoals);
//			
//			ResultSet r = pst.executeQuery();
//			
//			Vector<String> subgoal_names = new Vector<String>();
//			
//			while(r.next())
//			{
//				subgoal_names.add(r.getString(1));
//			}
//			
//			String subgoal_str = get_subgoal_str(view_name, subgoal_names, c, pst);
//			
//			
//			view = view_name + "(" + head_vars + "):";
//			
//			view += subgoal_str; 
//			
//			
//			String q_conditions = "select conditions from view2conditions where view = '" + view_name + "'";
//			
//			pst = c.prepareStatement(q_conditions);
//			
//			r = pst.executeQuery();
//			
//			Vector<Conditions> conditions = new Vector<Conditions>();
//			
//			String condition_str = new String();
//			
//			while(r.next())
//			{
//				
//				condition_str += "," + r.getString(1);
//				
//				
//				
//
//			}
//			
//			view += condition_str;
//			
//			Query v = Parse_datalog.parse_query(view);
//			
//			v.lambda_term = Gen_citation1.get_lambda_terms(view_name, c, pst);
//			Gen_citation1.check_equality(v);
			
			views.add(v);
			
		}
		
		return views;
				
				
		
		
	}
	
	
	public static String get_subgoal_str(String view_name, Vector<String> subgoal_names, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{				
		String subgoals = new String();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{
			
			Vector<String> col_names = Parse_datalog.get_columns(subgoal_names.get(i));
			
//			String query_table_col = "select arguments from subgoal_arguments where subgoal_names = '" + subgoal_names.get(i) + "'";
//			
//			pst = c.prepareStatement(query_table_col);
//			
//			ResultSet rs = pst.executeQuery();
			
//			if(rs.next())
//			{
				
//				String [] cols = rs.getString(1).split(",");
//				
//				String col_str = new String();
				
				String subgoal_str = subgoal_names.get(i) + "(";
				
				
				for(int j = 0; j<col_names.size(); j++)
				{
					
					if(j >= 1)
						subgoal_str += ",";
					
					subgoal_str += subgoal_names.get(i) + "_" + col_names.get(j);
					
				}
				
				subgoal_str += ")";
				
				if(i >= 1)
					subgoals += ",";
				
				subgoals += subgoal_str;
				
//			}
		}
		
//		String col_str = new String();
//		
//		for(int k = 0; k<col_names.size(); k++)
//		{
//			if(k >= 1)
//				col_str += ",";
//			
//			col_str += col_names.get(k);
//		}
//		
//		String condition_str = new String();
//		
//		for(int k = 0; k<conditions.size(); k++)
//		{
//			if(k >= 1)
//				condition_str += ",";
//			
//			condition_str += conditions.get(k);
//		}
		
		return subgoals;
		
		
		
	}
	
	
	static void check_equality(Query view)
	{
		
		HashMap<String, String> arg_name = new HashMap<String, String>();
		
		HashMap<String, Argument> arg_map = new HashMap<String, Argument>();
		
		Vector<Conditions> conditions = view.conditions;
		
		if(conditions == null)
			conditions = new Vector<Conditions>();
		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			for(int j = 0; j<subgoal.args.size(); j++)
			{
				
				Argument arg = (Argument)subgoal.args.get(j);
				String subgoal_name = arg_name.get(arg.name); 
				
				
				if(subgoal_name == null)
				{
					arg_name.put(arg.name, subgoal.name);
					arg_map.put(arg.name, arg);
				}
				else
				{
					
					Conditions condition = new Conditions(arg, subgoal.name, new op_equal(), arg_map.get(arg.name), subgoal_name);

					Vector<Argument> head_args = view.head.args;
					
					if(head_args.contains(arg))
					{
						head_args.add(new Argument(subgoal.name + "_" + arg.name, arg.origin_name));
					}
					
					arg.name = subgoal.name + "_" + arg.name;
					
					
					conditions.add(condition);
					
				}
			}
			
		}
		
		view.conditions = conditions;
	}
	
	
	static HashMap<String, Vector<Integer>> lambda_subgoal_mapping(Vector<Lambda_term> lambda_terms)
	{
		HashMap<String, Vector<Integer>> subgoal_lambda_term_map = new HashMap<String, Vector<Integer>>();
		
		for(int k = 0; k<lambda_terms.size(); k++)
		{
			String table_name = lambda_terms.get(k).table_name;
			
			Vector<Integer> Int_list = subgoal_lambda_term_map.get(table_name);
			
			if(Int_list == null)
			{
				Int_list = new Vector<Integer>();
				
				Int_list.add(k);
				
				subgoal_lambda_term_map.put(table_name, Int_list);
			}
			
			else
			{
				Int_list.add(k);
			}
			
		}
		
		return subgoal_lambda_term_map;
	}
	
	
//	static void get_insertion_annotation(HashMap<String, Vector<Integer>> subgoal_lambda_term_map)
//	{
//		
//		int num = lambda_terms.size();
//		String sql = new String();
//		Vector<String> paras = new Vector<String>();
//		
//		for(int i =0;i<num;i++)
//		{
//			String lambda_term = rs.getString(lambda_terms.get(i).toString());
//			if(i >= 1)
//				sql = sql + " and ";
//			sql = sql + lambda_terms.get(i) + "='" + lambda_term + "'";		
//			paras.add(lambda_term);
//			
//		}
//		
//		Vector<String> paras_subgoal = new Vector<String>();
//		
//		Vector<Integer> list = subgoal_lambda_term_map.get(subgoal.name);
//		
//		
//		
//		for(int k = 0; k<paras.size(); k++)
//		{
//			if(list!=null && list.contains(k))
//			{
//				paras_subgoal.add(paras.get(k));
//			}
//			else
//			{
//				paras_subgoal.add("");
//			}
//		}
//		
//		insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, paras_subgoal);
//	}
	
	static boolean exist_annotations(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		
		boolean annotation_exist = false;
		
		String query = "select citation_view from " + table_name;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			String annotation = rs.getString(1);

            annotation_exist = !(annotation == null || annotation.isEmpty());
		}
		
		return annotation_exist;
	}
	
	public static void gen_citation_view_query(Query view, HashMap<String, String> table_name_view, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
//		String citation_view_query = new String();
		
//		ResultSet rs = get_citation(view.name + "_table",view.lambda_term);
		
		HashMap<String, Vector<Integer>> subgoal_lambda_term_map = lambda_subgoal_mapping(view.lambda_term);
		
		String insert_annotation = new String();
		
		for(int i = 0; i<view.body.size(); i++)
		{	
			
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			Vector<String> primary_keys = get_primary_key(view.subgoal_name_mapping.get(subgoal.name), c, pst);
			
			String query_base = "update " + view.subgoal_name_mapping.get(subgoal.name) + suffix + " set citation_view = "; 
						
			String query1 = query_base;
			
			String query2 = query_base;
			
//			boolean annotation_exist = exist_annotations(subgoal.name + suffix, c, pst);
			
			if(subgoal_lambda_term_map.size() == 0)
			{
				
//				if(annotation_exist)
//				{
					query1 += "citation_view || '|" + view.name + "'";
//				}
//				else
//				{
					query2 += "'" + view.name + "'";
//				}
			}
			else
			{
				Vector<Integer> int_list = subgoal_lambda_term_map.get(subgoal.name);
				
//				if(annotation_exist)
					query1 += "citation_view || '|" + view.name + "('";
//				else
					query2 += "'" + view.name + "('";
				
//				for(int k = 0; k< view.lambda_term.size(); k++)
//				{
//					
//					if(k >= 1)
//					{
//						query1 += " || ','";
//						
//						query2 += " || ','";
//					}
//					
//					if(int_list != null && int_list.contains(k))
//					{
//						query1 += " || " + "f." + view.lambda_term.get(k).name.substring(subgoal.name.length() + 1, view.lambda_term.get(k).name.length());
//						
//						query2 += " || " + "f." + view.lambda_term.get(k).name.substring(subgoal.name.length() + 1, view.lambda_term.get(k).name.length());
//					}
//				}
				
				query1 += " || ')'";
				
				query2 += " || ')'";
				
//				query += insert_annotation;
				
			}
			
			query1 += " from ( select * from " + view.subgoal_name_mapping.get(subgoal.name) + ") as f where citation_view is not null";
			
			query2 += " from ( select * from " + view.subgoal_name_mapping.get(subgoal.name) + ") as f where citation_view is null";
			
			for(int k = 0; k<primary_keys.size(); k++)
			{
				query1 += " and f." + primary_keys.get(k) + " = " + view.subgoal_name_mapping.get(subgoal.name) + suffix + "." + primary_keys.get(k);
				
				query2 += " and f." + primary_keys.get(k) + " = " + view.subgoal_name_mapping.get(subgoal.name) + suffix + "." + primary_keys.get(k);
			}
			
			
			int local_predicate_num = 0;
			
			for(int k = 0; k<view.conditions.size(); k++)
			{
				Conditions condition = view.conditions.get(k);
				
				if(condition.arg2.isConst() && view.subgoal_name_mapping.get(condition.subgoal1).equals(subgoal.name))
				{
					
					String str = condition.arg2.name;
					
					if(condition.arg2.name.length() > 2)
					{
						str = "'" + condition.arg2.name.substring(1, condition.arg2.name.length() - 1).replaceAll("'", "''") + "'";
					}
					
					query1 += " and " + "f." + condition.arg1 + condition.op + str;
					
					query2 += " and " + "f." + condition.arg1 + condition.op + str;
				}
			}
			
			
			
//			System.out.println(query1);
			
//			System.out.println(query2);
			
			pst = c.prepareStatement(query1);
			
			pst.execute();
			
			pst = c.prepareStatement(query2);
			
			pst.execute();
			
		}
		
		
		
		
//		populate(citation_views, view, view.lambda_term,view.name + "_table", view.name, table_name_view, c,pst);
		
//		assign_view_query(view, citation_views, view.name, web_view, c, pst);
		
	}
	
	public static void assign_view_query(Query view, Vector<citation_view> citation_views, String view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Vector<Argument>> lambda_terms = new Vector<Vector<Argument>>();
		
		HashMap<String, String> table_name_view = new HashMap<String, String>();
		
//		if(web_view)
//		{
//			String get_tables = "select renamed_view, subgoal from web_view_table where view = '" + view_name + "'";
//			
//			pst = c.prepareStatement(get_tables);
//			
//			ResultSet r = pst.executeQuery();
//			
//			while(r.next())
//			{
//				table_name_view.put(r.getString(2), r.getString(1));
//			}
//			
//		}
		
		
		for(int i = 0; i<view.body.size();i++)
		{
			
			
			String lambda_term_sql = new String();

			
			Subgoal subgoal = (Subgoal) view.body.get(i);
			
			if(subgoal.contains(view.head.args));
			{
				Vector<Argument> args = new Vector<Argument>();//get_lambda_terms(view.lambda_term, subgoal);

				
				for(int j = 0; j<subgoal.args.size(); j++)
				{
					lambda_term_sql += "," + subgoal.args.get(j).toString();
					
					args.add((Argument) subgoal.args.get(j));
				}

			
			
			String citation_view_query = "select distinct citation_view" + lambda_term_sql + " from "+ view.name + "_table";
			
			pst = c.prepareStatement(citation_view_query);
		
			
		    ResultSet rs = pst.executeQuery();
		    
		    
		    while(rs.next())
		    {
		    	String citation_view = rs.getString(1);
		    	
		    	int pointer = 1;
		    	  		
		    		String key_sql = new String();
		    		
		    		String update_citation_view_sql = new String();
		    		
		    		for(int j = 0; j<args.size(); j++)
		    		{
						
						
						String item = rs.getString(++pointer);
						
						if(item!= null && item.contains("'"))
						{
							item = item.replaceAll("'", "''");
						}
						
//						if(item == null)
//						{
//							key_sql = key_sql + args.get(j).name + "= null";
//						}
						if(item != null)
						{
							if(j >= 1)
								key_sql = key_sql + " and ";
							
							key_sql = key_sql + args.get(j).name + "='" + item + "'";
						}
						
		    			
		    		}

		    		String curr_citation_view_sql = "select "+ "citation_view from " + subgoal.name + " where " + key_sql;
		    			    		
		    		pst = c.prepareStatement(curr_citation_view_sql);
		    			    		
		    		ResultSet temp_rs = pst.executeQuery();
		    		
		    		

		    		while(temp_rs.next())
		    		{
		    					    			
		    			String update_sql = key_sql;//primary_key + "='" + primary_key_value +"'"; 
		    			
	    				String curr_citation_view = temp_rs.getString(1);
	    				
	    				
//	    				if(web_view)
//	    				{
//	    					String renamed_view = table_name_view.get(subgoal.name);
//	    					
//	    					citation_view = rename_citation_view(citation_view, renamed_view);
//	    					
//	    				}
	    				

		    			if(curr_citation_view==null)
		    			{
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}
		    			else
		    			{
		    				
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + curr_citation_view + separator + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}

		    		}
		    		
		    					    
		    	}
				
			}

	    }
	}
	
	static String rename_citation_view(String curr_name, String new_name)
	{
			
		if(curr_name.contains("(") && curr_name.contains(")"))
		{
			int index = curr_name.indexOf("(");
			
			String sub_str = curr_name.substring(index, curr_name.length());
			
			return new_name + sub_str;
			
		}
		else
		{
			return new_name;
		}
	}
	
	public static Vector<Argument> get_lambda_terms(Vector<Argument> lambda_term, Subgoal subgoal)
	{
		Vector<Argument> lambda_terms = new Vector<Argument>();
		for(int i =0; i<lambda_term.size();i++)
		{
			if(subgoal.contains(lambda_term.get(i)))
			{
				lambda_terms.add(lambda_term.get(i));
			}
		}
		
		return lambda_terms;
	}
	
	
	
//	public static void populate(Vector<citation_view> citation_views, Query view, Vector<Lambda_term> lambda_terms , String table, String citation_name, HashMap<String, String> table_name_view, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
//	{
//		
//	    String test_col = "SELECT "+"* FROM "+table;
//	    
//	    pst = c.prepareStatement(test_col);
//	    
//	    ResultSet rs = pst.executeQuery();
//	    
//		if(!lambda_terms.isEmpty())
//		{
//			
//			HashMap<String, Vector<Integer>> subgoal_lambda_term_map = new HashMap<String, Vector<Integer>>();
//			
//			for(int k = 0; k<lambda_terms.size(); k++)
//			{
//				String table_name = lambda_terms.get(k).table_name;
//				
//				Vector<Integer> Int_list = subgoal_lambda_term_map.get(table_name);
//				
//				if(Int_list == null)
//				{
//					Int_list = new Vector<Integer>();
//					
//					Int_list.add(k);
//					
//					subgoal_lambda_term_map.put(table_name, Int_list);
//				}
//				
//				else
//				{
//					Int_list.add(k);
//				}
//				
//			}
//			
//			
//			while(rs.next())
//			{
//				int num = lambda_terms.size();
//				String sql = new String();
//				Vector<String> paras = new Vector<String>();
//				
//				for(int i =0;i<num;i++)
//				{
//					String lambda_term = rs.getString(lambda_terms.get(i).toString());
//					if(i >= 1)
//						sql = sql + " and ";
//					sql = sql + lambda_terms.get(i) + "='" + lambda_term + "'";		
//					paras.add(lambda_term);
//					
//				}
//							
//				String insert_citation_view;
//				
//
//				
//				for(int i = 0; i<view.body.size(); i++)
//				{
//					
//					Subgoal subgoal = (Subgoal) view.body.get(i);
//					
//					Vector<String> paras_subgoal = new Vector<String>();
//					
//					Vector<Integer> list = subgoal_lambda_term_map.get(subgoal.name);
//					
//					
//					
//					for(int k = 0; k<paras.size(); k++)
//					{
//						if(list!=null && list.contains(k))
//						{
//							paras_subgoal.add(paras.get(k));
//						}
//						else
//						{
//							paras_subgoal.add("");
//						}
//					}
//					
//					insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, paras_subgoal);
//					
//			
//					
////					String where_values = new String();
//					
//					
////					for(int k =0; k<subgoal.args.size(); k++)
////					{
////						Argument arg = (Argument) subgoal.args.get(k);
////											
////						String item = rs.getString(arg.name);
////						
////						if(item!= null && item.contains("'"))
////						{
////							item = item.replaceAll("'", "''");
////						}
////						
//////						if(item == null)
//////						{
//////							key_sql = key_sql + args.get(j).name + "= null";
//////						}
////						if(item != null && !item.isEmpty())
////						{
////							if(item.contains("."))
////							{
////								try{
////									float f = Float.valueOf(item);
////								}
////								catch(NumberFormatException e)
////								{
////									if( k >= 1)
////										where_values += " and ";
////									
////									where_values += arg.name + "='" + item + "'";
////								}
////							}
////							else
////							{
////								if( k >= 1)
////									where_values += " and ";
////								
////								where_values += arg.name + "='" + item + "'";
////						
////							}
////					
////							
//////							if( k >= 1)
//////								where_values += " and ";
//////							
//////							where_values += arg.name + "='" + item + "'";
////						}
////						
////						
////					}
//					
//					String curr_citation_sql = "select citation_view from " + subgoal.name;// + " where " + where_values;
//					
//					pst = c.prepareStatement(curr_citation_sql);
//					
//					ResultSet r = pst.executeQuery();
//					
//					if(r.next())
//					{
//						if(r.getString(1).isEmpty() || rs.getString(1) == null)
//						{
//							curr_citation_sql = "update citation_view set name = name || '" + insert_citation_view + "'";
//						}
//						else
//						{
//							curr_citation_sql = "update citation_view set name = name || '|" + insert_citation_view + "'";
//						}
//					}
//					
////					System.out.println(curr_citation_sql);
//					
//					pst = c.prepareStatement(curr_citation_sql);
//					
//					pst.execute();
//					
//					
////					String citations = null;
////					
////					if(r.next())
////					{
////						citations = r.getString(1);
////						
////					}
////					if(citations !=null)
////					{
////						String[] citation_list = citations.split("\\" + populate_db.separator);
////						
////						Vector<String> curr_citation_vec = new Vector<String>();
////						
////						curr_citation_vec.addAll(Arrays.asList(citation_list));
////						
////						if(!curr_citation_vec.contains(insert_citation_view))
////						{
////							
////							String update_sql = "update " + subgoal.name + " set citation_view='" + citations + populate_db.separator + insert_citation_view + "' where " + where_values;
////						    
////							pst = c.prepareStatement(update_sql);
////						    
////							pst.execute();
////						}
////												
////					}
////					else
////					{
////						String update_sql = "update " + subgoal.name + " set citation_view='" + insert_citation_view + "' where " + where_values;
////					    
////						pst = c.prepareStatement(update_sql);
////					    
////						pst.execute();
////					}
//										
//						
//						
//						
//					
//					
//				}
//				
////				String curr_citation_view = rs.getString("citation_view");
////				if(curr_citation_view!=null)
////				{
////					curr_citation_view = curr_citation_view + "," + insert_citation_view;
////				}
////				else
////				{
////					curr_citation_view = insert_citation_view;
////				}
////				
//				
//				
//			}
//		}
//		else
//		{
//			
//			
//			while(rs.next())
//			{
//				
//				String insert_citation_view;
//				
//
//				
//				for(int i = 0; i<view.body.size(); i++)
//				{
//					Subgoal subgoal = (Subgoal) view.body.get(i);
//										
//					insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, null);
//					
//					
////					for(int k =0; k<subgoal.args.size(); k++)
////					{
////						Argument arg = (Argument) subgoal.args.get(k);
////					
////						String item = rs.getString(arg.name);
////						
////						if(item!= null && item.contains("'"))
////						{
////							item = item.replaceAll("'", "''");
////						}
////
////						if(item != null && !item.isEmpty())
////						{
////							
////							if(item.contains("."))
////							{
////								try{
////									float f = Float.valueOf(item);
////								}
////								catch(NumberFormatException e)
////								{
////									if( k >= 1)
////										where_values += " and ";
////									
////									where_values += arg.name + "='" + item + "'";
////								}
////							}
////							else
////							{
////								if( k >= 1)
////									where_values += " and ";
////								
////								where_values += arg.name + "='" + item + "'";
////					
////							}
////							
////							
////							
////							
////						}
////						
////						
////					}
//					
//					
//					String curr_citation_sql = "select citation_view from " + subgoal.name;
//					
//					pst = c.prepareStatement(curr_citation_sql);
//					
//					ResultSet r = pst.executeQuery();
//					
//					if(r.next())
//					{
//						if(r.getString(1).isEmpty() || rs.getString(1) == null)
//						{
//							curr_citation_sql = "update citation_view set name = name || '" + insert_citation_view + "'";
//						}
//						else
//						{
//							curr_citation_sql = "update citation_view set name = name || '|" + insert_citation_view + "'";
//						}
//					}
//					
////					System.out.println(curr_citation_sql);
//					
//					pst = c.prepareStatement(curr_citation_sql);
//					
//					pst.execute();
//
//					
////					pst = c.prepareStatement(curr_citation_sql);
////					
////					ResultSet r = pst.executeQuery();
////					
////					
////					String citations = null;
////					
////					if(r.next())
////					{
////						citations = r.getString(1);
////						
////					}
////					
////					if(citations !=null)
////					{
////						String[] citation_list = citations.split("\\" + populate_db.separator);
////						
////						Vector<String> curr_citation_vec = new Vector<String>();
////						
////						curr_citation_vec.addAll(Arrays.asList(citation_list));
////						
////						if(!curr_citation_vec.contains(insert_citation_view))
////						{
////							
////							String update_sql = "update " + subgoal.name + " set citation_view='" + citations + populate_db.separator + insert_citation_view + "' where " + where_values;
////						    
////							pst = c.prepareStatement(update_sql);
////						    
////							pst.execute();
////						}
////												
////					}
////					else
////					{
////						String update_sql = "update " + subgoal.name + " set citation_view='" + insert_citation_view + "' where " + where_values;
////					    
////						pst = c.prepareStatement(update_sql);
////					    
////						pst.execute();
////					}
//					
//				}
//				
//			}
//						
//			
//		}
//	}
//	
//	public static Vector<citation_view> get_citation_view(Query view) throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//	      ResultSet rs = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
//	    
//	    int id = Integer.valueOf(view.name.substring(1, view.name.length()));
//	    
//	    String view_table_q = "select * from view_table where view = '" + id + "'";
//	    
//	    pst = c.prepareStatement(view_table_q);
//	    
//	    rs = pst.executeQuery();
//	    
//	    
//	    Vector<citation_view> citations = new Vector<citation_view>();
//	    
//	    Vector<String> lambda_terms = new Vector<String>();
//
//	    
//	    while(rs.next())
//	    {
////	    	boolean webview = rs.getBoolean(3);
//	    	
////	    	web_view.add(webview);
////	    	
////	    	if(webview)
////	    	{
////	    		
////	    		get_view_lambda_terms(view, lambda_terms, c, pst);
////	    		
//////	    		get_view_subgoals(view.name, subgoals, c, pst);
////	    		get_renamed_views(view.name, lambda_terms, citations, c, pst);
////
////	    	}
////	    	
////	    	else
//	    	{
//	    		get_view_lambda_terms(view, lambda_terms, c, pst);
//    		    
//	    		
//	    		Vector<String> subgoals = new Vector<String>();
//	    		
//	    		get_view_subgoals(view.name, subgoals, c, pst);
//    		    
//    		    if(!lambda_terms.isEmpty())
//    		    {	
//    		    	
//		    		citations.add(new citation_view_parametered(view.name, lambda_terms, subgoals));
//		    		    		    
//    		    }
//    		    else
//    		    {
//    		    	citations.add(new citation_view_unparametered(view.name, subgoals));
//    		    }
//	    	}
//	    	
//	    }
//	    
//		return citations;
//
////	    String test_col = "SELECT citation_view.citation_view_name FROM citation_view WHERE citation_view.view_name='"+view.name+"'";
////	    pst = c.prepareStatement(test_col);
////	    rs = pst.executeQuery();
////	    
////	    String citation_view_name = new String();
////	    
//////	    Vector<String> lambda_term_vec = new Vector<String>(); 
////	    
////	    if(rs.next())
////	    {
////	    	citation_view_name = rs.getString(1);
////	    }
//	    
//	   
//	}
//	
	
	static void get_renamed_views(String name, Vector<String> lambda_terms, Vector<citation_view> citations, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String rename_view_sql = "select renamed_view, subgoal from web_view_table where view = '" + name + "'";
		
	    pst = c.prepareStatement(rename_view_sql);
	    
	    ResultSet r = pst.executeQuery();
	    
	    while(r.next())
	    {
	    	String renamed_view = r.getString(1);
	    	
	    	String subgoal = r.getString(2);
	    	
	    	if(!lambda_terms.isEmpty())
	    	{
	    		Vector<String> table_names = new Vector<String>();
	    		
	    		table_names.add(subgoal);
	    		
	    		citation_view c_v = new citation_view_parametered(renamed_view, lambda_terms, table_names);
	    		
	    		citations.add(c_v);
	    	}
	    	
	    	else
	    	{
	    		Vector<String> table_names = new Vector<String>();
	    		
	    		table_names.add(subgoal);
	    		
	    		citation_view c_v = new citation_view_unparametered(renamed_view, table_names);
	    		
	    		citations.add(c_v);
	    	}
	    		
		}
		
	}
	
	static void get_view_lambda_terms(Query view, Vector<String> lambda_terms, Connection c, PreparedStatement pst)throws ClassNotFoundException, SQLException
	{
		String lambda_term_sql = "select lambda_term from view2lambda_term where view = '" + view.name + "'";
	    pst = c.prepareStatement(lambda_term_sql);
	    
	    ResultSet r = pst.executeQuery();
	    
	    while(r.next())
	    {
//		    	if(rs.getString(1) != null && rs.getString(1).length()!=0)
	    	
	    		lambda_terms.add(r.getString(1));
	    		
//		    		System.out.println(lambda_terms);
	    		
//	    		citation_view c_v = new citation_view_parametered(view.name, lambda_terms);
	    		
		}
	
	}
	
	static void get_view_subgoals(String name, Vector<String> subgoals, Connection c, PreparedStatement pst) throws SQLException
	{
		String subgoal_sql = "select subgoal_names from view2subgoals where view = '" + name + "'";
	    
		pst = c.prepareStatement(subgoal_sql);
	    
	    ResultSet r = pst.executeQuery();
	    	    
	    while(r.next())
	    {
//		    	if(rs.getString(1) != null && rs.getString(1).length()!=0)
	    	
	    		subgoals.add(r.getString(1));
	    		
//		    		System.out.println(lambda_terms);
	    		
//	    		citation_view c_v = new citation_view_parametered(view.name, lambda_terms);
	    		
		}
	}
	
//	public static ResultSet get_citation(String table, Vector<Lambda_term> lambda_term) throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//	      ResultSet rs = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
//	        "postgres","123");
////	    String lambda_term_string = lambda_term.toString();
//	    
//	    lambda_term_string = lambda_term_string.substring(1,lambda_term_string.length() - 1);
//	    
//    	Vector <Argument> t = new Vector<Argument>();
//
////	    
////	    t.add(new Argument("q",false));
////	    t.add(new Argument("r",false));
////	    
////	    
//		    String t_str = t.toString();
//		    
//		    String test_col = "SELECT "+"* FROM "+table;
//		    pst = c.prepareStatement(test_col);
//		    rs = pst.executeQuery();
//		    
////	    if(!lambda_term.isEmpty())
////	    {
////	    
////
////
////	    }
////	    else
////	    {
//////	    	String q_primary_key = "select kc.column_name from information_schema.table_constraints tc,information_schema.key_column_usage kc where tc.constraint_type = 'PRIMARY KEY' and kc.table_name = tc.table_name and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name and tc.table_name='"+ table +"'";
//////	    	pst = c.prepareStatement(q_primary_key);
//////		    rs = pst.executeQuery();
//////	    	
//////		    String primary_key = new String();
//////		    if(rs.next())
//////		    	primary_key = rs.getString(1);
//////	    	
//////		    lambda_term.clear();
//////		    lambda_term.add(new Argument(primary_key));
////	    	String test_col = "SELECT distinct citation_view FROM "+table;
////		    pst = c.prepareStatement(test_col);
////		    rs = pst.executeQuery();
////	    }
//	    return rs;
//	}

//	public static Vector<Query> get_views_schema(Connection c, PreparedStatement pst)
//	{
//	      ResultSet rs = null;
//	      Vector<Query> views = new Vector<Query>();
//	      
//	      try {
//	         
//	         
////	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
//	         pst = c.prepareStatement("SELECT *  FROM view_table");
//	         rs = pst.executeQuery();
//	         int num=1;
//	         
//	            while (rs.next()) {
//	            	
//	            	String head = rs.getString(1);
//	            	
//	            	String [] str_lambda_terms = null;
//	            	
//	            	if(!rs.getString(2).equals(""))
//	            	{
//	            		str_lambda_terms = rs.getString(2).split(",");
//	            	}
//	            	
//	            	String [] subgoals = rs.getString(3).split("\\),");
//	                
//	                subgoals[subgoals.length-1]=subgoals[subgoals.length-1].split("\\)")[0];
//	            	
//	            	
//	                Query view = parse_query(head, subgoals,str_lambda_terms);
//	                
//	                views.add(view);
////	                System.out.print(rs.getString(1));
////	                System.out.print('|');
////	                System.out.print(rs.getString(2));
////	                if(rs.getString(2).length()==0)
////	                {
////	                	int y=0;
////	                	y++;
////	                }
////	                System.out.print('|');
////	                System.out.println(rs.getString(3));
//	            }
//	         
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	         System.err.println(e.getClass().getName()+": "+e.getMessage());
//	         System.exit(0);
//	      }
//	      
//	      return views;
//	}
//	
//	static Query parse_query(String head, String []body, String []lambda_term_str)
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
