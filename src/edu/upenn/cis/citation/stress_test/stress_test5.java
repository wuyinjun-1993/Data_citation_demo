package edu.upenn.cis.citation.stress_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2;

public class stress_test5 {
	
	static int times = 5;
	
	static Query gen_query1() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand" + populate_db.separator + "ligand_id", "ligand"));
					
		head_args.add(new Argument("gpcr" + populate_db.separator + "object_id", "gpcr"));				
			
		Subgoal head = new Subgoal("q1", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		Vector<Argument> args3 = view_operation.get_full_schema("interaction", "interaction", c, pst);
		
		subgoals.add(new Subgoal("gpcr", args1));
		
		subgoals.add(new Subgoal("ligand", args2));
			
		subgoals.add(new Subgoal("interaction", args3));
	
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		subgoal_name_mapping.put("interaction", "interaction");
//		
		subgoal_name_mapping.put("gpcr", "gpcr");
		
		Query q = new Query("q1", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query2() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand" + populate_db.separator + "ligand_id", "ligand"));
					
		head_args.add(new Argument("gpcr" + populate_db.separator + "object_id", "gpcr"));				
			
		Subgoal head = new Subgoal("q2", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		Vector<Argument> args3 = view_operation.get_full_schema("interaction", "interaction", c, pst);
		
		subgoals.add(new Subgoal("gpcr", args1));
		
		subgoals.add(new Subgoal("ligand", args2));
			
		subgoals.add(new Subgoal("interaction", args3));
	
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
		
		conditions.add(new Conditions(new Argument("approved", "ligand"), "ligand", new op_equal(), new Argument("'t'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		subgoal_name_mapping.put("interaction", "interaction");
//		
		subgoal_name_mapping.put("gpcr", "gpcr");
		
		Query q = new Query("q2", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query3() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
//		head_args.add(new Argument("ligand_ligand_id", "ligand"));
					
		head_args.add(new Argument("object" + populate_db.separator + "object_id", "object"));				
			
		Subgoal head = new Subgoal("q3", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		Vector<Argument> args3 = view_operation.get_full_schema("interaction", "interaction", c, pst);
		
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("ligand", args2));
			
		subgoals.add(new Subgoal("interaction", args3));
	
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
		
		conditions.add(new Conditions(new Argument("affinity_high", "interaction"), "interaction", new op_greater(), new Argument("'8'"), new String()));
		
		conditions.add(new Conditions(new Argument("type", "ligand"), "ligand", new op_equal(), new Argument("'Peptide'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		subgoal_name_mapping.put("interaction", "interaction");
//		
		subgoal_name_mapping.put("object", "object");
		
		Query q = new Query("q3", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query4() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
//		head_args.add(new Argument("ligand_ligand_id", "ligand"));
					
		head_args.add(new Argument("object" + populate_db.separator + "object_id", "object"));				
			
		Subgoal head = new Subgoal("q3", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		Vector<Argument> args3 = view_operation.get_full_schema("interaction", "interaction", c, pst);
		
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("ligand", args2));
			
		subgoals.add(new Subgoal("interaction", args3));
	
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
		
		conditions.add(new Conditions(new Argument("approved", "ligand"), "ligand", new op_equal(), new Argument("'t'"), new String()));
		
		conditions.add(new Conditions(new Argument("primary_target", "interaction"), "interaction", new op_equal(), new Argument("'t'"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		subgoal_name_mapping.put("interaction", "interaction");
//		
		subgoal_name_mapping.put("object", "object");
		
		Query q = new Query("q3", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query5() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
//		head_args.add(new Argument("ligand_ligand_id", "ligand"));
					
		head_args.add(new Argument("object" + populate_db.separator + "object_id", "object"));				
			
		Subgoal head = new Subgoal("q5", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("pathophysiology", "pathophysiology", c, pst);
				
		Vector<Argument> args3 = view_operation.get_full_schema("disease", "disease", c, pst);
		
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("pathophysiology", args2));
			
		subgoals.add(new Subgoal("disease", args3));
	
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "pathophysiology"), "pathophysiology"));
//		
		conditions.add(new Conditions(new Argument("disease_id", "pathophysiology"), "pathophysiology", new op_equal(), new Argument("disease_id", "disease"), "disease"));
//		
//		conditions.add(new Conditions(new Argument("approved", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//		
//		conditions.add(new Conditions(new Argument("primary_target", "interaction"), "interaction", new op_equal(), new Argument("t"), new String()));
		
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		subgoal_name_mapping.put("pathophysiology", "pathophysiology");
//		
		subgoal_name_mapping.put("disease", "disease");
		
		Query q = new Query("q5", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query8() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
//		head_args.add(new Argument("ligand_ligand_id", "ligand"));
					
		head_args.add(new Argument("object" + populate_db.separator + "object_id", "object"));				
					
		Subgoal head = new Subgoal("q8", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
				
		subgoals.add(new Subgoal("object", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'t'"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		Query q = new Query("q8", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	static Query gen_query9() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand" + populate_db.separator + "ligand_id", "ligand"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("q9", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		subgoals.add(new Subgoal("ligand", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("'t'"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		Query q = new Query("q9", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
		
		
	}
	
	
	static Query gen_view1() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("family_name", "family"));
		
		head_args.add(new Argument("family_family_id", "family"));
		
		head_args.add(new Argument("family_previous_names", "family"));
		
		head_args.add(new Argument("family_last_modified", "family"));
		
		head_args.add(new Argument("family_type", "family"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v1", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
				
		subgoals.add(new Subgoal("family", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("family", "family");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("family_family_id", "family"));
		
		Query q = new Query("v1", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view2() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("introduction_text", "introduction"));
		
		head_args.add(new Argument("introduction_last_modified", "introduction"));

		head_args.add(new Argument("introduction_annotation_status", "introduction"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v2", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("introduction", "introduction", c, pst);
				
		subgoals.add(new Subgoal("introduction", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("introduction", "introduction");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("introduction_family_id", "introduction"));
		
		Query q = new Query("v2", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
//	static Query gen_view3() throws ClassNotFoundException, SQLException
//	{
//		Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager
//           .getConnection(populate_db.db_url,
//       	        populate_db.usr_name,populate_db.passwd);
//		
//        PreparedStatement pst = null;
//        
//		Vector<Argument> head_args = new Vector<Argument>();
//		
//		head_args.add(new Argument("family_id", "family"));
//		
//		head_args.add(new Argument("family_name", "family"));
//
//		head_args.add(new Argument("object_id", "gpcr"));
//					
////		head_args.add(new Argument("object_object_id", "object"));				
//					
//		Subgoal head = new Subgoal("v3", head_args);
//		
//		Vector<Subgoal> subgoals = new Vector<Subgoal>();
//		
//		Vector<Argument> args1 = view_operation.get_full_schema("family", "family", c, pst);
//		
//		Vector<Argument> args2 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
//		
//		Vector<Argument> args3 = view_operation.get_full_schema("receptor2family", "receptor2family", c, pst);
//				
//		subgoals.add(new Subgoal("family", args1));
//		
//		subgoals.add(new Subgoal("gpcr", args2));
//		
//		subgoals.add(new Subgoal("receptor2family", args3));
//
//		Vector<Conditions> conditions = new Vector<Conditions>();
//		
//		conditions.add(new Conditions(new Argument("family_id", "family"), "family", new op_equal(), new Argument("family_id", "receptor2family"), "receptor2family"));
////				
//		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_equal(), new Argument("object_id", "receptor2family"), "receptor2family"));
//		
//		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
//		
//		subgoal_name_mapping.put("family", "family");
//		
//		subgoal_name_mapping.put("gpcr", "gpcr");
//		
//		subgoal_name_mapping.put("receptor2family", "receptor2family");
//		
//		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
//		
////		l_term.add(new Lambda_term("introduction_family_id", "introduction"));
//		
//		Query q = new Query("v3", head, subgoals, l_term, conditions, subgoal_name_mapping);
//		
//		System.out.println(q);
//		
//		c.close();
//		
//		return q;
//	}
//	
	static Query gen_view4() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand_name", "ligand"));
		
		head_args.add(new Argument("ligand_ligand_id", "ligand"));

		head_args.add(new Argument("ligand_type", "ligand"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v4", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		subgoals.add(new Subgoal("ligand", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		Query q = new Query("v4", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view5() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand_name", "ligand"));
		
		head_args.add(new Argument("ligand_ligand_id", "ligand"));

		head_args.add(new Argument("ligand_type", "ligand"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v5", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("ligand", "ligand", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("interaction", "interaction", c, pst);
				
		subgoals.add(new Subgoal("ligand", args1));
		
		subgoals.add(new Subgoal("interaction", args2));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		subgoal_name_mapping.put("interaction", "interaction");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		l_term.add(new Lambda_term("interaction_species_id", "interaction"));
		
		Query q = new Query("v5", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view6() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("ligand_name", "ligand"));
		
		head_args.add(new Argument("ligand_ligand_id", "ligand"));

		head_args.add(new Argument("ligand_type", "ligand"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v6", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		subgoals.add(new Subgoal("ligand", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
//		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		Query q = new Query("v6", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view7() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("object_name", "object"));
		
		head_args.add(new Argument("object_object_id", "object"));

		head_args.add(new Argument("object_last_modified", "object"));
		
		head_args.add(new Argument("object_comments", "object"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v7", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
				
		subgoals.add(new Subgoal("object", args1));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
//		conditions.add(new Conditions(new Argument("in_gtip", "ligand"), "ligand", new op_equal(), new Argument("t"), new String()));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("object_object_id", "object"));
		
		Query q = new Query("v7", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view8() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("interaction_interaction_id", "interaction"));
		
		head_args.add(new Argument("object_object_id", "object"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v8", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("interaction", "interaction", c, pst);
				
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("interaction", args2));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		subgoal_name_mapping.put("interaction", "interaction");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
//		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		l_term.add(new Lambda_term("interaction_interaction_id", "interaction"));
		
		Query q = new Query("v8", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view9() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("interaction_interaction_id", "interaction"));
		
		head_args.add(new Argument("object_object_id", "object"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v9", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("interaction", "interaction", c, pst);
		
		Vector<Argument> args3 = view_operation.get_full_schema("ligand", "ligand", c, pst);
				
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("interaction", args2));
		
		subgoals.add(new Subgoal("ligand", args3));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
		
		conditions.add(new Conditions(new Argument("ligand_id", "ligand"), "ligand", new op_equal(), new Argument("ligand_id", "interaction"), "interaction"));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		subgoal_name_mapping.put("interaction", "interaction");
		
		subgoal_name_mapping.put("ligand", "ligand");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
//		l_term.add(new Lambda_term("ligand_ligand_id", "ligand"));
		
		l_term.add(new Lambda_term("object_object_id", "object"));
		
		Query q = new Query("v9", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
	static Query gen_view10() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
		
        PreparedStatement pst = null;
        
		Vector<Argument> head_args = new Vector<Argument>();
		
		head_args.add(new Argument("interaction_interaction_id", "interaction"));
		
		head_args.add(new Argument("object_object_id", "object"));
					
//		head_args.add(new Argument("object_object_id", "object"));				
					
		Subgoal head = new Subgoal("v8", head_args);
		
		Vector<Subgoal> subgoals = new Vector<Subgoal>();
		
		Vector<Argument> args1 = view_operation.get_full_schema("object", "object", c, pst);
		
		Vector<Argument> args2 = view_operation.get_full_schema("interaction", "interaction", c, pst);
				
		subgoals.add(new Subgoal("object", args1));
		
		subgoals.add(new Subgoal("interaction", args2));

		Vector<Conditions> conditions = new Vector<Conditions>();
		
		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_equal(), new Argument("object_id", "interaction"), "interaction"));
//				
		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
		
		subgoal_name_mapping.put("object", "object");
		
		subgoal_name_mapping.put("interaction", "interaction");
		
		Vector<Lambda_term> l_term = new Vector<Lambda_term>();
		
		l_term.add(new Lambda_term("object_object_id", "object"));
		
//		l_term.add(new Lambda_term("interaction_interaction_id", "interaction"));
		
		Query q = new Query("v10", head, subgoals, l_term, conditions, subgoal_name_mapping);
		
		System.out.println(q);
		
		c.close();
		
		return q;
	}
	
//	static HashSet<Query> gen_store_views() throws ClassNotFoundException, SQLException
//	{
//		HashSet<Query> views = new HashSet<Query>();
//		
//		Query view1 = gen_view1();
//		
////		view_operation.add(view1, view1.name);
//		
//		views.add(view1);
//		
//		Query view2 = gen_view2();
//		
////		view_operation.add(view2, view2.name);
//		
//		views.add(view2);
//		
////		Query view3 = gen_view3();
//		
////		view_operation.add(view3, view3.name);
//		
////		views.add(view3);
//		
//		Query view4 = gen_view4();
//		
////		view_operation.add(view4, view4.name);
//		
//		views.add(view4);
//		
//		Query view5 = gen_view5();
//		
////		view_operation.add(view5, view5.name);
//		
//		views.add(view5);
//		
//		Query view6 = gen_view6();
//		
////		view_operation.add(view6, view6.name);
//		
//		views.add(view6);
//		
//		Query view7 = gen_view7();
//		
////		view_operation.add(view7, view7.name);
//		
//		views.add(view7);
//		
//		Query view8 = gen_view8();
//		
////		view_operation.add(view8, view8.name);
//		
//		views.add(view8);
//		
//		Query view9 = gen_view9();
//		
////		view_operation.add(view9, view9.name);
//		
//		views.add(view9);
//		
//		Query view10 = gen_view10();
//		
////		view_operation.add(view10, view10.name);
//		
//		views.add(view10);
//		
//		return views;
//	}
//	
	static Vector<Query> gen_queries() throws ClassNotFoundException, SQLException
	{
		Vector<Query> queries = new Vector<Query>();
		
		queries.add(gen_query1());
		
		queries.add(gen_query2());
		
		queries.add(gen_query3());
		
		queries.add(gen_query4());
		
		queries.add(gen_query5());
		
		queries.add(gen_query8());
		
		queries.add(gen_query9());
		
		return queries;
	}
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
	    
//	    populate_db.renew_table(c, pst);
//	    
//		view_generator.initial();
//	    
//		view_generator.clear_views(c, pst);
//		
//		view_generator.clear_other_tables(c, pst);
//		
//		HashSet<Query> views = gen_store_views();
//		
//		view_generator.store_views(views, c, pst);
		
		Vector<Query> queries = gen_queries();
		
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();

		HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();
		
		Vector<Vector<citation_view_vector>> citation_view1 = new Vector<Vector<citation_view_vector>>();

		Vector<Vector<citation_view_vector>> citation_view2 = new Vector<Vector<citation_view_vector>>();

		
		
		
		for(int i = 0; i<queries.size(); i++)
		{
			
			double start_time = System.nanoTime();
			
			System.out.println(queries.get(i));

			
			for(int j = 0; j<times; j++)
			{
				
				citation_view_map1.clear();
				
				citation_strs.clear();
				
				citation_view1 = Tuple_reasoning1.tuple_reasoning(queries.get(i), citation_strs,  citation_view_map1, c, pst);

				
			}
			
			double end_time = System.nanoTime();
			
			
			
			
			double time = (end_time - start_time);
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			Set<Head_strs> h_l = citation_strs.keySet();
			
			double citation_size1 = 0;
			
			int row = 0;
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = citation_strs.get(h_value);
				
				citation_size1 += citations.size();
				
				row ++;
				
			}
			
			if(row !=0)
			citation_size1 = citation_size1 / row;
			
			System.out.print(citation_size1 + "	");
			
			Set<Head_strs> head = citation_view_map1.keySet();
			
			int row_num = 0;
			
			double origin_citation_size = 0.0;
			
			for(Iterator iter = head.iterator(); iter.hasNext();)
			{
				Head_strs head_val = (Head_strs) iter.next();
				
				Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
				
				row_num++;
				
				for(int p = 0; p<c_view.size(); p++)
				{
					origin_citation_size += c_view.get(p).size();
				}
				
			}
			
			
			
			if(row_num !=0)
				origin_citation_size = origin_citation_size / row_num;
			
			System.out.print(row_num + "	");
			
			System.out.print(origin_citation_size + "	");
			
			
			
			
			
			
			
			
			start_time = System.nanoTime();
			
			for(int k = 0; k<times; k++)
			{
				citation_view_map2.clear();
				
				citation_strs2.clear();
				
				Tuple_reasoning2.tuple_reasoning(queries.get(i), citation_strs2, citation_view_map2, c, pst);
			}
			
			end_time = System.nanoTime();
			
			time = (end_time - start_time);
			
			
			time = time /(times * 1.0 *1000000000);
			
			System.out.print(time + "s	");
			
			
			h_l = citation_strs2.keySet();
			
			double citation_size2 = 0.0;
			
			row = 0;
			
			for(Iterator iter = h_l.iterator(); iter.hasNext();)
			{
				Head_strs h_value = (Head_strs) iter.next();
				
				HashSet<String> citations = citation_strs2.get(h_value);
				
				citation_size2 += citations.size();
				
				row ++;
				
			}
			
			if(row !=0)
				citation_size2 = citation_size2 / row;

			System.out.print(citation_size2 + "	");

			
			head = citation_view_map2.keySet();
			
			row_num = 0;
			
			origin_citation_size = 0.0;
			
			for(Iterator iter = head.iterator(); iter.hasNext();)
			{
				Head_strs head_val = (Head_strs) iter.next();
				
				Vector<Vector<citation_view_vector>> c_view = citation_view_map2.get(head_val);
				
				row_num++;
				
				for(int p = 0; p<c_view.size(); p++)
				{
					origin_citation_size += c_view.get(p).size();
				}
				
			}
			
			
			
			if(row_num !=0)
				origin_citation_size = origin_citation_size / row_num;
			
			System.out.print(row_num + "	");
			
			System.out.print(origin_citation_size + "	");
			
			
			Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
			
//			Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
			
			System.out.println();
			
			

		}
		
		c.close();

	}
	
//	

}
