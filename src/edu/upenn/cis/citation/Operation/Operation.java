package edu.upenn.cis.citation.Operation;

public abstract class Operation {
	public abstract String get_op_name();
	
	public abstract String contains(String atom);
	
	@Override
	public abstract String toString();
	

	public abstract Operation negation();
	
	public abstract Operation counter_direction();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
//	public static final String op_equal = "=";
//	
//	public static final String op_greater = ">";
//	
//	public static final String op_less = "<";
//	
//	public static final String op_greater_equal = ">=";
//	
//	public static final String op_less_equal = "<=";
//	
//	public static final String op_not_equal = "<>";
	
	

}
