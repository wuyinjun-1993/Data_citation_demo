package edu.upenn.cis.citation.reasoning2;

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
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import edu.upenn.cis.citation.set_cover_algo.cost_function;

public class reasoning_min {
  
  
  static void init_computation(HashMap<Tuple, Integer> view_mappping_id_mappings, HashMap<Tuple, Double> view_mapping_cost, HashSet<Tuple> view_mappings, Query q, HashMap<Tuple, HashSet<String>> single_subgoal_relations, HashMap<Tuple, HashSet<Argument>> single_view_head_args)
  {
    int i = 0;
    
    for(Iterator iter = view_mappings.iterator();iter.hasNext();)
    {
        Tuple tuple = (Tuple)iter.next();
        
        double cost = cost_function.cost1(tuple, q);
        
        view_mapping_cost.put(tuple, cost);
        
        single_subgoal_relations.put(tuple, tuple.getTargetSubgoal_strs());
        
        HashSet<Argument> coverted_args = new HashSet<Argument>();
        
        coverted_args.addAll(tuple.getArgs());
        
        single_view_head_args.put(tuple, coverted_args);
        
        view_mappping_id_mappings.put(tuple, i);
        
        i++;
    }
  }
  
  static void get_all_covered_subgoals_args(HashSet<String> covered_subgoals, HashSet<Argument> covered_args, HashSet<Tuple> valid_view_mappings)
  {
    for(Tuple view_mapping: valid_view_mappings)
    {
      covered_subgoals.addAll(view_mapping.getTargetSubgoal_strs());
      
      covered_args.addAll(view_mapping.getArgs());
    }
  }
  
  static HashSet<Tuple> greedy_algorithm(HashMap<Tuple, Double> view_mapping_cost, HashSet<Tuple> valid_tuple_ids, HashSet<String> curr_all_relation_names, HashSet<Argument> curr_all_head_args, 
      HashMap<Tuple, HashSet<String>> single_subgoal_relations, HashMap<Tuple, HashSet<Argument>> single_view_head_args)
  {
      
      HashSet<String> all_relations_copy = (HashSet<String>) curr_all_relation_names.clone();
      
      HashSet<Argument> all_head_args_copy = (HashSet<Argument>) curr_all_head_args.clone();
      
      HashSet<Tuple> select_set = new HashSet<Tuple>();
      
      while(!all_relations_copy.isEmpty() || !all_head_args_copy.isEmpty())
      {
          double min_cost = Double.MAX_VALUE;
          
          Tuple min_view_mapping = null;
          
          for(Tuple view_mapping: valid_tuple_ids)
          {
              double cost = view_mapping_cost.get(view_mapping);
              
              HashSet<String> intersection = (HashSet<String>) single_subgoal_relations.get(view_mapping).clone();
              
              HashSet<Argument> intersection_args = (HashSet<Argument>) single_view_head_args.get(view_mapping).clone();
              
              intersection.retainAll(all_relations_copy);
              
              intersection_args.retainAll(all_head_args_copy);
              
              double curr_cost = cost/(intersection.size() + intersection_args.size());
              
              if(curr_cost < min_cost)
              {
                  min_view_mapping = view_mapping;
                  
                  min_cost = curr_cost;
              }
          }
          
          all_relations_copy.removeAll(single_subgoal_relations.get(min_view_mapping));
          
          all_head_args_copy.removeAll(single_view_head_args.get(min_view_mapping));
          
          select_set.add(min_view_mapping);
          
//        System.out.println("index::" + index);
          
//        for(int i = 0; i<int_matrix.size(); i++)
//        {
//            if(int_matrix.get(i)[index] == 1)
//            {
//                
//                sum_array = update_sum_array(sum_array, int_matrix.get(i));
//                
//                int_matrix.remove(i);
//                
//                
//                
//                i--;
//            }
//        }
//        
//        select_set.add(index);
      }
      
//    for(int i = 0; i<tuple_relations.size(); i++)
//    {
//        
//    }
//    
//    
//    
//    int [] sum_array = get_sum(int_matrix);
//    
//    ArrayList<Integer> select_set = new ArrayList<Integer>();
//    
//    while(!int_matrix.isEmpty())
//    {
//        
//        double min_cost = Double.MAX_VALUE;
//        
//        int index = -1;
//        
//        for(int i = 0; i < view_mapping_list.size; i++)
//        {
//            double cost = view_mapping_cost.get(view_mapping_list.list[i]);
//            
//            double curr_cost = cost/sum_array[i];
//            
//            if(curr_cost < min_cost)
//            {
//                index = i;
//                
//                min_cost = curr_cost;
//            }
//        }
//        
//        for(int i = 0; i<int_matrix.size(); i++)
//        {
//            if(int_matrix.get(i)[index] == 1)
//            {
//                
//                sum_array = update_sum_array(sum_array, int_matrix.get(i));
//                
//                int_matrix.remove(i);
//                
//                
//                
//                i--;
//            }
//        }
//        
//        select_set.add(index);
//    }
      
      return select_set;
  }
  
  public static Covering_set get_valid_citation_combination(HashSet<Tuple> citation_view_ids)
  {
//    HashSet<citation_view_vector> c_combinations = new HashSet<citation_view_vector>();
      
      Vector<citation_view> c_view_vec = new Vector<citation_view>();
      
      for(Tuple valid_tuple: citation_view_ids)
      {
          if(valid_tuple.lambda_terms.size() > 0)
              {
//                Vector<String> lambda_term_values = new Vector<String>(valid_tuple.lambda_terms.size());
                  
//                for(int p = 0; p<valid_tuple.lambda_terms.size(); p++)
//                {
//                    int l_id = lambda_term_id_mapping.get(valid_tuple.lambda_terms.get(p).toString());
//                    
//                    int pos = start_pos + l_id + 1;
//                    
//                    lambda_term_values.add(rs.getString(pos));
//                    
//                }
                  
                  citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);

//                citation_view_mapping.put(key, c);
                  
                  c_view_vec.add(c);
              }   
              else
              {
                  
                  citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
                  
//                citation_view_mapping.put(key, c);
                  
                  c_view_vec.add(c);
              }
      }
      
      Covering_set view_combinations = new Covering_set(c_view_vec);
      
      return view_combinations;
  }

}
