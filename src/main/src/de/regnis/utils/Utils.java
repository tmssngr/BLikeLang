package de.regnis.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class Utils {

	@NotNull
	public static <O> O notNull(@Nullable O value) {
		Objects.requireNonNull(value);
		return value;
	}

	@NotNull
	public static <O> O notNull(@Nullable O value, @NotNull O defaultValue) {
		return value != null ? value : defaultValue;
	}

	@Nullable
	public static String parseString(@NotNull String text, char surroundingChar) {
		final int length = text.length();
		if (length < 2 || text.charAt(0) != surroundingChar || text.charAt(length - 1) != surroundingChar) {
			return null;
		}

		final StringBuilder buffer = new StringBuilder();
		boolean wasBackslash = false;
		for (int i = 1; i < length - 1; i++) {
			char chr = text.charAt(i);
			if (wasBackslash) {
				chr = switch (chr) {
					case '\'' -> chr;
					case '"' -> chr;
					case '\\' -> chr;
					case 't' ->  '\t';
					case 'n' -> '\n';
					case 'r' -> '\r';
					default -> 0;
				};
				if (chr == 0) {
					break;
				}

				wasBackslash = false;
			}
			else if (chr == '\\') {
				wasBackslash = true;
				continue;
			}

			buffer.append(chr);
		}
		return wasBackslash ? null : buffer.toString();
	}

	public static StringBuilder appendCommaSeparated(Iterable<String> collection, StringBuilder buffer) {
		return appendCommaSeparated(collection, s -> s, buffer);
	}

	public static <O> StringBuilder appendCommaSeparated(Iterable<? extends O> collection, Function<O, String> function, StringBuilder buffer) {
		boolean isFirst = true;
		for (O obj : collection) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				buffer.append(", ");
			}
			buffer.append(function.apply(obj));
		}

		return buffer;
	}

	@NotNull
	public static <O extends Comparable<O>> List<O> toSortedList(Collection<O> strings) {
		final List<O> sorted = new ArrayList<>(strings);
		sorted.sort(Comparator.naturalOrder());
		return sorted;
	}

	public static <K extends Comparable<K>, V> void print(Map<K, V> map, String prefix, String padding, String suffix) {
		for (K key : toSortedList(map.keySet())) {
			System.out.print(prefix);
			System.out.print(key);
			System.out.print(padding);
			System.out.print(map.get(key));
			System.out.print(suffix);
		}
	}

	public static void assertTrue(boolean value) {
		if (!value) {
			throw new IllegalStateException();
		}
	}
}
