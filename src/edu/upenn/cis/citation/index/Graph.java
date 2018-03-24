package edu.upenn.cis.citation.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.citation_view.Covering_set;

public class Graph {

	Vector<node_index> tops = null;
	
	Vector<citation_view> all_views = null;
	
	Vector<node_index> nodes = new Vector<node_index>();
	
	HashMap<Integer, node_index> args_node_mapping = new HashMap<Integer, node_index>();

	HashSet<node_index_vector> node_vecs = new HashSet<node_index_vector>();
	
	HashSet<Argument> max_covered_head_args = new HashSet<Argument>();
	
	public HashSet<Covering_set> covering_sets = new HashSet<Covering_set>();
	
	HashSet<node_index_vector> node_sets = new HashSet<node_index_vector>();
	
	public static void main(String[] args)
	{
		String string = null;
		
		if(string != null && string.length() != 0)
		{
			System.out.println(string);
		}
		else
		{
			string = "string";
			
			System.out.println(string);
		}
	}
	
	public Graph(ArrayList<Tuple> tuples, HashSet<Argument> max_covered_head_args) throws ClassNotFoundException, SQLException
	{
		
		this.max_covered_head_args.addAll(max_covered_head_args);
		
		for(int i = 0; i<tuples.size(); i++)
		{
			
			Tuple curr_tuple = tuples.get(i);
			
			HashSet<Argument> args = new HashSet<Argument>();
			
			args.addAll(tuples.get(i).getArgs());
			
			args.retainAll(max_covered_head_args);
			
			if(args_node_mapping.get(args.hashCode()) == null)
			{
		
				node_index node = new node_index(args, tuples.get(i));
				
				nodes.add(node);
				
				args_node_mapping.put(args.hashCode(), node);
				
				insert2graph(node);
			}
			else
			{
				node_index node = args_node_mapping.get(args.hashCode());
				
				if(curr_tuple.lambda_terms.size() > 0)
				{
					citation_view_parametered view = new citation_view_parametered(curr_tuple.name, curr_tuple.query, curr_tuple);
					
					node.add_view_with_sam_args(view);
				}
				else
				{
					citation_view_unparametered view = new citation_view_unparametered(curr_tuple.name, curr_tuple);
					
					node.add_view_with_sam_args(view);
				}
				
				
			}
		}
		
		generate_sub_covering_sets_each_node();
	}
	
