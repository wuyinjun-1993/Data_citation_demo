import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Gen_citation2 {
	
	static String query;
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		query = "q(name):family_c(f,name,'gpcr'),introduction_c(f,text)";
		
		
		Query q = Parse_datalog.parse_query(query);
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		Vector<Vector<Vector<citation_view>>> citation_views = get_citation_views(q, c, pst);
		
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Vector<citation_view>> citation_view_vec = citation_views.get(i);
			
			for(int j = 0; j<citation_view_vec.size();j++)
			{
				Vector<citation_view> c_view = citation_view_vec.get(j);
				
				if(j > 0)
					System.out.print(",");
				
				for(int k = 0; k<c_view.size();k++)
				{
					citation_view  cv = c_view.get(k);
					if(k > 0)
						System.out.print("*");
					System.out.print(cv);
				}
				
				
			}
			
			System.out.println();
			
		}
		
	}
	
	public static Vector<citation_view> gen_citation_arr(Vector<Query> views)
	{
		return new Vector<citation_view>(views.size());
	}
	
	public static void gen_citation(Vector<citation_view> citation_views)
	{
		for(int i = 0; i<citation_views.size();i++)
		{
			Vector<String> query = citation_views.get(i).get_full_query();
			
			System.out.println(query);
		}
	}
	
	public static Vector<Vector<Vector<citation_view>>> get_citation_views(Query query,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<Vector<citation_view>>> c_views = query_execution(query, c, pst);
				
		return c_views;
	}
	
	public static Vector<Vector<Vector<citation_view>>> query_execution(Query query, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
				
		Vector<Vector<Vector<citation_view>>> c_views = new Vector<Vector<Vector<citation_view>>>();
				
		Vector<String> subgoal_names = new Vector<String>();
				
		Query_converter.pre_processing(query);
		
		String sql = Query_converter.datalog2sql_citation(query);
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			subgoal_names.add(subgoal.name);
							
		}
		
		pst = c.prepareStatement(sql);
		
		ResultSet rs = pst.executeQuery();
		
		boolean first = true;
		
		while(rs.next())
		{
			Vector<String[]> c_units = new Vector<String[]>();
			for(int i = 0; i<query.body.size();i++)
			{
				String[] c_unit= rs.getString(subgoal_names.get(i) + "_citation_view").split("\\"+ populate_db.separator);
				c_units.add(c_unit);
			}
			
			
			
			if(first)
			{
				Vector<Vector<citation_view>> c_unit_vec = get_citation_units(c_units);
				
				Vector<Vector<citation_view>> c_unit_combinaton = get_valid_citation_combination(c_unit_vec,subgoal_names);
				
				c_views.add(c_unit_combinaton);
				
				first = false;
			
			}
			
			else
			{
				
				HashMap<String,citation_view> c_unit_vec = get_citation_units_unique(c_units);
				
				Vector<Vector<citation_view>> curr_c_view = c_views.get(0);
				
				Vector<Vector<citation_view>> insert_c_view = (Vector<Vector<citation_view>>) curr_c_view.clone();
				
				Vector<Vector<citation_view>> update_c_view = update_valid_citation_combination(insert_c_view,c_unit_vec);
				
				c_views.add(update_c_view);
			}
		}
		
		Query_converter.post_processing(query);
		
		return c_views;
	}
	
	public static Vector<Vector<citation_view>> update_valid_citation_combination(Vector<Vector<citation_view>> insert_c_view, HashMap<String,citation_view> c_unit_vec)
	{
		
		Vector<Vector<citation_view>> update_c_views = new Vector<Vector<citation_view>>();
		
		for(int i = 0; i<insert_c_view.size();i++)
		{
			Vector<citation_view> insert_c_view_vec = insert_c_view.get(i);
			
			Vector<citation_view> update_c_view_vec = new Vector<citation_view>();
			
			for(int j = 0; j < insert_c_view_vec.size(); j++)
			{
				citation_view c = insert_c_view_vec.get(j);
				
				c = c_unit_vec.get(String.valueOf(c.get_index()));
				
				update_c_view_vec.add(c);
			}
			
			update_c_views.add(update_c_view_vec);
		}
		
		return update_c_views;
	}
	
	public static Vector<Vector<citation_view>> get_citation_units(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view>> c_views = new Vector<Vector<citation_view>>();
		
		for(int i = 0; i<c_units.size(); i++)
		{
			Vector<citation_view> c_view = new Vector<citation_view>();
			String [] c_unit_str = c_units.get(i);
			for(int j = 0; j<c_unit_str.length; j++)
			{
				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
				{
					
					String c_view_name = c_unit_str[j].split("\\(")[0];
					
					Vector<String> c_view_paras = new Vector<String>();
					
					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
					
					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
					
					curr_c_view.put_paramters(c_view_paras);
					
					c_view.add(curr_c_view);
				}
				else
				{
					c_view.add(new citation_view_unparametered(c_unit_str[j]));
				}
			}
			
			c_views.add(c_view);
		}
		
		return c_views;
	}
	
	
	public static HashMap<String,citation_view> get_citation_units_unique(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
	{
//		Vector<citation_view> c_views = new Vector<citation_view>();
		
		HashMap<String,citation_view> c_view_map = new HashMap<String, citation_view>();
		
		for(int i = 0; i<c_units.size(); i++)
		{
//			Vector<citation_view> c_view = new Vector<citation_view>();
			String [] c_unit_str = c_units.get(i);
			for(int j = 0; j<c_unit_str.length; j++)
			{
				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
				{
					
					String c_view_name = c_unit_str[j].split("\\(")[0];
					
					Vector<String> c_view_paras = new Vector<String>();
					
					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
					
					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
					
					curr_c_view.put_paramters(c_view_paras);
					
					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
				}
				else
				{
					citation_view_unparametered curr_c_view = new citation_view_unparametered(c_unit_str[j]);
					
					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
				}
			}
			
		}
		
//		Set set = c_view_map.keySet();
//		
//		for(Iterator iter = set.iterator();iter.hasNext();)
//		{
//			String key = (String) iter.next();
//			
//			c_views.add(c_view_map.get(key));
//		}
		
		return c_view_map;
	}
	
	
	
	public static Vector<Vector<citation_view>> get_valid_citation_combination(Vector<Vector<citation_view>>c_unit_vec, Vector<String> subgoal_names)
	{
		Vector<Vector<citation_view>> c_combinations = new Vector<Vector<citation_view>>();
		
		Vector<Vector<String>> c_subgoal_names = new Vector<Vector<String>>();
		
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
			
			Vector<citation_view> curr_combination = new Vector<citation_view>();
			
			Vector<Vector<String>> curr_subgoal_name = new Vector<Vector<String>>();
			
			//check validation
			for(int j = 0; j < curr_c_unit_vec.size(); j++)
			{
				citation_view curr_c_unit = curr_c_unit_vec.get(j);
				
				if(subgoal_names.containsAll(curr_c_unit.get_table_names()))
				{
					curr_combination.add(curr_c_unit);
					curr_subgoal_name.add(curr_c_unit.get_table_names());
				}
			}
			
			join_operation(c_combinations, curr_combination, c_subgoal_names, curr_subgoal_name, i);
		}
		
		remove_duplicate(c_combinations);
		
		return c_combinations;
	}
	
	
	public static void remove_duplicate(Vector<Vector<citation_view>> c_combinations)
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
		
		c_combinations.clear();
		
		Set set = update_combinations.keySet();
		
		for(Iterator iter = set.iterator();iter.hasNext();)
		{
			String key = (String) iter.next();
			
			c_combinations.add(update_combinations.get(key));
		}
	}
	
	public static void join_operation(Vector<Vector<citation_view>> c_combinations, Vector<citation_view> insert_citations, Vector<Vector<String>> c_subgoal_names, Vector<Vector<String>> curr_subgoal_name, int i)
	{
		if(i == 0)
		{
			for(int k = 0; k<insert_citations.size(); k++)
			{
				Vector<citation_view> c = new Vector<citation_view>();
				
				c.add(insert_citations.get(k));
				
				c_combinations.add(c);

			}
			c_subgoal_names.addAll(curr_subgoal_name);
		}
		else
		{
			Vector<Vector<citation_view>> updated_c_combinations = new Vector<Vector<citation_view>>();
			
			Vector<Vector<String>> updated_c_subgoals = new Vector<Vector<String>>();
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
				Vector<citation_view> curr_combination = c_combinations.get(j);
				
				Vector<String> curr_subgoal = c_subgoal_names.get(j);
				
				for(int k = 0; k<insert_citations.size(); k++)
				{
					if(insert_citations.get(k).get_table_names().containsAll(curr_subgoal))
					{
						Vector<citation_view> c = new Vector<citation_view>();
						
						c.add(insert_citations.get(k));
						
						updated_c_combinations.add(c);
						
						updated_c_subgoals.add(insert_citations.get(k).get_table_names());
					}
					else
					{
						if(curr_subgoal.containsAll(insert_citations.get(k).get_table_names()))
						{
							updated_c_combinations.add((Vector<citation_view>) curr_combination.clone());
							
							updated_c_subgoals.add(curr_subgoal);
						}
						
						else
						{
							Vector<citation_view> c = new Vector<citation_view>();
							
							Vector<String> str_vec = new Vector<String>();
							
							c = (Vector<citation_view>) curr_combination.clone();
							
							str_vec = (Vector<String>) curr_subgoal.clone();
							
							str_vec.addAll(insert_citations.get(k).get_table_names());
							
							c.add(insert_citations.get(k));
							
							updated_c_combinations.add(c);
							
							updated_c_subgoals.add(str_vec);
						}
					}
				}
			}
			
			c_combinations.clear();
			
			c_combinations.addAll(updated_c_combinations);
			
			c_subgoal_names.clear();
			
			c_subgoal_names.addAll(updated_c_subgoals);
		}
	}
	
	public static void rename_column(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column citation_view to "+ view.name + "_citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
	public static void rename_column_back(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column "+ view.name +"_citation_view to citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
	public static Vector<Vector<citation_view>> str2c_view(Vector<Vector<String>> citation_str) throws ClassNotFoundException, SQLException
	{
		Vector<Vector<citation_view>> c_view = new Vector<Vector<citation_view>>();
		
		for(int i = 0; i<citation_str.size(); i++)
		{
			Vector<String> curr_c_view_str = citation_str.get(i);
			
			Vector<citation_view> curr_c_view = new Vector<citation_view>();
			
			for(int j = 0; j<curr_c_view_str.size();j++)
			{
				if(citation_str.get(i).contains("(") && citation_str.get(i).contains(")"))
				{
					String citation_view_name = curr_c_view_str.get(i).split("\\(")[0];
					
					String [] citation_parameters = curr_c_view_str.get(i).split("\\(")[1].split("\\)")[0].split(",");
					
					Vector<String> citation_para_vec = new Vector<String>();
					
					citation_para_vec.addAll(Arrays.asList(citation_parameters));
					
					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
					
					inserted_citation_view.put_paramters(citation_para_vec);
										
					curr_c_view.add(inserted_citation_view);
				}
				else
				{
					curr_c_view.add(new citation_view_unparametered(curr_c_view_str.get(i)));
				}
			}
			
			c_view.add(curr_c_view);
		}
		
		return c_view;
	}
	
	
	public static Vector<String> intersection(Vector<String> c1, Vector<String> c2)
	{
		Vector<String> c = new Vector<String>();
		
		for(int i = 0; i<c1.size(); i++)
		{
			if(c2.contains(c1.get(i)))
				c.add(c1.get(i));
		}
		
		return c;
	}
	
	public static Vector<Vector<String>> get_citation(Subgoal subgoal,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<String>> c_view = new Vector<Vector<String>>();
		
		String table_name = subgoal.name + "_c";
		
		String schema_query = "select column_name from INFORMATION_SCHEMA.COLUMNS where table_name = '" + table_name + "'";
		
		
		pst = c.prepareStatement(schema_query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		boolean constant_variable = false;
		
		String citation_view_query = "select citation_view from " + table_name;
		
		while(rs.next())
		{
			String column_name = rs.getString(1);
			
			Argument arg = (Argument) subgoal.args.get(num++);
			
			if(arg.type == 1)
			{
				if(constant_variable == false)
				{
					constant_variable = true;
					
					citation_view_query += " where ";
				}
				
				citation_view_query += column_name + "=" + arg.name;
			}
			
			if(num >= subgoal.args.size())
				break;
		}
		
		pst = c.prepareStatement(citation_view_query);
		
		rs = pst.executeQuery();
		
		while(rs.next())
		{
			String[] citation_str = rs.getString(1).split("\\"+populate_db.separator);
			
			
			
			Vector<String> curr_c_view = new Vector<String>();
			
			curr_c_view.addAll(Arrays.asList(citation_str));
			
			c_view.add(curr_c_view);
		}
		
		return c_view;
	}
	
	public static Vector<citation_view> str2c_unit(String [] citation_str) throws ClassNotFoundException, SQLException
	{
		Vector<citation_view> c_view = new Vector<citation_view>();
			
			for(int j = 0; j<citation_str.length;j++)
			{
				if(citation_str[j].contains("(") && citation_str[j].contains(")"))
				{
					String citation_view_name = citation_str[j].split("\\(")[0];
					
					String [] citation_parameters = citation_str[j].split("\\(")[1].split("\\)")[0].split(",");
					
					Vector<String> citation_para_vec = new Vector<String>();
					
					citation_para_vec.addAll(Arrays.asList(citation_parameters));
					
					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
					
					inserted_citation_view.put_paramters(citation_para_vec);
										
					c_view.add(inserted_citation_view);
				}
				else
				{
					c_view.add(new citation_view_unparametered(citation_str[j]));
				}
			}
		
		return c_view;
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
