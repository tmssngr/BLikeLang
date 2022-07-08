package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public final class WhileStatement extends Statement {

	// Fields =================================================================

	public final Expression expression;
	public final StatementList statements;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public WhileStatement(Expression expression, StatementList statements) {
		this(expression, statements, -1, -1);
	}

	public WhileStatement(Expression expression, StatementList statements, int line, int column) {
		this.expression = expression;
		this.statements = statements;
		this.line = line;
		this.column = column;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitWhile(this);
	}
}
