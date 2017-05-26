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
import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Pre_processing.populate_db;
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

public class Query_converter {
	
	public static void main(String [] args) throws JSQLParserException, ClassNotFoundException, SQLException
	{
		
		String str = "Q(f):family(f, n, tx), introduction(f, ty)";
		
		Query query = Parse_datalog.parse_query(str);//sql2datalog("select * from family_c ,introduction_c where type = 'gpcr' and family_id= '2'");
		
		String q = datalog2sql(query);
		
		System.out.println(query.toString());
		
		System.out.println(q);
	}
	
	
	public static Query sql2datalog(String sql) throws JSQLParserException, ClassNotFoundException, SQLException
	{
		String statement= sql; 
		
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        
        Select select=(Select) parserManager.parse(new StringReader(statement));
        
        String query_name = "Q";
        
        Vector<Argument> query_arg = new Vector<Argument>();
        
        Vector<Subgoal> subgoals = new Vector<Subgoal>();
        
        PlainSelect plain=(PlainSelect)select.getSelectBody();    
        
        final List<String> columnList = new ArrayList<String>();
        
        final List<String> values = new ArrayList<String>();
        
        
        gen_conditions(plain, columnList, values);
        
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        
		List<String> tableList = tablesNamesFinder.getTableList(select);
		
        for(int i = 0; i<tableList.size(); i++)
        {
        	subgoals.add(gen_subgoal(tableList.get(i), columnList, values));
        }
        
        gen_select_item(plain, tableList, query_arg,columnList, values);
        
        Subgoal head = new Subgoal(query_name, query_arg);
		
        Query query = new Query(query_name, head, subgoals);
        
        return query;
        
	}
	
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
	
	static void gen_select_item(PlainSelect plain, List<String> tableList, Vector<Argument> query_arg, List<String> columnList, List<String> values) throws ClassNotFoundException, SQLException
	{
        String str = plain.getSelectItems().toString();
        
        int len = str.length();
        
        str = str.substring(1, len - 1);
        
        Vector<String> sel_col = new Vector<String>();
        
        sel_col.addAll(Arrays.asList(str.split(",")));
              
        pre_process(sel_col, tableList);
        
        for(int i=0;i<sel_col.size();i++)
        {
           String item = sel_col.get(i);
        	
           if(columnList.contains(item))
           {
        	   query_arg.add(new Argument("'" + values.get(columnList.indexOf(item)) + "'", item));
           }
           else
        	   query_arg.add(new Argument(item));
           
        }
	}
	
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
	
	public static Subgoal gen_subgoal(String table_name, List<String> columnList, List<String> values) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		
	    ResultSet rs = null;
	    
	    PreparedStatement pst = null;
	    
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    String schema_query = "SELECT column_name FROM information_schema.columns WHERE table_name = '"+ table_name + "'";
	    
	    pst = c.prepareStatement(schema_query);
	    
	    rs = pst.executeQuery();
	    
	    Subgoal subgoal = null;
	    
	    Vector<Argument> args = new Vector<Argument>();
	    
	    int size = rs.getFetchSize();
	    	    
	    int num = 0;
	    
	    while(rs.next())
	    {
	    	
	    	String column_name = rs.getString(1);
	    	
	    	if(columnList.contains(column_name))
	    	{
	    		args.add(new Argument("'"+ values.get(columnList.indexOf(column_name)) + "'", column_name));
	    	}
	    	
	    	else
	    		args.add(new Argument(column_name));
	    	
	    	num ++;
	    	
	    }
	    
	    args.remove(num - 1);
	    
	    subgoal = new Subgoal(table_name, args);
	    
	    return subgoal;
	    
	}
	
	
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
	public static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
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
						
		String[] str_l = gen_condition(query, arg_name_map);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
				
//		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		sql = "select distinct " + sel_item + " from " + citation_table + where ;//+ group_by_web_view;
		
		c.close();
		
		return sql;
	}
	
	public static String datalog2sql_citation_query(Query query) throws SQLException, ClassNotFoundException
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
						
		String where = gen_condition_citation_query(query);
				
		String citation_table = ;
				
//		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
////			Vector<String[]> table_names = arg_name_map.get(arg.name);
//			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		sql = "select distinct " + sel_item + " from " + citation_table + where ;//+ group_by_web_view;
		
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
		
	public static String datalog2sql_citation(Query query, Vector<Conditions> valid_conditions, Vector<Conditions> condition_seq, HashMap<String, HashSet<String>> return_vals, HashMap<String, Vector<Integer>> table_pos_map, boolean view, Vector<int[]> condition_seq_id, Vector<Integer> valid_subgoal_id) throws SQLException, ClassNotFoundException
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
		
		String[] str_l = gen_condition(query, arg_name_map, table_name_map, view, table_seq_map, valid_subgoal_id,c ,pst);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
		
		String citation_unit = str_l[2];
		
		String citation_condition = str_l[3];
		
