package edu.upenn.cis.citation.ui;

import java_cup.internal_error;


public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query  = "select family_if FROM family WHERE family_id = 3 ";
		String splitSQL = "";
		String[] q1 = null;
		String[] q2 = null;
		if (query.contains("from")) {
			q1 = query.split("from");
			splitSQL = q1[0].trim() + "\n";
			if (q1[1].contains("where")) {
				q2 = q1[1].split("where");
				splitSQL += "from " + q2[0].trim() + "\n" + "where " + q2[1].trim();
			}
		} else if (query.contains("FROM")){
			 q1 = query.split("FROM");
			 splitSQL = q1[0].trim() + "\n";
			 if (q1[1].contains("WHERE")) {
				 q2 = q1[1].split("WHERE");
				 splitSQL += "FROM " + q2[0].trim() + "\n" + "WHERE " + q2[1].trim();
				 }
		} else
			splitSQL = query;
	}

}
