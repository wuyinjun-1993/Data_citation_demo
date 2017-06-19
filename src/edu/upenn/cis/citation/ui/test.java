package edu.upenn.cis.citation.ui;

import sun.security.util.Length;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String join  = "=family.id";
		System.out.println(join.charAt(0));
		String[] splitchar = join.split("\\.");
		System.out.println(splitchar[0]);
		System.out.println(splitchar[0].substring(1, splitchar[0].length()));
	}

}
