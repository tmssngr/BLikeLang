package de.regnis.bril;

import de.regnis.utils.Utils;
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

	private int labelCounter;

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
			output.accept("\n");
		}
	}

	public BrilAsm simplify(Function<List<BrilCommand>, List<BrilCommand>> transformation) {
		boolean changed;
		do {
			final List<BrilCommand> newCommands = transformation.apply(Collections.unmodifiableList(commands));
			changed = !commands.equals(newCommands);
			commands.clear();
			commands.addAll(newCommands);
		}
		while (changed);
		return this;
	}

	public BrilAsm label(String label) {
		addCommand(new BrilCommand.Label(label));
		return this;
	}

	public BrilAsm iload(int dest, int src) {
		if (dest != src) {
			addCommand(new BrilCommand.Load16(dest, src));
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

	public BrilAsm bload(int dest, int src) {
		if (dest != src) {
			addCommand(new BrilCommand.Load8(dest, src));
		}
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
	public BrilAsm bstoreToStack(int sourceRegister, int spRegister, int offset) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				ldFramePointer(offset, output);
				output.accept("ldc rr" + spRegister + ", r" + sourceRegister);
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
	public BrilAsm isub(int dest, int src) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("sub r" + (dest + 1) + ", r" + (src + 1));
				output.accept("sbc r" + dest + ", r" + src);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm imul(int dest, int src) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				Utils.todo();
				output.accept("MUL r" + (dest + 1) + ", r" + (src + 1));
				output.accept("MUL r" + dest + ", r" + src);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm idiv(int dest, int src) {
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				Utils.todo();
				output.accept("DIV r" + (dest + 1) + ", r" + (src + 1));
				output.accept("DIV r" + dest + ", r" + src);
			}
		});
		return this;
	}

	@NotNull
	public BrilAsm ilt(int dest, int left, int right) {
		final String labelTrue = "comparison_" + labelCounter++;
		final String labelNext = "comparison_" + labelCounter++;
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("ld r" + dest + ", #0");
				output.accept("cp r" + left + ", r" + right);
			}
		});
		addCommand(new BrilCommand.Branch("lt", labelTrue));
		addCommand(new BrilCommand.Branch("nz", labelNext));
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("cp r" + (left + 1) + ", r" + (right + 1));
			}
		});
		addCommand(new BrilCommand.Branch("uge", labelNext));
		addCommand(new BrilCommand.Label(labelTrue));
		addCommand(new BrilCommand() {
			@Override
			public void appendTo(Consumer<String> output) {
				output.accept("dec r" + dest);
			}
		});
		addCommand(new BrilCommand.Label(labelNext));
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
		addCommand(BrilCommand.RET);
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
