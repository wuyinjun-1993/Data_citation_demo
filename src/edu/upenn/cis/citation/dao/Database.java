package edu.upenn.cis.citation.dao;

import java.sql.*;
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
				list.add(rs.getString("TABLE_NAME"));
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
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/iuphar_org", "postgres", "123");
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
}
