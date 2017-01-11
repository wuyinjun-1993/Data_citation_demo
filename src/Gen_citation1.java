import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Gen_citation1 {
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		
		
		
		String query = "q(name):family_c('1',name,type),introduction_c('1',text)";
		
		Vector views = get_views_schema();
		
		HashSet rewritings = GoodPlan.genPlan2("rw",Parse_datalog.parse_query(query), views);
		
		Connection c = null;
		
	    ResultSet rs = null;
	    
	    PreparedStatement pst = null;
	    
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    Vector<Vector<Vector<citation_view>>> c_views = gen_citation(rewritings, c, pst);

	    output(c_views);
	}
	
	public static void output(Vector<Vector<Vector<citation_view>>> c_views)
	{
		for(int i = 0; i<c_views.size(); i++)
		{
			Vector<Vector<citation_view>> c_view_vec = c_views.get(i);
			
			for(int j = 0; j<c_view_vec.size(); j++)
			{
				Vector<citation_view> c_vec = c_view_vec.get(j);
				
				for(int k = 0; k<c_vec.size(); k++)
				{
					if(k >= 1)
						System.out.print("*");
					System.out.print(c_vec.get(k));
				}
				
				System.out.println();
			}
		}
	}
	
//	public static Vector<Vector<citation_view>> gen_citation_combination_template(HashSet rewritings, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
//	{
//		
//		Vector<Vector<citation_view>> c_template = new Vector<Vector<citation_view>>();
//		
//		for(Iterator iter = rewritings.iterator();iter.hasNext();)
//		{
//			Rewriting rw = (Rewriting) iter.next();
//			
//			Vector<citation_view> c_view_model_vec = new Vector<citation_view>();
//			
//			for(Iterator iterator = rw.viewTuples.iterator();iterator.hasNext();)
//			{
//				
//				Tuple view = (Tuple) iterator.next();
//				
//				String get_citation_query = "select citation_view_name,lambda_term from citation_view join view_table on citation_view.view_name = view_table.view where citation_view.view_name = '" + view.name + "'";
//				
//				pst = c.prepareStatement(get_citation_query);
//				
//				ResultSet rs = pst.executeQuery();
//				
//				if(rs.next())
//				{
//					String citation_view_name = rs.getString(1);
//					
//					String lambda_terms = rs.getString(2);
//					
//					citation_view c_unit;
//					
//					if(lambda_terms.isEmpty())
//					{
//						c_unit = new citation_view_unparametered(citation_view_name);
//					}
//					else
//					{
//						Vector<String> lambda_term_vec = new Vector<String>();
//						
//						lambda_term_vec.addAll(Arrays.asList(lambda_terms.split(",")));
//						
//						c_unit = new citation_view_parametered(citation_view_name, lambda_term_vec);
//					}
//					
//					c_view_model_vec.add(c_unit);
//				}
//				
//				
//								
//			}
//			
//			c_template.add(c_view_model_vec);
//		}
//		
//		return c_template;
//	}
//	
	public static Vector<Vector<Vector<citation_view>>> gen_citation(HashSet rewritings, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		Vector<Vector<Vector<citation_view>>> c_view_m = new Vector<Vector<Vector<citation_view>>>();
			
		for(Iterator iter = rewritings.iterator();iter.hasNext();)
		{
			Rewriting rw = (Rewriting) iter.next();
			
			String citation_unit = new String();
			
			String citation_table = new String();
						
			int num = 0;
			
//			Query_converter.post_processing_view(rw.rew);
			
			Query_converter.pre_processing_view(rw.rew);
			
			String sql = Query_converter.datalog2sql_citation_view(rw.rew);
			
//			for(Iterator iterator = rw.viewTuples.iterator();iterator.hasNext();)
//			{
//				Tuple view = (Tuple) iterator.next();
//				
//				rename_column(view, c, pst);
//				
//				if(num >= 1)
//				{
//					citation_unit += ",";
//					
//					citation_table += " natural inner join ";
//				}
//				
//				citation_unit += view.name + "_citation_view";
//				
//				citation_table += view.name + "_table";
//				
//				num++;
//								
//			}
//			
//			sql = "select " + citation_unit + " from " + citation_table;
			
			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			Vector<Vector<citation_view>> c_view_vec = gen_citation_view_vec(rs, rw.rew.body.size());
			
			c_view_m.add(c_view_vec);
			
			Query_converter.post_processing_view(rw.rew);
		}
		
		return c_view_m;
	}
	
	
	public static Vector<Vector<citation_view>> gen_citation_view_vec(ResultSet rs, int num) throws SQLException, ClassNotFoundException
	{
		
		Vector<Vector<citation_view>> c_view_vec = new Vector<Vector<citation_view>>();
		
		while(rs.next())
		{
			Vector<citation_view> c_view = new Vector<citation_view>();
			
			for(int i = 0; i<num; i++)
			{
				String c_str = rs.getString(i+1);
				
				if(c_str.contains("(") && c_str.contains(")"))
				{
					String c_name = c_str.split("\\(")[0];
					
					String[] c_paras = c_str.split("\\(")[1].split("\\)")[0].split(",");
					
					citation_view_parametered c = new citation_view_parametered(c_name);
					
					c.put_paramters(new Vector<String>(Arrays.asList(c_paras)));
					
					c_view.add(c);
									
				}
				
				else
				{
					c_view.add(new citation_view_unparametered(c_str));
				}
			}
			
			c_view_vec.add(c_view);
		}
		
		return c_view_vec;
	}
	
	public static void rename_column(Tuple view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +"_table rename column citation_view to "+ view.name + "_citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
	public static void rename_column_back(Tuple view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +"_table rename column "+ view.name +"_citation_view to citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
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
    static Query parse_query(String query)
    {
        
        String head = query.split(":")[0];
        
        String head_name=head.split("\\(")[0];
        
        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
        
        Vector<Argument> head_v = new Vector<Argument>();
        
        for(int i=0;i<head_var.length;i++)
        {
        	head_v.add(new Argument(head_var[i]));
        }
        
        Subgoal head_subgoal = new Subgoal(head_name, head_v);
        
        String []body = query.split(":")[1].split("\\),");
        
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
        return new Query(head_name, head_subgoal, body_subgoals);
    }
	
//	public static Query gen_query()
//	{
//		
//	}

}
