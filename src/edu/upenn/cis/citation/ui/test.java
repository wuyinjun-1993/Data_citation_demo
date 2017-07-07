package edu.upenn.cis.citation.ui;

import org.apache.jena.base.Sys;

import java_cup.internal_error;


public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query  = "{\"author\":[\"Alan Wise\",\"Adam J. Pawson\",\"Adrian J. L. Clark\",\"Alain Fournier\",\"Adrian Hobbs\",\"Adel Giaid\",\"Adit Ben-Baruch\",\"Ahsan Husain\",\"Aguan D. Wei\",\"Adriaan P. IJzerman\"]}";
		String[] split = query.split("\\},");
		String jsonString = "";
    	for (int i = 0; i < split.length; i++ ) {
    		if(i == 0)
    			jsonString += "{\"citation\":[" + split[i];
    		else {
    			jsonString += split[i] + "},";
    		}
    	}
    	jsonString += "]}";
    	System.out.println(query);
    	System.out.println("~~~~~~~~~~~~~~~~");
    	System.out.println(jsonString);
	}

}
