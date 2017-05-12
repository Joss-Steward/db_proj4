package main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {

	/**
	 * Show a menu until the user selects a valid option. Zero indexed result,
	 * but shows as 1 indexed for user comfort.
	 */
	public static int showMenu(String prompt, String... menuOpts) {
		Scanner input = new Scanner(System.in);
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
				selection = input.nextInt() - 1;
			} catch (Exception e) {
				System.out.println("Error while reading input:");
				e.printStackTrace();
			}
		}

		return selection;
	}

	/**
	 * Fancily displays a result set
	 * 
	 * @param label
	 * @param results
	 * @param displayFormat
	 * @throws SQLException
	 */
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
}
