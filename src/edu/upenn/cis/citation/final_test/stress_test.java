package edu.upenn.cis.citation.final_test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.json.JSONException;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.reasoning1.Schema_level_approach;
import edu.upenn.cis.citation.reasoning1.Semi_schema_level_approach;
import edu.upenn.cis.citation.reasoning1.Tuple_level_approach;
import edu.upenn.cis.citation.reasoning2.SSLA_min;
import edu.upenn.cis.citation.reasoning2.TLA_min;
import edu.upenn.cis.citation.schema_level_reasoning.Prepare_citation_info;
import edu.upenn.cis.citation.schema_level_reasoning.Schema_reasoning_with_agg;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;

public class stress_test {
  
  
  static int times = 1;

  
  static String path = "reasoning_results/";
  
  static void reset() throws IOException
  {
      Files.deleteIfExists(Paths.get(populate_db.synthetic_query_files));
  }
  
  public static void init_views(Vector<Query> views, Vector<Query> new_views, boolean new_rounds, boolean new_start, Query query,  Vector<String> relations, Connection c1, Connection c2, PreparedStatement pst) throws SQLException
  {
    if(new_rounds)
    {
        
//      populate_db.renew_table(c1, pst);
        
//      get_table_size(relations, c1, pst);
        
//        views = view_generator.gen_default_views(relations, c2, pst);
        
        populate_db.renew_table(c1, pst);
        
        populate_db.populate_db2(views, c1, pst);
        
        view_generator.store_views_with_citation_queries(views, populate_db.synthetic_view_files, populate_db.synthetic_citation_query_files, populate_db.synthetic_citation_query_view_mapping_files);
        
//      views = view_generator.generate_store_views_without_predicates(relation_names, view_size, query.body.size());
        
        views.clear();
        
        views.addAll(Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c2, pst));
        
//        for(Iterator iter = views.iterator(); iter.hasNext();)
//        {
//            Query view = (Query) iter.next();
//            
//            System.out.println(view);
//        }
        
//      get_table_size(relations, c1, pst);
    }
    else
    {
//      views = view_operation.get_all_views(c2, pst);
      
//      views = Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c2, pst);
        
        if(new_start)
        {
            System.out.println();
            
            view_generator.initial();
            
            int offset = views.size();
            
//            Vector<Query> new_views = view_generator.gen_one_additional_view(views, relations, query.body.size(), query, c1, c2, pst);
            
            view_generator.append_views_with_citation_queries(new_views, offset, populate_db.synthetic_view_files, populate_db.synthetic_citation_query_files, populate_db.synthetic_citation_query_view_mapping_files);
            
            populate_db.populate_db2(new_views, c1, pst);
            
            views.clear();
            
            views.addAll(Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c2, pst));
            
//            for(Iterator iter = views.iterator(); iter.hasNext();)
//            {
//                Query view = (Query) iter.next();
//                
//                System.out.println(view);
//            }
            
//          get_table_size(relations, c1, pst);
        }
    }
  }
  
  
  public static Query init_query(int k, boolean new_query, Connection c1, Connection c2, PreparedStatement pst) throws IOException, SQLException
  {
    query_generator.init_parameterizable_attributes(c2, pst);
    if(new_query)
    {
        System.out.println("new query");
        
//          reset(c1, pst);
//          
//          reset(c2, pst);
        
        reset();
    }
                
    Query query = null;
    
    

    
    try{
//          query = query_storage.get_query_by_id(1, c2, pst);
      
      query = Load_views_and_citation_queries.get_query_test_case(populate_db.synthetic_query_files, c2, pst).get(0);
    }
    catch(Exception e)
    {
        query = query_generator.gen_query(k, c2, pst);

        Vector<Query> queries = new Vector<Query>();
        
        queries.add(query);
        
        Vector<String> query_strings = Load_views_and_citation_queries.views2text_strings(queries);
        Load_views_and_citation_queries.write2files(populate_db.synthetic_query_files, query_strings);
        
//            query_storage.store_user_query(query, "1", c2, pst);
//        System.out.println(query);
    }
    
//    if(new_query)
//    {
//        Vector<String> sqls = new Vector<String>();
//        
//        sqls.add(Query_converter.datalog2sql_test(query));
//        
//        Query_operation.write2file(path + "user_query_sql", sqls);
//    }
        
     return query;
  }
  
  
  public static Query init_query(int k, int instance_size, boolean new_query, Connection c1, Connection c2, PreparedStatement pst) throws IOException, SQLException
  {
    query_generator.init_parameterizable_attributes(c2, pst);
    if(new_query)
    {
        System.out.println("new query");
        
        reset();
    }
                
    Query query = null;
    
    

    
    try{
//          query = query_storage.get_query_by_id(1, c2, pst);
      
      query = Load_views_and_citation_queries.get_query_test_case(populate_db.synthetic_query_files, c2, pst).get(0);
      
      query = query_generator.generate_query_with_new_instance_size(query, instance_size, c2, pst);
    }
    catch(Exception e)
    {
        query_generator.query_result_size = instance_size;
      
        query = query_generator.gen_query(k, c2, pst);

        Vector<Query> queries = new Vector<Query>();
        
        queries.add(query);
        
        Vector<String> query_strings = Load_views_and_citation_queries.views2text_strings(queries);
        Load_views_and_citation_queries.write2files(populate_db.synthetic_query_files, query_strings);
        
//            query_storage.store_user_query(query, "1", c2, pst);
        System.out.println(query);
    }
    
    if(new_query)
    {
        Vector<String> sqls = new Vector<String>();
        
        sqls.add(Query_converter.datalog2sql_test(query));
        
        Query_operation.write2file(path + "user_query_sql", sqls);
    }
        
     return query;
  }
  
  public static void stress_test(Query query, Vector<Query> views, Vector<Query> citation_queries, HashMap<String, HashMap<String, String>> view_citation_query_mappings, boolean tuple_level, boolean schema_level, boolean agg_intersection, boolean test_case, String db_name1, String db_name2, String usr_name, String passwd) throws SQLException, IOException, InterruptedException, JSONException, ClassNotFoundException
  {
//    while(views.size() < view_max_size)
      if(tuple_level)
      {
          
          Connection c = null;
            PreparedStatement pst = null;
          Class.forName("org.postgresql.Driver");
          c = DriverManager
              .getConnection(populate_db.db_url_prefix + db_name1, usr_name , passwd);
          
          Tuple_level_approach.prepare_info = false;
      
          Tuple_level_approach.agg_intersection = agg_intersection;
          
          Tuple_level_approach.test_case = test_case;
          
          double end_time = 0;

          double middle_time = 0;
          
          double start_time = 0;
          
          double time1 = 0;
          
          HashSet<String> agg_citations = null;
                      
          start_time = System.nanoTime();
          
          Tuple_level_approach.tuple_reasoning(query, views, citation_queries, view_citation_query_mappings, c, pst);
          
          time1 = System.nanoTime();
          
//        ArrayList<HashSet<citation_view>> views_per_group = Tuple_level_approach.cal_covering_sets_schema_level(query, c, pst);
          
          middle_time = System.nanoTime();
          
          Prepare_citation_info.prepare_citation_information(Tuple_level_approach.citation_queries, Tuple_level_approach.max_author_num, view_citation_query_mappings, Tuple_level_approach.author_mapping, Tuple_level_approach.query_ids, Tuple_level_approach.query_lambda_str, views, citation_queries, c, pst);
          
          agg_citations = Tuple_level_approach.gen_citation_schema_level(c, pst);
          
//         Tuple_reasoning1_full_test_opt.tuple_gen_agg_citations(query, c, pst);
                                                  
          end_time = System.nanoTime();
          
          double time = (end_time - start_time)*1.0;
          
          double agg_time = (middle_time - time1) * 1.0/1000000000;
          
          double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
          
          double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
          
          time = time /(times * 1000000000);
                      
          System.out.print(Tuple_level_approach.group_num + " ");
          
          System.out.print(Tuple_level_approach.tuple_num + " ");
          
          System.out.print(time + "s  ");
          
          System.out.print("total_exe_time::" + time + "  ");
          
          System.out.print("reasoning_time::" + reasoning_time + "    ");
          
          System.out.print("aggregation_time::" + agg_time + "    ");
          
          System.out.print("citation_gen_time::" + citation_gen_time + "  ");
          
//          System.out.print("covering_sets::" + Tuple_level_approach.covering_set_schema_level + "  ");
          
          System.out.print("covering_set_size::" + Tuple_level_approach.covering_set_schema_level.size() + "   ");
          
//          int distinct_view_size = Aggregation5.cal_distinct_views();
          
//          System.out.print("distinct_view_size::" + distinct_view_size + "    ");
          
//        Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//        
//        double citation_size1 = 0;
//        
//        int row = 0;
//        
//        start_time = System.nanoTime();
//        
//        for(Iterator iter = h_l.iterator(); iter.hasNext();)
//        {
//            Head_strs h_value = (Head_strs) iter.next();
//            
//            HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//            
//            citation_size1 += citations.size();
//            
////              System.out.println(citations);
//            
//            row ++;
//            
//            if(row >= 10)
//                break;
//            
//        }
//        
//        end_time = System.nanoTime();
//        
//        if(row !=0)
//        citation_size1 = citation_size1 / row;
//        
//        time = (end_time - start_time)/(row * 1.0 * 1000000000);
//        
//        System.out.print(time + "s  ");
//        
//        System.out.print(citation_size1 + " ");
//        
//        System.out.print(row + "    ");
          
//        Set<Head_strs> head = citation_view_map1.keySet();
//        
//        int row_num = 0;
//        
//        double origin_citation_size = 0.0;
//        
//        for(Iterator iter = head.iterator(); iter.hasNext();)
//        {
//            Head_strs head_val = (Head_strs) iter.next();
//            
//            Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//            
//            row_num++;
//            
//            for(int p = 0; p<c_view.size(); p++)
//            {
//                origin_citation_size += c_view.get(p).size();
//            }
//            
//        }
//        
//        
//        
//        if(row_num !=0)
//            origin_citation_size = origin_citation_size / row_num;
//        
          
          
          System.out.print(Tuple_level_approach.covering_set_num * 1.0/Tuple_level_approach.tuple_num + " ");
          
          System.out.print("pre_processing::" + Tuple_level_approach.pre_processing_time + "  ");
          
          System.out.print("query::" + Tuple_level_approach.query_time + "    ");
          
          System.out.print("reasoning::" + Tuple_level_approach.reasoning_time + "    ");
          
          System.out.print("population::" + Tuple_level_approach.population_time + "  ");
          
          
          
//        time = (end_time - start_time) * 1.0/1000000000;
//        
//        System.out.print("Aggregation_time::" + time + "    ");
//        
//        System.out.print("Aggregation_size::" + agg_citations.size() + "    ");
          
//        start_time = System.nanoTime();
//        
//        Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
//        
//        agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
//                    
//        end_time = System.nanoTime();
//        
//        time = (end_time - start_time) * 1.0/1000000000;
//        
//        System.out.print("total_reasoning_time::" + time + "    ");
          
//        Vector<String> agg_results = new Vector<String>();
//        
//        agg_results.add(Tuple_reasoning1_full_min_test.covering_sets_query.toString());
//        
          Query_operation.write2file(path + "covering_sets", Tuple_level_approach.covering_set_schema_level);

//        for(int k = 0; k<Aggregation5.curr_res.size(); k++)
//        {
//            agg_results.add(Aggregation5.curr_res.get(k).toString());
//        }
          
//        String aggregate_result = Aggregation5.curr_res.toString();
          
//        System.out.println(aggregate_result);
                      
          
//        HashSet<String> agg_citations2 = Tuple_reasoning1_full_test.tuple_gen_agg_citations2(query);
//        
//        System.out.println("citation1::" + agg_citations);
//        
//        System.out.println("citation2::" + agg_citations2);
//        
//        if(!agg_citations.equals(agg_citations2))
//        {
//            
//            for(int p = 0; p < Aggregation3.author_lists.size(); p ++)
//            {
//                HashMap<String, HashSet<String>> citation1 = Aggregation3.author_lists.get(p);
//                
//                HashMap<String, HashSet<String>> citation2 = Aggregation5.full_citations.get(p);
//                
//                HashSet<String> author1 = citation1.get("author");
//                
//                HashSet<String> author2 = citation2.get("author");
//                
//                if(!author1.equals(author2))
//                {
//                    
//                    HashSet<String> curr_authors = new HashSet<String>();
//                    
//                    curr_authors.addAll(author1);
//                    
//                    curr_authors.removeAll(author2);
//                    
//                    System.out.println(Aggregation3.curr_res.get(p));
//                    
//                    System.out.println(Aggregation5.curr_res.get(p));
//                    
//                    System.out.println(p);
//                    
//                    System.out.println(author1.size());
//                    
//                    System.out.println(author2.size());
//                    
//                    int y = 0;
//                    
//                    y++;
//                }
//            }
//            
//            Assert.assertEquals(true, false);
//        }
                      
//        diff_agg_time(tuple_level, query);
          
          System.out.println();
          
//        System.out.println(agg_citations.toString());
          
          c.close();
          
//        System.out.println(agg_citations);
          
          
      }
      else
      {
          
          if(!schema_level)
          {
              Connection c = null;
                PreparedStatement pst = null;
              Class.forName("org.postgresql.Driver");
              c = DriverManager
                  .getConnection(populate_db.db_url_prefix + db_name2, usr_name , passwd);
              
              Semi_schema_level_approach.prepare_info = false;
              
              Semi_schema_level_approach.agg_intersection = agg_intersection;
              
              Semi_schema_level_approach.test_case = test_case;
              
              double end_time = 0;

              double middle_time = 0;
              
              double start_time = 0;
              
              double time1 = 0.0;
              
              HashSet<String> agg_citations = null;
                          
              start_time = System.nanoTime();
              
              Semi_schema_level_approach.tuple_reasoning(query, views, citation_queries, view_citation_query_mappings, c, pst);
              
              time1 = System.nanoTime();
              
//            ArrayList<HashSet<citation_view>> views_per_group = Tuple_reasoning2_full_test2.cal_covering_sets_schema_level(query, c, pst);
              
              middle_time = System.nanoTime();
              
              Prepare_citation_info.prepare_citation_information(Semi_schema_level_approach.citation_queries, Semi_schema_level_approach.max_author_num, view_citation_query_mappings, Semi_schema_level_approach.author_mapping, Semi_schema_level_approach.query_ids, Semi_schema_level_approach.query_lambda_str, views, citation_queries, c, pst);
              
//            Semi_schema_level_approach.gen_citation_schema_level(views_per_group, c, pst);
              
              agg_citations = Semi_schema_level_approach.gen_citation_schema_level(c, pst);
                                                      
              end_time = System.nanoTime();
              
              double time = (end_time - start_time)*1.0;
              
              double agg_time = (middle_time - time1) * 1.0/1000000000;
              
              double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
              
              double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
              
              time = time /(times * 1000000000);
                          
              System.out.print(Semi_schema_level_approach.group_num + "   ");
              
              System.out.print(Semi_schema_level_approach.tuple_num + "   ");
              
              System.out.print(time + "s  ");
              
              System.out.print("total_exe_time::" + time + "  ");
              
              System.out.print("reasoning_time::" + reasoning_time + "    ");
              
              System.out.print("aggregation_time::" + agg_time + "    ");
              
              System.out.print("citation_gen_time::" + citation_gen_time + "  ");

//              System.out.print("covering_sets::" + Semi_schema_level_approach.covering_set_schema_level + "  ");
              
              System.out.print("covering_set_size::" + Semi_schema_level_approach.covering_set_schema_level.size() + "   ");
              
//              int distinct_view_size = Aggregation5.cal_distinct_views();
//              
//              System.out.print("distinct_view_size::" + distinct_view_size + "    ");
              
//            Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//            
//            double citation_size1 = 0;
//            
//            int row = 0;
//            
//            start_time = System.nanoTime();
//            
//            for(Iterator iter = h_l.iterator(); iter.hasNext();)
//            {
//                Head_strs h_value = (Head_strs) iter.next();
//                
//                HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//                
//                citation_size1 += citations.size();
//                
////                  System.out.println(citations);
//                
//                row ++;
//                
//                if(row >= 10)
//                    break;
//                
//            }
//            
//            end_time = System.nanoTime();
//            
//            if(row !=0)
//            citation_size1 = citation_size1 / row;
//            
//            time = (end_time - start_time)/(row * 1.0 * 1000000000);
//            
//            System.out.print(time + "s  ");
//            
//            System.out.print(citation_size1 + " ");
//            
//            System.out.print(row + "    ");
              
//            Set<Head_strs> head = citation_view_map1.keySet();
//            
//            int row_num = 0;
//            
//            double origin_citation_size = 0.0;
//            
//            for(Iterator iter = head.iterator(); iter.hasNext();)
//            {
//                Head_strs head_val = (Head_strs) iter.next();
//                
//                Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//                
//                row_num++;
//                
//                for(int p = 0; p<c_view.size(); p++)
//                {
//                    origin_citation_size += c_view.get(p).size();
//                }
//                
//            }
//            
//            
//            
//            if(row_num !=0)
//                origin_citation_size = origin_citation_size / row_num;
//            
              
              
              System.out.print(Semi_schema_level_approach.covering_set_num * 1.0/Semi_schema_level_approach.tuple_num + " ");
              
              System.out.print("pre_processing::" + Semi_schema_level_approach.pre_processing_time + "    ");
              
              System.out.print("query::" + Semi_schema_level_approach.query_time + "  ");
              
              System.out.print("reasoning::" + Semi_schema_level_approach.reasoning_time + "  ");
              
              System.out.print("population::" + Semi_schema_level_approach.population_time + "    ");
              
              System.out.println();
              
              System.out.println(agg_citations);
              

              c.close();
              
//            Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
              
//            Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
              
              
//                        
//            reset();
//            
//            Vector<Query> queries = query_generator.gen_queries(j, size_range);
//            
//            Query query = query_generator.gen_query(j, c, pst);
              
//            System.out.println(queries.get(0));

              
//            Vector<String> relation_names = get_unique_relation_names(queries.get(0));
              
//            view_generator.generate_store_views(relation_names, num_views);
              
//            query_storage.store_query(queries.get(0), new Vector<Integer>());
          }
          else
          {
              Connection c = null;
                PreparedStatement pst = null;
              Class.forName("org.postgresql.Driver");
              c = DriverManager
                  .getConnection(populate_db.db_url_prefix + db_name2, usr_name , passwd);
              
              Schema_level_approach.prepare_info = false;
              
              Schema_level_approach.test_case = test_case;
                              
              double end_time = 0;

              double middle_time = 0;
              
              double start_time = 0;
              
              double inter_time = 0;
              
              HashSet<String> agg_citations = null;
                          
              start_time = System.nanoTime();
              
              Schema_level_approach.tuple_reasoning(query, views, citation_queries, view_citation_query_mappings, c, pst);
              
              inter_time = System.nanoTime();
                              
              Schema_level_approach.execute_query(query, c, pst);
              
              middle_time = System.nanoTime();
              
              Prepare_citation_info.prepare_citation_information(Schema_level_approach.citation_queries, Schema_level_approach.max_author_num, view_citation_query_mappings, Schema_level_approach.author_mapping, Schema_level_approach.query_ids, Schema_level_approach.query_lambda_str, views, citation_queries, c, pst);
              
              agg_citations = Schema_level_approach.tuple_gen_agg_citations(query, c, pst);
                                                      
              end_time = System.nanoTime();
              
              double time = (end_time - start_time)*1.0;
              
              double agg_time = (end_time - middle_time) * 1.0/1000000000;
              
              double reasoning_time = (inter_time - start_time) * 1.0/1000000000;
              
              time = time /(times * 1000000000);
              
              double query_time = (middle_time - inter_time) * 1.0/1000000000;
                          
              System.out.print(Schema_level_approach.tuple_num + "    ");
              
              System.out.print(time + "s  ");
              
              System.out.print("total_exe_time::" + time + "  ");
              
              System.out.print("total_reasoning_time::" + reasoning_time + "  ");
              
              System.out.print("aggregation_time::" + agg_time + "    ");
              
//              System.out.print("covering_sets::" + Schema_level_approach.covering_set_schema_level + "   ");
              
              System.out.print("covering_set_size::" + Schema_level_approach.covering_set_schema_level.size() + "    ");
              
//              int distinct_view_size = Aggregation5.cal_distinct_views();
//              
//              System.out.print("distinct_view_size::" + distinct_view_size + "    ");
              
//            Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//            
//            double citation_size1 = 0;
//            
//            int row = 0;
//            
//            start_time = System.nanoTime();
//            
//            for(Iterator iter = h_l.iterator(); iter.hasNext();)
//            {
//                Head_strs h_value = (Head_strs) iter.next();
//                
//                HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//                
//                citation_size1 += citations.size();
//                
////                  System.out.println(citations);
//                
//                row ++;
//                
//                if(row >= 10)
//                    break;
//                
//            }
//            
//            end_time = System.nanoTime();
//            
//            if(row !=0)
//            citation_size1 = citation_size1 / row;
//            
//            time = (end_time - start_time)/(row * 1.0 * 1000000000);
//            
//            System.out.print(time + "s  ");
//            
//            System.out.print(citation_size1 + " ");
//            
//            System.out.print(row + "    ");
              
//            Set<Head_strs> head = citation_view_map1.keySet();
//            
//            int row_num = 0;
//            
//            double origin_citation_size = 0.0;
//            
//            for(Iterator iter = head.iterator(); iter.hasNext();)
//            {
//                Head_strs head_val = (Head_strs) iter.next();
//                
//                Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//                
//                row_num++;
//                
//                for(int p = 0; p<c_view.size(); p++)
//                {
//                    origin_citation_size += c_view.get(p).size();
//                }
//                
//            }
//            
//            
//            
//            if(row_num !=0)
//                origin_citation_size = origin_citation_size / row_num;
//            
              
                              
              System.out.print("pre_processing::" + Schema_level_approach.pre_processing_time + " ");
              
              System.out.print("query::" + query_time + " ");
                                              
              System.out.println();
              

              c.close();
              
//            Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
              
//            Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
              
              
//                        
//            reset();
//            
//            Vector<Query> queries = query_generator.gen_queries(j, size_range);
//            
//            Query query = query_generator.gen_query(j, c, pst);
              
//            System.out.println(queries.get(0));

              
//            Vector<String> relation_names = get_unique_relation_names(queries.get(0));
              
//            view_generator.generate_store_views(relation_names, num_views);
              
//            query_storage.store_query(queries.get(0), new Vector<Integer>());
          }
          
          
          
          
          
      }
  }
  
  
  public static void stress_test_min(Query query, Vector<Query> views, Vector<Query> citation_queries, HashMap<String, HashMap<String, String>> view_citation_query_mappings, boolean tuple_level, boolean agg_intersection, boolean test_case, String db_name1, String db_name2, String usr_name, String passwd) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
  {
//    while(views.size() < view_max_size)
      if(tuple_level)
      {
          
          Connection c = null;
            PreparedStatement pst = null;
          Class.forName("org.postgresql.Driver");
          c = DriverManager
              .getConnection(populate_db.db_url_prefix + db_name1, usr_name , passwd);
          
          TLA_min.prepare_info = false;
      
          TLA_min.agg_intersection = agg_intersection;
          
          TLA_min.test_case = test_case;
          
          double end_time = 0;

          double middle_time = 0;
          
          double start_time = 0;
          
          double time1 = 0;
          
          HashSet<String> agg_citations = null;
                      
          start_time = System.nanoTime();
          
          TLA_min.tuple_reasoning(query, views, citation_queries, view_citation_query_mappings, c, pst);
          
          time1 = System.nanoTime();
          
//        ArrayList<HashSet<citation_view>> views_per_group = Tuple_level_approach.cal_covering_sets_schema_level(query, c, pst);
          
          middle_time = System.nanoTime();
          
          Prepare_citation_info.prepare_citation_information(TLA_min.citation_queries, TLA_min.max_author_num, view_citation_query_mappings, TLA_min.author_mapping, TLA_min.query_ids, TLA_min.query_lambda_str, views, citation_queries, c, pst);
          
          agg_citations = TLA_min.gen_citation_schema_level(c, pst);
          
//         Tuple_reasoning1_full_test_opt.tuple_gen_agg_citations(query, c, pst);
                                                  
          end_time = System.nanoTime();
          
          double time = (end_time - start_time)*1.0;
          
          double agg_time = (middle_time - time1) * 1.0/1000000000;
          
          double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
          
          double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
          
          time = time /(times * 1000000000);
                      
          System.out.print(TLA_min.group_num + " ");
          
          System.out.print(TLA_min.tuple_num + " ");
          
          System.out.print(time + "s  ");
          
          System.out.print("total_exe_time::" + time + "  ");
          
          System.out.print("reasoning_time::" + reasoning_time + "    ");
          
          System.out.print("aggregation_time::" + agg_time + "    ");
          
          System.out.print("citation_gen_time::" + citation_gen_time + "  ");
          
//          System.out.print("covering_sets::" + Tuple_level_approach.covering_set_schema_level + "  ");
          
          System.out.print("covering_set::" + TLA_min.covering_set_schema_level + "   ");
          
//          int distinct_view_size = Aggregation5.cal_distinct_views();
          
//          System.out.print("distinct_view_size::" + distinct_view_size + "    ");
          
//        Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//        
//        double citation_size1 = 0;
//        
//        int row = 0;
//        
//        start_time = System.nanoTime();
//        
//        for(Iterator iter = h_l.iterator(); iter.hasNext();)
//        {
//            Head_strs h_value = (Head_strs) iter.next();
//            
//            HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//            
//            citation_size1 += citations.size();
//            
////              System.out.println(citations);
//            
//            row ++;
//            
//            if(row >= 10)
//                break;
//            
//        }
//        
//        end_time = System.nanoTime();
//        
//        if(row !=0)
//        citation_size1 = citation_size1 / row;
//        
//        time = (end_time - start_time)/(row * 1.0 * 1000000000);
//        
//        System.out.print(time + "s  ");
//        
//        System.out.print(citation_size1 + " ");
//        
//        System.out.print(row + "    ");
          
//        Set<Head_strs> head = citation_view_map1.keySet();
//        
//        int row_num = 0;
//        
//        double origin_citation_size = 0.0;
//        
//        for(Iterator iter = head.iterator(); iter.hasNext();)
//        {
//            Head_strs head_val = (Head_strs) iter.next();
//            
//            Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//            
//            row_num++;
//            
//            for(int p = 0; p<c_view.size(); p++)
//            {
//                origin_citation_size += c_view.get(p).size();
//            }
//            
//        }
//        
//        
//        
//        if(row_num !=0)
//            origin_citation_size = origin_citation_size / row_num;
//        
          
          
          System.out.print(TLA_min.covering_set_num * 1.0/TLA_min.tuple_num + " ");
          
          System.out.print("pre_processing::" + TLA_min.pre_processing_time + "  ");
          
          System.out.print("query::" + TLA_min.query_time + "    ");
          
          System.out.print("reasoning::" + TLA_min.reasoning_time + "    ");
          
          System.out.print("population::" + TLA_min.population_time + "  ");
          
          
          
//        time = (end_time - start_time) * 1.0/1000000000;
//        
//        System.out.print("Aggregation_time::" + time + "    ");
//        
//        System.out.print("Aggregation_size::" + agg_citations.size() + "    ");
          
//        start_time = System.nanoTime();
//        
//        Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
//        
//        agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
//                    
//        end_time = System.nanoTime();
//        
//        time = (end_time - start_time) * 1.0/1000000000;
//        
//        System.out.print("total_reasoning_time::" + time + "    ");
          
//        Vector<String> agg_results = new Vector<String>();
//        
//        agg_results.add(Tuple_reasoning1_full_min_test.covering_sets_query.toString());
//        
//          Query_operation.write2file(path + "covering_sets", Tuple_level_approach_min.covering_set_schema_level);

//        for(int k = 0; k<Aggregation5.curr_res.size(); k++)
//        {
//            agg_results.add(Aggregation5.curr_res.get(k).toString());
//        }
          
//        String aggregate_result = Aggregation5.curr_res.toString();
          
//        System.out.println(aggregate_result);
                      
          
//        HashSet<String> agg_citations2 = Tuple_reasoning1_full_test.tuple_gen_agg_citations2(query);
//        
//        System.out.println("citation1::" + agg_citations);
//        
//        System.out.println("citation2::" + agg_citations2);
//        
//        if(!agg_citations.equals(agg_citations2))
//        {
//            
//            for(int p = 0; p < Aggregation3.author_lists.size(); p ++)
//            {
//                HashMap<String, HashSet<String>> citation1 = Aggregation3.author_lists.get(p);
//                
//                HashMap<String, HashSet<String>> citation2 = Aggregation5.full_citations.get(p);
//                
//                HashSet<String> author1 = citation1.get("author");
//                
//                HashSet<String> author2 = citation2.get("author");
//                
//                if(!author1.equals(author2))
//                {
//                    
//                    HashSet<String> curr_authors = new HashSet<String>();
//                    
//                    curr_authors.addAll(author1);
//                    
//                    curr_authors.removeAll(author2);
//                    
//                    System.out.println(Aggregation3.curr_res.get(p));
//                    
//                    System.out.println(Aggregation5.curr_res.get(p));
//                    
//                    System.out.println(p);
//                    
//                    System.out.println(author1.size());
//                    
//                    System.out.println(author2.size());
//                    
//                    int y = 0;
//                    
//                    y++;
//                }
//            }
//            
//            Assert.assertEquals(true, false);
//        }
                      
//        diff_agg_time(tuple_level, query);
          
          System.out.println();
          
//          System.out.println("citations::" + agg_citations);
//          
//        System.out.println(agg_citations.toString());
          
          c.close();
          
//        System.out.println(agg_citations);
          
          
      }
      else
      {
          
//          if(!schema_level)
          {
              Connection c = null;
                PreparedStatement pst = null;
              Class.forName("org.postgresql.Driver");
              c = DriverManager
                  .getConnection(populate_db.db_url_prefix + db_name2, usr_name , passwd);
              
              SSLA_min.prepare_info = false;
              
              SSLA_min.agg_intersection = agg_intersection;
              
              SSLA_min.test_case = test_case;
              
              double end_time = 0;

              double middle_time = 0;
              
              double start_time = 0;
              
              double time1 = 0.0;
              
              HashSet<String> agg_citations = null;
                          
              start_time = System.nanoTime();
              
              SSLA_min.tuple_reasoning(query, views, citation_queries, view_citation_query_mappings, c, pst);
              
              time1 = System.nanoTime();
              
//            ArrayList<HashSet<citation_view>> views_per_group = Tuple_reasoning2_full_test2.cal_covering_sets_schema_level(query, c, pst);
              
              middle_time = System.nanoTime();
              
              Prepare_citation_info.prepare_citation_information(SSLA_min.citation_queries, SSLA_min.max_author_num, view_citation_query_mappings, SSLA_min.author_mapping, SSLA_min.query_ids, SSLA_min.query_lambda_str, views, citation_queries, c, pst);
              
//            Semi_schema_level_approach.gen_citation_schema_level(views_per_group, c, pst);
              
              agg_citations = SSLA_min.gen_citation_schema_level(c, pst);
                                                      
              end_time = System.nanoTime();
              
              double time = (end_time - start_time)*1.0;
              
              double agg_time = (middle_time - time1) * 1.0/1000000000;
              
              double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
              
              double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
              
              time = time /(times * 1000000000);
                          
              System.out.print(SSLA_min.group_num + "   ");
              
              System.out.print(SSLA_min.tuple_num + "   ");
              
              System.out.print(time + "s  ");
              
              System.out.print("total_exe_time::" + time + "  ");
              
              System.out.print("reasoning_time::" + reasoning_time + "    ");
              
              System.out.print("aggregation_time::" + agg_time + "    ");
              
              System.out.print("citation_gen_time::" + citation_gen_time + "  ");

//              System.out.print("covering_sets::" + Semi_schema_level_approach.covering_set_schema_level + "  ");
              
              System.out.print("covering_set_size::" + SSLA_min.covering_set_schema_level + "   ");
              
//              int distinct_view_size = Aggregation5.cal_distinct_views();
//              
//              System.out.print("distinct_view_size::" + distinct_view_size + "    ");
              
//            Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//            
//            double citation_size1 = 0;
//            
//            int row = 0;
//            
//            start_time = System.nanoTime();
//            
//            for(Iterator iter = h_l.iterator(); iter.hasNext();)
//            {
//                Head_strs h_value = (Head_strs) iter.next();
//                
//                HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//                
//                citation_size1 += citations.size();
//                
////                  System.out.println(citations);
//                
//                row ++;
//                
//                if(row >= 10)
//                    break;
//                
//            }
//            
//            end_time = System.nanoTime();
//            
//            if(row !=0)
//            citation_size1 = citation_size1 / row;
//            
//            time = (end_time - start_time)/(row * 1.0 * 1000000000);
//            
//            System.out.print(time + "s  ");
//            
//            System.out.print(citation_size1 + " ");
//            
//            System.out.print(row + "    ");
              
//            Set<Head_strs> head = citation_view_map1.keySet();
//            
//            int row_num = 0;
//            
//            double origin_citation_size = 0.0;
//            
//            for(Iterator iter = head.iterator(); iter.hasNext();)
//            {
//                Head_strs head_val = (Head_strs) iter.next();
//                
//                Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//                
//                row_num++;
//                
//                for(int p = 0; p<c_view.size(); p++)
//                {
//                    origin_citation_size += c_view.get(p).size();
//                }
//                
//            }
//            
//            
//            
//            if(row_num !=0)
//                origin_citation_size = origin_citation_size / row_num;
//            
              
              
              System.out.print(SSLA_min.covering_set_num * 1.0/SSLA_min.tuple_num + " ");
              
              System.out.print("pre_processing::" + SSLA_min.pre_processing_time + "    ");
              
              System.out.print("query::" + SSLA_min.query_time + "  ");
              
              System.out.print("reasoning::" + SSLA_min.reasoning_time + "  ");
              
              System.out.print("population::" + SSLA_min.population_time + "    ");
              
              System.out.println();

//              System.out.println(agg_citations);

              c.close();
              
//            Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
              
//            Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
              
              
//                        
//            reset();
//            
//            Vector<Query> queries = query_generator.gen_queries(j, size_range);
//            
//            Query query = query_generator.gen_query(j, c, pst);
              
//            System.out.println(queries.get(0));

              
//            Vector<String> relation_names = get_unique_relation_names(queries.get(0));
              
//            view_generator.generate_store_views(relation_names, num_views);
              
//            query_storage.store_query(queries.get(0), new Vector<Integer>());
          }
          
          
          
          
          
      }
  }
  
