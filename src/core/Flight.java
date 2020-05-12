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
		Pattern p = Pattern.compile("(^([A-Z]{0,3})([0-9]{1,4})$)"); // 1-3 capital letters, 1-4 digits
		Matcher m = p.matcher(flightNumber);
		if (!m.find())
			return false;
		// pads the number with zeros to be 4 digits
		String filler = String.format("%04d", Integer.parseUnsignedInt(m.group(3)));
		this.flightNumber = flightNumber.replace(m.group(3), filler);
		return true;
	}
	

	public String getCity() {
		return city;
	}

	private void setCity(String city) {
		this.city = city;
	}

	public LocalDateTime getFlightTime() {
		return flightTime;
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
	public Flight(String airline, String flightNumber, String city, LocalDateTime flightTime, int terminal) {
		setAirline(airline);
		setFlightNumber(flightNumber);
		setCity(city);
		setFlightTime(flightTime);
		setTerminal(terminal);
	}

	// Methods
	@Override
	public int compareTo(Flight other) {
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
		if (!(obj instanceof Flight))
			return false;
		Flight other = (Flight) obj;
		return Objects.equals(airline, other.airline) && Objects.equals(city, other.city)
				&& Objects.equals(flightNumber, other.flightNumber) && Objects.equals(flightTime, other.flightTime)
				&& terminal == other.terminal;
	}

	@Override
	public String toString() {
		return String.format("%s flight number %s, departing to/coming from %s; scheduled time: %s, at terminal %d",
				airline, flightNumber, city, flightTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
				terminal);
	}
}