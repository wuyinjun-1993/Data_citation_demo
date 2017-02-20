package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class populate_subgoal_arguments {
	
public static String separator = "|";
	
	public static String db_name = "iuphar_org";
	
	
	public static String[] base_relations = {"contributor_c", "contributor2family_c", "contributor2object_c", "contributor2intro_c", "contributor2ligand_c", "introduction_c", "gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c"};
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
	    
	    populate(c, pst, base_relations);
		
	}
	
	
	static void populate(Connection c, PreparedStatement pst, String[] base_relations) throws SQLException
	{
//		String reset_query = "delete from subgoal_arguments";
//		
//		pst = c.prepareStatement(reset_query);
//		
//		pst.execute();
		
		for(int i = 0; i<base_relations.length; i++)
		{
			
			String check_exist = "select exists(select 1 from subgoal_arguments where subgoal_names = '" + base_relations[i] +"')";
			
			pst = c.prepareStatement(check_exist);
			
			
			ResultSet r = pst.executeQuery();
		
			boolean exist = false;
			
			if(r.next())
			{
				exist = r.getBoolean(1);
			}
			
			if(!exist)
			{
				String query = "select column_name from information_schema.columns where table_name ='" + base_relations[i] + "'";

				pst = c.prepareStatement(query);
				
				ResultSet rs = pst.executeQuery();
				
				int num = 0;
				
				String arguments = new String();
				
				while(rs.next())
				{
					if(num >= 1)
						arguments += ",";
					
					arguments += rs.getString(1);
					num++;
				}
				
				query = "insert into subgoal_arguments values ('" + base_relations[i] + "','" + arguments + "')";
				
//				query = "update subgoal_arguments set arguments = '" + arguments +"' where subgoal_names = '" + base_relations[i] + "'";
				
				pst = c.prepareStatement(query);
				
				pst.execute();
			}
			
			
			
			
		}
		
		
	}
	

}
