package edu.upenn.cis.citation.Operation;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.reasoning1.schema_reasoning;

public class Conditions {
	
	
	public String subgoal1;
		
	public Argument arg1;
	
	public Argument arg2;
		
	public String subgoal2 = new String();
	
	public Operation op;
	
	public String citation_view;
	
	public String expression;
	
	public boolean get_mapping1 = true;
	
	public boolean get_mapping2 = true;
	
	public int id1 = -1;
	
	public int id2 = -1;
	
	static final String[] ops = {"<>",">=","<=",">","<","="};
	
	public static void main(String [] args)
	{
		Conditions condition1 = new Conditions(new Argument("family_id", "family"), "family" , new op_greater(), new Argument("family_id", "introduction"), "introduction");
		
		Conditions condition2 = new Conditions(new Argument("family_id", "introduction"), "introduction" , new op_less(), new Argument("family_id", "family"), "family");
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(condition1);
		
		System.out.println(conditions.contains(condition2));
	}
	
	@Override
	public String toString()
	{
		
		String str = arg1.name;
		
		if(arg2.isConst())
			return subgoal1 + populate_db.separator + str + op + arg2;
		else
			return subgoal1 + populate_db.separator + str + op + subgoal2 + populate_db.separator + arg2;
	}
	
	public String toStringinsql()
	{
		if(arg2.isConst())
		{
			String str = arg2.name.replaceAll("'", "''");
			
			
			return subgoal1 + populate_db.separator + arg1.name + op + str;
		}
		else
			return subgoal1 + populate_db.separator + arg1.name + op + subgoal2 + populate_db.separator + arg2;
	}
	
	public Conditions(Argument arg1, String subgoal1, Operation op, Argument arg2, String subgoal2)
	{
		this.arg1 = arg1;
		
		this.op = op;
		
		this.arg2 = arg2;
		
		this.subgoal1 = subgoal1;
		
		this.subgoal2 = subgoal2;
	}
	
	public static Conditions parse(String constraint, Vector<Subgoal> subgoals, HashMap<String, String> origin_names)
	{
		int i = 0;
		
		for(i = 0; i< ops.length; i++)
		{
			if(constraint.contains(ops[i]))
				break;
		}
		
		String [] items = constraint.split(ops[i]);
		
		Argument arg1 = new Argument(items[0].trim(), origin_names.get(items[0].trim()));
		
		Argument arg2 = new Argument(items[1].trim(), origin_names.get(items[1].trim()));
		
		String sg1 = new String();
		
		String sg2 = new String();
		
		for(int j = 0; j<subgoals.size(); j++)
		{
			if(subgoals.get(j).args.contains(arg1))
			{
				sg1 = subgoals.get(j).name;
				break;
			}
		}
		
		if(arg2.type != 1)
		for(int j = 0; j<subgoals.size(); j++)
		{	
			if(subgoals.get(j).args.contains(arg2))
			{
				sg2 = subgoals.get(j).name;
				break;
			}
			
		}
		
		
		Operation op;
		
		switch(i)
		{
		case 0: op = new op_not_equal(); break;
		case 1: op = new op_greater_equal(); break;
		case 2: op = new op_less_equal(); break;
		case 3: op = new op_greater(); break;
		case 4: op = new op_less(); break;
		case 5: op = new op_equal(); break;
		default: op = null; break;
		}
		
		return new Conditions(arg1, sg1, op, arg2, sg2);
		
	}
	
	public static Conditions negation(Conditions conditions)
	{
		
		Conditions condition = new Conditions(conditions.arg1, conditions.subgoal1, conditions.op.negation(), conditions.arg2, conditions.subgoal2);
				
		return conditions;
	}
	
	
	public void negation()
	{
		op = op.negation();
	}
	
