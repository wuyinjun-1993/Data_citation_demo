package citation_view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import Corecover.Query;
import Pre_processing.populate_db;
import datalog.Parse_datalog;
import datalog.Query_converter;

public class citation_view_unparametered extends citation_view{
	public String name;
	
	public char index;
//	public boolean lambda;	
	public Vector<String> table_names = new Vector<String>();
	
	public citation_view_unparametered(String name) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
	        "postgres","123");
		
		gen_table_names(c, pst);
		
		gen_index();
	    
	    c.close();
//		lambda = false;
	}
	
	void gen_table_names(Connection c, PreparedStatement pst ) throws ClassNotFoundException, SQLException
	{
		
	    
	    String lambda_term_query = "select v.subgoal_names from citation_view c join subgoals v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    while(rs.next())
	    {
	    		    	
	    	table_names.add(rs.getString(1));
	    }
	    

	}

	
//	public citation_view_unparametered(String name, String query)
//	{
//		this.name = name;
//		this.query = query.toLowerCase();
////		lambda = false;
//	}
	
	
	//TODO: consider more complicated queries
	
//	public String get_full_query() throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
//	        "postgres","123");
//		
//	    String citation_query = "select citation_view_query from citation_view where citation_view_name = '" + name + "'";
//	    
//	    pst = c.prepareStatement(citation_query);
//	    
//	    ResultSet rs = pst.executeQuery();
//	    
//	    String query4citation = new String();
//	    
//	    if(rs.next())
//	    {
//	    	query4citation = rs.getString(1);
//	    }
//	    
//	    Query query = Parse_datalog.parse_query(query4citation);
//	    	    
//	    String q = Query_converter.datalog2sql(query);
//	    
//	    c.close();
//	    
//	    return q;
//		
//	}

	String get_head_variables(Connection c, PreparedStatement pst) throws SQLException
	{
		String citation_query = "select head_variables from citation_view where citation_view_name = '" + name + "'";
	    
	    pst = c.prepareStatement(citation_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    	return rs.getString(1);
	    else
	    	return null;
	}
	
	String get_body(Connection c, PreparedStatement pst) throws SQLException
	{
		String citation_query = "select subgoal_names, arguments from citation_subgoals join subgoal_arguments using (subgoal_names) where citation_view_name = '" + name + "'";
	    
	    pst = c.prepareStatement(citation_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    String body = new String();
	    
	    int num = 0;
	    
	    while(rs.next())
	    {
	    	
	    	if(num >= 1)
	    		body += ",";
	    	body += rs.getString(1) + "(" + rs.getString(2) + ")";
	    	
	    	num++;
	    }
	    return body;
	}
	
	@Override
	public String get_full_query() throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
	        "postgres","123");
		
	    String head_variables = get_head_variables(c, pst);
	    
	    String body = get_body(c, pst);
	    
	    String query4citation = this.name + "(" + head_variables + "):" + body;
	    
	    Query query = Parse_datalog.parse_query(query4citation);
	    
//	    assign_paras(query);
	    
	    String q = Query_converter.datalog2sql(query);
	    
	    c.close();
	    
	    return q;
	    
	    
		
	}
	
	@Override
	public String gen_citation_unit(Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		
		
		
		
		return name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public String get_name() {
		// TODO Auto-generated method stub
		return name;
	}

//	@Override
//	public Vector<String> get_parameters() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Vector<String> get_table_names() {
		// TODO Auto-generated method stub
		return table_names;
	}
	
	public void gen_index()
	{
		index = (char)Integer.parseInt(name.substring(1, name.length()));
	}

	@Override
	public char get_index() {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public void put_paras(String para, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean has_lambda_term() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public citation_view clone() {
		// TODO Auto-generated method stub
		return this;
	}

}
