package de.regnis.bril;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public interface BrilCommand {

	// Accessing ==============================================================

	void appendTo(Consumer<String> output);

	// Inner Classes ==========================================================

	@SuppressWarnings("InnerClassOfInterface")
	record Label(String label) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept(label + ":");
		}
	}

	@SuppressWarnings("InnerClassOfInterface")
	record Branch(String conditon, String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + conditon + ", " + target);
		}
	}

	@SuppressWarnings("InnerClassOfInterface")
	record Jump(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + target);
		}
	}

	@SuppressWarnings("InnerClassOfInterface")
	record Call(String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("call " + target);
		}
	}
}
