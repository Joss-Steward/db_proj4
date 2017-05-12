package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DBOperations
{

	MysqlDataSource dataSource;
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	private Scanner input;

	public DBOperations() throws SQLException
	{
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("csc371-30");
		dataSource.setPassword("Password30");
		dataSource.setDatabaseName("csc371-30");
		dataSource.setServerName("db.cs.ship.edu");
		input = new Scanner(System.in);

		conn = dataSource.getConnection();
	}

	public void closeConnection() throws SQLException
	{
		rs.close();
		ps.close();
		conn.close();
	}

	public void doSelect() throws SQLException
	{
		int filter = 0;

		while (filter < 3)
		{
			filter = Utils.showMenu("Choose filter to select by", "Show All", "Filter by minimum STR",
					"Filter by maximum HP", "Cancel");

			PreparedStatement stmt;
			String query;
			ResultSet rs;

			switch (filter)
			{
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

					while (!input.hasNextInt())
					{
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

					while (!input.hasNextInt())
					{
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

	public void doDelete() throws SQLException
	{
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

		while (!validrecord)
		{
			System.out.print("Enter the ID of a death record to delete: ");

			while (!input.hasNextInt())
			{
				input.next();
				System.out.print("Enter the ID of a death record to delete: ");
			}

			deathRecord = input.nextInt();

			query = "SELECT deathID FROM Deaths WHERE deathID = ?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, deathRecord);
			rs = stmt.executeQuery();

			// If there is record with that ID.
			if (rs.first())
			{
				validrecord = true;
			} else
			{
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
		if (rs.first())
		{
			affectsPlayerDeaths = true;
			System.out.println("Deleting this Death will result in deleting rows from the PlayerDeaths table as well.");
			System.out.print("Do you wish to continue? (y/n) ");

			while (!input.hasNext("[yYnN]"))
			{
				input.next();
				System.out.print("Please specify y or n: ");
			}

			// If the user chooses not to delete the record, exit
			if (input.next().equalsIgnoreCase("n"))
			{
				System.out.println("Cancelling");
				return;
			}

		} else
		{
			System.out.println("Deleting this Death will not affect the PlayerDeaths table. Proceeding.");
		}

		if (affectsPlayerDeaths)
		{
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
				+ "JOIN PlayerDeaths USING (playerID)"
				+ "JOIN Deaths USING (deathID)";
		stmt = conn.prepareStatement(query);

		rs = stmt.executeQuery(query);

		Utils.showResultSet("LISTING ALL DEATHS JOINED WITH PLAYERDEATHS AND PLAYERS", rs, "%3s   %-23s %-10s %-30s");

	}

	public void insert()
	{
		boolean success = false;
		int recipeID, itemID = 0, quantity, count;
		String itemName;
		String selectRecipeSQL = "SELECT * FROM Recipes";
		String insertRecipeSQL = "INSERT INTO Recipes ("
				+ "recipeID,"
				+ "craftedItemId, "
				+ "quantityMade)"
				+ " VALUES (?,?,?)";

		input = new Scanner(System.in);
		System.out.println("\nInserting Into Recipes Table\n");
		System.out.print("  Recipe ID[INT]:");
		recipeID = input.nextInt();
		input.nextLine();
		System.out.print("\n  Crafted Item Name[String]:");
		itemName = input.nextLine();

		System.out.print("\n  Quantity Made[INT]:");
		quantity = input.nextInt();

		itemID = getIDFromName(itemID, itemName);
		if (itemID == -1)
		{
			System.out.println("\nGOODBYE");
		}
		while (!success && itemID != -1)
		{
			try
			{
				ps = conn.prepareStatement(insertRecipeSQL);
				ps.setInt(1, recipeID);
				ps.setInt(2, itemID);
				ps.setInt(3, quantity);
				count = ps.executeUpdate();

				if (count > 0)
				{
					rs = ps.executeQuery(selectRecipeSQL);
					Utils.showResultSet("Recipe Table After Successful Insert", rs, "%-10s  %-12s %-12s%n");
					success = true;
					System.out.println("\nYou have Successfully Inserted into the Recipes Table\n\n\n");
				}

			} catch (SQLException e)
			{
				if (e.getMessage().toLowerCase().contains("primary"))
				{
					System.out.println("The Recipe ID {" + recipeID + "}" + " already exists Please enter a new one");
					System.out.print("\n  Recipe ID[INT]:");
					recipeID = input.nextInt();
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
	private int getIDFromName(int itemID, String itemName)
	{
		String selectItemSQL = "SELECT itemID FROM Items WHERE itemName = ?";

		try
		{
			ps = conn.prepareStatement(selectItemSQL);
			ps.setString(1, itemName);
			rs = ps.executeQuery();
			if (rs.next())
				itemID = rs.getInt("itemID");

			if (itemID == 0)
			{
				itemID = insertFK(itemName);
			}
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());

		}
		return itemID;
	}

	public int insertFK(String itemName)
	{
		boolean success = false;
		String insertItemSql = "INSERT INTO Items (itemName,weight,cost) VALUES (?,?,?)";
		String response = "";

		int weight = 0, cost = 0, count = 0, itemID = 0;

		input = new Scanner(System.in);
		System.out.print("The value {"
				+ itemName
				+ "} "
				+ "does not exist in the Items table"
				+ " would you like to create it? [y/n] :");
		while (!input.hasNext("[yYnN]"))
		{
			response = input.nextLine();
			System.out.print("Please specify y or n: " + response + ":");

		}
		if (input.next().equalsIgnoreCase("y"))
		{
			System.out.println("\nValues of {" + itemName + "} to insert into Table\n");

			System.out.print("\n\tWeight[INT]:");
			weight = input.nextInt();

			System.out.print("\n\tCost[INT]: ");
			cost = input.nextInt();

			while (!success)
			{

				try
				{
					ps = conn.prepareStatement(insertItemSql);
					ps.setString(1, itemName);
					ps.setInt(2, weight);
					ps.setInt(3, cost);
					count = ps.executeUpdate();
					if (count > 0)
					{
						success = true;
						itemID = displayItems();

					}
				} catch (SQLException e)
				{
					System.out.println("\n\n" + e.getMessage());
					if (e.getMessage().contains("Primary Key"))
					{
						System.out
								.println("The Item Name {" + itemName + "}" + " already exists Please enter a new one");
						System.out.print("\t Item Name[STRING]:");
						itemName = input.nextLine();
					}
					success = false;

				}

			}
		} else
		{
			String addAnyway;
			System.out.print("Would you like to insert anyway? [y/n] :");
			while (!input.hasNext("[yYnN]"))
			{
				response = input.nextLine();
				System.out.print("Please specify y or n: " + response + ":");

			}
			if (input.next().equalsIgnoreCase("y"))
			{
				try
				{
					displayItems();
				} catch (SQLException e)
				{
					System.out.println("Could Not Display Items");
				}
				System.out.print("Choose An Item By Item ID\n>");
				itemID = input.nextInt();
			} else
			{
				itemID = -1;
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
	public int displayItems() throws SQLException
	{
		int itemID = 0;
		String selectItemSQL = "SELECT * FROM Items";

		ps = conn.prepareStatement(selectItemSQL);
		rs = ps.executeQuery();

		Utils.showResultSet("Displaying Items Table", rs, "%-10s   %-20s %-16s %-30s");

		return itemID;
	}

}
