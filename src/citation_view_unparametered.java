import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class citation_view_unparametered extends citation_view{
	public String name;
	
	public char index;
//	public boolean lambda;
	public Vector<String> queries = new Vector<String>();
	
	public Vector<String> table_names = new Vector<String>();
	
	public citation_view_unparametered(String name) throws ClassNotFoundException, SQLException
	{
		this.name = name;
		
		get_lambda_terms();
		
		gen_index();
//		lambda = false;
	}
	
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
	    	
	    	String[] t_names = rs.getString(2).split(",");
	    	
	    	table_names.addAll(Arrays.asList(t_names));
	    }
	    
	    pst.close();
	    
	    c.close();
	}

	
//	public citation_view_unparametered(String name, String query)
//	{
//		this.name = name;
//		this.query = query.toLowerCase();
////		lambda = false;
//	}
	
	
	//TODO: consider more complicated queries
	
	public Vector<String> get_full_query()
	{
			return queries;
		
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

	@Override
	public Vector<String> get_parameters() {
		// TODO Auto-generated method stub
		return null;
	}

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

}
