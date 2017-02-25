package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.*;
import edu.upenn.cis.citation.datalog.Parse_datalog;

public class insert_new_view {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		String view = "q(introduction_c_family_id): introduction_c()";
		
		view = get_full_query(view);
		
		Query v = Parse_datalog.parse_query(view);
		
//		Lambda_term l = new Lambda_term("ligand_c_ligand_id", "ligand_c");
//		
//		v.lambda_term.add(l);
		
		String name = "introduction_view";
		
		insert_view(v, name);
		
		create_view.check_create_views();
		
		populate_db.update();
		
		
	}
	
	public static void add(Query v, String name) throws ClassNotFoundException, SQLException
	{
		insert_view(v, name);
		
		create_view.check_create_views();
		
		populate_db.update();
	}
	
	
	static void insert_view(Query view, String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
//        update_table(view, c, pst);
        
        int seq = get_view_num(c, pst) + 1;
        
        view.name = name;//"v" + seq;
        
        insert_view_table(view, c, pst);
        
        insert_view_conditions(view, c, pst);
        
        insert_view_lambda_term(view, c, pst);
        
        insert_view_subgoals(view, c, pst);
        
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
	
	static void insert_view_table(Query view, Connection c, PreparedStatement pst) throws SQLException
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
		
		String query = "insert into view_table values ('" + view_name + "','" + head_vars_str + "','f')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	static void insert_view_subgoals(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		String view_name = view.name;
		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			String query = "insert into view2subgoals values ('" + view_name + "','" + subgoal.name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
	}
	
	static void insert_view_lambda_term(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		String view_name = view.name;
		
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
			String query = "insert into view2lambda_term values ('" + view_name + "','" + lambda_term_str + "','" + lambda_term_table_name + "')";
			
			pst = c.prepareStatement(query);
			
			pst.execute();
		}
		

		
		
	}
	
	static void insert_view_conditions(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
		for(int i = 0; i<view.conditions.size(); i++)
		{
			String view_name = view.name;
			
			String query = "insert into view2conditions values ('" + view_name + "','" + view.conditions.get(i).arg1.name + view.conditions.get(i).op.get_op_name() + view.conditions.get(i).arg2.name + "')";
			
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
