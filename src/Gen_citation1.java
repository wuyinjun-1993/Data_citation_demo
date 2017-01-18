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

import java_cup.assoc;
import jdk.nashorn.internal.AssertsEnabled;

public class Gen_citation1 {
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
				
				
		String query = "q(name):family_c(f,name,type), introduction_c(f,text)";
		
		Vector<Vector<Vector<citation_view>>> c_views = gen_citation_main(query);
		
		
		
	    output(c_views);
		
//		compare(query);
	}
	
	public static void compare(String query) throws ClassNotFoundException, SQLException
	{		
		Vector<Vector<Vector<citation_view>>> c1 = gen_citation_main(query);
		
		Vector<Vector<citation_view_vector>> c2 = Gen_citation2.gen_citation_main(query);
		
		
		if(c1.get(0).size()!=c2.size())
			throw new IllegalArgumentException("two citation not equal");
				
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		for(int i = 0; i<c1.size(); i++)
		{
			Vector<Vector<citation_view>> c1_1 = c1.get(i);
			
			String index_c = get_index(c1_1.get(0));
			
			Vector<citation_view_vector> c2_1 = c2.get(0);
			
			for(int j = 0; j<c2_1.size(); j++)
			{
				citation_view_vector c2_1_1 = c2_1.get(j);
				
				String index = get_index(c2_1_1);
				
				
				if(index.equals(index_c))
				{
					
					
					
					
					map.put(i, j);
					break;
				}
			}
		}
		
		
		for(int i = 0; i<c1.size(); i++)
		{
			
			Vector<Vector<citation_view>> c1_1 = c1.get(i);
			
			int id = map.get(i);
			
			for(int j = 0; j<c2.size(); j++)
			{
				citation_view_vector c2_entry = c2.get(j).get(id);
				
				if(vec_contains(c1_1,c2_entry))
					continue;
				else
					throw new IllegalArgumentException("two citation not equal");
			}
			
		}
		
	}
	
	public static boolean vec_contains(Vector<Vector<citation_view>> c1, citation_view_vector c2)
	{
		int i = 0;
		
		Vector<citation_view> c2_entry = c2.c_vec;
		
		for(i = 0; i<c1.size(); i++)
		{
			Vector<citation_view> c1_entry = c1.get(i);
			
			HashMap<String, citation_view> map = new HashMap<String, citation_view>();
			
			for(int k = 0; k<c2_entry.size(); k++)
			{
				map.put(c2_entry.get(k).toString(), c2_entry.get(k));
			}
			
			int k = 0;
			
			for(k = 0; k<c1_entry.size(); k++)
			{
				if(map.get(c1_entry.get(k).toString()) == null)
				{
					break;
				}
			}
			
			if(k < c1_entry.size())
				continue;
			else
				break;
		}
		
		if(i < c1.size())
			return true;
		else
			return false;
	}
	
	public static String get_index(Vector<citation_view> c_combination)
	{		
		char[] seq = new char[c_combination.size()];
		
		for(int j = 0; j<c_combination.size();j++)
		{
			seq[j]= (c_combination.get(j).get_index());
		}
		
		Arrays.sort(seq);
		
		String str = String.valueOf(seq);
		
		return str;
	}
	
	public static String get_index(citation_view_vector c_combination)
	{		
		char[] seq = new char[c_combination.index_vec.size()];
		
		for(int j = 0; j<c_combination.index_vec.size();j++)
		{
			seq[j]= (c_combination.index_vec.get(j));
		}
		
		Arrays.sort(seq);
		
		String str = String.valueOf(seq);
		
		return str;
	}
	
	public static HashMap<String, Vector<citation_view>> build_index_vec(Vector<Vector<citation_view>> c_combinations)
	{
		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		for(int i = 0; i<c_combinations.size(); i++)
		{
			Vector<citation_view> c_combination = c_combinations.get(i);
			
			char[] seq = new char[c_combination.size()];
			
			for(int j = 0; j<c_combination.size();j++)
			{
				seq[j]= (c_combination.get(j).get_index());
			}
			
			Arrays.sort(seq);
			
			String str = String.valueOf(seq);
			
			update_combinations.put(str, c_combination);
		}
		
		return update_combinations;
	}
	
	public static Vector<Vector<Vector<citation_view>>> gen_citation_main(String query) throws ClassNotFoundException, SQLException
	{
		Vector views = get_views_schema();
		
		HashSet rewritings = GoodPlan.genPlan2(Parse_datalog.parse_query(query), views);
		
		
		
		Connection c = null;
		
	    ResultSet rs = null;
	    
	    PreparedStatement pst = null;
	    
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    Vector<Vector<Vector<citation_view>>> c_views = gen_citation(rewritings, c, pst);

	    return c_views;
			    
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
			
//			Query_converter.pre_processing_view(rw.rew);
			
			String sql = Query_converter.datalog2sql_citation(rw.rew, true);
			
			pst = c.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();
			
			Vector<Vector<citation_view>> c_view_vec = gen_citation_view_vec(rs, rw.rew.body.size());
			
			c_view_m.add(c_view_vec);
			
//			Query_converter.post_processing_view(rw.rew);
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
	         
	            while (rs.next()) {
	            	
	            	String head_name = rs.getString(1);
	            	
	            	String [] str_lambda_terms = null;
	            	
	            	String [] head_vars = rs.getString(2).split(",");
	            	
	            	
	            	if(rs.getString(3) != null && !rs.getString(3).equals(""))
	            	{
	            		str_lambda_terms = rs.getString(3).split(",");
	            	}
	            		            	
	            	Vector<String> subgoal_names = new Vector<String>();
	            	
	            	Vector<String> subgoal_arguments = new Vector<String>();
	            	
	            	get_subgoals(subgoal_names, subgoal_arguments, head_name, c, pst);	            	
	            	
	                Query view = parse_query(head_name, head_vars, subgoal_names, subgoal_arguments, str_lambda_terms);
	                
	                views.add(view);
	            }
	  	      c.close();

	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      
	      return views;
	}
	
	static void get_subgoals (Vector<String> subgoal_names, Vector<String> subgoal_arguments, String view_id, Connection c, PreparedStatement pst ) throws SQLException
	{
		String subgoal_query = "select s.subgoal_names, a.arguments from subgoals s join subgoal_arguments a using (subgoal_names) where s.view = '" + view_id + "'";
		
		pst = c.prepareStatement(subgoal_query);
		
		ResultSet rs = pst.executeQuery();
				
		while(rs.next())
		{
			subgoal_names.add(rs.getString(1));
			
			subgoal_arguments.add(rs.getString(2));
		}
		
	}

	
    static Query parse_query(String head_name, String[] head_vars, Vector<String> body, Vector<String> arguments, String []lambda_term_str)
    {
                        
        Vector<Argument> head_v = new Vector<Argument>();
        
        for(int i=0;i<head_vars.length;i++)
        {
        	head_v.add(new Argument(head_vars[i]));
        }
        
        Subgoal head_subgoal = new Subgoal(head_name, head_v);
        
//        String []body = query.split(":")[1].split("\\),");
                
        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.size());
        
        for(int i=0; i<body.size(); i++)
        {
        	String body_name=body.get(i);
            
            String []body_var=arguments.get(i).split(",");
            
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
	

}
