package sort_citation_view_vec;

import java.util.Vector;

public class sort_string_index {
	
	
	public static <T> int binary_search(Vector<T> list, T item, binary_compare bc)
	{
		
		int pos = list.size()/2;
		
		int start = 0;
		
		int end = list.size();
		
		while(start < end - 1)
		{
			
			int cmp_v = bc.compare(list.get(pos), item); 
			
			if(cmp_v < 0)
			{
				end = pos;
			}
			else
			{
				if(cmp_v > 0)
				{
					start = pos;
				}
				else
					break;
			}
			
			pos = (start + end) /2;
			
		}
		
		return pos;
	}

}
