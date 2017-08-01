package edu.upenn.cis.citation.data_structure;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;

public class StringList {
	
	public String[] list;
	
	public int size = 0;
	
	static int DEFAULT_INIT_SIZE = 10;
	
	str_comparator s_compare = new str_comparator();

	
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
		
		String [] arr_str = new String[len];
		
		StringList s_list = new StringList(len);
		
		for(int i = 0; i<len; i++)
		{
			arr_str[i] = String.valueOf(i);
			
			s_list.add(arr_str[i]);			
		}
		
		for(int i = 0; i<len; i++)
		{
			System.out.println(s_list.find(s_list.list[i]));
		}
	}
	
	public StringList(String [] str_list)
	{
		list = new String[str_list.length];
		
		for(int i = 0; i<str_list.length; i++)
		{
			add(str_list[i]);
		}
	}
	
	public StringList(int len)
	{
		list = new String[len];
	}
	
	public StringList()
	{
		size = 0;
	}
	
	public void add(String insert)
	{
		if(list == null || list.length == 0)
		{
			
			list = new String[DEFAULT_INIT_SIZE];
			
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
		
		String first = list[0];
		
		if(insert.hashCode()< first.hashCode())
		{
			
			if(size >= this.list.length)
				increaseCapacity();
			
			System.arraycopy(list, 0, list, 1, size);
			
			list[0] = insert;
			
			size ++;
//			index_vec.insertElementAt(insert, 0);
			
			return;
		}
		
		String last = list[size - 1];
		
		if(insert.hashCode() > last.hashCode())
		{
			if(size >= this.list.length)
				increaseCapacity();
			
			list[size] = insert;
			
			size ++;
			
			return;
		}
		
		
		
		
//		int pos = Arrays.binarySearch(list, insert, s_compare);;
		
		int pos = sort_insert.binary_search(list, insert, size, new binary_compare<String>(){
			
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
		
		if(size >= this.list.length)
			increaseCapacity();
		
		System.arraycopy(list, pos, list, pos + 1, size - pos);
		
		list[pos] = insert;
		
		size ++;
		
//		index_vec.insertElementAt(insert, pos);
		
	}
	
	public int find(String value)
	{
		int pos = binarySearch0(list, 0, size, value);
		
		return pos;
	}
	
	private static int binarySearch0(String[] a, int fromIndex, int toIndex, String key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            String midVal = a[mid];

            if (midVal.hashCode() < key.hashCode())
                low = mid + 1;
            else if (midVal.hashCode() > key.hashCode())
               high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }
	
	private class str_comparator implements Comparator<String>
	{

		@Override
		public int compare(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
			if(arg0.hashCode() > arg1.hashCode())
				return 1;
			else
			{
				if(arg0.hashCode() < arg1.hashCode())
					return -1;
				else
					return 0;
			}
			
		}
	}
	

}