//  public static void stress_test(Query query, Vector<Query> views, boolean tuple_level) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
//  {
//      HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
//      
//      HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();
//
//      
//      String f_name = new String();
//      
//      HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
//
//      HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
//      
//      Vector<Vector<Covering_set>> citation_view2 = new Vector<Vector<Covering_set>>();
//
//      
////    while(views.size() < view_max_size)
//      if(tuple_level)
//      {
//          
//          Connection c = null;
//            PreparedStatement pst = null;
//          Class.forName("org.postgresql.Driver");
//          c = DriverManager
//              .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
//          
//          Tuple_level_approach_min.prepare_info = false;
//          
//          Tuple_level_approach_min.test_case = true;
//      
//          double end_time = 0;
//
//          double middle_time = 0;
//          
//          double start_time = 0;
//          
//          HashSet<String> agg_citations = null;
//
//          
//          start_time = System.nanoTime();
//                      
//          Tuple_level_approach_min.tuple_reasoning(query, c, pst);
//          
//          middle_time = System.nanoTime();
//          
//          Tuple_level_approach_min.prepare_citation_information(views, c, pst);
//          
//          agg_citations = Tuple_level_approach_min.tuple_gen_agg_citations(query, c, pst);
//                                                  
//          end_time = System.nanoTime();
//          
//          double time = (end_time - start_time)*1.0;
//          
//          double agg_time = (end_time - middle_time) * 1.0/1000000000;
//          
//          double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
//          
//          time = time /(times * 1000000000);
//                      
//          System.out.print(Tuple_level_approach_min.group_num + " ");
//          
//          System.out.print(Tuple_level_approach_min.tuple_num + " ");
//          
//          System.out.print(time + "s  ");
//          
//          System.out.print("total_exe_time::" + time + "  ");
//          
//          System.out.print("reasoning_time::" + reasoning_time + "    ");
//          
//          System.out.print("aggregation_time::" + agg_time + "    ");
//          
//          System.out.print("covering set::" + Tuple_level_approach_min.covering_sets_query + "    ");
//          
//          System.out.print("citation_size::" + Tuple_level_approach_min.covering_sets_query.c_vec.size() + "  ");
//          
//          HashMap<String, HashSet<String>> view_strs = new HashMap<String, HashSet<String>>();
//          
//          int covering_set_size = Tuple_level_approach_min.compute_distinct_num_covering_sets(view_strs);
//          
//          System.out.print("distinct_covering_set_size::" + covering_set_size + " ");
//          
//          Set<String> view_name = view_strs.keySet();
//          
//          int diff_view_num = 0;
//          
//          for(Iterator iter = view_name.iterator(); iter.hasNext();)
//          {
//              String curr_v_name = (String)iter.next();
//              
//              HashSet<String> curr_views = view_strs.get(curr_v_name);
//              
//              diff_view_num += curr_views.size();
//          }
//          
//          System.out.print("distinct_view_size::" + diff_view_num + " ");
//          
////        Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
////        
////        double citation_size1 = 0;
////        
////        int row = 0;
////        
////        start_time = System.nanoTime();
////        
////        for(Iterator iter = h_l.iterator(); iter.hasNext();)
////        {
////            Head_strs h_value = (Head_strs) iter.next();
////            
////            HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
////            
////            citation_size1 += citations.size();
////            
//////              System.out.println(citations);
////            
////            row ++;
////            
////            if(row >= 10)
////                break;
////            
////        }
////        
////        end_time = System.nanoTime();
////        
////        if(row !=0)
////        citation_size1 = citation_size1 / row;
////        
////        time = (end_time - start_time)/(row * 1.0 * 1000000000);
////        
////        System.out.print(time + "s  ");
////        
////        System.out.print(citation_size1 + " ");
////        
////        System.out.print(row + "    ");
//          
////        Set<Head_strs> head = citation_view_map1.keySet();
////        
////        int row_num = 0;
////        
////        double origin_citation_size = 0.0;
////        
////        for(Iterator iter = head.iterator(); iter.hasNext();)
////        {
////            Head_strs head_val = (Head_strs) iter.next();
////            
////            Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
////            
////            row_num++;
////            
////            for(int p = 0; p<c_view.size(); p++)
////            {
////                origin_citation_size += c_view.get(p).size();
////            }
////            
////        }
////        
////        
////        
////        if(row_num !=0)
////            origin_citation_size = origin_citation_size / row_num;
////        
//          
//          
//          System.out.print(Tuple_level_approach_min.covering_set_num * 1.0/Tuple_level_approach_min.tuple_num + " ");
//          
//          System.out.print("pre_processing::" + Tuple_level_approach_min.pre_processing_time + "  ");
//          
//          System.out.print("query::" + Tuple_level_approach_min.query_time + "    ");
//          
//          System.out.print("reasoning::" + Tuple_level_approach_min.reasoning_time + "    ");
//          
//          System.out.print("population::" + Tuple_level_approach_min.population_time + "  ");
//          
//          
//          
////        time = (end_time - start_time) * 1.0/1000000000;
////        
////        System.out.print("Aggregation_time::" + time + "    ");
////        
////        System.out.print("Aggregation_size::" + agg_citations.size() + "    ");
//          
////        start_time = System.nanoTime();
////        
////        Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
////        
////        agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
////                    
////        end_time = System.nanoTime();
////        
////        time = (end_time - start_time) * 1.0/1000000000;
////        
////        System.out.print("total_reasoning_time::" + time + "    ");
//          
//          Vector<String> agg_results = new Vector<String>();
//          
//          agg_results.add(Tuple_level_approach_min.covering_sets_query.toString());
//          
//          Query_operation.write2file(path + "covering_sets", agg_results);
//
////        for(int k = 0; k<Aggregation5.curr_res.size(); k++)
////        {
////            agg_results.add(Aggregation5.curr_res.get(k).toString());
////        }
//          
////        String aggregate_result = Aggregation5.curr_res.toString();
//          
////        System.out.println(aggregate_result);
//                      
//          
////        HashSet<String> agg_citations2 = Tuple_reasoning1_full_test.tuple_gen_agg_citations2(query);
////        
////        System.out.println("citation1::" + agg_citations);
////        
////        System.out.println("citation2::" + agg_citations2);
////        
////        if(!agg_citations.equals(agg_citations2))
////        {
////            
////            for(int p = 0; p < Aggregation3.author_lists.size(); p ++)
////            {
////                HashMap<String, HashSet<String>> citation1 = Aggregation3.author_lists.get(p);
////                
////                HashMap<String, HashSet<String>> citation2 = Aggregation5.full_citations.get(p);
////                
////                HashSet<String> author1 = citation1.get("author");
////                
////                HashSet<String> author2 = citation2.get("author");
////                
////                if(!author1.equals(author2))
////                {
////                    
////                    HashSet<String> curr_authors = new HashSet<String>();
////                    
////                    curr_authors.addAll(author1);
////                    
////                    curr_authors.removeAll(author2);
////                    
////                    System.out.println(Aggregation3.curr_res.get(p));
////                    
////                    System.out.println(Aggregation5.curr_res.get(p));
////                    
////                    System.out.println(p);
////                    
////                    System.out.println(author1.size());
////                    
////                    System.out.println(author2.size());
////                    
////                    int y = 0;
////                    
////                    y++;
////                }
////            }
////            
////            Assert.assertEquals(true, false);
////        }
//                      
////        diff_agg_time(tuple_level, query);
//          
//          System.out.println();
//          
//          c.close();
//          
////        System.out.println(agg_citations);
//          
//          
//      }
//      else
//      {
//          
//          Connection c = null;
//            PreparedStatement pst = null;
//          Class.forName("org.postgresql.Driver");
//          c = DriverManager
//              .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
//          
//          Semi_schema_level_approach_min.prepare_info = false;
//          
//          Semi_schema_level_approach_min.test_case = true;
//          
//          double end_time = 0;
//
//          double middle_time = 0;
//          
//          double start_time = 0;
//          
//          HashSet<String> agg_citations = null;
//          
//          
//          start_time = System.nanoTime();
//          
//          Semi_schema_level_approach_min.tuple_reasoning(query, c, pst);
//          
//          middle_time = System.nanoTime();
//          
//          Semi_schema_level_approach_min.prepare_citation_information(views, c, pst);
//          
//          agg_citations = Semi_schema_level_approach_min.tuple_gen_agg_citations(query, c, pst);
//                                                  
//          end_time = System.nanoTime();
//          
//          double time = (end_time - start_time)*1.0;
//          
//          double agg_time = (end_time - middle_time) * 1.0/1000000000;
//          
//          double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
//          
//          time = time /(times * 1000000000);
//                      
//          System.out.print(Semi_schema_level_approach_min.group_num + "   ");
//          
//          System.out.print(Semi_schema_level_approach_min.tuple_num + "   ");
//          
//          System.out.print(time + "s  ");
//          
//          System.out.print("total_exe_time::" + time + "  ");
//          
//          System.out.print("reasoning_time::" + reasoning_time + "    ");
//          
//          System.out.print("aggregation_time::" + agg_time + "    ");
//          
//          System.out.print("covering set::" + Semi_schema_level_approach_min.covering_sets_query + "  ");
//          
//          System.out.print("citation_size::" + Semi_schema_level_approach_min.covering_sets_query.c_vec.size() + "    ");
//          
//          HashMap<String, HashSet<String>> view_strs = new HashMap<String, HashSet<String>>();
//          
//          int covering_set_size = Semi_schema_level_approach_min.compute_distinct_num_covering_sets(view_strs);
//          
//          System.out.print("distinct_covering_set_size::" + covering_set_size + " ");
//          
//          Set<String> view_name = view_strs.keySet();
//          
//          int diff_view_num = 0;
//          
//          for(Iterator iter = view_name.iterator(); iter.hasNext();)
//          {
//              String curr_v_name = (String)iter.next();
//              
//              HashSet<String> curr_views = view_strs.get(curr_v_name);
//              
//              diff_view_num += curr_views.size();
//          }
//          
//          System.out.print("distinct_view_size::" + diff_view_num + " ");
//          
////        Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
////        
////        double citation_size1 = 0;
////        
////        int row = 0;
////        
////        start_time = System.nanoTime();
////        
////        for(Iterator iter = h_l.iterator(); iter.hasNext();)
////        {
////            Head_strs h_value = (Head_strs) iter.next();
////            
////            HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
////            
////            citation_size1 += citations.size();
////            
//////              System.out.println(citations);
////            
////            row ++;
////            
////            if(row >= 10)
////                break;
////            
////        }
////        
////        end_time = System.nanoTime();
////        
////        if(row !=0)
////        citation_size1 = citation_size1 / row;
////        
////        time = (end_time - start_time)/(row * 1.0 * 1000000000);
////        
////        System.out.print(time + "s  ");
////        
////        System.out.print(citation_size1 + " ");
////        
////        System.out.print(row + "    ");
//          
////        Set<Head_strs> head = citation_view_map1.keySet();
////        
////        int row_num = 0;
////        
////        double origin_citation_size = 0.0;
////        
////        for(Iterator iter = head.iterator(); iter.hasNext();)
////        {
////            Head_strs head_val = (Head_strs) iter.next();
////            
////            Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
////            
////            row_num++;
////            
////            for(int p = 0; p<c_view.size(); p++)
////            {
////                origin_citation_size += c_view.get(p).size();
////            }
////            
////        }
////        
////        
////        
////        if(row_num !=0)
////            origin_citation_size = origin_citation_size / row_num;
////        
//          
//          
//          System.out.print(Semi_schema_level_approach_min.covering_set_num * 1.0/Semi_schema_level_approach_min.tuple_num + " ");
//          
//          System.out.print("pre_processing::" + Semi_schema_level_approach_min.pre_processing_time + "    ");
//          
//          System.out.print("query::" + Semi_schema_level_approach_min.query_time + "  ");
//          
//          System.out.print("reasoning::" + Semi_schema_level_approach_min.reasoning_time + "  ");
//          
//          System.out.print("population::" + Semi_schema_level_approach_min.population_time + "    ");
//          
//          System.out.println();
//          
//          Vector<String> agg_results = new Vector<String>();
//          
//          agg_results.add(Semi_schema_level_approach_min.covering_sets_query.toString());
//          
//          Query_operation.write2file(path + "covering_sets", agg_results);
//
//          c.close();
//          
////        Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
//          
////        Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
//          
//          
////                    
////        reset();
////        
////        Vector<Query> queries = query_generator.gen_queries(j, size_range);
////        
////        Query query = query_generator.gen_query(j, c, pst);
//          
////        System.out.println(queries.get(0));
//
//          
////        Vector<String> relation_names = get_unique_relation_names(queries.get(0));
//          
////        view_generator.generate_store_views(relation_names, num_views);
//          
////        query_storage.store_query(queries.get(0), new Vector<Integer>());
//          
//          
//      }
//  }
//  
  


}
