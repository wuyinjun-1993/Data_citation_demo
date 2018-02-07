package edu.upenn.cis.citation.Corecover;
/* 
 * Tuple.java
 * -------------
 * $Id: Tuple.java,v 1.8 2000/11/23 22:22:44 chenli Exp $
 */

import java.util.*;

import edu.upenn.cis.citation.Operation.Conditions;

public class Tuple {
  public String name = null;
  public Vector args = null;  // a list of arguments
  public Mapping phi = null;  // mapping from query args to the database deriving
		       // the tuple
  
  public Mapping phi_str = null;
  public Subgoal subgoal = null; // the subgoal that produces this tuple in canDb.
  public HashMap mapSubgoals = null;
  
  public HashMap mapSubgoals_str = null;
  public Query   query = null;
  public HashSet core = new HashSet();
    
  public Vector<Conditions> conditions = new Vector<Conditions>();

  HashSet equTuples = new HashSet(); // set of view tuples with the same tuple-core
  
  public Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
  
  double cost = 1;
  
  public Vector<HashSet<Integer>> cluster_subgoal_ids = new Vector<HashSet<Integer>>();
  
  public Vector<HashSet<Integer>> cluster_patial_mapping_condition_ids = new Vector<HashSet<Integer>>();

  public Vector<HashSet<Integer>> cluster_non_mapping_condition_ids = new Vector<HashSet<Integer>>();


  public double get_cost()
  {
	  return cost;
  }
  
  public void cal_cost()
  {
	  if(!lambda_terms.isEmpty())
	  {
		  
		  Vector temp = lambda_terms;
		  
		  for(int i=0;i<query.head.args.size();i++)
		  {
			  Argument arg = (Argument) query.head.args.get(i);
			  			  
			  if(arg.type == 1 && lambda_terms.contains(arg))
			  {
				  temp.remove(query.head.args.get(i));
			  }
		  }
		  
		  if(!temp.isEmpty())
			  cost=10;
	  }	  
  }
  
  public void set_cost(double cost)
  {
	  this.cost = cost;
  }
  
  
  Tuple(Subgoal subgoal) {
    this.subgoal = subgoal;
    this.name = subgoal.getName();
    this.args = (Vector) subgoal.getArgs().clone();
  }

  Tuple(String name, Vector args, Mapping phi, HashMap mapSubgoals) {
    this.name = name;
    this.args = args;
    this.phi  = phi;
    this.mapSubgoals = mapSubgoals;
    this.mapSubgoals_str = new HashMap<>();
    set_map_subgoal_str(mapSubgoals);
  }

  Tuple(String name, Vector args, Mapping phi, Mapping phi_str, HashMap mapSubgoals) {
	    this.name = name;
	    this.args = args;
	    this.phi  = phi;
	    this.phi_str = phi_str;
	    this.mapSubgoals = mapSubgoals;
	    this.mapSubgoals_str = new HashMap<>();
	    set_map_subgoal_str(mapSubgoals);
	  }
  
  void set_map_subgoal_str(HashMap mapSubgoals)
  {
	  Set<Subgoal> subgoals = mapSubgoals.keySet();
	  
	  for(Iterator iter = subgoals.iterator(); iter.hasNext();)
	  {
		  Subgoal subgoal = (Subgoal) iter.next();
		  
		  Subgoal mapped_subgoal = (Subgoal) mapSubgoals.get(subgoal);
		  
		  mapSubgoals_str.put(subgoal.name, mapped_subgoal.name);
	  }
  }
  
  public String getName() {
    return name;
  }

  public Vector getArgs() {
    return args;
  }

  public Mapping getMapping() {
    return phi;
  }
  
  public Mapping getMapping_str() {
	    return phi_str;
	  }

  public Query getQuery() {
    return query;
  }

  public Subgoal getSubgoal() {
    return subgoal;
  }

  public HashMap getMapSubgoals() {
    return mapSubgoals;
  }

  // set the query of the tuple, and compute the head of each tuple
  public void setQuery(Query query) {
    this.query = query;
    this.name = query.getName();

    // compute the new head under the phi
    
//    Set<Subgoal> subgoal_s = mapSubgoals.keySet();
//    
//    for(int i = 0; i<args.size(); i++)
//    {
//    	Argument curr_arg = (Argument) args.get(i);
//    	
//    	args.set(i, phi.apply(curr_arg));
//    }
    
//    args.clear();
//    
//    for(Iterator iter = subgoal_s.iterator(); iter.hasNext();)
//    {
//    	Subgoal key_subgoal = (Subgoal) iter.next();
//    	
//    	Subgoal map_subgoal = (Subgoal) mapSubgoals.get(key_subgoal);
//    	
//    	args.addAll(map_subgoal.getArgs());
//    }
    
//    Subgoal head = query.getHead();
////    if (head.size() != args.size())
////      UserLib.myerror("Tuple.setQuery(), wrong args!");
//    Subgoal newHead = phi.apply(args);
//    args = newHead.getArgs();  // set the new args
  }

