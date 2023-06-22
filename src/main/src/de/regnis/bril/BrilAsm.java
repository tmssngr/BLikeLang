package de.regnis.bril;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public class BrilAsm {

	// Fields =================================================================

	private final List<BrilCommand> commands = new ArrayList<>();

	// Setup ==================================================================

	protected BrilAsm() {
	}

	// Accessing ==============================================================

	public List<String> toLines() {
		final List<String> lines = new ArrayList<>();
		toLines(line -> lines.add(line));
		return lines;
	}

	public void toLines(Consumer<String> output) {
		for (BrilCommand command : commands) {
			command.appendTo(output);
		}
	}

	public BrilAsm transform(Function<List<BrilCommand>, List<BrilCommand>> transformation) {
		final List<BrilCommand> newCommands = transformation.apply(Collections.unmodifiableList(commands));
		commands.clear();
		commands.addAll(newCommands);
		return this;
	}

	public BrilAsm label(String label) {
		addCommand(new BrilCommand.Label(label));
		return this;
	}

	public BrilAsm iload(int dest, int src) {
		if (dest != src) {
			addCommand(new BrilCommand() {
				@Override
				public void appendTo(Consumer<String> output) {
					output.accept("ld r" + (dest + 1) + ", r" + (src + 1));
					output.accept("ld r" + dest + ", r" + src);
				}
			});
		}
		return this;
	}

	public BrilAsm iloadFromStack(int destRegister, int spRegister, int offset) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc r" + destRegister + ", rr" + spRegister);
				output.accept("incw r" + spRegister);
				output.accept("ldc r" + (destRegister + 1) + ", rr" + spRegister);
			}
		});
		return this;
	}

	public BrilAsm bloadFromStack(int destRegister, int spRegister, int offset) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc r" + destRegister + ", rr" + spRegister);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm istoreToStack(int sourceRegister, int spRegister, int offset) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
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
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("add r" + (dest + 1) + ", r" + (src + 1));
				output.accept("adc r" + dest + ", r" + src);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm iconst(int register, int value) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("ld r" + register + ", #" + highByte(value));
				output.accept("ld r" + (register + 1) + ", #" + lowByte(value));
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm ipush(int register) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("push r" + register);
				output.accept("push r" + (register + 1));
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm ipop(int register) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("pop r" + (register + 1));
				output.accept("pop r" + register);
			}
		});
		return this;
	}

	public BrilAsm call(String target) {
		addCommand(new BrilCommand.Call(target));
		return this;
	}

	public BrilAsm allocSpace(int byteCount) {
		if (byteCount > 0) {
			addCommand(new BrilCommand() {
				@Override
				public void appendTo(Consumer<String> output) {
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
			addCommand(new BrilCommand() {
				@Override
				public void appendTo(Consumer<String> output) {
					for (int i = 0; i < byteCount; i++) {
						output.accept("pop r0");
					}
				}
			});
		}
		return this;
	}

	public BrilAsm ret() {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("ret");
			}
		});
		return this;
	}

	public BrilAsm brIfElse(int register, String thenTarget, String elseTarget) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("or r" + register + ", r" + register);
			}
		});
		addCommand(new BrilCommand.Branch("z", elseTarget));
		addCommand(new BrilCommand.Jump(thenTarget));
		return this;
	}

	public BrilAsm brElse(int register, String elseTarget) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("or r" + register + ", r" + register);
			}
		});
		addCommand(new BrilCommand.Branch("z", elseTarget));
		return this;
	}

	public BrilAsm jump(String targetLabel) {
		addCommand(new BrilCommand.Jump(targetLabel));
		return this;
	}

	// Utils ==================================================================

	private void addCommand(@NotNull BrilCommand command) {
		commands.add(command);
	}

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
}
