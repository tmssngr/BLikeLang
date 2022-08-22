package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public record ReturnStatement(@Nullable Expression expression,
                              @NotNull Position position) implements ControlFlowStatement {

	// Setup ==================================================================

	public ReturnStatement(@Nullable Expression expression) {
		this(expression, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitReturn(this);
	}
}
