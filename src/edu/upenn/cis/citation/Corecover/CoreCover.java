package edu.upenn.cis.citation.Corecover;
/* 
 * CoreCover.java
 * -------------
 * Implements our CoreCover algorithm.
 * $Id: CoreCover.java,v 1.25 2000/11/28 00:48:08 chenli Exp $
 */

import java.util.*;

import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;

public class CoreCover {

  public static int numVTs = 0;
  public static long groupedNumVTs = 0;   // grouped view tuples
  public static int numMR = 0;
  public static int numGMR = 0;
  public static int sizeGMR = 0;
  public static long timeMR = 0;
  public static long timeFirstGMR = 0; // time when the 1st GMR is generated
  public static long timeAllGMR = 0;   // time when all GMRs are generated

  public static HashMap<String, HashSet<Integer>> query_subgoal_id_mappings = new HashMap<String, HashSet<Integer>>();
  

  static long startTime = 0;
  static long endTime   = 0;

  // calls our CoreCover algorithm to generate rewritings
  static HashSet genPlan(Query query, Vector views) {

    numVTs = 0;
    numMR         = 0;
    numGMR        = 0;
    sizeGMR       = 0;
    timeMR        = 0;
    timeFirstGMR  = 0;
    timeAllGMR    = 0;  
    startTime     = 0;
    endTime       = 0;

    startTime  = System.currentTimeMillis();

    // minimize the query
    query = query.minimize();

    // construct the canonical db
    Database canDb = constructCanonicalDB(query);
    UserLib.myprintln("canDb = " + canDb.toString());

    // compute view tuples
    HashSet viewTuples = computeViewTuples(canDb, views);

    // compute tuple-cores
    computeTupleCores(viewTuples, query);
    
    
//    computeViewCost(viewTuples);

    // group view tuples according to their tuple-cores
    numVTs        = computeNumVTs(viewTuples);
//    viewTuples    = groupViewTuples(viewTuples);
    groupedNumVTs = viewTuples.size();

    UserLib.myprintln("There are ***" + numVTs + 
		      "*** compressed view tuples = " + viewTuples);

    int newNum = viewTuples.size();
    //System.out.println("oldTupleNum = " + oldNum + " newTupleNum = " + newNum);
    UserLib.myprintln("viewTuples = " + viewTuples);
    // use tuple-cores to cover query subgoals
    HashSet rewritings = coverQuerySubgoals(viewTuples, query);
    
    for(Iterator iter = viewTuples.iterator(); iter.hasNext();)
    {
    	Tuple tuple = (Tuple) iter.next();
    	
    	int y = 0;
    	y++;
    	
    }
    
    
    UserLib.myprintln("rewritings = " + rewritings);

    if (!rewritings.isEmpty()) { // only count non-empty rewritings
      endTime = System.currentTimeMillis();
      timeMR = endTime - startTime;
    }

    return rewritings;
  }

  /**
   * constructs a canonical database of a query
   */
  public static Database constructCanonicalDB(Query query) {
    HashSet tuples = new HashSet();

    for (Iterator iter = query.getBody().iterator(); iter.hasNext();) {
      Subgoal subgoal = (Subgoal) iter.next();
      Tuple tuple = new Tuple(subgoal, query.subgoal_name_mapping);
      tuples.add(tuple);  // we treat each subgoal as a "tuple"
    }

    HashMap<String, HashSet<Integer>> cluster_subgoal_ids = get_cluster_subgoal_ids(query.body, query.subgoal_name_mapping);
    
    return new Database(tuples, query.subgoal_name_mapping, cluster_subgoal_ids);
  }
  
  static HashMap<String, HashSet<Integer>> get_cluster_subgoal_ids(Vector<Subgoal> subgoals, HashMap<String, String> subgoal_name_mappings)
  {
    HashMap<String, HashSet<Integer>> cluster_subgoal_ids = new HashMap<String, HashSet<Integer>>();
    
    for(int i = 0; i<subgoals.size(); i++)
    {
      Subgoal subgoal = (Subgoal) subgoals.get(i);
      
      String origin_subgoal_name = subgoal_name_mappings.get(subgoal.name);
      
      if(cluster_subgoal_ids.get(origin_subgoal_name) == null)
      {
        HashSet<Integer> ids = new HashSet<Integer>();
        
        ids.add(i);
        
        cluster_subgoal_ids.put(origin_subgoal_name, ids);
      }
      else
      {
        cluster_subgoal_ids.get(origin_subgoal_name).add(i);
      }
      
    }
    
    return cluster_subgoal_ids;
  }
  
    
 /**
  * given a canonical db and a set of views, computes the view tuples
  */
  
  static HashMap<String, Integer> get_subgoal_id_mappings(Query view)
  {
    HashMap<String, Integer> subgoal_id_mappings = new HashMap<String, Integer>();
    
    for(int i = 0; i<view.body.size(); i++)
    {
      Subgoal subgoal = (Subgoal) view.body.get(i);
      
      subgoal_id_mappings.put(subgoal.name, i);
    }
    
    return subgoal_id_mappings;
    
  }
  
