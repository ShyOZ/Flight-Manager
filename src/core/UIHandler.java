package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class UIHandler {
	// Fields
	private TreeSet<Flight> allFlights = new TreeSet<>();
	private TreeSet<Flight> incomingFlights = new TreeSet<>();
	private TreeSet<Flight> outgoingFlights = new TreeSet<>();
	private int numOfTerminals;
	private final int BASE_NUM_OF_TERMINALS = 3;

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
		this(0);
	}

	public UIHandler(int numOfTerminals) {
		setNumOfTerminals(numOfTerminals);
	}

	// Methods
	public int showMenu(Scanner scanner) {
		System.out.println("========== MENU ==========");
		System.out.println("1: Add a new Flight");
		System.out.println("2: Show all Flights");
		System.out.println("3: Show all incoming flights");
		System.out.println("4: Show all outgoing flights");
		System.out.println("5: Save to file");
//		System.out.println("6: Load from file");
//		System.out.println("7: Remove a flight");
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

		airline = getValidString("[a-zA-Z ]+", "Enter airline's name (letters and spaces only)", scanner);

		flightNumber = getValidString("[A-Z0-9]+", "Enter flight number (capital letters and numbers only)", scanner);

		System.out.println("Enter the time of departure");
		flightTime = dateTimeBuilder(scanner);

		incoming = (getValidString("IN|OUT", "Is the flight [IN] incoming or [OUT] outgoing?", scanner)
				.equalsIgnoreCase("IN"));
		city = incoming ? "origin" : "destination";

		city = getValidString("[a-zA-z ]+", String.format("Enter %s city (letters and spaces only)", city), scanner);
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
			int year = getValidInt(2000, 2030, "On what year is the flight?", scanner);
			int month = getValidInt(1, 12, "On what month is the flight?", scanner);
			int maxDayInMonth = YearMonth.of(year, month).lengthOfMonth();
			int day = getValidInt(1, maxDayInMonth, "On what day is the flight?", scanner);
			int hour = getValidInt(0, 23, "On what hour is the flight?", scanner);
			int min = getValidInt(0, 59, "On what minute is the flight?", scanner);
			return LocalDateTime.of(year, month, day, hour, min);
		}
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
		saveToFile("flights");
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
		for (Flight flight : flightsToAdd) {
			addFlight(flight);
		}
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
			System.err.println();
			return null;
		}
	}
}