package edu.upenn.cis.citation.citation_view;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public abstract class citation_view {
	
	public abstract String get_full_query() throws ClassNotFoundException, SQLException;

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
	
	@Override
	public abstract boolean equals(Object o);

}
