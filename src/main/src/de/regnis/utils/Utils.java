package de.regnis.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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

	public static void appendCommaSeparated(Iterable<String> collection, StringBuilder buffer) {
		appendCommaSeparated(collection, s -> s, buffer);
	}

	public static <O> void appendCommaSeparated(Iterable<? extends O> collection, Function<O, String> function, StringBuilder buffer) {
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
	}
}
