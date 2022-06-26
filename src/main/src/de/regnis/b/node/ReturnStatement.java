package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class ReturnStatement extends Statement {

	// Fields =================================================================

	public final Expression expression;

	// Setup ==================================================================

	public ReturnStatement(Expression expression) {
		this.expression = expression;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitReturn(this);
	}
}
