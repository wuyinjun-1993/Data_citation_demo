package edu.upenn.cis.citation.Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;

public class citation_view_operation {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		String c_names = "family";
		
		Vector<String> v_names = get_views(c_names);
		
		System.out.println(v_names.toString());
	}
	
	public static void add_citation_view(String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        int id = get_citation_view_num(c, pst) + 1;
        
//        String citation_id = "c" + id;
        
        insert_citation_table(id, name, c, pst);
        
        c.close();
        
	}
	
	public static Vector<String> get_views(String c_name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
        
        Vector<String> view_names = new Vector<String>();
        
        String sql = "select view_table.name from view_table, citation2view, citation_table where view_table.view = citation2view.view and citation2view.citation_view_id = citation_table.citation_view_id and citation_table.citation_view_name = '" + c_name + "'";
        
        pst = c.prepareStatement(sql);
                
        ResultSet rs = pst.executeQuery();
        
        while(rs.next())
        {
        	view_names.add(rs.getString(1));
        }
        
        c.close();
        
        return view_names;
	}
	
	public static void add_connection_view_with_citations(String citation_name, String view_name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		int view_id = view_operation.get_view_id(view_name, c, pst);
		
		int citation_id = get_citation_id(citation_name, c, pst);
		
		insert_citation2view(view_id, citation_id, c, pst);
		
		c.close();
	}
	

	
	public static void delete_connection_view_with_citations(String view_name, String citation_view_name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		int view_id = view_operation.get_view_id(view_name, c, pst);
		
		int citation_view_id = get_citation_id(citation_view_name, c, pst);
		
		String query = "delete from citation2view where citation_view_id = '" + citation_view_id + "' and view = '" + view_id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		c.close();
	}
	
	public static void delete_citation_views(String citation_name) throws ClassNotFoundException, SQLException
	{
		
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		int citation_view_id = get_citation_id(citation_name, c, pst);
		
		delete_citation2query(citation_view_id, c, pst);
		
		delete_citation2view(citation_view_id, c, pst);
		
		delete_citation_table(citation_view_id, c, pst);
		
		c.close();
	}

	public static void update_citation_view(String old_name, String new_name) throws SQLException, ClassNotFoundException
	{
		
		Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
           .getConnection(populate_db.db_url,
       	        populate_db.usr_name,populate_db.passwd);
        
        PreparedStatement pst = null;
		
		int citation_view_id = get_citation_id(old_name, c, pst);
		
		update_citation_view_name(citation_view_id, new_name, c, pst);
		
		c.close();
		
		
	}
	
	static void update_citation_view_name(int id, String new_name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "update citation_table set citation_view_name ='" + new_name + "' where citation_view_id = " + id;
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		
	}
	
//	public static void add_citation2query(String citation_name, String citation_block, )
	
	
	static void delete_citation2query(int citation_view_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2query where citation_view_id = '" + citation_view_id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_citation2view(int citation_view_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation2view where citation_view_id = '" + citation_view_id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static void delete_citation_table(int citation_view_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "delete from citation_table where citation_view_id = '" + citation_view_id + "'";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}	
	
	static int get_citation_view_num(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select max(citation_view_id) from citation_table";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		if(rs.next())
		{
			num = rs.getInt(1);
		}
		
		return num;
	}
	
	static void insert_citation_table(int id, String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "insert into citation_table values ('" + id + "','" + name + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
	}
	
	static int get_citation_id(String name, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select citation_view_id from citation_table where citation_view_name = '" + name + "'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int id = 0;
		
		if(rs.next())
			id = rs.getInt(1);
		
		return id;
	}
	
	
	static void insert_citation2view(int view_id, int citation_id, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "insert into citation2view values ('" + citation_id + "','" + view_id + "')";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
	}
	
	
	

}
