package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.locks.Condition;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2;

public class view_operation {
	
	public static void main(String [] args) throws ClassNotFoundException, SQLException
	{
//		delete_view("v9");
		
		String query = "v9(ligand_c_ligand_id,interaction_c_object_id, ligand_c_name):ligand_c(), interaction_c(), interaction_c_ligand_id=ligand_c_ligand_id";
		
		query = Tuple_reasoning2.get_full_query(query);
		
		Query view = Parse_datalog.parse_query(query);
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		Lambda_term l_term = new Lambda_term("ligand_c_ligand_id", "ligand_c");
		
		lambda_terms.add(l_term);
		
		view.lambda_term = lambda_terms;
		
		add(view, view.name);
		
//		initial();
		
	}
	
	public static void initial() throws ClassNotFoundException, SQLException
	{
		
//		create_view.check_create_views();
		
		populate_db.initial();
	}
	
	public static void add(Query v, String name) throws ClassNotFoundException, SQLException
	{
		String id = insert_view(v, name);
		
		Vector<String> subgoal_names = new Vector<String>();
		
		Vector<Subgoal> subgoals = v.body;
		
		for(int i = 0; i<subgoals.size(); i++)
		{
			subgoal_names.add(subgoals.get(i).name);
		}
		
//		create_view.create_views(id, subgoal_names, v.conditions, null, null);
		
		populate_db.update(v);

	}
	
	public static Query get_view(String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        Vector<Argument> head_var = new Vector<Argument>();
        
        String id = get_id_head_vars(name, head_var, c, pst);
        
        Vector<Conditions> conditions = get_view_conditions(id, c, pst);
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, c, pst);
        
        Vector<Lambda_term> lambda_terms = get_view_lambda_terms(id, c, pst);
        
        Subgoal head = new Subgoal(id, head_var);
        
        Query view = new Query(id, head, subgoals,lambda_terms, conditions);
        
        
        c.close();
                
