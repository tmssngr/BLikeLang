package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public abstract class ControlFlowBlock extends AbstractBlock {

	// Setup ==================================================================

	protected ControlFlowBlock(@NotNull String label, @Nullable BasicBlock prev) {
		super(label, prev);
	}
}