	void insert2graph(node_index node)
	{
		if(tops == null)
		{
			tops = new Vector<node_index>();
			
			tops.add(node);
		}
		else
		{
			boolean become_top = false;
			
			boolean become_child = false;
			
			Vector<node_index> parents = new Vector<node_index>();
			
			for(int i = 0; i<tops.size(); i++)
			{
				node_index top = tops.get(i);
				
				if(node.isparent(top))
				{
					tops.removeElementAt(i);
					
					become_top = true;
					
					node.add_one_child(top);
					
					i--;
				}
				
				if(top.isparent(node))
				{
					become_child = true;
					
					parents.add(top);
					
				}
			}
			
			if(become_top)
			{
				tops.add(node);
				
				return;
			}
			if(!become_top && !become_child)
			{
				tops.add(node);
				
				return;
			}
			if(become_child)
			{
				
				for(int k = 0; k<parents.size(); k++)
				{
					parents.get(k).add_child_recursively(node);
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		
		String string = new String();
		
		for(int i = 0; i<nodes.size(); i++)
		{
			string += "node " + i + "\n";
			
			string += nodes.get(i).toString() + "\n";
			
			string += "parent::" + nodes.get(i).get_parent() + "\n";
			
			string += "children::" + nodes.get(i).get_children() + "\n";
		}
		
		return string;
	}
	
	public void generate_sub_covering_sets_each_node() throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<nodes.size(); i++)
		{
			nodes.get(i).generate_sub_covering_sets();
		}
	}
	
	void generate_covering_sets_top_nodes() throws ClassNotFoundException, SQLException
	{
		HashMap<Argument, ArrayList<node_index>> arg_node_mapping = node_index.build_arg_node_mapping(tops);
		
		covering_sets = new HashSet<Covering_set>();
		
		node_sets = new HashSet<node_index_vector>();
		
		for(Iterator iter = max_covered_head_args.iterator(); iter.hasNext();)
		{
			Argument arg = (Argument) iter.next();
			
			ArrayList<node_index> nodes = arg_node_mapping.get(arg);
			
			covering_sets = join_views_curr_relation(nodes, covering_sets);
			
			node_sets = node_index.join_nodes(nodes, node_sets);
		}
		
		node_index_vector.remove_duplicate_view_combinations(covering_sets);
		
		node_index.remove_duplicate_nodes(node_sets);
	}
	
	static HashSet<Covering_set> join_views_curr_relation(ArrayList<node_index> nodes, HashSet<Covering_set> curr_view_com) throws ClassNotFoundException, SQLException
	{
		if(curr_view_com.isEmpty())
		{
//			if(tuples.isEmpty())
//				return new ArrayList<citation_view_vector2>();
//			else
			{
				HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
				
				for(int i = 0; i<nodes.size(); i++)
				{
					
					node_index curr_node = nodes.get(i);
					
					for(int j = 0; j<curr_node.views.size(); j++)
					{
						citation_view curr_view = curr_node.views.get(j);
						
						Covering_set curr_views = new Covering_set(curr_view);
						
						new_view_com.add(curr_views);
					}
				}
				
				return new_view_com;
			}
		}
		
		else
		{
			HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
			
			for(int i = 0; i<nodes.size(); i++)
			{
				
				node_index curr_node = nodes.get(i);
				
				for(int j = 0; j<curr_node.views.size(); j++)
				{
					citation_view c = curr_node.views.get(j);
					
					for(Iterator iter = curr_view_com.iterator();iter.hasNext();)
					{
						
						
						
						Covering_set old_view_com = ((Covering_set)iter.next()).clone();
						
						Covering_set view_com = Covering_set.merge(old_view_com, c);
						
						new_view_com.add(view_com);
					}
				}
				
				
			}
			
			return new_view_com;
		}
	}
	
	public void generate_all_covering_sets() throws ClassNotFoundException, SQLException
	{
		
		generate_covering_sets_top_nodes();
		
		HashSet<node_index_vector> update_node_sets = null;
		
		HashSet<node_index_vector> old_node_sets = node_sets;
		
		while(update_node_sets == null || (update_node_sets != null && update_node_sets.size() != 0))
		{
			
			update_node_sets = new HashSet<node_index_vector>();
			
			System.out.println(old_node_sets);
			
			HashSet<Covering_set> update_covering_sets = new HashSet<Covering_set>(); 
			
			for(Iterator iter = old_node_sets.iterator(); iter.hasNext();)
			{
				node_index_vector node_set = (node_index_vector) iter.next();
				
				for(int i = 0; i<node_set.c_vec.size(); i++)
				{
					
					node_index n1 = node_set.c_vec.get(i);
					
					if(n1.child_covering_set_nodes != null)
					{
						
						Vector<node_index> curr_nodes = (Vector<node_index>) node_set.c_vec.clone();
						
						curr_nodes.remove(i);
						
						node_index_vector node_set_without_curr_node = new node_index_vector(curr_nodes);
						
						for(Iterator it = n1.child_covering_set_nodes.iterator(); it.hasNext();)
						{
							node_index_vector child_node_set = (node_index_vector) it.next();
							
							node_index_vector update_node_set = node_index_vector.merge(node_set_without_curr_node, child_node_set);
							
							update_node_sets.add(update_node_set);
						}
						
						
					}
				}
			}
			
			node_index.remove_duplicate_nodes(update_node_sets);
			
			for(Iterator iter = update_node_sets.iterator(); iter.hasNext(); )
			{
				node_index_vector node_set = (node_index_vector) iter.next();
				
				update_covering_sets.addAll(node_set.cal_covering_sets());
			}
			
			node_index_vector.remove_duplicate_view_combinations(update_covering_sets);
						
			old_node_sets = update_node_sets;
			
			node_sets.addAll(update_node_sets);
			
			covering_sets.addAll(update_covering_sets);
			
			
		}
	}
	
}
