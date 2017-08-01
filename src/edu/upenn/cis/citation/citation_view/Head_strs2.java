package edu.upenn.cis.citation.citation_view;

import java.util.Arrays;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;

public class Head_strs2 {
	
	public String[] head_vals;
	
	public Head_strs2(String[] vec_str)
	{
		head_vals = vec_str;
		
//		System.arraycopy(vec_str, 0, head_vals, 0, vec_str.length);
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		Head_strs2 vec_str = (Head_strs2) obj;
		
		if(head_vals.length != vec_str.head_vals.length)
			return false;
		
		
		for(int i = 0; i<head_vals.length; i++)
		{
			if(head_vals[i] == null && vec_str.head_vals[i] == null)
				continue;
			
			
			if(head_vals[i] == null && vec_str.head_vals[i] != null)
				return false;
			
			if(head_vals[i] != null && vec_str.head_vals[i] == null)
				return false;
			
			if(!head_vals[i].equals(vec_str.head_vals[i]))
				return false;
		}
		
		return true;
		
	}
	
	@Override
	public int hashCode() {
		
//		int hashCode = 0;
//		
//	    for (int i = 0; i < head_vals.size(); i ++) {
//	      hashCode += head_vals.get(i).hashCode();
//	    }
	    
	    return toString().hashCode();
	  }
	
	
	@Override
	public String toString()
	{
		
		String output = new String();
		
		for(int i = 0; i<head_vals.length; i++)
		{
			if(i >= 1)
				output += " ";
			
			output += head_vals[i];
		}
		
		return output;
	}
	
	public void clear()
	{
		head_vals = null;
	}

}