//		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			
			System.out.println(arg.name);
			
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		String conditions = new String();
		
		String condition_order = new String();
		
		int condition_num = 0;
		
		for(int i = 0; i<valid_conditions.size(); i++)
		{
			Vector<String> table1 = table_name_map.get(valid_conditions.get(i).subgoal1);
			
			Vector<String> table2 = table_name_map.get(valid_conditions.get(i).subgoal2);
			
			Argument a1 = valid_conditions.get(i).arg1;
			
			Argument a2 = valid_conditions.get(i).arg2;
			
			String op = valid_conditions.get(i).op.toString();
			
			if(valid_conditions.get(i).subgoal1 != valid_conditions.get(i).subgoal2)
			{
				for(int j = 0; j<table1.size(); j++)
				{
					
					String t1 = table1.get(j);
					
					for(int k = 0; k<table2.size(); k++)
					{
						String t2 = table2.get(k);
						
						condition_seq.add(valid_conditions.get(i));
						
						int [] ids = new int[2];
						
						ids[0] = table_seq_map.get(t1);
												
						if(t2 != null && !t2.isEmpty())
						{
							ids[1] = table_seq_map.get(t2);
//							condition_seq.lastElement().id2 
						}
						else
							ids[1] = -1;
						condition_seq_id.add(ids);
						
						
						if(a2.isConst())
							conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
						else
							conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
						
						if(condition_num >= 1)
							condition_order += ",";
						
						condition_order += "condition" + condition_num + " desc";
						
						condition_num ++;
						
					}
				}
			}
			
			else
			{
				for(int j = 0; j<table1.size(); j++)
				{
					
					String t1 = table1.get(j);
					
					String t2 = table2.get(j);
					
					condition_seq.add(valid_conditions.get(i));
					
					int [] ids = new int[2];
					
					ids[0] = table_seq_map.get(t1);
											
					if(t2 != null && !t2.isEmpty())
					{
						ids[1] = table_seq_map.get(t2);
//						condition_seq.lastElement().id2 
					}
					else
						ids[1] = -1;
					condition_seq_id.add(ids);
					
					if(a2.isConst())
						conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
					else
						conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
					
					if(condition_num >= 1)
						condition_order += ",";
					
					condition_order += "condition" + condition_num + " desc";
					
					condition_num ++;
					
					
				}
			}
			
			
		}
		
		
		String sel_lambda_terms = new String();

		
		Set table_set = return_vals.keySet();
		
//		HashMap<String, HashSet<String>> return_vals
		
		int pos = 0;
		
		for(Iterator iter = table_set.iterator(); iter.hasNext();)
		{
			String curr_table_name = (String)iter.next();
			
			Vector<String> table_name_alias = table_name_map.get(curr_table_name);
			
			HashSet<String> lambda_term_names = return_vals.get(curr_table_name);
			
			
			
			
			for(int k = 0; k<table_name_alias.size(); k++)
			{
				Vector<Integer> int_list = table_pos_map.get(curr_table_name);
				
				if(int_list == null)
				{
					int_list = new Vector<Integer>();
					
					int_list.add(pos);
					
					table_pos_map.put(curr_table_name, int_list);
				}
				
				else
				{
					int_list.add(pos);
				}
				
				for(Iterator it = lambda_term_names.iterator(); it.hasNext();)
				{
					String lambda_term = (String)it.next();
										
					sel_lambda_terms += "," + table_name_alias.get(k) + "." + lambda_term.substring(lambda_term.indexOf("_") + 1, lambda_term.length());
					
					System.out.println("lambda::" + sel_lambda_terms);
																				
					pos++;
					
				}
				
				
			}
			
		}
		
		
		if(condition_num >= 1)
		{
			condition_order = " order by " + condition_order;
			
//			if(!group_by_web_view.isEmpty())
//			{
//				group_by_web_view = "," + group_by_web_view;
//			}
//		}
//		else
//		{
//			if(!group_by_web_view.isEmpty())
//			{
//				group_by_web_view = " order by " + group_by_web_view;
//			}
			
		}
		
		
		sql = "select " + sel_item + "," + citation_unit + sel_lambda_terms + conditions +  " from " + citation_table + where + " and " + citation_condition + condition_order ;//+ group_by_web_view;
		
		System.out.println(sql);
		
		c.close();
		
		return sql;
	}
	
	public static String datalog2sql_citation2(Query query, Vector<Conditions> valid_conditions, Vector<Conditions> condition_seq, HashMap<String, HashSet<String>> return_vals, Vector<Integer> start_pos, HashMap<String, Integer> table_pos_map, boolean view, Vector<int[]> condition_seq_id, int[]lambda_term_num) throws SQLException, ClassNotFoundException
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
		
		String citation_unit = str_l[2];
		
