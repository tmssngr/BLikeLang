package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public record IfStatement(@NotNull Expression expression, @NotNull StatementList trueStatements,
                          @NotNull StatementList falseStatements,
                          @NotNull Position position) implements ControlFlowStatement {

	// Setup ==================================================================

	public IfStatement(@NotNull Expression expression, @NotNull StatementList trueStatements, @NotNull StatementList falseStatements) {
		this(expression, trueStatements, falseStatements, Position.DUMMY);
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitIf(this);
	}
}
