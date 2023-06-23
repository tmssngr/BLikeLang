package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class BrilAsmTransformations {

	// Static =================================================================

	public static Function<List<BrilCommand>, List<BrilCommand>> transform() {
		return new Function<>() {
			@Override
			public List<BrilCommand> apply(List<BrilCommand> prevCommands) {
				final List<BrilCommand> commands = new ArrayList<>(prevCommands);
				fixJumpToNextLabel(commands);
				fixObsoleteLabels(commands);
				fixCallRet(commands);
				fixBiDiLoad(commands);
				return commands;
			}
		};
	}

	// Utils ==================================================================

	private static void fixJumpToNextLabel(List<BrilCommand> commands) {
		new DualIterator(commands) {
			@Override
			protected void handle(BrilCommand command1, BrilCommand command2) {
				if (command1 instanceof BrilCommand.Jump jump
						&& command2 instanceof BrilCommand.Label label
						&& jump.target().equals(label.label())) {
					remove();
				}
				else if (command1 instanceof BrilCommand.Branch branch
						&& command2 instanceof BrilCommand.Label label
						&& branch.target().equals(label.label())) {
					remove();
				}
			}
		}.iterator();
	}

	private static void fixCallRet(List<BrilCommand> commands) {
		new DualIterator(commands) {
			@Override
			protected void handle(BrilCommand command1, BrilCommand command2) {
				if (command1 instanceof BrilCommand.Call call
						&& command2 == BrilCommand.RET) {
					remove();
					replace(new BrilCommand.Jump(call.target()));
				}
			}
		}.iterator();
	}

	private static void fixBiDiLoad(List<BrilCommand> commands) {
		new DualIterator(commands) {
			@Override
			protected void handle(BrilCommand command1, BrilCommand command2) {
				if (command1 instanceof BrilCommand.Load16 load1
						&& command2 instanceof BrilCommand.Load16 load2
						&& load1.src() == load2.dest()
						&& load1.dest() == load2.src()) {
					removeNext();
				}
			}
		}.iterator();
	}

	private static void fixObsoleteLabels(List<BrilCommand> commands) {
		final Map<String, String> obsoleteToNewLabels = getObsoleteToNewLabels(commands);

		new SingleIterator(commands) {
			@Override
			protected void handle(BrilCommand command) {
				if (command instanceof BrilCommand.Label label) {
					if (obsoleteToNewLabels.containsKey(label.label())) {
						remove();
					}
				}
				else if (command instanceof BrilCommand.Jump jump) {
					final String newTarget = obsoleteToNewLabels.get(jump.target());
					if (newTarget != null) {
						replace(new BrilCommand.Jump(newTarget));
					}
				}
				else if (command instanceof BrilCommand.Branch branch) {
					final String newTarget = obsoleteToNewLabels.get(branch.target());
					if (newTarget != null) {
						replace(new BrilCommand.Branch(branch.conditon(), newTarget));
					}
				}
			}
		}.iterate();
	}

	@NotNull
	private static Map<String, String> getObsoleteToNewLabels(List<BrilCommand> commands) {
		final Set<String> unusedLabels = new HashSet<>();
		for (BrilCommand command : commands) {
			if (command instanceof BrilCommand.Label labelCommand) {
				final String label = labelCommand.label();
				if (!unusedLabels.add(label)) {
					throw new IllegalStateException("Duplicate label definition " + label);
				}
			}
		}

		final Map<String, String> obsoleteToNewLabels = new HashMap<>();
		boolean isFirst = true;
		@Nullable String prevLabel = null;
		for (BrilCommand command : commands) {
			if (command instanceof BrilCommand.Label labelCommand) {
				final String label = labelCommand.label();
				if (prevLabel == null) {
					prevLabel = label;
					if (isFirst) {
						unusedLabels.remove(label);
					}
				}
				else {
					obsoleteToNewLabels.put(label, prevLabel);
				}
			}
			else if (command instanceof BrilCommand.Jump jump) {
				unusedLabels.remove(jump.target());
			}
			else if (command instanceof BrilCommand.Branch branch) {
				unusedLabels.remove(branch.target());
			}
			else if (command instanceof BrilCommand.Call call) {
				unusedLabels.remove(call.target());
			}
			else {
				if (isFirst) {
					throw new IllegalStateException("expecting an initial label");
				}

				prevLabel = null;
			}
			isFirst = false;
		}

		for (String unusedLabel : unusedLabels) {
			obsoleteToNewLabels.put(unusedLabel, unusedLabel);
		}

		return obsoleteToNewLabels;
	}

	// Inner Classes ==========================================================

	private abstract static class SingleIterator {
		private int i;

		protected abstract void handle(BrilCommand command);

		private final List<BrilCommand> commands;

		protected SingleIterator(List<BrilCommand> commands) {
			this.commands = commands;
		}

		public void iterate() {
			i = 0;
			for (; i < commands.size(); i++) {
				final BrilCommand command = commands.get(i);
				handle(command);
			}
		}

		protected void remove() {
			commands.remove(i);
		}

		protected void replace(BrilCommand command) {
			remove();
			commands.add(i, command);
		}
	}

	private abstract static class DualIterator {
		private int i;

		protected abstract void handle(BrilCommand command1, BrilCommand command2);

		private final List<BrilCommand> commands;

		protected DualIterator(List<BrilCommand> commands) {
			this.commands = commands;
		}

		public void iterator() {
			i = 0;
			for (; i < commands.size() - 1; i++) {
				final BrilCommand command1 = commands.get(i);
				final BrilCommand command2 = commands.get(i + 1);
				handle(command1, command2);
			}
		}

		protected void remove() {
			commands.remove(i);
		}

		protected void removeNext() {
			commands.remove(i + 1);
		}

		protected void replace(BrilCommand command) {
			remove();
			commands.add(i, command);
		}
	}
}
