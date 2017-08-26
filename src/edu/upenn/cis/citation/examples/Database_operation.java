package edu.upenn.cis.citation.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.citation_view_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;

public class Database_operation {
	
	public static void clear() throws ClassNotFoundException, SQLException
	{
		
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        populate_db.renew_table(c, pst);
        
		view_operation.delele_all_views(c, pst);
		
		Query_operation.delete_all_queries(c, pst);
		
		citation_view_operation.delete_all_citation_table(c, pst);
		
		c.close();
	}
	
	public static void load2db(Vector<Query> views, Vector<Query> queries, Vector<String[]> connections) throws ClassNotFoundException, SQLException
	{
		clear();
		
		for(int i = 0; i<views.size(); i++)
		{
			String name = views.get(i).name;
			
			view_operation.add(views.get(i), views.get(i).name);
			
			citation_view_operation.add_citation_view(name);
			
			citation_view_operation.add_connection_view_with_citations(name, name);
		}
		
		for(int i = 0; i<queries.size(); i++)
		{
			Query_operation.add(queries.get(i), queries.get(i).name);
		}
		
		for(int i = 0; i<connections.size(); i++)
		{
			Query_operation.add_connection_citation_with_query(connections.get(i)[0], connections.get(i)[1], connections.get(i)[2]);
		}
	}

}
