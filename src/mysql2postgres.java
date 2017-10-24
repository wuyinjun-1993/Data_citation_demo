import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.sparql.resultset.ResultSetMem;

public class mysql2postgres {
	
	static String db_name = "dblp";
	
	public static String postgres_url = "jdbc:postgresql://localhost:5432/" + db_name;
	
	public static String usr_name = "wuyinjun";
	
	public static String passwd = "12345678";
	
	static String mysql_url = "jdbc:mysql://localhost:3306/" + db_name;
	
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		 Connection c1 = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
	      Class.forName("com.mysql.jdbc.Driver");  
	      Class.forName("org.postgresql.Driver");
	         c1 = DriverManager.getConnection(mysql_url,
	        	        usr_name, passwd);
	     Connection c2 = DriverManager.getConnection(postgres_url,
	        	        usr_name, passwd);
	     
//	     clear(c2, pst);
	     
	     Vector<String> table_names = get_all_table_names(c1, pst);
	     
	     get_average_size(table_names, c1, pst);
	     
//	     for(int i = 0; i<table_names.size(); i++)
//	     {
//	    	 String table_name = table_names.get(i);
//	    	 
//	    	 System.out.println(table_name);
//	    	 
//	    	 Vector<String []> field_type_mapping = get_schema_table(table_name, c1, pst);
////	    	 
//	    	 create_table_schema_postgres(table_name, field_type_mapping, c2, pst);
////	    	 
//	    	 read_write_record(table_name, c1, c2, pst);
//	     }
	     
	     c1.close();
	     
	     c2.close();
//	      try {
//	         
//	            
//	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
////	         pst = c.prepareStatement("SELECT *  FROM view_table");
//	         rs = pst.executeQuery();
//	         
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	         System.err.println(e.getClass().getName()+": "+e.getMessage());
//	         System.exit(0);
//	      }
	      System.out.println("Opened database successfully");
	}
	
	static Vector<String> get_all_table_names(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT table_name FROM information_schema.tables where table_schema='" + db_name +"'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String> table_names = new Vector<String>();
		
		while(rs.next())
		{
			String table_name = rs.getString(1);
			
			table_names.add(table_name);
		}
		
		return table_names;
	}
	
	static void get_average_size(Vector<String> table_names, Connection c, PreparedStatement pst) throws SQLException
	{
		int total_size = 0;
		
		for(int i = 0; i < table_names.size(); i++)
		{
			String query = "select count(*) from " + table_names.get(i);
			
			pst = c.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next())
			{
				int curr_size = rs.getInt(1);
				
				total_size += curr_size;
			}
		}
		
		double average_size = total_size * 1.0/table_names.size();
		
		System.out.println(average_size);
	}
	
	static Vector<String []> get_schema_table(String table_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "describe " + table_name;
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		Vector<String []> field_type_mapping = new Vector<String []>();
		
		while(rs.next())
		{
			String field_name = rs.getString(1);
			
			String type = rs.getString(2);
			
			String [] field_name_type = new String[2];
			
			field_name_type[0] = field_name;
			
			field_name_type[1] = type;
			
			field_type_mapping.add(field_name_type);
		}
		
		return field_type_mapping;
	}
	
	static void create_table_schema_postgres(String table_name, Vector<String []> field_type_mapping, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "create table " + table_name + "(";
				
		int num = 0;
		
		for(int i = 0; i<field_type_mapping.size(); i++)
		{
			String field_name = field_type_mapping.get(i)[0];
			
			String type = field_type_mapping.get(i)[1];
			
			if(type.contains("int"))
				type = "integer";
			
			if(num >= 1)
				query += ",";
			
			query += field_name + " " + type;
			
			num ++;
		}
		
		query += ")";
		
		System.out.println(query);
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void read_write_record(String table_name, Connection c1, Connection c2, PreparedStatement pst) throws SQLException
	{
		String query = "select * from " + table_name;
		
		pst = c1.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		ResultSetMetaData meta = rs.getMetaData();
		
		int col_num = meta.getColumnCount();
		
		int num = 0;
		
		while(rs.next())
		{
			String insert_q = "insert into " + table_name + " values (";
			
			for(int i = 0; i<col_num; i++)
			{
				String value = rs.getString(i + 1);
				
				if(i >= 1)
					insert_q += ",";
				
				if(value != null && value.contains("'"))
				{
					value = value.replaceAll("'", "''");
				}
				
				if(value != null)
					insert_q += "'" + value + "'";
				else
					insert_q += "''";
			}
			
			insert_q += ")";
						
			pst = c2.prepareStatement(insert_q);
			
			
			try{
				pst.execute();

			}
			catch (Exception e){
				
				System.out.println(insert_q);
				
				num ++;

				continue;
				
			}
			
			
		}
		
		System.out.println(num);
	}
	
	static void clear(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "DROP SCHEMA public CASCADE";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "CREATE SCHEMA public";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "GRANT ALL ON SCHEMA public TO " + usr_name;
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "GRANT ALL ON SCHEMA public TO public";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	

}
