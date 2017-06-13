package edu.upenn.cis.citation.citation_view;

import java.util.Vector;

public class Head_strs {
	
	Vector<String> head_vals;
	
	public Head_strs(Vector<String> vec_str)
	{
		
		head_vals = new Vector<String> ();
		this.head_vals.addAll(vec_str);
	}
	
	public boolean equals(Object obj)
	{
		Vector<String> vec_str = (Vector<String>) obj;
		
		if(head_vals.size() != vec_str.size())
			return false;
		
		
		for(int i = 0; i<head_vals.size(); i++)
		{
			if(!head_vals.get(i).equals(vec_str.get(i)))
				return false;
		}
		
		return true;
		
	}

}
