package edu.upenn.cis.citation.user_query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;

public class query_node {
	
	public String table_name;
	
	public HashSet<String> head_vars = new HashSet<String>();
	
	public HashSet<String> local_predicates = new HashSet<String>();
		
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
	  
	}
	
	
	public query_node(Query q, String subgoal_name)
	{
		this.table_name = q.subgoal_name_mapping.get(subgoal_name);
		
		for(int i = 0; i<q.head.args.size(); i++)
		{
			Argument arg = (Argument) q.head.args.get(i);
			
			if(arg.relation_name.equals(subgoal_name))
			{
				head_vars.add(arg.name.substring(arg.name.indexOf(populate_db.separator) + 1, arg.name.length()));
			}
			
		}
		
		for(int i = 0; i<q.conditions.size(); i++)
		{
			Conditions condition = q.conditions.get(i);
			
			if(condition.arg2.isConst())
			{
				if(condition.subgoal1.equals(subgoal_name))
				{
				  String arg1 = condition.arg1.name;
				  
					local_predicates.add(arg1.substring(arg1.indexOf(populate_db.separator) + 1, arg1.length()) + condition.op.toString() + condition.arg2.name);
				}
			}
//			else
//			{
//				if(condition.subgoal1.equals(subgoal_name))
//				{
//					global_predicates_attributes.add(condition.arg1.name);
//				}
//				if(condition.subgoal2.equals(subgoal_name))
//				{
//					global_predicates_attributes.add(condition.arg2.name);
//				}
//			}
		}
		
		System.out.println(head_vars.toString());
		
		System.out.println(local_predicates.toString());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		query_node q = (query_node) obj;
		
		if(this.table_name.equals(q.table_name) && this.local_predicates.equals(q.local_predicates) && this.head_vars.equals(q.head_vars))
		{
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		String str = new String();
		
		str = this.table_name + "(";
		
		int num = 0;
		
		for(Iterator iter = this.head_vars.iterator(); iter.hasNext();)
		{
			if(num >= 1)
				str += ",";
			
			String head_var = (String)iter.next();
			
			str += head_var;
			
			num ++;
		}
		
		str += ")";
		
		num = 0;
		
		for(Iterator iter = this.local_predicates.iterator(); iter.hasNext();)
		{
			String local_predicate = (String)iter.next();
			
			str += "," + local_predicate;
			
		}
		
		return str.hashCode();
	}
	
	@Override
	public String toString()
	{
		String str = new String();
		
		str = this.table_name + "(";
		
		int num = 0;
		
		for(Iterator iter = this.head_vars.iterator(); iter.hasNext();)
		{
			if(num >= 1)
				str += ",";
			
			String head_var = (String)iter.next();
			
			str += head_var;
			
			num ++;
		}
		
		str += ")";
		
		num = 0;
		
		for(Iterator iter = this.local_predicates.iterator(); iter.hasNext();)
		{
			String local_predicate = (String)iter.next();
			
			str += "," + local_predicate;
			
		}
		
		return str;
	}
}
