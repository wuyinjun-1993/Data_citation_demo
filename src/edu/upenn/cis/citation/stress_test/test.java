package edu.upenn.cis.citation.stress_test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.upenn.cis.citation.Pre_processing.populate_db;

public class test {
	
	static int times = 10;
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException
	{
		
		String s1 = ".*v1.*v2.*v2.*";
		
		String s2 = "v3v1v5v2v3v2v4";
		
		System.out.println(s2.matches(s1));
		
		Connection c = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c = DriverManager
	        .getConnection(populate_db.db_url, populate_db.usr_name , populate_db.passwd);
		
	    long start = System.nanoTime();
	    
	    for(int i = 0; i<times; i++)
	    {

	    	String query = "((select distinct ligand.withdrawn_drug as ligand_withdrawn_drug,ligand.type as ligand_type,ligand.emc_url as ligand_emc_url,ligand0.radioactive as ligand0_radioactive,ligand0.withdrawn_drug as ligand0_withdrawn_drug,ligand0.popn_pharmacokinetics as ligand0_popn_pharmacokinetics,ligand0.ligand_id as ligand0_ligand_id,ligand0.name as ligand0_name,ligand0.approved as ligand0_approved,ligand0.emc_url as ligand0_emc_url,ligand0.organ_function_impairment as ligand0_organ_function_impairment,contributor2ligand.first_names as first_names,contributor2ligand.surname as surname from ligand ligand,ligand ligand0,contributor2ligand contributor2ligand,ligand ligand1 where ligand0.ligand_id<'16' and ligand0.ligand_id<'9' and ligand.ligand_id<'14' and ligand0.ligand_id<'10' and ligand0.ligand_id<='16' and ligand.ligand_id<>'4' and ligand.ligand_id<>'7' and ligand0.ligand_id<>'7' and ligand.ligand_id<='16' and ligand0.ligand_id<>'4' and ligand.ligand_id<'9' and ligand.ligand_id<'16' and ligand.ligand_id<'10' and ligand0.ligand_id<'14' and ligand.ligand_id<='3' and ligand0.ligand_id<='3' and ligand1.type = ligand.type and contributor2ligand.ligand_id=ligand1.ligand_id) union (select distinct ligand.withdrawn_drug as ligand_withdrawn_drug,ligand.type as ligand_type,ligand.emc_url as ligand_emc_url,ligand0.radioactive as ligand0_radioactive,ligand0.withdrawn_drug as ligand0_withdrawn_drug,ligand0.popn_pharmacokinetics as ligand0_popn_pharmacokinetics,ligand0.ligand_id as ligand0_ligand_id,ligand0.name as ligand0_name,ligand0.approved as ligand0_approved,ligand0.emc_url as ligand0_emc_url,ligand0.organ_function_impairment as ligand0_organ_function_impairment,contributor2ligand.first_names as first_names,contributor2ligand.surname as surname from ligand ligand,ligand ligand0,contributor2ligand contributor2ligand,ligand ligand1 where ligand0.ligand_id<'16' and ligand0.ligand_id<'9' and ligand.ligand_id<'14' and ligand0.ligand_id<'10' and ligand0.ligand_id<='16' and ligand.ligand_id<>'4' and ligand.ligand_id<>'7' and ligand0.ligand_id<>'7' and ligand.ligand_id<='16' and ligand0.ligand_id<>'4' and ligand.ligand_id<'9' and ligand.ligand_id<'16' and ligand.ligand_id<'10' and ligand0.ligand_id<'14' and ligand.ligand_id<='3' and ligand0.ligand_id<='3' and ligand1.type = ligand0.type and contributor2ligand.ligand_id=ligand1.ligand_id))";
	    	
	    	pst = c.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
	    }
	    
		
		
		long end = System.nanoTime();
		
		double time = (end - start) * 1.0/(times * 1000000000); 
		
		System.out.println(time + "s");
		
		ResultSet rs = pst.executeQuery();
		
		
	}

}
