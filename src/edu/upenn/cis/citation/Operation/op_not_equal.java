package edu.upenn.cis.citation.Operation;

public class op_not_equal extends Operation{

	public static final String op = "<>";
	
	
	@Override
	public String get_op_name() {
		// TODO Auto-generated method stub
		return op;
	}

	@Override
	public String contains(String atom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return op;
	}

	@Override
	public Operation negation() {
		// TODO Auto-generated method stub
		return new op_equal();
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.op.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Operation operation = (Operation) obj;
		
		if(operation.get_op_name().equals(this.get_op_name()))
			return true;
		
		return false;
	}

	@Override
	public Operation counter_direction() {
		// TODO Auto-generated method stub
		return this;
	}

}
