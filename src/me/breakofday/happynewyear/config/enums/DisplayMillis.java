package me.breakofday.happynewyear.config.enums;

import me.breakofday.happynewyear.util.TimeRemaining;

import java.util.concurrent.TimeUnit;

public enum DisplayMillis {

	ALWAYS {
		@Override
		public boolean display(final TimeRemaining timeRemaining) {
			return true;
		}
	},
	NEVER {
		@Override
		public boolean display(final TimeRemaining timeRemaining) {
			return false;
		}
	},
	FINALE {
		@Override
		public boolean display(final TimeRemaining timeRemaining) {
			return TimeUnit.MILLISECONDS.toSeconds(timeRemaining.getRaw()) <= 60;
		}
	};

	public abstract boolean display(final TimeRemaining timeRemaining);

}
