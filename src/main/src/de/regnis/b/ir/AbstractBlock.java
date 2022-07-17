package de.regnis.b.ir;

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
		return label;
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
