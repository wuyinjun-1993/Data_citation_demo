import java.sql.*;
import java.sql.DriverManager;



public class connect2db {
	
	public static String db_url = "jdbc:postgresql://localhost:5432/test";
	
	public static String usr_name = "wuyinjun";
	
	public static String passwd = "";
	
	public static void main(String args[]) {
//	      Connection c = null;
//	      ResultSet rs = null;
//	      PreparedStatement pst = null;
//	      try {
//	         Class.forName("org.postgresql.Driver");
//	         c = DriverManager
//	            .getConnection(db_url,
//	        	        usr_name, passwd);
//	         
//	         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
////	         pst = c.prepareStatement("SELECT *  FROM view_table");
//	         rs = pst.executeQuery();
//	         
//	      } catch (Exception e) {
//	         e.printStackTrace();
//	         System.err.println(e.getClass().getName()+": "+e.getMessage());
//	         System.exit(0);
//	      }
//	      System.out.println("Opened database successfully");
	      
	      System.out.println(isNumber("1 "));
	      
	   }
	
	
	    public static boolean isNumber(String s) {
	        boolean number_start = false;
	        
	        boolean minus = false;
	        
	        boolean plus = false;
	        
	        boolean dot = false;
	        
	        
	        boolean exponential_symbol = false;
	        
	        boolean exponential_minus = false;
	        
	        boolean exponential_plus = false;
	        
	        boolean number_end = false;
	        
	        char last_symbol = 0;
	        
	        for(int i = 0; i<s.length(); i++)
	        {
	            char c = s.charAt(i);
	            
	            if(c != '-' && c != '+' && c != '.' && (c < '0' || c > '9') && c != 'e')
	                return false;
	            
	            if(number_end && c != ' ')
	            {
	                return false;
	            }
	            
	            if(c == '-' || c == '+' || c == '.' || (c >= '0' && c <= '9'))
	            {
	                number_start = true;
	            }
	            else
	            {
	                if(!number_start && c != ' ')
	                    return false;
	            }
	            
	            if(c == ' ')
	            {
	                if(number_start)
	                {
	                    number_end = true;
	                }
	            }
	            
	            
	            if(c == '+')
	            {
	                if(!plus)
	                {
	                    plus = true;
	                }
	                else
	                {
	                    
	                    if(!exponential_plus)
	                    {
	                        if(last_symbol != 'e')
	                        {
	                            return false;
	                        }
	                        else
	                        {
	                            exponential_plus = true;
	                        }
	                    }
	                    else
	                    {
	                        return false;   
	                    }
	                    
	                }    
	            }
	            
	            if(c == '-')
	            {
	                if(!minus)
	                {
	                    minus = true;
	                }
	                else
	                {
	                    
	                    if(!exponential_minus)
	                    {
	                        if(last_symbol != 'e')
	                        {
	                            return false;
	                        }
	                        else
	                        {
	                            exponential_minus = true;
	                        }
	                    }
	                    else
	                    {
	                        return false;   
	                    }
	                    
	                }    
	            }
	            
	            if(c == 'e')
	            {
	                if(!exponential_symbol)
	                {
	                    exponential_symbol = true;
	                }
	                else
	                    return false;
	            }
	            
	            if(c == '.')
	            {
	                if(!dot)
	                {
	                    if(last_symbol >= '0' && last_symbol <= '9')
	                        dot = true;
	                    else
	                    {
	                        if(last_symbol == ' ' || last_symbol == '+' || last_symbol == '-' || i == 0)
	                        {
	                            dot = true;
	                        }
	                        else
	                        {
	                            return false;
	                        }
	                    }    
	                }
	                else
	                {
	                    return false;   
	                }
	            }
	            
	            if(last_symbol == '.')
	            {
	                if(c > '9' || c < '0')
	                    return false;
	            }
	            
	            if(last_symbol == 'e')
	            {
	                if(c > '9' || c < '0')
	                {
	                    if(c != '+' && c != '-')
	                        return false;
	                }
	            }
	            
	            if(last_symbol == '+' || last_symbol == '-')
	            {
	                if(c > '9' || c < '0')
	                {
	                    if(c != 'e')
	                        return false;
	                }
	            }
	            
	         
	            last_symbol = c;
	        }
	        
	        if(last_symbol == '.' || last_symbol == '+' || last_symbol == '-')
	        {
	            return false;
	        }
	        
	        return true;
	        
	}

}
