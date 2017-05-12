package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DBOperations {

	MysqlDataSource dataSource;
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	private Scanner reader;

	public DBOperations() throws SQLException {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("csc371-30");
		dataSource.setPassword("Password30");
		dataSource.setDatabaseName("csc371-30");
		dataSource.setServerName("db.cs.ship.edu");

		conn = dataSource.getConnection();
	}

	public void closeConnection() throws SQLException {
		rs.close();
		ps.close();
		conn.close();
	}

	/*
	 * public String[] showTables(Connection conn) throws SQLException {
	 * String[] types = {"TABLE"}; String[] tables = new String[100]; int num =
	 * 0;
	 *
	 *
	 * DatabaseMetaData dbmd = conn.getMetaData(); ResultSet rs =
	 * dbmd.getTables(null, null, "%", types);
	 * System.out.printf(String.format("%78s","","").replace(" ", "-")); while
	 * (rs.next()) { if(num%3 == 0) System.out.println('\n');
	 *
	 * tables[num]=rs.getString("TABLE_NAME"); System.out.printf("%4d.%-20s|",
	 * (num+1), tables[num]); num++; } System.out.println("");
	 * System.out.printf(String.format("%78s","","").replace(" ", "-"));
	 * System.out.println("\n\n");
	 *
	 * return tables; }
	 */

	public void insert() {
		boolean success = false;
		int recipeID, itemID = 0, quantity, count;
		String itemName;
		String selectRecipeSQL = "SELECT * FROM Recipes";
		String insertRecipeSQL = "INSERT INTO Recipes (" + "recipeID," + "craftedItemId, " + "quantityMade)"
				+ " VALUES (?,?,?)";

		reader = new Scanner(System.in);
		System.out.println("Inserting Into Recipes Table\n");
		System.out.print("\t Recipe ID[INT]:");
		recipeID = reader.nextInt();
		reader.nextLine();
		System.out.print("\n\t Crafted Item Name[String]:");
		itemName = reader.nextLine();

		System.out.print("\n\t Quantity Made[INT]:");
		quantity = reader.nextInt();

		itemID = getIDFromName(itemID, itemName);
		while (!success) {

			try {
				ps = conn.prepareStatement(insertRecipeSQL);
				ps.setInt(1, recipeID);
				ps.setInt(2, itemID);
				ps.setInt(3, quantity);
				count = ps.executeUpdate();

				if (count > 0) {
					System.out.println("\n\n\n" + recipeID);

					rs = ps.executeQuery(selectRecipeSQL);

					System.out.printf("%-10s |  %-16s | %-30s%n", "Recipe ID", "Crafted Item ID", "Quantity Made");
					System.out.printf(String.format("%-10s |  %-16s | %-30s%n", "", "", "").replace(' ', '-'));

					while (rs.next()) {
						System.out.printf("%-10s |  %-16d | %-30d%n", rs.getString(1), rs.getInt(2), rs.getInt(3));
					}
					success = true;
					System.out.println("\nYou have Successfully Inserted into the Recipes Table\n\n\n");
				}

			} catch (SQLException e) {
				if (e.getMessage().toLowerCase().contains("primary")) {
					System.out.println("The Recipe ID {" + recipeID + "}" + " already exists Please enter a new one");
					System.out.print("\t Recipe ID[INT]:");
					recipeID = reader.nextInt();

				}
				success = false;
			}
		}
	}

	/**
	 * @param itemID
	 * @param itemName
	 * @param selectItemSQL
	 * @return
	 */
	private int getIDFromName(int itemID, String itemName) {
		String selectItemSQL = "SELECT itemID FROM Items WHERE itemName = ?";

		try {
			ps = conn.prepareStatement(selectItemSQL);
			ps.setString(1, itemName);
			rs = ps.executeQuery();
			if (rs.next())
				itemID = rs.getInt("itemID");

			if (itemID == 0) {
				itemID = insertFK(itemName);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());

		}
		return itemID;
	}

	public int insertFK(String itemName) {
		boolean success = false;
		String insertItemSql = "INSERT INTO Items (" + "itemName," + "weight," + "cost)" + "VALUES (?,?,?)";
		String response;
		int weight = 0, cost = 0, count = 0, itemID = 0;

		reader = new Scanner(System.in);
		System.out.print("The value {" + itemName + "} " + "does not exist in the Items table"
				+ " would you like to create it? [y/n] :");
		response = reader.next();
		if (response.equalsIgnoreCase("y")) {
			System.out.println("\nValues of {" + itemName + "} to insert into Table\n");

			System.out.print("\n\tWeight[INT]:");
			weight = reader.nextInt();

			System.out.print("\n\tCost[INT]: ");
			cost = reader.nextInt();

			while (!success) {

				try {
					ps = conn.prepareStatement(insertItemSql);
					ps.setString(1, itemName);
					ps.setInt(2, weight);
					ps.setInt(3, cost);
					count = ps.executeUpdate();
					if (count > 0) {
						success = true;
						itemID = displayItems();

					}
				} catch (SQLException e) {
					System.out.println("\n\n" + e.getMessage());
					if (e.getMessage().contains("Primary Key")) {
						System.out
								.println("The Item Name {" + itemName + "}" + " already exists Please enter a new one");
						System.out.print("\t Item Name[STRING]:");
						itemName = reader.nextLine();
					}
					success = false;

				}

			}
		} else if (response.equalsIgnoreCase("n")) {
			String addAnyway;
			System.out.print("Would you like to insert anyway? [y/n] :");
			reader.nextLine();
			addAnyway = reader.nextLine();
			System.out.println(addAnyway);
			if (addAnyway.equalsIgnoreCase("y")) {
				try {
					displayItems();
				} catch (SQLException e) {
					System.out.println("Could Not Display Items");
				}
				System.out.print("Choose An Item By Item ID:");
				itemID = reader.nextInt();
			}
		}
		return itemID;

	}

	/**
	 * @param selectItemSQL
	 * @param itemID
	 * @return
	 * @throws SQLException
	 */
	public int displayItems() throws SQLException {
		int itemID = 0;
		String selectItemSQL = "SELECT * FROM Items";

		ps = conn.prepareStatement(selectItemSQL);
		rs = ps.executeQuery();
		System.out.printf("-10d |  %-20s |  %-16s | %-30s%n", "Item Name", "Weight", "Cost");
		System.out.printf(String.format("%-10s |  %-16s | %-30s%n", "", "", "").replace(' ', '-'));

		while (rs.next()) {
			itemID = rs.getInt("itemID");
			System.out.printf("%-10d |  %-20s |  %-16d | %-30d%n", itemID, rs.getString("itemName"),
					rs.getInt("weight"), rs.getInt("cost"));

		}
		return itemID;
	}

	public void delete() {
		// TODO Auto-generated method stub

	}

}
