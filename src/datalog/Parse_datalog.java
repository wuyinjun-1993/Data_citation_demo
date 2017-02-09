package datalog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import Corecover.Argument;
import Corecover.Query;
import Corecover.Subgoal;
import Operation.Conditions;
import Pre_processing.populate_db;

public class Parse_datalog {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		String query = "q(name):family_c(f1,name,type), introduction_c(f2,text), f1 = f2";
		
		Query q = parse_query(query);
		
//		Subgoal subgoal = (Subgoal)q.body.get(0);
//		
//		Argument arg = (Argument) subgoal.args.get(0);
//		
//		arg = new Argument("'2'","family_id");
		
		System.out.println(q);
	}
	
	
    public static Query parse_query(String query) throws ClassNotFoundException, SQLException
    {
//        HashMap<String, Argument> args = new HashMap<String, Argument>();
        
        HashMap<String, String> orig_arg_names = new HashMap<String, String>();
    	
        String head = query.split(":")[0];
        
        String head_name=head.split("\\(")[0].trim();
        
        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
        
        Vector<Argument> head_v = new Vector<Argument>();
        

        
        String []body = query.split(":")[1].split("\\),");
        
        String []conditions = null;
        
        int lenth = 0;
        
        if(body[body.length - 1].contains("("))
        {
        	body[body.length-1]=body[body.length-1].split("\\)")[0];
        	lenth = body.length;
        }
        else
        {
        	conditions = body[body.length - 1].split(",");
        	
        	lenth = body.length - 1;
        }
        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
        
        
        
        for(int i=0; i<lenth; i++)
        {
        	String body_name=body[i].split("\\(")[0].trim();
        	
        	Vector<String> col_names = get_columns(body_name);
                    
            String []body_var=body[i].split("\\(")[1].split(",");
            
            Vector<Argument> body_v = new Vector<Argument>();
            
            for(int j=0;j<body_var.length;j++)
            {
//            	if(args.get(body_var[j]) == null)
            	{
            		if(body_var[j].contains("'"))
	            	{
            			orig_arg_names.put(body_var[j].trim(), col_names.get(j));
	            		body_v.add(new Argument(body_var[j].trim(), col_names.get(j)));
	            	}
	            	else
	            	{
	            		orig_arg_names.put(body_var[j].trim(), col_names.get(j));
	            		body_v.add(new Argument(body_var[j].trim(), col_names.get(j)));
	            	}
            	}
//            	else
//            	{
//            		args.get(body_var[j].trim()).origin_name = col_names.get(j);
//            		
//            		body_v.add(args.get(body_var[j].trim()));
//
//            	}
            }
            
            Subgoal body_subgoal = new Subgoal(body_name, body_v);
            
            body_subgoals.add(body_subgoal);
        }
        
        
        Vector<Conditions> condition_vec = new Vector<Conditions>();
        
        if(conditions != null)
        for(int i = 0; i<conditions.length; i++)
        {
        	condition_vec.add(Conditions.parse(conditions[i].trim(), body_subgoals, orig_arg_names));
        }
        
        for(int i=0;i<head_var.length;i++)
        {
        	Argument arg = new Argument(head_var[i].trim());
        	
        	head_v.add(arg);
        	
//        	args.put(head_var[i].trim(), arg);
        }
        
        Subgoal head_subgoal = new Subgoal(head_name, head_v);
        
        
        return new Query(head_name.trim(), head_subgoal, body_subgoals, new Vector<Argument>(), condition_vec);
    }
    
    public static Vector<String> get_columns(String body_name) throws ClassNotFoundException, SQLException
    {
    	String col_query = "select column_name FROM information_schema.columns where table_name = '" + body_name + "'";
    	
    	Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
	        "postgres","123");
	    
	    pst = c.prepareStatement(col_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    Vector<String> col_names = new Vector<String>();
	    
	    while(rs.next())
	    {
	    	col_names.add(rs.getString(1));
	    }
	    
	    if(col_names.contains("citation_view"))
	    	col_names.remove("citation_view");
	    if(col_names.contains("provenance"))
	    	col_names.remove("provenance");
	    
	    
	    c.close();
	    
	    return col_names;
    }

}
