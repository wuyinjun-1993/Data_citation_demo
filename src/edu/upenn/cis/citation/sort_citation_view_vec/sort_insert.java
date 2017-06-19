package edu.upenn.cis.citation.sort_citation_view_vec;

import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

public class sort_insert {
	
	static int upper_bound = 100000;
	
	static int num = 10000;
	
	public static void main(String [] args)
	{
		Random r = new Random();
		
		HashSet<Integer> set = new HashSet<Integer>();
		
		
		set.add(1);
		
		set.add(1);
		
		while(set.size() < num)
		{
			int value = r.nextInt(upper_bound);
			
			set.add(value);
		}
		
	}
	
	
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
	
	public static <T> void insert_sort(Vector<T> index_vec, T insert)
	{
		if(index_vec.size() == 0)
		{
			index_vec.add(insert);
			return;
		
		}
		
		T first = index_vec.firstElement();
		
		if(insert.hashCode()< first.hashCode())
		{
			index_vec.insertElementAt(insert, 0);
			
			return;
		}
		
		T last = index_vec.lastElement();
		
		if(insert.hashCode() > last.hashCode())
		{
			index_vec.add(insert);
			
			return;
		}
		
		
		int pos = sort_insert.binary_search(index_vec, insert, new binary_compare<String>(){
			
			@Override			
			public int compare(String a, String b)
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
		} );
		
		
		index_vec.insertElementAt(insert, pos);
		
	}

}
