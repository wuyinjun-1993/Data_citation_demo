import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class create_citation_view {
	
	
	public static Vector<String> citation_query = new Vector<String>(); 
	
	public static void init()
	{
		String query_1 = "select cr.surname,cr.first_names from introduction i join contributor2intro using (family_id) join contributor cr using (contributor_id) where family_id=\"x\"|"
				+ "select family.name from family where family_id=\"x\"";
		String query_2 = "select cr.surname,cr.first_names from introduction i join contributor2intro using (family_id) join contributor cr using (contributor_id)|"
				+ "select family.name from family";
		
		String query_3 = "select cr.surname,cr.first_names from contributor2family c join family using (family_id) join contributor cr using (contributor_id) where family_id=\"x\"|"
				+ "select family.name from family where family_id=\"x\"";
		
		citation_query.add(query_1);
		citation_query.add(query_2);
		citation_query.add(query_3);

	}
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		init();
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Vector<Query> views = get_views_schema();	         
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		for(int i=0;i<views.size();i++)
		{
			 String str = "insert into citation_view values('C"+(i+1)+"','v"+(i+1)+"','"+citation_query.get(i)+"')";
	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
	         pst = c.prepareStatement(str);
	         pst.execute();
		}
	}
	
	public static Vector<Query> get_views_schema()
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
	      Vector<Query> views = new Vector<Query>();
	      
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	            "postgres","123");
	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
	         pst = c.prepareStatement("SELECT *  FROM view_table");
	         rs = pst.executeQuery();
	         int num=1;
	         
	            while (rs.next()) {
	            	
	            	String head = rs.getString(1);
	            	
	            	String [] str_lambda_terms = null;
	            	
	            	if(!rs.getString(2).equals(""))
	            	{
	            		str_lambda_terms = rs.getString(2).split(",");
	            	}
	            	
	            	String [] subgoals = rs.getString(3).split("\\),");
	                
	                subgoals[subgoals.length-1]=subgoals[subgoals.length-1].split("\\)")[0];
	            	
	            	
	                Query view = parse_query(head, subgoals,str_lambda_terms);
	                
	                views.add(view);
//	                System.out.print(rs.getString(1));
//	                System.out.print('|');
//	                System.out.print(rs.getString(2));
//	                if(rs.getString(2).length()==0)
//	                {
//	                	int y=0;
//	                	y++;
//	                }
//	                System.out.print('|');
//	                System.out.println(rs.getString(3));
	            }
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      
	      return views;
	}


	static Query parse_query(String head, String []body, String []lambda_term_str)
    {
                
        String head_name=head.split("\\(")[0];
        
        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
        
        Vector<Argument> head_v = new Vector<Argument>();
        
        for(int i=0;i<head_var.length;i++)
        {
        	head_v.add(new Argument(head_var[i]));
        }
        
        Subgoal head_subgoal = new Subgoal(head_name, head_v);
        
//        String []body = query.split(":")[1].split("\\),");
        
        body[body.length-1]=body[body.length-1].split("\\)")[0];
        
        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
        
        for(int i=0; i<body.length; i++)
        {
        	String body_name=body[i].split("\\(")[0];
            
            String []body_var=body[i].split("\\(")[1].split(",");
            
            Vector<Argument> body_v = new Vector<Argument>();
            
            for(int j=0;j<body_var.length;j++)
            {
            	body_v.add(new Argument(body_var[j]));
            }
            
            Subgoal body_subgoal = new Subgoal(body_name, body_v);
            
            body_subgoals.add(body_subgoal);
        }
        
        
        Vector<Argument> lambda_terms = new Vector<Argument>();
        
        if(lambda_term_str != null)
        {
	        for(int i =0;i<lambda_term_str.length; i++)
	        {
	        	lambda_terms.add(new Argument(lambda_term_str[i]));
	        }
        
        }
        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
    }

}
