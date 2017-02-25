package edu.upenn.cis.citation.ui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.dao.Database;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2;

public class Util {
	
	public static String convertToDatalog(List<Entry> list) {
        if (list == null || list.size() == 0) return "";
        Set<String> tables = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);
            tables.add(e.getTable());
            if (i == 0) sb.append("q(");
            if (e.getShow()) sb.append(e.getTable() + "_c_" + e.getField() + ", ");
            if (i == list.size() - 1 && sb.charAt(sb.length()-1) ==  ' ')  {
                sb.delete(sb.length()-2, sb.length());
            }
        }
        sb.append("):");
        for (String table : tables) {
            sb.append(table + "_c" + "(), ");
        }
        for (Entry e : list) {
            if (e.getCriteria() != null && !e.getCriteria().isEmpty()) {
                sb.append(e.getTable() + "_c_" + e.getField() + " " + e.getCriteria() + ", ");
            }
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }
	
	public static String convertToDatalogOriginal(List<Entry> list) {
        if (list == null || list.size() == 0) return "";
        Set<String> tables = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);
            tables.add(e.getTable());
            if (i == 0) sb.append("q(");
            if (e.getShow()) sb.append(e.getField() + ", ");
            if (i == list.size() - 1 && sb.charAt(sb.length()-1) ==  ' ')  {
                sb.delete(sb.length()-2, sb.length());
            }
        }
        sb.append("):");
        for (String table : tables) {
            sb.append(table + "(), ");
        }
        for (Entry e : list) {
            if (e.getCriteria() != null && !e.getCriteria().isEmpty()) {
                sb.append(e.getField() + " " + e.getCriteria() + ", ");
            }
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }
	
    public static String convertToDatalog2(List<Entry> list) {
        if (list == null || list.size() == 0) return "";
        Set<String> tables = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append("λ ");
        for (Entry e : list) {
            if (e.getLambda()) sb.append(e.getField() + ". ");
        }
        if (sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-2, sb.length());
        sb.append(" ");
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);
            tables.add(e.getTable());
            if (i == 0) sb.append("A(");
            if (e.getShow()) sb.append(e.getField() + ", ");
            if (i == list.size() - 1 && sb.charAt(sb.length()-1) ==  ' ')  {
                sb.delete(sb.length()-2, sb.length());
            }
        }
        sb.append(") :- ");
        for (String table : tables) {
            sb.append(table + "(");
            List<String> attrs = Database.getAttrList(table);
            for (String attr : attrs) sb.append(attr + ", ");
            if (attrs.size() > 0) sb.delete(sb.length()-2, sb.length());
            sb.append("), ");
        }
        for (Entry e : list) {
            if (e.getCriteria() != null && !e.getCriteria().isEmpty()) {
                sb.append(e.getField() + " " + e.getCriteria() + ", ");
            }
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }

    public static String convertToSQL(List<Entry> list) {
        if (list == null || list.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        Set<String> tables = new HashSet<>();
        List<String> selected = new ArrayList<>(), wheres = new ArrayList<>();
        for (Entry item : list) {
            if (item.getShow()) selected.add(item.getTable() + "." + item.getField());
            tables.add(item.getTable());
            if (item.getCriteria() != null && !item.getCriteria().isEmpty()) {
                wheres.add(item.getTable() + "." + item.getField() + " " + item.getCriteria());
            }
        }
        sb.append("SELECT ");
        for (int i = 0; i < selected.size(); i++) {
            sb.append(selected.get(i));
            if (i < selected.size() - 1)  sb.append(", ");
        }
        sb.append(" FROM ");
        for (String table : tables) sb.append(table + ", ");
        if (sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-2, sb.length());
        if (wheres.size() > 0) {
            sb.append(" WHERE ");
            for (int i = 0; i < wheres.size(); i++) sb.append(wheres.get(i));
            if (sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-2, sb.length());
        }
        return sb.toString();
    }

    public static String convertToSQLWithLambda(List<Entry> list) {
        if (list == null || list.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        Set<String> tables = new HashSet<>();
        List<String> lambdas = new ArrayList<>();
        List<String> selected = new ArrayList<>(), wheres = new ArrayList<>();
        for (Entry item : list) {
            if (item.getShow()) selected.add(item.getTable() + "_c_" + item.getField());
            if (item.getLambda()) lambdas.add(item.getTable() + "_c_" + item.getField());
            tables.add(item.getTable() + "_c");
            if (item.getCriteria() != null && !item.getCriteria().isEmpty()) {
                wheres.add(item.getTable() + "_c_"  + item.getField() + " " + item.getCriteria());
            }
        }
        sb.append("SELECT ");
        for (int i = 0; i < selected.size(); i++) {
            sb.append(selected.get(i));
            if (i < selected.size() - 1) sb.append(", ");
        }
        sb.append(" FROM ");
        for (String table : tables) sb.append(table + ", ");
        if (sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-2, sb.length());
        if (wheres.size() + lambdas.size() > 0) {
            sb.append(" WHERE ");
            for (String where : wheres) sb.append(where + ", ");
            for (String lambda : lambdas) sb.append(lambda + "=? AND ");
            if (lambdas.size() > 0 && sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-5, sb.length());
            else sb.delete(sb.length()-2, sb.length());
        }
        return sb.toString();
    }

    public static List<String> getLambda(List<Entry> list) {
        List<String> lambdas = new ArrayList<>();
        for (Entry e : list) {
            if (e.getLambda()) {
                lambdas.add(e.getTable() + "." + e.getField());
            }
        }
        return lambdas;
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException {
    	Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
    	String datalog = "q(family_c_family_id, family_c_name):family_c()";
		try {
			System.out.println("[DEBUG] datalog: " + datalog);
			Vector<Vector<citation_view_vector>> c_views = Tuple_reasoning2.tuple_reasoning(datalog, citation_strs);
			// Vector<String> agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views);
			// Vector<String> subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
			// System.out.println("[!!!!!!] " + subset_agg_citations);
		} catch (ClassNotFoundException | SQLException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
		for (Vector<String> v : citation_strs) System.out.println(Math.max(v.get(0).length(), v.get(1).length()));
	}

}
