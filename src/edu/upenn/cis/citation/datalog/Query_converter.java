package edu.upenn.cis.citation.datalog;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.sql.Statement;

import edu.upenn.cis.citation.Corecover.Argument;
import edu.upenn.cis.citation.Corecover.Lambda_term;
import edu.upenn.cis.citation.Corecover.Mapping;
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.citation_view_parametered;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.gen_citation.gen_citation1;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import net.sf.jsqlparser.util.TablesNamesFinder;
import java.util.Random;



public class Query_converter {
	
	public static void main(String [] args) throws JSQLParserException, ClassNotFoundException, SQLException
	{
		
		String str = "Q(f):family(f, n, tx), introduction(f, ty)";
		
//		Query query = Parse_datalog.parse_query(str);//sql2datalog("select * from family_c ,introduction_c where type = 'gpcr' and family_id= '2'");
//		
//		String q = datalog2sql(query);
//		
//		System.out.println(query.toString());
//		
//		System.out.println(q);
	}
	
	
	static String get_group_conditions(HashSet<Conditions> conditions, String curr_str)
	{
		
		String condition_str = new String();
		
		if(curr_str.isEmpty())
			return condition_str;
		
		int i = 0;
		
		for(Iterator iter = conditions.iterator(); iter.hasNext();)
		{
			
			Conditions condition = (Conditions) iter.next();
			
			if(i >= 1)
				condition_str += " and ";
			
			if(curr_str.charAt(i) == '1')
			{
				condition_str += get_single_condition_str(condition);
			}
			else
			{
				condition_str += get_single_condition_str_neg(condition);
			}
			
			i ++;
		}
		
		return condition_str;
	}
	
	public static Vector<String> group_sub_query_citations(citation_view_vector c_views, HashSet<Conditions> valid_conditions, String curr_str, Query q, HashMap<Head_strs, Vector<HashSet<String>>> author_lists) throws ClassNotFoundException, SQLException
	{
		String head_vars = get_renamed_sel_item(q);
		
		String group_vars = get_group_item(q);
		
		String query_condition_str = get_condition(q);
		
		String group_condition_str = get_group_conditions(valid_conditions, curr_str);
		
		String query_relations = get_relations_without_citation_table(q);
		
		Vector<String> author_block_queries = gen_query_author_block(c_views, q, head_vars, query_condition_str, group_condition_str, query_relations, group_vars, author_lists);
		
//		System.out.println(author_block_queries.get(0));
		
		return author_block_queries;
	}
	
	
	static Vector<String> gen_query_author_block(citation_view_vector c_views, Query q, String head_vars, String query_condition_str, String group_condition_str, String query_relations, String group_vars, HashMap<Head_strs, Vector<HashSet<String>>> author_lists) throws ClassNotFoundException, SQLException
	{
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
			    
		Vector<String> citation_queries = new Vector<String>();

//	    System.out.println(c_views.toString());
		
		
		for(int i = 0; i<c_views.c_vec.size(); i++)
		{
			Vector<Integer> query_ids = gen_citation1.get_query_id(c_views.c_vec.get(i).get_name(), c, pst);
						
			Vector<String> curr_citation_queries = new Vector<String>();
			
			for(int j = 0; j<query_ids.size(); j++)
			{
				Query citation_query = Query_operation.get_query_by_id(query_ids.get(j));
				
//				System.out.println(citation_query.toString());
				
				String citation_query_relation_names = get_relations_without_citation_table(q, citation_query);

				
				String citation_query_sel_item = get_citation_query_sel_item(citation_query);
				
				
				String citation_sub_query_join_condition = get_citation_subquery_join_condition(c_views.c_vec.get(i), citation_query, q);
				
				String citation_condition_str = get_condition(citation_query);
				
//				System.out.println(citation_query.toString());
				
				String subquery = gen_citation_sub_query(citation_query_sel_item, q, head_vars, query_condition_str, group_condition_str, query_relations, citation_query_relation_names, citation_sub_query_join_condition, citation_condition_str);
				
				curr_citation_queries.add("(" + subquery + ")");
				
//				System.out.println(c_views.c_vec.get(i) + ":" + subquery);
				
				citation_queries = citation_queries_cross_union(citation_queries, curr_citation_queries, i);
				
			}
			
		}
		
		Vector<String> full_queries = new Vector<String>();
		
		for(int i = 0; i<citation_queries.size(); i++)
		{
			full_queries.add("select distinct " + group_vars + ", string_agg(first_names ||' '|| surname, ',') from (" + citation_queries.get(i) + ") h group by " + group_vars);
			
//			System.out.println(citation_queries.get(i));
			
			pst = c.prepareStatement(citation_queries.get(i));
			
			ResultSet rs = pst.executeQuery();
			
			HashMap<Head_strs, HashSet<String>> curr_authors = new HashMap<Head_strs, HashSet<String>>();
			
			while(rs.next())
			{
				
				Vector<String> head_vals = new Vector<String> ();
				
				for(int k = 0; k<q.head.size() ; k++)
				{
					head_vals.add(rs.getString(k + 1));
				}
				
				Head_strs h_vals = new Head_strs(head_vals);
				
				String author = rs.getString(q.head.size() + 1) + " " + rs.getString(q.head.size() + 2);
				
				if(curr_authors.get(h_vals) == null)
				{
					HashSet<String> authors = new HashSet<String>();
					
					authors.add(author);
					
					curr_authors.put(h_vals, authors);
				}
				else
				{
					HashSet<String> authors = curr_authors.get(h_vals);
					
					authors.add(author);
					
					curr_authors.put(h_vals, authors);
				}
			}
			
			
			Set<Head_strs> h_keys = curr_authors.keySet();
			
			for(Iterator iter = h_keys.iterator(); iter.hasNext();)
			{
				Head_strs h_val = (Head_strs) iter.next();
				
				HashSet<String> curr_author_lists = curr_authors.get(h_val);
				
				if(author_lists.get(h_val) == null)
				{
					Vector<HashSet<String>> authors = new Vector<HashSet<String>>();
					
					authors.add(curr_author_lists);
					
					author_lists.put(h_val, authors);
					
				}
				else
				{
					Vector<HashSet<String>> authors = author_lists.get(h_val);
					
					authors.add(curr_author_lists);
					
					author_lists.put(h_val, authors);
				}
				
			}
			
			
		}
		
		c.close();
		
		
		return full_queries;
		
	}
	
	
	static Vector<String> citation_queries_cross_union(Vector<String> citation_queries, Vector<String> curr_citation_queries, int i)
	{
		
		if(i == 0)
			return curr_citation_queries;
		
		Vector<String> updated_citation_queries = new Vector<String>();
		
		for(int k = 0; k<curr_citation_queries.size(); k++)
		{
			for(int r = 0; r < citation_queries.size(); r++)
			{
				updated_citation_queries.add(citation_queries.get(r) + " union " + curr_citation_queries.get(k));
			}
		}
		
		
		return updated_citation_queries;
	}
	
