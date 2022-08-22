package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public abstract class Expression {

	// Abstract ===============================================================

	public abstract <O> O visit(ExpressionVisitor<O> visitor);

	// Setup ==================================================================

	protected Expression() {
	}
}
