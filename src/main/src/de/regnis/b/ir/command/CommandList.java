package de.regnis.b.ir.command;

import de.regnis.b.ir.ControlFlowGraphPrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class CommandList {

	// Fields =================================================================

	private final List<Command> commands = new ArrayList<>();

	// Setup ==================================================================

	public CommandList() {
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return print(new StringStringOutput()).toString();
	}

	// Accessing ==============================================================

	public void add(@NotNull Command command) {
		commands.add(command);
	}

	@NotNull
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public void compact() {
		Utils.assertTrue(commands.size() >= 2);
		Utils.assertTrue(commands.get(0) instanceof Label);
		Utils.assertTrue(!(commands.get(commands.size() - 1) instanceof Label));

		boolean changed;
		do {
			changed = removeUselessJumpsToNextCommand();

			if (replaceLabels()) {
				changed = true;
			}

			if (removeUnusedLabels()) {
				changed = true;
			}
		}
		while (changed);
	}

	@NotNull
	public StringOutput print(@NotNull StringOutput output) {
		for (Command command : commands) {
			if (!(command instanceof Label)) {
				output.print(ControlFlowGraphPrinter.INDENTATION);
			}
			output.print(command.toString());
			output.println();
		}
		return output;
	}

	// Utils ==================================================================

	@NotNull
	private Map<String, String> findLabelsPointingToSameLocation() {
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

	private boolean replaceLabels() {
		final Map<String, String> labelToLabel = findLabelsPointingToSameLocation();

		return filterCommands(new Function<>() {
			@Nullable
			@Override
			public Command apply(Command command) {
				if (command instanceof JumpCommand jumpCommand) {
					final String newLabel = labelToLabel.get(jumpCommand.label());
					if (newLabel != null) {
						return new JumpCommand(newLabel);
					}
				}

				if (command instanceof CmpJump cmpJump) {
					String newTrueLabel = labelToLabel.get(cmpJump.trueLabel());
					String newFalseLabel = labelToLabel.get(cmpJump.falseLabel());
					if (newTrueLabel != null || newFalseLabel != null) {
						if (newTrueLabel == null) {
							newTrueLabel = cmpJump.trueLabel();
						}
						if (newFalseLabel == null) {
							newFalseLabel = cmpJump.falseLabel();
						}
						return new CmpJump(cmpJump.destRegister(), cmpJump.srcRegister(),
						                   cmpJump.trueCondition(), newTrueLabel,
						                   cmpJump.falseCondition(), newFalseLabel);
					}
				}

				if (command instanceof CmpCJump cmpJump) {
					String newTrueLabel = labelToLabel.get(cmpJump.trueLabel());
					String newFalseLabel = labelToLabel.get(cmpJump.falseLabel());
					if (newTrueLabel != null || newFalseLabel != null) {
						if (newTrueLabel == null) {
							newTrueLabel = cmpJump.trueLabel();
						}
						if (newFalseLabel == null) {
							newFalseLabel = cmpJump.falseLabel();
						}
						return new CmpCJump(cmpJump.register(), cmpJump.literal(),
						                    cmpJump.trueCondition(), newTrueLabel,
						                    cmpJump.falseCondition(), newFalseLabel);
					}
				}

				if (command instanceof Label label
						&& labelToLabel.containsKey(label.name())) {
					return null;
				}

				return command;
			}
		});
	}

	private boolean removeUselessJumpsToNextCommand() {
		final List<Command> newCommands = new ArrayList<>();

		for (int i = 0; i < commands.size(); i++) {
			final Command command = commands.get(i);
			if (i < commands.size() - 1
					&& command instanceof JumpCommand jumpCommand
					&& commands.get(i + 1) instanceof Label label
					&& label.name().equals(jumpCommand.label())) {
				continue;
			}

			newCommands.add(command);
		}

		return setCommands(newCommands);
	}

	private boolean removeUnusedLabels() {
		final Set<String> usedLabels = determineUsedLabels();

		return filterCommands(new Function<>() {
			@Nullable
			@Override
			public Command apply(Command command) {
				if (command instanceof Label label
						&& !usedLabels.contains(label.name())) {
					return null;
				}

				return command;
			}
		});
	}

	private Set<String> determineUsedLabels() {
		final Set<String> usedLabels = new HashSet<>();
		for (Command command : commands) {
			if (command instanceof JumpCommand jump) {
				usedLabels.add(jump.label());
			}
			else if (command instanceof CmpJump jump) {
				usedLabels.add(jump.trueLabel());
				usedLabels.add(jump.falseLabel());
			}
			else if (command instanceof CmpCJump jump) {
				usedLabels.add(jump.trueLabel());
				usedLabels.add(jump.falseLabel());
			}
		}
		return usedLabels;
	}

	private boolean filterCommands(Function<Command, Command> function) {
		final List<Command> newCommands = new ArrayList<>();

		// start at second command, because the first (method label) must be kept
		for (int i = 0; i < commands.size(); i++) {
			final Command command = commands.get(i);
			final Command newCommand = i > 0 ? function.apply(command) : command;
			if (newCommand != null) {
				newCommands.add(newCommand);
			}
		}
		return setCommands(newCommands);
	}

	private boolean setCommands(List<Command> newCommands) {
		if (newCommands.equals(commands)) {
			return false;
		}

		commands.clear();
		commands.addAll(newCommands);
		return true;
	}
}
