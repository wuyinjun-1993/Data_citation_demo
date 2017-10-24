import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

public class process_text {
	
	static String path = "final_test_result/";
	
	public static void main(String[] strs)
	{
		process_text_full_case(path + "final_stress_test_lambda_num_full.txt", path + "tuple_level_stress_test_lambda_num_full_agg_intersection.csv", path + "tuple_level_stress_test_lambda_num_full_agg_union.csv", path + "semi_schema_level_stress_test_lambda_num_full_agg_intersection.csv", path + "semi_schema_level_stress_test_lambda_num_full_agg_union.csv", path + "schema_level_stress_test_lambda_num_full.csv");
		
//		process_text_min_case(path + "final_real_test_min2.txt", path + "exp_final_real_test_min2_tuple.csv", path + "exp_final_real_test_min2_semi_schema.csv", path + "exp_final_real_test_min2_schema.csv");
	}

	
	static void process(String file, String f1)
	{
		
		Vector<String> content1 = new Vector<String>();
				
		int num1 = 0;
		
		int num2 = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		   
		    
		    
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
//		    	String[] str = line.split("	");
//		    	
//		    	if(StringUtils.isNumeric(str[0]) && line.length() > 10)
//		    	{
//		    		content1.add(line);
//		    		
//		    		num1 ++;
//		    	}
//		    	else
		    	{
		    		if(line.startsWith("original_relation_size::"))
		    		{
		    			String prefix = "original_relation_size::";
		    			
		    			
		    			line = line.substring(prefix.length(), line.length());
		    			
		    			content1.add(line);
		    			
		    			num2++;
		    		}
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		write(f1, content1);
				
		System.out.println(content1.size());
		
	}
	
	static void process(String file, String f1, String f2)
	{
		
		Vector<String> content1 = new Vector<String>();
		
		Vector<String> content2 = new Vector<String>();
		
		int num1 = 0;
		
		int num2 = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		   
		    
		    
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String[] str = line.split("	");
		    	
		    	if(StringUtils.isNumeric(str[0]) && line.length() > 10)
		    	{
		    		content1.add(line);
		    		
		    		num1 ++;
		    	}
		    	else
		    	{
		    		if(line.startsWith("execution time"))
		    		{
		    			String prefix = "execution time::";
		    			
		    			
		    			line = line.substring(prefix.length(), line.length());
		    			
		    			content2.add(line);
		    			
		    			num2++;
		    		}
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		write(f1, content1);
		
		write(f2, content2);
		
		System.out.println(content1.size());
		
		System.out.println(content2.size());
	}
	
	static void process(String file, String f1, String f2, String f3)
	{
		
		Vector<String> content1 = new Vector<String>();
		
		Vector<String> content2 = new Vector<String>();
		
		Vector<String> content3 = new Vector<String>();
		
		int num1 = 0;
		
		int num2 = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    
		   
		    
		    
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String[] str = line.split("	");
		    	
		    	if(StringUtils.isNumeric(str[0]) && line.length() > 10)
		    	{
		    		
		    		if(num1 % 20 < 10)
		    		{
			    		content1.add(line);
		    		}
		    		else
		    		{
			    		content2.add(line);
		    		}
		    		
		    		num1 ++;
		    	}
		    	else
		    	{
		    		if(line.startsWith("execution time"))
		    		{
		    			String prefix = "execution time::";
		    			
		    			
		    			line = line.substring(prefix.length(), line.length());
		    			
		    			content3.add(line);
		    			
		    			num2++;
		    		}
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		write(f1, content1);
		
		write(f2, content2);
		
		write(f3, content3);
		
		System.out.println(content1.size());
		
		System.out.println(content2.size());
	}
	
	
	static void write(String file_name, Vector<String> line)
	{
		 BufferedWriter bw = null;
	      try {
	         //Specify the file name and path here
		 File file = new File(file_name);

		 /* This logic will make sure that the file 
		  * gets created if it is not present at the
		  * specified location*/
		  if (!file.exists()) {
		     file.createNewFile();
		  }

		  FileWriter fw = new FileWriter(file);
		  bw = new BufferedWriter(fw);
		  
		  for(int i = 0; i<line.size(); i++)
		  {
			  bw.append(line.get(i));
			  bw.newLine();
		  }
		  		  
	      bw.close();


		  
	      } catch (IOException ioe) {
		   ioe.printStackTrace();
		}
	      
	}
	
	static void process_text_full_case(String file_name, String f1, String f2, String f3, String f4, String f5)
	{
		Vector<Vector<Double>> tuple_level_agg_intersection = new Vector<Vector<Double>>();
	    
	    Vector<Vector<Double>> tuple_level_agg_union = new Vector<Vector<Double>>();
	    
	    Vector<Vector<Double>> semi_schema_agg_intersection = new Vector<Vector<Double>>();
	    
	    Vector<Vector<Double>> semi_schema_agg_union = new Vector<Vector<Double>>();
	    
	    Vector<Vector<Double>> schema = new Vector<Vector<Double>>();
		
	    int m = 3;
	    
		try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
		    String line;		    
		    
		    int num = 0;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String[] str = line.split("	");
		    	
		    	if(StringUtils.isNumeric(str[0]) && line.length() > 10)
		    	{
		    		Vector<Double> curr_values = new Vector<Double>();
		    		
//		    		System.out.println(num + "::" + line);
		    		
		    		for(int i = 0; i<str.length; i++)
		    		{
		    			if(str[i].contains("::"))
		    			{
		    				String num_val = str[i].split("::")[1];
		    				
		    				System.out.print(str[i].split("::")[0] + "	");
		    				
		    				if(isNumeric(num_val))
		    				{
		    					double value = Double.valueOf(num_val);
		    					
		    					curr_values.add(value);
		    				}
		    			}
		    		}
		    		
		    		System.out.println();
		    		
		    		if(num % (5*m) <m)
		    		{
		    			tuple_level_agg_intersection.add(curr_values);
		    		}
		    		else
		    		{
		    			if(num % (5*m) <2*m && num % (5*m) >=m)
		    			{
		    				tuple_level_agg_union.add(curr_values);
		    			}
		    			else
		    			{
		    				if(num % (5*m) < 3*m && num % (5*m) >= 2*m)
		    				{
		    					semi_schema_agg_intersection.add(curr_values);
		    				}
		    				else
		    				{
		    					if(num % (5*m) < 4*m && num % (5*m) >= 3*m)
		    					{
		    						semi_schema_agg_union.add(curr_values);
		    					}
		    					else
		    					{
		    						schema.add(curr_values);
		    					}
		    				}
		    			}
		    		}
		    		
			    	num ++;

		    	}
		    	
		    }
		    
		    
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Vector<Vector<Double>> tuple_level_agg_intersection_average = cal_average(tuple_level_agg_intersection, m);		    
	    
	    Vector<Vector<Double>> tuple_level_agg_union_average = cal_average(tuple_level_agg_union, m);
	    
	    Vector<Vector<Double>> semi_schema_agg_intersection_average = cal_average(semi_schema_agg_intersection, m);
	    
	    Vector<Vector<Double>> semi_schema_agg_union_average = cal_average(semi_schema_agg_union, m);
	    
	    Vector<Vector<Double>> schema_average = cal_average(schema, m);
	    
	    write(f1, covert2vec_str(tuple_level_agg_intersection_average));
	    
	    write(f2, covert2vec_str(tuple_level_agg_union_average));
	    
	    write(f3, covert2vec_str(semi_schema_agg_intersection_average));
	    
	    write(f4, covert2vec_str(semi_schema_agg_union_average));
	    
	    write(f5, covert2vec_str(schema_average));
	}
	
	static void process_text_min_case(String file_name, String f1, String f2, String f3)
	{
		Vector<Vector<Double>> tuple_level = new Vector<Vector<Double>>();
	    	    
	    Vector<Vector<Double>> semi_schema = new Vector<Vector<Double>>();
	    	    
	    Vector<Vector<Double>> schema = new Vector<Vector<Double>>();
		
	    int m = 10;
	    
		try (BufferedReader br = new BufferedReader(new FileReader(file_name))) {
		    String line;		    
		    
		    int num = 0;
		    
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	String[] str = line.split("	");
		    	
		    	if(StringUtils.isNumeric(str[0]) && line.length() > 10)
		    	{
		    		Vector<Double> curr_values = new Vector<Double>();
		    		
//		    		System.out.println(num + "::" + line);
		    		
		    		for(int i = 0; i<str.length; i++)
		    		{
		    			if(str[i].contains("::"))
		    			{
		    				String num_val = str[i].split("::")[1];
		    				
		    				System.out.print(str[i].split("::")[0] + "	");
		    				
		    				if(isNumeric(num_val))
		    				{
		    					double value = Double.valueOf(num_val);
		    					
		    					curr_values.add(value);
		    				}
		    			}
		    		}
		    		
		    		System.out.println();
		    		
		    		if(num % (2*m) <m)
		    		{
		    			tuple_level.add(curr_values);
		    		}
		    		else
		    		{
		    			
		    			semi_schema.add(curr_values);
	    					    			
		    		}
		    		
			    	num ++;

		    	}
		    	
		    	if(str[0].startsWith("execution time::"))
		    	{
		    		Vector<Double> curr_values = new Vector<Double>();
		    		
//		    		System.out.println(num + "::" + line);
		    		
		    		for(int i = 0; i<str.length; i++)
		    		{
		    			if(str[i].contains("::"))
		    			{
		    				String num_val = str[i].split("::")[1];
		    				
		    				System.out.print(str[i].split("::")[0] + "	");
		    				
		    				if(isNumeric(num_val))
		    				{
		    					double value = Double.valueOf(num_val);
		    					
		    					curr_values.add(value);
		    				}
		    			}
		    		}
		    		
		    		schema.add(curr_values);
		    		
		    		System.out.println();
		    	}
		    	
		    }
		    
		    
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Vector<Vector<Double>> tuple_level_average = cal_average(tuple_level, m);		    
	    	    
	    Vector<Vector<Double>> semi_schema_average = cal_average(semi_schema, m);
	    	    
	    Vector<Vector<Double>> schema_average = cal_average(schema, m);
	    
	    write(f1, covert2vec_str(tuple_level_average));
	    	    
	    write(f2, covert2vec_str(semi_schema_average));
	    
	    write(f3, covert2vec_str(schema_average));

	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	static Vector<String> covert2vec_str(Vector<Vector<Double>> values)
	{
		Vector<String> value_strs = new Vector<String>();
		
		
		for(int i = 0; i<values.size(); i++)
		{
			Vector<Double> curr_values = values.get(i);
			
			String curr_value_str = new String();
			
			for(int j = 0; j<curr_values.size(); j++)
			{
				
				if(j >= 1)
					curr_value_str += ",";
				
				curr_value_str += curr_values.get(j);
			}
			
			value_strs.add(curr_value_str);
		}
		
		return value_strs;
	}
	
	static Vector<Vector<Double>> cal_average(Vector<Vector<Double>> values, int num)
	{
		Vector<Vector<Double>> averages = new Vector<Vector<Double>>();
		
		for(int i = 0; i<values.size()/num; i++)
		{
			
			Vector<Double> average = new Vector<Double>();
			
			for(int j = 0; j<values.get(0).size(); j++)
			{
				if(i *num + num - 1 < values.size())
				{
					
					double value = 0;
					
					for(int k = 0; k<num; k++)
					{
						value += values.get(i * num + k).get(j);
					}
					
					value = value / num;
										
					average.add(value);
				}
			}
			
			if(!average.isEmpty())
				averages.add(average);
		}
		
		return averages;
	}
}
