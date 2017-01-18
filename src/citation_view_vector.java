import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class citation_view_vector {
	
	public Vector<citation_view> c_vec;
	
	public Vector<Character> index_vec;
	
	public HashSet<String> tuple_cores;
	
	public citation_view_vector(Vector<citation_view> vec){
		
		c_vec = vec;
		
		index_vec = new Vector<Character>();
		
		tuple_cores = new HashSet<String>();
		
		for(int i = 0; i<c_vec.size(); i++)
		{
			index_vec.add(c_vec.get(i).get_index());
			
			Tuple tuple = Gen_citation2.map.get(c_vec.get(i).get_name());
			
			for(Iterator iter = tuple.core.iterator();iter.hasNext();)
			{
				String core_name = iter.next().toString();
				
				tuple_cores.add(core_name);
			}
			
			
		}
		
	}
	
	public citation_view_vector(Vector<citation_view> vec, Vector<Character> index_vec, HashSet<String> tuple_cores){
		
		this.c_vec = vec;
		
		this.index_vec = index_vec;
		
		this.tuple_cores = tuple_cores;
		
	}
	
	public citation_view_vector(citation_view c){
		
		c_vec = new Vector<citation_view>();
		
		c_vec.add(c);
		
		index_vec = new Vector<Character>();
		
		tuple_cores = new HashSet<String>();
		
		for(int i = 0; i<c_vec.size(); i++)
		{
			index_vec.add(c_vec.get(i).get_index());
			
			Tuple tuple = Gen_citation2.map.get(c_vec.get(i).get_name());
			
			for(Iterator iter = tuple.core.iterator();iter.hasNext();)
			{
				String core_name = iter.next().toString();
				
				tuple_cores.add(core_name);
			}
			
			
		}
		
	}
	
	
	public static citation_view_vector merge(citation_view_vector vec, citation_view c)
	{
		citation_view_vector insert_vec = new citation_view_vector(c);
		
		if(vec.tuple_cores.containsAll(insert_vec.tuple_cores))
		{
			return vec;
		}
		else
		{
			if(insert_vec.tuple_cores.containsAll(vec.tuple_cores))
			{
				return insert_vec;
			}
			else
			{
				return merge_vector(insert_vec, vec);
			}
		}
	}
	
	static citation_view_vector merge_vector(citation_view_vector vec1, citation_view_vector vec2)
	{
		Vector<citation_view> vec_new = (Vector<citation_view>) vec1.c_vec.clone();
		
		vec_new.addAll((vec2.c_vec));
		
		Vector<Character> index_new = (Vector<Character>) vec1.index_vec.clone();
		
		index_new.addAll(vec2.index_vec);
		
		HashSet<String> tuple_cores_new = (HashSet<String>) vec1.tuple_cores.clone();
		
		tuple_cores_new.addAll(vec2.tuple_cores);
		
		return new citation_view_vector(vec_new, index_new, tuple_cores_new);
		
		
	}
	
	@Override
	public String toString()
	{
		String str = new String();
		
		for(int i = 0; i<c_vec.size(); i++)
		{
			
			if(i >= 1)
				str += "*";
			str += c_vec.get(i).toString();
		}
		
		return str;
	}
	
	public static void main(String [] args)
	{
		Vector<Character> c_vec = new Vector<Character>();
		
		Vector<Character> vec = new Vector<Character>();
		
		
		c_vec.add('c');
		
		c_vec.add('a');
		
		c_vec.add('b');
		
		vec.add('b');
		
		vec.add('c');
		
		if(c_vec.containsAll(vec))
		{
			int y = 0; 
			y++;
		}
		
	}

}
