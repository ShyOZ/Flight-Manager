package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class UIHandler {
	// Fields
	private TreeSet<Flight> allFlights = new TreeSet<>();
	private TreeSet<Flight> incomingFlights = new TreeSet<>();
	private TreeSet<Flight> outgoingFlights = new TreeSet<>();
	private int numOfTerminals;
	private final static int BASE_NUM_OF_TERMINALS = 3;
	private final String VALID_NAME = "[a-zA-Z ]+";
	private final String VALID_FLIGHTNUMBER = "(^([A-Z]{0,3})([0-9]{1,4})$)";
	private final String YES_NO_QUESTION = "Y|N";
	private final String IN_OR_OUT = "IN|OUT";

	// Properties (Getters and Setters)

	private void setNumOfTerminals(int numOfTerminals) {
		this.numOfTerminals = (numOfTerminals > BASE_NUM_OF_TERMINALS) ? numOfTerminals : BASE_NUM_OF_TERMINALS;
	}

	public TreeSet<Flight> getAllFlights() {
		return allFlights;
	}

	public TreeSet<Flight> getIncomingFlights() {
		return incomingFlights;
	}

	public TreeSet<Flight> getOutgoingFlights() {
		return outgoingFlights;
	}

	public void clearFlightList() {
		allFlights.clear();
		incomingFlights.clear();
		outgoingFlights.clear();
	}

	// Constructors
	public UIHandler() {
		this(BASE_NUM_OF_TERMINALS);
	}

	public UIHandler(int numOfTerminals) {
		setNumOfTerminals(numOfTerminals);
	}

	// Methods
	public int showMenu(Scanner scanner) {
		System.out.println("========== MENU ==========");
		System.out.println("1: Add a new Flight");
		System.out.println("2: Show all Flights");
		System.out.println("3: Filter and show flights");
		System.out.println("4: Save to file");
		System.out.println("5: Load from file");
		System.out.println("0: EXIT");
		System.out.println("========== MENU ==========");
		return getValidInt(0, 5, "Enter your choice", scanner);
	}

	public boolean addFlight(Flight newFlight) {
		if (!allFlights.add(newFlight))
			return false;
		return (newFlight instanceof IncomingFlight) ? incomingFlights.add((IncomingFlight) newFlight)
				: outgoingFlights.add((OutgoingFlight) newFlight);
	}

	public boolean addNewFlight(Scanner scanner) {
		return addFlight(generateNewFlight(scanner));
	}

	public Flight generateNewFlight(Scanner scanner) {
		String airline;
		String flightNumber;
		LocalDateTime flightTime;
		boolean incoming;
		String city;
		int terminal;

		airline = getValidString(VALID_NAME, "Enter airline's name (letters and spaces only)", scanner);

		flightNumber = getValidString(VALID_FLIGHTNUMBER, "Enter flight number (capital letters and numbers only)",
				scanner);

		System.out.println("Enter the time of departure");
		flightTime = dateTimeBuilder(scanner);

		incoming = (getValidString(IN_OR_OUT, "Is the flight [IN] incoming or [OUT] outgoing?", scanner)
				.equalsIgnoreCase("IN"));
		city = incoming ? "origin" : "destination";

		city = getValidString(VALID_NAME, String.format("Enter %s city (letters and spaces only)", city), scanner);
		terminal = getValidInt(1, numOfTerminals, "Enter terminal number", scanner);
		scanner.nextLine();
		return (incoming) ? new IncomingFlight(airline, flightNumber, city, flightTime, terminal)
				: new OutgoingFlight(airline, flightNumber, city, flightTime, terminal);
	}

	private String getValidString(String regex, String message, Scanner scanner) {
		while (true) {
			System.out.println(message);
			String string = scanner.nextLine();
			if (string.isBlank())
				string = scanner.nextLine();
			if (string.matches(regex))
				return string;
		}
	}

	private LocalDateTime dateTimeBuilder(Scanner scanner) {
		while (true) {
			int year = getValidInt(2000, 2030, "What year?", scanner);
			int month = getValidInt(1, 12, "What month?", scanner);
			int maxDayInMonth = YearMonth.of(year, month).lengthOfMonth();
			int day = getValidInt(1, maxDayInMonth, "On what day?", scanner);
			int hour = getValidInt(0, 23, "At what hour?", scanner);
			int min = getValidInt(0, 59, "At what minute?", scanner);
			return LocalDateTime.of(year, month, day, hour, min);
		}
	}

	public ArrayList<Flight> showFlightsByFilter(Scanner scanner) {
		// Function that receives a list of all flights here
		List<Flight> flightList = new ArrayList<>(allFlights);
		System.out.println("Would you like to filter by Time?");
		System.out.println("0: No");
		System.out.println("1: All flights before a certain date");
		System.out.println("2: All flights after a certain date");
		System.out.println("3: All flights in a certain range");
		int choice = getValidInt(0, 3, "Enter your choice", scanner);
		switch (choice) {
		case 1:
			System.out.println("Before what time?");
			flightList = filterFlightsBefore(flightList, dateTimeBuilder(scanner));
			break;
		case 2:
			System.out.println("After what time?");
			flightList = filterFlightsAfter(flightList, dateTimeBuilder(scanner));
			break;
		case 3:
			System.out.println("After what time?");
			LocalDateTime laterThan = dateTimeBuilder(scanner);
			LocalDateTime earlierThan;
			do {
				System.out.println("Before what time?");
				earlierThan = dateTimeBuilder(scanner);
			} while (laterThan.isAfter(earlierThan));
			flightList = filterFlightsBefore(flightList, earlierThan);
			flightList = filterFlightsAfter(flightList, laterThan);
			break;
		}
		boolean answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by airline? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			// In this case, user wants to filter by Airline
			String name = getValidString(VALID_NAME, "Enter Airline Name (Letters only): ", scanner);
			flightList = filterByAirline(name, flightList, scanner);
		}
		answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by city? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			// In this case, user wants to filter by City
			String name = getValidString(VALID_NAME, "Enter City Name (Letters only): ", scanner);
			flightList = filterByCity(name, flightList, scanner);
		}
		answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by terminal? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			choice = getValidInt(1, numOfTerminals, "Enter terminal number: ", scanner);
			flightList = filterByTerminal(choice, flightList, scanner);
		}
		System.out.println("Would you like to filter by Direction?");
		System.out.println("0: No");
		System.out.println("1: All incoming");
		System.out.println("2: All outgoing");
		choice = getValidInt(0, 2, "Enter your choice ", scanner);
		switch (choice) {
		case 1:
			flightList = filterByInOut(flightList, IncomingFlight.class, scanner);
			break;
		case 2:
			flightList = filterByInOut(flightList, OutgoingFlight.class, scanner);
			break;
		}
		return (ArrayList<Flight>) flightList;
	}

	private List<Flight> filterByInOut(List<Flight> flightList, Class<? extends Flight> flightType, Scanner scanner) {
		return flightList.stream().filter(flight -> flight.getClass().equals(flightType)).collect(Collectors.toList());
	}

	private List<Flight> filterByTerminal(int choice, List<Flight> flightList, Scanner scanner) {
		return flightList.stream().filter(flight -> flight.getTerminal() == choice).collect(Collectors.toList());

	}

	private List<Flight> filterByCity(String name, List<Flight> flightList, Scanner scanner) {
		return flightList.stream().filter(flight -> flight.getCity().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	private List<Flight> filterByAirline(String name, List<Flight> flightList, Scanner scanner) {
		return flightList.stream().filter(flight -> flight.getAirline().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	private List<Flight> filterFlightsBefore(List<Flight> flightList, LocalDateTime dateTimeBuilder) {
		return flightList.stream().filter(flight -> flight.getFlightTime().compareTo(dateTimeBuilder) <= 0)
				.collect(Collectors.toList());

	}

	private List<Flight> filterFlightsAfter(List<Flight> flightList, LocalDateTime dateTimeBuilder) {
		return flightList.stream().filter(flight -> flight.getFlightTime().compareTo(dateTimeBuilder) >= 0)
				.collect(Collectors.toList());

	}

	/**
	 * <p>
	 * Prompts through the standard output stream to enter a number in the range
	 * {@code [min,max]} and tries to read from the given {@code scanner}.
	 * </p>
	 * The code handles {@code InputMismatchException}s and {@code out of range}
	 * inputs by simply ignoring them and trying to get a valid input
	 * 
	 * @param min     lower bound of the input range
	 * @param max     upper bound of the input range
	 * @param message message prompt to be printed along with the required range
	 * @param scanner
	 * @return an integer between {@code min} and {@code max}, or {@code min} if
	 *         {@code min = max}
	 */
	public int getValidInt(int min, int max, String message, Scanner scanner) {
		if (min == max)
			return min;
		int n;

		if (max < min) {
			n = max;
			max = min;
			min = n;
		}

		String range = "";

		if (max == Integer.MAX_VALUE) {
			// [min, 'inf')
			if (min != Integer.MIN_VALUE)
				range = String.format("[at least %d]", min);
		} // ('-inf',max]
		else if (min == Integer.MIN_VALUE) {
			range = String.format("[at most %d]", max);
		} else if (max - min == 1)
			range = String.format("[%d | %d]", min, max);
		else
			range = String.format("[%d - %d]", min, max);

		while (true) {
			try {
				System.out.println(message + " " + range);
				n = scanner.nextInt();
				while (n < min || max < n) {
					System.out.println(message + " " + range);
					n = scanner.nextInt();
				}
				return n;
			} catch (InputMismatchException e) {
				scanner.nextLine();
			}
		}
	}

	public void saveFlightsToFile() {
		saveToFile("resources/flights.csv");
	}

	// no need to test
	public void showAllFlights() {
		showFlightList(allFlights);
	}

	// no need to test
	public void showIncomingFlights() {
		showFlightList(incomingFlights);
	}

	// no need to test
	public void showOutgoingFlights() {
		showFlightList(outgoingFlights);
	}

	// no need to test
	private void showFlightList(Set<Flight> wantedList) {
		if (wantedList.isEmpty())
			System.out.println("Empty flight list!");
		for (Flight flight : wantedList)
			System.out.println(flight);
	}

	private String flightToCommaSeparatedValue(Flight flight) {
		String direction = flight.getClass().getSimpleName().replace("Flight", "").toUpperCase();
		return String.format("%s,%s,%s,%s,%s,%s", flight.getAirline(), flight.getFlightNumber(), flight.getCity(),
				flight.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm")), flight.getTerminal(),
				direction);
	}

	private Flight commaSeparatedValueToFlight(String line) {
		String[] values = line.split(",");
		String airline = values[0];
		String flightNumber = values[1];
		String city = values[2];
		int year = Integer.parseInt(values[3]);
		int month = Integer.parseInt(values[4]);
		int dayOfMonth = Integer.parseInt(values[5]);
		int hour = Integer.parseInt(values[6]);
		int minute = Integer.parseInt(values[7]);
		LocalDateTime flightTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		int terminal = Integer.parseInt(values[8]);
		boolean isIncoming = values[9].contentEquals("INCOMING");
		return (isIncoming) ? new IncomingFlight(airline, flightNumber, city, flightTime, terminal)
				: new OutgoingFlight(airline, flightNumber, city, flightTime, terminal);
	}
	
	
	public boolean saveToFile(String pathname) {
		File f;
		f = (pathname.endsWith(".csv")) ? new File(pathname) : new File(pathname + ".csv");
		return saveToFile(f);
	}

	public boolean saveToFile(File file) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);

			StringBuilder sb = new StringBuilder(
					"Airline,Flight Number,Year,Month,Day,Hour,Minute,City,Terminal,Direction\n");
			for (Flight flight : allFlights) {
				sb.append(flightToCommaSeparatedValue(flight) + "\n");
			}
			pw.print(sb.toString());
			pw.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void loadFromFile(Scanner scanner) {
		String pathname = getValidString(".+", "Enter file pathname", scanner);
		loadFromFile(pathname);
	}

	/**
	 * <p>
	 * <b><i> Overrides existing flights</i></b> and adds the flights from a file
	 * with the given {@code pathname}
	 * </p>
	 * 
	 * @param file - CSV File to add from
	 */
	public void loadFromFile(String pathname) {
		File file = (pathname.endsWith(".csv")) ? new File(pathname) : new File(pathname + ".csv");
		loadFromFile(file);
	}

	/**
	 * <p>
	 * <b><i> Overrides existing flights</i></b> and adds the flights from the
	 * {@code file}
	 * </p>
	 * 
	 * @param file - CSV File to add from
	 */
	public void loadFromFile(File file) {
		clearFlightList();
		addFromFile(file);
	}

	public void addFromFile(Scanner scanner) {
		String pathname = getValidString(".+", "Enter file pathname", scanner);
		addFromFile(pathname);
	}

	/**
	 * <p>
	 * Adds new flights to existing flights from a file with the given
	 * {@code pathname}
	 * </p>
	 * 
	 * @param file - CSV File to add from
	 */
	public void addFromFile(String pathname) {
		File file = (pathname.endsWith(".csv")) ? new File(pathname) : new File(pathname + ".csv");

		addFromFile(file);
	}

	/**
	 * <p>
	 * Adds new flights to existing flights from the {@code file}
	 * </p>
	 * 
	 * @param file - CSV File to add from
	 */
	public void addFromFile(File file) {
		TreeSet<Flight> flightsToAdd = readFromFile(file);
		if (flightsToAdd != null)
			for (Flight flight : flightsToAdd)
				addFlight(flight);
	}

	private TreeSet<Flight> readFromFile(File file) {
		try {
			Scanner scanner = new Scanner(file);
			scanner.nextLine(); // skips header line
			TreeSet<Flight> flights = new TreeSet<>();
			while (scanner.hasNextLine()) {
				flights.add(commaSeparatedValueToFlight(scanner.nextLine()));
			}
			scanner.close();
			return flights;
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			return null;
		}
	}
}