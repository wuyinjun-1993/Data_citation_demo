package edu.upenn.cis.citation.citation_view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;


import static org.junit.Assert.*;

public class citation_view_vector {
	
	public Vector<citation_view> c_vec;
	
	public Vector<String> index_vec;
	
	public HashSet<Argument> head_variables = new HashSet<Argument>();
	
	public HashSet<String> table_names;
		
	public String index_str;
	
	public String view_name_str;
	
	public String table_name_str;
//	
//	public String head_var_string = new String();
	
	public citation_view_vector()
	{
		c_vec = new Vector<citation_view>();
		
		index_vec = new Vector<String>();
		
		table_names = new HashSet<String>();
		
	}
	
	public static void main(String [] args)
	{
		HashSet<String> h1 = new HashSet<String>();
		
		HashSet<String> h2 = new HashSet<String>();
		
		h1.add("123");
		
		h1.add("456");
		
		h2.add("123");
		
		System.out.println(h1.equals(h2));
		
		
	}
	
	public citation_view_vector(Vector<citation_view> vec){
		
		
		index_vec = new Vector<String>();
		
//		Vector<String> curr_table_name_str = new Vector<String>();
		
		table_names = new HashSet<String>();
		
		c_vec = new Vector<citation_view>();
		
		view_name_str = new String();
		
		index_str = new String();
		
		table_name_str = new String();
		
		for(int i = 0; i<vec.size(); i++)
		{
			
			if(!table_names.containsAll(vec.get(i).get_table_names()) || !head_variables.contains(vec.get(i).get_view_tuple().getArgs()))
			{
				table_names.addAll(vec.get(i).get_table_names());
				
//				insert_sort(index_vec, vec.get(i).toString());
				
				insert_sort(c_vec, vec.get(i));
				
				head_variables.addAll(vec.get(i).get_view_tuple().getArgs());
				
			}

		}
		
		for(int i = 0; i< c_vec.size(); i++)
		{
			index_str += c_vec.get(i).toString();
			
			index_vec.add(c_vec.get(i).toString());
			
			view_name_str += c_vec.get(i).toString().replaceFirst("\\(.*\\)", "");
			
			table_name_str += c_vec.get(i).get_table_name_string();
		}
		
	}
	
