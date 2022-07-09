package de.regnis.b.ast;

import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public final class ReturnStatement extends ControlFlowStatement {

	// Fields =================================================================

	@Nullable
	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public ReturnStatement(@Nullable Expression expression) {
		this(expression, -1, -1);
	}

	public ReturnStatement(@Nullable Expression expression, int line, int column) {
		this.expression = expression;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitReturn(this);
	}
}