  public static HashSet computeViewTuples(Database canDb, Vector views) {
	HashSet viewTuples = new HashSet();
	
//	System.out.println("tuples::" + canDb.tuples);
	
    for (int i = 0; i < views.size(); i ++) {
      Query view = (Query) views.elementAt(i);

//      System.out.println(view);
      
      HashSet<Tuple> all_view_mappings = canDb.execQuery2(view);
      
//      System.out.println("relation:::" + rel);
      
      
//      if(!rel.getTuples().isEmpty())
//      {
//    	  for(int p = 0; p<view.body.size(); p++)
//    	  {
//    		  Subgoal subgoal = (Subgoal)view.body.get(p);
//    		  
//    		  System.out.print(subgoal.name + " ");
//    	  }
//    	  
//    	  System.out.println();
//      }
//      
      view.subgoal_id_mappings = get_subgoal_id_mappings(view);
      
      for (Iterator iter = all_view_mappings.iterator(); iter.hasNext();) {
    	      	  
    	  Tuple tuple = (Tuple) iter.next();
    	  
//          System.out.println(tuple.toString() + tuple.mapSubgoals_str);
    	  
    	  if(!set_tuple_lambda_term(tuple, view))
    	  {
    		  continue;
    	  }

    	  set_tuple_conditions(tuple, view.conditions, view);
    	  
          viewTuples.add(tuple);
          
          tuple.setQuery(view);
      }
       // add them to the results
    }
    return viewTuples;
  }
  
  static boolean set_tuple_lambda_term(Tuple tuple, Query view)
  {
	  Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
	  
	  for(int i = 0; i<view.lambda_term.size(); i++)
	  {
		  String curr_lambda_name = tuple.phi_str.apply(view.lambda_term.get(i).name);
		  
		  if(curr_lambda_name == null)
			  return false;
		  
		  String curr_table_name = curr_lambda_name.substring(0, curr_lambda_name.indexOf(populate_db.separator));
		  
		  Lambda_term l_term = new Lambda_term(curr_lambda_name, curr_table_name);
		  
		  lambda_terms.add(l_term);
	  }
	  
	  tuple.lambda_terms = lambda_terms;
	  
	  return true;
  }
  
  static void set_tuple_conditions(Tuple tuple, Vector<Conditions> view_conditions, Query view)
  {
      Vector<Conditions> conditions = new Vector<Conditions>();
      
      for(int i = 0; i<view_conditions.size(); i++)
      {
          boolean get_mapping1 = true;
          
          boolean get_mapping2 = true;
          
          String curr_arg1 = tuple.phi_str.apply(view_conditions.get(i).subgoal1 + populate_db.separator + view_conditions.get(i).arg1.name);
          
          if(curr_arg1 == null)
              get_mapping1 = false;
          
          String subgoal1 = (get_mapping1 == true) ? curr_arg1.substring(0, curr_arg1.indexOf(populate_db.separator)) : view_conditions.get(i).subgoal1;
          
          curr_arg1 = (get_mapping1 == true) ? curr_arg1.substring(curr_arg1.indexOf(populate_db.separator) + 1, curr_arg1.length()):view_conditions.get(i).arg1.name;
          
          String curr_arg2 = new String();
          
          String subgoal2 = new String();
          
          Conditions condition = null;
          
          if(view_conditions.get(i).arg2.isConst())
          {
              curr_arg2 = view_conditions.get(i).arg2.name;
              
//            if(get_mapping1)
                  get_mapping2 = true;
              
              condition = new Conditions(new Argument(curr_arg1, subgoal1), subgoal1, view_conditions.get(i).op, new Argument(curr_arg2), subgoal2);
          }
          else
          {
              curr_arg2 = tuple.phi_str.apply(view_conditions.get(i).subgoal2 + populate_db.separator + view_conditions.get(i).arg2.name);
                          
              if(curr_arg2 == null)
                  get_mapping2 = false;
              
              subgoal2 = (get_mapping2 == true) ? curr_arg2.substring(0, curr_arg2.indexOf(populate_db.separator)):view_conditions.get(i).subgoal2;
              
              curr_arg2 = (get_mapping2 == true) ? curr_arg2.substring(curr_arg2.indexOf(populate_db.separator) + 1, curr_arg2.length()): view_conditions.get(i).arg2.name;
              
              condition = new Conditions(new Argument(curr_arg1, subgoal1), subgoal1, view_conditions.get(i).op, new Argument(curr_arg2, subgoal2), subgoal2);
          }
                  
          condition.get_mapping1 = get_mapping1;
          
          condition.get_mapping2 = get_mapping2;
          
//        if(!get_mapping1 && get_mapping2)
//        {
//          condition.swap_args();
//        }
          
          
          if((condition.get_mapping1 & condition.get_mapping2) == false)
          {
            cluster_relational_subgoals(view, tuple, view_conditions.get(i), i, condition.get_mapping1, condition.get_mapping2);
            
          }
          
          if(!get_mapping1 && !get_mapping2)
          {
            
//          cluster_relational_subgoals(view, tuple, condition, i, condition.get_mapping1, condition.get_mapping2);
            
            continue;
            
            
          }
          if(!get_mapping1 && view_conditions.get(i).arg2.isConst())
          {
//          cluster_relational_subgoals(view, tuple, condition, i, condition.get_mapping1, condition.get_mapping2);
            
            continue;
          }
          
          conditions.add(condition);
          
//          System.out.println(condition);
//          
//          System.out.println(condition.get_mapping1);
//          
//          System.out.println(condition.get_mapping2);
          
      }
      
      tuple.conditions = conditions;
      
//    System.out.println(tuple.cluster_subgoal_ids);
//    
//    System.out.println(tuple.cluster_patial_mapping_condition_ids);
//    
//    System.out.println(tuple.cluster_non_mapping_condition_ids);
  }
  
