package main;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) throws SQLException {
		DBOperations dbo = new DBOperations();

		int result = 0;

		while (result < 3) {
			result = Utils.showMenu("Select an operation", "Select from Players", "Insert a new Recipe",
					"Delete a Death Record", "Exit");

			switch (result) {
			case 0:
				dbo.doSelect();
				break;
			case 1:
				dbo.insert();
				break;
			case 2:
				dbo.doDelete();
				break;
			case 3:
				System.out.println("Goodbye\n");
				dbo.closeConnection();
				break;
			default:
				System.out.println("Please select a valid option.");
				break;
			}
		}
	}
}