package edu.upenn.cis.citation.covering_sets_calculation1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view1.Covering_set;
import edu.upenn.cis.citation.citation_view1.citation_view;
import edu.upenn.cis.citation.citation_view1.citation_view_parametered;
import edu.upenn.cis.citation.citation_view1.citation_view_unparametered;

public class Join_covering_sets {
  
  public static HashSet<Covering_set> join_operation(HashSet<Covering_set> c_combinations, HashSet<Covering_set> insert_citations)
  {
    if(insert_citations.isEmpty())
    {
      return c_combinations;
    }
    
    if(c_combinations.isEmpty())
      return insert_citations;
    
      HashSet<Covering_set> updated_c_combinations = new HashSet<Covering_set>();
      
      for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
      {
          Covering_set curr_combination1 = (Covering_set) iter.next();
          
          HashSet<Covering_set> iterated_covering_sets = new HashSet<Covering_set>();
                          
          for(Iterator it = insert_citations.iterator(); it.hasNext();)
          {
              Covering_set curr_combination2 = (Covering_set)it.next(); 
              
              iterated_covering_sets.add(curr_combination2);
              
              Covering_set new_citation_vec = curr_combination2.clone();

              Covering_set new_covering_set = curr_combination1.merge(new_citation_vec);

              remove_duplicate(updated_c_combinations, new_covering_set);
              
          }
      }
      
      return updated_c_combinations;
  }
  
  
  public static HashSet<Covering_set> join_views_curr_relation(ArrayList<Tuple> tuples, HashSet<Covering_set> curr_view_com)
  {
      if(curr_view_com.isEmpty())
      {
          if(tuples.isEmpty())
              return new HashSet<Covering_set>();
          else
          {
              HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
              
              for(int i = 0; i<tuples.size(); i++)
              {
                  
                  Tuple valid_tuple = (Tuple) tuples.get(i);
                  
                  if(valid_tuple.lambda_terms.size() > 0)
                  {
                      
                      citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);
                      
                      Covering_set curr_views = new Covering_set(c);
                      
                      remove_duplicate(new_view_com, curr_views);
                  }   
                  else
                  {
                      
                      citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
                      
                      Covering_set curr_views = new Covering_set(c);
                      
                      remove_duplicate(new_view_com, curr_views);
                  }
              }
              
              return new_view_com;
          }
      }
      
      else
      {
          HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
          
          for(int i = 0; i<tuples.size(); i++)
          {
              Tuple valid_tuple = (Tuple) tuples.get(i);
              
              citation_view c = null;
              
              if(valid_tuple.lambda_terms.size() > 0)
              {
                  
                  c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);
              }   
              else
              {
                  
                  c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
              }
              
              for(Iterator iter = curr_view_com.iterator(); iter.hasNext();)
              {
                  Covering_set old_view_com = (Covering_set)iter.next();
                  
                  Covering_set old_view_com_copy = old_view_com.clone(); 
                  
                  Covering_set view_com = Covering_set.merge(old_view_com_copy, c);
                  
                  remove_duplicate(new_view_com, view_com);
              }
          }
          