  static void cluster_relational_subgoals(Query view, Tuple tuple, Conditions condition, int j, boolean get_mapping1, boolean get_mapping2)
  {
//    Vector<HashSet<Integer>> cluster_ids = new Vector<HashSet<Integer>>();
    
//    for(int j = 0; j<view.conditions.size(); j++)
    {
      String subgoal1 = condition.subgoal1;
      
      String subgoal2 = condition.subgoal2;
      
      int id1 = view.subgoal_id_mappings.get(subgoal1);
      
      int id2 = -1;
      
      if(!(subgoal2 == null) && !subgoal2.isEmpty())
        id2 = view.subgoal_id_mappings.get(subgoal2);
      
      
      int i = 0;
      
      int matched_cluster_id1 = -1;
      
      int matched_cluster_id2 = -1;
      
      for(i = 0; i<tuple.cluster_subgoal_ids.size(); i++)
      {
        HashSet<Integer> curr_cluster = tuple.cluster_subgoal_ids.get(i);
        
        
        
        for(Integer id:curr_cluster)
        {
          if(id == id1)
          {
            matched_cluster_id1 = i;
            
          }
          
          if(id == id2)
          {
            matched_cluster_id2 = i;
            
          }
        }
      }
      
      if(matched_cluster_id1 < 0 && matched_cluster_id2 < 0)
      {
        HashSet<Integer> new_clusters = new HashSet<Integer>();
        
//        System.out.println(id1);
//        
//        System.out.println(id2);
//        
//        System.out.println(get_mapping1);
//        
//        System.out.println(get_mapping2);
        
        if(!get_mapping1)
          new_clusters.add(id1);
        
        if(id2 >= 0 && !get_mapping2)
          new_clusters.add(id2);
        
        HashSet<Integer> curr_cluster_condition_ids = new HashSet<Integer>();
        
        curr_cluster_condition_ids.add(j);
        
        if(get_mapping1 | get_mapping2 == true)
        {
          tuple.cluster_patial_mapping_condition_ids.add(curr_cluster_condition_ids);
          
          tuple.cluster_non_mapping_condition_ids.add(new HashSet<Integer>());
        }
        else
        {
          tuple.cluster_patial_mapping_condition_ids.add(new HashSet<Integer>());
          
          tuple.cluster_non_mapping_condition_ids.add(curr_cluster_condition_ids);
        }
        
        tuple.cluster_subgoal_ids.add(new_clusters);
        
      }
      else
      {
        if(matched_cluster_id1 < 0 && matched_cluster_id2 >= 0)
        {
          if(!get_mapping1)
            tuple.cluster_subgoal_ids.get(matched_cluster_id2).add(id1);
          if(get_mapping1 | get_mapping2 == true)
          {
            tuple.cluster_patial_mapping_condition_ids.get(matched_cluster_id2).add(j);
          }
          else
          {
            tuple.cluster_non_mapping_condition_ids.get(matched_cluster_id2).add(j);
          }
        }
        else
        {
          if(matched_cluster_id2 < 0 && matched_cluster_id1 >= 0)
          {
            
            if(id2 >= 0 && !get_mapping2)
              tuple.cluster_subgoal_ids.get(matched_cluster_id1).add(id2);
            
            if(get_mapping1 | get_mapping2 == true)
            {
              tuple.cluster_patial_mapping_condition_ids.get(matched_cluster_id1).add(j);
            }
            else
            {
              tuple.cluster_non_mapping_condition_ids.get(matched_cluster_id1).add(j);
            }

          }
          else
          {
            tuple.cluster_subgoal_ids.get(matched_cluster_id1).addAll(tuple.cluster_subgoal_ids.get(matched_cluster_id2));
                        
            tuple.cluster_subgoal_ids.removeElementAt(matched_cluster_id2);
            
            tuple.cluster_patial_mapping_condition_ids.get(matched_cluster_id1).addAll(tuple.cluster_patial_mapping_condition_ids.get(matched_cluster_id2));
            
            tuple.cluster_patial_mapping_condition_ids.removeElementAt(matched_cluster_id2);
            
            tuple.cluster_non_mapping_condition_ids.get(matched_cluster_id1).addAll(tuple.cluster_non_mapping_condition_ids.get(matched_cluster_id2));
            
            tuple.cluster_non_mapping_condition_ids.removeElementAt(matched_cluster_id2);
            
          }
        }
      }
      
      
    }
    
  }
  
  static void set_tuple_conditions(Tuple tuple, Query view)
  {
	  Vector<Conditions> conditions = new Vector<Conditions>();
	  
	  for(int i = 0; i<view.conditions.size(); i++)
	  {
		  boolean get_mapping1 = true;
		  
		  boolean get_mapping2 = true;
		  
		  String curr_arg1 = tuple.phi_str.apply(view.conditions.get(i).subgoal1 + populate_db.separator + view.conditions.get(i).arg1.name);
		  
		  if(curr_arg1 == null)
			  get_mapping1 = false;
		  
		  String subgoal1 = (get_mapping1 == true) ? curr_arg1.substring(0, curr_arg1.indexOf(populate_db.separator)) : view.conditions.get(i).subgoal1;
		  
		  curr_arg1 = (get_mapping1 == true) ? curr_arg1.substring(curr_arg1.indexOf(populate_db.separator) + 1, curr_arg1.length()):view.conditions.get(i).arg1.name;
		  
		  String curr_arg2 = new String();
		  
		  String subgoal2 = new String();
		  
		  Conditions condition = null;
		  
		  if(view.conditions.get(i).arg2.isConst())
		  {
			  curr_arg2 = view.conditions.get(i).arg2.name;
			  
			  if(get_mapping1)
				  get_mapping2 = true;
			  
			  condition = new Conditions(new Argument(curr_arg1, subgoal1), subgoal1, view.conditions.get(i).op, new Argument(curr_arg2), subgoal2);
		  }
		  else
		  {
			  curr_arg2 = tuple.phi_str.apply(view.conditions.get(i).subgoal2 + populate_db.separator + view.conditions.get(i).arg2.name);
			  			  
			  if(curr_arg2 == null)
				  get_mapping2 = false;
			  
			  subgoal2 = (get_mapping2 == true) ? curr_arg2.substring(0, curr_arg2.indexOf(populate_db.separator)):view.conditions.get(i).subgoal2;
			  
			  curr_arg2 = (get_mapping2 == true) ? curr_arg2.substring(curr_arg2.indexOf(populate_db.separator) + 1, curr_arg2.length()) : view.conditions.get(i).arg2.name;
			  
			  condition = new Conditions(new Argument(curr_arg1, subgoal1), subgoal1, view.conditions.get(i).op, new Argument(curr_arg2, subgoal2), subgoal2);
		  }
		  
		  condition.get_mapping1 = get_mapping1;
		  
		  condition.get_mapping2 = get_mapping2;
		  
		  if(!get_mapping1 && !get_mapping2)
			  continue;
		  if(!get_mapping1 && get_mapping2 && view.conditions.get(i).arg2.isConst())
			  continue;
		  
		  conditions.add(condition);
		  
	  }
	  
	  tuple.conditions = conditions;
  }
  
