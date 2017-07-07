package edu.upenn.cis.citation.ui;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
//import org.apache.naming.java.javaURLContextFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class CitationConverter {
	public static String rdfPath = "/Users/gaoyan/workspace/DataCitation/JenaWeb_v2.4/DATA_1.0_0416/Output";
	public static void main(String [] args)
	{
		Dataset dataset = TDBFactory.createDataset(rdfPath);
		dataset.begin(ReadWrite.READ);
		Model model = initializeModel(dataset);
		String eID = "<http://eagle-i.itmat.upenn.edu/i/00000138-81cd-dbef-9cd7-d7e280000000>";
//		String citeResult = "{\"citation\":[{\"name\":[\"RNA-Seq Unified Mapper\"]},{\"developerName\":[\"Grant, Gregory R., Ph.D.\",\"DeLaurentis, Michael\",\"Pizarro, Angel\"]},{\"usedBy\":[\"University of Pennsylvania\"]},{\"URL\":[\"https://github.com/PGFI/rum\",\"http://www.cbil.upenn.edu/RUM/userguide.php\"]},{\"eagle-i_ID\":[\"http://eagle-i.itmat.upenn.edu/i/0000013a-3178-df7b-01af-beb880000000\"]}]}";
		String citeResult = "";
		citeResult = getCitationbyModel(eID, model);
		System.out.println("[cite result] " + citeResult);
		try 
		{
			System.out.println("[convertToXML] ");
			System.out.println(convertToXML(citeResult));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		try {
		//	convertToBiblatex(citeResult);
			System.out.println("[convertToBiblatex] ");
			System.out.println(convertToBiblatex(citeResult, "others"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("[convertToRIS] ");
			System.out.println(convertToRIS(citeResult));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		model.close();
		
		dataset.end();
		
//		checkReliability();
		
	}
	
	public static String getCitation(String eID)
	{
		Dataset dataset = TDBFactory.createDataset(rdfPath);
		dataset.begin(ReadWrite.READ);	
		Model model = initializeModel(dataset);	
		String citation = getCitationbyModel(eID, model);
		model.close();
		return citation;
	}
	
	public static String getCitation(String eID, Model model)
	{
		String error = "{\"citation\": [{\"error\":[\"No Citation Schema or Data Available! Check your input argument. "
				+ "An eagle-i ID appears in eagle-i webpage URL and is like: http://eagle-i.itmat.upenn.edu/i/0000013a-3178-df7b-01af-beb880000000\"]}]}";
		String citation = "";
		eID = eID.replaceAll(" ", "");
		try
		{
			citation = getCitationbyModel(eID, model);
		}
		catch (Exception e)
		{
			return error;
		}
		return citation;
	}
	
	public static String getCitationbyModel(String eID, Model model)
	{
		String pstm = "SELECT * \n"
				+ "WHERE { " + eID + " a ?categoryNode	. \n"
				+ "?categoryNode rdfs:subClassOf* ?parentNodes .\n"
				+ "OPTIONAL{ ?parentNodes owl:someValuesFrom ?property } \n"
				+ "OPTIONAL{ ?parentNodes owl:equivalentClass ?equClass1. "
				+ "?equClass1 owl:intersectionOf ?equClass2."
				+ "?equClass2 rdf:first ?equClass3."
				+ "?equClass3 rdfs:subClassOf* ?parentNodes2 } } ";
	
		ParameterizedSparqlString pss = testQuery(pstm);
		Query query = QueryFactory.create(pss.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();

		String outStr = ResultSetFormatter.asText(results);
//		System.out.println(pstm + " \n" + outStr);

		//Software
		if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000071>") != -1)
		{
			String tempOutStr = getSoftwareCitation(eID,model);
			return tempOutStr;
		}
		//BioSpec
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000020>") != -1)
		{
			String tempOutStr = getBioSpecCitation(eID,model);
			return tempOutStr;
		}
		//Database
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0001716>") != -1)
		{
			String tempOutStr = getSoftwareCitation(eID,model);
			return tempOutStr;
		}
		//Human Study
		//Various types, how to determine? 
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000004>") != -1)
		{
			String tempOutStr = getInstrumentCitation(eID,model);
			return tempOutStr;
		}
		//instrument
		//Are we going to use manufacturer? (Too many)
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000015>") != -1)
		{
			String tempOutStr = getHumanStudyCitation(eID,model); 
			return tempOutStr;
		}
		//Organism or Virus
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/OBI_0100026>") != -1)
		{
			String tempOutStr = getOrganismCitation(eID,model);
			return tempOutStr;
		}
		//Protocol
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/OBI_0000272>") != -1)
		{
			String tempOutStr = getProtocolCitation(eID,model);
			return tempOutStr;
		}
		//Serious problem with reagent: many parallel reagent types in Eagle-i ontology. 
		//How to specify different kinds of reagent? 
		//Reagent
		//use property_roles
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/OBI_0000086>") != -1 ||
				outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000006>") != -1 || 
				outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000285>") != -1 )
		{
			String tempOutStr = getReagentCitation(eID,model); 
			return tempOutStr;
		}
		//Research Oppotunity
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000595>") != -1)
		{
			String tempOutStr = getResearchOppotunityCitation(eID,model);
			return tempOutStr;
		}
		//Service
		//Ignore citation of service fee
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/ERO_0000005>") != -1)
		{
			String tempOutStr = getServiceCitation(eID,model); 
			return tempOutStr;
		}
		else if(outStr.indexOf("<http://purl.obolibrary.org/obo/BFO_0000001>") != -1)
		{
			String tempOutStr = getDefaultCitation(eID,model); 
			return tempOutStr;
		}
		else
		{
			return "{\"citation\": [{\"error\":[\"No Citation Schema or Data Available! Check your input argument. "
					+ "An eagle-i ID is like: http://eagle-i.itmat.upenn.edu/i/0000013a-3178-df7b-01af-beb880000000\"]}]}";
		}
	}
	
	static Dataset populateTDB(Dataset ds, String name, String dataPath) 
	{
		Model tdb = ds.getNamedModel(name);

		// read the input file
		FileManager.get().readModel(tdb, dataPath);

		return ds;
	}
	
	public static Model initializeModel(Dataset ds)
	{
		// load the default model
		Model model = ModelFactory.createDefaultModel();

		// get all the named graphs in the TDB
		Iterator<String> graphNames = ds.listNames();
		while (graphNames.hasNext()) 
		{
			String graphName = graphNames.next();
			model.add(ds.getNamedModel(graphName));
		}
		
		return model;
	}

	
	private static ParameterizedSparqlString testQuery(String pstm) 
	{

		// prepared statement initialization
		ParameterizedSparqlString pss = new ParameterizedSparqlString();

		// set the prefixes
		pss.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		pss.setNsPrefix("dc", "http://purl.org/dc/terms/");
		pss.setNsPrefix("obo", "http://purl.obolibrary.org/obo/");
		pss.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		pss.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		pss.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		pss.setNsPrefix("", "http://eagle-i.org/ont/repo/1.0/");
		pss.setNsPrefix("sesame", "http://www.openrdf.org/schema/sesame#");
		pss.setNsPrefix("fn", "http://www.w3.org/2005/xpath-functions#");
		
		pss.setCommandText(pstm);
		
		return pss;

	}
	

	private static String GetSerializedOutputFromQueryResult(ResultSet qresults)
	{
		String resultText= ResultSetFormatter.asText(qresults);
	//	ResultSetFormatter.outputAsJSON(qresults);

		Pattern pattern = Pattern.compile("\\\"(.*?)\\\"");
		Matcher matcher = pattern.matcher(resultText);
		String outputText ="";
		while(matcher.find())
		{
			outputText += matcher.group()+"; ";
		}
		outputText=outputText.replaceAll("\"", "");
		
		
		return outputText;
	}
	
	private static String GetSerializedOutputFromQueryResult(ResultSet qresults, String eID)
	{
//		String outputText ="";
//		ResultSetFormatter.output(qresults);
		// TODO
		ResultSet q1 = qresults;
		ResultSet q2 = qresults;
		ResultSet q3 = qresults;
//		System.out.println("[outputAsJSON] ");ResultSetFormatter.outputAsJSON(q1);
//		System.out.println("[outputAsXML] ");ResultSetFormatter.outputAsXML(q2);
//		String resultText1 = ResultSetFormatter.asText(qresults);
//		String resultText1 = ResultSetFormatter.asXMLString(qresults);
//		System.out.println(resultText1);
		Map<String, List<String>> answer = new LinkedHashMap<String, List<String>>();
		List<String> queryTitle = qresults.getResultVars();
		
		while(qresults.hasNext())
		{
			QuerySolution soln = qresults.next();
			Iterator<String> names = soln.varNames();

			while (names.hasNext())
			{
				String var = names.next();
				List<String> thisRow = new ArrayList<String>();
				if(qresults.getRowNumber() > 1)
				{
					thisRow = answer.get(var);
					Iterator<String> iter = thisRow.iterator();
					int listContainsItem = 0;
					while(iter.hasNext())
					{
						if(iter.next().equals(soln.get(var).toString())) 
							listContainsItem = 1;
						
					}
					if(listContainsItem == 0)
						thisRow.add(soln.get(var).toString());
				}
				else
				{
					thisRow.add(soln.get(var).toString());
				}
				answer.put(var, thisRow);
			}

		}
		System.out.println("[answer ]" + answer);
		
		Map<String, List<String>> eachItem = new LinkedHashMap<String, List<String>>();
		List<Map<String, List<String>>> finalCitation = new ArrayList<Map<String, List<String>>>();
		for(int i = 0; i < queryTitle.size(); i ++)
		{
	//		System.out.println(queryTitle.get(i));
			if(answer.containsKey(queryTitle.get(i)))
			{
				eachItem = new LinkedHashMap<String, List<String>>();
				eachItem.put(queryTitle.get(i), answer.get(queryTitle.get(i)));
				finalCitation.add(eachItem);
			}
		}
		eachItem = new LinkedHashMap<String, List<String>>();
		List<String> eIDList = new ArrayList<String>();
		eIDList.add(eID.substring(1,eID.length() - 1));
		eachItem.put("eagle-i_ID", eIDList);
		finalCitation.add(eachItem);
	//	System.out.println(finalCitation);
		JSONArray content = new JSONArray(finalCitation);
		JSONObject citeInJSON = new JSONObject();
		citeInJSON.put("citation", content);
		
		return citeInJSON.toString();
	}

	private static String getSoftwareCitation(String eID, Model model)
	{
	//	ResultSet qresultName = queryGeneralInfo(model, eID, "?developerName ?manuName ?usedBy ((?name + ' ' + STR(?version + ' ')) AS ?softName) ?URL");
		ResultSet qresultName = queryGeneralInfo(model, eID, "?developerName ?manuName ?usedBy ?name ((\"Ver.\" + ?version) AS ?softVer) ?URL");
	//	ResultSet qresultName = queryGeneralInfo(model, eID, "?developerName ?manuName ?usedBy (CONCAT(?name, \", \", ?version, \" \") AS ?softName) ?URL");
		
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getBioSpecCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?location ?name (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getHumanStudyCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?performedBy  ?topicName ?subjectArea ?name (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getInstrumentCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?location ?name ?modelNumber (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getOrganismCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?location ?name (LCASE(STR(?resourceType)) AS ?type)");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getProtocolCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?usedBy ?author ?name (LCASE(STR(?resourceType)) AS ?type)");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getReagentCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?location ?name (LCASE(STR(?resourceType)) AS ?type) (('InventoryNumber: ' + ?categoryNumber) AS ?inventoryNumber) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getResearchOppotunityCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?researchProvider ?name (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getServiceCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?serviceProvider ?name (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static String getDefaultCitation(String eID, Model model)
	{
		ResultSet qresultName = queryGeneralInfo(model, eID, "?name (LCASE(STR(?resourceType)) AS ?type) ?URL");
		String serialOutput = GetSerializedOutputFromQueryResult(qresultName,  eID);
		return serialOutput; 
	}
	
	private static ResultSet queryGeneralInfo(Model model, String eID, String Item)
	{
		String pstm = "SELECT DISTINCT " + Item + " \n"
				+ "WHERE	{	" +eID +" rdfs:label ?name; \n"
				+ "OPTIONAL {"+eID+" <http://purl.obolibrary.org/obo/ERO_0000480> ?URL} \n"
				+ "OPTIONAL {"+eID+" a ?resourceTypeID. \n"
					+ "?resourceTypeID rdfs:label ?resourceType} \n"
				+ "OPTIONAL{"+eID+" <http://purl.obolibrary.org/obo/ERO_0000034> ?manuId. "
					+ "?manuId rdfs:label ?manuName} \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000072> ?version } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0001521> ?performedByID. \n"
					+ " ?performedByID rdfs:label ?performedBy } \n"
				+ "OPTIONAL{"+eID+" <http://purl.obolibrary.org/obo/ERO_0000070> ?usedByID . "
					+ "?usedByID rdfs:label ?usedBy} \n"
				+ "OPTIONAL{ "+eID+" <http://eagle-i.org/ont/app/1.0/has_subject_area> ?subjectAreaID. \n"
					+ "?subjectAreaID rdfs:label ?subjectArea } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000234>  ?topicNameID. \n"
					+ "?topicNameID rdfs:label ?topicName } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/RO_0001025>  ?locationID. \n"
					+ "?locationID rdfs:label ?location } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000232>  ?authorID. \n"
					+ "?authorID rdfs:label ?author } \n"
				+ "OPTIONAL{ "+eID+" <http://eagle-i.org/ont/app/1.0/research_opportunity_provided_by> ?researchProviderID. \n"
					+ "?researchProviderID rdfs:label ?researchProvider } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000390> ?serviceProviderID. \n"
					+ "?serviceProviderID rdfs:label ?serviceProvider } \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000050>  ?modelNumber} \n"
				+ "OPTIONAL{ "+eID+" <http://purl.obolibrary.org/obo/ERO_0000044> ?categoryNumber} \n"
				+ "OPTIONAL{"+eID+" <http://purl.obolibrary.org/obo/ERO_0001719> ?developerID. "
					+ "?developerID rdfs:label ?developerName} }";
				
		
		ParameterizedSparqlString pss = testQuery(pstm);
		
	//	System.out.println(pss);
		
		Query query = QueryFactory.create(pss.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet qresults = qexec.execSelect();
		return qresults;
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
	
	public static boolean checkReliability()
	{
		Dataset dataset = TDBFactory.createDataset(rdfPath);
		Model model = ModelFactory.createDefaultModel();
		Model upennModel = ModelFactory.createDefaultModel(); 


//		model.add(dataset.getNamedModel(harvardName));
//		model.add(dataset.getNamedModel(eroName));
//		model.add(dataset.getNamedModel(globalName));
//		
//		upennModel.add(dataset.getNamedModel(harvardName));
		
		String pstm = "SELECT ?eID ?typeName \n"
				+ "WHERE {?eID :hasWorkflowState :WFS_Published;"
				+ "a ?typeNode. ?typeNode rdfs:label ?typeName}";

		ParameterizedSparqlString pss = testQuery(pstm);
		Query query = QueryFactory.create(pss.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, upennModel);
		ResultSet qresults = qexec.execSelect();

	//	String outStr = ResultSetFormatter.asText(qresults);

		
		String checkResult = "";
		while(qresults.hasNext())
		{
			QuerySolution soln = qresults.next();
			Iterator<String> names = soln.varNames();
			
			String normalCitation = "";
			boolean noCitation = false;
			while (names.hasNext())
			{
				
				String var = names.next();
				String tempEID = "";
		//		System.out.println(tempEID);
				if(var.equals("eID"))
				{
					tempEID = "<"+ soln.get(var).toString().trim()+">";
					normalCitation = jsonToCitaton(getCitationbyModel(tempEID, model));
					if(normalCitation.contains("No Citation"))
						noCitation = true;
					else noCitation = false;
					if(noCitation) checkResult += String.format("%-75s", tempEID) + "|\t";
				}
				else
				{
					int strLength = soln.get(var).toString().length();
					if(noCitation) checkResult += String.format("%-30s", 
							soln.get(var).toString().substring(0, Integer.min(27, strLength))) + "|\t";
				}
			}
			if(noCitation) checkResult += normalCitation + "\r\n";
		}

		System.out.println(checkResult);
		try 
		{
			String filePath = "test.txt";
			File file1 = new File(filePath);
			if (file1.exists()) 
			{  
	          //  System.out.println("Exist");  
	        } 
			else 
	        {  
	          //  System.out.println("Not Exist");  
				file1.createNewFile();
	
	        } 
			BufferedWriter output = new BufferedWriter(new FileWriter(file1));  
	        
			output.write(checkResult);
	        output.close(); 
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return false;
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



