package de.regnis.b.out;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public
class StringStringOutput implements StringOutput {
	private final StringBuilder buffer = new StringBuilder();

	@Override
	public void print(@NotNull String s) {
		buffer.append(s);
	}

	@Override
	public void println() {
		print("\n");
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
}
