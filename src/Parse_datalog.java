import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Parse_datalog {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		String query = "q(name):family_c(f,name,'gpcr'),introduction_c(f,text)";
		
		Query q = parse_query(query);
		
		Subgoal subgoal = (Subgoal)q.body.get(0);
		
		Argument arg = (Argument) subgoal.args.get(0);
		
		arg = new Argument("'2'","family_id");
		
		System.out.println(q);
	}
	
	
    public static Query parse_query(String query) throws ClassNotFoundException, SQLException
    {
        HashMap<String, Argument> args = new HashMap<String, Argument>();
    	
        String head = query.split(":")[0];
        
        String head_name=head.split("\\(")[0].trim();
        
        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
        
        Vector<Argument> head_v = new Vector<Argument>();
        
        for(int i=0;i<head_var.length;i++)
        {
        	Argument arg = new Argument(head_var[i].trim());
        	
        	head_v.add(arg);
        	
        	args.put(head_var[i].trim(), arg);
        }
        
        Subgoal head_subgoal = new Subgoal(head_name, head_v);
        
        String []body = query.split(":")[1].split("\\),");
        
        body[body.length-1]=body[body.length-1].split("\\)")[0];
        
        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
                
        for(int i=0; i<body.length; i++)
        {
        	String body_name=body[i].split("\\(")[0].trim();
        	
        	Vector<String> col_names = get_columns(body_name);
            
            String []body_var=body[i].split("\\(")[1].split(",");
            
            Vector<Argument> body_v = new Vector<Argument>();
            
            for(int j=0;j<body_var.length;j++)
            {
            	if(args.get(body_var[j]) == null)
            	{
            		if(body_var[j].contains("'"))
	            	{
	            		body_v.add(new Argument(body_var[j].trim(), col_names.get(j)));
	            	}
	            	else
	            		body_v.add(new Argument(body_var[j].trim()));
            	}
            	else
            		body_v.add(args.get(body_var[j].trim()));
            }
            
            Subgoal body_subgoal = new Subgoal(body_name, body_v);
            
            body_subgoals.add(body_subgoal);
        }
        return new Query(head_name.trim(), head_subgoal, body_subgoals);
    }
    
    static Vector<String> get_columns(String body_name) throws ClassNotFoundException, SQLException
    {
    	String col_query = "select column_name FROM information_schema.columns where table_name = '" + body_name + "'";
    	
    	Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    pst = c.prepareStatement(col_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    Vector<String> col_names = new Vector<String>();
	    
	    while(rs.next())
	    {
	    	col_names.add(rs.getString(1));
	    }
	    
	    col_names.remove(col_names.size() - 1);
	    
	    c.close();
	    
	    return col_names;
    }

}
