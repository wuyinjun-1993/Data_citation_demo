package edu.upenn.cis.citation.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.sort_citation_view_vec.binary_compare;
import edu.upenn.cis.citation.sort_citation_view_vec.sort_insert;


import static org.junit.Assert.*;

public class node_index_vector {
	
	public Vector<node_index> c_vec;
	
//	public Vector<String> index_vec;
//	
	public HashSet<Argument> args = new HashSet<Argument>();
//	
//	public HashSet<String> table_names;
//		
//	public String index_str;
//	
//	public String view_name_str;
//	
//	public String table_name_str;
//	
//	public String head_var_string = new String();
	
	public node_index_vector()
	{
		c_vec = new Vector<node_index>();
		
//		index_vec = new Vector<String>();
//		
//		table_names = new HashSet<String>();
		
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
	
	public node_index_vector(Vector<node_index> vec){
		
		
//		index_vec = new Vector<String>();
//		
//		Vector<String> curr_table_name_str = new Vector<String>();
//		
//		table_names = new HashSet<String>();
		
		c_vec = new Vector<node_index>();
		
//		view_name_str = new String();
//		
//		index_str = new String();
//		
//		table_name_str = new String();
		
		for(int i = 0; i<vec.size(); i++)
		{
			
			if(!c_vec.contains(vec.get(i)))
			{
//				table_names.addAll(vec.get(i).get_table_names());
				
//				insert_sort(index_vec, vec.get(i).toString());
				
				insert_sort(c_vec, vec.get(i));
				
				args.addAll(vec.get(i).args);
				
			}

		}
		
//		for(int i = 0; i< c_vec.size(); i++)
//		{
//			index_str += c_vec.get(i).toString();
//			
//			index_vec.add(c_vec.get(i).toString());
//			
//			view_name_str += c_vec.get(i).toString().replaceFirst("\\(.*\\)", "");
//			
//			table_name_str += c_vec.get(i).get_table_name_string();
//		}
		
	}
	
	public node_index_vector merge(node_index c)
	{
		
		Vector<node_index> vec_new = (Vector<node_index>) c_vec.clone();
	
		if(!vec_new.contains(c) && !this.args.containsAll(c.args))
		{
			insert_sort(vec_new, c);

		}
		
		node_index_vector c_v = new node_index_vector(vec_new);
		
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
	
	public void insert_sort(Vector<node_index> index_vec, node_index insert_str)
	{
		if(index_vec.size() == 0)
		{
			index_vec.add(insert_str);
			return;
		
		}
		
		String s1 = insert_str.toString();
		
		node_index first_str = index_vec.firstElement();
		
		String s2 = first_str.toString();
		
		if(s1.compareTo(s2) < 0)
		{
			index_vec.insertElementAt(insert_str, 0);
			
			return;
		}
		
		node_index last_str = index_vec.lastElement();
		
		s2 = last_str.toString();
		
		if(s1.compareTo(s2) > 0)
		{
			index_vec.add(insert_str);
			
			return;
		}
		
		
		int pos = sort_insert.binary_search(index_vec, insert_str, new binary_compare<node_index>(){
			
			@Override			
			public int compare(node_index a, node_index b)
			{
				String s1 = b.toString();
				
				String s2 = a.toString();

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
	
	public node_index_vector(node_index c){
		
		c_vec = new Vector<node_index>();
		
		c_vec.add(c);
		
	}
	
	
	public static node_index_vector merge(node_index_vector vec, node_index c)
	{
		
		return vec.merge(c);
	}
	
	public static node_index_vector merge(node_index_vector vec, node_index_vector c)
	{
		return vec.merge(c);
	}
	
	public node_index_vector merge(node_index_vector c)
	{
		node_index_vector updated_covering_set = this.clone();
		
		node_index_vector insert_covering_set = c.clone();
		
		for(int i = 0; i<insert_covering_set.c_vec.size(); i++)
		{
			updated_covering_set = updated_covering_set.merge(insert_covering_set.c_vec.get(i));
		}
		
		return updated_covering_set;
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

	
	public node_index_vector clone()
	{
		node_index_vector c_v = new node_index_vector();
		
		for(int i = 0; i<this.c_vec.size(); i++)
		{
			c_v.c_vec.add((node_index)c_vec.get(i).clone());
		}
	
		return c_v;
		
		
		
	}
	
	@Override
	public boolean equals(Object o)
	{
		node_index_vector c_vec = (node_index_vector) o;

		return c_vec.c_vec.equals(this.c_vec);
	}
	
	@Override
	public int hashCode()
	{
		return this.c_vec.toString().hashCode();
	}

	
	public HashSet<citation_view_vector> cal_covering_sets() throws ClassNotFoundException, SQLException
	{
		
		HashSet<citation_view_vector> covering_sets = new HashSet<citation_view_vector>();
		
		for(int i = 0; i<c_vec.size();i++)
		{
			covering_sets = join_views_curr_relation(c_vec.get(i), covering_sets);
		}
		
		return covering_sets;
	}
	
	public static HashSet<citation_view_vector> join_views_curr_relation(node_index nodes, HashSet<citation_view_vector> curr_view_com) throws ClassNotFoundException, SQLException
	{
		if(curr_view_com.isEmpty())
		{
//			if(tuples.isEmpty())
//				return new ArrayList<citation_view_vector2>();
//			else
			{
				HashSet<citation_view_vector> new_view_com = new HashSet<citation_view_vector>();
				
//				for(int i = 0; i<nodes.size(); i++)
				{
					
					for(int j = 0; j<nodes.views.size(); j++)
					{
						citation_view curr_view = nodes.views.get(j);
						
						citation_view_vector curr_views = new citation_view_vector(curr_view);
						
						new_view_com.add(curr_views);
					}
				}
				
				return new_view_com;
			}
		}
		
		else
		{
			HashSet<citation_view_vector> new_view_com = new HashSet<citation_view_vector>();
			
			
			for(int j = 0; j<nodes.views.size(); j++)
			{
				citation_view c = nodes.views.get(j);
				
				for(Iterator iter = curr_view_com.iterator();iter.hasNext();)
				{
					
					
					
					citation_view_vector old_view_com = ((citation_view_vector)iter.next()).clone();
					
					citation_view_vector view_com = citation_view_vector.merge(old_view_com, c);
					
					new_view_com.add(view_com);
				}
			}
			
			return new_view_com;
		}
	}
	
	public static void remove_duplicate_view_combinations(HashSet<citation_view_vector> c_view_template)
	{
		
		HashSet<citation_view_vector> c_view_template_cp = new HashSet<citation_view_vector>();
		
		for(Iterator it1 = c_view_template.iterator(); it1.hasNext();)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			citation_view_vector c_combination = (citation_view_vector) it1.next();
			
			int num = 0;

			
			for(Iterator it2 = c_view_template.iterator(); it2.hasNext();)
			{
//				String string = (String) iterator.next();
				
				citation_view_vector curr_combination = (citation_view_vector) it2.next();
				
//				if(str!=string)
				if(!c_combination.equals(curr_combination))
				{					
					
//					if(c_combination.c_vec.size() == 3)
//					{
//						
//						System.out.println(c_combination);
//						
//						System.out.println(curr_combination);
//						
//						int k = 0;
//						
//						k++;
//					}
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && table_names_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					{
						
						if(c_combination.table_names.equals(curr_combination.table_names))
						{														
							break;
						}
					}
					
					if(c_combination.index_str.equals(curr_combination.index_str) && c_combination.table_name_str.equals(curr_combination.table_name_str))
					{												
						break;
					}
				}
			
				num ++;
			}
			
			if(num >= c_view_template.size())
			{
				c_view_template_cp.add(c_combination);
			}
		}
		
		c_view_template.clear();
		
		c_view_template.addAll(c_view_template_cp);
		
	}
	
	static boolean table_names_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		String s1 = ".*";
		
		String s2 = c_vec1.table_name_str;
		
		for(int i = 0; i<c_vec2.c_vec.size(); i++)
		{
			
			String str = c_vec2.c_vec.get(i).get_table_name_string();
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			s1 += str + ".*";
			
		}
		
		return s2.matches(s1);
	}
	
	static boolean view_vector_contains(citation_view_vector c_vec1, citation_view_vector c_vec2)
	{
		
		String s1 = ".*";
		
		String s2 = c_vec1.index_str;
		
		for(int i = 0; i<c_vec2.index_vec.size(); i++)
		{
			String str = c_vec2.index_vec.get(i);
			
			str = str.replaceAll("\\(", "\\\\(");
			
			str = str.replaceAll("\\)", "\\\\)");
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			str = str.replaceAll("\\/", "\\\\/");
			
			s1 += str + ".*";
		}
		
		return s2.matches(s1);
	}
	
}
