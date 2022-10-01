package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Thomas Singer
 */
public abstract class Block {

	// Abstract ===============================================================

	public abstract void visit(@NotNull BlockVisitor visitor);

	// Fields =================================================================

	private final List<Block> prevBlocks = new ArrayList<>();
	private final List<Block> nextBlocks = new ArrayList<>();
	public final String label;

	// Setup ==================================================================

	protected Block(@NotNull String label, @Nullable Block prevBlock) {
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

	public final void addPrev(@NotNull Block prev) {
		prevBlocks.add(prev);
		prev.nextBlocks.add(this);
	}

	public final List<Block> getPrevBlocks() {
		return Collections.unmodifiableList(prevBlocks);
	}

	public final List<Block> getNextBlocks() {
		return Collections.unmodifiableList(nextBlocks);
	}

	public final void checkIntegrity() {
		checkNoDuplicate(prevBlocks);
		checkNoDuplicate(nextBlocks);

		for (Block block : prevBlocks) {
			if (!block.nextBlocks.contains(this)) {
				throw new IllegalStateException(label + ": missing next link from " + block.label);
			}
		}
		for (Block block : nextBlocks) {
			if (!block.prevBlocks.contains(this)) {
				throw new IllegalStateException(label + ": missing prev link from " + block.label);
			}
		}
	}

	public final void remove() {
		if (nextBlocks.size() != 1) {
			return;
		}

		final Block singleNext = nextBlocks.get(0);
		for (Block block : prevBlocks) {
			block.replaceNext(this, singleNext);
		}

		singleNext.prevBlocks.remove(this);
		singleNext.prevBlocks.addAll(prevBlocks);
	}

	// Utils ==================================================================

	private void checkNoDuplicate(@NotNull List<Block> blocks) {
		final Set<Block> uniqueBlocks = new HashSet<>(blocks);
		if (uniqueBlocks.size() != blocks.size()) {
			throw new IllegalStateException(label + ": duplicate entry");
		}
	}

	private void replaceNext(Block oldBlock, Block newBlock) {
		final int index = nextBlocks.indexOf(oldBlock);
		if (index < 0) {
			throw new IllegalStateException();
		}

		nextBlocks.remove(index);
		nextBlocks.add(index, newBlock);
	}
}
