import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public abstract class citation_view {
	
	public abstract String get_full_query() throws ClassNotFoundException, SQLException;

	public abstract String get_name();
	
	public abstract Vector<String> get_parameters();
		
	public abstract String gen_citation_unit(Vector<String> lambda_terms);
	
	public abstract Vector<String> get_table_names();
	
	public abstract char get_index();

}
