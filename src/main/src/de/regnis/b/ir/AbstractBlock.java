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
	private final Set<String> tunnel = new LinkedHashSet<>();
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
		buffer.append(" ");
		getVarInputOutput(buffer);
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

	public final Set<String> getTunnel() {
		return Collections.unmodifiableSet(tunnel);
	}

	public final void addReadVar(String name) {
		if (!output.contains(name)) {
			input.add(name);
		}
	}

	public final void addWrittenVar(@NotNull String name) {
		output.add(name);
	}

	public final boolean addUsedFromNext() {
		final Set<String> requiredByNext = getRequiredByAllNext();

		boolean changed = false;
		for (String required : requiredByNext) {
			if (!output.contains(required) && !tunnel.contains(required)) {
				changed = true;
				tunnel.add(required);
			}
		}

		return changed;
	}

	public final void removeUnusedFromNext() {
		final Set<String> requiredByNext = getRequiredByAllNext();

		output.retainAll(requiredByNext);
	}

	@NotNull
	private Set<String> getRequiredByAllNext() {
		final Set<String> requiredByNext = new HashSet<>();
		for (AbstractBlock nextBlock : next) {
			requiredByNext.addAll(nextBlock.input);
			requiredByNext.addAll(nextBlock.tunnel);
		}
		return requiredByNext;
	}

	public final void getVarInputOutput(StringBuilder buffer) {
		buffer.append("in: [");
		Utils.appendCommaSeparated(input, buffer);
		buffer.append("], out: [");
		Utils.appendCommaSeparated(output, buffer);
		buffer.append("], tunnel: [");
		Utils.appendCommaSeparated(tunnel, buffer);
		buffer.append("]");
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

	public final void remove() {
		if (next.size() != 1) {
			return;
		}

		final AbstractBlock singleNext = next.get(0);
		for (AbstractBlock block : prev) {
			block.replaceNext(this, singleNext);
		}

		singleNext.prev.remove(this);
		singleNext.prev.addAll(prev);
	}

	// Utils ==================================================================

	private void checkNoDuplicate(@NotNull List<AbstractBlock> blocks) {
		final Set<AbstractBlock> uniqueBlocks = new HashSet<>(blocks);
		if (uniqueBlocks.size() != blocks.size()) {
			throw new IllegalStateException(label + ": duplicate entry");
		}
	}

	private void replaceNext(AbstractBlock oldBlock, AbstractBlock newBlock) {
		final int index = next.indexOf(oldBlock);
		if (index < 0) {
			throw new IllegalStateException();
		}

		next.remove(index);
		next.add(index, newBlock);
	}
}
