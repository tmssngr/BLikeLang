package de.regnis.b;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class Messages {
	@NotNull
	public static String errorBreakStatementNotInWhile(int line, int column) {
		return line + ":" + column + ": The break statement only is allowed inside a while loop.";
	}
}
