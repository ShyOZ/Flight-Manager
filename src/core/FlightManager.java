package core;

import java.util.Scanner;

public class FlightManager {
	protected static boolean cmdVersion;

	public static void main(String[] args) {
		cmdVersion = args.length > 0;
		UIHandler handler = new UIHandler();
		if (!cmdVersion) {
			Scanner scanner = new Scanner(System.in);
			int choice;
			do {
				choice = handler.showMenu(scanner);
				switch (choice) {
				case 0:
					System.out.println("Bye Bye!");
					break;
				case 1:
					handler.addNewFlight(scanner);
					break;
				case 2:
					handler.showAllFlights();
					break;
				case 3:
					handler.showFlightsByFilter(scanner);
					break;
				case 4:
					handler.saveAllToDefaultFile();
					break;
				case 5:
					handler.loadFromFile(scanner);
					break;
				}
			} while (choice != 0);
		}
		// if using a command line arguments
		else {
			handler.loadFromDefaultFile();
			if (args.length == 1 & args[0].equalsIgnoreCase("all"))
				handler.showAllFlights();
			else {
				handler.filterByArguments(args);
				handler.showFilteredFlights();
			}
		}
	}
}
