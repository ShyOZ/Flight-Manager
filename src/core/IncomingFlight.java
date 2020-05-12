package core;

import java.time.LocalDateTime;

public class IncomingFlight extends Flight {
	public IncomingFlight(String airline, String flightNumber, String originCity, LocalDateTime flightTime,
			int terminal) {
		super(airline, flightNumber, originCity, flightTime, terminal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof IncomingFlight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString().replace("departing to/coming from", "coming from");
	}
}

