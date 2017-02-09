package citation_view;

import java.util.Vector;

public class Lambda_term {
	
	public String name;
	
	public String table_name;
	
	public String table_name_alias;
	
	public int id;
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public Lambda_term(String name)
	{
		this.name = name;
	}

}
