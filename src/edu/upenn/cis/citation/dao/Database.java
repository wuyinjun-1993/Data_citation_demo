package edu.upenn.cis.citation.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

	public static String DB_ADDR =  "jdbc:postgresql://datacitation.cn7s3bpawoj2.us-east-1.rds.amazonaws.com/postgres";
	public static String DB_USERNAME = "postgres";
	public static String DB_PASSWORD = "12345678";
	
	public static List<String> getTableList() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rs = metaData.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (!tableName.endsWith("_c")) {
					list.add(tableName);
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static List<String> getAttrList(String tableName) {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rs = metaData.getColumns(null, null, tableName, "%");
			while (rs.next()) {
				list.add(rs.getString("COLUMN_NAME"));
			}
		} catch (SQLException | ClassNotFoundException e) {
			
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static List<String> getDistincts(String tableName, String field) {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
			Statement statement = conn.createStatement();
			statement.execute(String.format("SELECT DISTINCT %s FROM %s A ORDER BY %s", field, tableName, field));
			ResultSet rs = statement.getResultSet();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException | ClassNotFoundException e) {

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static void main(String args[]) {
		System.out.println(getDistincts("family", "family_id"));
	}
	
	public static List<String> getDataViews() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
			Statement statement = conn.createStatement();
			statement.execute("SELECT DISTINCT view FROM citation_view ORDER BY view");
			ResultSet rs = statement.getResultSet();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException | ClassNotFoundException e) {

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static List<String> getCitationViews() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
			Statement statement = conn.createStatement();
			statement.execute("SELECT DISTINCT citation_view_name FROM citation_view ORDER BY citation_view_name");
			ResultSet rs = statement.getResultSet();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException | ClassNotFoundException e) {

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
