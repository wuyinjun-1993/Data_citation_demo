package edu.upenn.cis.citation.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;

public class Load_views_and_citation_queries {

	
	static String split1 = "|";
	
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
//		System.out.println(line);
		
		String [] strs = line.split("\\" + split1);
		
		String view_name = strs[0];
		
		String heads = strs[1];
		
		String body = strs[2];
		
		String predicates = strs[3];
		
		String lambda_term_str = strs[4];
		
		String relation_mapping_str = strs[5];
		
		Subgoal head_subgoal = new Subgoal(view_name, split_head(heads));
		
		HashMap<String, String> relation_mapping = get_relation_mapping(relation_mapping_str);
		
		Vector<Subgoal> relational_subgoals = split_bodies(body, relation_mapping, c ,pst); 
		
		Vector<Conditions> predicate_subgoal = split_predicates(predicates);
		
		Vector<Lambda_term> l_terms = split_lambda_terms(lambda_term_str);
		
		return new Query(view_name, head_subgoal, relational_subgoals, l_terms, predicate_subgoal, relation_mapping);
		
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
	    
	    String l_term = lambda_terms.get(i).name;
	    
	    l_term = l_term.replace(populate_db.separator, ".");
	    
	    string += l_term;
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
	
	static Vector<Conditions> split_predicates(String predicates_str)
	{
		if(predicates_str.isEmpty())
			return new Vector<Conditions>();
		
		String [] predicates = predicates_str.split(",");
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		for(int i = 0; i<predicates.length; i++)
		{
			String predicate = predicates[i].trim();
						
			Conditions condition = view_operation.parse_conditions(predicate, ".");
			
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
	
	
	
	static Vector<Subgoal> split_bodies(String body_str, HashMap<String, String> relation_mapping, Connection c, PreparedStatement pst) throws SQLException
	{
		String [] relations = body_str.split(",");
		
        Vector<Subgoal> subgoals = new Vector<Subgoal>();
        
		for(int i = 0; i<relations.length; i++)
		{
			String relation = relations[i].trim();
			
			Vector<Argument> args = view_operation.get_full_schema(relation, relation_mapping.get(relation), c, pst);
			
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
