package core;

import java.util.Scanner;

public class FlightManager {
	protected static String runVersion = "normal";

	public static void main(String[] args) {
		for(String arg : args) {
			if (arg.startsWith("outformat")) {
				runVersion = arg.replace("outformat-", "");
				if (!(runVersion.equalsIgnoreCase("HTML") || runVersion.equalsIgnoreCase("text"))) {
					runVersion = "normal";
				}
				break;
			}
		}
		
		UIHandler handler = new UIHandler();
		if (runVersion.equalsIgnoreCase("normal")) {
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
