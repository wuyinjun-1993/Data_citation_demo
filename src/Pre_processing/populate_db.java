package Pre_processing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import Corecover.Argument;
import Corecover.Query;
import Corecover.Subgoal;
import citation_view.*;
import schema_reasoning.Gen_citation1;

public class populate_db {
	
	
	public static String separator = "|";
	
	public static String db_name = "IUPHAR";
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
		add_column(c,pst);
		populate_db(c, pst);
	}
	
	public static HashSet<String> get_table_name(Vector<Query> views)
	{
		HashSet<String> subgoals = new HashSet<String>();
		for(int i = 0; i<views.size(); i++)
		{
			Query view =views.get(i);
			for(int j = 0; j <view.body.size(); j++)
			{
				Subgoal subgoal = (Subgoal) view.body.get(j);
				subgoals.add(subgoal.name);
			}
		}
		
		return subgoals;
	}
	
	public static void add_column(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		HashSet<String> table_names = get_table_name(Gen_citation1.get_views_schema());
	      ResultSet rs = null;

		for(Iterator<String> iter = table_names.iterator();iter.hasNext();)
		{
			String table_name = iter.next();
			String test_col = "SELECT column_name FROM information_schema.columns WHERE table_name='" + table_name + "' and column_name='citation_view'";
		    pst = c.prepareStatement(test_col);
		    rs = pst.executeQuery();
		    
		    if(rs.next())
		    {
		    	if(rs.getRow()!=0)
		    	{
		    		String remove_sql = "alter table "+ table_name +" drop column citation_view";
		    		
		    		pst = c.prepareStatement(remove_sql);
		    		
		    		pst.execute();
		    	}
		    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN citation_view text";
	    	    pst = c.prepareStatement(add_col);
	    	    pst.execute();
		    }
		    else
		    {
		    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN citation_view text";
	    	    pst = c.prepareStatement(add_col);
	    	    pst.execute();
		    }
			
		}
	    
	 	Vector<Query> views = Gen_citation1.get_views_schema();
	 	
	 	for(int i = 0; i<views.size(); i++)
	 	{
	 		add_column_view(views.get(i), c, pst);
	 	}
	 	
	}
	
	public static void add_column_view(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
	    String test_col = "SELECT table_name FROM information_schema.columns WHERE table_name='"+view.name + "_table'";
	    
	    pst = c.prepareStatement(test_col);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	if(rs.getRow()!=0)
	    	{
	    		String update_table = "drop table " + view.name + "_table";
	    		
	    	    pst = c.prepareStatement(update_table);

	    	    pst.execute();
	    	}
	    }
		
		String create_view_table_sql = "create table "+ view.name + "_table as " + "select * from " + view.name;
		
	    pst = c.prepareStatement(create_view_table_sql);
	    
	    pst.execute();
	    
	    String add_column_sql = "alter table " + view.name + "_table add column citation_view text";
	    
	    pst = c.prepareStatement(add_column_sql);
	    
	    pst.execute();
	    
	}

	public static void populate_db(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
	 	Vector<Query> views = Gen_citation1.get_views_schema();
		
		for(int i = 0; i<views.size();i++)
		{
			Query view = views.get(i);
			
			citation_view citation_view = get_citation_view(view);
			
			gen_citation_view_query(citation_view,view, c, pst);
		}
	      
	    
	}
	
	
	public static void gen_citation_view_query(citation_view citation_views, Query view, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
//		String citation_view_query = new String();
		
		ResultSet rs = get_citation(view.name + "_table",view.lambda_term);
		
		populate(rs,citation_views,view.lambda_term,view.name + "_table",c,pst);
		
		assign_view_query(view, c, pst);
		
	}
	
	public static void assign_view_query(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Vector<Argument>> lambda_terms = new Vector<Vector<Argument>>();
		
		
		for(int i = 0; i<view.body.size();i++)
		{
			
			
			String lambda_term_sql = new String();

			
			Subgoal subgoal = (Subgoal) view.body.get(i);
			
			if(subgoal.contains(view.head.args));
			{
				Vector<Argument> args = new Vector<Argument>();//get_lambda_terms(view.lambda_term, subgoal);

				
				for(int j = 0; j<subgoal.args.size(); j++)
				{
					lambda_term_sql += "," + subgoal.args.get(j).toString();
					
					args.add((Argument) subgoal.args.get(j));
				}

			
			
			String citation_view_query = "select distinct citation_view" + lambda_term_sql + " from "+ view.name + "_table";
			
			pst = c.prepareStatement(citation_view_query);
		
			
		    ResultSet rs = pst.executeQuery();
		    
		    
		    while(rs.next())
		    {
		    	String citation_view = rs.getString(1);
		    	
		    	int pointer = 1;
		    	  		
		    		String key_sql = new String();
		    		
		    		String update_citation_view_sql = new String();
		    		
		    		for(int j = 0; j<args.size(); j++)
		    		{
						if(j >= 1)
							key_sql = key_sql + " and ";
						
						String item = rs.getString(++pointer);
						
						if(item.contains("'"))
							item = item.replaceAll("'", "''");
						
		    			key_sql = key_sql + args.get(j).name + "='" + item + "'";
		    		}

		    		String curr_citation_view_sql = "select "+ "citation_view from " + subgoal.name + " where " + key_sql;
		    			    		
		    		pst = c.prepareStatement(curr_citation_view_sql);
		    			    		
		    		ResultSet temp_rs = pst.executeQuery();
		    		
		    		

		    		while(temp_rs.next())
		    		{
		    			
		    			String primary_key_value = temp_rs.getString(1);
		    			
		    			String update_sql = key_sql;//primary_key + "='" + primary_key_value +"'"; 
		    			
	    				String curr_citation_view = temp_rs.getString(1);

		    			if(curr_citation_view==null)
		    			{
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}
		    			else
		    			{
		    				
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + curr_citation_view + separator + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}

		    		}
		    		
		    					    
		    	}
				
			}

	    }
	}
	
	public static Vector<Argument> get_lambda_terms(Vector<Argument> lambda_term, Subgoal subgoal)
	{
		Vector<Argument> lambda_terms = new Vector<Argument>();
		for(int i =0; i<lambda_term.size();i++)
		{
			if(subgoal.contains(lambda_term.get(i)))
			{
				lambda_terms.add(lambda_term.get(i));
			}
		}
		
		return lambda_terms;
	}
	
	
	
	public static void populate(ResultSet rs, citation_view citation_views, Vector<Argument> lambda_terms , String table, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		if(!lambda_terms.isEmpty())
		{
			while(rs.next())
			{
				int num = lambda_terms.size();
				String sql = new String();
				Vector<String> paras = new Vector<String>();
				
				for(int i =0;i<num;i++)
				{
					String lambda_term = rs.getString(i+1);
					if(i >= 1)
						sql = sql + " and ";
					sql = sql + lambda_terms.get(i) + "='" + lambda_term + "'";		
					paras.add(lambda_term);
					
				}
							
				String insert_citation_view = citation_views.gen_citation_unit(paras);
				
				String curr_citation_view = rs.getString(num+1);
				if(curr_citation_view!=null)
				{
					curr_citation_view = curr_citation_view + "," + insert_citation_view;
				}
				else
				{
					curr_citation_view = insert_citation_view;
				}
				
				
				String update_sql = "update " + table + " set citation_view='" + curr_citation_view + "' where " + sql;
			    pst = c.prepareStatement(update_sql);
			    pst.execute();
			}
		}
		else
		{
			String update_sql = "update " + table + " set citation_view='" + citation_views.get_name() + "'";
		    pst = c.prepareStatement(update_sql);
		    pst.execute();
		}
	}
	
	public static citation_view get_citation_view(Query view) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
	    String test_col = "SELECT citation_view.citation_view_name FROM citation_view WHERE citation_view.view_name='"+view.name+"'";
	    pst = c.prepareStatement(test_col);
	    rs = pst.executeQuery();
	    
	    String citation_view_name = new String();
	    
	    Vector<String> lambda_terms = new Vector<String>();
