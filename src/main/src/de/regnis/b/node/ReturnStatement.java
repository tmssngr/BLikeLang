package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class ReturnStatement extends StatementNode {

	// Fields =================================================================

	public final ExpressionNode expression;

	// Setup ==================================================================

	public ReturnStatement(ExpressionNode expression) {
		this.expression = expression;
	}

	// Implemented ============================================================

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitReturn(this);
	}
}
