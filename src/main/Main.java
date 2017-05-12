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

	public static void doDelete() throws SQLException {
		Scanner input = new Scanner(System.in);

		PreparedStatement stmt;
		String query;
		ResultSet rs;

		// Show all rows without filtering
		query = "SELECT deathID, timeOfDeath, causeOfDeath ID FROM Deaths";
		stmt = conn.prepareStatement(query);

		rs = stmt.executeQuery(query);

		Utils.showResultSet("Deaths TABLE AFTER DELETION", rs, "%8s  %-23s %-30s");

		rs.close();
		stmt.close();

		boolean validrecord = false;
		int deathRecord = 0;

		while (!validrecord) {
			System.out.print("Enter the ID of a death record to delete: ");

			while (!input.hasNextInt()) {
				input.next();
				System.out.print("Enter the ID of a death record to delete: ");
			}

			deathRecord = input.nextInt();

			query = "SELECT deathID FROM Deaths WHERE deathID = ?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, deathRecord);
			rs = stmt.executeQuery();

			// If there is record with that ID.
			if (rs.first()) {
				validrecord = true;
			} else {
				System.out.println("Please enter a valid ID");
			}

			rs.close();
			stmt.close();
		}

		query = "SELECT deathID FROM PlayerDeaths WHERE deathID = ?";
		stmt = conn.prepareStatement(query);
		stmt.setInt(1, deathRecord);
		rs = stmt.executeQuery();
		boolean affectsPlayerDeaths = false;

		// If there is record with that ID.
		if (rs.first()) {
			affectsPlayerDeaths = true;
			System.out.println("Deleting this Death will result in deleting rows from the PlayerDeaths table as well.");
			System.out.print("Do you wish to continue? (y/n) ");

			while (!input.hasNext("[yYnN]")) {
				input.next();
				System.out.print("Please specify y or n: ");
			}

			// If the user chooses not to delete the record, exit
			if (input.next().equalsIgnoreCase("n")) {
				System.out.println("Cancelling");
				return;
			}

		} else {
			System.out.println("Deleting this Death will not affect the PlayerDeaths table. Proceeding.");
		}

		if (affectsPlayerDeaths) {
			query = "DELETE FROM PlayerDeaths WHERE deathID = ?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, deathRecord);
			stmt.executeUpdate();

			query = "SELECT playerID, deathID FROM PlayerDeaths";
			stmt = conn.prepareStatement(query);

			rs = stmt.executeQuery();

			Utils.showResultSet("PlayerDeaths TABLE AFTER DELETION", rs, "%9s  %9s");
		}

		query = "DELETE FROM Deaths WHERE deathID = ?";
		stmt = conn.prepareStatement(query);
		stmt.setInt(1, deathRecord);
		stmt.executeUpdate();

		System.out.print('\n');

		query = "SELECT deathID, timeOfDeath, causeOfDeath ID FROM Deaths";
		stmt = conn.prepareStatement(query);

		rs = stmt.executeQuery(query);

		Utils.showResultSet("Deaths TABLE AFTER DELETION", rs, "%8s  %-23s %-30s");

		rs.close();
		stmt.close();

		// Show all rows without filtering
		query = "SELECT Deaths.deathID as ID, Deaths.timeOfDeath as Time, Players.playerName as Victim, Deaths.causeOfDeath as Cause FROM Players "
				+ "JOIN PlayerDeaths USING (playerID)" + "JOIN Deaths USING (deathID)";
		stmt = conn.prepareStatement(query);

		rs = stmt.executeQuery(query);

		Utils.showResultSet("LISTING ALL DEATHS JOINED WITH PLAYERDEATHS AND PLAYERS", rs, "%3s   %-23s %-10s %-30s");

	}

	public static void main(String[] args) throws SQLException {
		dataSource.setUser("csc371-30");
		dataSource.setPassword("Password30");
		dataSource.setDatabaseName("csc371-30");
		dataSource.setServerName("db.cs.ship.edu");

		conn = dataSource.getConnection();

		int result = 0;

		while (result < 3) {
			result = Utils.showMenu("Select an operation", "Select from Players", "Insert a new Recipe",
					"Delete a Death Record", "Exit");

			switch (result) {
			case 0:
				doSelect();
				break;
			case 1:
				DBOperations dbo = new DBOperations();
				dbo.insert();
				break;
			case 2:
				doDelete();
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