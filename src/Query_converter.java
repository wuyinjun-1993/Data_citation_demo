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
		
		Query query = sql2datalog("select * from family_c join introduction_c using (family_id) where family_id = '2' and type = 'gpcr'");
		
		String q = Datalog2Sql(query);
		
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
	
	public static String Datalog2Sql(Query query) throws ClassNotFoundException, SQLException
	{
		pre_processing(query);
		
		String q = datalog2sql(query);
		
		post_processing(query);
		
		return q;
	}
	
	static String datalog2sql(Query query) throws SQLException, ClassNotFoundException
	{
		
//		String citation_unit = new String();
		
		String sel_item = new String();
		
		String citation_table = new String();
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();
		
		int num = 0;
				
		String where = new String();
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			if(num >= 1)
			{				
				citation_table += " natural inner join ";
			}
									
			citation_table += subgoal.name;
			
			num++;
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
				if(arg.type == 1)
				{
					map.put(arg.lambda_term, arg.name);
				}
			}
						
		}
		
		if(!map.isEmpty())
		{
			Set set = map.keySet();
			
			where = " where ";
			
			int number = 0;
			
			for(Iterator iter = set.iterator();iter.hasNext();)
			{
				String key = (String) iter.next();
				
				String value = map.get(key);
				
				if(number >= 1)
					where += " and ";
				
				where += key + "=" + value;
				
				number ++;
			}
			
		}
			
		
		
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument arg = (Argument)query.head.args.get(i);
			
			if(i >= 1)
				sel_item += ",";
			if(arg.type == 0)
				sel_item += arg;
			else
			{
				sel_item += arg.lambda_term;
			}
				
		}
		
		sql = "select " + sel_item + " from " + citation_table + where;
		
		c.close();
		
		return sql;
	}
	
	static String datalog2sql_citation(Query query) throws SQLException, ClassNotFoundException
	{
		
		String citation_unit = new String();
		
//		String sel_item = new String();
		
		String citation_table = new String();
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();
		
		int num = 0;
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			if(num >= 1)
			{
				citation_unit += ",";
				
				citation_table += " natural inner join ";
			}
			
			citation_unit += subgoal.name + "_citation_view";
						
			citation_table += subgoal.name;
			
			num++;
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
				if(arg.type == 1)
				{
					map.put(arg.lambda_term, arg.name);
				}
			}
							
		}
				
		String where = new String();
		
		if(!map.isEmpty())
		{
			Set set = map.keySet();
			
			where = " where ";
			
			int number = 0;
			
			for(Iterator iter = set.iterator();iter.hasNext();)
			{
				String key = (String) iter.next();
				
				String value = map.get(key);
				
				if(number >= 1)
					where += " and ";
				
				where += key + "=" + value;
				
				number ++;
			}
			
		}
		
		sql = "select " + citation_unit + " from " + citation_table + where;

//		pst.close();
		
		c.close();
		
		return sql;
	}

	static String datalog2sql_citation_view(Query query) throws SQLException, ClassNotFoundException
	{
		
		String citation_unit = new String();
		
//		String sel_item = new String();
		
		String citation_table = new String();
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String sql = new String();
		
		int num = 0;
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
						
			if(num >= 1)
			{
				citation_unit += ",";
				
				citation_table += " natural inner join ";
			}
			
			citation_unit += subgoal.name + "_table" + "_citation_view";
						
			citation_table += subgoal.name + "_table";
			
			num++;
			
			for(int j = 0; j<subgoal.args.size();j++)
			{
				Argument arg = (Argument) subgoal.args.get(j);
				
				if(arg.type == 1)
				{
					map.put(arg.lambda_term, arg.name);
				}
			}
							
		}
				
		String where = new String();
		
		if(!map.isEmpty())
		{
			Set set = map.keySet();
			
			where = " where ";
			
			int number = 0;
			
			for(Iterator iter = set.iterator();iter.hasNext();)
			{
				String key = (String) iter.next();
				
				String value = map.get(key);
				
				if(number >= 1)
					where += " and ";
				
				where += key + "=" + value;
				
				number ++;
			}
			
		}
		
		sql = "select " + citation_unit + " from " + citation_table + where;

//		pst.close();
		
		c.close();
		
		return sql;
	}

	
	public static void pre_processing(Query query) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			rename_column(subgoal.name);
		}
	}
	
	public static void post_processing(Query query) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			rename_column_back(subgoal.name);
		}
	}
	
	public static void pre_processing_view(Query query) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			rename_column(subgoal.name + "_table");
		}
	}
	
	public static void post_processing_view(Query query) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i < query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			rename_column_back(subgoal.name + "_table");
		}
	}
	
	static void rename_column(String view) throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String rename_query = "alter table "+view +" rename column citation_view to "+ view + "_citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
		
		c.close();
	}
	
	static void rename_column_back(String view) throws SQLException, ClassNotFoundException
	{
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
		
		String rename_query = "alter table "+view +" rename column "+ view +"_citation_view to citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
		
		c.close();
	}
}
