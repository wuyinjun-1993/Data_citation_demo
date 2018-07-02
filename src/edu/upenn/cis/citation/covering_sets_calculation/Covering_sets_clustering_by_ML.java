package edu.upenn.cis.citation.covering_sets_calculation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_unparametered;
import fr.lri.tao.apro.ap.Apro;
import fr.lri.tao.apro.ap.AproBuilder;
import fr.lri.tao.apro.data.DataProvider;
import fr.lri.tao.apro.data.MatrixProvider;

public class Covering_sets_clustering_by_ML {
  
  static int factor = 1;
  
  public static void reasoning_single_tuple(ArrayList<Tuple>[] head_variable_view_mapping, double[][] distances, HashSet<Covering_set> resulting_covering_sets, HashMap<String, Query> view_mapping_name_mappings) throws SQLException
  {
      
//  ArrayList<citation_view_vector> view_com = new ArrayList<citation_view_vector>();
      
    HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
    
DataProvider provider = new MatrixProvider(distances);
    
    AproBuilder builder = new AproBuilder();
    builder.setDebug(false);
    
    builder.setThreads(1); // no parallelization
    Apro apro = builder.build(provider);
    apro.setDebug(false);
    
    apro.run(200);
    
    HashMap<Integer, Vector<Integer>> clusters = new HashMap<Integer, Vector<Integer>>();
    
    for(int i = 0; i<apro.getExemplars().length; i++)
    {
      int exemplar = apro.getExemplars()[i];
      
      if(clusters.get(exemplar) == null)
      {
        Vector<Integer> points = new Vector<Integer>();
        
        points.add(i);
        
        clusters.put(exemplar, points);
      }
      else
      {
        clusters.get(exemplar).add(i);
      }
    }

    Set<Integer> ids = clusters.keySet();

    Vector<Integer> sorted_ids = new Vector<Integer>();
    
    Integer curr_id = 0;
    
    for(Integer id:ids)
    {
      if(sorted_ids.isEmpty())
      {
        curr_id = id;
        
        sorted_ids.add(id);
      }
      else
      {
        curr_id = get_next_cluster(distances, curr_id, clusters, sorted_ids, ids);
        
        sorted_ids.add(curr_id);
      }
    }
    
    
    
    
    for(Integer id : sorted_ids)
    {
      Vector<Integer> points = clusters.get(id);
      
//      HashSet<citation_view_vector> curr_covering_sets = new HashSet<citation_view_vector>();
      
      for(int i = 0; i<points.size(); i++)
      {
        if(all_covering_sets.isEmpty())
        {
          all_covering_sets = convert_view_mapping2covering_set(head_variable_view_mapping[points.get(i)], view_mapping_name_mappings);
        }
        else
        {
          all_covering_sets = Join_covering_sets.join_views_curr_relation(head_variable_view_mapping[points.get(i)], all_covering_sets);
        }
      }
      
      
//      if(resulting_covering_sets == null)
//      {
//        resulting_covering_sets = curr_covering_sets;
//      }
//      else
//      {
//        resulting_covering_sets = join_operation(resulting_covering_sets, curr_covering_sets);
//      }
    }
    
    resulting_covering_sets.addAll(all_covering_sets);
    
    all_covering_sets.clear();
      
  }
  
  static HashSet<Covering_set> convert_view_mapping2covering_set(ArrayList<Tuple> view_mapping_lists, HashMap<String, Query> view_mapping_name_mappings)
  {
    HashSet<Covering_set> covering_sets = new HashSet<Covering_set>();
    
    for(Tuple view_mapping: view_mapping_lists)
    {
      citation_view c_view = null;
      
      if(view_mapping.lambda_terms == null && view_mapping.lambda_terms.size() == 0)
      {
        c_view = new citation_view_unparametered(view_mapping.name, view_mapping);
      }
      else
      {
        c_view = new citation_view_parametered(view_mapping.name, view_mapping_name_mappings.get(view_mapping.name), view_mapping);
      }
      
      Covering_set c_set = new Covering_set(c_view);
      
      covering_sets.add(c_set);
    }
    return covering_sets;
  }
  
  static int get_next_cluster(double [][]distances, int curr_id, HashMap<Integer, Vector<Integer>> clusters, Vector<Integer> sorted_ids, Set<Integer> all_ids)
  {
    Vector<Integer> curr_cluster = clusters.get(curr_id);
    
    double min_max_cluster_distance = Double.MAX_VALUE;
    
    int min_max_cluster_id = -1;
    
    for(Integer id: all_ids)
    {
      if(!sorted_ids.contains(id) && id != curr_id)
      {
        Vector<Integer> curr_check_cluster = clusters.get(id);
        
        double max_distance = -1;
        
        for(int i = 0; i<curr_cluster.size(); i++)
        {
          for(int j = 0; j<curr_check_cluster.size(); j++)
          {
            if(distances[curr_cluster.get(i)][curr_check_cluster.get(j)] * (-1) > max_distance)
            {
              max_distance = distances[curr_cluster.get(i)][curr_check_cluster.get(j)] * (-1); 
            }
          }
        }
        
        if(max_distance < min_max_cluster_distance)
        {
          min_max_cluster_distance = max_distance;
          
          min_max_cluster_id = id;
        }
        
      }
    }
    
    return min_max_cluster_id;
  }
  
  public static double[][] cal_distances(ArrayList<int[]> tuple_index)
  {
    if(tuple_index.size() == 1)
    {
      double[][] distances = new double[tuple_index.size()][tuple_index.size()];
      
      for(int i = 0; i<tuple_index.size(); i++)
      {
        distances[i][i] = 0;
      }
      
      return distances;
    }
    
    
    double[][] distances = new double[tuple_index.size()][tuple_index.size()];
    
    double []similarity_values = new double[tuple_index.size() * tuple_index.size() - tuple_index.size()];
    
    int num = 0;
    
    for(int i = 0; i < tuple_index.size(); i++)
    {
      for(int j = 0; j<tuple_index.size(); j++)
      {
        if(i != j)
        {
          distances[i][j] = get_distance(tuple_index.get(i), tuple_index.get(j));
          
          similarity_values[num++] = distances[i][j];
        }
      }
    }
    
    Arrays.sort(similarity_values);
    
    for(int i = 0; i<tuple_index.size(); i++)
    {
      distances[i][i] = factor * similarity_values[similarity_values.length/2];
    }
    
    return distances;
  }
  
  
  static double get_distance(int[] index1, int []index2)
  {
    double distance = 0;
    
    for(int i = 0; i<index1.length; i++)
    {
      distance += Math.pow((index1[i] - index2[i]), 2);
    }
    
    return -distance;
  }

}
