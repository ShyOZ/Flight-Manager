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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class UIHandler {
	// Fields
	private TreeSet<Flight> allFlights = new TreeSet<>();
	private TreeSet<Flight> incomingFlights = new TreeSet<>();
	private TreeSet<Flight> outgoingFlights = new TreeSet<>();
	private TreeSet<Flight> filteredFlights = new TreeSet<>();
	private int numOfTerminals;
	private final static int BASE_NUM_OF_TERMINALS = 3;
	private final String VALID_NAME = "[a-zA-Z ]+";
	private final String VALID_FLIGHT_NUMBER = "(^([A-Z]{0,3})([0-9]{1,4})$)";
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
		println("========== MENU ==========");
		println("1: Add a new Flight");
		println("2: Show all Flights");
		println("3: Filter and show flights");
		println("4: Save to file");
		println("5: Load from file");
		println("0: EXIT");
		println("========== MENU ==========");
		return getValidInt(0, 5, "Enter your choice", scanner);
	}

	// Add an existing flight
	public boolean addFlight(Flight newFlight) {
		if (!allFlights.add(newFlight))
			return false;
		return (newFlight instanceof IncomingFlight) ? incomingFlights.add((IncomingFlight) newFlight)
				: outgoingFlights.add((OutgoingFlight) newFlight);
	}

	// Generates a new flight and adds it using console
	public boolean addNewFlight(Scanner scanner) {
		return addFlight(generateNewFlight(scanner));
	}

	// Generate a new flight using console
	public Flight generateNewFlight(Scanner scanner) {
		String airline;
		String flightNumber;
		LocalDateTime flightTime;
		boolean incoming;
		String city;
		String country;
		String airport;
		int terminal;

		airline = getValidString(VALID_NAME, "Enter airline's name (letters and spaces only)", scanner);
		flightNumber = getValidString(VALID_FLIGHT_NUMBER, "Enter flight number (capital letters and numbers only)",
				scanner);
		println("Enter the time of departure");
		flightTime = dateTimeBuilder(scanner);
		incoming = (getValidString(IN_OR_OUT, "Is the flight [IN] incoming or [OUT] outgoing?", scanner)
				.equalsIgnoreCase("IN"));
		String inOrOut = incoming ? "origin" : "destination";
		city = getValidString(VALID_NAME, String.format("Enter %s city (letters and spaces only)", inOrOut), scanner);
		country = getValidString(VALID_NAME, String.format("Enter %s country (letters and spaces only)", inOrOut),
				scanner);
		airport = getValidString(VALID_NAME, String.format("Enter %s airport (letters and spaces only)", inOrOut),
				scanner);
		terminal = getValidInt(1, numOfTerminals, "Enter terminal number", scanner);
		scanner.nextLine();
		return (incoming) ? new IncomingFlight(airline, flightNumber, city, country, airport, flightTime, terminal)
				: new OutgoingFlight(airline, flightNumber, city, country, airport, flightTime, terminal);
	}

	/**
	 * General purpose function for receiving String value from user with scanner
	 * 
	 * @param regex   - the regular expression, i.e. the format of the requires
	 *                string.
	 * @param message - the instruction message shown to the user.
	 * @param scanner - the {@code Scanner} object used for input,
	 * @return the user's valid string
	 */
	private String getValidString(String regex, String message, Scanner scanner) {
		while (true) {
			println(message);
			String string = scanner.nextLine();
			if (string.isBlank())
				string = scanner.nextLine();
			if (string.matches(regex))
				return string;
		}
	}

	// Generate a new LocalDateTime variable
	private LocalDateTime dateTimeBuilder(Scanner scanner) {
		while (true) {
			int year = getValidInt(2000, 2030, "What year?", scanner);
			int month = getValidInt(1, 12, "What month?", scanner);
			int maxDayInMonth = YearMonth.of(year, month).lengthOfMonth();
			int day = getValidInt(1, maxDayInMonth, "On what day?", scanner);
			int hour = getValidInt(0, 23, "At what hour?", scanner);
			int minute = getValidInt(0, 59, "At what minute?", scanner);
			return LocalDateTime.of(year, month, day, hour, minute);
		}
	}

	public ArrayList<Flight> showFlightsByFilter(Scanner scanner) {
		List<Flight> flightList = new ArrayList<>(allFlights);
		println("Would you like to filter by Time?");
		println("0: No");
		println("1: All flights before a certain date");
		println("2: All flights after a certain date");
		println("3: All flights in a certain range");

		// user choice for filtering by date / time
		int choice = getValidInt(0, 3, "Enter your choice", scanner);
		switch (choice) {
		case 1:
			println("Before what time?");
			flightList = filterFlightsTo(flightList, dateTimeBuilder(scanner));
			break;
		case 2:
			println("After what time?");
			flightList = filterFlightsFrom(flightList, dateTimeBuilder(scanner));
			break;
		case 3:
			println("After what time?");
			LocalDateTime laterThan = dateTimeBuilder(scanner);
			LocalDateTime earlierThan;
			do {
				println("Before what time?");
				earlierThan = dateTimeBuilder(scanner);
			} while (laterThan.isAfter(earlierThan));
			flightList = filterFlightsTo(flightList, earlierThan);
			flightList = filterFlightsFrom(flightList, laterThan);
			break;
		}

		// filtering menu by other means
		boolean answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by airline? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			// In this case, user wants to filter by Airline
			String name = getValidString(VALID_NAME, "Enter Airline Name (Letters only): ", scanner);
			flightList = filterByAirline(name, flightList);
		}
		answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by city? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			// In this case, user wants to filter by City
			String name = getValidString(VALID_NAME, "Enter City Name (Letters only): ", scanner);
			flightList = filterByCity(name, flightList);
		}
		answer = (getValidString(YES_NO_QUESTION, "Would you like to filter by terminal? [Y|N]", scanner)
				.equalsIgnoreCase("Y"));
		if (answer) {
			choice = getValidInt(1, numOfTerminals, "Enter terminal number: ", scanner);
			flightList = filterByTerminal(choice, flightList);
		}
		println("Would you like to filter by Direction?");
		println("0: No");
		println("1: All incoming");
		println("2: All outgoing");
		choice = getValidInt(0, 2, "Enter your choice ", scanner);
		switch (choice) {
		case 1:
			flightList = filterByInOut(flightList, IncomingFlight.class);
			break;
		case 2:
			flightList = filterByInOut(flightList, OutgoingFlight.class);
			break;
		}
		filteredFlights = new TreeSet<Flight>(flightList);
		return (ArrayList<Flight>) flightList;
	}

	// Filter of Direction
	private List<Flight> filterByInOut(List<Flight> flightList, Class<? extends Flight> flightType) {
		return flightList.stream().filter(flight -> flight.getClass().equals(flightType)).collect(Collectors.toList());
	}

	private List<Flight> filterByTerminal(int choice, List<Flight> flightList) {
		return flightList.stream().filter(flight -> flight.getTerminal() == choice).collect(Collectors.toList());
	}

	private List<Flight> filterByCity(String name, List<Flight> flightList) {
		return flightList.stream().filter(flight -> flight.getCity().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	private List<Flight> filterByCountry(String name, List<Flight> flightList) {
		return flightList.stream().filter(flight -> flight.getCountry().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	private List<Flight> filterByAirport(String name, List<Flight> flightList) {
		return flightList.stream().filter(flight -> flight.getAirport().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	private List<Flight> filterByAirline(String name, List<Flight> flightList) {
		return flightList.stream().filter(flight -> flight.getAirline().equalsIgnoreCase(name))
				.collect(Collectors.toList());
	}

	// Filter by upper bound date
	private List<Flight> filterFlightsTo(List<Flight> flightList, LocalDateTime dateTimeBuilder) {
		return flightList.stream().filter(flight -> flight.getFlightTime().compareTo(dateTimeBuilder) <= 0)
				.collect(Collectors.toList());
	}

	// Filter by lower bound date
	private List<Flight> filterFlightsFrom(List<Flight> flightList, LocalDateTime dateTimeBuilder) {
		return flightList.stream().filter(flight -> flight.getFlightTime().compareTo(dateTimeBuilder) >= 0)
				.collect(Collectors.toList());
	}

	private List<Flight> filterFlightsByDayOfWeek(List<Flight> flightList, String[] daysOfWeek) {
		return flightList.stream().filter(flight -> {
			for (String day : daysOfWeek)
				if (flight.getDayOfWeek().equalsIgnoreCase(day))
					return true;
			return false;
		}).collect(Collectors.toList());
	}

	public ArrayList<Flight> filterByArguments(String[] args) {
		return (ArrayList<Flight>) filterByArguments(new ArrayList<Flight>(allFlights), args);
	}

	/**
	 * the function receives a flight list and array of filter arguments and returns
	 * a filtered list. also sets the filtered flights property for future uses.
	 * 
	 * @param flightList - the flight list to be filtered
	 * @param args       - the list of arguments to use for filtering
	 * @return The Filtered Flight List
	 */
	private List<Flight> filterByArguments(List<Flight> flightList, String[] args) {
		List<Flight> filteredFlights = flightList;
		String value;
		TreeMap<String, String> map = new TreeMap<>();
		for (String s : args) {
			String[] keyValue = s.split("-");
			if (keyValue.length == 2)
				map.put(keyValue[0], keyValue[1]);
		}
		value = map.get("from");
		if (value != null) {
			try {
				LocalDateTime date = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
				filteredFlights = filterFlightsFrom(filteredFlights, date);
			} catch (Exception e) {
				System.err.println("The date string " + value + " is not of the format \"yyyy/MM/dd HH:mm\".");
			}
		}

		value = map.get("to");
		if (value != null) {
			try {
				LocalDateTime date = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
				filteredFlights = filterFlightsTo(filteredFlights, date);
			} catch (Exception e) {
				System.err.println("The date string " + value + " is not of the format \"yyyy/MM/dd HH:mm\".");
			}
		}

		value = map.get("airline");
		if (value != null) {
			if (value.matches(VALID_NAME))
				filteredFlights = filterByAirline(value, filteredFlights);
			else
				System.err.println("Invalid airline name.");
		}

		value = map.get("city");
		if (value != null) {
			if (value.matches(VALID_NAME))
				filteredFlights = filterByCity(value, filteredFlights);
			else
				System.err.println("Invalid city name.");
		}

		value = map.get("country");
		if (value != null) {
			if (value.matches(VALID_NAME))
				filteredFlights = filterByCountry(value, filteredFlights);
			else
				System.err.println("Invalid country name.");
		}

		value = map.get("airport");
		if (value != null) {
			if (value.matches(VALID_NAME))
				filteredFlights = filterByAirport(value, filteredFlights);
			else
				System.err.println("Invalid airport name.");
		}

		value = map.get("terminal");
		if (value != null) {
			try {
				int terminalNumber = Integer.parseInt(value);
				if (terminalNumber < 0 || numOfTerminals < terminalNumber)
					System.err.println("Invalid terminal input (integer not in range).");
				else
					filteredFlights = filterByTerminal(terminalNumber, filteredFlights);
			} catch (Exception e) {
				System.err.println("Invalid terminal input (not an integer).");
			}
		}

		value = map.get("direction");
		if (value != null) {
			if (value.equalsIgnoreCase("arrivals"))
				filteredFlights = filterByInOut(filteredFlights, IncomingFlight.class);
			else if (value.equalsIgnoreCase("departures"))
				filteredFlights = filterByInOut(filteredFlights, OutgoingFlight.class);
			if (!(value.equalsIgnoreCase("all")))
				System.err.println("Invalid direction.");
		}

		value = map.get("day_of_week");
		if (value != null) {
			String[] days = value.split(",");
			filteredFlights = filterFlightsByDayOfWeek(filteredFlights, days);
		}

		this.filteredFlights = new TreeSet<Flight>(filteredFlights);
		return filteredFlights;
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
		int input;
		if (max < min) {
			input = max;
			max = min;
			min = input;
		}
		String range = "";
		if (max == Integer.MAX_VALUE) {
			// [min, 'inf')
			if (min != Integer.MIN_VALUE)
				range = String.format("[at least %d]", min);
		} // ('-inf',max]
		else if (min == Integer.MIN_VALUE)
			range = String.format("[at most %d]", max);
		else if (max - min == 1)
			range = String.format("[%d | %d]", min, max);
		else
			range = String.format("[%d - %d]", min, max);

		while (true) {
			try {
				println(message + " " + range);
				input = scanner.nextInt();
				while (input < min || max < input) {
					println(message + " " + range);
					input = scanner.nextInt();
				}
				return input;
			} catch (InputMismatchException e) {
				scanner.nextLine();
			}
		}
	}

	public void showAllFlights() {
		showFlightList(allFlights);
	}

	public void showIncomingFlights() {
		showFlightList(incomingFlights);
	}

	public void showOutgoingFlights() {
		showFlightList(outgoingFlights);
	}

	public void showFilteredFlights() {
		showFlightList(filteredFlights);
	}

	private void showFlightList(Set<Flight> wantedList) {
		if (wantedList.isEmpty())
			println("Empty flight list!");
		for (Flight flight : wantedList)
			println(flight);
	}

	// return a string value of a flight for saving inside CSV based DB.
	private String flightToCommaSeparatedValue(Flight flight) {
		String direction = flight.getClass().getSimpleName().replace("Flight", "").toUpperCase();
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s", flight.getAirline(), flight.getFlightNumber(),
				flight.getCountry(), flight.getCity(), flight.getAirport(),
				flight.getFlightTime().format(DateTimeFormatter.ofPattern("yyyy,MM,dd,HH,mm")), flight.getTerminal(),
				direction);
	}

	// reverse of above.
	private Flight commaSeparatedValueToFlight(String line) {
		String[] values = line.split(",");
		String airline = values[0];
		String flightNumber = values[1];
		String country = values[2];
		String city = values[3];
		String airport = values[4];
		int year = Integer.parseInt(values[5]);
		int month = Integer.parseInt(values[6]);
		int dayOfMonth = Integer.parseInt(values[7]);
		int hour = Integer.parseInt(values[8]);
		int minute = Integer.parseInt(values[9]);
		LocalDateTime flightTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		int terminal = Integer.parseInt(values[10]);
		boolean isIncoming = values[11].contentEquals("INCOMING");
		return (isIncoming) ? new IncomingFlight(airline, flightNumber, city, country, airport, flightTime, terminal)
				: new OutgoingFlight(airline, flightNumber, city, country, airport, flightTime, terminal);
	}

	public void saveAllToDefaultFile() {
		saveAllToFile("resources/flights.csv");
	}

	public void saveFilteredToDefaultFile() {
		saveFilteredToFile("resources/flights.csv");
	}

	public boolean saveAllToFile(String pathname) {
		File file;
		file = (pathname.endsWith(".csv")) ? new File(pathname) : new File(pathname + ".csv");
		return saveAllToFile(file);
	}

	public boolean saveFilteredToFile(String pathname) {
		File file;
		file = (pathname.endsWith(".csv")) ? new File(pathname) : new File(pathname + ".csv");
		return saveFilteredToFile(file);
	}

	public boolean saveAllToFile(File file) {
		return saveToFile(allFlights, file);
	}

	public boolean saveFilteredToFile(File file) {
		return saveToFile(filteredFlights, file);
	}

	private boolean saveToFile(TreeSet<Flight> flights, File file) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);
			StringBuilder sb = new StringBuilder(
					"Airline,Flight Number,Country,City,Airport,Year,Month,Day,Hour,Minute,Terminal,Direction\n");
			for (Flight flight : flights) {
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

	public void loadFromDefaultFile() {
		loadFromFile("resources/flights.csv");
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

	private <T> void println(T s) {
		if (FlightManager.runVersion.equalsIgnoreCase("HTML"))
			System.out.print(s + "<br>");
		else
			System.out.println(s);
	}
}