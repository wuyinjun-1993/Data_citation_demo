package Pre_processing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import Corecover.Argument;
import Corecover.Lambda_term;
import Corecover.Query;
import Corecover.Subgoal;
import Operation.Conditions;
import Operation.Operation;
import Operation.op_equal;
import Operation.op_greater;
import Operation.op_greater_equal;
import Operation.op_less;
import Operation.op_less_equal;
import Operation.op_not_equal;
import citation_view.*;
import datalog.Parse_datalog;
import datalog.Query_converter;
import schema_reasoning.Gen_citation1;

public class populate_db {
	
	
	public static String separator = "|";
	
	public static String db_name = "iuphar_org";

	public static String[] base_relations = {"gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c", "family_c", "introduction_c"};

	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
		add_column(c,pst);
		populate_db(c, pst);
	}
	
	public static HashSet<String> get_table_name(Vector<Query> views)
	{
		HashSet<String> subgoals = new HashSet<String>();
		for(int i = 0; i<views.size(); i++)
		{
			Query view =views.get(i);
			for(int j = 0; j <view.body.size(); j++)
			{
				Subgoal subgoal = (Subgoal) view.body.get(j);
				subgoals.add(subgoal.name);
			}
		}
		
		return subgoals;
	}
	
	public static void add_column(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
//		HashSet<String> table_names = get_table_name(Gen_citation1.get_views_schema());
	      ResultSet rs = null;

//		for(Iterator<String> iter = table_names.iterator();iter.hasNext();)
	      
	    for(int k = 0; k<base_relations.length; k++)
		{
//			String table_name = iter.next();
	    	String table_name = base_relations[k];
	    	
	    	String [] col_name = {"citation_view", "web_view_vec"};
	    	
	    	for(int p = 0; p<col_name.length; p++)
	    	{
	    		String test_col = "SELECT column_name FROM information_schema.columns WHERE table_name='" + table_name + "' and column_name='"+ col_name[p]+ "'";
			    pst = c.prepareStatement(test_col);
			    rs = pst.executeQuery();
			    
			    if(rs.next())
			    {
			    	if(rs.getRow()!=0)
			    	{
			    		String remove_sql = "alter table "+ table_name +" drop column " + col_name[p];
			    		
			    		pst = c.prepareStatement(remove_sql);
			    		
			    		pst.execute();
			    	}
			    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN "+ col_name[p] +" text";
		    	    pst = c.prepareStatement(add_col);
		    	    pst.execute();
			    }
			    else
			    {
			    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN "+ col_name[p]+ " text";
		    	    pst = c.prepareStatement(add_col);
		    	    pst.execute();
			    }
	    	}
	    	
//	    	initialize(table_name, c, pst);
			
		}
	    
	 	Vector<Query> views = Gen_citation1.get_views_schema();
	 	
	 	for(int i = 0; i<views.size(); i++)
	 	{
	 		add_column_view(views.get(i), c, pst);
	 	}
	 	
	}
	
	static void initialize(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String inialization_q = "update " + table_name + " set web_view_vec = " + "'t'";
		
		pst = c.prepareStatement(inialization_q);
		
		pst.execute();
	}
	
	public static void add_column_view(Query view, Connection c, PreparedStatement pst) throws SQLException
	{
	    String test_col = "SELECT table_name FROM information_schema.columns WHERE table_name='"+view.name + "_table'";
	    
	    pst = c.prepareStatement(test_col);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	if(rs.getRow()!=0)
	    	{
	    		String update_table = "drop table " + view.name + "_table";
	    		
	    	    pst = c.prepareStatement(update_table);

	    	    pst.execute();
	    	}
	    }
		
		String create_view_table_sql = "create table "+ view.name + "_table as " + "select * from " + view.name;
		
	    pst = c.prepareStatement(create_view_table_sql);
	    
	    pst.execute();
	    
	    String add_column_sql = "alter table " + view.name + "_table add column citation_view text";
	    
	    pst = c.prepareStatement(add_column_sql);
	    
	    pst.execute();
	    
	}

	public static void populate_db(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
	 	Vector<Query> views = get_views_schema(c, pst);
	 	
 		HashMap<String, String> table_name_view = new HashMap<String, String>();

		String get_tables = "select renamed_view, subgoal from web_view_table";
		
		pst = c.prepareStatement(get_tables);
		
		ResultSet r = pst.executeQuery();
		
		while(r.next())
		{
			table_name_view.put(r.getString(2), r.getString(1));
		}
		
		for(int i = 0; i<views.size();i++)
		{
			Query view = views.get(i);
			
			Vector<Boolean> web_view = new Vector<Boolean> ();
			
			
			
			Vector<citation_view> citation_view = get_citation_view(view, web_view);
			
			
			gen_citation_view_query(citation_view,view, web_view.get(0), table_name_view, c, pst);
		}
	      
	    
	}
	
	
	
	public static Vector<Query> get_views_schema(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String query= "select * from view_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<Query> views = new Vector<Query>();
		
		while(rs.next())
		{
			String view = new String();
			
			String view_name = rs.getString(1);
			
			String q_subgoals = "select subgoal_names from view2subgoals where view = '" + view_name + "'";
			
			String head_vars = rs.getString(2);
			
			
			
			pst = c.prepareStatement(q_subgoals);
			
			ResultSet r = pst.executeQuery();
			
			Vector<String> subgoal_names = new Vector<String>();
			
			while(r.next())
			{
				subgoal_names.add(r.getString(1));
			}
			
			String subgoal_str = get_subgoal_str(view_name, subgoal_names, c, pst);
			
			
			view = view_name + "(" + head_vars + "):";
			
			view += subgoal_str; 
			
			
			String q_conditions = "select conditions from view2conditions where view = '" + view_name + "'";
			
			pst = c.prepareStatement(q_conditions);
			
			r = pst.executeQuery();
			
			Vector<Conditions> conditions = new Vector<Conditions>();
			
			String condition_str = new String();
			
			while(r.next())
			{
				
				condition_str += "," + r.getString(1);
				
				
				

			}
			
			view += condition_str;
			
			Query v = Parse_datalog.parse_query(view);
			
			v.lambda_term = Gen_citation1.get_lambda_terms(view_name, c, pst);
			Gen_citation1.check_equality(v);
			
			views.add(v);
			
		}
		
		return views;
				
				
		
		
	}
	
	
	public static String get_subgoal_str(String view_name, Vector<String> subgoal_names, Connection c, PreparedStatement pst) throws SQLException
	{		
		
		Vector<String> col_names = new Vector<String>();
		
		String subgoals = new String();
		
		for(int i = 0; i<subgoal_names.size(); i++)
		{
			String query_table_col = "select arguments from subgoal_arguments where subgoal_names = '" + subgoal_names.get(i) + "'";
			
			pst = c.prepareStatement(query_table_col);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next())
			{
				
				String [] cols = rs.getString(1).split(",");
				
				String col_str = new String();
				
				String subgoal_str = subgoal_names.get(i) + "(";
				
				
				for(int j = 0; j<cols.length; j++)
				{
					
					if(j >= 1)
						subgoal_str += ",";
					
					subgoal_str += cols[j];
					
				}
				
				subgoal_str += ")";
				
				if(i >= 1)
					subgoals += ",";
				
				subgoals += subgoal_str;
				
			}
		}
		
