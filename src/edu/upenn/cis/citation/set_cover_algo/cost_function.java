package edu.upenn.cis.citation.set_cover_algo;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;

public class cost_function {
	
	public static double cost1(Tuple tuple, Query query)
	{
		return tuple.get_relations().size() * 1.0/query.body.size();
	}

}
