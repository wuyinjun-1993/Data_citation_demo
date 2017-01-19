import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Add_provenance {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		add_provenance_table();
		
		add_provenance_views();
	}
	
	public static void add_provenance_views() throws ClassNotFoundException, SQLException
	{
		Vector<Query> views =  Gen_citation1.get_views_schema();
		
		for(int i = 0; i<views.size(); i++)
		{
			add_column(views.get(i).name + "_table");
			
			String query = Query_converter.datalog2sql_provenance(views.get(i), false);
			
			add_provenance_view(views.get(i), query);
		}
		
	}
	
	public static void add_provenance_view(Query view, String query) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    pst = c.prepareStatement(query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    while(rs.next())
	    {
	    	int[] provenances = new int[view.body.size()];
	    	
	    	for(int i = 0; i<view.body.size(); i++)
	    	{
	    		provenances[i] = Integer.valueOf(rs.getString(i+1));
	    	}
	    	
	    	Arrays.sort(provenances);
	    	
	    	String update_provenance = new String();
	    	
	    	for(int i = 0; i<provenances.length; i++)
	    	{
	    		if(i >= 1)
	    			update_provenance += ",";
	    		
	    		update_provenance += provenances[i];
	    	}
	    	
	    	
	    	
	    	String update_conditions = new String();
	    	
	    	for(int i = view.body.size(); i < view.body.size() + view.head.args.size(); i++)
	    	{
	    		if(i > view.body.size())
	    			update_conditions += " and ";
	    		String item = rs.getString(i + 1).replaceAll("'", "''");
	    			    		
	    		update_conditions += view.head.args.get(i - view.body.size()) + "= '"+ item + "'";
	    	}
	    	
	    	String update_query = "update " + view.name + "_table set provenance = '" + update_provenance + "' where " + update_conditions;
	    	
	    	pst = c.prepareStatement(update_query);
	    	
	    	pst.execute();
	    }
	}
	
	public static void add_provenance_table() throws ClassNotFoundException, SQLException
	{
		HashSet<String> table_names = populate_db.get_table_name(Gen_citation1.get_views_schema());
		
		int label = 0;
		
		for(Iterator iter = table_names.iterator();iter.hasNext();)
		{
			String str = (String) iter.next();
			
			add_column(str);
			
			Vector<String> col_names = Parse_datalog.get_columns(str);
			
			label = add_provenance_table(str, label, col_names);
			
		}
		
		
		
	}
	
	
	public static int add_provenance_table(String table_name, int label, Vector<String> col_names) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String col_str = new String();
	    
	    for(int i = 0; i<col_names.size(); i++)
	    {
	    	if(i >= 1)
	    		col_str += ",";
	    	
	    	col_str += col_names.get(i);
	    }
	    
	    String str_query = "select " + col_str + " from " + table_name;
	    
	    pst = c.prepareStatement(str_query);
	    
	    ResultSet rs = pst.executeQuery();
	    
	    ResultSetMetaData rsmd = rs.getMetaData();
	    
	    int columnsNumber = rsmd.getColumnCount();
	    
	    Vector<String> tuples = new Vector<String>(); 
	    
	    while(rs.next())
	    {
	    	for(int j = 0; j<columnsNumber; j++)
	    	{
	    		String item = rs.getString(j + 1);
	    		
	    		if(item.contains("'"))
	    			item = item.replaceAll("'", "''");
	    		
	    		tuples.add(item);
	    	}
	    	
	    	add_provenance_tuple(c, pst, table_name, col_names, tuples, label);
	    	
	    	tuples.clear();
	    	
	    	label ++;
	    }
	    
	    return label;
	    
	}
	
	public static void add_provenance_tuple(Connection c, PreparedStatement pst,String table_name, Vector<String> col_names, Vector<String> tuples, int label) throws SQLException
	{
		
		String str_assignment = new String();
		
		for(int i = 0; i<col_names.size(); i++)
		{
			if(i >= 1)
				str_assignment += " and ";
			
			str_assignment += col_names.get(i) + "='" + tuples.get(i) + "'";
		}
		
		
		String str_update = "update " + table_name + " set provenance = " + label + " where " + str_assignment;
		
		pst = c.prepareStatement(str_update);
		
		pst.execute();
	}
	
	public static void add_column(String table_name) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/IUPHAR",
	        "postgres","123");
	    
	    String insert_col = "provenance";
	    
	    String test_col = "SELECT column_name FROM information_schema.columns WHERE table_name='" + table_name + "' and column_name='" + insert_col +"'";
	    
	    pst = c.prepareStatement(test_col);
	    rs = pst.executeQuery();
	    
	    if(rs.next())
	    {
	    	if(rs.getRow()!=0)
	    	{
	    		String remove_sql = "alter table "+ table_name +" drop column "+ insert_col;
	    		
	    		pst = c.prepareStatement(remove_sql);
	    		
	    		pst.execute();
	    	}
	    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN " + insert_col +" text";
    	    pst = c.prepareStatement(add_col);
    	    pst.execute();
	    }
	    else
	    {
	    	String add_col = "ALTER TABLE "+ table_name +" ADD COLUMN " +insert_col+ " text";
    	    pst = c.prepareStatement(add_col);
    	    pst.execute();
	    }
	    
	    c.close();
	}
}
