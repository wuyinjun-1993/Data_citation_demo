package edu.upenn.cis.citation.schema_level_reasoning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.CoreCover;
import edu.upenn.cis.citation.Corecover.Database;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Corecover.UserLib;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.covering_sets_calculation.Covering_sets_clustering_by_ML;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.datalog.Query_converter;

public class Schema_reasoning_with_agg {
  
  static void build_query_head_variable_mapping(Query query, HashMap<String, ArrayList<Integer>> head_variable_query_mapping)
  {
      for(int i = 0; i<query.head.args.size(); i++)
      {
          Argument head_arg = (Argument)query.head.args.get(i);
          
          String head_arg_str = head_arg.toString();
          
          int partition_id = head_arg_str.indexOf(populate_db.separator);
          
          String relation_name = head_arg_str.substring(0, partition_id);
          
          ArrayList<Integer> head_variable_strs = head_variable_query_mapping.get(relation_name);
          
          if(head_variable_strs == null)
          {
              head_variable_strs = new ArrayList<Integer>();
          }
          
          head_variable_strs.add(i);
          
          head_variable_query_mapping.put(relation_name, head_variable_strs);
      }
      
  }
  
  
  public static ArrayList<Tuple>[] get_curr_view_mappings_per_head_variables(HashSet<Tuple> view_mappings, ArrayList<Tuple>[] view_mappings_per_head_variable)
  {
    ArrayList<Tuple>[] curr_view_mappings_per_head_variables = new ArrayList[view_mappings_per_head_variable.length];
    
    for(int i = 0; i<curr_view_mappings_per_head_variables.length; i++)
    {
      ArrayList<Tuple> curr_view_mapping_sets = (ArrayList<Tuple>) view_mappings_per_head_variable[i].clone();
      
      curr_view_mapping_sets.retainAll(view_mappings);
      
      curr_view_mappings_per_head_variables[i] = curr_view_mapping_sets;
    }
    
    return curr_view_mappings_per_head_variables;
  }
  
  public static double[][] cal_distances(HashSet<Tuple> view_mappings, ArrayList<Tuple>[] curr_view_mappings_per_head_variables)
  {
    int id = 0;
    
    ArrayList<int[]> tuple_index = new ArrayList<int[]>();
    
    HashMap<Tuple, Integer> view_mapping_id_mappings = new HashMap<Tuple, Integer>();
    
    for(Tuple view_mapping: view_mappings)
    {
      view_mapping_id_mappings.put(view_mapping, id);
      
      id++;
    }
    
    for(int i = 0; i<curr_view_mappings_per_head_variables.length; i++)
    {
      int [] tuple_ids = new int[view_mapping_id_mappings.size()];
      
      for(int j = 0; j<curr_view_mappings_per_head_variables[i].size(); j++)
      {
        tuple_ids[view_mapping_id_mappings.get(curr_view_mappings_per_head_variables[i].get(j))] = 1;
      }
      
      tuple_index.add(tuple_ids);
    }
    
    return Covering_sets_clustering_by_ML.cal_distances(tuple_index);
    
  }
  