//	    Vector<String> lambda_term_vec = new Vector<String>(); 
	    
	    if(rs.next())
	    {
	    	citation_view_name = rs.getString(1);
	    }
	    
	    String lambda_term_sql = "select lambda_term from view_table where view = '" + view.name + "'";
	    pst = c.prepareStatement(lambda_term_sql);
	    
	    rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	if(rs.getString(1) != null && rs.getString(1).length()!=0)
	    	{
	    		lambda_terms.addAll(Arrays.asList(rs.getString(1).split(",")));
	    		
//	    		System.out.println(lambda_terms);
	    		
	    		return new citation_view_parametered(citation_view_name, lambda_terms);
	    	}
	    	else
	    		return new citation_view_unparametered(citation_view_name);
	    }
	    
	    
	    return null;
	}
	
	
	public static ResultSet get_citation(String table, Vector<Argument> lambda_term) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
	    String lambda_term_string = lambda_term.toString();
	    
	    lambda_term_string = lambda_term_string.substring(1,lambda_term_string.length() - 1);
	    
    	Vector <Argument> t = new Vector<Argument>();

//	    
//	    t.add(new Argument("q",false));
//	    t.add(new Argument("r",false));
//	    
//	    
		    String t_str = t.toString();
	    if(!lambda_term.isEmpty())
	    {
	    

		    String test_col = "SELECT "+lambda_term_string+",citation_view FROM "+table;
		    pst = c.prepareStatement(test_col);
		    rs = pst.executeQuery();
	    }
	    else
	    {
//	    	String q_primary_key = "select kc.column_name from information_schema.table_constraints tc,information_schema.key_column_usage kc where tc.constraint_type = 'PRIMARY KEY' and kc.table_name = tc.table_name and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name and tc.table_name='"+ table +"'";
//	    	pst = c.prepareStatement(q_primary_key);
//		    rs = pst.executeQuery();
//	    	
//		    String primary_key = new String();
//		    if(rs.next())
//		    	primary_key = rs.getString(1);
//	    	
//		    lambda_term.clear();
//		    lambda_term.add(new Argument(primary_key));
	    	String test_col = "SELECT distinct citation_view FROM "+table;
		    pst = c.prepareStatement(test_col);
		    rs = pst.executeQuery();
	    }
	    return rs;
	}

