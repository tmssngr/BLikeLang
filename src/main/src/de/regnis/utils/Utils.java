package de.regnis.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
public class Utils {

	@NotNull
	public static <O> O notNull(@Nullable O value) {
		Objects.requireNonNull(value);
		return value;
	}

	@NotNull
	public static <O> O notNull(@Nullable O value, @NotNull O defaultValue) {
		return value != null ? value : defaultValue;
	}
}
