package edu.upenn.cis.citation.data_structure;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;

public class IntList {
	
	public int[] list;
	
	public int size = 0;
	
	static int DEFAULT_INIT_SIZE = 10;
	
	private void increaseCapacity() {
	    int newCapacity = (this.list.length == 0) ? DEFAULT_INIT_SIZE
	        : this.list.length < 64 ? this.list.length * 2
	            : this.list.length < 1067 ? this.list.length * 3 / 2
	                : this.list.length * 5 / 4;
	    // never allocate more than we will ever need
//	    if (newCapacity > ArrayContainer.DEFAULT_MAX_SIZE && !allowIllegalSize) {
//	      newCapacity = ArrayContainer.DEFAULT_MAX_SIZE;
//	    }
//	    // if we are within 1/16th of the max, go to max
//	    if (newCapacity > ArrayContainer.DEFAULT_MAX_SIZE - ArrayContainer.DEFAULT_MAX_SIZE / 16
//	        && !allowIllegalSize) {
//	      newCapacity = ArrayContainer.DEFAULT_MAX_SIZE;
//	    }
	    this.list = Arrays.copyOf(this.list, newCapacity);
	  }
	
	
	
	public static void main(String[] args)
	{
		
		int len = 100;
		
		int [] arr_str = new int[len];
		
		IntList s_list = new IntList(len);
		
		Random r = new Random();
		
		for(int i = 0; i<len; i++)
		{
			arr_str[i] = r.nextInt(len);
			
			s_list.add(arr_str[i]);			
		}
		
		for(int i = 0; i<len; i++)
		{
			System.out.println(s_list.find(s_list.list[i]));
		}
	}
	
	public IntList(int [] str_list)
	{
		list = new int[str_list.length];
		
		for(int i = 0; i<str_list.length; i++)
		{
			add(str_list[i]);
		}
	}
	
	public IntList(){
		size = 0;
		
//		list = new int[DEFAULT_INIT_SIZE];
	}
	
	public IntList(int len)
	{
		list = new int[len];
	}
	
	public void add(int insert)
	{
		if(list == null || list.length == 0)
		{
			
			list = new int[DEFAULT_INIT_SIZE];
			
			list[0] = insert;
			
			size = 1;
			
			return;
		
		}
		
		if(list.length > 0 && size == 0)
		{
			list[0] = insert;
			
			size ++;
			
			return;
		}
		
		int first = list[0];
		
		if(insert< first)
		{
			
			if(size >= this.list.length)
				increaseCapacity();
			
			System.arraycopy(list, 0, list, 1, size);
			
			list[0] = insert;
			
			size ++;
//			index_vec.insertElementAt(insert, 0);
			
			return;
		}
		
		int last = list[size - 1];
		
		if(insert > last)
		{
			if(size >= this.list.length)
				increaseCapacity();
			
			list[size] = insert;
			
			size ++;
			
			return;
		}
		
		
		
		
//		int pos = Arrays.binarySearch(list, insert, s_compare);;
		
		int pos = binary_search(list, insert, size);
		
		pos = binary_search(list, insert, size);
		
		if(size >= this.list.length)
			increaseCapacity();
		
		System.arraycopy(list, pos, list, pos + 1, size - pos);
		
		list[pos] = insert;
		
		size ++;
		
//		index_vec.insertElementAt(insert, pos);
		
	}
	
	public int find(int value)
	{
		int pos = binarySearch0(list, 0,  size, value);
		
		return pos;
	}
	
	private static int binarySearch0(int[] a, int fromIndex, int toIndex, int key) {
			        int low = fromIndex;
			        int high = toIndex - 1;
			
			        while (low <= high) {
			            int mid = (low + high) >>> 1;
			            int midVal = a[mid];
			
			            if (midVal < key)
			                low = mid + 1;
			            else if (midVal > key)
			               high = mid - 1;
			            else
			                return mid; // key found
			        }
			        return -(low + 1);  // key not found.
			    }
	
	public static int compare(int a, int b)
	{
		if(a> b)
			return 1;
		else
		{
			if(a < b)
				return -1;
			else
				return 0;
		}
	}

	
	public static int binary_search(int [] list, int item, int len)
	{
		
//		int pos = list.size()/2;
		
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
	

}
