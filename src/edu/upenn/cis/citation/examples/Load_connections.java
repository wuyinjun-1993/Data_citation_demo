package edu.upenn.cis.citation.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;

public class Load_connections {
	
	static String separator = "|"; 
	
	public static Vector<String []> get_view_citation_connection(String file)
	{
		Vector<String []> connections = new Vector<String []>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	
		    	String [] view_citation = line.split("\\" + separator);
		    	
		    	connections.add(view_citation);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connections;
		
	}

}
