package edu.upenn.cis.citation.citation_view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_string_index;

public class citation_view_vector {
	
	public Vector<citation_view> c_vec;
	
	public Vector<String> index_vec;
	
	public HashSet<String> table_names;
		
	public String index_str;
	
	public String table_name_str;
	
	public citation_view_vector()
	{
		c_vec = new Vector<citation_view>();
		
		index_vec = new Vector<String>();
		
		table_names = new HashSet<String>();
		
	}
	
	public citation_view_vector(Vector<citation_view> vec){
		
		
		index_vec = new Vector<String>();
		
//		Vector<String> curr_table_name_str = new Vector<String>();
		
		table_names = new HashSet<String>();
		
		c_vec = new Vector<citation_view>();
		
		table_name_str = new String();
		
		index_str = new String();
		
		for(int i = 0; i<vec.size(); i++)
		{
			
			if(!table_names.containsAll(vec.get(i).get_table_names()))
			{
				table_names.addAll(vec.get(i).get_table_names());
				
				insert_sort(index_vec, vec.get(i).toString());
								
				c_vec.add(vec.get(i));
				
			}

		}
		
		for(int i = 0; i< index_vec.size(); i++)
		{
			index_str += index_vec.get(i);
			
			table_name_str += index_vec.get(i).replaceFirst("\\(.*\\)", "");
		}
		
	}
	
	public citation_view_vector merge(citation_view c)
	{
		
		Vector<citation_view> vec_new = (Vector<citation_view>) c_vec.clone();
				
		Vector<String> index_new = (Vector<String>) index_vec.clone();
//				
		HashSet<String> table_names_new = (HashSet<String>) table_names.clone();
		
		String index_str_new = new String();
		
		String table_name_str_new = new String();
		
		if(!table_names_new.containsAll(c.get_table_names()))
		{
			table_names_new.addAll(c.get_table_names());
			
			insert_sort(index_new, c.toString());
			
			vec_new.add(c);
			

		}
		
		for(int i = 0; i< index_new.size(); i++)
		{
			index_str_new += index_new.get(i);
			
			String str = index_new.get(i).replaceFirst("\\(.*\\)", "");
						
			table_name_str_new += str;
		}
		
		citation_view_vector c_v = new citation_view_vector(vec_new, index_new, table_names_new);
		
		c_v.index_str = index_str_new;
		
		c_v.table_name_str = table_name_str_new;
		
		return c_v;
	}
	
	public void insert_sort(Vector<String> index_vec, String insert_str)
	{
		if(index_vec.size() == 0)
		{
			index_vec.add(insert_str);
			return;
		
		}
		
		String first_str = index_vec.firstElement();
		
		if(insert_str.compareTo(first_str) < 0)
		{
			index_vec.insertElementAt(insert_str, 0);
			
			return;
		}
		
		String last_str = index_vec.lastElement();
		
		if(insert_str.compareTo(last_str) > 0)
		{
			index_vec.add(insert_str);
			
			return;
		}
		
		
		int pos = sort_string_index.binary_search(index_vec, insert_str, new binary_compare<String>(){
			
			@Override			
			public int compare(String a, String b)
			{
				if(a.compareTo(b) > 0)
					return 1;
				else
				{
					if(a.compareTo(b) < 0)
						return -1;
					else
						return 0;
				}
			}
		} );
		
		
		index_vec.insertElementAt(insert_str, pos);
		
	}
	
	public citation_view_vector(Vector<citation_view> vec, Vector<String> index_vec, HashSet<String> table_names){
		
		this.c_vec = vec;
		
		this.index_vec = index_vec;
		
		this.table_names = table_names;
//		this.view_strs = tuple_cores;
		
	}
	
	public citation_view_vector(citation_view c){
		
		c_vec = new Vector<citation_view>();
		
		c_vec.add(c);
		
		index_vec = new Vector<String>();
		
		table_names = new HashSet<String>();
		
		table_names.add(c.toString());
		
		index_vec.add(c.get_index());
		
	}
	
	
	public static citation_view_vector merge(citation_view_vector vec, citation_view c)
	{
		
		return vec.merge(c);
		
//		if(vec.view_strs.contains(c.toString()))
//		{
//			return vec;
//		}
//		else
//			return merge_vector(new citation_view_vector(c), vec);
	}
	
	static citation_view_vector merge_vector(citation_view_vector vec1, citation_view_vector vec2)
	{
		
		Vector<citation_view> vec_new = (Vector<citation_view>) vec1.c_vec.clone();
		
		
		vec_new.addAll((vec2.c_vec));
		
		Vector<String> index_new = (Vector<String>) vec1.index_vec.clone();
		
		index_new.addAll(vec2.index_vec);
		
		HashSet<String> tuple_cores_new = (HashSet<String>) vec1.table_names.clone();
		
		tuple_cores_new.addAll(vec2.table_names);
		
		return new citation_view_vector(vec_new, index_new, tuple_cores_new);
		
		
	}
	
	@Override
	public String toString()
	{
		String str = new String();
		
		for(int i = 0; i<c_vec.size(); i++)
		{
			
			if(i >= 1)
				str += "*";
			str += c_vec.get(i).toString();
		}
		
		return str;
	}
	
	public static void main(String [] args)
	{
		
		
		
	}
	
	public citation_view_vector clone()
	{
		citation_view_vector c_v = new citation_view_vector();
		
		for(int i = 0; i<this.c_vec.size(); i++)
		{
			c_v.c_vec.add(c_vec.get(i).clone());
		}
		
		for(int i = 0; i<this.index_vec.size(); i++)
		{
			c_v.index_vec.add(index_vec.get(i));
		}
		
		c_v.table_names.addAll(table_names);
		
		c_v.index_str = index_str;
		
		c_v.table_name_str = table_name_str;
		
		return c_v;
		
		
		
	}

}
