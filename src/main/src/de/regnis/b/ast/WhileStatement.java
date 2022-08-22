package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record WhileStatement(@NotNull Expression expression, @NotNull StatementList statements,
                             @NotNull Position position) implements ControlFlowStatement {

	// Setup ==================================================================

	public WhileStatement(@NotNull Expression expression, @NotNull StatementList statements) {
		this(expression, statements, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitWhile(this);
	}
}
