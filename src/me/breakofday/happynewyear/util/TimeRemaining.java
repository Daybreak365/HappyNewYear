package me.breakofday.happynewyear.util;

import java.util.concurrent.TimeUnit;

public class TimeRemaining {

	private final long raw, days, hours, minutes, seconds, millis;

	public TimeRemaining(final long inMillis) {
		this.raw = inMillis;
		this.days = TimeUnit.MILLISECONDS.toDays(inMillis);
		this.hours = TimeUnit.MILLISECONDS.toHours(inMillis) % 24;
		this.minutes = TimeUnit.MILLISECONDS.toMinutes(inMillis) % 60;
		this.seconds = TimeUnit.MILLISECONDS.toSeconds(inMillis) % 60;
		this.millis = inMillis % 1000;
	}

	public long getRaw() {
		return raw;
	}

	public long getDays() {
		return days;
	}

	public long getHours() {
		return hours;
	}

	public long getMinutes() {
		return minutes;
	}

	public long getSeconds() {
		return seconds;
	}

	public long getMillis() {
		return millis;
	}
}
