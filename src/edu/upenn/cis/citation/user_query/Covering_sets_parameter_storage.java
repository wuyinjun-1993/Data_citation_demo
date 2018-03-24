package edu.upenn.cis.citation.user_query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import org.apache.poi.hwpf.usermodel.Paragraph;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.ui.CitationConverter;



public class Covering_sets_parameter_storage {
  
  public static void store_covering_sets(HashSet<Covering_set> covering_sets, int query_id, Connection c, PreparedStatement pst) throws SQLException
  {
    for(Covering_set covering_set : covering_sets)
    {
      store_covering_set(covering_set, query_id, c, pst);
    }
  }
  
  static void store_covering_set(Covering_set covering_set, int query_id, Connection c, PreparedStatement pst) throws SQLException
  {
     HashSet<citation_view> views = covering_set.c_vec;
     
     String view_string = new String();
     
     int count = 0;
     
     for(citation_view view : views)
     {
       if(count >= 1)
         view_string += ",";
       
       view_string += view.get_view_tuple().toString();
       
       count ++;
     }
     
     String sql = "insert into user_query2covering_set values('" + query_id + "','" + view_string + "')";
     
     pst = c.prepareStatement(sql);
     
     pst.execute();
     
     
  }
  
  public static void store_parameters_view_mapping(int qid, HashMap<Tuple, ArrayList<Head_strs>> parameters, Connection c, PreparedStatement pst) throws SQLException
  {
    Set<Tuple> view_mappings = parameters.keySet();
    
    String sql = "insert into user_query_view_mapping2parameters values";
    
    int count = 0;
    
    for(Tuple view_mapping: view_mappings)
    {
      ArrayList<Head_strs> curr_parameters = parameters.get(view_mapping);
      
      for(Head_strs curr_parameter: curr_parameters)
      {
        String paramter_string = curr_parameter.head_vals.toString().replaceAll("'", "''");
        
        paramter_string = paramter_string.substring(1, paramter_string.length() - 1);
        
        if(count >= 1)
          sql += ",";
        
        sql += "('" + qid + "','" + view_mapping.toString() + "','" + paramter_string + "')";
        
        count ++;
        
      }
    }
    
    pst = c.prepareStatement(sql);
    
    pst.execute();
  }
  
  public static void store_query_citations(int qid, HashSet<String> citations, Connection c, PreparedStatement pst) throws SQLException
  {
    for(String citation : citations)
    {
      String t_citation = citation.replaceAll("'", "''");
      
      String formatted_citation = CitationConverter.formatCitation(t_citation);
      
      store_query_citation(qid, formatted_citation, c, pst);
    }
  }
  
  static void store_query_citation(int qid, String citation, Connection c, PreparedStatement pst) throws SQLException
  {
    String sql = "insert into user_query2citation values ('" + qid + "','" + citation + "')";
    
    pst = c.prepareStatement(sql);
    
    pst.execute();
  }
  

}
