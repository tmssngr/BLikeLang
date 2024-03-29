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
			final List<Command> prevCommands = commands;
			commands = fixLabels(commands);

			if (commands.equals(prevCommands)) {
				break;
			}
		}

		commands = filterTempLd(commands);
		commands = replaceTempWithFinalCommands(commands);
		commands = filterAndOrXor(commands);
		commands = filterCpJump(commands);
		commands = filterCondJumpJump(commands);

		while (true) {
			final List<Command> prevCommands = commands;
			commands = filterCondJumpJumpLabel(commands);
			commands = fixLabels(commands);

			if (commands.equals(prevCommands)) {
				break;
			}
		}

		final CommandList commandList = new CommandList();
		commands.forEach(commandList::add);
		return commandList;
	}

	// Utils ==================================================================

	@NotNull
	private static List<Command> fixLabels(List<Command> commands) {
		commands = removeUselessJumpsToNextCommand(commands);
		commands = replaceLabels(commands);
		commands = replaceLabelJump(commands);
		commands = replaceJumpJump(commands);
		commands = removeUnusedLabels(commands);
		return commands;
	}

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
		return replaceLabels(labelToLabel, commands);
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

	private static List<Command> replaceLabelJump(List<Command> commands) {
		final Map<String, String> labelToLabel = findLabelsFollowedByJump(commands);
		return replaceLabels(labelToLabel, commands);
	}

	@NotNull
	private static Map<String, String> findLabelsFollowedByJump(List<Command> commands) {
		final Map<String, String> labelToLabel = new HashMap<>();
		filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				if (command1 instanceof Label label
						&& command0 instanceof JumpCommand jumpCommand
						&& jumpCommand.condition() == null) {
					labelToLabel.put(label.name(), jumpCommand.label());
				}
			}
		});
		return labelToLabel;
	}

	@NotNull
	private static List<Command> replaceJumpJump(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				if (command1 instanceof JumpCommand jump1
						&& jump1.condition() == null
						&& !(command0 instanceof Label)) {
					stack.drop();
				}
			}
		});
	}

	@NotNull
	private static List<Command> replaceLabels(Map<String, String> labelToLabel, List<Command> commands) {
		if (labelToLabel.isEmpty()) {
			return commands;
		}
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
	private static List<Command> filterAndOrXor(List<Command> commands) {
		return filterCommands(commands, new SingleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command, @NotNull Stack stack) {
				if (command instanceof ArithmeticLiteral ac) {
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

	private static boolean doesNotChange(Command command, int register) {
		if (command instanceof CallCommand
				|| command instanceof JumpCommand
				|| command instanceof Label) {
			return false;
		}
		if (command instanceof Arithmetic a) {
			return a.srcRegister() != register;
		}
		if (command instanceof ArithmeticLiteral a) {
			return a.register() != register;
		}
		if (command instanceof Ld ld) {
			return ld.targetRegister() != register;
		}
		if (command instanceof LdLiteral ld) {
			return ld.register() != register;
		}
		if (command instanceof LdFromMem lfm) {
			return lfm.destRegister() != register;
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
	private static List<Command> filterTempLd(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				// Example:
				// ldw r8, r0 (ld1)
				// ldw r0, r8 (ld0)
				if (command1 instanceof TempLd ld1
						&& command0 instanceof TempLd ld0
						&& ld1.srcRegister() == ld0.destRegister()
						&& ld0.srcRegister() == ld1.destRegister()) {
					stack.drop();
				}
			}
		});
	}

	private static List<Command> replaceTempWithFinalCommands(List<Command> commands) {
		return filterCommands(commands, new SingleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command, @NotNull Stack stack) {
				if (command instanceof TempLd temp) {
					stack.drop();
					stack.add(new Ld(temp.destRegister(), temp.srcRegister()));
					stack.add(new Ld(temp.destRegister() + 1, temp.srcRegister() + 1));
				}
				else if (command instanceof TempArithmetic temp) {
					stack.drop();
					stack.add(new Arithmetic(temp.op(), temp.destRegister() + 1, temp.srcRegister() + 1));
					stack.add(new Arithmetic(getMsbOp(temp.op()), temp.destRegister(), temp.srcRegister()));
				}
				else if (command instanceof TempLdLiteral temp) {
					stack.drop();
					stack.add(new LdLiteral(temp.register(), temp.literal() >> 8));
					stack.add(new LdLiteral(temp.register() + 1, temp.literal() & 0xFF));
				}
				else if (command instanceof TempArithmeticLiteral temp) {
					stack.drop();
					if (temp.op() == ArithmeticOp.add && temp.literal() == 1) {
						stack.add(new RegisterCommand(RegisterCommand.Op.incw, temp.register()));
					}
					else if (temp.op() == ArithmeticOp.sub && temp.literal() == 1) {
						stack.add(new RegisterCommand(RegisterCommand.Op.decw, temp.register()));
					}
					else {
						stack.add(new ArithmeticLiteral(temp.op(), temp.register() + 1, temp.literal() & 0xFF));
						stack.add(new ArithmeticLiteral(getMsbOp(temp.op()), temp.register(), temp.literal() >> 8));
					}
				}
			}

			@NotNull
			private ArithmeticOp getMsbOp(@NotNull ArithmeticOp op) {
				ArithmeticOp msbOp = op;
				if (msbOp == ArithmeticOp.add) {
					msbOp = ArithmeticOp.adc;
				}
				else if (msbOp == ArithmeticOp.sub) {
					msbOp = ArithmeticOp.sbc;
				}
				return msbOp;
			}
		});
	}

	private static List<Command> filterCpJump(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				if (command1 instanceof ArithmeticLiteral cp
						&& cp.op() == ArithmeticOp.cp
						&& command0 instanceof JumpCommand j) {
					if (cp.literal() == 0) {
						// all is >= 0
						if (j.condition() == JumpCondition.uge) {
							stack.drop();
							stack.drop();
							stack.add(new JumpCommand(j.label()));
						}
						// nothing is < 0
						else if (j.condition() == JumpCondition.ult) {
							stack.drop();
							stack.drop();
						}
					}
					if (cp.literal() == 0xFF) {
						// nothing is > 255
						if (j.condition() == JumpCondition.ugt) {
							stack.drop();
							stack.drop();
						}
						// all is <= 255
						else if (j.condition() == JumpCondition.ule) {
							stack.drop();
							stack.drop();
							stack.add(new JumpCommand(j.label()));
						}
					}
				}
			}
		});
	}

	private static List<Command> filterCondJumpJump(List<Command> commands) {
		return filterCommands(commands, new DualCommandVisitor() {
			@Override
			public void visit(@NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				// replace
				//   jump nz, foo
				//   jump foo
				// with
				//   jump foo
				if (command1 instanceof JumpCommand j1
						&& j1.condition() != null
						&& command0 instanceof JumpCommand j0
						&& j0.condition() == null
						&& j1.label().equals(j0.label())) {
					stack.drop();
					stack.drop();
					stack.add(j0);
				}
			}
		});
	}

	private static List<Command> filterCondJumpJumpLabel(List<Command> commands) {
		return filterCommands(commands, new TripleCommandVisitor() {
			@Override
			public void visit(@NotNull Command command2, @NotNull Command command1, @NotNull Command command0, @NotNull Stack stack) {
				// replace
				//   jump nz, foo
				//   jump bar
				// foo:
				// with
				//   jump z, bar
				// foo:
				if (command2 instanceof JumpCommand j2
						&& j2.condition() != null
						&& command1 instanceof JumpCommand j1
						&& j1.condition() == null
						&& command0 instanceof Label label
						&& j2.label().equals(label.name())) {
					final var inverseOp = switch (j2.condition()) {
						case lt -> JumpCondition.ge;
						case le -> JumpCondition.gt;
						case ge -> JumpCondition.lt;
						case gt -> JumpCondition.le;
						case ult -> JumpCondition.uge;
						case ule -> JumpCondition.ugt;
						case uge -> JumpCondition.ult;
						case ugt -> JumpCondition.ule;
						case z -> JumpCondition.nz;
						case nz -> JumpCondition.z;
					};
					stack.drop();
					stack.drop();
					stack.drop();
					stack.add(new JumpCommand(inverseOp, j1.label()));
					stack.add(label);
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