//	public static Vector<Query> get_views_schema(Connection c, PreparedStatement pst)
//	{
//	      ResultSet rs = null;
//	      Vector<Query> views = new Vector<Query>();
//	      
//	      try {
//	         
//	         
////	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
//	         pst = c.prepareStatement("SELECT *  FROM view_table");
//	         rs = pst.executeQuery();
//	         int num=1;
//	         
//	            while (rs.next()) {
//	            	
//	            	String head = rs.getString(1);
//	            	
//	            	String [] str_lambda_terms = null;
//	            	
//	            	if(!rs.getString(2).equals(""))
//	            	{
//	            		str_lambda_terms = rs.getString(2).split(",");
//	            	}
//	            	
//	            	String [] subgoals = rs.getString(3).split("\\),");
//	                
//	                subgoals[subgoals.length-1]=subgoals[subgoals.length-1].split("\\)")[0];
//	            	
//	            	
//	                Query view = parse_query(head, subgoals,str_lambda_terms);
//	                
//	                views.add(view);
////	                System.out.print(rs.getString(1));
////	                System.out.print('|');
////	                System.out.print(rs.getString(2));
////	                if(rs.getString(2).length()==0)
////	                {
////	                	int y=0;
////	                	y++;
////	                }
////	                System.out.print('|');
////	                System.out.println(rs.getString(3));
//	            }
//	         
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	         System.err.println(e.getClass().getName()+": "+e.getMessage());
//	         System.exit(0);
//	      }
//	      
//	      return views;
//	}
//	
//	static Query parse_query(String head, String []body, String []lambda_term_str)
//    {
//                
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
////        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        
//        
//        Vector<Argument> lambda_terms = new Vector<Argument>();
//        
//        if(lambda_term_str != null)
//        {
//	        for(int i =0;i<lambda_term_str.length; i++)
//	        {
//	        	lambda_terms.add(new Argument(lambda_term_str[i]));
//	        }
//        
//        }
//        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
//    }


}
