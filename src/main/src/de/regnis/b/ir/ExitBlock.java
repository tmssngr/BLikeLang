package de.regnis.b.ir;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class ExitBlock extends Block {

	// Setup ==================================================================

	public ExitBlock(String prefix) {
		super(prefix + "exit", null);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitExit(this);
	}
}
