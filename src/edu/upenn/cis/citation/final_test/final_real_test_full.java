package edu.upenn.cis.citation.final_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Example_real;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.reasoning1.Schema_level_approach;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;
import edu.upenn.cis.citation.user_query.query_storage;

public class final_real_test_full {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> relations = new Vector<String>();
	
	static String path = "real_example/";
	
	static String path2 = "reasoning_results/";
	
	static Vector<String> get_unique_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
		
		HashSet<String> relations = new HashSet<String>();
		
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relations.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
		
		relation_names.addAll(relations);
		
		return relation_names;
		
	}
	
	static Vector<String> get_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
				
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relation_names.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
				
		return relation_names;
		
	}
	
	public static void get_table_size(Vector<String> relations, Connection c, PreparedStatement pst) throws SQLException
	{
		int original_size = 0;
		
		int annotated_relation_size = 0;
		
		for(int i = 0; i<relations.size(); i++)
		{
			original_size += get_single_table_size(relations.get(i), c, pst);
			
//			annotated_relation_size += get_single_table_size(relations.get(i) + populate_db.suffix, c, pst);
		}
		
		System.out.println("original_relation_size::" + original_size);
		
//		System.out.println("annotated_relation_size::" + annotated_relation_size);
	}
	
	static int get_single_table_size(String relation, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT pg_total_relation_size('"+ relation +"')";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			int size =  rs.getInt(1);
			
//			int size_int = Integer.valueOf(size.substring(0, size.indexOf(" ")));
			
			return size;
		}
		
		return 0;
	}

	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{		
		Connection c1 = null;
		
		Connection c2 = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
		
//	    System.out.println(get_single_table_size("family", c, pst));
	    
	    int view_id = Integer.valueOf(args[0]);

        boolean new_round = Boolean.valueOf(args[1]);
	    
	    boolean tuple_level = Boolean.valueOf(args[2]);
        
        boolean schema_level = Boolean.valueOf(args[3]);
        
        boolean agg_intersection = Boolean.valueOf(args[4]);
	    
	    String path = args[5];
	    
	    String db_name1 = args[6];
	    
	    String db_name2 = args[7];
	    
	    String user_name = args[8];
	    
	    String passwd = args[9];
	    
	    c1 = DriverManager
            .getConnection(populate_db.db_url_prefix + db_name1, user_name, passwd);
        
        c2 = DriverManager
            .getConnection(populate_db.db_url_prefix + db_name2, user_name, passwd);
	    
	    
    	Vector<Query> queries = Load_views_and_citation_queries.get_views(path + "query", c2, pst);
    	
    	Vector<Query> views = Load_views_and_citation_queries.get_views(path + "views", c2, pst);
    	
    	Vector<Query> citation_queries = Load_views_and_citation_queries.get_views(path + "citation_queries", c2, pst);
    	
    	HashMap<String, HashMap<String, String>> view_citation_query_mappings = Load_views_and_citation_queries.get_view_citation_query_mappings(path + "view_citation_query_mappings");
	    
    	relations = get_unique_relation_names(queries.get(view_id));
    	
//    	stress_test.init_views(views, new_views, new_rounds, new_start, queries.get(view_id), relations, c1, c2, pst);
    	
    	if(new_round)
    	{
    	  
    	  
//    	  stress_test.clear_views_in_database(c1, pst);
//    	  
//    	  stress_test.clear_views_in_database(c2, pst);
//    	  
//    	  stress_test.materialize_views(views, c1, pst);
//    	  
//    	  stress_test.materialize_views(views, c2, pst);
    	  
    	  
    	  populate_db.renew_table(c1, pst);
          
          populate_db.populate_db2(views, c1, pst);
    	}
    	
    	c1.close();
    	
    	c2.close();
    	
    	stress_test.stress_test(queries.get(view_id), views, citation_queries, view_citation_query_mappings, tuple_level, schema_level, agg_intersection, false, db_name1, db_name2, user_name, passwd);
    	
//		    Example_real.load_view_and_citations(path + "views", path + "citation_queries", path + "connection", true, c1, pst);
//		    
//		    Example_real.load_view_and_citations(path + "views", path + "citation_queries", path + "connection", false, c2, pst);
	    
	    	    		
				
//		for(int i = 0; i<queries.size(); i++)
//		{
////			System.out.println(i);
//			
//			
//			stress_test(queries.get(view_id), tuple_level, schema_level, agg_intersection);
//		}		
//		
//		Vector<Query> user_query = new Vector<Query>();
//		
//		user_query.add(queries.get(view_id));
//		
//		output_queries(user_query, path2 + "user_queries");
//		
//		Vector<Query> views = view_operation.get_all_views(c2, pst);
//		
//		output_queries(views, path2 + "views");
//		
//		Vector<Query> all_citation_queries = Query_operation.get_all_citation_queries(c2, pst);
//		
//		output_queries(all_citation_queries, path2 + "citation_query");
//		
//		Vector<String> sqls = new Vector<String>();
//		
//		sqls.add(Query_converter.datalog2sql(queries.get(view_id)));
//		
//		Query_operation.write2file(path2 + "user_query_sql", sqls);
//		
//		c1.close();
//		
//		c2.close();
//		
////		Query query = query_storage.get_query_by_id(1);
		
		
	}
	
	static void output_queries(Vector<Query> queries, String file_name) throws IOException
	{
		Vector<String> query_strs = new Vector<String>();
		
		for(int i = 0; i<queries.size(); i++)
		{
			String query_str = Query_operation.covert2data_str(queries.get(i));
			
			query_strs.add(query_str);
		}
		
		Query_operation.write2file(file_name, query_strs);
	}
	
	static void reset(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		String query = "delete from user_query2conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query2subgoals";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		
	}

}
