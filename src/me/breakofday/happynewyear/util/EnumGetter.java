package me.breakofday.happynewyear.util;

public class EnumGetter {

	private EnumGetter() {}

	public static <E extends Enum<E>> E getOrDefault(final Class<E> enumClass, final String name, final E def) {
		try {
			return Enum.valueOf(enumClass, name);
		} catch (IllegalArgumentException e) {
			return def;
		}
	}

}
