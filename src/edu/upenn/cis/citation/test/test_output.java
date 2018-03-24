package edu.upenn.cis.citation.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.Gen_query;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;

public class test_output {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		Query query = gen_example_query();
		
		System.out.println(query);
		
		Vector<Head_strs> head_vals = new Vector<Head_strs>();
		
		Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();

		String file_name = "tuple_level.xlsx";
		
		//use the tuple level reasoning algorithm to generate the citations, the citations for each tuple will be output to a excel file. The name of the excel file will be specified by the variable file_name. The default file name will be tuple_level.xlsx, which is located in the project directory.
		
//		Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning1.tuple_reasoning(query, citation_strs, head_vals, file_name);
		
		
		
		
	}
	
	
	
	/**
	 * In order to generate a query for a citation, we need to pass 5 arguments to the function gen_query
	 * Arg1: String name : the name of query/view
	 * Arg2: HashMap<String, String> relation_mapping   (relation_name in the body of the query, original_name) pair
	 * Arg3: Vector<String[]> head_vars,  each element in head_var array contains two elements (var_name and relation_name in the body of the query)
	 * Arg4: Vector<String []> condition_str, each element in condition array contains 5 elements (var_name1, relation_name1, operation_name(“=”, “<>”, “>”, “<”, “>=”, “<=”), var_name2, relation_name2. If a condition contains a constant, we put it in var_name2 with an empty string in relation_name2)
	 * Arg5: Vector<String[]> lambda_term_str, each element in lambda_terms array contains 2 elements (var_name and relation name)
	*/
	
	static Query gen_query(String name, HashMap<String, String> relation_mapping, Vector<String []> head_vars, Vector<String []> condition_str, Vector<String []> lambda_term_str) throws ClassNotFoundException, SQLException
	{		
		Query q = Gen_query.gen_query_full(name, relation_mapping, head_vars, condition_str, lambda_term_str);
		
		return q;
	}
	
	/**
	 * the following function will generate an example query:
	 * Q(fid2, name) :- family(fid1, name, type), introduction(fid2, tx), fid1 = fid2, type = 'gpcr'
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	
	
	static Query gen_example_query() throws ClassNotFoundException, SQLException
	{
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
		
		//we put two relations in the body of this query, one is "family", renamed as "family1". The other is "introduction", renamed as "introduction1"
		
		relation_mapping.put("family1", "family");
		
		relation_mapping.put("introduction1", "introduction");
		
		//we put two head variables in this query, one is "name" from relation "family1". The other is from "family_id" from relation "introduction1"
		
		Vector<String []> head_vars = new Vector<String []> ();
		
		String [] head_var1 = {"name", "family1"};
		
		String [] head_var2 = {"family_id", "introduction1"};
		
		head_vars.add(head_var1);
		
		head_vars.add(head_var2);
		
		//we put two conditions in this query, one is the join condition, the other one is to specify the type as "gpcr"
		
		Vector<String []> condition_strs = new Vector<String []>();
		
		
		//This condition means that "family1.family_id = introduction1.family_id"
		String [] condition_s1 = {"family_id", "family1", "=", "family_id", "introduction1"};
		
		
		//This condition means that "family.type = 'gpcr'". Since the second argument of this condition is a constant, the second relation will be specified as empty string
		String [] condition_s2 = {"type", "family1", "=", "'gpcr'", ""};
		
		condition_strs.add(condition_s1);
		
		condition_strs.add(condition_s2);
		
		//We put one lambda string to this query, i.e. family_id which comes from the relation "introduction1"
		Vector<String []> lambda_strs = new Vector<String []>();
		
		String [] lambda_str = {"family_id", "introduction1"};
		
		lambda_strs.add(lambda_str);
		
		return gen_query("q", relation_mapping, head_vars, condition_strs, lambda_strs);
		
	}

}
