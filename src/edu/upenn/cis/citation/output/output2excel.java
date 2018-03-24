package edu.upenn.cis.citation.output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import  org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.citation_view.Covering_set;

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
	
	public static void citation_output(ResultSet rs, Query q, Vector<Vector<String>> values , Vector<Vector<Covering_set>> c_views, String file_name, Vector<Vector<String>> citation_strs) throws SQLException
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
		
		
		for(int i = 0; i<c_views.get(0).size();i++)
		{			
			String name = "covering_set_" + i;
			
			Cell cell = row.createCell(2*i + q.head.args.size());
			
			cell.setCellValue(name);
			
			Cell cell_c = row.createCell(2*i + q.head.args.size() + 1);
			
			String citation_str = "citation_" + i;
			
			cell_c.setCellValue(citation_str);
			
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
			
			Vector<Covering_set> c_vec = c_views.get(k);
			
			for(int i = 0; i<c_vec.size();i++)
			{				
				Cell cell = row.createCell(2*i + q.head.args.size());

//				String citation_str = new String();
//				
//				for(int p = 0; p<c_vec.size(); p++)
//				{
//					if(p >= 1)
//						citation_str += "+";
//					
//					citation_str += c_vec.get(p);
//					
//				}
				
				cell.setCellValue(c_vec.get(i).toString());
				
				Cell cell_c = row.createCell(2*i + 1 + q.head.args.size());
				
				
				cell_c.setCellValue(citation_strs.get(k).get(i));
				
				
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
	
	public static void citation_output_row(ResultSet rs, Query q, Vector<String> values , Vector<Covering_set> c_views, String file_name, int row_id, Vector<String> citation_str) throws SQLException, IOException, InterruptedException
	{
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		int col_num = rsmd.getColumnCount();
		
        XSSFWorkbook workbook = null;
        //Create a blank sheet
        XSSFSheet sheet = null;
        
        if(row_id == 0)
        {
        	workbook = new XSSFWorkbook();
        	
        	sheet = workbook.createSheet("123");
        }
        else
        {
//        	FileInputStream inputStream = new FileInputStream(new File(file_name));
        	
    		workbook = new XSSFWorkbook(file_name);
        	
        	sheet = workbook.getSheet("123");

        }
        
		
        Row row = sheet.createRow(row_id);
        
//        System.out.println(row_id + ":" + values.get(0));
        
//		for(int i = 0; i<q.head.args.size(); i++)
//		{
//			String name = rsmd.getColumnName(i + 1);
//			
//			
//			Cell cell = row.createCell(i);
//			
//			cell.setCellValue(name);
//			
//		}
//		
//		
//		for(int i = 0; i<1;i++)
//		{			
//			String name = "citation_view";
//			
//			Cell cell = row.createCell(i + q.head.args.size());
//			
//			cell.setCellValue(name);
//			
//		}
		
		
//		for(int k = 0; k<values.size(); k++)
//		{
//			row = sheet.createRow(k + 1);
			
//			Vector<String> vals = values.get(k);
			
			for(int i = 0; i<q.head.args.size(); i++)
			{
	
				Cell cell = row.createCell(i);
				
				cell.setCellValue(values.get(i));
				
			}
			
//			Vector<citation_view_vector> c_vec = c_views.get(k);
			
			for(int i = 0; i<c_views.size();i++)
			{				
				Cell cell = row.createCell(2*i + q.head.args.size());
				
				cell.setCellValue(c_views.get(i).toString());
				
				Cell cell_c = row.createCell(2*i + q.head.args.size() + 1);
				
				cell_c.setCellValue(citation_str.get(i));

//				String citation_str = new String();
//				
//				for(int p = 0; p<c_views.size(); p++)
//				{
//					if(p >= 1)
//						citation_str += "+";
//					
//					citation_str += c_views.get(p);
//					
//				}
//				
//				cell.setCellValue(citation_str);

				
				
			}
			
		
			
		int y = 0;
			
		while(true)
		{
			y++;
			if(y < 1000000)
				break;
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
