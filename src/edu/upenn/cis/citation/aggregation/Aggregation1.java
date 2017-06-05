package edu.upenn.cis.citation.aggregation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning1;

public class Aggregation1 {
	
//	static String query = "q(object_c_object_id): object_c(), gpcr_c(), interaction_c(), gpcr_c_object_id = object_c_object_id, interaction_c_object_id = gpcr_c_object_id";
	
	static String query = "q(object_c_object_id):object_c()";
	
	public static Vector<Vector<citation_view_vector>> do_aggregate(Vector<Vector<citation_view_vector>> curr_res, Vector<citation_view_vector> c, int seq)
	{
		
		if(seq == 0)
		{
			
			for(int i = 0; i<c.size(); i++)
			{
				Vector<citation_view_vector> c_vec = new Vector<citation_view_vector>();
				
				c_vec.add(c.get(i));
				
				curr_res.add(c_vec);
			}
			
			
			
			return curr_res;
		}
		
		
		
		Vector<Vector<citation_view_vector>> agg_res = new Vector<Vector<citation_view_vector>>();
		
		int i1 = 0;
		
		int i2 = 0;
				
		while(i1 < curr_res.size() && i2 < c.size())
		{
			String id1 = curr_res.get(i1).get(0).table_name_str;
						
			String id2 = c.get(i2).table_name_str;
								
			if(id1.equals(id2))
			{
				
				curr_res.get(i1).add(c.get(i2));
				
				agg_res.add(curr_res.get(i1));
				
//				System.out.println(id1 + "+++" + id2);
				
				
				i1++;
				
				i2++;
			}
			else
			{
				if(id1.compareTo(id2) < 0)
				{
					i1 ++;
				}
				else
					i2 ++;
			}
		}
		
		return agg_res;
		
		
	}
	
	public static Vector<Vector<citation_view_vector>> aggegate(Vector<Vector<citation_view_vector>> c_views)
	{
		Vector<Vector<citation_view_vector>> agg_res = new Vector<Vector<citation_view_vector>>();
		
		for(int i = 0; i<c_views.size(); i++)
		{			
			agg_res = do_aggregate(agg_res, c_views.get(i), i);
		}
		
		return agg_res;
	}
	
	
	public static Vector<Vector<citation_view_vector>> aggegate(Vector<Vector<citation_view_vector>> c_views, Vector<Integer> indexes)
	{
		Vector<Vector<citation_view_vector>> agg_res = new Vector<Vector<citation_view_vector>>();
		
		for(int i = 0; i<indexes.size(); i++)
		{
			agg_res = do_aggregate(agg_res, c_views.get(indexes.get(i)), i);
		}
		
		return agg_res;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException
	{
		
//		String q = Tuple_reasoning1.get_full_query(query);
//		
//		Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning1.gen_citation_main(q);
//		
//		
//		Vector<Vector<citation_view_vector>> agg_res = aggegate(c_views);
//		
//		output(agg_res);
	}
	
	
	public static void output(Vector<Vector<citation_view_vector>> agg_res)
	{
		for(int i = 0; i < agg_res.size(); i++)
		{
			Vector<citation_view_vector> c_vec = agg_res.get(i);
			
			
			for(int j = 0; j<c_vec.size(); j++)
			{
				System.out.print(c_vec.get(j).toString());
				
				System.out.println(" ");
			}
			
			System.out.println(i);
		}
	}

}
