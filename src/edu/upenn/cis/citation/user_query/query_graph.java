package edu.upenn.cis.citation.user_query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Pre_processing.populate_citation_view_table;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;

public class query_graph {
		
	public Vector<query_node> q_nodes;
	
	public Vector<query_edge> q_edges;
	
	public String version;
	
	public query_graph(Query query, String version)
	{
		
		q_nodes = new Vector<query_node>();
		
		q_edges = new Vector<query_edge>();
		
		HashMap<String, query_node> subgoal_mapping = new HashMap<String, query_node>();
		
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			query_node q_node = new query_node(query, subgoal.name);
			
			sort_insert.insert_sort(q_nodes, q_node);
						
			subgoal_mapping.put(subgoal.name, q_node);
		}
		
		for(int i = 0; i<query.conditions.size(); i++)
		{
			
			Conditions condition = query.conditions.get(i);
			
			if(!condition.arg2.isConst())
			{
				query_node q1 = subgoal_mapping.get(condition.subgoal1);
				
				query_node q2 = subgoal_mapping.get(condition.subgoal2);
				
				String arg1 = condition.arg1.name;
				
				String arg2 = condition.arg2.name;
				
				query_edge q_edge = new query_edge(q1, q2, arg1.substring(arg1.indexOf(populate_db.separator) + 1, arg1.length()), arg2.substring(arg2.indexOf(populate_db.separator) + 1, arg2.length()), condition.op);
				
				sort_insert.insert_sort(q_edges, q_edge);
			}
			
			
		}
		
	}
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
		Query q1 = gen_query();
		
		Query q2 = gen_query2();
		
		query_graph g1 = new query_graph(q1, "0");
		
		query_graph g2 = new query_graph(q2, "0");
		
		System.out.println(g1.equals(g2));
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
				
		
//		head_args.add(new Argument("family1_family_id", "family1"));
//		
//		head_args.add(new Argument("family2_family_id", "family2"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		
		head_args.add(new Argument("introduction_family_id", "introduction"));

		
//		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
//		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
		
//		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
		subgoals.add(new Subgoal("family", args1));
		
//		subgoals.add(new Subgoal("family1", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		subgoals.add(new Subgoal("introduction", args3));
		
//		subgoals.add(new Subgoal("introduction1", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_greater(), new Argument("family_id", "introduction"), "introduction"));
		
//		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
		
//		subgoal_name_mapping.put("family1", "family");
//		
//		subgoal_name_mapping.put("family2", "family");
		
//		subgoal_name_mapping.put("family3", "family");
				
		subgoal_name_mapping.put("introduction", "introduction");
		
//		subgoal_name_mapping.put("introduction1", "introduction");
//
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");


		
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
		
//		head_args.add(new Argument("introduction_family_id", "introduction"));
//
//		
//		head_args.add(new Argument("family_family_id", "family"));
				
		
		head_args.add(new Argument("family1_family_id", "family1"));
//		
//		head_args.add(new Argument("family2_family_id", "family2"));
				
		
//		head_args.add(new Argument("family3_family_id", "family3"));
		

		
		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//				
//		head_args.add(new Argument("introduction2_family_id", "introduction2"));
		
//		head_args.add(new Argument("introduction3_family_id", "introduction3"));
		
		Subgoal head = new Subgoal("q", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
//		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("family1", "family", c, pst);
		
		
//		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
		
//		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
		
//		Vector<Argument> args3 = view_operation.get_full_schema("introduction", "introduction", c, pst);
		
		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//		
//		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
		
//		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);

		
//		subgoals.add(new Subgoal("introduction", args3));
		
//		subgoals.add(new Subgoal("family", args1));
		
		subgoals.add(new Subgoal("family1", args2));
//		
//		subgoals.add(new Subgoal("family2", args6));
		
//		subgoals.add(new Subgoal("family3", args7));
				
		
		subgoals.add(new Subgoal("introduction1", args4));
//		
//		subgoals.add(new Subgoal("introduction2", args5));
		
//		subgoals.add(new Subgoal("introduction3", args8));
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_less_equal(), new Argument("2"), new String()));
//		
		conditions.add(new Conditions(new Argument("family_id", "family1"), "family1", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
		
//		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("2"), new String()));
		
		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("family_id", "family1"), "family1"));
//		
//		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));

//		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));

		
//		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
//		subgoal_name_mapping.put("introduction", "introduction");

		
//		subgoal_name_mapping.put("family", "family");
		
		subgoal_name_mapping.put("family1", "family");
//		
//		subgoal_name_mapping.put("family2", "family");
		
//		subgoal_name_mapping.put("family3", "family");
				
		
		subgoal_name_mapping.put("introduction1", "introduction");
//
//		subgoal_name_mapping.put("introduction2", "introduction");
		
//		subgoal_name_mapping.put("introduction3", "introduction");


		
		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}

	
	@Override
	public int hashCode()
	{
		String str = new String();
		
		str += "[";
		
		for(int i = 0; i<q_nodes.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			str += q_nodes.get(i).toString();
		}
		
		str += "]";
		
		if(q_edges.size() > 0)
		{
			str += ",[";
			
			for(int i = 0; i < q_edges.size(); i++)
			{
				if(i >= 0)
					str += ",";
				
				str += q_edges.get(i).toString();
			}
			
			str += "]";
		}
		
		str += "," + version;
		
		return str.hashCode();
		
		
	}
	
	@Override
	public boolean equals(Object obj)
	{
		query_graph q_graph = (query_graph) obj;
		
		if(this.hashCode() == q_graph.hashCode())
			return true;
		return false;
	}
	
	

}
