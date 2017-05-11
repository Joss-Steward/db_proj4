package main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Main {

	public static void main(String[] args) throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("csc371-30");
		dataSource.setPassword("Password30");
		dataSource.setDatabaseName("csc371-30");
		dataSource.setServerName("db.cs.ship.edu");
		
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT playerId, playerName, class FROM Players");

		ResultSetMetaData rsmd = rs.getMetaData();
		String name = rsmd.getColumnName(1);
		 
		System.out.printf("%5s | %-60s\n", "ID", name);
		
		System.out.printf(String.format("%5s | %60s\n", "", "").replace(' ', '-'));
		
		while(rs.next()) {
			
			System.out.printf("%5s | %-20s | %-20s\n", rs.getInt(1), rs.getString(2), rs.getString(3));
		}
		
		rs.close();
		stmt.close();
		conn.close();
	}

}