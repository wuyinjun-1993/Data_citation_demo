package edu.upenn.cis.citation.citation_view;
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
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.ParentIterator;

import edu.upenn.cis.citation.Corecover.*;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;

public class citation_view_parametered extends citation_view{
	
	public String name = new String ();
	
	public String table_name_str = new String();
	
	
	
	
	
	//the lambda_terms in the view tuple
	public Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
	
	public Query view;
	
	public Tuple view_tuple;
		
//	public Vector<String> parameters = new Vector<String>();
	
	public HashMap<String, String> map = new HashMap<String, String>();
	
	
	//relation_name in the body of query
	public Vector<String> table_names = new Vector<String>();
		
	void gen_lambda_terms(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	    
	    String lambda_term_query = "select vl.lambda_term, vl.table_name from citation_view c join view_table v on v.view=c.view_name join view2lambda_term vl on vl.view = v.view where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    	    
	    while(rs.next())
	    {
//	    	String []lambda_term_strs = rs.getString(1).split(",");
	    	Lambda_term l_term = new Lambda_term(rs.getString(1));
	    	
	    	l_term.table_name = rs.getString(2);
	    	
	    	this.lambda_terms.add(l_term);
	    	
//	    	for(int i = 0; i<lambda_term_strs.length; i++)
//	    	{
//	    		lambda_terms.add(lambda_term_strs[i]);
//	    	}
	    }
	    

	}
	
	void gen_lambda_terms(boolean web_view, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
		if(web_view)
		{
			String lambda_term_query = "select v.lambda_term, v.table_name from view2lambda_term v join web_view_table w using (view) where w.renamed_view = '"+name + "'";
			
			pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet rs = pst.executeQuery();
		    	    
		    while(rs.next())
		    {
//			    	String []lambda_term_strs = rs.getString(1).split(",");
		    	Lambda_term l_term = new Lambda_term(rs.getString(1));
		    	
		    	l_term.table_name = rs.getString(2);
		    	
		    	this.lambda_terms.add(l_term);
		    	
//			    	for(int i = 0; i<lambda_term_strs.length; i++)
//			    	{
//			    		lambda_terms.add(lambda_term_strs[i]);
//			    	}
		    }
			
			
		}
		
		else
		{
			String lambda_term_query = "select v.lambda_term, v.table_name from view2lambda_term v where v.view = '"+ name +"'";
		    
		    pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet rs = pst.executeQuery();
		    	    
		    while(rs.next())
		    {
//		    	String []lambda_term_strs = rs.getString(1).split(",");
		    	Lambda_term l_term = new Lambda_term(rs.getString(1));
		    	
		    	l_term.table_name = rs.getString(2);
		    	
		    	this.lambda_terms.add(l_term);
		    	
//		    	for(int i = 0; i<lambda_term_strs.length; i++)
//		    	{
//		    		lambda_terms.add(lambda_term_strs[i]);
//		    	}
		    }
		}
		
	    
	    
	    

	}
	
	void gen_table_names(Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	    
		String lambda_term_query = "select subgoal_names from view2subgoals where view = '"+ name +"'";

		
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(!rs.wasNull())
	    {
	    	while(rs.next())
		    {
		    		    	
		    	table_names.add(rs.getString(1));
		    }
	    }
	    
	    else
	    {
	    	lambda_term_query = "select subgoal from web_view_table where renamed_view = '"+ name +"'";

			
		    pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet r = pst.executeQuery();

		    while(r.next())
		    {
		    		    	
		    	table_names.add(r.getString(1));
		    }
	    }
	    

	}
	
	void gen_table_names(boolean web_view, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		
	    
		
	    
	    if(!web_view)
	    {
	    	String lambda_term_query = "select subgoal_names from view2subgoals where view = '"+ name +"'";

			
		    pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet rs = pst.executeQuery();
	    	
	    	while(rs.next())
		    {
		    		    	
		    	table_names.add(rs.getString(1));
		    }
	    }
	    
	    else
	    {
	    	String lambda_term_query = "select subgoal from web_view_table where renamed_view = '"+ name +"'";

			
		    pst = c.prepareStatement(lambda_term_query);
		    
		    ResultSet r = pst.executeQuery();

		    while(r.next())
		    {
		    		    	
		    	table_names.add(r.getString(1));
		    }
	    }
	    

	}
	
	
//	public void gen_index()
//	{
//		index = (char)Integer.parseInt(name.substring(1, name.length()));
//	}
	
