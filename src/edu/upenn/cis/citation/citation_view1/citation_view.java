package edu.upenn.cis.citation.citation_view1;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;

public abstract class citation_view {
	
	public abstract String get_name();
	
//	public abstract Vector<String> get_parameters();
		
	public abstract String gen_citation_unit(Vector<String> lambda_terms);
	
	public abstract String gen_citation_unit(String name, Vector<String> lambda_terms);
	
	public abstract Vector<String> get_table_names();
	
	public abstract String get_index();
	
	public abstract void put_paras(String para, String value);
	
	@Override
	public abstract String toString();
	
	public abstract boolean has_lambda_term();
	
	public abstract citation_view clone();
	
	public abstract String get_table_name_string();
	
	public abstract double get_weight_value();
	
	public abstract void calculate_weight(Query query);
	
	public abstract Tuple get_view_tuple();
	
	@Override
	public abstract boolean equals(Object o);
	
	public abstract long[] get_mapped_table_name_index();
	
	public abstract long[] get_mapped_head_var_index();
	
	public abstract long[] get_tuple_index();
	
	@Override
	public abstract int hashCode();

}
