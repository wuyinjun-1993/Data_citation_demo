package edu.upenn.cis.citation.data_structure;

import java.util.ArrayList;
import java.util.HashMap;

import edu.upenn.cis.citation.citation_view.Head_strs;

public class Query_lm_authors {
	
	public Head_strs[] lm_values;
	
	public Unique_StringList[] author_lists;
	
	public int size;
	
	public void init(int size)
	{
		lm_values = new Head_strs[size];
		
		author_lists = new Unique_StringList[size];
		
		this.size = size;
	}

	public void addAll(HashMap<Head_strs,Unique_StringList> author_mappings, ArrayList<Head_strs> h_list)
	{
		
		h_list = sort(h_list);
		
		for(int i = 0; i<h_list.size(); i++)
		{
			lm_values[i] = h_list.get(i);
			
			author_lists[i] = author_mappings.get(lm_values[i]);
		}
	}
	
	public Unique_StringList get_author(Head_strs lm_value)
	{
		int pos = binarySearch0(lm_values, 0,  size, lm_value);
		
		if(pos < 0)
			return null;
		
		return author_lists[pos];
	}
	
	private static int binarySearch0(Head_strs[] a, int fromIndex, int toIndex, Head_strs key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
			Head_strs midVal = a[mid];

            if (midVal.hashCode() < key.hashCode())
                low = mid + 1;
            else if (midVal.hashCode() > key.hashCode())
               high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

	public static int compare(Head_strs a, Head_strs b)
	{
		if(a.hashCode()> b.hashCode())
		return 1;
		else
		{
		if(a.hashCode() < b.hashCode())
			return -1;
		else
			return 0;
		}
	}


	public static ArrayList<Head_strs> sort(ArrayList<Head_strs> h_list)
	{
		ArrayList<Head_strs> sorted_list = new ArrayList<Head_strs>();
		
		for(int i = 0; i<h_list.size(); i++)
		{
			add(sorted_list, h_list.get(i));
		}
		
		return sorted_list;
	}
	
	public static void add(ArrayList<Head_strs> sorted_list, Head_strs insert)
	{
		if(sorted_list.isEmpty())
		{
			sorted_list.add(insert);
			
			return;
		}
		
//		if(list == null || list.length == 0)
//		{
//			
//			list = new int[DEFAULT_INIT_SIZE];
//			
//			list[0] = insert;
//			
//			size = 1;
//			
//			return;
//		
//		}
//		
//		if(list.length > 0 && size == 0)
//		{
//			list[0] = insert;
//			
//			size ++;
//			
//			return;
//		}
		
		Head_strs first = sorted_list.get(0);
		
		if(insert.hashCode()< first.hashCode())
		{
			
			sorted_list.add(0, insert);
			
//			System.arraycopy(list, 0, list, 1, size);
//			
//			list[0] = insert;
//			
//			size ++;
//			index_vec.insertElementAt(insert, 0);
			
			return;
		}
		
		Head_strs last = sorted_list.get(sorted_list.size() - 1);
		
		if(insert.hashCode() > last.hashCode())
		{
			
			sorted_list.add(insert);
			
			return;
		}
		
		
		
		
//		int pos = Arrays.binarySearch(list, insert, s_compare);;
		
		int pos = binary_search(sorted_list, insert, sorted_list.size());
		
//		pos = binary_search(sorted_list, insert, sorted_list.size());
		
		sorted_list.add(pos, insert);
		
//		System.arraycopy(list, pos, list, pos + 1, size - pos);
//		
//		list[pos] = insert;
//		
//		size ++;
//		
//		index_vec.insertElementAt(insert, pos);
		
	}
	
	public static int binary_search(Head_strs [] list, Head_strs item, int len)
	{
	
		//int pos = list.size()/2;
		
		int start = 0;
		
		int end = len - 1;
		
		int pos = (start + end) /2;
		
		while(start < end)
		{
		
		
		
		int cmp_v = compare(item, list[pos]); 
		
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
		
		int cmp_v = compare(item, list[pos]); 
		
		if(cmp_v > 0)
		{
		pos = pos + 1;
		}
		
		
		
		return pos;
	}
	
	public static int binary_search(ArrayList<Head_strs> list, Head_strs item, int len)
	{
	
		//int pos = list.size()/2;
		
		int start = 0;
		
		int end = len - 1;
		
		int pos = (start + end) /2;
		
		while(start < end)
		{
		
		
		
		int cmp_v = compare(item, list.get(pos)); 
		
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
		
		int cmp_v = compare(item, list.get(pos)); 
		
		if(cmp_v > 0)
		{
		pos = pos + 1;
		}
		
		
		
		return pos;
	}
}

	
	