	public citation_view_parametered()
	{
		
	}
	
	public citation_view_parametered(String name) throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	        populate_db.usr_name,populate_db.passwd);
	    
		this.name = name;
		gen_lambda_terms(c,pst);
		
		gen_table_names(c, pst);
		
//		gen_index();
			    
	    c.close();
//		get_queries();
	}
	
	public citation_view_parametered(String name, boolean web_view) throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
		this.name = name;
		gen_lambda_terms(web_view, c,pst);
		
		gen_table_names(web_view, c, pst);
		
//		gen_index();
			    
	    c.close();
//		get_queries();
	}
	
	
	public citation_view_parametered(String name, Query view, Tuple tuple, Vector<String> lambda_term_values) throws ClassNotFoundException, SQLException 
	{
		
		this.name = name;
		
		this.lambda_terms = tuple.lambda_terms;
		
		this.view = view;
		
		this.view_tuple = tuple;
		
		put_paramters(lambda_term_values);
		
		set_table_names(tuple);
		
//		gen_index();
			    
//		gen_table_names(c,pst);
//		get_queries();
	}
	 
	void set_table_names(Tuple tuple)
	{
		
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			Subgoal subgoal = (Subgoal)iter.next();
			
			table_names.add(subgoal.name);
		}
		
		table_name_str = table_names.toString();
	}
	
	public citation_view_parametered(String name, Vector<String> lambda_terms, Vector<String> table_names) throws ClassNotFoundException, SQLException {
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		this.name = name;
		
		for(int i = 0; i<lambda_terms.size(); i++)
		{
			this.lambda_terms.add(new Lambda_term(lambda_terms.get(i)));
		}
		
		
//		gen_index();
			    
//		gen_table_names(c,pst);
		
		this.table_names = table_names;
		
	    c.close();
//		get_queries();
	}
	
	public void put_paramters(Vector<String> para)
	{
//		parameters = para;
		
		assert(para.size()==lambda_terms.size());
		
		for(int i = 0; i<lambda_terms.size(); i++)
		{
			map.put(lambda_terms.get(i).toString(), para.get(i));
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
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
	    String head_variables = get_head_variables(c, pst);
	    
	    String body = get_body(c, pst);
	    
	    String query4citation = this.name + "(" + head_variables + "):" + body;
	    
	    Query query = null;//Parse_datalog.parse_query(query4citation);
	    
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
	public String gen_citation_unit(String name, Vector<String> lambda_terms) {
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
		
		for(int i = 0; i<lambda_terms.size();i++)
		{
			str = str + map.get(lambda_terms.get(i).toString());
			
			if(i < lambda_terms.size() - 1)				
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

//	@Override
//	public Vector<String> get_parameters() {
//		// TODO Auto-generated method stub
//		return parameters;
//	}

	@Override
	public Vector<String> get_table_names() {
		// TODO Auto-generated method stub
		return table_names;
	}

	@Override
	public String get_index() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void put_paras(String para, String value) {
		// TODO Auto-generated method stub
		
//		if(this.lambda_terms.contains(para))
//		{
			map.put(para, value);
//		}
		
	}

	
	public void put_lambda_paras(Lambda_term para, String value) {
		// TODO Auto-generated method stub
		
//		if(this.lambda_terms.contains(para))
		{
			map.put(para.toString(), value);
		}
		
	}
	
	@Override
	public boolean has_lambda_term() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public citation_view clone() {
		// TODO Auto-generated method stub
		
		citation_view_parametered c_v = new citation_view_parametered();
		
//		c_v.index = this.index;
		
		c_v.lambda_terms = this.lambda_terms;
		
		c_v.name = this.name;
		
		c_v.map = (HashMap<String, String>) this.map.clone();
		
		c_v.table_names = this.table_names;
		
		c_v.view = this.view;
		
		c_v.view_tuple = this.view_tuple;
		
		return c_v;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		
		citation_view c = (citation_view) o;
		
		if(c.toString().equals(this.toString()))
			return true;
		
		return false;
	}

	@Override
	public String get_table_name_string() {
		// TODO Auto-generated method stub
		return table_name_str;
	}
	

}