  public void setCore(HashSet core) {
    this.core = core;
  }

  public HashSet getCore() {
    return core;
  }
  public HashSet get_relations()
  {
	  
	  HashSet relations = new HashSet ();
	  
	  HashSet subgoals = getTargetSubgoals();
	  
	  for(Iterator iter = subgoals.iterator(); iter.hasNext();)
	  {
		  Subgoal subgoal = (Subgoal)iter.next();
		  
		  relations.add(subgoal.name);
	  }
	  
	  return relations;
  }

  /**
   * gets the target query subgoals under the mapping 
   */
  public HashSet getTargetSubgoals() {
    HashSet result = new HashSet();

    Set entrySet = mapSubgoals.entrySet();
    for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
      Map.Entry mapEntry = (Map.Entry) iter.next();
      Subgoal querySubgoal = (Subgoal) mapEntry.getValue();
      result.add(querySubgoal);
    }

    return result;
  }

  /**
   * Indicates whether some other tuple is "equal to" this one.
   * Notice that the parameter must be an "Object" class in order to
   * override Object.equals(), not an "Tuple" class.
   *
   * @param argument The reference object with which to compare.
   * @return <code>true(false)</code> if this item is the same(not same) as 
   * the argument.
   */
//  public boolean equals(Object tuple){
//    Tuple t = (Tuple) tuple;
//
//    // same name
//    if (!this.getName().equals(t.getName())) return false;
//
//    Vector tArgs = t.getArgs();
//    if (args.size() != tArgs.size()) return false;
//
//    for (int i = 0; i < args.size(); i ++) {
//	Argument arg  = (Argument) args.elementAt(i);
//	Argument tArg = (Argument) tArgs.elementAt(i);
//	if (!arg.equals(tArg)) return false;
//    }
//    
//    String phi_str1 = phi.toString();
//    
//    String phi_str2 = t.phi.toString();
//    
//    if(!phi_str1.equals(phi_str2))
//    	return false;
//
//    return true;
//  }

  /**
   *
   */
  public boolean sameCore(Tuple viewTuple) {
    return this.getCore().equals(viewTuple.getCore());
  }

  /**
   * adds itself to the equivalence class
   */
  public void setEquSelf() {
    equTuples.add(this); // add itself
  }

  /**
   * adds a view tuple to the equivalence class
   */
  public void addEquTuple(Tuple viewTuple) {
    equTuples.add(viewTuple);
  }
  
  /**
   * Overrides the hash function Object.hasCode().  As an item in a
   * HashSet collection, called when HashSet.equals() is called.
   */
  @Override
  public int hashCode() {
	  
	  
	  if(this.mapSubgoals_str == null)
	  {
		  String hash_string = this.getName();
		  
//	    int hashCode = this.getName().hashCode();
//	    for (int i = 0; i < args.size(); i ++) {
//	      Argument arg  = (Argument) args.elementAt(i);
//	      hashCode += arg.hashCode();
//	    }
	    
	    return hash_string.hashCode();
	  }
	  
	  String hash_string = this.getName() + this.mapSubgoals_str.toString();
	  
	  

    
    return hash_string.hashCode();
  }
  
  @Override
  public boolean equals(Object obj)
  {
	  Tuple tuple = (Tuple) obj;
	  
	  return tuple.hashCode() == this.hashCode();
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    if (name != null)
      result.append(name);
    result.append(args.toString());

    /*if (phi != null)
      result.append("; " + phi.toString());*/
    return (result.toString());
  }
  
  @Override
  public Object clone()
  {
	  
	  String name = this.name;
	  
	  Vector<Argument> args = (Vector<Argument>) this.args.clone();
	    Mapping phi  = (Mapping) this.phi.clone();
	    Mapping phi_str = new Mapping();
	    
	    phi_str.map.putAll(this.phi_str.map);
	    
	    HashMap mapSubgoals = (HashMap) this.mapSubgoals.clone();
	    HashMap mapSubgoals_str = (HashMap) this.mapSubgoals_str.clone();
	  
	    
	    
	  Tuple tuple = new Tuple(name, args, phi, phi_str, mapSubgoals);
	  
	  tuple.lambda_terms.addAll(this.lambda_terms);
	  
	  tuple.conditions.addAll(this.conditions);
	  
	  return tuple;
	  
  }
}
