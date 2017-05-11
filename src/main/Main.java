package main;

import java.sql.ResultSet;
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
		
		DBOperations dbo = new DBOperations(conn);
		dbo.insert();
		//dbo.showTables();
		ResultSet rs = stmt.executeQuery("SELECT playerId, playerName FROM Players");
		
		
		
		
		System.out.printf("%5s | %-60s\n", "ID", "Name");
		System.out.printf(String.format("%5s | %60s\n", "", "").replace(' ', '-'));
		
		while(rs.next()) {
			System.out.printf("%5s | %-60s\n", rs.getInt(1), rs.getString(2));
		}
		
		rs.close();
		stmt.close();
		conn.close();
	}

}

class DBOperations{
	
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
		boolean success=false, fkFail=false;
		int recipeID, itemID, quantity, count;
		
		
		String insertRecipeSQL = "INSERT INTO Recipes ("
				+ "recipeID,"
				+ "craftedItemId, "
				+ "quantityMade)"
				+ " VALUES (?,?,?)";
		
		
		Scanner reader = new Scanner(System.in);
		System.out.println("Inserting Into Recipes Table\n");
		System.out.print("\t Recipe ID[INT]:");
		recipeID = reader.nextInt();
		
		System.out.print("\n\t Crafted Item Id[INT]:");
		itemID = reader.nextInt();
		
		System.out.print("\n\t Quantity Made[INT]:");
		quantity = reader.nextInt();
		
		while(!success)
		{
		
			try {
				PreparedStatement ps = conn.prepareStatement(insertRecipeSQL);
				ps.setInt(1, recipeID);
				ps.setInt(2, itemID);
				ps.setInt(3, quantity);
				count = ps.executeUpdate();
				if(count > 0)
					System.out.println("\nYou have Successfully Inserted into the DB\n\n\n");
				success = true;

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
		String insertItemSql = "INSERT INTO Items ("
				+ "itemName"
				+ "weight"
				+ "cost)"
				+ "VALUES (?,?,?)";
		String response, itemName = null;
		int  weight = 0, cost = 0;
		
		Scanner reader = new Scanner(System.in);
		System.out.println("The value {" + itemID + "} "
				+ "does not exist in this table"
				+ " would you like to create it [y/n]?");
		response = reader.next();
		if (response.equalsIgnoreCase("y"))
		{
			System.out.println("Inserting Into Recipes Table\n");
			System.out.print("\tItem Name[STRING]:");
			itemName = reader.next();
			
			System.out.print("\n\tWeight[INT]:");
			weight = reader.nextInt();
			
			System.out.print("\n\tCost[INT]");
			cost = reader.nextInt();
		}
		try {
			PreparedStatement ps = conn.prepareStatement(insertItemSql);
			ps.setString(1, itemName);
			ps.setInt(2, weight);
			ps.setInt(3, cost);
			ps.executeUpdate();
		} catch (SQLException e){
			System.out.println("\n\n"+ e.getMessage());	
			if(e.getMessage().contains("Primary Key"))
				insertFK(itemID);
				
		}
		
				
	}
	
	
}