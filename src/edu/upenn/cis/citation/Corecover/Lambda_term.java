package edu.upenn.cis.citation.Corecover;

import java.util.Vector;

public class Lambda_term {
	
	public String name;
	
	public String table_name;
	
	public String table_name_alias;
	
	public int id;
	
	public int table_index;
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public Lambda_term(String name)
	{
		this.name = name;
	}
	
	public Lambda_term(String name, String table_name)
	{
		this.name = name;
		
		this.table_name = table_name;
	}
	
	public void update_table_name(String new_table_name)
	{
		table_name = new_table_name;
		
		name = table_name + "_" + name.substring(name.indexOf("_") + 1, name.length());
	}

	@Override
	public Object clone()
	{
		return new Lambda_term(this.name, this.table_name);
	}
}
