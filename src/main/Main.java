package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Main {
	private static MysqlDataSource dataSource = new MysqlDataSource();
	private static Connection conn;

	public static void doSelect() throws SQLException {
		Scanner input = new Scanner(System.in);
		int filter = 0;

		while (filter < 3) {
			filter = Utils.showMenu("Choose filter to select by", "Show All", "Filter by minimum STR",
					"Filter by maximum HP", "Cancel");

			PreparedStatement stmt;
			String query;
			ResultSet rs;

			switch (filter) {
			case 0:
				// Show all rows without filtering
				query = "SELECT playerId as ID, playerName as Name, class as Class, experiencePoints as XP, strength as STR, currentHealth as HP FROM Players";
				stmt = conn.prepareStatement(query);

				rs = stmt.executeQuery(query);

				Utils.showResultSet("SHOWING ALL", rs, "%3s %-13s %-20s  %4s  %4s  %4s");

				rs.close();
				stmt.close();
				break;
			case 1:
				// Filter by minimum strength
				query = "SELECT playerId as ID, playerName as Name, class as Class, experiencePoints as XP, "
						+ "strength as STR, currentHealth as HP FROM Players WHERE (strength > ?) ORDER BY strength DESC";
				stmt = conn.prepareStatement(query);

				int minStrength = 0;

				System.out.print("Please enter a minimum strength: ");

				while (!input.hasNextInt()) {
					input.next();
					System.out.print("Please enter a minimum strength: ");
				}

				minStrength = input.nextInt();

				stmt.setInt(1, minStrength);
				rs = stmt.executeQuery();

				Utils.showResultSet("SHOWING ALL WITH STRENGTH > " + minStrength + " ORDERED BY DESC", rs,
						"%3s %-13s  %-20s  %4s  %4s  %4s");

				rs.close();
				stmt.close();
				break;
			case 2:
				// Filter by minimum strength
				query = "SELECT playerId as ID, playerName as Name, class as Class, experiencePoints as XP, "
						+ "strength as STR, currentHealth as HP FROM Players WHERE (currentHealth < ?) ORDER BY currentHealth DESC";
				stmt = conn.prepareStatement(query);

				int minHealth = 0;

				System.out.print("Please enter a maximum health: ");

				while (!input.hasNextInt()) {
					input.next();
					System.out.print("Please enter a maximum health: ");
				}

				minHealth = input.nextInt();

				stmt.setInt(1, minHealth);
				rs = stmt.executeQuery();

				Utils.showResultSet("SHOWING ALL WITH HEALTH < " + minHealth + " ORDERED BY ASC", rs,
						"%3s %-13s  %-20s  %4s  %4s  %4s");

				rs.close();
				stmt.close();
				break;
			default:
				System.out.println("Please select a valid option.");
				break;
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		dataSource.setUser("csc371-30");
		dataSource.setPassword("Password30");
		dataSource.setDatabaseName("csc371-30");
		dataSource.setServerName("db.cs.ship.edu");

		conn = dataSource.getConnection();

		int result = 0;

		while (result < 3) {
			result = Utils.showMenu("Select an operation", "Select from Players", "Insert", "Delete", "Exit");

			switch (result) {
			case 0:
				doSelect();
				break;
			case 1:
				System.out.println("Insert");
				// dbo.insert();
				break;
			case 2:
				System.out.println("Delete");
				// dbo.delete();
				break;
			case 3:
				System.out.println("Goodbye\n");
				break;
			default:
				System.out.println("Please select a valid option.");
				break;
			}
		}

		// DBOperations dbo = new DBOperations(conn);

		// dbo.insert();
		// input.close();

		conn.close();
	}
}