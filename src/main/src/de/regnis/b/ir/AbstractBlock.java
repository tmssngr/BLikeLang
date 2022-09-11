package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public abstract sealed class AbstractBlock
		permits StatementsBlock, ExitBlock {

	// Fields =================================================================

	private final List<AbstractBlock> prevBlocks = new ArrayList<>();
	private final List<AbstractBlock> nextBlocks = new ArrayList<>();
	public final String label;

	// Setup ==================================================================

	protected AbstractBlock(@NotNull String label, @Nullable AbstractBlock prevBlock) {
		this.label = label;

		if (prevBlock != null) {
			addPrev(prevBlock);
		}
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return label;
	}

	// Accessing ==============================================================

	public final void addPrev(@NotNull AbstractBlock prev) {
		prevBlocks.add(prev);
		prev.nextBlocks.add(this);
	}

	public final List<AbstractBlock> getPrevBlocks() {
		return Collections.unmodifiableList(prevBlocks);
	}

	public final List<AbstractBlock> getNextBlocks() {
		return Collections.unmodifiableList(nextBlocks);
	}

	public final void checkIntegrity() {
		checkNoDuplicate(prevBlocks);
		checkNoDuplicate(nextBlocks);

		for (AbstractBlock block : prevBlocks) {
			if (!block.nextBlocks.contains(this)) {
				throw new IllegalStateException(label + ": missing next link from " + block.label);
			}
		}
		for (AbstractBlock block : nextBlocks) {
			if (!block.prevBlocks.contains(this)) {
				throw new IllegalStateException(label + ": missing prev link from " + block.label);
			}
		}
	}

	public final void remove() {
		if (nextBlocks.size() != 1) {
			return;
		}

		final AbstractBlock singleNext = nextBlocks.get(0);
		for (AbstractBlock block : prevBlocks) {
			block.replaceNext(this, singleNext);
		}

		singleNext.prevBlocks.remove(this);
		singleNext.prevBlocks.addAll(prevBlocks);
	}

	// Utils ==================================================================

	private void checkNoDuplicate(@NotNull List<AbstractBlock> blocks) {
		final Set<AbstractBlock> uniqueBlocks = new HashSet<>(blocks);
		if (uniqueBlocks.size() != blocks.size()) {
			throw new IllegalStateException(label + ": duplicate entry");
		}
	}

	private void replaceNext(AbstractBlock oldBlock, AbstractBlock newBlock) {
		final int index = nextBlocks.indexOf(oldBlock);
		if (index < 0) {
			throw new IllegalStateException();
		}

		nextBlocks.remove(index);
		nextBlocks.add(index, newBlock);
	}
}
