package edu.upenn.cis.citation.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.Select;

import edu.upenn.cis.citation.Pre_processing.populate_db;

public class Database {

//	public static String DB_ADDR =  "jdbc:postgresql://datacitation.cn7s3bpawoj2.us-east-1.rds.amazonaws.com/postgres";
//	public static String DB_USERNAME = "postgres";
//  public static String DB_PASSWORD = "12345678";
	
	public static List<String> getTableList() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
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
	
	public static List<String> getGeneratedQueryList() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
			Statement statement = conn.createStatement();
			statement.execute(String.format("select query_id from user_query_table"));
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
	
	public static List<String> getAttrList(String tableName) {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
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
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
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
		System.out.println(getDataViewDataLog("v1"));
	}
	
	public static List<String> getDataViews() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
			Statement statement = conn.createStatement();
			statement.execute("SELECT DISTINCT name FROM view_table ORDER BY name");
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
		
		System.out.println("DataViews: " + list.toString());
		
		return list;
	}
	
	public static List<String> getCitationViews(String dv) {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
			Statement statement = conn.createStatement();
			if (dv == null || dv.isEmpty()) {
				statement.execute("SELECT DISTINCT query2head_variables.name FROM query2head_variables ORDER BY query2head_variables.name");
			} else {
				statement.execute("SELECT DISTINCT query2head_variables.name FROM query2head_variables, citation2query, citation2view, view_table "
						+ "WHERE query2head_variables.query_id = citation2query.query_id and citation2query.citation_view_id = citation2view.citation_view_id "
						+ "and view_table.view = citation2view.view and view_table.name = '" + dv + "' ORDER BY query2head_variables.name");
			}
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
		System.out.println("CitationViews: " + list.toString());
		return list;
	}

	public static void insertDCTuple(String dv, String cv) {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
			Statement statement = conn.createStatement();
			statement.execute("SELECT * FROM citation_view");
            statement.close();
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
	}

    public static String getDataViewDataLog(String dv) {

	    Connection conn = null;
		String head_varibales = "";
		ArrayList<String> lambda_term = new ArrayList<>();
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<String> subgoals = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);

			Statement statement = conn.createStatement();
			statement.execute(String.format("SELECT * FROM view_table WHERE view='" + dv + "'"));
			ResultSet rs = statement.getResultSet();
			if (rs.next()) {
				head_varibales = rs.getString("head_variables");
				// System.out.println("[DEGBU] head_varibales: " + head_varibales);
			}
			rs.close();
			statement.close();

			statement = conn.createStatement();
			statement.execute(String.format("SELECT * FROM view2lambda_term WHERE view='" + dv + "'"));
			rs = statement.getResultSet();
			while (rs.next()) {
                lambda_term.add(rs.getString("lambda_term"));
				// System.out.println("[DEGBU] lambda_term: " + rs.getString("lambda_term"));
			}
			rs.close();
			statement.close();

			statement = conn.createStatement();
			statement.execute(String.format("SELECT * FROM view2conditions WHERE view='" + dv + "'"));
			rs = statement.getResultSet();
			if (rs.next()) {
				conditions.add(rs.getString("conditions"));
				// System.out.println("[DEGBU] conditions: " + rs.getString("conditions"));
			}
			rs.close();
			statement.close();

			statement = conn.createStatement();
			statement.execute(String.format("SELECT * FROM view2subgoals WHERE view='" + dv + "'"));
			rs = statement.getResultSet();
			if (rs.next()) {
				subgoals.add(rs.getString("subgoal_names"));
				// System.out.println("[DEGBU] subgoals: " + rs.getString("subgoal_names"));
			}
			rs.close();
			statement.close();

            // Construct datalog
            sb.append("λ ");
            if (lambda_term.size() > 0) sb.append(lambda_term.get(0));
            for (int i = 1; i < lambda_term.size(); i++) sb.append(", " + lambda_term.get(i));
            sb.append("  q(" + head_varibales + "): ");
            for (int i = 0; i < subgoals.size(); i++) sb.append(subgoals.get(i) + "(), ");
            for (int i = 0; i < conditions.size(); i++) sb.append(conditions.get(i) + ", ");
            if (sb.charAt(sb.length()-2) == ',') sb.delete(sb.length() - 2, sb.length());

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
        return sb.toString();
    }

	public static void deleteDataViewByName(String dv) {
//		Connection conn = null;
//		try {
//			Class.forName("org.postgresql.Driver");
//			conn = DriverManager.getConnection(DB_ADDR, DB_USERNAME, DB_PASSWORD);
//			Statement statement = conn.createStatement();
//			statement.execute("SELECT * FROM citation_view");
//		} catch (SQLException | ClassNotFoundException e) {
//
//		} finally {
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

    public static void createDataViewByName(String dv) {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
			Statement statement = conn.createStatement();
			statement.execute("INSERT INTO view_table (view, web_view) VALUES ('" + dv + "', FALSE )");
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
    }
}
