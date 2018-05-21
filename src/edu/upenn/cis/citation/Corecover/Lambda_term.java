package edu.upenn.cis.citation.Corecover;

import java.util.Vector;
import edu.upenn.cis.citation.Pre_processing.populate_db;


public class Lambda_term {
	
	public String arg_name;
	
	public String table_name;
	
	public Argument arg;
	
	@Override
	public String toString()
	{
		return arg_name;
	}
	
	public Lambda_term(Argument arg)
	{
	  this.arg = arg;
	  
	  arg_name = arg.name;
	  
	  table_name = arg.name.substring(arg.name.indexOf(populate_db.separator) + 1, arg.name.length());
	}
	
	public Lambda_term(String name)
	{
		this.arg_name = name;
	}
	
	public Lambda_term(String name, String table_name)
	{
		this.arg_name = name;
		
		this.table_name = table_name;
	}
	
	public void update_table_name(String new_table_name)
	{
		table_name = new_table_name;
		
		arg_name = table_name + populate_db.separator + arg_name.substring(arg_name.indexOf(populate_db.separator) + 1, arg_name.length());
	}

	@Override
	public Object clone()
	{
		return new Lambda_term(this.arg_name, this.table_name);
	}
	
	@Override
	public int hashCode()
	{
		return arg_name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Lambda_term l = (Lambda_term) obj;
		
		return (l.arg_name.equals(this.arg_name));
		
	}
}
