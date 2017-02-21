package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class create_contributor2ligand {
	
	
	public static void main(String []args) throws SQLException, ClassNotFoundException
	{
		Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    create_table(c, pst);
	}
	
	static int get_max_id(Connection c, PreparedStatement pst) throws SQLException
	{
		String q = "select max(ligand_id) from ligand";
		
		pst = c.prepareStatement(q);
		
		ResultSet rs = pst.executeQuery();
		
		int max_val = 0;
		
		if(rs.next())
		{
			max_val = rs.getInt(1);
		}
		
		return max_val;
		
	}
	
	
	static void create_table(Connection c, PreparedStatement pst) throws SQLException
	{
		int max_val = get_max_id(c, pst);
		
		for(int i = 1; i<= max_val; i++)
		{
			String author_q = "select authors from reference join reference2ligand on reference.reference_id = reference2ligand.reference_id where ligand_id = ?";
			
			pst = c.prepareStatement(author_q);
			
			pst.setInt(1, i);
			
			ResultSet r = pst.executeQuery();
			
			Vector<String []> names = new Vector<String[]>();
			
			while(r.next())
			{
				String [] authors = r.getString(1).split(",");
				
				for(int k = 0; k<authors.length; k++)
				{
					
					String [] curr_names = authors[k].trim().split("\\s+");
					
					String [] n = new String[2];
					
					n[0] = curr_names[0].trim();
					
					if(n[0].contains("'"))
					{
						n[0] = n[0].replaceAll("'", "''");
					}
					
					if(curr_names.length > 1)
					{
						n[1] = curr_names[1].trim();
						
						if(n[1].contains("'"))
						{
							n[1] = n[1].replaceAll("'", "''");
						}
						
						names.add(n);
					}
					
					
				}
				
			}
			
			for(int k = 0; k<names.size(); k++)
			{
				String insert_q = "insert into contributor2ligand values ('" + i + "','" + names.get(k)[0] + "','" + names.get(k)[1] + "')";				
				
				pst = c.prepareStatement(insert_q);
				
				pst.execute();
			}
			
			
		}
		
		
	}

}
