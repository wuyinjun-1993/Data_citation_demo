package Pre_processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class populate_subgoal_arguments {
	
public static String separator = "|";
	
	public static String db_name = "iuphar_org";
	
	
	public static String[] base_relations = {"gpcr_c","object_c","interaction_c","ligand_c","pathophysiology_c","interaction_affinity_refs_c","gtip_process_c","process_assoc_c","disease_c"};
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
	      Connection c = null;
	      ResultSet rs = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection("jdbc:postgresql://localhost:5432/" + db_name,
	        "postgres","123");
	    
	    populate(c, pst);
		
	}
	
	
	static void populate(Connection c, PreparedStatement pst) throws SQLException
	{
//		String reset_query = "delete from subgoal_arguments";
//		
//		pst = c.prepareStatement(reset_query);
//		
//		pst.execute();
		
		for(int i = 0; i<base_relations.length; i++)
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
				
				arguments += base_relations[i] + "_" + rs.getString(1);
				num++;
			}
			
			query = "update subgoal_arguments set arguments = '" + arguments +"' where subgoal_names = '" + base_relations[i] + "'";
			
			pst = c.prepareCall(query);
			
			pst.execute();
			
		}
		
		
	}
	

}