  static void build_head_variable_mapping(HashMap<String, HashSet<Integer>> relation_arg_mapping, ArrayList<Tuple>[]head_variable_view_mapping, Tuple tuple, Query q)
  {
    
    for(int i = 0; i<tuple.getArgs().size(); i++)
    {
                      
          Argument target_attr_name = (Argument) tuple.getArgs().get(i);
          
          String relation_name = target_attr_name.relation_name;
          
          int target_attr_name_id = q.head.args.indexOf(target_attr_name);
          
          if(target_attr_name_id < 0)
            continue;
          
          if(relation_arg_mapping.get(relation_name) == null)
          {
              HashSet<Integer> args = new HashSet<Integer>();
              
              args.add(target_attr_name_id);
              
              relation_arg_mapping.put(relation_name, args);
          }
          else
          {
              relation_arg_mapping.get(relation_name).add(target_attr_name_id);
          }   
          
    }
    
    if(!q.head.has_agg)
    {
      for(int i = 0; i<tuple.getArgs().size(); i++)
      {
                      
          Argument target_attr_name = (Argument) tuple.getArgs().get(i);
          
          int id = q.head.args.indexOf(target_attr_name);
          
          if(id < 0)
          {
            continue;
          }
          
          ArrayList<Tuple> tuples = head_variable_view_mapping[id];
          
          if(tuples == null)
          {
              tuples = new ArrayList<Tuple>();
              
              tuples.add(tuple);
          }
          else
              tuples.add(tuple);
          
          tuple.covered_arg_ids.add(id);
          
          head_variable_view_mapping[id] = tuples;
      }
    }
    else
    {
      if(tuple.getArgs().containsAll(q.head.args))
      {
        for(int i = 0; i<q.head.agg_args.size(); i++)
        {
          Vector<Argument> agg_args = q.head.agg_args.get(i);
          
          if(tuple.getArgs().containsAll(agg_args))
          {
            ArrayList<Tuple> tuples = head_variable_view_mapping[i];
            
            if(tuples == null)
              tuples = new ArrayList<Tuple>();
            
            tuples.add(tuple);
            
            tuple.covered_arg_ids.add(i);
            
            head_variable_view_mapping[i] = tuples;
          }
        }
        
        
      }
    }
    
  }
  
  
  static boolean check_head_mapping(Vector<Argument> args, Query q)
  {
    
    if(!q.head.has_agg)
    {
      Vector<Argument> q_head_args = q.head.args;
      
      for(int i = 0; i<args.size(); i++)
      {
          for(int j = 0; j<q_head_args.size(); j++)
          {   
//              if(args.get(i).toString().equals(q_head_args.get(j).toString()) && args.get(i).relation_name.equals(q_head_args.get(j).relation_name))
            if(args.get(i).equals(q_head_args.get(j)))
                  return true;
          }
      }
    }
    else
    {
      Vector<Vector<Argument>> q_head_args = q.head.agg_args;
      
      for(int i = 0; i<q_head_args.size(); i++)
      {
        Vector<Argument> head_args = q_head_args.get(i);
        
        if(args.containsAll(head_args))
          return true;
      }
      
      
//      for(int i = 0; i<args.size(); i++)
//      {
//          for(int j = 0; j<q_head_args.size(); j++)
//          {   
//              if(args.get(i).toString().equals(q_head_args.get(j).toString()) && args.get(i).relation_name.equals(q_head_args.get(j).relation_name))
//                  return true;
//          }
//      }
    }

    return false;
      
  }
  
  static HashSet<Tuple> check_distinguished_variables(HashSet<Tuple> viewtuples, Query q)
  {
      HashSet<Tuple> update_tuples = new HashSet<Tuple>();
      
      for(Iterator iter = viewtuples.iterator(); iter.hasNext();)
      {           
          Tuple tuple = (Tuple)iter.next();
          
//        System.out.println(tuple);
          
          if(check_head_mapping(tuple.getArgs(), q))// || check_condition_mapping(tuple.getArgs(), q))
          {
              update_tuples.add(tuple);
          }
          
      }
      
      return update_tuples;
  }
  