	static String get_citation_subquery_join_condition(citation_view c_view, Query citation_query, Query q)
	{
		
		if(citation_query.lambda_term.size() > 0)
		{
			
			citation_view_parametered c_view_p = (citation_view_parametered) c_view;
			
			String join_condition = new String();
			
			int num = 0;
			
			for(int i = 0; i < citation_query.lambda_term.size(); i++)
			{
				
				HashMap var_mapping = c_view_p.view_tuple.getMapSubgoals();
				
				String subgoal = citation_query.subgoal_name_mapping.get(citation_query.lambda_term.get(i).table_name);
				
				String mapped_subgoal = (String) c_view_p.view_tuple.mapSubgoals_str.get(subgoal);
				
				String var_name = citation_query.lambda_term.get(i).name;
				
				String variable = var_name.substring(var_name.indexOf("_") + 1, var_name.length());
				
				
				
				
//				if(!subgoal.equals(mapped_subgoal))
				{
					if(num >= 1)
					{
						mapped_subgoal += " and ";
					}
					
					join_condition += citation_query.lambda_term.get(i).table_name + "." + variable + " = " + mapped_subgoal + "." + variable;

					num ++;
				}
				
				
			}
			
			return join_condition;
		}
		else
		{
			return new String();
		}
		
		
	}
	
	static String gen_citation_sub_query(String citation_query_sel_item, Query q, String head_vars, String query_condition_str, String group_condition_str, String query_relations, String citation_query_relation_names, String citation_sub_query_join_condition, String citation_condition_str)
	{
		String query = "select distinct " + head_vars + "," + citation_query_sel_item + " from " + query_relations + "," + citation_query_relation_names;
		
		Vector<String> condition_strs = new Vector<String>();
		
		if(!query_condition_str.isEmpty())
		{
			condition_strs.add(query_condition_str);
		}
		
		if(!group_condition_str.isEmpty())
		{
			condition_strs.add(group_condition_str);
		}
		
		if(!citation_sub_query_join_condition.isEmpty())
		{
			condition_strs.add(citation_sub_query_join_condition);
		}
		
		if(!citation_condition_str.isEmpty())
		{
			condition_strs.add(citation_condition_str);
		}
		
		if(!condition_strs.isEmpty())
		{
			query += " where ";
			
			for(int k = 0; k<condition_strs.size(); k++)
			{
				
				if(k >= 1)
					query += " and ";
				
				query += condition_strs.get(k);
			}
		}
		
		return query;
		
	}
	
	
//	public static Query sql2datalog(String sql) throws JSQLParserException, ClassNotFoundException, SQLException
//	{
//		String statement= sql; 
//		
//        CCJSqlParserManager parserManager = new CCJSqlParserManager();
//        
//        Select select=(Select) parserManager.parse(new StringReader(statement));
//        
//        String query_name = "Q";
//        
//        Vector<Argument> query_arg = new Vector<Argument>();
//        
//        Vector<Subgoal> subgoals = new Vector<Subgoal>();
//        
//        PlainSelect plain=(PlainSelect)select.getSelectBody();    
//        
//        final List<String> columnList = new ArrayList<String>();
//        
//        final List<String> values = new ArrayList<String>();
//        
//        
//        gen_conditions(plain, columnList, values);
//        
//        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//        
//		List<String> tableList = tablesNamesFinder.getTableList(select);
//		
//        for(int i = 0; i<tableList.size(); i++)
//        {
//        	subgoals.add(gen_subgoal(tableList.get(i), columnList, values));
//        }
//        
//        gen_select_item(plain, tableList, query_arg,columnList, values);
//        
//        Subgoal head = new Subgoal(query_name, query_arg);
//		
//        Query query = new Query(query_name, head, subgoals);
//        
//        return query;
//        
//	}
	
	static void gen_conditions(PlainSelect plain, List<String> columnList, List<String> values)
	{
		Expression where = plain.getWhere();
        where.accept(new ExpressionVisitorAdapter() {

            @Override
            public void visit(Column column) {
                super.visit(column);
                columnList.add(column.getColumnName());
            }
            
            @Override
            public void visit(StringValue value) {
            	super.visit(value);
            	values.add(value.getValue());
            }
            
        });
	}
	
//	static void gen_select_item(PlainSelect plain, List<String> tableList, Vector<Argument> query_arg, List<String> columnList, List<String> values) throws ClassNotFoundException, SQLException
//	{
//        String str = plain.getSelectItems().toString();
//        
//        int len = str.length();
//        
//        str = str.substring(1, len - 1);
//        
//        Vector<String> sel_col = new Vector<String>();
//        
//        sel_col.addAll(Arrays.asList(str.split(",")));
//              
//        pre_process(sel_col, tableList);
//        
//        for(int i=0;i<sel_col.size();i++)
//        {
//           String item = sel_col.get(i);
//        	
//           if(columnList.contains(item))
//           {
//        	   query_arg.add(new Argument("'" + values.get(columnList.indexOf(item)) + "'", item));
//           }
//           else
//        	   query_arg.add(new Argument(item));
//           
//        }
//	}
	
	static void pre_process(Vector<String> selectitems, List<String> tableList) throws ClassNotFoundException, SQLException
	{
		if(selectitems.size() == 1 && selectitems.get(0).equals("*"))
		{
			Connection c = null;
			
		    ResultSet rs = null;
		    
		    PreparedStatement pst = null;
		    
			Class.forName("org.postgresql.Driver");
			
		    c = DriverManager
		        .getConnection(populate_db.db_url,
		    	        populate_db.usr_name,populate_db.passwd);
		    
		    
		    HashSet<String> column_list = new HashSet<String>();
		    
		    for(int i = 0; i<tableList.size(); i++)
		    {
		    	String schema_query = "SELECT column_name FROM information_schema.columns WHERE table_name = '"+ tableList.get(i) + "'";
			    
			    pst = c.prepareStatement(schema_query);
			    
			    rs = pst.executeQuery();
			    
			    while(rs.next())
			    {
			    	column_list.add(rs.getString(1));
			    }
			    
			    if(column_list.contains("citation_view"))
			    	column_list.remove("citation_view");
		    }
		    
		    selectitems.clear();
		    
		    selectitems.addAll(column_list);
//		    selectitems.addAll(c)
		}
	}
	
//	public static Subgoal gen_subgoal(String table_name, List<String> columnList, List<String> values) throws ClassNotFoundException, SQLException
//	{
//		Connection c = null;
//		
//	    ResultSet rs = null;
//	    
//	    PreparedStatement pst = null;
//	    
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
//	    
//	    String schema_query = "SELECT column_name FROM information_schema.columns WHERE table_name = '"+ table_name + "'";
//	    
//	    pst = c.prepareStatement(schema_query);
//	    
//	    rs = pst.executeQuery();
//	    
//	    Subgoal subgoal = null;
//	    
//	    Vector<Argument> args = new Vector<Argument>();
//	    
//	    int size = rs.getFetchSize();
//	    	    
//	    int num = 0;
//	    
//	    while(rs.next())
//	    {
//	    	
//	    	String column_name = rs.getString(1);
//	    	
//	    	if(columnList.contains(column_name))
//	    	{
//	    		args.add(new Argument("'"+ values.get(columnList.indexOf(column_name)) + "'", column_name));
//	    	}
//	    	
//	    	else
//	    		args.add(new Argument(column_name));
//	    	
//	    	num ++;
//	    	
//	    }
//	    
//	    args.remove(num - 1);
//	    
//	    subgoal = new Subgoal(table_name, args);
//	    
//	    return subgoal;
//	    
//	}
//	
	
