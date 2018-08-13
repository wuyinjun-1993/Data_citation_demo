package edu.upenn.cis.citation.covering_sets_calculation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.set_cover_algo.cost_function;

public class Covering_sets_min {
  
  
  public static HashMap<Tuple, Double> Cal_cost_per_view_mappings(HashSet<Tuple> view_mappings, Query q)
  {
    HashMap<Tuple, Double> view_mapping_cost_mappings = new HashMap<Tuple, Double>();
    
    for(Tuple tuple: view_mappings)
    {
      double cost = cost_function.cost1(tuple, q);
      
      view_mapping_cost_mappings.put(tuple, cost);
    }
    
    return view_mapping_cost_mappings;
  }

  public static int[] init(HashSet<Tuple> viewTuples, HashMap<Tuple, Integer> view_mapping_id_mappings)
  {
    int [] view_mapping_global_valid = new int[viewTuples.size()];
    
    for(int i = 0; i<view_mapping_global_valid.length; i++)
    {
        view_mapping_global_valid[i] = 1;
    }
    
    int i = 0;
    
    for(Tuple tuple: viewTuples)
    {
      view_mapping_id_mappings.put(tuple, i);
      
      i++;
    }
    
    return view_mapping_global_valid;
  }
  
  
//  static ArrayList<Integer> greedy_algorithm(HashMap<Tuple, Double>view_mapping_cost, HashMap<Tuple, Integer> view_mapping_id_mappings, ArrayList<Tuple> valid_tuple_ids, HashSet<String> curr_all_relation_names, HashSet<Argument> curr_all_head_args)
//  {
//      
////      Collections.sort(valid_tuple_ids);
//      
//      HashSet<String> all_relations_copy = (HashSet<String>) curr_all_relation_names.clone();
//      
//      HashSet<Argument> all_head_args_copy = (HashSet<Argument>) curr_all_head_args.clone();
//      
////    ArrayList<HashSet<String>> single_relation_names_copy = new ArrayList<HashSet<String>>();;
////    
////    for(int i = 0; i < single_subgoal_relations.size(); i++)
////    {
////        single_relation_names_copy.add((HashSet<String>)single_subgoal_relations.get(i).clone());
////    }
//      
//      ArrayList<Integer> select_set = new ArrayList<Integer>();
//      
//      while(!all_relations_copy.isEmpty() || !all_head_args_copy.isEmpty())
//      {
//          double min_cost = Double.MAX_VALUE;
//          
//          int index = -1;
//          
//          for(int i = 0; i < valid_tuple_ids.size(); i++)
//          {
//              double cost = view_mapping_cost.get(valid_tuple_ids.get(i));
//              
//              HashSet<String> intersection = (HashSet<String>) single_subgoal_relations.get(valid_tuple_ids.get(i)).clone();
//              
//              Vector<Argument> intersection_args = (Vector<Argument>) single_view_head_args.get(valid_tuple_ids.get(i)).clone();
//              
//              intersection.retainAll(all_relations_copy);
//              
//              intersection_args.retainAll(all_head_args_copy);
//              
//              double curr_cost = cost/(intersection.size() + intersection_args.size());
//              
//              if(curr_cost < min_cost)
//              {
//                  index = i;
//                  
//                  min_cost = curr_cost;
//              }
//          }
//          
//          all_relations_copy.removeAll(single_subgoal_relations.get(valid_tuple_ids.get(index)));
//          
//          all_head_args_copy.removeAll(single_view_head_args.get(valid_tuple_ids.get(index)));
//          
//          select_set.add(view_mapping_id_mappings.get(valid_tuple_ids));
//          
//      }
//      
//      return select_set;
//  }
//  
  static ArrayList<Tuple> reasoning_single_tuple(HashSet views, HashMap<Tuple, Integer> view_mapping_id_mappings, int [] view_mapping_global_valid) throws ClassNotFoundException, SQLException
  {
      
      ArrayList<Tuple> valid_tuple_ids = new ArrayList<Tuple>();
      
      for(Iterator iter = views.iterator(); iter.hasNext();)
      {
          Tuple tuple = (Tuple) iter.next();
          
          valid_tuple_ids.add(tuple);
          
          int id = view_mapping_id_mappings.get(tuple);
          
          view_mapping_global_valid[id] += 1;
          
      }
      
      for(int i = 0; i<view_mapping_global_valid.length; i++)
      {
          view_mapping_global_valid[i] = view_mapping_global_valid[i]/2;
      }
      
      return valid_tuple_ids;

      
//    Tuple_reasoning1_min_test.remove_duplicate_view_combinations_final(c_view_template);
  }
}