  public static HashSet computeViewTuples(Database canDb, Vector views, Query query) {
	    HashSet viewTuples = new HashSet();
	    for (int i = 0; i < views.size(); i ++) {
	      Query view = (Query) views.elementAt(i);

	      viewTuples.addAll(canDb.execQuery2(view));
	      
//	      for (Iterator iter = rel.getTuples().iterator(); iter.hasNext();) {
//	    	  Tuple tuple = (Tuple) iter.next();
//	          tuple.lambda_terms = view.lambda_term;
//	          tuple.conditions = view.conditions;
//	          viewTuples.add(tuple);
//	      }
	       // add them to the results
	    }
	    return viewTuples;
	  }
  
  
  static void computeViewCost(HashSet viewTuples)
  {
	  int i = 0;
	  for (Iterator iter = viewTuples.iterator(); iter.hasNext();) {
	      Tuple viewTuple = (Tuple) iter.next();
	      viewTuple.cal_cost();
	  }
  }
  /**
   * computes the tuple cores of all view tuples.
   */
  public static void computeTupleCores(HashSet viewTuples, Query query) {
    for (Iterator iter = viewTuples.iterator(); iter.hasNext();) {
      Tuple viewTuple = (Tuple) iter.next();
      computeTupleCore(viewTuple, query);
    }
  }

  /**
   * Computes the tuple core of a vew tuple, assuming the query is minimal.
   * find a maximum subset G of query subgoals such that there is a set
   * H of subgoals in t^exp, such that there is a mapping \mu from G to
   * H ,and the mapping has the three properties
   */ 
  static void computeTupleCore(Tuple viewTuple, Query query) {

    // view-tuple expansion
    Mapping phi          = viewTuple.getMapping();
    Query   view         = viewTuple.getQuery();
    Query   viewExp      = phi.apply(view);
    Vector  viewSubgoals = viewExp.getBody();
    HashSet vSubgoalSubsets = UserLib.genSubsets(viewSubgoals);
    
    // query subgoals
    Vector  querySubgoals   = query.getBody();
    HashSet qSubgoalSubsets = UserLib.genSubsets(querySubgoals);

    int upperBound = querySubgoals.size();
    if (upperBound > viewSubgoals.size())
      upperBound = viewSubgoals.size();

    // scans the subsets from the largests one to the smallest one
    for (int size = upperBound; size >= 1; size --) 
    {
      for (Iterator iter = qSubgoalSubsets.iterator(); iter.hasNext();) 
      {
	// for this "i"^th around, we consider only subsets with size i 
	HashSet qSubgoalSubset = (HashSet) iter.next();
	if (qSubgoalSubset.size() != size) 
	  continue;

	if (findCore(qSubgoalSubset, query, viewTuple, viewExp,
		     vSubgoalSubsets)) {
	  UserLib.myprintln("!!!!Find a core: " + qSubgoalSubset);
	  viewTuple.setCore(qSubgoalSubset);
	  viewTuple.set_cost(0);//cal_cost(view, query, phi));
	  return;
	}
      }
    }

    // empty core
    viewTuple.setCore(new HashSet());
  }

  
//  static double cal_cost(Query view, Query query, Mapping phi)
//  {
////	  double cost = 1;
////	  if(!view.lambda_term.isEmpty())
////	  {
////		  
////		  Vector temp = view.lambda_term;
////		  boolean map2const = true;
////		  
////		  
////		  for(int k =0;k<temp.size();k++)
////		  {
////			  Argument arg = (Argument) phi.map.get(temp.get(k));
////			  if(arg.type != 1)
////				  map2const = false;
////			  
////		  }
////		  if(!map2const)
////			  cost = 10;
////	  }
//	  
//	  
//	  return cost;
//  }
//  
  /**
   * Given a set of query subgoals, a view tuple, and all subsets of the
   * subgoals in the tuple's expansion, checks if there is a mapping from
   * this query-subgoal subset to a view subgoal subset such that the
   * mapping has the "three properties."
   */
  static boolean findCore(HashSet qSubgoalSubset, Query query,
			  Tuple viewTuple, Query viewExp,
			  HashSet vSubgoalSubsets) {

    // change the set to a sequence
    Vector qSubgoalSeq = new Vector(qSubgoalSubset);
    
    for (Iterator iter1 = vSubgoalSubsets.iterator(); iter1.hasNext();) 
    {
      HashSet vSubgoalSubset = (HashSet) iter1.next();
      if (vSubgoalSubset.size() != qSubgoalSubset.size()) 
	continue;

      HashSet vSubgoalsPermus = UserLib.genPermutations(vSubgoalSubset);

      for (Iterator iter2 = vSubgoalsPermus.iterator(); iter2.hasNext();) 
      {
	Vector vSubgoalSeq = (Vector) iter2.next();
	if (testCoreMapping(qSubgoalSeq, query, vSubgoalSeq, viewExp)) 
	  return true; // found it!
      }
    }
    
    return false;
  }

