package de.regnis.bril;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public interface BrilCommand {

	// Constants ==============================================================

	BrilCommand RET = new BrilCommand() {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ret");
		}
	};

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
	record Branch(String condition, String target) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("jp " + condition + ", " + target);
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

	@SuppressWarnings("InnerClassOfInterface")
	record Load16(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
			output.accept("ld r" + (dest + 1) + ", r" + (src + 1));
		}
	}

	@SuppressWarnings("InnerClassOfInterface")
	record Load8(int dest, int src) implements BrilCommand {
		@Override
		public void appendTo(Consumer<String> output) {
			output.accept("ld r" + dest + ", r" + src);
		}
	}
}
