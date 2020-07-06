package dev.estudos.jbank.utils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class FlexibleCalendar {

	static Clock clock = Clock.systemDefaultZone();

	public static LocalDate currentDate() {
		return LocalDate.now(clock);
	}

	public static LocalDateTime currentDateTime() {
		return LocalDateTime.now(clock);
	}

	public static ZonedDateTime currentZonedDateTime() {
		return ZonedDateTime.now(clock);
	}

	public static OffsetDateTime currentOffsetDateTime() {
		return OffsetDateTime.now(clock);
	}

	public static void setCurrentDate(LocalDate date) {
		setCurrentDate(date, ZoneId.systemDefault());
	}

	public static void setCurrentDate(LocalDate date, ZoneId zoneId) {
		Clock fixedClock = Clock.fixed(date.atStartOfDay(zoneId).toInstant(), zoneId);
		clock = fixedClock;
	}

	public static void useSystemDate() {
		clock = Clock.systemDefaultZone();
	}

}
