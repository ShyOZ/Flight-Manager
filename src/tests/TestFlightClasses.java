package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import core.Flight;
import core.IncomingFlight;
import core.OutgoingFlight;

class TestFlightClasses {

	@Test
	public void testFlightAirline() {
		Flight testFlight = new IncomingFlight("Test Airline", "TEST101", "London", LocalDateTime.of(2022, 2, 2, 2, 2),
				3);
		assertEquals("NUL0000", testFlight.getFlightNumber());
	}

	@Test
	public void testIncomingFlightToString() {

		Flight newYork = new IncomingFlight("Elal", "LY1", "New York", LocalDateTime.of(2020, 05, 20, 00, 45), 3);
		String expectedNewYorkToString = "Elal flight number LY0001, coming from New York; scheduled time: 2020/05/20 00:45, at terminal 3";

		assertEquals(expectedNewYorkToString, newYork.toString());
	}

	@Test
	public void testOutgoingFlightToString() {

		Flight london = new OutgoingFlight("Elal", "LY315", "London", LocalDateTime.of(2020, 05, 20, 10, 10), 3);
		String expectedLondonToString = "Elal flight number LY0315, departing to London; scheduled time: 2020/05/20 10:10, at terminal 3";

		assertEquals(expectedLondonToString, london.toString());
	}

	@Test
	public void testFlightsAreEqualIfSameObject() {
		Flight testIncomingFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testOutgoingFlight = new OutgoingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(true, testIncomingFlight.equals(testIncomingFlight));
		assertEquals(true, testOutgoingFlight.equals(testOutgoingFlight));
	}

	@Test
	public void testFlightsAreEqualIfDifferentObjectButSameContent() {
		Flight testIncomingFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);

		Flight testOutgoingFlight = new OutgoingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testContentEqualsAndSameIncomingType = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);

		Flight testContentEqualsAndSameOutgoingType = new OutgoingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(true, testIncomingFlight.equals(testContentEqualsAndSameIncomingType));
		assertEquals(true, testOutgoingFlight.equals(testContentEqualsAndSameOutgoingType));
	}

	@Test
	public void testFlightNotEqualToSomeObject() {
		Flight testIncomingFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);

		Flight testOutgoingFlight = new OutgoingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(false, testIncomingFlight.equals(new Object()));
		assertEquals(false, testOutgoingFlight.equals(new Object()));
	}

	@Test
	public void testIncomingFlightNotEqualsToOutgoingFlight() {

		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testOutgoingFlight = new OutgoingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(false, testFlight.equals(testOutgoingFlight));
		assertEquals(false, testOutgoingFlight.equals(testFlight));

	}

	@Test
	public void testCompareFlightTime() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testCompareFlightTime = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 3), 3);
		assertEquals(false, testFlight.equals(testCompareFlightTime));
		assertEquals(-1, testFlight.compareTo(testCompareFlightTime));

	}

	@Test
	public void testCompareAirline() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testcompareAirline = new IncomingFlight("Test Airline2", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(false, testFlight.equals(testcompareAirline));
		assertEquals(-1, testFlight.compareTo(testcompareAirline));
	}

	@Test
	public void testCompareFlightNumber() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testCompareFlightNumber = new IncomingFlight("Test Airline", "TST102", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(false, testFlight.equals(testCompareFlightNumber));
		assertEquals(-1, testFlight.compareTo(testCompareFlightNumber));
	}

	@Test
	public void testCompareCity() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testCompareCity = new IncomingFlight("Test Airline", "TST101", "London2",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		assertEquals(false, testFlight.equals(testCompareCity));
		assertEquals(-1, testFlight.compareTo(testCompareCity));
	}

	@Test
	public void testCompareTerminal() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testCompareTerminal = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 4);
		assumeFalse(testFlight.equals(testCompareTerminal));
		assertEquals(-1, testFlight.compareTo(testCompareTerminal));
	}

	@Test
	public void testCompareFlightTypes() {
		IncomingFlight testFlight = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 3);
		Flight testCompareTerminal = new IncomingFlight("Test Airline", "TST101", "London",
				LocalDateTime.of(2022, 2, 2, 2, 2), 4);
		assertEquals(false, testFlight.equals(testCompareTerminal));
		assertEquals(-1, testFlight.compareTo(testCompareTerminal));
	}
}
