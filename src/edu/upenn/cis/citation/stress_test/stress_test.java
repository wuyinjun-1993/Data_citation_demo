package edu.upenn.cis.citation.stress_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1_citation_opt;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2_citation_opt;
import edu.upenn.cis.citation.user_query.query_storage;

public class stress_test {
	
	static int size_range = 100;
	
	static int times = 3;
	
	static int size_upper_bound = 5;
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		
//		reset();
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
		
		
		
		HashMap<Head_strs, Vector<String> > citation_strs = new HashMap<Head_strs, Vector<String>>();
		
		HashMap<Head_strs, Vector<String> > citation_strs2 = new HashMap<Head_strs, Vector<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
		
		Vector<Vector<citation_view_vector>> citation_view1 = new Vector<Vector<citation_view_vector>>();

		Vector<Vector<citation_view_vector>> citation_view2 = new Vector<Vector<citation_view_vector>>();
		
		for(int j = 2; j<size_upper_bound; j++)
		{
			
//			Vector<Query> queries = new Vector<Query>();
//			
//			Query query = query_storage.get_query_by_id(1);
//			
//			queries.add(query);
			
			reset();
			
			Vector<Query> queries = query_generator.gen_queries(j, size_range);
			
			
			
			query_storage.store_query(queries.get(0), new Vector<Integer>());
			
			for(int i = 0; i<queries.size(); i++)
			{
				
				Query q = queries.get(i);

				
				if(i == 0)
				{
					System.out.println(q);
					
					System.out.println(Query_converter.datalog2sql(q));
								
					System.out.println(q.body.size());
				}
				
				System.out.print(q.conditions + "	");
				
				double end_time = 0;

				
				double start_time = System.nanoTime();
				
				
				for(int k = 0; k<times; k++)
				{
					
					citation_view_map1.clear();
					
					citation_strs.clear();
					
					citation_view1 = Tuple_reasoning1_citation_opt.tuple_reasoning(q, citation_strs,  citation_view_map1, c, pst);
					
				}
				
				
				
				end_time = System.nanoTime();
				
				double time = (end_time - start_time);
				
				time = time /(times * 1.0 *1000000000);
				
				System.out.print(time + "s	");
				
				Set<Head_strs> h_l = citation_strs.keySet();
				
				double citation_size = 0;
				
				int row = 0;
				
				for(Iterator iter = h_l.iterator(); iter.hasNext();)
				{
					Head_strs h_value = (Head_strs) iter.next();
					
					Vector<String> citations = citation_strs.get(h_value);
					
					citation_size += citations.size();
					
					row ++;
					
				}
				
				if(row !=0)
				citation_size = citation_size / row;
				
				System.out.print(citation_size + "	");
				
				start_time = System.nanoTime();
				
				for(int k = 0; k<times; k++)
				{
					citation_view_map2.clear();
					
					citation_strs2.clear();
					
					Tuple_reasoning2_citation_opt.tuple_reasoning(q, citation_strs2, citation_view_map2, c, pst);
				}
				
				end_time = System.nanoTime();
				
				time = (end_time - start_time);
				
				
				time = time /(times * 1.0 *1000000000);
				
				System.out.print(time + "s	");
				
				
				h_l = citation_strs2.keySet();
				
				citation_size = 0;
				
				row = 0;
				
				for(Iterator iter = h_l.iterator(); iter.hasNext();)
				{
					Head_strs h_value = (Head_strs) iter.next();
					
					Vector<String> citations = citation_strs2.get(h_value);
					
					citation_size += citations.size();
					
					row ++;
					
				}
				
				if(row !=0)
				citation_size = citation_size / row;

				System.out.print(citation_size + "	");

				
				Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
				
//				System.out.println();

			}
		}
		
		c.close();
		
		
	}
	
	static void reset() throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String query = "delete from user_query2conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query2subgoals";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}

}
