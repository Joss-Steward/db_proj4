package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DBOperations{
	
	Connection conn;
	public DBOperations(Connection conn)
	{
		this.conn = conn;
	}
/*	public String[] showTables(Connection conn) throws SQLException
	{
        String[] types = {"TABLE"};
        String[] tables = new String[100];
        int num = 0;
        
		
		DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "%", types);
        System.out.printf(String.format("%78s","","").replace(" ", "-"));
        while (rs.next()) {
        	if(num%3 == 0)
        		System.out.println('\n');
        	
        	tables[num]=rs.getString("TABLE_NAME");
            System.out.printf("%4d.%-20s|",  (num+1), tables[num]);
            num++;
        }
        System.out.println("");
        System.out.printf(String.format("%78s","","").replace(" ", "-"));
        System.out.println("\n\n");
        
        return tables;
	}*/
	public void insert() 
	{
		System.out.printf("%-10s |  %-7s | %-30s%n", "","Item Name", "Weight", "Cost");

		boolean success=false;
		int recipeID, itemID = 0, quantity, count;
		String itemName;
		String selectRecipeSQL = "SELECT * FROM Recipes";
		String selectItemSQL = "SELECT itemID FROM Items WHERE itemName = ?";
		String insertRecipeSQL = "INSERT INTO Recipes ("
				+ "recipeID,"
				+ "craftedItemId, "
				+ "quantityMade)"
				+ " VALUES (?,?,?)";
		
		
		Scanner reader = new Scanner(System.in);
		System.out.println("Inserting Into Recipes Table\n");
		System.out.print("\t Recipe ID[INT]:");
		recipeID = reader.nextInt();
		reader.nextLine();
		System.out.print("\n\t Crafted Item Name[String]:");
		itemName = reader.nextLine();
		
		System.out.print("\n\t Quantity Made[INT]:");
		quantity = reader.nextInt();
		PreparedStatement ps;
		ResultSet rs;

		try {
			
			ps = conn.prepareStatement(selectItemSQL);
			ps.setString(1, itemName);
			rs = ps.executeQuery();
			while(rs.next())
			{
				itemID = rs.getInt(1);
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		while(!success)
		{
		
			try {
				ps = conn.prepareStatement(insertRecipeSQL);
				ps.setInt(1, recipeID);
				ps.setInt(2, itemID);
				ps.setInt(3, quantity);
				count = ps.executeUpdate();
				if(count > 0)
				{
					System.out.println("\nYou have Successfully Inserted into the Recipes Table\n\n\n");
					success = true;
					rs = ps.executeQuery(selectRecipeSQL);
					
					System.out.printf("%-10s |  %-16s | %-30s%n", "Recipe ID", "Crafted Item ID", "Quantity Made");
					System.out.printf(String.format("%-10s |  %-16s | %-30s%n", "","", "").replace(' ', '-'));
					
					while(rs.next()) {
						System.out.printf("%-10s |  %-16d | %-30d%n", rs.getString(1), rs.getInt(2), rs.getInt(3));
					}
				}

			} catch (SQLException e){
				System.out.println("\n\n"+ e.getMessage());	
				if(e.getMessage().contains("foreign key"))
					insertFK(itemID);
				else if(e.getMessage().toLowerCase().contains("primary"))
				{
					System.out.println("The Recipe ID {" + recipeID + "}"
							+ " already exists Please enter a new one");
					System.out.print("\t Recipe ID[INT]:");
					recipeID = reader.nextInt();
				}
				success = false;		
			}
		}
	}
	
	private void insertFK(int itemID)
	{
		boolean success = false;
		
		String selectItemSQL = "SELECT * FROM Items";
		String insertItemSql = "INSERT INTO Items ("
				+ "itemName,"
				+ "weight,"
				+ "cost)"
				+ "VALUES (?,?,?)";
		String response, itemName = null;
		int  weight = 0, cost = 0, count;
		
		Scanner reader = new Scanner(System.in);
		System.out.print("The value {" + itemID + "} "
				+ "does not exist in this table"
				+ " would you like to create it [y/n]?");
		response = reader.next();
		if (response.equalsIgnoreCase("y"))
		{
			System.out.println("Inserting Into Recipes Table\n");
			System.out.print("\tItem Name[STRING]:");
			reader.nextLine();
			itemName = reader.nextLine();
			
			System.out.print("\n\tWeight[INT]:");
			weight = reader.nextInt();
			
			System.out.print("\n\tCost[INT]");
			cost = reader.nextInt();
		}
		
		while(!success)
		{
	
			try {
				PreparedStatement ps = conn.prepareStatement(insertItemSql);
				ps.setString(1, itemName);
				ps.setInt(2, weight);
				ps.setInt(3, cost);
				count = ps.executeUpdate();
				if(count > 0)
				{
					System.out.println("\nYou have Successfully Inserted into the Item Table\n\n\n");
					success = true;
					ResultSet rs = ps.executeQuery(selectItemSQL);
					
					System.out.printf("%-10s |  %-16s | %-30s%n", "Item Name", "Weight", "Cost");
					System.out.printf(String.format("%-10s |  %-16s | %-30s%n", "","", "").replace(' ', '-'));
					
					while(rs.next()) {
						System.out.printf("%-10s |  %-16d | %-30d%n", rs.getString(2), rs.getInt(3), rs.getInt(4));
					}
					
					
				}
			} catch (SQLException e){
				System.out.println("\n\n"+ e.getMessage());	
				if(e.getMessage().contains("Primary Key"))
				{
					System.out.println("The Item Name {" + itemName + "}"
							+ " already exists Please enter a new one");
					System.out.print("\t Item Name[STRING]:");
					itemName = reader.nextLine();
				}
				success = false;
					
			}
		
				
	}
	
	}
}
