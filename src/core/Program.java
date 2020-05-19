package core;

import java.util.Scanner;

public class Program {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		UIHandler handler = new UIHandler();
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
				handler.saveFlightsToFile();
				break;
			case 5:
				handler.loadFromFile(scanner);
				break;
			}
		} while (choice != 0);
	}
}
