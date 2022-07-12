package de.regnis.b.ir;

import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public abstract class AbstractBlock {

	// Abstract ===============================================================

	public abstract void visit(@NotNull BlockVisitor visitor);

	// Fields =================================================================

	private final List<AbstractBlock> prev = new ArrayList<>();
	private final List<AbstractBlock> next = new ArrayList<>();
	private final Set<String> input = new LinkedHashSet<>();
	private final Set<String> output = new LinkedHashSet<>();
	public final String label;

	// Setup ==================================================================

	protected AbstractBlock(@NotNull String label, @Nullable AbstractBlock prev) {
		this.label = label;

		if (prev != null) {
			addPrev(prev);
		}
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		if (input.isEmpty() && output.isEmpty()) {
			return label;
		}

		final StringBuilder buffer = new StringBuilder();
		buffer.append(label);
		buffer.append(" (");
		Utils.appendCommaSeparated(input, buffer);
		buffer.append(") -> (");
		Utils.appendCommaSeparated(output, buffer);
		buffer.append(")");
		return buffer.toString();
	}

	// Accessing ==============================================================

	public final void addPrev(@NotNull AbstractBlock prev) {
		this.prev.add(prev);
		prev.next.add(this);
	}

	public final List<AbstractBlock> getPrev() {
		return Collections.unmodifiableList(prev);
	}

	public final List<AbstractBlock> getNext() {
		return Collections.unmodifiableList(next);
	}

	public final Set<String> getInput() {
		return Collections.unmodifiableSet(input);
	}

	public final Set<String> getOutput() {
		return Collections.unmodifiableSet(output);
	}

	public final void addReadVar(String name) {
		if (!output.contains(name)) {
			input.add(name);
		}
	}

	public final void addWrittenVar(@NotNull String name) {
		output.add(name);
	}

	public final boolean updateInputOutputFromNextBlocks() {
		final Set<String> requiredByNext = new HashSet<>();
		for (AbstractBlock nextBlock : next) {
			requiredByNext.addAll(nextBlock.input);
		}

		boolean changed = output.retainAll(requiredByNext);

		for (String required : requiredByNext) {
			if (output.add(required)) {
				changed = true;
				input.add(required);
			}
		}

		return changed;
	}

	public final void checkIntegrity() {
		checkNoDuplicate(prev);
		checkNoDuplicate(next);

		for (AbstractBlock block : prev) {
			if (!block.next.contains(this)) {
				throw new IllegalStateException(label + ": missing next link from " + block.label);
			}
		}
		for (AbstractBlock block : next) {
			if (!block.prev.contains(this)) {
				throw new IllegalStateException(label + ": missing prev link from " + block.label);
			}
		}
	}

	// Utils ==================================================================

	private void checkNoDuplicate(@NotNull List<AbstractBlock> blocks) {
		final Set<AbstractBlock> uniqueBlocks = new HashSet<>(blocks);
		if (uniqueBlocks.size() != blocks.size()) {
			throw new IllegalStateException(label + ": duplicate entry");
		}
	}
}
