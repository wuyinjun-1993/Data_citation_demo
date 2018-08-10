package edu.upenn.cis.citation.covering_sets_calculation1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view1.Covering_set;

public class Covering_sets_no_clustering {
  
  public static void reasoning_single_tuple(HashMap<String, ArrayList<Integer>> head_variable_query_mapping, ArrayList<Tuple>[] head_variable_view_mapping, HashMap<String, HashSet<Integer>> relation_arg_mapping, HashSet views, Query query, ResultSet rs, HashSet<Covering_set> c_view_template, HashSet<Argument> curr_head_vars) throws SQLException
  {
      
//  ArrayList<citation_view_vector> view_com = new ArrayList<citation_view_vector>();
      
      HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
      
      for(int i = 0; i < head_variable_view_mapping.length; i ++)
      {
        ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping[i].clone();
        
        curr_tuples.retainAll(views);
        
//        System.out.println(i + "::" + curr_tuples);
        
        all_covering_sets = Join_covering_sets.join_views_curr_relation(curr_tuples, all_covering_sets);

      }
              
      c_view_template.addAll(all_covering_sets);
      
      all_covering_sets.clear();
      
  }

}
