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

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Query_converter {
	
	public static void main(String [] args) throws JSQLParserException, ClassNotFoundException, SQLException
	{
		
		String str = "Q(v,head_variables):view_table(v, head_variables, lambda_term),citation_view(citation_view_name, v, h)";
		
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
		        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
		        "postgres","123");
		    
		    
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
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
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
	
//	public static String Datalog2Sql(Query query) throws ClassNotFoundException, SQLException
//	{
//		pre_processing(query);
//		
//		String q = datalog2sql(query);
//		
//		post_processing(query);
//		
//		return q;
//	}
	
	static String[] gen_condition(Query query, HashMap<String, Vector<String[]>>arg_name_map,boolean view)
	{
		String where = new String();
		
		String citation_table = new String();
		
		int num = 0;
		
		String citation_str = new String();
		
		String citation_provenance = new String();
		
		boolean condition = false;
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
									
			if(i >= 1)
			{
				citation_str += ",";
				citation_provenance += ",";
			}
			if(!view)	
			{
				citation_str += subgoal.name + "." + "citation_view";
				citation_provenance += subgoal.name + ".provenance";
			}
			else
			{
				citation_str += subgoal.name + "_table." + "citation_view";
				citation_provenance += subgoal.name + "_table." + "provenance";
			}
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
//				String[] str_list = {arg.name, subgoal.name};
				
				if(arg.type == 1)
				{
					
					if(!condition)
					{
						where += " where ";
						condition = true;
					}
					
					if(num >= 1)
						where += " and "; 
					
					if(!view)
						where += subgoal.name + "." +arg.origin_name  + "=" + arg.name;
					else
						where += subgoal.name + "_table" + "." +arg.origin_name  + "=" + arg.name;
					
					if(arg_name_map.get(arg.name)==null)
					{
						Vector<String[]> table_names = new Vector<String[]>();
						
						String[] str_list = {subgoal.name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
												
						String[] str_list = {subgoal.name, arg.origin_name};
						
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
						
						String[]str_list = {subgoal.name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
					}
					
					else
					{
						Vector<String[]> table_names = arg_name_map.get(arg.name);
						
						int vec_size = table_names.size();
						
						if(!condition)
						{
							where += " where ";
							condition = true;
						}
						
						if(num >= 1)
							where += " and "; 
						
						if(!view)
							where += table_names.get(vec_size - 1)[0] + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "." + arg.origin_name;
						else
							where += table_names.get(vec_size - 1)[0] + "_table" + "." + table_names.get(vec_size - 1)[1] + "=" + subgoal.name + "_table" + "." + arg.origin_name;
						
						String [] str_list = {subgoal.name, arg.origin_name};
						
						table_names.add(str_list);
						
						arg_name_map.put(arg.name, table_names);
						
						num++;
						
					}
					
				}
			}
			
			if(i >= 1)
				citation_table += ",";
			
			if(!view)
				citation_table += subgoal.name;
			else
				citation_table += subgoal.name + "_table";
		}
		
		String []str_l = {where, citation_table,citation_str, citation_provenance};
		
		return str_l;
	}
	
	static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
	{
		
//		String citation_unit = new String();
		
		String sel_item = new String();
		
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();				
				
		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
				
		String[] str_l = gen_condition(query, arg_name_map, false);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
		
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
//			if(arg.type == 0)
//				sel_item += arg;
//			else
//			{
//				sel_item += arg.origin_name;
//			}
				
		}
		
		sql = "select " + sel_item + " from " + citation_table + where;
		
		c.close();
		
		return sql;
	}
	
	static String datalog2sql_citation(Query query, boolean view) throws SQLException, ClassNotFoundException
	{
				
		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();
		
		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
		
		String[] str_l = gen_condition(query, arg_name_map,view);
		
		String where = str_l[0];
		
		String citation_table = str_l[1];
		
		String citation_unit = str_l[2];
		
		String citation_provenance = str_l[3];
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			
			Vector<String[]> table_names = arg_name_map.get(arg.name);
			sel_item += table_names.get(0)[0] + "." + table_names.get(0)[1];
				
		}
		
		sql = "select " + citation_unit + "," + citation_provenance + " from " + citation_table + where;

//		pst.close();
		
		c.close();
		
		return sql;
	}
	
	static String datalog2sql_provenance(Query query, boolean view) throws SQLException, ClassNotFoundException
	{
				
		String sel_item = new String();
				
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();
		
		HashMap<String, Vector<String[]>> arg_name_map = new HashMap<String, Vector<String[]>>();
		
		String[] str_l = gen_condition(query, arg_name_map,view);
		
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
//	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
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
//	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
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
//	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
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
