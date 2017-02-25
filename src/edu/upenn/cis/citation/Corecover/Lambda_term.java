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

}
