package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public final class ReturnStatement implements ControlFlowStatement {

	// Fields =================================================================

	@Nullable
	public final Expression expression;
	public final Position position;

	// Setup ==================================================================

	public ReturnStatement(@Nullable Expression expression) {
		this(expression, Position.DUMMY);
	}

	public ReturnStatement(@Nullable Expression expression, @NotNull Position position) {
		this.expression = expression;
		this.position = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitReturn(this);
	}
}
