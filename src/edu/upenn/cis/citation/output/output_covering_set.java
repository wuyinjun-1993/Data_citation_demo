package edu.upenn.cis.citation.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import edu.upenn.cis.citation.citation_view.Covering_set;

public class output_covering_set {
  
  public static void write2file(String file_name, HashSet views) throws IOException
  {
      File fout = new File(file_name);
      FileOutputStream fos = new FileOutputStream(fout);
   
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
   
      if(views != null && !views.isEmpty())
      for (Object view: views) {
          bw.write(view.toString());
          bw.newLine();
      }
   
      bw.close();
  }
  
  public static void write2file(String file_name, HashMap<String, HashSet<Covering_set>> views) throws IOException
  {
    File fout = new File(file_name);
    FileOutputStream fos = new FileOutputStream(fout);
 
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
    Set<String> group_label = views.keySet();
    
    int num = 0;
    
    for(String label: group_label)
    {
      bw.write("group " + num);
      bw.newLine();
      
      HashSet<Covering_set> covering_sets = views.get(label);
      
      String [] covering_set_string = new String [covering_sets.size()];
      
      int id = 0;
      
      for(Covering_set covering_set: covering_sets)
      {
        covering_set_string[id ++] = covering_set.toString(); 
      }
      
      Arrays.sort(covering_set_string);
      
      for(String covering_set_str: covering_set_string)
      {
        bw.write(covering_set_str);
        bw.newLine();
      }
      
      num++;
      
    }
    
    bw.close();

  }
 

}