        return view;
	}
	
	static String get_view_id(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view from view_table where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		String id = new String();
				
		if(rs.next())
		{
			id = rs.getString(1);
		}
		
		return id;
	}
	
	public static void delete_view(String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
//        String id = get_view_id(name, c, pst);
        
        String id = name;
        
        boolean has_lambda = delete_lambda_terms(id, c, pst);
        
        Vector<Subgoal> subgoals = get_view_subgoals(id, c, pst);
                
        delete_subgoals(id, c, pst);
        
        delete_conditions(id, c, pst);
        
        delete_citation_view(id, c, pst);
        
        delete_view_table(id, c, pst);
        
        c.close();
        
        populate_db.delete(id, subgoals, has_lambda);

	}
	
	static void delete_citation_view(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2view where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	
	
	static boolean delete_lambda_terms(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		
		
		String query = "select count(*) from view2lambda_term where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		boolean has_lambda_term = false;
		
		if(rs.next())
		{
			int num = rs.getInt(1);
			
			if(num > 0)
				has_lambda_term = true;
		}
		
		query = "delete from view2lambda_term where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		return has_lambda_term;
		
	}
	
	static void delete_conditions(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2conditions where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_subgoals(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view2subgoals where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_view_table(String id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from view_table where view = '" + id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static Vector<Conditions> get_view_conditions(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_conditions = "select conditions from view2conditions where view = '" + name + "'";
		
		pst = c.prepareStatement(q_conditions);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Conditions> conditions = new Vector<Conditions>();
		
		while(r.next())
		{
			
			String condition_str = r.getString(1);
			
			String []strs = null;
			
			Operation op = null;
			
			
			if(condition_str.contains(op_equal.op))
			{
				strs = condition_str.split(op_equal.op);
				
				op = new op_equal();
			}
			else
			{
				if(condition_str.contains(op_less.op))
				{
					strs = condition_str.split(op_less.op);
					
					op = new op_less();
				}
				else
				{
					if(condition_str.contains(op_greater.op))
					{
						strs = condition_str.split(op_greater.op);
						
						op = new op_greater();
					}
					else
					{
						if(condition_str.contains(op_less_equal.op))
						{
							strs = condition_str.split(op_less_equal.op);
							
							op = new op_less_equal();
						}
						else
						{
							if(condition_str.contains(op_greater_equal.op))
							{
								strs = condition_str.split(op_greater_equal.op);
								
								op = new op_greater_equal();
							}
							else
							{
								if(condition_str.contains(op_not_equal.op))
								{
									strs = condition_str.split(op_not_equal.op);
									
									op = new op_not_equal();
								}
							}
						}
					}
				}
				
			}
			
			
			String str1 = strs[0];
			
			String str2 = strs[1];
			

			String []strs1 = str1.split("_");
			
			String subgoal1 = strs1[0] + "_" + strs1[1];
			
			String arg1 = str1.trim();//.substring(subgoal1.length() + 1, str1.length());
			
			String subgoal2 = new String();
			
			String arg2 = new String ();
			
			if(str2.contains("'"))
			{
				arg2 = str2;
			}
			else
			{
				String []strs2 = str2.split("_");
				
				subgoal2 = strs2[0] + "_" + strs2[1];
				
				arg2 = str2.trim();//.substring(subgoal2.length() + 1, str2.length());
			}
				
			Conditions condition = new Conditions(new Argument(arg1), subgoal1, op, new Argument(arg2), subgoal2);
			
			conditions.add(condition);
		}
		return conditions;
		
	}
	
	static String get_id_head_vars(String name, Vector<Argument> head_var, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select view, head_variables from view_table where name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		String id = new String();
		
		String head_var_str = new String();
		
		if(rs.next())
		{
			id = rs.getString(1).trim();
			
			head_var_str = rs.getString(2).trim();
		}
		
		String [] head_var_strs = head_var_str.split(",");
		
		for(int i = 0; i<head_var_strs.length; i++)
		{
			Argument arg = new Argument(head_var_strs[i].trim());
			
			head_var.add(arg);
		}
		
		return id;
			
	}
	
	static Vector<Subgoal> get_view_subgoals(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String q_subgoals = "select subgoal_names from view2subgoals where view = '" + name + "'";
		
		pst = c.prepareStatement(q_subgoals);
		
		ResultSet r = pst.executeQuery();
		
		Vector<Subgoal> subgoal_names = new Vector<Subgoal>();
		
		while(r.next())
		{
			String subgoal_name = r.getString(1);
			
			Vector<Argument> args = new Vector<Argument>();
			
			Subgoal subgoal = new Subgoal(subgoal_name, args);
			
			subgoal_names.add(subgoal);
		}
		
		return subgoal_names;
	}
	
	static Vector<Lambda_term> get_view_lambda_terms(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select lambda_term, table_name from view2lambda_term where view = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		while(rs.next())
		{
			String lambda_str = rs.getString(1);
			
			String table_name = rs.getString(2);
			
			Lambda_term l_term = new Lambda_term(lambda_str, table_name);
			
			lambda_terms.add(l_term);
		}
		
		return lambda_terms;
		
	}
	
	static String insert_view(Query view, String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
//        update_table(view, c, pst);
        
        int seq = get_view_num(c, pst) + 1;
        
        view.name = name;//"v" + seq;
        
        insert_view_table(seq, view, c, pst);
        
        insert_view_conditions(seq, view, c, pst);
        
        insert_view_lambda_term(seq, view, c, pst);
        
        insert_view_subgoals(seq, view, c, pst);
                
        c.close();
        
        String id = "v" + seq;
        
        return id;
        
	}
	
	
	static int get_view_num(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select count(*) from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		if(rs.next())
		{
			num = rs.getInt(1);
		}
		
		return num;
	}
	
	static void insert_view_table(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		String view_name = view.name;
		
		String head_vars_str = new String();
		
		for(int i = 0; i<view.head.args.size(); i++)
		{
			Argument arg = (Argument)view.head.args.get(i);
			
			if(i >= 1)
				head_vars_str += ",";
			
			head_vars_str += arg.name;
		}
		
		String query = "insert into view_table values ('v" + seq + "','" + head_vars_str + "','" + view_name + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static void insert_view_subgoals(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			String query = "insert into view2subgoals values ('v" + seq + "','" + subgoal.name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	
	static void insert_view_lambda_term(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{		
		String lambda_term_str = new String();
		
		String lambda_term_table_name = new String();
		
		for(int i = 0; i<view.lambda_term.size(); i++)
		{
			if(i >= 1)
			{
				lambda_term_str += populate_db.separator;
				
				lambda_term_table_name += populate_db.separator;
			}
			
			lambda_term_str += view.lambda_term.get(i).toString();
			
			lambda_term_table_name += view.lambda_term.get(i).table_name;
		}
		
		if(!view.lambda_term.isEmpty())
		{
			String query = "insert into view2lambda_term values ('v" + seq + "','" + lambda_term_str + "','" + lambda_term_table_name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
		

		
		
	}
	
	static void insert_view_conditions(int seq, Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<view.conditions.size(); i++)
		{			
			String query = "insert into view2conditions values ('v" + seq + "','" + view.conditions.get(i).arg1.name + view.conditions.get(i).op.get_op_name() + view.conditions.get(i).arg2.name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
			
		}
	}
	
	
	static void update_table(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		
		String [] base_relations = new String[view.body.size()];
		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) view.body.get(i);
			
			base_relations[i] = subgoal.name;
		}
		
		rename_base_relation.create_table(c, pst, base_relations);
	}
	
	
	static Vector<String> get_args(String table_name) throws SQLException, ClassNotFoundException
	{
		 Class.forName("org.postgresql.Driver");
         Connection c = DriverManager
            .getConnection(populate_db.db_url,
        	        populate_db.usr_name,populate_db.passwd);
         
//         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
         
         
//         String get_arg_sql = "select arguments from subgoal_arguments where subgoal_names = '" + table_name + "'";
         
         String get_arg_sql = "SELECT column_name  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '"+ table_name +"'";
         
         PreparedStatement pst = c.prepareStatement(get_arg_sql);
         
         ResultSet rs = pst.executeQuery();
         
         
         Vector<String> args = new Vector<String>();
         
         while(rs.next())
         {
        	 args.add(rs.getString(1));
         }
         
         
         c.close();
         
         return args;
	}

	static String get_full_query(String query) throws ClassNotFoundException, SQLException
	{
		
		
		String head = query.split(":")[0];
		
		String []subgoals = query.split(":")[1].split(",");
		
		String subgoal_str = new String();
		
		boolean start = false;
		
		for(int i = 0; i<subgoals.length; i++)
		{
			if(subgoals[i].contains("()"))
			{
				if(start)
				{
					subgoal_str += ",";
				}
				
				subgoals[i] = subgoals[i].trim();
				
				subgoals[i] = subgoals[i].substring(0, subgoals[i].length() - 2).trim();
				
				subgoal_str += subgoals[i] + "(";
				
				Vector<String> args = Parse_datalog.get_columns(subgoals[i]);
				
				for(int k = 0; k<args.size(); k++)
				{
					if(k >= 1)
						subgoal_str += ",";
					
					subgoal_str += args.get(k).trim();
				}
				
				subgoal_str += ")";
				
				start = true;
			}
			else
			{
				subgoal_str += "," + subgoals[i];
			}
		}
		
		String update_q = head + ":" + subgoal_str;
		
		return update_q;
	}
	
	
	
	

}
