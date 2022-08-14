package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public final class BasicBlock extends StatementsBlock {

	// Setup ==================================================================

	public BasicBlock() {
		super("start", null);
	}

	public BasicBlock(@NotNull String label, @NotNull ControlFlowBlock prev) {
		super(label, prev);
	}

	public BasicBlock(@NotNull String label, @NotNull BasicBlock prev) {
		super(label, prev);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitBasic(this);
	}

	// Accessing ==============================================================

	@NotNull
	public AbstractBlock getSingleNext() {
		final List<AbstractBlock> next = getNextBlocks();
		if (next.size() != 1) {
			throw new IllegalStateException();
		}
		return next.get(0);
	}
}
