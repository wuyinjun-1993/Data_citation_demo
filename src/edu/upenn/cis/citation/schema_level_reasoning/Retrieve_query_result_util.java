package edu.upenn.cis.citation.schema_level_reasoning;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Retrieve_query_result_util {
  
  public static boolean retrieve_boolean_arr_values(ResultSet rs, int i) throws SQLException
  {
    Array citation_vec = rs.getArray(i + 1);
    
    String citation_vec_string = citation_vec.toString();
    
//    System.out.println(citation_vec_string);
    
    String[] citation_vec_arr = citation_vec_string.substring(1, citation_vec_string.length() - 1).split(",");
    
    int k = 0;
    
    for(k = 0; k<citation_vec_arr.length; k++)
    {
      if(citation_vec_arr[k].equals("f"))
        break;
    }
    
    return (k < citation_vec_arr.length);
  }

}