//		String col_str = new String();
//		
//		for(int k = 0; k<col_names.size(); k++)
//		{
//			if(k >= 1)
//				col_str += ",";
//			
//			col_str += col_names.get(k);
//		}
//		
//		String condition_str = new String();
//		
//		for(int k = 0; k<conditions.size(); k++)
//		{
//			if(k >= 1)
//				condition_str += ",";
//			
//			condition_str += conditions.get(k);
//		}
		
		return subgoals;
		
		
		
	}
	
	
	static void check_equality(Query view)
	{
		
		HashMap<String, String> arg_name = new HashMap<String, String>();
		
		HashMap<String, Argument> arg_map = new HashMap<String, Argument>();
		
		Vector<Conditions> conditions = view.conditions;
		
		if(conditions == null)
			conditions = new Vector<Conditions>();
		
		for(int i = 0; i<view.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal)view.body.get(i);
			
			for(int j = 0; j<subgoal.args.size(); j++)
			{
				
				Argument arg = (Argument)subgoal.args.get(j);
				String subgoal_name = arg_name.get(arg.name); 
				
				
				if(subgoal_name == null)
				{
					arg_name.put(arg.name, subgoal.name);
					arg_map.put(arg.name, arg);
				}
				else
				{
					
					Conditions condition = new Conditions(arg, subgoal.name, new op_equal(), arg_map.get(arg.name), subgoal_name);

					Vector<Argument> head_args = view.head.args;
					
					if(head_args.contains(arg))
					{
						head_args.add(new Argument(subgoal.name + "_" + arg.name, arg.origin_name));
					}
					
					arg.name = subgoal.name + "_" + arg.name;
					
					
					conditions.add(condition);
					
				}
			}
			
		}
		
		view.conditions = conditions;
	}
	
	public static void gen_citation_view_query(Vector<citation_view> citation_views, Query view, Boolean web_view, HashMap<String, String> table_name_view, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
//		String citation_view_query = new String();
		
//		ResultSet rs = get_citation(view.name + "_table",view.lambda_term);
		
		populate(citation_views, view, view.lambda_term,view.name + "_table", view.name, web_view, table_name_view, c,pst);
		
//		assign_view_query(view, citation_views, view.name, web_view, c, pst);
		
	}
	
	public static void assign_view_query(Query view, Vector<citation_view> citation_views, String view_name, Boolean web_view, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Vector<Argument>> lambda_terms = new Vector<Vector<Argument>>();
		
		HashMap<String, String> table_name_view = new HashMap<String, String>();
		
		if(web_view)
		{
			String get_tables = "select renamed_view, subgoal from web_view_table where view = '" + view_name + "'";
			
			pst = c.prepareStatement(get_tables);
			
			ResultSet r = pst.executeQuery();
			
			while(r.next())
			{
				table_name_view.put(r.getString(2), r.getString(1));
			}
			
		}
		
		
		for(int i = 0; i<view.body.size();i++)
		{
			
			
			String lambda_term_sql = new String();

			
			Subgoal subgoal = (Subgoal) view.body.get(i);
			
			if(subgoal.contains(view.head.args));
			{
				Vector<Argument> args = new Vector<Argument>();//get_lambda_terms(view.lambda_term, subgoal);

				
				for(int j = 0; j<subgoal.args.size(); j++)
				{
					lambda_term_sql += "," + subgoal.args.get(j).toString();
					
					args.add((Argument) subgoal.args.get(j));
				}

			
			
			String citation_view_query = "select distinct citation_view" + lambda_term_sql + " from "+ view.name + "_table";
			
			pst = c.prepareStatement(citation_view_query);
		
			
		    ResultSet rs = pst.executeQuery();
		    
		    
		    while(rs.next())
		    {
		    	String citation_view = rs.getString(1);
		    	
		    	int pointer = 1;
		    	  		
		    		String key_sql = new String();
		    		
		    		String update_citation_view_sql = new String();
		    		
		    		for(int j = 0; j<args.size(); j++)
		    		{
						
						
						String item = rs.getString(++pointer);
						
						if(item!= null && item.contains("'"))
						{
							item = item.replaceAll("'", "''");
						}
						
//						if(item == null)
//						{
//							key_sql = key_sql + args.get(j).name + "= null";
//						}
						if(item != null)
						{
							if(j >= 1)
								key_sql = key_sql + " and ";
							
							key_sql = key_sql + args.get(j).name + "='" + item + "'";
						}
						
		    			
		    		}

		    		String curr_citation_view_sql = "select "+ "citation_view from " + subgoal.name + " where " + key_sql;
		    			    		
		    		pst = c.prepareStatement(curr_citation_view_sql);
		    			    		
		    		ResultSet temp_rs = pst.executeQuery();
		    		
		    		

		    		while(temp_rs.next())
		    		{
		    					    			
		    			String update_sql = key_sql;//primary_key + "='" + primary_key_value +"'"; 
		    			
	    				String curr_citation_view = temp_rs.getString(1);
	    				
	    				
	    				if(web_view)
	    				{
	    					String renamed_view = table_name_view.get(subgoal.name);
	    					
	    					citation_view = rename_citation_view(citation_view, renamed_view);
	    					
	    				}
	    				

		    			if(curr_citation_view==null)
		    			{
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}
		    			else
		    			{
		    				
		    				update_citation_view_sql = "update " + subgoal.name + " set citation_view = '" + curr_citation_view + separator + citation_view + "' where ";
				    		
				    		update_citation_view_sql = update_citation_view_sql + update_sql;
				    		
						    pst = c.prepareStatement(update_citation_view_sql);
						    
						    pst.execute();
		    			}

		    		}
		    		
		    					    
		    	}
				
			}

	    }
	}
	
	static String rename_citation_view(String curr_name, String new_name)
	{
			
		if(curr_name.contains("(") && curr_name.contains(")"))
		{
			int index = curr_name.indexOf("(");
			
			String sub_str = curr_name.substring(index, curr_name.length());
			
			return new_name + sub_str;
			
		}
		else
		{
			return new_name;
		}
	}
	
	public static Vector<Argument> get_lambda_terms(Vector<Argument> lambda_term, Subgoal subgoal)
	{
		Vector<Argument> lambda_terms = new Vector<Argument>();
		for(int i =0; i<lambda_term.size();i++)
		{
			if(subgoal.contains(lambda_term.get(i)))
			{
				lambda_terms.add(lambda_term.get(i));
			}
		}
		
		return lambda_terms;
	}
	
	
	
	public static void populate(Vector<citation_view> citation_views, Query view, Vector<Lambda_term> lambda_terms , String table, String citation_name, boolean web_view, HashMap<String, String> table_name_view, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
	    String test_col = "SELECT "+"* FROM "+table;
	    
	    pst = c.prepareStatement(test_col);
	    
	    ResultSet rs = pst.executeQuery();
	    
		if(!lambda_terms.isEmpty())
		{
			
			HashMap<String, Vector<Integer>> subgoal_lambda_term_map = new HashMap<String, Vector<Integer>>();
			
			for(int k = 0; k<lambda_terms.size(); k++)
			{
				String table_name = lambda_terms.get(k).table_name;
				
				Vector<Integer> Int_list = subgoal_lambda_term_map.get(table_name);
				
				if(Int_list == null)
				{
					Int_list = new Vector<Integer>();
					
					Int_list.add(k);
					
					subgoal_lambda_term_map.put(table_name, Int_list);
				}
				
				else
				{
					Int_list.add(k);
				}
				
			}
			
			
			while(rs.next())
			{
				int num = lambda_terms.size();
				String sql = new String();
				Vector<String> paras = new Vector<String>();
				
				for(int i =0;i<num;i++)
				{
					String lambda_term = rs.getString(lambda_terms.get(i).toString());
					if(i >= 1)
						sql = sql + " and ";
					sql = sql + lambda_terms.get(i) + "='" + lambda_term + "'";		
					paras.add(lambda_term);
					
				}
							
				String insert_citation_view;
				

				
				for(int i = 0; i<view.body.size(); i++)
				{
					
					Subgoal subgoal = (Subgoal) view.body.get(i);
					
					Vector<String> paras_subgoal = new Vector<String>();
					
					Vector<Integer> list = subgoal_lambda_term_map.get(subgoal.name);
					
					
					if(!web_view)
					{
						for(int k = 0; k<paras.size(); k++)
						{
							if(list!=null && list.contains(k))
							{
								paras_subgoal.add(paras.get(k));
							}
							else
							{
								paras_subgoal.add("");
							}
						}
						
						insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, paras_subgoal);
					}
					else
					{
						paras_subgoal = paras;
						
						insert_citation_view = citation_views.get(0).gen_citation_unit(table_name_view.get(subgoal.name), paras_subgoal);
					}
					
//					if(!web_view)
//						insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, paras_subgoal);
//					else
//					{
//					}					
					
					String where_values = new String();
					
					
					for(int k =0; k<subgoal.args.size(); k++)
					{
						Argument arg = (Argument) subgoal.args.get(k);
											
						String item = rs.getString(arg.name);
						
						if(item!= null && item.contains("'"))
						{
							item = item.replaceAll("'", "''");
						}
						
//						if(item == null)
//						{
//							key_sql = key_sql + args.get(j).name + "= null";
//						}
						if(item != null && !item.isEmpty())
						{
							if(item.contains("."))
							{
								try{
									float f = Float.valueOf(item);
								}
								catch(NumberFormatException e)
								{
									if( k >= 1)
										where_values += " and ";
									
									where_values += arg.name + "='" + item + "'";
								}
							}
							else
							{
								if( k >= 1)
									where_values += " and ";
								
								where_values += arg.name + "='" + item + "'";
						
							}
					
							
//							if( k >= 1)
//								where_values += " and ";
//							
//							where_values += arg.name + "='" + item + "'";
						}
						
						
					}
					
					String curr_citation_sql = "select citation_view from " + subgoal.name + " where " + where_values;
					
					System.out.println(curr_citation_sql);
					
					pst = c.prepareStatement(curr_citation_sql);
					
					ResultSet r = pst.executeQuery();
					
					
					String citations = null;
					
					if(r.next())
					{
						citations = r.getString(1);
						
					}
					if(citations !=null)
					{
						String[] citation_list = citations.split("\\" + populate_db.separator);
						
						Vector<String> curr_citation_vec = new Vector<String>();
						
						curr_citation_vec.addAll(Arrays.asList(citation_list));
						
						if(!curr_citation_vec.contains(insert_citation_view))
						{
							
							String update_sql = "update " + subgoal.name + " set citation_view='" + citations + populate_db.separator + insert_citation_view + "' where " + where_values;
						    
							pst = c.prepareStatement(update_sql);
						    
							pst.execute();
						}
												
					}
					else
					{
						String update_sql = "update " + subgoal.name + " set citation_view='" + insert_citation_view + "' where " + where_values;
					    
						pst = c.prepareStatement(update_sql);
					    
						pst.execute();
					}
										
						
						
						
					
					
				}
				
//				String curr_citation_view = rs.getString("citation_view");
//				if(curr_citation_view!=null)
//				{
//					curr_citation_view = curr_citation_view + "," + insert_citation_view;
//				}
//				else
//				{
//					curr_citation_view = insert_citation_view;
//				}
//				
				
				
			}
		}
		else
		{
			
			
			while(rs.next())
			{
				
				String insert_citation_view;
				

				
				for(int i = 0; i<view.body.size(); i++)
				{
					Subgoal subgoal = (Subgoal) view.body.get(i);
					
					String where_values = new String();
					
					if(web_view)
					{
						insert_citation_view = citation_views.get(0).gen_citation_unit(table_name_view.get(subgoal.name), null);
					}
//						
					else
					{
						insert_citation_view = citation_views.get(0).gen_citation_unit(citation_name, null);
					}
					
					for(int k =0; k<subgoal.args.size(); k++)
					{
						Argument arg = (Argument) subgoal.args.get(k);
					
						String item = rs.getString(arg.name);
						
						if(item!= null && item.contains("'"))
						{
							item = item.replaceAll("'", "''");
						}

						if(item != null && !item.isEmpty())
						{
							
							if(item.contains("."))
							{
								try{
									float f = Float.valueOf(item);
								}
								catch(NumberFormatException e)
								{
									if( k >= 1)
										where_values += " and ";
									
									where_values += arg.name + "='" + item + "'";
								}
							}
							else
							{
								if( k >= 1)
									where_values += " and ";
								
								where_values += arg.name + "='" + item + "'";
					
							}
							
							
							
							
						}
						
						
					}
					
					
					String curr_citation_sql = "select citation_view from " + subgoal.name + " where " + where_values;
					

					
					pst = c.prepareStatement(curr_citation_sql);
					
					ResultSet r = pst.executeQuery();
					
					
					String citations = null;
					
					if(r.next())
					{
						citations = r.getString(1);
						
					}
					
					if(citations !=null)
					{
						String[] citation_list = citations.split("\\" + populate_db.separator);
						
						Vector<String> curr_citation_vec = new Vector<String>();
						
						curr_citation_vec.addAll(Arrays.asList(citation_list));
						
						if(!curr_citation_vec.contains(insert_citation_view))
						{
							
							String update_sql = "update " + subgoal.name + " set citation_view='" + citations + populate_db.separator + insert_citation_view + "' where " + where_values;
						    
							pst = c.prepareStatement(update_sql);
						    
							pst.execute();
						}
												
					}
					else
					{
						String update_sql = "update " + subgoal.name + " set citation_view='" + insert_citation_view + "' where " + where_values;
					    
						pst = c.prepareStatement(update_sql);
					    
						pst.execute();
					}
					
				}
				
			}
						
			
		}
	}
	
	public static Vector<citation_view> get_citation_view(Query view, Vector<Boolean> web_view) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
	    
	    
	    String view_table_q = "select * from view_table where view = '" + view.name + "'";
	    
	    pst = c.prepareStatement(view_table_q);
	    
	    rs = pst.executeQuery();
	    
	    
	    Vector<citation_view> citations = new Vector<citation_view>();
	    
	    Vector<String> lambda_terms = new Vector<String>();

	    
	    while(rs.next())
	    {
	    	boolean webview = rs.getBoolean(3);
	    	
	    	web_view.add(webview);
	    	
	    	if(webview)
	    	{
	    		
	    		get_view_lambda_terms(view, lambda_terms, c, pst);
	    		
//	    		get_view_subgoals(view.name, subgoals, c, pst);
	    		get_renamed_views(view.name, lambda_terms, citations, c, pst);

	    	}
	    	
	    	else
	    	{
	    		get_view_lambda_terms(view, lambda_terms, c, pst);
    		    
	    		
	    		Vector<String> subgoals = new Vector<String>();
	    		
	    		get_view_subgoals(view.name, subgoals, c, pst);
    		    
    		    if(!lambda_terms.isEmpty())
    		    {	
    		    	
		    		citations.add(new citation_view_parametered(view.name, lambda_terms, subgoals));
		    		    		    
    		    }
    		    else
    		    {
    		    	citations.add(new citation_view_unparametered(view.name, subgoals));
    		    }
	    	}
	    	
	    }
	    
		return citations;

