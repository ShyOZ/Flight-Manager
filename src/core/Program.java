package core;

import java.util.Scanner;

public class Program {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		UIHandler helper = new UIHandler();
		int choice;
		do {
			choice = helper.showMenu(scanner);
			switch (choice) {
			case 0:
				System.out.println("Bye Bye!");
				break;
			case 1:
				helper.addNewFlight(scanner);
				break;
			case 2:
				helper.showAllFlights();
				break;
			case 3:
				helper.showIncomingFlights();
				break;
			case 4:
				helper.showOutgoingFlights();
				break;
			case 5:
				helper.saveFlightsToFile();
				break;
			case 6:
				helper.loadFromFile("flights");
				break;
			}
		} while (choice != 0);
	}
}

