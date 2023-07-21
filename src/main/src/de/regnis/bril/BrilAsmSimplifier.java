package de.regnis.bril;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class BrilAsmSimplifier {

	// Static =================================================================

	public static Function<List<BrilAsm>, List<BrilAsm>> create() {
		return new Function<>() {
			@Override
			public List<BrilAsm> apply(List<BrilAsm> prevCommands) {
				final List<BrilAsm> commands = new ArrayList<>(prevCommands);
				fixJumpToNextLabel(commands);
				fixObsoleteLabels(commands);
				fixCallRet(commands);
				fixBiDiLoad(commands);
				return commands;
			}
		};
	}

	// Utils ==================================================================

	private static void fixJumpToNextLabel(List<BrilAsm> commands) {
		new DualPeepHoleSimplifier(commands) {
			@Override
			protected void handle(BrilAsm command1, BrilAsm command2) {
				if (command1 instanceof BrilAsm.Jump jump
						&& command2 instanceof BrilAsm.Label label
						&& jump.target().equals(label.label())) {
					remove();
				}
				else if (command1 instanceof BrilAsm.Branch branch
						&& command2 instanceof BrilAsm.Label label
						&& branch.target().equals(label.label())) {
					remove();
				}
			}
		}.iterator();
	}

	private static void fixCallRet(List<BrilAsm> commands) {
		new DualPeepHoleSimplifier(commands) {
			@Override
			protected void handle(BrilAsm command1, BrilAsm command2) {
				if (command1 instanceof BrilAsm.Call call
						&& command2 == BrilAsm.RET) {
					remove();
					replace(new BrilAsm.Jump(call.target()));
				}
			}
		}.iterator();
	}

	private static void fixBiDiLoad(List<BrilAsm> commands) {
		new DualPeepHoleSimplifier(commands) {
			@Override
			protected void handle(BrilAsm command1, BrilAsm command2) {
				if (command1 instanceof BrilAsm.Load16 load1
						&& command2 instanceof BrilAsm.Load16 load2
						&& load1.src() == load2.dest()
						&& load1.dest() == load2.src()) {
					removeNext();
				}
			}
		}.iterator();
	}

	private static void fixObsoleteLabels(List<BrilAsm> commands) {
		final Map<String, String> obsoleteToNewLabels = getObsoleteToNewLabels(commands);

		new SinglePeepHoleSimplifier(commands) {
			@Override
			protected void handle(BrilAsm command) {
				if (command instanceof BrilAsm.Label label) {
					if (obsoleteToNewLabels.containsKey(label.label())) {
						remove();
					}
				}
				else if (command instanceof BrilAsm.Jump jump) {
					final String newTarget = obsoleteToNewLabels.get(jump.target());
					if (newTarget != null) {
						replace(new BrilAsm.Jump(newTarget));
					}
				}
				else if (command instanceof BrilAsm.Branch branch) {
					final String newTarget = obsoleteToNewLabels.get(branch.target());
					if (newTarget != null) {
						replace(new BrilAsm.Branch(branch.condition(), newTarget));
					}
				}
			}
		}.iterate();
	}

	@NotNull
	private static Map<String, String> getObsoleteToNewLabels(List<BrilAsm> commands) {
		final Set<String> unusedLabels = new HashSet<>();
		for (BrilAsm command : commands) {
			if (command instanceof BrilAsm.Label labelCommand) {
				final String label = labelCommand.label();
				if (!unusedLabels.add(label)) {
					throw new IllegalStateException("Duplicate label definition " + label);
				}
			}
		}

		final Map<String, String> obsoleteToNewLabels = new HashMap<>();
		boolean isFirst = true;
		@Nullable String prevLabel = null;
		for (BrilAsm command : commands) {
			if (command instanceof BrilAsm.Label labelCommand) {
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
			else if (command instanceof BrilAsm.Jump jump) {
				unusedLabels.remove(jump.target());
			}
			else if (command instanceof BrilAsm.Branch branch) {
				unusedLabels.remove(branch.target());
			}
			else if (command instanceof BrilAsm.Call call) {
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

	private abstract static class SinglePeepHoleSimplifier {
		private int i;

		protected abstract void handle(BrilAsm command);

		private final List<BrilAsm> commands;

		protected SinglePeepHoleSimplifier(List<BrilAsm> commands) {
			this.commands = commands;
		}

		public void iterate() {
			i = 0;
			for (; i < commands.size(); i++) {
				final BrilAsm command = commands.get(i);
				handle(command);
			}
		}

		protected void remove() {
			commands.remove(i);
		}

		protected void replace(BrilAsm command) {
			remove();
			commands.add(i, command);
		}
	}

	private abstract static class DualPeepHoleSimplifier {
		private int i;

		protected abstract void handle(BrilAsm command1, BrilAsm command2);

		private final List<BrilAsm> commands;

		protected DualPeepHoleSimplifier(List<BrilAsm> commands) {
			this.commands = commands;
		}

		public void iterator() {
			i = 0;
			for (; i < commands.size() - 1; i++) {
				final BrilAsm command1 = commands.get(i);
				final BrilAsm command2 = commands.get(i + 1);
				handle(command1, command2);
			}
		}

		protected void remove() {
			commands.remove(i);
		}

		protected void removeNext() {
			commands.remove(i + 1);
		}

		protected void replace(BrilAsm command) {
			remove();
			commands.add(i, command);
		}
	}
}
