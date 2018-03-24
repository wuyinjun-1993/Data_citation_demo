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
		Query q = gen_query();
		
		Query q2 = gen_query2();
		
		query_node n1 = new query_node(q, "family");
		
		query_node n2 = new query_node(q, "introduction");
				
		query_node n3 = new query_node(q2, "family");

		query_node n4 = new query_node(q, "introduction");

		Vector<query_node> n_l1 = new Vector<query_node>();
		
		n_l1.add(n1);
		
		n_l1.add(n2);
		
		Vector<query_node> n_l2 = new Vector<query_node>();
		
		n_l2.add(n4);
		
		n_l2.add(n3);
		
		
		
//		query_node q4 = new query_node(q, "introduction");
//		
//		HashSet<query_node> query_node_set1 = new HashSet<query_node>();
//		
//		query_node_set1.add(q1);
//		
//		query_node_set1.add(q2);
//		
//		HashSet<query_node> query_node_set2 = new HashSet<query_node>();
//		
//		query_node_set2.add(q3);
//		
//		query_node_set2.add(q4);
		
		System.out.println(n_l1.containsAll(n_l2));
		
		System.out.println(n_l2.containsAll(n_l1));
		
//		System.out.println(query_node_set1);
//		
//		System.out.println(query_node_set2);
	}
	
	static Query gen_query() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family_family_id", "family"));
		
		head_args.add(new Argument("family_name", "family"));
		
		head_args.add(new Argument("introduction_family_id", "introduction"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
			
		subgoals.add(new Subgoal("family", args1));
			
		subgoals.add(new Subgoal("introduction", args3));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
		
		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query2() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family_name", "family"));
		
		head_args.add(new Argument("family_family_id", "family"));
				
		head_args.add(new Argument("introduction_family_id", "introduction"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
			
		subgoals.add(new Subgoal("family", args1));
			
		subgoals.add(new Subgoal("introduction", args3));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
		
		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
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