//	    String test_col = "SELECT citation_view.citation_view_name FROM citation_view WHERE citation_view.view_name='"+view.name+"'";
//	    pst = c.prepareStatement(test_col);
//	    rs = pst.executeQuery();
//	    
//	    String citation_view_name = new String();
//	    
////	    Vector<String> lambda_term_vec = new Vector<String>(); 
//	    
//	    if(rs.next())
//	    {
//	    	citation_view_name = rs.getString(1);
//	    }
	    
	   
	}
	
	
	static void get_renamed_views(String name, Vector<String> lambda_terms, Vector<citation_view> citations, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		String rename_view_sql = "select renamed_view, subgoal from web_view_table where view = '" + name + "'";
		
	    pst = c.prepareStatement(rename_view_sql);
	    
	    ResultSet r = pst.executeQuery();
	    
	    while(r.next())
	    {
	    	String renamed_view = r.getString(1);
	    	
	    	String subgoal = r.getString(2);
	    	
	    	if(!lambda_terms.isEmpty())
	    	{
	    		Vector<String> table_names = new Vector<String>();
	    		
	    		table_names.add(subgoal);
	    		
	    		citation_view c_v = new citation_view_parametered(renamed_view, lambda_terms, table_names);
	    		
	    		citations.add(c_v);
	    	}
	    	
	    	else
	    	{
	    		Vector<String> table_names = new Vector<String>();
	    		
	    		table_names.add(subgoal);
	    		
	    		citation_view c_v = new citation_view_unparametered(renamed_view, table_names);
	    		
	    		citations.add(c_v);
	    	}
	    		
		}
		
	}
	
	static void get_view_lambda_terms(Query view, Vector<String> lambda_terms, Connection c, PreparedStatement pst)throws ClassNotFoundException, SQLException
	{
		String lambda_term_sql = "select lambda_term from view2lambda_term where view = '" + view.name + "'";
	    pst = c.prepareStatement(lambda_term_sql);
	    
	    ResultSet r = pst.executeQuery();
	    
	    while(r.next())
	    {
//		    	if(rs.getString(1) != null && rs.getString(1).length()!=0)
	    	
	    		lambda_terms.add(r.getString(1));
	    		
//		    		System.out.println(lambda_terms);
	    		
//	    		citation_view c_v = new citation_view_parametered(view.name, lambda_terms);
	    		
		}
	
	}
	
	static void get_view_subgoals(String name, Vector<String> subgoals, Connection c, PreparedStatement pst) throws SQLException
	{
		String subgoal_sql = "select subgoal_names from view2subgoals where view = '" + name + "'";
	    
		pst = c.prepareStatement(subgoal_sql);
	    
	    ResultSet r = pst.executeQuery();
	    	    
	    while(r.next())
	    {
//		    	if(rs.getString(1) != null && rs.getString(1).length()!=0)
	    	
	    		subgoals.add(r.getString(1));
	    		
//		    		System.out.println(lambda_terms);
	    		
//	    		citation_view c_v = new citation_view_parametered(view.name, lambda_terms);
	    		
		}
	}
	