  public static<K, V> void put_mappings(HashMap<K, ArrayList<V>> maps, K key, V value)
  {
    ArrayList<V> values = maps.get(key);
    
    if(values == null)
    {
      values = new ArrayList<V>();
    }
    
    values.add(value);
    
    maps.put(key, values);
  }
  
  
  public static void put_view_mapping_var_id_mappings(HashMap<Tuple, ArrayList<Integer>> view_mapping_head_var_ids_mappings, Query query, HashSet<Tuple> view_mappings)
  {
    if(query.head.has_agg)
    {
      for(int i = 0; i<query.head.agg_args.size(); i++)
      {
        for(Tuple view_mapping: view_mappings)
        {
          Vector<Argument> mapped_args = view_mapping.args;
          
          if(mapped_args.containsAll(query.head.agg_args.get(i)))
          {
            put_mappings(view_mapping_head_var_ids_mappings, view_mapping, i);
          }
        }
      }
    }
    else
    {
      for(int i = 0; i<query.head.args.size(); i++)
      {
        for(Tuple view_mapping: view_mappings)
        {
          Vector<Argument> mapped_args = view_mapping.args;
          
          if(mapped_args.contains(query.head.args.get(i)))
          {
            put_mappings(view_mapping_head_var_ids_mappings, view_mapping, i);
          }
        }
      }
    }
  }
  
  
  public static HashMap<Tuple, Integer> build_view_mapping_id_mappings(HashSet<Tuple> view_mappings)
  {
    int i = 0;
    
    HashMap<Tuple, Integer> view_mapping_id_mappings = new HashMap<Tuple, Integer>();
    
    for(Tuple view_mapping: view_mappings)
    {
      view_mapping_id_mappings.put(view_mapping, i);
      
      i++;
    }
    
    return view_mapping_id_mappings;
  }
  
  
  public static HashSet pre_processing(boolean isTLA, HashMap<Integer, Query> citation_queries, HashMap<String, Integer> max_author_num, HashMap<String, HashMap<String, Integer>> view_query_mapping, HashMap<String, ArrayList<ArrayList<String>>> author_mapping, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, boolean prepare_info, HashMap<String, Integer> lambda_term_id_mapping, Vector<Lambda_term> valid_lambda_terms, HashMap<Conditions, ArrayList<Tuple>> conditions_map, Vector<Conditions> valid_conditions, HashMap<String, ArrayList<Tuple>> view_tuple_mapping, HashMap<String, ArrayList<Integer>> head_variable_query_mapping, HashMap<String, HashSet<Integer>> relation_arg_mapping, ArrayList<Tuple>[]head_variable_view_mapping, Vector<Query> views, Query q, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, HashMap<String, Integer> with_sub_queries_id_mappings, Vector<String> full_mapping_condition_str, Connection c, PreparedStatement pst) throws SQLException
  {
      
      build_query_head_variable_mapping(q, head_variable_query_mapping);
      
      HashSet<Conditions> query_negated_conditions = q.get_all_negated_conditions();

      
//  q = q.minimize();

      // construct the canonical db
      Database canDb = CoreCover.constructCanonicalDB(q);
      UserLib.myprintln("canDb = " + canDb.toString());

      // compute view tuples
      HashSet<Tuple> viewTuples = CoreCover.computeViewTuples(canDb, views);
      
      viewTuples = check_distinguished_variables(viewTuples, q);
                      
      int tuple_id = 0;
      
      for(Iterator iter = viewTuples.iterator();iter.hasNext();)
      {
          Tuple tuple = (Tuple)iter.next();
          
          if(!build_view_tuple_mapping(tuple, q, view_tuple_mapping))
            continue;
          
          tuple_id++;
//      System.out.println(tuple.name + "|" + tuple.mapSubgoals_str);
          
          for(int i = 0; i<tuple.conditions.size(); i++)
          {
              Conditions condition = tuple.conditions.get(i);
              
              if(condition.get_mapping1 == true && condition.get_mapping2 == true)
              {
                
                if(query_negated_conditions.contains(condition))
                {
                  iter.remove();
                  
                  continue;
                }
                
                if(isTLA && condition.arg2.isConst())
                  continue;
                
                if(!q.conditions.contains(condition) && !valid_conditions.contains(condition))
                {
                  valid_conditions.add(tuple.conditions.get(i));
                  
                  String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
                  
                  full_mapping_condition_str.add(curr_full_mapping_condition_str);
                  
                  ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
                  
                  if(curr_tuples == null)
                  {
                      curr_tuples =  new ArrayList<Tuple>();
                      
                      curr_tuples.add(tuple);
                      
                      conditions_map.put(condition, curr_tuples);
                  }
                  else
                  {                   
                      curr_tuples.add(tuple);
                      
                      conditions_map.put(condition, curr_tuples);
                  }
                  
                }
                else
                {
                  if(valid_conditions.contains(condition))
                  {
//                  valid_conditions.add(tuple.conditions.get(i));
                    
//                  String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
                    
//                  full_mapping_condition_str.add(curr_full_mapping_condition_str);
                    
                    ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
                    
                    if(curr_tuples == null)
                    {
                        curr_tuples =  new ArrayList<Tuple>();
                        
                        curr_tuples.add(tuple);
                        
                        conditions_map.put(condition, curr_tuples);
                    }
                    else
                    {                   
                        curr_tuples.add(tuple);
                        
                        conditions_map.put(condition, curr_tuples);
                    }
                    
                  }
                }
                
                
                
                
              }
              
//            ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
//            
//            if(curr_tuples == null)
//            {
//                curr_tuples =  new ArrayList<Tuple>();
//                
//                curr_tuples.add(tuple);
//                
//                conditions_map.put(condition, curr_tuples);
//            }
//            else
//            {                   
//                curr_tuples.add(tuple);
//                
//                conditions_map.put(condition, curr_tuples);
//            }
//                            
//          if(condition_id_mapping.get(condition) == null)
//          {
//              condition_id_mapping.put(condition, valid_conditions.size());
//              
//              valid_conditions.add(tuple.conditions.get(i));
//          }
          }
          
          Vector<String> curr_partial_mapping_expression = Query_converter.get_partial_mapping_boolean_expressions(tuple, tuple.query, with_sub_queries_id_mappings);
          
          if(!curr_partial_mapping_expression.isEmpty())
          {
            for(String curr_partial_mapping_string : curr_partial_mapping_expression)
            {
              if(!partial_mapping_strings.contains(curr_partial_mapping_string))
              {
                partial_mapping_strings.add(curr_partial_mapping_string);
                
                HashSet<Tuple> curr_view_mappings = new HashSet<Tuple>();
                
                curr_view_mappings.add(tuple);
                
                partial_mapping_view_mapping_mappings.put(curr_partial_mapping_string, curr_view_mappings);
                
              }
              else
              {
                partial_mapping_view_mapping_mappings.get(curr_partial_mapping_string).add(tuple);
              }
              
            }
            
            
            
          }
          
          build_head_variable_mapping(relation_arg_mapping, head_variable_view_mapping, tuple, q);
          
          
          for(int i = 0; i<tuple.lambda_terms.size(); i++)
          {
              String lambda_str = tuple.lambda_terms.get(i).toString();
                              
              if(lambda_term_id_mapping.get(lambda_str) == null)
              {
                  lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
                  
                  valid_lambda_terms.add(tuple.lambda_terms.get(i));
              }
          }
          
      }

      if(prepare_info)
          Prepare_citation_info.prepare_citation_information(citation_queries, max_author_num, view_query_mapping, author_mapping, query_ids, query_lambda_str, views, c, pst);
              
      
//  System.out.println("partial_mapping_conditions:::" + partial_mapping_strings);
      
      return viewTuples;
      
      
  }
  
