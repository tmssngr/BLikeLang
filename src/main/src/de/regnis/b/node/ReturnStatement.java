package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class ReturnStatement extends Statement {

	// Fields =================================================================

	public final Expression expression;
	public final int line;
	public final int column;

	// Setup ==================================================================

	public ReturnStatement(Expression expression) {
		this(expression, -1, -1);
	}

	public ReturnStatement(Expression expression, int line, int column) {
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
