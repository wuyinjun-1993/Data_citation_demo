package edu.upenn.cis.citation.Corecover;
/* 
 * Argument.java
 * -------------
 * $Id: Argument.java,v 1.10 2000/11/17 01:35:37 chenli Exp $
 */

import java.util.*;

public class Argument {

  final public static int VAR   = 0;
  final public static int CONST = 1;
  
  public int    type;  // VAR or CONST
  public String name;
  
  public String data_type = new String();
  
  public String value = null;
  
  public String origin_name = null;
  
  public String relation_name = null; //not the original name
  
//  public String origin_relation_name = null;
  
  public static void main(String [] args)
  {
    Argument arg1 = new Argument("family|family_id", "family");
       
    Argument arg2 = new Argument("family|family_id", "family");
    
    arg1.set_value("1");
    
    System.out.println(arg1.equals(arg2));
    
    System.out.println(arg2.value);
    
  }
  
  Argument(String name, String relation_name, boolean isConstant) {
    if (name == null) {
      UserLib.myerror("Argument:constructor, name == null");
    }
    // string starting with an upper-case char is a variable
    //if (Character.isUpperCase(name.charAt(0)))
    if(name.startsWith("'") && name.endsWith("'"))
      type = CONST;
    else
      type = VAR;

    if(isConstant)
        type = CONST;

    this.name = name;
    
    this.relation_name = relation_name;
  }
  
  public Argument(String name, String relation_name) {
      this(name, relation_name, false);
  }
  
  public Argument(String name)
  {
	  this(name, new String(), true);
  }
  
  public Argument(String name, String relation_name, String lambda_term) {
      this(name, relation_name);
      
      this.origin_name = lambda_term;
  }

  public void set_value(String value)
  {
    this.value = value;
  }
  
  public void set_data_type(String data_type)
  {
    this.data_type = data_type;
  }
  
  /**
   * Given a partial mapping phi, an HH, and a set of distinguished
   * variables, checks whether this argument can be 
   * unified with a given argument by expanding the phihh.  If so, phi and
   * hh are extended correspondingly.
   */
  public boolean unifiable(Argument arg, Mapping phi, Mapping hh,
			   Vector distVars) {

    // we cannot map a constant to a different constant 
    if (this.isConst() && arg.isConst() && !this.equals(arg))
      return false;

    // two vars, checks the target of this arg in phi
    Argument image = phi.apply(this);
    if (image == null) { // not mapped.  
      phi.put(this, arg); // adds the pair
      return true;  
    }

    // this arg has been mapped.  checks if it has been mapped
    // to the given arg
    if (image.equals(arg)) return true;

    // this arg was already mapped to a different arg. let's see if we can
    // expand the hh to create this mapping

    // first, the two args must both be distinguished
    if ((!distVars.contains(image)) || (!distVars.contains(arg)))
      return false;

    // We don't allow constants to appear in the head
    if (image.isConst() || arg.isConst()) {
      System.out.println("Argument.unifiable().  We don't allow " +
			 "constants to be in a view's head.");
      System.exit(1);
    }

    // equates these two args if the new arg has not been equated
    if (hh.apply(arg) == null) hh.put(arg, image);

    return true;
  }

  /**
   * renames this argument to generate a new argument
   */
  public Argument rename(int newId, Map argMap) {
    if (argMap.containsKey(this))
      return ((Argument) argMap.get(this));
    
    return new Argument(relation_name, name + "_" + newId); // a new name
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public boolean isConst() {
    return type == CONST;
  }

  /**
   * Indicates whether some other argument is "equal to" this one.
   * Notice that the parameter must be an "Object" class in order to
   * override Object.equals(), not an "Argument" class.
   *
   * @param argument The reference object with which to compare.
   * @return <code>true(false)</code> if this item is the same(not same) as 
   * the argument.
   */
  public boolean equals(Object argument){
    Argument arg = (Argument) argument;
    return this.getName().equalsIgnoreCase(arg.getName()) 
      && this.getType() == arg.getType();
  }
  
  /**
   * Overrides the hash function Object.hasCode().  As an item in a
   * HashSet collection, called when HashSet.equals() is called.
   */
  public int hashCode() {
    return toString().hashCode();
  }

  public String toString() {
    return getName();
  }
}
