package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

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

	public BrilAsm iload(int dest, int src) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("ld r" + (dest + 1) + ", r" + (src + 1));
				output.accept("ld r" + dest + ", r" + src);
			}
		});
		return this;
	}

	public BrilAsm iloadFromStack(int destRegister, int spRegister, int offset) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc r" + destRegister + ", rr" + spRegister);
				output.accept("incw r" + spRegister);
				output.accept("ldc r" + (destRegister + 1) + ", rr" + spRegister);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm istoreToStack(int sourceRegister, int spRegister, int offset) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc rr" + spRegister + ", r" + sourceRegister);
				output.accept("incw r" + spRegister);
				output.accept("ldc rr" + spRegister + ", r" + (sourceRegister + 1));
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm iadd(int dest, int src) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("add r" + (dest + 1) + ", r" + (src + 1));
				output.accept("adc r" + dest + ", r" + src);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm iconst(int register, int value) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("ld r" + register + ", #" + highByte(value));
				output.accept("ld r" + (register + 1) + ", #" + lowByte(value));
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm ipush(int register) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("push r" + register);
				output.accept("push r" + (register + 1));
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm ipop(int register) {
		commands.add(new Command() {
			@Override
			protected void appendTo(Consumer<String> output) {
				output.accept("pop r" + (register + 1));
				output.accept("pop r" + register);
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
		if (byteCount > 0) {
			commands.add(new Command() {
				@Override
				protected void appendTo(Consumer<String> output) {
					for (int i = 0; i < byteCount; i++) {
						output.accept("push r0");
					}
				}
			});
		}
		return this;
	}

	public BrilAsm freeSpace(int byteCount) {
		if (byteCount > 0) {
			commands.add(new Command() {
				@Override
				protected void appendTo(Consumer<String> output) {
					for (int i = 0; i < byteCount; i++) {
						output.accept("pop r0");
					}
				}
			});
		}
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
