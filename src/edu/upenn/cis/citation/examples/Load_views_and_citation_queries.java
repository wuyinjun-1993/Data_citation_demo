package edu.upenn.cis.citation.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.datalog.Query_converter;

public class Load_views_and_citation_queries {

	
	static String split1 = "|";
	
	static String split2 = "#";
	
	static int col_nums = 4;
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		Vector<Query> queries = get_views("data/Exp1/views", c, pst);
		
		for(int i = 0; i < queries.size(); i++)
		{
			System.out.println(queries.get(i));
		}
		
		c.close();
	}
	
	public static Vector<Query> get_views(String file, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Query> queries = new Vector<Query>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	
		    	queries.add(get_view(line, c, pst));
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return queries;
	}
	
    static Query get_view(String line, Connection c, PreparedStatement pst) throws SQLException
    {
//      System.out.println(line);
        
        String [] strs = line.split("\\" + split1);
        
        String view_name = strs[0];
        
        String heads = strs[1];
        
        String body = strs[2];
        
        String predicates = strs[3];
        
        String lambda_term_str = strs[4];
        
        String relation_mapping_str = strs[5];
        
        HashMap<String, Argument> name_arg_mappings = new HashMap<String, Argument>();
        
        HashMap<String, String> relation_mapping = get_relation_mapping(relation_mapping_str);
        
        Vector<Subgoal> relational_subgoals = split_bodies(body, relation_mapping, name_arg_mappings, c ,pst);
        
        Subgoal head_subgoal = split_head(view_name, heads, name_arg_mappings);
                
        Vector<Conditions> predicate_subgoal = split_predicates(predicates, name_arg_mappings);
        
        Vector<Lambda_term> l_terms = split_lambda_terms(lambda_term_str);
        
        return new Query(view_name, head_subgoal, relational_subgoals, l_terms, predicate_subgoal, relation_mapping);
        
    }
	
	   static String[] get_agg_function_string(String arg_with_agg_function)
	    {
	      arg_with_agg_function = arg_with_agg_function.trim();
	      
	      String [] arg_with_agg_function_strings = null;
	      
	      if(arg_with_agg_function.contains("("))
	      {
	        arg_with_agg_function_strings = new String[2];
	        
	        arg_with_agg_function_strings[0] = arg_with_agg_function.substring(0, arg_with_agg_function.indexOf("("));
	        
	        arg_with_agg_function_strings[1] = arg_with_agg_function.substring(arg_with_agg_function.indexOf("(") + 1, arg_with_agg_function.indexOf(")"));
	        
	      }
	      
	      return arg_with_agg_function_strings;
	    }
	
	   static Subgoal split_head(String name, String head, HashMap<String, Argument> name_arg_mappings)
	    {       
	        String [] head_arg_strs = head.split(",");
	        
	        Vector<Argument> head_args = new Vector<Argument>();
	        
	        Vector<Vector<Argument>> head_agg_args = null;
	        
	        Vector<String> head_agg_functions = null; 
	        
	        boolean has_agg = false;
	        
	        for(int i = 0; i<head_arg_strs.length; i++)
	        {
	          
	          String[] arg_with_agg_function_strings = get_agg_function_string(head_arg_strs[i]);
	            
	          String head_var = null;
	          
	          String agg_function = null;
	          
	          if(arg_with_agg_function_strings != null)
	          {
	            head_var = arg_with_agg_function_strings[1];
	            
	            if(head_agg_args == null)
	            {
	              head_agg_args = new Vector<Vector<Argument>>();
	                
	              head_agg_functions = new Vector<String>();
	            }
	            
	            String[] all_head_vars = head_var.split(split2);
	            
	            Vector<Argument> curr_head_agg_args = new Vector<Argument>();
	            
	            for(int k = 0; k<all_head_vars.length; k++)
	            {
	              String[] relation_arg = all_head_vars[k].trim().split("\\" + ".");
	              
	              String relation = relation_arg[0].trim();
	                
	              String arg = relation_arg[1].trim();
	                
	              Argument Arg = name_arg_mappings.get(relation + populate_db.separator + arg);
	              
	              curr_head_agg_args.add(Arg);
	            }
	            
	            agg_function = arg_with_agg_function_strings[0].toLowerCase();
	            
	            head_agg_functions.add(agg_function);
	            
	            head_agg_args.add(curr_head_agg_args);
	          }
	          else
	          {
	            head_var = head_arg_strs[i];
	            
	            String [] relation_arg = head_var.trim().split("\\" + ".");
	            
	            String relation = relation_arg[0].trim();
	            
	            String arg = relation_arg[1].trim();
	            
	            Argument Arg = name_arg_mappings.get(relation + populate_db.separator + arg);
	            
	            head_args.add(Arg);
	          }
	          
	        }
	        
	        if(head_agg_args != null)
	          has_agg = true;
	        
	        return new Subgoal(name, head_args, head_agg_args, head_agg_functions, has_agg);
	    }
	    
	   
	static String Convert_query2string(Query query)
	{
	  String string = new String();
	  
	  string += query.name + split1;
	  
	  string += convert_head2string(query.head) + split1;
	  
	  string += convert_subgoal2string(query.body) + split1;
	  
	  string += convert_condition2string(query.conditions) + split1;
	  
	  string += convert_lambda_terms2string(query.lambda_term) + split1;
	  
	  string += convert_subgoal_mapping2string(query.subgoal_name_mapping);
	  
	  return string;
	  
	}
	
	static String convert_subgoal_mapping2string(HashMap<String, String> subgoal_mappings)
	{
	  String string = new String();
	  
	  Set<String> origin_subgoal_names = subgoal_mappings.keySet();
	  
	  int count = 0;
	  
	  for(String subgoal_name : origin_subgoal_names)
	  {
	    if(count >= 1)
	      string += ",";
	    
	    string += subgoal_name + ":" + subgoal_mappings.get(subgoal_name);
	    
	    count++;
	  }
	  
	  return string;
	}
	
	static String convert_head2string(Subgoal head)
	{
	  Vector<Argument> args = head.args;
	  
	  String string = new String();
	  
	  for(int i = 0; i<args.size(); i++)
	  {
	    Argument arg = args.get(i);
	    
	    if(i >= 1)
	      string += ",";
	    
	    String arg_name = arg.name.substring(arg.name.indexOf(populate_db.separator) + 1, arg.name.length());
	    
	    string += arg_name;
	    
	  }
	  
	  return string;
	}
	
	static String convert_subgoal2string(Vector<Subgoal> subgoals)
	{
	  String string = new String();
	  
	  for(int i = 0; i<subgoals.size(); i++)
	  {
	    if(i >= 1)
	      string += ",";
	      
	    string += subgoals.get(i).name;
	  }
	  
	  return string;
	}
	
	static String convert_condition2string(Vector<Conditions> conditions)
	{
	  String string = new String();
	  
	  for(int i = 0; i<conditions.size(); i++)
	  {
	    Conditions condition = conditions.get(i);
	    
	    if(i >= 1)
	      string += ",";
	    
	    string += condition.subgoal1 + "." + condition.arg1.name + condition.op + condition.subgoal2 + "." + condition.arg2.name;
	  }
	  
	  return string;
	}
	
	static String convert_lambda_terms2string(Vector<Lambda_term> lambda_terms)
	{
	  String string = new String();
	  
	  for(int i = 0; i<lambda_terms.size(); i++)
	  {
	    if(i >= 1)
	      string += ",";
	    
	    String l_term = lambda_terms.get(i).arg_name;
	    
	    l_term = l_term.replace(populate_db.separator, ".");
	    
	    string += l_term;
	  }
	  
	  return string;
	}
	
	   public static void write2files(String file_name, Vector<String> views)
	    {
	      
	      BufferedWriter bw = null;
	      try {
	         //Specify the file name and path here
	     File file = new File(file_name);

	     /* This logic will make sure that the file 
	      * gets created if it is not present at the
	      * specified location*/
	      if (!file.exists()) {
	         file.createNewFile();
	      }

	      FileWriter fw = new FileWriter(file);
	      bw = new BufferedWriter(fw);
	      
	      for(int i = 0; i<views.size(); i++)
	      {
	        bw.append(views.get(i));
	        bw.newLine();
	      }
	      
	      bw.close();

	      } catch (IOException ioe) {
	       ioe.printStackTrace();
	    }
	    }
	
	public static Vector<String> views2text_strings(Vector<Query> views)
    {
      Vector<String> strings = new Vector<String>();
      for(int i = 0; i<views.size(); i++)
      {
        strings.add(view2text_string(views.get(i)));
      }
      return strings;
    }
    
    static String view2text_string(Query view)
    {
      String string = view.name + "|";

      for(int i = 0; i<view.head.args.size(); i++)
      {
        if(i >= 1)
          string += ",";
        String arg_name = view.head.args.get(i).toString();
        string += arg_name.replaceAll("\\" + populate_db.separator, ".");
      }
      
      if(view.head.agg_args != null)
      {
        for(int i = 0; i<view.head.agg_args.size(); i++)
          {
            if(view.head.args.size() > 0 || (view.head.args.size() == 0 && i >= 1))
              string += ",";
            String arg_name = view.head.agg_function.get(i).toString() + "(" + Query_converter.get_agg_attr_string(view.head.agg_args.get(i), "#") + ")";
            string += arg_name.replaceAll("\\" + populate_db.separator, ".");
          }
      }
      
      string += "|";
      
      for(int i = 0; i<view.body.size(); i++)
      {
        Subgoal subgoal = (Subgoal) view.body.get(i);
        if(i >= 1)
          string += ",";
        
        string += subgoal.name;
      }
      
      string += "|" ;
      
      for(int i = 0; i<view.conditions.size(); i++)
      {
        if(i >= 1)
          string += ",";
        string += view.conditions.get(i).toString().replaceAll("\\" + populate_db.separator, ".");
      }
      
      string += "|";
      
      for(int i = 0; i<view.lambda_term.size(); i++)
      {
        if(i >= 1)
          string += ",";
        string += view.lambda_term.get(i).toString().replaceAll("\\" + populate_db.separator, ".");
      }
      
      string += "|";
      
      int count = 0;
      for(Entry<String, String> subgoal_mappings: view.subgoal_name_mapping.entrySet())
      {
        if(count >= 1)
          string += ",";
        
        string += subgoal_mappings.getKey() + ":" + subgoal_mappings.getValue();
        
        count ++;
        
      }
      
      
      
      return string;
    }
	
	static Vector<Lambda_term> split_lambda_terms(String lambda_term_str)
	{
		
		if(lambda_term_str.isEmpty())
		{
			return new Vector<Lambda_term>();
		}
		
		String [] lambda_strs = lambda_term_str.split(",");
		
		Vector<Lambda_term> l_terms = new Vector<Lambda_term>();
		
		for(int i = 0; i<lambda_strs.length; i++)
		{
			String [] relation_arg = lambda_strs[i].trim().split("\\" + ".");
			
			
			l_terms.add(new Lambda_term(relation_arg[0].trim() + populate_db.separator + relation_arg[1].trim(), relation_arg[0].trim()));
		}
		
		return l_terms;
	}
	
    static Vector<Conditions> split_predicates(String predicates_str, HashMap<String, Argument> name_arg_mappings)
    {
        if(predicates_str.isEmpty())
            return new Vector<Conditions>();
        
        String [] predicates = predicates_str.split(",");
        
        Vector<Conditions> conditions = new Vector<Conditions>();
        
        for(int i = 0; i<predicates.length; i++)
        {
            String predicate = predicates[i].trim();
                        
            Conditions condition = view_operation.parse_conditions(predicate, name_arg_mappings, ".");
            
            conditions.add(condition);
        }
        
        return conditions;
    }
	
	static HashMap<String, String> get_relation_mapping(String relation_mapping_str)
	{
		String [] relation_mappings = relation_mapping_str.split(",");
		
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
		
		for(int i = 0; i<relation_mappings.length; i++)
		{
			String [] curr_relation_mapping = relation_mappings[i].trim().split(":");
			
			relation_mapping.put(curr_relation_mapping[0].trim(), curr_relation_mapping[1].trim());
			
		}
		
		return relation_mapping;
	}
	
	
	
    static Vector<Subgoal> split_bodies(String body_str, HashMap<String, String> relation_mapping, HashMap<String, Argument> name_arg_mappings, Connection c, PreparedStatement pst) throws SQLException
    {
        String [] relations = body_str.split(",");
        
        Vector<Subgoal> subgoals = new Vector<Subgoal>();
        
        for(int i = 0; i<relations.length; i++)
        {
            String relation = relations[i].trim();
            
            Vector<Argument> args = view_operation.get_full_schema(relation, relation_mapping.get(relation), name_arg_mappings, c, pst);
            
            Subgoal subgoal = new Subgoal(relation, args);
            
            subgoals.add(subgoal);
        }
                
        return subgoals;
    }
	
	static Vector<Argument> split_head(String head)
	{		
		String [] head_arg_strs = head.split(",");
		
		Vector<Argument> head_args = new Vector<Argument>();
		
		for(int i = 0; i<head_arg_strs.length; i++)
		{
			
			String [] relation_arg = head_arg_strs[i].trim().split("\\" + ".");
			
			String relation = relation_arg[0].trim();
			
			String arg = relation_arg[1].trim();
			
			head_args.add(new Argument(relation + populate_db.separator + arg, relation));
		}
		
		return head_args;
	}
	
	
	
}