//	public static ResultSet get_citation(String table, Vector<Lambda_term> lambda_term) throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//	      ResultSet rs = null;
//	      PreparedStatement pst = null;
//		Class.forName("org.postgresql.Driver");
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
//	        "postgres","123");
////	    String lambda_term_string = lambda_term.toString();
//	    
//	    lambda_term_string = lambda_term_string.substring(1,lambda_term_string.length() - 1);
//	    
//    	Vector <Argument> t = new Vector<Argument>();
//
////	    
////	    t.add(new Argument("q",false));
////	    t.add(new Argument("r",false));
////	    
////	    
//		    String t_str = t.toString();
//		    
//		    String test_col = "SELECT "+"* FROM "+table;
//		    pst = c.prepareStatement(test_col);
//		    rs = pst.executeQuery();
//		    
////	    if(!lambda_term.isEmpty())
////	    {
////	    
////
////
////	    }
////	    else
////	    {
//////	    	String q_primary_key = "select kc.column_name from information_schema.table_constraints tc,information_schema.key_column_usage kc where tc.constraint_type = 'PRIMARY KEY' and kc.table_name = tc.table_name and kc.table_schema = tc.table_schema and kc.constraint_name = tc.constraint_name and tc.table_name='"+ table +"'";
//////	    	pst = c.prepareStatement(q_primary_key);
//////		    rs = pst.executeQuery();
//////	    	
//////		    String primary_key = new String();
//////		    if(rs.next())
//////		    	primary_key = rs.getString(1);
//////	    	
//////		    lambda_term.clear();
//////		    lambda_term.add(new Argument(primary_key));
////	    	String test_col = "SELECT distinct citation_view FROM "+table;
////		    pst = c.prepareStatement(test_col);
////		    rs = pst.executeQuery();
////	    }
//	    return rs;
//	}

