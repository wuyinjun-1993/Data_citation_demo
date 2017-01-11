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
	
	public Vector<Sql> queries = new Vector<Sql>();
	
	public Vector<String> parameters = new Vector<String>();
	
	HashMap<String, String> map = new HashMap<String, String>();
	
	public Vector<String> table_names = new Vector<String>();
	
	public char index;
	
	void get_lambda_terms() throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String lambda_term_query = "select v.lambda_term, v.subgoal_names from citation_view c join view_table v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(lambda_term_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    while(rs.next())
	    {
	    	String []lambda_term_strs = rs.getString(1).split(",");
	    	
	    	for(int i = 0; i<lambda_term_strs.length; i++)
	    	{
	    		lambda_terms.add(lambda_term_strs[i]);
	    	}
	    	
	    	String[] t_names = rs.getString(2).split(",");
	    	
	    	table_names.addAll(Arrays.asList(t_names));
	    }
	    
	    pst.close();
	    
	    c.close();
	}
	
	void get_queries() throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String citation_view_query = "select c.citation_view_query from citation_view c where c.citation_view_name = '"+ name +"'";
	    
	    pst = c.prepareStatement(citation_view_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	String [] query_strs = rs.getString(1).split("\\" + populate_db.separator);
	    	
	    	for(int i = 0; i<query_strs.length; i++)
	    	{
	    		if(query_strs[i].contains("(") && query_strs[i].contains(")"))
	    		{
	    			String q_name = query_strs[i].split("\\(")[0];
	    			
	    			Vector<String> q_lambda_terms = new Vector<String>();
	    			
	    			q_lambda_terms.addAll(Arrays.asList(query_strs[i].split("\\(")[1].split("\\")[0]));
	    			
	    			queries.add(new Sql_parametered(q_name, q_lambda_terms));
	    		}
	    		
	    		else
	    		{
	    			queries.add(new Sql_unparametered(query_strs[i]));
	    		}
	    	}
	    	
	    	
	    }
	}
	
	public void gen_index()
	{
		index = (char)Integer.parseInt(name.substring(1, name.length()));
	}
	
	public citation_view_parametered(String name) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		get_lambda_terms();
		gen_index();
//		get_queries();
	}
	
	
	public citation_view_parametered(String name, Vector<String> lambda_terms) throws ClassNotFoundException, SQLException {
		this.name = name;
		this.lambda_terms = lambda_terms;
		gen_index();
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
	
	
	@Override
	public Vector<String> get_full_query()
	{
//		if(map.length == 0)
//			return null;
//		else
		
//			String [] queries = query.split("|");
			String new_query = null;
			
			Vector<String> rs_queries = new Vector<String>();
			
			
			for(int i=0;i<queries.size();i++)
			{
				String new_sub_query = null;
				String [] sub_terms = queries.get(i).sql.split(" ");
				for(int j = 0; j<sub_terms.length; j++)
				{
					if(sub_terms[j].contains("="))
					{
						
						
						String key = sub_terms[j].split("=")[0];
						if(key.contains("."))
							key=key.split(".")[1];
						String variable = map.get(key);
						sub_terms[j] = sub_terms[j].replace("x", variable);
					}
					if(j!=0)
					new_sub_query = new_sub_query + " " + sub_terms[j];
					else
						new_sub_query = sub_terms[j];
				}
				
				
				
				rs_queries.add(new_sub_query);
	//			if(sub_terms.length <= 1)
	//				continue;
	//			else
	//			{
	//				String[] terms = sub_terms[1].split("and|or");
	//				
	//				for(int j = 0;j<terms.length;j++)
	//				{
	//					String []assign_v = terms[j].split("=");
	//					String variable = map.get(assign_v[0]);
	//					String new_assign_v = assign_v[1].replace("x", variable);
	//					
	//				}
	//				 
	//			}
			
			
//			for(int i = 0; i<queries.size(); i++)
//			{
//				if(i!=0)
//					new_query = new_query + "|" + queries[i];
//				else
//					new_query = queries[i];
//			}
//			
//			return new_query;
		}
			
			
		return rs_queries;
//		else
//			return query;
		
	}

	@Override
	public String gen_citation_unit(Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		
		String insert_citation_view = name + "(";
		int num = lambda_terms.size();
		String sql = new String();
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