	static String gen_condition_citation_query(Query query)
	{
		String where = new String();
		
		for(int i = 0; i<query.conditions.size(); i++)
		{
			if(i == 0)
			{
				where = " where ";
			}
			if(i >= 1)
			{
				where = " and ";
			}
			
			Conditions c = query.conditions.get(i);
			
			if(c.subgoal2 == null)
			{				
//				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += c.subgoal1 + "." + c.arg1.origin_name + query.conditions.get(i).op + c.arg2 ;
			}
			else
			{
				where += c.subgoal1 + "." + c.arg1.origin_name + query.conditions.get(i).op + c.subgoal2 + "." + c.arg2.origin_name ;
			}
			
		}
		
		return where;
	}
	
	static String[] gen_condition(Query query, HashMap<String, Vector<String[]>>arg_name_map)
	{
		String where = new String();
		
		String citation_table = new String();
		
		int num = 0;
						
		boolean condition = false;
		
		HashSet<String> table_set = new HashSet<String>();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			String curr_table_name = new String();
			
			if(i >= 1)
				citation_table += ",";
			
//			if(!view)
			{
				if(!table_set.contains(subgoal.name))
				{
					citation_table += subgoal.name;
					
					curr_table_name = subgoal.name;
					
					table_set.add(subgoal.name);
					
					Vector<String> t_str = new Vector<String>();
					
					t_str.add(curr_table_name);
					
				}
				else
				{
					citation_table += subgoal.name + " " + subgoal.name + i;
										
					curr_table_name = subgoal.name + i;
					
				}
			}
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
//				String[] str_list = {arg.name, subgoal.name};
				
				if(arg.type == 1)
				{
					
//					if(!condition)
//					{
//						where += " where ";
//						condition = true;
//					}
//					
//					if(num >= 1)
//						where += " and "; 
//					
//					if(!view)
//						where += subgoal.name + "." +arg.origin_name  + "=" + arg.name;
//					else
//						where += subgoal.name + "_table" + "." +arg.origin_name  + "=" + arg.name;
					
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
												
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
												
					}
					
					num++;
				}
				
