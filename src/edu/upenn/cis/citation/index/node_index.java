package edu.upenn.cis.citation.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.Covering_set;

public class node_index {
	
	public Vector<citation_view> views = new Vector<citation_view>();
	
	public HashSet<Argument> args = new HashSet<Argument>();
	
	public Vector<node_index> children = null;
	
	public Vector<node_index> parent = null;
		
	public HashSet<node_index_vector> child_covering_set_nodes = null;
	
	
	public node_index(HashSet<Argument> args, Tuple tuple) throws ClassNotFoundException, SQLException
	{
		
		if(tuple.lambda_terms.size() > 0)
		{
			citation_view_parametered view = new citation_view_parametered(tuple.name, tuple.query, tuple);
			
			views.add(view);
			
		}
		else
		{
			citation_view_unparametered view = new citation_view_unparametered(tuple.name, tuple);
			
			views.add(view);
		}
		
		this.args.addAll(args) ;
	}
	
	public node_index()
	{
		
	}
	
	public void add_view_with_sam_args(citation_view view)
	{
		views.add(view);
	}

	public void add_one_child(node_index node)
	{
		if(children == null)
		{
			children = new Vector<node_index>();
			
		}
		if(node.parent == null)
		{
			node.parent = new Vector<node_index>();
		}
		
		children.add(node);
		
		node.parent.add(this);
	}
	
	public void add_child_recursively(node_index node)
	{
		if(children == null)
		{
			this.add_one_child(node);
			
			return;
		}
		
		
		
		Vector<node_index> curr_children = children;

		int i = 0;
		
		boolean isCurr_child = false;
		
		boolean isGrand_child = false;
		
		Vector<node_index> intersect_siblings = new Vector<node_index>();
		
		for(i = 0; i<curr_children.size(); i++)
		{
			node_index curr_node = curr_children.get(i);
			
			if(node.isparent(curr_node))
			{
				node.add_one_child(curr_node);
				
				curr_children.removeElementAt(i);
				
				curr_node.parent.remove(this);

				i--;
				
				isCurr_child = true;
				
				continue;
			}
			
			
			if(curr_node.isparent(node))
			{
//				curr_children = curr_node.children;
				
				if(curr_node.children == null)
				{
					curr_node.add_one_child(node);
				}
				else
				{
					curr_node.add_child_recursively(node);
				}
				
				isGrand_child = true;
				
				continue;
			}
			
			HashSet<Argument> curr_node_args = (HashSet<Argument>) curr_node.args.clone();
			
			curr_node_args.retainAll(node.args);
			
			if(curr_node_args.size() > 0)
			{
				curr_node.find_child_for_siblings(node);
			}
		}
		
		if(isCurr_child)
			this.add_one_child(node);
		if(!isCurr_child && !isCurr_child)
			this.add_one_child(node);
			
	}
	
	public void find_child_for_siblings(node_index sibling)
	{
		if(this.children == null)
			return;
		
		for(int i = 0;i < this.children.size(); i++)
		{
			node_index child = this.children.get(i);
			
			if(sibling.isparent(child))
			{
				sibling.add_one_child(child);
			}
			
			HashSet<Argument> args = (HashSet<Argument>) child.args.clone();
			
			args.retainAll(sibling.args);
			
			if(args.size() > 0)
			{
				child.find_child_for_siblings(sibling);
			}
		}
	}
	
	public boolean isparent(node_index node)
	{
		return this.args.containsAll(node.args);
	}
	
	@Override
	public String toString()
	{
		String str = new String();
		
		for(int i = 0; i<views.size(); i++)
		{
			if(i >= 1)
				str += populate_db.separator;
			
			str += views.get(i).get_name() + views.get(i).get_table_name_string();
		}
		
//		str += "\n";
//				
//		str += args.toString();
		
		return str;
	}
	
	public String get_children()
	{
		String str = "children:::";
		
		if(children != null)
		for(int i = 0; i<children.size(); i++)
		{
			if(i >= 1)
				str += populate_db.separator;
			
			str += children.get(i).views;
		}
		
		return str;
	}
	
	public String get_parent()
	{
		String str = "parent:::";
		
		if(parent != null)
		for(int i = 0; i<parent.size(); i++)
		{
			if(i >= 1)
				str += populate_db.separator;
			
			str += parent.get(i).views;
		}
		
		return str;
	}
	
	@Override
	public int hashCode()
	{
		String string = views.toString() + populate_db.separator + args.toString();
		
		return string.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		node_index node = (node_index) obj;
		
		return (this.hashCode() == node.hashCode());
	}
	
