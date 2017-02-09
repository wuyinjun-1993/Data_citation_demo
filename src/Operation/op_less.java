package Operation;

public class op_less extends Operation{

	public static final String op = "<";
	
	
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
		return new op_greater_equal();
	}

}
