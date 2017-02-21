package edu.upenn.cis.citation.ui;

import edu.upenn.cis.citation.dao.Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    public static String convertToDatalog(List<Entry> list) {
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
            if (item.getShow()) selected.add(item.getTable() + "." + item.getField());
            if (item.getLambda()) lambdas.add(item.getTable() + "." + item.getField());
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
        if (wheres.size() + lambdas.size() > 0) {
            sb.append(" WHERE ");
            for (String where : wheres) sb.append(where + ", ");
            for (String lambda : lambdas) sb.append(lambda + "=? AND ");
            if (sb.charAt(sb.length()-1) ==  ' ') sb.delete(sb.length()-5, sb.length());
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

}
