package de.regnis.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public class Utils {

	public static <O> O notNull(@Nullable O value, @NotNull O defaultValue) {
		return value != null ? value : defaultValue;
	}
}