//		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		String conditions = new String();
		
		String condition_order = new String();
		
		int condition_num = 0;
		
		for(int i = 0; i<valid_conditions.size(); i++)
		{
			Vector<String> table1 = table_name_map.get(valid_conditions.get(i).subgoal1);
			
			Vector<String> table2 = table_name_map.get(valid_conditions.get(i).subgoal2);
			
			Argument a1 = valid_conditions.get(i).arg1;
			
			Argument a2 = valid_conditions.get(i).arg2;
			
			String op = valid_conditions.get(i).op.toString();
			
			if(valid_conditions.get(i).subgoal1 != valid_conditions.get(i).subgoal2)
			{
				for(int j = 0; j<table1.size(); j++)
				{
					
					String t1 = table1.get(j);
					
					for(int k = 0; k<table2.size(); k++)
					{
						String t2 = table2.get(k);
						
						condition_seq.add(valid_conditions.get(i));
						
						int [] ids = new int[2];
						
						ids[0] = table_seq_map.get(t1);
												
						if(t2 != null && !t2.isEmpty())
						{
							ids[1] = table_seq_map.get(t2);
//							condition_seq.lastElement().id2 
						}
						else
							ids[1] = -1;
						condition_seq_id.add(ids);
						
						
						if(a2.isConst())
							conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
						else
							conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
						
						if(condition_num >= 1)
							condition_order += ",";
						
						condition_order += "condition" + condition_num + " desc";
						
						condition_num ++;
						
					}
				}
			}
			
			else
			{
				for(int j = 0; j<table1.size(); j++)
				{
					
					String t1 = table1.get(j);
					
					String t2 = table2.get(j);
					
					condition_seq.add(valid_conditions.get(i));
					
					int [] ids = new int[2];
					
					ids[0] = table_seq_map.get(t1);
											
					if(t2 != null && !t2.isEmpty())
					{
						ids[1] = table_seq_map.get(t2);
//						condition_seq.lastElement().id2 
					}
					else
						ids[1] = -1;
					condition_seq_id.add(ids);
					
					if(a2.isConst())
						conditions += ", (" + t1 + "." + a1.origin_name + op + a2.name + ") as condition" + condition_num;
					else
						conditions += ", (" + t1 + "." + a1.origin_name + op + t2 + "." + a2.origin_name + ") as condition" + condition_num;
					
					if(condition_num >= 1)
						condition_order += ",";
					
					condition_order += "condition" + condition_num + " desc";
					
					condition_num ++;
					
					
				}
			}
			
			
		}
		
		
		String sel_lambda_terms = new String();
		
		
		Set table_set = return_vals.keySet();
		
//		HashMap<String, HashSet<String>> return_vals
		
		int pos = 0;
		
//		String group_by_web_view = new String();
				
		HashMap<String, Integer> table_name_num = new HashMap<String, Integer>();
				
		for(int p = 0; p<query.body.size(); p++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(p);
			
			String curr_table_name = subgoal.name;
			
			int times = 0;
			
			if(table_name_num.get(curr_table_name) != null)
			{
				times = table_name_num.get(curr_table_name);//table_name_num.put(curr_table_name, times);
				
				times ++;
			}
			

			
			table_name_num.put(curr_table_name, times);
			
			String alias_name = table_name_map.get(curr_table_name).get(times);
						
//			if(p >= 1)
//				group_by_web_view += ",";
//			
//			group_by_web_view += alias_name + ".web_view_vec";
						
			HashSet<String> lambda_term_names = return_vals.get(curr_table_name);
			
			start_pos.add(pos);

				if(lambda_term_names != null)
				for(Iterator it = lambda_term_names.iterator(); it.hasNext();)
				{
					String lambda_term = (String)it.next();
					
					sel_lambda_terms += "," + alias_name + "." + lambda_term;
					
					pos++;
					
					lambda_term_num[0] ++;
					
				}
				
				
//			}
			
		}
		
		
		if(condition_num >= 1)
		{
			condition_order = " order by " + condition_order;
			
//			if(!group_by_web_view.isEmpty())
//			{
//				group_by_web_view = "," + group_by_web_view;
//			}
//		}
//		else
//		{
//			if(!group_by_web_view.isEmpty())
//			{
//				group_by_web_view = " order by " + group_by_web_view;
//			}
			
		}
		
		
		sql = "select " + sel_item + sel_lambda_terms + conditions +  " from " + citation_table + where + condition_order;
		
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