//	public static Vector<Query> get_views_schema(Connection c, PreparedStatement pst)
//	{
//	      ResultSet rs = null;
//	      Vector<Query> views = new Vector<Query>();
//	      
//	      try {
//	         
//	         
////	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
//	         pst = c.prepareStatement("SELECT *  FROM view_table");
//	         rs = pst.executeQuery();
//	         int num=1;
//	         
//	            while (rs.next()) {
//	            	
//	            	String head = rs.getString(1);
//	            	
//	            	String [] str_lambda_terms = null;
//	            	
//	            	if(!rs.getString(2).equals(""))
//	            	{
//	            		str_lambda_terms = rs.getString(2).split(",");
//	            	}
//	            	
//	            	String [] subgoals = rs.getString(3).split("\\),");
//	                
//	                subgoals[subgoals.length-1]=subgoals[subgoals.length-1].split("\\)")[0];
//	            	
//	            	
//	                Query view = parse_query(head, subgoals,str_lambda_terms);
//	                
//	                views.add(view);
////	                System.out.print(rs.getString(1));
////	                System.out.print('|');
////	                System.out.print(rs.getString(2));
////	                if(rs.getString(2).length()==0)
////	                {
////	                	int y=0;
////	                	y++;
////	                }
////	                System.out.print('|');
////	                System.out.println(rs.getString(3));
//	            }
//	         
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	         System.err.println(e.getClass().getName()+": "+e.getMessage());
//	         System.exit(0);
//	      }
//	      
//	      return views;
//	}
//	
//	static Query parse_query(String head, String []body, String []lambda_term_str)
//    {
//                
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
////        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        
//        
//        Vector<Argument> lambda_terms = new Vector<Argument>();
//        
//        if(lambda_term_str != null)
//        {
//	        for(int i =0;i<lambda_term_str.length; i++)
//	        {
//	        	lambda_terms.add(new Argument(lambda_term_str[i]));
//	        }
//        
//        }
//        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
//    }


}
