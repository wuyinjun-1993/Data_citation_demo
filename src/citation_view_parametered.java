import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.ParentIterator;

public class citation_view_parametered extends citation_view{
	
	public String name = new String ();
	
	public Vector<String> lambda_terms = new Vector<String>();
		
	public Vector<String> parameters = new Vector<String>();
	
	HashMap<String, String> map = new HashMap<String, String>();
	
	public Vector<String> table_names = new Vector<String>();
	
	public char index;
	
	void gen_lambda_terms(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	    
	    String lambda_term_query = "select v.lambda_term from citation_view c join view_table v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    while(rs.next())
	    {
	    	String []lambda_term_strs = rs.getString(1).split(",");
	    	
	    	lambda_terms.addAll(Arrays.asList(lambda_term_strs));
	    	
//	    	for(int i = 0; i<lambda_term_strs.length; i++)
//	    	{
//	    		lambda_terms.add(lambda_term_strs[i]);
//	    	}
	    }
	    

	}
	
	void gen_table_names(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	    
	    String lambda_term_query = "select v.subgoal_names from citation_view c join subgoals v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    while(rs.next())
	    {
	    		    	
	    	table_names.add(rs.getString(1));
	    }
	    

	}
	
	
	public void gen_index()
	{
		index = (char)Integer.parseInt(name.substring(1, name.length()));
	}
	
	public citation_view_parametered(String name) throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
		this.name = name;
		gen_lambda_terms(c,pst);
		
		gen_table_names(c, pst);
		
		gen_index();
			    
	    c.close();
//		get_queries();
	}
	
	
	public citation_view_parametered(String name, Vector<String> lambda_terms) throws ClassNotFoundException, SQLException {
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		this.name = name;
		this.lambda_terms = lambda_terms;
		gen_index();
			    
		gen_table_names(c,pst);
		
	    c.close();
//		get_queries();
	}
	
	public void put_paramters(Vector<String> para)
	{
		parameters = para;
		
		assert(parameters.size()==lambda_terms.size());
		
		for(int i = 0; i<parameters.size(); i++)
		{
			map.put(lambda_terms.get(i), parameters.get(i));
		}
	}
	//TODO: consider more complicated queries
	
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
	    	
	    	num ++;
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
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
	    String head_variables = get_head_variables(c, pst);
	    
	    String body = get_body(c, pst);
	    
	    String query4citation = this.name + "(" + head_variables + "):" + body;
	    
	    Query query = Parse_datalog.parse_query(query4citation);
	    
	    assign_paras(query);
	    
	    String q = Query_converter.datalog2sql(query);
	    
	    c.close();
	    
	    return q;
	    
	    
		
	}
	
	public void assign_paras(Query query)
	{
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument) query.head.args.get(i);
			
			if(map.get(arg.toString()) != null)
			{				
				query.head.args.setElementAt(new Argument("'"+ map.get(arg.toString()) + "'",arg.toString()), i);
				
				break;
				
			}
		}
		
		for(int i = 0; i<query.body.size(); i++)
		{
			
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			for(int j = 0; j<subgoal.args.size(); j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
				if(map.get(arg.toString()) != null)
				{				
					subgoal.args.setElementAt(new Argument("'" + map.get(arg.toString()) + "'",arg.toString()), j);
					
					break;
				}
			}
		}
	}

	@Override
	public String gen_citation_unit(Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		
		String insert_citation_view = name + "(";
		int num = lambda_terms.size();
		for(int i =0;i<num;i++)
		{
			String lambda_term = lambda_terms.get(i);
			
			if(i!=num-1)
				insert_citation_view = insert_citation_view + lambda_term + ",";
			else
				insert_citation_view = insert_citation_view + lambda_term;
			
		}
		insert_citation_view = insert_citation_view + ")";
		
		
		return insert_citation_view;
	}
	
	@Override
	public String toString()
	{
		String str = name + "(";
		
		for(int i = 0; i<parameters.size();i++)
		{
			str = str + parameters.get(i);
			
			if(i < parameters.size() - 1)				
				str = str + ",";
				
		}
		
		str = str + ")";
		
		return str;
	}

	@Override
	public String get_name() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public Vector<String> get_parameters() {
		// TODO Auto-generated method stub
		return parameters;
	}

	@Override
	public Vector<String> get_table_names() {
		// TODO Auto-generated method stub
		return table_names;
	}

	@Override
	public char get_index() {
		// TODO Auto-generated method stub
		return index;
	}
	

}
