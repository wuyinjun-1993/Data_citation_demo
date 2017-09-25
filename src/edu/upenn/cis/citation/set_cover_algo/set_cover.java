package edu.upenn.cis.citation.set_cover_algo;

import java.util.ArrayList;

public class set_cover {
	
	static ArrayList<Double> costs = new ArrayList<Double>();
	
	static ArrayList<int []> int_matrix = new ArrayList<int []>();
	
	public static void main(String [] args)
	{
		init();
		
		System.out.println(greedy_algorithm(int_matrix));
	}
	
	
	static void init()
	{
		costs.add(1.0);
		
		costs.add(2.0);
		
		costs.add(5.0);
		
		costs.add(3.0);
		
		costs.add(1.0);
		
		int [] a1 = {1, 0, 1, 0, 1};
		
		int [] a2 = {0, 0, 0, 1, 1};
		
		int [] a3 = {0, 1, 0, 1, 0};
		
		int_matrix.add(a1);
		
		int_matrix.add(a2);
		
		int_matrix.add(a3);
	}
	
	public static ArrayList<Integer> greedy_algorithm(ArrayList<int[]> int_matrix)
	{
		int [] sum_array = get_sum(int_matrix);
		
		ArrayList<Integer> select_set = new ArrayList<Integer>();
		
		while(!int_matrix.isEmpty())
		{
			
			double min_cost = Double.MAX_VALUE;
			
			int index = -1;
			
			for(int i = 0; i < costs.size(); i++)
			{
				double cost = costs.get(i);
				
				double curr_cost = cost/sum_array[i];
				
				if(curr_cost < min_cost)
				{
					index = i;
					
					min_cost = curr_cost;
				}
			}
			
			for(int i = 0; i<int_matrix.size(); i++)
			{
				if(int_matrix.get(i)[index] == 1)
				{
					
					sum_array = update_sum_array(sum_array, int_matrix.get(i));
					
					int_matrix.remove(i);
					
					
					
					i--;
				}
			}
			
			select_set.add(index);
		}
		
		return select_set;
	}
	
	static int[] update_sum_array(int []sum_array, int [] deleted_array)
	{
		int[] updated_sum_array = new int[sum_array.length] ;
		
		for(int i = 0; i < sum_array.length; i++)
		{
			updated_sum_array[i] = sum_array[i] - deleted_array[i];
		}
		
		return updated_sum_array;
	}
	
	static int[] get_sum(ArrayList<int[]> int_matrix)
	{
		int[] sum_list = new int[int_matrix.get(0).length];
		
		for(int i = 0; i<int_matrix.size(); i++)
		{
			
			for(int j = 0; j<sum_list.length; j++)
			{
				sum_list[j] += int_matrix.get(i)[j];
			}
			
			
		}
		
		return sum_list;
		
		
	}

}
