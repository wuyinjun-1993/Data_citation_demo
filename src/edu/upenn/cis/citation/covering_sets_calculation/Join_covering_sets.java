package edu.upenn.cis.citation.covering_sets_calculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;

public class Join_covering_sets {
  
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
      int i = 0;
      
      int size = c_combinations.size();
      
      if(c_combinations.contains(c_view))
      {
        return c_combinations;
      }
              
      for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
      {
                      
          Covering_set c_combination = (Covering_set) iter.next();
          
          Covering_set curr_combination = c_view;
          
          if(c_combination.c_vec.containsAll(curr_combination.c_vec)&& curr_combination.table_names.containsAll(c_combination.table_names) && curr_combination.head_variables.containsAll(c_combination.head_variables) && c_combination.c_vec.size() > curr_combination.c_vec.size())
          {
              iter.remove();                      
          }
          
          if(curr_combination.c_vec.containsAll(c_combination.c_vec) && c_combination.table_names.containsAll(curr_combination.table_names) && c_combination.head_variables.containsAll(curr_combination.head_variables) && curr_combination.c_vec.size() > c_combination.c_vec.size())
          {
              break;
          }
          
          i++;
      }
      
      
      if(i >= size)
      {
        c_combinations.add(c_view);
      }
      
              
      return c_combinations;
  }
  
  

}
