package com.mf.auth.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateTimeUtils {

	/**
	 * Parse a string in format "yyyy-MM-dd HH:mm:ss: + x nanoseconds"
	 * to java LocalDateTime.
	 *
	 * @param datetime  datetime string
	 *
	 * @return LocalDateTime
	 */
	public static LocalDateTime fromString(String datetime) {
		var formatter = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd HH:mm:ss")
			.optionalStart()
			.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
			.optionalEnd()
			.toFormatter();

		return LocalDateTime.parse(datetime, formatter);
	}
}
