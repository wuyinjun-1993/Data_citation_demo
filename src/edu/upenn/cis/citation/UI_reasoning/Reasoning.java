package edu.upenn.cis.citation.UI_reasoning;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import org.json.JSONException;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Tuple;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.aggregation.Aggregation6;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_full_test2_copy;
import edu.upenn.cis.citation.ui.CitationConverter;

public class Reasoning {
  
  public static boolean prepare_info = false;
  
  public static boolean test_case = false;
  
  public static void main(String[] args) throws SQLException, ClassNotFoundException, JSONException, IOException, InterruptedException
  {
    Class.forName("org.postgresql.Driver");
    Connection c = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
    
    PreparedStatement pst = null;
    
    //read query from the file
    Vector<Query> queries = Load_views_and_citation_queries.get_views("query", c, pst);
    
    Query user_query = queries.get(0);
    
    HashSet<Covering_set> covering_sets = Reasoning.tuple_reasoning(user_query, c, pst);
    
    HashSet<String> citations = Reasoning.gen_citation_schema_level(c, pst);
    
    HashMap<Tuple, ArrayList<Head_strs>> parameters_in_views = Reasoning.get_parameters_in_views();
    
    System.out.println(covering_sets);
    
    c.close();
    
  }
  
  public static void set_prepare_info(boolean b)
  {
    Tuple_reasoning2_full_test2_copy.prepare_info = b;
  }
  
  public static void set_test_case(boolean b)
  {
    Tuple_reasoning2_full_test2_copy.test_case = b;
  }
  
  public static HashMap<Tuple, ArrayList<Head_strs>> get_parameters_in_views()
  {
    return Aggregation6.lambda_values;
  }
  
//  public static HashSet<HashMap<String, HashSet<String>>> gen_json_citation_schema_level(Connection c, PreparedStatement pst) throws JSONException, SQLException
//  {
//    Tuple_reasoning2_full_test2_copy.gen_citation_schema_level(c, pst);
//    
//    return Aggregation6.full_citations;
//  }
  
  public static String gen_json_formatted_formatted_citation_schema_level(Connection c, PreparedStatement pst) throws JSONException, SQLException
  {
    Tuple_reasoning2_full_test2_copy.gen_citation_schema_level(c, pst);
    
    return CitationConverter.formatCitation(Aggregation6.full_citations);
  }
  
  public static HashSet<String> gen_citation_schema_level(Connection c, PreparedStatement pst) throws JSONException, SQLException
  {
    return Tuple_reasoning2_full_test2_copy.gen_citation_schema_level(c, pst);
  }

  public static HashSet<Covering_set> tuple_reasoning(Query query, Connection c, PreparedStatement pst) throws JSONException, SQLException, IOException, InterruptedException
  {
    Tuple_reasoning2_full_test2_copy.tuple_reasoning(query, c, pst);
    
    return Tuple_reasoning2_full_test2_copy.covering_set_schema_level;
  }
  
  public static HashSet<String> tuple_gen_citation_per_tuple(Vector<String> names, Connection c, PreparedStatement pst) throws JSONException, SQLException
  {
    return Tuple_reasoning2_full_test2_copy.tuple_gen_citation_per_tuple(names, c, pst);
  }
}
