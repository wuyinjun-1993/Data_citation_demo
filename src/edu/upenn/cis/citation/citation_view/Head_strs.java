package edu.upenn.cis.citation.citation_view;

import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;

public class Head_strs {
	
	Vector<String> head_vals;
	
	public Head_strs(Vector<String> vec_str)
	{
		
		head_vals = new Vector<String> ();
		this.head_vals.addAll(vec_str);
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		Head_strs vec_str = (Head_strs) obj;
		
		if(head_vals.size() != vec_str.head_vals.size())
			return false;
		
		
		for(int i = 0; i<head_vals.size(); i++)
		{
			if(!head_vals.get(i).equals(vec_str.head_vals.get(i)))
				return false;
		}
		
		return true;
		
	}
	
	@Override
	public int hashCode() {
		
		int hashCode = 0;
		
	    for (int i = 0; i < head_vals.size(); i ++) {
	      hashCode += head_vals.get(i).hashCode();
	    }
	    
	    return hashCode;
	  }
	
	
	@Override
	public String toString()
	{
		
		String output = new String();
		
		for(int i = 0; i<head_vals.size(); i++)
		{
			if(i >= 1)
				output += " ";
			
			output += head_vals.get(i);
		}
		
		return output;
	}

}
