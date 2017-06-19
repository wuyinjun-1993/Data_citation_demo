package edu.upenn.cis.citation.user_query;

import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;

public class query_edge {
	
	public query_node q1;
	
	public query_node q2;
	
	public Operation op;
	
	public String attr1;
	
	public String attr2;
	
	public query_edge(query_node q1, query_node q2, String attr1, String attr2, Operation op)
	{
		this.q1 = q1;
		
		this.q2 = q2;
		
		this.attr1 = attr1;
		
		this.attr2 = attr2;
		
		this.op = op;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		query_edge q_edge = (query_edge) obj;
		
		if(this.op.equals(q_edge.op))
		{
			if(this.q1.equals(q_edge.q1) && this.q2.equals(q_edge.q2) && this.attr1.equals(q_edge.attr1) && this.attr2.equals(q_edge.attr2))
			{
				return true;
			}
		}
		
		if(this.op.equals(q_edge.op.counter_direction()))
		{
			if(this.q2.equals(q_edge.q1) && this.q1.equals(q_edge.q2) && this.attr2.equals(q_edge.attr1) && this.attr1.equals(q_edge.attr2))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		String str = new String();
		
		if(q1.hashCode() > q2.hashCode())
		{
			str = q1 + "->" + attr1 + op.toString() + q2 + "->" + attr2;
		}
		else
		{
			str = q2 + "->" + attr2 + op.counter_direction().toString() + q1 + "->" + attr1;
		}
		
		return str.hashCode();
	}
}
