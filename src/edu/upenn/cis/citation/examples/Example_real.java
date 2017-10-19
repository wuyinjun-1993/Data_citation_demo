package edu.upenn.cis.citation.examples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.populate_db;

public class Example_real {
	
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
//		populate_db.db_url = "jdbc:postgresql://localhost:5432/test";
		
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		Database_operation.clear(c, pst, true);
		
		load_view_and_citations(c, pst);
		
		c.close();
	}
	
	public static void load_view_and_citations(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		Init_examples init = new Init_examples();
		
		init.load_properties();
		
		String view_file = init.configProp.getProperty("test.views");
		
		String citation_query_file = init.configProp.getProperty("test.citation_query");
		
		String connection_file = init.configProp.getProperty("test.connection");
		
		Vector<Query> views = Load_views_and_citation_queries.get_views(view_file, c, pst);
		
		Vector<Query> citation_queries = Load_views_and_citation_queries.get_views(citation_query_file, c, pst);
		
		Vector<String []> connections = Load_connections.get_view_citation_connection(connection_file);
		
//		populate_db.db_url = "jdbc:postgresql://localhost:5432/example";
		
		Database_operation.load2db(views, citation_queries, connections, true, c, pst);
	}
	
	public static void load_view_and_citations(String view_file, String citation_query_file, String connection_file, boolean tuple_level, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		Vector<Query> views = Load_views_and_citation_queries.get_views(view_file, c, pst);
		
		Vector<Query> citation_queries = Load_views_and_citation_queries.get_views(citation_query_file, c, pst);
		
		Vector<String []> connections = Load_connections.get_view_citation_connection(connection_file);
		
//		populate_db.db_url = "jdbc:postgresql://localhost:5432/example";
		
		Database_operation.load2db(views, citation_queries, connections, tuple_level, c, pst);
	}

}
