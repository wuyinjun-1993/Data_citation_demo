package edu.upenn.cis.citation.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import edu.upenn.cis.citation.Corecover.Query;

public class test_equality {
	
	static String file1 = "reasoning_results/covering_sets";
	
	static String file2 = "reasoning_results/covering_sets2";
	
	public static void main(String [] args)
	{
		HashSet<String> arr1 = get_view_strs(file1);
		
		HashSet<String> arr2 = get_view_strs(file2);
		
		System.out.println(arr1);
		
		System.out.println(arr2);
		
		System.out.println(arr1.equals(arr2));
	}
	
	
	public static HashSet<String> get_view_strs(String file_name)
	{
		HashSet<String> covering_sets = new HashSet<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	
		    	covering_sets.add(line);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return covering_sets;
	}

}
