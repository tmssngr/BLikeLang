package de.regnis.b.ir;

import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public abstract class ControlFlowBlock extends AbstractBlock {

	// Setup ==================================================================

	protected ControlFlowBlock(@Nullable BasicBlock prev) {
		super(prev);
	}
}
