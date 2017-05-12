package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Main {
	private static Scanner input;
	private static MysqlDataSource dataSource = new MysqlDataSource();
	private static Connection conn;

	/*
	 * Show a menu until the user selects a valid option. Zero indexed result,
	 * but shows as 1 indexed for user comfort.
	 */
	public static int showMenu(String prompt, String... menuOpts) {
		System.out.print('\n');

		int maxArgLength = prompt.length();

		// Find the length of the longest option
		for (String opt : menuOpts) {
			if (opt.length() > maxArgLength)
				maxArgLength = opt.length();
		}

		// Pad the slot by 2
		maxArgLength += 2;
		int selection = -1;

		while (selection < 0 || selection >= menuOpts.length) {
			System.out.println(prompt);

			// Print a horizontal bar
			for (int i = 0; i < maxArgLength; i++) {
				System.out.print("=");
			}

			System.out.print("\n");

			int argIndex = 0;
			for (String option : menuOpts) {
				System.out.printf("%2d) %s\n", argIndex + 1, option);

				argIndex++;
			}

			System.out.print("> ");

			try {
				input = new Scanner(System.in);
				selection = input.nextInt() - 1;
			} catch (Exception e) {
				System.out.println("Error while reading input:");
				e.printStackTrace();
			}
		}

		return selection;
	}

	public static void showResultSet(String label, ResultSet results, String displayFormat) throws SQLException {
		System.out.print('\n');
		System.out.println(label);

		ResultSetMetaData rsmd = results.getMetaData();
		ArrayList<String> headers = new ArrayList<String>();

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			headers.add(rsmd.getColumnLabel(i));
		}

		String header = String.format(displayFormat, headers.toArray());
		String dashes = header.replaceAll("[\\w\\d\\s]", "=");

		System.out.printf(header + '\n');
		System.out.printf(dashes + '\n');

		while (results.next()) {
			ArrayList<String> row = new ArrayList<String>();

			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				row.add(results.getString(i));
			}

			System.out.printf(displayFormat + '\n', row.toArray());
		}

		System.out.print('\n');
	}

	public static void doSelect() throws SQLException {
		int filter = 0;

		while (filter < 3) {
			filter = showMenu("Choose filter to select by", "Show All", "Filter by minimum STR", "Filter by maximum HP",
					"Cancel");

			PreparedStatement stmt;
			String query;
			ResultSet rs;

			switch (filter) {
			case 0:
				// Show all rows without filtering
				query = "SELECT playerId as ID, playerName as Name, class as Class, experiencePoints as XP, strength as STR, currentHealth as HP FROM Players";
				stmt = conn.prepareStatement(query);

				rs = stmt.executeQuery(query);

				showResultSet("SHOWING ALL", rs, "%3s %-13s %-20s  %4s  %4s  %4s");

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

				showResultSet("SHOWING ALL WITH STRENGTH > " + minStrength + " ORDERED BY DESC", rs,
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

				showResultSet("SHOWING ALL WITH HEALTH < " + minHealth + " ORDERED BY ASC", rs,
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
			result = showMenu("Select an operation", "Select from Players", "Insert", "Delete", "Exit");

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