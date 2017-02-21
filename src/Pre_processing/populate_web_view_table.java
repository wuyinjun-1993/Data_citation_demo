package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class populate_web_view_table {
	
	public static String db_name = "iuphar_org";

	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
	    
	    populate(c, pst);
		
	}
	
	static void populate(Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "select v_t.view, s.subgoal_names, v_t.head_variables from view_table v_t join subgoals s using (view) where v_t.web_view = 't'";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		int n = 0;
		
		while(rs.next())
		{
			String view_name = rs.getString(1);
			
			String subgoal_name = rs.getString(2);
			
			String []head_variables = rs.getString(3).split(",");
			
			String sub_q = "select arguments from subgoal_arguments where subgoal_names = '" + subgoal_name + "'";
			
			pst = c.prepareStatement(sub_q);
			
			ResultSet r = pst.executeQuery();
			
			String [] arguments = null;
			
			if(r.next())
			{
				arguments = r.getString(1).split(",");
			}
			
			
			String new_head_vars = new String();
			
			int num = 0;
			
			
			for(int i = 0; i<head_variables.length; i++)
			{	
				for(int j = 0; j<arguments.length; j++)
				{
					if(arguments[j].equals(head_variables[i].trim()))
					{
						if(num >= 1)
							new_head_vars += ",";
						
						new_head_vars += head_variables[i];
						
						num ++;
						
						break;
					}
				}
				
			}
			
			n++;
			
			String insert_q = "insert into web_view_table values ('" + view_name + "','" + view_name + "_" + n + "','" + subgoal_name + "','" + new_head_vars + "')"; 
			
			pst = c.prepareStatement(insert_q);
			
			pst.execute();
			
		}
		
		
	}

}