          return new_view_com;
      }
  }
  
  public static HashSet<String> convert_citation_view2string(HashSet<citation_view> covering_sets)
  {
    HashSet<String> covering_set_strings = new HashSet<String>();
    
    for(citation_view c: covering_sets)
    {
      covering_set_strings.add(c.get_name());
    }
    
    return covering_set_strings;
  }
  
  public static HashSet<Covering_set> remove_duplicate(HashSet<Covering_set> c_combinations, Covering_set c_view)
  {
      boolean removed = false;
      
      if(c_combinations.contains(c_view))
      {
        return c_combinations;
      }
              
      for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
      {
                      
          Covering_set c_combination = (Covering_set) iter.next();
          
          Covering_set curr_combination = c_view;
          
          if(Covering_set.contains(c_combination.tuple_index, curr_combination.tuple_index) && Covering_set.contains(curr_combination.arg_name_index,  c_combination.arg_name_index) && Covering_set.contains(curr_combination.table_name_index, c_combination.table_name_index))
          {
              iter.remove();   
              
          }
          
          if(Covering_set.contains(curr_combination.tuple_index, c_combination.tuple_index) && Covering_set.contains(c_combination.arg_name_index,  curr_combination.arg_name_index) && Covering_set.contains(c_combination.table_name_index, curr_combination.table_name_index))
          {
            
            removed = true;
            
            break;
          }
          
      }
      
      if(!removed)
      {
        c_combinations.add(c_view);
      }
              
      return c_combinations;
  }
  
  
  static HashMap<String, Integer> build_query_subgoal_id_mappings(Query query)
  {
    HashMap<String, Integer> subgoal_id_mappings = new HashMap<String, Integer>();
    
    for(int i = 0; i<query.body.size(); i++)
    {
      Subgoal subgoal = (Subgoal) query.body.get(i);
      
      subgoal_id_mappings.put(subgoal.name, i);
    }
    
    return subgoal_id_mappings;
  }
  
  
  public static ArrayList<int[]> get_valid_view_mappings(ArrayList<HashSet<Covering_set>> covering_sets_per_attributes, Vector<Argument> args, Query query, ArrayList<Tuple>[] valid_view_mappings_per_head_var, HashMap<Tuple, Integer> tuple_ids, HashMap<Tuple, ArrayList<Integer>> view_mapping_query_arg_ids_mappings)
  {
    
//    System.out.println(tuple_ids);
    
    HashMap<String, Integer> query_subgoal_id_mappings = build_query_subgoal_id_mappings(query);
    
    HashSet<HashSet<Tuple>> all_tuples = new HashSet<HashSet<Tuple>>();
    
    ArrayList<int[]> tuple_index = new ArrayList<int[]>();
    
    for(int i = 0; i<valid_view_mappings_per_head_var.length; i++)
    {
      ArrayList<Tuple> valid_view_mappings = valid_view_mappings_per_head_var[i];
      
//      valid_view_mappings.retainAll(valid_view_mappings_schema_level);
      
//      Set<Single_view> views = all_possible_view_mappings.keySet();
//      
//      HashSet<Tuple> curr_tuples = new HashSet<Tuple>();
//            
//      for(Iterator iter = views.iterator(); iter.hasNext();)
//      {
//        Single_view view = (Single_view) iter.next();
//        
//        HashSet<Tuple> tuples1 = valid_view_mappings.get(view);
//        
//        HashSet<Tuple> tuples2 = all_possible_view_mappings.get(view);
//        
//        if(tuples2 == null)
//        {
//          iter.remove();
//          
//          continue;
//        }
//        
//        tuples1.retainAll(tuples2);
//        
//        if(tuples1.isEmpty())
//          iter.remove();
//        
//        curr_tuples.addAll(tuples1);
//        
//      }
      
//      if(all_tuples.contains(valid_view_mappings))
//      {
//        valid_view_mappings_per_head_var.remove(i);
//        
//        i--;
//      }
//      else
      {
//        all_tuples.add(valid_view_mappings);
        
        int [] curr_tuple_index = new int[tuple_ids.size()];
        
        for(int k = 0; k<curr_tuple_index.length; k++)
        {
          curr_tuple_index[k] = 0;
        }
        
        HashSet<Covering_set> curr_covering_sets = new HashSet<Covering_set>();
        
        for(Tuple tuple: valid_view_mappings)
        {
          int id = tuple_ids.get(tuple);
          
          
          curr_tuple_index[id] = 1;
          
          Tuple valid_tuple = (Tuple) tuple.clone();
          
          valid_tuple.args.retainAll(args);
              
          
          long [] tuple_id_contained = new long[(tuple_ids.size() + Long.SIZE - 1)/Long.SIZE];
          
          tuple_id_contained[id/Long.SIZE] |= (1L << (id % Long.SIZE));
          
          int arg_size = (query.head.has_agg)?query.head.agg_args.size():query.head.args.size(); 
          
          if(valid_tuple.lambda_terms.size() > 0)
          {
              
              citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple, query_subgoal_id_mappings, arg_size, view_mapping_query_arg_ids_mappings, tuple_ids);
              
              Covering_set curr_views = new Covering_set(c, tuple_id_contained);
              
              remove_duplicate(curr_covering_sets, curr_views);
              
//              curr_covering_sets.add(curr_views);
              
              
              
          }   
          else
          {
              
              citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple, query_subgoal_id_mappings, arg_size, view_mapping_query_arg_ids_mappings, tuple_ids);
              
              Covering_set curr_views = new Covering_set(c, tuple_id_contained);
              
              remove_duplicate(curr_covering_sets, curr_views);
          }
        }
        
        covering_sets_per_attributes.add(curr_covering_sets);
        
        tuple_index.add(curr_tuple_index);
      }
    }
    
    return tuple_index;
  }
  
  

}