	public void generate_sub_covering_sets() throws ClassNotFoundException, SQLException
	{
		HashSet<Argument> all_args = new HashSet<Argument>();
				
		if(children != null)
		for(int i = 0; i<children.size(); i++)
		{
			
			HashSet<Argument> args = children.get(i).args;
			
			all_args.addAll(args);
		}
		
		if(this.args.equals(all_args))
		{
			HashMap<Argument, ArrayList<node_index>> arg_node_mapping = build_arg_node_mapping(children);
						
			child_covering_set_nodes = new HashSet<node_index_vector>();
			
			for(Iterator iter = all_args.iterator(); iter.hasNext();)
			{
				Argument arg = (Argument) iter.next();
				
				ArrayList<node_index> nodes = arg_node_mapping.get(arg);
								
				HashSet<node_index_vector> update_covering_set_nodes = join_nodes(nodes, child_covering_set_nodes);
				
				child_covering_set_nodes = update_covering_set_nodes;
			}
			
			remove_duplicate_nodes(child_covering_set_nodes);
			
		}
	}
	
	public static HashSet<node_index_vector> join_nodes(ArrayList<node_index> nodes, HashSet<node_index_vector> curr_child_covering_set_nodes)
	{
		if(curr_child_covering_set_nodes.size() == 0)
		{
			for(int i = 0; i<nodes.size(); i++)
			{
				node_index node = nodes.get(i);
				
				node_index_vector node_list = new node_index_vector(node);
								
				curr_child_covering_set_nodes.add(node_list);
			}
			
			return curr_child_covering_set_nodes;
		}
		else
		{
			
			HashSet<node_index_vector> new_child_covering_set_nodes = new HashSet<node_index_vector>();
			
			for(Iterator iter = curr_child_covering_set_nodes.iterator(); iter.hasNext();)
			{
				node_index_vector node_list = (node_index_vector) iter.next();
				
				for(int j = 0; j<nodes.size(); j++)
				{
					node_index curr_node = nodes.get(j);
										
					new_child_covering_set_nodes.add(node_list.merge(curr_node));
				}
				
			}
			
			return new_child_covering_set_nodes;
		}
		
	}
	
	
	
	
	
	public static void remove_duplicate_nodes(HashSet<node_index_vector> c_view_template)
	{
		
		HashSet<node_index_vector> c_view_template_cp = new HashSet<node_index_vector>();
		
		for(Iterator it1 = c_view_template.iterator(); it1.hasNext();)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			node_index_vector c_combination = (node_index_vector) it1.next();
			
			int num = 0;

			
			for(Iterator it2 = c_view_template.iterator(); it2.hasNext();)
			{
//				String string = (String) iterator.next();
				
				node_index_vector curr_combination = (node_index_vector) it2.next();
				
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
					if((c_combination.c_vec.containsAll(curr_combination.c_vec)))
					{
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{														
							break;
						}
					}
					
//					if(c_combination.index_str.equals(curr_combination.index_str) && c_combination.table_name_str.equals(curr_combination.table_name_str))
//					{												
//						break;
//					}
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
	
	
	
	public static HashMap<Argument, ArrayList<node_index>> build_arg_node_mapping(Vector<node_index> children)
	{
		HashMap<Argument, ArrayList<node_index>> arg_node_mapping = new HashMap<Argument, ArrayList<node_index>>();
		
		for(int i = 0; i<children.size(); i++)
		{
			
			HashSet<Argument> args = children.get(i).args; 
			
			for(Iterator iter = args.iterator(); iter.hasNext();)
			{
				Argument arg = (Argument) iter.next();
				
				ArrayList<node_index> mapped_nodes = arg_node_mapping.get(arg);
				
				if(mapped_nodes == null)
				{
					mapped_nodes = new ArrayList<node_index>();
					
					mapped_nodes.add(children.get(i));
					
					arg_node_mapping.put(arg, mapped_nodes);
				}
				else
				{
					mapped_nodes.add(children.get(i));
					
					arg_node_mapping.put(arg, mapped_nodes);
				}
			}
		}
		
		return arg_node_mapping;
	}
	
	@Override
	public Object clone()
	{
		node_index node = new node_index();
		
		
		Vector<citation_view> views = (Vector<citation_view>) this.views.clone();
		
		HashSet<Argument> args = (HashSet<Argument>) this.args.clone();
		
		Vector<node_index> children = null;
		
		if(this.children != null)
			children = (Vector<node_index>) this.children.clone();
		
		Vector<node_index> parent = null;
		
		if(this.parent != null)
		{
			parent = (Vector<node_index>) this.parent.clone();
		}
		
		HashSet<node_index_vector> child_covering_set_nodes = null;
		
		if(this.child_covering_set_nodes != null)
		{
			child_covering_set_nodes = (HashSet<node_index_vector>) this.child_covering_set_nodes.clone();
		}
		
		node.views = views;
		
		node.args = args;
		
		node.children = children;
		
		node.parent = parent;
				
		node.child_covering_set_nodes = child_covering_set_nodes;
		
		return node;
		
	}
}
