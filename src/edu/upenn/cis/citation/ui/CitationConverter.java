package edu.upenn.cis.citation.ui;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


public class CitationConverter {
	
  public static String formatCitation(String citation) {
    String[] split = citation.split("\\},");
    String jsonString = "";
    for (int i = 0; i < split.length; i++ ) {
        if(i == 0)
            jsonString += "{\"citation\":[" + split[i];
        else {
            jsonString += split[i] + "},";
        }
    }
    jsonString += "]}";
    return jsonString;
}
  
  public static void main(String [] args)
  {
    HashMap<String, HashSet<String>> maps = new HashMap<String, HashSet<String>>();
    
    HashSet<String> set1 = new HashSet<String>();
    
    set1.add("2");
    
    HashSet<String> set2 = new HashSet<String>();
    
    set2.add("4");
    
    maps.put("1", set1);
    
    maps.put("3", set2);
    
    HashMap<String, HashSet<String>> maps2 = new HashMap<String, HashSet<String>>();
    
    HashSet<String> set3 = new HashSet<String>();
    
    set3.add("2");
    
    HashSet<String> set4 = new HashSet<String>();
    
    set4.add("4");
    
    maps2.put("5", set3);
    
    maps2.put("6", set4);
    
    HashSet<HashMap<String, HashSet<String>>> super_maps = new HashSet<HashMap<String, HashSet<String>>>();
    
    super_maps.add(maps);
    
    super_maps.add(maps2);
    
    System.out.println(formatCitation(super_maps));
  }
  
  static String add_qutation(HashSet<String> set)
  {
    String string = "[";
    
    int count = 0;
    
    for(String s : set)
    {
      if(count >= 1)
        string += ",";
      
      string += "\"" + s + "\"";
      
      count ++;
    }
    
    string += "]";
    
    return string;
  }
  
  public static String formatCitation(HashSet<HashMap<String, HashSet<String>>> citation_infos) {
    
    String string = "{\"citation\":[";
    
    int citation_count = 0;
    
    for(HashMap<String, HashSet<String>> citation_info : citation_infos)
    {
      Set<String> keys = citation_info.keySet();
      
      if(citation_count >= 1)
        string += ",";
      
      int count = 0;
      
      string += "{";
      
      for(String key :keys)
      {
        if(count >= 1)
          string += ",";
        
        string += "\"" + key + "\"" + ":" + add_qutation(citation_info.get(key));
        
        count ++;
      }
      
      string += "}";
      
      citation_count ++;
    }
    
    
    string += "]}";
    
    return string;
}
	
