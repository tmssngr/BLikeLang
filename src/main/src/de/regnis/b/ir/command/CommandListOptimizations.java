package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class CommandListOptimizations {

	// Static =================================================================

	@NotNull
	public static CommandList optimize(@NotNull CommandList input) {
		List<Command> commands = input.getCommands();
		Utils.assertTrue(commands.size() >= 2);
		Utils.assertTrue(commands.get(0) instanceof Label);
		Utils.assertTrue(!(commands.get(commands.size() - 1) instanceof Label));

		while (true) {
			List<Command> processedCommands = removeUselessJumpsToNextCommand(commands);
			processedCommands = replaceLabels(processedCommands);
			processedCommands = removeUnusedLabels(processedCommands);
			processedCommands = filterAddSub(processedCommands);
			processedCommands = filterAndOrXor(processedCommands);
			processedCommands = filterLd3(processedCommands);
			processedCommands = filterLd2(processedCommands);

			if (processedCommands.equals(commands)) {
				final CommandList commandList = new CommandList();
				processedCommands.forEach(commandList::add);
				return commandList;
			}

			commands = processedCommands;
		}
	}

	// Utils ==================================================================

	private static List<Command> removeUselessJumpsToNextCommand(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				if (command1 instanceof JumpCommand jumpCommand
						&& command0 instanceof Label label
						&& label.name().equals(jumpCommand.label())) {
					stack.drop();
					stack.drop();
					stack.add(command0);
				}
			}
		});
	}

	private static List<Command> replaceLabels(List<Command> commands) {
		final Map<String, String> labelToLabel = findLabelsPointingToSameLocation(commands);

		return filterCommands(commands, new SingleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command, @NotNull Stack stack) {
				if (command instanceof JumpCommand jumpCommand) {
					final String newLabel = labelToLabel.get(jumpCommand.label());
					if (newLabel != null) {
						stack.drop();
						stack.add(new JumpCommand(jumpCommand.condition(), newLabel));
					}
					return;
				}

				if (command instanceof Label label
						&& labelToLabel.containsKey(label.name())) {
					stack.drop();
				}
			}
		});
	}

	@NotNull
	private static Map<String, String> findLabelsPointingToSameLocation(List<Command> commands) {
		final Map<String, String> labelToLabel = new HashMap<>();
		final List<String> consecutiveLabels = new ArrayList<>();

		for (int i = 1; i < commands.size(); i++) {
			final Command command = commands.get(i);
			if (command instanceof Label label) {
				consecutiveLabels.add(label.name());
				continue;
			}

			if (consecutiveLabels.size() < 2) {
				consecutiveLabels.clear();
				continue;
			}

			final String lastLabel = consecutiveLabels.remove(consecutiveLabels.size() - 1);
			for (String label : consecutiveLabels) {
				Utils.assertTrue(labelToLabel.put(label, lastLabel) == null);
			}
			consecutiveLabels.clear();
		}
		return labelToLabel;
	}

	private static List<Command> removeUnusedLabels(List<Command> commands) {
		final Set<String> usedLabels = determineUsedLabels(commands);

		return filterCommands(commands, new SingleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command, @NotNull Stack stack) {
				if (command instanceof Label label
						&& !usedLabels.contains(label.name())) {
					stack.drop();
				}
			}
		});
	}

	private static Set<String> determineUsedLabels(List<Command> commands) {
		final Set<String> usedLabels = new HashSet<>();
		for (Command command : commands) {
			if (command instanceof JumpCommand jump) {
				usedLabels.add(jump.label());
			}
		}
		return usedLabels;
	}

	@NotNull
	private static List<Command> filterAddSub(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				if (command1 instanceof ArithmeticC ac1
						&& command0 instanceof ArithmeticC ac0
						&& ac1.register() == ac0.register() + 1
						&& ac1.literal() == 1
						&& ac0.literal() == 0) {
					if (ac1.op() == ArithmeticOp.add
							&& ac0.op() == ArithmeticOp.adc) {
						stack.drop();
						stack.drop();
						stack.add(new RegisterCommand(RegisterCommand.Op.incw, ac0.register()));
						return;
					}
					if (ac1.op() == ArithmeticOp.sub
							&& ac0.op() == ArithmeticOp.sbc) {
						stack.drop();
						stack.drop();
						stack.add(new RegisterCommand(RegisterCommand.Op.decw, ac0.register()));
					}
				}
			}
		});
	}

	@NotNull
	private static List<Command> filterAndOrXor(List<Command> commands) {
		return filterCommands(commands, new SingleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command, @NotNull Stack stack) {
				if (command instanceof ArithmeticC ac) {
					if (ac.op() == ArithmeticOp.and) {
						if (ac.literal() == 0xFF) {
							stack.drop();
							return;
						}

						if (ac.literal() == 0) {
							stack.drop();
							stack.add(new LdLiteral(ac.register(), 0));
							return;
						}
					}
					if (ac.op() == ArithmeticOp.or) {
						if (ac.literal() == 0) {
							stack.drop();
							return;
						}

						if (ac.literal() == 0xFF) {
							stack.drop();
							stack.add(new LdLiteral(ac.register(), 0xFF));
							return;
						}
					}
					if (ac.op() == ArithmeticOp.xor) {
						if (ac.literal() == 0) {
							stack.drop();
							return;
						}

						if (ac.literal() == 0xFF) {
							stack.drop();
							stack.add(new RegisterCommand(RegisterCommand.Op.com, ac.register()));
							return;
						}
					}
				}
			}
		});
	}

	@NotNull
	private static List<Command> filterLd3(List<Command> commands) {
		return filterCommands(commands, new TripleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command2, @NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				// Example:
				// ld r8, r0 (ld2)
				// ld r9, r1
				// ld r0, r8 (ld0)
				// ld r1, r9
				if (command2 instanceof Ld ld2
						&& command0 instanceof Ld ld0
						&& ld2.sourceRegister() == ld0.targetRegister()
						&& ld0.sourceRegister() == ld2.targetRegister()) {
					if (!doesNotChange(command1, ld0.sourceRegister())) {
						return;
					}
					stack.drop();
				}
			}
		});
	}

	private static boolean doesNotChange(Command command, int register) {
		if (command instanceof CallCommand
				|| command instanceof JumpCommand
				|| command instanceof Label) {
			return false;
		}
		if (command instanceof Arithmetic a) {
			return a.srcRegister() != register;
		}
		if (command instanceof ArithmeticC a) {
			return a.register() != register;
		}
		if (command instanceof Ld ld) {
			return ld.targetRegister() != register;
		}
		if (command instanceof LdLiteral ld) {
			return ld.register() != register;
		}
		if (command instanceof LdFromMem lfm) {
			return lfm.targetRegister() != register;
		}
		if (command instanceof RegisterCommand r) {
			if (r.op() == RegisterCommand.Op.push) {
				return true;
			}
			//noinspection SimplifiableIfStatement
			if ((r.op() == RegisterCommand.Op.decw
					|| r.op() == RegisterCommand.Op.incw)
					&& (r.register() & 0xFE) == (register & 0xFE)) {
				return true;
			}
			return r.register() != register;
		}
		throw new IllegalStateException();
	}

	@NotNull
	private static List<Command> filterLd2(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				// Example:
				// ld r8, r0 (ld1)
				// ld r0, r8 (ld0)
				// ld r1, r9
				if (command1 instanceof Ld ld1
						&& command0 instanceof Ld ld0
						&& ld1.sourceRegister() == ld0.targetRegister()
						&& ld0.sourceRegister() == ld1.targetRegister()) {
					stack.drop();
				}
			}
		});
	}

	private static List<Command> filterCommands(List<Command> commands, SingleCommandVisitor filter) {
		final List<Command> newCommands = new ArrayList<>();
		final Stack stack = new StackImpl(newCommands);
		for (Command command : commands) {
			stack.add(command);
			if (newCommands.size() > 1) {
				filter.visit(command, stack);
			}
		}

		return newCommands;
	}

	private static List<Command> filterCommands(List<Command> commands, DualCommandVisitor filter) {
		final List<Command> newCommands = new ArrayList<>();
		final Stack stack = new StackImpl(newCommands);
		for (Command command : commands) {
			stack.add(command);
			if (newCommands.size() > 1) {
				final Command prevCommand = newCommands.get(newCommands.size() - 2);
				filter.visit(prevCommand, command, stack);
			}
		}

		return newCommands;
	}

	private static List<Command> filterCommands(List<Command> commands, TripleCommandVisitor filter) {
		final List<Command> newCommands = new ArrayList<>();
		final Stack stack = new StackImpl(newCommands);
		for (Command command : commands) {
			stack.add(command);
			if (newCommands.size() > 2) {
				final Command prevCommand2 = newCommands.get(newCommands.size() - 3);
				final Command prevCommand = newCommands.get(newCommands.size() - 2);
				filter.visit(prevCommand2, prevCommand, command, stack);
			}
		}

		return newCommands;
	}

	// Inner Classes ==========================================================

	private interface SingleCommandVisitor {

		void visit(@NotNull Command command, @NotNull Stack stack);
	}

	private interface DualCommandVisitor {

		void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack);
	}

	private interface TripleCommandVisitor {

		void visit(@NotNull Command command2, @NotNull Command command1, @NotNull Command command0, @NotNull Stack stack);
	}

	private interface Stack {
		void add(@NotNull Command command);

		void drop();
	}

	private record StackImpl(List<Command> commands) implements Stack {

		@Override
		public void add(@NotNull Command command) {
			commands.add(command);
		}

		@Override
		public void drop() {
			commands.remove(commands.size() - 1);
		}
	}
}
