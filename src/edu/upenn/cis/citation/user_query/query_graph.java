package edu.upenn.cis.citation.user_query;

import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_citation_view_table;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;

public class query_graph {
		
	public Vector<query_node> q_nodes;
	
	public Vector<query_edge> q_edges;
	
	public query_graph(Query query)
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
				
				query_edge q_edge = new query_edge(q1, q2, condition.arg1.name, condition.arg2.name, condition.op);
				
				sort_insert.insert_sort(q_edges, q_edge);
			}
			
			
		}
		
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
