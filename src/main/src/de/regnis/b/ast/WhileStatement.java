package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class WhileStatement implements ControlFlowStatement {

	// Fields =================================================================

	public final Expression expression;
	public final StatementList statements;
	public final Position position;

	// Setup ==================================================================

	public WhileStatement(Expression expression, StatementList statements) {
		this(expression, statements, Position.DUMMY);
	}

	public WhileStatement(Expression expression, StatementList statements, @NotNull Position position) {
		this.expression = expression;
		this.statements = statements;
		this.position   = position;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(@NotNull StatementVisitor<O> visitor) {
		return visitor.visitWhile(this);
	}
}
