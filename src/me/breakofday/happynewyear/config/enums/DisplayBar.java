package me.breakofday.happynewyear.config.enums;

import me.breakofday.happynewyear.util.TimeRemaining;

import java.util.concurrent.TimeUnit;

public enum DisplayBar {

	FORCE {
		@Override
		public boolean isForced(final TimeRemaining timeRemaining) {
			return true;
		}
	},
	OPTION {
		@Override
		public boolean isForced(final TimeRemaining timeRemaining) {
			final long raw = TimeUnit.MILLISECONDS.toSeconds(timeRemaining.getRaw());
			return raw <= 60 && raw > 0;
		}
	};

	public abstract boolean isForced(final TimeRemaining timeRemaining);

}