	public static String convertToXML(String jsonStr) throws Exception 
	{
		//TODO: 
		JSONObject jsObj = new JSONObject(jsonStr);
		JSONArray jsArray = jsObj.getJSONArray("citation");
		JSONObject eachNode = new JSONObject();
		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

		Element citationRoot = doc.createElement("citation");
		
		for(int i = 0; i < jsArray.length(); i ++)
		{
			eachNode = jsArray.getJSONObject(i);
			String thisKey = eachNode.keys().next().toString();
	//		System.out.println(thisKey);
			Element nodeElement = doc.createElement(thisKey);
			JSONArray tempArray = eachNode.getJSONArray(thisKey);
			if(tempArray.length() <= 1)//<name>
			{
				String tempContent = tempArray.getString(0);
				Text nodeContent = doc.createTextNode(tempContent);
				nodeElement.appendChild(nodeContent);
			}
			else//<names/>
			{
				nodeElement = doc.createElement(thisKey+"s");
				for(int j = 0; j < tempArray.length(); j ++)
				{
					String tempContent = tempArray.getString(j);
					Element tempElement = doc.createElement(thisKey);
					Text nodeContent = doc.createTextNode(tempContent);
					tempElement.appendChild(nodeContent);
					nodeElement.appendChild(tempElement);
				}
			}

			citationRoot.appendChild(nodeElement);
		}
  
        doc.appendChild(citationRoot);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", new Integer(2));
        Transformer t= tf.newTransformer();
        
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.setOutputProperty(OutputKeys.INDENT,"yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        
//        FileOutputStream out = new FileOutputStream(file);
//        StreamResult xmlResult = new StreamResult(out);
//        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(new File("f:/text.xml"))));
        t.transform(new DOMSource(doc), new StreamResult(bos));

		return bos.toString();
	}
	
	public static String jsonToCitaton(String jsonCitation)
	{
		JSONObject jsObj = new JSONObject(jsonCitation);
		String parseXML = XML.toString(jsObj);
		
		String xmlCitation = "";
        try 
        {
			xmlCitation = CitationConverter.convertToXML(jsonCitation);
		} 
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuffer normalCitation = new StringBuffer(); 
		String reg = "(<.*?>)*(.*?)</.*?>";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(parseXML);
        while(m.find())
        {
        	String s1 = m.group(2);
            //System.out.println("data-->"+s1);
            normalCitation.append(s1);
            normalCitation.append("; ");
        }
 //       System.out.println(xmlCitation);
		
		//send normal citation back to web page as response
        String citationResult = normalCitation.toString();
        citationResult = citationResult.substring(0, citationResult.length() - 2);
  //      citationResult = citationResult.replaceAll("; ;", ";");
        return citationResult;
	}
	
	
	
	public static String convertToBiblatex(String jsonStr, String type)
	{
		String biblaTexEntry = "";
		JSONObject jsObj = new JSONObject(jsonStr);
		JSONArray jsArray = jsObj.getJSONArray("citation");

		System.out.println("");
		String citationKey = "";
	//	String citationKey = jsArray.getJSONObject(0).getJSONArray("name").toString();
		for(int i = 0; i < jsArray.length(); i ++)
		{
			String tempKey = jsArray.getJSONObject(i).keySet().toString();
	//		System.out.println(tempKey);
			if(tempKey.contains("name")) 
			{
//				citationKey = jsArray.getJSONObject(i).getJSONArray("name").toString();
				citationKey = jsArray.getJSONObject(i).getJSONArray("author").toString();
				break;
			}
		}
		citationKey = citationKey.replace("[", "");
		citationKey = citationKey.replace("]", "");
		citationKey = citationKey.replace("\"", ""); // Get rid of "
		citationKey = citationKey.replace(" ", "");
		citationKey = citationKey.replace("(", "");
		citationKey = citationKey.replace(")", "");
		citationKey = citationKey.replace("-", "");
		citationKey = citationKey.replace("_", "");
		citationKey = citationKey.toLowerCase();
		//keySet(); gets the name (TAG) from the object
		
		/*We are keeping track of the type because if the type is "software" we are going to create the BibTex with
		 * entry type @software. When the type is not "software" we are going to use @customA*/
		if ("software".equals(type)){
			biblaTexEntry = "@software{" +  citationKey + ",";
		}else{
			biblaTexEntry = "@customa{" +  citationKey + ",";
		}
		
		for (int i = 0; i < jsArray.length(); i++){
			// Example:
			// TAG = name
			// content = RNA-Seq Unified Mapper
			// finalLine = name = {RNA-Seq Unified Mapper},
			

			// Gets the TAGS, they will be [name], [title], [url], etc.
				String TAG = jsArray.getJSONObject(i).keySet().toString();
				TAG = TAG.replace("[", "");
				TAG = TAG.replace("]", "");
				if(TAG.equals("inventoryNumber")) continue;
				
				//Gets the contante whithin the TAG
				String content = jsArray.getJSONObject(i).getJSONArray(TAG).toString();
				content = content.replace("\",\"", " and "); // Bibtex use and instead of commas to separe names
				content = content.replace("[", "");
				content = content.replace("]", "");
				content = content.replace("\"", "");
				
				/* Many tags are comming with names that are not supported by standard BibTex, we are changing
				 * these tags names so they will work and also make more sense*/
				switch (TAG) 
				{
					case "name":
						TAG = "title";
						break;
					case "developerName":
						TAG = "author";
						break;
					case "manuName":
						TAG = "author";
						break;			
					case "performedBy":
						TAG = "author";
						break;	
					case "usedBy":
						TAG = "organization";
						break;
					case "location":
						TAG = "organization";
						break;
					case "researchProvider":
						TAG = "organization";
						break;
					case "serviceProvider":
						TAG = "organization";
						break;
					case "URL":
						TAG = "howpublished";
						break;
					case "eagle-i_ID":
						TAG = "url";
						break;
			//		case "resourceType":
					case "inventoryNumber":
						TAG = "version";
						break;
					case "softVer":
						TAG = "version";
						break;
					case "type":
						TAG = "type";
						break;
					default:
						break;
				}
				
				// The final BibTex line
				String finalLine = TAG + " = {" + content + "},";
				
				// This is necessary to BiblaTex work
				if (TAG == "howpublished")
				{
					finalLine = TAG + " = \"" + "\\url{" + content + "}\",";
				}
				
				biblaTexEntry = biblaTexEntry + "\n" + finalLine;

		}

		
		biblaTexEntry = biblaTexEntry + "\n}";
		System.out.println(biblaTexEntry);
		System.out.println(" ");

		// We will return biblaTexEntry
		return biblaTexEntry;
		
	}
	///RIS
	// string parsing is a good approach to create BibTex document
	public static String convertToRIS(String jsonStr) 
	{	
		// first tag that has to be above every RIS entry 
		String risCitation = "";
		risCitation += "TY"+ "  - " + "DBASE" + "\n";
		System.out.println("\n"+"TY"+ "  - " + "DBASE");
		// array stores the two letter tags used in RIS 
		String[][] abbrevation = {{"name","TI"},{"developerName","AU"},{"usedBy", "DP"},{"URL","UR"},{"eagle-i_ID","ID"},{"type","M3"},{"softVer","M2"},
				{"manuName","AU"},{"performedBy", "AU"},{"location","DP"},{"researchProvider","DP"},{"serviceProvider","DP"},{"inventoryNumber","M1"},{"author", "AU"},{"title","TI"}
				};
		
		JSONObject jsObj = new JSONObject(jsonStr);
		JSONArray jsArray = jsObj.getJSONArray("citation");
		
		for (int i = 0; i < jsArray.length(); i++){
			// getting the key for each part of the data (example: name) 
			String Key = jsArray.getJSONObject(i).keySet().toString();
		    Key = Key.replace("[","");
		    Key = Key.replace("]","");
		    //for loop checks the double array created above for the key 
			 for(int l = 0; l < abbrevation.length; l++){
				 //if key is found in the array then the content from the JSONArray is retrieved (example: ["RNA-Seq Unified Mapper"]);
				 if (abbrevation[l][0].equals(Key)){
					 String[] subject = (jsArray.getJSONObject(i).getJSONArray(Key).toString()).split("\",\"");
					 // loop goes through the content array 
				     for(int j = 0; j < subject.length; j++){
				    	 if(j>0){
				    		 abbrevation[l][1] = abbrevation[l][1].replace((abbrevation[l][1]).charAt(1),Integer.toString(j+1).charAt(0));
				    	 }
				    	 	System.out.print(abbrevation[l][1]+"  - ");
				    	 	risCitation += abbrevation[l][1]+"  - ";
				    	 	subject[j] = subject[j].replace("[\"", "");
				    	 	subject[j] = subject[j].replace("\"]", "");
				        	System.out.println(subject[j]);
				        	risCitation += subject[j] + "\n";
				     }
				 }
			 }
	      
			}
		System.out.println("ER"+ "  - ");
		risCitation += "ER"+ "  - " + "\n";
		return risCitation;
	
	}
	
}