	public static boolean compare(Conditions c1, Conditions c2)
	{
//		if((c1.arg1.origin_name.equals(c2.arg1.origin_name) && c1.arg2.origin_name.equals(c2.arg2.origin_name) && c1.subgoal1.equals(c2.subgoal1) && c1.subgoal2.equals(c2.subgoal2) ))
		
		if(c1.toString().equals(c2.toString()))
			 return true;
		
		if(c1.toString().equals(reverse_condition(c2).toString()))
			return true;
		
		if(c1.arg2.isConst() && c2.arg2.isConst() && (c1.subgoal1.toString() + populate_db.separator + c1.arg1.name).equals(c2.subgoal1.toString() + populate_db.separator + c2.arg1.name))
		{
			try{
				double c1_arg2 = Double.valueOf(c1.arg2.name);
				
				double c2_arg2 = Double.valueOf(c2.arg2.name);
				
				if(c1.op.equals(c2.op))
				{
					if((c1.op.toString().equals(">") || c1.op.toString().equals(">=")) && (c1_arg2 >= c2_arg2))
						return true;
					
					if((c1.op.toString().equals("<") || c1.op.toString().equals("<=")) && (c1_arg2 <= c2_arg2))
						return true;
				}
				
				if(c1.op.toString().equals(">") && c2.op.toString().equals(">=") && c1_arg2 >= c2_arg2)
					return true;
				
				if(c1.op.toString().equals("<") && c2.op.toString().equals("<=") && c1_arg2 <= c2_arg2)
					return true;
				
				if(c1.op.toString().equals(">=") && c2.op.toString().equals(">") && c1_arg2 > c2_arg2)
					return true;
				
				if(c1.op.toString().equals("<=") && c2.op.toString().equals("<") && c1_arg2 < c2_arg2)
					return true;
				
				
			}
			catch(Exception e)
			{
				String c1_arg2 = c1.arg2.name;
				
				String c2_arg2 = c2.arg2.name;
				
				if(c1.op.equals(c2.op))
				{
					if((c1.op.toString().equals(">") || c1.op.toString().equals(">=")) && (c1_arg2.compareTo(c2_arg2)) >= 0)
						return true;
					
					if((c1.op.toString().equals("<") || c1.op.toString().equals("<=")) && (c1_arg2.compareTo(c2_arg2)) <= 0)
						return true;
				}
				
				if(c1.op.toString().equals(">") && c2.op.toString().equals(">=") && (c1_arg2.compareTo(c2_arg2)) >= 0)
					return true;
				
				if(c1.op.toString().equals("<") && c2.op.toString().equals("<=") && (c1_arg2.compareTo(c2_arg2)) <= 0)
					return true;
				
				if(c1.op.toString().equals(">=") && c2.op.toString().equals(">") && (c1_arg2.compareTo(c2_arg2)) > 0)
					return true;
				
				if(c1.op.toString().equals("<=") && c2.op.toString().equals("<") && (c1_arg2.compareTo(c2_arg2)) < 0)
					return true;
			}
			
			
		}
		
		
		return false;
		
//		if((c1.arg2.origin_name.equals(c2.arg1.origin_name) && c1.arg1.origin_name.equals(c2.arg2.origin_name) && c1.subgoal2.equals(c2.subgoal1) && c1.subgoal1.equals(c2.subgoal2) ))
    }
	
