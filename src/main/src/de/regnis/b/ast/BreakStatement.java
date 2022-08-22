package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class BreakStatement implements ControlFlowStatement {

	// Fields =================================================================

	public final Position position;

	// Setup ==================================================================

	public BreakStatement(@NotNull Position position) {
		this.position = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitBreak(this);
	}
}
