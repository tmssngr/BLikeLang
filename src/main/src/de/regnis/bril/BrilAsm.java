package de.regnis.bril;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public final class BrilAsm {

	// Fields =================================================================

	private final List<Command> commands = new ArrayList<>();

	// Setup ==================================================================

	public BrilAsm() {
	}

	// Accessing ==============================================================

	public List<String> toLines() {
		final List<String> lines = new ArrayList<>();
		toLines(line -> lines.add(line));
		return lines;
	}

	public void toLines(Consumer<String> output) {
		for (Command command : commands) {
			command.appendTo(output);
		}
	}

	public BrilAsm label(String label) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept(label + ":");
			}
		});
		return this;
	}


	public BrilAsm iloadX(int offset) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				iload(0, offset, output);
			}
		});
		return this;
	}

	public BrilAsm iloadY(int offset) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				iload(2, offset, output);
			}
		});
		return this;
	}

	public BrilAsm istoreX(int offset) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc rr14, r" + offset);
				output.accept("incw r14");
				output.accept("ldc rr14, r" + (offset + 1));
			}
		});
		return this;
	}

	public BrilAsm iaddXY() {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("add r1, r3");
				output.accept("adc r0, r2");
			}
		});
		return this;
	}

	public BrilAsm iconstX(int value) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("ld r1, #" + lowByte(value));
				output.accept("ld r0, #" + highByte(value));
			}
		});
		return this;
	}

	public BrilAsm ipushX() {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("push r0");
				output.accept("push r1");
			}
		});
		return this;
	}

	public BrilAsm ipop() {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("pop r2");
				output.accept("pop r2");
			}
		});
		return this;
	}

	public BrilAsm call(String name) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("call " + name);
			}
		});
		return this;
	}

	public BrilAsm allocSpace(int byteCount) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				for (int i = 0; i < byteCount; i++) {
					output.accept("push r0");
				}
			}
		});
		return this;
	}

	public BrilAsm freeSpace(int byteCount) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				for (int i = 0; i < byteCount; i++) {
					output.accept("pop r0");
				}
			}
		});
		return this;
	}

	public BrilAsm ret() {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("ret");
			}
		});
		return this;
	}

	// Utils ==================================================================

	private BrilAsm ldFramePointer(int offset, Consumer<String> output) {
		output.accept("ld r14, %FE");
		output.accept("ld r15, %FF");
		output.accept("add r15, #" + lowByte(offset));
		output.accept("adc r14, #" + highByte(offset));
		return this;
	}

	private BrilAsm iload(int register, int offset, Consumer<String> output) {
		ldFramePointer(offset, output);
		output.accept("ldc r" + register + ", rr14");
		output.accept("incw r14");
		output.accept("ldc r" + (register + 1) + ", rr14");
		return this;
	}

	private static int highByte(int offset) {
		return offset >> 8;
	}

	private static int lowByte(int offset) {
		return offset & 0xFF;
	}

	// Inner Classes ==========================================================

	public abstract static class Command {
		protected abstract void appendTo(Consumer<String> output);
	}
}