	static Conditions reverse_condition(Conditions c)
	{
		Operation op = c.op.negation();
		
		return new Conditions(c.arg2, c.subgoal2, op, c.arg1, c.subgoal1);
	}

	
	public static Conditions parse(String condition_str)
	{
		int i = 0;
		
		for(i = 0; i< ops.length; i++)
		{
			if(condition_str.contains(ops[i]))
				break;
		}
		
		
		Operation op;
		
		switch(i)
		{
		case 0: op = new op_not_equal(); break;
		case 1: op = new op_greater_equal(); break;
		case 2: op = new op_less_equal(); break;
		case 3: op = new op_greater(); break;
		case 4: op = new op_less(); break;
		case 5: op = new op_equal(); break;
		default: op = null; break;
		}
		
		String [] args = condition_str.split(op.toString());
		
		String arg1_str = args[0].trim();
		
		String []strs = arg1_str.split("_");
		
		String t1 = strs[0] + "_" + strs[1];
		
		String arg1 = arg1_str;//.substring(t1.length() + 1, arg1_str.length());
		
		if(!args[1].contains("'"))
		{
			String arg2_str = args[1].trim();
			
			strs = arg2_str.split("_");
			
			String t2 = strs[0] + "_" + strs[1];
			
			String arg2 = arg2_str;//.substring(t2.length() + 1, arg2_str.length());
			
			return new Conditions(new Argument(arg1, arg1), t1, op, new Argument(arg2, arg2), t2);

		}
		
		else
		{
			return new Conditions(new Argument(arg1, arg1), t1, op, new Argument(args[1], arg1), new String());
		}
		
		
		
		
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		
		Conditions condition = (Conditions) obj;
		
//		if(this.arg1.name.equals(condition.arg1.name) && this.arg1.relation_name.equals(condition.arg1.relation_name) 
//				&& this.arg2.name.equals(condition.arg2.name) && this.arg2.relation_name.equals(condition.arg2.relation_name) 
//				&& this.subgoal1.equals(condition.subgoal1) && this.subgoal2.equals(condition.subgoal2) && this.op.get_op_name().equals(condition.op.get_op_name()))
//			return true;
		
		if(condition.hashCode() == this.hashCode())
		  return true;
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
	  if(this.subgoal2 ==null || this.subgoal2.isEmpty())
	  {
	    String string = this.subgoal1 + populate_db.separator + this.arg1.name + this.op + this.arg2.name + populate_db.separator + this.get_mapping1 + populate_db.separator + this.get_mapping2;
	    
	    return string.hashCode();
	  }
	  else
	  {
	    if(this.subgoal1.compareTo(this.subgoal2) > 0 || ((this.subgoal1.compareTo(this.subgoal2) == 0) && this.arg1.name.compareTo(this.arg2.name) > 0))
	    {
	      String string = this.subgoal1 + populate_db.separator + this.arg1.name + this.op + this.subgoal2 + populate_db.separator + this.arg2.name + populate_db.separator + this.get_mapping1 + populate_db.separator + this.get_mapping2;
	        
	      return string.hashCode();
	    }
	    else
	    {
	          String string = this.subgoal2 + populate_db.separator + this.arg2.name + this.op.counter_direction() + this.subgoal1 + populate_db.separator + this.arg1.name + populate_db.separator + this.get_mapping2 + populate_db.separator + this.get_mapping1;
	            
	          return string.hashCode();
	    }
	  }
	  
//		return this.arg1.hashCode() * 10000 + this.subgoal1.hashCode()*1000 + this.op.hashCode()*100 + this.arg2.hashCode()*10 + this.subgoal2.hashCode();
	}
	
	
	public static boolean check_predicate_match(Conditions condition1, Conditions condition2)
	{
		if(condition1.arg2.isConst())
		{
			if(condition2.arg2.isConst() && condition1.subgoal1.equals(condition2.subgoal1) && condition1.arg1.equals(condition2.arg1))
			{
				return true;
			}
		}
		else
		{
			if(!condition2.arg2.isConst() && condition1.subgoal1.equals(condition2.subgoal1) && condition1.subgoal2.equals(condition2.subgoal2) && condition1.arg1.equals(condition2.arg1) && condition1.arg2.equals(condition2.arg2))
			{
				return true;
			}
			else
			{
				if(!condition2.arg2.isConst() && condition1.subgoal1.equals(condition2.subgoal2) && condition1.subgoal2.equals(condition2.subgoal1) && condition1.arg1.equals(condition2.arg2) && condition1.arg2.equals(condition2.arg1))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	static boolean check_smaller_greater_values(Operation op1, Operation op2)
	{
		if(op1.toString().equals(">") || op1.toString().equals(">="))
		{
			if(op2.toString().equals(">") || op2.toString().equals(">=") || op2.toString().equals("="))
			{
				return true;
			}
		}
		
		return false;
	}
	
	static boolean check_greater_smaller_values(Operation op1, Operation op2)
	{
		if(op1.toString().equals("<") || op1.toString().equals("<="))
		{
			if(op2.toString().equals("<") || op2.toString().equals("<=") || op2.toString().equals("="))
			{
				return true;
			}
		}
		
		return false;
	}
	
	//condition1: view condition;;condition2:query condition
	public static boolean check_predicates_satisfy(Conditions condition1, Conditions condition2, Query query, HashMap<Head_strs, String> attr_type_mapping)
	{
		if(condition1.arg2.isConst())
		{
			String string1 = condition1.arg2.name;
			
			String string2 = condition2.arg2.name;
			
			String relation = condition1.subgoal1;
			
			Operation op1 = condition1.op;
			
			Operation op2 = condition2.op;
			
			String arg_name = condition1.arg1.name.substring(condition1.arg1.name.indexOf(populate_db.separator) + 1, condition1.arg1.name.length());
			
			String mapped_relation = query.subgoal_name_mapping.get(relation);
			
			Vector<String> vec_str = new Vector<String>();
			
			vec_str.add(mapped_relation);
			
			vec_str.add(arg_name);
			
			Head_strs head = new Head_strs(vec_str);
			
			String type = attr_type_mapping.get(head);
			
			if(schema_reasoning.numberic_type.contains(type))
			{
				double value1 = Double.valueOf(string1);
				
				double value2 = Double.valueOf(string1);
				
				if(value1 < value2)
				{
					return check_smaller_greater_values(op1, op2);
				}
				else
				{
					if(value1 > value2)
					{
						return check_greater_smaller_values(op1, op2);
					}
					else
					{
						if(op1.equals(op2))
							return true;
						else
							return false;
					}
				}
			}
			else
			{
				if(string1.compareTo(string2) < 0)
				{
					
					return check_smaller_greater_values(op1, op2);
					
				}
				else
				{
					if(string1.compareTo(string2) > 0)
					{
						return check_greater_smaller_values(op1, op2);
					}
					else
					{
						if(op1.equals(op2))
							return true;
						else
							return false;
					}
				}
			}
		}
		else
		{
			Operation op1 = condition1.op;
			
			Operation op2 = condition2.op;
			
			if(op1.equals(op2))
				return true;
			else
			{
				if(op1.toString().equals(">="))
				{
					if(op2.toString().equals(">") || op2.toString().equals("="))
						return true;
				}
				else
				{
					if(op1.toString().equals("<="))
					{
						if(op2.toString().equals("<") || op2.toString().equals("="))
							return true;
					}
				}
			}
			
			return false;
		}
	}
	
}
