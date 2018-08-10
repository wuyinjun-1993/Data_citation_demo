package edu.upenn.cis.citation.citation_view1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.*;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.*;

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
	
	public long[] table_name_index;
	
	public long[] arg_name_index;
	
	public long[] tuple_index;
		
	public double weight = 0.0;
	
	public String unique_id_string = new String();
	
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
		
		Vector<Conditions> conditions = tuple.conditions;
		
		put_paramters(lambda_term_values);
		
		set_table_names(tuple);
		
//		gen_index();
			    
//		gen_table_names(c,pst);
//		get_queries();
	}
	
	public citation_view_parametered(String name, Query view, Tuple tuple)
	{
		
		this.name = name;
		
		this.lambda_terms = tuple.lambda_terms;
		
		this.view = view;
		
		this.view_tuple = tuple;
		
		Vector<Conditions> conditions = tuple.conditions;
				
		set_table_names(tuple);
		
//		gen_index();
			    
//		gen_table_names(c,pst);
//		get_queries();
	}
	
	
	void build_table_name_index(Tuple tuple, HashMap<String, Integer> query_subgoal_id_mappings)
	{
	  HashSet<String> target_subgoal_strs = tuple.getTargetSubgoal_strs();
	  
	  for(String target_str: target_subgoal_strs)
	  {
	    int id = query_subgoal_id_mappings.get(target_str);
	    
	    table_name_index[id/Long.SIZE] |= (1L << (id % Long.SIZE));
	  }
	  
	}
	
	void build_arg_name_index(Tuple tuple, ArrayList<Integer> query_arg_ids)
    {      
//      Vector<Argument> head_args = tuple.args;
      
      for(Integer query_arg_id: query_arg_ids)
      {
//        int id = query_arg_id_mappings.get(arg);
        
        arg_name_index[query_arg_id/Long.SIZE] |= (1L << (query_arg_id));
      }
      
    }
	
	public citation_view_parametered(String name, Query view, Tuple tuple, HashMap<String, Integer> query_subgoal_id_mappings, int query_head_arg_size, HashMap<Tuple, ArrayList<Integer>> view_mapping_query_arg_ids_mappings, HashMap<Tuple, Integer> tuple_ids)
    {
        
        this.name = name;
        
        this.lambda_terms = tuple.lambda_terms;
        
        this.view = view;
        
        this.view_tuple = tuple;
        
        set_table_names(tuple);
        
        table_name_index = new long[(query_subgoal_id_mappings.size() + Long.SIZE - 1)/Long.SIZE];
        
        arg_name_index = new long[(query_head_arg_size + Long.SIZE - 1)/Long.SIZE];
        
        tuple_index = new long[(tuple_ids.size() + Long.SIZE - 1)/Long.SIZE];
     
        int curr_tuple_id = tuple_ids.get(tuple);
        
        tuple_index[curr_tuple_id/Long.SIZE] |= (1L << (curr_tuple_id % Long.SIZE)); 
        
        build_table_name_index(tuple, query_subgoal_id_mappings);
        
        build_arg_name_index(tuple, view_mapping_query_arg_ids_mappings.get(tuple));
        
        
        unique_id_string = get_unique_string_id();
//      gen_index();
                
//      gen_table_names(c,pst);
//      get_queries();
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
		
		c_v.table_name_str = this.table_name_str;
		
		c_v.view = this.view;
		
		c_v.view_tuple = this.view_tuple;
		
		c_v.tuple_index = tuple_index;
		
		c_v.table_name_index = table_name_index;
		
		c_v.arg_name_index = arg_name_index;
		
		c_v.unique_id_string = unique_id_string;
		
		return c_v;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		
		citation_view c = (citation_view) o;
		
		if(c.hashCode() == this.hashCode())
			return true;
		
		return false;
	}

	@Override
	public String get_table_name_string() {
		// TODO Auto-generated method stub
		return table_name_str;
	}

	@Override
	public double get_weight_value() {
		// TODO Auto-generated method stub
		return weight;
	}

	@Override
	public void calculate_weight(Query query) {
		// TODO Auto-generated method stub
		this.weight = this.table_names.size() * 1.0/query.body.size();
	}

	@Override
	public Tuple get_view_tuple() {
		// TODO Auto-generated method stub
		return view_tuple;
	}
	
	@Override
	public int hashCode()
	{
//		String string = this.get_name() + init.separator + this.get_table_name_string();
		
		return unique_id_string.hashCode();//string.hashCode();
	}

	   public String get_unique_string_id()
	    {
	        String string = new String();
	        
//	      for(int i = 0; i<c_vec.size(); i++)
//	      {
//	          if(i >= 1)
//	              string += init.separator;
//	          
//	          string += c_vec.get(i).get_name() + c_vec.get(i).get_table_name_string(); 
//	      }
	        
	        
	        
	        for(int i = 0; i<tuple_index.length; i++)
	        {
	          
	          if(i >= 1)
	            string += ",";
	          
	          string += String.valueOf(tuple_index[i]);
	        }
	        
	        return string;
	    }
	
  @Override
  public long[] get_mapped_table_name_index() {
    // TODO Auto-generated method stub
    return table_name_index;
  }

  @Override
  public long[] get_mapped_head_var_index() {
    // TODO Auto-generated method stub
    return arg_name_index;
  }

  @Override
  public long[] get_tuple_index() {
    // TODO Auto-generated method stub
    return tuple_index;
  }
	

}
