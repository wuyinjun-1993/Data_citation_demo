package edu.upenn.cis.citation.sort_citation_view_vec;

import java.util.Vector;

public class sort_string_index {
	
	
	public static <T> int binary_search(Vector<T> list, T item, binary_compare bc)
	{
		
//		int pos = list.size()/2;
		
		int start = 0;
		
		int end = list.size() - 1;
		
		int pos = (start + end) /2;
		
		while(start < end)
		{
			
			
			
			int cmp_v = bc.compare(item, list.get(pos)); 
			
			if(cmp_v < 0)
			{
				end = pos - 1;
			}
			else
			{
				if(cmp_v > 0)
				{
					start = pos + 1;
				}
				else
					break;
			}
			
			pos = (start + end) /2;
			
		}
		
		int cmp_v = bc.compare(item, list.get(pos)); 
		
		if(cmp_v > 0)
		{
			pos = pos + 1;
		}
		
		
		
		return pos;
	}

}
