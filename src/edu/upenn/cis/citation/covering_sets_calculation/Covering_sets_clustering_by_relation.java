package edu.upenn.cis.citation.covering_sets_calculation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;

public class Covering_sets_clustering_by_relation {
  
  public static void reasoning_single_tuple(HashMap<String, ArrayList<Integer>> head_variable_query_mapping, ArrayList<Tuple>[] head_variable_view_mapping, HashMap<String, HashSet<Integer>> relation_arg_mapping, HashSet views, Query query, ResultSet rs, HashSet<Covering_set> c_view_template, HashSet<Argument> curr_head_vars) throws SQLException
  {
      HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
      
      Set<String> relations = head_variable_query_mapping.keySet();
      
      for(Iterator iter = relations.iterator(); iter.hasNext();)
      {
          String relation = (String) iter.next();
          
          HashSet<Integer> view_head_args = relation_arg_mapping.get(relation);
          
          if(view_head_args == null)
          {
              continue;
          }
          else
          {
              HashSet<Integer> view_head_args_copy = (HashSet<Integer>) view_head_args.clone();
              
              ArrayList<Integer> q_head_args = head_variable_query_mapping.get(relation);
              
              view_head_args_copy.retainAll(q_head_args);
              
              for(Iterator it = view_head_args_copy.iterator(); it.hasNext();)
              {
                  Integer arg_id = (Integer) it.next();
                  
                  ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping[arg_id].clone();
                  
                  curr_tuples.retainAll(views);
                  
                  all_covering_sets = Join_covering_sets.join_views_curr_relation(curr_tuples, all_covering_sets);
                  
              }
              
          }
          
          
          
      }
              
      c_view_template.addAll(all_covering_sets);
      
      all_covering_sets.clear();
      
  }
  

}