  /**
   * Given a sequence of query subgoals and a sequence of view subgoals,
   * tests whether there is a mapping from the former to the latter that
   * satisifies the three properties in the definition of tuple-core.
   */
  static boolean testCoreMapping(Vector qSubgoalSeq, Query query,
				 Vector vSubgoalSeq, Query viewExp) {
    if (qSubgoalSeq.size() != vSubgoalSeq.size())
      UserLib.myerror("CoreCover.testCoreMapping(): wrong sequences");

    HashMap mu = new HashMap();
    for (int i = 0; i < qSubgoalSeq.size(); i ++) {
      Subgoal querySubgoal = (Subgoal) qSubgoalSeq.elementAt(i);
      Subgoal viewSubgoal  = (Subgoal) vSubgoalSeq.elementAt(i);

      if (!querySubgoal.isSameName(viewSubgoal))
	return false;

      Vector queryArgs = querySubgoal.getArgs();
      Vector viewArgs  = viewSubgoal.getArgs();
      if (queryArgs.size() != viewArgs.size()) 
	UserLib.myerror("CoreCover.testCoreMapping(), wrong args!");

      // checks if a query arg is unifiable with the corresponding view arg
      for (int j = 0; j < queryArgs.size(); j ++) {
	Argument queryArg = (Argument) queryArgs.elementAt(j);
	Argument viewArg  = (Argument)  viewArgs.elementAt(j);
	
	if (queryArg.isConst()) {
	  if (!viewArg.isConst()) // constant -> same constant
	    return false;
	  if (!queryArg.equals(viewArg))
	    return false;
	  continue;
	}

	// queryArg is a var
	if (viewExp.isDistVar(viewArg)) {
	  if (!queryArg.equals(viewArg))  // View D vars, identity
	    return false;
	  continue;
	}

	// viewArg is ND
	if (query.isDistVar(queryArg))  // NOT D (query) -> ND (view)
	  return false;

	// queryArg = ND, and viewArg = ND
	if (!checkProperty2(queryArg, qSubgoalSeq, query))
	  return false;

	// remember the 1-to-1 mapping
	Argument image = (Argument) mu.get(queryArg);
	if (image == null)
	  mu.put(queryArg, viewArg);
	else {
	  if (!image.equals(viewArg)) // one-to-one
	    return false;
	}
      }
    }
    return true;
  }

  /**
   * checks the second property of the mapping. i.e., tests if all the
   * subgoals in the query that use a given queryArg appear in qSubgoalSeq.
   */
  static boolean checkProperty2(Argument queryArg, Vector qSubgoalSeq, 
				Query query) {

    Vector body = query.getBody();
    //UserLib.myprintln("body = " + body);

    for (int i = 0; i < body.size(); i ++) {
      Subgoal subgoal = (Subgoal) body.elementAt(i);
      Vector args = subgoal.getArgs();
      if (args.contains(queryArg) && !qSubgoalSeq.contains(subgoal))
	return false;
    }

    return true;
  }
  
  /**
   * Computes the tuple core of a vew tuple, assuming both the query and
   * the view are minimal already.
   * TODO: what if they are NOT minimal.
   */ 
  static void computeTupleCore_OLD(Tuple viewTuple, Query query) {
    Query   view = viewTuple.getQuery();
    
    // sets core to the target query subgoals under the mapping 
    HashSet core = viewTuple.getTargetSubgoals();
    UserLib.myprintln("before shrinking: core = " + core);

    // shrinking core
    Mapping phi = viewTuple.getMapping();
    HashMap reversedMap = new HashMap();
    // scans the args in phi
    Set entrySet = phi.getMap().entrySet();
    for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
      Map.Entry mapEntry = (Map.Entry) iter.next();
      Argument viewArg   = (Argument) mapEntry.getKey();
      Argument queryArg  = (Argument) mapEntry.getValue();

      if (removeable(core, phi, reversedMap, view, viewArg, query, queryArg)) {
	// finds a conflict, remove all query subgoals that use this arg
	core = removeConflictSubgoals(core, queryArg);
      } else {
	reversedMap.put(queryArg, viewArg); // reverses the mapping
      }
    }    

