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
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Pre_processing.populate_db;


public class citation_view_unparametered extends citation_view{
	public String name;
	
//	public boolean lambda;	
	public Vector<String> table_names = new Vector<String>();
	
	public String table_name_str = new String();
	
	public double weight = 0.0;
	
	public Tuple view_tuple = null;
	
	public long[]table_name_index;
	
	public long[]arg_name_index;
	
	public long[] tuple_index;
	
    public String unique_id_string = new String();
	
	public citation_view_unparametered(String name, Tuple tuple)
	{
		this.name = name;
		
		set_table_name(tuple);
	
		this.view_tuple = tuple;
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
		
//		gen_table_names(c, pst);
		
//		gen_index();
	    
//	    c.close();
//		lambda = false;
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
	
	public citation_view_unparametered(String name, Tuple tuple, HashMap<String, Integer> query_subgoal_id_mappings, int query_head_arg_size, HashMap<Tuple, ArrayList<Integer>> view_mapping_query_head_arg_ids_mappings, HashMap<Tuple, Integer> tuple_ids)
    {
        this.name = name;
        
        set_table_name(tuple);
    
        this.view_tuple = tuple;
        
        table_name_index = new long[(query_subgoal_id_mappings.size() + Long.SIZE - 1)/Long.SIZE];
        
        arg_name_index = new long[(query_head_arg_size + Long.SIZE - 1)/Long.SIZE];
        
        tuple_index = new long[(tuple_ids.size() + Long.SIZE - 1)/Long.SIZE];
        
        int curr_tuple_id = tuple_ids.get(tuple);
        
        tuple_index[curr_tuple_id/Long.SIZE] |= (1L << (curr_tuple_id % Long.SIZE)); 
        
        build_table_name_index(tuple, query_subgoal_id_mappings);
        
        build_arg_name_index(tuple, view_mapping_query_head_arg_ids_mappings.get(tuple));
        
        unique_id_string = get_unique_string_id();
//      Connection c = null;
//      
//      PreparedStatement pst = null;
//        
//      Class.forName("org.postgresql.Driver");
//      
//      c = DriverManager
//          .getConnection(populate_db.db_url,
//                  populate_db.usr_name,populate_db.passwd);
        
//      gen_table_names(c, pst);
        
//      gen_index();
        
//      c.close();
//      lambda = false;
    }
	
	void set_table_name(Tuple tuple)
	{
		
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			Subgoal subgoal = (Subgoal)iter.next();
			
			table_names.add(subgoal.name);
		}
		
		table_name_str = table_names.toString();
	}
	
	public citation_view_unparametered(String name, Vector<String> table_names) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	            populate_db.usr_name,populate_db.passwd);
		
//		gen_table_names(c, pst);
	    
	    this.table_names = table_names;
		
//		gen_index();
	    
	    c.close();
//		lambda = false;
	}
	
	public citation_view_unparametered() {
		// TODO Auto-generated constructor stub
	}

	void gen_table_names(Connection c, PreparedStatement pst ) throws ClassNotFoundException, SQLException
	{
		
	    
//	    String lambda_term_query = "select v.subgoal_names from citation_view c join subgoals v on v.view=c.view_name where c.citation_view_name = '"+ name +"'";
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
	
//	public void gen_index()
//	{
//		index = (char)Integer.parseInt(name.substring(1, name.length()));
//	}

	@Override
	public String get_index() {
		// TODO Auto-generated method stub
		return name;
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
		citation_view_unparametered c_v = new citation_view_unparametered();
		
//		c_v.index = this.index;
				
		c_v.name = this.name;
				
		c_v.table_names = this.table_names;
		
		c_v.table_name_str = this.table_name_str;
				
		c_v.view_tuple = this.view_tuple;
		
		c_v.tuple_index = tuple_index;
        
        c_v.table_name_index = table_name_index;
        
        c_v.arg_name_index = arg_name_index;
        
        c_v.unique_id_string = unique_id_string;
		
		return c_v;
	}

	@Override
	public String gen_citation_unit(String name, Vector<String> lambda_terms) {
		// TODO Auto-generated method stub
		return name;
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

	public String get_unique_string_id()
    {
        String string = new String();
        
//    for(int i = 0; i<c_vec.size(); i++)
//    {
//        if(i >= 1)
//            string += init.separator;
//        
//        string += c_vec.get(i).get_name() + c_vec.get(i).get_table_name_string(); 
//    }
        
        
        
        for(int i = 0; i<tuple_index.length; i++)
        {
          
          if(i >= 1)
            string += ",";
          
          string += String.valueOf(tuple_index[i]);
        }
        
        return string;
    }
	
	@Override
	public int hashCode()
	{
//		String string = this.get_name() + init.separator + this.get_table_name_string();
		
		return unique_id_string.hashCode();//string.hashCode();
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