				else
				{
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[]str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
						
						int vec_size = table_names.size();
						
						if(!condition)
						{
//							where += " where ";
							condition = true;
						}
						
//						if(num >= 1)
//							where += " and "; 
						
//						if(!view)
//							where += table_names.get(vec_size - 1)[0] + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "." + arg.origin_name;
//						else
//							where += table_names.get(vec_size - 1)[0] + "_table" + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "_table" + "." + arg.origin_name;
						
						String [] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
						
						num++;
						
					}
					
				}
			}
			
		}
		
		for(int i = 0; i<query.conditions.size(); i++)
		{
			
			if(!condition)
			{
				where += " where ";
				condition = true;
			}
			
			if(i >= 1)
				where += " and "; 
			
			if(query.conditions.get(i).subgoal2 == null)
			{
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
//				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + query.conditions.get(i).arg2 ;
			}
			else
			{
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + table2 + "." + query.conditions.get(i).arg2.origin_name;
			}
		}
		
		
		String []str_l = {where, citation_table};
		
		return str_l;
	}
	
	static String[] gen_condition(Query query, HashMap<String, Vector<String[]>>arg_name_map, HashMap<String, Vector<String>> table_name_map, boolean view, HashMap<String, Integer> table_seq_map)
	{
		String where = new String();
		
		String citation_table = new String();
		
		int num = 0;
		
		String citation_str = new String();
		
		String citation_provenance = new String();
		
		boolean condition = false;
		
		HashSet<String> table_set = new HashSet<String>();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			String curr_table_name = new String();
			
			if(i >= 1)
				citation_table += ",";
			
			if(!view)
			{
				if(!table_set.contains(subgoal.name))
				{
					citation_table += subgoal.name;
					
					curr_table_name = subgoal.name;
					
					table_set.add(subgoal.name);
					
					Vector<String> t_str = new Vector<String>();
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
				else
				{
					citation_table += subgoal.name + " " + subgoal.name + i;
					
					Vector<String> t_str = table_name_map.get(subgoal.name);
					
					curr_table_name = subgoal.name + i;
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
			}
			else
			{
				if(!table_set.contains(subgoal.name))
				{
					citation_table += subgoal.name + "_table";
					
					curr_table_name = subgoal.name + "_table";
					
					table_set.add(subgoal.name);
					
					Vector<String> t_str = new Vector<String>();
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
				else
				{
					citation_table += subgoal.name + "_table" + " " + subgoal.name + "_table" + i;
					
					curr_table_name = subgoal.name + "_table" + i;
					
					Vector<String> t_str = table_name_map.get(subgoal.name);
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
			}
			
									
			if(i >= 1)
			{
				citation_str += ",";
//				citation_provenance += ",";
			}
					
			citation_str += curr_table_name + "." + "citation_view";
//			citation_provenance += curr_table_name + ".provenance";
			
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
//				String[] str_list = {arg.name, subgoal.name};
				
				if(arg.type == 1)
				{
					
//					if(!condition)
//					{
//						where += " where ";
//						condition = true;
//					}
//					
//					if(num >= 1)
//						where += " and "; 
//					
//					if(!view)
//						where += subgoal.name + "." +arg.origin_name  + "=" + arg.name;
//					else
//						where += subgoal.name + "_table" + "." +arg.origin_name  + "=" + arg.name;
					
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
												
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
												
					}
					
					num++;
				}
				
				else
				{
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[]str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
						
						int vec_size = table_names.size();
						
						if(!condition)
						{
//							where += " where ";
							condition = true;
						}
						
//						if(num >= 1)
//							where += " and "; 
						
//						if(!view)
//							where += table_names.get(vec_size - 1)[0] + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "." + arg.origin_name;
//						else
//							where += table_names.get(vec_size - 1)[0] + "_table" + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "_table" + "." + arg.origin_name;
						
						String [] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
						
						num++;
						
					}
					
				}
			}
			
			table_seq_map.put(curr_table_name, i);
		}
		
		for(int i = 0; i<query.conditions.size(); i++)
		{
			
			if(!condition)
			{
				where += " where ";
				condition = true;
			}
			
			if(i >= 1)
				where += " and "; 
			
			if(query.conditions.get(i).subgoal2 == null || query.conditions.get(i).subgoal2.isEmpty())
			{
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
//				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + query.conditions.get(i).arg2 ;
			}
			else
			{
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + table2 + "." + query.conditions.get(i).arg2.origin_name;
			}
		}
		
		
		String []str_l = {where, citation_table,citation_str, citation_provenance};
		
		return str_l;
	}
	
	static Vector<String> get_primary_keys(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT a.attname"
	    		+ " FROM   pg_index i"
	    		+ " JOIN   pg_attribute a ON a.attrelid = i.indrelid"
	    		+ " AND a.attnum = ANY(i.indkey)"
	    		+ " WHERE  i.indrelid = '"+ table_name + "'::regclass"
	    		+ " AND    i.indisprimary";
    	
//    	pst = c.prepareStatement(query);
    	
    	Statement stmt = c.createStatement(
    		    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE
    		);
    	
    	ResultSet rs = stmt.executeQuery(query);
    	
    	Vector<String> primary_keys = new Vector<String>();
    	
    	while(rs.next())
    	{
    		primary_keys.add(rs.getString(1));
    	}
    	
    	return primary_keys;
	}
	
	static String[] gen_condition(Query query, HashMap<String, Vector<String[]>>arg_name_map, HashMap<String, Vector<String>> table_name_map, boolean view, HashMap<String, Integer> table_seq_map, Vector<Integer> valid_ids, Connection c, PreparedStatement pst) throws SQLException
	{
		String where = new String();
		
		String citation_table = new String();
		
		int num = 0;
		
		String citation_str = new String();
		
		String citation_provenance = new String();
		
		boolean condition = false;
		
		HashSet<String> table_set = new HashSet<String>();
		
		Vector<String> primary_keys = new Vector<String>();
		
		String citation_condition = new String();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			String curr_table_name = new String();
			
			if(i >= 1)
			{
				citation_table += ",";
				
				citation_condition += " and ";
			}
			
			if(!view)
			{
				if(!table_set.contains(subgoal.name))
				{
					primary_keys = get_primary_keys(subgoal.name, c, pst);
					
					citation_table += subgoal.name + ",";
					
					citation_table += subgoal.name + populate_db.suffix;
					
					curr_table_name = subgoal.name;
					
					table_set.add(subgoal.name);
					
					Vector<String> t_str = new Vector<String>();
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
					
				}
				else
				{
					citation_table += subgoal.name + " " + subgoal.name + i + ",";
					
					citation_table += subgoal.name + populate_db.suffix + " " + subgoal.name + i + populate_db.suffix; 
					
					Vector<String> t_str = table_name_map.get(subgoal.name);
					
					curr_table_name = subgoal.name + i;
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
				
				for(int k = 0; k<primary_keys.size(); k++)
				{
					if(k >= 1)
						citation_condition += " and ";
					
					citation_condition += subgoal.name + "." + primary_keys.get(k) + " = " + subgoal.name + populate_db.suffix + "." + primary_keys.get(k); 
				}
			}
			else
			{
				if(!table_set.contains(subgoal.name))
				{
					citation_table += subgoal.name + "_table";
					
					curr_table_name = subgoal.name + "_table";
					
					table_set.add(subgoal.name);
					
					Vector<String> t_str = new Vector<String>();
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
				else
				{
					citation_table += subgoal.name + "_table" + " " + subgoal.name + "_table" + i;
					
					curr_table_name = subgoal.name + "_table" + i;
					
					Vector<String> t_str = table_name_map.get(subgoal.name);
					
					t_str.add(curr_table_name);
					
					table_name_map.put(subgoal.name, t_str);
				}
			}
			
			
			if(valid_ids.contains(i))
			{
				if(num >= 1)
				{
					citation_str += ",";
//					citation_provenance += ",";
				}
						
				citation_str += curr_table_name + populate_db.suffix + "." + "citation_view";
				
				num++;
			}
			
			
//			citation_provenance += curr_table_name + ".provenance";
			System.out.println(subgoal);
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
//				String[] str_list = {arg.name, subgoal.name};
				
				if(arg.type == 1)
				{
					
//					if(!condition)
//					{
//						where += " where ";
//						condition = true;
//					}
//					
//					if(num >= 1)
//						where += " and "; 
//					
//					if(!view)
//						where += subgoal.name + "." +arg.origin_name  + "=" + arg.name;
//					else
//						where += subgoal.name + "_table" + "." +arg.origin_name  + "=" + arg.name;
					
					if(arg_name_map.get(arg.name)==null)
					{
						
						System.out.println("::" + arg.name);
						
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
												
						String[] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
												
					}
					
					num++;
				}
				
				else
				{
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[]str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
						
						int vec_size = table_names.size();
						
						if(!condition)
						{
//							where += " where ";
							condition = true;
						}
						
//						if(num >= 1)
//							where += " and "; 
						
//						if(!view)
//							where += table_names.get(vec_size - 1)[0] + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "." + arg.origin_name;
//						else
//							where += table_names.get(vec_size - 1)[0] + "_table" + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "_table" + "." + arg.origin_name;
						
						String [] str_list = {curr_table_name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
						
						num++;
						
					}
					
				}
			}
			
			table_seq_map.put(curr_table_name, i);
		}
		
		for(int i = 0; i<query.conditions.size(); i++)
		{
			
			if(!condition)
			{
				where += " where ";
				condition = true;
			}
			
			if(i >= 1)
				where += " and "; 
			
			if(query.conditions.get(i).subgoal2 == null || query.conditions.get(i).subgoal2.isEmpty())
			{
				
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
//				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + query.conditions.get(i).arg2 ;
			}
			else
			{
				String table1 = arg_name_map.get(query.conditions.get(i).arg1.name).get(0)[0];
				
				String table2 = arg_name_map.get(query.conditions.get(i).arg2.name).get(0)[0];
				
				where += table1 + "." + query.conditions.get(i).arg1.origin_name + query.conditions.get(i).op + table2 + "." + query.conditions.get(i).arg2.origin_name;
			}
		}
		
		
		String []str_l = {where, citation_table, citation_str, citation_condition, citation_provenance};
		
		return str_l;
	}
	
