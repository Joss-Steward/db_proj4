package main;

import java.sql.SQLException;
import java.util.Scanner;

public class Main
{

	public static void main(String[] args) throws SQLException
	{
		DBOperations dbo = new DBOperations();
		boolean quit = false;
		int input = 0;
		Scanner reader = new Scanner(System.in);
		while (!quit)
		{
			System.out.println("\n\n\n\n 1. Select \n 2. Insert\n 3. Delete\n 4. Quit\n\n\n");
			System.out.print("What would you like to do:");
			input = reader.nextInt();
			System.out.println("\n\n");

			switch (input)
			{
				case 1:
					dbo.select();
					break;
				case 2:
					dbo.insert();
					break;
				case 3:
					dbo.delete();
					break;
				case 4:
					quit = true;
					break;
			}

		}
	}

}