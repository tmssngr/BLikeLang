package de.regnis.b.ir.command;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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

		return newCommands;
	}

	private static List<Command> replaceLabels(List<Command> commands) {
		final Map<String, String> labelToLabel = findLabelsPointingToSameLocation(commands);

		return filterCommands(commands, new Function<>() {
			@Nullable
			@Override
			public Command apply(Command command) {
				if (command instanceof JumpCommand jumpCommand) {
					final String newLabel = labelToLabel.get(jumpCommand.label());
					if (newLabel != null) {
						return new JumpCommand(jumpCommand.condition(), newLabel);
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

		return filterCommands(commands, new Function<>() {
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

	private static Set<String> determineUsedLabels(List<Command> commands) {
		final Set<String> usedLabels = new HashSet<>();
		for (Command command : commands) {
			if (command instanceof JumpCommand jump) {
				usedLabels.add(jump.label());
			}
		}
		return usedLabels;
	}

	private static List<Command> filterCommands(List<Command> commands, Function<Command, Command> function) {
		final List<Command> newCommands = new ArrayList<>();

		// start at second command, because the first (method label) must be kept
		for (int i = 0; i < commands.size(); i++) {
			final Command command = commands.get(i);
			final Command newCommand = i > 0 ? function.apply(command) : command;
			if (newCommand != null) {
				newCommands.add(newCommand);
			}
		}
		return newCommands;
	}
}