//	public static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
//	{
//		
////		String citation_unit = new String();
//		
//		String sel_item = new String();
//		
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
//	        "postgres","123");
//		
//		String sql = new String();				
//				
//		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
//		
//		HashMap<String, Vector<String>> table_name_map = new HashMap<String, Vector<String>>();
//		
//		HashMap<String, Integer> table_seq_map = new HashMap<String, Integer>();
//				
//		String[] str_l = gen_condition(query, arg_name_map, table_name_map, false, table_seq_map);
//		
//		String where = str_l[0];
//		
//		String citation_table = str_l[1];
//		
//		
//		for(int i = 0; i<query.head.args.size(); i++)
//		{
//			Argument arg = (Argument)query.head.args.get(i);
//			
//			if(i >= 1)
//				sel_item += ",";
//			
//			Vector<String[]> table_names = arg_name_map.get(arg.name);
//			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
////			if(arg.type == 0)
////				sel_item += arg;
////			else
////			{
////				sel_item += arg.origin_name;
////			}
//				
//		}
//		
//		sql = "select " + sel_item + " from " + citation_table + where;
//		
//		c.close();
//		
//		return sql;
//	}
//	
//	public static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
//	{
//		String sel_item = new String();
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection(populate_db.db_url,
//	    	        populate_db.usr_name,populate_db.passwd);
//		
//		String sql = new String();
//		
//		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
//						
//		String[] str_l = gen_condition(query, arg_name_map);
//		
//		String where = str_l[0];
//		
//		String citation_table = str_l[1];
//				
////		String citation_provenance = str_l[3];
//		
//		for(int i = 0; i<query.head.args.size(); i++)
//		{
//			Argument arg = (Argument)query.head.args.get(i);
//			
//			if(i >= 1)
//				sel_item += ",";
//			
//			Vector<String[]> table_names = arg_name_map.get(arg.name);
//			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
//				
//		}
//		
//		sql = "select distinct " + sel_item + " from " + citation_table + where ;//+ group_by_web_view;
//		
//		c.close();
//		
//		return sql;
//	}
//	
	public static String datalog2sql_citation_query(Query query) throws SQLException, ClassNotFoundException
	{		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String sql = new String();
		
		String sel_item = get_sel_item(query);	
		
//		String citation_unit = get_sel_citation_unit(query);
		
//		String sel_lambda_terms = get_lambda_str(query, lambda_terms);
		
//		String [] condition_str = get_condition_boolean_value(query, query.conditions);
		
//		System.out.println("c1:::" + condition_str[0]);
//		
//		System.out.println("c2::::"+ condition_str[1]);
		
		String citation_table = get_relations_without_citation_table(query);
		
		String condition = get_condition(query);
		
//		String citation_condition = get_citation_condition(query, c, pst);
		
		sql = "select distinct " + sel_item;
		
//		if(sel_lambda_terms != null && !sel_lambda_terms.isEmpty())
//			sql += "," + sel_lambda_terms;	
//		if(condition_str[0] != null && !condition_str[0].isEmpty())
//			sql += "," + condition_str[0];
		
		sql += " from " + citation_table;// + " where " +  citation_condition;
		
		if(condition != null && !condition.isEmpty())
			sql += " where " + condition;
		
//		if(condition_str[1] != null && !condition_str[1].isEmpty())
//			sql += " order by " + condition_str[1]; 
		
//		System.out.println("sql:::" + sql);
		
		c.close();
		
		return sql;
	}
	
	
	public static String datalog2sql_full(Query query) throws SQLException, ClassNotFoundException
	{
		
//		String citation_unit = new String();
		
		String sel_item = new String();
		
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String sql = new String();				
				
		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
		
		HashMap<String, Vector<String>> table_name_map = new HashMap<String, Vector<String>>();
		
		HashMap<String, Integer> table_seq_map = new HashMap<String, Integer>();
				
		String[] str_l = gen_condition(query, arg_name_map, table_name_map, false, table_seq_map);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
		
		
		Set set = arg_name_map.keySet();
		
		int num = 0;
		
		for(Iterator iter = set.iterator();iter.hasNext();)
		{
			String key = (String) iter.next();
			
			
			if(num >= 1)
				sel_item += ",";
			Vector<String[]> table_names = arg_name_map.get(key);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
			
			num ++;
		}
		
//		for(int i = 0; i<query.head.args.size(); i++)
//		{
//			Argument arg = (Argument)query.head.args.get(i);
//			
//			
//			
//			Vector<String[]> table_names = arg_name_map.get(arg.name);
//			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
////			if(arg.type == 0)
////				sel_item += arg;
////			else
////			{
////				sel_item += arg.origin_name;
////			}
//				
//		}
		
		sql = "select " + sel_item + " from " + citation_table + where;
		
		c.close();
		
		return sql;
	}
		
	
	static String get_sel_item(Query q)
	{
		String str = new String();
		
//		System.out.println("head::" + q.head);
		
		for(int i = 0; i<q.head.size(); i++)
		{
			Argument arg = (Argument) q.head.args.get(i);
			
			if(i >= 1)
				str += ",";
			
			str += arg.relation_name + "." + arg.name.substring(arg.name.indexOf("_") + 1, arg.name.length());
			
		}
		return str;
	}
	
	static String get_renamed_sel_item(Query q)
	{
		String str = new String();
		
//		System.out.println("head::" + q.head);
		
		for(int i = 0; i<q.head.size(); i++)
		{
			Argument arg = (Argument) q.head.args.get(i);
			
			if(i >= 1)
				str += ",";
			
			str += arg.relation_name + "." + arg.name.substring(arg.name.indexOf("_") + 1, arg.name.length()) + " as " + arg.name;
			
		}
		return str;
	}
	
	static String get_group_item(Query q)
	{
		String str = new String();
		
//		System.out.println("head::" + q.head);
		
		for(int i = 0; i<q.head.size(); i++)
		{
			Argument arg = (Argument) q.head.args.get(i);
			
			if(i >= 1)
				str += ",";
			
			str += arg.name;
			
		}
		return str;
	}
	
	static String get_citation_query_sel_item(Query q)
	{
		String str = new String();
		
//		System.out.println("head::" + q.head);
		
		
		for(int i = 0; i<q.head.size(); i++)
		{
			Argument arg = (Argument) q.head.args.get(i);
			
			if(i >= 1)
				str += ",";
			
			if(arg.name.contains("first_names"))
			{
				str += arg.relation_name + "." + arg.name.substring(arg.name.indexOf("_") + 1, arg.name.length()) + " as first_names"; 
			}
			
			if(arg.name.contains("surname"))
			{
				str += arg.relation_name + "." + arg.name.substring(arg.name.indexOf("_") + 1, arg.name.length()) + " as surname"; 
			}
			
			
		}
		return str;
	}
	
	static String get_sel_citation_unit(Query q)
	{
		String str = new String ();
		
		for(int i = 0; i<q.body.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			Subgoal subgoal = (Subgoal)q.body.get(i);
			
			str += subgoal.name + populate_db.suffix + ".citation_view";
			
		}
		
		return str;
	}
	
	static String get_relations_with_citation_table(Query q)
	{
		String str = new String();
		
		for(int i = 0; i<q.body.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			str += q.subgoal_name_mapping.get(subgoal.name) + " " + subgoal.name;
			
			str += "," + q.subgoal_name_mapping.get(subgoal.name) + populate_db.suffix + " " + subgoal.name + populate_db.suffix; 
		}
		
		return str;
		
	}
	
	static String get_relations_without_citation_table(Query q)
	{
		String str = new String();
		
		for(int i = 0; i<q.body.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			str += q.subgoal_name_mapping.get(subgoal.name) + " " + subgoal.name;
			
//			str += "," + q.subgoal_name_mapping.get(subgoal.name) + populate_db.suffix + " " + subgoal.name + populate_db.suffix; 
		}
		
		return str;
		
	}
	
	static String get_relations_without_citation_table(Query q, Query citation_query)
	{
		String str = new String();
		
//		int num = 0;
		
		for(int i = 0; i<citation_query.body.size(); i++)
		{
			
			
			Subgoal subgoal = (Subgoal) citation_query.body.get(i);
			
			if(i >= 1)
				str += ",";
			
			if(q.subgoal_name_mapping.get(subgoal.name) == null)
			{
				
				
				
				str += citation_query.subgoal_name_mapping.get(subgoal.name) + " " + subgoal.name;
				
			}
			else
			{
				
				int rand_num =  gen_random_number(q.body.size());
				
				while(q.subgoal_name_mapping.get(subgoal.name + rand_num) != null)
				{
					rand_num =  gen_random_number(q.body.size());
				}
				
				str += citation_query.subgoal_name_mapping.get(subgoal.name) + " " + subgoal.name + rand_num;
				
				update_conditions(citation_query, subgoal.name, subgoal.name + rand_num);
				
				update_head_vars(citation_query, subgoal.name, subgoal.name + rand_num);
				
				update_lambda_terms(citation_query, subgoal.name, subgoal.name + rand_num);
				
				update_subgoal_name(citation_query, subgoal.name, subgoal.name + rand_num);

			}
			
			
			
			
//			str += "," + q.subgoal_name_mapping.get(subgoal.name) + populate_db.suffix + " " + subgoal.name + populate_db.suffix; 
		}
		
		return str;
		
	}
	
	static int gen_random_number(int subgoal_num)
	{
		
		Random rand = new Random();
		
		return rand.nextInt(subgoal_num);
	}
	
	static void update_subgoal_name(Query citation_query, String old_name, String new_name)
	{
		String mapping_subgoal_name = citation_query.subgoal_name_mapping.get(old_name);
		
		citation_query.subgoal_name_mapping.remove(old_name);
		
		citation_query.subgoal_name_mapping.put(new_name, mapping_subgoal_name);
		
		for(int i = 0; i<citation_query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) citation_query.body.get(i);
			
			if(subgoal.name.equals(old_name))
			{
				subgoal.name = new_name;
				
				citation_query.body.set(i, subgoal);
				
				break;
			}
			
		}
		
		
	}
	
	static void update_head_vars(Query citation_query, String old_name, String new_name)
	{
		for(int i = 0; i<citation_query.head.size(); i++)
		{
			Argument arg = (Argument)citation_query.head.args.get(i);
			
			if(arg.relation_name.equals(old_name))
			{
				arg.relation_name = new_name;
			}
			
			citation_query.head.args.set(i, arg);
			
		}
	}
	
	static void update_conditions(Query citation_query, String old_name, String new_name)
	{
		for(int i = 0; i<citation_query.conditions.size(); i++)
		{
			Conditions condition = citation_query.conditions.get(i);
			
			if(condition.subgoal1.equals(old_name))
			{
				condition.subgoal1 = new_name;
			}
			
			if(condition.subgoal2.equals(old_name))
			{
				condition.subgoal2 = new_name;
			}
			
			citation_query.conditions.set(i, condition);
		}
	}
	
	static void update_lambda_terms(Query citation_query, String old_name, String new_name)
	{
		for(int i = 0; i<citation_query.lambda_term.size(); i++)
		{
			Lambda_term l = citation_query.lambda_term.get(i);
			
			if(l.table_name.equals(old_name))
			{
				l.update_table_name(new_name);
			}
			
			citation_query.lambda_term.set(i, l);
		}
	}
	
	static String get_single_condition_str(Conditions condition)
	{
		String str = new String();
		
		str += condition.subgoal1 + "." + condition.arg1 + condition.op;
		
		if(condition.arg2.isConst())
		{
			
			String arg2 = condition.arg2.toString();
			
			if(arg2.length() > 2)
			{
				arg2 = arg2.substring(1, arg2.length() - 1).replaceAll("'", "''");
				str += "'" + arg2 + "'";
			}
			else
			{
				str += arg2;
			}
						
			
			
			
		}
		else
		{
			str += condition.subgoal2 + "." + condition.arg2;
		}
		
		return str;
	}
	
	static String get_single_condition_str_neg(Conditions condition)
	{
		String str = new String();
		
		str += condition.subgoal1 + "." + condition.arg1 + condition.op.negation();
		
		if(condition.subgoal2 == null || condition.subgoal2.isEmpty())
		{
			str += condition.arg2;
		}
		else
		{
			str += condition.subgoal2 + "." + condition.arg2;
		}
		
		return str;
	}
	
	static String get_condition(Query q)
	{
		String str = new String();
		
		for(int i = 0; i<q.conditions.size(); i++)
		{
			if(i >= 1)
				str += " and ";
			
//			str += q.conditions.get(i).subgoal1 + "." + q.conditions.get(i).arg1 + q.conditions.get(i).op;
//			
//			if(q.conditions.get(i).subgoal2 == null || q.conditions.get(i).subgoal2.isEmpty())
//			{
//				str += q.conditions.get(i).arg2;
//			}
//			else
//			{
//				str += q.conditions.get(i).subgoal2 + "." + q.conditions.get(i).arg2;
//			}
			
			str += get_single_condition_str(q.conditions.get(i));
		}
		
		return str;
	}
	
	static String get_citation_condition(Query q, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException
	{
		String str = new String();
		
		int num = 0;
		
		for(int i = 0; i<q.body.size(); i++)
		{
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			Vector<String> primary_keys = populate_db.get_primary_key(q.subgoal_name_mapping.get(subgoal.name), c, pst);
			
			for(int k = 0; k<primary_keys.size(); k++)
			{
				
				if(num >= 1)
					str += " and ";
				
				str += subgoal.name + "." + primary_keys.get(k) + " = " + subgoal.name + populate_db.suffix + "." + primary_keys.get(k);
				
				num ++;
			}
			
		}
		
		return str;
	}
	
	static String[] get_condition_boolean_value(Query q, HashSet<Conditions> valid_conditions)
	{
		
		String [] str = new String[2];
		
		str[0] = new String();
		
		str[1] = new String();
		
		int condition_num = 0;
		
//		for(int i = 0; i<valid_conditions.size(); i++)
		int i = 0;
		
		for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
		{
			Conditions condition = (Conditions) iter.next();
			
			if(i >= 1)
			{
				str[0] += ",";
				str[1] += ",";
			}
			
			if(condition.subgoal2 == null || condition.subgoal2.isEmpty())
				str[0] += "(" + condition.subgoal1 + "." + condition.arg1 + condition.op + condition.arg2 + ") as condition" + condition_num;
			else
				str[0] += "(" + condition.subgoal1 + "." + condition.arg1 + condition.op + condition.subgoal2 + "." + condition.arg2 + ") as condition" + condition_num;
			
			str[1] += "condition" + condition_num + " desc";
			
			condition_num ++;

			i++;
		}
		
		return str;
	}
	
	static String get_lambda_str(Query v, Vector<Lambda_term> lambda_terms)
	{
		String str = new String();
		
		for(int i = 0; i<lambda_terms.size(); i++)
		{
			if(i >= 1)
				str += ",";
			
			String name = lambda_terms.get(i).name;
			
			str += lambda_terms.get(i).table_name + "." + name.substring(name.indexOf("_") + 1, name.length());
		}
		
		return str;
	}
	
	public static String datalog2sql_citation(Query query, Vector<Lambda_term> lambda_terms, HashSet<Conditions> valid_conditions) throws SQLException, ClassNotFoundException
	{
				
//		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    
		
		String sql = new String();

		String sel_item = get_sel_item(query);
		
		String citation_unit = get_sel_citation_unit(query);
		
		String sel_lambda_terms = get_lambda_str(query, lambda_terms);
		
		String [] condition_str = get_condition_boolean_value(query, valid_conditions);
		
		
		String citation_table = get_relations_with_citation_table(query);
		
		String condition = get_condition(query);
		
		String citation_condition = get_citation_condition(query, c, pst);
		
		sql = "select " + sel_item + "," + citation_unit;
		
		if(sel_lambda_terms != null && !sel_lambda_terms.isEmpty())
			sql += "," + sel_lambda_terms;
		
		if(condition_str[0] != null && !condition_str[0].isEmpty())
			sql += "," + condition_str[0];
		
		sql += " from " + citation_table + " where " +  citation_condition;
		
		if(condition != null && !condition.isEmpty())
			sql += " and " + condition;
		
		if(condition_str[1] != null && !condition_str[1].isEmpty())
			sql += " order by " + condition_str[1]; 
		
//		System.out.println("sql:::" + sql);
		
		c.close();
		
		return sql;
	}
	
	public static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
	{
				
//		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    
		
		String sql = new String();

		String sel_item = get_sel_item(query);
			
		String citation_table = get_relations_without_citation_table(query);
		
		String condition = get_condition(query);
				
		sql = "select " + sel_item;
		
		sql += " from " + citation_table + " where ";
		
		if(condition != null && !condition.isEmpty())
			sql += condition;
		
		
//		System.out.println("sql:::" + sql);
		
		c.close();
		
		return sql;
	}
	
	
	public static String datalog2sql_citation2(Query query, HashSet<Conditions> valid_conditions, Vector<Lambda_term> lambda_terms) throws SQLException, ClassNotFoundException
	{
				
		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String sql = new String();
		
//		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
//		
//		HashMap<String, Vector<String>> table_name_map = new HashMap<String, Vector<String>>();
//		
//		HashMap<String, Integer> table_seq_map = new HashMap<String, Integer>();
//		
//		String[] str_l = gen_condition(query, arg_name_map, table_name_map, view, table_seq_map);
//		
//		String where = str_l[0];
//		
//		String citation_table = str_l[1];
//		
//		String citation_unit = str_l[2];
//		
////		String citation_provenance = str_l[3];
//		
//		for(int i = 0; i<query.head.args.size(); i++)
//		{
//			Argument arg = (Argument)query.head.args.get(i);
//			
//			if(i >= 1)
//				sel_item += ",";
//			
//			Vector<String[]> table_names = arg_name_map.get(arg.name);
//			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
//				
//		}
//		
//		String conditions = new String();
//		
//		String condition_order = new String();
//		
//		int condition_num = 0;
//		
//		for(int i = 0; i<valid_conditions.size(); i++)
//		{
//			Vector<String> table1 = table_name_map.get(valid_conditions.get(i).subgoal1);
//			
//			Vector<String> table2 = table_name_map.get(valid_conditions.get(i).subgoal2);
//			
//			Argument a1 = valid_conditions.get(i).arg1;
//			
//			Argument a2 = valid_conditions.get(i).arg2;
//			
//			String op = valid_conditions.get(i).op.toString();
//			
//			if(valid_conditions.get(i).subgoal1 != valid_conditions.get(i).subgoal2)
//			{
//				for(int j = 0; j<table1.size(); j++)
//				{
//					
//					String t1 = table1.get(j);
//					
//					for(int k = 0; k<table2.size(); k++)
//					{
//						String t2 = table2.get(k);
//						
//						condition_seq.add(valid_conditions.get(i));
//						
//						int [] ids = new int[2];
//						
//						ids[0] = table_seq_map.get(t1);
//												
//						if(t2 != null && !t2.isEmpty())
//						{
//							ids[1] = table_seq_map.get(t2);
////							condition_seq.lastElement().id2 
//						}
//						else
//							ids[1] = -1;
//						condition_seq_id.add(ids);
//						
//						
//						if(a2.isConst())
//							conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
//						else
//							conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
//						
//						if(condition_num >= 1)
//							condition_order += ",";
//						
//						condition_order += "condition" + condition_num + " desc";
//						
//						condition_num ++;
//						
//					}
//				}
//			}
//			
//			else
//			{
//				for(int j = 0; j<table1.size(); j++)
//				{
//					
//					String t1 = table1.get(j);
//					
//					String t2 = table2.get(j);
//					
//					condition_seq.add(valid_conditions.get(i));
//					
//					int [] ids = new int[2];
//					
//					ids[0] = table_seq_map.get(t1);
//											
//					if(t2 != null && !t2.isEmpty())
//					{
//						ids[1] = table_seq_map.get(t2);
////						condition_seq.lastElement().id2 
//					}
//					else
//						ids[1] = -1;
//					condition_seq_id.add(ids);
//					
//					if(a2.isConst())
//						conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
//					else
//						conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
//					
//					if(condition_num >= 1)
//						condition_order += ",";
//					
//					condition_order += "condition" + condition_num + " desc";
//					
//					condition_num ++;
//					
//					
//				}
//			}
//			
//			
//		}
//		
//		
//		String sel_lambda_terms = new String();
//		
//		
//		Set table_set = return_vals.keySet();
//		
////		HashMap<String, HashSet<String>> return_vals
//		
//		int pos = 0;
//		
////		String group_by_web_view = new String();
//				
//		HashMap<String, Integer> table_name_num = new HashMap<String, Integer>();
//				
//		for(int p = 0; p<query.body.size(); p++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(p);
//			
//			String curr_table_name = subgoal.name;
//			
//			int times = 0;
//			
//			if(table_name_num.get(curr_table_name) != null)
//			{
//				times = table_name_num.get(curr_table_name);//table_name_num.put(curr_table_name, times);
//				
//				times ++;
//			}
//			
//
//			
//			table_name_num.put(curr_table_name, times);
//			
//			String alias_name = table_name_map.get(curr_table_name).get(times);
//						
////			if(p >= 1)
////				group_by_web_view += ",";
////			
////			group_by_web_view += alias_name + ".web_view_vec";
//						
//			HashSet<String> lambda_term_names = return_vals.get(curr_table_name);
//			
//			start_pos.add(pos);
//
//				if(lambda_term_names != null)
//				for(Iterator it = lambda_term_names.iterator(); it.hasNext();)
//				{
//					String lambda_term = (String)it.next();
//					
//					sel_lambda_terms += "," + alias_name + "." + lambda_term;
//					
//					pos++;
//					
//					lambda_term_num[0] ++;
//					
//				}
//				
//				
////			}
//			
//		}
//		
//		
//		if(condition_num >= 1)
//		{
//			condition_order = " order by " + condition_order;
//			
////			if(!group_by_web_view.isEmpty())
////			{
////				group_by_web_view = "," + group_by_web_view;
////			}
////		}
////		else
////		{
////			if(!group_by_web_view.isEmpty())
////			{
////				group_by_web_view = " order by " + group_by_web_view;
////			}
//			
//		}
//		
//		
//		sql = "select " + sel_item + sel_lambda_terms + conditions +  " from " + citation_table + where + condition_order;
//		
//		c.close();
//		
//		return sql;
		sel_item = get_sel_item(query);
		
//		String citation_unit = get_sel_citation_unit(query);
		
		String sel_lambda_terms = get_lambda_str(query, lambda_terms);
		
		String [] condition_str = get_condition_boolean_value(query, valid_conditions);
		
		
		String citation_table = get_relations_without_citation_table(query);
		
		String condition = get_condition(query);
		
//		String citation_condition = get_citation_condition(query, c, pst);
		
		sql = "select " + sel_item;
		
		if(sel_lambda_terms != null && !sel_lambda_terms.isEmpty())
			sql += "," + sel_lambda_terms;
		
		if(condition_str[0] != null && !condition_str[0].isEmpty())
			sql += "," + condition_str[0];
		
		sql += " from " + citation_table;
		
		if(condition != null && !condition.isEmpty())
			sql += " where " + condition;
		
		if(condition_str[1] != null && !condition_str[1].isEmpty())
			sql += " order by " + condition_str[1]; 
		
//		System.out.println("sql:::" + sql);
		
		c.close();
		
		return sql;
		
		
		
	}
	
	public static String datalog2sql_provenance(Query query, boolean view) throws SQLException, ClassNotFoundException
	{
				
		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
		String sql = new String();
		
		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
		
		HashMap<String, Vector<String>> table_name_map = new HashMap<String, Vector<String>>();
		
		HashMap<String, Integer> table_seq_map = new HashMap<String, Integer>();
		
		String[] str_l = gen_condition(query, arg_name_map, table_name_map, view, table_seq_map);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
		
		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		sql = "select " + citation_provenance + "," + sel_item + " from " + citation_table + where;

//		pst.close();
		
		c.close();
		
		return sql;
	}
	

//	static String datalog2sql_citation_view(Query query) throws SQLException, ClassNotFoundException
//	{
//		
//		String citation_unit = new String();
//		
////		String sel_item = new String();
//		
//		String citation_table = new String();
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
//	        "postgres","123");
//		
//		String sql = new String();
//		
//		int num = 0;
//		
//		HashMap<String, String> map = new HashMap<String, String>();
//		
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//						
//			if(num >= 1)
//			{
//				citation_unit += ",";
//				
//				citation_table += " natural inner join ";
//			}
//			
//			citation_unit += subgoal.name + "_table" + "_citation_view";
//						
//			citation_table += subgoal.name + "_table";
//			
//			num++;
//			
//			for(int j = 0; j<subgoal.args.size();j++)
//			{
//				Argument arg = (Argument) subgoal.args.get(j);
//				
//				if(arg.type == 1)
//				{
//					map.put(arg.origin_name, arg.name);
//				}
//			}
//							
//		}
//				
//		String where = new String();
//		
//		if(!map.isEmpty())
//		{
//			Set set = map.keySet();
//			
//			where = " where ";
//			
//			int number = 0;
//			
//			for(Iterator iter = set.iterator();iter.hasNext();)
//			{
//				String key = (String) iter.next();
//				
//				String value = map.get(key);
//				
//				if(number >= 1)
//					where += " and ";
//				
//				where += key + "=" + value;
//				
//				number ++;
//			}
//			
//		}
//		
//		sql = "select " + citation_unit + " from " + citation_table + where;
//
////		pst.close();
//		
//		c.close();
//		
//		return sql;
//	}

	
//	public static void pre_processing(Query query) throws ClassNotFoundException, SQLException
//	{
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//			
//			rename_column(subgoal.name);
//		}
//	}
	
//	public static void post_processing(Query query) throws ClassNotFoundException, SQLException
//	{
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//			
//			rename_column_back(subgoal.name);
//		}
//	}
//	
//	public static void pre_processing_view(Query query) throws ClassNotFoundException, SQLException
//	{
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//			
//			rename_column(subgoal.name + "_table");
//		}
//	}
//	
//	public static void post_processing_view(Query query) throws ClassNotFoundException, SQLException
//	{
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			Subgoal subgoal = (Subgoal) query.body.get(i);
//			
//			rename_column_back(subgoal.name + "_table");
//		}
//	}
	
//	static void rename_column(String view) throws SQLException, ClassNotFoundException
//	{
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
//	        "postgres","123");
//		
//		String rename_query = "alter table "+view +" rename column citation_view to "+ view + "_citation_view";
//		
//		pst = c.prepareStatement(rename_query);
//		
//		pst.execute();
//		
//		c.close();
//	}
//	
//	static void rename_column_back(String view) throws SQLException, ClassNotFoundException
//	{
//		
//		Connection c = null;
//		
//	    PreparedStatement pst = null;
//	      
//		Class.forName("org.postgresql.Driver");
//		
//	    c = DriverManager
//	        .getConnection("jdbc:postgresql://localhost:5432/" + populate_db.db_name,
//	        "postgres","123");
//		
//		String rename_query = "alter table "+view +" rename column "+ view +"_citation_view to citation_view";
//		
//		pst = c.prepareStatement(rename_query);
//		
//		pst.execute();
//		
//		c.close();
//	}
}
