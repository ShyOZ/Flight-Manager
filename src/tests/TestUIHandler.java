package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import core.Flight;
import core.IncomingFlight;
import core.OutgoingFlight;
import core.UIHandler;

class TestUIHandler {
	private final String TEST_FILE_PATH = "src\\tests\\testCSVFile.csv";
	private UIHandler handler = new UIHandler();

	@Test
	void testGetValidInt_InvalidArguments() {
		Scanner testScanner = new Scanner("1.23\nhelp\n-1\n10");
		assertEquals(10, handler.getValidInt(0, 10, "Testing invalid arguments...", testScanner));
	}

	@Test
	void testGetValidInt_MaxLessThanMin_AnyNumber() {
		Scanner testScanner = new Scanner("1.23\nhelp\n10");
		assertEquals(10,
				handler.getValidInt(Integer.MAX_VALUE, Integer.MIN_VALUE, "Testing any number range...", testScanner));
	}

	@Test
	void testGetValidInt_LessThan() {
		Scanner testScanner = new Scanner("1.23\nhelp\n10\n-10");
		assertEquals(-10, handler.getValidInt(Integer.MIN_VALUE, 0, "Testing less than...", testScanner));
	}

	@Test
	void testGetValidInt_GreaterThan() {
		Scanner testScanner = new Scanner("1.23\nhelp\n-1\n10");
		assertEquals(10, handler.getValidInt(0, Integer.MAX_VALUE, "Testing greater than...", testScanner));
	}

	@ParameterizedTest
	@ValueSource(strings = { "IN", "OUT" })
	public void testFlightGeneration(String flightType) {
		handler = new UIHandler();
		StringBuilder input = new StringBuilder();
		input.append("---\nTest Airline\n"); // airline
		input.append("TST0101\n"); // flight number
		input.append("2021\n12\n02\n20\n21\n"); // date
		input.append(flightType + "\n"); // IN = incoming flight, OUT = outgoing flight
		input.append("Testing Grounds\n"); // city
		input.append("3\n"); // terminal
		Scanner testScanner = new Scanner(input.toString());

		Flight expectedFlight = (flightType.contentEquals("IN"))
				? new IncomingFlight("Test Airline", "TST0101", "Testing Grounds",
						LocalDateTime.of(2021, 12, 02, 20, 21), 3)
				: new OutgoingFlight("Test Airline", "TST0101", "Testing Grounds",
						LocalDateTime.of(2021, 12, 02, 20, 21), 3);
		handler.addNewFlight(testScanner);
		assertEquals(expectedFlight, handler.getAllFlights().first());
	}

	@Test
	void testReadAndWriteToCSV() throws FileNotFoundException {
		handler = new UIHandler();
		Flight newYorkIn = new IncomingFlight("Elal", "LY1", "New York", LocalDateTime.of(2020, 05, 20, 00, 45), 3);
		Flight londonOut = new OutgoingFlight("Elal", "LY315", "London", LocalDateTime.of(2020, 05, 20, 10, 10), 3);
		handler.addFlight(newYorkIn);
		handler.addFlight(londonOut);
		handler.saveToFile(TEST_FILE_PATH);

		StringBuilder expectedCSVContent = new StringBuilder();
		expectedCSVContent.append("Airline,Flight Number,Year,Month,Day,Hour,Minute,City,Terminal,Direction\n");
		expectedCSVContent.append("Elal,LY0001,New York,2020,05,20,00,45,3,INCOMING\n");
		expectedCSVContent.append("Elal,LY0315,London,2020,05,20,10,10,3,OUTGOING\n");

		StringBuilder actualCSVContent = new StringBuilder();
		Scanner scanner = new Scanner(new File(TEST_FILE_PATH));
		while (scanner.hasNextLine()) {
			actualCSVContent.append(scanner.nextLine() + "\n");
		}

		assertEquals(expectedCSVContent.toString(), actualCSVContent.toString());
		handler.loadFromFile(TEST_FILE_PATH);
		TreeSet<Flight> tree = new TreeSet<Flight>();
		tree.add(newYorkIn);
		tree.add(londonOut);
		assertEquals(handler.getAllFlights(), tree);
	}

	@Test
	void testFlightFilter() {
		handler = new UIHandler();
		// This flight is supposed to remain at the end of the test.
		Flight testFlight1 = new IncomingFlight("Elal", "LY1", "New York", LocalDateTime.of(2020, 05, 20, 00, 45), 3);
		// These flights is supposed to be filtered by time.
		Flight testFlight2FilteredAfter = new IncomingFlight("Elal", "LY1", "New York",
				LocalDateTime.of(2022, 11, 10, 00, 45), 3);
		Flight testFlight3FilteredBefore = new IncomingFlight("Elal", "LY1", "New York",
				LocalDateTime.of(2020, 05, 10, 00, 45), 3);
		// This flight is filtered by Airline.
		Flight testFlight4FilteredByAirline = new IncomingFlight("Turkish", "LY1", "New York",
				LocalDateTime.of(2021, 06, 15, 00, 45), 3);
		// This flight is filtered by City.
		Flight testFlight5FilteredByCity = new IncomingFlight("Elal", "LY1", "London",
				LocalDateTime.of(2020, 05, 20, 00, 45), 3);
		// This flight is filtered by Terminal.
		Flight testFlight6FilteredByTerminal = new IncomingFlight("Elal", "LY1", "New York",
				LocalDateTime.of(2020, 05, 20, 00, 45), 1);
		// This flight is filtered by Direction.
		Flight testFlight7FilteredByDirection = new OutgoingFlight("Elal", "LY1", "New York",
				LocalDateTime.of(2020, 05, 20, 00, 45), 3);
		handler.addFlight(testFlight1);
		handler.addFlight(testFlight2FilteredAfter);
		handler.addFlight(testFlight3FilteredBefore);
		handler.addFlight(testFlight4FilteredByAirline);
		handler.addFlight(testFlight5FilteredByCity);
		handler.addFlight(testFlight6FilteredByTerminal);
		handler.addFlight(testFlight7FilteredByDirection);
		String input = "3\n2020\n5\n16\n0\n0\n2022\n10\n10\n0\n0\nY\nElal\nY\nNew York\nY\n3\n1";
		Scanner scanner = new Scanner(input);

		ArrayList<Flight> testList = handler.showFlightsByFilter(scanner);
		for (Flight flight : testList) {
			System.out.println(flight);
		}
		ArrayList<Flight> expectedList = new ArrayList<>();
		expectedList.add(testFlight1);
		assertEquals(expectedList, testList);
	}

}
