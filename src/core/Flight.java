package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Flight implements Comparable<Flight> {
	// Constants

	// Fields
	protected String airline;
	protected String flightNumber = "NUL0000";
	protected String city;
	protected String country;
	protected String airport;
	protected LocalDateTime flightTime;
	protected int terminal;

	// Properties (Getters and Setters)
	public String getAirline() {
		return airline;
	}

	private void setAirline(String airline) {
		this.airline = airline;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	private boolean setFlightNumber(String flightNumber) {
		Pattern pattern = Pattern.compile("(^([A-Z]{0,3})([0-9]{1,4})$)"); // 1-3 capital letters, 1-4 digits
		Matcher matcher = pattern.matcher(flightNumber);
		if (!matcher.find())
			return false;
		// pads the number with zeros to be 4 digits
		String filler = String.format("%04d", Integer.parseUnsignedInt(matcher.group(3)));
		this.flightNumber = flightNumber.replace(matcher.group(3), filler);
		return true;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getAirport() {
		return this.airport;
	}

	private void setCity(String city) {
		this.city = city;
	}

	private void setCountry(String country) {
		this.country = country;
	}

	private void setAirport(String airport) {
		this.airport = airport;
	}

	public LocalDateTime getFlightTime() {
		return flightTime;
	}

	public String getDayOfWeek() {
		return flightTime.getDayOfWeek().toString();
	}

	private Boolean setFlightTime(LocalDateTime flightTime) {
		this.flightTime = LocalDateTime.from(flightTime);
		return true;
	}

	public int getTerminal() {
		return terminal;
	}

	private void setTerminal(int terminal) {
		this.terminal = terminal;
	}

	// Constructors
	public Flight(String airline, String flightNumber, String city, String country, String airport,
			LocalDateTime flightTime, int terminal) {
		setAirline(airline);
		setFlightNumber(flightNumber);
		setCity(city);
		setCountry(country);
		setAirport(airport);
		setFlightTime(flightTime);
		setTerminal(terminal);
	}

	// Methods
	@Override
	public int compareTo(Flight other) {
		// Compares [this] flight to another flight, by time, airline, flight number, city, and terminal
		// Returns -1, 0 or 1 depending on the aforementioned parameters by order to determined lexicographic order
		int comparedFlightTime = flightTime.compareTo(other.flightTime);
		if (comparedFlightTime == 0) {
			int comparedAirline = airline.compareTo(other.airline);
			if (comparedAirline == 0) {
				int comparedFlightNumber = flightNumber.compareTo(other.flightNumber);
				if (comparedFlightNumber == 0) {
					int comparedCity = city.compareTo(other.city);
					if (comparedCity == 0) {
						int compareTerminal = Integer.compare(terminal, other.terminal);
						if (compareTerminal == 0)
							return this.getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
						else
							return compareTerminal;
					} else
						return comparedCity;
				} else
					return comparedFlightNumber;
			} else
				return comparedAirline;
		} else
			return comparedFlightTime;
	}

	@Override
	public boolean equals(Object obj) {
		// Compare [this] flight to another flight, and returns true if all properties are equal.
		if (!(obj instanceof Flight))
			return false;
		Flight other = (Flight) obj;
		return Objects.equals(airline, other.airline) && Objects.equals(city, other.city)
				&& Objects.equals(country, other.country) && Objects.equals(airport, other.airport)
				&& Objects.equals(flightNumber, other.flightNumber) && Objects.equals(flightTime, other.flightTime)
				&& terminal == other.terminal;
	}

	@Override
	public String toString() {
		return String.format(
				"%s flight number %s, departing to/coming from %s, %s, %s airport; scheduled time: %s, TLV terminal %d",
				airline, flightNumber, city, country, airport,
				flightTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")), terminal);
	}
}