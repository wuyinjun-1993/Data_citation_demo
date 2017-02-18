package output;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import  org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import Corecover.Query;
import Corecover.Subgoal;
import citation_view.citation_view_vector;

public class output2excel {
	
	public static void main(String[] args)
	{
		XSSFWorkbook workbook = new XSSFWorkbook(); 
        
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Employee Data.xlsx");
          
        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"ID", "NAME", "LASTNAME"});
        data.put("2", new Object[] {1, "Amit", "Shukla"});
        data.put("3", new Object[] {2, "Lokesh", "Gupta"});
        data.put("4", new Object[] {3, "John", "Adwards"});
        data.put("5", new Object[] {4, "Brian", "Schultz"});
          
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("howtodoinjava_demo.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	
	public static void citation_output(ResultSet rs, Query q, Vector<Vector<String>> values , Vector<Vector<citation_view_vector>> c_views, String file_name) throws SQLException
	{
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		int col_num = rsmd.getColumnCount();
		
		XSSFWorkbook workbook = new XSSFWorkbook(); 
        
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(file_name);
		
		
        Row row = sheet.createRow(0);
        
        
        
		for(int i = 0; i<q.head.args.size(); i++)
		{
			String name = rsmd.getColumnName(i + 1);
			
			
			Cell cell = row.createCell(i);
			
			cell.setCellValue(name);
			
		}
		
		
		for(int i = 0; i<1;i++)
		{			
			String name = "citation_view";
			
			Cell cell = row.createCell(i + q.head.args.size());
			
			cell.setCellValue(name);
			
		}
		
		
		for(int k = 0; k<values.size(); k++)
		{
			row = sheet.createRow(k + 1);
			
			Vector<String> vals = values.get(k);
			
			for(int i = 0; i<q.head.args.size(); i++)
			{
	
				Cell cell = row.createCell(i);
				
				cell.setCellValue(vals.get(i));
				
			}
			
			Vector<citation_view_vector> c_vec = c_views.get(k);
			
			for(int i = 0; i<1;i++)
			{				
				Cell cell = row.createCell(i + q.head.args.size());

				String citation_str = new String();
				
				for(int p = 0; p<c_vec.size(); p++)
				{
					if(p >= 1)
						citation_str += "+";
					
					citation_str += c_vec.get(p);
					
				}
				
				cell.setCellValue(citation_str);

				
				
			}
			
		}
		
	
		try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(file_name), true);
            workbook.write(out);
            out.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	

}
