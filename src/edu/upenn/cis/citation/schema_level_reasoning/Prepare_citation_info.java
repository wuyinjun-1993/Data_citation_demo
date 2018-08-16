package edu.upenn.cis.citation.schema_level_reasoning;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.gen_citation.gen_citation0;

public class Prepare_citation_info {
  
  public static void prepare_citation_information(HashSet<Tuple> view_mappings, HashMap<String, Query> citation_queries, HashMap<String, Integer> max_author_num, HashMap<String, HashMap<String, String>> view_query_mapping, HashMap<String, ArrayList<ArrayList<String>>> author_mapping, IntList query_ids, ArrayList<Lambda_term[]> query_lambda_str, Vector<Query> views, Vector<Query> cqs, Connection c, PreparedStatement pst) throws SQLException
  {
//      gen_citation0.get_all_query_ids(query_ids, c, pst);
      
//    view_query_mapping = new ArrayList<HashMap<String, Integer>>(view_list.size);
              
      for(int i = 0; i<query_ids.size; i++)
      {
          query_lambda_str.add(null);
      }
      
      HashSet<String> view_name_strings = new HashSet<String>();
      
      for(Tuple view_mapping: view_mappings)
      {
        view_name_strings.add(view_mapping.name);
      }
              
      gen_citation0.init_author_mapping(view_name_strings, cqs, view_query_mapping, author_mapping, max_author_num, c, pst, citation_queries);

  }

}
