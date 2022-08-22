package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class IfStatement implements ControlFlowStatement {

	// Fields =================================================================

	public final Expression expression;
	public final StatementList trueStatements;
	public final StatementList falseStatements;
	public final Position position;

	// Setup ==================================================================

	public IfStatement(Expression expression, StatementList trueStatements, StatementList falseStatements) {
		this(expression, trueStatements, falseStatements, Position.DUMMY);
	}

	public IfStatement(Expression expression, StatementList trueStatements, StatementList falseStatements, @NotNull Position position) {
		this.expression = expression;
		this.trueStatements = trueStatements;
		this.falseStatements = falseStatements;
		this.position = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitIf(this);
	}
}
