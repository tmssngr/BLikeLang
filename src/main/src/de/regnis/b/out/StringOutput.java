package de.regnis.b.out;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface StringOutput {
	void print(@NotNull String s);

	void println();

	StringOutput out = new StringOutput() {
		@Override
		public void print(@NotNull String s) {
			System.out.print(s);
		}

		@Override
		public void println() {
			System.out.println();
		}
	};
}