  static boolean build_view_tuple_mapping(Tuple tuple, Query q, HashMap<String, ArrayList<Tuple>> view_tuple_mapping)
  {
    if(!tuple.args.containsAll(q.head.args))
      return false;
    
    int k = 0;
    
//  System.out.println("Tuple_head_arg::" + tuple.args);
    
    if(q.head.has_agg)
    {
//    Vector<Integer> agg_arg_ids = new Vector<Integer>();
      
      for(k = 0; k<q.head.agg_args.size(); k++)
        {
          Vector<Argument> curr_agg_args = q.head.agg_args.get(k);
          
//        System.out.println(curr_agg_args);
          
          if(tuple.args.containsAll(curr_agg_args))
            break;
          
        }
      
      if(k >= q.head.agg_args.size())
        return false;
    }
    
      for(Iterator iter = q.body.iterator(); iter.hasNext();)
      {
          Subgoal subgoal = (Subgoal) iter.next();
          
          String key = tuple.name + populate_db.separator + subgoal.name;
          
          ArrayList<Tuple> curr_tuples = view_tuple_mapping.get(key);
          
          if(curr_tuples == null)
          {
              curr_tuples = new ArrayList<Tuple>();
              
              curr_tuples.add(tuple);
              
              view_tuple_mapping.put(key, curr_tuples);
          }
          else
          {
              curr_tuples.add(tuple);
              
              view_tuple_mapping.put(key, curr_tuples);
          }
          
      }
      
      return true;
  }


}
