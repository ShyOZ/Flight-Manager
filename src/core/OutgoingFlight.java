package core;

import java.time.LocalDateTime;

public class OutgoingFlight extends Flight {
	public OutgoingFlight(String airline, String flightNumber, String destinationCity, String country, String airport,
			LocalDateTime flightTime, int terminal) {
		super(airline, flightNumber, destinationCity, country, airport, flightTime, terminal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof OutgoingFlight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString().replace("departing to/coming from", "departing to");
	}
}
