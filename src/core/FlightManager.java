package core;

import java.util.Scanner;

public class FlightManager {

	protected static String runVersion = "normal";

	public static void main(String[] args) {
		// every argument delete the 'outformat-' from each string, if it exists.
		// Make sure the runVersion we receive is not "HTML" or "text".
		for (String arg : args) {
			if (arg.startsWith("outformat")) {
				runVersion = arg.replace("outformat-", "");
				if (!(runVersion.equalsIgnoreCase("HTML") || runVersion.equalsIgnoreCase("text"))) {
					runVersion = "normal";
				}
				break;
			}
		}

		// create new UIHandler, and run it in console if the runVersion is "normal"
		// with the following menu
		UIHandler handler = new UIHandler();
		if (runVersion.equalsIgnoreCase("normal")) {
			Scanner scanner = new Scanner(System.in);
			int choice;

			do {
				choice = handler.showMenu(scanner);
				switch (choice) {
				case 0: // EXIT
					System.out.println("Bye Bye!");
					break;
				case 1: // Add a new flight
					handler.addNewFlight(scanner);
					break;
				case 2: // show all the flights
					handler.showAllFlights();
					break;
				case 3: // start filtering flights by requested filters, show results
					handler.showFlightsByFilter(scanner);
					break;
				case 4: // save all flights into the CSV file
					handler.saveAllToDefaultFile();
					break;
				case 5: // load all the flights from the CSV file
					handler.loadFromFile(scanner);
					break;
				}
			} while (choice != 0); // as long as the choice is valid
		}
		// if using a command line arguments
		else { // in case the renVersion is not "Normal"
			handler.loadFromDefaultFile();
			if (args.length == 1 & args[0].equalsIgnoreCase("all")) // if the first argument is "all" and no other
																	// arguments exist
				handler.showAllFlights();
			else {
				handler.filterByArguments(args);
				handler.showFilteredFlights();
			}
		}
	}
}
