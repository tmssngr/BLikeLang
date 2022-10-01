package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class BasicBlock extends StatementsBlock {

	// Setup ==================================================================

	public BasicBlock(String prefix) {
		super(prefix + "start", null);
	}

	public BasicBlock(@NotNull String label, @NotNull Block prev) {
		super(label, prev);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitBasic(this);
	}

	// Accessing ==============================================================

	@NotNull
	public Block getSingleNext() {
		final List<Block> next = getNextBlocks();
		if (next.size() != 1) {
			throw new IllegalStateException();
		}
		return next.get(0);
	}
}