	public citation_view_vector merge(citation_view c)
	{
		
		Vector<citation_view> vec_new = (Vector<citation_view>) c_vec.clone();
				
		Vector<String> index_new = new Vector<String>();
//				
		HashSet<String> table_names_new = (HashSet<String>) table_names.clone();
		
		String index_str_new = new String();
		
		String view_name_str_new = new String();
		
		String table_name_str = new String();
		
		if(!table_names_new.containsAll(c.get_table_names()) || !head_variables.containsAll(c.get_view_tuple().getArgs()))
		{
			table_names_new.addAll(c.get_table_names());
			
//			insert_sort(index_new, c.toString());
			
			insert_sort(vec_new, c);
			
//			for(int j = 1; j<index_new.size(); j++)
//			{
//				if(index_new.get(j).compareTo(index_new.get(j - 1))< 0)
//				{
//					assertEquals(1, 0);
//				}
//			}
			
//			vec_new.add(c);
			

		}
		
		for(int i = 0; i< vec_new.size(); i++)
		{
			index_str_new += vec_new.get(i).toString();
			
			index_new.add(vec_new.get(i).toString());
			
			String str = vec_new.get(i).toString().replaceFirst("\\(.*\\)", "");
						
			view_name_str_new += str;
			
			table_name_str += vec_new.get(i).get_table_name_string();
		}
		
		citation_view_vector c_v = new citation_view_vector(vec_new, index_new, table_names_new);
		
//		c_v.head_variables = new HashSet<Argument>();
		
		c_v.head_variables = this.head_variables;
		
		c_v.head_variables.addAll(c.get_view_tuple().getArgs());
		
		c_v.index_str = index_str_new;
		
		c_v.view_name_str = view_name_str_new;
		
		c_v.table_name_str = table_name_str;
		
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
		
		
		int pos = sort_insert.binary_search(index_vec, insert_str, new binary_compare<String>(){
			
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
	
	public void insert_sort(Vector<citation_view> index_vec, citation_view insert_str)
	{
		if(index_vec.size() == 0)
		{
			index_vec.add(insert_str);
			return;
		
		}
		
		String s1 = insert_str.get_name() + insert_str.get_table_name_string();
		
		citation_view first_str = index_vec.firstElement();
		
		String s2 = first_str.get_name() + first_str.get_table_name_string();
		
		if(s1.compareTo(s2) < 0)
		{
			index_vec.insertElementAt(insert_str, 0);
			
			return;
		}
		
		citation_view last_str = index_vec.lastElement();
		
		s2 = last_str.get_name() + last_str.get_table_name_string();
		
		if(s1.compareTo(s2) > 0)
		{
			index_vec.add(insert_str);
			
			return;
		}
		
		
		int pos = sort_insert.binary_search(index_vec, insert_str, new binary_compare<citation_view>(){
			
			@Override			
			public int compare(citation_view a, citation_view b)
			{
				String s1 = b.get_name() + b.get_table_name_string();
				
				String s2 = a.get_name() + a.get_table_name_string();

				if(s2.compareTo(s1) > 0)
					return 1;
				else
				{
					if(s2.compareTo(s1) < 0)
						return -1;
					else
						return 0;
				}
			}
		} );
		
		
		index_vec.insertElementAt(insert_str, pos);
		
	}
//	public static int binary_search(Vector<citation_view> list, citation_view item, binary_compare bc)
//	{
//		
////		int pos = list.size()/2;
//		
//		int start = 0;
//		
//		int end = list.size() - 1;
//		
//		int pos = (start + end) /2;
//		
//		while(start < end)
//		{
//			
//			
//			
//			int cmp_v = bc.compare(item, list.get(pos)); 
//			
//			if(cmp_v < 0)
//			{
//				end = pos - 1;
//			}
//			else
//			{
//				if(cmp_v > 0)
//				{
//					start = pos + 1;
//				}
//				else
//					break;
//			}
//			
//			pos = (start + end) /2;
//			
//		}
//		
//		int cmp_v = bc.compare(item, list.get(pos)); 
//		
//		if(cmp_v > 0)
//		{
//			pos = pos + 1;
//		}
//		else
//		{
//			if(cmp_v == 0)
//			{
//				if(item.toString().compareTo(list.get(pos).toString()) > 0)
//				{
//					pos = pos + 1;
//				}
//			}
//		}
//		
//		
//		return pos;
//	}
	
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
		
		table_names.addAll(c.get_table_names());
		
		index_vec.add(c.get_index());
		
		table_name_str = c.get_table_name_string();
		
		index_str = c.toString();
		
		head_variables.addAll(c.get_view_tuple().getArgs());
		
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
	
	public static citation_view_vector merge(citation_view_vector vec, citation_view_vector c)
	{
		return vec.merge(c);
	}
	
	public citation_view_vector merge(citation_view_vector c)
	{
		citation_view_vector updated_covering_set = this.clone();
		
		citation_view_vector insert_covering_set = c.clone();
		
		for(int i = 0; i<insert_covering_set.c_vec.size(); i++)
		{
			updated_covering_set = updated_covering_set.merge(insert_covering_set.c_vec.get(i));
		}
		
		return updated_covering_set;
	}
	
//	static citation_view_vector merge_vector(citation_view_vector vec1, citation_view_vector vec2)
//	{
//		
//		Vector<citation_view> vec_new = (Vector<citation_view>) vec1.c_vec.clone();
//		
//		
//		vec_new.addAll((vec2.c_vec));
//		
//		Vector<String> index_new = (Vector<String>) vec1.index_vec.clone();
//		
//		index_new.addAll(vec2.index_vec);
//		
//		HashSet<String> tuple_cores_new = (HashSet<String>) vec1.table_names.clone();
//		
//		tuple_cores_new.addAll(vec2.table_names);
//		
//		return new citation_view_vector(vec_new, index_new, tuple_cores_new);
//		
//		
//	}
	
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
		
		c_v.view_name_str = view_name_str;
		
		c_v.table_name_str = table_name_str;
		
		c_v.head_variables = (HashSet<Argument>) head_variables.clone();
		
		return c_v;
		
		
		
	}
	
	@Override
	public boolean equals(Object o)
	{
		citation_view_vector c_vec = (citation_view_vector) o;
		
//		if(c_vec.c_vec.size() != this.c_vec.size())
//			return false;
//		
//		for(int i = 0; i < c_vec.c_vec.size(); i++)
//		{
//			citation_view c1 = c_vec.c_vec.get(i);
//			
//			int j = 0;
//			
//			for(j = 0; j< this.c_vec.size(); j++)
//			{
//				citation_view c2 = this.c_vec.get(j);
//				
//				if(c1.equals(c2))
//					break;
//			}
//			
//			if(j < this.c_vec.size())
//				continue;
//			else
//				return false;
//		}
//		
//		for(int i = 0; i < this.c_vec.size(); i++)
//		{
//			citation_view c1 = this.c_vec.get(i);
//			
//			int j = 0;
//			
//			for(j = 0; j< c_vec.c_vec.size(); j++)
//			{
//				citation_view c2 = c_vec.c_vec.get(j);
//				
//				if(c1.equals(c2))
//					break;
//			}
//			
//			if(j < c_vec.c_vec.size())
//				continue;
//			else
//				return false;
//		}
		
		return c_vec.index_str.equals(this.index_str);
	}
	
	@Override
	public int hashCode()
	{
		return this.index_str.hashCode();
	}

}