    UserLib.myprintln("Final core = " + core);
    viewTuple.setCore(core);
  }

  /*
   * checks if some subgoals should be removed from a core
   */
  static boolean removeable(HashSet core, Mapping phi, HashMap reversedMap,
			    Query view, Argument viewArg, 
			    Query query, Argument queryArg) {

    // Removes subgoals if one of the following conditions is satisfied:
    Argument otherViewArg = (Argument) reversedMap.get(queryArg);
    if (otherViewArg != null && !otherViewArg.equals(viewArg)) {
      // we have a different view arg mapped to this query arg
      Argument image = null;
      if (view.isDistVar(otherViewArg))
	image = phi.apply(otherViewArg); // ?????
      else
	image = otherViewArg;
    
      if (image == null)
	UserLib.myerror("CoreCover.removeable(): image is missing, 1.");
      if (!image.equals(queryArg)) // they should have the same image
	return true;
    }

    // 1. viewArg = "D", not removed
    if (view.isDistVar(viewArg))
      return false;

    // 2. viewArg = "ND" and queryArg = "D"
    if (query.isDistVar(queryArg)) {
      //System.out.println("case 2: queryArg = D, viewArg = ND");
      return true;
    }

    // 3. both are "ND", and some query subgoals that use this query arg
    //    are not in the core
    Vector body = query.getBody();
    for (int i = 0; i < body.size(); i ++) {
      Subgoal subgoal = (Subgoal) body.elementAt(i);
      if (subgoal.getArgs().contains(queryArg) && !core.contains(subgoal))
	return true;
    }

    // 1. two variables in the view are mapped to
    //    the same variable Y in the query, then remove all query
    //    subgoals that use this variable Y    
    /*Argument otherViewArg = (Argument) reversedMap.get(queryArg);
    if (otherViewArg != null && !otherViewArg.equals(viewArg)) {

      Argument image = null;
      if (view.isDist(otherViewArg))
	image = phi.get(otherViewArg); // ?????
      else
	image = otherViewArg;

      if (image == null)
	UserLib.myerror("CoreCover.removeable(): image is missing, 1.");
      if (!image.equals(queryArg)) // they should have the same image
	return true;
    }*/
    
    return false;
  }

  /**
   * removes the subgoals that use a query arg from a core
   */
  static HashSet removeConflictSubgoals(HashSet core, Argument queryArg) {
    HashSet result = new HashSet();

    for (Iterator iter = core.iterator(); iter.hasNext();) {
      Subgoal subgoal = (Subgoal) iter.next();

      // checks if this subgoal uses this query arg
      if (!subgoal.getArgs().contains(queryArg)) 
	result.add(subgoal);
      else {
	UserLib.myprintln("Remove subgoal " + subgoal);
      }
    }
    
    return result;
  }

  static HashSet<Argument> get_Covered_head_args(HashSet viewTuples, Query query)
  {
	  HashSet<Argument> coreUnion = new HashSet<Argument>();
	    for (Iterator iter2 = viewTuples.iterator(); iter2.hasNext();) {
	      Tuple viewTuple = (Tuple) iter2.next();
//	      HashSet core = viewTuple.getCore();
//	      coreUnion.addAll(core);
	      coreUnion.addAll(viewTuple.getArgs());
	      
	    }
	    
	    coreUnion.retainAll(query.head.args);

	    return coreUnion;
  }
  
  public static HashSet coverQuerySubgoals(HashSet viewTuples, Query query) {
    HashSet rewritings = new HashSet();
        
    // considers all the subsets in the descending order
    //HashSet tupleSubsets = UserLib.genSubsets(viewTuples);
    //UserLib.myprintln("after UserLib.genSubsets()");

    sizeGMR = 0;
    numMR   = 0;
    numGMR  = 0;

    
//    if (upperBound >= query.getSubgoalNum()) // according to LMSS95
//      upperBound = query.getSubgoalNum();

    // if the union of the tuple-cores doesn't cover all query subgoals,
    // just return no rewriting
    HashSet covered_relations = get_CoverSubgoals(viewTuples, query);
    
    HashSet<Argument> covered_head_args = get_Covered_head_args(viewTuples, query);
    
    int upperBound = (covered_head_args.size() > covered_relations.size()) ? covered_head_args.size() : covered_relations.size();

    
//    if (!unionCoverSubgoals(viewTuples, query))
//	return rewritings;

    // scans the subsets from the smallest one to the largest one
    boolean found = false;
    
    
    double min_cost = Double.MAX_VALUE;
    Rewriting min_rewriting = null;
    
    for (int size = 1; (size <= upperBound); size ++) 
    {
      // considers subsets with the current size 
      //System.out.println("# of view tuples = " + viewTuples.size() +
      //" size = " + size);
    	
        HashSet tupleSubsets = UserLib.genSubsets(viewTuples, size);

    	
//    	if(curr_rewritings.size() == 0)
    		gen_rewriting(tupleSubsets, size, covered_relations, covered_head_args, query, rewritings);
//    	else
//    	{
    		
//    		HashSet curr_rewritings_all = new HashSet();
//    		
//    		for(Iterator iter = curr_rewritings.iterator();iter.hasNext();)
//    		{
//    			HashSet curr_rewriting = (HashSet) iter.next();
//    			curr_rewritings_all.addAll(curr_rewriting);
//    			
//    		}
//			gen_rewriting(gen_complementary_set(tupleSubsets, curr_rewritings), size, covered_relations, query, rewritings, curr_rewritings);
//    	}

//      if (found) {
//    	  rewritings.add(min_rewriting);
//	endTime = System.currentTimeMillis();
//	timeAllGMR = endTime - startTime;
//      }
    }

    return rewritings;
  }
  
  public static HashSet coverQuerySubgoals(HashSet viewTuples, Query query, boolean opt) {
	    HashSet rewritings = new HashSet();
	    	    
	    // considers all the subsets in the descending order
	    //HashSet tupleSubsets = UserLib.genSubsets(viewTuples);
	    //UserLib.myprintln("after UserLib.genSubsets()");

	    sizeGMR = 0;
	    numMR   = 0;
	    numGMR  = 0;

	    int upperBound = query.body.size();
//	    if (upperBound >= query.getSubgoalNum()) // according to LMSS95
//	      upperBound = query.getSubgoalNum();

	    // if the union of the tuple-cores doesn't cover all query subgoals,
	    // just return no rewriting
	    HashSet covered_relations = get_CoverSubgoals(viewTuples, query);

	    
//	    if (!unionCoverSubgoals(viewTuples, query))
//		return rewritings;

	    // scans the subsets from the smallest one to the largest one
	    boolean found = false;
	    
	    
	    double min_cost = Double.MAX_VALUE;
	    Rewriting min_rewriting = null;
	    
//	    HashSet non_covering = new HashSet();
	    
	    HashSet covering_set_size = new HashSet<>();
	    
	    for (int size = 1; (size <= upperBound); size ++) 
	    {
	      // considers subsets with the current size 
	      //System.out.println("# of view tuples = " + viewTuples.size() +
	      //" size = " + size);
	    	
	    	
	        HashSet non_redundant_set = UserLib.genSubsets(viewTuples, size, rewritings, covering_set_size);
	        
//	        non_covering.clear();
	        
//	        covering_set_size.clear();

	    	
//	    	if(curr_rewritings.size() == 0)
//	    	{
	    		gen_rewriting(non_redundant_set, size, covered_relations, query, rewritings, covering_set_size);
	    		
//	    		if(non_covering.isEmpty())
//	    			break;
//	    	}
//	    	else
//	    	{
	    		
//	    		HashSet curr_rewritings_all = new HashSet();
//	    		
//	    		for(Iterator iter = curr_rewritings.iterator();iter.hasNext();)
//	    		{
//	    			HashSet curr_rewriting = (HashSet) iter.next();
//	    			curr_rewritings_all.addAll(curr_rewriting);
//	    			
//	    		}
//				gen_rewriting(gen_complementary_set(tupleSubsets, curr_rewritings), size, covered_relations, query, rewritings, curr_rewritings);
//	    	}

//	      if (found) {
//	    	  rewritings.add(min_rewriting);
//		endTime = System.currentTimeMillis();
//		timeAllGMR = endTime - startTime;
//	      }
	    }

	    return rewritings;
	  }

  
  
  static HashSet gen_complementary_set(HashSet viewTuples, HashSet curr_rewriting)
  {
	  HashSet rs = new HashSet();
	  
	  for(Iterator iter1 = viewTuples.iterator(); iter1.hasNext();)
	  {
		  HashSet t1 = (HashSet)iter1.next();
		  
		  int num = 0;
		  
//		  System.out.println(t1);
		  
		  for(Iterator iter2 = curr_rewriting.iterator(); iter2.hasNext();)
		  {
			  HashSet t2 = (HashSet) iter2.next();
			  
			  if(t1.contains(t2))
			  {
				  System.out.println("contained");
				  
				  break;
			  }
			  num++;
		  }
		  
		  if(num >= curr_rewriting.size())
			  rs.add(t1);
	  }
	  
//	  rs.removeAll(curr_rewriting);
	  
	  return rs;
  }
  
  static void gen_rewriting(HashSet tupleSubsets, int size, HashSet covered_relations, Query query, HashSet rewritings)
  {
      //System.out.println("# of tupleSubsets = " + tupleSubsets.size());

      
      for (Iterator iter = tupleSubsets.iterator(); iter.hasNext();) 
      {
	// for this "i"^th around, we consider only subsets with size i 
	HashSet tupleSubset = (HashSet) iter.next();
	if (tupleSubset.size() != size) 
	  UserLib.myerror("CoreCover.coverQuerySubgoals(), error!");

	if (unionCoverSubgoals(tupleSubset, covered_relations))  
	{// found one
	  
//	  if(cost < min_cost)
//	  {
//		  min_rewriting = new Rewriting(tupleSubset, query);
//		  min_cost = cost;
//	  }
	  
//	  if(!curr_rewritings.contains(tupleSubset))
	  {
		  rewritings.add(new Rewriting(tupleSubset, query));
		  
//		  curr_rewritings.add(tupleSubset);
//		
//		  sizeGMR = size;
//		  numGMR ++;
//		  numMR ++;
	  }
	  /*System.out.println("\n tupleSubset " + tupleSubset +
			     " query = " + query + "\n");*/
	  
	  // time when the 1st GMR is generated
//	  if (!found) {
//	    endTime = System.currentTimeMillis();
//	    timeFirstGMR = endTime - startTime;
//	  }
//
//	  found = true;
	}
      }
  }
  
  static void gen_rewriting(HashSet tupleSubsets, int size, HashSet covered_relations, HashSet<Argument> covered_head_args, Query query, HashSet rewritings)
  {
      //System.out.println("# of tupleSubsets = " + tupleSubsets.size());

      
      for (Iterator iter = tupleSubsets.iterator(); iter.hasNext();) 
      {
	// for this "i"^th around, we consider only subsets with size i 
	HashSet tupleSubset = (HashSet) iter.next();
	if (tupleSubset.size() != size) 
	  UserLib.myerror("CoreCover.coverQuerySubgoals(), error!");

	if (unionCoverSubgoals(tupleSubset, covered_head_args, covered_relations))  
	{// found one
	  
//	  if(cost < min_cost)
//	  {
//		  min_rewriting = new Rewriting(tupleSubset, query);
//		  min_cost = cost;
//	  }
	  
//	  if(!curr_rewritings.contains(tupleSubset))
	  {
		  rewritings.add(new Rewriting(tupleSubset, query));
		  
//		  curr_rewritings.add(tupleSubset);
//		
//		  sizeGMR = size;
//		  numGMR ++;
//		  numMR ++;
	  }
	  /*System.out.println("\n tupleSubset " + tupleSubset +
			     " query = " + query + "\n");*/
	  
	  // time when the 1st GMR is generated
//	  if (!found) {
//	    endTime = System.currentTimeMillis();
//	    timeFirstGMR = endTime - startTime;
//	  }
//
//	  found = true;
	}
      }
  }
  
  
  static void gen_rewriting(HashSet tupleSubsets, int size, HashSet covered_relations, Query query, HashSet rewritings, HashSet covering_set_size)
  {
      //System.out.println("# of tupleSubsets = " + tupleSubsets.size());

      
      for (Iterator iter = tupleSubsets.iterator(); iter.hasNext();) 
      {
	// for this "i"^th around, we consider only subsets with size i 
	HashSet tupleSubset = (HashSet) iter.next();
	if (tupleSubset.size() != size) 
	  UserLib.myerror("CoreCover.coverQuerySubgoals(), error!");
	
	
	

	if (unionCoverSubgoals(tupleSubset, covered_relations))  
	{// found one
		covering_set_size.add(tupleSubset);
//	  if(cost < min_cost)
//	  {
//		  min_rewriting = new Rewriting(tupleSubset, query);
//		  min_cost = cost;
//	  }
	  
		rewritings.add(new Rewriting(tupleSubset, query));
		
//	  if(!curr_rewritings.contains(tupleSubset))
//	  {
//		  
//		  
//		  curr_rewritings.add(tupleSubset);
//		
//		  sizeGMR = size;
//		  numGMR ++;
//		  numMR ++;
//	  }
	  /*System.out.println("\n tupleSubset " + tupleSubset +
			     " query = " + query + "\n");*/
	  
	  // time when the 1st GMR is generated
//	  if (!found) {
//	    endTime = System.currentTimeMillis();
//	    timeFirstGMR = endTime - startTime;
//	  }
//
//	  found = true;
	}
//	else
//	{
//		non_covering.add(tupleSubset);
//	}
      }
  }
  

  // checks if the union of tuple cores covers all query subgoals
  static boolean unionCoverSubgoals(HashSet tupleSubset, Query query) {
    HashSet coreUnion = new HashSet();
    for (Iterator iter2 = tupleSubset.iterator(); iter2.hasNext();) {
      Tuple viewTuple = (Tuple) iter2.next();
      HashSet core = viewTuple.getCore();
      coreUnion.addAll(core);
    }

    return coreUnion.containsAll(query.getBody());
  }
  
  static boolean unionCoverSubgoals(HashSet tupleSubset, HashSet query) {
	    HashSet coreUnion = new HashSet();
	    for (Iterator iter2 = tupleSubset.iterator(); iter2.hasNext();) {
	      Tuple viewTuple = (Tuple) iter2.next();
	      HashSet core = viewTuple.get_relations();
	      coreUnion.addAll(core);
	    }

	    return coreUnion.containsAll(query);
	  }
  
  static boolean unionCoverSubgoals(HashSet tupleSubset, HashSet<Argument> head_args, HashSet query) {
	    HashSet coreUnion = new HashSet();
	    
	    HashSet<Argument> covered_head_args = new HashSet<Argument>();
	    
	    for (Iterator iter2 = tupleSubset.iterator(); iter2.hasNext();) {
	      Tuple viewTuple = (Tuple) iter2.next();
	      HashSet core = viewTuple.get_relations();
	      coreUnion.addAll(core);
	      
	      HashSet<Argument> curr_covered_head_args = new HashSet<Argument>();
	      
	      curr_covered_head_args.addAll(viewTuple.getArgs());
	      
	      curr_covered_head_args.retainAll(head_args);
	      
	      covered_head_args.addAll(curr_covered_head_args);
	    }

	    boolean b1 = coreUnion.containsAll(query);
	    
	    boolean b2 = covered_head_args.containsAll(head_args);
	    
	    return b1 && b2;
	  }
  
  static HashSet get_CoverSubgoals(HashSet tupleSubset, Query query) {
	    HashSet coreUnion = new HashSet();
	    for (Iterator iter2 = tupleSubset.iterator(); iter2.hasNext();) {
	      Tuple viewTuple = (Tuple) iter2.next();
//	      HashSet core = viewTuple.getCore();
	      HashSet relations = viewTuple.get_relations();
//	      coreUnion.addAll(core);
	      coreUnion.addAll(relations);
	      
	    }

	    return coreUnion;
	  }

  /**
   * Checks if a rewriting has a used a subset of view tuples in tupleSubset.
   */
  static boolean containsTupleSubset(HashSet rewritings, HashSet tupleSubset) {
    for (Iterator iter = rewritings.iterator(); iter.hasNext();) {
      Rewriting rewriting = (Rewriting) iter.next();
      HashSet viewTuples = rewriting.getViewTuples();
      if (tupleSubset.containsAll(viewTuples))
	return true;
    }
    return false;
  }


  /**
   * groups different views into equivalence classes
   */
  static HashSet groupViewTuples(HashSet viewTuples) {
    HashSet groups = new HashSet();

    for (Iterator iter = viewTuples.iterator(); iter.hasNext();) {
      Tuple viewTuple = (Tuple) iter.next();
      
      // search for view tuples with the same utple-core
      boolean found = false;
      for (Iterator iter2 = groups.iterator(); iter2.hasNext();) {
	Tuple group = (Tuple) iter2.next();
	if ((group != viewTuple) && group.sameCore(viewTuple)) {
	  found = true;
	  group.addEquTuple(viewTuple);
	  break;
	}
      }

      if (!found) { // new equivalence class
	viewTuple.setEquSelf();
	groups.add(viewTuple);
      }
    }
    
    return groups;
  }

  // computes the number of view tuples by considering the equivalence
  // view classes: computes the total.
  public static int computeNumVTs(HashSet viewTuples) {
    int num = 0;
    for (Iterator iter = viewTuples.iterator(); iter.hasNext();) {
      Tuple tuple = (Tuple) iter.next();
      num += tuple.getQuery().getCount();  
    }

    return num;
  }
}
